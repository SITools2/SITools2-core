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
Ext.define('sitools.admin.forms.componentsListPanel', { 
    extend : 'Ext.grid.Panel',
    width : 220,
    id: 'gridsource',
    draggable : true,
    layout : "fit",
    title : i18n.get('title.componentList'),
    autoScroll : true, 
    ddGroup : 'gridComponentsList',
    enableDragDrop : true,
    forceFit : true,
    stripeRows: true,

    initComponent : function () {
        
        this.title = i18n.get('label.chooseComponent');
        
        this.store = Ext.create("Ext.data.JsonStore", {
            proxy : {
                type : 'ajax',
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_FORMCOMPONENTS_URL'),
                simpleSortMode : true,
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
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
            sorters : [{
			    property: 'type',
			    direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
			}]
           
        });
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });

        this.columns = [{
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
        }];

        
        this.viewConfig = {
            plugins: {
                ddGroup: 'gridComponentsList',
                ptype: 'gridviewdragdrop',
                enableDrop: false
            }
        };

        sitools.admin.forms.componentsListPanel.superclass.initComponent.call(this);
    },
    
    
    afterRender : function () {
        sitools.admin.forms.componentsListPanel.superclass.afterRender.apply(this, arguments);
        this.dd = new Ext.dd.DragDrop(this.getId(), 'gridComponentsTest', {});
        
        new Ext.dd.DragZone(this.getEl(), {
            notifyDrag: function(dragsource, event, data) {
                Ext.example.msg("drag target");
            }
        });
        
//        this.dd.addToGroup('gridComponentsTest');
//        this.gridComponents.setSize(this.body.getSize());
    },
//    onValidate : function () {
//        var rec = this.getSelectionModel().getSelected();
//        if (!rec) {
//            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
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
    var previewWin = Ext.create("Ext.Window", {
        title : i18n.get('label.showPreview'),
        modal : true,
        border : false,
        resizable : false,
        items : [{
            xtype : 'component',
            padding : 5,
            border : false,
            autoEl: {
                id : 'preview-win',
                tag: 'img',
                border : false,
                src: value
            },
            listeners : {
                afterrender : function (img) {
                    img.getEl().on('load', function () {
                        var hiImg = img.offsetHeight;
                        var wiImg = img.offsetWidth;
                        img.up("window").setSize(wiImg, hiImg);
                    });
                }
            }
        }],
        layout : "fit",
        buttons : [ {
            text : i18n.get('label.close'),
            handler : function (button) {
                button.up("window").close();
            }
        } ]
    });
    previewWin.show();
};