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
/*
 * @include "../../resourcesPlugins/resourcesPluginsCrud.js"
 */
Ext.namespace('sitools.admin.datasets.services');

/**
 * A panel to managed Dataset resources.
 * @requires sitools.admin.resourcesPlugins.ResourcesPluginsCrud
 * @class sitools.admin.datasets.services.DatasetServicesCrud 
 * @extends Ext.grid.GridPanel
 */
// ExtJS4.3 'Ext.grid.EditorGridPanel'
Ext.define('sitools.admin.datasets.services.DatasetServicesCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-dataset_services',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    modify : false,
    urlGrid : null,    
    conflictWarned : false,
    clicksToEdit: 1,
    forceFit : true,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.datasets.services.DatasetServicesProp',
                'sitools.admin.resourcesPlugins.ResourcesPluginsProp',
                'sitools.admin.datasets.services.DatasetServicesCopyProp'],
    
    viewConfig : {
        autoFill : true,
        allLoaded : false
//        getRowClass : function (row, index, rowParams, store) {
//            var cls = '';
//            if (this.allLoaded) {
//                if (index === 0) {
//                    this.message = "";
//                }
//                var data = row.data;
//                if (data.modelVersion !== data.definitionVersion && !Ext.isEmpty(data.definitionVersion)) {
//                    this.message += "Resources " + data.name + " definition (v" + data.modelVersion + ") may conflict with current class version : "
//                            + data.definitionVersion + "<br/>";                        
//                    cls = "red-row";
//                }
//                if (!this.conflictWarned && !Ext.isEmpty(this.message) && index === store.getCount() - 1) {
//                    Ext.Msg.alert("warning.version.conflict", this.message);
//                    this.conflictWarned = true;
//                }
//            }
//            return cls; 
//        } 
    },
    
    initComponent : function () {
        this.appClassName = "fr.cnes.sitools.dataset.DataSetApplication";
        this.parentType = "dataset";

        /** ************************************************************************************************* */

        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        
        /** URL TO GET ALL SERVICES (IHM OR SERVER) EXISTING IN SITOOLS **/
        this.urlAllServicesIHM = '/sitools/guiServices';
        this.urlAllServicesSERVER = '/sitools/plugins/resources/classes';
        
        /** URL TO GET ALL SERVICES (IHM AND SERVER) OF A DATASET **/
        this.urlDatasetAllServices = '/sitools/datasets/{idDataset}/services';
        
        /** URL TO GET THE LIST OF ALL SERVICES (IHM OR SERVER) OF A DATASET **/
        this.urlDatasetAllServicesIHM = '/sitools/datasets/{idDataset}/services/gui';
        this.urlDatasetAllServicesSERVER = '/sitools/datasets/{idDataset}/services/server';
        
        /** URL TO GET OR MODIFY ONE EXITING SERVICE OF A DATASET (IHM OR SERVER) **/
        this.urlDatasetServiceIHM = '/sitools/datasets/{idDataset}/services/gui/{idService}';
        this.urlDatasetServiceSERVER = '/sitools/datasets/{idDataset}/services/server/{idService}';
        

        // LIST OF PARENTS
        this.storeParents = Ext.create("Ext.data.JsonStore", {
            fields : [ 'id', 'name', 'type', 'sitoolsAttachementForUsers' ],
            proxy : {
                type : "ajax",
                url : this.urlDatasets,
                reader : {
                    type : 'json',
                    root : "data"
                }
            },
            autoLoad : true
        });
        
        this.comboParents = Ext.create("Ext.form.ComboBox", {
            store : this.storeParents,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.select' + this.parentType + 's'),
            selectOnFocus : true,            
            listeners : {
                scope : this,
                select : function (combo, records, index) {
                    var rec = records[0];
                    this.parentId = rec.get("id");
                    if (!Ext.isEmpty(rec.get("type"))) {
                        this.appClassName = rec.data.type;
                    }    
                    this.savePropertiesBtn.removeCls('not-save-textfield');
                    var url = this.urlDatasetAllServices.replace('{idDataset}', this.parentId);
                    
                    this.getStore().setProxy({
                        type :'ajax',
                        url : url,
                        reader : {
                            type :'json',
                            root : "ServiceCollectionModel.services",
                            idProperty : 'id'
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
                name : 'type',
                type : 'string'
            }, {
                name : 'category',
                type : 'string'
            }, {
                name : 'icon',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
                name : 'visible',
                type : 'bool'
            }, {
                name : 'position',
                type : 'string'
            }, {
                name : 'dataSetSelection'
            }, {
                name : 'modelVersion',
                type : 'string'
            }, {
                name : 'definitionVersion',
                type : 'string'
            } ],
            listeners : {
                scope : this,
                beforeLoad : function () {
                    this.getView().allLoaded = false;  
                    this.getView().conflictWarned = false;
                },
                load : this.loadServerPluginClassDescription                    
            }
        });

        var visible = {
            xtype : 'checkcolumn',
            header : i18n.get('headers.visible') + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'visible',
            width : 55,
            listeners : {
                scope : this,
                checkchange : function (combo, rowIndex, checked) {
                    this.savePropertiesBtn.addCls('not-save-textfield');
                }
            }
        };
        
        this.columns = [{
            header : i18n.get('label.type'),
            dataIndex : 'type',
            width : 80,
            resizable : false,
            renderer : function (value, meta, record, index, colIndex, store) {
                meta.tdCls += value;
                return value;
            }
        }, {
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 160
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 230,
            sortable : false
        }, {
            header : i18n.get('label.labelEditable') + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'label',
            width : 140,
            editor : {
                xtype : 'textfield',
                listeners : {
                    scope : this,
                    change : function (textfield, newValue, oldValue) {
                        this.savePropertiesBtn.addCls('not-save-textfield');
                    }
                }
            }
        }, {
            header : i18n.get('label.categoryEditable') + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'category',
            width : 120,
            editor : {
                xtype : 'textfield',
                listeners : {
                    scope : this,
                    change : function (textfield, newValue, oldValue) {
                        this.savePropertiesBtn.addCls('not-save-textfield');
                    }
                }
            }
        }, {
            header : 'Position' + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'position',
            width : 100,
            editor: {
                xtype : 'combo',
                typeAhead : true,
                triggerAction : 'all',
                queryMode : 'local',
                store : Ext.create("Ext.data.ArrayStore", {
                    id : 0,
                    fields : ['position'],
                    data : [['left'], ['right']]
                }),
                value : 'Left',
                valueField : 'position',
                displayField : 'position',
                listeners : {
                    scope : this,
                    change : function (textfield, newValue, oldValue) {
                        this.savePropertiesBtn.addCls('not-save-textfield');
                    }
                }
            }
        }, {
            header : i18n.get('label.icon'),
            dataIndex : 'icon',
            width : 50,
            sortable : false,
            renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                if (!Ext.isEmpty(value)) {
                    value = '<img src="' + value + '" height=15 width=18 style="margin:auto; display: block;"/>';
                }
                return value;
            }
        }, visible];
        
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            pluginId : 'cellEditing',
            clicksToEdit: 1
        })];

        this.savePropertiesBtn = Ext.create("Ext.Button", {
            text : i18n.get('label.saveProperties'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png',
            handler : this.onSaveProperties,
            tooltip : i18n.get('label.savePropertiesHelp'),
            xtype : 's-menuButton'
        });
        
        this.tbar = {
//            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboParents, '-', {
                text : i18n.get('label.addServiceIhm'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create_serviceIhm.png',
                handler : function () {
                    this.onCreate('GUI');
                },
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.addServiceServer'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create_serviceServer.png',
                handler : function () {
                    this.onCreate('SERVER');
                },
                xtype : 's-menuButton'
            }, '-', {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, this.savePropertiesBtn, '-',
            {
                text : i18n.get('label.duplicate'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/presentation1.png',
                handler : this.onDuplicate,
                xtype : 's-menuButton'
            }]
        };
        
        this.rbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            alignRight : false,
            layout : {
                pack : 'center'
            },
            defaults : {
                scope : this
            },
            listeners : {
                scope : this,
                afterrender : function (toolbar) {
                    var buttons = Ext.ComponentQuery.query('toolbar[xtype=sitools.public.widget.grid.GridSorterToolbar] > button');
                    Ext.each(buttons, function (button) {
                        button.on('click', function () {
                            if (this.getLastSelectedRecord() != undefined)
                                this.savePropertiesBtn.addCls('not-save-textfield');
                        }, this);
                    }, this);
                }
            }
        };

        this.bbar = {
                xtype : 'pagingtoolbar',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
        this.sm = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });
        
        this.listeners = {
            scope : this, 
            celldblclick : function (grid, td, cellIndex, record, tr, rowIndex) {
                if (this.columns[cellIndex].hasEditor()) {
                    return;
                } else {
                    this.onModify();    
                }
            }
        };

        sitools.admin.datasets.services.DatasetServicesCrud.superclass.initComponent.call(this);
    },
    
    /**
     * Open a {sitools.admin.resourcesPlugins.resourcesPluginsProp} resource plugin property window
     *  to create a new resource for the selected dataset in the comboBox
     *  @require type
     *  the type of service to create
     */
    onCreate : function (type) {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var urlParent = this.urlDatasets + "/" + parentId;

        if (type === "GUI") {
            var gui = Ext.create("sitools.admin.datasets.services.DatasetServicesProp", {
                action : 'create',
                parentPanel : this,
                parentType : this.parentType,
                appClassName : this.appClassName,
                idParent : parentId,
                urlDataset : urlParent,
                urlAllServicesIHM : this.urlAllServicesIHM,
                urlDatasetServiceIHM : this.urlDatasetServiceIHM.replace('{idDataset}', parentId)
            });
            gui.show();
        }
        else if (type === "SERVER") {
            var server = Ext.create("sitools.admin.resourcesPlugins.ResourcesPluginsProp", {
                action : 'create',
                parentPanel : this,
                urlResources : this.urlAllServicesSERVER,
                urlResourcesCRUD : this.urlDatasetAllServicesSERVER.replace('{idDataset}', parentId),
                urlParent : urlParent,
                parentType : this.parentType,
                appClassName : this.appClassName,
                idParent : parentId
            });
            server.show();
        }
    },

    /**
     * Generic method managing the dataset services (SERVER or IHM) for modifications
     */
    onModify : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var rec = this.getLastSelectedRecord();
        
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
       

        if (rec.data.type === "GUI") {
            this.editGUIService(rec.data.id);
        } else if (rec.data.type === "SERVER") {
            this.editSERVERService(rec.data.id);
        }
        
    },

    onDuplicate : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        
        var parentId = this.comboParents.getValue();
        
        var arrayRecords = this.getSelectionModel().getSelection();
        
        if (Ext.isEmpty(arrayRecords)) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        var datasetServicesCopy = Ext.create("sitools.admin.datasets.services.DatasetServicesCopyProp", {
            storeCombo : this.storeParents,
            services : arrayRecords,
            parentDatasetId : parentId,
            urlDatasetServiceSERVER : this.urlDatasetAllServicesSERVER,
            urlDatasetServiceGUI : this.urlDatasetAllServicesIHM,
            storeServices : this.store
        });
        datasetServicesCopy.show();
    },
    
    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('resourcesPlugins' + this.parentType + 'Crud.delete'), rec.get("name")),
            scope : this,
            fn : function (btn, text) {
                if (btn === 'yes') {
                    this.doDelete(rec, parentId);
                }
            }
        });
    },

    /**
     * done the delete of the passed record
     * @param rec the record to delete
     */
    doDelete : function (rec, parentId) {

        var url = null;
        if (rec.data.type === "GUI") {
            url = this.urlDatasetServiceIHM.replace('{idDataset}', parentId);
        } else if (rec.data.type === "SERVER") {
            url = this.urlDatasetServiceSERVER.replace('{idDataset}', parentId);
        }
        url = url.replace('{idService}', rec.data.id);
        
        Ext.Ajax.request({
            url : url,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    /**
     * Save all Services Properties (label, icon, category) for the current Dataset
     */
    onSaveProperties : function () {
        var service = {};
        service.id = this.parentId;
        service.name = "";
        service.description = "";
        service.services = [];
        

        this.getStore().each(function (rec) {
            var record = rec.data;
            Ext.destroyMembers(record, 'modelVersion');
            Ext.destroyMembers(record, 'definitionVersion');
            service.services.push(record);
        });

        Ext.Ajax.request({
            url : this.urlDatasetAllServices.replace('{idDataset}', this.parentId),
            method : 'PUT',
            jsonData : service,
            scope : this,
            success : function (ret) {
                this.savePropertiesBtn.removeCls('not-save-textfield');
                popupMessage(i18n.get('label.information'), i18n.get('label.datasetServicePropertiesSaved'),
                loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
                
                this.store.reload();
            },
            failure : alertFailure
        });
    },
    
    /**
     * Modify an existing GUI Service
     * 
     * @param idService
     *            the id of GUI Service to modify
     */
    editGUIService : function (idService) {
        
        var parentId = this.comboParents.getValue();
        var urlParent = this.urlDatasets + "/" + parentId;
        
        Ext.Ajax.request({
            url : this.urlDatasetServiceIHM.replace('{idService}', idService).replace('{idDataset}', this.parentId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);

                var up = Ext.create("sitools.admin.datasets.services.DatasetServicesProp", {
                    action : 'modify',
                    record : json.guiServicePlugin,
                    parentPanel : this,
                    urlDataset : urlParent,
                    parentType : this.parentType,
                    appClassName : this.appClassName,
                    idParent : this.parentId,
                    urlAllServicesIHM : this.urlAllServicesIHM,
                    urlDatasetServiceIHM : this.urlDatasetServiceIHM.replace('{idDataset}', this.parentId)
                });
                up.show();
            },
            failure : alertFailure
        });
    },
    

    /**
     * Modify an existing SERVER Service
     * 
     * @param idService
     *            the id of SERVER Service to modify
     */
    editSERVERService : function (idService) {
        var urlParent = this.urlDatasets + "/" + this.parentId;
        Ext.Ajax.request({
            url : this.urlDatasetServiceSERVER.replace('{idService}', idService).replace('{idDataset}', this.parentId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                var resourcePlugin = {};
                resourcePlugin.data = json.resourcePlugin;
                var up = Ext.create("sitools.admin.resourcesPlugins.ResourcesPluginsProp", {
                    action : 'modify',
                    record : resourcePlugin,
                    parentPanel : this,
                    urlResources : this.urlAllServicesSERVER,
                    urlResourcesCRUD : this.urlDatasets + "/" + this.parentId + "/services/server",
                    urlParent : urlParent,
                    appClassName : this.appClassName,
                    idParent : this.parentId,
                    parentType : this.parentType
                });
                up.show();
            },
            failure : alertFailure
        });
    },
    

    loadServerPluginClassDescription : function (store, records, options) {
        Ext.Ajax.request({
            url : this.urlDatasetAllServicesSERVER.replace('{idDataset}', this.parentId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (json.success) {
                    var data = json.data;
                    Ext.each(data, function (pluginFromServer) {
                        var plugin = store.getById(pluginFromServer.id);
                        if (plugin) {
                            plugin.set("definitionVersion", pluginFromServer.currentClassVersion);
                            plugin.set("modelVersion", pluginFromServer.classVersion);
                        }
                    }, this);
                    this.loadGuiPluginModelDescription(store, records, options);
                }

            },
            failure : alertFailure
        });
    },

    loadGuiPluginModelDescription : function (store, records, options) {
        Ext.Ajax.request({
            url : this.urlDatasetAllServicesIHM.replace('{idDataset}', this.parentId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (json.success) {
                    var data = json.data;
                    Ext.each(data, function (guiPluginFromServer) {
                        var plugin = store.getById(guiPluginFromServer.id);
                        if (plugin) {
                            plugin.set("definitionVersion", guiPluginFromServer.currentGuiServiceVersion);
                            plugin.set("modelVersion", guiPluginFromServer.version);
                        }
                    }, this);
                    this.getView().allLoaded = true;
                    this.getView().refresh();
                }
            },
            failure : alertFailure
        });
    }
});
