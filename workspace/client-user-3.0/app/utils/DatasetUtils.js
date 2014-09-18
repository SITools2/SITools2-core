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
/*global Ext, sitools, i18n,document */
Ext.namespace("sitools.user.utils");

/**
 * A static method to transform a parameter to a sitools component.
 * @static
 * @param {} parameter the parameter as stored in the formParameter Model
 * @param {string} dataUrl the Url to request eventual data 
 * @param {string} formId the main formId
 * @param {} datasetCm the dataset Column model 
 * @param {string} context should be dataset or project. 
 *  @param {formComponentsPanel} the parent form
 * @return {Ext.Container} the container to include into form
 */
Ext.define('sitools.user.utils.DatasetUtils', {
    singleton : true,
    
    openDataset : function (url, extraCmpConfig) {
    	Ext.Ajax.request({
    		method : "GET", 
    		url : url, 
    		success : function (ret) {
                var Json = Ext.decode(ret.responseText);
//                if (showResponse(ret)) {
                var dataset = Json.dataset;
	            var componentCfg, javascriptObject;
	            var windowConfig = {
	                datasetName : dataset.name, 
	                saveToolbar : true, 
	                toolbarItems : []
	            };
	            
                javascriptObject = Desktop.getNavMode().getDatasetOpenMode(dataset);
            
                var datasetViewComponent  = Ext.create(javascriptObject);
                datasetViewComponent.create(Desktop.getApplication());
                
                Ext.apply(windowConfig, {
                	winWidth : 900, 
                	winHeight : 400,
                	title : i18n.get('label.dataTitle') + " : " + dataset.name, 
                	id : dataset.id, 
                	iconCls : "dataviews"
                });
                
                Ext.apply(dataset, extraCmpConfig);
                datasetViewComponent.init(dataset, windowConfig);
                
//                
//                componentCfg = {
//                    dataUrl : dataset.sitoolsAttachementForUsers,
//                    datasetId : dataset.Id,
//                    datasetCm : dataset.columnModel, 
//                    datasetName : dataset.name,
//                    dictionaryMappings : dataset.dictionaryMappings,
//                    datasetViewConfig : dataset.datasetViewConfig, 
//                    preferencesPath : "/" + dataset.datasetName, 
//                    preferencesFileName : "datasetOverview", 
//                    sitoolsAttachementForUsers : dataset.sitoolsAttachementForUsers
//                };
//            
//                Ext.applyIf(componentCfg, extraCmpConfig);
//				SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
    		}
    	});
    },
    
    openDefinition : function (url) {
    	
    	Ext.Ajax.request({
    		method : "GET", 
    		url : url, 
    		success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                
                var dataset = Json.dataset;
	            
	            var columnDefinition  = Ext.create("sitools.user.component.datasets.columnsDefinition.ColumnsDefinition");
	            columnDefinition.create(Desktop.getApplication());
	            var configService = {
	            		datasetId : dataset.id,
	            		datasetDescription : dataset.description,
	            		datasetCm : dataset.columnModel,
	            		datasetName : dataset.name,
	            		dictionaryMappings : dataset.dictionaryMappings,
	            		preferencesPath : "/" + dataset.name,
	            		preferencesFileName : "semantic"
	            };
	            columnDefinition.init(configService);
    		}
    	});
    	
    }
    
});
