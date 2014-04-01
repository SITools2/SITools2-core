/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.component.sva');

Ext.define('sitools.component.sva.svaCrudPanel', { extend : 'Ext.grid.Panel',
	alias : 'widget.s-sva',
    border : false,
    height : 300,
    id : ID.BOX.SVA,
    pageSize : 10,
    modify : false,
    urlGrid : null,    
	conflictWarned : false,
    viewConfig : {
        forceFit : true,
        autoFill : true, 
		getRowClass : function (row, index) { 
			var cls = ''; 
			var data = row.data;
			if (data.classVersion !== data.currentClassVersion
			    && data.currentClassVersion !== null 
				&& data.currentClassVersion !== undefined) {
				if (!this.conflictWarned) {
					Ext.Msg.alert("warning.version.conflict", "SVA " 
					+ data.name 
					+ " definition (v" 
					+ data.classVersion 
					+ ") may conflict with current class version : " 
					+ data.currentClassVersion);
					this.conflictWarned = true;
				}
				cls = "red-row";
			}
			return cls; 
		} 
		
	},

    initComponent : function () {
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        this.svaUrlPart = loadUrl.get('APP_SVA_URL');
	
        this.httpProxyForms = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : "data",
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'className',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
				name : 'classVersion',
				type : 'string'
            }, {
				name : 'classAuthor',
				type : 'string'
            }, {
				name : 'currentClassVersion',
				type : 'string'
            }, {
				name : 'currentClassAuthor',
				type : 'string'
            },
            {
                name : 'classOwner',
                type : 'string'
            }],
            proxy : this.httpProxyForms
        });

        var storeDatasets = new Ext.data.JsonStore({
            fields : [ 'id', 'name' ],
            url : this.urlDatasets,
            root : "data",
            autoLoad : true
        });
        this.comboDatasets = new Ext.form.ComboBox({
            store : storeDatasets,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDatasets'),
            selectOnFocus : true,            
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.datasetId = rec.data.id;
                    var url = this.urlDatasets + "/" + this.datasetId + this.svaUrlPart;
                    this.httpProxyForms.setUrl(this.urlDatasets + "/" + this.datasetId + this.svaUrlPart, true);

                    // this.loadParameters(this.datasetId);
                    this.getStore().load();

                }

            }
        });
        
        this.bbar = {
                xtype : 'pagingtoolbar',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
        this.columns = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.label'),
                dataIndex : 'label',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.className'),
                dataIndex : 'className',
                width : 150,
                sortable : false
            }, {
                header : i18n.get('label.classVersion'),
                dataIndex : 'classVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.currentClassVersion'),
                dataIndex : 'currentClassVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.classAuthor'),
                dataIndex : 'classAuthor',
                width : 100,
                sortable : false
            },
            {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : false
            }]
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboDatasets, {
                text : i18n.get('label.add'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        sitools.component.sva.svaCrudPanel.superclass.initComponent.call(this);

    },

    onRender : function () {
        sitools.component.sva.svaCrudPanel.superclass.onRender.apply(this, arguments);
    },

    onCreate : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var up = new sitools.component.sva.svaProp({
            action : 'create',
            store : this.store,
            parent : this,
            datasetId : this.datasetId,
            urlDatasets : this.urlDatasets,
            svaUrlPart : this.svaUrlPart
        });
        up.show();
    },

    onModify : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var rec = this.getSelectionModel().getSelected();

        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        var up = new sitools.component.sva.svaProp({
            action : 'modify',
            store : this.store,
            parent : this,
            svaId : rec.data.id,
            datasetId : this.datasetId,
            urlDatasets : this.urlDatasets,
            svaUrlPart : this.svaUrlPart

        });
        up.show();

    },

    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('svaCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });
    },

    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.svaUrlPart + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.svaUrlPart + "/" + rec.data.id + "/start",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    _onDisactive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.svaUrlPart + "/" + rec.data.id + "/stop",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }
});
