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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/
Ext.namespace('sitools.user.view.modules.fitsViewer');
/**
 * Visualize data from Fits header in a grid
 *
 * @class sitools.user.view.modules.fitsViewer.FitsHeaderView
 * @extends Ext.grid.Panel
 */
Ext.define('sitools.user.view.modules.fitsViewer.FitsHeaderView', {
    extend : 'Ext.grid.Panel',
	alias : 'widget.fitsHeaderView',

    split : true,
    forceFit: true,
    border : false,

    initComponent : function () {

        this.store = Ext.create('Ext.data.JsonStore', {
            idProperty: 'name',
            fields: ['name', 'value', 'description']
        });
        
        this.colModel = {
            items : [{header: i18n.get('headers.name'), width: 200, sortable: true, dataIndex: 'name'},
                  {header: 'Description', width: 200, sortable: true, dataIndex: 'description'},
                {header: i18n.get('headers.value'), width: 200, sortable: true, dataIndex: 'value'}]
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });
        
        this.listeners = {
            scope : this,
            afterrender : function(grid) {
                Ext.iterate(grid.headerData.cards, function (meta, value) {
                    if (value.length == 2) {
                        var rec = {
                           name : meta,
                           description : "", 
                           value : value[1]
                        };
                        grid.getStore().add(rec);
                        
                    } else if (value.length == 3) {
                        var rec = {
                           name : meta,
                           description : value[2], 
                           value : value[1]
                        };
                        grid.getStore().add(rec);
                    }
                });
            }
        };
        
        this.callParent(arguments);
    }
});