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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 */

Ext.namespace('sitools.user.view.component.datasets.services');

Ext.define('sitools.user.view.component.datasets.services.ServiceToolbarView', {
    extend : 'Ext.toolbar.Toolbar',
    alias : 'widget.serviceToolbarView',
    
    config : {
        store : null,
        guiServiceStore : null,
        sortedRecords : null
    },
    border : false,
    layout : {
        type : 'hbox',
        align : 'stretch'
    },

    initComponent : function () {
        var store = Ext.create("sitools.user.store.DatasetServicesStore", {
            datasetUrl : this.datasetUrl  
        });
        
        this.setStore(store);
        
        store.load({
            scope : this,
            callback : function (records, operation, success) {
            	var recs = this.sortServices(records);
            	this.setSortedRecords(recs);
            	this.onLoadServices(recs)
            }
        });
        
        var guiServiceStore = Ext.create("sitools.user.store.GuiServicesStore", {
            datasetUrl : this.datasetUrl,
            columnModel : this.columnModel
        });
        guiServiceStore.load();
        this.setGuiServiceStore(guiServiceStore);

        this.staticToolbar = Ext.create('Ext.toolbar.Toolbar', {
            itemId : 'staticDsToolbar',
            border : false,
            width : 165
        });

        this.servicesToolbar = Ext.create('Ext.toolbar.Toolbar', {
            itemId : 'servicesDsToolbar',
            enableOverflow : true,
            border : false,
            flex : 1
        });

        this.items = [this.servicesToolbar, this.staticToolbar];

        this.callParent(arguments);
    },
    
    afterRenderToolbar : function (grid) {
        var customToolbarButtons = grid.getCustomToolbarButtons();
        var serviceToolbarView = grid.down('serviceToolbarView');
        serviceToolbarView.staticToolbar.add(customToolbarButtons);
    },
    
    onLoadServices : function (records) {
        var icon, category, menu = this, btn, cfg = [];
        Ext.each(records, function (item) {
            menu = cfg;
            
            if (item instanceof Ext.toolbar.Item || !this.isService(item)) {
            	menu.push(item);
                return;
            }
            
            if (!item.get('visible')) {
                return;
            }
            
            if (!this.isSelectionOK(item.get('dataSetSelection'))) {
                return;
            }
            
            var xtype = "button";
            if (!Ext.isEmpty(category = item.get('category'))) {
                menu = this.getMenu(category, cfg);
                xtype = "menuitem";
            }
            
            btn = {};
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
                xtype : xtype
            });
            
            menu.push(btn);
            
            if (this.id === menu.id) {
            	menu.push(' ');
            }
            
        }, this);

        this.servicesToolbar.add(cfg);
    },
    
    /**
     * Update the toolbar according to the dataview selection 
     */
    updateContextToolbar : function () {
        if (this.store.getTotalCount() === 0) {
            return;
        }
        
        var records = this.getSortedRecords();
        this.servicesToolbar.removeAll();
        
        this.onLoadServices(records);
    },
    
    isService : function (item) {
        return item instanceof sitools.user.model.DatasetServicesModel;
    },
    
    getMenu : function (category, cfg) {
        var buttonSearch = this.findButtonInCfg(category, cfg);
        var button;
        if (Ext.isEmpty(buttonSearch)) {
            button = {
        		xtype : "button",
                category : category,
                text : category,
                cls : 'services-toolbar-btn',
                menu : {
                	border : false,
                    showSeparator : false,
                    items : []
                },
                iconAlign : "right",
                clickEvent : 'mousedown'
            };
            cfg.push(button);
            cfg.push(' ');
        } else {
            button = buttonSearch;
        }
        return button.menu.items;
    },
    
    
    findButtonInCfg : function (category, cfg) {
    	var result = null;
    	Ext.each(cfg, function(item) {
    		if(item.xtype == "button" && item.category == category){
    			result = item;
    			return;
    		}
    	});    	
    	return result;
    	
    },
    
    /**
     * Sort all services in the right order before being displayed
     * @param records
     *          
     * @returns Tab of records
     */
    sortServices : function (records) {
        var tbRight = [], tb = [];

//        tb = tb.concat(this.addAdditionalButton());
        this.staticToolbar.add(this.addAdditionalButton());

        Ext.each(records, function (item) {
        	if (item.get("visible")) {
	            if (item.get('position') === 'left' || Ext.isEmpty(item.get('position'))) {
	                tb.push(item);
	            } else {
	                tbRight.push(item);
	            }
        	}
        });
        tb.push("->");
        
        return tb.concat(tbRight);
    },
    
    /**
     * Return a array with the column filter button
     */
    addAdditionalButton : function () {
        //var dataview = this.up('component[componentType=datasetView]');
        var dataview = this.up('component[sitoolsType=datasetView]');
        return dataview.getCustomToolbarButtons();
    },
    
    /**
     * Return true if the datasetSelection match the dataview selection
     * 
     * @param selectionString
     *          the datasetSelection string (NONE, SINGLE, MULTIPLE, ALL)
     * @returns {Boolean}
     */
    isSelectionOK : function (selectionString) {
        var selectionOK = false;
        //var dataview = this.up('component[componentType=datasetView]');
        var dataview = this.up('component[sitoolsType=datasetView]');
        var nbRowsSelected = dataview.getNbRowsSelected();
        switch (selectionString) {

        case "NONE":
            selectionOK = true;            
            break;
            
        case "SINGLE":
            if (!Ext.isEmpty(nbRowsSelected) && nbRowsSelected === 1) {
                selectionOK = true;
            }
            break;
            
        case "MULTIPLE":
            if (!Ext.isEmpty(nbRowsSelected) && nbRowsSelected >= 1) {
                selectionOK = true;
            }
            break;
            
        case "ALL":
            if (!Ext.isEmpty(nbRowsSelected) && dataview.isAllSelected()) {
                selectionOK = true;
            }
            break;
        }
        return selectionOK;
    }
    
});
