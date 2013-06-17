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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.component.applications');

/**
 * A window to display roles and add any selected roles to a parameter store. 
 * @cfg {Ext.data.JsonStore} storeRolesApplication The store to add records. 
 * @class sitools.component.applications.rolesPanel
 * @extends Ext.Window
 */
sitools.component.applications.rolesPanel = Ext.extend(Ext.Window, {
    // url + mode + storeref
    width : 350,
    modal : true,
    closable : false,
    pageSize : 10,

    /**
     * 
     */
    initComponent : function () {
        this.title = i18n.get('label.roleWin');

        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            autoSave : false,
            idProperty : 'id',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_ROLES_URL'),
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
        this.grid = new Ext.grid.GridPanel({
            sm : new Ext.grid.RowSelectionModel(),
            store : this.store,
            height : 200,
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description'
            } ]
        });
        this.items = [ {
            xtype : 'panel',
            title : i18n.get('label.selectRoles'),
            items : [ this.grid ],
            bbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ '->', {
                    text : i18n.get('label.ok'),
                    handler : this._onOK
                }, {
                    text : i18n.get('label.cancel'),
                    handler : this._onCancel
                } ]
            }
        } ];
        // this.relayEvents(this.store, ['destroy', 'save', 'update']);
        sitools.component.applications.rolesPanel.superclass.initComponent.call(this);
    },

    /**
     * Loads the store 
     */
    onRender : function () {
        sitools.component.applications.rolesPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            scope : this,
            params : {
                start : 0,
                limit : this.pageSize
            },
            callback : function (r, options, success) {
                if (!success) {
                    this.close();
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.loadError'));
                }
            }
        });
    },
	/**
	 * Called when button ok is pressed. 
	 * will add the selected roles in the storeRolesApplication. 
	 */
    _onOK : function () {
        Ext.each(this.grid.getSelectionModel().getSelections(), function (role) {
            if (this.storeRolesApplication.find('role', role.data.name) == -1) {
                this.storeRolesApplication.add(new Ext.data.Record({
                    role : role.data.name
                }));
            }
        }, this);
        this.close();
    },
	/**
	 * Called when button cancel is pressed. 
	 */
    _onCancel : function () {
        this.destroy();
    }

});
