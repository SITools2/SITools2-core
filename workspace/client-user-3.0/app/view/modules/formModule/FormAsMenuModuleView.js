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
        
    	this.formStore = Ext.StoreManager.lookup('formAsMenuModule_FormStore');
    	this.formMultiDsStore = Ext.StoreManager.lookup('formAsMenuModule_MultiDsFormStore');
    	
    	if (!Ext.isEmpty(this.formStore) && !Ext.isEmpty(this.formMultiDsStore)) {
    	    this.items = this.getMenuItems();
    	}

        this.callParent(arguments);
    },
    onLoadDatasetsForms : function (store, records, successful) {
		var itemsArray = Ext.Array.toArray(this.items);
        this.removeAll();
        this.add(this.getFormMenuItems());
        this.add(itemsArray);
	},
	onLoadMultiDSForms : function (store, records, successful) {
	    this.add(this.getMultiDsFormMenuItems());
	},
	getMenuItems: function () {
	    return Ext.Array.union(this.getFormMenuItems(), this.getMultiDsFormMenuItems());
	},
	getFormMenuItems: function () {
	    var menuItems = [];
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
        return menuItems;
	},
	getMultiDsFormMenuItems: function () {
	    var menuItems = [];
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
        return menuItems;
	},
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : Ext.emptyFn
});
