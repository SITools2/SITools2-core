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
 showHelp */
Ext.namespace('sitools.admin.forms');

Ext.define('sitools.admin.forms.WinParent', { 
    extend : 'Ext.Window',
    modal : true,
    title : i18n.get('label.chooseParent'),
    width : 500, 
    initComponent : function () {
        var storeComponents = this.store;
        var smComponents = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });
    
        var columns = {
            items : [ {
                header : i18n.get('headers.label'),
                dataIndex : 'label'
            }, {
                header : i18n.get('headers.type'),
                dataIndex : 'type'
            }],
            defaults : {
                sortable : true,
                width : 100
            }
        };
        this.gridFormComponents = Ext.create("Ext.grid.GridPanel", {
            title : i18n.get('title.gridComponents'),
            layout : 'fit', 
            id : "gridFormParentComponents",
            height : 430,
            store : storeComponents,
            columns : columns,
            selModel : smComponents,
            viewConfig : {
                forceFit : true
            }
        });
        
        this.items = [{
            xtype : 'panel', 
            layout : 'fit', 
            items : [this.gridFormComponents ], 
            buttons : [{
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.none'),
                scope : this,
                handler : this._none
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : this._close
            }]
        }];
        sitools.admin.forms.WinParent.superclass.initComponent.call(this);
    }, 
    onValidate : function () {
        if (!Ext.isEmpty(this.gridFormComponents.getSelectionModel().getLastSelected())) {
            var selected = this.gridFormComponents.getSelectionModel().getLastSelected(); 
            this.parentParamField.setValue(selected.get("id"));
            this.parentParamFieldDisplay.setValue(selected.get("label"));
            this.close();
        }
        
    }, 
    _close : function () {
        this.close();
    }, 
    _none : function () {
        this.parentParamField.setValue(null);
        this.parentParamFieldDisplay.setValue(null);
        this.close();
    }
    
});
