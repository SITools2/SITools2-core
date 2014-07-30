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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.user.component.datasets.services');

/**
 * Window that contains a tools to sort a store
 * 
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you
 *      can do a sort.
 * @class sitools.user.component.datasets.services.SorterService
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.services.SorterServiceView', {
    extend : 'Ext.window.Window',
    alias : 'widget.sorterServiceView',
    layout : 'fit',
    initComponent : function () {

        var dataCombo = [ [ "", "" ] ];
        var columns;
        if (!Ext.isEmpty(this.columns)) {
            columns = this.columns;
        } else {
            columns = this.columnModel;
        }
        Ext.each(columns, function (column) {
            dataCombo.push([ column.columnAlias, column.text ]);
        });

        this.storeCombo = Ext.create("Ext.data.ArrayStore", {
            fields : [ "dataIndex", "columnHeader" ],
            data : dataCombo
        });

        this.f = Ext.create("Ext.form.FormPanel", {
            padding : 5,
            forceFit : true,
            border : false,
            bodyBorder : false,
        });
        // var sorters = this.store.getSortState(), len;
        var sorters = this.store.sorters, len;
        if (Ext.isEmpty(sorters) && !Ext.isEmpty(this.dataview.storeSort)) {
            this.sorters = (!Ext.isEmpty(this.dataview.storeSort.sorters)) ? this.dataview.storeSort.sorters : this.dataview.storeSort;
        }
        if (!Ext.isEmpty(sorters) && !Ext.isEmpty(sorters.items)) {
            if (Ext.isArray(sorters.items)) {
                this.sorters = sorters.items;
            } else {
                this.sorters = [ sorters ];
            }
            len = this.sorters.length;
        } else {
            this.sorter = [];
            len = 3;
        }

        for ( var i = 0; i < len; i++) {
            var compositeField = this.buildCompositeField(i);
            this.f.add(compositeField);
        }

        Ext.apply(this, {
            title : i18n.get("label.multiSort"),
            autoScroll : true,
            width : 400,
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
    /**
     * Add a new sort option.
     * 
     * @param {numeric}
     *            i the index of the sort option
     * @return {Ext.form.CompositeField} the composite field with a comboBox to
     *         choose the column and a sort information.
     */
    buildCompositeField : function (i) {
        var combo = Ext.create("Ext.form.ComboBox", {
            typeAhead : true,
            name : "field_" + i,
            triggerAction : 'all',
            lazyRender : true,
            queryMode : 'local',
            store : this.storeCombo,
            valueField : 'dataIndex',
            displayField : 'columnHeader',
            flex : 1
        });

        var direction = Ext.create("Ext.form.RadioGroup", {
            flex : 1,
            value : 'ASC',
            items : [ {
                boxLabel : 'ASC',
                name : 'direction_' + i,
                inputValue : 'ASC'
            }, {
                boxLabel : 'DESC',
                name : 'direction_' + i,
                inputValue : 'DESC'
            } ]
        });
        var compositeField = Ext.create("Ext.form.FieldContainer", {
            labelWidth : 100,
            anchor : '100%',
            fieldLabel : i18n.get('label.sortingOrder'),
            layout : 'hbox',
            items : [ combo, direction ]
        });
        return compositeField;
    },
    
    
    /**
     * Fill the panel with the sort configuration of the store.
     */
     fillSorts : function () {
        this.setPosition(this.dataview.pos);
        var i = 0;
        Ext.each(this.sorters, function (sorter) {
            var compositeField = this.f.getComponent(i);
            compositeField.items.items[0].setValue(sorter.property);
            var sort = {}
            sort['direction_' + i] =sorter.direction;
            compositeField.items.items[1].setValue(sort);
            i++;
        }, this);
    }
});