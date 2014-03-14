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
sitools.admin.applications.applicationsCrudPanel = Ext.extend(Ext.grid.GridPanel, {
//sitools.component.applications.applicationsCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.GROUP,
    sm : new Ext.grid.RowSelectionModel(),
    pageSize : 10,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_APPLICATIONS_URL');
        this.urlAuthorizations = loadUrl.get('APP_URL') + loadUrl.get('APP_AUTHORIZATIONS_URL');

        var reader = new Ext.data.JsonReader({
			root : 'data',
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
            }, {
                name : 'category',
                type : 'string'
            }, {
                name : 'urn',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'url',
                type : 'string'
            }, {
                name : 'author',
                type : 'string'
            }, {
                name : 'owner',
                type : 'string'
            }, {
                name : 'lastUpdate',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'wadl',
                type : 'string', 
                convert : function (v, record) {
                }
            } ]
        }); 
        
        this.store = new Ext.data.GroupingStore({
            reader : reader, 
            url : this.url,
            restful : true,
            remoteSort : false,
            sortInfo : {field : 'name', direction : "ASC"},
            groupField : 'category'

        });
        
		var expander = new Ext.ux.grid.RowExpander({
	        tpl : new Ext.XTemplate(
				'<tpl if="this.descEmpty(description)" ><div></div></tpl>', 
				'<tpl if="this.descEmpty(description) == false" ><div class="sitoolsDescription"><div class="sitoolsDescriptionHeader">Description :&nbsp;</div><p class="sitoolsDescriptionText"> {description} </p></div></tpl>', 
				{
					compiled : true, 
					descEmpty : function (description) {
						return Ext.isEmpty(description);
					}
				}), 
			expandOnDblClick : true
	    });
		
        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [expander,  {
                header : i18n.get('label.category'),
                dataIndex : 'category',
                width : 150, 
                hidden : true
            }, {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 500
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 50,
                sortable : false
            }, {
                header : "",
                dataIndex : "url",
                width : 20,
                sortable : false, 
                renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                    var applicationStatus = record.get("status");
                    if (Ext.isEmpty(applicationStatus) || "INACTIVE" == applicationStatus) {
                        return null;
                    } else {
                        return String.format("<a onClick='onClickOption(\"{0}\"); return false;' href=#>{1}</a>", value, String.format(
                                                    "<img alt={0} src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/wadl.gif'>", i18n
                                                            .get('label.wadl')));    
                    }
                    
                    
                }
            }]
        });

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
        this.view = new Ext.grid.GroupingView({
            forceFit : true, 
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})' 
        });

        this.plugins = expander;
        sitools.admin.applications.applicationsCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * Basic on Render and load the applications store...
     */
    onRender : function () {
        sitools.admin.applications.applicationsCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load();
    },

    /**
     * Called when user click on Authorizations button
     * Open a {sitools.admin.applications.applicationsRolePanel} window.
     * @return {}
     */
    onDefineRole : function () {
        var rec = this.getSelectionModel().getSelected(), up = new sitools.admin.applications.applicationsRolePanel({
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
        var rec = this.getSelectionModel().getSelected(), up = new sitools.admin.applications.applicationsPropPanel({
            applicationRecord : rec
        });
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        up.show(ID.BOX.APPLICATION);
    },

//    /**
//     * Called when user click on Details button
//     * Open a {sitools.admin.applications.applicationsPropPanel} window.
//     * @return {}
//     */
//    onModify : function () {
//        var rec = this.getSelectionModel().getSelected(), up = new sitools.admin.applications.applicationsPropPanel({
//            url : this.url + '/' + rec.id,
//            action : 'modify',
//            store : this.getStore()
//        });
//        if (!rec) {
//            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
//        }
//
//        up.show(ID.BOX.APPLICATION);
//    },

	/**
	 * Called when delete Button is pressed :
	 * Ask for confirmation and call doDelete 
	 * @return {Boolean}
	 */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected(), tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : i18n.get('applicationsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn === 'yes') {
                    this.doDelete(rec);
                }
            }

        });
        if (!rec) {
            return false;
        }

    },
    /**
     * Send a delete request to the server with the application url. 
     * @param {} rec
     */
    doDelete : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.id,
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
     * Call the resource start on the application 
     */
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '?action=start',
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
            url : this.url + '/' + rec.id + '?action=stop',
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

Ext.reg('s-applications', sitools.admin.applications.applicationsCrudPanel);


