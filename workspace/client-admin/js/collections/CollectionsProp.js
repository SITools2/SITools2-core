/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @include "../id.js" 
 * @include "../projects/datasetsWin.js"
 */
Ext.namespace('sitools.admin.collections');

/**
 * Create, or Edit a Collection 
 * @cfg {String} urlCollections the url to request the collection,
 * @cfg {string} action should be "modify", "create"
 * @cfg {Ext.data.Store} store the store that contains all collections
 * @class sitools.admin.collections.CollectionsProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.collections.CollectionsProp', { 
    extend :'Ext.Window', 
	alias : 'widget.s-collectionsprop',
	width : 700,
    height : 580,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    dataSets : "",
	allModulesDetached : false, 
	allModulesInvisible : false, 
	mixins : {
        utils : "sitools.admin.utils.utils"
    },
	
    initComponent : function () {
        var action = this.action;
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyCollection');            
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createCollection');
        }

        var storeDataSets = Ext.create("Ext.data.JsonStore", {
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'mediaType',
                type : 'string'
            }, {
                name : 'visible',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'properties'
            }, {
                name : 'url',
                type : 'string'
            } ]
        });

        var smDataSets = Ext.create('Ext.selection.RowModel', {
            mode : 'MULTI'
        });
        var tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.add'),
                hidden : this.mode == 'select',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this._onAttachDataset
            }, {
                text : i18n.get('label.remove'),
                hidden : this.mode == 'select',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this._onDeleteDataset
            } ]
        };

        /**
         * {Ext.grid.EditorGridPanel} gridDataSets The grid that displays datasets
         */
        this.gridDataSets = Ext.create("Ext.grid.GridPanel", {
            id : 'gridDataSets',
            flex : 1, 
            title : i18n.get('title.gridDataSets'),
            store : storeDataSets,
            padding : 10,
            tbar : tbar,
            columns : [{
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 400
            }, {
                header : i18n.get('headers.description'),
                dataIndex : 'description',
                width : 400
            }],
            selModel : smDataSets,
            forceFit : true,
            listeners : {
				"activate" : function () {
					if (action == 'view') {
						this.getEl().mask();
					}
				}
            }
        });

        /**
         * {Ext.FormPanel} formCollection The main Form 
         */
        this.formCollection = Ext.create("Ext.FormPanel", {
            title : i18n.get('label.CollectionInfo'),
            border : false,
            bodyBorder : false,
            padding : 10,
            trackResetOnLoad : true,
            defaults : {
                padding : 5
            },
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
                anchor : '100%'
            }]
        });
        
        
        /**
         * {Ext.Panel} mainPanel the main Item of the window
         */
        this.mainPanel = Ext.create("Ext.Panel", {
//            height : 550,
            layout : {
                type : "vbox",
                align : "stretch"
            },
            items : [this.formCollection, this.gridDataSets],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate, 
                hidden : this.action == "view"
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]        
        });
        this.items = [this.mainPanel];
		this.listeners = {
			scope : this, 
			resize : function (window, width, height) {
				var size = window.body.getSize();
				this.mainPanel.setSize(size);
			}

        };        
        this.callParent(arguments);
    },
    /**
     * Create a {sitools.admin.projects.DatasetsWin} datasetWindow to add datasets
     */
    _onAttachDataset : function () {
        var up = Ext.create("sitools.admin.projects.DatasetsWin", {
            mode : 'select',
            url : loadUrl.get('APP_URL') + '/datasets',
            storeDatasets : this.gridDataSets.getStore()
        });
        up.show(this);

    },
    /**
     * Delete a selected Dataset
     * @return {}
     */
    _onDeleteDataset : function () {
        var recs = this.getLastSelectedRecord(this.gridDataSets);
        if (recs.length === 0) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        this.gridDataSets.getStore().remove(recs);
    },
    /**
     * Check the validity of form
     * and call onSaveCollection method
     * @return {Boolean}
     */
    onValidate : function () {
		var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
		this.onSaveCollection();
	},
    /**
     * Build the object to save collection. 
     * Then call a PUT or POST request (depending on action) to save collection.
     */        
    onSaveCollection : function () {
        var f = this.down('form').getForm();
        var putObject = {};
        Ext.iterate(f.getValues(), function (key, value) {
            putObject[key] = value;
        }, this);
        
        var store = this.down('grid').getStore();
        if (store.getCount() > 0) {
            putObject.dataSets = [];
            store.each(function (record) {
                putObject.dataSets.push({
                    id : record.data.id,
                    description : record.data.description,
                    name : record.data.name,
                    mediaType : 'DataSet',
                    type : 'DataSet',
                    visible : record.data.visible, 
                    status : record.data.status, 
                    properties : record.data.properties, 
                    url : record.data.url
                });
            });
        }

        var method = (this.action == 'modify') ? "PUT" : "POST";
		
        Ext.Ajax.request({
			url : this.urlCollections,
			method : method,
			scope : this,
			jsonData : putObject,
			success : function (ret) {
				var data = Ext.decode(ret.responseText);
				if (data.success === false) {
					Ext.Msg.alert(i18n.get('label.warning'), i18n
									.get(data.message));
				} else {
					this.close();
					this.store.reload();
				}
				// Ext.Msg.alert(i18n.get('label.information'),
				// i18n.get('msg.uservalidate'));
			},
			failure : alertFailure
		});
        

    },
	/**
	 * do a specific render to fill informations from the collection. 
	 */
    onRender : function () {
        this.callParent(arguments);
        if (this.urlCollections) {
            // var gs = this.groupStore, qs = this.quotaStore;
            if (this.action == 'modify') {
                Ext.Ajax.request({
                    url : this.urlCollections,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var f = this.down('form').getForm();
                        var grid = this.down('grid');
                        var store = grid.getStore();
                        var data = Ext.decode(ret.responseText).collection;
                        var dataSets = data.dataSets;


                        // Chargement des dataSets disponible et mise a jour de
                        Ext.each(dataSets, function (dataSet) {
                            var rec = {};
                            rec.id = dataSet.id;
                            rec.name = dataSet.name;
                            rec.description = dataSet.description;
                            rec.type = dataSet.description;
                            rec.mediaType = dataSet.mediaType;  
                            rec.status = dataSet.status;
                            rec.visible = dataSet.visible;
                            rec.properties = dataSet.properties;
                            rec.url = dataSet.url;

                            store.add(rec);
                        });
                        // ceuw attaches au projet

                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                         
                        f.setValues(rec);
                    },
                    failure : function (ret) {
                        var data = Ext.decode(ret.responseText);
                        Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                    }
                });
            }
            
        }
        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this.onValidate();
            }
        }, this);
    }
});

