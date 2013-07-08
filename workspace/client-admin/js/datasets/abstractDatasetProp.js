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
/*global Ext, sitools, ID, i18n, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl*/
Ext.namespace('sitools.admin.datasets');


/**
 * @class sitools.admin.datasets.abstractDatasetWin
 */
//sitools.component.datasets.abstractDatasetWin = {
sitools.admin.datasets.abstractDatasetWin = {
	/**
	 * @cfg minWidth
	 * @inheritdoc
	 */
	minWidth : 700,
	/**
	 * @cfg minHeight
	 * @inheritdoc
	 */
	minHeight : 480,
	/**
	 * @cfg width
	 * @inheritdoc
	 */
    width : 700,
	/**
	 * @cfg height
	 * @inheritdoc
	 */
    height : 480,
	/**
	 * @cfg modal
	 * @inheritdoc
	 */
    modal : true,
	/**
	 * The defaultValue of width for each Column
	 * @type Number
	 */
    defaultColumnWidth : 100,
	/**
	 * The defaultValue of visible for each Column
	 * @type boolean
	 */
    defaultColumnVisible : true,
	/**
	 * The defaultValue of Sortable for each Column
	 * @type boolean
	 */
    defaultColumnSortable : true,
	/**
	 * The defaultValue of filtrable for each Column
	 * @type boolean
	 */
    defaultColumnFiltrable : true, 
	/**
	 * The query type should be W for Wizard or S for SQL 
	 * @type String
	 */
    queryType : 'W',
	/**
	 * @cfg id
	 * @inheritdoc
	 */
    id : 'datasetsMultiTablesPanel',
	/**
	 * @cfg resizable
	 * @inheritdoc
	 */
    resizable : true,


    /**
     * TODO : à documenter...
     * @method 
     */
    onUpload : function () {
        function validate(data, config) {

            config.fieldUrl.setValue(data.url);
        }
        var chooser = new ImageChooser({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_UPLOAD_URL') + '/?media=json',
            width : 515,
            height : 350,
            fieldUrl : this.ownerCt.items.items[0]
        });
        chooser.show(document, validate);
    },

    /**
     * get all the nodes of the wizard to build sql join
     * @method
     * @param {} root
     * @param {} parent
     */
    getAllNodes : function (root, parent) {
        var node = {};
        if (Ext.isEmpty(root)) {
            return;
        } else if (root.isLeaf()) {
            node = {
                leaf : true,
                predicat : root.attributes.predicat,
                type : root.attributes.type
            };
            parent.push(node);
        } else {

            node = {
                table : root.attributes.table, 
                typeJointure : root.attributes.typeJointure,
                children : [],
                leaf : false,
                type : root.attributes.type
            };
            parent.push(node);

            // we call recursively getAllNodes to get all childNodes
            var childs = root.childNodes;
            var i;
            for (i = 0; i < childs.length; i++) {
                this.getAllNodes(childs[i], node.children);
            }
        }
    },

    /**
     * Call to fill the window with the initialValue 
     * @method
     */
    loadDataset : function () {
        // Si l'objet est en modification, on charge l'objet en
        // question
        if (this.action === 'modify' || this.action === 'view' || this.action === "duplicate") {
            var url = "";
            if (this.action === "duplicate") {
                url = this.datasetUrlToCopy;
            }
            else {
                url = this.url;
            }
            Ext.Ajax.request({
                url : url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var i;
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        this.close();
                        Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        return;
                    }
                    var f = this.findByType('form')[0].getForm();
                    var store = this.findById('gridColumnSelect').getStore();

                    var data = Json.dataset;
                    this.initialData = data;

                    var rec = {};
                    if (this.action !== "duplicate") {
						rec.id = data.id;
						rec.sitoolsAttachementForUsers = data.sitoolsAttachementForUsers;
						rec.dirty = data.dirty;
						rec.name = data.name;
                    }
                    else {
						rec.name = data.name + "_copy";
                    }
                    rec.description = data.description;
                    if (!Ext.isEmpty(data.image)) {
                        rec.image = data.image.url;
                    }
                    rec.expirationDate = data.expirationDate;
                    rec.queryType = data.queryType;
                    rec.visible = data.visible;
                    rec.orderBy = data.orderBy;
                    rec.descriptionHTML = data.descriptionHTML;
					
                    var record = new Ext.data.Record(rec);
                    f.loadRecord(record);

                    this.queryType = data.queryType;
                    Ext.getCmp('radioQueryType').setValue(this.queryType);
                    Ext.getCmp('sqlQuery').setValue(data.sqlQuery);

                    this.formulairePrincipal.loadDatasources(
                        function () {
                            if (data.datasource) {
                                var combo = this.findByType('form')[0].getComponent('comboDataSource');
                                var oldValue = combo.getValue();
                                combo.setValue(data.datasource.id);
                                combo.fireEvent("initValue", combo, data.datasource.id);
                            }
                        }, 
                        this.formulairePrincipal
                    );

                    this.viewConfigPanel.getDatasetViewsCombo().getStore().load({
                        scope : this,
                        callback : function (recs) {
                            if (data.datasetView) {
                                var me = this.viewConfigPanel.getDatasetViewsCombo().getStore();
                                var recSelected = me.getAt(me.find("id", data.datasetView.id));
                                this.viewConfigPanel.getDatasetViewsCombo().setValue(data.datasetView.id);
                                this.viewConfigPanel.setViewConfigParamsValue(data.datasetViewConfig);
                                
                                this.viewConfigPanel.getDatasetViewsCombo().fireEvent("select", this.viewConfigPanel.getDatasetViewsCombo(), recSelected);
                                
                            }
                        }
                    });
//                    if (data.structures) {
//                        var structures = data.structures;
//                        var storeTablesDataset = this.gridTablesDataset.getStore();
//                        for (i = 0; i < structures.length; i++) {
//                            storeTablesDataset.add(new Ext.data.Record({
//                                name : structures[i].name,
//                                alias : structures[i].alias,
//                                schemaName : structures[i].schemaName
//                            }));
//
//                        }
//                    }
                    if (data.predicat) {
//                        this.loadColumnsJDBC();

                        var predicat = data.predicat;
                        var storeWhereClause = this.panelWhere.getWizardWhereClause().getStore();
                        
                        for (i = 0; i < predicat.length; i++) {
                            var leftAttribute;
                            if (predicat[i]) {
                                leftAttribute = {
                                    tableName : predicat[i].leftAttribute.tableName,
                                    tableAlias : predicat[i].leftAttribute.tableAlias,
                                    schemaName : predicat[i].leftAttribute.schemaName,
                                    dataIndex : predicat[i].leftAttribute.dataIndex,
                                    columnAlias : predicat[i].leftAttribute.columnAlias,
                                    specificColumnType : predicat[i].leftAttribute.specificColumnType
                                };
                                var RecordType = storeWhereClause.recordType;

                                rec = new RecordType({
                                    parentheseFermante : predicat[i].closedParenthesis,
                                    parentheseOuvrante : predicat[i].openParenthesis,
                                    opLogique : predicat[i].logicOperator,
                                    operateur : predicat[i].compareOperator,
                                    leftAttribute : leftAttribute,
                                    rightAttribute : predicat[i].rightValue.$
                                });
                                storeWhereClause.add(rec);

                            }
                        }
                    }

                    if (!data.columnModel) {
                        return;
                    }
                    var columnModel = data.columnModel;
                    this.findById('gridColumnSelect').getData(columnModel);
                    if (! Ext.isEmpty(data.structure)) {
						this.panelWhere.getWizardJoinCondition().items.items[0].loadTree(data);
                    }
                    
                    if (data.properties) {
						var storeProp = this.gridProperties.getStore();
						var recProp;
						Ext.each(data.properties, function (prop) {
                            if (prop.type == "Date") {
                                var date = Date.parseDate(prop.value, SITOOLS_DATE_FORMAT);
                                if (Ext.isEmpty(date)) {
                                    prop.value = "invalid date";
                                } else {
								    prop.value = date.format(SITOOLS_DEFAULT_IHM_DATE_FORMAT);
                                }
							}
                            recProp = new Ext.data.Record(prop);
							storeProp.add(recProp);
						});
                    }
                    this.applyCkeditor();
                },
                failure : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                }
            });
        } else {
//            this.formulairePrincipal.getDataSourceCombo().getStore().load();
            this.formulairePrincipal.loadDatasources();
            this.viewConfigPanel.getDatasetViewsCombo().getStore().load();
            this.applyCkeditor();
        }

    },
    
    applyCkeditor : function () {
        // Selectively replace <textarea> elements, based on
        // custom assertions.
        CKEDITOR.replaceAll(function (textarea, config) {
            if (!Ext.isEmpty(textarea.classList) && textarea.classList.contains("ckeditor")) {
                config.customConfig = 'config-basic.js';
                config.width = "95%";
                config.displayDatasetLink = false;
                return true;
            } else {
                return false;
            }
        });
    },
    
    refreshTextAreaValues : function () {
        Ext.iterate(CKEDITOR.instances, function(key, instance){
            instance.updateElement();
        });
    }
};
