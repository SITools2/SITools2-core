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
 * @include "../id.js"
 * @include "MultiDsPropPanel.js"
 */
Ext.namespace('sitools.admin.multiDs');

/**
 * A Grid to display all projects Forms 
 * @class sitools.admin.multiDs.MultiDsCrudPanel
 * @extends Ext.grid.GridPanel
 * @requires sitools.admin.multiDs.MultiDsPropPanel
 */
Ext.define('sitools.admin.multiDs.MultiDsCrudPanel', { extend : 'Ext.grid.Panel',
    alias : 'widget.s-multiDs',
	border : false,
    height : 300,
    id : ID.BOX.MULTIDS,
    pageSize : 10,
    urlMultiDs : "/tmp",
    forceFit : true,

    initComponent : function () {
        this.baseUrlMultiDs = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');
        this.urlProjects = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');
        
        this.httpProxyMultiDs = new Ext.data.HttpProxy({
            url : this.baseUrlMultiDs,
            restful : true,
            method : 'GET'
        });
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            proxy : this.httpProxyMultiDs,
            remoteSort : true,
            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'collection'
            }, {
                name : 'dictionary'
            } ]
        });

        var storeProjects = new Ext.data.JsonStore({
            fields : [ 'id', 'name'],
            url : this.urlProjects,
            root : "data",
            autoLoad : true
        });
        
        this.comboProjects = new Ext.form.ComboBox({
            store : storeProjects,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectProjects'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
//                    this.projectId = rec.data.id;
                    this.loadProject(rec[0].data.id);
                }

            }
        });

        this.columns = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 400,
                sortable : false
            } ]
        });

        this.bbar = {
            xtype : 'pagingtoolbar',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboProjects, {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        sitools.admin.multiDs.MultiDsCrudPanel.superclass.initComponent.call(this);

    },
    loadProject : function (projectId) {
        // alert (dictionaryId);
        this.httpProxyMultiDs.url = this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL');
        this.getStore().load({
            scope : this,
            callback : function () {
                this.getView().refresh();
            }
        });
    },

    onCreate : function () {
        var projectId = this.comboProjects.getValue();
                    
        if (Ext.isEmpty(projectId)) {
            return;
        }
        this.httpProxyMultiDs.url = this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL');
        
        var up = new sitools.admin.multiDs.MultiDsPropPanel({
            urlMultiDs : this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL'), 
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.FORMS);
    },

    onModify : function () {
        var projectId = this.comboProjects.getValue();
        if (Ext.isEmpty(projectId)) {
            return;
        }
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        this.httpProxyMultiDs.url = this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL');
        
        var up = new sitools.admin.multiDs.MultiDsPropPanel({
            urlMultiDs : this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL'), 
            action : 'modify',
            store : this.getStore(), 
            formId : rec.data.id, 
            collection : rec.data.collection, 
            dictionary : rec.data.dictionary
        });
        up.show(ID.PROP.MULTIDSPROP);
    },

    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('formsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        var projectId = this.comboProjects.getValue();
        if (Ext.isEmpty(projectId)) {
            return;
        }
        Ext.Ajax.request({
            url : this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL') + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.httpProxyMultiDs.url = this.baseUrlMultiDs + "/" + projectId + loadUrl.get('APP_FORMPROJECT_URL');
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    }

});


