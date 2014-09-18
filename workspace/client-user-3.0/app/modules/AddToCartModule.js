/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.modules');
/**
 * datasetExplorer Module
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.AddToCartModule', {
    extend : 'sitools.user.core.Module',
    
    controllers : ['sitools.user.controller.modules.addToCartModule.AddToCartController'],

    init : function () {
        var view = Ext.create('sitools.user.modules.AddToCartModuleView');
        
        this.show(view);

        this.callParent(arguments);
    },

    createViewForDiv : function () {
    	return Ext.create('sitools.user.modules.AddToCartModuleView');
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    },
    
    getParameters : function () {
        var projectProp = Ext.getCmp(ID.COMPONENT_SETUP.PROJECT);
        var projectId = projectProp.formProject.down('hidden[name=id]').getValue();
        
        this.urlParents = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');
        this.resourcesUrlPart = loadUrl.get('APP_RESOURCES_URL');
        this.urlResources = loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_RESOURCES_URL') + '/classes';
        this.appClassName = "fr.cnes.sitools.project.ProjectApplication" ;
        
//        var url = this.urlParents + "/" + projectId + this.resourcesUrlPart + this.urlParentsParams;
        var url = this.urlParents + "/" + projectId + this.resourcesUrlPart;
        
        return [{
            jsObj : "sitools.component.datasets.selectItems",
            config : {
                height : 200,
                layout : {
                    type : 'hbox',
                    pack : 'start',
                    align : 'stretch'
                },
                grid1 : Ext.create('Ext.grid.Panel', {
                    width : 200,
                    forceFit : true,
                    margins : {
                        top : 0,
                        right : 2,
                        bottom : 0,
                        left : 0
                    },
                    store : Ext.create('Ext.data.JsonStore', {
                        fields : [ 'id', 'name', 'description', 'parameters' ],
                        idProperty : 'id',
                        url : url,
                        root : "data",
                        autoLoad : true
                    }),
                    columns : [{
                        header : i18n.get('Project Services'),
                        dataIndex : 'name',
                        sortable : true
                    }]
                }),
                grid2 : Ext.create('Ext.grid.Panel', {
                    selModel : Ext.create('Ext.selection.RowModel'),
                    width : 200,
                    forceFit : true,
                    margins : {
                        top : 0,
                        right : 0,
                        bottom : 0,
                        left : 2
                    },
                    store : new Ext.data.JsonStore({
                        fields : [ 'id', 'name', 'description', 'url', 'label' ],
                        idProperty : 'id',
                        listeners : {
                            add : function (store, records) {
                                Ext.each(records, function (rec) {
                                    Ext.each(rec.data.parameters, function (param) {
                                        if (param.name == "url") {
                                            rec.data.url = param.value;
                                        }
                                    });
                                });
                            }
                        }
                    }),
                    columns : [{
                        header : i18n.get('Order Services'),
                        dataIndex : 'name',
                        width : 80,
                        sortable : true
                    }, {
                        header : i18n.get('label.labelEditable') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
                        dataIndex : 'label',
                        width : 80,
                        editor :{
                            xtype : 'textfield'
                        }
                    }]
                }),
                name : "orderServices",
                value : [],
                getValue : function () {
                    var orderServices = [];
                    var store = this.grid2.getStore();

                    store.each(function (srv) {
                        var service = {};
                        service.id = srv.data.id;
                        service.name = srv.data.name;
                        service.url = srv.data.url;
                        service.label = (!Ext.isEmpty(srv.data.label)) ? srv.data.label : srv.data.name;
                        orderServices.push(service);
                    });
                    var orderServicesString = Ext.util.JSON.encode(orderServices);
                    return orderServicesString;
                },
                setValue : function (value) {
                    var orderServices = Ext.util.JSON.decode(value);
                    Ext.each(orderServices, function (service) {
                        var serviceRecord = new Ext.data.Record(service);
                        this.grid2.getStore().add(serviceRecord);
                    }, this);
                    this.value = value;
                }
            }
        }];
    }
});

