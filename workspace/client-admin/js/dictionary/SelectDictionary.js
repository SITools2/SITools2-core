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
Ext.namespace('sitools.admin.dictionary');

/**
 * A window that displays the columns of a dataset. 
 * @cfg string field The attribute of the record to edit 
 * @cfg Ext.data.Record storeRecord the record to edit
 * @cfg Ext.data.Store parentStore the store of the record
 * @cfg Ext.grid.View parentView the view of the grid
 * @cfg string url the url to request dataset
 * @class sitools.admin.dictionary.SelectDictionary
 * @extends Ext.Window
 */
Ext.define('sitools.admin.dictionary.SelectDictionary', { 
    extend : 'Ext.Window',
	alias : 'widget.s-selectDictionaryWin',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    

    initComponent : function () {
        this.title = i18n.get('title.selectDictionary');

        this.columns = [{
            id : 'name',
            header : i18n.get('headers.name'),
            sortable : true,
            dataIndex : 'name'
        }, {
            id : 'description',
            header : i18n.get('headers.description'),
            sortable : false,
            dataIndex : 'description'
        } ];

        this.smselectDictionary = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });

        this.gridselectDictionary = Ext.create("Ext.grid.GridPanel", {
            height : 380,
            autoScroll : true,
            forceFit : true,
            store : Ext.create("Ext.data.JsonStore", {
                proxy : {
                    url : this.url,
                    type : 'ajax',
                    reader : {
                        type : 'json',
                        idProperty : 'id',
                        root : 'data',
                    }
                },
                remoteSort : true,
                fields : [ {
                    name : 'id',
                    type : 'string'
                }, {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'description',
                    type : 'string'
                } ],
                autoLoad : true
            }),
            columns : this.columns,
            selModel : Ext.create('Ext.selection.RowModel',{
                mode : "SINGLE"
            })
        });

        this.items = [ {
            xtype : 'panel',
            layout : 'fit',
            items : [ this.gridselectDictionary ],
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
        sitools.admin.dictionary.SelectDictionary.superclass.initComponent.call(this);
    },
    /**
     * Method called on Ok button. 
     * update the Record and close the window
     */
    onValidate : function () {
        var rec = this.getLastSelectedRecord();
        if (rec !== null) {
	        this.record.data[this.field] = rec.data.name;
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


