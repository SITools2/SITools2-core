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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

/**
 * Dataset Overview to display dataset on Fixed mode
 * @class sitools.user.view.component.datasets.overview.OverviewView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.component.datasets.plot.DataPlotterView', {
    extend: 'Ext.panel.Panel',

    requires: ["sitools.public.widget.item.ColorField"],

    alias: 'widget.dataPlotterView',

    bodyBorder: false,
    border: false,


    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {},

    labelWidth : 150,
    /**
     *
     */
    initComponent: function () {

        /** Variable to know if the plot has been done once */
        this.hasPlotted = false;

        /** Initial fields list */
        var initialFieldsStore = this.getNumericFields(this.columnModel);
        /** Point tag field list */
        var pointTagFieldsStore = this.getVisibleFields(this.columnModel);

        /**
         * Whether or not there was a selection
         */
        this.isSelection = !Ext.isEmpty(this.ranges);

        /** field for x axis label */
        this.titleX = Ext.create("Ext.form.field.Text", {
            fieldLabel: i18n.get('label.plot.form.xlabel'),
            anchor: "100%",
            name: "titleX",
            itemId : 'titleX',
            enableKeyEvents : true,
            labelWidth : this.labelWidth
        });

        /** field for x axis label */
        this.xFormat = null;

        /** field for y axis label */
        this.titleY = Ext.create("Ext.form.field.Text", {
            fieldLabel: i18n.get('label.plot.form.ylabel'),
            anchor: "100%",
            name: "titleY",
            itemId : 'titleY',
            enableKeyEvents : true,
            labelWidth : this.labelWidth
        });

        /** combobox for x field */
        this.comboX = Ext.create("Ext.form.field.ComboBox", {
            store: initialFieldsStore,
            anchor: "100%",
            name: "comboX",
            allowBlank: false,
            emptyText: i18n.get('label.plot.select.xaxis'),
            fieldLabel: i18n.get('label.plot.select.xcolumn'),
            selectOnFocus: true,
            triggerAction: 'all',
            valueField: "columnAlias",
            displayField: "columnAlias",
            editable: false,
            queryMode: 'local',
            itemId : 'comboX',
            labelWidth : this.labelWidth
        });

        /** combo box for y data */
        this.comboY = Ext.create("Ext.form.field.ComboBox", {
            store: initialFieldsStore,
            name: "comboY",
            allowBlank: false,
            anchor: "100%",
            emptyText: i18n.get('label.plot.select.yaxis'),
            fieldLabel: i18n.get('label.plot.select.ycolumn'),
            selectOnFocus: true,
            editable: false,
            valueField: "columnAlias",
            displayField: "columnAlias",
            triggerAction: 'all',
            queryMode: 'local',
            itemId : 'comboY',
            labelWidth : this.labelWidth
        });

        /** field for x axis label */
        this.titlePlot = Ext.create("Ext.form.field.Text", {
            anchor: "100%",
            fieldLabel: i18n.get('label.plot.form.title'),
            name: "titlePlot",
            itemId : 'titlePlot',
            enableKeyEvents : true,
            labelWidth : this.labelWidth
        });

        /** field for x axis label */
        var numberRecords = Ext.create("Ext.form.field.Number", {
            anchor: "100%",
            fieldLabel: i18n.get('label.plot.form.nbRecords'),
            name: "nbRecords",
            itemId : "nbRecords",
            value: DEFAULT_LIVEGRID_BUFFER_SIZE,
            disabled: this.isSelection,
            allowBlank: false,
            labelWidth : this.labelWidth
        });

        /** checkbox for drawing line */
        this.checkLine = Ext.create("Ext.form.field.Checkbox", {
            fieldLabel: i18n.get('label.plot.form.drawline'),
            name: "checkLine",
            itemId  : "checkLine",
            labelWidth : this.labelWidth
        });


        /** Combo box for tag title */
        this.comboTag = Ext.create("Ext.form.field.ComboBox", {
            store: pointTagFieldsStore,
            name: "comboTag",
            anchor: "100%",
            allowBlank: true,
            emptyText: i18n.get('label.plot.select.tag'),
            fieldLabel: i18n.get('label.plot.select.tagcolumn'),
            selectOnFocus: true,
            queryMode: 'local',
            scope: this,
            itemId : 'comboTag',
            labelWidth : this.labelWidth
        });

        this.comboXColor = Ext.create("sitools.public.widget.item.ColorField", {
            fieldLabel: i18n.get('label.plot.label.color'),
            anchor: "100%",
            name: "comboXColor",
            value: "#000000",
            labelWidth : this.labelWidth
        });

        this.comboYColor = Ext.create("sitools.public.widget.item.ColorField", {
            fieldLabel: i18n.get('label.plot.label.color'),
            anchor: "100%",
            name: "comboYColor",
            value: "#000000",
            labelWidth : this.labelWidth
        });

        this.fieldSetX = Ext.create("Ext.form.FieldSet", {
            title: i18n.get('title.plot.xAxis'),
            items: [this.comboX, this.titleX, this.comboXColor],
            collapsible: true
        });
        this.fieldSetY = Ext.create("Ext.form.FieldSet", {
            title: i18n.get('title.plot.yAxis'),
            items: [this.comboY, this.titleY, this.comboYColor],
            collapsible: true
        });


        var fields = this.getFields(this.columnModel);

        this.storeData = Ext.create("sitools.user.store.dataviews.DataviewsStore", {
            fields: fields,
            urlAttach: this.dataUrl,
            primaryKey: this.primaryKey,
            formFilters: this.formFilters,
            gridFilters: this.gridFilters,
            gridFiltersCfg: this.gridFiltersCfg,
            sortInfo: this.sortInfo,
            formConceptFilters: this.formConceptFilters,
            ranges : this.ranges,
            autoLoad : false
        });


        /** button to draw the plot */
        this.drawPlotButton = Ext.create("Ext.button.Button", {
            text: i18n.get('label.plot.draw'),
            disabled: true,
            itemId : 'drawplot'
        });

        /** button to draw the plot */
        this.savePlotButton = Ext.create("Ext.button.Button", {
            text: i18n.get('label.plot.savePng'),
            disabled: true,
            itemId : 'savePlot'
        });

        var bbar;
        if (this.isSelection) {
            bbar = Ext.create("Ext.toolbar.Toolbar", {
                hidden : true,
                itemId : "bottom",
                items : [ '->', {
                    itemId : 'plot-tb-text',
                    xtype : 'tbtext'
                } ]
            });
        } else {
            bbar = Ext.create("Ext.toolbar.Paging", {
                hidden : true,
                itemId : "bottom",
                store: this.storeData,       // grid and PagingToolbar using same store
                displayInfo: true
            });
        }


        /** right panel is the plot place */
        this.rightPanel = Ext.create("Ext.Panel", {
            //id : 'plot-right-panel',
            title: i18n.get('title.plot.panel'),
            //region : 'center',
            scope: this,
            itemId : 'rightpanel',
            flex: 1,
            bbar : bbar
        });


        /** left panel is a form */
        this.leftPanel = Ext.create("Ext.FormPanel", {
            title: i18n.get('title.plot.form'),
            width: 350,
            bodyPadding: 5,
            autoScroll: true,
            collapsible: true,
            collapseDirection: "left",
            items: [this.titlePlot, numberRecords, this.checkLine, this.comboTag, this.fieldSetX, this.fieldSetY],
            buttons: [this.savePlotButton, this.drawPlotButton],
            itemId : 'leftPanel'
        });

        var splitter = Ext.create("Ext.resizer.Splitter", {
            style: 'background-color:#EBEBEB;',
            itemId: 'eastSplitter'
        });

        this.items = [this.leftPanel, splitter, this.rightPanel];

        this.callParent(arguments);
    },

    /** function to get numeric fields */
    getVisibleFields: function (arrayFields) {
        var visibleFields = [];
        Ext.each(arrayFields, function (field) {
            if (!field.hidden) {
                visibleFields.push(field.columnAlias);
            }
        }, this);
        return visibleFields;
    },

    /** function to get numeric fields */
    getNumericFields: function (arrayFields) {
        var numericFields = [];
        var store = Ext.create("Ext.data.JsonStore", {
            proxy: {
                type: 'memory'
            },
            fields: [{
                name: "columnAlias",
                type: "string"
            }, {
                name: "sqlColumnType",
                type: "string"
            }]
        });
        Ext.each(arrayFields, function (field) {
            if (!Ext.isEmpty(field.sqlColumnType)) {
                var extType = sql2ext.get(field.sqlColumnType);
                if (extType.match(/^(numeric)+[48]?$/gi) !== null && !field.hidden) {
                    store.add(field);
                }
                if (extType.match(/dateAsString/gi) !== null && !field.hidden) {
                    store.add(field);
                }
            }
        }, this);

        return store;
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return this.component._getSettings();
    },

    getFields: function (listeColonnes) {
        var fields = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                fields[i] = Ext.create("Ext.data.Field", {
                    name: item.columnAlias,
                    primaryKey: item.primaryKey,
                    type: sql2ext.get(item.sqlColumnType)
                });
                if (sql2ext.get(item.sqlColumnType) === 'boolean') {
                    Ext.apply(fields[i], {
                        convert: function (value, record) {
                            if (value == "f" || value == "false" || value === 0) {
                                return 0;
                            }
                            if (value == "t" || value == "true" || value == 1) {
                                return 1;
                            }
                            return value;
                        }
                    });
                }
                i++;

            }, this);
        }
        return fields;
    }




});
