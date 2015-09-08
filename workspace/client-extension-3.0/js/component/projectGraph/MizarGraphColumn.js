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

Ext.namespace('sitools.extension.component.projectGraph');

/**
 * @class sitools.extension.component.projectGraph.MizarGraphColumn
 */
Ext.define('sitools.extension.component.projectGraph.MizarGraphColumn', {
    extend : "sitools.user.core.Component",

    getAdditionalColumns : function () {
        return [{
            xtype:'actioncolumn',
            text : i18n.get("label.mizarMappingService"),
            flex : 1,
            name : 'mizar',
            dataIndex : "datasetId",
            items: [{
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_EXTENSION_URL') + '/resources/img/mizarModule/mizar.png',
                tooltip: 'Mizar Service',
                scope : this,
                handler: function(grid, rowIndex, colIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    var mizarServiceUrl = record.get("url") + '/services/gui';
                    this.callMizarService(record, mizarServiceUrl);
                }
            }]
        }];
    },

    callMizarService : function (recordDataset, mizarServiceUrl) {
        Ext.Ajax.request({
            method : 'GET',
            url : mizarServiceUrl,
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    return;
                }
                var mizarService = this.checkIfMizarServiceExists(Json.data);
                if (mizarService != null) {
                    Ext.Ajax.request({
                        method: 'GET',
                        url: recordDataset.get('url'),
                        scope: this,
                        success: function (ret) {
                            var Json = Ext.decode(ret.responseText);
                            if (!Json.success) {
                                return;
                            }

                            var config = Ext.apply({
                                fromProjectGraph : true,
                                parameters : mizarService.parameters,
                                dataset : Json.dataset
                            });

                            var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');

                            var mizarXtype = 'sitools.extension.component.datasets.services.MizarMappingService';
                            sitoolsController.openComponent(mizarXtype, config);
                        }
                    });
                } else {
                    popupMessage('', i18n.get('label.mizarServiceNotFound'));
                }
            }
        });
    },

    checkIfMizarServiceExists : function (services) {
        var mizarService = null;
        Ext.each(services, function (service) {
            if (service.xtype = 'sitools.extension.component.datasets.services.MizarMappingService') {
                mizarService = service;
                return;
            }
        });
        return mizarService;
    }

});
