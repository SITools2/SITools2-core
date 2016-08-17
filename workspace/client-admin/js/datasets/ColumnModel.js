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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl, ADMIN_PANEL_HEIGHT*/

Ext.define('sitools.admin.datasets.ColumnModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name : 'id',
        type : 'string'
    }, {
        name : 'dataIndex',
        type : 'string'
    }, {
        name : 'schemaName',
        mapping : 'schema',
        type : 'string'
    }, {
        name : 'tableAlias',
        type : 'string'
    }, {
        name : 'tableName',
        type : 'string'
    }, {
        name : 'header',
        type : 'string'
    }, {
        name : 'toolTip',
        type : 'string'
    }, {
        name : 'width',
        type : 'int'
    }, {
        name : 'sortable',
        type : 'boolean'
    }, {
        name : 'visible',
        type : 'boolean'
    }, {
        name : 'filter',
        type : 'boolean'
    }, {
        name : 'columnOrder',
        type : 'int'
    },
    // {name : 'urlColumn', type : 'boolean'},
    // {name : 'previewColumn', type : 'boolean'},
    {
        name : 'columnRendererCategory',
        type : 'String'
    }, {
        name : 'columnRenderer',
        type : 'object'
    }, {
        name : 'primaryKey',
        type : 'boolean'
    }, {
        name : 'sqlColumnType',
        type : 'string'
    }, {
        name : 'columnAlias',
        type : 'string'
    }, {
        name : 'specificColumnType',
        type : 'string'
    }, {
        name : 'javaSqlColumnType',
        type : 'int'
    }, {
        name : 'columnClass',
        type : 'int'
    }, {
        name : 'dimensionId', 
        type : 'string'
    }, {
        name : 'unit'            
    }, {
        name : 'format', 
        type : 'string'
    }, {
        name : 'orderBy', 
        type : 'string'
    }, {
        name : 'category', 
        type : 'string'
    }]
});