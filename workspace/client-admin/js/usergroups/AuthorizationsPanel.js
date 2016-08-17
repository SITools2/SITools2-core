/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * A Panel retrieving all the authorizations attached to a role
 * 
 * @cfg {String} the mode (select or list) to load groups
 * @cfg {data} the groups data from the record
 * @class sitools.admin.usergroups.GroupsPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.AuthorizationsPanel', {
    extend : 'Ext.window.Window', 
	alias : 'widget.s-roleAuthorizations',
	width : 500,
    height : 350,
    modal : true,
    closable : false,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    layout : 'fit',

    initComponent : function () {
        
        this.title = i18n.get('label.roleAuthorizations');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                reader : {
                    type : 'json',
                    idProperty : 'name',
                    root : 'data'
                }
            },
            fields : [{
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }]
        });
        
        this.label = {
    		xtype : 'label',
    		padding : 10,
            html : Ext.String.format(i18n.get('label.authorizationsAttachedRole'), this.data.name)
        };
        
        this.grid = Ext.create('Ext.grid.Panel', {
            store : this.store,
            height : 200,
            forceFit : true,
            padding : 10,
            border : false,
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [{
                    text : i18n.get('label.removeAllAuthorizations'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                    scope : this,
                    handler : this._onDelete
                }]
            },
            columns : [{
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
        
        this.items = [this.label, this.grid];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this._onOK
        }];

        this.callParent(arguments);
    },

    /**
     * done a specific render to load groups from the store. 
     */ 
    onRender : function () {
        this.callParent(arguments);
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

        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this._onOK();
            }
        }, this);
    },

    /**
     * Delete the selected group from the selected role
     */
    _onDelete : function () {
    	var records = this.grid.getSelectionModel().getSelection();
    	
    	if (Ext.isEmpty(records)) {
    		return popupMessage("", i18n.get('label.noAuthorizationToDelete'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
    	}
    	
    	Ext.Msg.show({
            title : i18n.get('label.delete'),
            icon : Ext.Msg.INFO,
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.removeAllAuthorizationsConfirm'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                	Ext.Ajax.request({
                        url: this.url,
                        method: 'DELETE',
                        scope: this,
                        success : function (ret) {
                            var json = Ext.decode(ret.responseText);
                            popupMessage("", i18n.get(json.message), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
                            if (json.success) {
                            	this.store.removeAll();
                            }
                        }
                    });
                }
            }
        });
    },

    /**
     * Save the groups of the current role
     */
    _onOK : function () {
        this.destroy();
    }
    

});