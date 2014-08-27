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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.user.controller.modules.datasets.dataviews');

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.component.datasets.dataviews.Livegrid', {
    extend : 'sitools.user.core.Component',
    
    controllers : ['sitools.user.controller.component.datasets.dataviews.LivegridController',
                   'sitools.user.controller.component.datasets.services.ServicesController'],
    
    requires : ['sitools.user.view.component.datasets.dataviews.LivegridView',
                'sitools.user.store.DataviewsStore'],
    
    /**
     * @require dataset
     * @optionnal formParams
     */
//    init : function (dataset, formParams) {
	init : function (componentConfig, windowConfig) {
        
		var windowSettings = {}, componentSettings = {};
		
        var dataviewConfig = sitoolsUtils.arrayProperties2Object(componentConfig.datasetViewConfig);

        var fields = this.getFields(componentConfig.columnModel);
        var columns = this.getColumns(componentConfig.columnModel, componentConfig.dictionaryMappings, dataviewConfig);
        var primaryKey = this.getPrimaryKey(componentConfig);
        
        var datasetStore = Ext.create("sitools.user.store.DataviewsStore", {
            fields : fields,
            urlAttach : componentConfig.sitoolsAttachementForUsers,
            primaryKey : primaryKey,
            formParams : componentConfig.formParams
        });
        
        Ext.apply(windowSettings, windowConfig, {
        	datasetName : componentConfig.name,
            type : "data",
            title : i18n.get('label.datasets') + " : " + componentConfig.name,
//            id : "dataset" + dataset.id,
            id : Ext.id(),
            saveToolbar : true,
            winWidth : 900,
            winHeight : 400,
            iconCls : "dataviews",
            typeWindow : 'data'
        });
        
        Ext.apply(componentSettings, componentConfig, {
            dataset : componentConfig,
            store : datasetStore,
            preferencesPath : "/" + componentConfig.name + "/datasets",
            preferencesFileName : componentConfig.name,
            columns : columns,
            // searchAction : this.searchAction,
            scope : this
        });
        
        var view = Ext.create('sitools.user.view.component.datasets.dataviews.LivegridView', componentSettings);

        this.setComponentView(view);
        this.show(view, windowSettings);
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
     * Build a {Ext.grid.ColumnModel} columnModel with a dataset informations
     * @param {Array} listeColonnes Array of dataset Columns
     * @param {Array} dictionnaryMappings Array of Dataset dictionnary mappings 
     * @param {Object} dataviewConfig the specific dataview Configuration.
     * @return {Ext.grid.ColumnModel} the builded columnModel
     */
    getColumns : function (listeColonnes, dictionnaryMappings, dataviewConfig, dataviewId) {
        var columns = [];
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                
                var tooltip = "";
                if (item.toolTip) {
                    tooltip = item.toolTip;
                } else {
                    if (Ext.isArray(dictionnaryMappings) && !Ext.isEmpty(dictionnaryMappings)) {
                        var dico = dictionnaryMappings[0];
                        var dicoMapping = dico.mapping || [];
                        Ext.each(dicoMapping, function (mapping) {
                            if (item.columnAlias == mapping.columnAlias) {
                                var concept = mapping.concept || {};
                                if (!Ext.isEmpty(concept.description)) {
                                    tooltip += concept.description.replace('"', "''") + "<br>";
                                }
                            }
                        });
                    }
                }
               
//                var renderer = sitools.user.component.dataviews.dataviewUtils.getRendererLiveGrid(item, dataviewConfig, dataviewId);
                var hidden;
                if (Ext.isEmpty(item.visible)) {
                    hidden = item.hidden;
                } else {
                    hidden = !item.visible;
                }
                if (Ext.isEmpty(item.columnRenderer) ||  ColumnRendererEnum.NO_CLIENT_ACCESS != item.columnRenderer.behavior) {
                    columns.push({
                        columnAlias : item.columnAlias,
                        dataIndexSitools : item.dataIndex,
                        dataIndex : item.columnAlias,
                        text : item.header,
                        width : item.width,
                        sortable : item.sortable,
                        hidden : hidden,
                        tooltip : tooltip,
                        //TODO
//                        renderer : renderer,
                        schema : item.schema,
                        tableName : item.tableName,
                        tableAlias : item.tableAlias,
                        id : item.id,
                        primaryKey : item.primaryKey,
                        previewColumn : item.previewColumn,
                        filter : item.filter,
                        sqlColumnType : item.sqlColumnType, 
                        columnRenderer : item.columnRenderer, 
                        specificColumnType : item.specificColumnType,
                        menuDisabled : true,
                        format : item.format
                    });
                }
                
            }, this);
        }
        return columns;
    },
    
    getFields : function (listeColonnes) {
        var fields = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                fields[i] = Ext.create("Ext.data.Field", {
                    name : item.columnAlias,
                    primaryKey : item.primaryKey,
                    type : sql2ext.get(item.sqlColumnType)
                });
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