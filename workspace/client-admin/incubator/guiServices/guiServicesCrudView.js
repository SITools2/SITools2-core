/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.guiServices');

sitools.admin.guiServices.guiServicesCrudView = Ext.extend(Ext.grid.GridPanel, {
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    sitoolsSelectorType : "guiServicesCrudView",
    sm : new Ext.grid.RowSelectionModel({
        singleSelect : true
    }),
    pageSize : 10,
    
    initComponent : function () {
    	
        this.cm = new Ext.grid.ColumnModel({
            defaults : {
                sortable : true
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 300,
                sortable : false
            }, {
                header : i18n.get('label.xtype'),
                dataIndex : 'xtype',
                width : 350,
                sortable : false
            }]
        });
        
        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                xtype : 's-menuButton',
                sitoolsSelectorType : 'guiServicesCrudView-button-create'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                xtype : 's-menuButton',
                sitoolsSelectorType : 'guiServicesCrudView-button-modify'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                xtype : 's-menuButton',
                sitoolsSelectorType : 'guiServicesCrudView-button-delete'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.view = new Ext.grid.GridView({
            forceFit : true
        });
        
        sitools.admin.guiServices.guiServicesCrudView.superclass.initComponent.call(this);

    }
     
});


