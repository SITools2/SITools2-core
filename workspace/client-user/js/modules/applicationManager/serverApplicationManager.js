/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, i18n, callBackServerData, callBackAddComp, SitoolsDesk, document, sitools, projectGlobal*/

Ext.namespace('sitools.user.modules');

/**
 * Manager d'applications
 * 
 * @author a.labeau
 */

/**
 * Desktop Manager Object
 * @class sitools.user.modules.serverApplicationManager
 */
sitools.user.modules.serverApplicationManager = function (config) {
    Ext.QuickTips.init();
    /**
     * Variables privees
     */
    var appManager = new sitools.user.modules.applicationManager.dependencies.applicationManager();
    var store = null;
    var dataServer = [];
    var conn = new Ext.data.Connection();

    /*
     * Methodes privees
     */

    function getStore() {

        // create the data store
        store = new Ext.data.ArrayStore({
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'author',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'url',
                type : 'string'
            } ]
        });
        return store;
    }

    var gridAppServer = new Ext.grid.GridPanel({
        store : getStore(),
        loadMask : true,
        autoExpandColumn : 'url',
        sm : new Ext.grid.RowSelectionModel({
            singleSelect : true
        }),
        columns : [ {
            header : i18n.get('label.name'),
            sortable : true,
            dataIndex : 'name'
        }, {
            header : i18n.get('label.author'),
            sortable : true,
            dataIndex : 'author'
        }, {
            header : i18n.get('label.description'),
            sortable : true,
            dataIndex : 'description'
        }, {
            id : 'url',
            header : i18n.get('label.url'),
            sortable : true,
            dataIndex : 'url'
        } ]

    });
	var oneData = null;
	
	Ext.each(projectGlobal.modules, function (conf) {
        oneData = [ conf.id, conf.name, conf.author, conf.description, conf.url ];
        dataServer.push(oneData);
    });
    // On charge les donnees du store dans le callBack
    // pour s'assurer qu'elles ont bien etes recuperees sur le serveur
    store.loadData(dataServer);

    // On ne peut supprimer un composant s'il n'y en a pas...
    appManager.getSelectionModel().on('selectionchange', function (sm) {
        Ext.getCmp("remCptBtn").setDisabled(sm.getCount() < 1);
    });

    // On ne peut ajouter un composant s'il n'y en a pas sur le serveur
    gridAppServer.getSelectionModel().on('selectionchange', function (sm) {
        Ext.getCmp("addCptBtn").setDisabled(sm.getCount() < 1);
    });

    var addBtnHandler = function () {
        // ajout du composant dans 'myApplications'
        var recordSld = gridAppServer.getSelectionModel().getSelected();
        if (appManager.addAppHandler(recordSld)) {
            // Recuperation des donnees sur le serveur de maniere asynchrone
            conn.request({
//                url : '/sitools/client-user/tmp/listeComponents.json',
                url : projectGlobal.sitoolsAttachementForUsers + "/listComponents.json",
                method : 'GET',
                params : {
                    "profile" : "leo"
                },
                scriptTag : true,
                success : function (response) {
                    callBackAddComp(response.responseText, recordSld.data.id);
                },
                failure : function () {
                    Ext.Msg.alert('Status', 'Unable to locate desktop data.');
                }
            });
        }
    };

    /***************************************************************************
     * Le callbackAddComp permet de rajouter dans l'application * un composant
     * venant du serveur avec son nouveau ID *
     **************************************************************************/

    function callBackAddComp(response, idAppSelected) {
        var conf = Ext.decode(response);
        var comp = null;
        Ext.each(conf.components, function (conf) {
            if (conf.id.match(idAppSelected)) {
                comp = conf;
                comp.id = idAppSelected;
            }
        });

        SitoolsDesk.addApplication(comp);

        var notify = new Ext.ux.Notification({
            iconCls : 'x-icon-error',
            title : i18n.get('label.appAdded'),
            html : comp.name + " " + i18n.get('label.added'),
            autoDestroy : true,
            hideDelay : 1000
        });
        notify.show(document);
    }

    var delBtnHandler = function () {
        var recordSld = appManager.getSelectionModel().getSelected();

        var notify = new Ext.ux.Notification({
            iconCls : 'x-icon-error',
            title : i18n.get('label.appRemoved'),
            html : recordSld.data.name + " " + i18n.get('label.removed'),
            autoDestroy : true,
            hideDelay : 1000
        });
        notify.show(document);

        SitoolsDesk.removeApplication(recordSld.data.id);
        appManager.delAppHandler();
    };

    // ***************** CONSTRUCTION DU MODULE *************************//
    var mainPanel = new Ext.Panel({
        layout : 'vbox',
        layoutConfig : {
            align : 'stretch',
            pack : 'start'
        },
        items : [ {
            xtype : 'panel',
            title : i18n.get('label.myDesktop'),
            region : 'north',
            autoScroll : true,
            split : true,
            layout : 'fit',
            flex : 1,
            items : appManager

        }, {
            xtype : 'panel',
            region : 'center',
            layout : 'hbox',
            autoScroll : true,
            split : true,
            autoHeight : false,
            height : 30,
            maxHeight : 10,
            layoutConfig : {

            // flex : 'All even'

            },
            items : [ {
                xtype : 'tbbutton',
                id : 'addCptBtn',
                iconCls : 'controller-add',
                text : i18n.get('label.AddToDesktop'),
                handler : addBtnHandler,
                disabled : true,
                flex : 1

            }, {
                xtype : 'tbbutton',
                id : 'remCptBtn',
                iconCls : 'controller-delete',
                text : i18n.get('label.RemoveFromDesktop'),
                disabled : true,
                handler : delBtnHandler,
                flex : 1
            } ]
        }, {
            xtype : 'panel',
            title : i18n.get('label.serverApp'),
            region : 'south',
            layout : 'fit',
            split : true,
            autoScroll : true,
            flex : 1,
            items : gridAppServer
        } ],
        buttons : [ {
            text : i18n.get("label.close"),
            handler : function () {
                this.ownerCt.ownerCt.ownerCt.close();
            }
        } ]
    });
    return mainPanel;
};

Ext.reg('sitools.user.modules.serverApplicationManager', sitools.user.modules.serverApplicationManager);
