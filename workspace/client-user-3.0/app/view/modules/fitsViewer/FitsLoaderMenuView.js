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
/*global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.user.view.modules.fitsViewer');

/**
 * Load a fits from the given URL
 *
 * @class sitools.user.view.modules.fitsViewer.FitsLoaderMenuView
 * @extends Ext.menu.Menu
 */
Ext.define('sitools.user.view.modules.fitsViewer.FitsLoaderMenuView', {
    extend : 'Ext.menu.Menu',
    alias: 'widget.fitsLoaderMenuView',

    width : 400,
    border : false,
    plain : true,

    initComponent : function () {

        this.items = [{
            xtype : 'textfield',
            name : 'fitsUrl',
            cls : 'menuItemCls',
            fieldLabel : i18n.get('label.fitsUrl'),
            listeners : {
                scope : this,
                afterrender : function (textfield) {
                    Ext.defer(textfield.focus, 100, textfield);
                },
                specialkey : function (field, e) {
                    if (e.getKey() == e.ENTER) {
                        this.loadFitsFromMenu();
                    }
                }
            }
        }, {
            xtype : 'menuseparator',
            separatorCls : 'customMenuSeparator'
        }, {
            text : i18n.get("label.load"),
            icon : loadUrl.get('APP_URL') + '/common/res/images/ux/accept.png',
            cls : 'menuItemCls',
            scope : this,
            handler : this.loadFitsFromMenu
        }];

        this.callParent(arguments);
    },

    loadFitsFromMenu : function () {
        var textfieldUrl =  this.down('textfield');
        if (!textfieldUrl.isValid()) {
            return;
        }
        var urlFitsValue = textfieldUrl.getValue();
        var url = "/sitools/proxy?external_url=" + urlFitsValue;
        this.fitsMainView.loadFits(url);
        this.close();
    }

});