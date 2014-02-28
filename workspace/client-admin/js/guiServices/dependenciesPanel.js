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
Ext.namespace('sitools.admin.util');

/**
 * A Panel to show all the project modules in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the project modules data
 * @class sitools.admin.projects.modules.ProjectModulesCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.util.DependenciesPanel', { extend : 'Ext.grid.plugin.RowEditing',

    height : 180,
    viewConfig : {
		forceFit : true
    },

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
            xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateDependencies
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteDependencies
            }]
        };
        
       
        this.columns = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('label.url'),
                dataIndex : 'url',
                editor : new Ext.form.TextField({
                    allowBlank : false
                })
            } ]
        });
        
        this.sm = Ext.create('Ext.selection.RowModel',{
            singleSelect : true
        });
        
        sitools.admin.util.DependenciesPanel.superclass.initComponent.call(this);
    },
    
    /**
     * Add a new Record to the dependencies property of a guiservice
     */
    onCreateDependencies : function () {
        this.getStore().insert(this.getStore().getCount(), {});
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteDependencies : function () {
        var s = this.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.getStore().remove(r);
        }
    }
});