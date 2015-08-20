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
Ext.define('sitools.user.ResetPasswordApplication', {
    name: 'sitools.user',

    requires: ['Ext.container.Viewport',

        /* CORE */
        'sitools.public.utils.LoginDef',
        'sitools.user.utils.Def',

        /* UTILS PUBLIC */
        'sitools.public.widget.vtype',
        'sitools.public.utils.reference',
        'sitools.public.utils.i18n',
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.Locale',
        'sitools.public.utils.LoginUtils',
        'sitools.public.crypto.Base64',
        'sitools.public.utils.PublicStorage',
        'sitools.public.utils.PopupMessage',
        'sitools.user.utils.I18nRegistry',

        /* MODULE LOADER, uncomment if all plugins have to be loaded at startup*/
//                 'sitools.user.utils.PluginDependenciesLoader'
    ],

    extend: 'Ext.app.Application',

    isReady: false,
    modules: null,
    useQuickTips: true,

    config: {
        ready: false,
        loaded: false
    },

    init: function () {
        Ext.Ajax.defaultHeaders = {
            "Accept" : "application/json",
            "X-User-Agent" : "Sitools"
        };
        loadUrl.load(appUrl + '/client-admin/siteMap', function () {
            i18n.load(loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/en/gui.properties', function () {

                Ext.MessageBox.buttonText.yes = i18n.get('label.yes');
                Ext.MessageBox.buttonText.no = i18n.get('label.no');
                Ext.QuickTips.init();

                var resetPasswordPanel = Ext.create("sitools.public.userProfile.ResetPasswordPanel", {
                    challengeToken : challengeToken,
                    resourceUrl : resourceUrl
                });

                var win = Ext.create("Ext.window.Window", {
                    title : i18n.get("title.changePassword"),
                    items : [resetPasswordPanel],
                    modal : true,
                    width: 500,
                    resizable : false
                });

                win.show();

            });
        });

    }


});