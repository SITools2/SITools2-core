/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @include "absoluteLayoutProp.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A window to present all form Components type
 * @cfg {Ext.data.JsonStore} storeConcepts the store with concepts
 * @class sitools.admin.forms.componentsListPanel
 * @extends Ext.Window
 */
sitools.admin.forms.advancedFormPanel = Ext.extend(Ext.form.FieldSet, {
    
    border : true,
    collapsible : true,


    initComponent : function () {

        Ext.apply(this, {
        	
            layout : "absolute",
            autoScroll : true, 
            enableDragDrop : true,
            
            listeners : {
            	
            	scope : this,
            	
            	activate : function(){
            		
                    var y = 0;
                    var x = 25;
                    var componentId = "";
                    this.removeAll(true);
                    
                    var mypanel = this;
                    
                    this.formComponentsStore.each(function (component) {
                    	
                   		var containerId = component.data.containerPanelId;
          	
                    	if (containerId == this.id ) { 
                    		
	                        y = Ext.isEmpty(component.data.ypos) ? y + 50 : component.data.ypos;
	                        x = Ext.isEmpty(component.data.xpos) ? x : component.data.xpos;
	                        // height = Ext.isEmpty (component.data.height) ? height :
	                        // component.data.height;
	                        var containerItems = [ sitools.common.forms.formParameterToComponent(component.data, null, null, this.datasetColumnModel, this.context).component ];
	                        containerItems[0].setDisabled(true);
	                        
	                        var container = new Ext.Container({
	                        	
	                            width : parseInt(component.data.width, 10),
	                            height : parseInt(component.data.height, 10),
	                            bodyCssClass : "noborder",
	                            cls : component.data.css,
	                            x : x,
	                            y : y,
	                            id : component.data.id,
	                            componentData : component.data, 
	                            labelWidth : 100,
	                            items : containerItems, 
	                            displayPanel : this, 
	                            record : component, 
	                            
	                            onEdit : function () {
	            			        var rec = this.record;
	            			        if (!rec) {
	            			            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
	            			        }
	            			        var propComponentPanel = new sitools.admin.forms.componentPropPanel({
	            			            datasetColumnModel : this.displayPanel.datasetColumnModel,
	            			            action : 'modify', 
	            			            urlFormulaire : this.displayPanel.urlFormulaire, 
	            			            context : this.displayPanel.context, 
	            			            storeConcepts : this.displayPanel.storeConcepts, 
	            			            record : this.record, 
	            			            formComponentsStore : this.displayPanel.formComponentsStore, 
	            			            absoluteLayout : this.displayPanel,
	            			            containerPanelId : mypanel.id
	            			        });
	            			        propComponentPanel.show();
	                            }, 
	                            
	            			    onDelete : function () {
	            			        var rec = this.record;
	            			        if (!rec) {
	            			            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
	            			        }
	
	            			        var childrenExists = false, childrens = [];
	            			        this.displayPanel.formComponentsStore.each(function (record) {
	            			            if (rec.data.id === record.data.parentParam) {
	            			                childrenExists = true;
	            			                childrens.push(record.data.label);
	            			            }
	            			        });
	            			        if (childrens.length > 0) {
	            			            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.atLeastOneChildren') + childrens.join(", "));
	            			            return;
	            			        }
	            			        this.displayPanel.formComponentsStore.remove(rec);
	            			        this.displayPanel.fireEvent("activate");
	            			        
	            			    }	
	            			                    
	                        });
	                        this.add(container);
	                    }
	            	}
                    , this);
            		
            	}
            }
            
        });

        sitools.admin.forms.advancedFormPanel.superclass.initComponent.call(this);
    },

    afterRender : function () {
    	
    	sitools.admin.forms.advancedFormPanel.superclass.afterRender.apply(this, arguments);
        
        var ddGroup = this.ddGroup;
        var datasetColumnModel = this.datasetColumnModel;
        var formComponentsStore = this.formComponentsStore;
        var storeConcepts = this.storeConcepts;
        var absoluteLayout = this.absoluteLayout;
        
        var mypanel = this;
        
        var advPanelDropTargetEl =  this.body.dom;
		
		var bodyEl = this.body;
		
		var advPanelDropTarget = new Ext.dd.DropTarget(advPanelDropTargetEl, {
			
			ddGroup     : ddGroup,
			
			notifyDrop  : function (ddSource, e, data) {

				var xyDrop = e.xy;
				var xyRef = Ext.get(bodyEl).getXY();
				
				var xyOnCreate = {
					x : xyDrop[0] - xyRef[0], 
					y : xyDrop[1] - xyRef[1]
				};
				// Reference the record (single selection) for readability
				var rec = ddSource.dragData.selections[0];
		        var ComponentWin = new sitools.admin.forms.componentPropPanel({
		            urlAdmin : rec.data.jsonDefinitionAdmin,
		            datasetColumnModel : datasetColumnModel,
		            ctype : rec.data.type,
		            action : "create",
		            componentDefaultHeight : rec.data.componentDefaultHeight,
		            componentDefaultWidth : rec.data.componentDefaultWidth,
		            dimensionId : rec.data.dimensionId,
		            unit : rec.data.unit,
		            extraParams : rec.data.extraParams, 
		            jsAdminObject : rec.data.jsAdminObject, 
		            jsUserObject : rec.data.jsUserObject, 
		            context : "dataset", 
		            xyOnCreate : xyOnCreate, 
		            storeConcepts : storeConcepts, 
		            absoluteLayout : absoluteLayout, 
		            record : rec, 
		            formComponentsStore : formComponentsStore,
		            containerPanelId : mypanel.id
		        });
		        ComponentWin.show();
			},
			overClass : 'not-save-textfield'
		});
		

    }


});

