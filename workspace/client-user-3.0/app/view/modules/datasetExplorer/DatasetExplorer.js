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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse*/

Ext.namespace('sitools.user.view.modules.datasetExplorer');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.modules.datasetExplorer.DatasetExplorer', {
    extend : 'Ext.tree.Panel',
    layout : 'fit',
    itemId : 'datasetExplorer',
    
    border : false,
    bodyBorder : false,
    
    initComponent : function () {
        
        var project = Ext.getStore('ProjectStore').getProject();
        this.store = Ext.create("sitools.user.store.DatasetTreeStore");
        
        this.setRootNode({
            text : 'datasets',
            leaf : 'false'
        });  
        
        Ext.Ajax.request({
            url : project.get('sitoolsAttachementForUsers') + '/datasets',
            method : 'GET',
            scope : this,
            success : function (response) {
                var datasets = Ext.decode(response.responseText).data;
                Ext.each(datasets, function(dataset){
                    dataset.text = dataset.name;
                    this.getRootNode().appendChild(dataset);
                }, this);
            }
        });
        
        
        this.callParent(arguments);
    },
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id,
            xtype : this.$className
        };

    }
});
