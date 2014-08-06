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
 * @class sitools.user.controller.header.HeaderController
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.header.HeaderController', {
    
    extend : 'Ext.app.Controller',
    
    views : ['header.HeaderView', 
             'header.UserProfileView',
             'header.ButtonTaskBarView'],
    
    heightNormalMode : 0,
    heightMaximizeDesktopMode : 44,
    
    config : {
        HeaderView : null,
        UserProfileView : null
    },
    
    init : function () {
        
        this.getApplication().on('projectLoaded', this.onProjectLoaded, this);
        
        this.control({
        	
        	/* HeaderView events */
        	'headerView' : {
        		afterrender : function (me) {
                    // var enteteEl = SitoolsDesk.getEnteteEl();
                    var enteteEl = Ext.get('x-headers');
                    me.setHeight(enteteEl.getHeight());

                    me.heightNormalMode = enteteEl.getHeight();
                    me.heightMaximizeDesktopMode = me.NavBarsPanel.getHeight();
                    
                },
                
                maximizeDesktop : this.onMaximizeDesktop,
                minimizeDesktop : this.onMinimizeDesktop,
                windowResize : function (me) {
                    if (!Ext.isEmpty(me.userContainer) && me.userContainer.isVisible()) {
                        me.userContainer.hide();
                    }
                },
                desktopReady : function (me) {
                    me.entetePanel.fireEvent("desktopReady", me.navToolbarButtons);
                }
        	},
        	
        	'headerView toolbar[name=navbarPanels]' : {
        		maximizeDesktop : this.onMaximizeDesktopNavbar,
                minimizeDesktop : this.onMinimizeDesktopNavbar
        	},
        	
        	/* UserProfilerView events */
			'userProfileWindow' : {
                beforerender : function (usrProfileWindow) {
                    usrProfileWindow.x = Ext.getBody().getWidth() - usrProfileWindow.width;
                    usrProfileWindow.y = this.getEnteteEl().getHeight(); 
                },
                blur : function (userProfileWindow) {
                	userProfileWindow.close();
                }
            },
            
            'userProfileWindow menuitem[name="usrProfileLogout"]' : {
                click : function (btn) {
                    sitools.public.utils.LoginUtils.logout();
                }
            },
            
            'userProfileWindow menuitem[name="usrProfileLogin"]' : {
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
            
            'userProfileWindow menuitem[name="usrProfileRegister"]' : {
                click : function (btn) {
                    var register = Ext.create('sitools.public.userProfile.Register', {
                        closable : true,
                        url : loadUrl.get('APP_URL')+ "/inscriptions/user",
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist',
                        login : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login'
                    });
                    register.show();
                }
            },
            
            'userProfileWindow menuitem[name="usrProfilePersonal"]' : {
                click : function (btn) {
                	var menu = btn.up('userProfileWindow');
                	
                    var personnalController = this.getApplication().getController('header.UserPersonalController')
                    var personalView = personnalController.getView('header.UserPersonalView').create({
                    	user : menu.user
                    });
                    
                    personalView.show();
                }
            },
            
            /* ButtonTaskbarView events */
            'buttonTaskBarView button[name=profilBtn]' : {
            	click : function(btn) {
            		var usrProfileWin = Ext.ComponentQuery.query('userProfileWindow')[0];
					if (Ext.isEmpty(usrProfileWin) || !usrProfileWin.isVisible()) {
						var win = this.getView('header.UserProfileView').create({
							buttonId : btn.id
						});
						win.show();
					}
				}
            },
            
			'buttonTaskBarView button[name=maximizeBtn]' : {
				click : function (btn) {
					if (Desktop.getDesktopMaximized() == false) {
						this.getApplication().getController('DesktopController').maximize();
						Desktop.setDesktopMaximized(true);
					}
					else {
						this.getApplication().getController('DesktopController').minimize(); 
						Desktop.setDesktopMaximized(false);
					}
				}
			},
			
			'buttonTaskBarView button[name=versionBtn]' : {
				click : function (btn) {
					Ext.create('sitools.public.utils.Version').show();
				}
			}
        });
        this.callParent(arguments);
    },
    
    onProjectLoaded : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        
        this.HeaderView = this.getView('header.HeaderView').create({
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
	},
	
	 /**
     * listeners of maximizeDesktop event :
     */
    onMaximizeDesktop : function () {
    	var me = this.getHeaderView();
    	
    	me.entetePanel.hide();
    	me.container.setHeight(this.heightMaximizeDesktopMode);
    	me.setHeight(this.heightMaximizeDesktopMode);
    	
    	me.NavBarsPanel.fireEvent("maximizeDesktop");
        
    	// this.userContainer.setVisible(! SitoolsDesk.desktopMaximizeMode);
        if (me.userContainer) {
        	me.userContainer.fireEvent("maximizeDesktop", me.userContainer, me.navToolbarButtons);
        	me.userContainer = null;
        }
    },
    /**
     * listeners of minimizeDesktop event :
     */
    onMinimizeDesktop : function () {
    	var me = this.getHeaderView();
    	
    	me.entetePanel.setVisible(true);
    	me.container.dom.style.height = "";
    	me.setHeight(me.heightNormalMode);
    	
    	me.NavBarsPanel.fireEvent("minimizeDesktop");
        
    	// this.userContainer.setVisible(! SitoolsDesk.desktopMaximizeMode);
        if (me.userContainer) {
        	me.userContainer.fireEvent("minimizeDesktop", me.userContainer, me.navToolbarButtons);
        	me.userContainer = null;
        }
    },
	
	/**
     * listeners of maximizeDesktop event
     */
    onMaximizeDesktopNavbar : function () {
    	var me = this.getHeaderView();
    	
    	me.navBarModule.fireEvent("maximizeDesktop");
    	me.navToolbarButtons.fireEvent("maximizeDesktop");
    },

    /**
     * listeners of minimizeDesktop event
     */
    onMinimizeDesktopNavbar : function () {
    	var me = this.getHeaderView();
    	
    	me.navBarModule.fireEvent("minimizeDesktop");
    	me.navToolbarButtons.fireEvent("minimizeDesktop");
    }
});