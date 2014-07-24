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
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/

Ext.namespace('sitools.user.component.dataviews.services');


sitools.user.component.dataviews.services.GuiServicesStore = Ext.extend(Ext.data.JsonStore, {
    
    
 constructor : function (config) {
        
        Ext.apply(config, {
            root : 'data',
            restful : true,
            proxy : new Ext.data.HttpProxy({
                url : config.datasetUrl + "/services/gui",
                restful : true,
                method : 'GET'
            }),
            idProperty : 'id',
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
                name : 'label',
                type : 'string'
            }, {
                name : 'xtype',
                type : 'string'
            }, {
                name : 'author',
                type : 'string'
            }, {
                name : 'version',
                type : 'string'
            }, {
                name : 'icon',
                type : 'string'
            }, {
                name : 'parameters'                    
            }, {
                name : 'priority',
                type : 'int'
            }, {
                name : 'dataSetSelection'
            }/*, {
                name : 'dependencies'
            }*/, {
                name : 'defaultVisibility',
                type : 'boolean'
            }, {
                name : 'parametersMap'
            }]
        });
        
        sitools.user.component.dataviews.services.GuiServicesStore.superclass.constructor.call(this, config);
     }
});
    
