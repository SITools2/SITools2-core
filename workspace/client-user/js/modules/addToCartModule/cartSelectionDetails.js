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
 * @class sitools.user.modules.cartSelectionDetails
 * @param {Ext.data.Record} selection : the current selection
 * @param {columnModel} columnModel : the column model of the dataset
 * @param {Array} records : the records of the current selection
 * @param {File} cartSelectionFile : the all Cart Selection file
 * @param {sitools.user.modules.addToCartModule} cartModule : the cart Module
 * @extends grid.GridPanel
 */
sitools.user.modules.cartSelectionDetails = Ext.extend(Ext.grid.GridPanel, {
    initComponent : function () {

        this.selectionId = this.selection.data.selectionId;
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";
        this.recordsFileUrl = loadUrl.get('APP_URL') + this.AppUserStorage + "/"
            + DEFAULT_ORDER_FOLDER + "/records/" + userLogin + "_" + this.selectionId + "_records.json";
        
        this.viewConfig = {
                autoFill : true
        };
        
        this.contextMenu = new Ext.menu.Menu({
            items : [{
                text : i18n.get('label.deleteRecordSelection'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/delete.png',
                tooltip : i18n.get('label.deleteResource'),
                scope : this,
                handler : function () {
                    Ext.Msg.show({
                        title : i18n.get('label.delete'),
                        buttons : Ext.Msg.YESNO,
                        msg : i18n.get('label.deleteRecordSelectionConfirm'),
                        scope : this,
                        fn : function (btn, text) {
                            if (btn == 'yes') {
                                this.onDelete();
                            }
                        }
                    });
                }
            }]
        });
        Ext.QuickTips.init();
        
        this.listeners = {
            scope : this,
            rowcontextmenu : function (grid, rowIndex, e) {
                e.stopEvent();
                if (!this.getSelectionModel().hasSelection()){
                    this.getSelectionModel().selectRow(rowIndex);
                }
                this.contextMenu.showAt(e.getXY());
            }
        };
        
        var fields = [];
        Ext.each(this.columnModel, function (col) {
            var field = {name : col.dataIndex};
            fields.push(field);
        }, this);
        
        this.store = new Ext.data.JsonStore({
            root : 'records',
            fields : fields
        });
        
        this.colModel = getColumnModel(this.columnModel);
        this.primaryKey = this.getPrimaryKeyFromCm(this.colModel);
        
        sitools.user.modules.cartSelectionDetails.superclass.initComponent.call(this);
    },
    
    onRender : function () {
        sitools.user.modules.cartSelectionDetails.superclass.onRender.apply(this, arguments);
        this.loadRecordsFile();
    },
    
    loadRecordsFile : function () {
        userStorage.get(userLogin + "_" + this.selection.data.selectionId + "_records.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.loadRecordsInStore);
    },
    
    loadRecordsInStore : function (response) {
        if (Ext.isEmpty(response.responseText)) {
            return;
        }
        try {
            var json = Ext.decode(response.responseText);
            this.records = json;
            this.store.loadData(this.records);
        } catch (err) {
            return;
        }
    },
    
    onDelete : function () {
        if (!this.cartSelectionFile) {
            return;
        }
        var recordsToRemove = this.getSelectionModel().getSelections();
        
        if (recordsToRemove.length == 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        Ext.each(recordsToRemove, function (record) {
           this.store.remove(record); 
        }, this);
        
        var recordsOrder = {};
        recordsOrder.records = [];
        
        this.store.each(function (rec) {
            recordsOrder.records.push(rec.data);
        }, this);
        recordsOrder.selectionId = this.selectionId;
        
        var recSelection = this.cartModule.store.getById(this.selection.data.selectionId);
        var indexSelection = this.cartModule.store.indexOf(recSelection);
        
        var selectionFromFile = this.cartSelectionFile.cartSelections[indexSelection];
        selectionFromFile.nbRecords = this.store.getCount();
        
        this.cartSelectionFile.cartSelections[indexSelection] = selectionFromFile;
        
        userStorage.set(userLogin + "_" + this.selection.data.selectionId + "_records.json", "/" + DEFAULT_ORDER_FOLDER + "/records", recordsOrder, this.updateGlobalSelection, this);
    },
    
    updateGlobalSelection : function () {
        userStorage.set(userLogin + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this.cartSelectionFile, this.onRefresh, this);
    },
    
    onRefresh : function () {
//        this.store.removeAll();
//        this.store.loadData(this.records);
//        this.cartModule.fireEvent('onDetail');
        this.cartModule.store.reload();
    },
    
    getPrimaryKeyFromCm : function (colModel) {
        var primaryKey = null;
        Ext.each(colModel.config, function (col) {
            if (col.primaryKey) {
                primaryKey = col.dataIndex;
                return false;
            }
        });
        return primaryKey;
    }
    
});

Ext.reg('sitools.user.modules.cartSelectionDetails', sitools.user.modules.cartSelectionDetails);
