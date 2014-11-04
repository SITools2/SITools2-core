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
Ext.namespace('sitools.component.projects');

sitools.component.projects.projectsCrudPanel = Ext.extend(Ext.grid.EditorGridPanel, {
    border : false,
    height : 300,
    id : ID.BOX.PROJECTS,
    pageSize : 10,

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
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            url : this.url,
            remoteSort : true,
            idProperty : 'id',
            fields : [ {
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
            } ]
        });

        this.sm = new Ext.grid.RowSelectionModel();
        
        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 120,
                sortable : true
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
                width : 260
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 150
            }, {
                header : i18n.get('label.maintenance'),
                dataIndex : 'maintenance',
                width : 50, 
                 
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
                editor : new Ext.form.TextField({
                    listeners : {
                        scope : this,
                        change : function (textfield, newValue, oldValue) {
                            this.savePropertiesBtn.addClass('not-save-textfield');
                            this.savePropertiesBtn.hasToBeSaved = true;
                        }
                    }
                })
            }, {
                header : i18n.get('label.projectPriority') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'priority',
                width : 50,
                sortable : true,
                tooltip : i18n.get('label.projectPriorityHelp'),
                editor : new Ext.form.NumberField({
                    minValue: 0,
                    maxValue: 100,
                    allowDecimals: false,
                    allowNegative : false,
                    listeners : {
                        scope : this,
                        change : function (textfield, newValue, oldValue) {
                            this.savePropertiesBtn.addClass('not-save-textfield');
                            this.savePropertiesBtn.hasToBeSaved = true;
                        }
                    }
                })
            }]
        });

        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
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
                                var oek;
                            },
                            scope : this
                        });
                        return false;
                        
                    }
                }
            }
        };

        this.savePropertiesBtn = new Ext.Button({
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
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
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
            }, '-', {
                text : i18n.get('label.startmaintenance'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png',
                handler : this._onStartMaintenance,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.stopmaintenance'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png',
                handler : this._onStopMaintenance,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.duplicate'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/presentation1.png',
                handler : this.onDuplicate
            }, this.savePropertiesBtn,
            // { text: i18n.get('label.members'), icon:
            // 'res/images/icons/toolbar_group_add.png', handler: this.onMembers
            // },
            '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };
        this.view = new Ext.grid.GridView({
            forceFit : true
        });

        this.listeners = {
            scope : this, 
//            rowDblClick : this.onModify,
            celldblclick : function (grid, row, col) {
                if (grid.getColumnModel().isCellEditable(col, row)) {
                    return;
                }
                this.onModify();
            }
        };
        sitools.component.projects.projectsCrudPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.component.projects.projectsCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    onCreate : function () {
        var up = new sitools.component.projects.ProjectsPropPanel({
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.PROJECTS);
    },

    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
//        if (rec.data.status == i18n.get('status.active')) {
//            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
//            return;
//        }
        var up = new sitools.component.projects.ProjectsPropPanel({
            url : this.url + '/' + rec.id,
            action : rec.data.status == 'ACTIVE' ? "view" : "modify",
            store : this.getStore(),
            projectName : rec.data.name, 
            projectAttachement : rec.data.sitoolsAttachementForUsers,
            propertiesToKeep : {
            	priority : rec.data.priority,
            	categoryProject : rec.data.categoryProject            
            }
        });
        up.show(ID.BOX.PROJECTS);
    },
    /**
     * Handler to delete a project
     * @return {Boolean} false if there is an error while deleting the project
     */
    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('projectCrud.delete'),
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
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    },
    _onStartMaintenance : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/startmaintenance',
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
    _onStopMaintenance : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/stopmaintenance',
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

    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/start',
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

    _onDisactive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/stop',
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
    
    onDuplicate : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.component.projects.ProjectsPropPanel({
            projectUrlToCopy : this.url + "/" + rec.id,
            url : this.url,
            action : 'duplicate',
            store : this.getStore()
        });
        up.show(ID.BOX.DATASETS);
    },
    
    /**
     * Save priority and category for current page projects
     */
    onSaveProperties : function () {
        var pageProjects = this.getStore().data.items;
        
        var putObject = {};
        putObject.minimalProjectPriorityList = [];
        Ext.each(pageProjects, function (project) {
            if (Ext.isEmpty(project.data.categoryProject) && Ext.isEmpty(project.data.priority)) {
                return;
            }
            var minimalRec = {
                    id : project.id,
                    categoryProject : project.data.categoryProject,
                    priority : project.data.priority
                };
            putObject.minimalProjectPriorityList.push(minimalRec);
        }, this);
        
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (ret) {
                this.savePropertiesBtn.removeClass('not-save-textfield');
                this.savePropertiesBtn.hasToBeSaved = false;
                
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.priorityCategoryProjectSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                
                this.store.reload();
            },
            failure : alertFailure
        });
    }

});

Ext.reg('s-projects', sitools.component.projects.projectsCrudPanel);
