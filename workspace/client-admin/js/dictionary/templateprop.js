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
Ext.namespace('sitools.component.dictionary');

Ext.define('sitools.component.dictionary.templatePropPanel', { 
    extend : 'Ext.Window',
	alias : 'widget.s-templateprop',
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
    layout : 'fit',

    initComponent : function () {
        
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyTemplate');
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createTemplate');
        }

        var storeProperty = Ext.create('Ext.data.JsonStore', {
            id : 'storePropertiesSelect',
            proxy : {
                type : 'memory',
                reader : {
                    type : 'json',
                    root : 'property',
                    idProperty : 'name'
                }
            },
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'value',
                type : 'string'
            } ]
        });

        var smProperty = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });

        var tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateProperty
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteProperty
            } ]
        };

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        var gridProperty = Ext.create('Ext.grid.Panel', {
            id : 'gridPropertySelect',
            title : i18n.get('title.gridProperty'),
            store : storeProperty,
            tbar : tbar,
            forceFit : true,
            columns :  [{
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 100,
                editor : {
                    xtype : 'textfield',
                    allowBlank : false
                }
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                width : 150,
                editor : {
                    xtype : 'textfield',
                    allowBlank : true
                }
            }],
            selModel : smProperty,
            plugins : [cellEditing]
        });
        
        this.items = [{
            xtype : 'tabpanel',
            height : 450,
            activeTab : 0,
            items : [{
                    height : 400,
                    title : i18n.get('label.templateInfo'),
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
                        allowBlank : false
                    }, {
                        xtype : 'textfield',
                        name : 'description',
                        fieldLabel : i18n.get('label.description'),
                        anchor : '100%'
                    }]
            }, gridProperty ],
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
        sitools.component.dictionary.templatePropPanel.superclass.initComponent.call(this);
    },

    onUpload : function () {
        // TODO gerer l'upload de fichier.
        Ext.Msg.alert('upload non impl&eacute;ment&eacute;');
    },
    onCreateProperty : function () {
        this.down('gridpanel').getStore().add({});
    },
    onDeleteProperty : function () {
        var grid = this.down('gridpanel');
        var rec = grid.getSelectionModel().getLastSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        grid.getStore().remove(rec);

    },
    onValidate : function () {
        var f, putObject = {}, store, tmp = [], i;
        f = this.down('form').getForm();
		if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        if (this.action == 'modify') {
			Ext.iterate(f.getValues(), function (key, value) {
                if (key == 'image') {
                    // TODO : definir une liste de mediaType et type
                    putObject.image = {};
                    putObject.image.url = value;
                    putObject.image.type = "Image";
                    putObject.image.mediaType = "Image";
                } else {
                    putObject[key] = value;
                }
            }, this);

            store = this.down('gridpanel').getStore();
            if (store.getCount() > 0) {
                putObject.properties = [];
                
                for (i = 0; i < store.getCount(); i++) {
                    putObject.properties.push(store.getAt(i).data);
                }
            }

            Ext.Ajax.request({
                url : this.url,
                method : 'PUT',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    this.close();
                    this.store.reload();
                },
                failure : alertFailure
            });
        }
        if (this.action == 'create') {
            Ext.iterate(f.getValues(), function (key, value) {
                if (key == 'image') {
                    // TODO : definir une liste de mediaType et type
                    putObject.image = {};
                    putObject.image.url = value;
                    putObject.image.type = "Image";
                    putObject.image.mediaType = "Image";
                } else {
                    putObject[key] = value;
                }
            }, this);

            store = this.down('gridpanel').getStore();
            if (store.getCount() > 0) {
                putObject.properties = [];
                for (i = 0; i < store.getCount(); i++) {
                    putObject.properties.push(store.getAt(i).data);
                }
            }
            Ext.Ajax.request({
                url : this.url,
                method : 'POST',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    this.close();
                    this.store.reload();
                    // Ext.Msg.alert(i18n.get('label.information'),
                    // i18n.get('msg.uservalidate'));
                },
                failure : alertFailure
            });
        }

    },

    onRender : function () {
        sitools.component.dictionary.templatePropPanel.superclass.onRender.apply(this, arguments);
        if (this.url) {
            // var gs = this.groupStore, qs = this.quotaStore;
            var i;
            if (this.action == 'modify') {
                Ext.Ajax.request({
                    url : this.url,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var f = this.down('form').getForm();
                        var store = this.down('gridpanel').getStore();

                        var data = Ext.decode(ret.responseText).template;
                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                        
                        f.setValues(rec);

                        if (!data.properties) {
                            return;
                        }

                        var properties;
                        if (typeof data.properties[0] && data.properties[0] instanceof Array) {
                            properties = data.properties[0];
                        } else {
                            properties = data.properties;
                        }
                        for (i = 0; i < properties.length; i++) {
//                            rec = new Ext.data.Record(properties[i]);
//                            store.add(rec);
                            store.add(properties[i]);
                        }

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


