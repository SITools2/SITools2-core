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
/*global Ext, sitools, i18n, userLogin, DEFAULT_ORDER_FOLDER, document, alertFailure, getDesktop, SitoolsDesk, projectGlobal, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.modules.userSpaceDependencies');

sitools.user.modules.userSpaceDependencies.orderPanel = Ext.extend(Ext.Panel, {
    layout : {
        type : 'vbox',
        flex : 1,
        align : 'stretch'
    },
    height : 430,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + "/orders/users/" + userLogin;
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";
        this.title = i18n.get('label.orders');
        var tbar = new Ext.Toolbar({});
        this.treePanel = new Ext.tree.TreePanel({
            title : i18n.get('label.selectResource'),
            useArrows : true,
            autoScroll : true,
            height : 280,
            flex : 1,
            animate : true,
            enableDD : false,
            containerScroll : true,
            rootVisible : true,
            frame : true,
            root : {
                text : DEFAULT_ORDER_FOLDER,
                children : [],
                nodeType : 'async', 
                url : loadUrl.get('APP_URL') + this.AppUserStorage + "/" + DEFAULT_ORDER_FOLDER 
            },

            listeners : {
				scope : this,
                beforeload : function (node) {
                    return node.isRoot || Ext.isDefined(node.attributes.children);
                },
                beforeexpandnode : function (node) {
                    node.removeAll();
                    Ext.Ajax.request({
                        url : node.attributes.url + "/?media=json",
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                Ext.each(Json, function (child) {
                                    node.appendChild({
                                        cls : child.cls,
                                        text : child.text,
                                        url : child.url,
                                        leaf : child.leaf,
                                        children : [],
                                        checked : child.checked
                                    });
                                });
                                return true;
                            } catch (err) {
                                Ext.Msg.alert(i18n.get('warning'), err);
                                return false;
                            }
                        },
                        failure : function (ret) {
                            return null;
                        }
                    });
                    return true;
                }, 
                click : function (n) {
                    if (n.attributes.leaf) {
                        Ext.Ajax.request({
                            url : n.attributes.url,
                            method : 'GET',
                            scope : this,
                            success : function (ret) {
                                var Json;
                                try {
                                    Json = Ext.decode(ret.responseText);
                                    if (!Ext.isEmpty(Json.orderRequest)) {
                                        this.orderRequest(Json.orderRequest, n.attributes.text);
                                    }
                                    if (!Ext.isEmpty(Json.orderRecord)) {
                                        this.orderRecord(Json.orderRecord, n.attributes.text);
                                    }
                                } catch (err) {
                                    Ext.Msg.alert(err);
                                }
                            },
                            failure : function (ret) {
                                return null;
                            }
                        });
                    }
                }
            }
        });

        this.items = [ {
            xtype : 'panel',
            flex : 1,
            items : [ {
                xtype : 'form',
                border : false,
                padding : 10,
                items : [ {
                    xtype : 'hidden',
                    name : 'userId',
                    value : userLogin
                }, {
                    xtype : 'hidden',
                    name : 'resourceDescriptor',
                    value : DEFAULT_ORDER_FOLDER
                }, {
                    xtype : 'textarea',
                    name : 'description',
                    fieldLabel : i18n.get('label.msgToAdmin'),
                    anchor : '100%',
                    maxLength : 100
                } ]
            }, this.treePanel ],
            buttons : [ {
                text : i18n.get('label.refreshResource'),
                scope : this,
                handler : this._onRefresh
            }, {
                text : i18n.get('label.deleteResource'),
                scope : this,
                handler : this._onDelete
            }, {
                text : i18n.get('label.sendOrder'),
                scope : this,
                handler : this._onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.destroy();
                }
            } ]
        } ];

        sitools.user.modules.userSpaceDependencies.orderPanel.superclass.initComponent.call(this);

    },
    _onRefresh : function () {
        this.treePanel.getRootNode().collapse();
    },

    _onDelete : function () {
        var selNodes = this.treePanel.getChecked();
        if (selNodes.length === 0) {
            return;
        }

        Ext.each(selNodes, function (node) {
            Ext.Ajax.request({
                method : 'DELETE',
                url : node.attributes.url + "?recursive=true",
                scope : this,
                success : function (response, opts) {
                    var notify = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('label.resourceDeleted'),
                        autoDestroy : true,
                        hideDelay : 1000
                    });
                    notify.show(document);
                    node.destroy();
                },
                failure : alertFailure
            }, this);
        });
    },
    _onValidate : function () {
        var selNodes = this.treePanel.getChecked();
        if (selNodes.length === 0) {
            return;
        }
        var putObject = {};
        putObject.resourceCollection = [];

        Ext.each(selNodes, function (node) {
            putObject.resourceCollection.push(node.attributes.url);
        });

        var f = this.findByType('form')[0].getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('warning.invalidForm'));
            return;
        }
        Ext.iterate(f.getValues(), function (key, value) {
            putObject[key] = value;
        }, this);

        Ext.Ajax.request({
            url : this.url,
            method : 'POST',
            scope : this,
            params : {
                filename : "",
                filepath : DEFAULT_ORDER_FOLDER
            },
            jsonData : putObject,
            success : function (ret) {
                var notify = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.orderSend'),
                    autoDestroy : true,
                    hideDelay : 1000
                });
                notify.show(document);
                this.destroy();
            },
            failure : alertFailure
        });

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
                        datasetName : json.dataset.name,
                        filtersCfg : orderRequest.filtersCfg, 
						dictionaryMappings : json.dataset.dictionaryMappings, 
	                    datasetViewConfig : json.dataset.datasetViewConfig, 
	                    preferencesPath : "/" + json.dataset.name, 
	                    preferencesFileName : "datasetView"
                    };

                    if (!Ext.isEmpty(orderRequest.formParams)) {
                        componentCfg.formParams = orderRequest.formParams;
                    }
                    var jsObj = eval(json.dataset.datasetView.jsObject);
                    
                    var windowConfig = {
                        id : "winOrderId", 
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
        var winOrderDetail = new sitools.user.modules.userSpaceDependencies.viewRecordSelectionDetail({
            orderRecord : orderRecord,
            nomFichier : nomFichier
        });
        winOrderDetail.show();
    }

});
