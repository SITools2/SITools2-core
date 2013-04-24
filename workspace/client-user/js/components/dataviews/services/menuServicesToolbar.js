/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Create A Toolbar from the currents dataset services
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.menuServicesToolbar
 * @extends Ext.Toolbar
 */
sitools.user.component.dataviews.services.menuServicesToolbar = Ext.extend(Ext.Toolbar, {
    enableOverflow: true,
//    width : "97%",
    initComponent : function () {
        this.cls = "services-toolbar"; 
        
        this.urlDatasetServiceIHM = this.datasetUrl + "/services" + '/gui/{idService}';

        this.store = new Ext.data.JsonStore({
            url : this.datasetUrl + "/services",
            idProperty : 'id',
            root : 'ServiceCollectionModel.services',
            autoload : true,
            fields : [ 'id', 'type', 'name', 'description', 'icon', 'label', 'category', 'visible' ],
            listeners : {
                scope : this,
                load : this.createMenuServices
            }
        });
        this.store.load();
        
        this.serverServiceUtil = new sitools.user.component.dataviews.services.serverServicesUtil({
            datasetUrl : this.datasetUrl,
            grid : this.dataview,
            origin : this.origin
        });
        
        sitools.user.component.dataviews.services.menuServicesToolbar.superclass.initComponent.call(this);
    },
    

    createMenuServices : function (store, records, opts) {
        var icon, category, menu, btn = {};
        Ext.each(records, function (item) {
            
            if (!item.get('visible')) {
                return;
            }
            
            if (!Ext.isEmpty(category = item.get('category'))) {
                menu = this.getMenu(category);
            } else {
                menu = this;
            }
            
            
            if (!Ext.isEmpty(icon = item.get('icon'))) {
                Ext.apply(btn, {
                    iconCls : 'btn-format-icon',
                    icon : icon
                });
            }
            
            Ext.apply(btn, {
                idService : item.get('id'),
                typeService : item.get('type'),
                text : i18n.get(item.get('label')),
                cls : 'services-toolbar-btn',
                icon : icon,
                scope : this,
                handler : this.callService
            });
            
            menu.add(btn);
            
            if (this.id === menu.id) {
                this.add('-');
            }
            
        }, this);
        this.createColumnsButton();
        this.doLayout();
    },
    

    createColumnsButton : function () {
        if (this.origin !== "sitools.user.component.dataviews.tplView.TplView") {
            this.add('->', '-');
            this.add({
                tooltip : i18n.get('label.addOrDeleteColumns'),
                icon : '/sitools/cots/extjs/resources/images/default/grid/columns.gif',
                menu : this.dataview.getDatasetView().colMenu
            });
            this.dataview.getDatasetView().hdCtxIndex = 0;
        }
        
    },
    

    callService : function (button, e) {
        if (button.typeService === 'SERVER') {
            this.serverServiceUtil.callServerService(button.idService, this.dataview.getSelections());
        } else {
            this.callGUIService(button.idService);
        }
    },
    
    callGUIService : function (idService) {
        Ext.Ajax.request({
            url : this.urlDatasetServiceIHM.replace('{idService}', idService),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var guiServicePlugin = Ext.decode(ret.responseText).guiServicePlugin;

                
                
                var JsObj = eval(guiServicePlugin.xtype);

                var config = Ext.applyIf(guiServicePlugin, {
                    columnModel : this.dataview.getColumnModel(),
                    store : this.dataview.getStore(),
                    dataview : this.dataview,
                    origin : this.origin
                });

                JsObj.executeAsService(config);

            },
            failure : alertFailure
        });
    },
    
    getMenu : function (category) {
        var buttonSearch = this.find('category', category);
        var button;
        if (Ext.isEmpty(buttonSearch)) {
            button = new Ext.Button({
                category : category,
                text : category,
                cls : 'services-toolbar-btn',
                menu : new Ext.menu.Menu({
                    showSeparator : false
                }),
                iconAlign : "left",
                clickEvent : 'mousedown'
            });
            this.add(button);
            this.add('-');
        } else {
            button = buttonSearch[0];
        }
        return button.menu;
    }
});