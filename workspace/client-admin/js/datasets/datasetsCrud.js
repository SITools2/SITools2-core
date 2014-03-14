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
 * @class sitools.admin.datasets.datasetsCrudPanel
 * @extends Ext.grid.GridPanel
 */
//sitools.component.datasets.datasetsCrudPanel = Ext.extend(Ext.grid.GridPanel, {
sitools.admin.datasets.datasetsCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.DATASETS,
    sm : new Ext.grid.RowSelectionModel(),
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    viewConfig : {
        forceFit : true,
        autoFill : true, 
		getRowClass : function (row, index) { 
			var cls = ''; 
			var data = row.data; 
			if (data.dirty == "true") {
				cls = "red-row";
			}
			return cls; 
		} 
		
	},

    // loadMask: true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            remoteSort : true,
            url : this.url,
            // sortField: 'name',
            idProperty : 'id',
            fields : [ {
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
            } ]
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : false
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 350
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 90,
                sortable : true
            } ]
        });

        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        var buttons = [];
        
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this, 
                xtype : 's-menuButton'
            },
            items : [ {
	            text : i18n.get('label.create'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
	            handler : this.onCreate
	        }, {
	            text : i18n.get('label.modify'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
	            handler : this.onEdit
	        }, {
	            text : i18n.get('label.delete'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
	            handler : this.onDelete
	        }, {
	            text : i18n.get('label.opensearch'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_open_search.png',
	            handler : this.onEditOpenSearch
	        }, {
	            text : i18n.get('label.refresh'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_refresh.png',
	            handler : this._onRefresh
	        }, {
	            text : i18n.get('label.active'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png',
	            handler : this._onActive
	        }, {
	            text : i18n.get('label.disactive'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png',
	            handler : this._onDisactive
	        }, {
	            text : i18n.get('label.sqlString'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/sql_request.png',
	            handler : this._getSqlString
	        }, {
                text : i18n.get('label.semantic'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_dictionary.png',
                handler : this._onEditSemantic
            }, {
	            text : i18n.get('label.duplicate'),
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/presentation1.png',
	            handler : this.onDuplicate
	        }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

		this.listeners = {
            scope : this, 
            rowDblClick : this.onEdit
        };
        
        sitools.admin.datasets.datasetsCrudPanel.superclass.initComponent.call(this);
    },
	/**
	 * Called when double click on a dataset, or the edit Button
	 * Will open {@link sitools.component.datasets.datasetsMultiTablesPanel} window.
	 * @method
	 * @return {}
	 */
	onEdit : function () {
		var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
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
	 * Will open {@link sitools.component.datasets.datasetsMultiTablesPanel} window.
	 * @method
	 * @return {}
	 */
	onDuplicate : function () {
		var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.component.datasets.datasetsMultiTablesPanel({
            datasetUrlToCopy : this.url + "/" + rec.id,
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
    onRender : function () {
        sitools.admin.usergroups.GroupCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Called when click on create Button 
     * @method
     */
    onCreate : function () {
        var up = new sitools.component.datasets.datasetsMultiTablesPanel({
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.component.datasets.datasetsMultiTablesPanel({
            url : this.url + '/' + rec.id,
            action : 'view',
            store : this.getStore(), 
            datasetId : rec.id
        });
        up.show(ID.BOX.DATASETS);
    },
	/**
     * Called when click on modify Button 
     * @method
     */
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (rec.data.status == i18n.get('status.active')) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.component.datasets.datasetsMultiTablesPanel({
            url : this.url + '/' + rec.id,
            action : 'modify',
            store : this.getStore(), 
            datasetId : rec.id
        });
        up.show(ID.BOX.DATASETS);
    },
	/**
     * Called when click on delete Button 
     * @method
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('datasetCrud.delete'),
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
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.datasets.datasetsOpenSearch({
            url : this.url + '/' + rec.id,
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        if (rec.data.status != i18n.get("status.active")) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/refresh',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                Ext.Msg.alert(i18n.get('label.info'), i18n.get('dataset.refresh.needReactive'));
                if (showResponse(ret)) {
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/getSqlString',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (json.success) {
                    var winMsg = new Ext.Window({
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
                    // Ext.Msg.alert (ret.responseText);

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
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        } 
        var up = new sitools.admin.datasets.DicoMapping({
            url : this.url + '/' + rec.id,
            masked : (rec.data.status == "ACTIVE")
        });
        up.show(ID.BOX.DATASETS);
    }

});

Ext.reg('s-datasets', sitools.admin.datasets.datasetsCrudPanel);
