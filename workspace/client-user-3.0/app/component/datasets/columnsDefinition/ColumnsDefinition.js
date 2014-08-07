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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.user.component.datasets.columnsDefinition');

/**
 * Window that contains a tools to sort a store
 * 
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you
 *      can do a sort.
 * @class sitools.user.component.datasets.services.SorterService
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.columnsDefinition.ColumnsDefinition', {
    extend : 'sitools.user.core.Component',
//    controllers : ['sitools.user.controller.component.datasets.services.SorterServiceController'],
    alias : 'sitools.user.component.dataviews.services.columnsDefinitionService',
    statics : {
        getParameters : function () {
            return [];
        }
    },
    
    /**
     * 
     * @param config
     *  datasetName the name of the dataset
     *  datasetDescription the description of the dataset
     *  datasetId the id of the dataset
     *  datasetCm the columns of the dataset
     *  dictionaryMappings the dictionnary mapping    
     */
    init : function (config) {
        var windowConfig = {
            title : i18n.get('label.definitionTitle') + " : "
                    + config.datasetName,
            datasetName : config.datasetName,
            iconCls : "semantic",
            datasetDescription : config.datasetDescription,
            type : "defi",
            saveToolbar : true,
            toolbarItems : [],
            id : "defi" + config.datasetId
        };
        
        var columnDef = Ext.create("sitools.user.view.component.datasets.columnsDefinition.ColumnsDefinitionView", config);
        this.setComponentView(columnDef);
        this.show(columnDef, windowConfig);
    },
    
    executeAsService : function (config) {
        
        var configService = {
            datasetId : config.dataview.dataset.id,
            datasetDescription : config.dataview.dataset.description,
            datasetCm : config.dataview.columns,
            datasetName : config.dataview.dataset.name,
            dictionaryMappings : config.dataview.dataset.dictionaryMappings,
            preferencesPath : "/" + config.dataview.dataset.name,
            preferencesFileName : "semantic"
        };
        
        this.init(configService);
    }
});