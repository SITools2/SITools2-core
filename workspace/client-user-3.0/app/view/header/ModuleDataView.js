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
    cls : 'modulesDataview',
    bodyCls : 'modulesDataview',
    modal : true,
    
    initComponent : function () {
    	
    	this.renderTo = Desktop.getMainDesktop();
    	
    	this.width = (Desktop.getDesktopEl().getWidth() * 2) / 3,
    	this.height = (Desktop.getDesktopEl().getHeight() * 2) / 3,
        
    	this.store = Ext.getStore('ProjectStore').getProject().modulesStore;

        this.dataview = Ext.create('Ext.view.View', {
            store: this.store,
            itemSelector: 'div.moduleDv',
            tpl: [
                '<tpl for=".">',
                        '<div class="moduleDv">',
                        	'<img src="/sitools/common/res/images/sitools-logo-big.png" height=72 />',
	                        '<div class="moduleDvText">',
	                        	'{name}',
	                    	'</div>',
                        '</div>',
                '</tpl>',
            ]
        });
        
        this.items = [this.dataview];
        
        this.mon(Ext.getBody(), 'click', function(el, e){
        	this.hide();
        }, this, { delegate: '.x-mask' });
        
        this.callParent();
    },
  
});
