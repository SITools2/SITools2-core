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
 * A Panel retrieving all the groups in Sitools2
 * 
 * @cfg {String} the mode (select or list) to load groups
 * @cfg {data} the groups data from the record
 * @class sitools.admin.usergroups.GroupsPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.GroupsPanel', { 
    extend : 'Ext.Window', 
	alias : 'widget.s-groups',
	width : 500,
    height : 350,
    modal : true,
    closable : false,
    pageSize : 10,
    layout : 'fit',

    initComponent : function () {
        
        this.title = this.mode == 'list' ? i18n.get('label.groups') : i18n.get('label.selectGroups');
        
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
        
        this.grid = Ext.create('Ext.grid.Panel', {
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : 'MULTI'
            }),
            store : this.store,
            height : 200,
            forceFit : true,
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ {
                    text : i18n.get('label.add'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                    handler : this._onCreate
                }, {
                    text : i18n.get('label.remove'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                    handler : this._onDelete
                }, '->', {
                    xtype : 's-filter',
                    hidden : this.mode == 'list',
                    emptyText : i18n.get('label.search'),
                    store : this.store,
                    pageSize : this.pageSize
                } ]
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
        
        this.items = [this.grid];
        
        this.buttons = [{
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
        
        sitools.admin.usergroups.GroupsPanel.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load groups from the store. 
     */ 
    onRender : function () {
        sitools.admin.usergroups.GroupsPanel.superclass.onRender.apply(this, arguments);
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
     * Create a new {sitools.admin.usergroups.GroupsPanel} group Panel and retrieve all the groups
     */
    _onCreate : function () {
        var up = Ext.create('sitools.admin.usergroups.GroupsPanel', {
            mode : 'select',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_SECURITY_URL') + '/groups?media=json',
            storeref : this.store
        });
        up.show(this);
    },

    /**
     * Delete the selected group from the selected role
     */
    _onDelete : function () {
        var recs = this.grid.getSelectionModel().getSelection();
        if (!recs) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        this.store.remove(recs);
    },

    /**
     * Gets the selected groups and add them to the list groups of the current parent object
     */
    _onAdd : function () { // sub window -> no action
        var recs = this.grid.getSelectionModel().getSelection();
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
     * Save the groups of the current role
     */
    _onOK : function () {
        var putObject = this.data;
        putObject.groups = [];
        this.store.each(function (record) {
            var resource = {
                id : record.data.name
            };
            putObject.groups.push(resource);
        });
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            scope : this,
            jsonData : putObject,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    return false;
                }
                this.close();
                this.store.reload();
            },
            failure : alertFailure
        });
    },

    /**
     * Destroy the {sitools.admin.usergroups.GroupsPanel} group Panel
     */
    _onCancel : function () {
        this.destroy();
    }

});