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
 * @include "absoluteLayoutProp.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A window to present all form Components type
 * @cfg {Ext.data.JsonStore} storeConcepts the store with concepts
 * @class sitools.admin.forms.componentsListPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.forms.componentsListPanel', { extend : 'Ext.grid.Panel',
    width : 220,
    id: 'gridsource',
    draggable : true,

    initComponent : function () {
        this.title = i18n.get('label.chooseComponent');
        var storeComponents = new Ext.data.JsonStore({
            root : 'data',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_FORMCOMPONENTS_URL'),
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'componentDefaultHeight',
                type : 'string'
            }, {
                name : 'componentDefaultWidth',
                type : 'string'
            }, {
                name : 'jsAdminObject',
                type : 'string'
            }, {
                name : 'jsUserObject',
                type : 'string'
            }, {
                name : 'imageUrl',
                type : 'string'
            }, {
                name : 'dimensionId',
                type : 'string'
            }, {
                name : 'unit',
                type : 'string'
            }, {
                name : 'extraParams'
            } ],
            autoLoad : true,
            sortInfo: {
			    field: 'type',
			    direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
			}
           
        });
        var smComponents = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        var cmComponents = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.type'),
                dataIndex : 'type', 
                width : 150
            }, {
                header : i18n.get('headers.image'),
                dataIndex : 'imageUrl',
                renderer : function (value) {
                    return "<a href='#' onClick='sitools.admin.forms.componentsListPanel.showPreview(\"" + value + "\"); return false;'>" + i18n.get('label.preview') + "</a>";
                }, 
                width : 70
            } ],
            defaults : {
                sortable : true,
                width : 100
            }
        });
//        this.gridComponents = new Ext.grid.GridPanel({
//            layout : "fit",
//            title : i18n.get('title.componentList'),
//            autoScroll : true, 
//            store : storeComponents,
//            cm : cmComponents,
//            sm : smComponents,
//            viewConfig : {
//                forceFit : true
//            }, 
//            buttons : [ {
//                text : i18n.get('label.ok'),
//                scope : this,
//                handler : this.onValidate
//            }, {
//                text : i18n.get('label.cancel'),
//                scope : this,
//                handler : function () {
//                    this.close();
//                }
//            } ]
//        });
////        this.items = [ this.gridComponents ];
        Ext.apply(this, {
            layout : "fit",
            title : i18n.get('title.componentList'),
            autoScroll : true, 
            store : storeComponents,
            cm : cmComponents,
            sm : smComponents,
            ddGroup : 'gridComponentsList',
            enableDragDrop : true,
            viewConfig : {
                forceFit : true
            }
//            , 
//            buttons : [ {
//                text : i18n.get('label.ok'),
//                scope : this,
//                handler : this.onValidate
//            }, {
//                text : i18n.get('label.cancel'),
//                scope : this,
//                handler : function () {
//                    this.close();
//                }
//            } ]
        });
        sitools.admin.forms.componentsListPanel.superclass.initComponent.call(this);
    },
    afterRender : function () {
        sitools.admin.forms.componentsListPanel.superclass.afterRender.apply(this, arguments);
        this.dd.addToGroup('gridComponentsTest');
//        this.gridComponents.setSize(this.body.getSize());
    },
//    onValidate : function () {
//        var rec = this.getSelectionModel().getSelected();
//        if (!rec) {
//            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
//        }
//        var ComponentWin = new sitools.admin.forms.componentPropPanel({
//            urlAdmin : rec.data.jsonDefinitionAdmin,
//            datasetColumnModel : this.datasetColumnModel,
//            ctype : rec.data.type,
//            gridFormComponents : this.gridFormComponents,
//            action : this.action,
//            componentDefaultHeight : rec.data.componentDefaultHeight,
//            componentDefaultWidth : rec.data.componentDefaultWidth,
//            dimensionId : rec.data.dimensionId,
//            unit : rec.data.unit,
//            extraParams : rec.data.extraParams, 
//            jsAdminObject : rec.data.jsAdminObject, 
//            jsUserObject : rec.data.jsUserObject, 
//            context : this.context, 
//            storeConcepts : this.storeConcepts
//        });
//        ComponentWin.show();
//        this.close();
//    },
    onClose : function () {
        this.close();
    }

});

/**
 * Show a preview of a specific compoennt type
 * @static
 * @param {string} value
 */
sitools.admin.forms.componentsListPanel.showPreview = function (value) {
    var previewWin = new Ext.Window({
        title : i18n.get('label.showPreview'),
        modal : true,
        html : "<img src ='" + value + "'>",
        layout : "fit",
        height : "auto",
        width : "auto",
        buttons : [ {
            text : i18n.get('label.close'),
            handler : function () {
                this.ownerCt.ownerCt.destroy();
            }
        } ]
    });
    previewWin.show();
};