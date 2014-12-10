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

/*global Ext, sitools, i18n, Project, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/

Ext.namespace('sitools.user.view.modules.addToCartModule');
/**
 * AddToCart Module
 * @class AddToCartModuleView
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.modules.addToCartModule.AddToCartModuleView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.addToCartModule',
    
    bodyBorder : false,
    border : false,
    layout : 'fit',
    padding : "5px",
    
    initComponent : function () {
        (Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', this.user) + "/files";
        this.urlCartFileForServer = this.AppUserStorage + getCartFolder(Project.projectName) + "/" + this.user + "_CartSelections.json";
        this.urlCartFile = loadUrl.get('APP_URL') + this.urlCartFileForServer;
        
        var orderServices;
        this.moduleModel.listProjectModulesConfig().each(function (config) {
            if (!Ext.isEmpty(config.get("value"))) {
                switch (config.get("name")) {
                case "orderServices" :
                    orderServices = Ext.JSON.decode(config.get("value"));
                    break;
                }
            }
        }, this);
        
        this.tbarMenu = Ext.create('Ext.menu.Menu', {
        	border : false,
        	plain : true,
        	itemId : 'orderMenu'
        });
        
//        this.tbarMenu.add('<b class="menu-title"><i>' + i18n.get('label.broadcastMode') + '</i></b>', '-');
        
        this.tbarMenu.add({
        	text : i18n.get('label.broadcastMode'),
        	plain : false,
        	canActivate : false,
        	cls : 'userMenuCls'
        }, '-');
        
        Ext.each(orderServices, function (service) {
            this.tbarMenu.add({
                text : service.label,
                serviceName : service.name,
                serviceId : service.id,
                serviceUrl : service.url,
                cls : 'menuItemCls'
            });
        }, this);
        
        
        var staticToolbar = Ext.create('Ext.toolbar.Toolbar', {
            itemId : 'staticCartToolbar',
            border : false,
            width : 65,
            items : [{
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
                tooltip : i18n.get('label.refreshOrder'),
                cls : 'button-transition',
                itemId : 'refresh'
            }, {
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                tooltip : i18n.get('label.deleteArticleSelection'),
                itemId : 'deleteOrder',
                cls : 'button-transition'
            }]
        });
        
        var mainToolbar = Ext.create('Ext.toolbar.Toolbar', {
            itemId : 'mainMenuToolbar',
            enableOverflow : true,
            border : false,
            flex : 1,
            items : [{
                xtype : 'button',
                text : '<b>' + i18n.get('label.downloadOrder') + '</b>',
                name : 'orderBtn',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/download.png',
                tooltip : i18n.get('label.downloadOrder'),
                cls : 'sitools-btn-green',
                menu : this.tbarMenu
            }]
        });
        
        this.tbar = {
            xtype : 'toolbar',
            cls : 'services-toolbar',
            padding : "0px",
            layout : {
                type : 'hbox',
                align : 'stretch'
            },
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [mainToolbar, staticToolbar]
        };
        
        this.cartsSelectionsStore = Ext.create('sitools.user.store.CartsSelectionsStore');
        this.cartsSelectionsStore.setCustomUrl(this.urlCartFile);
        
        var columns =  [{
            header : i18n.get('label.datasetName'),
            width : 250,
            sortable : true,
            dataIndex : 'datasetName'
        }, {
            header : i18n.get('label.orderAddToCart'),
            width : 250,
            sortable : true,
            dataIndex : 'orderDate',
            format : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
            xtype : 'datecolumn'
        }, {
            header : i18n.get('label.nbArticles'),
            width : 150,
            sortable : true,
            dataIndex : 'nbRecords'
        }];
        
        this.gridPanel = Ext.create('Ext.grid.Panel', {
        	name : 'cartGridPanel',
            region : 'center',
            columns : columns,
            store : this.cartsSelectionsStore,
            forceFit : true,
            itemId: "selectionsPanel"
        });
        
        this.containerArticlesDetailsPanel = Ext.create('Ext.panel.Panel', {
            region : 'south',
//            frame : true,
            bodyBorder : false,
            border : false,
            collapsible : true,
            collapsed : true,
            forceLayout : true,
            layout : 'fit',
            height : 300,
            split : true
        });
        
        var cartModuleItems = [this.gridPanel, this.containerArticlesDetailsPanel];
        
        var description = i18n.get('label.descriptionAddToCartModule');
        
        if (description !== "label.descriptionAddToCartModule") {
            var descriptionPanel = Ext.create('Ext.panel.Panel', {
                height : 120,
                html : description, 
                region : "north",
                bodyStyle : "background-color : white;",
                border : false,
                collapsible : true, 
                autoScroll : true, 
                title : i18n.get('label.information')
            });
            cartModuleItems.push(descriptionPanel);
        }
        
        
        this.hboxPanel = Ext.create('Ext.panel.Panel', {
            id : 'cartModuleHBox',
            layout : 'border',
            items : cartModuleItems,
            border : false,
            bodyStyle : "background-color : white;"
        });
        
        this.items = [ this.hboxPanel ];
        
		this.callParent(arguments);
    }, 
    
//    /**
//     * Set the file order for the current user from responseText
//     * @param response the responseText to use as cartOrder
//     */
//    setCartOrdersFile : function (response) {
//        if (Ext.isEmpty(response.responseText)) {
//            return;
//        }
//        try {
//            var json = Ext.decode(response.responseText);
//            this.cartOrderFile = json;
//            this.cartsSelectionsStore.loadData(json);
//            this.deleteOrder();
//        } catch (err) {
//            return;
//        }
//    },
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    } 
});
