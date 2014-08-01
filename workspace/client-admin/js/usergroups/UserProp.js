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
 * A Panel user data from a specific user
 * 
 * @cfg {String} the url where get the user
 * @cfg {String} the action to perform
 * @cfg {Ext.data.JsonStore} the store where saved the user data
 * @class sitools.admin.usergroups.UserProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.UserProp', { 
    extend : 'Ext.Window',
	alias : 'widget.s-userprop',
	id: 'winCreateUser',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    layout : 'fit',
    // quotaStore : new Ext.data.JsonStore({
    // fields: [
    // {name: 'vol', type: 'string'},
    // {name: 'used', type: 'float'},
    // {name: 'quota', type: 'float'},
    // {name: 'unit', type: 'string'},
    // {name: 'quota_enabled', type: 'boolean'}
    // ]}),
    //
    // joinCheck : new Ext.grid.CheckColumn({dataIndex: 'join', header:
    // i18n.get('header.add')}),
    // quotaCheck : new Ext.grid.CheckColumn({dataIndex: 'quota_enabled',
    // header: i18n.get('header.enabledquota')}),

    initComponent : function () {
        
//        if (this.action == "create") {
//            this.title = i18n.get('label.createUser');
//        } else {
//            this.title = i18n.get('label.modifyUser');
//        }
        
        this.title = i18n.get('label.userInfo');

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
        
        var storeScope = Ext.create("Ext.data.ArrayStore", {
            fields : ['scope'],
            data : [ [ 'Editable' ], [ 'ReadOnly' ], [ 'Hidden' ] ]
        });
        
        var columns =  [ {
            header : i18n.get('headers.name'),
            dataIndex : 'name',
            sortable : false,
            width : 100,
            editor : {
                xtype : 'textfield',
                allowBlank : false
            }
        }, {
            header : i18n.get('headers.value'),
            dataIndex : 'value',
            sortable : false,
            width : 100,
            editor : {
                xtype : 'textfield',
                allowBlank : false
            }
        },
        {
            header : i18n.get('headers.scope'),
            dataIndex : 'scope',
            sortable : false,
            width : 100,
            editor : {
                xtype : 'combo',
                typeAhead : true,
                triggerAction : 'all',
                lazyInit : false,
                queryMode : 'local',
                forceSelection : true,
                valueField : 'scope',
                displayField : 'scope',
                store : storeScope
            }
        }];
        
        var tbar = Ext.create("Ext.Toolbar", {
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
        
        this.cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1,
            pluginId : 'cellEditing'
        });

        this.gridProperties = Ext.create('Ext.grid.Panel', {
            title : i18n.get('title.properties'),
            id : 'userGridProperties',
            store : storeProperties,
            tbar : tbar,
            minHeight : 160,
            columns : columns,
            padding : '5 5 5 5',
            forceFit : true,
            selModel : smProperties = Ext.create('Ext.selection.RowModel',{
                mode : 'SINGLE'
            }),
            plugins : [this.cellEditing],
            flex : 1
        });
        
        this.items = [{
//            title : i18n.get('label.userInfo'),
            xtype : 'panel',
            padding : 5,
            layout : {
                type :'vbox',
                align : 'stretch'
            },
            padding : '5 5 5 5',
            border : false,
            bodyBorder : false,
            autoScroll : true,
            items : [{
                height : 'auto',
                xtype : 'form',
                id: 'formCreateUser',
                border : false,
                bodyBorder : false,
                padding : 10,
                defaults : {
                    labelWidth : 150
                },
                items : [ {
                    xtype : 'textfield',
                    name : 'firstName',
                    fieldLabel : i18n.get('label.firstName'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'lastName',
                    fieldLabel : i18n.get('label.lastName'),
                    anchor : '100%',
                    growMax : 400,
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'email',
                    fieldLabel : i18n.get('label.email'),
                    vtype: 'uniqueemail',
                    allowBlank: false,
                    validationEvent : '',
                    anchor : '100%'
                }, {
                    xtype : 'textfield',
                    name : 'identifier',
                    fieldLabel : i18n.get('label.login'),
                    anchor : '100%',
                    allowBlank : false, 
                    disabled : this.action == "create" ? false : true,
                    vtype : "name", 
                    id : "nameField"
                }, {
                    xtype : 'textfield',
                    fieldLabel : i18n.get('label.password'),
                    anchor : '100%',
                    inputType : 'password',
                    name : 'secret',
                    id : "passwordField", 
                    vtype: 'passwordComplexity',
                    allowBlank : false
                }, {
                    id : "confirmSecret",
                    xtype : 'textfield',
                    fieldLabel : i18n.get('label.confirmPassword'),
                    anchor : '100%',
                    inputType : 'password',
                    initialPassField: 'passwordField',
                    vtype: 'password',
                    name : 'confirmSecret',
                    submitValue : false,
                    allowBlank : false
                }, {
                    xtype : 'checkbox',
                    name : 'generate',
                    fieldLabel : i18n.get('label.generatePassword'),
                    checked : false,
                    listeners : {
                        scope : this,
                        change : function (checkbox, checked) {
                            var f = this.down('form').getForm();
                            f.findField('secret').setDisabled(checked);
                            f.findField('confirmSecret').setDisabled(checked);
                            if(checked) {
                                f.findField('secret').setValue("");
                                f.findField('confirmSecret').setValue("");
                            }
                        } 
                    }
                }]
            }, this.gridProperties],
        }];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onModifyOrCreate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        sitools.admin.usergroups.UserProp.superclass.initComponent.call(this);
    },
    
    getUserAsJson : function() {
    	var f = this.down('form').getForm();
    	if (!f.isValid()) {
    		return null;
    	}
    	return f.getValues();
    },
    /**
     * Create a new record to add a property to the current user
     */
    onCreateProperties : function () {
        this.gridProperties.getStore().insert(this.gridProperties.getStore().getCount(), {});
        
        var rowIndex = this.gridProperties.getStore().getCount() -1;
        
        this.gridProperties.getView().focusRow(rowIndex);
        
        this.gridProperties.getPlugin('cellEditing').startEditByPosition({
            row: rowIndex, 
            column: 0
        });
    },
    
    /**
     * Delete the selected user property
     */
    onDeleteProperties : function () {
        var recs = this.gridProperties.getSelectionModel().getSelection();
        if (Ext.isEmpty(recs)) {
            popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
            return;
        }
        this.gridProperties.getStore().remove(recs);
    },

    /**
     * Create or save an user properties
     */
    onModifyOrCreate : function () {
        var method = (this.action == "create") ? "POST" : "PUT";

        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }
        var putObject = f.getValues();
        Ext.destroyMembers(putObject, "generate");
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
            method : method,
            scope : this,
            jsonData : putObject,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                this.close();
                //wait for the server to refresh authorizations
                Ext.defer(this.store.load, 1000, this);
                // Ext.Msg.alert(i18n.get('label.information'),
                // i18n.get('msg.uservalidate'));
            },
            failure : alertFailure
        });
    },

    /**
     * done a specific render to load informations from the user. 
     */
    onRender : function () {
        sitools.admin.usergroups.UserProp.superclass.onRender.apply(this, arguments);
        if (this.url) {
            // var gs = this.groupStore, qs = this.quotaStore;
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var f = this.down('form').getForm();
                    var data = Ext.decode(ret.responseText);
                    if (data.user !== undefined) {
                        f.setValues(data.user);
                        f.findField('identifier').setValue(data.user.identifier);
						if (!Ext.isEmpty(data.user.properties)) {
							Ext.each(data.user.properties, function (property) {
				                var rec = {
				                    name : property.name,
				                    value : property.value,
				                    scope : property.scope
				                };
				                this.gridProperties.getStore().add(rec);
							}, this);
						}
                    }
                },
                failure : alertFailure
            });
        }
    },
    
    /**
     * Generate a random password
     */
    generatePassword : function () {
        var generator = new PasswordGenerator();
        var randomstring = generator.generate(10);
        console.log(randomstring);
        
        var f = Ext.getCmp('formCreateUser').getForm();
        f.findField('secret').setValue(randomstring);
        f.findField('confirmSecret').setValue(randomstring);

    }
    

});

