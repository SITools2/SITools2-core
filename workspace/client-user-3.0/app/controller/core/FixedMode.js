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
Ext.define('sitools.user.controller.core.FixedMode', {

    extend : 'sitools.user.controller.core.NavigationMode',
    
    openComponent : function (view, windowConfig) {
    	Ext.applyIf(windowConfig, {
    		x : (!Ext.isEmpty(windowConfig.position)) ? windowConfig.position.x : undefined,
    		y : (!Ext.isEmpty(windowConfig.position)) ? windowConfig.position.y : undefined,
    		width : (!Ext.isEmpty(windowConfig.size)) ? windowConfig.size.width : undefined,
    		height : (!Ext.isEmpty(windowConfig.size)) ? windowConfig.size.height : undefined,
    		specificType : 'componentWindow'
    	});
    	
    	Ext.apply(windowConfig, this.getStatefullComponentConfig());
        this.getApplication().getController('DesktopController').createPanel(view, windowConfig);
    },
    
    openModule : function (view, module) {
        var windowConfig = {
    		id: module.get('id'),
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            iconCls : module.get('icon'),
            label : module.get('label'),
            specificType : 'moduleWindow'
        };
        
        Ext.apply(windowConfig, this.getStatefullWindowConfig());
        this.getApplication().getController('DesktopController').createPanel(view, windowConfig);
    },
    
    getFormOpenMode : function () {
        return sitools.user.component.DatasetOverview;
    },
    
    getDesktopSettings : function (forPublicUser) {
        var desktopSettings = [];
        
//        var activePanel = this.getApplication().getController('DesktopController').desktopView.getActiveWindow();
        
    	Ext.WindowManager.each(function (window) {
            var componentSettings;
            if (!Ext.isEmpty(window.specificType) && (window.specificType === 'componentWindow' || window.specificType === 'moduleWindow')) {
                // Bug 3358501 : add a test on Window.saveSettings.
                if (Ext.isFunction(window.saveSettings)) {
//                    var component = window.get(0);
                    var component = window.items.items[0];

                    componentSettings = component._getSettings();
                    componentSettings.preferencesFileName = this.name;
                    desktopSettings.push(window.saveSettings(componentSettings, forPublicUser));
                }
            }
        });
        return desktopSettings;
    },
    
    getStatefullWindowConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    // TODO find a better way to set the right Y position
			    var position = {
		    		x : this.getX(),
		    		y : this.getY()
			    };
			    
			    var size = {
		    		height : this.getHeight(),
		    		width : this.getWidth()
			    };

			    var putObject = {};

			    // putObject['datasetId'] = datasetId;
			    // putObject['componentType'] = componentType;
			    putObject.componentSettings = componentSettings;

			    putObject.windowSettings = {};
			    putObject.windowSettings.size = size;
			    putObject.windowSettings.position = position;
			    putObject.windowSettings.specificType = this.specificType;
			    putObject.windowSettings.moduleId = this.getId();
			    putObject.windowSettings.typeWindow = this.typeWindow;
			    putObject.windowSettings.maximized = this.maximized;
			    
			    var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName();
			    
			    var filePath = componentSettings.preferencesPath;
			    var fileName = componentSettings.preferencesFileName;
			    if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
			    	return;
			    }
			    
			    filePath = baseFilePath + filePath;
			    
			    if (forPublicUser) {
			    	PublicStorage.set(fileName, filePath, putObject);
			    }
			    else {
			    	UserStorage.set(fileName, filePath, putObject);
			    }
			    return putObject;
		    }
		};
	},
	
	getStatefullComponentConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    // TODO find a better way to set the right Y position
			    var position = {
		    		x : this.getX(),
		    		y : this.getY()
			    };
			    
			    var size = {
		    		height : this.getHeight(),
		    		width : this.getWidth()
			    };

			    var putObject = {};
			    putObject.componentSettings = componentSettings;

			    putObject.windowSettings = {};
			    putObject.windowSettings.size = size;
			    putObject.windowSettings.position = position;
			    putObject.windowSettings.specificType = this.specificType;
			    putObject.windowSettings.componentId = this.getId();
			    putObject.windowSettings.typeWindow = this.typeWindow;
			    putObject.windowSettings.maximized = this.maximized;
			    
			    var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName();
			    
			    var filePath = componentSettings.preferencesPath;
			    var fileName = componentSettings.preferencesFileName;
			    if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
			    	return;
			    }
			    
			    filePath = baseFilePath + filePath;
			    
			    if (forPublicUser) {
			    	PublicStorage.set(fileName, filePath, putObject);
			    }
			    else {
			    	UserStorage.set(fileName, filePath, putObject);
			    }
			    return putObject;
		    }
		};
	}
});