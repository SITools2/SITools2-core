/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
        'sitools.public.utils.i18n',
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.reference',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.LoginUtils',
        'sitools.public.utils.Logout',
        'sitools.public.crypto.Base64',
        'sitools.public.utils.PopupMessage',
        'sitools.public.utils.Utils',
        'sitools.public.utils.LoginDef',
        'sitools.admin.def',
        'sitools.admin.id',
        'sitools.admin.gui',
        'sitools.public.utils.PackagingUtils',
        'sitools.public.utils.Date',
        
        /* QUICKSTART MENU */
        'sitools.admin.quickstart.Qs',
        'sitools.admin.quickstart.QsDataset',
        'sitools.admin.quickstart.QsDatasource',
        'sitools.admin.quickstart.QsForm',
        'sitools.admin.quickstart.QsProject',
        'sitools.admin.quickstart.QsSecurity',
        'sitools.admin.quickstart.QsStart',
        
        'sitools.admin.menu.seeAlso',
        
        /* WIDGETS */
        'sitools.public.widget.vtype',
        'sitools.public.widget.grid.SitoolsView',
        'sitools.public.widget.StatusBar',
        
        'sitools.public.version.Version',
        
        'sitools.public.widget.grid.GridSorterToolbar',
        
        'Ext.ux.notification.Notification',
        
        'sitools.admin.menu.TreeMenu',
        
        'sitools.public.widget.datasets.columnRenderer.BehaviorEnum',
        
        'sitools.public.widget.item.menuButton',
        'sitools.public.widget.item.TextFilter',
        'Ext.ux.form.MultiSelect',
        
        /* WIDGETS TO BE INCLUDED IN OTHER FILES */
        'sitools.public.forms.formParameterToComponent',
        
        'sitools.public.forms.ComponentFactory',
        
        'sitools.public.forms.AbstractWithUnit',
        
        'sitools.public.widget.imageChooser.TriggerField',
        
        'sitools.public.widget.date.DatePicker',
        
        'sitools.public.ux.form.ToolFieldSet',
        
        /* SITOOLS MODULES */
        'sitools.admin.applications.ApplicationsCrud',
        'sitools.admin.authorizations.AuthorizationsCrud',
        'sitools.admin.collections.CollectionsCrud',
        'sitools.admin.applications.plugins.ApplicationPluginCrud',
        'sitools.admin.applications.plugins.ApplicationResourcesCrud',
        'sitools.admin.converters.ConvertersCrud',
        'sitools.admin.datasetViews.DatasetViewsCrud',
        'sitools.admin.datasource.jdbc.DataBaseCrud',
        'sitools.admin.datasource.mongoDb.DataBaseCrud',
        'sitools.admin.dictionary.DictionaryCrud',
        'sitools.admin.dictionary.TemplateCrud',
        'sitools.admin.fileEditor.CssEditorCrud',
        'sitools.admin.fileEditor.FtlEditorCrud',
        'sitools.admin.fileEditor.LicenceEditorCrud',
        'sitools.admin.filters.FiltersCrud',
        'sitools.admin.formComponents.FormComponentsCrud',
        'sitools.admin.forms.FormCrud',
        'sitools.admin.graphs.GraphsCrud',
        'sitools.admin.guiservices.GuiServicesCrud',
        'sitools.admin.logs.AnalogProp',
        'sitools.admin.multiDs.MultiDsCrud',
        'sitools.admin.order.OrderCrud',
        'sitools.admin.portal.RssFeedPortalCrud',
        'sitools.admin.projects.ProjectsCrud',
        'sitools.admin.projects.modules.ProjectModulesCrud',
        'sitools.admin.projects.resourcesPlugins.ProjectResourcesCrud',
        'sitools.admin.resourcesPlugins.ResourcesPluginsCrud',
        'sitools.admin.rssFeed.RssFeedCrud',
////        'sitools.admin.storages.plugins.StorageFiltersCrud',
        'sitools.admin.storages.StoragesCrud',
        'sitools.admin.units.UnitsCrud',
        'sitools.admin.usergroups.GroupCrud',
        'sitools.admin.usergroups.RegCrud',
        'sitools.admin.usergroups.RoleCrud',
        'sitools.admin.usergroups.UserCrud',
        'sitools.admin.userStorage.UserStorageCrud',
        'sitools.admin.datasets.plugins.DatasetResourcesCrud',
        'sitools.admin.datasets.DatasetsCrud',
        'sitools.admin.projects.RssFeedProject',
        'sitools.admin.datasets.RssFeedDataset',
        'sitools.admin.datasets.services.DatasetServicesCrud'
        
    ],
    
    useQuickTips: true,
    
    launch : function () {
        sitools.admin.def.init();
        loadUrl.load('/sitools/client-admin/siteMap', function () {
            i18n.load(loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + LOCALE + '/gui.properties', function () {
                sitools.admin.def.initLocalizedVariables();
                Ext.MessageBox.buttonText.yes = i18n.get('label.yes');
                Ext.MessageBox.buttonText.no = i18n.get('label.no');
                Ext.QuickTips.init();
                if(!checkCookieDuration()){
                    Ext.Msg.show({
                        title: i18n.get('label.warning'),
                        msg : i18n.get("label.wrongcookieduration.configuration")
                    });
                    return;
                }
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
