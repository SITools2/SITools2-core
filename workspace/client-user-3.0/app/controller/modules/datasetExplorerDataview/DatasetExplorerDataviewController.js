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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.controller.modules.datasetExplorerDataview');
/**
 * Controller for DatasetExplorerDataViewView
 *
 * @class sitools.user.controller.modules.datasetExplorerDataview.DatasetExplorerDataviewController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.user.controller.modules.datasetExplorerDataview.DatasetExplorerDataviewController', {
    extend : 'Ext.app.Controller',

    views : [ 'sitools.user.view.modules.datasetExplorerDataview.DatasetExplorerDataViewView' ],

    init : function () {
        this.control({
            'datasetExplorerDataViewView' : {
                render : function (datasetExplorerDataview) {
                    datasetExplorerDataview.store.load();
                }
            },

            'datasetExplorerDataViewView toolbar button#openGraphFromDsExplorer' : {
                click : function (button) {
                    var datasetViewExplorer = button.up('datasetExplorerDataViewView');

                    var sitoolsController = this.getApplication().getController('core.SitoolsController');
                    sitoolsController.openModule(datasetViewExplorer.projectGraphModule);
                }
            },

            'datasetExplorerDataViewView toolbar button#sorterNameBtn' : {
                click : function (button) {
                    var datasetViewExplorer = button.up('datasetExplorerDataViewView');
                    this.changeSortDirection(datasetViewExplorer, button, true);
                }
            },

            'datasetExplorerDataViewView toolbar button#sorterNbRecordsBtn' : {
                click : function (button) {
                    var datasetViewExplorer = button.up('datasetExplorerDataViewView');
                    this.changeSortDirection(datasetViewExplorer, button, true);
                }
            }
        });
    },

    /**
     * Callback handler used when a sorter button is clicked or reordered
     * @param {Ext.Button} button The button that was clicked
     * @param {Boolean} changeDirection True to change direction (default). Set to false for reorder
     * operations as we wish to preserve ordering there
     */
    changeSortDirection: function (datasetViewExplorer, button, changeDirection) {
        var sortData = button.sortData,
            iconCls  = button.iconCls;

        if (sortData != undefined) {
            if (changeDirection == true) {
                var direction = (sortData.direction == "ASC") ? "DESC" : "ASC";
                var cls = (iconCls == "sort-asc") ? "sort-desc" : "sort-asc";

                button.toggle("ASC", "DESC");
                button.sortData.direction = direction;
                button.setIconCls(cls);
            }

            datasetViewExplorer.store.clearFilter();
            var sorters = this.getSorters(datasetViewExplorer);
            datasetViewExplorer.store.sort(sorters);
        }
    },

    /**
     * Returns an array of sortData from the sorter buttons
     * @return {Array} Ordered sort data from each of the sorter buttons
     */
    getSorters: function (datasetViewExplorer) {
        var sorters = [];

        Ext.each(datasetViewExplorer.query('toolbar button'), function (button) {
            if (!Ext.isEmpty(button.sortData)) {
                sorters.push(button.sortData);
            }
        }, this);

        return sorters;
    }
});
