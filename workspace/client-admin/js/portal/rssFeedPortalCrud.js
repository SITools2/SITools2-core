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
Ext.namespace('sitools.component.portal');

sitools.component.portal.rssFeedPortalCrud = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.RSSPORTAL,
    pageSize : 10,
    label : i18n.get("label.selectPortal"),
    forceFit : "true",

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL');
        this.urlRef = loadUrl.get('APP_FEEDS_URL');
        
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
                name : 'visible',
                type : 'boolean'
            }, {
                name : 'feedSource',
                type : 'string'
            }, {
                name : 'externalUrl',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            } ]
        });

        // var storeCombo = new Ext.data.JsonStore({
        // fields : [ 'id', 'name' ],
        // url : this.url,
        // root : "data",
        // autoLoad : true
        // });
        //
        // this.combobox = new Ext.form.ComboBox({
        // store : storeCombo,
        // displayField : 'name',
        // valueField : 'id',
        // typeAhead : true,
        // mode : 'local',
        // forceSelection : true,
        // triggerAction : 'all',
        // emptyText : this.label,
        // selectOnFocus : true,
        // listeners : {
        // scope : this,
        // select : function(combo, rec, index) {
        // this.dataId = rec.data.id;
        //
        // this.loadRss();
        //
        // }
        //
        // }
        // });

        // colonne avec checkbox pour choisir quelle colonne est la clé primaire
        var visible = new Ext.grid.CheckColumn({
            header : i18n.get('headers.visible'),
            dataIndex : 'visible',
            width : 80
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            },
            columns : [ {
                header : i18n.get('label.titleRss'),
                dataIndex : 'title',
                width : 150
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 150
            }, {
                header : i18n.get('label.url'),
                dataIndex : 'externalUrl',
                width : 150
            }, {
                header : i18n.get('headers.type'),
                dataIndex : 'feedType',
                width : 50
            }, visible, {
                header : i18n.get('headers.feedSource'),
                dataIndex : 'feedSource',
                width : 100
            } ]
        });

        // définition des plugins nécessaires (colonnes avec checkbox )
        this.plugins = [ visible ];

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.save'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
                handler : this.onSave,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.dataId = "idPortal";

        this.loadRss();

        sitools.component.portal.rssFeedPortalCrud.superclass.initComponent.call(this);

    },
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

    onSave : function () {

        var json = {};
        json.feeds = [];
        var i;
        for (i = 0; i < this.store.getCount(); i++) {
            var rec = this.store.getAt(i).data;
            json.feeds.push(rec);
        }
        var url = this.url + "/" + this.dataId + this.urlRef;

        Ext.Ajax.request({
            url : url,
            method : "PUT",
            scope : this,
            jsonData : json,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.feedsSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                this.store.load();
            }

        });

    }

});

Ext.reg('s-rssFeedPortal', sitools.component.portal.rssFeedPortalCrud);
