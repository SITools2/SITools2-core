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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, showHelp, loadUrl
 */
/*
 * @include "../def.js"
 */

Ext.namespace('sitools.admin.storages.plugins');

/**
 * A window that displays Storages properties.
 *
 * @cfg {string} url The url to Save the data
 * @cfg {string} action The action should be modify or create
 * @cfg {Ext.data.Store} store The storages store
 * @class sitools.admin.storages.storageCopyProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.storages.plugins.StorageCopyProp', {
    extend: 'Ext.Window',
    alias: 'widget.s-storage_copy',
    width: 230,
    height: 145,
    modal: true,

    initComponent: function () {

        this.title = i18n.get('label.storageCopy');

        this.items = [{
            xtype: 'form',
            id: 'formCopyId',
            layout: 'fit',
            border: false,
            buttonAlign: 'center',
            bodyStyle: 'padding:15px 10px 15px 15px;',
            items: [{
                xtype: 'label',
                text: i18n.get('label.destinationStorage')
            }, {
                xtype: 'combo',
                id: 'comboId',
                fieldLabel: 'Name',
                height: 30,
                typeAhead: true,
                triggerAction: 'all',
                store: this.store,
                valueField: 'id',
                displayField: 'name'
            }]
        }];

        this.buttons = [{
            xtype: 'button',
            iconAlign: 'right',
            text: i18n.get('label.storageRunCopy'),
            icon: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/converter.png',
            scope: this,
            handler: this.runCopy
        }];

        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);
        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this.runCopy();
            }
        }, this);
    },

    runCopy: function () {
        var f = Ext.getCmp('formCopyId').getForm();
        var idDest = f.findField('comboId').getValue();

        if (this.idSrc == idDest) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.sameStorage'));
        } else {
            Ext.Ajax.request({
                url: this.urlDirectories + "/" + this.idSrc + "?action=copy&idDest=" + idDest,
                method: 'PUT',
                scope: this,
                success: function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (showResponse(ret)) {
                        //this.store.reload();
                    }
                    this.close();
                },
                failure: alertFailure
            });
        }
    }

});
