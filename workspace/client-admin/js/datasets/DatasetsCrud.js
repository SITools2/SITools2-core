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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl, ADMIN_PANEL_HEIGHT, ADMIN_PANEL_NB_ELEMENTS  */
 
/*
 *@include "datasetsMultiTablesProp.js" 
 *@include "../../../public/js/env.js"
 */
  
Ext.namespace('sitools.admin.datasets');

/**
 * Displays the list of all the datasets in the Administration
 * @class sitools.admin.datasets.DatasetsCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.datasets.DatasetsCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-datasets',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.DATASETS,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    requires : ['sitools.admin.datasets.DatasetsMultiTablesPanel',
                'sitools.admin.datasets.opensearch.Opensearch',
                'sitools.admin.datasets.dictionaryMapping.DictionaryMapping'],
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'dirty',
                type : 'string'
            }]
        });

        this.columns = [{
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 100,
            sortable : true,
            renderer : function (value, meta, record) {
                meta.style = "font-weight: bold;";
                return value;
            }
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 350
        }, {
            header : i18n.get('label.status'),
            dataIndex : 'status',
            width : 90,
            sortable : true,
            renderer : function (value, meta, record, index, colIndex, store) {
                meta.tdCls += value;
                return value;
            }
        }];

        this.bbar = {
            xtype : 'pagingtoolbar',
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this, 
                xtype : 's-menuButton'
            },
            items : [{
	            text : i18n.get('label.create'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
	            handler : this.onCreate
	        }, {
	            text : i18n.get('label.modify'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
	            handler : this.onEdit
	        }, {
	            text : i18n.get('label.delete'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
	            handler : this.onDelete
	        }, {
	            text : i18n.get('label.opensearch'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_open_search.png',
	            handler : this.onEditOpenSearch
	        }, {
	            text : i18n.get('label.refresh'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_refresh.png',
	            handler : this._onRefresh
	        }, {
	            text : i18n.get('label.active'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
	            handler : this._onActive
	        }, {
	            text : i18n.get('label.disactive'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
	            handler : this._onDisactive
	        }, {
	            text : i18n.get('label.sqlString'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/sql_request.png',
	            handler : this._getSqlString
	        }, {
                text : i18n.get('label.semantic'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/tree_dictionary.png',
                handler : this._onEditSemantic
            }, {
	            text : i18n.get('label.duplicate'),
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/presentation1.png',
	            handler : this.onDuplicate
	        }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            }]
        };

		this.listeners = {
            scope : this, 
            itemdblclick : this.onEdit
        };
        
        sitools.admin.datasets.DatasetsCrud.superclass.initComponent.call(this);
    },
	/**
	 * Called when double click on a dataset, or the edit Button
	 * Will open {@link sitools.admin.datasets.DatasetsMultiTablesPanel} window.
	 * @method
	 * @return {}
	 */
	onEdit : function () {
		var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        if (rec.data.status == "ACTIVE") {
			this.onView();
        }
        else {
			this.onModify();
        }
    }, 
	/**
	 * Called when click on duplicate Button
	 * Will open {@link sitools.admin.datasets.DatasetsMultiTablesPanel} window.
	 * @method
	 * @return {}
	 */
	onDuplicate : function () {
		var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
        }
        
        var up = Ext.create('sitools.admin.datasets.DatasetsMultiTablesPanel', {
            datasetUrlToCopy : this.url + "/" + rec.data.id,
            action : 'duplicate',
            store : this.getStore(), 
            url : this.url
        });
        up.show(ID.BOX.DATASETS);
    }, 
    /**
     * Load the store of the datasets when rendering the grid
     * @method
     */
    afterRender : function () {
        sitools.admin.datasets.DatasetsCrud.superclass.afterRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    /**
     * Called when click on create Button 
     * @method
     */
    onCreate : function () {
        var up = Ext.create('sitools.admin.datasets.DatasetsMultiTablesPanel', {
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.DATASETS);
    },
    /**
     * Called when click on view Button 
     * @method
     */
    onView : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        var up = Ext.create('sitools.admin.datasets.DatasetsMultiTablesPanel', {
            url : this.url + '/' + rec.data.id,
            action : 'view',
            store : this.getStore(), 
            datasetId : rec.data.id
        });
        up.show(ID.BOX.DATASETS);
    },
	/**
     * Called when click on modify Button 
     * @method
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (rec.data.status == i18n.get('status.active')) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        var up = Ext.create('sitools.admin.datasets.DatasetsMultiTablesPanel', {
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.getStore(), 
            datasetId : rec.data.id
        });
        up.show(ID.BOX.DATASETS);
    },
	/**
     * Called when click on delete Button 
     * @method
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('datasetCrud.delete'), rec.data.name),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    /**
     * Called by the onDelete method : delete the record
     * @param Ext.data.Record the selected record. 
     * @method
     */
    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }

            },
            failure : alertFailure
        });
    },
	/**
     * Called when click on openSearch Button 
     * @method
     */
    onEditOpenSearch : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        var up = Ext.create('sitools.admin.datasets.opensearch.opensearch', {
            url : this.url + '/' + rec.data.id,
            action : 'edit',
            store : this.getStore()
        });
        up.show(ID.BOX.DATASETS);
    },
	/**
     * Called when click on refresh Button 
     * Execute a request on the dataset with refresh
     * @method
     */
    _onRefresh : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        if (rec.data.status != i18n.get("status.active")) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/refresh',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                Ext.Msg.alert(i18n.get('label.info'), i18n.get('dataset.refresh.needReactive'));
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_refresh.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    /**
     * Called when click on active Button 
     * @method
     */
    _onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
	/**
     * Called when click on disactive Button 
     * @method
     */
    _onDisactive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
	/**
     * Called when click on sqlString Button 
     * @method
     */
    _getSqlString : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/getSqlString',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (json.success) {
                    var winMsg = Ext.create("Ext.Window", {
                        title : i18n.get('label.sqlString'),
                        height : 300,
                        width : 500,
                        modal : true,
                        resizable : true,
                        autoScroll : true,

                        html : json.message,
                        buttons : [ {
                            text : i18n.get('label.ok'),
                            handler : function () {
                                this.ownerCt.ownerCt.close();
                            }
                        } ]
                    });
                    winMsg.show();
                }
            },
            failure : alertFailure
        });
    }, 
    /**
     * Called when click on semantic Button 
     * @method
     */
    _onEditSemantic : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        } 
        var up = Ext.create("sitools.admin.datasets.dictionaryMapping.DictionaryMapping", {
            url : this.url + '/' + rec.data.id,
            masked : (rec.data.status == "ACTIVE")
        });
        up.show(ID.BOX.DATASETS);
    }

});

