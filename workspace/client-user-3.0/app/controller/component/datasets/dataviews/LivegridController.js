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
Ext.define('sitools.user.controller.component.datasets.dataviews.LivegridController', {
    extend : 'Ext.app.Controller',

    views : [ 'component.datasets.dataviews.LivegridView',
              'component.datasets.services.ServiceToolbarView'],
    
    requires : ['sitools.public.widget.datasets.columnRenderer.BehaviorEnum',
                'sitools.public.utils.Utils'],
                

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
                                url : loadUrl.get('APP_URL') + "/common/html/"+ locale.getLocale() + "/tips.html",
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
                        var view = item.up('livegridView');
                        var colModel = extColModelToSrv(view.columns);
                        view.getStore().load({
                            params : {
                                colModel : Ext.JSON.encode(colModel)
                            }
                        });
                    }
                }
            },
            'livegridView' : {
            	resize : function (view) {
                    view.getSelectionModel().updateSelection();
                },

                afterrender : function (view) {
                    view.getView().getEl().on('scroll', function (e, t, eOpts) {
                        view.getSelectionModel().updateSelection();
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
                }
                
            }
        });
    }
});