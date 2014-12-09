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

Ext.namespace('sitools.user.controller.component.datasets.services');

/**
 * Window that contains a tools to filter a store
 * 
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you
 *      can do a sort.
 * @class sitools.user.component.datasets.services.FilterService
 * @extends Ext.Window
 */
Ext.define('sitools.user.controller.component.datasets.services.FilterServiceController', {
    extend : 'Ext.app.Controller',
    
    requires : ['sitools.public.widget.sitoolsFilter.StringFilter',
                'sitools.public.widget.sitoolsFilter.FiltersCollection',
                'sitools.public.widget.sitoolsFilter.DateFilter',
                'sitools.public.widget.sitoolsFilter.NumericFilter'],

    paramPrefix : "filter",
    
    init : function () {
        this.control({
            "filterServiceView" : {
                afterrender : function (view) {
                    view.setPosition(view.pos);
                    var i = 0;
                    Ext.each(view.filters, function (filter) {
                        var compositeField = view.f.getComponent(i);
                        this.updateFilterUI(compositeField, filter);
                        i++;
                    }, this);
                }
            },
            "filterServiceView combo" : {
                select : function (combo, record, index) {
                    if (Ext.isEmpty(record)) {
                        return;
                    }
                    var rec = record[0];
                    
                    if (Ext.isEmpty(rec.get("columnAlias"))) {
                        return;
                    }
                    var compositeField = combo.up('container');
                    var containerFilter = compositeField.down('container[type=filter]');
                    var filter;
                    containerFilter.removeAll();
                    switch (rec.get("columnType")) {
                    case ("numeric") :
                        filter = Ext.create("sitools.public.widget.sitoolsFilter.NumericFilter", {
                            columnAlias : rec.data.columnAlias
                        });
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        //containerFilter.syncSize();
                        break;
                    case ("string") :
                        filter = Ext.create("sitools.public.widget.sitoolsFilter.StringFilter", {
                            columnAlias : rec.data.columnAlias
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    case ("dateAsString") :
                        filter = Ext.create("sitools.public.widget.sitoolsFilter.DateFilter", {
                            columnAlias : rec.data.columnAlias,
                            format : rec.data.format
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    
                    default :
                        filter = Ext.create("sitools.public.widget.sitoolsFilter.StringFilter", {
                            columnAlias : rec.get("columnAlias")
                        });
                        
                        containerFilter.add(filter);
                        compositeField.setHeight(filter._getHeight());
                        containerFilter.setHeight(filter._getHeight());
                        break;
                    }                    
                }  
            },
            "filterServiceView button#add" : {
                click : function (button) {
                    var view = button.up('filterServiceView');
                    var i = view.f.items.length;
                    var compositeField = view.buildCompositeField(i);
                    view.f.add(compositeField);
                }
            },
            "filterServiceView button#remove" : {
                click : function (button) {
                    var view = button.up('filterServiceView');
                    var i = view.f.items.length - 1;
                    if (i < 0) {
                        return;
                    }
                    view.f.remove(view.f.getComponent(i));
                }  
            },
            "filterServiceView button#cancel" : {
                click : function (button) {
                    button.up('filterServiceView').close();
                }
            },
            "filterServiceView button#ok" : {
                click : function (button) {
                    var view = button.up('filterServiceView');
                    var servicefilters = Ext.create("sitools.public.widget.sitoolsFilter.FiltersCollection");
                    
                    var filters = [];
                    var filtersCfg = [];
                    var i = 0;
                    var store = view.store;
                    
                    
                    var filtersComponents = view.query('component[specificType=filter]');
                    Ext.each(filtersComponents, function (filter) {
                        //Build the filters for the store query
                        var filterValue = filter.getValue();                    
                        Ext.each(filterValue, function (filterValueItem) {
                            servicefilters.add(i++, filterValueItem);        
                            if (!Ext.isEmpty(filterValueItem)) {
                                filters.push(filterValueItem);
                            }
                        });
                        
                        if (!Ext.isEmpty(filter.getConfig())) {
                            filtersCfg.push(filter.getConfig());
                        }
                        
                        
                        
                    }, view);
                    
                    view.store.setGridFilters(servicefilters.getFilterData());
                    view.store.setGridFiltersCfg(filtersCfg);
                    

                    var options = view.store.lastOptions || {};
//                  
                    options.params = options.params || {};
                    
                    this.cleanParams(options.params);
                      
                    //var params = view.store.buildQuery(filters);
                    //set that view a new filter configuration
                    view.store.isNewFilter = true;
                    //Ext.apply(options.params, params);
                    //save the options has last options in the store
                    //view.store.storeOptions(options);
                    view.store.load(options);
                    //clear the selections
                    view.dataview.getSelectionModel().clearSelections();
                    view.close();
                }
            }
        });
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
    
    updateFilterUI : function (container, filter) {
        var combo = container.down("combobox");
        var store = combo.getStore();
        var rec = store.findRecord("columnAlias", filter.columnAlias);
        
        combo.setValue(filter.columnAlias);
        combo.fireEvent('select', combo, [rec]);
        
        
        var filterCmp = container.down("container[specificType=filter]");
        filterCmp.setValue(filter.value);
        
    }
});