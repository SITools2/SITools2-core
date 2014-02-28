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
Ext.namespace('sitools.admin.usergroups');

/**
 * A Panel to show all the Roles in Sitools2
 * 
 * @cfg {String} the url where get the registers
 * @cfg {Ext.data.JsonStore} the store where saved the roles data
 * @class sitools.admin.usergroups.RoleCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.usergroups.RoleCrudPanel', { extend :'Ext.grid.Panel',
	alias : 'widget.s-rolecrud',
    border : false,
    height : 300,
    id : ID.BOX.ROLE,
    forceFit : true,
    selModel : Ext.create('Ext.selection.RowModel',{
        singleSelect : true
    }),
    pageSize : 10,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ROLES_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            url : this.url,
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
            } ]
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
                width : 200
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
            items : [ {
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
            }, {
                text : i18n.get('label.users'),
                icon : 'res/images/icons/icons_perso/toolbar_userman.png',
                handler : this.onUsers,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.groups'),
                icon : 'res/images/icons/icons_perso/toolbar_groupman.png',
                handler : this.onGroups,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        sitools.admin.usergroups.RoleCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load roles from the store. 
     */ 
    onRender : function () {
        sitools.admin.usergroups.RoleCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Open a {sitools.admin.usergroups.RolePropPanel} role panel to create a new role
     */
    onCreate : function () {
        var up = new sitools.admin.usergroups.RolePropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        up.show(ID.BOX.ROLE);
    },

    /**
     * Open a {sitools.admin.usergroups.RolePropPanel} role panel to modify an existing role
     */
    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.usergroups.RolePropPanel({
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.store
        });
        up.show(ID.BOX.ROLE);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('roleCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    
    /**
     * done the delete of the passed record
     * @param rec the record to delete
     */
    doDelete : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
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
     * Open a {sitools.admin.usergroups.UsersPanel} users panel retrieving all the users of the current role
     */
    onUsers : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.usergroups.UsersPanel({
            mode : 'list',
            url : this.url + '/' + rec.data.id + '/users',
            data : rec.data
        });
        up.show(ID.BOX.ROLE);
    },

    /**
     * Open a {sitools.admin.usergroups.GroupsPanel} groups panel retrieving all the groups of the current role
     */
    onGroups : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var gp = new sitools.admin.usergroups.GroupsPanel({
            mode : 'list',
            url : this.url + '/' + rec.data.id + '/groups',
            data : rec.data
        });
        gp.show(ID.BOX.ROLE);
    }
});
