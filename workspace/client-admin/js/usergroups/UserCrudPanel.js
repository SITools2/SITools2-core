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
 * A Panel to show all the users in Sitools2
 * 
 * @cfg {String} the url where get the users
 * @cfg {Ext.data.JsonStore} the store where saved the users data
 * @class sitools.admin.usergroups.UserCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.usergroups.UserCrudPanel', { extend :'Ext.grid.Panel',
	alias : 'widget.s-usercrud',
    border : false,
    height : 300,
    id : ID.BOX.USER,
    sm : Ext.create('Ext.selection.RowModel',{
        singleSelect : true
    }),
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_SECURITY_URL') + '/users';
        
        /*
         * // The new DataWriter component. var writer = new
         * Ext.data.JsonWriter({ encode: false // <-- don't return encoded JSON --
         * causes Ext.Ajax#request to send data using jsonData config rather
         * than HTTP params });
         */
        // create the restful Store
        // Method url action
        // POST /users create
        // GET /users read
        // PUT /users/id update
        // DESTROY /users/id delete
        this.store = Ext.create('Ext.data.JsonStore', {
            root : 'data',
            restful : true,
            autoSave : false,
            url : this.url,
            idProperty : 'identifier',
            remoteSort : true,
            fields : [ {
                name : 'identifier',
                type : 'string'
            }, {
                name : 'firstName',
                type : 'string'
            }, {
                name : 'lastName',
                type : 'string'
            }, {
                name : 'email',
                type : 'string'
            } ]
        });

        this.cm = Ext.create('Ext.grid.ColumnModel', {
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.login'),
                dataIndex : 'identifier',
                width : 100
            }, {
                header : i18n.get('label.firstName'),
                dataIndex : 'firstName',
                width : 100
            }, {
                header : i18n.get('label.lastName'),
                dataIndex : 'lastName',
                width : 250
            }, {
                header : i18n.get('label.email'),
                dataIndex : 'email',
                width : 200
            } ]
        });

        this.bbar = {
            xtype : 'paging',
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
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };
//        this.view = Ext.create('Ext.grid.View', {
//            forceFit : true
//        });

        this.listeners = {
            scope : this, 
            rowDblClick : this._onModify
        };
        this.callParent(arguments);
//        sitools.admin.usergroups.UserCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load users from the store. 
     */
    onRender : function () {
        sitools.admin.usergroups.UserCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Open a {sitools.admin.usergroups.UserPropPanel} userPropertyPanel to create a new user
     */
    _onCreate : function () {
        var up = new sitools.admin.usergroups.UserPropPanel({
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.USER);
        // return Ext.Msg.alert(i18n.get('label.information'),
        // i18n.get('msg.notavailable'));
    },

    /**
     * Open a {sitools.admin.usergroups.UserPropPanel} userPropertyPanel to modify an user
     */
    _onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.usergroups.UserPropPanel({
            url : this.url + '/' + rec.id,
            action : 'modify',
            store : this.getStore()
        });
        up.show(ID.BOX.USER);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    _onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('userCrud.delete'),
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
        Ext.Ajax.request({
            url : this.url + "/" + rec.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                }

                this.store.reload();
                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get(Json.message),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
            },
            failure : alertFailure
        });
    }

});

