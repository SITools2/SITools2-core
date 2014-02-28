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
Ext.namespace('sitools.component.portal');

Ext.define('sitools.component.portal.rssFeedPortalCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-rssFeedPortal',
    border : false,
    height : 300,
    id : ID.BOX.RSSPORTAL,
    pageSize : 10,
    label : i18n.get("label.selectPortal"),
    forceFit : "true",
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL');
        this.urlRef = loadUrl.get('APP_FEEDS_URL');
        this.dataId = "idPortal";

        var urlRss = this.url + "/" + this.dataId + this.urlRef;
        
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : 'data',
            url : urlRss,
            restful : true,
            autoLoad : true,
            method : 'GET',
            fields : [{
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
            }]
        });

        // colonne avec checkbox pour choisir quelle colonne est la clé primaire
        var visible = Ext.create('Ext.grid.column.CheckColumn', {
            header : i18n.get('headers.visible'),
            dataIndex : 'visible',
            width : 80
        });

        this.columns = [ {
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
            }, {
                xtype : 'checkcolumn',
                header : i18n.get('headers.visible'),
                dataIndex : 'visible',
                width : 80
            }, {
                header : i18n.get('headers.feedSource'),
                dataIndex : 'feedSource',
                width : 100
            }];

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


        sitools.component.portal.rssFeedPortalCrud.superclass.initComponent.call(this);
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

