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
 showHelp, loadUrl, ColumnRendererEnum*/

Ext.namespace('sitools.admin.datasets.columnRenderer');
/**
 * Form panel used to fill specific information from a datasetLink columnRenderer
 * @cfg {String} behaviorType (required) : the type of the behavior selected (datasetLink ou datasetIconLink)
 * @class sitools.admin.datasets.columnRenderer.DatasetLinkColumnPanel
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.datasets.columnRenderer.DatasetLinkColumnPanel', { 
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
    
    requires : ['sitools.public.widget.StatusBar'],
    
    initComponent : function () {
        
        this.gridColumns = this.createDatasetColumnGrid(this.datasetColumnStore);
        
        this.items = [];
        
        if (this.behaviorType == ColumnRendererEnum.DATASET_LINK_COLUMN_ICON) {
            
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
        
        this.callParent(arguments);
    },
    
    afterRender : function () {
    	this.callParent(arguments);
        
        if (!Ext.isEmpty(this.columnRenderer) && this.columnRenderer.behavior == this.behaviorType) {
            if (this.columnRenderer.behavior == ColumnRendererEnum.DATASET_LINK_COLUMN_ICON
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
        if (this.behaviorType == ColumnRendererEnum.DATASET_LINK_COLUMN_ICON) {
            isValid = this.formImage.getForm().isValid();
        }
        if (isValid) {
            var column = this.gridColumns.getSelectionModel().getSelection()[0];
            if (Ext.isEmpty(column)) {
                isValid = false;
                Ext.getCmp('status_bar_column').show();
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
        var column = this.gridColumns.getSelectionModel().getSelection()[0];
        if (Ext.isEmpty(column)) {
            return false;   
        }
        columnRenderer.columnAlias = column.get("columnAlias");
        if (this.behaviorType == ColumnRendererEnum.DATASET_LINK_COLUMN_ICON) {
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
     * Create a GridPanel to display and select a column from the store given 
     * @param {Ext.data.JsonStore} store the store containing the list of columns for the dataset
     * @return {Ext.grid.GridPanel} a grid panel from the given store
     * @private
     */
    createDatasetColumnGrid : function (store) {

        var cmColumns = {
            items : [ {
                header : i18n.get('headers.tableName'),
                dataIndex : 'tableName'
            }, {
                header : i18n.get('headers.columnAlias'),
                dataIndex : 'columnAlias'
            } ],
            defaults : {
                sortable : true,
                width : 100
            }
        };

        var smColumns = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE',
            listeners : {
                scope : this,
                rowselect : function (selectionModel, rowIndex, record) {
                    Ext.getCmp('status_bar_column').hide();
                    this.doLayout();
                }
            }
        });

        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            id : "status_bar_column",
            hidden : true,
            text : i18n.get("label.no_column_selected"),
            iconCls : 'x-status-error'
        });

        this.gridColumns = Ext.create('Ext.grid.Panel', {
            layout : 'fit',
            autoScroll : true,
            store : store,
            columns : cmColumns,
            selModel : smColumns,
            flex : 1,
            bbar : this.bbar,
            forceFit : true,
            listeners : {
                scope : this,
                //select the column selected before
                viewReady : function () {
                    if (!Ext.isEmpty(this.columnRenderer) && !Ext.isEmpty(this.columnRenderer.columnAlias)) {
                        var columnAlias = this.columnRenderer.columnAlias;
                        var index = this.gridColumns.getStore().find("columnAlias", columnAlias);
                        if (index != -1) {
                            this.gridColumns.getSelectionModel().select(index);
                        }
                    }
                }
            }
        });

        return this.gridColumns;

    },

});