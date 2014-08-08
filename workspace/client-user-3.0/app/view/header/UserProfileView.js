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
/*global Ext, utils_logout, sitools, SitoolsDesk, window, userLogin, showResponse, projectGlobal, 
  userStorage, DEFAULT_PREFERENCES_FOLDER, i18n, extColModelToJsonColModel, loadUrl*/

Ext.namespace('sitools.user.view.header');

/**
 * @cfg {String} buttonId the id of the button that displays the window
 * @class sitools.user.component.entete.UserProfile
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.header.UserProfileView', {
    extend : 'Ext.menu.Menu',
    alias: 'widget.userProfileWindow',
    
    width : 150,
    height : 137,
    border : false,
    plain : true,
    closeAction : 'hide',
    
    initComponent : function () {
        
        var UserStore = Ext.data.StoreManager.get('UserStore');
        this.user = UserStore.getAt(0);
        
        var userIdentifier = this.user.get('identifier');
        var userLanguage = locale.getLocale(), userLargeIcon;
        
        Ext.each(locale.getLanguages(), function (language) {
            if (userLanguage === language.localName) {
                userLargeIcon = language.largeIcon;
            }
        });
        
//        this.height =  (userIdentifier === "public") ? 140 : 220;
        
        var logout = Ext.create('Ext.menu.Item', {
            text  : i18n.get('label.logout'),
            name : 'usrProfileLogout',
            hidden : (userIdentifier == "public"),
            iconCls : 'logoutIcon',
        	cls : 'menuItemCls'
        });
        
        var login = Ext.create('Ext.menu.Item', {
            text  : i18n.get('label.login'),
            name : 'usrProfileLogin',
            hidden : (userIdentifier != "public"),
            iconCls : 'loginIcon',
        	cls : 'menuItemCls'
        });
        
        var register = Ext.create('Ext.menu.Item', {
            text  : i18n.get('label.register'),
            name : 'usrProfileRegister',
            hidden : (userIdentifier != "public"),
            iconCls : 'registerIcon',
            cls : 'menuItemCls'
        });
        
        var personal = Ext.create('Ext.menu.Item', {
            text  : i18n.get('label.personal'),
            name : 'usrProfilePersonal',
            hidden : (userIdentifier == "public"),
            iconCls : 'personalIcon',
            cls : 'menuItemCls'
        });
        
        var menuLangues = Ext.create('Ext.menu.Menu', {
            plain : true,
            border : false
        });
        
        Ext.each(Project.getLanguages(), function (language) {
            menuLangues.add({
                text : language.displayName,
                cls : 'menuItemCls',
                scope : this,
                handler : function () {
                    var callback = function () {
                        Ext.util.Cookies.set('language', language.localName);
                        window.location.reload();
                    };
                    var date = new Date();
                    Ext.util.Cookies.set('language', language.localName, date.add(Date.MINUTE, 20));
                    var userPreferences = {};
                    userPreferences.language = language.localName;
                    if (!Ext.isEmpty(userLogin)) {
                        userStorage.set(loadUrl.get('APP_PORTAL_URL'),  "/" + DEFAULT_PREFERENCES_FOLDER + loadUrl.get('APP_PORTAL_URL'), userPreferences, callback);
                    } else {
                        window.location.reload();
                    }

                },
                icon : language.image
            });
        }, this);
        
        var language = Ext.create('Ext.menu.Item', {
        	text : 'Language',
        	itemId : 'languageMenuId',
            identifier : "language", 
            name: i18n.get("label.langues"), 
            iconCls : 'languageMenuIcon',
            cls : 'menuItemCls',
            menu : menuLangues
        });
        
//        var fielsetInfoUser = Ext.create('Ext.form.FieldSet', {
//        	title : userIdentifier,
//        	id : 'userProfilePopup',
//        	width : 250,
//        	margins: 5,
//        	items: [userLogo, {
//        		xtype: 'fieldcontainer',
//        		layout: 'hbox',
//        		pack : 'end',
//        		align : 'stretch',
//        		items: [ logout, login, register ]
//        	}]
//        });
        
        var userLabel = Ext.create('Ext.menu.Item', {
        	text : userIdentifier,
        	plain : false,
        	canActivate : false,
        	cls : 'userMenuCls'
        });
        
        var logoUserUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/menu/usersGroups.png';
        
        this.items = [userLabel, '-', logout, login, '-', register, personal,  '-', language];
        
        this.callParent(arguments);
    }
    
});