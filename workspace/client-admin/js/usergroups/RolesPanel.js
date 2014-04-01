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
 * A Panel retrieving sitools2 roles
 * 
 * @cfg {String} the mode (select or list) to load roles
 * @cfg {Ext.data.Record} the project record
 * @class sitools.admin.usergroups.RolesPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.RolesPanel', { 
    extend : 'Ext.Window',
	alias : 'widget.s-roles',
	width : 500,
    height : 350,
    modal : true,
    closable : false,
    pageSize : 10,
    layout : 'fit',

    initComponent : function () {
        
        this.title = this.mode == 'list' ? i18n.get('label.roles') : i18n.get('label.selectRoles');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            autoSave : false,
            idProperty : 'name',
            url : this.url,
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
            xtype : 'grid',
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : 'MULTI'
            }),
            store : this.store,
            height : 200,
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description'
            }],
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ {
                    text : i18n.get('label.add'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                    handler : this._onCreate
                }, {
                    text : i18n.get('label.remove'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                    handler : this._onDelete
                }, '->', {
                    xtype : 's-filter',
                    hidden : this.mode == 'list',
                    emptyText : i18n.get('label.search'),
                    store : this.store,
                    pageSize : this.pageSize
                } ]
            },
            bbar : {
                xtype : 'pagingtoolbar',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            }
        });
        
        this.items = [this.grid];
            
        this.buttons = [ '->', {
            text : i18n.get('label.add'),
            scope : this,
            handler : this._onAdd,
            hidden : this.mode == 'list'
        }, {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this._onOK,
            hidden : this.mode == 'select'
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : this._onCancel
        }];
        
        sitools.admin.usergroups.RolesPanel.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load roles from the store. 
     */ 
    onRender : function () {
        sitools.admin.usergroups.RolesPanel.superclass.onRender.apply(this, arguments);
        
        if (this.mode == 'select'){
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
        }
        else {
            Ext.each(this.rec.data.listRoles, function (item){
                this.store.add(item);
            }, this);
            
        }
    },

    /**
     * Create a new {sitools.admin.usergroups.RolesPanel} role panel and display all roles
     */
    _onCreate : function () {
        var up = new sitools.admin.usergroups.RolesPanel({
            mode : 'select',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_SECURITY_URL') + '/roles?media=json',
            storeref : this.store
        });
        up.show(this);
    },

    /**
     * Delete the selected role from the store
     */
    _onDelete : function () {
        var rec = this.grid.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        this.store.remove(rec);
    },

    /**
     * Gets the selected roles and add them to the list roles of the current parent object
     */
    _onAdd : function () { // sub window -> no action
        var recs = this.grid.getSelectionModel().getSelections();
        var newrecs = [];
        Ext.each(recs, function (rec) {
            newrecs.push({
                name : rec.data.name,
                description : rec.data.description
            });
        });
        this.storeref.add(newrecs);
        this.close();
    },

    /**
     * Save the groups of the current project
     */
    _onOK : function () {
        var putObject = [];
        this.store.each(function (record) {
            var resource = {
                id : record.data.id,
                name : record.data.name,
                description : record.data.description
            };
            putObject.push(resource);
        });
        this.rec.set('listRoles', putObject);
        this.close();
    },

    /**
     * Destroy the {sitools.admin.usergroups.RolesPanel} role Panel
     */
    _onCancel : function () {
        this.destroy();
    }

});

