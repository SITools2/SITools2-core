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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl, ADMIN_PANEL_HEIGHT*/

Ext.define('sitools.admin.applications.ApplicationModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name : 'colId',
        type : 'string',
        convert : function () {
            return Ext.id();
        }
    }, {
        name : 'id',
        type : 'string'
    }, {
        name : 'name',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'category',
        type : 'string'
    }, {
        name : 'urn',
        type : 'string'
    }, {
        name : 'type',
        type : 'string'
    }, {
        name : 'url',
        type : 'string'
    }, {
        name : 'author',
        type : 'string'
    }, {
        name : 'owner',
        type : 'string'
    }, {
        name : 'lastUpdate',
        type : 'string'
    }, {
        name : 'status',
        type : 'string'
    }, {
        name : 'wadl',
        type : 'string'
    }],
});