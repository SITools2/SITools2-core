/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.userProfile');
/*
 * config { url + handler }
 */
sitools.userProfile.editProfile = Ext.extend(Ext.Panel, {
//    id : 'winEditProfile',
//    layout : 'hbox',
//    width : 420,
//    height : 410,
//    resizable : false,
//    closable : true,
//    modal : true,
	padding : "10px 10px 0px 60px",
	frame : true,
	layout : "fit", 

    initComponent : function () {
    	this.bodyStyle = "background-image: url("+loadUrl.get('APP_URL')+"/common/res/images/ux/login-big.gif);" +
    			"background-position: top left;" +
    			"background-repeat: no-repeat;";

//        this.title = i18n.get('label.editProfile');
        this.bbar = new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            id : 'sbWinEditProfile',
            iconCls : 'x-status-valid'
        });

        var storeProperties = new Ext.data.JsonStore({
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'value',
                type : 'string'
            }, {
                name : 'scope',
                type : 'string'
            } ],
            autoLoad : false
        });
        var smProperties = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        var cmProperties = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                editor : new Ext.form.TextField({
                    readOnly : true
                })
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : new Ext.form.TextField({
                    allowBlank : false
                })
            }],
            defaults : {
                sortable : false,
                width : 100
            }
        });

        this.gridProperties = new Ext.grid.EditorGridPanel({
            title : i18n.get('title.properties'),
            id : 'userGridProperties',
            height : 130,
            autoScroll : true,
            clicksToEdit : 1,
            store : storeProperties,
            cm : cmProperties,
            sm : smProperties,
            viewConfig : {
                forceFit : true,
                getRowClass : function (row, col) { 
                    var data = row.data;
                    if (data.scope == 'ReadOnly') {
                        return "row-grid-readOnly"; 
                    }
                } 
            },
            listeners : {
                beforeedit : function (e) {
                    var scope = e.record.data.scope;
                    var name = e.field;
                    if (scope == 'ReadOnly' || name == 'name') {
                        return false;
                    }
                }
            }
        });
        
        this.items = [ {
            xtype : 'form',
            flex : 1, 
            border : false,
            buttonAlign : 'center',
            id : 'frmEditProfile',
            labelWidth : 120,
            items : [ {
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
            }, this.gridProperties ],
            buttons : [ {
                text : i18n.get('label.saveEdit'),
                x : 30,
                handler : this.saveEdit,
                scope : this
            }]
        } ];
        
        sitools.userProfile.editProfile.superclass.initComponent.call(this);
        
    },

    saveEdit : function () {
        var f = Ext.getCmp('frmEditProfile').getForm();

        if (!f.isValid()) {
            Ext.getCmp('sbWinEditProfile').setStatus({
                text : i18n.get('warning.checkForm'),
                iconCls : 'x-status-error'
            });
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

        this.body.mask();
        Ext.getCmp('sbWinEditProfile').showBusy();

        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (response, opts) {
                var json = Ext.decode(response.responseText);
                if (json.success) {
                    this.ownerCt.close();
                    
                    var notify = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : json.message,
                        autoDestroy : true,
                        hideDelay : 1000
                    });
                    notify.show(document);
                } else {
                    Ext.getCmp('winEditProfile').body.unmask();
                    Ext.getCmp('sbWinEditProfile').setStatus({
                        text : json.message,
                        iconCls : 'x-status-error'
                    });

                }
                if (this.handler !== null && this.handler !== undefined) {
                    this.handler.call(this.scope || this, putObject);
                }
            },
            failure : function (response, opts) {
                var txt;
                if (response.status == 200) {
                    var ret = Ext.decode(response.responseText).message;
                    txt = i18n.get('msg.error') + ': ' + ret;
                } else {
                    txt = i18n.get('warning.serverError') + ': ' + response.statusText;
                }
                Ext.getCmp('winEditProfile').body.unmask();
                Ext.getCmp('sbWinEditProfile').setStatus({
                    text : txt,
                    iconCls : 'x-status-error'
                });
            }
        });
    },

    onRender : function () {
        sitools.userProfile.editProfile.superclass.onRender.apply(this, arguments);
        if (this.url) {
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var f = this.findByType('form')[0].getForm();
                    var data = Ext.decode(ret.responseText);
                    if (data.user !== undefined) {
                        f.setValues(data.user);
                        f.findField('secret').setValue('');
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
