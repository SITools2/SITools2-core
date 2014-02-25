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
 * A Panel user data from a specific user
 * 
 * @cfg {String} the url where get the user
 * @cfg {String} the action to perform
 * @cfg {Ext.data.JsonStore} the store where saved the user data
 * @class sitools.admin.usergroups.UserPropPanel
 * @extends Ext.Window
 */
//sitools.component.usergroups.UserPropPanel = Ext.extend(Ext.Window, {
sitools.admin.usergroups.UserPropPanel = Ext.extend(Ext.Window, {
	id: 'winCreateUser',
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
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
        if (this.action == "create") {
            this.title = i18n.get('label.createUser');
        } else {
            this.title = i18n.get('label.modifyUser');
        }

        // this.joinCheck.header = i18n.get('header.add');
        // this.quotaCheck.header = i18n.get('header.enabledquota');

        // this.groupStore = new Ext.data.JsonStore({
        // root: 'data',
        // restful: true,
        // url:this.url+'/groups',
        // fields: [
        // {name: 'name', type: 'string'},
        // {name: 'description', type: 'string'},
        // {name: 'join', type: 'boolean'},
        // ]});

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
            fields : ['scope'],
            data : dataScope
        });
        
        var smProperties = new Ext.grid.RowSelectionModel({
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
            sm : smProperties,
            viewConfig : {
                forceFit : true
            }
        });
        
        this.items = [ {
            // xtype: 'tabpanel',
            // activeTab: 0,
            // layoutOnTabChange: true,
            // height: 450,
            // items: [
            // {
            xtype : 'panel',
            height : 450,
            title : i18n.get('label.userInfo'),
            items : [ {
                xtype : 'form',
                id: 'formCreateUser',
                border : false,
                padding : 10,
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
                    xtype : 'button',
                    name : 'generate',
                    id : 'generatePass',
                    text : i18n.get('label.generatePassword'),
                    anchor : '9%',
                    handler : this.generatePassword
                } ]
            }, this.gridProperties],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onModifyOrCreate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]

        } ];
        // ,
        // {
        // xtype: 'panel',
        // title: i18n.get('label.userGroups'),
        // items: [
        // {
        // xtype: 'grid',
        // store: this.groupStore,
        // plugins: this.joinCheck,
        // height: 400,
        // columns: [
        // {header: i18n.get('label.name'), dataIndex: 'name', width: 100},
        // {header: i18n.get('label.description'), dataIndex: 'description',
        // width: 400},
        // this.joinCheck
        // ]
        // }
        // ]
        // },
        // {
        // xtype: 'panel',
        // title: i18n.get('label.userQuota'),
        // items: [
        // {
        // xtype: 'editorgrid',
        // store: this.quotaStore,
        // plugins: this.quotaCheck,
        // height: 400,
        // columns: [
        // {xtype: 'gridcolumn', dataIndex: 'vol', header:
        // i18n.get('label.volume'), width: 100, editor: {xtype: 'textfield'}},
        // {xtype: 'numbercolumn', dataIndex: 'used', header:
        // i18n.get('label.usedcapacity'), width: 100, align: 'right', editor:
        // {xtype: 'numberfield'}},
        // {xtype: 'numbercolumn', dataIndex: 'quota', header:
        // i18n.get('label.quota'), width: 100, align: 'right', format:'0',
        // editor: {xtype: 'numberfield'}},
        // {dataIndex: 'unit', header: i18n.get('headers.unit'), align: 'right',
        // width: 100, editor:
        // { xtype:'combo', mode:'local', editable:false, width:30,
        // triggerAction:'all', lazyRender:true,
        // store: new Ext.data.ArrayStore({fields: ['v','l'], data:
        // [['MB','MB'],['GB','GB']] }),
        // valueField: 'v', displayField: 'l' }
        // },
        // this.quotaCheck
        // ]
        // }
        // ]
        // }
        // ],
        // }
        // ];
        sitools.admin.usergroups.UserPropPanel.superclass.initComponent.call(this);
    },
    
    /**
     * Create a new record to add a property to the current user
     */
    onCreateProperties : function () {
        var e = new Ext.data.Record();
        this.gridProperties.getStore().insert(this.gridProperties.getStore().getCount(), e);
    },
    
    /**
     * Delete the selected user property
     */
    onDeleteProperties : function () {
        var s = this.gridProperties.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.gridProperties.getStore().remove(r);
        }
    },

    /**
     * Create or save an user properties
     */
    onModifyOrCreate : function () {
        var method = (this.action == "create") ? "POST" : "PUT";

        var f = this.findByType('form')[0].getForm();
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
                this.store.reload();
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
        sitools.admin.usergroups.UserPropPanel.superclass.onRender.apply(this, arguments);
        if (this.url) {
            // var gs = this.groupStore, qs = this.quotaStore;
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var f = this.findByType('form')[0].getForm();
                    var data = Ext.decode(ret.responseText);
                    if (data.user !== undefined) {
                        f.setValues(data.user);
                        f.findField('identifier').setValue(data.user.identifier);
						if (!Ext.isEmpty(data.user.properties)) {
							Ext.each(data.user.properties, function (property) {
				                var rec = new Ext.data.Record({
				                    name : property.name,
				                    value : property.value,
				                    scope : property.scope
				                });
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

Ext.reg('s-userprop', sitools.admin.usergroups.UserPropPanel);
