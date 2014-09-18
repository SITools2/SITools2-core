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
/*global Ext, sitools, window, showVersion, publicStorage, userLogin, projectGlobal, SitoolsDesk, showResponse, i18n, extColModelToJsonColModel, loadUrl*/

/**
 * @cfg {Array} modules la liste des modules
 * @class sitools.user.view.header.ModuleDataView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.header.ModuleDataView', {
    extend : 'Ext.window.Window',
    alias: 'widget.moduleDataview',
    
    layout : 'fit',
    border : false,
    bodyBorder : false,
    header : false,
    resizable : false,
    autoScroll : true,
    shadow : false,
    cls : 'modulesDataview modulesDataview-bg',
    bodyCls : 'modulesDataview-bg',
    modal : true,
    
    initComponent : function () {
    	
    	Ext.EventManager.onWindowResize(this.resizeDataview, this);
    	
    	this.renderTo = Desktop.getDesktopEl();
    	
    	this.width = (Desktop.getDesktopEl().getWidth() * 2) / 2.5,
    	this.height = (Desktop.getDesktopEl().getHeight() * 2) / 4,
        
    	this.store = Ext.data.StoreManager.lookup("ModulesStore");

        this.dataview = Ext.create('Ext.view.View', {
            store: this.store,
            itemSelector: 'div.moduleDv',
            tpl: new Ext.XTemplate (
                '<tpl for=".">',
                        '<div class="moduleDv">',
                        '<tpl if="!this.isEmpty(icon)">',
                        	'<div class="moduleDvImg {icon}" height=72></div>',
                    	'<tpl else>',
                    		'<div class="moduleDvImg">',
                    			'<img src="/sitools/common/res/images/sitools-logo-big.png" height=72 />',
                			'</div>',
                		'</tpl>',
	                        '<div class="moduleDvText">',
	                        	'{name}',
	                    	'</div>',
                        '</div>',
                '</tpl>',{
                	isEmpty : function (text) {
                		return Ext.isEmpty(text)
                	}
            }),
            listeners : {
            	render : function (view) {
            		view.tip = Ext.create('Ext.tip.ToolTip', {
            	        target: view.el,
            	        delegate: view.itemSelector,
            	        anchor : 'bottom',
            	        anchorOffset : 50,
            	        showDelay : 20,
						hideDelay : 50,
						dismissDelay : 0,
            	        renderTo: Ext.getBody(),
            	        listeners:{
            	            beforeshow: function updateTipBody(tip){
            	                tip.update(
            	                    view.getRecord(tip.triggerElement).get('description')
            	                );
            	            }
            	        }
            	    });
            	}
            }
            
        });
        
        
        var versionButton = Ext.create('Ext.Button', {
        	name : 'versionBtn',
            iconCls : 'version-icon-dv',
            cls : 'toolbarBtnModules',
            scale : 'large',
            listeners : {
				afterrender : function (btn) {
					var tooltipCfg = {
						html : i18n.get('label.version'),
						target : btn.getEl(),
						anchor : 'bottom',
						showDelay : 20,
						hideDelay : 50,
						dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        
        var helpButton = Ext.create('Ext.Button', {
        	name : 'helpBtn',
            iconCls : 'help-icon-dv',
            cls : 'toolbarBtnModules',
//            handler : SitoolsDesk.showHelp,
            scope : this, 
            handler : function () {
                alert('todo');
            },
            scale : 'large',
            listeners : {
				afterrender : function (btn) {
					var tooltipCfg = {
						html : i18n.get('label.help'),
						target : btn.getEl(),
						anchor : 'bottom',
						showDelay : 20,
						hideDelay : 50,
						dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        
        this.bbar = Ext.create('Ext.toolbar.Toolbar', {
        	cls : 'modulesDataview-bg',
        	layout:{
                pack: 'center'
            },
        	border : false,
        	items : [versionButton, helpButton]
        });
        
        this.listeners = {
        	scope : this,
        	render : function (wind) {
        		wind.getEl().slideIn('t', {
        	         easing: 'easeOut',
        	         duration: 300
        	     });
    	    }
    	};
        
        this.items = [this.dataview];
        
        this.mon(Ext.getBody(), 'click', function(el, e) {
        	this.close();
        }, this, { delegate: '.x-mask' });
        
        this.callParent();
    },
    
    resizeDataview : function (desktopWidth, desktopHeight) {
    	
    	var newWidth = (desktopWidth * 2) / 2.5;
    	var newHeight = (desktopHeight * 2) / 4;
    	
    	this.setWidth(newWidth);
    	this.setHeight(newHeight);
    	
    	this.center();
    	this.doComponentLayout();
    }
    
});
