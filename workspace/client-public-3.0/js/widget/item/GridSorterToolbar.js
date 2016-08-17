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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/

/**
 * A toolbar with buttons to move rows up or down
 * 
 * @cfg {string} gridId :
 *            the id of the grid. This is not mandatory if the grid is
 *            not the scope of the buttons
 * @class sitools.widget.GridSorterToolbar
 * @extends Ext.Toolbar
 */
Ext.define('sitools.public.widget.item.GridSorterToolbar', {
    alternateClassName : ['sitools.widget.GridSorterToolbar'],
    extend : 'Ext.toolbar.Toolbar',
    alias : 'widget.sitools.widget.GridSorterToolbar',
    alignRight : true, //default to true
    initComponent : function () {
        
        sitools.widget.GridSorterToolbar.superclass.initComponent.call(this);
        
        if (this.alignRight) {
            this.add('->');
        }
        this.add(Ext.create('sitools.widget.GridTop', {
                gridId : this.gridId
            }), Ext.create('sitools.widget.GridUp', {
                gridId : this.gridId
            }), Ext.create('sitools.widget.GridDown', {
                gridId : this.gridId
            }), Ext.create('sitools.widget.GridBottom', {
                gridId : this.gridId
            }));
    }
});