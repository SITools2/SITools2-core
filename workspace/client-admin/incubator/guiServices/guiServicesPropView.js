/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.guiServices');

sitools.admin.guiServices.guiServicesPropView = Ext.extend(Ext.Window, {
    width : 700,
    height : 540,
    modal : true,
    layout : 'fit',
    sitoolsSelectorType : 'guiServicesPropView',
    
    initComponent : function () {
    	
        if (this.action == 'create') {
            this.title = i18n.get('label.createGuiService');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyGuiService');
        }
        
        var tbar = {
			xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                sitoolsSelectorType : 'guiServicesPropView-button-dependencies-create'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                sitoolsSelectorType : 'guiServicesPropView-button-dependencies-delete'
            }]
        };
        
        this.gridDependencies = new Ext.grid.EditorGridPanel({
            title : i18n.get('title.dependencies'),
            height : 180,
            store : this.store,
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
                    name : 'iconClass',
                    fieldLabel : i18n.get('label.iconClass'),
                    anchor : '100%', 
                    allowBlank : true
                }, {
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
                sitoolsSelectorType : 'guiServicesPropView-button-ok'
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                sitoolsSelectorType : 'guiServicesPropView-button-cancel'
            } ]
        });
        
        this.items = [this.tabPanel];
        
        sitools.admin.guiServices.guiServicesPropView.superclass.initComponent.call(this);

    }
     
});


