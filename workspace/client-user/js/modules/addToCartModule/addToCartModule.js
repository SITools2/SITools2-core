/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin*/

Ext.namespace('sitools.user.modules');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.addToCartModule
 * @extends Ext.Panel
 */
sitools.user.modules.addToCartModule = Ext.extend(Ext.Panel, {
    initComponent : function () {
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";
        this.urlCartFile = loadUrl.get('APP_URL') + this.AppUserStorage + "/" + DEFAULT_ORDER_FOLDER + "/records/" + userLogin + "_CartSelections.json";
        
        this.tbar = [ {
            text : i18n.get('label.detail'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
            tooltip : i18n.get('label.selectionDetails'),
            scope : this,
            handler : this.onDetail
        }, '->', {
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
            tooltip : i18n.get('label.refreshResource'),
            scope : this,
            handler : this.onRefresh
        }, {
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/delete.png',
            tooltip : i18n.get('label.deleteResource'),
            scope : this,
            handler : function () {
                Ext.Msg.show({
                    title : i18n.get('label.delete'),
                    buttons : Ext.Msg.YESNO,
                    msg : i18n.get('label.deleteCartSelection'),
                    scope : this,
                    fn : function (btn, text) {
                        if (btn == 'yes') {
                            this.onDelete();
                        }
                    }
                });
            }
        } ];
        
        this.store = new Ext.data.JsonStore({
            root : 'cartSelections',
            autoLoad : true,
            url : this.urlCartFile,
            fields : [{
                name : 'selectionName',
                type : 'string'
            }, {
                name : 'datasetName',
                type : 'string'
            }, {
                name : 'nbRecords',
                type : 'int'
            }, {
                name : 'orderDate',
                type: 'string'
            }, {
                name : 'colModel'
            }, {
                name : 'datasetId'
            }, {
                name : 'records'
            }]
                
        });
        
        this.columnModel = new Ext.grid.ColumnModel({
            columns : [{
                header : i18n.get('label.selectionName'),
                width : 150,
                sortable : true,
                dataIndex : 'selectionName'
            }, {
                header : i18n.get('label.datasetName'),
                width : 200,
                sortable : true,
                dataIndex : 'datasetName'
            }, {
                header : i18n.get('label.orderDate'),
                width : 150,
                sortable : true,
                dataIndex : 'orderDate'
            }, {
                header : i18n.get('label.nbRecords'),
                width : 100,
                sortable : true,
                dataIndex : 'nbRecords'
            }]
        });
        
        this.layout = 'fit';
        
        this.gridPanel = new Ext.grid.GridPanel({
            region : 'center',
            colModel : this.columnModel,
            store : this.store,
            viewConfig : {
              forceFit : true,
              autoFill : true
            },
            listeners : {
                scope : this,
                rowdblclick : function (grid, ind) {
                    this.containerDetailsSelectionPanel.expand();
                    this.onDetail();
                } 
            }
            
        });
        
        this.containerDetailsSelectionPanel = new Ext.Panel({
            title : i18n.get('label.selectionDetails'),
            region : 'south',
            height : 300,
            collapsible : true,
            collapsed : true,
            forceLayout : true
        });
        
        this.hboxPanel = new Ext.Panel({
            layout : 'border',
            region : 'center',
            items : [this.gridPanel, this.containerDetailsSelectionPanel]
        });
        
        this.items = [ this.hboxPanel ];
        
        this.bbar = {
                xtype : 'paging',
                pageSize : 10,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
		sitools.user.modules.addToCartModule.superclass.initComponent.call(this);
    }, 
    
    onDetail : function () {
        var selected = this.gridPanel.getSelectionModel().getSelections()[0];
        
        this.containerDetailsSelectionPanel.removeAll();
        var detailsSelectionPanel = new sitools.user.modules.cartSelectionDetails({
            height : this.containerDetailsSelectionPanel.getInnerHeight(),
            columnModel : selected.data.colModel,
            records : selected.data.records
        });
        
        this.containerDetailsSelectionPanel.add(detailsSelectionPanel);
        this.containerDetailsSelectionPanel.setTitle(String.format(i18n.get('label.selectionDetails'),selected.data.selectionName));
        
        this.containerDetailsSelectionPanel.doLayout();
        
    },
    
    onRefresh : function () {
        this.store.reload();
    },
    
    onDelete : function () {
        userStorage.get(userLogin + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this,
                this.getCartSelectionFile, Ext.emptyFn, this.deleteSelection);
    },
    
    getCartSelectionFile : function (response) {
        if (Ext.isEmpty(response.responseText)) {
            return;
        }
        try {
            var json = Ext.decode(response.responseText);
            this.cartSelectionFile = json;
        } catch (err) {
            return;
        }
    },
    
    deleteSelection : function () {
        if (!this.cartSelectionFile) {
            return;
        }
        var selections = this.gridPanel.getSelectionModel().getSelections();
        
        if (selections.length == 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        var collSelections = new Ext.util.MixedCollection();
        collSelections.addAll(selections);
        
        var collCartSelections = new Ext.util.MixedCollection();
        collCartSelections.addAll(this.cartSelectionFile.cartSelections);
        
        collCartSelections.each(function (cartSelection, indCart) {
            collSelections.each(function (delSelection, indDel) {
                if (cartSelection.orderDate == delSelection.data.orderDate) {
                    collCartSelections.remove(cartSelection);
                }
            });
        });
        this.cartSelectionFile.cartSelections = collCartSelections.items;
        
        userStorage.set(userLogin + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this.cartSelectionFile, this.onRefresh, this);
    },
    
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

Ext.reg('sitools.user.modules.addToCartModule',sitools.user.modules.addToCartModule);
