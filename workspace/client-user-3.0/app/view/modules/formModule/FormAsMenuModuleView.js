/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.view.modules.formModule');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.view.modules.formModule.FormAsMenuModuleView', {
    extend : 'Ext.menu.Menu',
    alias : 'widget.formsAsMenuModuleView',
    
    menuMultiDsFormLoaded : false, 
    formDsLoaded : false, 
    formMultiDsLoaded : false,
    plain : true,
    border : false,
    
    initComponent : function () {
        
    	var project = Ext.getStore('ProjectStore').getProject();
        
        this.formStore = Ext.create('sitools.user.store.FormStore', {
    		 autoLoad : true, 
        	 listeners : {
     			scope : this, 
     			load : this.onLoadDatasetsForms
     		}
        });
        this.formStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/forms');
   	
        this.formMultiDsStore = Ext.create('sitools.user.store.FormProjectStore', {
        	autoLoad : true,
            listeners : {
    			scope : this, 
    			load : this.onLoadMultiDSForms
    		}
        });
        this.formMultiDsStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/formsProject');
    	
        this.callParent(arguments);
    },
    
    onLoadDatasetsForms : function (store, records, successful) {
		var menuItems = [];
		
//		Ext.each(records, function (record) {
//			record.set('type', 'component');
//			record.set('formType', 'simpleDs');
//			record.set('xtype', this.moduleXtype);
//			
//			this.storeDataview.add(record);
//		}, this);
		
		this.formStore.each(function (rec) {
			menuItems.push(Ext.create('Ext.menu.Item', {
				text : rec.get("name"),
				cls : 'menuItemCls',
				iconCls : 'form',
				sitoolsType : 'datasetForm',
				rec : rec
			}), {
            	xtype : 'menuseparator',
            	separatorCls : 'customMenuSeparator'
    		});
		}, this);

		if (menuItems.length > 0) {
		    menuItems.unshift(Ext.create('Ext.menu.Item', {
		        text : i18n.get('label.forms'),
		        cls : 'userMenuCls',
		        plain : false,
	        	canActivate : false
		    }), {
            	xtype : 'menuseparator',
            	separatorCls : 'customMenuSeparator'
    		});
		}
		
		this.add(menuItems);
	},
	
	onLoadMultiDSForms : function (store, records, successful) {
		var menuItems = [];
		
//		Ext.each(records, function (record) {
//			record.set('type', 'component');
//			record.set('formType', 'multiDs'); // used to open form from moduleDataview
//			record.set('xtype', this.moduleXtype);
//			this.storeDataview.add(record);
//		}, this);
		
		this.formMultiDsStore.each(function (rec) {
			menuItems.push(Ext.create('Ext.menu.Item', {
				text : rec.get("name"), 
				cls : 'menuItemCls',
				iconCls : 'form',
				rec : rec,
				sitoolsType : 'projectForm'
			}), {
            	xtype : 'menuseparator',
            	separatorCls : 'customMenuSeparator'
    		});
		}, this);

		if (menuItems.length > 0) {
			menuItems.unshift(Ext.create('Ext.menu.Item', {
		        text : i18n.get('label.projectForm'),
		        cls : 'userMenuCls',
		        plain : false,
	        	canActivate : false
		    }), {
            	xtype : 'menuseparator',
            	separatorCls : 'customMenuSeparator'
    		});
		}
		
		this.add(menuItems);
		this.formsLoaded = true;
		this.menuMultiDsFormLoaded = true;	
		
	},
	
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : Ext.emptyFn
});
