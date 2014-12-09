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
    
    requires : ['sitools.user.utils.ServerServiceUtils'],
    
    init : function () {
        this.control({
            'component[componentType=datasetView]' : {
                selectionchange : function (selectionModel, selected, opts) {
                    selectionModel.gridView.down('toolbar').updateContextToolbar();
                },
                afterrender : function (datasetView) {
                    var serverServiceUtil = Ext.create('sitools.user.utils.ServerServiceUtils', {
                        datasetUrl : datasetView.dataset.sitoolsAttachementForUsers,
                        grid : datasetView,
                        origin : this.$className
                    });
                    datasetView.serviceServerUtil = serverServiceUtil;
                }
            },
            'component[componentType=datasetView] serviceToolbarView > #servicesDsToolbar button, component[componentType=datasetView] serviceToolbarView > #servicesDsToolbar menuitem' : {
                click : function (button, e, opts) {
                    this.callService(button);
                }
            }
        });
    },
    
    callService : function (button) {
        if (button.typeService === 'SERVER') {
            var dataview = button.up("component[componentType=datasetView]");
            dataview.serviceServerUtil.callServerService(button.idService);
        } else if (button.typeService === 'GUI') {
            
            var idService = button.idService;
            var serviceToolbarView = button.up("serviceToolbarView");

	        var guiServiceStore = serviceToolbarView.getGuiServiceStore();
	        var service = guiServiceStore.getById(idService);

//            this.callGuiService(button);
            this.callGuiService(service, serviceToolbarView);
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
    callGuiService : function (service, serviceView, record, columnAlias) {
        var guiServiceStore = serviceView.getGuiServiceStore();
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
        
        var dataview = serviceView.up("component[componentType=datasetView]");
        
        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController'); 
        
        var config = Ext.apply(guiServicePlugin, {
            columnModel : dataview.columns,
            store : dataview.getStore(),
            dataview : dataview,
            origin : this.origin,
            record : record,
            columnAlias : columnAlias
        });

        var serviceObj = sitoolsController.openComponent(guiServicePlugin.xtype, config);            
    },
    
     getService : function (columnAlias, serviceToolbarView) {
        return serviceToolbarView.guiServiceStore.guiServiceMap.get(columnAlias);
    }
    
});