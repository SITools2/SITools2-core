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

Ext.namespace('sitools.component.fileEditor');

sitools.component.fileEditor.ftlEditorCrud = Ext.extend(Ext.grid.GridPanel, {
    
    border : false,
    height : 300,
    id : ID.BOX.FILEEDITORFTL,
    sm : new Ext.grid.RowSelectionModel({
        singleSelect : true
    }),
    pageSize : 15,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ADMINISTRATOR_URL');
        this.iconCls = loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png';

        this.store = new Ext.data.JsonStore({
            root : 'items',
            restful : true,
            remoteSort : false,
            url : this.url + '/ftl',
            idProperty : 'name',
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
        
        this.cm = new Ext.grid.ColumnModel({
            defaults : {
                sortable : true
            },
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 500,
                sortable : true
            },
            {
                header : i18n.get('label.size'),
                dataIndex : 'size',
                width : 100,
                sortable : true
            },
            {
                header : i18n.get('label.lastModif'),
                dataIndex : 'lastModif',
                width : 360,
                sortable : true
            }]
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [{
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }]
        };
       
        this.listeners = {
                scope : this, 
                rowDblClick : this.onModify
            };
        
        sitools.component.fileEditor.ftlEditorCrud.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.component.fileEditor.ftlEditorCrud.superclass.onRender.apply(this, arguments);
        this.store.load();
    },
    
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        var l = rec.data.name.length;
        var from = l - 3;
        var ftlProp;
        if (rec.id.substring(from, l) == "txt") {
            ftlProp = new sitools.component.fileEditor.fileEditorProp({
                url : this.url + '/ftl/' + rec.id,
                fileName : rec.id,
                sourceEdit : false,
                modalType : false,
                mediaType : 'text/plain',
                editable : true
            });
            ftlProp.show();
        } else {
            ftlProp = new sitools.component.fileEditor.fileEditorProp({
                url : this.url + '/ftl/' + rec.id,
                fileName : rec.id,
                sourceEdit : true,
                modalType : true,
                mediaType : 'text/freemarker',
                editable : false
            });
            ftlProp.show();
        }
    }
    
});

Ext.reg('s-ftlEditor', sitools.component.fileEditor.ftlEditorCrud);