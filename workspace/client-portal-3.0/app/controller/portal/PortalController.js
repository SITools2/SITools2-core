/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*!
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */

/*global Ext, i18n, locale:true, sitools, loadUrl, userLogin, DEFAULT_PREFERENCES_FOLDER*/
/*
 * @include "../../client-public/js/siteMap.js"
 * @include "portal/portal.js"
 */

var portal;
var userLogin = Ext.util.Cookies.get('userLogin');

Ext.namespace('sitools.clientportal.controller.portal');

Ext.define('sitools.clientportal.controller.portal.PortalController', {
    extend : 'Ext.app.Controller',
    projects : null,
    languages : null,
    preferences : null,
    autoChainAjaxRequest : true,
    
    views : ['sitools.clientportal.view.portal.PortalView'],
    
    init : function () {

        this.control({
            'dataview[cls=projectDataView]' : {
                itemclick : this.openProject
            },
            'dataview[name="viewProjectPrivate"]' : {
                itemclick : this.openProject
            },
            'button[name="editProfileBtn"]' : {
                click : function (button, e) {
                    var callback = Ext.Function.bind(this.onEditProfile, this, [button.identifier, button.edit]);
                    sitools.public.utils.LoginUtils.editProfile(callback);
                }
            }
        });
    },
    
    openProject : function (dataView, record, item, index, node, e) {
        // get the projectId
        
        var data = record.data;
        var projectName = data.name;
        var authorized = data.authorized;
        var maintenance = data.maintenance;
        var maintenanceText = data.maintenanceText;
        var urlProject = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') +  '/index.html?project=' +projectName;
        
        if (authorized) {
            if (!maintenance) {
                window.open(urlProject);
            }
            else {
                if(Ext.isEmpty(maintenanceText)) {
                    maintenanceText = i18n.get("label.defaultMaintenance.text");
                }
                var alertWindow = Ext.create('Ext.window.Window', {
                    title : i18n.get('label.maintenance'),
                    width : 600, 
                    height : 400, 
                    autoScroll : true,
                    items : [{
                        border : false,
                        xtype : 'panel', 
                        layout : 'fit', 
                        autoScroll : true, 
                        html : maintenanceText, 
                        padding : "5"
                    }], 
                    modal : true
                });
                alertWindow.show();
            }
        } else {
            sitools.public.utils.LoginUtils.connect({
                closable : true,
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                register : loadUrl.get('APP_URL') + '/inscriptions/user',
                reset : loadUrl.get('APP_URL') + '/lostPassword',
                unblacklist : loadUrl.get('APP_URL') + '/unblacklist',
                urlProject : urlProject,
                callback : function (success) {
                    if (success && !maintenance) {
                        window.open(urlProject);
                    }
                }
            });
        }
    },
    
    onEditProfile : function (user, url) {
        var win = Ext.create('Ext.window.Window', {
            title : i18n.get('label.editProfile'),
            modal : true,
            width : 400,
            height : 460,
            resizable : false,
            layout : 'fit',
            items : [Ext.create('sitools.public.userProfile.editProfile', {
                identifier : user,
                url : url
            })]
        });
        
        win.show();
    }
});

var portalApp = {};
