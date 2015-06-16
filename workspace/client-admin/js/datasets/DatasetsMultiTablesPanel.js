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
/*global Ext, includeJs, sitools, ID, i18n, document, showResponse, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, alertFailure, LOCALE, ImageChooser, loadUrl*/
Ext.namespace('sitools.admin.datasets');


/**
 * Define the window of the dataset Configuration
 * @cfg {String} url the Url to save the data (only when modify)
 * @cfg {String} action (required) "active", "modify" "view"
 * @cfg {Ext.data.Store} store (required) : the datasets store 
 * 
 * @class sitools.admin.datasets.DatasetsMultiTablesPanel
 * @requires sitools.admin.datasets.PredicatsPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.DatasetsMultiTablesPanel', { 
    extend : 'sitools.admin.datasets.AbstractDatasetWin',
	alias : 'widget.s-datasetsMultiTablesPanel',
	layout : 'fit',

    border : false,
    bodyBorder : false,
	
	requires : ['sitools.admin.datasets.GridFieldSetup',
	            'sitools.admin.datasets.DatasetForm',
	            'sitools.admin.datasets.DatasetSelectTables',
	            'sitools.admin.datasets.DatasetSelectFields',
	            'sitools.admin.datasets.DatasetCriteria',
	            'sitools.admin.datasets.DatasetProperties',
	            'sitools.admin.datasets.DatasetViewConfig',
	            'sitools.admin.datasets.datasourceUtils.DatasourceFactory'],
	
//	closeAction : 'close',
    initComponent : function () {
		//do it when loadUrl is ready.
        this.urlDictionary = loadUrl.get('APP_URL') + loadUrl.get('APP_DICTIONARIES_URL');
        this.urlDatasources = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASOURCES_URL');
        this.urlDatasourcesMongoDB = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASOURCES_MONGODB_URL');
        this.urlDimension = loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension';
        this.urlDatasetViews = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_VIEWS_URL');
        var action = this.action;
        if (this.action === 'modify') {
            this.title = i18n.get('label.modifyDataset');
        }
        
        if (this.action === 'create' || this.action === "duplicate") {
            this.title = i18n.get('label.datasetProject');
        }
        if (this.action === 'view') {
            this.title = i18n.get('label.viewDataset');
        }

        /**
         * Construction de la grille Fields Setup
         */
        this.gridFields = Ext.create('sitools.admin.datasets.GridFieldSetup', {
			urlDictionary : this.urlDictionary, 
			urlDimension : this.urlDimension, 
			action : action, 
			urlDataset : this.url, 
			scope : this
        });
		
		this.gridFields.addListener('activate', function (panel) {
			if (action === 'view') {
				panel.getEl().mask();
			}
		});
		
		/**
		 * The main form of the dataset definition. 
		 */
		this.formulairePrincipal = Ext.create('sitools.admin.datasets.DatasetForm', {
			urlDatasetViews : this.urlDatasetViews, 
			action : this.action, 
			urlDatasources : this.urlDatasources, 
            urlDatasourcesMongoDB : this.urlDatasourcesMongoDB,
			observer : this
		});
		
		

        this.panelSelectTables = Ext.create('sitools.admin.datasets.DatasetSelectTables', {
            scope : this,
            action : this.action
        });
        
        // Correction bug Table alias KO - ID: 3566683
        this.panelSelectTables.addListener('activate', function (panel) {
            var indexAlias = -1;
            Ext.Array.findBy(this.datasourceUtils.getCmTablesDataset(), function (item, index) {
                if (item.id == "alias") {
                    indexAlias = index;
                }
            });
            
			if (indexAlias !== -1) {
				if (this.gridFields.getStore().getCount() > 0) {
				    panel.gridTablesDataset.columns[indexAlias].setDisabled(true);
				}
				else {
				    panel.gridTablesDataset.columns[indexAlias].setDisabled(false);
				}
			}
//			
//            panel.gridTablesDataset.getView().refresh();
		}, this);
		
		// Permet de prendre en compte la nouvelle valeur
		// d'une cellule qui a le focus en changeant de tabPanel
		
		this.panelSelectTables.addListener('deactivate', function (panel) {
			panel.gridTablesDataset.getPlugin('tableAliasEditing').completeEdit();
		});

        // Creation de la grid des columns d'une table
        // !!! le store de cette grille est le meme que celui de fields
        // setup....
		
        this.panelSelectFields = Ext.create('sitools.admin.datasets.DatasetSelectFields', {
			scope : this, 
			gridFieldsDataset : this.gridFields.getStore(),
            action : this.action
        });

        
        
        this.panelWhere = Ext.create('sitools.admin.datasets.DatasetCriteria', {
			scope : this, 
			layout : {
			    type : "vbox",
				align : "stretch", 
				flex : "ratio"
			}, 
			title : i18n.get('label.whereClause')
        });

		this.panelWhere.addListener('activate', function () {
			if (action === 'view') {
				this.getEl().mask();
			}
		});
		
		this.gridProperties = Ext.create('sitools.admin.datasets.DatasetProperties', {
			action : this.action
		});
		
        this.viewConfigPanel = Ext.create('sitools.admin.datasets.DatasetViewConfig', {
			urlDatasetViews : this.urlDatasetViews, 
			action : this.action
        });

        /**
         * The main tabPanel of the window
         * @type Ext.TabPanel
         */
        this.tabPanel = Ext.create('Ext.tab.Panel', {
            height : 450,
            border : false,
            bodyBorder : false,
            layoutConfig : {
				layoutOnCardChange : true
            }, 
            activeTab : 0,
            items : [ this.formulairePrincipal, this.gridProperties, this.panelSelectTables, this.panelSelectFields, this.gridFields, this.panelWhere, this.viewConfigPanel ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate, 
                disabled : action === "view" ? true : false

            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.destroyCkeditor();
                    this.close();
                }
            } ], 
            listeners : {
				scope : this, 
				beforetabchange : function (tabP, newTab, currentTab) {
					var check = {
						success : true
					};
					if (!Ext.isEmpty(currentTab) && currentTab.id === "gridColumnSelect") {
						check = this.gridFields.gridFieldSetupValidation();
					}
					if (!Ext.isEmpty(currentTab) && currentTab.id === "selectTablesPanel") {
						check = this.panelSelectTables.validatePanel();
					}
					if (! check.success) {
			            Ext.create("Ext.ux.Notification", {
				            iconCls : 'x-icon-information',
				            title : i18n.get('label.error'),
				            html : check.message,
				            autoDestroy : true,
				            hideDelay : 1000
				        }).show(document);
				        return false;
			        }
					
					return true;
				}, 
				initComboDatasource : function (field, value) {
					var datasourceType = this.formulairePrincipal.getDataSourceCombo().getDatasourceType();
					this.datasourceUtils = sitools.admin.datasets.datasourceUtils.DatasourceFactory.getDatasource(datasourceType, this);
					
					this.panelSelectTables.fireEvent("initializeDatasource");
                    this.panelSelectFields.fireEvent("initializeDatasource");
				}, 
				datasourceChanged : function (field, newValue, oldValue) {
                    var datasourceType = this.formulairePrincipal.getDataSourceCombo().getDatasourceType();
                    this.datasourceUtils = sitools.admin.datasets.datasourceUtils.DatasourceFactory.getDatasource(datasourceType, this);
					
					if (Ext.isEmpty(oldValue)) {
						this.panelSelectTables.fireEvent("initializeDatasource");
                        this.panelSelectFields.fireEvent("initializeDatasource");
						return;
                    }
                    
					if (newValue != oldValue) {
                        if (this.gridFields.getStore().getCount() > 0  ||
                                this.panelWhere.getWizardWhereClause().getStore().getCount() > 0 || this.panelSelectTables.isFilled() ||
                                (Ext.getCmp('sqlQuery').getValue() && Ext.getCmp('sqlQuery').getValue() !== "")) {
                            var tot = Ext.Msg.show({
                                title : i18n.get('label.delete'),
                                buttons : Ext.Msg.YESNO,
                                msg : i18n.get('warning.changeDatasource'),
                                scope : this,
                                fn : function (btn, text) {
                                    if (btn === 'yes') {
                                        this.gridFields.getStore().removeAll();
                                        this.panelWhere.getWizardJoinCondition().deleteJoinPanelItems();
                                        this.panelWhere.getWizardWhereClause().getStore().removeAll();
                                        this.panelSelectTables.getStoreSelectedTables().removeAll();
                                        Ext.getCmp('sqlQuery').setValue("");
                                    } else {
                                        field.setValue(oldValue);
                                    }
                                }

                            });
                        }
                        this.panelSelectTables.fireEvent("datasourceChanged");
                    }
					
				}
            }

        });
        this.listeners = {
			scope : this, 
			resize : function (window, width, height) {
				var size = window.body.getSize();
				this.tabPanel.setSize(size);
			}

        };
        this.items = [ this.tabPanel ];
        this.callParent(arguments);
    },

    afterRender : function () {
        this.callParent(arguments);
        this.loadDataset();
    },
    /**
     * called when user click on Ok button. 
     * it will 
     * <ul class="mdetail-params">
     * <li>check the dataset (calling datasetValidation())</li>
     * <li>build the json object of the Dataset</li>
     * <li>call a method (PUT or POST depending on Action config)</li>
     * </ul>
     * @method
     */
    onValidate : function () {
        this.queryType = this.panelWhere.getQueryType();
        var datasetValidation = this.datasetValidation();
        if (!datasetValidation.success) {
            Ext.Msg.alert(i18n.get('label.error'), datasetValidation.message);
            return;
        }
        
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }

        this.refreshTextAreaValues();
        var putObject = {};
        Ext.iterate(f.getFieldValues(), function (key, value) {
            if (key === 'image') {
                // TODO : definir une liste de mediaType et type
                putObject.image = {};
                putObject.image.url = value;
                putObject.image.type = "Image";
                putObject.image.mediaType = "Image";
            } else if (key === 'comboDataSource') {
                putObject.datasource = {};
                putObject.datasource.url = this.dataSourceUrl;
                putObject.datasource.id = this.formulairePrincipal.getDataSourceCombo().getValue();
                putObject.datasource.type = "datasource";
                putObject.datasource.mediaType = "datasource";
            } else if (key !== "visible") {
                putObject[key] = value;
            }
        }, this);
        
        putObject.datasetView = {};
        var storeDatasetView = this.viewConfigPanel.getDatasetViewsCombo().getStore();
        var indexSelected = storeDatasetView.find("id", this.viewConfigPanel.getDatasetViewsCombo().getValue());
        var recDatasetView = storeDatasetView.getAt(indexSelected);
        putObject.datasetView = recDatasetView.data;
		
        putObject.datasetViewConfig = this.viewConfigPanel.getParametersValue();
        
        //save Properties...
        putObject.properties = [];
        this.gridProperties.getStore().each(function (rec) {
			if (rec.get("type") === "Date") {
			    var dateValue = rec.get("value");
			    var date = Ext.Date.parse(dateValue, SITOOLS_DEFAULT_IHM_DATE_FORMAT);
			    if (!Ext.isEmpty(date)) {
					rec.set("value", Ext.Date.format(date, SITOOLS_DATE_FORMAT));
			    }
			}
			putObject.properties.push(rec.data);
        });
        
        //visible field handling
        var visibleField = f.findField("visible");
        putObject.visible = visibleField.getValue();
        
        putObject.queryType = this.queryType;

        putObject.sqlQuery = Ext.getCmp('sqlQuery').getValue();

        if (this.panelWhere.getWizardJoinCondition() || this.panelWhere.getWizardWhereClause()) {
            putObject.predicat = [];
        }
        if (this.panelWhere.getWizardWhereClause()) {
            this.panelWhere.getWizardWhereClause().getStore().each(function (item) {
                putObject.predicat.push({
                    closedParenthesis : item.data.parentheseFermante,
                    openParenthesis : item.data.parentheseOuvrante,
                    logicOperator : item.data.opLogique,
                    compareOperator : item.data.operateur,
                    leftAttribute : item.data.leftAttribute,
                    rightValue : item.data.rightAttribute
                });
            });
        }

        putObject.structures = [];
        this.panelSelectTables.getStoreSelectedTables().each(function (item) {
            putObject.structures.push({
                alias : item.data.alias,
                name : item.data.name,
                schemaName : item.data.schemaName,
                type : 'table'
            });
        });

        var store = this.gridFields.getStore();
        store.clearFilter();
        if (store.getCount() > 0) {
            putObject.columnModel = [];
            var i;
            for (i = 0; i < store.getCount(); i++) {
                var rec = store.getAt(i).data;

                var tmp = {
                    id : rec.id,
                    dataIndex : rec.dataIndex,
                    header : rec.header,
                    toolTip : rec.toolTip,
                    width : rec.width,
                    sortable : rec.sortable,
                    orderBy : rec.orderBy, 
                    visible : rec.visible,
                    filter : rec.filter,
                    sqlColumnType : rec.sqlColumnType,
                    primaryKey : rec.primaryKey,
                    schema : rec.schemaName,
                    tableAlias : rec.tableAlias,
                    tableName : rec.tableName,
                    specificColumnType : rec.specificColumnType,
                    columnAlias : rec.columnAlias, 
                    datasetDetailUrl : rec.datasetDetailUrl, 
                    columnAliasDetail : rec.columnAliasDetail, 
                    javaSqlColumnType : rec.javaSqlColumnType,
                    format : rec.format,
                    image : rec.image,
                    dimensionId : rec.dimensionId, 
                    unit : rec.unit
                };
                if (!Ext.isEmpty(rec.columnRendererCategory) && !Ext.isEmpty(rec.columnRenderer)) {
                    tmp.columnRenderer = rec.columnRenderer;
                }

                //delete tmp.columnOrder;
                putObject.columnModel.push(tmp);
            }
        }
        
        //Gestion des structures
        //build Default if the panel is not activated...
        this.panelWhere.getWizardJoinCondition().buildDefault();
        this.treeStructure = this.panelWhere.getWizardJoinCondition().items.items[0];
        
        var mainTableNode = this.treeStructure.getRootNode();
        if (Ext.isEmpty(mainTableNode)) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noStructure'));
			return;
        }
        var mainTable = mainTableNode.get("table");
        
		var tree = [];

        var childs = mainTableNode.childNodes;
        for (var j = 0; j < childs.length; j++) {
            this.getAllNodes(childs[j], tree);
        }
        
        var structure = {
			mainTable : mainTable, 
			nodeList : tree
        };
        putObject.structure = structure;
        
        if (this.action === 'modify') {
            Ext.Ajax.request({
                url : this.url,
                method : 'PUT',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get(Json.message));
                        return;
                    }
                    this.close();
                    this.store.reload();
                },
                failure : alertFailure,
                callback : function () {
                    this.destroyCkeditor();
                }
            });
        } else {
            Ext.Ajax.request({
                url : this.url,
                method : 'POST',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get(Json.message));
                        return;
                    }
                    this.close();
                    this.store.reload();
				},
                failure : alertFailure,
                callback : function () {
                    this.destroyCkeditor();
                }
            });
        }
    },
    
    /**
     * Validate the Dataset : 
     *  - One and only one primary key
     *  - columnAlias must be unique
     * @returns {} an object with attributes : 
     *  - success : boolean
     *  - message : a message if success == false
     * 
     */
    datasetValidation : function () {
        var result = this.gridFields.gridFieldSetupValidation();
        
        if (this.queryType === "S" && (Ext.getCmp('sqlQuery').getValue().substr(0, 4) !== "FROM" || !Ext.getCmp('sqlQuery').getValue().toLowerCase().match("where"))) {
			result = {
                success : false, 
                message : i18n.get('label.invalidSQl')
            };
        }
        return result;
    },
    
    onDestroy : function () {
        this.callParent(arguments);
        this.destroyCkeditor();
    },

    destroyCkeditor : function () {
        Ext.iterate(CKEDITOR.instances, function (key, instance) {
            if (instance) {
                CKEDITOR.remove(instance);
            }
            instance.updateElement();
        });
    }
});

