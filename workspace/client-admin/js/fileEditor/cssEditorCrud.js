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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl
 */
 
/*
 *@include "datasetsMultiTablesProp.js" 
 *@include "../../../public/js/env.js"
 */

Ext.namespace('sitools.admin.fileEditor');

Ext.define('sitools.admin.fileEditor.cssEditorCrud', { extend : 'Ext.grid.Panel',
    alias : 'widget.s-cssEditor',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.FILEEDITORCSS,
    selModel : Ext.create('Ext.selection.RowModel',{
        mode : 'SINGLE'
    }),
    pageSize : 15,
    forceFit : true,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.fileEditor.fileEditorProp'],

    initComponent : function () {
        
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ADMINISTRATOR_URL');

        this.store = Ext.create("Ext.data.JsonStore", {
            pageSize : this.pageSize,
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.url + '/css',
                reader : {
                    type : 'json',
                    root : 'items',
                    idProperty : 'name'
                }
            },
            remoteSort : false,
            fields : [ {
                name : 'name',
                type : 'string',
                mapping : 'name'
            }, {
                name : 'url',
                type : 'string',
                mapping : 'url'
            }, {
                name : 'lastModif',
                type : 'date',
                mapping : 'lastmod',
                dateFormat : 'timestamp'
            }, {
                name : 'size',
                type : 'int',
                mapping : 'size'
            } ]
        });
        
        this.columns = {
            defaults : {
                sortable : true
            },
            items : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 500
            },
            {
                header : i18n.get('label.size'),
                dataIndex : 'size',
                width : 100
            },
            {
                header : i18n.get('label.lastModif'),
                dataIndex : 'lastModif',
                width : 360
            }]
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            } ]
        };

        this.listeners = {
            scope : this,
            itemdblclick : this.onModify
        };
        
        sitools.admin.fileEditor.cssEditorCrud.superclass.initComponent.call(this);
    },

    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }

        var cssProp = Ext.create("sitools.admin.fileEditor.fileEditorProp", {
            url : this.url + '/css/' + rec.data.id,
            fileName : rec.data.id,
            sourceEdit : true,
            modalType : true,
            mediaType : 'text/css',
            editable : false
        });
        cssProp.show();
    }
    
});

