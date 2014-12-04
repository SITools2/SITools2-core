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
Ext.define('sitools.user.Application', {
    name: 'sitools.user',

    requires: ['Ext.container.Viewport',

        /* CORE */
        'sitools.public.utils.LoginDef',
        'sitools.user.utils.Def',
        'sitools.user.core.Project',
        'sitools.user.core.Desktop',

        /* UTILS PUBLIC */
        'sitools.public.widget.vtype',
        'sitools.public.widget.datasets.columnRenderer.BehaviorEnum',
        'sitools.public.utils.reference',
        'sitools.public.utils.i18n',
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.Locale',
        'sitools.public.utils.LoginUtils',
        'sitools.public.crypto.Base64',
        'sitools.public.utils.UserStorage',
        'sitools.public.utils.PublicStorage',
        'sitools.public.utils.PopupMessage',
        'sitools.public.version.Version',
        'sitools.user.utils.I18nRegistry',
        'sitools.user.utils.ScriptLoader',

        /* UTILS USER */
        'sitools.user.utils.FormUtils',
        'sitools.user.utils.ServerServiceUtils',
        'sitools.user.utils.DataviewUtils',
        'sitools.user.utils.ModuleUtils',
        'sitools.user.core.Module',


        /* MODULE LOADER, uncomment if all plugins have to be loaded at startup*/
//                 'sitools.user.utils.PluginDependenciesLoader'
    ],

    extend: 'Ext.app.Application',

    controllers: ['core.SitoolsController',
        'DesktopController',
        'header.HeaderController',
        'footer.FooterController',
        'core.NavigationModeFactory',
        'component.personal.UserPersonalController',
        'component.personal.TaskController',
        'sitools.user.controller.component.personal.OrderController'
    ],
    isReady: false,
    modules: null,
    useQuickTips: true,

    config: {
        ready: false,
        loaded: false
    },

    init: function () {
        var me = this, desktopCfg;
        this.addMask();
        if (me.useQuickTips) {
            Ext.QuickTips.init();
        }

        if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
            var auth = Ext.util.Cookies.get('hashCode');

            Ext.Ajax.defaultHeaders = {
                "Authorization": auth,
                "Accept": "application/json",
                "X-User-Agent": "Sitools"
            };
            taskCheckSessionExpired.delay(COOKIE_DURATION * 60 * 1000);

        } else {
            Ext.Ajax.defaultHeaders = {
                "Accept": "application/json",
                "X-User-Agent": "Sitools"
            };
        }

        Desktop.init();
        Desktop.setApplication(this);
        this.initSiteMap();
    },

    // 1
    initSiteMap: function () {
        loadUrl.load('/sitools/client-user/siteMap', this.initLanguages, this);
    },

    // 2
    initLanguages: function () {
        locale.initLocale();
        locale.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/statics/langues.json', this.initi18n, this);
    },

    // 3
    initi18n: function () {
        i18n.load(
            loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + locale.getLocale() + '/gui.properties',
            function () {
                if(!checkCookieDuration()){
                    Ext.Msg.show({
                        title: i18n.get('label.warning'),
                        msg : i18n.get("label.wrongcookieduration.configuration")
                    });
                    this.removeMask();
                    return;
                }
                //def.js
                initLocalisedVariables();
                this.initSql2ext();
            }
            , this);
    },

    // 4
    initSql2ext: function () {
        this.updateMaskText();
        sql2ext.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/conf/sql2ext.properties', this.initUser, this);
    },

    // 5
    initUser: function () {
        var storeUser = Ext.create('sitools.user.store.UserStore', {
            storeId: 'UserStore'
        });

        storeUser.setCustomUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_USER_ROLE_URL'));

//        var url = sitools.user.utils.Project.getSitoolsAttachementForUsers();
//        storeUser.setCustomUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_USER_ROLE_URL'));
        storeUser.load({
            scope: this,
            callback: function (records, operation, success) {
                if (Ext.isEmpty(records)) {
                    storeUser.add({
                        firstName: "public",
                        identifier: "public",
                        email: "&nbsp;"
                    });
                }
                this.initProject();
            }
        });
    },

    // 6
    initProject: function () {
        sitools.user.core.Project.init(this.projectInitialized, this);
    },

    // 7
    projectInitialized: function () {
        this.setReady(true);
        this.fireEvent('projectInitialized'); // listened by SitoolsController
    },

    // 11
    noticeProjectLoaded: function () {
        this.setLoaded(true);
        this.fireEvent('projectLoaded');
        this.removeMask();
    },

    addMask: function () {
        this.splashScreen = Ext.getBody().mask('', 'splashscreen sitoolsLoadingWhite');
        this.splashScreen.addCls('splashscreen');
        Ext.DomHelper.insertFirst(Ext.query('.x-mask-msg')[0], {
            cls: 'x-splash-icon'
        });
    },

    updateMaskText: function () {
        Ext.dom.Query.selectNode('.x-mask-msg-text').innerHTML = i18n.get("label.loadingSitools");
    },

    removeMask: function () {
        this.splashScreen.fadeOut({
            duration: 1000,
            remove: true
        });

        this.splashScreen.next().fadeOut({
            duration: 1000,
            remove: true
        });
    }

});
