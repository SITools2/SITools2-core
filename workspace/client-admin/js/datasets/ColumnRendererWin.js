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
 /*
 * @include "columnRender/ColumnRenderEnum.js" 
 */
Ext.namespace('sitools.admin.datasets');

/**
 * This window is used when the administrator needs to configure a columnRenderer 
 * (aka featureType) on a particular column.
 * @cfg {Ext.data.Record} selectedRecord (required) the selected Column
 * @cfg {Ext.grid.View} gridView (required) : the view to refresh when saving
 * @cfg {Ext.data.JsonStore} datasetColumnStore (required) : the store of the column chosen for the current dataset 
 * @cfg {String} columnRendererType (required) : the type of the columnRenderer (Image, URL or datasetLink)
 * @cfg {String} lastColumnRendererType (required) : the type of columnRenderer configured before calling this window
 * @class sitools.admin.datasets.ColumnRendererWin
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.ColumnRendererWin', { 
    extend : 'Ext.Window',
    width : 400,
    height : 600,
    modal : true,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },

    requires : ['sitools.admin.datasets.columnRenderer.UrlPanel',
                'sitools.admin.datasets.columnRenderer.ImagePanel',
                'sitools.admin.datasets.columnRenderer.DatasetLinkPanel'],
    
    initComponent : function () {
        
        this.title = this.columnRendererType + " " + i18n.get('label.detailColumnDefinition');
        
        var behaviorData;
        
        switch (this.columnRendererType) {
		case "URL" :
			behaviorData = [[i18n.get("label.url_local"), ColumnRendererEnum.URL_LOCAL],
						[i18n.get("label.url_ext_new_tab"), ColumnRendererEnum.URL_EXT_NEW_TAB],
						[i18n.get("label.url_ext_desktop"), ColumnRendererEnum.URL_EXT_DESKTOP]];
			break;
		case "Image" :
			behaviorData = [[i18n.get("label.image_no_thumb"), ColumnRendererEnum.IMAGE_NO_THUMB],
						[i18n.get("label.image_thumb_from_image"), ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE],
						[i18n.get("label.image_from_sql"), ColumnRendererEnum.IMAGE_FROM_SQL]];
			break;
		case "DataSetLink" :
            behaviorData = [[i18n.get("label.dataset_link"), ColumnRendererEnum.DATASET_LINK],
						[i18n.get("label.dataset_icon_link"), ColumnRendererEnum.DATASET_ICON_LINK]];
			break;
        case "Other" :
            behaviorData = [[i18n.get("label.noClientAccess"), ColumnRendererEnum.NO_CLIENT_ACCESS]];
            break;
		default :
			break;
        }
        
        var storeBehavior = Ext.create('Ext.data.ArrayStore', {
            fields : [{
                name : 'name'
            }, {
                name : 'behavior'
            }],
            data : behaviorData
        });
        
        this.comboBehavior = Ext.create('Ext.form.field.ComboBox', {
            store : storeBehavior,
            displayField : 'name',
            valueField : 'behavior',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            emptyText : i18n.get("label.selectABehavior"),
            fieldLabel : i18n.get('label.behavior'),
            anchor : "100%",
            allowBlank : false,
            listeners : {
                scope : this,
                select : function (combo, recs, index) {
                    if (!Ext.isEmpty(this.panelDetails)) {
						this.remove(this.panelDetails);
                        this.panelDetails = null;
					}
                    var rec = recs[0];
                    var value = rec.get("behavior");
                    //if we modify the columnRenderer it will not be null
                    var columnRenderer = this.selectedRecord.get("columnRenderer");
                    switch (this.columnRendererType) {
				    case "URL" :
                        this.panelDetails = Ext.create('sitools.admin.datasets.columnRenderer.UrlPanel', {
                            behaviorType : value,
                            title : i18n.get("label.behaviorDetails"),
                            columnRenderer : columnRenderer
                        });
				        break;
				    case "Image" :
				        this.panelDetails = Ext.create('sitools.admin.datasets.columnRenderer.ImagePanel', {
                            behaviorType : value,
                            title : i18n.get("label.behaviorDetails"),
                            datasetColumnStore : this.datasetColumnStore,
                            columnRenderer : columnRenderer
                        });                     
                        break;				        
				    case "DataSetLink" :
				        this.panelDetails = Ext.create('sitools.admin.datasets.columnRenderer.DatasetLinkPanel', {
                            behaviorType : value,
                            title : i18n.get("label.behaviorDetails"),
                            columnRenderer : columnRenderer
                        });                     
				        break;
				    default :
				        break;
				    }
                    if (!Ext.isEmpty(this.panelDetails)) {
                        this.add(this.panelDetails);
//                        this.doLayout();
                    }
                }

            }
        });
        
        this.behaviorForm = Ext.create('Ext.form.Panel', {
            title : i18n.get('label.behavior'),
            height : 120,
            padding : '5 5 5 5',
            bodyPadding : '5 5 5 5',
            bodyBorder : false,
            border : false,
            items : [this.comboBehavior, {
                fieldLabel : i18n.get('label.tooltip'),
                name : 'toolTip',
                xtype : 'textfield'
            }]
        });
        
        this.items = [this.behaviorForm];
        
        this.buttons = [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate

            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ];
            
        this.listeners = {
			scope : this,
			beforeclose : function () {
                if (!this.validAction) {
					// clear the grid record
                    var colRendererCategory = this.lastColumnRendererType;
                    this.selectedRecord.set("columnRendererCategory", colRendererCategory);
                    var ok = (Ext.isEmpty(this.validAction)) ? false : this.validAction;
                    Ext.callback(this.callback, this.scope, [ok]);
                }
			}
		};

        sitools.admin.datasets.ColumnRendererWin.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.admin.datasets.ColumnRendererWin.superclass.afterRender.apply(this, arguments);
        var columnRenderer = this.selectedRecord.get("columnRenderer");
        if (!Ext.isEmpty(columnRenderer)) {
            var behavior = columnRenderer.behavior;
            var toolTip = columnRenderer.toolTip;
            this.behaviorForm.getForm().findField('toolTip').setValue(toolTip);
            
            var index = this.comboBehavior.getStore().find("behavior", behavior);
            if (index != -1) {
	            this.comboBehavior.setValue(behavior);
	            var record = this.comboBehavior.getStore().getAt(index);            
	            this.comboBehavior.fireEvent("select", this.comboBehavior, [record], index);
            }
        }
    },
    
    onValidate : function () {
        var behavior = this.comboBehavior.getValue();
        var tooltip = this.behaviorForm.getForm().findField('toolTip').getValue();
        var ok = true;
        
        if (this.behaviorForm.getForm().isValid()) {
	        var columnRenderer = {};
	        columnRenderer.behavior = behavior;
	        columnRenderer.toolTip = tooltip;
	        if (!Ext.isEmpty(this.panelDetails)) {
	            if (this.panelDetails.isValid()) {
		            this.panelDetails.fillSpecificValue(columnRenderer);		            
	            } else {
                    ok = false;
                }
	        }
            
            if (ok) {
                this.selectedRecord.set("columnRenderer", columnRenderer);
                this.gridView.refresh();
                this.validAction = true;
                this.close();
            }
        }
    }
    
});

