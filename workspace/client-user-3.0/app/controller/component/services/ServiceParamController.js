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

Ext.namespace('sitools.user.controller.component.services');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.component.services.ServiceParamController', {
    extend : 'Ext.app.Controller',
    
    views : ['component.services.ServiceParamView'],
    
    init : function () {
    	this.control({
    		'serviceParamView button#submit' : {
    			click : this.onCall
    		},
    		'serviceParamView button#cancel' : {
    			click : function(btn) {
    				var self = btn.up("serviceParamView");
    				self.up("component[specificType=componentWindow]").close();
    				if (!Ext.isEmpty(self.callback)) {
    					Ext.callback(self.callback, self, false);
    				}
    			}
    		}


		});
    },
	onCall : function (btn) {
		var self = btn.up("serviceParamView");
	    var method;
	    if (self.showMethod) {
	        var form = self.formParams.getForm();
	        method = form.findField("method").getValue();
	    }
	    else {
			method = self.defaultMethod;	
	    }
		
	    var runTypeUserInput;
	    if (self.showRunType) {
			runTypeUserInput = self.formParamsUserInput.getForm().findField("runTypeUserInput").getValue();
	    }
	    else {
			runTypeUserInput = self.runType;
	    }
	    var limit;
	
	    var userParameters = {};
	    if (!Ext.isEmpty(self.formParamsUserInput)) {
	        var formParams = self.formParamsUserInput.getForm();
	        Ext.iterate(formParams.getValues(), function (key, value) {
	            userParameters[key] = value;                
	        });
	    }
	    
	    Ext.each(self.parameters, function (param) {
	        if (param.type == "PARAMETER_IN_QUERY") {
	            userParameters[param.name] = param.value;
	        }
	    });
	    
	    self.serverServiceUtil.onResourceCallClick(self.resource, self.url, method, runTypeUserInput, limit, userParameters, self.postParameter, self.callback);
	    self.up("component[specificType=componentWindow]").close();
	}
});
