/***************************************
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
***************************************/
Ext.namespace('sitools.public.widget.grid');

/**
 * A toolbar with buttons to move rows up or down
 * 
 * @cfg {string} gridId :
 *            the id of the grid. This is not mandatory if the grid is
 *            not the scope of the buttons
 * @class sitools.public.widget.grid.GridSorterToolbar
 * @extends Ext.Toolbar
 */
Ext.define('sitools.public.widget.grid.GridSorterToolbar', {
    extend : 'Ext.toolbar.Toolbar',
    requires : ['sitools.public.widget.grid.GridTop',
                'sitools.public.widget.grid.GridUp',
                'sitools.public.widget.grid.GridDown',
                'sitools.public.widget.grid.GridBottom'],
    alias : 'widget.sitools.public.widget.grid.GridSorterToolbar',
    alignRight : true, //default to true
    initComponent : function () {
        
        sitools.public.widget.grid.GridSorterToolbar.superclass.initComponent.call(this);
        
        if (this.alignRight) {
            this.add('->');
        }
        this.add(Ext.create('sitools.public.widget.grid.GridTop', {
                gridId : this.gridId
            }), Ext.create('sitools.public.widget.grid.GridUp', {
                gridId : this.gridId
            }), Ext.create('sitools.public.widget.grid.GridDown', {
                gridId : this.gridId
            }), Ext.create('sitools.public.widget.grid.GridBottom', {
                gridId : this.gridId
            }));
    }
});