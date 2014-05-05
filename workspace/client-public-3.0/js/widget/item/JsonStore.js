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
 * A simple json store with new methods
 * @class sitools.widget.JsonStore
 * @extends Ext.data.JsonStore
 */
Ext.define('sitools.public.widget.item.JsonStore', { 
    alternateClassName : ['sitools.widget.JsonStore'],
    extend : 'Ext.data.JsonStore',
    /**
     * dirty : true if one record have been added
     * @type Boolean
     */
    dirty : false,

    /**
     * Setter for dirty
     * @param {Boolean} value
     */
    _setDirty : function (value) {
        this.dirty = value;
    },
    /**
     * Get the dirty value
     * @return {Boolean}
     */
    _getDirty : function () {
        return this.dirty;
    },
    /**
     * Set dirty to true
     * @param {Array} records the list of records added
     */
    add : function (records) {
        this.dirty = true;
        this.callParent(arguments);
    },
    /**
     * Set dirty to true
     * @param {Ext.data.Record} record the record removed
     */
    remove : function (record) {
        this.dirty = true;
        this.callParent(arguments);
    }
});