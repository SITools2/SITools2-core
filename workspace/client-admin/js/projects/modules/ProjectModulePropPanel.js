/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * A Panel to show project module properties from a specific project
 * 
 * @cfg {String} the url where get the resource
 * @cfg {String} the action to perform (modify or create)
 * @cfg {Ext.data.JsonStore} the store where get the record
 * @class sitools.admin.projects.modules.ProjectModulePropPanel
 * @extends Ext.Window
 */
//sitools.component.projects.modules.ProjectModulePropPanel = Ext.extend(Ext.Window, {
sitools.admin.projects.modules.ProjectModulePropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 540,
    modal : true,
    id : ID.PROP.PROJECTMODULE,
    layout : 'fit',
    initComponent : function () {
        if (this.action == 'create') {
            this.title = i18n.get('label.createProjectModule');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyProjectModule');
        }
        
        var storeDependencies = new Ext.data.JsonStore({
            fields : [ {
                name : 'url',
                type : 'string'
            }],
            autoLoad : false
        });
        
        var tbar = {
			xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateDependencies
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteDependencies
            }]
        };
        
        this.gridDependencies = new Ext.grid.EditorGridPanel({
            title : i18n.get('title.dependencies'),
            height : 180,
            store : storeDependencies,
            tbar : tbar,
            cm : new Ext.grid.ColumnModel({
                columns : [ {
                    header : i18n.get('label.url'),
                    dataIndex : 'url',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                } ]
            }),
            sm : new Ext.grid.RowSelectionModel({
                singleSelect : true
            }),
            viewConfig : {
                forceFit : true
            }
        });
        
        
        this.formPanel = new Ext.form.FormPanel({
            title : i18n.get('label.projectModuleInfo'),
            border : false,
            labelWidth : 150,
            padding : 10,
            items : [ {
                    xtype : 'textfield',
                    name : 'id',
                    hidden : true
                }, {
                    xtype : 'textfield',
                    name : 'name',
                    fieldLabel : i18n.get('label.name'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'description',
                    fieldLabel : i18n.get('label.description'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'label',
                    fieldLabel : i18n.get('headers.label'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'author',
                    fieldLabel : i18n.get('label.author'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'version',
                    fieldLabel : i18n.get('label.version'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'title',
                    fieldLabel : i18n.get('label.form.title'),
                    anchor : '100%', 
                    allowBlank : true
                }, {
                    xtype : 'numberfield',
                    name : 'defaultWidth',
                    fieldLabel : i18n.get('label.width'),
                    anchor : '100%', 
                    allowBlank : false,
                    allowDecimals : false,
                    allowNegative : false
                }, {
                    xtype : 'numberfield',
                    name : 'defaultHeight',
                    fieldLabel : i18n.get('label.height'),
                    anchor : '100%', 
                    allowBlank : false,
                    allowDecimals : false,
                    allowNegative : false
                }, {
                    xtype : 'textfield',
                    name : 'icon',
                    fieldLabel : i18n.get('label.iconClass'),
                    anchor : '100%', 
                    allowBlank : true
                }, {
                    xtype : 'numberfield',
                    name : 'x',
                    fieldLabel : i18n.get('label.x'),
                    anchor : '100%', 
                    allowBlank : false,
                    allowDecimals : false,
                    allowNegative : false
                }, {
                    xtype : 'numberfield',
                    name : 'y',
                    fieldLabel : i18n.get('label.y'),
                    anchor : '100%', 
                    allowBlank : false,
                    allowDecimals : false,
                    allowNegative : false
                },  {
                    xtype : 'textfield',
                    name : 'xtype',
                    fieldLabel : i18n.get('label.xtype'),
                    anchor : '100%', 
                    allowBlank : false
                }, {
                    xtype : 'spinnerfield',
                    name : 'priority',
                    id : 'priorityId', 
                    fieldLabel : i18n.get('label.priority'),
                    minValue : 0,
                    maxValue : 10,
                    allowDecimals : false,
                    incrementValue : 1,
                    accelerate : true,
                    anchor : "50%", 
                    allowBlank : false
                }
            ]
        });
            
        
        
        this.tabPanel = new Ext.TabPanel({
            activeTab : 0,
            items : [ this.formPanel, this.gridDependencies ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this._onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        });
        
        this.items = [this.tabPanel];
        
        
        sitools.admin.projects.modules.ProjectModulePropPanel.superclass.initComponent.call(this);
    },
    
    /**
     * done a specific render to load project modules properties. 
     */
    onRender : function () {
        sitools.admin.projects.modules.ProjectModulePropPanel.superclass.onRender.apply(this, arguments);
        if (this.action == 'modify') {
            var f = this.findByType('form')[0].getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.projectModule);
                    
                    var dependencies = data.projectModule.dependencies;
                    var storeDependencies = this.gridDependencies.getStore();
                    if (!Ext.isEmpty(dependencies.js)) {
                        Ext.each(dependencies.js, function (item) {
                            storeDependencies.add(new Ext.data.Record(item));
                        }, this);
                    }
                    if (!Ext.isEmpty(dependencies.css)) {
                        Ext.each(dependencies.css, function (item) {
                            storeDependencies.add(new Ext.data.Record(item));
                        }, this);
                    }
                    
                },
                failure : alertFailure
            });
        }
    },

    /**
     * Save project modules properties for a specific project module
     */
    _onValidate : function () {
        var frm = this.findByType('form')[0].getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var met = this.action == 'modify' ? 'PUT' : 'POST';
        var jsonObject = frm.getFieldValues();
            
        jsonObject.dependencies = {};
        jsonObject.dependencies.js = [];
        jsonObject.dependencies.css = [];
        this.gridDependencies.getStore().each(function (item) {
            if (!Ext.isEmpty(item.data.url)) {
                if (item.data.url.indexOf(".css") != -1) {
                    jsonObject.dependencies.css.push({
                        url : item.data.url
                    });
                }
                if (item.data.url.indexOf(".js") != -1) {
                    jsonObject.dependencies.js.push({
                        url : item.data.url
                    });
                }
            }
        });
        
        
        Ext.Ajax.request({
            url : this.url,
            method : met,
            scope : this,
            jsonData : jsonObject,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    return false;
                }
                //load the scripts defined in this component. 
//				includeJs(frm.items.get('url').getValue());
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });
    },
    
    /**
     * Add a new Record to the dependencies property of a project module
     */
    onCreateDependencies : function () {
        var e = new Ext.data.Record();
        this.gridDependencies.getStore().insert(this.gridDependencies.getStore().getCount(), e);
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteDependencies : function () {
        var s = this.gridDependencies.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.gridDependencies.getStore().remove(r);
        }
    }

});

Ext.reg('s-projectmoduleprop', sitools.admin.projects.modules.ProjectModulePropPanel);
