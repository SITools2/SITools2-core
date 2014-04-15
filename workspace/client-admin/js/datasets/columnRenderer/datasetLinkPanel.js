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
 showHelp, loadUrl, ColumnRendererEnum*/

Ext.namespace('sitools.admin.datasets.columnRenderer');
/**
 * Form panel used to fill specific information from a datasetLink columnRenderer
 * @cfg {String} behaviorType (required) : the type of the behavior selected (datasetLink ou datasetIconLink)
 * @class sitools.admin.datasets.columnRenderer.datasetLinkPanel
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.datasets.columnRenderer.datasetLinkPanel', { 
    extend : 'Ext.panel.Panel', 
    flex : 1,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    bodyBorder : false,
    border : false,
    padding : '5 5 5 5',
    bodyPadding : '5 5 5 5',
    
    initComponent : function () {
        
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'); 
        
        this.storeColumns = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'ajax',
                url : this.urlDatasets,
                reader : {
                    type : 'json',
                    root : 'dataset.columnModel',
                    idProperty : 'id'
                }
            },
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'tableName',
                type : 'string'
            }, {
                name : 'columnAlias',
                type : 'string'
            }]
        });
        
        this.smColumns = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE',
            listeners : {
                scope : this,
                rowselect : function (selectionModel, rowIndex, record) {
                    Ext.getCmp('status_bar_column').hide();
                    this.doLayout();
                }
            }
        });
        
        this.bbar = Ext.create('Ext.ux.StatusBar', {
            id : "status_bar_column",
            hidden : true,
            text: i18n.get("label.no_column_selected"),
            iconCls: 'x-status-error'
        });
        
        var storeDatasets = Ext.create('Ext.data.JsonStore', {
            autoLoad : true, 
            proxy : {
                type : 'ajax',
                url : this.urlDatasets,
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
            fields : [ 'id', 'name', 'sitoolsAttachementForUsers' ],
            listeners : {
                scope : this, 
                load : function () {
                    if (!Ext.isEmpty(this.columnRenderer) && this.columnRenderer.behavior == this.behaviorType) {
                        var datasetLinkUrl = this.columnRenderer.datasetLinkUrl;
                        var index = this.comboDatasets.getStore().find("sitoolsAttachementForUsers", datasetLinkUrl);
                        if (index != -1) {
                            this.comboDatasets.setValue(datasetLinkUrl);
                            var record = this.comboDatasets.getStore().getAt(index);            
                            this.comboDatasets.fireEvent("select", this.comboDatasets, record, index);
                        }
                    }
                }
            }
        });
        
        this.comboDatasets = Ext.create('Ext.form.ComboBox', {
            store : storeDatasets,
            name : "comboDatasets",
            displayField : 'name',
            valueField : 'sitoolsAttachementForUsers',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get("label.selectADataset"),
            selectOnFocus : true,
            anchor : "100%",
            fieldLabel : i18n.get('label.dataset'),
            allowBlank : false,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.loadColumns(rec[0].data.id);                    
                }

            }
        });
        
        this.gridColumns = Ext.create('Ext.grid.Panel', {
//            id : 'gridColumnsSelect',
            title : i18n.get('title.datasetLinkDetails'),
            layout : 'fit',
            flex : 1,
            forceFit: true,
            viewConfig : {
                listeners : {
                    scope : this, 
                    refresh : function () {
                        if (!Ext.isEmpty(this.columnRenderer) 
                            && !Ext.isEmpty(this.columnRenderer.columnAlias)
                            && this.comboDatasets.getValue() == this.columnRenderer.datasetLinkUrl) {
                            var columnAlias = this.columnRenderer.columnAlias;
                            var index = this.gridColumns.getStore().find("columnAlias", columnAlias);
                            if (index != -1) {
                                this.gridColumns.getSelectionModel().selectRow(index);
                            }
                        }
                    }
                }
            },
            autoScroll : true,
            store : this.storeColumns,
            columns : [ {
                header : i18n.get('headers.tableName'),
                dataIndex : 'tableName'
            }, {
                header : i18n.get('headers.columnAlias'),
                dataIndex : 'columnAlias'
            }],
            selModel : this.smColumns,
            bbar : this.bbar,
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ this.comboDatasets ]
            }
        });
        
        this.items = [];
        
        if (this.behaviorType == ColumnRendererEnum.DATASET_ICON_LINK) {
            
            this.formImage = Ext.create('Ext.form.Panel', {
                padding : '5 5 5 5',
                bodyPadding : '5 5 5 5',
                border : false,
                bodyBorder : false,
                items : [{
                    xtype : 'sitoolsSelectImage',
                    name : 'image',
                    vtype : "image",
                    fieldLabel : i18n.get('label.image'),
                    anchor : '100%',
                    allowBlank : false
                }]
            });
            this.items.push(this.formImage);
            
        } else {
            this.title = "";
            
        }
        
        this.items.push(this.gridColumns);
        
        sitools.admin.datasets.columnRenderer.datasetLinkPanel.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.admin.datasets.columnRenderer.datasetLinkPanel.superclass.afterRender.apply(this, arguments);
        
        if (!Ext.isEmpty(this.columnRenderer) && this.columnRenderer.behavior == this.behaviorType) {
            if (this.columnRenderer.behavior == ColumnRendererEnum.DATASET_ICON_LINK
                && !Ext.isEmpty(this.columnRenderer.image)) {
                this.formImage.getForm().findField("image").setValue(this.columnRenderer.image.url);                
            }
        }
    },
    
    /**
     * This function is used to validate the panel
     * @return {Boolean} true if the panel is valid, false otherwise
     */
    isValid : function () {
        var isValid = true;
        if (this.behaviorType == ColumnRendererEnum.DATASET_ICON_LINK) {
            isValid = this.formImage.getForm().isValid();
        }
        if (isValid) {
            isValid = this.comboDatasets.isValid();
            if (isValid) {
	            var column = this.gridColumns.getSelectionModel().getSelection()[0];
	            if (Ext.isEmpty(column)) {
	                isValid = false;
	                Ext.getCmp('status_bar_column').show();
	            }
            
            }            
        }
        return isValid;
        
    },
    
    /**
     * This function is used to fill the record with the specific information of the
     * @return {boolean} true if it has succeed, false otherwise
     *  
     */
    fillSpecificValue : function (columnRenderer) {
        var dataset = this.comboDatasets.getValue();
        columnRenderer.datasetLinkUrl = dataset;
        var column = this.gridColumns.getSelectionModel().getSelection()[0];
        if (Ext.isEmpty(column)) {
            return false;   
        }
        columnRenderer.columnAlias = column.get("columnAlias");
        if (this.behaviorType == ColumnRendererEnum.DATASET_ICON_LINK) {
            var image = this.formImage.getForm().findField("image").getValue();
            var resourceImage = {};
            
	        if (!Ext.isEmpty(image)) {
	            resourceImage.url = image;
	            resourceImage.type = "Image";
	            resourceImage.mediaType = "Image";
	        }
            columnRenderer.image = resourceImage;
        }
        return true;
        
        
    },
    
    /**
     * Load the columns of a specific dataset
     * @param {String} datasetId (required) the dataset Id
     */
    loadColumns : function (datasetId) {
        // alert (dictionaryId);
        this.storeColumns.getProxy().url = this.urlDatasets + "/" + datasetId;
//        this.httpProxy.url = this.urlDatasets + "/" + datasetId;
        
        this.gridColumns.getStore().load({
            callback : function () {
                this.gridColumns.getView().refresh();
            },
            scope : this
        });
    }

});