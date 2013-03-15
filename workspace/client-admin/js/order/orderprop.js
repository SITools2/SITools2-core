/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

sitools.component.order.orderPropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
    dataSets : "",
    layout : 'fit', 

    initComponent : function () {
        this.title = i18n.get('label.details') + " : ";

        this.title += this.orderRec.data.userId;
        this.title += " " + i18n.get('label.the');
        this.title += " " + this.orderRec.data.dateOrder;

        var eventsStore = new Ext.data.JsonStore({
            fields : [ {
                name : 'eventDate',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'message',
                type : 'string'
            } ],
            data : this.orderRec.data.events
        });

        var eventsCm = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get("headers.date"),
                dataIndex : 'eventDate',
                width : 150
            },
            // {header : i18n.get("headers.order"), dataIndex: 'order', width :
            // 100},
            {
                header : i18n.get("headers.description"),
                dataIndex : 'description',
                width : 250
            }, {
                header : i18n.get("headers.message"),
                dataIndex : 'message',
                width : 150
            } ]
        });
        var eventsGrid = new Ext.grid.GridPanel({
            layout : 'fit', 
            flex : 0.5, 
            title : i18n.get('label.events'),
            store : eventsStore,
            cm : eventsCm,
            rowSelectionModel : new Ext.grid.RowSelectionModel(),
            autoScroll : true,
            collapsible : false
        });

        var resourceCollectionStore = new Ext.data.JsonStore({
            fields : [ {
                name : 'resourceCollection',
                type : 'string'
            } ]
        });
        if (!Ext.isEmpty(this.orderRec.data.resourceCollection)) {
            Ext.each(this.orderRec.data.resourceCollection, function (resource) {
                var rec = new resourceCollectionStore.recordType({
                    resourceCollection : resource
                });
                resourceCollectionStore.add(rec);
            });
            
        }
        
        if (!Ext.isEmpty(this.orderRec.data.adminResourceCollection)) {
            Ext.each(this.orderRec.data.adminResourceCollection, function (resource) {
                var rec = new resourceCollectionStore.recordType({
                    resourceCollection : resource
                });
                resourceCollectionStore.add(rec);
            });
            
        }

        var resourceCollectionCm = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get("headers.resource"),
                dataIndex : 'resourceCollection',
                width : 600
            } ]
        });
        var resourceCollectionGrid = new Ext.grid.GridPanel({
            layout : 'fit', 
            flex : 0.5, 
            title : i18n.get('label.resourceCollection'),
            store : resourceCollectionStore,
            cm : resourceCollectionCm,
            rowSelectionModel : new Ext.grid.RowSelectionModel(),
            autoScroll : true,
            collapsible : false
        });

        this.items = [ {
            xtype : 'panel',
            layout : 'fit', 
            items : [ {
                xtype : 'panel',
	            layout : 'vbox', 
	            layoutConfig : {
					align : 'stretch', 
					flex : 'ratio'
	            }, 
                items : [ {
                    xtype : 'form',
                    border : false,
                    padding : 10,
                    items : [ {
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
                }, resourceCollectionGrid, eventsGrid ]
            } ],
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

    onRender : function () {
        sitools.component.order.orderPropPanel.superclass.onRender.apply(this, arguments);
        var f = this.findByType('form')[0].getForm();
        f.loadRecord(this.orderRec);
    }

});

Ext.reg('s-orderprop', sitools.component.order.orderPropPanel);
