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
	position : 0,
	
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
        
        this.position = this.position + 1;
        
        this.zoneStore.add(new Ext.data.Record({
            id : aPanel.id,
            height : aPanel.height,
            position : this.position
        }));
    	
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
        
        if (Ext.isEmpty(this.zoneStore.data.items)){
	        this.zoneStore.add(new Ext.data.Record({
	            id : mainPanel.id,
	            height : mainPanel.height,
	            title : 'Main Panel',
	            position : 0
	        }));
        }
        
        if ( this.action == 'create'  ) {
        	this.y = 250;
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
	        		parent.y = parent.y + 210;
		        });

	        }

//        }

        this.doLayout();

		Ext.each(this.items.items, function (container) {
        	container.getEl().on('contextmenu', this._onContextMenu, container);
        }, this);
        
        
        /* loop on the panels */
        if (!Ext.isEmpty(this.items)){
        	
	        this.items.each(function(item){
	        	item.fireEvent('activate');
	        }, this);
	        
        }
        
		
    }, 
    
    
    _removePanel : function () {

    	console.log('remove panel');
    	
//    	var aPanel = new sitools.admin.forms.advancedFormPanel({
//    		title: 'advanced form',
//    		frame : true,
//    		height: 200,
//    		width: 200,
//    		ddGroup     : 'gridComponentsList',
//    		datasetColumnModel : this.datasetColumnModel,
//    		storeConcepts : this.storeConcepts, 
//    		formComponentsStore : this.formComponentsStore,
//    		absoluteLayout : this,
//    	});
//    	this.add(aPanel);
//    	
//    	// resize container panel if needed
//    	this.y = this.y + 200;
//    	if (this.y > 500) {
//    		this.formSize.height = this.y;
//    	}
//        var size = {
//           width : this.formSize.width,
//           height : this.formSize.height
//        };
//        this.setSize(size);
//    	
//    	this.doLayout();

    },
    
    _onContextMenu : function (event, htmlEl, options) {
		//ici le this est le container sur lequel on a cliqué. 
		event.stopEvent();
		var ctxMenu = new Ext.menu.Menu({
			items : [ {
				text : 'Delete this form', 
				scope : this, 
				handler : function(){
					this.deletePanel();
				}
			} ]
        });
		var xy = event.getXY();
		ctxMenu.showAt(xy);
    },

    

});
