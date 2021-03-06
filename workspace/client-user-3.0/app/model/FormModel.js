/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, window */

Ext.define('sitools.user.model.FormModel', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'string'
    }, {
        name : 'parent',
        type : 'string'
    }, {
        name : 'name',
        type : 'string'
    }, {
        name : 'css',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'width',
        type : 'int'
    }, {
        name : 'height',
        type : 'int'
    }, {
        name : 'parentUrl',
        type : 'string'
    }, {
        name : 'parameters'
    }, {
        name : 'zones'
    }, {
        name : 'collection'
    }, {
        name : 'dictionary'
    }, {
        name : 'urlServicePropertiesSearch', 
        type : 'string'
    }, {
        name : 'urlServiceDatasetSearch', 
        type : 'string'
    }, {
		name : 'authorized'	
    }, {
        name : 'nbDatasetsMax', 
        type : 'int'
    } ]

});
