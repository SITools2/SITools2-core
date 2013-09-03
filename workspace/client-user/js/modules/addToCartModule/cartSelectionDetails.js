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
 * @param {File} cartOrderFile : the all Cart Selection file
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
            listeners : {
                scope : this,
                refresh : function (view) {
                    this.getEl().unmask();
                }
            }
        };
       
        var fields = [];
        var columns = [];
        
        Ext.each(this.columnModel, function (item, index, totalItems) {
            if (Ext.isEmpty(item.columnRenderer) ||  ColumnRendererEnum.NO_CLIENT_ACCESS != item.columnRenderer.behavior) {
                var field = {name : item.columnAlias};
                fields.push(field);
                item.dataIndex = item.columnAlias;
                columns.push(new Ext.grid.Column(
                    item
                ));
            }
        });
        
        this.colModel = new Ext.grid.ColumnModel({
            columns : columns
        });
        this.primaryKey = this.getPrimaryKeyFromCm(this.colModel);
        this.sm = null;
        
        this.params = Ext.urlDecode(this.selections);
        
        var colModel = extColModelToSrv(this.columnModel);
        this.params.colModel = Ext.util.JSON.encode(colModel);
        
        this.store = new Ext.data.JsonStore({
            proxy : new Ext.data.HttpProxy({
                method : 'GET',
                url : this.url
            }),
            baseParams : this.params,
            restful : true,
            root : 'data',
            fields : fields
            
        });

        this.tbar = [ '->', {
            text : i18n.get('label.modifySelection'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
            scope : this,
            handler : this.modifySelection
        }];

        this.bbar = {
            xtype : 'paging',
            pageSize : 300,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };
        
        sitools.user.modules.cartSelectionDetails.superclass.initComponent.call(this);
    },
    
    onRender : function () {
        sitools.user.modules.cartSelectionDetails.superclass.onRender.apply(this, arguments);
        this.loadRecordsFile();
    },
    
    afterRender : function () {
        sitools.user.modules.cartSelectionDetails.superclass.afterRender.apply(this, arguments);
        this.getEl().mask(i18n.get('label.loadingArticles'));
    },
    
    
    loadRecordsFile : function () {
        this.store.load({
            params : {
                start : 0,
                limit : 300
            }
        });
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
    
    onRefresh : function () {
//        this.store.removeAll();
//        this.store.loadData(this.records);
//        this.cartModule.fireEvent('onDetail');
        this.cartModule.store.reload();
        this.getEl().unmask();
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
    },
    
    modifySelection : function () {
        var url = this.selection.data.dataUrl;
        sitools.user.clickDatasetIcone(url, 'data', {ranges : this.selection.get('ranges')});
    }
    
});

Ext.reg('sitools.user.modules.cartSelectionDetails', sitools.user.modules.cartSelectionDetails);
