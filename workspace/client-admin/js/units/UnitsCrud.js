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
 showHelp, ann, mainPanel, helpUrl:true, loadUrl*/
Ext.namespace('sitools.admin.units');

/**
 * A Panel to show all the dimensions in Sitools2
 * 
 * @cfg {String} the urlAdmin where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the dimensions data
 * @class sitools.admin.units.unitsCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.units.unitsCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-units',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.UNITS,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    
    requires : ['sitools.admin.units.unitsProp'],
    
    initComponent : function () {
        this.urlAdmin = loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.urlAdmin + "/dimension",
                reader : {
                    type : 'json',
                    root : "data",
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
            }, {
                name : 'dimensionHelperName',
                type : 'string'
            }, {
                name : 'unitConverters'
            }, {
                name : 'unitNames'
            }, {
                name : 'isConsistent',
                type : 'boolean'
            }]
        });

        this.columns = [{
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 150,
            renderer : function (value, meta, record) {
                meta.style = "font-weight: bold;";
                return value;
            }
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 200,
            sortable : false
        }, {
            header : i18n.get('label.dimensionHelperName'),
            dataIndex : 'dimensionHelperName',
            width : 300,
            sortable : false
        }, {
            header : i18n.get('label.isConsistent'),
            dataIndex : 'isConsistent',
            width : 100,
            sortable : false
        }];

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.add'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            } ]
        };

        this.bbar = {
                xtype : 'pagingtoolbar',
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : "SINGLE"
        });
        
        sitools.admin.units.unitsCrud.superclass.initComponent.call(this);

    },

    /**
     * done a specific render to load dimensions from the store. 
     */
    onRender : function () {
        sitools.admin.units.unitsCrud.superclass.onRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    /**
     * Create a new {sitools.admin.units.unitsProp} unit prop to create a new dimension
     */
    onCreate : function () {
        var up = Ext.create("sitools.admin.units.unitsProp", {
            action : 'create',            
            parent : this,          
            urlAdmin : this.urlAdmin
        });
        up.show(ID.BOX.UNITS);
    },

    /**
     * Create a new {sitools.admin.units.unitsProp} unit prop to modify a dimension
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord();

        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.units.unitsProp", {
            action : 'modify',
            record : rec,
            parent : this,
            urlAdmin : this.urlAdmin            
        });
        up.show(ID.BOX.UNITS);

    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('unitsCrud.delete'), rec.data.name),
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
            url : this.urlAdmin + "/dimension/" + rec.data.id,
            method : 'DELETE',
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


