/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
sitools.user.component.dataviews.services.filterService = Ext.extend(Ext.Window, {
    paramPrefix : 'filter', 
    
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
                dataCombo.push({
                    columnAlias : column.columnAlias, 
                    columnHeader : column.header, 
                    columnType : sql2ext.get(column.sqlColumnType),
                    //add the date format
                    format : column.format
                });
            }
        });

        this.storeCombo = new Ext.data.JsonStore({
            fields : [ "columnAlias", "columnHeader", "columnType", "format" ],
            data : dataCombo
        });

        this.f = new Ext.Panel();
        
        this.filters = this.store.filtersCfg;
        
        if (Ext.isEmpty(this.filters) && !Ext.isEmpty(this.dataview.filtersCfg)) {
            this.filters = this.dataview.filtersCfg;
        }
        
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
        sitools.user.component.dataviews.services.filterService.superclass.initComponent.call(this);
    },
    getCompositeField : function (index) {
        return this.f.items.items[index];       
    }, 
    onValidate : function () {
        

        if (Ext.isEmpty(this.store.filters)) {
            this.store.filters = new sitools.widget.FiltersCollection();
        }
        
        this.store.filters.clear();
        
        var filters = [];
        var filtersCfg = [];
        var i = 0;
        var store = this.store;
        Ext.each(this.f.items.items, function (compositeField) {
            if (!Ext.isEmpty(compositeField.items.items[0].getValue())) {
                var filterCmp = compositeField.findBy(function (cmp) {
                    return cmp.specificType === "filter";
                });
                var filterValue;
                if (!Ext.isEmpty(filterCmp) && ! Ext.isEmpty(filterCmp[0])) {
                    var filter = filterCmp[0];
                    //Build the filters for the store query
                    filterValue = filter.getValue();                    
                    Ext.each(filterValue, function (filterValueItem) {
                        store.filters.add(i++, filterValueItem);        
                        if (!Ext.isEmpty(filterValueItem)) {
                            filters.push(filterValueItem);
                        }
                    });
                    if (!Ext.isEmpty(filter.getConfig())) {
                        filtersCfg.push(filter.getConfig());
                    }
                    
                }
            }
        }, this);
        
        this.store.filtersCfg = filtersCfg;
//      this.store.filters = filters;
        
        // var options = this.store.storeOptions() || {};
        var options = this.store.lastOptions || {};
        
//      
        options.params = options.params || {};
        
        this.cleanParams(options.params);
          
        //var params = this.store.buildQuery(filters);
        //set that this a new filter configuration
        this.store.isNewFilter = true;
        //Ext.apply(options.params, params);
        //save the options has last options in the store
        //this.store.storeOptions(options);
        this.store.load(options);
        //clear the selections
        this.dataview.getSelectionModel().clearSelections();
        this.close();
    },
     
    cleanParams : function (p) {
        // if encoding just delete the property
        if (this.encode) {
            delete p[this.paramPrefix];
        // otherwise scrub the object of filter data
        } else {
            var regex;
            regex = new RegExp('^' + this.paramPrefix + '\[[0-9]+\]');
            for (var key in p) {
                if (regex.test(key)) {
                    delete p[key];
                }
            }
        }
    },
    buildCompositeField : function (i) {
        var combo = new Ext.form.ComboBox({
            index : i,
            typeAhead : true,
            name : "field" + 1,
            triggerAction : 'all',
            lazyRender : true,
            mode : 'local',
            store : this.storeCombo,
            valueField : 'columnAlias',
            displayField : 'columnHeader',
            flex : 0.4, 
            listeners : {
                "select" : function (combo, rec, index) {
                    if (Ext.isEmpty(rec.data.columnAlias)) {
                        return;
                    }
                    var compositeField = combo.ownerCt;
                    var containerFilter = compositeField.items.items[1];
                    var filter;
                    containerFilter.removeAll();
                    switch (rec.data.columnType) {
                    case ("numeric") :
                        filter = new sitools.widget.NumericFilter({
                            columnAlias : rec.data.columnAlias
                        });
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        //containerFilter.syncSize();
                        break;
                    case ("string") :
                        filter = new sitools.widget.StringFilter({
                            columnAlias : rec.data.columnAlias
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    case ("dateAsString") :
                        filter = new sitools.widget.DateFilter({
                            columnAlias : rec.data.columnAlias,
                            format : rec.data.format
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    
                    default :
                        filter = new sitools.widget.StringFilter({
                            columnAlias : rec.data.columnAlias
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    }
                    
                }
            }
        });

        var filter = new Ext.Container({
            flex : 0.6
        });
        var compositeField = new Ext.Container({
            layout : "hbox",
            items : [ combo, filter ], 
            style: {
                padding: '5px'
            }
        });
        return compositeField;
    },
    onAdd : function () {
        var i = this.f.items.length;
        var compositeField = this.buildCompositeField(i);
        this.f.add(compositeField);
        this.f.doLayout();
        this.doLayout();
    }, 
    onRemove : function () {
        var i = this.f.items.length - 1;
        if (i < 0) {
            return;
        }
        this.f.remove(this.f.getComponent(i));
        this.f.doLayout();
        this.doLayout();
    }, 
    afterRender : function () {
        sitools.user.component.dataviews.services.filterService.superclass.afterRender.call(this);
        this.setPosition(this.pos);
        var i = 0;
        Ext.each(this.filters, function (filter) {
            var compositeField = this.f.getComponent(i);
            this.updateFilterUI(compositeField, filter);
            i++;
        }, this); 
    }, 
    updateFilterUI : function (container, filter) {
        var combo = container.items.items[0];
        var store = combo.getStore();
        var index = store.find("columnAlias", filter.columnAlias);
        var rec = null;
        if (index) {
            rec = store.getAt(index);
        }   
        
        combo.setValue(filter.columnAlias);
        combo.fireEvent('select', combo, rec, index);
        
        
        var filterCmp = container.findBy(function (cmp) {
            return cmp.specificType === "filter";
        });
        filterCmp[0].setValue(filter.value);
        
    }
});
sitools.user.component.dataviews.services.filterService.getParameters = function () {
    return [];
};

sitools.user.component.dataviews.services.filterService.executeAsService = function (config) {
    var filterTool = new sitools.user.component.dataviews.services.filterService(config);
    filterTool.show();
};

Ext.reg('sitools.user.component.dataviews.services.filterService', sitools.user.component.dataviews.services.filterService);
