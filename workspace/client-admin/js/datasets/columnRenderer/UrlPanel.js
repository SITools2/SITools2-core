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
 * @cfg {String} behaviorType (required) : the type of the behavior selected (dsLink ou dsIconLink)
 * @cfg {Object} columnRenderer : the columnRenderer Object to load if we modify the value
 * @class sitools.admin.datasets.columnRenderer.DatasetLinkPanel
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.datasets.columnRenderer.UrlPanel', { 
        extend : 'Ext.panel.Panel',
		flex : 1,
		layout : "fit",
        bodyBorder : false,
        border : false,
        padding : '5 5 5 5',
        bodyPadding : '5 5 5 5',
		
        initComponent : function () {
            
            this.formPanel = Ext.create('Ext.form.Panel', {  
                defaults : {
                    anchor : "95%"
                },
                bodyBorder : false,
                border : false,
                padding : '5 5 5 5',
                bodyPadding : '5 5 5 5',
                items : [{
                    fieldLabel : i18n.get('label.display'),
                    name : 'display',
                    xtype : 'radiogroup',
                    columns : 2,
                    items : [{
                        boxLabel : 'Text',
                        itemId : 'displayTextId',
                        name : 'display',
                        inputValue : "Text",
                        checked : true
                    }, {
                        boxLabel : 'Image',
                        itemId : 'displayImageId',
                        name : 'display',
                        inputValue : "Image"
                    }],
                    listeners : {
                        scope : this,
                        change : function (radioGroup, newValue, oldValue) {
//                            var name = radioChecked.getGroupValue();
                            var name = newValue.display;
                            var isImage = true;
                            if (name == "Text") {                        
                                isImage = false;
                            }
                            this.formPanel.down("textfield[name=linkText]").setVisible(!isImage);
                            this.formPanel.down("textfield[name=linkText]").setDisabled(isImage);
                            
                            this.formPanel.down("textfield[name=image]").setVisible(isImage);
                            this.formPanel.down("textfield[name=image]").setDisabled(!isImage);
                            this.doLayout();
                        }
                    }
                }, {
						fieldLabel : i18n.get('label.linkText'),
						name : 'linkText',
                        xtype : 'textfield',
                        allowBlank : false
					}, {
						xtype : 'sitoolsSelectImage',
						name : 'image',
//						vtype : "image",
                        hidden : true,
                        disabled : true,
						fieldLabel : i18n.get('label.image'),
						growMax : 400,
                        allowBlank : false
					}, {
                        xtype : 'checkbox',
			            fieldLabel : i18n.get('label.isDisplayable'),
			            name : 'displayable',
                        tooltip : i18n.get('label.isDisplayable.tooltip'),
                        hidden : (this.behaviorType != ColumnRendererEnum.URL_EXT_DESKTOP),
                        disabled : (this.behaviorType != ColumnRendererEnum.URL_EXT_DESKTOP)
			        }
				]
            });
            
            this.items = [this.formPanel];            
            
            sitools.admin.datasets.columnRenderer.UrlPanel.superclass.initComponent.call(this);
        
        },
        /**
         * This function is used to validate the panel
         * @return {Boolean} true if the panel is valid, false otherwise
         */
        isValid : function () {
            var form = this.formPanel.getForm();
            return form.isValid();
        },
        
        /**
	     * This function is used to fill the record with the specific information of the
	     *  
	     */
	    fillSpecificValue : function (columnRenderer) {
			var form = this.formPanel.getForm();
//			var display = form.findField("display").getValue().getGroupValue();
			var display = form.findField("display").getValue().display;
            if ("Text" == display) {
				var linkText = form.findField("linkText").getValue();
				columnRenderer.linkText = linkText;
			} else {
                var image = form.findField("image").getValue();
                var resourceImage = {};
                if (!Ext.isEmpty(image)) {
                    resourceImage.url = image;
                    resourceImage.type = "Image";
                    resourceImage.mediaType = "Image";
                    columnRenderer.image = resourceImage;
                }
            }  
            if (this.behaviorType == ColumnRendererEnum.URL_EXT_DESKTOP) {
	            columnRenderer.displayable = form.findField("displayable").getValue();
			}
            
            return true;
		},
        
        afterRender : function () {
            sitools.admin.datasets.columnRenderer.UrlPanel.superclass.afterRender.apply(this, arguments);
            if (!Ext.isEmpty(this.columnRenderer) && this.columnRenderer.behavior == this.behaviorType) {
                var form = this.formPanel.getForm();
                var record = {};
                var isImage;
                if (!Ext.isEmpty(this.columnRenderer.linkText)) {
                    record.linkText = this.columnRenderer.linkText;
                    record.display = "Text";
                    isImage = false;
                    this.down('radiogroup radio#displayTextId').setValue(true);
                }
                if (!Ext.isEmpty(this.columnRenderer.image)) {
                    record.image = this.columnRenderer.image.url;
                    record.display = "Image";
                    isImage = true;
                    this.down('radiogroup radio#displayImageId').setValue(true);
                }
                if (this.behaviorType == ColumnRendererEnum.URL_EXT_DESKTOP) {
                    record.displayable = this.columnRenderer.displayable;
                } else {
                    record.displayable = false;
                }
                
                form.setValues(record);
                
                form.findField("linkText").setVisible(!isImage);
                form.findField("linkText").setDisabled(isImage);
                
                form.findField("image").setVisible(isImage);
                form.findField("image").setDisabled(!isImage);
                
            }
        }
    });