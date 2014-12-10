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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Service used to build a window to define filter on a store using Filter API.  
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.services.FilterServiceView', {
    extend : 'Ext.window.Window',
    alias : 'widget.filterServiceView',
    
    layout : 'fit',
    
    initComponent : function () {

        var dataCombo = [ [ "", "" ] ];
        var columns;
        if (! Ext.isEmpty(this.columns)) {
            columns = this.columns;
        }
        else {
            columns = this.columnModel;
        }
        Ext.each(columns, function (column) {
            if (!column.isSelectionModel) {
                dataCombo.push({
                    columnAlias : column.columnAlias, 
                    columnHeader : column.text, 
                    columnType : sql2ext.get(column.sqlColumnType),
                    //add the date format
                    format : column.format
                });
            }
        });

        this.storeCombo = Ext.create('Ext.data.JsonStore', {
            fields : [ "columnAlias", "columnHeader", "columnType", "format" ],
            data : dataCombo
        });

        this.f = Ext.create("Ext.form.FormPanel", {
            padding : 5,
            forceFit : true,
            border : false,
            bodyBorder : false
        });
        
        this.filters = this.store.getGridFiltersCfg();
        
        var len;
        if (! Ext.isEmpty(this.filters)) {
            len = this.filters.length;
        }      
        else {
            this.filters = [];
            len = 3;
        }
        
        
        for (var i = 0; i < len; i++) {
            var compositeField = this.buildCompositeField(i);
            this.f.add(compositeField);
        }    

        Ext.apply(this, {
            title : i18n.get("label.filter"),
            autoScroll : true, 
            width : 450,
            modal : true, 
            items : [ this.f ],
            buttons : [ {
                text : i18n.get('label.add'),
                itemId : 'add'
            }, {
                text : i18n.get('label.remove'),
                itemId : 'remove'
            }, {
                text : i18n.get('label.ok'),
                itemId : 'ok'
            }, {
                text : i18n.get('label.cancel'),
                itemId : 'cancel'
            } ]
        });
        this.callParent(arguments);
    },
    getCompositeField : function (index) {
        return this.f.items.items[index];       
    }, 
    buildCompositeField : function (i) {
        var combo = Ext.create("Ext.form.ComboBox", {
            index : i,
            typeAhead : true,
            name : "field" + 1,
            triggerAction : 'all',
            lazyRender : true,
            queryMode : 'local',
            store : this.storeCombo,
            valueField : 'columnAlias',
            displayField : 'columnHeader',
            flex : 1
        });

        var filter = Ext.create("Ext.Container", {
            flex : 1,
            type : 'filter'
        });
        
        var compositeField = Ext.create("Ext.Container", {
            layout : {
                type : "hbox"
            },
            items : [ combo, filter ], 
            padding: 5
            
        });
        return compositeField;
    }
    
});