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

Ext.namespace('sitools.user.controller.header');

/**
 * Populate the div x-headers of the sitools Desktop. 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define("sitools.user.controller.header.HeaderController", {
    
    extend : 'Ext.app.Controller',
    
    views : ['header.Header', 
             'header.UserProfile'],
    
    heightNormalMode : 0, 
    heightMaximizeDesktopMode : 0,
    
    config : {
        HeaderView : null,
        UserProfileView : null
    },
    
    init : function () {
        
        this.getApplication().on('projectLoaded', this.onProjectLoaded, this);
        
        this.control({
			'userProfileWindow' : {
                beforerender : function (usrProfileWindow) {
                    usrProfileWindow.x = Ext.getBody().getWidth() - usrProfileWindow.width;
                    usrProfileWindow.y = this.getEnteteEl().getHeight(); 
                }
            },
            
            'userProfileWindow button[name="usrProfileLogout"]' : {
                click : function (btn) {
                    sitools.public.utils.LoginUtils.logout();
                }
            },
            
            'userProfileWindow button[name="usrProfileLogin"]' : {
                click : function (btn) {
                    sitools.public.utils.LoginUtils.connect({
                        closable : true,
                        url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                        register : loadUrl.get('APP_URL') + '/inscriptions/user',
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist'                    
                    });
                }
            },
            
            'userProfileWindow button[name="usrProfileRegister"]' : {
                click : function (btn) {
                    var register = new sitools.public.userProfile.Register({
                        closable : true,
                        url : loadUrl.get('APP_URL')+ "/inscriptions/user",
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist',
                        login : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login'
                    });
                    register.show();
                }
            }
        });
        this.callParent(arguments);
    },
    
    onProjectLoaded : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        
        this.HeaderView = this.getView('header.Header').create({
            renderTo : "x-headers",
            htmlContent : project.get('htmlHeader'),
            modules : project.modules(),
            listeners : {
                resize : function (me) {
                    me.setSize(SitoolsDesk.getEnteteEl().getSize());
                }
            }
        });
    },

	getEnteteEl : function () {
		return this.getHeaderView().getEl();
	}
});