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
Ext.define('sitools.user.controller.component.datasets.services.SorterServiceController', {
    extend : 'Ext.app.Controller',
    
    init : function () {
        this.control({
            "sorterServiceView button#add" : {
                click : function (button) {
                    var view = button.up("sorterServiceView");
                    var i = view.f.items.length;
                    var compositeField = view.buildCompositeField(i);
                    view.f.add(compositeField);
                }
            },
            "sorterServiceView button#remove" : {
                click : function (button) {
                    var view = button.up("sorterServiceView");
                    var i = view.f.items.length - 1;
                    if (i < 0) {
                        return;
                    }
                    view.f.remove(view.f.getComponent(i));
                }
            }, 
            "sorterServiceView button#ok" : {
                click : function (button) {
                    var view = button.up("sorterServiceView");
                    var sorters = [];
                    var property;
                    var direction;
                    
                    Ext.iterate(view.f.getValues(), function (key, value) {
                        var index = key.substring(key.indexOf('_') + 1);
                        if (!Ext.isEmpty(value)) {
                            if (key.indexOf('field') != -1) {
                                property = value;
                            } else {
                                direction = value;
                            }
                        }
                        
                        if(!Ext.isEmpty(property) && !Ext.isEmpty(direction)) {
                            sorters[index] = {};
                            sorters[index].property = property;
                            sorters[index].direction = direction;
                            field = null;
                            direction = null;
                        }
                    });

                    view.store.sorters.clear();
                    if (sorters.length < 1) {
                        view.store.load();
                    } else if (sorters.length === 1) {
                        view.store.sort(sorters[0].property, sorters[0].direction);
                    } else {
                        view.store.sort(sorters);
                    }
                    // clear the selections
                    view.dataview.getSelectionModel().clearSelections();
                    view.close();
                }
            },
            "sorterServiceView button#cancel" : {
                click : function (button) {
                    var view = button.up("sorterServiceView");
                    view.close();
                }
            },
            "sorterServiceView" : {
                afterrender : function (view) {
                    view.fillSorts();
                }
            } 
        });
    }
});