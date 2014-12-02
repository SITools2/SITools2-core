/*******************************************************************************
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
 ******************************************************************************/

/**
 * Basic Box for all admin panels.
 * @class sitools.widget.Box
 * @extends Ext.Panel
 */
Ext.namespace("sitools.public.widget.item");

Ext.define('sitools.public.widget.item.TextPanelView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.textpanelview',
    layout : 'fit',
    autoScroll : true,

    initComponent : function () {
        if (this.formatJson) {
            try {
                if (Ext.isFunction(JSON.parse) && Ext.isFunction(JSON.stringify)) {
                    var obj = JSON.parse(this.text);
                    this.html = JSON.stringify(obj, null, 4);
                    this.style = "white-space: pre";
                }
            } catch (err) {
                this.html = this.text;
            }
        }
        else {
            if (this.isOpenable(this.url)) {
                this.items = [{
                    xtype : 'textarea',
                    readOnly : true,
                    value : this.text

                }];
            } else {
                this.html = this.text;
            }

        }

        this.callParent(arguments);
    },

    isOpenable : function (text) {
        var textRegex = /\.(txt|json|css|xml)$/;
        return text.match(textRegex);
    }
});