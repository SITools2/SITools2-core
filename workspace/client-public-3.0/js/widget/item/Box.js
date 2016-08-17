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

Ext.define('sitools.public.widget.item.Box', {
    alternateClassName : ['sitools.widget.Box'],
    extend : 'Ext.panel.Panel',
    alias : 'widget.s-box',
    width : '98%',
    frame : true,
    baseCls : 'x-box',
    cls : 'x-box-blue module-root centering',
    _title : '',

    constructor : function (cfg) {

      Ext.apply({
          _title : cfg.label,
          _idItem : cfg.idItem
      }, this);
      
      this.callParent(arguments);
    },

    initComponent : function () {
        this.items.unshift({
            xtype : 'component',
            html : this._title,
            cls : 'subtitle icon-' + this.idItem
        });
        sitools.public.widget.item.Box.superclass.initComponent.call(this);
    } 
    
});