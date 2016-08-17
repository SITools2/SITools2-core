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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.define('sitools.admin.graphs.GraphNodeModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name :'type'
    }, {
        name : 'description'
    }, {
        name : 'image'
    }, {
        name : 'nbRecord'
    }, {
        name : 'datasetId'
    }, {
        name : 'imageDs'
    }, {
        name : 'readme'
    }, {
        name : 'status'
    }, {
        name : 'visible'
    }, {
        name : 'url'
    }, {
        name : 'text'
    }, {
        name : 'iconCls',
        convert : function (value, record) {
            if (record.get("leaf")) {
                return "x-tree-node-dataset";
            } else {
                return "x-tree-node-folder";
            }
        }
    }]
});