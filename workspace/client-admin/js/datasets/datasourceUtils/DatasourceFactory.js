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
/*global Ext, sitools, ID, i18n, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl*/
Ext.namespace('sitools.admin.datasets.datasourceUtils');


/**
 * @class sitools.admin.datasets.DatasourceFactory
 */
Ext.define('sitools.admin.datasets.datasourceUtils.DatasourceFactory', {
    singleton : true,
    requires : ['sitools.admin.datasets.datasourceUtils.jdbcUtils',
               'sitools.admin.datasets.datasourceUtils.mongoDbUtils'],
    
    getDatasource : function (datasourceType, scope) {
        //  sitools.user.forms.components.ComponentFactory = function (context) {
        if (datasourceType.jdbc) {
            return Ext.create('sitools.admin.datasets.datasourceUtils.jdbcUtils', {
                scope : scope
            });
        }
        if (datasourceType.mongoDb) {
            return Ext.create('sitools.admin.datasets.datasourceUtils.mongoDbUtils', {
                scope : scope
            });
        }
    }
});