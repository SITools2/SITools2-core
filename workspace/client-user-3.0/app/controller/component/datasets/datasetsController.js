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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext*/

Ext.namespace('sitools.user.controller.modules.datasets');

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.controller.component.datasets.DatasetsController', {
    extend : 'sitools.user.controller.component.ComponentController',

    views : [ 'component.datasets.DatasetsView' ],

    init : function () {
        this.control({});
    },

    openDataset : function (dataset, winConfig) {

        var fields = this.getFields(dataset);
        var primaryKey = this.getPrimaryKey(dataset);
        
        var datasetStore = Ext.create("Ext.data.JsonStore", {
            autoLoad : true,
            pageSize : 300,
            buffered : true,
            fields : fields,
            proxy : {
                type : 'ajax',
                url : dataset.sitoolsAttachementForUsers + "/records",
                reader : {
                    type : 'json',
                    id : primaryKey,
                    root : 'data'
                }
            }
        });

        var windowSettings = {
            datasetName : dataset.name,
            type : "data",
            title : i18n.get('label.datasets') + " : " + dataset.name,
            id : "dataset" + dataset.id,
            saveToolbar : true,
            winWidth : 900,
            winHeight : 400,
            iconCls : "dataviews"
        };
        var view = Ext.create('sitools.user.view.component.datasets.DatasetsView', {
            dataset : dataset,
            store : datasetStore,
            preferencesPath : "/" + dataset.name + "/datasets",
            preferencesFileName : dataset.name,
            // searchAction : this.searchAction,
            scope : this
        });

        this.setComponentView(view);
        this.open(view, windowSettings);
    },

    getFields : function (dataset) {
        var listeColonnes = dataset.columnModel;
        var fields = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                fields[i] = {
                    name : item.columnAlias,
                    primaryKey : item.primaryKey,
                    type : sql2ext.get(item.sqlColumnType)
                };
                if (sql2ext.get(item.sqlColumnType) === 'boolean') {
                    Ext.apply(fields[i], {
                        convert : function (value, record) {
                            if (value == "f" || value == "false" || value === 0) {
                                return 0;
                            }
                            if (value == "t" || value == "true" || value == 1) {
                                return 1;
                            }
                            return value;
                        }
                    });
                }
                i++;

            }, this);
        }
        return fields;

    },
    
    /**
     * @param {Array}
     *            ColumnModel of the grid
     * 
     * @returns {String} The columnAlias of the primaryKey
     */
    getPrimaryKey : function (dataset) {
        var listeColonnes = dataset.columnModel;
        var i = 0, primaryKey = "";
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                if (!Ext.isEmpty(item.primaryKey)) {
                    if (item.primaryKey) {
                        primaryKey = item.columnAlias;
                    }
                }
            }, this);
        }
        return primaryKey;
    },

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    }
});
