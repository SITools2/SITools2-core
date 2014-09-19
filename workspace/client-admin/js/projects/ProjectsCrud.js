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
Ext.namespace('sitools.admin.projects');

Ext.define('sitools.admin.projects.ProjectsCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-projects',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.PROJECTS,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    
    requires : ['sitools.admin.projects.ProjectsProp'],

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');
        /*
         * // The new DataWriter component. var writer = new
         * Ext.data.JsonWriter({ encode: false // <-- don't return encoded JSON --
         * causes Ext.Ajax#request to send data using jsonData config rather
         * than HTTP params });
         */
        // create the restful Store
        // Method url action
        // POST /groups create
        // GET /groups read
        // PUT /groups/id update
        // DESTROY /groups/id delete
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            pageSize : this.pageSize,
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
                name : 'name',
                type : 'string'
            }, {
                name : 'image',
                type : 'string',
                mapping : 'image.url'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'maintenance',
                type : 'boolean'
            }, {
                name : 'priority',
                type : 'integer'
            }, {
                name : 'categoryProject',
                type : 'string'
            }]
        });

        this.columns = {
            defaults : {
                sortable : false
            },
            items : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.image'),
                dataIndex : 'image',
                width : 50,
                renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                    if (!Ext.isEmpty(value)) {
                        value = '<img src="' + value + '" height=15 width=18 style="margin:auto; display: block;"/>';
                    }
                    return value;
                }
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 70,
                renderer : function (value, meta, record, index, colIndex, store) {
                    meta.tdCls += value;
                    return value;
                }
            }, {
                header : i18n.get('label.maintenance'),
                dataIndex : 'maintenance',
                width : 70, 
                 
                renderer: function (value) {
					if (value) {
						return '<div class="sitools-maintenance">&nbsp;</div>';
					}
					else {
						return '<div>&nbsp;</div>';
					}
                }
            }, {
                header : i18n.get('label.projectCategory') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'categoryProject',
                width : 100,
                sortable : true,
                tooltip : i18n.get('label.projectCategoryHelp'),
                editor : {
                	xtype : 'textfield',
                    listeners : {
                        scope : this,
                        change : function (textfield, newValue, oldValue) {
                            this.savePropertiesBtn.addCls('not-save-textfield');
                            this.savePropertiesBtn.hasToBeSaved = true;
                        }
                    }
                }
            }, {
                header : i18n.get('label.projectPriority') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'priority',
                width : 50,
                sortable : true,
                tooltip : i18n.get('label.projectPriorityHelp'),
                editor : {
                	xtype : 'numberfield',
                    minValue: 0,
                    maxValue: 100,
                    allowDecimals: false,
                    allowNegative : false,
                    listeners : {
                        scope : this,
                        change : function (textfield, newValue, oldValue) {
                            this.savePropertiesBtn.addCls('not-save-textfield');
                            this.savePropertiesBtn.hasToBeSaved = true;
                        }
                    }
                }
            }]
        };

        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })];
        
        this.bbar = {
            xtype : 'pagingtoolbar',
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty'),
            listeners : {
                scope : this,
                beforechange : function (paging, start, limit) {
                    if (this.savePropertiesBtn.hasToBeSaved) {
                        Ext.Msg.show({
                            title : i18n.get('label.warning'),
                            msg : i18n.get('label.warning'),
                            icon : Ext.MessageBox.WARNING,
                            buttons : Ext.MessageBox.YESNO,
                            closable : false,
                            start : start,
                            limit : limit,
                            fn : function (btnId, text) {
                            },
                            scope : this
                        });
                        return false;
                    }
                }
            }
        };

        this.savePropertiesBtn = Ext.create('Ext.button.Button', {
            text : i18n.get('label.saveProperties'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
            handler : this.onSaveProperties,
            tooltip : i18n.get('label.savePropertiesHelp'),
            xtype : 's-menuButton',
            hasToBeSaved : false
        });
        
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            enableOverflow : true,
            items : [ {
                text : i18n.get('label.create'),
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
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            }, '-', {
                text : i18n.get('label.startmaintenance'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
                handler : this._onStartMaintenance,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.stopmaintenance'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
                handler : this._onStopMaintenance,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.duplicate'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/presentation1.png',
                handler : this.onDuplicate
            }, this.savePropertiesBtn, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify,
            celldblclick : function (grid, row, col) {
//                if (grid.getColumnModel().isCellEditable(col, row)) {
//                    return;
//                }
                this.onModify();
            }
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : "SINGLE"
        });
        
        this.callParent(arguments);
    },

    onRender : function () {
    	this.callParent(arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    onCreate : function () {
        var up = Ext.create('sitools.admin.projects.ProjectsProp', {
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.PROJECTS);
    },

    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
        }
//        if (rec.data.status == i18n.get('status.active')) {
//            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
//            return;
//        }
        var up = Ext.create('sitools.admin.projects.ProjectsProp', {
            url : this.url + '/' + rec.data.id,
            action : rec.data.status == 'ACTIVE' ? "view" : "modify",
            store : this.getStore(),
            projectName : rec.name, 
            projectAttachement : rec.sitoolsAttachementForUsers
        });
        up.show(ID.BOX.PROJECTS);
    },
    /**
     * Handler to delete a project
     * @return {Boolean} false if there is an error while deleting the project
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('projectCrud.delete'), rec.data.name),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    /**
     * Delete the project with the specified identifier given
     * @param {String} rec the identifier of the project to delete
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
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    },
    _onStartMaintenance : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/startmaintenance',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    _onStopMaintenance : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/stopmaintenance',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    _onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    _onDisactive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                popupMessage("",  
                        Ext.String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    onDuplicate : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create('sitools.admin.projects.ProjectsProp', {
            projectUrlToCopy : this.url + "/" + rec.data.id,
            url : this.url,
            action : 'duplicate',
            store : this.getStore()
        });
        up.show(ID.BOX.PROJECTS);
    },
    
    /**
     * Save priority and category for current page projects
     */
    onSaveProperties : function () {
        var pageProjects = this.getStore().data.items;
        
        var putObject = {};
        putObject.minimalProjectPriorityList = [];
        Ext.each(pageProjects, function (project) {
            if (Ext.isEmpty(project.get('categoryProject')) && Ext.isEmpty(project.get('priority'))) {
                return;
            }
            var minimalRec = {
                    id : project.get('id'),
                    categoryProject : project.get('categoryProject'),
                    priority : project.get('priority')
                };
            putObject.minimalProjectPriorityList.push(minimalRec);
        }, this);
        
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (ret) {
                this.savePropertiesBtn.removeCls('not-save-textfield');
                this.savePropertiesBtn.hasToBeSaved = false;
                
                popupMessage(i18n.get('label.information'), i18n.get('label.priorityCategoryProjectSaved'), null, 'x-icon-information');
                
                this.store.reload();
            },
            failure : alertFailure
        });
    }

});

