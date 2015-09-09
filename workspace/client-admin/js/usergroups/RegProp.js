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
Ext.namespace('sitools.admin.usergroups');

/**
 * A Panel to show register data from a specific register
 * 
 * @cfg {String} the url where get the registers
 * @cfg {Ext.data.JsonStore} the store where saved the register data
 * @class sitools.admin.usergroups.RegProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.RegProp', { extend : 'Ext.Window',
	alias : 'widget.s-regprop',
    width : 700,
    height : 480,
    modal : true,
    storePanel : null,
    layout : 'fit',
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },

    initComponent : function () {
        this.storePanel = this.store;
        this.title = i18n.get('label.modifyRegister');
        
        var storeProperties = Ext.create("Ext.data.JsonStore", {
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

        var storeScope = Ext.create("Ext.data.ArrayStore", {
            fields : [ 'scope' ],
            data : dataScope
        });
        
        var smProperties = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });

        var cmProperties = {
            items : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                editor : Ext.create("Ext.form.TextField", {
                    allowBlank : false
                })
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : Ext.create("Ext.form.TextField", {
                    allowBlank : false
                })
            },
            {
                header : i18n.get('headers.scope'),
                dataIndex : 'scope',
                editor : Ext.create("Ext.form.ComboBox", {
                    typeAhead : true,
                    triggerAction : 'all',
                    lazyInit : false,
                    queryMode : 'local',
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
        };
        var tbar = new Ext.Toolbar({
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreateProperties
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteProperties
            }]
        });

        this.gridProperties = Ext.create("Ext.grid.GridPanel", {
            title : i18n.get('title.properties'),
            height : 180,
            store : storeProperties,
            tbar : tbar,
            columns : cmProperties,
            selModel : smProperties,
            plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1,
                pluginId : 'cellEditing'
            })],
            flex : 1
        });
        
        this.items = [ {
            xtype : 'panel',
            padding : 5,
            border : false,
            bodyBorder : false,
            layout : {
                type : 'vbox',
                align : 'stretch'
            },
            items : [ {
                height : "auto",
                xtype : 'form',
                border : false,
                bodyBorder : false,
                padding : 5,
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
        this.callParent(arguments);
    },
    onCreateProperties : function () {
        this.gridProperties.getStore().insert(this.gridProperties.getStore().getCount(), {});
        
        var rowIndex = this.gridProperties.getStore().getCount() -1;
        
        this.gridProperties.getView().focusRow(rowIndex);

        this.gridProperties.getPlugin('cellEditing').startEditByPosition({
            row: rowIndex, 
            column: 0
        });
    },
    onDeleteProperties : function () {
        var selections = this.gridProperties.getSelectionModel().getSelection();
        Ext.each(selections, function (selection) {
            this.gridProperties.getStore().remove(selection);
        }, this);
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
				name : item.get("name"), 
				value : item.get("value"),
				scope : item.get("scope")
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
        this.callParent();
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
        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this.onModify();
            }
        }, this);
    }

});
