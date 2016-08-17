 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

/**
 * Dataset Overview controller
 *
 * @class sitools.user.controller.component.datasets.overview.OverviewController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.user.controller.component.datasets.plot.DataPlotterController', {
    extend: 'Ext.app.Controller',

    views: ['component.datasets.plot.DataPlotterView'],

    requires: [],

    listeners : {
        drawplot : function (controller, view) {
            view.down("button#savePlot").setDisabled(false);
        }
    },

    init: function () {

        this.control({
            'dataPlotterView textfield#titleX, dataPlotterView textfield#titleY, dataPlotterView textfield#titlePlot, dataPlotterView textfield#xFormat, dataPlotterView textfield#yFormat': {
                keyup: this.handlePlotLayout
            },
            'dataPlotterView checkbox#checkLine': {
                change: this.handlePlotLayout
            },
            'dataPlotterView combobox#comboTag': {
                select: this.handlePlotLayout
            },
            'dataPlotterView colorfield': {
                select: this.handlePlotLayout
            },
            'dataPlotterView combo#comboX': {
                change: function (combo, newValue, oldValue) {
                    var view = combo.up("dataPlotterView"),
                        record = combo.getStore().findRecord("columnAlias", newValue);

                    view.titleX.setValue(combo.getValue());
                    var extType = sql2ext.get(record.get("sqlColumnType"));
                    var xFormat = view.down("textfield#xFormat");
                    if (extType.match(/dateAsString/gi) !== null) {
                        if (Ext.isEmpty(xFormat)) {
                            xFormat = Ext.create("Ext.form.field.Text", {
                                fieldLabel: i18n.get('label.plot.form.xFormat'),
                                anchor: "100%",
                                name: "xFormat",
                                itemId: 'xFormat',
                                labelWidth: view.labelWidth,
                                value: view.userPreference && view.userPreference.xFormat ? view.userPreference.xFormat : SITOOLS_DEFAULT_IHM_DATE_FORMAT
                            });
                            view.fieldSetX.insert(1, xFormat);
                        }
                    }
                    else {
                        if (!Ext.isEmpty(xFormat)) {
                            view.fieldSetX.remove(xFormat);
                        }
                    }
                },
                expand: function (combo) {
                    var view = combo.up("dataPlotterView");
                    combo.store.clearFilter(true);
                    if (view.comboY.getValue() !== '' && view.comboY.getValue() !== null) {
                        combo.store.filterBy(function (record, id) {
                            return record.get('columnAlias') !== view.comboY.getValue();
                        }, this);
                    }

                }
            },
            'dataPlotterView combo#comboY': {
                change: function (combo, newValue, oldValue) {
                    var view = combo.up("dataPlotterView"),
                        record = combo.getStore().findRecord("columnAlias", newValue);

                    view.titleY.setValue(combo.getValue());
                    var extType = sql2ext.get(record.get("sqlColumnType"));
                    var yFormat = view.down("textfield#yFormat");
                    if (extType.match(/dateAsString/gi) !== null) {
                        if (Ext.isEmpty(yFormat)) {
                            yFormat = Ext.create("Ext.form.field.Text", {
                                fieldLabel: i18n.get('label.plot.form.yFormat'),
                                anchor: "100%",
                                name: "yFormat",
                                itemId: 'yFormat',
                                labelWidth: view.labelWidth,
                                value: view.userPreference && view.userPreference.yFormat ? view.userPreference.yFormat : SITOOLS_DEFAULT_IHM_DATE_FORMAT
                            });
                            view.fieldSetY.insert(1, yFormat);
                        }
                    }
                    else {
                        if (!Ext.isEmpty(yFormat)) {
                            view.fieldSetY.remove(yFormat);
                        }
                    }
                },
                expand: function (combo) {
                    var view = combo.up("dataPlotterView");
                    combo.store.clearFilter(true);
                    if (view.comboX.getValue() !== '' && view.comboX.getValue() !== null) {
                        combo.store.filterBy(function (record, id) {
                            return record.get('columnAlias') !== view.comboX.getValue();
                        }, this);
                    }

                }
            },
            'dataPlotterView button#drawplot': {
                click: function (button, e) {
                    var view = button.up("dataPlotterView");
                    var pageSize;
                    if (!view.isSelection) {
                        pageSize = view.leftPanel.down("numberfield#nbRecords").getValue();
                    } else {
                        pageSize = view.selectionSize;
                    }

                    view.getEl().mask(i18n.get('label.drawingPlot'), "x-mask-loading");
                    if (pageSize > view.maxWarningRecords) {
                        Ext.Msg.show({
                            title: i18n.get("label.warning"),
                            msg: Ext.String.format(i18n.get("label.plot.toManyRecordsAsked"), pageSize, view.maxWarningRecords),
                            buttons: Ext.Msg.YESNO,
                            icon: Ext.MessageBox.WARNING,
                            scope: this,
                            fn: function (buttonId) {
                                if (buttonId === 'yes') {
                                    this.loadPlot(pageSize, view);
                                } else {
                                    view.getEl().unmask();
                                }
                            }
                        });
                    } else {
                        this.loadPlot(pageSize, view);
                    }
                }
            },
            'dataPlotterView panel#rightpanel': {
                resize: function (panel, width, height) {
                    var view = panel.up("dataPlotterView");
                    if (view.isVisible() && view.hasPlotted && view.storeData.getCount() > 0) {
                        this.displayPlot(view.storeData.getRange(), view);
                    }
                },
                afterrender: function (panel) {
                    var view = panel.up("dataPlotterView");
                    // create a new loadingMask
                    this.loadMask = new Ext.LoadMask(view.rightPanel.getEl(), {
                        msg: i18n.get("label.plot.waitForPlot"),
                        store: view.storeData
                    });

                    view.storeData.on("load", function (store, records, options) {
                        this.displayPlot(records, view);
                    }, this);

                }
            },
            'dataPlotterView form#leftPanel': {
                validitychange: function (panel, valid) {
                    var view = panel.owner.up("dataPlotterView");
                    if (valid && (view.comboX.getValue() !== view.comboY.getValue())) {
                        view.drawPlotButton.setDisabled(false);
                    } else {
                        view.drawPlotButton.setDisabled(true);
                    }
                }
            },
            'dataPlotterView': {
                afterrender: function (view) {
                    if (Ext.isEmpty(view.userPreference)) {
                        return;
                    }

                    var record, idx,
                        preferences = view.userPreference.leftPanelValues;
                    view.leftPanel.getForm().setValues(preferences);
                }
            },
            'dataPlotterView button#savePlot': {
                click : function (btn) {
                    var view = btn.up("dataPlotterView");
                    view.plot.download.saveImage("png");
                }
            }
        });
    },

    /**
     * Handler to change the plot layout.
     * Should be used in a form param event
     */
    handlePlotLayout: function (cmp) {
        var view = cmp.up("dataPlotterView");
        if (view.hasPlotted) {
            this.displayPlot(view.storeData.getRange(), view);
        }
    },

    /**
     * @private
     * Main function to display the plot by getting the data from the server
     * @param {int} pageSize the number of records to get
     */
    loadPlot: function (pageSize, view) {
        var form = view.leftPanel.getForm();

        if (!this.isSelection) {
            view.storeData.pageSize = pageSize;
        }

        view.storeData.load({
            scope : this,
            params: {
                start: 0,
                limit: pageSize
            },
            callback : function () {
                view.getEl().unmask();
            }
        });
    },

    /**
     * @private
     * Main function to display the plot directly from its data. Without a server call
     * @param {Array} records the list records to show in the plot
     */
    displayPlot: function (records, view) {
        if (view.isSelection) {
            view.rightPanel.down('tbtext#plot-tb-text').setText(Ext.String.format(i18n.get("label.plot.displayNbRecords"),
                records.length));
        }
        view.rightPanel.down("toolbar#bottom").setVisible(true);

        view.storeData.isFirstCountDone = true;
        var plotConfig = this.getPlotConfig(view.columnModel, records, view);
        view.plot = Flotr.draw($(view.rightPanel.body.id), [plotConfig.data], plotConfig.config);
        $(view.rightPanel.body.id).stopObserving('flotr:click');
        $(view.rightPanel.body.id).observe('flotr:click', this.handleClick.bind(this, [plotConfig, view]));
        $(view.rightPanel.body.id).stopObserving('flotr:select');
        $(view.rightPanel.body.id).observe('flotr:select', function (evt) {
            var area = evt.memo[0];
            var options = plotConfig.config;
            options.xaxis.min = area.x1;
            options.xaxis.max = area.x2;
            options.yaxis.min = area.y1;
            options.yaxis.max = area.y2;
            view.plot = Flotr.draw($(this.id), [plotConfig.data], options);
        });
        view.hasPlotted = true;
        this.fireEvent("drawplot", this, view);
    },

    /**
     * @private
     * Create the config for a plot and add the given data to it
     * @param {} columnModel the column model
     * @param {} newdata the list of records to plot
     * @return {} the config of the plot
     */
    getPlotConfig: function (columnModel, newdata, view) {
        var d1 = this.createData(columnModel, newdata, view);

        var xAxisFormat = "Normal";
        var colX = this.getColumn(columnModel, view.comboX.getValue());
        var colXType = sql2ext.get(colX.sqlColumnType);


        var plotConfig = {
            HtmlText: false,
            colors: ['#00A8F0', '#C0D800', '#cb4b4b', '#4da74d', '#9440ed'], // =>
            // The default colorscheme.When there are > 5 series, additional colors are generated.
            title: view.titlePlot.getValue(),
            legend: {
                show: true, // => setting to true will show the legend, hide
                // otherwise
                noColumns: 1, // => number of colums in legend table
                labelFormatter: null, // => fn: string -> string
                labelBoxBorderColor: '#ccc', // => border color for the
                // little label boxes
                container: null, // => container (as jQuery object) to put
                // legend in, null means default on top of
                // graph
                position: 'ne', // => position of default legend container
                // within plot
                margin: 5, // => distance from grid edge to default legend
                // container within plot
                backgroundColor: '#CCCCCC', // => null means auto-detect
                backgroundOpacity: 1.0
                // => set to 0 to avoid background, set to 1 for a solid background
            },
            xaxis: {
                ticks: null, // => format: either [1, 3] or [[1, 'a'], 3]
                noTicks: 5, // => number of ticks for automagically
                color: view.comboXColor.getValue() ? view.comboXColor.getValue() : "#000000",
                // generated ticks
                tickDecimals: null, // => no. of decimals, null means auto
                min: null, // => min. value to show, null means set
                // automatically
                max: null, // => max. value to show, null means set
                // automatically
                autoscaleMargin: 0, // => margin in % to add if auto-setting
                // min/max
                title: view.titleX.getValue(),
                mode: colXType == "dateAsString" ? "time" : "Normal",
                labelsAngle: colXType == "dateAsString" ? 45 : 0,
                timeFormat: view.xFormat ? view.xFormat.getValue() : SITOOLS_DATE_FORMAT

                // ,
                // scale : scaleTypeFromCheckBox(logX)
            },
            yaxis: {
                ticks: null, // => format: either [1, 3] or [[1, 'a'], 3]
                color: view.comboYColor.getValue() ? view.comboYColor.getValue() : "#000000",
                noTicks: 5, // => number of ticks for automagically
                // generated ticks
                tickDecimals: null, // => no. of decimals, null means auto
                min: null, // => min. value to show, null means set
                // automatically
                max: null, // => max. value to show, null means set
                // automatically
                autoscaleMargin: 0, // => margin in % to add if auto-setting
                // min/max
                title: view.titleY.getValue(),
                mode: colYType == "dateAsString" ? "time" : "Normal",
                labelsAngle: 0,
                timeFormat: SITOOLS_DATE_FORMAT

                // ,
                // scale : scaleTypeFromCheckBox(logY)
            },
            y2axis: {
                title: ' '
            },
            points: {
                show: true, // => setting to true will show points, false
                // will hide
                radius: 3, // => point radius (pixels)
                lineWidth: 2,
                fill: true, // => true to fill the points with a color,
                // false for (transparent) no fill
                fillColor: '#ffffff' // => fill color
            },
            lines: {
                show: view.checkLine.getValue(), // => setting to true will show
                // lines, false will hide
                lineWidth: 0.1, // => line width in pixels
                fill: false, // => true to fill the area from the line to the
                // x axis, false for (transparent) no fill
                fillColor: null
                // => fill color
            },
            grid: {
                color: '#545454', // => primary color used for outline and
                // labels
                backgroundColor: '#FFFFFF', // => null for transparent, else
                // color
                tickColor: '#dddddd', // => color used for the ticks
                labelMargin: 3
                // => margin in pixels
            },
            selection: {
                mode: 'xy', // => one of null, 'x', 'y' or 'xy'
                color: '#B6D9FF', // => selection box color
                fps: 10
                // => frames-per-second
            },
            spreadsheet: {
                show: false
            },
            mouse: {
                track: true, // => true to track the mouse, no tracking
                // otherwise
                position: 'se', // => position of the value box (default
                // south-east)
                margin: 2, // => margin in pixels of the valuebox
                color: '#ff3f19', // => line color of points that are drawn
                // when mouse comes near a value of a series
                trackDecimals: 1, // => decimals for the track values
                sensibility: 2, // => the lower this number, the more
                // precise you have to aim to show a value
                radius: 3,
                trackFormatter: (function (o) {
                    return this.getTagValueFromObject(o);
                }).bind(this)
                // => radius of the track point
            },
            shadowSize: 4
            // => size of the 'fake' shadow
        };

        if (colXType === "dateAsString") {
            var xFormat = view.down("textfield#xFormat").getValue();
            var xFormatter = Ext.bind(this.dateFormatter, this, [xFormat], true);
            plotConfig.xaxis.tickFormatter = xFormatter;
        }

        var yAxisFormat = "Normal";
        var colY = this.getColumn(columnModel, view.comboY.getValue());
        var colYType = sql2ext.get(colY.sqlColumnType);

        if (colYType === "dateAsString") {
            var yFormat = view.down("textfield#yFormat").getValue();
            var yFormatter = Ext.bind(this.dateFormatter, this, [yFormat], true);
            plotConfig.yaxis.tickFormatter = yFormatter;
        }

        var out = {
            data: d1,
            config: plotConfig
        };
        return out;
    },

    /**
     * Formater for the X axis
     */
    dateFormatter: function (value, c, format) {
        var dt = new Date();
        dt.setTime(value);
        return Ext.Date.format(dt, !Ext.isEmpty(format) ? format : SITOOLS_DEFAULT_IHM_DATE_FORMAT);
    },

    /**
     * @private
     * Create plot-able dataset from the store
     * @param {} columnModel the columnModel of the dataset
     * @param {} storeItems the records from the store
     * @return the plot-able data
     */
    createData: function (columnModel, storeItems, view) {
        var outData = [];
        Ext.each(storeItems, function (item) {
            var tag = view.comboTag.getValue() !== '' ? item.get(view.comboTag.getValue()) : null;
            var colXType = sql2ext.get(this.getColumn(columnModel, view.comboX.getValue()).sqlColumnType);
            var colYType = sql2ext.get(this.getColumn(columnModel, view.comboY.getValue()).sqlColumnType);
            var xValue, yValue;
            switch (colXType) {
                case "dateAsString" :
                    xValue = Ext.Date.parse(item.get(view.comboX.getValue()), SITOOLS_DATE_FORMAT, true);
                    if (!Ext.isEmpty(xValue)) {
                        xValue = xValue.getTime();
                    }
                    break;
                case "numeric" :
                    xValue = parseFloat(item.get(view.comboX.getValue()));
                    break;
                default :
                    xValue = item.get(view.comboX.getValue());
                    break;
            }
            switch (colYType) {
                case "dateAsString" :
                    var value = item.get(view.comboY.getValue());
                    yValue = Ext.Date.parse(value, SITOOLS_DATE_FORMAT, true);

                    if (!Ext.isEmpty(yValue)) {
                        yValue = yValue.getTime();
                    }
                    break;
                case "numeric" :
                    yValue = parseFloat(item.get(view.comboY.getValue()));
                    break;
                default :
                    yValue = item.get(view.comboY.getValue());
                    break;
            }
            outData.push([xValue, yValue, item.id, tag ? tag : item.id]);
        }, this);
        return outData;
    },
    /**
     * Get a column from the given columnModel corresponding to the given columnAlias
     * @param {} columnModel the list of columns
     * @param {} columnAlias the column alias to search for
     * @return {} the columnFound or undefined if not found
     */
    getColumn: function (columnModel, columnAlias) {
        var result;
        for (var i = 0; i < columnModel.length; i++) {
            if (columnModel[i].columnAlias == columnAlias) {
                result = columnModel[i];
            }
        }
        return result;
    },

    getIdFromObject: function (object) {
        var index = object.index;
        return object.series.data[index][2];
    },

    getTagValueFromObject: function (object) {
        var index = object.index;
        return object.series.data[index][3];
    },

    handleClick: function (params, evt, view) {
        var plotConfig = params[0];
        var view = params[1];
        var memo = evt.memo[1];
        var object = memo.prevHit;

        if (Ext.isEmpty(object)) {
            var id = memo.el.id;
            var options = plotConfig.config;
            options.xaxis.min = null;
            options.xaxis.max = null;
            options.yaxis.min = null;
            options.yaxis.max = null;
            this.plot = Flotr.draw($(id), [plotConfig.data], options);
        } else {
            var primaryKey = this.getIdFromObject(object);

            var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');

            //var openSearchView = grid.up('opensearchView');

            var config = {
                grid : view,
                selections : [view.storeData.getAt(object.index)],
                fromWhere : "plot",
                datasetId : view.datasetId,
                baseUrl : view.dataUrl + "/records",
                datasetUrl : view.dataUrl,
                datasetName : view.datasetName,
                preferencesPath : "/" + view.datasetName,
                preferencesFileName : "dataDetails"
            };

            var serviceObj = sitoolsController.openComponent("sitools.user.component.datasets.recordDetail.RecordDetailComponent", config);


            //this.showDataDetail(primaryKey);
            //alert("TODO");
        }
    }
});