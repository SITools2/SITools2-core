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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

/**
 * Plot service
 * @class sitools.user.component.datasets.services.PlotService
 * @extends sitools.user.core.Component
 */
Ext.define('sitools.user.component.datasets.services.PlotService', {
    extend : 'sitools.user.core.Component',

    statics : {
        getParameters : function () {
            return [{
                jsObj : "Ext.form.field.Text",
                config : {
                    anchor : "100%",
                    fieldLabel : i18n.get("label.warning_nb_records"),
                    value : sitools.user.component.datasets.services.PlotService.WARNING_NB_RECORDS_PLOT,
                    name : "warning_nb_records"
                }
            }];
        },
        WARNING_NB_RECORDS_PLOT : 10000
    },
    
    init : function (config) {

        var grid = config.dataview;
        var dataset = config.dataview.dataset;

        var maxWarningRecords = sitools.user.component.datasets.services.PlotService.WARNING_NB_RECORDS_PLOT;
        Ext.each(config.parameters, function (param) {
            if (param.name === "warning_nb_records") {
                maxWarningRecords = parseInt(param.value, 10);
            }
        }, this);

        var componentCfg = {
            ranges: grid.getSelectionsRange(),
            selectionSize : grid.getNbRowsSelected(),
            maxWarningRecords : maxWarningRecords,

            componentType : "plot",
            datasetName : dataset.name,
            datasetId : dataset.id,
            dataUrl : dataset.sitoolsAttachementForUsers,
            columnModel : config.columnModel,
            preferencesPath : "/" + dataset.name,
            preferencesFileName : "plot"
        };

        var sortersAndFilters = this.createSortersAndFilters(grid);

        Ext.apply(componentCfg, sortersAndFilters);

        //On récupère le sitoolsController
        var sitoolsController = Desktop.getApplication().getController	('core.SitoolsController');
        //On ouvre le composant
        sitoolsController.openComponent("sitools.user.component.datasets.plot.DataPlotter", componentCfg, {});
    },

    /**
     * Get filters, sort, formParams to the current order selection
     *
     * @param {Object}
     *            order
     * @param {Ext.grid.GridPanel}
     *            grid The current grid.
     *
     */
    createSortersAndFilters : function(grid) {
        var config = {};
        // Grid filters
        var gridFilters = grid.getRequestGridFilterParams();
        if (!Ext.isEmpty(gridFilters)) {
            config.gridFilters = gridFilters.filter;
            config.gridFiltersCfg = grid.store.getGridFiltersCfg();
        }

        //Sorters
        var storeSort = grid.getSortParams();
        if (!Ext.isEmpty(storeSort)) {
            config.sortInfo = Ext.JSON.decode(storeSort.sort);
        }

        // Form filters
        var formFilters = grid.getRequestFormFilters();
        if (!Ext.isEmpty(formFilters)) {
            config.formFilters = formFilters;
        }

        // Form concept filters
        var formConceptFilters = grid.getRequestFormConceptFilters();
        if (!Ext.isEmpty(formConceptFilters)) {
            config.formConceptFilters = formConceptFilters;
        }

        return config;
    }

});