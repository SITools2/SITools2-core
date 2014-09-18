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
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.define('sitools.user.controller.core.SitoolsController', {

    extend : 'Ext.app.Controller',

    stores : [ 'ProjectStore', 'ModulesStore' ],

    init : function () {
        var me = this, desktopCfg;

        this.control({
            
            'moduleTaskBar #sitoolsButton' : {
				click : function (btn) {
					btn.toggle();
					this.dvModules = Ext.create('sitools.user.view.header.ModuleDataView');
					this.dvModules.show();
				}
			},
			
			'moduleDataview' : {
				blur : function (window) {
					window.hide();
					Desktop.getDesktopEl().unmask();
				},
				boxready : function (window) {
					window.center();
					window.focus();
					
				}
			},
			
			'moduleDataview dataview' : {
				itemclick : function (dataview, moduleRecord, item, index, event) {
					this.openModule(moduleRecord, event);
					dataview.up('window').hide();
					Desktop.getDesktopEl().unmask();
				}
			},
			
			'moduleDataview toolbar button[name=versionBtn]' : {
				click : function (btn) {
					btn.up('moduleDataview').close();
					Ext.create('sitools.public.version.Version').show();
				}
			},
			
			'moduleDataview toolbar button[name=helpBtn]' : {
				click : function (btn) {
					btn.up('moduleDataview').close();
					Ext.create('sitools.public.utils.Help').show();
				}
			}
        });

        this.getApplication().on('projectInitialized', this.loadProject, this);
        this.getApplication().on('footerLoaded', Desktop.loadPreferences, this);
        this.getApplication().on('footerLoaded', Desktop.loadModulesInDiv, this);
        
    },
    
    // 8
    loadProject : function () {
        var url = Project.getSitoolsAttachementForUsers();
        var store = this.getStore("ProjectStore");
        store.setCustomUrl(url);
        store.load({
            scope : this,
            callback : function (records, operation, success) {
            	this.loadModules();
            }
        });
    },
    
    // 9
    loadModules : function () {
    	var store = this.getStore("ModulesStore");
    	var url = Project.getSitoolsAttachementForUsers() + loadUrl.get('APP_PROJECTS_MODULES_URL');
        store.setCustomUrl(url);
        
        store.load({
        	scope : this,
        	callback : function (records, operation, success) {
                this.getApplication().noticeProjectLoaded();
            }
        })
    },

    openModule : function (moduleModel, event) {
        var module = Ext.create(moduleModel.data.xtype);
        module.create(this.getApplication(), moduleModel);
        module.init(event);
    },
    
    openComponent : function (componentClazz, componentConfig, windowConfig) {
        var component = Ext.create(componentClazz);
        component.create(this.getApplication());
        component.init(componentConfig, windowConfig);
    },
    
    onOpenModule : function (button, e, opts) {
        // get the module from the button
        var module = button.module;
        this.openModule(module);
    }

});