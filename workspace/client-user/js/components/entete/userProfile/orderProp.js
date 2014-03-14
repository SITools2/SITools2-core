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
/*global Ext, sitools, i18n, userLogin, DEFAULT_ORDER_FOLDER, SitoolsDesk, projectGlobal, DEFAULT_PREFERENCES_FOLDER, loadUrl, viewFileContent*/

Ext.namespace('sitools.user.component.entete.userProfile');

sitools.user.component.entete.userProfile.orderProp = Ext.extend(Ext.Panel, {
    pageSize : 10,
    dataSets : "",
    autoScroll : true, 
	layout : 'fit',
	
    initComponent : function () {
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
            collapsible : false, 
            viewConfig : {
				forceFit : true
            }
            
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

        var resourceCollectionCm = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get("headers.resource"),
                dataIndex : 'resourceCollection',
                width : 500
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
            viewConfig : {
				forceFit : true
            }, 
            collapsible : false, 
            listeners : {
                scope : this, 
                rowclick : function (grid, rowIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
                    if (Ext.isEmpty(rec)) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noSelection'));
                        return;
                    }
                    var tabTmp = rec.data.resourceCollection.split('/');
                    var nomFichier = tabTmp[tabTmp.length - 1];
                    tabTmp = nomFichier.split('.');
                    var extension = tabTmp[tabTmp.length - 1];
                    
                    
                    if (extension != "json") {
                        var url = rec.data.resourceCollection;
                        viewFileContent(url, nomFichier);
                    } else {
                        Ext.Ajax.request({
                            url : rec.data.resourceCollection,
                            method : 'GET',
                            scope : this,
                            success : function (ret) {
                                var Json;
                                if (extension == "json") {
                                    //If it is a Json file, try to decode it and use it into a specific grid
                                    try {
                                        Json = Ext.decode(ret.responseText);
                                        if (!Ext.isEmpty(Json.orderRequest)) {
                                            this.orderRequest(Json.orderRequest, nomFichier);
                                        }
                                        else if (!Ext.isEmpty(Json.orderRecord)) {
                                            this.orderRecord(Json.orderRecord, nomFichier);
                                        }
                                        else {
                                            var windowConfig = {
                                                id : "winPreferenceDetailId", 
                                                title : nomFichier
                                            };
                                            var jsObj = Ext.Panel;
                                            var componentCfg = {
                                                layout : 'fit', 
                                                autoScroll : true, 
                                                html : ret.responseText
                                            };
                                            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
                                        }
                                    } catch (err) {
                                        Ext.Msg.alert(err);
                                    }
                                }                            
                            },
                            failure : function (ret) {
                                Ext.Msg.alert(i18n.get('label.error'), ret.responseText);
                            }
                        });
                    }
                }
            }
        });

        this.items = [ {
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
            }, resourceCollectionGrid, eventsGrid ],
            buttons : [ {
                text : i18n.get('label.close'),
                scope : this,
                handler : function () {
                    this.ownerCt.close();
                }
            } ]

        } ];
        sitools.user.component.entete.userProfile.orderProp.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.user.component.entete.userProfile.orderProp.superclass.onRender.apply(this, arguments);
        var f = this.findByType('form')[0].getForm();
        f.loadRecord(this.orderRec);
    }, 
    orderRequest : function (orderRequest, nomFichier) {
        Ext.Ajax.request({
            scope : this,
            method : 'GET',
            url : orderRequest.datasetUrl,
            success : function (response, opts) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (!json.success) {
                        throw (json.message);
                    }

                    var componentCfg = {
                        dataUrl : json.dataset.sitoolsAttachementForUsers,
                        datasetId : orderRequest.datasetId,
                        datasetCm : Ext.decode(orderRequest.colModel),
                        filters : orderRequest.filters, 
						dictionaryMappings : json.dataset.dictionaryMappings, 
	                    datasetViewConfig : json.dataset.datasetViewConfig, 
	                    preferencesPath : "/" + json.dataset.name, 
	                    preferencesFileName : "datasetView"
                    // ,
                    // sorters : orderRequest.sort.sorters
                    };
                    if (!Ext.isEmpty(orderRequest.formParams)) {
                        componentCfg.formParams = orderRequest.formParams;
                    }

                    var jsObj = eval(json.dataset.datasetView.jsObject);
                    
                    var windowConfig = {
                        id : "winOrderId" + json.dataset.id, 
                        title : nomFichier, 
                        datasetName : json.dataset.name, 
                        saveToolbar : true, 
                        type : "data"
                    };
                    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);

                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }
            },
            failure : function (response, opts) {
                Ext.Msg.alert(response);
            }
        });
    },
    orderRecord : function (orderRecord, nomFichier) {
        var winOrderDetail = new sitools.user.component.entete.userProfile.viewRecordSelectionDetail({
            orderRecord : orderRecord,
            nomFichier : nomFichier
        });
        winOrderDetail.show();
    }

});

Ext.reg('s-orderprop', sitools.user.component.entete.userProfile.orderProp);
