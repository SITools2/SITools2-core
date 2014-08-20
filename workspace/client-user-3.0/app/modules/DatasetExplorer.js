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

Ext.namespace('sitools.user.modules.datasetExplorer');
/**
 * datasetExplorer Module
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.DatasetExplorer', {
    extend : 'sitools.user.core.Module',
    
    requires : ['sitools.user.controller.modules.datasetExplorer.DatasetExplorer'],
    controllers : ['sitools.user.controller.modules.datasetExplorer.DatasetExplorer'],

    init : function () {
        this.setViewCmp(Ext.create('sitools.user.view.modules.datasetExplorer.DatasetExplorer'));
        
        this.show(this.getViewCmp());

        this.callParent(arguments);
    },
    
    createViewForDiv : function () {
    	return Ext.create('sitools.user.view.modules.datasetExplorer.DatasetExplorer');
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

    }
});
