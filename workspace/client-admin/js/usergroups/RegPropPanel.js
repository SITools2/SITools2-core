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
Ext.namespace('sitools.admin.usergroups');

/**
 * A Panel to show register data from a specific register
 * 
 * @cfg {String} the url where get the registers
 * @cfg {Ext.data.JsonStore} the store where saved the register data
 * @class sitools.admin.usergroups.RegPropPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.RegPropPanel', { extend : 'Ext.Window',
	alias : 'widget.s-regprop',
    width : 700,
    height : 480,
    modal : true,
    storePanel : null,

    initComponent : function () {
        this.storePanel = this.store;
        this.title = i18n.get('label.modifyRegister');
        
        var storeProperties = new Ext.data.JsonStore({
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'value',
                type : 'string'
            },
            {
                name : 'scope',
                type : 'string'
            }],
            autoLoad : false
        });
        
        var dataScope = [ [ 'Editable' ], [ 'ReadOnly' ], [ 'Hidden' ] ];

        var storeScope = new Ext.data.ArrayStore({
            fields : [ 'scope' ],
            data : dataScope
        });
        
        var smProperties = Ext.create('Ext.selection.RowModel',{
            singleSelect : true
        });

        var cmProperties = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                editor : new Ext.form.TextField({
                    allowBlank : false
                })
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : new Ext.form.TextField({
                    allowBlank : false
                })
            },
            {
                header : i18n.get('headers.scope'),
                dataIndex : 'scope',
                editor : new Ext.form.ComboBox({
                    typeAhead : true,
                    triggerAction : 'all',
                    lazyRender : true,
                    lazyInit : false,
                    mode : 'local',
                    forceSelection : true,
                    valueField : 'scope',
                    displayField : 'scope',
                    store : storeScope
                })
            }],
            defaults : {
                sortable : false,
                width : 100
            }
        });
        var tbar = new Ext.Toolbar({
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateProperties
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteProperties
            }]
        });

        this.gridProperties = new Ext.grid.EditorGridPanel({
            title : i18n.get('title.properties'),
            id : 'userGridProperties',
            height : 180,
            store : storeProperties,
            tbar : tbar,
            cm : cmProperties,
            selModel : smProperties,
            viewConfig : {
                forceFit : true
            }
        });
        this.items = [ {
            xtype : 'panel',
            height : 450,
            items : [ {
                xtype : 'panel',
                title : i18n.get('label.userInfo'),
                items : [ {
                    xtype : 'form',
                    border : false,
                    padding : 10,
                    items : [ {
                        xtype : 'textfield',
                        name : 'id',
                        hidden : true
                    }, {
                        xtype : 'textfield',
                        name : 'identifier',
                        fieldLabel : i18n.get('label.login'),
                        anchor : '100%'
                    }, {
                        xtype : 'textfield',
                        name : 'firstName',
                        fieldLabel : i18n.get('label.firstName'),
                        anchor : '100%'
                    }, {
                        xtype : 'textfield',
                        name : 'lastName',
                        fieldLabel : i18n.get('label.lastName'),
                        anchor : '100%',
                        growMax : 400
                    }, {
                        xtype : 'textfield',
                        name : 'email',
                        fieldLabel : i18n.get('label.email'),
                        anchor : '100%'
                    }, {
                        xtype : 'textfield',
                        fieldLabel : i18n.get('label.password'),
                        anchor : '100%',
                        inputType : 'password',
                        name : 'password', 
                        id : "regPassword"
                    }, {
                        xtype : 'textfield',
                        fieldLabel : i18n.get('label.confirmPassword'),
                        anchor : '100%',
                        inputType : 'password',
                        name : 'confirmPassword',
                        submitValue : false,
                        initialPassField: 'regPassword',
                        vtype : 'password'
                    } ]
                } ]
            }, this.gridProperties ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onModify
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        } ];
        sitools.admin.usergroups.RegPropPanel.superclass.initComponent.call(this);
    },
    onCreateProperties : function () {
        this.gridProperties.getStore().insert(this.gridProperties.getStore().getCount(), {});
    },
    onDeleteProperties : function () {
        var s = this.gridProperties.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.gridProperties.getStore().remove(r);
        }
    },

    onModify : function () {
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }
        var putObject = f.getValues();
        putObject.properties = [];
        this.gridProperties.getStore().each(function (item) {
			putObject.properties.push({
				name : item.data.name, 
				value : item.data.value,
				scope : item.data.scope
			});
        });
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            scope : this,
            jsonData : putObject,
            success : function (ret) {
				var jsonObject = Ext.decode(ret.responseText);
                if (!jsonObject.success) {
					alertFailure(ret);
					this.storePanel.reload();
					return;
                }
                this.close();
                this.storePanel.reload();
            },
            failure : alertFailure
        });
    },

    onRender : function () {
        sitools.admin.usergroups.RegPropPanel.superclass.onRender.apply(this, arguments);
        if (this.url) {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    data.inscription.password = "";
                    f.setValues(data.inscription);
					if (!Ext.isEmpty(data.inscription.properties)) {
						Ext.each(data.inscription.properties, function (property) {
			                var rec = {
			                    name : property.name,
			                    value : property.value,
			                    scope : property.scope
			                };
			                this.gridProperties.getStore().add(rec);
						}, this);
					}
                    
                },
                failure : alertFailure
            });
        }
    }

});
