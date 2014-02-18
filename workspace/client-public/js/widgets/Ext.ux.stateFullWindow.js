/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n,document,projectGlobal,userStorage*/
Ext.ns('Ext.ux');
Ext.ux.stateFullWindow = Ext.extend(Ext.Window, {
	alias : 'widget.statewindow',
    saveSettings : function (componentSettings, forPublicUser) {
	    if (Ext.isEmpty(userLogin)) {
		    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
		    return;
	    }
	    var position = Ext.encode(this.getPosition(true));
	    var size = Ext.encode(this.getSize());

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
	    
	    var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + projectGlobal.projectName;
	    
	    var filePath = componentSettings.preferencesPath;
	    var fileName = componentSettings.preferencesFileName;
	    if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
	    	return;
	    }
	    
	    filePath = baseFilePath + filePath;
	    
	    if (forPublicUser) {
	    	publicStorage.set(fileName, filePath, putObject);
	    }
	    else {
	    	userStorage.set(fileName, filePath, putObject);
	    }
	    return putObject;
    }, 
    //Change the posistion when maximizing the window (according to the desktop position
    maximize : function(){
        if(!this.maximized){
            this.expand(false);
            this.restoreSize = this.getSize();
            this.restorePos = this.getPosition(true);
            if (this.maximizable){
                this.tools.maximize.hide();
                this.tools.restore.show();
            }
            this.maximized = true;
            this.el.disableShadow();

            if(this.dd){
                this.dd.lock();
            }
            if(this.collapsible){
                this.tools.toggle.hide();
            }
            this.el.addClass('x-window-maximized');
            this.container.addClass('x-window-maximized-ct');

            this.setPosition(0,0);
            this.fitContainer();
            this.fireEvent('maximize', this);
        }
        return this;
    }
    
});
