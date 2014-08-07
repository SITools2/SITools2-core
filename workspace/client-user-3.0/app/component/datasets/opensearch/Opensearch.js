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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.component.datasets.opensearch.Opensearch', {
    extend : 'sitools.user.core.Component',
    
    requires : ['sitools.user.view.component.datasets.opensearch.OpensearchView'],
    
    controllers : ['sitools.user.controller.component.datasets.opensearch.OpensearchController'],
    
    /**
     * 
     * @param config
     *  datasetId the dataset id
     *  dataUrl the dataset url 
     *  datasetName the dataset name 
     *  
     */
    init : function (config) {
        
    	var windowSettings = {
    	    title : i18n.get('label.opensearch') + " : " + config.datasetName,
	        id : "openSearch" + config.datasetId, 
            iconCls : "openSearch",
            type : "openSearch"
        };
    	
    	var opensearch = Ext.create('sitools.user.view.component.datasets.opensearch.OpensearchView', {
            dataUrl : config.dataUrl,
            datasetName : config.datasetName,
            datasetId : config.datasetId
        });

        this.setComponentView(opensearch);
        this.show(opensearch, windowSettings);
    },

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/opensearch",
            preferencesFileName : this.id
        };

    }
});