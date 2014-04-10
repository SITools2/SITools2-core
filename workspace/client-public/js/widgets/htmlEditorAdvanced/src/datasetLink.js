/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/* global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.widget.HtmlEditor');
/**
 * datasetLink widget
 * 
 * @class sitools.widget.HtmlEditor.datasetLink
 * @extends Ext.util.Observable
 */
Ext.define('sitools.widget.HtmlEditor.datasetLink', {
    extend : 'Ext.util.Observable',
	alias : 'widget.sitools.widget.HtmlEditor.datasetLink',
	init : function(cmp) {
//        this.cmp = cmp;
//        this.cmp.on('render', this.onRender, this);
		
		this.browseField = new Ext.form.TriggerField({
            name : 'datasetLink',
            fieldLabel : i18n.get('label.selectDatasetLink'),
            allowBlank : false,
            labelWidth : 100,
            editable : false,
            emptyText : i18n.get('label.openDatasetBrowser'),
            anchor : '100%',
            forceSelection: true,
            onTriggerClick : function () {
		        if (!this.disabled) {
		            var browser = new sitools.widget.HtmlEditor.datasetBrowser({
                        field : this
		            });
		            new Ext.Window({
		            	title : i18n.get('label.selectDatasetLink'),
		            	iconCls : 'x-edit-datasetLink',
		            	layout : 'fit',
		            	height : 300,
		            	width : 300,
		            	autoScroll : true,
		            	items : [browser]
		            }).show();
		        }
		    }
        });
        
        var sel;
		this.datasetLinkWindow = new Ext.Window({
			title : i18n.get('label.insertDatasetLink'),
			closeAction : 'hide',
			resizable : false,
			width : 400,
			height : 175,
			items : [{
				xtype : 'form',
				itemId : 'insert-datasetLink',
				border : false,
				plain : true,
				bodyStyle : 'padding: 10px;',
				labelWidth : 100,
				labelAlign : 'right',
				bbar : new Ext.ux.StatusBar({
		            text : i18n.get('label.ready'),
		            id : 'sbWinEditProfile',
		            iconCls : 'x-status-valid',
		            hidden : true
		        }),
				items : [{
							xtype : 'textfield',
							allowBlank : false,
							fieldLabel : i18n.get('label.text'),
							name : 'text',
							anchor : '100%',
							value : ''
						}, this.browseField ]
			}],
			buttons : [{
				text : i18n.get('label.insertDatasetLink'),
				handler : function() {
					var frm = this.datasetLinkWindow
							.getComponent('insert-datasetLink').getForm();
					if (frm.isValid()) {
						text = frm.findField('text').getValue(); 
						browseField = frm.findField('datasetLink');
						
						var html = Ext.String.format(browseField.dataLinkComponent + "{0}</a>", text);
						this.cmp.insertAtCursor(html);
						this.datasetLinkWindow.close();
					} else {
						var sb = Ext.getCmp('sbWinEditProfile');
						 sb.setStatus({
			                text : i18n.get('warning.checkForm'),
			                iconCls : 'x-status-error',
			                hidden : false
			            });
			            sb.setVisible(true);
			            return;
					}

				},
				scope : this
			}, {
				text : i18n.get('label.cancel'),
				handler : function() {
					this.datasetLinkWindow.close();
				},
				scope : this
			}]
		});
	},

	onRender : function() {
		this.btnToolbar = this.cmp.getToolbar().addButton({
			iconCls : 'x-edit-datasetLink',
			scope : this,
			handler : this.showDatasetLinkWindow,
			tooltip : i18n.get('label.insertDatasetLink')
		});
	},
	
	showDatasetLinkWindow : function () {
		if (!this.datasetLinkWindow.isDestroyed) {
			this.datasetLinkWindow.show();
		} 
		else {
			this.datasetLinkWindow.destroy();
			this.init(this.cmp);
			this.datasetLinkWindow.show();
		}
	}
	
});

