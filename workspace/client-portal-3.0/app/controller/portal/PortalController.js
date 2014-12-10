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
        this.initAppliPortal({
            siteMapRes : '/sitools' + '/client-user'
        });
        
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
    
    initProjects : function (callback) {
        Ext.Ajax.request({
            scope : this,
            /* sitools/portal/projects...*/
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL') + '/projects?media=json', 
            method : 'GET',
            success : function (response) {
                try {
                    this.projects = Ext.decode(response.responseText).data;
                    if (this.autoChainAjaxRequest) {
                        this.initLanguages();
                    }
                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }

                // portal = new sitools.Portal(response.responseJSON.data);
            },
            failure : function (response) {
                Ext.Msg.alert('Status', i18n.get('warning.serverError'));
            }
        });
    },
    
    initLanguages : function () {
        Ext.Ajax.request({
            scope : this,
            method : "GET",
            /* /sitools/client-user */
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/statics/langues.json',
            success : function (response) {
                this.languages = Ext.decode(response.responseText).data;
                if (this.autoChainAjaxRequest) {
                    this.initPreferences();
                }
            },
            failure : function (response) {
                Ext.Msg.alert('Status', i18n.get('warfning.serverError'));
            }
        });
    },
    
    initPreferences : function (cb) {
        if (Ext.isEmpty(userLogin)) {
            var projects = this.projects;
            var languages = this.languages;
            var preferences = this.preferences;
            var callback;
            if (this.autoChainAjaxRequest) {
                callback = function () {
                    if(!checkCookieDuration()){
                        Ext.Msg.show({
                            title: i18n.get('label.warning'),
                            msg : i18n.get("label.wrongcookieduration.configuration")
                        });
                        return;
                    }
                    // loadUrl.load('/sitools/client-user/siteMap', function (){
//                    portal = new sitools.Portal(projects, languages, preferences);
                    portal = Ext.create('sitools.clientportal.view.portal.PortalView', {
                        projects : projects,
                        languages : languages,
                        preferences : preferences
                    });
                    // });
                };
            }
            else {
                callback = cb;
            }
            
            i18n.load(loadUrl.get('APP_URL') + '/client-public/res/i18n/' + locale.getLocale() + '/gui.properties', callback);
            
            return;
        }
        var filePath = "/" + DEFAULT_PREFERENCES_FOLDER + '/portal';
        var success = function (response) {
            this.preferences = Ext.decode(response.responseText);
            if (!Ext.isEmpty(this.preferences.language)) {
                locale.setLocale(this.preferences.language);
            }

        };
        var failure = function () {
            return;
        };
        var callback = function () {
            var projects = this.projects;
            var languages = this.languages;
            var preferences = this.preferences;
            i18n.load(loadUrl.get('APP_URL') + '/client-public/res/i18n/' + locale.getLocale() + '/gui.properties', function () {
                if(!checkCookieDuration()){
                    Ext.Msg.show({
                        title: i18n.get('label.warning'),
                        msg : i18n.get("label.wrongcookieduration.configuration")
                    });
                    return;
                }

                // loadUrl.load('/sitools/client-user/siteMap', function (){
//                portal = new sitools.Portal(projects, languages, preferences);
                portal = Ext.create('sitools.clientportal.view.portal.PortalView', {
                    projects : projects,
                    languages : languages,
                    preferences : preferences
                });
                // });
            });
        };
        
        UserStorage.get("portal", filePath, this, success, failure, callback);
        
    },
    
    initAppliPortal : function (opts, callback) {
        if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
            var auth = Ext.util.Cookies.get('hashCode');
            Ext.Ajax.defaultHeaders = {
                "Authorization" : auth,
                "Accept" : "application/json",
                "X-User-Agent" : "Sitools"
            };
        } else {
            Ext.Ajax.defaultHeaders = {
                "Accept" : "application/json",
                "X-User-Agent" : "Sitools"
            };
        }
        Ext.QuickTips.init();
//        this.callbacks[0](opts.siteMapRes, callback);
        this.callSiteMapResource(opts.siteMapRes, callback);
        //this.initProjects();
    },
    
    callSiteMapResource : function (res, cb) {
        var callback;
        if (this.autoChainAjaxRequest) {
            callback = this.initProjects;
        } else {
            callback = cb;
        }
        loadUrl.load(res + '/siteMap', callback, this);
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
            modal : true,
            width : 400,
            height : 430,
            resizable : false,
            items : [Ext.create('sitools.public.userProfile.editProfile', {
                identifier : user,
                url : url
            })]
        });
        
        win.show();
    }
    
});

var portalApp = {
    
};
