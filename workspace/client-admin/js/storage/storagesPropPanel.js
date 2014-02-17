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
/*
 * @include "../def.js"
 */

Ext.namespace('sitools.admin.storages');

/**
 * A window that displays Storages properties.
 * 
 * @cfg {string} url The url to Save the data
 * @cfg {string} action The action should be modify or create
 * @cfg {Ext.data.Store} store The storages store 
 * @class sitools.admin.storages.storagesPropPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.storages.storagesPropPanel', { extend : 'Ext.Window',
	alias : 'widget.s-storagesprop',
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
    dataSets : "",
    id : ID.COMPONENT_SETUP.STORAGE,

    initComponent : function () {
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyStorage');
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createStorage');
        }
        this.items = [ {
            xtype : 'panel',
            height : 450,
            items : [ {
                xtype : 'panel',
                height : 410,
                title : i18n.get('label.storageInfo'),
                items : [ {
                    xtype : 'form',
                    border : false,
                    padding : 10,
                    items : [ {
                        xtype : 'hidden',
                        name : 'id'
                    }, {
                        xtype : 'textfield',
                        name : 'name',
                        fieldLabel : i18n.get('label.name'),
                        anchor : '100%',
                        maxLength : 30, 
                        allowBlank : false
                    }, {
                        xtype : 'textfield',
                        name : 'description',
                        fieldLabel : i18n.get('label.description'),
                        anchor : '100%',
                        maxLength : 100
                    }, {
                        xtype : 'textfield',
                        name : 'localPath',
                        fieldLabel : i18n.get('label.localPath'),
                        allowBlank : false,
                        anchor : '100%',
                        maxLength : 100
                    }, {
                        xtype : 'textfield',
                        name : 'attachUrl',
                        fieldLabel : i18n.get('label.attachUrl'),
                        anchor : '100%',
                        maxLength : 100, 
                        vtype : "attachment", 
                        allowBlank : false
                    }, {
                        xtype : 'checkbox', 
                        fieldLabel: i18n.get('label.deeplyAccessible'),
                        name: 'deeplyAccessible'
                    }, {
                        xtype : 'checkbox', 
                        fieldLabel: i18n.get('label.listingAllowed'),
                        name: 'listingAllowed'
                    }, {
                        xtype : 'checkbox', 
                        fieldLabel: i18n.get('label.modifiable'),
                        name: 'modifiable'
                    }]
                } ]
            }],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        } ];
        sitools.admin.storages.storagesPropPanel.superclass.initComponent.call(this);
    },
    /**
     * Validate the modification. 
     * Call an ajax request with method depending on action. 
     * @return {Boolean}
     */
    onValidate : function () {
        var f = this.findByType('form')[0].getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var putObject = {};
        Ext.iterate(f.getFieldValues(), function (key, value) {
            putObject[key] = value;
        }, this);

        if (putObject.localPath.match("file:///") == null) {
            putObject.localPath = "file:///" + putObject.localPath;
        }

        if (this.action == 'modify') {

            Ext.Ajax.request({
                url : this.url,
                method : 'PUT',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (showResponse(ret)) {
                        this.close();
                        this.store.reload();
                    }
                    
                },
                failure : alertFailure
            });
        }
        if (this.action == 'create') {
            Ext.Ajax.request({
                url : this.url,
                method : 'POST',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (showResponse(ret)) {
                        this.close();
                        this.store.reload();
                    }
                },
                failure : alertFailure
            });
        }

    },
	/**
	 * Load the selected storage in case of modification.
	 */
    onRender : function () {
        sitools.admin.storages.storagesPropPanel.superclass.onRender.apply(this, arguments);
        if (this.url) {
            // var gs = this.groupStore, qs = this.quotaStore;
            if (this.action == 'modify') {
                Ext.Ajax.request({
                    url : this.url,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var f = this.findByType('form')[0].getForm();
                        var data = Ext.decode(ret.responseText).directory;

                        
                        var record = new Ext.data.Record(data);

                        f.loadRecord(record);
                    },
                    failure : function (ret) {
                        var data = Ext.decode(ret.responseText);
                        Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                    }
                });
            }
        }
    }

});

