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
/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
(function() {
    Ext.override(Ext.grid.column.Column, {
        init : function() {    
            var types = Ext.data.Types,
                st = this.sortType;
                    
            if(this.type){
                if(Ext.isString(this.type)){
                    this.type = Ext.data.Types[this.type.toUpperCase()] || types.AUTO;
                }
            }else{
                this.type = types.AUTO;
            }

            // named sortTypes are supported, here we look them up
            if(Ext.isString(st)){
                this.sortType = Ext.data.SortTypes[st];
            }else if(Ext.isEmpty(st)){
                this.sortType = this.type.sortType;
            }
        }
    });

    // TO CHECK
    
    Ext.tree.Column = Ext.define('Ext.tree.Column', {
        extend : 'Ext.grid.Column'
    });
    
    Ext.tree.NumberColumn = Ext.define('Ext.tree.NumberColumn', {
        extend : 'Ext.grid.column.Number'
    });
    
    Ext.tree.DateColumn = Ext.define('Ext.tree.DateColumn', {
        extend : 'Ext.grid.Column.Date'
    });
    
    Ext.tree.BooleanColumn = Ext.define('Ext.tree.BooleanColumn', {
        extend : 'Ext.grid.column.Boolean'
    });
    
    Ext.define('tgcolumn',{
        alias : 'Ext.grid.Column'
    });
    Ext.define('tgnumbercolumn',{
        alias : 'Ext.grid.column.Number'
    });
    Ext.define('tgdatecolumn',{
        alias : 'Ext.grid.Column.Date'
    });
    Ext.define('tgbooleancolumn',{
        alias : 'Ext.grid.column.Boolean'
    });
    
})();
