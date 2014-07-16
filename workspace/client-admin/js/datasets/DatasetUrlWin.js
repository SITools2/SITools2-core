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
Ext.namespace('sitools.admin.datasets');

/**
 * This window is used when the administrator needs to link a dataset with another.
 * @cfg {Ext.data.Record} selectedRecord (required) the selected Column
 * @cfg {Ext.grid.View} gridView (required) : the view to refresh when saving
 * @cfg {boolean} withImage : true if this link is with image
 * @class sitools.admin.datasets.datasetUrlWin
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.datasetUrlWin', { 
    extend : 'Ext.Window',
    width : 400,
    height : 400,
    modal : true,

    initComponent : function () {
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'); 
        
        this.title = i18n.get('label.detailColumnDefinition');

        this.httpProxy = new Ext.data.HttpProxy({
            url : this.urlDatasets,
            restful : true,
            method : 'GET'
        });
        
        this.storeColumns = new Ext.data.JsonStore({
//            id : 'datasetColumnId',
            root : 'dataset.columnModel',
            idProperty : 'id',
            proxy : this.httpProxy,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'tableName',
                type : 'string'
            }, {
                name : 'columnAlias',
                type : 'string'
            } ]
        });
        
        this.gridViewColumns = new Ext.grid.View({
            listeners : {
                scope : this, 
                refresh : function () {
                    if (!Ext.isEmpty(this.selectedRecord.data.columnAliasDetail)) {
                        var recIndex = this.storeColumns.find('columnAlias', this.selectedRecord.data.columnAliasDetail);
                        if (recIndex > -1) {
                            this.smColumns.selectRow(recIndex);    
                        }
                    }
                }
            }

        });
        this.cmColumns = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.tableName'),
                dataIndex : 'tableName'
            }, {
                header : i18n.get('headers.columnAlias'),
                dataIndex : 'columnAlias'
            }],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        this.smColumns = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });

        this.gridColumns = Ext.create('Ext.grid.Panel', {
//            id : 'gridColumnsSelect',
            title : i18n.get('title.gridColumns'),
            layout : 'fit',
            height : 280, 
            view : this.gridViewColumns, 
            autoScroll : true,
            store : this.storeColumns,
            cm : this.cmColumns,
            selModel : this.smColumns,
            forceFit : true
        });

        var storeDatasets = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'sitoolsAttachementForUsers' ],
            url : this.urlDatasets,
            root : "data",
            autoLoad : true, 
            listeners : {
                scope : this, 
                load : function () {
                    if (!Ext.isEmpty(this.selectedRecord.data.datasetDetailUrl)) {
                        this.comboDatasets.setValue(this.selectedRecord.data.datasetDetailUrl);
                        this.loadColumns(this.selectedRecord.data.datasetDetailUrl);
                    }
                }
            }
        });
        this.comboDatasets = new Ext.form.ComboBox({
            store : storeDatasets,
            displayField : 'name',
            valueField : 'sitoolsAttachementForUsers',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : 'Select a dataset...',
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.loadColumns(rec.data.id);
                }

            }
        });
        
        
        
        this.formImage = new Ext.form.FormPanel({
			layout : 'fit',
			title : i18n.get('label.formImage'),
            height : 80, 
			items : [{
				xtype : 'sitoolsSelectImage',
				name : 'image',
				vtype : "image",
				fieldLabel : i18n
						.get('label.image'),
				anchor : '100%',
				growMax : 400

			}]
		});
        
        var items = [ this.comboDatasets, this.gridColumns];
        
        if (this.withImage) {
            this.height += 80;
            items.push(this.formImage);
        }
        
        this.items = [ {
            xtype : 'panel', 
            layout : 'fit', 
            title : i18n.get('label.datasetSelect'),
            items : items,
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
        sitools.admin.datasets.datasetUrlWin.superclass.initComponent.call(this);
    },
    /**
     * Load the selectedRecord if not empty
     */
    afterRender : function () {
        sitools.admin.datasets.datasetUrlWin.superclass.afterRender.apply(this, arguments);
        if (!Ext.isEmpty(this.selectedRecord) && !Ext.isEmpty(this.selectedRecord.data) && !Ext.isEmpty(this.selectedRecord.data.image)) {
            var rec = {};
            var form = this.formImage.getForm();
            rec.image = this.selectedRecord.data.image.url;
            
            form.setValues(rec);
        }
    },
    /**
     * Load the columns of a specific dataset
     * @param {String} datasetId (required) the dataset Id
     */
    loadColumns : function (datasetId) {
        // alert (dictionaryId);
        this.httpProxy.url = this.urlDatasets + "/" + datasetId;
        this.gridColumns.getStore().load({
            callback : function () {
                this.gridColumns.getView().refresh();
            },
            scope : this
        });
    },
    
    /**
     * When click on button ok. 
     * Validate the link. 
     */
    onValidate : function () {
        var rec = this.gridColumns.getSelectionModel().getSelected();
        this.selectedRecord.data.datasetDetailUrl = this.comboDatasets.getValue();
        this.selectedRecord.data.columnAliasDetail = rec.data.columnAlias;
        var form = this.formImage.getForm();
        var image = form.findField("image").getValue();
        if (!Ext.isEmpty(image)) {
	        this.selectedRecord.data.image = {};
	        this.selectedRecord.data.image.url = image;
            this.selectedRecord.data.image.type = "Image";
            this.selectedRecord.data.image.mediaType = "Image";
        }
        
        this.gridView.refresh();
        this.close();
    }

});
