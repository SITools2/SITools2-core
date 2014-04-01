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
 showHelp*/
/*
 * @include "../id.js"
 * @include "databasetest.js"
 */
Ext.namespace('sitools.admin.datasource.mongoDb');

/**
 * A panel to view, modify databases config
 * @requires sitools.admin.datasource.mongoDb.DataBaseTest
 * @cfg {string} url the url to request the database
 * @cfg {string} action the action to perform should be view, modify or create
 * @cfg {Ext.data.Store} store the store that contains all databases. 
 * @class sitools.admin.datasource.mongoDb.DataBasePropPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasource.mongoDb.DataBasePropPanel', { 
    extend : 'Ext.Window',
	alias : 'widget.s-databaseprop',
    width : 700,
    height : 480,
    modal : true,
    id : ID.COMPONENT_SETUP.DATABASE,
    layout : 'fit',

    initComponent : function () {
//        if (this.action === 'create') {
//			this.title = i18n.get('label.createDatabase');
//		} else if (this.action === 'modify') {
//			this.title = i18n.get('label.modifyDatabase');
//		} else if (this.action === 'view') {
//			this.title = i18n.get('label.viewDatabase');
//		}
        
        this.title = i18n.get('label.databaseInfo');
        
        this.items = [{
            xtype : 'form',
            formId : 'datasourceForm',
            border : false,
            labelWidth : 150,
            padding : 10,
            defaultType : "textfield",
            defaults : {
                anchor : '100%',
                allowBlank : false
            },
            items : [ {
                name : 'id',
                hidden : true,
                allowBlank : true
            }, {
                name : 'name',
                fieldLabel : i18n.get('label.name')
            }, {
                name : 'description',
                fieldLabel : i18n.get('label.description'),
                allowBlank : true
            }, {
                xtype : 'combo',
                id : 'driverDatasourceId',
                mode : 'local',
                triggerAction : 'all',
                editable : false,
                name : 'driverClass',
                fieldLabel : i18n.get('label.driver'),
                width : 100,
                store : new Ext.data.ArrayStore({
                    fields : [ {
                        name : 'code'
                    }, {
                        name : 'label'
                    } ],
                    data : [ [ 'com.mongo.driver', 'MongoDb' ]]
                }),
                valueField : 'code',
                displayField : 'label',
                anchor : "50%", 
                value : "com.mongo.driver"
            }, {
                name : 'url',
                fieldLabel : i18n.get('label.url'), 
                validator : function (value) {
                    var driverValue = Ext.getCmp('driverDatasourceId').getValue();
                    if (Ext.isEmpty(driverValue)) {
                        return "The Driver is empty";
                    }
                    return true;
                }
            }, {
                name : 'sitoolsAttachementForUsers',
                fieldLabel : i18n.get('label.userAttach'), 
                vtype : "attachment"
            }, {
                name : 'databaseName',
                fieldLabel : i18n.get('label.databaseName'), 
                vtype : "withoutSpace"
            }, {
                xtype : "numberfield",
                name : 'portNumber',
                fieldLabel : i18n.get('label.portNumber')
            }, {
                name : 'authentication',
                fieldLabel : i18n.get('label.authentication'), 
                xtype : "checkbox", 
                listeners : {
                    check : function (me, checked) {
                        var f = me.ownerCt.getForm();
                        f.findField("userLogin").setVisible(checked);
                        f.findField("userPassword").setVisible(checked);
                    }
                }
            }, {
                name : 'userLogin',
                allowBlank : true, 
                fieldLabel : i18n.get('label.userLogin')
            }, {
                fieldLabel : i18n.get('label.userPassword'),
                allowBlank : true, 
                inputType : 'password',
                name : 'userPassword'
            },  {
                xtype : "numberfield",
                name : 'maxActive',
                id : 'maxActiveId',
                fieldLabel : i18n.get('label.maxActive'),
                minValue : 0,
                maxValue : 20,
                allowDecimals : false,
                incrementValue : 1,
                accelerate : true,
                anchor : "50%", 
                value : 10
            }]
         }];
        
        this.buttons = [{
            text : i18n.get('label.testCnx'),
            name : 'testConnectionButton',
            scope : this,
            handler : this._onTest
        }, {
            text : i18n.get('label.ok'),
            name : 'okButton',
            scope : this,
            handler : this._onValidate
        },
        {
            text : i18n.get('label.cancel'),
            name : 'cancelButton',
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        this.listeners = {
			scope : this, 
	        resize : function (window, width, height) {
				var size = window.body.getSize();
				this.down('form').setSize(size);
			}
        };
        sitools.admin.datasource.mongoDb.DataBasePropPanel.superclass.initComponent.call(this);
    },
    afterRender : function () {
        sitools.admin.datasource.mongoDb.DataBasePropPanel.superclass.afterRender.apply(this, arguments);
        var frm = this.down('form');
        var basicFrm = frm.getForm();
        Ext.each(frm.items.items, function (item) {
            item.disable();
        }, this);
        
        this.down('button[name=testConnectionButton]').disable();
        this.down('button[name=okButton]').disable();
        
        if (this.action === 'modify' || this.action === 'view') {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.mongodbdatasource);
                    var tmp = f.isValid();
                },
                failure : alertFailure
            });
        }
        else {
            basicFrm.findField("authentication").setValue(true);
        }
        if (this.action === 'modify' || this.action === "create") {
            Ext.each(frm.items.items, function (item) {
                item.enable();
            }, this);
            
            this.down('button[name=testConnectionButton]').enable();
            this.down('button[name=okButton]').enable();
        }
		basicFrm.findField("userLogin").setVisible(basicFrm.findField("authentication").getValue());
		basicFrm.findField("userPassword").setVisible(basicFrm.findField("authentication").getValue());
    },
    
    _onValidate : function () {
        var frm = this.down('form').getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var met = this.action === 'modify' ? 'PUT' : 'POST';
        Ext.Ajax.request({
            url : this.url,
            method : met,
            scope : this,
            jsonData : frm.getFieldValues(),
            success : function (ret) {
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });
    },

    _onTest : function () {
        var frm = this.down('form').getForm();
        var vals = frm.getFieldValues();
        var dbt = new sitools.admin.datasource.DataBaseTest({
            url : this.url + '/test',
            data : vals
        });
        dbt.show();
    }

});

