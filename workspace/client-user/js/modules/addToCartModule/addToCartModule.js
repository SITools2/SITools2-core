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
    frame : true,
    bodyBorder : false,
    initComponent : function () {
        
        (Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', this.user) + "/files";
        this.urlCartFile = loadUrl.get('APP_URL') + this.AppUserStorage + "/" + DEFAULT_ORDER_FOLDER + "/records/" + this.user + "_CartSelections.json";
        
        this.tbar = [{
            text : i18n.get('label.selectAll'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
            tooltip : i18n.get('label.selectAll'),
            enableToggle : true,
            pressed : false,
            scope : this,
            handler : function (button) {
                this.changeSelectButton(button);
                var sm = this.gridPanel.selModel;
                if (button.pressed) {
                    sm.selectAll();
                    sm.lock();
                }
                else {
                    sm.unlock();
                    this.gridPanel.selModel.clearSelections();
                }
                
            }
        }, {
            text : i18n.get('label.downloadSelection'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/download.png',
            tooltip : i18n.get('label.downloadSelection'),
            scope : this,
            handler : this.downloadSelection
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
        }];
        
        this.store = new Ext.data.JsonStore({
            root : 'cartSelections',
            idProperty : 'selectionId',
            autoLoad : true,
            url : this.urlCartFile,
            fields : [{
                name : 'selectionName',
                type : 'string'
            }, {
                name : 'selectionId'
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
                width : 150,
                sortable : true,
                dataIndex : 'datasetName'
            }, {
                header : i18n.get('label.orderDate'),
                width : 150,
                sortable : true,
                dataIndex : 'orderDate'
            }, {
                header : i18n.get('label.nbRecords'),
                width : 150,
                sortable : true,
                dataIndex : 'nbRecords'
            }]
        });
        
        this.layout = 'fit';
        
        this.gridPanel = new Ext.ux.PersistantSelectionGridPanel({
//        this.gridPanel = new Ext.grid.GridPanel({
            region : 'center',
            colModel : this.columnModel,
            store : this.store,
            view : new  Ext.grid.GridView({
                forceFit : true,
                autoFill : true,
                preserveScrollOnRefresh : true,
                listeners : {
                    scope : this,
                    refresh : function (view) {
                        var select = view.grid.selModel.getSelections()[0];
                    }
                }
            }),
            listeners : {
                scope : this,
                rowdblclick : function (grid, ind) {
                    this.containerDetailsSelectionPanel.expand();
                    this.onDetail();
                } 
            }
        });
        
        this.containerDetailsSelectionPanel = new Ext.Panel({
            region : 'south',
            height : 300,
            frame : true,
            bodyBorder : false,
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
        
//        this.bbar = {
//                xtype : 'paging',
//                pageSize : 10,
//                store : this.store,
//                displayInfo : true,
//                displayMsg : i18n.get('paging.display'),
//                emptyMsg : i18n.get('paging.empty')
//            };
        
        this.addListener('onDetail', this.onDetail, this);
        
		sitools.user.modules.addToCartModule.superclass.initComponent.call(this);
    }, 
    
    afterRender : function () {
        sitools.user.modules.addToCartModule.superclass.afterRender.apply(this, arguments);
        userStorage.get(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.getCartSelectionFile);
    },
    
    downloadSelection : function () {
        var selections = this.gridPanel.getSelectionModel().getSelections();
        
        Ext.each(selections , function (selection) {
           selection.data.colModel = undefined;
        });
        
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + "/orders/cart/",
            method : 'PUT',
            jsonData : selections,
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    changeSelectButton : function (button) {
        if (button.pressed) {
            button.setText(i18n.get('label.deselectAll'));
            button.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_remove.png');
        }
        else {
            button.setText(i18n.get('label.selectAll'));
            button.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png');
        }
    },
    
    onDetail : function () {
        var selected = this.gridPanel.getSelectionModel().getSelections()[0];
        
        this.containerDetailsSelectionPanel.removeAll();
        var detailsSelectionPanel = new sitools.user.modules.cartSelectionDetails({
            height : this.containerDetailsSelectionPanel.getInnerHeight(),
            selection : selected,
            columnModel : selected.data.colModel,
            cartSelectionFile : this.cartSelectionFile,
            cartModule : this
            
        });
        
        this.containerDetailsSelectionPanel.add(detailsSelectionPanel);
        this.containerDetailsSelectionPanel.setTitle(String.format(i18n.get('label.selectionDetails'), selected.data.selectionName));
        this.containerDetailsSelectionPanel.doLayout();
    },
    
    onRefresh : function () {
        this.store.reload();
        this.containerDetailsSelectionPanel.collapse(true);
        this.containerDetailsSelectionPanel.setTitle('');
        this.containerDetailsSelectionPanel.removeAll();
    },
    
    onDelete : function () {
        userStorage.get(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.getCartSelectionFile, Ext.emptyFn, this.deleteSelection);
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
                if (cartSelection.selectionId == delSelection.data.selectionId) {
                    collCartSelections.remove(cartSelection);
                }
            });
        });
        this.cartSelectionFile.cartSelections = collCartSelections.items;
        
        userStorage.set(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this.cartSelectionFile, this.onRefresh, this);
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

Ext.reg('sitools.user.modules.addToCartModule', sitools.user.modules.addToCartModule);
