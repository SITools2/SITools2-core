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
    cls : 'modulesDataview modulesDataview-bg',
    bodyCls : 'modulesDataview-bg',
    modal : true,
    
    initComponent : function () {
    	
    	this.renderTo = Desktop.getDesktopEl();
    	
    	this.width = (Desktop.getDesktopEl().getWidth() * 2) / 2.5,
    	this.height = (Desktop.getDesktopEl().getHeight() * 2) / 3,
        
    	this.store = Ext.data.StoreManager.lookup("ModulesStore");

        this.dataview = Ext.create('Ext.view.View', {
            store: this.store,
            itemSelector: 'div.moduleDv',
            tpl: new Ext.XTemplate (
                '<tpl for=".">',
                        '<div class="moduleDv">',
                        	'<div class="moduleDvImg {icon}" height=72>',
//                        		'<img src="/sitools/common/res/images/sitools-logo-big.png" height=72 />',
//                        		'<div class="{icon}" height=72> </div>',
                        	'</div>',
	                        '<div class="moduleDvText">',
	                        	'{name}',
	                    	'</div>',
                        '</div>',
                '</tpl>'
                )
        });
        
        
        var versionButton = Ext.create('Ext.Button', {
        	name : 'versionBtn',
            iconCls : 'version-icon-dv', 
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
        
//        Ext.create('Ext.fx.Anim', {
//        	target: this,
//        	easing : 'easeIn',
//        	duration: 300,
//        	from: {
//        		opacity: 0,
//        		width : this.width / 2,
//        		height : this.height / 2
//        	},
//        	to: {
//        		opacity: 1,
//        		width : this.width,
//        		height : this.height
//        	}
//        });
        
        this.callParent();
    }
    
});
