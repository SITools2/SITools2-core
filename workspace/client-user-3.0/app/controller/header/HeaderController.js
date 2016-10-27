/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

    extend: 'Ext.app.Controller',

    views: ['header.HeaderView',
        'header.UserProfileView',
        'header.ButtonTaskBarView'],

    heightNormalMode: 0,
    heightMaximizeDesktopMode: 0,

    config: {
        HeaderView: null,
        UserProfileView: null
    },

    init: function () {

        this.getApplication().on('projectLoaded', this.onProjectLoaded, this);

        this.listen({ // listen action handle by DesktopController
            controller: {
                '#DesktopControllerId': {
                    profilBtnClicked: this.profilBtnClicked,
                    versionBtnClicked: this.versionBtnClicked,
                    maximizedBtnClicked: this.maximizedBtnClicked,
                    saveBtnClicked: this.saveBtnClicked
                }
            }
        });

        this.control({

            /* HeaderView events */
            'headerView': {
                afterrender: function (me) {
                    var enteteEl = Ext.get('x-headers');

                    me.setHeight(enteteEl.getHeight());
                    me.heightNormalMode = enteteEl.getHeight();
                    //me.heightMaximizeDesktopMode = me.NavBarsPanel.getHeight();

                },

                maximizeDesktop: this.onMaximizeDesktop,
                minimizeDesktop: this.onMinimizeDesktop,
                windowResize: function (me) {
                    if (!Ext.isEmpty(me.userContainer) && me.userContainer.isVisible()) {
                        me.userContainer.hide();
                    }
                },
                desktopReady: function (me) {
                    me.entetePanel.fireEvent("desktopReady", me.navToolbarButtons);
                }
            },

//        	'headerView toolbar[name=navbarPanels]' : {
//        		maximizeDesktop : this.onMaximizeDesktopNavbar,
//                minimizeDesktop : this.onMinimizeDesktopNavbar
//        	},

            /* UserProfilerView events */
            'userProfileWindow': {
                boxready: function (usrProfileWindow) {
                    var taskbarHeight = this.getController('DesktopController').desktopView.taskbar.getHeight();

                    var x = (Desktop.getDesktopEl().getWidth() + Desktop.getDesktopEl().getX()) - usrProfileWindow.getWidth();
                    var y = Desktop.getEnteteEl().getHeight() + taskbarHeight;
                    usrProfileWindow.setPosition(x, y);
                }
            },

            'userProfileWindow menuitem[name="usrProfileLogout"]': {
                click: function (btn) {
                    sitools.public.utils.LoginUtils.logout();
                }
            },

            'userProfileWindow menuitem[name="usrProfileLogin"]': {
                click: function (btn) {
                    sitools.public.utils.LoginUtils.connect({
                        closable: true,
                        url: loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                        register: loadUrl.get('APP_URL') + '/inscriptions/user',
                        reset: loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist: loadUrl.get('APP_URL') + '/unblacklist'
                    });
                }
            },

            'userProfileWindow menuitem[name="usrProfileRegister"]': {
                click: function (btn) {
                    var register = Ext.create('sitools.public.userProfile.Register', {
                        closable: true,
                        url: loadUrl.get('APP_URL') + "/inscriptions/user",
                        reset: loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist: loadUrl.get('APP_URL') + '/unblacklist',
                        login: loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login'
                    });
                    register.show();
                }
            },

            'userProfileWindow menuitem[name="usrProfilePersonal"]': {
                click: function (btn) {
                    var menu = btn.up('userProfileWindow');

                    var userPersonalComponent = Ext.create('sitools.user.component.personal.UserPersonalComponent');
                    userPersonalComponent.create(this.getApplication());
                    userPersonalComponent.init({
                        user: menu.user
                    });
                }
            },

            'userProfileWindow menuitem[name=versionBtn]': {
                click: function (btn) {
                    btn.up('userProfileWindow').close();
                    Ext.create('sitools.public.version.Version').show();
                }
            },

            'userProfileWindow menuitem[name=helpBtn]': {
                click: function (btn) {
                    btn.up('userProfileWindow').close();
                    Ext.create('sitools.public.utils.Help').show();
                }
            },
            'menu#saveMenu': {
                boxready: function (menu) {
                    var taskbarHeight = this.getController('DesktopController').desktopView.taskbar.getHeight();

                    var x = (Desktop.getDesktopEl().getWidth() + Desktop.getDesktopEl().getX()) - menu.getWidth();
                    var y = Desktop.getEnteteEl().getHeight() + taskbarHeight;
                    menu.setPosition(x, y);
                }
            },
            'menu#saveMenu menuitem#saveUser': {
                click: function (btn) {
                    Desktop.saveWindowSettings();
                }
            },
            'menu#saveMenu menuitem#deleteUser': {
                click: function (btn) {
                    UserStorage.remove();
                }
            },
            'menu#saveMenu menuitem#savePublic': {
                click: function (btn) {
                    Desktop.saveWindowSettings(true);
                }
            },
            'menu#saveMenu menuitem#deletePublic': {
                click: function (btn) {
                    PublicStorage.remove();
                }
            }
        });

        Ext.EventManager.onWindowResize(this.fireResize, this);

        this.callParent(arguments);
    },

    fireResize : function () {
        var headerView = Ext.ComponentQuery.query("headerView")[0];
        if (headerView) {
            headerView.setWidth(Ext.getBody().getWidth());
        }
    },

    /** Button Taskbar Events **/

    profilBtnClicked: function (btn) {
        var usrProfileWin = Ext.ComponentQuery.query('userProfileWindow')[0];
        if (Ext.isEmpty(usrProfileWin) || !usrProfileWin.isVisible()) {
            var win = this.getView('header.UserProfileView').create({
                buttonId: btn.id
            });
            win.show();
        }
    },

    versionBtnClicked: function (btn) {
        Ext.create('sitools.public.version.Version').show();
    },

    maximizedBtnClicked: function (btn) {
        if (Desktop.getDesktopMaximized() == false) {
            this.getApplication().getController('DesktopController').maximize();
            Desktop.setDesktopMaximized(true);
            btn.setIconCls('mini_button_img');
            Ext.ComponentQuery.query("tooltip#maximizeButtonTip")[0].update(i18n.get('label.minimize'));
        }
        else {
            this.getApplication().getController('DesktopController').minimize();
            Desktop.setDesktopMaximized(false);
            btn.setIconCls('maxi_button_img');
            Ext.ComponentQuery.query("tooltip#maximizeButtonTip")[0].update(i18n.get('label.maximize'));
        }
    },

    saveBtnClicked: function (btn, event) {
//	        if (!Ext.isEmpty(userLogin) && projectGlobal && projectGlobal.isAdmin) {
        if (!Ext.isEmpty(userLogin)) {

            var saveLabel = Ext.create('Ext.menu.Item', {
                text: i18n.get('label.chooseSaveType'),
                plain: false,
                canActivate: false,
                cls: 'userMenuCls'
            });

            var ctxMenu = Ext.create('Ext.menu.Menu', {
                border: false,
                plain: true,
                width: 260,
                closeAction: 'hide',
                itemId: 'saveMenu',
                items: [saveLabel, {
                    xtype: 'menuseparator',
                    separatorCls: 'customMenuSeparator'
                }, {
                    text: i18n.get("label.myself"),
                    cls: 'menuItemCls',
                    iconCls: 'saveUserIcon',
                    itemId: 'saveUser'
                }, {
                    xtype: 'menuseparator',
                    separatorCls: 'customMenuSeparator'
                }, {
                    text: i18n.get('label.deleteUserPref'),
                    cls: 'menuItemCls',
                    iconCls: 'deleteSaveIcon',
                    itemId: 'deleteUser'
                }, {
                    xtype: 'menuseparator',
                    separatorCls: 'customMenuSeparator'
                }, {
                    text: i18n.get("label.publicUser"),
                    cls: 'menuItemCls',
                    iconCls: 'savePublicIcon',
                    itemId: 'savePublic'
                }, {
                    xtype: 'menuseparator',
                    separatorCls: 'customMenuSeparator'
                }, {
                    text: i18n.get('label.deletePublicPref'),
                    cls: 'menuItemCls',
                    iconCls: 'deleteSaveIcon',
                    itemId: 'deletePublic'
                }]
            });

            var taskbarHeight = this.getController('DesktopController').desktopView.taskbar.getHeight();
            ctxMenu.showAt([Desktop.getDesktopEl().getWidth() - ctxMenu.width, Desktop.getEnteteEl().getHeight() + taskbarHeight]);
        }
        else {
            Desktop.saveWindowSettings();
        }
    },

    /****/

    onProjectLoaded: function () {
        var project = Ext.getStore('ProjectStore').getProject();

        var modulesStore = Ext.data.StoreManager.lookup("ModulesStore");

        if (Desktop.getEnteteEl().getHeight() == 0) {
            return this.getApplication().fireEvent('headerLoaded');
        }

        this.HeaderView = this.getView('header.HeaderView').create({
            renderTo: "x-headers",
            htmlContent: project.get('htmlHeader'),
            modules: modulesStore
        });

        this.getApplication().fireEvent('headerLoaded');
    },

    /**
     * listeners of maximizeDesktop event :
     */
    onMaximizeDesktop: function () {
        var me = this.getHeaderView();

        if (Ext.isEmpty(me)) {
            return;
        }

        me.entetePanel.hide();
        me.container.setHeight(this.heightMaximizeDesktopMode);
        me.setHeight(this.heightMaximizeDesktopMode);

//    	me.NavBarsPanel.fireEvent("maximizeDesktop");

        // this.userContainer.setVisible(! SitoolsDesk.desktopMaximizeMode);
        if (me.userContainer) {
            me.userContainer.fireEvent("maximizeDesktop", me.userContainer, me.navToolbarButtons);
            me.userContainer = null;
        }
    },
    /**
     * listeners of minimizeDesktop event :
     */
    onMinimizeDesktop: function () {
        var me = this.getHeaderView();

        if (Ext.isEmpty(me)) {
            return;
        }

        me.entetePanel.setVisible(true);
        me.container.dom.style.height = "";
        me.setHeight(me.heightNormalMode);

//    	me.NavBarsPanel.fireEvent("minimizeDesktop");

        // this.userContainer.setVisible(! SitoolsDesk.desktopMaximizeMode);
        if (me.userContainer) {
            me.userContainer.fireEvent("minimizeDesktop", me.userContainer, me.navToolbarButtons);
            me.userContainer = null;
        }
    },

    /**
     * listeners of maximizeDesktop event
     */
    onMaximizeDesktopNavbar: function () {
        var me = this.getHeaderView();

        me.navBarModule.fireEvent("maximizeDesktop");
        me.navToolbarButtons.fireEvent("maximizeDesktop");
    },

    /**
     * listeners of minimizeDesktop event
     */
    onMinimizeDesktopNavbar: function () {
        var me = this.getHeaderView();

        me.navBarModule.fireEvent("minimizeDesktop");
        me.navToolbarButtons.fireEvent("minimizeDesktop");
    }
});