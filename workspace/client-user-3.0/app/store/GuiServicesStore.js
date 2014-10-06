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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP
 */

Ext.define('sitools.user.store.GuiServicesStore', {
    extend : 'Ext.data.Store',
    model : 'sitools.user.model.GuiServiceModel',
    storeId : 'GuiServicesStore',
    
    requires : ['sitools.public.widget.datasets.columnRenderer.behaviorEnum'],

    config : {
        guiServiceMap : new Ext.util.MixedCollection()
    },
    
    constructor : function (config) {
        Ext.apply(config, {
            root : 'data',
            proxy : {
                url : config.datasetUrl + "/services/gui",
                type : 'ajax',
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            listeners : {
                scope : this,
                load : this.onLoad
            }
        });
        this.callParent([config]);
    },
    
    onLoad : function (store, services, success, opts) {
        this.createMapParamServices(services);
        
        //For each column, find the Gui_Service configured, if exists
        Ext.each(this.columnModel, function (column) {
            if (!Ext.isEmpty(column.columnRenderer)) {
                var featureTypeColumn = sitools.public.widget.datasets.columnRenderer.BehaviorEnum.getColumnRendererCategoryFromBehavior(column.columnRenderer.behavior);
                
                var guiServiceWithColumn = null;
                var guiServiceWithoutColumn = null;
                
                
                Ext.each(services, function (service) {
                    var parameters = service.get("parametersMap");
                    if (!Ext.isEmpty(parameters)) {
                        var featureTypeService = parameters.get("featureType");
                        if (!Ext.isEmpty(featureTypeService) && featureTypeService === featureTypeColumn) {
                            var columnAlias = parameters.get("columnAlias");
                            if (columnAlias === column.columnAlias) {
                                guiServiceWithColumn = service;
                            } else if (Ext.isEmpty(columnAlias)) {
                                guiServiceWithoutColumn = service;
                            }
                        }
                    }
                }, this);
                
                if (!Ext.isEmpty(guiServiceWithColumn)) {
                    this.guiServiceMap.add(column.columnAlias, guiServiceWithColumn);
                } else if (!Ext.isEmpty(guiServiceWithoutColumn)) {
                    this.guiServiceMap.add(column.columnAlias, guiServiceWithoutColumn);
                }
            }
        }, this);
        
        this.fireEvent("guiServicesLoaded");
    },
    
    /**
     * Get the service if exists configured on the given column identified
     * by its columnAlias
     * 
     * @param columnAlias
     *            {String} the columnAlias
     * @returns the service if exists configured on the given column
     *          identified by its columnAlias
     */
    getService : function (columnAlias) {
        return this.guiServiceMap.get(columnAlias);
    },
    


    /**
     * Create a collection of parameters for each services with parameter
     * and add it to the service record
     * 
     * @param services
     *            {Array} the list of services
     */
    createMapParamServices : function (services) {
      //  create a map of parameters for each guiservice
        Ext.each(services, function (service) {
            var parameters = service.get("parameters");
            //if the parameters are empty, try to get the defaultParameters
            if (Ext.isEmpty(parameters)) {
                try {
                    var JsObj = Ext.create(service.get("xtype"));
                    if (Ext.isFunction(JsObj.getDefaultParameters)) {
                        parameters = JsObj.getDefaultParameters();
                    }
                } catch (e) {
                    service.set("parametersMap", new Ext.util.MixedCollection());
                }
            }
            if (!Ext.isEmpty(parameters)) {
                var collection = new Ext.util.MixedCollection();
                Ext.each(parameters, function (param) {
                    collection.add(param.name, param.value);
                });
                service.set("parametersMap", collection);
            }
        });
    }
});
