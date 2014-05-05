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
Ext.namespace('sitools.admin.usergroups');

/**
 * A Panel to show group properties from a specific group
 * 
 * @cfg {String} the action to perform (modify or create)
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where get the record
 * @class sitools.admin.usergroups.GroupProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.usergroups.GroupProp', {
    extend : 'Ext.Window', 
	alias : 'widget.s-groupprop',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    layout : 'fit',
    
    initComponent : function () {
//        if (this.action == "create") {
//            this.title = i18n.get('label.createGroup');
//        } else {
//            this.title = i18n.get('label.modifyGroup');
//        }
        
        this.title = i18n.get('label.groupInfo'),

        this.items = [ {
            xtype : 'panel',
            height : 450,
//            title : i18n.get('label.groupInfo'),
            items : [ {
                xtype : 'form',
                border : false,
                padding : 10,
                items : [ {
                    xtype : 'textfield',
                    name : 'name',
                    fieldLabel : i18n.get('label.name'),
                    anchor : '100%',
                    disabled : this.action != "create", 
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'description',
                    fieldLabel : i18n.get('label.description'),
                    anchor : '100%',
                    growMax : 400
                } ]
            } ]
        } ];

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
        
        sitools.admin.usergroups.GroupProp.superclass.initComponent.call(this);
    },

    /**
     * Create or save a group properties
     */
    onModifyOrCreate : function () {
        var jsonGroup = {};
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }
        Ext.iterate(f.getFieldValues(), function (key, value) {
            jsonGroup[key] = value;
        });
        
        var method = (this.action === "create") ? "POST" : "PUT";
        
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('warning.invalidForm'));
            return;
        }
        Ext.Ajax.request({
            url : this.url,
            method : method,
            scope : this,
            jsonData : jsonGroup,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
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
     * done a specific render to load informations from the group. 
     */
    onRender : function () {
        sitools.admin.usergroups.GroupProp.superclass.onRender.apply(this, arguments);
        if (this.url) {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    if (data.group !== undefined) {
                        f.setValues(data.group);
                    }
                },
                failure : alertFailure
            });
        }
    }

});

