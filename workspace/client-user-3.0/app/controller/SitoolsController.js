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
Ext.define('sitools.user.controller.SitoolsController', {

    extend : 'Ext.app.Controller',

    stores : [ 'ProjectStore' ],

    init : function () {
        var me = this, desktopCfg;

        this.control({
            'moduleTaskBar button' : {
                click : this.openModule
            }
        });

        this.getApplication().on('projectInitialized', this.loadProject, this);
    },

    loadProject : function () {
        var url = sitools.user.utils.Project.getSitoolsAttachementForUsers();
        var store = this.getStore("ProjectStore");
        store.setCustomUrl(url);
        store.load({
            scope : this,
            callback : function (records, operation, success) {
                this.getApplication().noticeProjectLoaded();
            }
        });
    },

    openModule : function (button, e, opts) {

        // get the module from the button
        console.log('TODO open ' + button.text);

        var module = button.module;
        console.log("Create module : " + module.xtype);
        
        var moduleController = this.getApplication().getController(module.xtype);
        moduleController.initModule(module);
        moduleController.onLaunch(this.getApplication());
    }

});