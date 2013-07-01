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
                    
                    this.doLayout();
            		
//                    //add a resizer on each container.
                  Ext.each(this.items.items, function (container) {
                      var resizer = new Ext.Resizable(container.getId(), {
                          handles : 's e',
                          minWidth : 150,
                          maxWidth : 1000,
            	                // minHeight : 30,
            	//                maxHeight : 200,
                          constrainTo : this.body,
                          resizeChild : true,
                          listeners : {
                              scope : this,
                              resize : function (resizable, width, height, e) {
                                  var store = this.formComponentsStore;
            	
                                  var rec = store.getAt(store.find('id', container.getId()));
                                  var PanelPos = this.getEl().getAnchorXY();
            	
                                  rec.set("width", width);
                                  rec.set("height", height);
                                  container.items.items[0].setSize(width - container.getEl().getPadding('l') - container.getEl().getPadding('r'), height);
            	                        //redimensionner dans le cas de listbox : 
                                  if (rec.data.type === "LISTBOX" || rec.data.type === "LISTBOXMULTIPLE") {
            							var multiselect = container.findByType('multiselect')[0];
            							multiselect.view.container.setHeight(height - container.getEl().getPadding('b') - container.getEl().getPadding('t') - 40);
                                  }
                              }
                          }
                      });
                  	}, this);
                    
                    Ext.each(this.items.items, function (container) {
                    	container.getEl().on('contextmenu', this.onContextMenu, container);
                    	 var dd = new Ext.dd.DDProxy(container.getEl().dom.id, 'group', {
                             isTarget : false
                         });
                         Ext.apply(dd, {
                             win : this,
                             startDrag : function (x, y) {
                                 var dragEl = Ext.get(this.getDragEl());
                                 var el = Ext.get(this.getEl());

                                 dragEl.applyStyles({
                                     border : '',
                                     'z-index' : this.win.ownerCt.ownerCt.lastZIndex + 1
                                 });
                                 dragEl.update(el.dom.innerHTML);
                                 dragEl.addClass(el.dom.className);

                                 this.constrainTo(this.win.body);
                             },
                             afterDrag : function () {
                                 var dragEl = Ext.get(this.getDragEl());
                                 var container = Ext.get(this.getEl());

                                 var x = dragEl.getX();
                                 var y = dragEl.getY();

                                 var store = this.win.formComponentsStore;

                                 var rec = store.getAt(store.find('id', container.id));
                                 var PanelPos = Ext.get(this.win.body).getAnchorXY();

                                 rec.set("xpos", x - PanelPos[0]);
                                 rec.set("ypos", y - PanelPos[1]);
                             }
                         });
                    }, this);
                    
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
		

    },
    
    
    onContextMenu : function (event, htmlEl, options) {
		//ici le this est le container sur lequel on a cliqué. 
		event.stopEvent();
		var ctxMenu = new Ext.menu.Menu({
			items : [{
				text : i18n.get('label.edit'), 
				scope : this, 
				handler : this.onEdit
			}, {
				text : i18n.get('label.delete'), 
				scope : this, 
				handler : this.onDelete
			} ]
        });
		var xy = event.getXY();
		ctxMenu.showAt(xy);
    }


});

