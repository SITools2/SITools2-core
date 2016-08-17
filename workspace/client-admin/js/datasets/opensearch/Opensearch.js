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
Ext.namespace('sitools.admin.datasets.opensearch');

/**
 * Define an openSearch on a Dataset.
 * @cfg {String} url (required) The url of the dataset 
 * @cfg {Ext.data.Store} store (required) The store that contains all datasets.
 * @class sitools.admin.datasets.opensearch.Opensearch
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.opensearch.Opensearch', { 
    extend : 'Ext.Window',
	alias : 'widget.s-datasetsOpenSearch',
    width : 800,
    height : 520,
    modal : true,
    resizable : false,
    state : null,
    layout : 'border',
    id : ID.COMPONENT_SETUP.OPENSEARCH, 
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    bodyStyle : 'background: #FFF;',
    
    requires : ['sitools.admin.datasets.opensearch.RelativeLink'],

    initComponent : function () {

        // paramètre métier de la fenêtre
        this.idOpenSearch = null;

        this.title = i18n.get('label.editOpenSearchIndexes');

        /* paramétres du formulaire */
        var itemsForm = [{
            fieldLabel : i18n.get('label.name'),
            name : 'name',
            anchor : '90%',
            maxLength : 16, // name size must be less or equal 16 (opensearch
            // 1.1 specifications)
            allowBlank : false
        }, {
            fieldLabel : i18n.get('label.description'),
            name : 'description',
            anchor : '90%',
            allowBlank : false
        }, {
            xtype : 'sitoolsSelectImage',
            name : 'image',
            fieldLabel : i18n.get('label.image'),
            anchor : '90%',
            growMax : 400
        }, {
            fieldLabel : i18n.get('label.lastImportDate'),
            name : 'lastImportDate',
            anchor : '90%',
            disabled : true
        }];

        this.formPanel = Ext.create("Ext.FormPanel", {
            labelWidth : 100,
            height : 160,
            defaultType : 'textfield',
            items : itemsForm,
            region : 'north',
            border : false,
            bodyBorder : false,
            padding : 10,
            defaults : {
                padding : 3
            }
        });

        var store = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'memory',
                reader : {
                    type :'json',
                    root : 'dataset.columnModel',
                    idProperty : 'id'
                }
            },
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'dataIndex',
                type : 'string'
            }, {
                name : 'width',
                type : 'string'
            }, {
                name : 'sortable',
                type : 'boolean'
            }, {
                name : 'visible',
                type : 'boolean'
            }, {
                name : 'filter',
                type : 'boolean'
            }, {
                name : 'columnOrder',
                type : 'string'
            }, {
                name : 'columnType',
                type : 'string'
            }, {
                name : 'notion',
                type : 'string'
            }, {
                name : 'schema',
                type : 'string'
            }, {
                name : 'tableName',
                type : 'string'
            }, {
                name : 'sqlColumnType',
                type : 'string'
            }, {
                name : "specificColumnType",
                type : 'string'
            }, {
                name : "columnAlias",
                type : "string"
            }, {
                name : 'indexed',
                type : 'boolean'
            }, {
                name : 'returned'
            }, {
                name : 'defaultSearchField',
                type : 'boolean'
            }, {
                name : 'uniqueKey',
                type : 'boolean'
            }, {
                name : 'primaryKey',
                type : 'boolean'
            }, {
                name : 'keyword',
                type : 'boolean'
            }, {
                name : 'solrType',
                type : 'string'
            }, {
                name : 'linkFieldRelative',
                type : 'boolean'
            }]
        });

        var indexed = {
            xtype : 'checkcolumn',
            header : i18n.get('header.indexed'),
            dataIndex : 'indexed',
            width : 60
        };

        var defaultSearchField = {
            xtype : 'checkcolumn',
            header : i18n.get('header.defaultSearchField'),
            dataIndex : 'defaultSearchField',
            width : 120
        };

        var uniqueKey = {
            xtype : 'checkcolumn',
            header : i18n.get('header.uniqueKey'),
            dataIndex : 'uniqueKey',
            width : 80
        };

        var keyWords = {
            xtype : 'checkcolumn',
            header : i18n.get('header.useForKeyWord'),
            dataIndex : 'keyword',
            width : 80
        };

        this.rssFieldData = [ [ 0, "&nbsp;" ], [ 1, 'titleField' ], [ 2, 'linkField' ], [ 3, 'guidField' ], [ 4, 'pubDateField' ], [ 5, 'descriptionField' ] ];

        var returned = {
            header : i18n.get('header.returned'),
            dataIndex : 'returned',
            width : 130,
            scope : this, 
            editor : {
                xtype : 'combo',
                typeAhead : true,
                triggerAction : 'all',
                lazyInit : false,
                queryMode : 'local',
                emptyText : " ",
                // on force la selection pour que l'utilisateur ne puisse pas
                // saisir de valeur au clavier
//                forceSelection : true,
                store : Ext.create('Ext.data.ArrayStore', {
                    fields : [ 'idRssField', 'textRssField' ],
                    data : this.rssFieldData
                }),
                valueField : 'textRssField',
                displayField : 'textRssField',
                listeners : {
                    scope : this, 
                    select : function (combo, records) {
                        if (Ext.isEmpty(records)) {
                            return;
                        }
                        
                        if (combo.getValue() == "" || combo.getValue() == "&nbsp;"){ 
                            combo.setValue(null);
                        }
                        
                        var record = records[0];
                        if (record.data.textRssField == "linkField") {
                            var tmp = Ext.create('sitools.admin.datasets.opensearch.RelativeLink', {
                                selectedRecord : this.getLastSelectedRecord(this.grid)
                            });
                            tmp.show();
                        }
                    }
                }
            }
        };
        
        this.solrTypeData = [ [ 0, "&nbsp;" ], [1, 'text' ], [ 2, 'string' ], [ 3, 'rss_date' ], [ 4, 'date' ] ];

        var solrType = {
            header : i18n.get('headers.solrType'),
            dataIndex : 'solrType',
            width : 130,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/opensearch/solrType.html",
            editor : {
                xtype : 'combo',
                triggerAction : 'all',
                queryMode : 'local',
                emptyText : " ",
                store : Ext.create('Ext.data.ArrayStore', {
                    fields : [ 'idSolrType', 'textSolrType' ],
                    data : this.solrTypeData
                }),
                valueField : 'textSolrType',
                displayField : 'textSolrType'
            },
            listeners : {
                scope : this, 
                select : function (combo, records) {
                    if (combo.getValue() == "" || combo.getValue() == "&nbsp;"){ 
                        combo.setValue(null);
                    }
                }
            }
        };

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        var smColumn = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });
        
        this.grid = Ext.create('Ext.grid.Panel', {
            border : false,
            layout : 'fit',
            pageSize : ADMIN_PANEL_NB_ELEMENTS,
            padding : 2,
            title : 'Fields',
            urlDatasets : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'),
            columns : [{
                header : i18n.get('label.name'),
                id : 'dataIndex',
                dataIndex : 'columnAlias',
                locked : true
            }, {
                header : i18n.get('headers.tableName'),
                id : 'tableName',
                dataIndex : 'tableName',
                locked : true
            }, indexed, returned, defaultSearchField, uniqueKey, keyWords, solrType ],
            selModel : smColumn,
            store : store,
            plugins : [cellEditing],
            enableColumnMove : false,
            region : 'center',
            forceFit : true,
            viewConfig : {
				autoFill : true
//				getRowClass : function (row, index) {
//					var cls = '';
//					var data = row.data;
//					if (!data.indexed && !Ext.isEmpty(data.returned)) {
//						cls = "red-row";
//					}
//					return cls;
//				}
			}
        });
        
        this.items = [ this.formPanel, this.grid ]; 

        this.saveAndQuitButton = Ext.create('Ext.button.Button', {
            text : i18n.get('label.save'),
            scope : this,
            hidden : true
        });
        
        this.refreshStatusButton = Ext.create('Ext.button.Button', {
            text : i18n.get('label.refreshStatus'),
            scope : this,
            hidden : true,           
            handler : function () {
                this._onRefreshStatusHandler();
            }
        });
        
        this.buttonCancel = Ext.create('Ext.button.Button', {
            text : i18n.get('label.cancel'),
            scope : this,
            hidden : true,
            handler : function () {
                this._onCancel();
            }
        });
        
        // ajout des boutons dans la fenetre
        this.buttons = [ this.saveAndQuitButton, this.refreshStatusButton, this.buttonCancel, {
            text : i18n.get('label.close'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];

        // définition des différents boutons en fonction des états
        this.buttonState1 = [{
            text : i18n.get('label.saveIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png',
            handler : this._onSaveIndexHandler,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.help'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/help.png',
            handler : this._onHelp,
            xtype : 's-menuButton'
        } ];

        this.buttonState2 = [{
            text : i18n.get('label.saveIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png',
            handler : this._onUpdateIndexHandler,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.deleteIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
            handler : this._onDeleteIndex,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.activateIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
            handler : this._onActivateIndex,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.help'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/help.png',
            handler : this._onHelp,
            xtype : 's-menuButton'
        }];
        
        this.buttonState3 = [];
        
        this.buttonState4 = [{
            text : i18n.get('label.refreshIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_refresh.png',
            handler : this._onRefreshIndex,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.desactivateIndex'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
            handler : this._onDesactivateIndex,
            xtype : 's-menuButton'
        }, {
            text : i18n.get('label.help'),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/help.png',
            handler : this._onHelp,
            xtype : 's-menuButton'
        }];
        // définition des constantes pour la définition des états
        //edition de l'opensearch
        this.state1 = 1;
        //opensearch créé
        this.state2 = 2;
        //opensearch en cours d'indexation
        this.state3 = 3;
        //opensearch activé
        this.state4 = 4;
        

        // instantiation de la toolbar
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            }
        };
        
        this.buttonbbar = Ext.create('Ext.button.Button', {
            text : i18n.get("label.errorDetails")
        });
        
        this.bbar = Ext.create('Ext.ux.StatusBar', {
            id: 'statusBar',
            hidden : true,
            items : [this.buttonbbar],
            text: i18n.get("label.osErrorLastExe"),
            iconCls: 'x-status-error'                        
        });

        this.grid.getView().addListener("refresh", function () {
            this.applyMask(this.state);
        }, this);

        
        this.callParent(arguments);
    },

    /**
     * @method
     * On render, load the dataset values requesting the Dataset Url . 
     * Call loadModification if success.
     */
    onRender : function () {
        this.callParent(arguments);
        this.objectList = {};

        // sinon on charge le dataset
        Ext.Ajax.request({
            url : this.url,
            method : 'GET',
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                this.idOpenSearch = data.dataset.id;
                var dataIndexList = data.dataset.columnModel;
                var store = this.grid.getStore();
                for (var i = 0; i < dataIndexList.length; i++) {
                    // on stocke l'ensemble des colonnes d'un dataset de
                    // specificColumnType DATABASE
                    if (dataIndexList[i].specificColumnType === "DATABASE" || dataIndexList[i].specificColumnType === "SQL") {
                        store.add(dataIndexList[i]);
                    }
                }
                // on charge les valeurs de l'index s'il y en a déjà un
                this.loadModifications();
            }
        });

    },
    /**
     * @method
     * if exists, load the index values
     * on Success will call updateState, populateForm, populateStore
     */
    loadModifications : function () {

        Ext.Ajax.request({
            url : this.url + loadUrl.get('APP_OPENSEARCH_URL'),
            method : 'GET',
            scope : this,
            // si index existe on le charge
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.information'), "Server error");
                    return false;
                }
                data = data.opensearch;
                this.updateState(data);
                this.populateForm(data);
                this.populateStore(data);

            },
            // si on a une erreur serveur
            failure : alertFailure
        });
    },

    /**
     * Will populate the store of the grid 
     * @param {} data (required) The index request data.
     */
    populateStore : function (data) {

        this.cleanGridInput();
        if (data !== undefined) {
            var i;
            var valueObject;
            var dataColumnIndex = data.indexedColumns;
            if (dataColumnIndex !== undefined) {
                for (i = 0; i < dataColumnIndex.length; i++) {
                    valueObject = this.getObjectFromStore("id", dataColumnIndex[i].idColumn);
                    if (valueObject !== undefined) {
                        valueObject.indexed = true;
                        valueObject.solrType = dataColumnIndex[i].type;
                    }
                }
            }
            var keyword = data.keywordColumns;
            if (keyword !== undefined) {
                for (i = 0; i < keyword.length; i++) {
                    valueObject = this.getObjectFromStore("columnAlias", keyword[i]);
                    if (valueObject !== undefined) {
                        valueObject.keyword = true;
                    }
                }
            }

            // set defaultSearchField value
            valueObject = this.getObjectFromStore("id", data.defaultSearchField);
            if (valueObject !== undefined) {
                valueObject.defaultSearchField = true;
            }

            // set uniqueKey value
            valueObject = this.getObjectFromStore("id", data.uniqueKey);
            if (valueObject !== undefined) {
                valueObject.uniqueKey = true;
            }

            this.setReturnField("titleField", data);
            this.setReturnField("linkField", data);
            this.setReturnField("guidField", data);
            this.setReturnField("pubDateField", data);
            this.setReturnField("descriptionField", data);

            if (keyword !== undefined) {
                for (i = 0; i < keyword.length; i++) {
                    valueObject = this.getObjectFromStore("dataIndex", keyword[i]);
                    if (valueObject !== undefined) {
                        valueObject.keyword = true;
                    }
                }
            }
            this.grid.getView().refresh();
        } 
    },
    /**
     * 
     * @param {} data (required) The index request data.
     */
    updateState : function (data) {
        Ext.getCmp('statusBar').hide();
        if (data !== undefined) {
            // check if the index is activated or not
            if (data.status == "ACTIVE") {
                this.updateButtons(this.state4);
                this.state = this.state4;

            } else if (data.status == "PENDING") {
                this.updateButtons(this.state3);
				this.state = this.state3;
				
            } else {
                this.updateButtons(this.state2);
                this.state = this.state2;
                if (!Ext.isEmpty(data.errorMsg)) {
					/*
					 * Ext.Msg.alert(i18n.get("msg.error"), i18n
					 * .get("label.osErrorLastExe") + "<BR/>" + data.errorMsg + "<BR/>" +
					 * i18n.get("label.checkLogMoreInfo"));
					 */
                    Ext.getCmp('statusBar').show();
					this.buttonbbar.setHandler(function () {
						Ext.Msg.alert(i18n.get("msg.error"),i18n.get("label.osErrorLastExe") + "<BR/>" + data.errorMsg + "<BR/>" 
						        + i18n.get("label.checkLogMoreInfo"));
					});
				}
            }
        } else {
            this.updateButtons(this.state1);
            this.state = this.state1;
        }
    },

    /**
     * load the main form with requested data
     * @param {} data (required) The index request data.
     */
    populateForm : function (data) {
        if (data !== undefined) {
            var form = this.formPanel.getForm();

            var rec = {};
            rec.name = data.name;
            rec.description = data.description;
            if (data.image !== undefined) {
                rec.image = data.image.url;
            }
            if (data.lastImportDate !== undefined) {
                rec.lastImportDate = data.lastImportDate;
            }

            form.setValues(rec);
        }
    },

    /**
     * retourne l'objet de type field et de valeur value présente dans le store
     * retourne undefined si l'objet n'est pas trouve
     * @param {String} field the field to get
     * @param {} value the value to look for
     * @return {} 
     */
    getObjectFromStore : function (field, value) {
        var store = this.grid.getStore();
        var index = store.find(field, value);
        if (index != -1) {
            var valueObject = store.getAt(index);
            return valueObject.data;
        } else {
            return undefined;
        }
    },

    /**
     * 
     * @param {} field
     * @param {} data
     */
    setReturnField : function (field, data) {
        var store = this.grid.getStore();
        if (data[field] !== undefined) {
            var valueObject = this.getObjectFromStore("id", data[field]);
            if (valueObject !== undefined) {
                valueObject.returned = field;
                if (field == "linkField") {
                    valueObject.linkFieldRelative = data.linkFieldRelative; 
                }
            }
        }
    },
    /**
     * met à jour les boutons en fonction des actions de l'utilisateur
     * @param {} state
     */
    updateButtons : function (state) {

        var tb = this.down('toolbar');
        tb.removeAll();
        this.hideLoadingMask();
        this.applyMask(state);    
        
        if (state == this.state1) {
            this.addButtonToToolbar(tb, this.buttonState1);
            this.saveAndQuitButton.setHandler(this._onSaveAndQuitIndexHandler, this);
            this.saveAndQuitButton.show();        
            this.refreshStatusButton.hide();
            this.buttonCancel.hide();
            
        } else if (state == this.state2) {
            this.addButtonToToolbar(tb, this.buttonState2);
            
            this.saveAndQuitButton.show();
            this.saveAndQuitButton.setHandler(this._onUpdateAndQuitIndexHandler, this);
            
            this.refreshStatusButton.hide();
            this.buttonCancel.hide();

        } else if (state == this.state3) {
            this.addButtonToToolbar(tb, this.buttonState2);
            this.saveAndQuitButton.hide();
            this.refreshStatusButton.show();            
            this.buttonCancel.show();
            
        } else if (state == this.state4) {
            this.addButtonToToolbar(tb, this.buttonState4);
            this.saveAndQuitButton.hide();
            this.refreshStatusButton.hide();
            this.buttonCancel.hide();
        } 
    },
    /**
     * Apply a mask on all window
     * @param {} state
     */
    applyMask : function (state) {
		if (state == this.state4) {
//			this.grid.getView().getLockedBody().mask();
			this.grid.setDisabled(true);
//            this.grid.getView().mainBody.mask();
            this.formPanel.getEl().mask();
		} else if (state == this.state3) {
			this.applyLoadingMask();
		} else {
//			this.grid.getView().getLockedBody().unmask();
			this.grid.setDisabled(false);
//            this.grid.getView().mainBody.unmask();
            this.formPanel.getEl().unmask();
		}
	},
    
    /**
     * Apply a loading Mask on grid
     */
    applyLoadingMask : function () {
        this.down('form').getEl().mask();
        this.down('toolbar').getEl().mask();
        this.down('grid').getEl().mask(i18n.get("label.waitForActivation") + "<br/>" + i18n.get("label.refreshOsToCheckStatus"));

    },
    /**
     * hide loadingMask
     */
    hideLoadingMask : function () {
        this.down('form').getEl().unmask();
        this.down('grid').getEl().unmask();
        this.down('toolbar').getEl().unmask();
    },

    /**
	 * ajoute les boutons a la top bar tb = la top bar buttonStates = la liste
	 * des boutons a ajouter
	 */
    addButtonToToolbar : function (tb, buttonStates) {
        for (var i = 0; i < buttonStates.length; i++) {
            tb.add(buttonStates[i]);
        }
    },
    // action d'activer un index
    _onActivateIndex : function () {

        this.applyLoadingMask();
        
        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL') +  "/start",
            method : 'PUT',
            scope : this,
            success : function (ret) {

				var getData = Ext.decode(ret.responseText);
				if (!getData.success) {
					Ext.Msg.alert(i18n.get('label.warning'),
							getData.message);
					return false;
				}
				var data = getData.opensearch;
				this.updateState(data);
				this.populateForm(data);
				this.populateStore(data);

			},
            failure : function (response, opts) {
                alertFailure(response, opts);
            },
            callback : function () {
                this.hideLoadingMask();
            }
        });
    },
    // action de desactiver un index
    _onDesactivateIndex : function () {
        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL') + "/stop",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var getData = Ext.decode(ret.responseText);
                if (!getData.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), getData.message);
                    return false;
                }
                var data = getData.opensearch;
                this.updateState(data);
                this.populateForm(data);
                this.populateStore(data);
            },
            failure : alertFailure
        });

        // envoyer requète au serveur
    },
    // handler to modify the index and quit
    _onUpdateAndQuitIndexHandler : function () {
        this._onSaveIndex("update", true);

    },
    // handler to create the index and quit
    _onSaveAndQuitIndexHandler : function () {
        this._onSaveIndex("create", true);

    },

    // handler to modify the index
    _onUpdateIndexHandler : function () {
        this._onSaveIndex("update", false);
    },
    // handler to create the index
    _onSaveIndexHandler : function () {
        this._onSaveIndex("create", false);
    },

    _onHelp : function () {
        showHelp(loadUrl.get('APP_URL') + '/client-admin/res/help/en/openSearch.html');
    },

    // fonction de génération de l'index
    // envoi au serveur la liste des colonnes à indexer et des colonnes à
    // retourner
    // le paramètre verbose permet de spécifier si la fenetre doit se fermer a
    // la fin de l'execution
    _onSaveIndex : function (action, quit) {
        var storeGrid = this.grid.getStore();
        // récupère un potentiel doublon dans le choix des colonnes à retourner
        var doublonValue = this.checkForDoubloon(this.rssFieldData, storeGrid, "returned");
        // notify the errors
        if (doublonValue !== null) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.doubloonReturnAsMsg') + doublonValue);
            return false;
        }

        var colReturnedButNotIndex = false;
        
        // var to store indexed column
        var listIndex = [];
        // var to store keyword column
        var keywordIndex = [];
        // var to store the JSON to be send
        var returnedJSON = {};
        for (var i = 0; i < storeGrid.getCount(); i++) {
            var rec = storeGrid.getAt(i).data;
            if (rec.indexed) {
                var indexColumn = {};
                indexColumn.idColumn = rec.id;
                if (!Ext.isEmpty(rec.solrType) && rec.solrType !== "") {
                    indexColumn.type = rec.solrType;
                }
                listIndex.push(indexColumn);
            }
            if (rec.keyword) {
                keywordIndex.push(rec.columnAlias);
            }
            if (rec.defaultSearchField) {
                returnedJSON.defaultSearchField = rec.id;
            }
            if (rec.uniqueKey) {
                returnedJSON.uniqueKey = rec.id;
            }
            if (!Ext.isEmpty(rec.returned)) {
                returnedJSON[rec.returned] = rec.id;
                if (!rec.indexed) {
                    colReturnedButNotIndex = true;
                }
                if (rec.returned == "linkField") {
                    returnedJSON.linkFieldRelative = rec.linkFieldRelative;
                }
            }
        }
        if (colReturnedButNotIndex) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.osColReturnedButNotIndexed'));
            return false;
        }
        
        returnedJSON.indexedColumns = listIndex;
        returnedJSON.keywordColumns = keywordIndex;
        returnedJSON.id = this.idOpenSearch;

        // store the form fields
        var form = this.formPanel.getForm();
        Ext.iterate(form.getValues(), function (key, value) {
            if (key == 'image') {
                // TODO : définir une liste de mediaType et type
                returnedJSON.image = {};
                returnedJSON.image.url = value;
                returnedJSON.image.type = "Image";
                returnedJSON.image.mediaType = "Image";
            } else {
                returnedJSON[key] = value;
            }
        }, this);

        if (listIndex.length === 0) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noColumnMsg'));
        } else if (returnedJSON.defaultSearchField === undefined) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noDefaultSearchField'));
        } else if (returnedJSON.uniqueKey === undefined) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noUniqueKey'));
        }
        // check if the the form is valid before sending to the server
        else if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.formErrorValidation'));
        }
         // vérifie si tout les champs rss sont mappés
        var checkMapping = this.checkMapping(this.rssFieldData, storeGrid, "returned");
        // notify the errors
        if (!checkMapping) {
            Ext.Msg.show({
                title : i18n.get('label.information'),
                buttons : Ext.Msg.YESNO,
                msg : i18n.get('label.opensearch.checkMapping'),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        this._doSaveIndex(action, quit, returnedJSON);
                    }
                }
            });
        } else {
            this._doSaveIndex(action, quit, returnedJSON);
        }
    },
    _doSaveIndex : function (action, quit, returnedJSON) {
		// if no error send the request
		
		// set the method depending on the action needed
		// POST = create
		// PUT = modify
		var method = (action == "create") ? "POST" : "PUT";
		
		Ext.Ajax.request({
            url : this.url + loadUrl.get('APP_OPENSEARCH_URL'),
            method : method,
            scope : this,
            jsonData : returnedJSON,
            success : function (ret) {
                // check for the success of the request
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    popupMessage("",  
                            data.message,
                            loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-error.png');
                    return false;
                }
                if (!quit) {
                    popupMessage("",  
                            i18n.get('label.indexSaved'),
                            loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png');
                    
                    this.updateButtons(this.state2);
                } else {
                    this.close();
                }
            },
            failure : alertFailure
        });
	},
	
    // handler to delete an opensearch index
    _onDeleteIndex : function () {
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('label.opensearch.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete();
                }
            }
        });

    },
    
    // method to delete an opensearch index
    doDelete : function () {
        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL'),
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                // check if the request was successfull
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                }
                this.cleanGridInput();
                this.cleanForm();
                this.updateButtons(this.state1);
            },
            failure : alertFailure
        });
    },

    cleanGridInput : function () {
        // update the store to clean the datas
        var storeGrid = this.grid.getStore();
        for (var i = 0; i < storeGrid.getCount(); i++) {
            var rec = storeGrid.getAt(i).data;
            rec.indexed = false;
            rec.returned = undefined;
            rec.defaultSearchField = false;
            rec.uniqueKey = false;
            rec.keyword = false;
            rec.solrType = undefined;

        }
        // refresh the grid
        this.grid.getView().refresh();
    },

    cleanForm : function () {
        var form = this.formPanel.getForm();
        form.reset();
    },

    _onRefreshIndex : function () {
        this.applyLoadingMask();

        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL') + "/refresh",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var getData = Ext.decode(ret.responseText);
                if (!getData.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), getData.message);
                    return false;
                }
                var data = getData.opensearch;
                this.updateState(data);
                this.populateForm(data);
            },
            failure : function (response, opts) {
                alertFailure(response, opts);
            }
        });
    },
    
    
    _onRefreshStatusHandler : function () {
        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL'),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var getData = Ext.decode(ret.responseText);
                if (!getData.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), getData.message);
                    return false;
                }
                var data = getData.opensearch;
                this.updateState(data);                
                this.populateForm(data);
            },
            failure : function (response, opts) {
                alertFailure(response, opts);                
            }
        });
    },
    
    _onCancel : function () {
        this.hideLoadingMask();

        Ext.Ajax.request({
            url : this.url  + loadUrl.get('APP_OPENSEARCH_URL') + "/cancel",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var getData = Ext.decode(ret.responseText);
                if (!getData.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), getData.message);
                    return false;
                }
                //if the cancel is successfull, status is INACTIVE
                var osData = {};
                osData.opensearch = {};
                osData.opensearch.status = "INACTIVE";
                this.updateState(osData);
                
            },
            failure : function (response, opts) {
                alertFailure(response, opts);
                
            }
        });
    },

    // vérifie s'il y a des doublons dans les choix de retour RSS
    // retourne le nom du premier doublon trouvé s'il y en a un
    // retourne undefined sinon
    checkForDoubloon : function (data, array, field) {
        var arrayCount = [data.length];
        for (var i = 0; i < data.length; i++) {
            arrayCount[i] = 0;
        }
        var doublon = null;
        for (i = 0; i < array.getCount() && doublon === null; i++) {
            var rec = array.getAt(i).data;
            var found = false;
            if (rec[field] !== "") {
                for (var j = 0; j < data.length && doublon === null && !found; j++) {
                    if (rec[field] == data[j][1]) {
                        arrayCount[j]++;
                        found = true;
                        if (arrayCount[j] > 1) {
                            doublon = data[j][1];
                        }
                    }
                }
            }
        }
        return doublon;
    },
    
    checkMapping : function (data, array, field) {
        var nbMapping = 0;
        var nbFields = data.length - 1; // the first field is an empty field
        for (var i = 0; i < array.getCount() && nbMapping < nbFields; i++) {
            var rec = array.getAt(i).data;
            if (!Ext.isEmpty(rec[field])) {
                nbMapping++;
            }
        }
        return nbMapping == nbFields;
    }

});




