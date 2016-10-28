/*******************************************************************************
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
 ******************************************************************************/

/**
 * Basic Box for all admin panels.
 * @class sitools.widget.Box
 * @extends Ext.Panel
 */
Ext.namespace("sitools.public.widget.item");

Ext.define('sitools.public.widget.item.GroupComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.s-groupComboBox',

    /*
     * @cfg groupField String value of field to groupBy, set this to any field in your model
     */
    groupField: 'group',
    listConfig: {
        cls: 'grouped-list'
    },
    initComponent: function () {
        var me = this;

        me.tpl = new Ext.XTemplate([
            '{[this.currentGroup = null]}',
            '<tpl for=".">',
            '   <tpl if="this.shouldShowHeader(' + me.groupField + ')">',
            '       <div class="group-header">{[this.showHeader(values.' + me.groupField + ')]}</div>',
            '   </tpl>',
            '   <div class="x-boundlist-item">{' + me.displayField + '}</div>',
            '</tpl>',
            {
                shouldShowHeader: function (group) {
                    return this.currentGroup != group;
                },
                showHeader: function (group) {
                    this.currentGroup = group;
                    return group;
                }
            }
        ]);
        me.callParent(arguments);
    }
});