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
 showHelp*/
Ext.namespace('sitools.admin.datasets');

/**
 * A window that displays the columns of a dataset. 
 * @cfg string field The attribute of the record to edit 
 * @cfg Ext.data.Record storeRecord the record to edit
 * @cfg Ext.data.Store parentStore the store of the record
 * @cfg Ext.grid.View parentView the view of the grid
 * @cfg string url the url to request dataset
 * @class sitools.admin.datasets.selectColumn
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.selectColumn', { 
    extend : 'Ext.Window',
	alias : 'widget.s-datasetscolumnwin',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,

    initComponent : function () {
        this.title = i18n.get('title.selectColumn');

        this.smSelectColumn = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });

        this.gridSelectColumn = Ext.create("Ext.grid.GridPanel", {
            height : 380,
            autoScroll : true,
            forceFit : true,
            store : Ext.create("Ext.data.JsonStore", {
                proxy : {
                    type :'ajax',
                    method : 'GET',
                    url : this.url,
                    reader : {
                        type : 'json',
                        idProperty : 'id',
                        root : 'dataset.columnModel'
                    }
                },
                remoteSort : true,
                fields : [ {
                    name : 'id',
                    type : 'string'
                }, {
                    name : 'columnAlias',
                    type : 'string'
                }, {
                    name : 'visible',
                    type : 'boolean'
                }, {
                    name : 'schema',
                    type : 'string'
                }, {
                    name : 'tableName',
                    type : 'string'
                }, {
                    name : 'columnRenderer',
                    type : 'string'
                } ],
                autoLoad : true,
                listeners : {
                    scope : this,
                    load : function (store, records, options) {
                        var i;
                        for (i = 0; i < records.length; i++) {
                            if (records[i].data !== undefined && !records[i].data.visible && records[i].data.columnRenderer != "noClientAccess") {
                                store.remove(records[i]);
                            }
                        }
                    }
                }
            }),
            columns : [{
                id : 'name',
                header : i18n.get('headers.name'),
                sortable : true,
                dataIndex : 'columnAlias'
            }],
            selModel : this.smSelectColumn
        });

        this.items = [ {
            xtype : 'panel',
            layout : 'fit',
            items : [ this.gridSelectColumn ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate

            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]

        } ];
        sitools.admin.datasets.selectColumn.superclass.initComponent.call(this);
    },
    /**
     * Method called on Ok button. 
     * update the Record and close the window
     */
    onValidate : function () {
        var rec = this.gridSelectColumn.getSelectionModel().getLastSelected();
        if (rec !== null) {
	        this.record.data[this.field] = rec.get("columnAlias");
	
	        // this.recordColumn.data.dataIndex = rec.data.dataIndex;
	        // this.recordColumn.data.schema = rec.data.schema;
	
	        this.parentView.refresh();
	        this.close();
        } else {
            new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('warning.noselection'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
        }

    }

});

