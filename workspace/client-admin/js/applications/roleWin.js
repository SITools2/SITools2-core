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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.component.applications');

/**
 * A window to display roles and add any selected roles to a parameter store. 
 * @cfg {Ext.data.JsonStore} storeRolesApplication The store to add records. 
 * @class sitools.component.applications.rolesPanel
 * @extends Ext.Window
 */
Ext.define('sitools.component.applications.rolesPanel', { 
    extend : 'Ext.Window',
    // url + mode + storeref
    width : 500,
    height : 350,
    modal : true,
    closable : false,
    pageSize : 10,
    layout : 'fit',

    initComponent : function () {
        this.title = i18n.get('label.roleWin');

        this.store = Ext.create('Ext.data.JsonStore', {
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_ROLES_URL'),
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }]
        });
        
        this.grid = Ext.create('Ext.grid.Panel', {
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : 'MULTI'
            }),
            store : this.store,
            forceFit : true,
            height : 200,
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description'
            }],
            bbar : {
                xtype : 'pagingtoolbar',
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            }
        });
        
        this.items = [ this.grid ];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            handler : this._onOK,
            scope : this
        }, {
            text : i18n.get('label.cancel'),
            handler : this._onCancel,
            scope : this
            
        }];                
        
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
        Ext.each(this.grid.getSelectionModel().getSelection(), function (role) {
            if (this.storeRolesApplication.find('role', role.data.name) == -1) {
                var record = Ext.create('AuthorizationModel', {
                    role : role.data.name
                });

                this.storeRolesApplication.add(record);
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
