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

Ext.namespace('sitools.user.controller.modules.datasets.services');

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.controller.component.datasets.services.ServicesController', {
    extend : 'Ext.app.Controller',

    views : ['component.datasets.services.ServiceToolbarView'],
    
    requires : [],
    
    init : function () {
        this.control({
            'livegridView' : {
                selectionchange : function (selectionModel, selected, opts) {
                    selectionModel.gridView.down('toolbar').updateContextToolbar();
                }
            },
            'livegridView toolbar button' : {
                click : function (button, e, opts) {
                    this.callService(button);
                }
            }
        });
    },
    callService : function (button) {
        if (button.typeService === 'SERVER') {
            this.serverServiceUtil.callServerService(button.idService, this.dataview.getSelections());
        } else if (button.typeService === 'GUI') {
            this.callGuiService(button);
        }
    },
    
    /**
     * Call a GuiService
     * 
     * @param idService
     *            {String} the id of the service
     * @param record
     *            {Ext.data.Record} the record to execute the guiservice
     * @param columnAlias
     *            {String} the columnAlias
     */
    callGuiService : function (button, record, columnAlias) {
        var idService = button.idService;
        var guiServiceStore = button.up('toolbar').getGuiServiceStore();
        var service = guiServiceStore.getById(idService);
        if (Ext.isEmpty(service)) {
            popupMessage({
                iconCls : 'x-icon-information',
                title : i18n.get('label.warning'),
                html : i18n.get("label.cannot-find-guiservice"),
                autoDestroy : true,
                hideDelay : 1000
            });
            return;
        }
        
        var guiServicePlugin = {};
        Ext.apply(guiServicePlugin, service.data);
        
        console.log("TODO execute : " + guiServicePlugin.xtype);
        
        var dataview = button.up("livegridView");
        
        Ext.require(guiServicePlugin.xtype, function (serviceObj) {
            var config = Ext.apply(guiServicePlugin, {
                columnModel : dataview.columns,
                store : dataview.getStore(),
                dataview : dataview,
                origin : this.origin,
                record : record,
                columnAlias : columnAlias
            });

            serviceObj.executeAsService(config);     
        }, this);
    }
});