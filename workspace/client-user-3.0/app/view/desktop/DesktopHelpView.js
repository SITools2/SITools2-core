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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.view.desktop');

/**
 * Help for desktop user
 * @class sitools.user.view.desktop.DesktopHelpView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.desktop.DesktopHelpView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.desktopHelpView',

    header: false,
    bodyStyle: 'background-color: rgb(47, 47, 47);',
    style: 'opacity: .85; z-index:100000; position:absolute; top:0; left:0;',
    border: false,
    bodyBorder: false,

    initComponent: function () {

        this.renderTo = Ext.getBody();
        this.height = Ext.getBody().getHeight();
        this.width = Ext.getBody().getWidth();

        this.loader = {
            url: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + '/resources/html/' + locale.getLocale() + '/desktopHelp.html',
            autoLoad: true
        };

        this.tbar = {
            xtype: 'toolbar',
            style: 'background-color: rgb(47, 47, 47);',
            items: ['->', {
                text: i18n.get('label.close'),
                scope: this,
                handler: function () {
                    this.close();
                }
            }]
        };

        this.bbar = {
            xtype: 'toolbar',
            border: false,
            style: 'background-color: rgb(47, 47, 47);',
            layout: {
                type: 'hbox',
                pack: 'end',
                align: 'middle'
            },
            items: [{
                xtype: 'label',
                style: 'color:white; font-size: 13px;',
                html: i18n.get('label.showDesktopHelp')
            }, {
                xtype: 'checkbox',
                listeners: {
                    change: function (combo, newValue, oldValue) {
                        Ext.util.Cookies.set('showDesktopHelp', newValue);
                    },
                    afterrender: function (cb) {
                        cb.setValue(Ext.util.Cookies.get('showDesktopHelp'));
                    }
                }
            }]
        };

        this.listeners = {
            afterrender: function (windowFrame) {
                windowFrame.body.on('click', function (e, target) {
                    this.close();
                }, windowFrame);

                Ext.EventManager.onWindowResize(function (width, height) {
                    this.setSize(width, height);
                }, windowFrame);
            }
        };

        Ext.create('Ext.fx.Anim', {
            target: this,
            duration: 800,
            from: {
                opacity: 0
            },
            to: {
                opacity: .8
            }
        });

        this.callParent(arguments);
    }
});