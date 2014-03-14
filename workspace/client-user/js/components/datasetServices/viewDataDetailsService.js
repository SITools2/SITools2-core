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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * service used to show the details of a specific record
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addSelectionService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.viewDataDetailsService = {};

Ext.reg('sitools.user.component.dataviews.services.viewDataDetailsService', sitools.user.component.dataviews.services.viewDataDetailsService);

sitools.user.component.dataviews.services.viewDataDetailsService.getParameters = function () {
    return [];
};

sitools.user.component.dataviews.services.viewDataDetailsService.executeAsService = function (config) {
    
    var grid = config.dataview;
    var fromWhere = config.origin;
    var urlRecords = grid.urlRecords;
    var datasetId = grid.datasetId;
    var datasetUrl = grid.sitoolsAttachementForUsers;
    var datasetName = grid.datasetName;
    var selections = grid.getSelections();
    


    if (Ext.isEmpty(selections) || selections.length === 0) {
        return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
    }
    
    var componentCfg = {
        baseUrl : urlRecords + "/",
        grid : grid,
        fromWhere : fromWhere,
        datasetId : datasetId,
        datasetUrl : datasetUrl,
        selections : selections,
        preferencesPath : "/" + datasetName,
        preferencesFileName : "dataDetails"
    };
    var jsObj = sitools.user.component.viewDataDetail;

    var windowConfig = {
        id : "dataDetail" + datasetId,
        title : i18n.get('label.viewDataDetail') + " : " + datasetName,
        datasetName : datasetName,
        saveToolbar : true,
        iconCls : "dataDetail",
        type : "dataDetail",
        shadow : true,
        shadowOffset : 5,
        toolbarItems : [ {
            iconCls : 'arrow-back',
            handler : function () {
                this.ownerCt.ownerCt.items.items[0].goPrevious();
            }
        }, {
            iconCls : 'arrow-next',
            handler : function () {
                this.ownerCt.ownerCt.items.items[0].goNext();
            }
        } ]
    };
    
    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
};