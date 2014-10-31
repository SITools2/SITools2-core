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
/* global Ext, sitools, window */

Ext.define('sitools.user.store.ModulesStore', {
    extend : 'Ext.data.Store',
    model : 'sitools.user.model.ModuleModel',
    storeId : 'ModuleStore',
    proxy : {
        type : 'ajax',
        reader : {
            type : 'json',
            root : 'data',
            idProperty : 'id'
        }
    },
    listeners : {
    	load : function (store, records, success) {
    		var modulesStore = Ext.data.StoreManager.lookup("ProjectStore").getProject().modules();
    		
    		modulesStore.each(function (moduleFromProject) {
    			Ext.each(records, function (moduleToCheck) {
    				if (moduleFromProject.get('xtype') === moduleToCheck.get('xtype')) {
    					Ext.applyIf(moduleToCheck, moduleFromProject);
    					moduleToCheck.set('categoryModule', moduleFromProject.get('categoryModule'));
    					
    					store.add(moduleToCheck);
    					
    					if (!Ext.isEmpty(moduleFromProject.get('divIdToDisplay'))) { // adding modulesInDiv to Project
    						moduleToCheck.data.divIdToDisplay = moduleFromProject.data.divIdToDisplay;
    						Project.modulesInDiv.push(moduleToCheck);
    					}
    				}
    				
    			});
    		});
    		
    		
    	}
    },
    
    setCustomUrl : function (url) {
        this.getProxy().url = url;
    }
});