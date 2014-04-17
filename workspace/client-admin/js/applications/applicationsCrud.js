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
Ext.namespace('sitools.admin.applications');

/**
 * A Panel to display all sitools Applications. 
 * @class sitools.admin.applications.applicationsCrudPanel
 * @extends Ext.grid.GridPanel
 * @requires sitools.admin.applications.applicationsPropPanel
 * @requires sitools.admin.applications.applicationsRolePanel
 */
Ext.define('sitools.admin.applications.applicationsCrudPanel', { 
    extend : 'Ext.grid.Panel',
    alias : 'widget.s-applications',
	border : false,
    height : 300,
    id : ID.BOX.GROUP,
    forceFit : true,
    pageSize : 10,
    mixins : {
        utils : 'js.utils.utils'
    },

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_APPLICATIONS_URL');
        this.urlAuthorizations = loadUrl.get('APP_URL') + loadUrl.get('APP_AUTHORIZATIONS_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : false,
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.url,
                limitParam : undefined,
                startParam : undefined,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'colId'
                }
            },
            model : 'ApplicationModel',
            sorters : [ {
                property : 'category',
                direction : 'ASC'
            }, {
                property : 'name',
                direction : 'ASC'
            } ],
            groupField: 'category'
        });
        
        this.columns = [{
            text: 'Name',
            flex: 1,
            dataIndex: 'name'
        }, {
            text: 'Status',
            flex: 1,
            dataIndex: 'status',
            renderer : function (value, meta, record, index, colIndex, store) {
                meta.tdCls += value;
                return value;
            }
        }, {
            xtype: 'actioncolumn',
            width: 30,
            dataIndex : "url",
            sortable : false,
            items : [{
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/wadl.gif',
                tooltip : i18n.get('WADL'),
                scope : this,
                handler : function (view, rowIndex, colIndex, item, e, record, row) {
                    onClickOption(record.data.url);
                }
            }]
        }];

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.details'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
                handler : this.onDetails,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.authorizations'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_autorizations.png',
                handler : this.onDefineRole,
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
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store
            } ]
        };
        
        this.features = [{
            ftype:'grouping',
            groupHeaderTpl: '{columnName}: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})',
            startCollapsed : true,
            enableGroupingMenu: false
        }];
        
        sitools.admin.applications.applicationsCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * Called when user click on Authorizations button
     * Open a {sitools.admin.applications.applicationsRolePanel} window.
     * @return {}
     */
    onDefineRole : function () {
        var rec = this.getLastSelectedRecord();
        var up = new sitools.admin.applications.applicationsRolePanel({
            urlAuthorizations : this.urlAuthorizations + "/" + rec.data.id,
            applicationRecord : rec
        });
        
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        up.show(ID.BOX.APPLICATION);
    },

    /**
     * Called when user click on Details button
     * Open a {sitools.admin.applications.applicationsPropPanel} window.
     * @return {}
     */
    onDetails : function () {
        var rec = this.getLastSelectedRecord();
        var up = new sitools.admin.applications.applicationsPropPanel({
            applicationRecord : rec
        });
        
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
//            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        up.show(ID.BOX.APPLICATION);
    },

	/**
	 * Called when delete Button is pressed :
	 * Ask for confirmation and call doDelete 
	 * @return {Boolean}
	 */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        
        if (!rec) {
            return false;
        }
        
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : Ext.String.format(i18n.get('applicationsCrud.delete'), rec.data.name),
            scope : this,
            fn : function (btn, text) {
                if (btn === 'yes') {
                    this.doDelete(rec);
                }
            }
        });
    },
    /**
     * Send a delete request to the server with the application url. 
     * @param {} rec
     */
    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",  
                        Ext.String.format(i18n.get('label.' + jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png');
                
                if (jsonResponse.success) {
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
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '?action=start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",
                        Ext.String.format(i18n.get('label.' + jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png');
                
                if (jsonResponse.success) {
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
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '?action=stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",  
                        Ext.String.format(i18n.get('label.' + jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }

});



