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
 showHelp*/
/*
 * @include "../id.js"
 * @include "databasetest.js"
 */
Ext.namespace('sitools.admin.datasource.jdbc');

/**
 * A panel to view, modify databases config
 * @requires sitools.admin.datasource.jdbc.DataBaseTest
 * @cfg {string} url the url to request the database
 * @cfg {string} action the action to perform should be view, modify or create
 * @cfg {Ext.data.Store} store the store that contains all databases. 
 * @class sitools.admin.datasource.jdbc.DataBasePropPanel
 * @extends Ext.Window
 */
sitools.admin.datasource.jdbc.DataBasePropPanel = Ext.extend(Ext.Window, {
//sitools.admin.datasource.DataBasePropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    id : ID.COMPONENT_SETUP.DATABASE,

    initComponent : function () {
        if (this.action == 'create') {
            this.title = i18n.get('label.createDatabase');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyDatabase');
        } else if (this.action == 'view') {
            this.title = i18n.get('label.viewDatabase');
        }
        this.items = [ {
            xtype : 'panel',
            layout : "fit", 
            title : i18n.get('label.databaseInfo'),
            items : [ {
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
                        data : [ [ 'org.gjt.mm.mysql.Driver', 'MySQL' ], [ 'org.postgresql.Driver', 'PostgreSQL' ]                                         ]
                    }),
                    valueField : 'code',
                    displayField : 'label',
                    anchor : "50%"
                }, {
                    name : 'url',
                    fieldLabel : i18n.get('label.url'), 
                    validator : function (value) {
						var driverValue = Ext.getCmp('driverDatasourceId').getValue();
						if (Ext.isEmpty(driverValue)) {
							return "The Driver is empty";
						}
						else {
							switch (driverValue) {
							case 'org.gjt.mm.mysql.Driver' :
								if (value.match("mysql")) {
									return true;
								} else {
									return "The driver doesn't match with this url";
								}
								break;
							case 'org.postgresql.Driver' :
								if (value.match("postgresql")) {
									return true;
								} else {
									return "The driver doesn't match with this url";
								}
								break;
							}
						}
						return true;
                    }
                }, {
                    name : 'schemaOnConnection',
                    fieldLabel : i18n.get('label.schemaOnConnection'),
                    allowBlank : true
                }, {
                    name : 'sitoolsAttachementForUsers',
                    fieldLabel : i18n.get('label.userAttach'), 
                    vtype : "attachment"
                }, {
                    name : 'userLogin',
                    fieldLabel : i18n.get('label.userLogin'), 
                    vtype : "withoutSpace"
                }, {
                    fieldLabel : i18n.get('label.userPassword'),
                    inputType : 'password',
                    name : 'userPassword', 
                    vtype : "withoutSpace"
                },  {
                    xtype : 'spinnerfield',
                    name : 'maxActive',
                    id : 'maxActiveId', 
                    fieldLabel : i18n.get('label.maxActive'),
                    minValue : 0,
                    maxValue : 20,
                    allowDecimals : false,
                    incrementValue : 1,
                    accelerate : true,
                    anchor : "50%", 
                    value : 10,
                    validator : function (value) {
						var initialSizeValue = Ext.getCmp("initialSizeId").getValue();
						if (Ext.isEmpty(initialSizeValue)) {
							initialSizeValue = 0;
						}
						if (Ext.isEmpty(value)) {
							return i18n.get('label.nullValue');
						}
						if (initialSizeValue > value) {
							return String.format(i18n.get('label.dbMaxActiveError'), value, initialSizeValue);
						}
						else {
							return true;
						}
                    }
                }, {
                    xtype : 'spinnerfield',
                    name : 'initialSize',
                    id : 'initialSizeId', 
                    fieldLabel : i18n.get('label.initialSize'),
                    minValue : 0,
                    maxValue : 20,
                    allowDecimals : false,
                    incrementValue : 1,
                    accelerate : true,
                    anchor : "50%", 
                    value : 5,
                    validator : function (value) {
						var maxActiveValue = Ext.getCmp("maxActiveId").getValue();
						if (Ext.isEmpty(maxActiveValue)) {
							maxActiveValue = 0;
						}
						if (Ext.isEmpty(value)) {
							return i18n.get('label.nullValue');
						}
						if (maxActiveValue < value) {
							return String.format(i18n.get('label.dbMaxActiveError'), maxActiveValue, value);
						}
						else {
							return true;
						}
                    }

                } ]
            } ],
            buttons : [ {
                text : i18n.get('label.testCnx'),
                scope : this,
                handler : this._onTest
            }, {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this._onValidate
            },
            {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
            
        } ];
        this.listeners = {
			scope : this, 
	        resize : function (window, width, height) {
				var size = window.body.getSize();
				this.findByType('panel')[0].setSize(size);
			}
        };
        sitools.admin.datasource.jdbc.DataBasePropPanel.superclass.initComponent.call(this);
    },
    onRender : function () {
        sitools.admin.datasource.jdbc.DataBasePropPanel.superclass.onRender.apply(this, arguments);
        Ext.each(this.findByType('form')[0].items.items, function (item) {
            item.disable();
        }, this);
        this.findByType('panel')[0].buttons[1].disable();
        this.findByType('panel')[0].buttons[0].disable();
        if (this.action == 'modify' || this.action == 'view') {
            var f = this.findByType('form')[0].getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.jdbcdatasource);
                    var tmp = f.isValid();
                },
                failure : alertFailure
            });
        }
        if (this.action == 'modify' || this.action == "create") {
            Ext.each(this.findByType('form')[0].items.items, function (item) {
                item.enable();
            }, this);
            this.findByType('panel')[0].buttons[1].enable();
            this.findByType('panel')[0].buttons[0].enable();
        }
        
    },
    
    _onValidate : function () {
        var frm = this.findByType('form')[0].getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var met = this.action == 'modify' ? 'PUT' : 'POST';
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
        var frm = this.findByType('form')[0].getForm();
        var vals = frm.getFieldValues();
        var dbt = new sitools.admin.datasource.DataBaseTest({
            url : this.url + '/test',
            data : vals
        });
        dbt.show();
    }

});

Ext.reg('s-databaseprop', sitools.admin.datasource.jdbc.DataBasePropPanel);
