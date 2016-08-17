/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/* global Ext, sitools, i18n,document */
Ext.namespace('sitools.public.userProfile');
/*
 * config { url + handler }
 */
Ext.define('sitools.public.userProfile.editProfile', {
    extend : 'Ext.panel.Panel',
	padding : 10,
	layout : 'fit',
	border : false,
    bodyBorder : false,

    initComponent : function () {
        
        var storeProperties = Ext.create('Ext.data.JsonStore', {
            fields : [{
                name : 'name',
                type : 'string'
            }, {
                name : 'value',
                type : 'string'
            }, {
                name : 'scope',
                type : 'string'
            }]
        });

        var smProperties = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });

        var cmProperties = {
            items : [{
                header : i18n.get('headers.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : {
                    xtype : 'textfield',
                    allowBlank : false
                }
            }],
            defaults : {
                sortable : false,
                width : 100
            }
        };

        this.gridProperties = Ext.create('Ext.grid.Panel', {
            title : i18n.get('title.properties'),
            padding : 5,
            height : 150,
            autoScroll : true,
            clicksToEdit : 1,
            store : storeProperties,
            columns : cmProperties,
            selModel : smProperties,
            forceFit : true,
            plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
            })],
            viewConfig : {
//                getRowClass : function (row, col) {
//                    var data = row.data;
//                    if (data.scope == 'ReadOnly') {
//                        return "row-grid-readOnly";
//                    }
//                }
            },
            listeners : {
                beforeedit : function (editor, object) {
                    var scope = object.record.data.scope;
                    var name = object.field;
                    if (scope == 'ReadOnly' || name == 'name') {
                        return false;
                    }
                }
            }
        });

        this.items = [{
            xtype : 'form',
            border : false,
            bodyBorder : false,
            buttonAlign : 'center',
            padding : 10,
            fieldDefaults : {
                labelWidth : 140
            },
            items : [{
                xtype : 'textfield',
                name : 'identifier',
                fieldLabel : i18n.get('label.login'),
                anchor : '100%',
                allowBlank : false,
                readOnly : true,
                style : {
                    color : '#C0C0C0'
                },
                id : "nameField"
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.firstName'),
                name : 'firstName',
                id : 'regFirstName',
                allowBlank : false,
                anchor : '100%'
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.lastName'),
                name : 'lastName',
                id : 'regLastName',
                allowBlank : false,
                anchor : '100%'
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.password'),
                anchor : '100%',
                inputType : 'password',
                name : 'secret',
                value : '',
                id : "passwordField",
                vtype : 'passwordComplexity'
            }, {
                id : "confirmSecret",
                xtype : 'textfield',
                fieldLabel : i18n.get('label.confirmPassword'),
                anchor : '100%',
                inputType : 'password',
                initialPassField : 'passwordField',
                vtype : 'password',
                name : 'confirmSecret',
                submitValue : false,
                value : ''
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.email'),
                id : 'regEmail',
                name : 'email',
                vtype : 'uniqueemail',
                allowBlank : false,
                validationEvent : '',
                anchor : '100%'
            }, this.gridProperties]
        }];

        this.bbar = {
            xtype : 'toolbar',
            layout : {
                type : 'hbox',
                pack : 'center',
                align : 'middle'
            },
            items : [{
                text : i18n.get('label.saveEdit'),
                scale : 'medium',
                handler : this.saveEdit,
                scope : this
            }]
        };
        
        this.callParent(arguments);
    },

    saveEdit : function () {
        var f = this.down('form').getForm();

        if (!f.isValid()) {
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

        var changePwd = !Ext.isEmpty(putObject.secret);
        
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (response, opts) {
                var json = Ext.decode(response.responseText);
                if (json.success) {
                    this.ownerCt.close();
                    
//                    var notify = new Ext.ux.Notification({
//                        iconCls : 'x-icon-information',
//                        title : i18n.get('label.information'),
//                        html : json.message,
//                        autoDestroy : true,
//                        hideDelay : 1000
//                    });
                    if (changePwd){ 
                        sitools.public.utils.LoginUtils.logout();
                    }
//                    notify.show(document);
                }
                if (this.handler !== null && this.handler !== undefined) {
                    this.handler.call(this.scope || this, putObject);
                }
            },
            failure : function (response, opts) {
                if (response.status == 200) {
                    var ret = Ext.decode(response.responseText).message;
                    Ext.Msg.show({
                        title : i18n.get('msg.error'),
                        msg : ret,
                        buttons : Ext.Msg.OK,
                        icon : Ext.Msg.ERROR
                    });
                } else {
                    Ext.Msg.show({
                        title : i18n.get('warning.serverError'),
                        msg : response.statusText,
                        buttons : Ext.Msg.OK,
                        icon : Ext.Msg.ERROR
                    });
                }
            }
        });
    },

    afterRender : function () {
        this.callParent(arguments);
        if (this.url) {
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var f = this.down('form').getForm();
                    var data = Ext.decode(ret.responseText);
                    if (data.user !== undefined) {
                        f.setValues(data.user);
                        f.findField('secret').setValue('');
                        if (!Ext.isEmpty(data.user.properties)) {
                            Ext.each(data.user.properties, function (property) {
                                var rec ={
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
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }

});
