/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 showHelp, loadUrl*/
/*
 * @include "datasetViewprop.js"
 */
Ext.namespace('sitools.admin.datasetView');

/**
 * A Panel to show the different datasetView available in Sitools2. 
 * @requires sitools.admin.datasetView.DatasetViewPropPanel
 * @class sitools.admin.datasetView.DatasetViewsCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.datasetView.DatasetViewsCrudPanel', { extend : 'Ext.grid.Panel',
	alias : 'widget.s-datasetView',
    border : false,
    height : 300,
    id : ID.BOX.DATASETVIEW,
    sm : Ext.create('Ext.selection.RowModel',{
        singleSelect : true
    }),
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_VIEWS_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            remoteSort : true,
            autoSave : false,
            url : this.url,
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
                name : 'jsObject',
                type : 'string'
            }, {
                name : 'fileUrl',
                type : 'string'
            }, {
                name : 'imageUrl',
                type : 'string'
            }, {
				name : "priority", 
				type : "integer"
            }]
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
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
                width : 350,
                sortable : false
            }, {
                header : i18n.get('label.jsObject'),
                dataIndex : 'jsObject',
                width : 350,
                sortable : false
            }, {
                header : i18n.get('label.priority'),
                dataIndex : 'priority',
                width : 50,
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

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.view = new Ext.grid.GridView({
            forceFit : true
        });
        this.listeners = {
            scope : this, 
            rowDblClick : this._onModify
        };
        sitools.admin.datasetView.DatasetViewsCrudPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.datasetView.DatasetViewsCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    _onCreate : function () {
        var dbp = new sitools.admin.datasetView.DatasetViewPropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        dbp.show(ID.PROP.DATASETVIEW);
    },

    _onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        var dbp = new sitools.admin.datasetView.DatasetViewPropPanel({
            url : this.url + '/' + rec.id,
            action : 'modify',
            store : this.store
        });
        dbp.show(ID.PROP.DATASETVIEW);
    },

    _onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }

        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('datasetViewsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
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
    }

});
