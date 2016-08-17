/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.public.widget.ckeditor.sitoolsPlugins');
/**
 * datasetLink widget
 * 
 * @class sitools.widget.sitoolsEditorPlugins.moduleBrowser
 * @extends Ext.util.Observable
 */
Ext.define('sitools.public.widget.ckeditor.sitoolsPlugins.ModuleBrowser', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.moduleBrowser',    
    
    layout : 'fit',
    forceFit: true,
    initComponent : function () {
        
        this.urlModule = Project.sitoolsAttachementForUsers + loadUrl.get('APP_PROJECTS_MODULES_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'ajax',
	            url : this.urlModule,
                reader : {
                    type : 'json',
		            root : 'data'
                }
            },
            autoLoad : true,
            fields : [{
                name : 'id'
            }, {
                name : 'name'
            },{
                name : 'title'
            }, {
                name : 'description'
            }, {
                name : 'imagePath'
            },{
                name : 'icon'
            }, {
                name : 'xtype'
            }]
        });
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE',
            listeners : {
                scope : this,
	            select : function (rowModel, record, item) {
	                var btn = this.down('toolbar button[name="selectButton"]');
	                btn.setDisabled(false);
	            }
            }
        });
        
        this.columns = {
            items : [{
                width : 35,
                sortable : true,
                dataIndex : 'icon',
                renderer : function(value, metaData, record, rowIndex, colIndex, store) {
                    if (Ext.isEmpty(value)) {
                        return "";
                    }
//                    var imageReference = new Reference(value);
                    metaData.css += " " + value;
                    return "";
//                    return "<img src=" + imageReference.getFile() + " height=16 width=16 />";
                }
            }, {
                header : i18n.get('headers.name'),
                width : 170,
                sortable : true,
                dataIndex : 'name'
            }, {
                header : i18n.get('header.title'),
                width : 170,
                sortable : true,
                dataIndex : 'title'
            }]
        };
        
        this.bbar = ['->', {
            xtype : 'button',
            name : 'selectButton',
            text : i18n.get('label.select'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
            scope : this,
            disabled : true,
            handler : this.onValidate
        }];
        
        this.callParent(arguments);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function() {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };
    },

    onValidate : function() {
        var module = this.getSelectionModel().getSelection()[0];
        
        this.browseField.moduleTitle = module.data.title;
        this.browseField.moduleComponent = Ext.String.format("parent.sitools.user.utils.ModuleUtils.openModule(\"{0}\"); return false;", module.data.id);
        
        
        this.browseField.setValue('Module : ' + module.data.title);
        this.textField.setValue(module.data.title);
        
        this.up('window').close();
    }
});
