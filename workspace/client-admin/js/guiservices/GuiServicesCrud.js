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
Ext.namespace('sitools.admin.guiservices');

/**
 * A Panel to show all the project modules in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the project modules data
 * @class sitools.admin.projects.modules.ProjectModulesCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.guiservices.GuiServicesCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-guiservices',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.GUISERVICES,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ["sitools.admin.guiservices.GuiServicesStore"/*,
                "sitools.admin.guiservices.GuiServicesProp"*/],

    initComponent : function () {
        
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_GUI_SERVICES_URL');
        
        this.store = Ext.create("sitools.admin.guiservices.GuiServicesStore", {
            url : this.url,
            pageSize : this.pageSize
        });
        
        this.columns = [{
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 150,
            sortable : true,
            renderer : function (value, meta, record) {
                meta.style = "font-weight: bold;";
                return value;
            }
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 250,
            sortable : false
        }, {
            header : i18n.get('label.xtype'),
            dataIndex : 'xtype',
            width : 300,
            sortable : false
        }, {
            xtype : 'checkcolumn',
            header : i18n.get('headers.defaultGuiService'),
            dataIndex : 'defaultGuiService',
            editable : false,
            width : 55,
            processEvent: function () { return false; },
            sortable : false
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
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
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
            itemdblclick : this._onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : "SINGLE"
        });
        
        sitools.admin.guiservices.GuiServicesCrud.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load project modules informations from the store. 
     */
    onRender : function () {
        sitools.admin.guiservices.GuiServicesCrud.superclass.onRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    /**
     * Open a {sitools.admin.projects.modules.ProjectModuleProp} project property panel
     *  to create a new project module
     */
    _onCreate : function () {
        var dbp = Ext.create("sitools.admin.guiservices.GuiServicesProp", {
            action : 'create',
            store : this.store
        });
        dbp.show(ID.PROP.GUISERVICES);
    },

    /**
     * Open a {sitools.admin.projects.modules.ProjectModuleProp} project property panel
     *  to modify an existing project module
     */
    _onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }

        var dbp = Ext.create("sitools.admin.guiservices.GuiServicesProp", {
            action : 'modify',
            store : this.store,
            recordId : rec.data.id
        });
        dbp.show(ID.PROP.GUISERVICES);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    _onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }

        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('guiServicesCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn === 'yes') {
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
        this.store.deleteRecord(rec);
    }

});

