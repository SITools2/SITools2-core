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
 * @include "componentsListPanel.js"
 * @include "componentPropPanel.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A grid to display all components of the selected Form. 
 * @cfg {Ext.grid.ColumnModel} datasetColumnModel the dataset Column Model (used for dataset mode)
 * @cfg {Ext.data.JsonStore} storeConcepts the store of concepts
 * @cfg {String} context The concept should be "dataset" or "project"
 * @class sitools.admin.forms.FormGridComponents
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.forms.FormGridComponents', { extend : 'Ext.grid.Panel',
	initComponent : function () {
		var storeComponents = new Ext.data.JsonStore({
            root : 'data',
//            url : this.urlFormulaire,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'code',
                type : 'string'
            }, {
                name : 'values'
            }, {
                name : 'width',
                type : 'int'
            }, {
                name : 'height',
                type : 'int'
            }, {
                name : 'xpos',
                type : 'int'
            }, {
                name : 'ypos',
                type : 'int'
            }, {
                name : 'css',
                type : 'string'
            }, {
                name : 'jsAdminObject',
                type : 'string'
            }, {
                name : 'jsUserObject',
                type : 'string'
            }, {
                name : 'defaultValues'
            }, {
                name : 'valueSelection',
                type : 'string'
            }, {
                name : 'autoComplete', 
                type : 'boolean'
            }, {
                name : 'parentParam'
            }, {
                name : 'dimensionId',
                type : 'string'
            }, {
                name : 'unit'
            }, {
				name : 'extraParams'
            }],
            autoLoad : false
        });
        var smComponents = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        var cmComponents = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.label'),
                dataIndex : 'label'
            }, {
                header : i18n.get('headers.type'),
                dataIndex : 'type'
            } ],
            defaults : {
                sortable : true,
                width : 100
            }
        });
        /*if (this.action == 'modify') {
            // storeComponents.load();
        }*/

        var tbar = {
            xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete
            } ]
        };
		Ext.apply(this, {
            title : i18n.get('title.gridComponents'),
            id : "gridFormComponents",
            height : 430,
            store : storeComponents,
            cm : cmComponents,
            sm : smComponents,
            tbar : tbar,
            viewConfig : {
                forceFit : true
            }, 
			listeners : {
	            scope : this, 
	            rowDblClick : this.onModify, 
	            dictionaryChanged : function (dictionaryId) {
					this.dictionaryId = dictionaryId;
	            }, 
	            collectionChanged : function (collectionId) {
					this.collectionId = collectionId;
	            }
			}
		});
		sitools.admin.forms.FormGridComponents.superclass.initComponent.call(this);
	}, 
    onCreate : function () {
        var listComponentWin = new sitools.admin.forms.componentsListPanel({
            datasetColumnModel : this.datasetColumnModel,
            gridFormComponents : this,
            action : 'create', 
            context : this.context, 
            storeConcepts : this.storeConcepts
        });
        listComponentWin.show();
    },
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var propComponentPanel = new sitools.admin.forms.componentPropPanel({
            datasetColumnModel : this.datasetColumnModel,
            gridFormComponents : this,
            action : 'modify', 
            urlFormulaire : this.urlFormulaire, 
            context : this.context, 
            storeConcepts : this.storeConcepts
        });
        propComponentPanel.show();
    },
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        var childrenExists = false, childrens = [];
        this.getStore().each(function (record) {
            if (rec.data.id === record.data.parentParam) {
                childrenExists = true;
                childrens.push(record.data.label);
            }
        });
        if (childrens.length > 0) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.atLeastOneChildren') + childrens.join(", "));
            return;
        }
        this.getStore().remove(rec);
    }	
});