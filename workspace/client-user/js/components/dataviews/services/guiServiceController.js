/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Create A Toolbar from the currents dataset services
 * @required datasetId
 * @required datasetUrl
 * @cfg {Array} columnModel the dataset columnModel
 * @class sitools.user.component.dataviews.services.menuServicesToolbar
 * @extends Ext.Toolbar
 */
sitools.user.component.dataviews.services.GuiServiceController =  function (config) {
    
    Ext.apply(this, config);
    
    
    //Map<Colonne, Service> avec colonne = columnAlias et Service = l'objet Service
    this.guiServiceMap = new Ext.util.MixedCollection();
    
    this.guiServiceStore = new sitools.user.component.dataviews.services.GuiServicesStore({
        datasetUrl : this.datasetUrl,
        autoLoad : true,
        listeners : {
            scope : this,
            load : function (store, services, options) {
                //create a map of parameters for each guiservice
                Ext.each(services, function (service) {
                    var parameters = service.get("parameters");
                    if (!Ext.isEmpty(parameters)) {
                        var collection = new Ext.util.MixedCollection();
                        Ext.each(parameters, function (param) {
                            collection.add(param.name, param.value);
                        });
                        service.set("parametersMap", collection);
                    }
                });
                
                
                
                Ext.each(this.columnModel, function (column) {
                    if (!Ext.isEmpty(column.columnRenderer)) {
                        var featureTypeColumn = sitools.admin.datasets.columnRenderer.behaviorEnum.getColumnRendererCategoryFromBehavior(column.columnRenderer.behavior);
                        
                        var guiServiceWithColumn = null;
                        var guiServiceWithoutColumn = null;
                        
                        
                        Ext.each(services, function (service) {
                            var parameters = service.get("parametersMap");
                            if (!Ext.isEmpty(parameters)) {
                                var featureTypeService = parameters.get("featureType");
                                if (!Ext.isEmpty(featureTypeService) && featureTypeService === featureTypeColumn) {
                                    var columnAlias = parameters.get("columnImage");
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
                                    
            }
            
        }
        
    });
    sitools.user.component.dataviews.services.GuiServiceController.superclass.constructor.call(this);
};

Ext.extend(sitools.user.component.dataviews.services.GuiServiceController, Ext.util.Observable, {
    
    
    getService : function (columnAlias) {
        return this.guiServiceMap.get(columnAlias);
    },
    
    callGuiService : function (idService, record, columnAlias) {
        var service = this.guiServiceStore.getById(idService);
        if (Ext.isEmpty(service)) {
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.warning'),
                html : i18n.get("label.cannot-find-guiservice"),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
            return;
        }
        var guiServicePlugin = service.data;
        var JsObj = eval(guiServicePlugin.xtype);

        var config = Ext.applyIf(guiServicePlugin, {
            columnModel : this.dataview.getColumnModel(),
            store : this.dataview.getStore(),
            dataview : this.dataview,
            origin : this.origin,
            record : record,
            columnAlias : columnAlias
        });

        JsObj.executeAsService(config);            
    }
});
    
    
