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
Ext.define('sitools.user.controller.component.datasets.dataviews.SimpleGridController', {
    extend : 'Ext.app.Controller',

    views : [ 'component.datasets.dataviews.SimpleGridView',
              'component.datasets.services.ServiceToolbarView'],
    
    requires : ['sitools.public.widget.datasets.columnRenderer.BehaviorEnum'],
                

    init : function () {
        this.control({
            'toolbar button[name=tipsLivegrid]' : {
                click : function (btn, e) {
                    //only create one tooltip
                    if (Ext.isEmpty(btn.tTip)) {
                        btn.tTip = Ext.create("Ext.tip.ToolTip", {
                            target : btn.getEl(),
                            autoWidth : true,
                            autoLoad : {
                                url : loadUrl.get('APP_URL') + "/common/html/"+ locale.getLocale() + "/tips.html"
                            },
                            anchor : 'left',
                            autoHide : false,
                            closable : true
                        });
                    }
                    btn.tTip.show();
                },
                mouseover : function (btn, e) {
//                    btn.tTip = null;
                }
            },
            'component#columnItem menucheckitem' : {
                checkchange : function (item, checked) {
                    if (checked) {
                        var view = item.up('simpleGridView');
                        var colModel = extColModelToSrv(view.getColumns());
                        view.getStore().load({
                            params : {
                                colModel : Ext.JSON.encode(colModel)
                            }
                        });
                    }
                }
            },
            'simpleGridView' : {
                //resize : function (view) {
                //    view.getSelectionModel().updateSelection();
                //    view.down("pagingtoolbar").updateInfo();
                //},

                afterrender : function (view) {
                    //view.getView().getEl().on('scroll', function (e, t, eOpts) {
                    //    view.getSelectionModel().updateSelection();
                    //    view.down("pagingtoolbar").updateInfo();
                    //}, view);


                    view.getStore().on("load", function(records, operation, success) {
                        //check that the store is well loaded
                        if(!success) {
                            var response = view.getStore().getProxy().exceptionResponse;

                            var parent = view.up("component");
                            parent.remove(view);

                            parent.add(Ext.create("Ext.panel.Panel", {
                                title : i18n.get("label.error"),
                                layout : 'fit',
                                html : response.responseText
                            }));
                            return;
                        }
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

                cellclick : function (table, td, cellIndex, record, tr, rowIndex, e) {
                    var livegrid = table.up('simpleGridView');
                    
                    var column = livegrid.columnManager.getHeaderAtIndex(cellIndex);
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
                
            }
        });
    },
    
    selectRangeDataview : function (dataview, ranges) {
         Ext.each(ranges, function (range) {
             dataview.getSelectionModel().selectRange(range[0], range[1], true);
         }, this);
     }
});