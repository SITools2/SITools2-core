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

Ext.namespace('sitools.user.controller.modules.projectDescription');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.projectDescription.projectDescription', {
    extend : 'sitools.user.controller.modules.ModuleController',
    alias : 'sitools.user.modules.projectDescription',
    
    views : ['modules.projectDescription.projectDescription'],
    
    onLaunch : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        Ext.Ajax.request({
            method : "GET", 
            url : project.get('sitoolsAttachementForUsers'), 
            success : function (response) {
//              if (!showResponse(response, false)) {
//                  return;
//              }
                var json = Ext.decode(response.responseText);
                
                var view = Ext.create('sitools.user.view.modules.projectDescription.projectDescription', {
                    html : Ext.util.Format.htmlDecode(json.project.htmlDescription), 
                    autoScroll : true
                });
                
                this.setViewCmp(view);
                
                this.open(view);
            }, 
            failure : alertFailure, 
            scope : this
        });
    },
    
    initModule : function (moduleModel) {
        console.log("initModule");
        this.callParent(arguments);
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
