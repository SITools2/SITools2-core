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
Ext.namespace('sitools.admin.dictionary');

Ext.define('sitools.admin.dictionary.DictionaryCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-dictionary',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    id : ID.BOX.DICTIONARY,
    
    requires : ['sitools.admin.dictionary.DictionaryProp'],

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DICTIONARIES_URL');

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
                name : 'template',
                type : 'string'
            } ]
        });

        this.columns = {
            items : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 400,
                sortable : false
            }]
        };

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
                scope : this
            },
            items : [{
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            }]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel',{
            mode : "SINGLE"
        });
        
        sitools.admin.dictionary.DictionaryCrud.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.dictionary.DictionaryCrud.superclass.onRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    onCreate : function () {
        var up = new sitools.admin.dictionary.DictionaryProp({
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.DICTIONARY);
    },

    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }

        var up = new sitools.admin.dictionary.DictionaryProp({
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.getStore()
        });
        up.show(ID.BOX.DICTIONARY);
    },

    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }

        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('dictionaryCrud.delete'), rec.data.name),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    }

});

