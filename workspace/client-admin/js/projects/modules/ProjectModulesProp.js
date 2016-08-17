/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @class sitools.admin.projects.modules.ProjectModulesProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.projects.modules.ProjectModulesProp', { 
    extend : 'Ext.Window', 
	alias : 'widget.s-projectmoduleprop',
    width : 700,
    height : 540,
    modal : true,
    id : ID.PROP.PROJECTMODULE,
    layout : 'fit',

    initComponent : function () {
        if (this.action === 'create') {
            this.title = i18n.get('label.createProjectModule');
        } else if (this.action === 'modify') {
            this.title = i18n.get('label.modifyProjectModule');
        }
        
        this.formPanel = Ext.create("Ext.form.FormPanel", {
            padding : 10,
            border : false,
            bodyBorder : false,
            defaults : {
                labelWidth : 150
            },
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
                    xtype : 'numberfield',
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

        this.items = [this.formPanel];

        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this._onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];

        this.callParent(arguments);
    },
    
    /**
     * done a specific render to load project modules properties. 
     */
    onRender : function () {
        this.callParent(arguments);
        if (this.action == 'modify') {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.projectModule);
                },
                failure : alertFailure
            });
        }

        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this._onValidate();
            }
        }, this);
    },

    /**
     * Save project modules properties for a specific project module
     */
    _onValidate : function () {
        var frm = this.down('form').getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var met = this.action === 'modify' ? 'PUT' : 'POST';
        var jsonObject = frm.getFieldValues();
            
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
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });
    }
});

