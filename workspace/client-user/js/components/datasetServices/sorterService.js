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
 * Window that contains a tools to sort a store
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you can do a sort. 
 * @class sitools.user.component.dataviews.services.sorterService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.sorterService = Ext.extend(Ext.Window, {
    
    initComponent : function () {
        
        var dataCombo = [ [ "", "" ] ];
        var columns;
        if (! Ext.isEmpty(this.columnModel.columns)) {
            columns = this.columnModel.columns;
        }
        else {
            columns = this.columnModel;
        }
        Ext.each(columns, function (column) {
            if (!column.isSelectionModel) {
                dataCombo.push([ column.columnAlias, column.header ]);
            }
        });

        this.storeCombo = new Ext.data.ArrayStore({
            fields : [ "dataIndex", "columnHeader" ],
            data : dataCombo
        });

        this.f = new Ext.form.FormPanel({
            padding : 5
        });
        var sorters = this.store.getSortState(), len;
        if (Ext.isEmpty(sorters) && !Ext.isEmpty(this.dataview.storeSort)) {
            this.sorters = (!Ext.isEmpty(this.dataview.storeSort.sorters)) ? this.dataview.storeSort.sorters : this.dataview.storeSort;
        }
        if (! Ext.isEmpty(sorters)) {
            if (Ext.isArray(sorters.sorters)) {
                this.sorters = sorters.sorters;
            }
            else {
                this.sorters = [sorters];
            }
            len = this.sorters.length;
        }      
        else {
            this.sorter = [];
            len = 3;
        }
        
        for (var i = 0; i < len; i++) {
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
                scope : this,
                handler : this.onAdd
            }, {
                text : i18n.get('label.remove'),
                scope : this,
                handler : this.onRemove
            }, {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        });
        sitools.user.component.dataviews.services.sorterService.superclass.initComponent.call(this);
    },
    /**
     * Build a sorters objetct to apply it to a store.
     */
    onValidate : function () {
        
        var sorters = [];
        Ext.each(this.f.items.items, function (compositeField) {
            if (!Ext.isEmpty(compositeField.items.items[0].getValue())) {
                sorters.push({
                    field : compositeField.items.items[0].getValue(),
                    direction : compositeField.items.items[1].getValue().getGroupValue()
                });
            }
        }, this); 
    
        if (sorters.length < 1) {
            this.close();
            return;
        }
        else if (sorters.length === 1) {
            this.store.sort(sorters[0].field, sorters[0].direction);
        }
        else {
            this.store.sort(sorters);
        }
        //clear the selections
        this.dataview.getSelectionModel().clearSelections();
        this.close();
    },
    /**
     * Add a new sort option. 
     * @param {numeric} i the index of the sort option
     * @return {Ext.form.CompositeField} the composite field with a comboBox to choose the column and a sort information.
     */
    buildCompositeField : function (i) {
        var combo = new Ext.form.ComboBox({
            typeAhead : true,
            name : "field" + 1,
            triggerAction : 'all',
            lazyRender : true,
            mode : 'local',
            store : this.storeCombo,
            valueField : 'dataIndex',
            displayField : 'columnHeader',
            flex : 0.6
        });

        var direction = new Ext.form.RadioGroup({
            fieldLabel : i18n.get('label.direction'),
            flex : 0.4,
            value : 'ASC',
            items : [ {
                boxLabel : 'ASC',
                name : 'direction' + i,
                inputValue : 'ASC'
            }, {
                boxLabel : 'DESC',
                name : 'direction' + i,
                inputValue : 'DESC'
            } ]
        });
        var compositeField = new Ext.form.CompositeField({
            labelWidth : 100,
            anchor : '100%',
            fieldLabel : i18n.get('label.sortingOrder'),
            items : [ combo, direction ]
        });
        return compositeField;
    }, 
    /**
     * Called on add button : 
     * Adds a new sort option. 
     */
    onAdd : function () {
        var i = this.f.items.length;
        var compositeField = this.buildCompositeField(i);
        this.f.add(compositeField);
        this.f.doLayout();
        this.doLayout();
    }, 
    /**
     * Called when remove button is pressed.
     * Remove the last sort option.
     */
    onRemove : function () {
        var i = this.f.items.length - 1;
        if (i < 0) {
            return;
        }
        this.f.remove(this.f.getComponent(i));
        this.f.doLayout();
        this.doLayout();
    }, 
    /**
     * Fill the panel with the sort configuration of the store.
     */
    afterRender : function () {
        sitools.user.component.dataviews.services.sorterService.superclass.afterRender.call(this);
        this.setPosition(this.dataview.pos);
        var i = 0;
        Ext.each(this.sorters, function (sorter) {
            var compositeField = this.f.getComponent(i);
            compositeField.items.items[0].setValue(sorter.field);
            compositeField.items.items[1].setValue(sorter.direction);
            i++;
        }, this); 
    }
});
Ext.reg('sitools.user.component.dataviews.services.sorterService', sitools.user.component.dataviews.services.sorterService);
sitools.user.component.dataviews.services.sorterService.getParameters = function () {
    return [];
};

sitools.user.component.dataviews.services.sorterService.executeAsService = function (config) {
    var sorterTool = new sitools.user.component.dataviews.services.sorterService(config);
    sorterTool.show();
};

