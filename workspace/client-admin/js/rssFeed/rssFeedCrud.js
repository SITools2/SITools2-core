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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.rssFeed');

/**
 * Generic component to create RssFeed Can be used for DataSets, Projects or
 * archive Parameters :
 * 
 * @cfg : url , the url to get the list of DataSets, Projects or archive
 * @cfg : label , the label to display ( Select DataSet ... )
 * @cfg : urlRef , the relative url of the RSS API
 * @class sitools.admin.rssFeed.rssFeedCrud
 * @extends Ext.grid.GridPanel
 */
//sitools.component.rssFeedCrud = Ext.extend(Ext.grid.GridPanel, {
sitools.admin.rssFeed.rssFeedCrud = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    pageSize : 10,
    modify : false,
    forceFit : "true",

    initComponent : function () {
        this.httpProxyRss = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });

        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : 'data',
            proxy : this.httpProxyRss,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'title',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'link',
                type : 'string'
            }, {
                name : 'feedType',
                type : 'string'
            }, {
                name : 'feedSource',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            } ]
        });

        var storeCombo = new Ext.data.JsonStore({
            fields : [ 'id', 'name' ],
            url : this.url,
            root : "data",
            autoLoad : true
        });

        this.combobox = new Ext.form.ComboBox({
            store : storeCombo,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : this.label,
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.dataId = rec.data.id;
                    this.getTopToolbar().findById("s-filter").enable();
                    this.loadRss();

                }

            }
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100
            }, {
                header : i18n.get('label.titleRss'),
                dataIndex : 'title',
                width : 150
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200
            }, {
                header : i18n.get('label.linkTitle'),
                dataIndex : 'link',
                width : 200
            }, {
                header : i18n.get('headers.type'),
                dataIndex : 'feedType',
                width : 50
            }, {
                header : i18n.get('headers.feedSource'),
                dataIndex : 'feedSource',
                width : 100
            } ]
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.combobox, {
                text : i18n.get('label.add'),
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
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize,
                disabled : true,
                id : "s-filter"
            } ]
        };

        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };
        sitools.admin.rssFeed.rssFeedCrud.superclass.initComponent.call(this);

    },

    /**
     * Load the rss feeds of the selected dataset in the comboBox
     */
    loadRss : function () {

        var urlRss = this.url + "/" + this.dataId + this.urlRef;
        this.httpProxyRss.setUrl(urlRss, true);
        this.getStore().load({
            scope : this,
            callback : function () {
                this.getView().refresh();
            }
        });
    },

    /**
     * Open a {sitools.admin.rssFeed.rssFeedProps} rss property window
     *  to create a new rss feed for the selected dataset
     */
    onCreate : function () {
        if (Ext.isEmpty(this.combobox.getValue())) {
            return;
        }
        var up = new sitools.admin.rssFeed.rssFeedProps({
            action : 'create',
            store : this.store,
            id : this.dataId,
            url : this.url,
            urlRef : this.urlRef
        });
        up.show();
    },

    /**
     * Open a {sitools.admin.rssFeed.rssFeedProps} rss property window
     *  to modify an existing rss feed
     */
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        var up = new sitools.admin.rssFeed.rssFeedProps({
            action : 'modify',
            store : this.store,
            id : this.dataId,
            url : this.url,
            urlRef : this.urlRef,
            idFeed : rec.data.id
        });
        up.show();
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('feedCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    
    /**
     * done the delete of the passed record
     * @param rec the record to delete
     */
    doDelete : function (rec) {

        Ext.Ajax.request({
            url : this.url + "/" + this.dataId + this.urlRef + "/" + rec.data.id,
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

Ext.reg('s-rssFeedCrud', sitools.admin.rssFeed.rssFeedCrud);
