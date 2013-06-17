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
 * @include "../id.js"
 * @include "CollectionsPropPanel.js"
 */
Ext.namespace('sitools.admin.collections');

//sitools.component.forms.formsCrudPanel = Ext.extend(Ext.grid.GridPanel, {
/**
 * A GridPanel to show all collections.
 * @class sitools.admin.collections.CollectionsCrudPanel
 * @extends Ext.grid.GridPanel
 * @requires sitools.admin.collections.CollectionsPropPanel
 */
sitools.admin.collections.CollectionsCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.COLLECTIONS,
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        this.urlCollections = loadUrl.get('APP_URL') + loadUrl.get('APP_COLLECTIONS_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            url : this.urlCollections, 
            remoteSort : true,
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
            } ], 
            autoLoad : true
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
                width : 400,
                sortable : false
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
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '->', {
				xtype : 's-filter', 
				emptyText: i18n.get('label.search'),
				store : this.store, 
				pageSize : this.pageSize
            }]
        };

        this.view = new Ext.grid.GridView({
            forceFit : true
        });
        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };
        sitools.admin.collections.CollectionsCrudPanel.superclass.initComponent.call(this);

    },
    /**
     * Action on Create Button
     */
    onCreate : function () {
        var up = new sitools.admin.collections.CollectionsPropPanel({
            urlCollections : this.urlCollections,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.FORMS);
    },

    /**
     * Action on Modify Button
     */
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.collections.CollectionsPropPanel({
            urlCollections : this.urlCollections + "/" + rec.data.id,
            action : 'modify',
            store : this.getStore()
        });
        up.show();
    },

    /**
     * Action on Delete Button
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('collectionsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    /**
     * send the Delete request
     */
    doDelete : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.urlCollections + "/" + rec.id,
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

Ext.reg('s-collections', sitools.admin.collections.CollectionsCrudPanel);
