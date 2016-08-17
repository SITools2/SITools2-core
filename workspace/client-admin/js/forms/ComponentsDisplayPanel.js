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
 showHelp, loadUrl*/
/*
 * @include "absoluteLayoutProp.js"
 * @include "componentsListPanel.js"
 * @include "componentProp.js"
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
Ext.define('sitools.admin.forms.ComponentsDisplayPanel', { 
    extend : 'Ext.panel.Panel',
	y : 0,
	position : 0,
	padding : 5,
	border : false, 
	bodyBorder : false,
	bodyStyle : 'background-color : #EAEAEA;',
	requires : ['sitools.admin.forms.AdvancedFormPanel'],
	
	initComponent : function () {
        
        Ext.apply(this, {
			id : "absoluteLayout",
			layout : {
                type : 'vbox',
                align : 'stretch',
				padding : 5
            },
	        autoScroll : true,
	        height : this.formSize.height, 
	        width : this.formSize.width, 
	        listeners : {
	            scope : this,
	            activate : this._activeDisposition
	        }        
        });
        
        this.callParent(arguments);
	},

	_activeDisposition : function () {
        var formPincipal = this.up('window').down('form > textfield[name=css]');
        this.body.addCls(formPincipal.getValue());
        this.setWidth(this.formSize.width);
        
        this.removeAll(true);

        var totalHeight = 0;
        
        //add a first zone if none exists
        if (this.action != 'modify' && this.zoneStore.getCount() == 0) {
			var initialZone = {
				containerPanelId : Ext.id(),
				title : i18n.get('label.mainForm'),
				height : 300,
				collapsible : false,
				position : "0"
			};
		    this.zoneStore.add(initialZone);
		}
	        

        if (this.zoneStore.getCount() > 0) {
            this.zoneStore.each(function (rec) {
                totalHeight += rec.data.height;

                var zonePanel = Ext.create("sitools.admin.forms.AdvancedFormPanel", {
	                    containerPanelId : rec.data.containerPanelId,
	                    title: rec.data.title,
	                    height: rec.data.height,
	                    position : rec.data.position,
	                    collapsible : rec.data.collapsible,
	                    border: true,
	                    ddGroup : 'gridComponentsList',
	                    datasetColumnModel : this.datasetColumnModel,
	                    formComponentsStore : this.formComponentsStore,
	                    storeConcepts : this.storeConcepts,
	                    context : this.context,
	                    absoluteLayout : this
	                });
				this.add(zonePanel);
				
            }, this);
        }
        
        if (this.formSize.height <= totalHeight) {
			this.formSize.height = totalHeight + 30;
        	this.setHeight(totalHeight + 30);
		}
        
//        this.doLayout();

        /* loop on the panels */
        if (!Ext.isEmpty(this.items)) {
            this.items.each(function (item) {
                item.fireEvent('activate');
            }, this);

        }
    }
    
});
