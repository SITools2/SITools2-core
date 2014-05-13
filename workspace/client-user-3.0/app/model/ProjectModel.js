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
/* global Ext, sitools, window */

Ext.define('sitools.user.model.ProjectModel', {
    extend : 'Ext.data.Model',

//    requires : [ 'sitools.user.model.LinkModel', 'sitools.user.model.ModuleModel', 'sitools.user.model.ResourceModel' ],

    fields : [ {
        name : 'id',
        type : 'string'
    }, {
        name : 'name',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'image'
    }, {
        name : 'css',
        type : 'string'
    }, {
        name : 'status',
        type : 'string'
    }, {
        name : 'sitoolsAttachementForUsers',
        type : 'string'
    }, {
        name : 'visible',
        type : 'boolean'
    }, {
        name : 'authorized',
        type : 'boolean'
    }, {
        name : 'htmlHeader',
        type : 'string'
    }, {
        name : 'htmlDescription',
        type : 'string'
    }, {
        name : 'maintenance',
        type : 'boolean'
    }, {
        name : 'maintenanceText',
        type : 'string'
    }, {
        name : 'ftlTemplateFile',
        type : 'string'
    }, {
        name : 'navigationMode',
        type : 'string'
    }, {
        name : 'datasets'
    }, {
        name : 'modules'
    }, {
        name : 'links'
    } ],
//    hasMany : [ {
//        model : 'ResourceModel',
//        name : 'dataSets'
//    }, {
//        model : 'ModuleModel',
//        name : 'modules'
//    }, {
//        model : 'LinkModel',
//        name : 'links'
//    } ]
});
