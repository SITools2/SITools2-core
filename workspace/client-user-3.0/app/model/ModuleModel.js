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

Ext.define('sitools.user.model.ModuleModel', {
    extend : 'sitools.user.model.ResourceModel',

    requires : [ 'sitools.user.model.PropertyModel', 'sitools.user.model.RoleModel' ],

    fields : [ {
        name : 'priority',
        type : 'int'
    }, {
        name : 'categoryModule',
        type : 'string'
    }, {
        name : 'divIdToDisplay',
        type : 'string'
    }, {
        name : 'xtype',
        type : 'string'
    }, {
        name : 'label',
        type : 'string'
    } ],
    hasMany : [ {
        model : 'RoleModel',
        name : 'listRoles'
    }, {
        model : 'PropertyModel',
        name : 'listProjectModulesConfig'
    } ]
});
