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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
/*
 * @include "absoluteLayoutProp.js"
 * @include "componentsListPanel.js"
 * @include "componentPropPanel.js"
 * @include "../../../client-public/js/forms/formParameterToComponent.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * Panel de disposition des composants de formulaires. 
 * @cfg {Ext.data.Store} storeColumns The store with all filtrable columns
 * @cfg {Ext.data.Store} formComponentsStore The store with all Components.
 * @class sitools.admin.forms.ComponentsDisplayPanel
 * @extends Ext.Panel
 */
sitools.admin.forms.ComponentsDisplayPanel = Ext.extend(Ext.Panel, {
	
	y : 0,
	
	initComponent : function () {
		
        Ext.apply(this, {
			id : "absoluteLayout",
			layout : 
			{ type:'vbox',
                 align:'stretch'
             },
	        title : i18n.get('label.disposition'),
	        autoScroll : true,
	        height : this.formSize.height, 
	        width : this.formSize.width, 
	        tbar : new Ext.Toolbar({
	
	            items : [ {
	                scope : this,
	                text : i18n.get("label.changeFormSize"),
	                handler : this._sizeUp
	            } ,
	            {
	                scope : this,
	                text : "Add advanced criteria",
	                handler : this._addPanel
	            }
	            
	            ]
	
	        }),
	        listeners : {
	            scope : this,
	            activate : this._activeDisposition,
//	            afterRender : function () {
//	                var ddTarget = new Ext.dd.DDTarget("ddTargetId", 'group');
//	            }
	        }        
        });
        sitools.admin.forms.ComponentsDisplayPanel.superclass.initComponent.call(this);
		
	}, 
	
    _sizeUp : function () {
        var panelProp = new sitools.admin.forms.absoluteLayoutProp({
            absoluteLayout : this,
            tabPanel : this.ownerCt.ownerCt.ownerCt,
            win : this.ownerCt.ownerCt.ownerCt.ownerCt, 
            formSize : this.formSize
        });
        panelProp.show();

    },
    
    _addPanel : function () {

    	var aPanel = new sitools.admin.forms.advancedFormPanel({
    		title: 'advanced form',
    		frame : true,
    		height: 200,
    		width: 200,
    		ddGroup     : 'gridComponentsList',
    		datasetColumnModel : this.datasetColumnModel,
    		storeConcepts : this.storeConcepts, 
    		formComponentsStore : this.formComponentsStore,
    		absoluteLayout : this,
    	});
    	this.add(aPanel);
    	
    	// resize container panel if needed
    	this.y = this.y + 200;
    	if (this.y > 500) {
    		this.formSize.height = this.y;
    	}
        var size = {
           width : this.formSize.width,
           height : this.formSize.height
        };
        this.setSize(size);
    	
    	this.doLayout();

    },
    
    _activeDisposition : function () {
    	
        this.body.addClass(Ext.getCmp("formMainFormId").find('name', 'css')[0].getValue());
        this.setSize(this.formSize);
        
        this.removeAll();

        var mainPanel = new sitools.admin.forms.advancedFormPanel({
        	
        	title: 'form',
        	height: 200,
			
			border: true,
			id : 'mainpanel',
			cls: 'toto',
			
			ddGroup : 'gridComponentsList',
    		datasetColumnModel : this.datasetColumnModel,
    		storeConcepts : this.storeConcepts, 
    		formComponentsStore : this.formComponentsStore,
    		absoluteLayout : this,
			
        });
        
        this.add(mainPanel);
        
        if ( this.action == 'create'  ) {
        	this.y = 200;
        }
        
//        if ( this.action == 'modify'  ) {

        	
        	/*  loop on the form components
	         *	retrieve the container panel id
	         * */
	        if (!Ext.isEmpty(this.formComponentsStore.getModifiedRecords())){
	        	
	        	var parent = this;
	        	parent.y =  0;
	        	var recordsArray = this.formComponentsStore.getModifiedRecords();
	        	var panels = {};
	        
	        	//parent.removeAll();
	        	
	        	for (var i = 0; i < recordsArray.length; i++) {
	
	        		var containerId = recordsArray[i].data.containerPanelId;
	        		
	        		var aPanel = new sitools.admin.forms.advancedFormPanel({
	            		id: recordsArray[i].data.containerPanelId,
	            		title: 'advanced form',
	            		frame : true,
	            		height: 200,
	            		ddGroup : 'gridComponentsList',
	            		datasetColumnModel : this.datasetColumnModel,
	            		storeConcepts : this.storeConcepts, 
	            		formComponentsStore : this.formComponentsStore,
	            		absoluteLayout : this,
	        		});
	        		
	        		if ( !Ext.isEmpty(containerId) && Ext.isEmpty(panels[containerId])){
	            		panels[containerId] = [];
	            		panels[containerId].push(aPanel);
	            	}
	        	}
	        	
	        	Ext.iterate(panels, function(key, value){
	        		parent.add(value);
	        		parent.y = parent.y + 200;
		        });

	        }

//        }




        
//        var mainPanel = new Ext.Panel({
//        	
//        	title: 'main',
//        	height: 200,
//			
//			border: true,
//			id : 'mainpanel',
//			cls: 'toto',
//
//			listeners : {
//				
//				activate : function () {
//	
//					var mainPanelDropTargetEl =  this.body.dom;
//					
//					var mainPanelDropTarget = new Ext.dd.DropTarget(mainPanelDropTargetEl, {
//						
//						ddGroup     : 'gridComponentsList',
//						
//						notifyDrop  : function (ddSource, e, data) {
//							console.log('youpi');
//						},
//						
//						overClass : 'not-save-textfield'
//					});
//					//Ext.getCmp('gridsource').dd.addToGroup('gridComponentsTest');
//					
//				}
//			}
//        });
//        
//        this.add(mainPanel);
      
        this.doLayout();
        
        
        /* loop on the panels */
        if (!Ext.isEmpty(this.items)){
        	
	        this.items.each(function(item){
	        	item.fireEvent('activate');
	        }, this);
	        
        }
      
        
        //add a resizer on each container.
//      Ext.each(this.items.items, function (container) {
//          var resizer = new Ext.Resizable(container.getId(), {
//              handles : 's e',
//              minWidth : 150,
//              maxWidth : 1000,
//	                // minHeight : 30,
//	//                maxHeight : 200,
//              constrainTo : this.body,
//              resizeChild : true,
//              listeners : {
//                  scope : this,
//                  resize : function (resizable, width, height, e) {
//                      var store = this.formComponentsStore;
//	
//                      var rec = store.getAt(store.find('id', container.getId()));
//                      var PanelPos = this.getEl().getAnchorXY();
//	
//                      rec.set("width", width);
//                      rec.set("height", height);
//                      container.items.items[0].setSize(width - container.getEl().getPadding('l') - container.getEl().getPadding('r'), height);
//	                        //redimensionner dans le cas de listbox : 
//                      if (rec.data.type === "LISTBOX" || rec.data.type === "LISTBOXMULTIPLE") {
//							var multiselect = container.findByType('multiselect')[0];
//							multiselect.view.container.setHeight(height - container.getEl().getPadding('b') - container.getEl().getPadding('t') - 40);
//                      }
//                  }
//              }
//          });
//      	}, this);
        
        
        
//        Ext.each(this.items.items, function (container) {
//        	
//        	container.getEl().on('contextmenu', this.onContextMenu, container);
//            var dd = new Ext.dd.DDProxy(container.getEl().dom.id, 'group', {
//                isTarget : false
//            });
//            Ext.apply(dd, {
//                win : this,
//                startDrag : function (x, y) {
//                    var dragEl = Ext.get(this.getDragEl());
//                    var el = Ext.get(this.getEl());
//
//                    dragEl.applyStyles({
//                        border : '',
//                        'z-index' : this.win.ownerCt.ownerCt.lastZIndex + 1
//                    });
//                    dragEl.update(el.dom.innerHTML);
//                    dragEl.addClass(el.dom.className);
//
//                    this.constrainTo(this.win.body);
//                },
//                afterDrag : function () {
//                    var dragEl = Ext.get(this.getDragEl());
//                    var container = Ext.get(this.getEl());
//
//                    var x = dragEl.getX();
//                    var y = dragEl.getY();
//
//                    var store = this.win.formComponentsStore;
//
//                    var rec = store.getAt(store.find('id', container.id));
//                    var PanelPos = Ext.get(this.win.body).getAnchorXY();
//
//                    rec.set("xpos", x - PanelPos[0]);
//                    rec.set("ypos", y - PanelPos[1]);
//                }
//            });
//        }, this);
//        
      
        

        
        

//        Ext.each(this.items.items, function (container) {
//
//        	container.getEl().on('contextmenu', this.onContextMenu, container);
//        	
//            var dd = new Ext.dd.DDProxy(container.getEl().dom.id, 'group', {
//                isTarget : false
//            });
//
//            Ext.apply(dd, {
//                win : this,
//                startDrag : function (x, y) {
//                    var dragEl = Ext.get(this.getDragEl());
//                    var el = Ext.get(this.getEl());
//
//                    dragEl.applyStyles({
//                        border : '',
//                        'z-index' : this.win.ownerCt.ownerCt.lastZIndex + 1
//                    });
//                    dragEl.update(el.dom.innerHTML);
//                    dragEl.addClass(el.dom.className);
//
//                    this.constrainTo(this.win.body);
//                },
//                afterDrag : function () {
//                    var dragEl = Ext.get(this.getDragEl());
//                    var container = Ext.get(this.getEl());
//
//                    var x = dragEl.getX();
//                    var y = dragEl.getY();
//
//                    var store = this.win.formComponentsStore;
//
//                    var rec = store.getAt(store.find('id', container.id));
//                    var PanelPos = Ext.get(this.win.body).getAnchorXY();
//
//                    rec.set("xpos", x - PanelPos[0]);
//                    rec.set("ypos", y - PanelPos[1]);
//                }
//            });
//        }, this);
		
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
			} , {
				text : 'test',
				
			}]
        });
		var xy = event.getXY();
		ctxMenu.showAt(xy);
    }
    

});
