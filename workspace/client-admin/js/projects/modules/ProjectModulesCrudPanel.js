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
Ext.namespace('sitools.admin.projects.modules');

/**
 * A Panel to show all the project modules in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the project modules data
 * @class sitools.admin.projects.modules.ProjectModulesCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.projects.modules.ProjectModulesCrudPanel', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-projectmodule',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.PROJECTMODULE,
    selModel : Ext.create('Ext.selection.RowModel',{
        mode : 'SINGLE'
    }),
    pageSize : 10,
    forceFit : true,

    initComponent : function () {
        // url = '/sitools/projectModules'
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_MODULES_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            remoteSort : true,
            url : this.url,
//            idProperty : null,
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
                name : 'author',
                type : 'string'
            }, {
                name : 'version',
                type : 'string'
            }, {
                name : 'url',
                type : 'string'
            }, {
                name : 'imagePath',
                type : 'string'
            }, {
                name : 'defaultWidth',
                type : 'string'
            }, {
                name : 'defaultHeight',
                type : 'string'
            }, {
                name : 'x',
                type : 'string'
            }, {
                name : 'y',
                type : 'string'
            }, {
                name : 'xtype',
                type : 'string'
            }, {
                name : 'specificType',
                type : 'string'
            }, {
                name : 'dependencies',
                type : 'string'
            }, {
				name : "priority", 
				type : "integer"
            }]
        });

        this.columns = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [{
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
                width : 300,
                sortable : false
            }, {
                header : i18n.get('label.xtype'),
                dataIndex : 'xtype',
                width : 350,
                sortable : false
            }]
        });

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
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
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
        sitools.admin.projects.modules.ProjectModulesCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load project modules informations from the store. 
     */
    onRender : function () {
        sitools.admin.projects.modules.ProjectModulesCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    /**
     * Open a {sitools.admin.projects.modules.ProjectModulePropPanel} project property panel
     *  to create a new project module
     */
    _onCreate : function () {
        var dbp = new sitools.admin.projects.modules.ProjectModulePropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        dbp.show(ID.PROP.PROJECTMODULE);
    },

    /**
     * Open a {sitools.admin.projects.modules.ProjectModulePropPanel} project property panel
     *  to modify an existing project module
     */
    _onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }

        var dbp = new sitools.admin.projects.modules.ProjectModulePropPanel({
            url : this.url + '/' + rec.get("id"),
            action : 'modify',
            store : this.store
        });
        dbp.show(ID.PROP.PROJECTMODULE);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    _onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }

        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : String.format(i18n.get('projectModulesCrud.delete'), rec.data.name),
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
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.get("id"),
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }

});

