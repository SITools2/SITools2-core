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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.filters');

Ext.define('sitools.admin.filters.FiltersCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-filters',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.FILTERS,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    modify : false,
    urlGrid : null,
    filterChainedId : {},
    forceFit : true,
    // loadMask: true,
    conflictWarned : false,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.filters.FiltersProp'],
    viewConfig : {
        autoFill : true, 
//		getRowClass : function (row, index) { 
//			var cls = ''; 
//			var data = row.data;
//			if (data.classVersion !== data.currentClassVersion
//			    && data.currentClassVersion !== null 
//				&& data.currentClassVersion !== undefined) {
//				if (!this.conflictWarned) {
//					Ext.Msg.alert("warning.version.conflict", "Filter " 
//					+ data.name 
//					+ " definition (v" 
//					+ data.classVersion 
//					+ ") may conflict with current class version : " 
//					+ data.currentClassVersion);
//					this.conflictWarned = true;
//				}
//				cls = "red-row";
//			}
//			return cls; 
//		} 
	},

    initComponent : function () {
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        this.filterUrlPart = loadUrl.get('APP_DATASETS_FILTERS_URL');
        
        var storeDatasets = Ext.create("Ext.data.JsonStore", {
            fields : [ 'id', 'name' ],
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.urlDatasets,
                limitParam : undefined,
                startParam : undefined,
                reader : {
                    type : 'json',
                    root : "data"                    
                }
            }
        });
        
        this.comboDatasets = Ext.create("Ext.form.ComboBox", {
            store : storeDatasets,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDatasets'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.datasetId = rec[0].data.id;
                    
                    this.getStore().setProxy({
                        type : 'ajax',
                        url : this.urlDatasets + "/" + this.datasetId + this.filterUrlPart,
                        reader : {
                            type : 'json',
                            idProperty : 'id',
                            root : "filterChainedModel.filters",
                        }
                    });
                    
                    this.getStore().removeAll();
                    this.getStore().load();
                }
        
            }
        });
        
        this.store = Ext.create("Ext.data.JsonStore", {
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
            }
            ],
            proxy : this.httpFilterForms
        });

       

        this.columns = {
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            items : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150,
                sortable : true,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
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
                sortable : false,
                renderer : function (value, meta, record, index, colIndex, store) {
                    meta.tdCls += value;
                    return value;
                }
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
        };

        this.tbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboDatasets, {
                text : i18n.get('label.add'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '-', {
                text : i18n.get('label.deleteAll'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteAll,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.saveOrder'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png',
                handler : this.onSave,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.callParent(arguments);

    },

    onCreate : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var up = Ext.create("sitools.admin.filters.FiltersProp", {
            action : 'create',            
            parent : this,
            datasetId : this.datasetId,
            urlDatasets : this.urlDatasets,
            filterUrlPart : this.filterUrlPart
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
            var rec = this.store.getAt(i);
            jsonReturn.idOrder.push(rec.get("id"));
        }
        var url = this.urlDatasets + "/" + datasetId + this.filterUrlPart;
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
                Ext.create("Ext.ux.Notification", {
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.filtersSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededFilter"));

                //this.fillGrid(data);
                this.getStore().reload();
            }
        });
    },
    
    onModify : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        var rec = this.getLastSelectedRecord();
        var index = this.getStore().indexOf(rec);
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.filters.filtersProp", {
            action : 'modify',
            parent : this,
            filter : rec.data,
            index : index,
            datasetId : this.datasetId,
            urlDatasets : this.urlDatasets,
            filterChainedId : this.filterChainedId,
            filterUrlPart : this.filterUrlPart
        });
        up.show();

    },
    
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (Ext.isEmpty(rec)) {
            return false;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.filtersCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec.data.id);
                }
            }
        });
    },

    doDelete : function (filterId) {
        var datasetId = this.comboDatasets.getValue();
        var url = this.urlDatasets + "/" + datasetId + this.filterUrlPart + "/" + filterId;

        Ext.Ajax.request({
            url : url,
            method : "DELETE",
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.getStore().reload();
                    Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededFilterDelete"));
                }

            }
        });
    },

    onDeleteAll : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.filtersCrud.delete.all'),
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
        var url = this.urlDatasets + "/" + datasetId + this.filterUrlPart;

        Ext.Ajax.request({
            url : url,
            method : "DELETE",
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.getStore().removeAll();
                    this.getStore().reload();
                    Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededFilterDelete"));
                }

            }
        });
    },
    _onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.filterUrlPart + "/" + rec.data.id + "/start",
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
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.urlDatasets + "/" + this.datasetId + this.filterUrlPart + "/" + rec.data.id + "/stop",
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

