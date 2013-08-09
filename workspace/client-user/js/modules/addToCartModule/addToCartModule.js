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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/

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
        
        // Default broadcast mode
        this.broadcastMode = "uspr";
        
        this.selectAllButton = new Ext.Button({
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
        });
        
        this.tbar = [ this.selectAllButton, {
            xtype : 'splitbutton',
            text : i18n.get('label.downloadOrder'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/download.png',
            tooltip : i18n.get('label.downloadOrder'),
            scope : this,
            handler : this.downloadSelection,
            menu : new Ext.menu.Menu({
                items : [ '<b class="menu-title"><i>' + i18n.get('label.broadcastMode') + '</i></b>', '-', {
                    text : i18n.get('label.userStoragePrivate'),
                    broadcastMode : 'uspr',
                    group : 'broadcast',
                    checked : true,
                    scope : this,
                    handler : this.setBroadcastMode
                }, {
                    text : i18n.get('label.userStoragePublic'),
                    broadcastMode : 'usp',
                    group : 'broadcast',
                    checked : false,
                    scope : this,
                    handler : this.setBroadcastMode
                }, {
                    text : i18n.get('label.streaming'),
                    broadcastMode : 'stream',
                    group : 'broadcast',
                    checked : false,
                    scope : this,
                    handler : this.setBroadcastMode
                }, {
                    text : i18n.get('label.ftp'),
                    broadcastMode : 'ftp',
                    group : 'broadcast',
                    checked : false,
                    scope : this,
                    handler : this.setBroadcastMode
                } ]
            })
        }, '->', {
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
            tooltip : i18n.get('label.refreshOrder'),
            cls : 'button-transition',
            scope : this,
            handler : this.onRefresh
        }, {
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
            tooltip : i18n.get('label.deleteOrder'),
            cls : 'button-transition',
            scope : this,
            handler : function () {
                Ext.Msg.show({
                    title : i18n.get('label.delete'),
                    buttons : Ext.Msg.YESNO,
                    msg : i18n.get('label.deleteCartOrder'),
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
                header : i18n.get('label.orderName'),
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
                    this.containerArticlesDetailsPanel.expand();
                    this.viewArticlesDetails();
                } 
            }
        });
        
        this.containerArticlesDetailsPanel = new Ext.Panel({
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
            items : [this.gridPanel, this.containerArticlesDetailsPanel]
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
        
		sitools.user.modules.addToCartModule.superclass.initComponent.call(this);
    }, 
    
    afterRender : function () {
        sitools.user.modules.addToCartModule.superclass.afterRender.apply(this, arguments);
        this.loadOrderFile();
    },
    
    loadOrderFile : function () {
        userStorage.get(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.setCartOrdersFile);
    },
    
    /**
     * Set the file order for the current user from responseText
     * @param response the responseText to use as cartOrder
     */
    setCartOrdersFile : function (response) {
        if (Ext.isEmpty(response.responseText)) {
            return;
        }
        try {
            var json = Ext.decode(response.responseText);
            this.cartOrderFile = json;
        } catch (err) {
            return;
        }
    },
    
    /**
     * Download orders (metadata + records)
     * @returns
     */
    downloadSelection : function () {
        
        var selections = this.gridPanel.getSelectionModel().getSelections();
        if (selections.length == 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        var putObject = {};
        putObject.selections = [];
        
        Ext.each(selections, function (selection) {
            var tmpSelection = {};
            delete selection.data.colModel;
            delete selection.data.records;
            Ext.apply(tmpSelection, selection.data);
            putObject.selections.push(tmpSelection);
        });
        
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + "/orders/cart/",
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (ret) {
                var notify = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get(ret.responseText),
                    autoDestroy : true,
                    hideDelay : 1000
                });
                notify.show(document);
//                var Json = Ext.decode(ret.responseText);
//                if (showResponse(ret)) {
//                    this.store.reload();
//                }
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
            this.resetSelectButton(button);
        }
    },
    
    resetSelectButton : function (button) {
        button.setText(i18n.get('label.selectAll'));
        button.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png');
    },
    
    viewArticlesDetails : function () {
        var selected = this.gridPanel.getSelectionModel().getSelections()[0];
        
        this.containerArticlesDetailsPanel.removeAll();
        var detailsSelectionPanel = new sitools.user.modules.cartSelectionDetails({
            height : this.containerArticlesDetailsPanel.getInnerHeight(),
            selection : selected,
            columnModel : selected.data.colModel,
            cartOrderFile : this.cartOrderFile,
            cartModule : this
        });
        
        this.containerArticlesDetailsPanel.add(detailsSelectionPanel);
        this.containerArticlesDetailsPanel.setTitle(String.format(i18n.get('label.orderDetails'), selected.data.selectionName));
        this.containerArticlesDetailsPanel.doLayout();
    },
    
    onRefresh : function () {
        this.store.reload();
        this.resetSelectButton(this.selectAllButton);
        this.containerArticlesDetailsPanel.collapse(true);
        this.containerArticlesDetailsPanel.setTitle('');
        this.containerArticlesDetailsPanel.removeAll();
    },
    
    onDelete : function () {
        userStorage.get(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.setCartOrdersFile, Ext.emptyFn, this.deleteOrder);
    },
    
    /**
     * Delete articles file(s) for the selected order(s)
     * @param listRecordsFilesToRemove the list all order selectionID to remove
     */
    deleteArticlesOrder : function (listRecordsFilesToRemove) {
        var fileToRemove = listRecordsFilesToRemove[0];
        
        if (Ext.isEmpty(fileToRemove)){
            return;
        }
        
        // gettings url file to remove
        var urlRecords = loadUrl.get('APP_URL') + this.AppUserStorage + "/" + 
            DEFAULT_ORDER_FOLDER + "/records/" + this.user + "_" + fileToRemove + "_" + "records.json";
        
        Ext.Ajax.request({
            url : urlRecords,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                listRecordsFilesToRemove.shift();
                
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.fileDeleted'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
            },
            callback : function () {
                this.deleteArticlesOrder(listRecordsFilesToRemove);
            },
            failure : function (response, opts) {
                Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
            }
        });
    },
    
    /**
     * Delete selected orders
     */
    deleteOrder : function () {
        if (!this.cartOrderFile) {
            return;
        }
        var selections = this.gridPanel.getSelectionModel().getSelections();
        
        if (selections.length == 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        var collSelections = new Ext.util.MixedCollection();
        collSelections.addAll(selections);
        
        var collCartSelections = new Ext.util.MixedCollection();
        collCartSelections.addAll(this.cartOrderFile.cartSelections);
        
        var recordsToRemove = [];
        collCartSelections.each(function (cartSelection, indCart) {
            collSelections.each(function (delSelection, indDel) {
                if (cartSelection.selectionId == delSelection.data.selectionId) {
                    recordsToRemove.push(cartSelection.selectionId);
                    collCartSelections.remove(cartSelection);
                }
            });
        });
        this.cartOrderFile.cartSelections = collCartSelections.items;
        this.deleteArticlesOrder(recordsToRemove);
        
        userStorage.set(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this.cartOrderFile, this.onRefresh, this);
    },
    
    /**
     * Set the download mode
     * @param btn the broadcast mode chosen
     */
    setBroadcastMode : function (btn) {
        this.broadcastMode = btn.broadcastMode;
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