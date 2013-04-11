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
 * @include "../../resourcesPlugins/resourcesPluginsCrud.js"
 */
Ext.namespace('sitools.admin.datasets.services');

/**
 * A panel to managed Dataset resources.
 * @requires sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel
 * @class sitools.admin.datasets.services.datasetServicesCrud 
 * @extends Ext.grid.GridPanel
 */
sitools.admin.datasets.services.datasetServicesCrud = Ext.extend(Ext.grid.EditorGridPanel, {
    
    border : false,
    height : 300,
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
                    Ext.Msg.alert("warning.version.conflict", "Resources " 
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
    	this.appClassName = "fr.cnes.sitools.dataset.DataSetApplication";
    	this.parentType = "dataset";
    	
        /****************************************************************************************************/
    	
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
        
    	//LIST OF PARENTS
        var storeParents = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'type' ],
            url : this.urlDatasets,
            root : "data",
            autoLoad : true
        });
        
        this.comboParents = new Ext.form.ComboBox({
            store : storeParents,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.select' + this.parentType + 's'),
            selectOnFocus : true,            
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.parentId = rec.data.id;
                    if (!Ext.isEmpty(rec.data.type)) {
                        this.appClassName = rec.data.type;
                    }                    
                    var url = this.urlDatasetAllServices.replace('{idDataset}', this.parentId);
                    this.httpProxyResources.setUrl(url, true);
                    this.getStore().removeAll();
                    this.getStore().load();
                }
            }
        });
        
        this.httpProxyResources = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });
        
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : "ServiceCollectionModel.services",
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, /*{
                name : 'parent',
                type : 'string'
            },*/ {
            	name : 'type',
                type : 'string'
            }, {
            	name : 'category',
                type : 'string'
            }, {
            	name : 'icon',
                type : 'string'
            }],
            proxy : this.httpProxyResources
        });

        this.cm = new Ext.grid.ColumnModel({
            defaults : {
                sortable : true
            },
            columns : [ {
                header : i18n.get('label.type'),
                dataIndex : 'type',
                width : 80,
                resizable : false,
                renderer: function(value, metadata, record, rowIndex, colIndex, store) {
                	if (value == "SERVER"){
                		metadata.style += "font-weight:bold; color:blue;";
                	}
                	else {
                		metadata.style += "font-weight:bold; color:green;";
                	}
                	return value;
                }
            }, {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 180
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 250,
                sortable : false
            }, {
                header : i18n.get('label.label'),
                dataIndex : 'label',
                width : 140,
                editor : new Ext.form.TextField()
            }, {
                header : i18n.get('label.icon'),
                dataIndex : 'icon',
                width : 150,
                sortable : false,
                editor : new Ext.form.TextField()
            }, {
                header : i18n.get('label.category'),
                dataIndex : 'category',
                width : 180,
                editor : new Ext.form.TextField()
            }  ]
        });

        this.tbar = {
            xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
            	scope : this
            },
            items : [ this.comboParents, '-' , {
                text : i18n.get('label.addServiceIhm'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create_serviceIhm.png',
                handler : function (){
                	this.onCreate('GUI');
                },
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.addServiceServer'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create_serviceServer.png',
                handler : function (){
                	this.onCreate('SERVER');
                },
                xtype : 's-menuButton'
            }, '-' , {
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
                text : i18n.get('label.saveProperties'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
                handler : this.onSaveProperties,
                tooltip : i18n.get('label.savePropertiesHelp'),
                xtype : 's-menuButton'
            }, '-' ]
        };

        this.bbar = {
                xtype : 'paging',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty'),
            };
        
        this.sm = new Ext.grid.RowSelectionModel();
        
        this.listeners = {
            scope : this, 
            celldblclick : function (grid, row, col){
            	if (grid.getColumnModel().isCellEditable(col, row)){
            		return;
            	}
            	this.onModify();
            }
        };

        sitools.admin.datasets.services.datasetServicesCrud.superclass.initComponent.call(this);
    },
    
    /**
     * done a specific render to load resources plugins from the store. 
     */ 
    onRender : function () {
    	sitools.admin.datasets.services.datasetServicesCrud.superclass.onRender.apply(this, arguments);        
    },

    /**
     * Open a {sitools.admin.resourcesPlugins.resourcesPluginsProp} resource plugin property window
     *  to create a new resource for the selected dataset in the comboBox
     *  @require type
     *  		the type of service to create
     */
    onCreate : function (type) {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var urlParent = this.urlDatasets + "/" + parentId;
        
        
        if (type === "GUI"){
        	var up = new sitools.admin.datasets.services.datasetServicesProp({
        		action : 'create',            
        		parentPanel : this,          
        		parentType : this.parentType,
        		appClassName : this.appClassName,
        		idParent : parentId,
        		urlAllServicesIHM : this.urlAllServicesIHM,
        		urlDatasetServiceIHM : this.urlDatasetServiceIHM.replace('{idDataset}', parentId)
        	});
        	up.show();
        }
        else if (type === "SERVER") {
        	var up = new sitools.admin.resourcesPlugins.resourcesPluginsProp({
        		action : 'create',            
        		parentPanel : this,          
        		urlResources : this.urlAllServicesSERVER,
        		urlResourcesCRUD : this.urlDatasetAllServicesSERVER.replace('{idDataset}', parentId),
        		urlParent : urlParent,
        		parentType : this.parentType,
        		appClassName : this.appClassName,
        		idParent : parentId
        	});
        	up.show();
        }
    },

    /**
     * Open a {sitools.admin.resourcesPlugins.resourcesPluginsProp} resource plugin property window
     *  to modify a resource for the selected dataset in the comboBox
     */
    onModify : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var rec = this.getSelectionModel().getSelected();
        
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
       
        if (rec.data.type == "GUI"){
        	this.editGUIService(rec.data.id);
        }else if (rec.data.type == "SERVER") {
        	this.editSERVERService(rec.data.id);
        }
        
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('resourcesPlugins' + this.parentType + 'Crud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
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
    	
    	var url;
    	if (rec.data.type == "GUI"){
    		url = this.urlDatasetServiceIHM.replace('{idDataset}', parentId);
    	}
    	else if (rec.data.type == "SERVER"){
    		url = this.urlDatasetServiceSERVER.replace('{idDataset}', parentId);
    	}
    	url += url.replace('{idService}',rec.id);
    	
        Ext.Ajax.request({
            url : url,
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
    
    onSaveProperties : function () {
    	var service = {};
    	service.id = this.parentId;
    	service.name = "";
    	service.description = "";
    	service.services = [];
    	
    	this.getStore().each(function(rec){
    		service.services.push(rec.data);
    	});
    	
    	Ext.Ajax.request({
            url : this.urlDatasetAllServices.replace('{idDataset}', this.parentId),
            method : 'PUT',
            jsonData : service,
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
    
    editGUIService : function (idService){
    	Ext.Ajax.request({
            url : this.urlDatasetServiceIHM.replace('{idService}', idService),
            method : 'GET',
            scope : this,
            success : function (ret) {
            	var json = Ext.decode(ret.responseText);
            	
            	var up = new sitools.admin.datasets.services.datasetServicesProp({
            		action : 'modify',
            		record : json.guiServicePlugin,
            		parentPanel : this,          
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
    
    editSERVERService : function (idService){
    	var urlParent = this.urlDatasets + "/" + this.parentId;
    	Ext.Ajax.request({
            url : this.urlDatasetServiceSERVER.replace('{idService}', idService),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                var resourcePlugin = {};
                resourcePlugin.data = json.resourcePlugin;
                var up = new sitools.admin.resourcesPlugins.resourcesPluginsProp({
            		action : 'modify',
            		record : resourcePlugin,
            		parentPanel : this,          
            		urlResources : this.urlAllServicesSERVER,
//            		urlResourcesCRUD : this.httpProxyResources.url,
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
    }
});

Ext.reg('s-dataset_services', sitools.admin.datasets.services.datasetServicesCrud);
