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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, WARNING_NB_RECORDS_PLOT*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * service used to show the plot
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addSelectionService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.plotService = {};

Ext.reg('sitools.user.component.dataviews.services.plotService', sitools.user.component.dataviews.services.plotService);

sitools.user.component.dataviews.services.plotService.getParameters = function () {
    return [{
        jsObj : "Ext.form.TextField", 
        config : {
            anchor : "100%", 
            fieldLabel : i18n.get("label.warning_nb_records"), 
            value : WARNING_NB_RECORDS_PLOT,
            name : "warning_nb_records"
        }
    }];
};

sitools.user.component.dataviews.services.plotService.executeAsService = function (config) {

    var grid = config.dataview;
    var datasetId = grid.datasetId;
    var datasetUrl = grid.sitoolsAttachementForUsers;
    var datasetName = grid.datasetName;
    var columnModel = grid.datasetCm;
    var formParams = grid.formParams;
    var formMultiDsParams = grid.formMultiDsParams;
    
    var sortInfo = grid.getSortInfo(); 
    
    var maxWarningRecords = WARNING_NB_RECORDS_PLOT;
    Ext.each(config.parameters, function (param) {
        if (param.name === "warning_nb_records") {
            maxWarningRecords = parseInt(param.value, 10);
        }
    }, this);
    
    var jsObj = sitools.user.component.dataPlotter;
    var componentCfg = {
        columnModel : columnModel,
        formParams : formParams,
        formMultiDsParams : formMultiDsParams,
        dataUrl : datasetUrl,
        datasetName : datasetName,
        datasetId : datasetId,
        componentType : "plot",
        preferencesPath : "/" + datasetName,
        preferencesFileName : "plot",
        filters : grid.getFilters(),
        selections : Ext.isEmpty(grid.getSelections()) ? undefined : grid.getSelectionForPlot(),
        sortInfo : sortInfo,
        selectionSize : (grid.isAllSelected()) ? grid.store.getTotalCount() : grid.getNbRowsSelected(),
        maxWarningRecords : maxWarningRecords
    };
    var windowConfig = {
        id : "plot" + datasetId,
        title : "Data plot : " + datasetName,
        iconCls : "plot",
        datasetName : datasetName,
        type : "plot",
        saveToolbar : true,
        winHeight : 600
    };
    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
};