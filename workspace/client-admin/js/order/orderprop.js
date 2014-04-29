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
Ext.namespace('sitools.component.order');

Ext.define('sitools.component.order.orderPropPanel', {
    extend : 'Ext.Window',
	alias : 'widget.s-orderprop',
    width : 700,
    height : 500,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    dataSets : "",
    layout : 'fit', 

    initComponent : function () {
        this.title = i18n.get('label.details') + " : ";

        this.title += this.orderRec.data.userId;
        this.title += " " + i18n.get('label.the');
        this.title += " " + this.orderRec.data.dateOrder;

        var eventsStore = Ext.create('Ext.data.JsonStore', {
            fields : [{
                name : 'eventDate',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'message',
                type : 'string'
            }],
            data : this.orderRec.data.events
        });
        
        var eventsGrid = Ext.create('Ext.grid.Panel', {
            layout : 'fit', 
            flex : 0.5, 
            title : i18n.get('label.events'),
            store : eventsStore,
            columns : [{
                header : i18n.get("headers.date"),
                dataIndex : 'eventDate',
                width : 150
            }, {
                header : i18n.get("headers.description"),
                dataIndex : 'description',
                width : 250
            }, {
                header : i18n.get("headers.message"),
                dataIndex : 'message',
                width : 150
            }],
            rowSelectionModel : Ext.create('Ext.selection.RowModel', {
                mode : 'SINGLE'
            }),
            autoScroll : true,
            padding : '5 5 5 5',
            forceFit : true
        });

        var resourceCollectionStore = Ext.create('Ext.data.JsonStore', {
            fields : [{
                name : 'resourceCollection',
                type : 'string'
            }]
        });
        
        if (!Ext.isEmpty(this.orderRec.data.resourceCollection)) {
            Ext.each(this.orderRec.data.resourceCollection, function (resource) {
                var rec = {
                    resourceCollection : resource
                };
                resourceCollectionStore.add(rec);
            });
            
        }
        
        if (!Ext.isEmpty(this.orderRec.data.adminResourceCollection)) {
            Ext.each(this.orderRec.data.adminResourceCollection, function (resource) {
                var rec = {
                    resourceCollection : resource
                };
                resourceCollectionStore.add(rec);
            });
            
        }

        var resourceCollectionGrid = Ext.create('Ext.grid.Panel', {
            layout : 'fit', 
            flex : 0.5,
            title : i18n.get('label.resourceCollection'),
            store : resourceCollectionStore,
            columns : [{
                header : i18n.get("headers.resource"),
                dataIndex : 'resourceCollection',
                width : 600
            }],
            rowSelectionModel : Ext.create('Ext.selection.RowModel', {
                mode : 'SINGLE'
            }),
            autoScroll : true,
            padding : '5 5 5 5',
            forceFit : true
        });

        this.items = [{
            xtype : 'panel',
            layout : {
                type : 'vbox',
                align : 'stretch',
                pack  : 'start'
            },
            items : [{
                    xtype : 'form',
                    border : false,
                    padding : 10,
                    height : 140,
                    items : [{
                        xtype : 'hidden',
                        name : 'orderId',
                        id : 'userValueFieldId'
                    }, {
                        xtype : 'textfield',
                        name : 'userId',
                        fieldLabel : i18n.get('label.userLogin'),
                        anchor : '100%',
                        maxLength : 100,
                        readOnly : true
                    }, {
                        xtype : 'textfield',
                        name : 'description',
                        fieldLabel : i18n.get('label.description'),
                        anchor : '100%',
                        maxLength : 100,
                        readOnly : true
                    }, {
                        xtype : 'textfield',
                        name : 'resourceDescriptor',
                        fieldLabel : i18n.get('label.url'),
                        anchor : '100%',
                        maxLength : 30,
                        readOnly : true
                    }, {
                        xtype : 'textfield',
                        name : 'dateOrder',
                        fieldLabel : i18n.get('label.dateOrder'),
                        anchor : '100%',
                        maxLength : 100,
                        readOnly : true
                    } ]
                }, resourceCollectionGrid, eventsGrid],
            buttons : [ {
                text : i18n.get('label.close'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        } ];
        
        sitools.component.order.orderPropPanel.superclass.initComponent.call(this);
    },

    afterRender : function () {
        sitools.component.order.orderPropPanel.superclass.afterRender.apply(this, arguments);
        var f = this.down('form').getForm();
        f.loadRecord(this.orderRec);
    }

});


