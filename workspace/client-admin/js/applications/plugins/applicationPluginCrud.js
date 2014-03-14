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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl*/
/*
 * @include "applicationPluginProp.js"
 * @include "../../id.js" 
 */
Ext.namespace('sitools.admin.applications.plugins');
/**
 * @class sitools.admin.applications.plugins.ApplicationPluginCrudPanel
 * @extends Ext.grid.GridPanel
 */
//sitools.component.applicationPlugin.applicationPluginCrudPanel = Ext.extend(Ext.grid.GridPanel, {
sitools.admin.applications.plugins.ApplicationPluginCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.APPLICATIONPLUGIN,
    pageSize : 10,    
    modify : false,
    urlGrid : null,
    
    // Warning for version conflicts
	conflictWarned : false,
    viewConfig : {
        forceFit : true,
        autoFill : true, 
		getRowClass : function (row, index) { 
			var cls = ''; 
			var data = row.data;
			if (data.classVersion !== data.currentClassVersion
			    && data.currentClassVersion !== null 
				&& data.currentClassVersion !== undefined) {
				if (!this.conflictWarned) {
					Ext.Msg.alert("warning.version.conflict", "Application plugin " 
					+ data.name 
					+ "definition (v" 
					+ data.classVersion 
					+ ") may conflict with current class version : " 
					+ data.currentClassVersion);
					this.conflictWarned = true;
				}
				cls = "red-row";
			}
			return cls; 
		} 
		
	},
	
	initComponent : function () {
        this.urlAdmin = loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_APPLICATIONS_URL') + '/instances';
        this.urlList = loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_APPLICATIONS_URL') + '/classes';
        this.httpProxyForms = new Ext.data.HttpProxy({
            url : this.urlAdmin,
            restful : true,
            method : 'GET'
            
        });
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : "data",
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
                name : 'className',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
                name : 'urlAttach',
                type : 'string'
            }, {
				name : 'classVersion',
				type : 'string'
            }, {
				name : 'classAuthor',
				type : 'string'
            }, {
				name : 'currentClassVersion',
				type : 'string'
            }, {
				name : 'currentClassAuthor',
				type : 'string'
            },
            {
                name : 'classOwner',
                type : 'string'
            }],
            proxy : this.httpProxyForms
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.label'),
                dataIndex : 'label',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.urlAttach'),
                dataIndex : 'urlAttach',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.className'),
                dataIndex : 'className',
                width : 150,
                sortable : false
            }, {
                header : i18n.get('label.classVersion'),
                dataIndex : 'classVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.currentClassVersion'),
                dataIndex : 'currentClassVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.classAuthor'),
                dataIndex : 'classAuthor',
                width : 100,
                sortable : false
            },
            {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : false
            }]
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.add'),
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
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            } ]
        };

        this.bbar = {
                xtype : 'paging',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };
        sitools.admin.applications.plugins.ApplicationPluginCrudPanel.superclass.initComponent.call(this);

    },

	/**
	 * Load the Store on render event 
	 */
    onRender : function () {
        sitools.admin.applications.plugins.ApplicationPluginCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Open a {sitools.admin.applications.plugins.applicationPluginProp} window
     * to create a new Application plugin
     */
    onCreate : function () {
        var up = new sitools.admin.applications.plugins.applicationPluginProp({
            action : 'create',            
            parent : this,          
            urlList : this.urlList,
            urlAdmin : this.urlAdmin
        });
        up.show();
    },

    /**
     * Open a {sitools.admin.applications.plugins.applicationPluginProp} window
     * to edit an Application plugin
     */
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        if ("ACTIVE" === rec.data.status) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        var up = new sitools.admin.applications.plugins.applicationPluginProp({
            action : 'modify',
            record : rec,
            parent : this,
            urlList : this.urlList,
            urlAdmin : this.urlAdmin            
        });
        up.show();

    },

    /**
     * Open a confirmation window before deleting selected record
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : i18n.get('applicationPluginCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });
    },

    /**
     * Call the delete method
     */
    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.urlAdmin + "/" + rec.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    /**
     * Call the resource start on the application 
     */
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.urlAdmin + "/" + rec.id + "/start",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    /**
     * Call the resource stop on the application 
     */
    _onDisactive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.urlAdmin + "/" + rec.id + "/stop",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }
});

Ext.reg('s-Application_plugins', sitools.admin.applications.plugins.ApplicationPluginCrudPanel);
