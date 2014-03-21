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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.modules');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.modules.formsModule', {
    alias : 'sitools.user.modules.formsModule',
    
    constructor : function () {
        var urlFormsModule = projectGlobal.sitoolsAttachementForUsers + "/forms";
        var storeFormDs = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            remoteSort : true,
            url : urlFormsModule,
            // sortField: 'name',
            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'parent',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'css',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'width',
                type : 'numeric'
            }, {
                name : 'height',
                type : 'numeric'
            }, {
                name : 'parameters'
            }, {
                name : 'authorized' 
            }, {
                name : 'parentUrl',
                type : 'string'
            }, {
                name : 'zones'            
            }], 
            autoLoad : true
        });

        var cmFormDs = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : false
            // columns are not sortable by default
            },
            columns : [{
                header : "",
                dataIndex : 'authorized',
                renderer : function (value) {
                    if (value === "false") {
                        return "<img height=\"15\" src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png'>";
                    }
                }, 
                width : 20
            }, {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 350
            } ]
        });

        var tbarFormDs = {
            xtype : 'toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [ {
                text : i18n.get('label.viewForm'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
                handler : function () {
                    var rec = this.gridFormsDs.getSelectionModel().getSelected();
                    if (Ext.isEmpty(rec)) {
                        return;
                    }
                    this.showDetail(rec);
                },
                xtype : 's-menuButton'
            } ]
        };
        var smFormDs = Ext.create('Ext.selection.RowModel');

        this.gridFormsDs = new Ext.grid.GridPanel({
            title : i18n.get("label.datasetForm"), 
            store : storeFormDs, 
            cm : cmFormDs, 
            sm : smFormDs,
            region : 'center',
            tbar : tbarFormDs,
            frame : true,
            viewConfig : {
                forceFit : true
            },
            listeners : {
                scope : this, 
                'rowdblClick' : function (grid, rowIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
                    this.showDetail(rec);
                }
            }, 
            flex : 1
        });
        
        var urlFormsMultiDs = projectGlobal.sitoolsAttachementForUsers + "/formsProject";
        
        var storeFormsMultiDs = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            remoteSort : true,
            url : urlFormsMultiDs,
            // sortField: 'name',
            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'parent',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'css',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'width',
                type : 'numeric'
            }, {
                name : 'height',
                type : 'numeric'
            }, {
                name : 'parameters'
            }, {
                name : 'authorized' 
            }, {
                name : 'parentUrl',
                type : 'string'
            }, {
                name : 'properties'
            }, {
                name : 'urlServicePropertiesSearch', 
                type : 'string'
            }, {
                name : 'urlServiceDatasetSearch', 
                type : 'string'
            }, {
                name : 'collection'
            }, {
                name : 'dictionary'
            }, {
                name : 'nbDatasetsMax', 
                type : 'numeric'
            }, {
                name : 'zones'
            }], 
            autoLoad : true
        });

        var cmFormsMultiDs = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : false
            // columns are not sortable by default
            },
            columns : [{
                header : "",
                dataIndex : 'authorized',
                renderer : function (value) {
                    if (value === "false") {
                        return "<img height=\"15\" src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png'>";
                    }
                }, 
                width : 20
            }, {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 350
            } ]
        });

        var smFormsMultiDs = Ext.create('Ext.selection.RowModel');
        
        var tbarFormsMultiDs = {
            xtype : 'toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [ {
                text : i18n.get('label.viewForm'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
                handler : function () {
                    var rec = this.gridFormsMultiDs.getSelectionModel().getSelected();
                    if (Ext.isEmpty(rec)) {
                        return;
                    }
                    this.showDetailMultiDs(rec);
                },
                xtype : 's-menuButton'
            } ]
        };
        
        this.gridFormsMultiDs = new Ext.grid.GridPanel({
            title : i18n.get("label.projectForm"), 
            store : storeFormsMultiDs, 
            cm : cmFormsMultiDs, 
            sm : smFormsMultiDs,
            frame : true,
            region : 'south',
            height : 250,
            tbar : tbarFormsMultiDs, 
            viewConfig : {
                forceFit : true
            },
            listeners : {
                scope : this, 
                'rowdblClick' : function (grid, rowIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
                    this.showDetailMultiDs(rec);
                }
            }, 
            flex : 1
        });
        
        this.containerPanel = new Ext.Panel({
            layout : 'border',
            items : [ this.gridFormsDs, this.gridFormsMultiDs ]
        });
        
        this.tbar = {
                xtype : 'toolbar',
                cls : 'services-toolbar',
                height : 15,
                defaults : {
                    scope : this,
                    cls : 'services-toolbar-btn'
                },
                items : [ ]
        };
        
        sitools.user.modules.formsModule.superclass.constructor.call(this, Ext.apply({
            layout : 'fit',
            items : [ this.containerPanel ]
            
        }));

    }
});

Ext.extend(sitools.user.modules.formsModule, Ext.Panel, {
    showDetail : function (rec) {
        if (rec.data.authorized === "false") {
			return;
        }
        Ext.Ajax.request({
            url : rec.data.parentUrl,
            method : 'GET', 
            success : function (response) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = json.dataset;
                    var jsObj = SitoolsDesk.navProfile.getFormOpenMode();
                    
                    var componentCfg = {
                        dataUrl : dataset.sitoolsAttachementForUsers,
                        dataset : dataset,
                        formId : rec.data.id,
                        formName : rec.data.name,
                        formParameters : rec.data.parameters,
                        formWidth : rec.data.width,
                        formHeight : rec.data.height, 
                        formCss : rec.data.css, 
                        preferencesPath : "/" + dataset.name + "/forms", 
                        preferencesFileName : rec.data.name,
                        formZones : rec.data.zones
                    };

                    SitoolsDesk.navProfile.addSpecificFormParameters(componentCfg, dataset);
                    
                    var windowSettings = {
                        datasetName : dataset.name, 
                        type : "form", 
                        title : i18n.get('label.forms') + " : " + dataset.name + "." + rec.data.name, 
                        id : "form" + dataset.id + rec.data.id, 
                        saveToolbar : true, 
                        iconCls : "form"
                    };
                    
                    SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
                    return;
                }
                catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                    return;
                }
                
            }, 
            failure : function () {
                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noActiveDatasetFound'));
                return;
            }
        });
    }, 
    showDetailMultiDs : function (rec) {
		if (Ext.isEmpty(rec)) {
			return;
		}
		var jsObj = sitools.user.component.forms.projectForm;

        var componentCfg = {
            formId : rec.data.id,
            formName : rec.data.name,
            formParameters : rec.data.parameters,
            formWidth : rec.data.width,
            formHeight : rec.data.height, 
            formCss : rec.data.css, 
            properties : rec.data.properties, 
            urlServicePropertiesSearch : rec.data.urlServicePropertiesSearch, 
            urlServiceDatasetSearch : rec.data.urlServiceDatasetSearch, 
            dictionaryName : rec.data.dictionary.name,
            nbDatasetsMax : rec.data.nbDatasetsMax, 
            preferencesPath : "/formProjects", 
            preferencesFileName : rec.data.name,
            formZones : rec.data.zones
        };
        var windowSettings = {
            type : "formProject", 
            title : i18n.get('label.forms') + " : " + rec.data.name + ", Collection " + rec.data.collection.name, 
            id : "formProject"  + rec.data.id, 
            saveToolbar : true, 
            datasetName : rec.data.name, 
            winWidth : 600, 
            winHeight : 600, 
            iconCls : "form"
        };
        
        SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
        return;
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
});
