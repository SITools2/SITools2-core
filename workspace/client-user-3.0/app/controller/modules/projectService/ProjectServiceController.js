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

Ext.namespace('sitools.user.controller.modules.projectService');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.projectService.ProjectServiceController', {
    extend : 'Ext.app.Controller',
    
    views : ['modules.projectService.ProjectServiceView'],
    

    init : function () {
        this.control({
            'projectService actioncolumn' : {
                click : this.runJob
            }
        });
    },
    
    runJob : function (view, rowIndex, colIndex, item, e, record) {
    	var resource = record.getData();
    	
		var parameters = resource.parameters;
        var url = null, icon = null, method = null, runTypeUserInput = null;
        Ext.each(parameters, function (param) {
            switch (param.name) {
            case "methods":
                method = param.value;
                break;
            case "url":
                url = Project.sitoolsAttachementForUsers + param.value;
                break;
            case "runTypeUserInput":
                runTypeUserInput = param.value;
                break;
            case "image":
                icon = param.value;
                break;
			}
        }, this);
        
        view.up('projectService').serviceServerUtil.projectServiceController = this;
        view.up('projectService').serviceServerUtil.resourceClick(resource, url, method, runTypeUserInput, parameters, null, Ext.emptyFn);
	}
    
});
