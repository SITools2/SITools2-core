/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*
 * @include "convertersProp.js"
 * @include "../id.js"
 */
Ext.namespace('sitools.admin.converters');

/**
 * Converters Crud Panel
 * @requires sitools.admin.converters.convertersProp
 * @class sitools.admin.converters.convertersCrudPanel
 * @extends Ext.grid.GridPanel
 */
sitools.admin.converters.convertersCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.CONVERTERS,
    pageSize : 10,
    modify : false,
    urlGrid : null,
    converterChainedId : {},
    // loadMask: true,
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
					Ext.Msg.alert("warning.version.conflict", "Converter " 
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
        this.converterUrlPart = loadUrl.get('APP_DATASETS_CONVERTERS_URL');
        
        this.httpProxyForms = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : "converterChainedModel.converters",
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
                name : 'descriptionAction',
                type : 'string'
            }, {
                name : 'parameters'
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
            }, {
                name : 'status',
                type : 'string'
            }, {
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
                    this.httpProxyForms.setUrl(this.urlDatasets + "/" + this.datasetId + this.converterUrlPart, true);
                    this.getStore().removeAll();
                    this.getStore().load();

                }

            }
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150,
                sortable : false
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.descriptionAction'),
                dataIndex : 'descriptionAction',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 50,
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
            }, {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : false
            } ]
        });

        this.tbar = {
            xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboDatasets, {
                text : i18n.get('label.add'),
                icon : '/sitools/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : '/sitools/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : '/sitools/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '-', {
                text : i18n.get('label.deleteAll'),
                icon : '/sitools/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteAll,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.saveOrder'),
                icon : '/sitools/common/res/images/icons/save.png',
                handler : this.onSave,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.active'),
                icon : '/sitools/common/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : '/sitools/common/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            } ]
        };
        
        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };

        sitools.admin.converters.convertersCrudPanel.superclass.initComponent.call(this);

    },

    onCreate : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var up = new sitools.admin.converters.convertersProp({
            action : 'create',
            parent : this,
            datasetId : this.datasetId,
            converterUrlPart : this.converterUrlPart, 
            urlDatasets : this.urlDatasets
        });
        up.show();
    },

    onSave : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        
        var datasetId = this.comboDatasets.getValue(), jsonReturn = {};
        
        jsonReturn.id = datasetId;
        jsonReturn.idOrder = [];
        
        for (var i = 0; i < this.store.getCount(); i++) {
            var rec = this.store.getAt(i).data;
            jsonReturn.idOrder.push(rec.id);
        }
        var url = this.urlDatasets + "/" + datasetId + this.converterUrlPart;
        Ext.Ajax.request({
            url : url,
            method : "PUT",
            scope : this,
            jsonData : jsonReturn,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.converterSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededConv"));

                //this.fillGrid(data);
                this.getStore().reload();
            }
        });
    },

    onModify : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var rec = this.getSelectionModel().getSelected();
        var index = this.getStore().indexOf(rec);
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.converters.convertersProp({
            action : 'modify',
            parent : this,
            converter : rec.data,
            index : index,
            datasetId : this.datasetId,
            converterUrlPart : this.converterUrlPart, 
            urlDatasets : this.urlDatasets,
            converterChainedId : this.converterChainedId
        });
        up.show();

    },

    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (Ext.isEmpty(rec)) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.convertersCrud.deleteConverter'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec.data.id);
                }
            }

        });
    },
    
    doDelete : function (converterId) {
        var datasetId = this.comboDatasets.getValue();
        var url = this.urlDatasets + "/" + datasetId + this.converterUrlPart + "/" + converterId;

        Ext.Ajax.request({
            url : url,
            method : "DELETE",
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.getStore().reload();
                    Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededConvDelete"));
                }

            }
        });
    },

    onDeleteAll : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.convertersCrud.delete.all'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDeleteAll();
                }
            }

        });
    },

    doDeleteAll : function () {
        var datasetId = this.comboDatasets.getValue();
        var url = this.urlDatasets + "/" + datasetId + this.converterUrlPart;

        Ext.Ajax.request({
            url : url,
            method : "DELETE",
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.getStore().removeAll();
                    this.getStore().reload();
                    Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededConvDelete"));
                }

            }
        });
    },
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.converterUrlPart + "/" + rec.data.id + "/start",
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
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.converterUrlPart + "/" + rec.data.id + "/stop",
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

Ext.reg('s-converters', sitools.admin.converters.convertersCrudPanel);
