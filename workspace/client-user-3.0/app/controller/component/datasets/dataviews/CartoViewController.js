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
Ext.define('sitools.user.controller.component.datasets.dataviews.CartoViewController', {
    extend: 'Ext.app.Controller',

    views: ['component.datasets.dataviews.CartoViewView',
        'component.datasets.services.ServiceToolbarView'],

    requires: ['sitools.public.widget.datasets.columnRenderer.BehaviorEnum'],


    init: function () {
        this.control({
            'component[componentType=datasetView] component#columnItem menucheckitem': {
                checkchange: function (item, checked) {
                    if (checked) {
                        var view = item.up('component[componentType=datasetView]');
                        var colModel = extColModelToSrv(view.getColumns());
                        view.getStore().load({
                            params: {
                                colModel: Ext.JSON.encode(colModel)
                            }
                        });
                    }
                }
            },
            'cartoView gridpanel#grid': {
                //relay event to main view to be catched by the servicesController
                selectionchange: function (selectionModel, selected, opts) {
                    selectionModel.gridView.fireEvent("selectionchange", selectionModel, selected, opts);
                },
                select: function (selectionModel, record, index, eOpts) {
                    var center = record.raw.geometry.getBounds().getCenterLonLat();
                    var map = selectionModel.gridView.down("gx_mappanel").map;
                    if (!map.getExtent().containsLonLat(center)) {
                        map.setCenter(center);
                    }
                },
                cellclick: function (table, td, cellIndex, record, tr, rowIndex, e) {
                    var view = table.up('gridpanel');

                    var column = view.columnManager.getHeaderAtIndex(cellIndex)
                    if (Ext.isEmpty(column.columnRenderer)) {
                        return;
                    }

                    var serviceToolbarView = view.up("cartoview").down('serviceToolbarView');
                    var serviceController = this.getApplication().getController('sitools.user.controller.component.datasets.services.ServicesController');
                    sitools.user.utils.DataviewUtils.featureTypeAction(column, record, serviceController, serviceToolbarView);
                }
            },
            'cartoView': {
                selectAll: function (selectionModel, selected) {

                    // Compute bounding box for the visible features
                    var bbox = new OpenLayers.Bounds();
                    Ext.each(selected, function (feature) {
                        bbox.extend(feature.raw.geometry.getBounds());
                    });
                    var map = selectionModel.gridView.down("gx_mappanel").map;
                    map.zoomToExtent(bbox);
                },
                render: function (view) {
                    //load the store at the specified page
                    var ranges = view.getRanges();
                    if (!Ext.isEmpty(ranges)) {
                        ranges = Ext.JSON.decode(ranges);
                        // calculate the page to load from the first range value
                        var firstRange = ranges[0][0];
                        var pageSize = view.getStore().pageSize;
                        var pageIndex = Math.ceil(firstRange / pageSize) + 1;
                        view.getStore().loadPage(pageIndex);
                    } else {
                        view.getStore().load();
                    }
                    view.getStore().on("load", function (store, records, success, eOpts) {
                        // do selection if those ranges are passed to the dataview
                        var ranges = view.getRanges();
                        if (!Ext.isEmpty(ranges)) {
                            var nbRecordsSelection = view.getNbRecordsSelection();
                            if (!Ext.isEmpty(nbRecordsSelection) && (nbRecordsSelection === view.store.getTotalCount())) {
                                view.getSelectionModel().selectAll();
                                view.setNbRecordsSelection(null);
                                view.setRanges(null);
                            } else {
                                ranges = Ext.JSON.decode(ranges);
                                this.selectRangeDataview(view, ranges);
                                view.setNbRecordsSelection(null);
                                view.setRanges(null);
                            }
                        }
                        var paging =view.down("pagingtoolbar");
                        var pageCount = paging.getPageData().pageCount;
                        var width = this.getPageNumberWidth(pageCount);
                        paging.down("numberfield#inputItem").setWidth(width);

                    }, this);
                },
                destroy : function (panel) {
                    Ext.util.CSS.removeStyleSheet(panel.id);
                }
            },

            'cartoView pagingtoolbar': {
                change: function (paging, pageData, eOpts) {
                    var view = paging.up("cartoView");
                    view.getSelectionModel().updateSelection();
                }
            }
            /*'livegridView' : {

             render : function (view) {
             view.getStore().on("load", function() {
             // do selection if those ranges are passed to the dataview
             var ranges = view.getRanges();
             if (!Ext.isEmpty(ranges)) {
             var nbRecordsSelection = view.getNbRecordsSelection();
             if (!Ext.isEmpty(nbRecordsSelection) && (nbRecordsSelection === view.store.getTotalCount())) {
             view.getSelectionModel().selectAll();
             view.setNbRecordsSelection(null);
             view.setRanges(null);
             } else {
             ranges = Ext.JSON.decode(ranges);
             this.selectRangeDataview(view, ranges);
             view.setNbRecordsSelection(null);
             view.setRanges(null);
             }
             }
             }, this);

             },

             resize : function (view) {
             view.getSelectionModel().updateSelection();
             view.down("pagingtoolbar").updateInfo();
             },

             afterrender : function (view) {
             view.getView().getEl().on('scroll', function (e, t, eOpts) {
             view.getSelectionModel().updateSelection();
             view.down("pagingtoolbar").updateInfo();
             }, view);
             },

             cellclick : function (table, td, cellIndex, record, tr, rowIndex, e) {
             var livegrid = table.up('livegridView');

             var column = livegrid.columnManager.getHeaderAtIndex(cellIndex)
             if (Ext.isEmpty(column.columnRenderer)) {
             return;
             }

             var serviceToolbarView = livegrid.down('serviceToolbarView');
             var serviceController = this.getApplication().getController('sitools.user.controller.component.datasets.services.ServicesController');
             sitools.user.utils.DataviewUtils.featureTypeAction(column, record, serviceController, serviceToolbarView);
             },

             viewready : function (view) {
             view.down("pagingtoolbar").updateInfo();
             },

             destroy : function (panel) {
             Ext.util.CSS.removeStyleSheet(panel.id);
             }

             }*/
        });
    },

    getPageNumberWidth : function (pagecount) {
        var width = 30;
        pagecount /=10;
        while (pagecount > 10) {
            width+=10;
            pagecount /=10;
        }
        return width;
    },

    selectRangeDataview: function (dataview, ranges) {
        var index = dataview.getStore().lastOptions.start;
        Ext.each(ranges, function (range) {
            dataview.getSelectionModel().selectRange(range[0] - index, range[1] - index, true);
        }, this);
    }
});