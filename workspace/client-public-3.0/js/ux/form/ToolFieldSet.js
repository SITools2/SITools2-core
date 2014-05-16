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
/**
 * shortcut for console.assert(object is not null neither undefined neither
 * empty string)
 */
/*global Ext,cerr,ctrace,isFirebugConsoleIsActive,console*/

Ext.namespace('sitools.public.ux.form');

Ext.define('sitools.public.ux.form.ToolFieldSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.toolfieldset',
    tools: [],

    createLegendCt: function () {
        var me = this,
            items = [],
            legend = {
                xtype: 'container',
                baseCls: me.baseCls + '-header',
                id: me.id + '-legend',
                autoEl: 'legend',
                items: items,
                ownerCt: me,
                ownerLayout: me.componentLayout
            };

        // Title
        items.push(me.createTitleCmp());
        
        // Checkbox
        if (me.checkboxToggle) {
            items.push(me.createCheckboxCmp());
        } else if (me.collapsible) {
            // Toggle button
            items.push(me.createToggleCmp());
        }
        // Add Extra Tools
        if (Ext.isArray(me.tools)) {
            for(var i = 0; i < me.tools.length; i++) {
                items.push(me.createToolCmp(me.tools[i]));
            }
        }

        return legend;
    },

    createToolCmp: function(toolCfg) {
        var me = this;
        Ext.apply(toolCfg, {
            xtype:  'tool',
            width:  15,
            height: 15,
            id:     me.id + '-tool-' + toolCfg.type,
            scope:  me
        });
        return Ext.widget(toolCfg);
    }
});