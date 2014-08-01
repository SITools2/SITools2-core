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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.util');

/**
 * A Panel to show all the project modules in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the project modules data
 * @class sitools.admin.projects.modules.ProjectModulesCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.utils.DependenciesPanel', { 
    extend : 'Ext.grid.Panel',
    height : 180,
    forceFit : true,
    
    initComponent : function () {
        this.title = i18n.get('title.dependencies');

        this.store = new Ext.data.JsonStore({
            fields : [ {
                name : 'url',
                type : 'string'
            }],
            autoLoad : false
        });
        
        this.tbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [{
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreateDependencies
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteDependencies
            }]
        };
        
       
        this.columns = [{
            header : i18n.get('label.url'),
            dataIndex : 'url',
            editor : {
                xtype : 'textfield',
                allowBlank : false
            }
        }];
        
        this.sm = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });
        
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1,
            pluginId : 'cellEditing'
        })];
        
        sitools.admin.utils.DependenciesPanel.superclass.initComponent.call(this);
    },
    
    /**
     * Add a new Record to the dependencies property of a guiservice
     */
    onCreateDependencies : function () {
        this.getStore().insert(this.getStore().getCount(), {});
        var rowIndex = this.getStore().getCount() -1;
        
        this.getView().focusRow(rowIndex);
        
        this.getPlugin('cellEditing').startEditByPosition({
            row: rowIndex, 
            column: 0
        });
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteDependencies : function () {
        var selections = this.getSelectionModel().getSelection();
        this.getStore().remove(selections);
    }
});