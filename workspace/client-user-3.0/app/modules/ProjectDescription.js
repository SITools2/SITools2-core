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

Ext.namespace('sitools.user.modules.projectDescription');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.ProjectDescription', {
    extend : 'sitools.user.core.Module',
    
    controllers : ['sitools.user.controller.modules.projectDescription.ProjectDescription'],
    
    init : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        Ext.Ajax.request({
            method : "GET",
            url : project.get('sitoolsAttachementForUsers'), 
            success : function (response) {
                var json = Ext.decode(response.responseText);
                
                this.view = Ext.create('sitools.user.view.modules.projectDescription.ProjectDescription', {
                    html : Ext.util.Format.htmlDecode(json.project.htmlDescription), 
                    autoScroll : true
                });
                
                this.show(this.view);
            }, 
            failure : alertFailure, 
            scope : this
        });
    },
    
    createViewForDiv : function () {
    	var project = Ext.getStore('ProjectStore').getProject();
    	
        Ext.Ajax.request({
            method : "GET",
            url : project.get('sitoolsAttachementForUsers'), 
            scope : this,
            success : function (response) {
                var json = Ext.decode(response.responseText);
                
                this.view = Ext.create('sitools.user.view.modules.projectDescription.ProjectDescription', {
                    html : Ext.util.Format.htmlDecode(json.project.htmlDescription), 
                    autoScroll : true
                });
                
            },
            callback : function (success) {
            	if (success) {
            		return this.view;
            	}
            },
            failure : alertFailure
        });
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
