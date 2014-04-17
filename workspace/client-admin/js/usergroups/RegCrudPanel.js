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
Ext.namespace('sitools.admin.usergroups');

/**
 * A Panel to show all the groups in Sitools2
 * 
 * @cfg {String} the url where get the registers
 * @cfg {Ext.data.JsonStore} the store where saved the register data
 * @class sitools.admin.usergroups.RegCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.usergroups.RegCrudPanel', {
    extend : 'Ext.grid.Panel', 
	alias : 'widget.s-regcrud',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.REG,
    forceFit : true,
    selModel : Ext.create('Ext.selection.RowModel', {
        mode : 'SINGLE'
    }),
    pageSize : 10,
    mixins : {
        utils : 'js.utils.utils'
    },
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_INSCRIPTIONS_ADMIN_URL');
        
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
        // PUT /users/[id] update
        // DESTROY /users/[id] delete
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            proxy : {
                type : 'ajax',
                url : this.url,
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
            }]
        });

        this.columns = [{
            header : i18n.get('label.login'),
            dataIndex : 'identifier',
            width : 100,
            renderer : function (value, meta, record) {
                meta.style = "font-weight: bold;";
                return value;
            }
        }, {
            header : i18n.get('label.firstName'),
            dataIndex : 'firstName',
            width : 100
        }, {
            header : i18n.get('label.lastName'),
            dataIndex : 'lastName',
            width : 100
        }, {
            header : i18n.get('label.email'),
            dataIndex : 'email',
            width : 100
        }];

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
                text : i18n.get('label.validate'),
                icon : 'res/images/icons/icons_perso/toolbar_validate.png',
                handler : this.onValidate,
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
        
        this.callParent(arguments);
    },

    /**
     * done a specific render to load registers from the store. 
     */ 
    onRender : function () {
        sitools.admin.usergroups.RegCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Accept a registration and create an user
     */
    onValidate : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        
        Ext.Ajax.request({
            url : this.url + '/' + rec.get("id"),
            method : 'PUT',
            scope : this,
            success : function (ret) {
                this.store.reload();
            },
            failure : alertFailure
        });
    },

    /**
     * Open a panel to edit properties of the selected user registration
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        
        var up = Ext.create('sitools.admin.usergroups.RegPropPanel', {
            url : this.url + '/' + rec.get("id"),
            store : this.store
        });
        up.show(ID.BOX.REG);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }
        
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('label.regcrud.delete'), rec.get("identifier")),
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
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
                
                if (!jsonResponse.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), jsonResponse.message);
                    return;
                }
            },
            failure : alertFailure
        });
    }
});

