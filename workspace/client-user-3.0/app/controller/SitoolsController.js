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

    stores : [ 'ProjectStore', 'UserStore' ],

    init : function () {
        var me = this, desktopCfg;

        this.getApplication().on('projectInitialized', this.loadProject, this);
    },

    loadProject : function () {
        console.log('loadProject');
        var url = sitools.user.utils.Project.getSitoolsAttachementForUsers();
        var store = this.getStore("ProjectStore");
        store.setCustomUrl(url);
        store.load(
                {
                    scope : this,
                    callback: function(records, operation, success) {
                        this.getApplication().noticeProjectLoaded();
                    }
        });
        
        this.loadUser();
    },
    
    loadUser : function () {
        var storeUser = this.getStore("UserStore");
        
        var url = sitools.user.utils.Project.getSitoolsAttachementForUsers();
        storeUser.setCustomUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_USER_ROLE_URL'));
        storeUser.load({
            callback: function(records, operation, success) {
                // the operation object
                // contains all of the details of the load operation
                console.log(records);
            }
        });
    }
});