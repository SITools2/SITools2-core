/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl, initAppli*/
Ext.define('sitools.admin.Application', {
    name : 'sitools',

    extend : 'Ext.app.Application',
    
    requires : [ 
        /* UTILS */
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.i18n',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.LoginUtils',
        'sitools.public.crypto.Base64',
        
                /* WIDGETS */
        'sitools.public.widget.StatusBar',
        
        'sitools.public.version.Version',
        
        'sitools.public.widget.grid.GridSorterToolbar',
        
        'Ext.ux.notification.Notification',
        
        'sitools.admin.menu.TreeMenu',
        
        'sitools.public.widget.datasets.columnRenderer.behaviorEnum',
        
        'sitools.public.widget.grid.SitoolsView',
        
        'sitools.public.widget.item.menuButton',
        'sitools.public.widget.item.TextFilter',
        
        /* WIDGETS TO BE INCLUDED IN OTHER FILES */
        'sitools.public.forms.formParameterToComponent',
        
        'sitools.public.forms.ComponentFactory',
        
        'sitools.public.forms.AbstractWithUnit',
        
        'sitools.public.widget.imageChooser.TriggerField',
        
        'sitools.public.widget.date.DatePicker',
        
        'Ext.ux.form.ToolFieldSet',
        
        /* SITOOLS MODULES */
        'sitools.admin.applications.applicationsCrud',
        'sitools.admin.authorizations.authorizationsCrud',
        'sitools.admin.collections.CollectionsCrud',
        'sitools.admin.applications.plugins.ApplicationPluginCrud',
        'sitools.admin.applications.plugins.applicationResourcesCrud',
        'sitools.admin.converters.convertersCrud',
        'sitools.admin.datasetViews.DatasetViewsCrud',
        'sitools.admin.datasource.jdbc.DataBaseCrud',
        'sitools.admin.datasource.mongoDb.DataBaseCrud',
        'sitools.admin.dictionary.dictionaryCrud',
        'sitools.admin.dictionary.templateCrud',
        'sitools.admin.fileEditor.cssEditorCrud',
        'sitools.admin.fileEditor.ftlEditorCrud',
        'sitools.admin.fileEditor.licenceEditorCrud',
        'sitools.admin.filters.filtersCrud',
        'sitools.admin.formComponents.FormComponentsCrud',
        'sitools.admin.forms.formCrud',
        'sitools.admin.graphs.graphsCrud',
        'sitools.admin.guiservices.GuiServicesCrud',
        'sitools.admin.logs.analogProp',
        'sitools.admin.multiDs.MultiDsCrud',
        'sitools.admin.order.orderCrud',
        'sitools.admin.portal.rssFeedPortalCrud',
        'sitools.admin.projects.projectsCrud',
        'sitools.admin.projects.modules.ProjectModulesCrud',
        'sitools.admin.projects.resourcesPlugins.projectResourcesCrud',
        'sitools.admin.resourcesPlugins.resourcesPluginsCrud',
        'sitools.admin.rssFeed.rssFeedCrud',
////        'sitools.admin.storages.plugins.storageFiltersCrud',
        'sitools.admin.storages.storagesCrud',
        'sitools.admin.units.unitsCrud',
        'sitools.admin.usergroups.GroupCrud',
        'sitools.admin.usergroups.RegCrud',
        'sitools.admin.usergroups.RoleCrud',
        'sitools.admin.usergroups.UserCrud',
        'sitools.admin.userStorage.userStorageCrud',
        'sitools.admin.datasets.plugins.datasetResourcesCrud',
        'sitools.admin.datasets.datasetsCrud',
        'sitools.admin.projects.rssFeedProject',
        'sitools.admin.datasets.rssFeedDataset',
        'sitools.admin.datasets.services.datasetServicesCrud'
        
    ],
    
    useQuickTips: true,
    

    launch : function () {
        loadUrl.load('/sitools/client-admin/siteMap', function () {
            i18n.load(loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + LOCALE + '/gui.properties', function () {
                Ext.MessageBox.buttonText.yes = i18n.get('label.yes');
                Ext.MessageBox.buttonText.no = i18n.get('label.no');
                Ext.QuickTips.init();
                if (Ext.isEmpty(Ext.util.Cookies.get('scheme')) || Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
                    sitools.public.utils.LoginUtils.connect({
                        url : loadUrl.get('APP_URL') + '/authentication/login',
                        handler : initAppli,
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist'
                    });
                } else {
                    initAppli();
                }
            });
        });

    }
    
    
    
    
});
