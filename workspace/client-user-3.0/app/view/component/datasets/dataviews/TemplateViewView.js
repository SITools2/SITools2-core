/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/datasets/datasets.js"
 * @include "../../components/datasets/projectForm.js"
 */

/**
 * Datasets Module :
 * Displays All Datasets depending on datasets attached to the project.
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.view.component.datasets.dataviews.TemplateViewView', {
    extend: 'Ext.panel.Panel',

    mixins: {
        datasetView: 'sitools.user.view.component.datasets.dataviews.AbstractDataview'
    },

    alias: 'widget.templateView',
    layout: 'border',
    bodyBorder: false,
    border: false,
    componentType: 'datasetView',

    config: {
        ranges: null,
        nbRecordsSelection: null,
        isModifySelection: null
    },

    initComponent: function () {

        this.urlRecords = this.dataset.sitoolsAttachementForUsers;

        this.columnModel = getColumnModel(this.dataset.columnModel);

        this.origin = "sitools.user.view.component.datasets.dataviews.TemplateViewView";

        this.tbar = Ext.create("sitools.user.view.component.datasets.services.ServiceToolbarView", {
            enableOverflow: true,
            border: false,
            datasetUrl: this.dataset.sitoolsAttachementForUsers,
            columnModel: this.dataset.columnModel,
            datasetId: this.dataset.id,
            dataview: this,
            origin: this.origin
        });

        if (!Ext.isEmpty(this.storeSort)) {
            if (!Ext.isEmpty(this.storeSort.sorters)) {
                this.store.hasMultiSort = true;
                this.store.multiSortInfo = this.storeSort;
            } else {
                this.store.sortInfo = this.storeSort;
            }
        }

        //list of events to consider that everything is loaded
        this.allIsLoadedEvent = new Ext.util.MixedCollection();
        this.allIsLoadedEvent.add("load", false);
        this.allIsLoadedEvent.add("allservicesloaded", false);

        this.store.filters = Ext.create('sitools.public.widget.sitoolsFilter.FiltersCollection', {
            filters: this.filters
        });

        this.store.on("beforeload", function (store, options) {
            //set the nocount param to false.
            //before load is called only when a new action (sort, filter) is applied
            var params = options.params || {};
            var noCount;
            if (!store.isFirstCountDone || store.isNewFilter) {
                params.nocount = false;
            } else {
                params.nocount = true;
            }

            if (store.isNewFilter) {
                params.start = (this.startIndex) ? this.startIndex : 0;
                params.limit = DEFAULT_LIVEGRID_BUFFER_SIZE;
            }

            store.isNewFilter = false;
            options.params = params;

            if (!Ext.isEmpty(store.filters)) {
                var params = store.buildQuery(store.filters.getFilterData());
                Ext.apply(options.params, params);
            }

            store.appendOperationParam(options, store);
        }, this);

        this.store.on('load', function (store, records, options) {
            var plotComp = Ext.getCmp("plot" + this.dataset.id);
            if (plotComp) {
                var rightPanel = plotComp.findById('plot-right-panel');
                var success = rightPanel.fireEvent('buffer', store);
            }
            store.isFirstCountDone = true;
            this.down('serviceToolbarView').updateContextToolbar();
            this.processFeatureType();
        }, this);

        var tplString = '<tpl for="."><div class="thumb-wrap">';
        var style, maxColLength = 0, dataviewConfig = {};
        Ext.each(this.columnModel.items, function (col) {
            if (!col.hidden) {
                if (col.columnAlias.length > maxColLength) {
                    maxColLength = col.columnAlias.length;
                }
            }
        });

        var tplStringImg = '<ul>';

        maxColLength = (maxColLength + 3) * 7;
        style = Ext.String.format("width :{0}px", maxColLength);

        dataviewConfig = sitoolsUtils.arrayProperties2Object(this.dataset.datasetViewConfig);

        Ext.each(this.columnModel.items, function (col) {
            if (!col.hidden) {
                if (this.isColumnImage(col.columnRenderer)) {
                    tplStringImg += sitools.user.utils.DataviewUtils.getRendererDataView(col, style, dataviewConfig);
                } else {
                    tplString += sitools.user.utils.DataviewUtils.getRendererDataView(col, style, dataviewConfig);
                }
            }
        }, this);

        tplStringImg += '</ul>';
        tplString += Ext.String.format('<span><div class=x-view-entete style="{0}">{1} </div></span>', style, i18n.get("label.imagesAndServices"));
        tplString += '<span class="linkImageDataView"> ' + tplStringImg + '</span></div></tpl><div class="x-clear"></div>';

        var tpl = new Ext.XTemplate(
            tplString,
            {
                compiled: true,
                isEmpty: function (value) {
                    return Ext.isEmpty(value);
                },
                isNotEmpty: function (value) {
                    return !Ext.isEmpty(value);
                },
                isValidDate: function (value) {
                    try {
                        var dt = Date.parseDate(value, SITOOLS_DATE_FORMAT, true);
                        if (Ext.isEmpty(dt)) {
                            return false;
                        }
                        return true;
                    }
                    catch (err) {
                        return false;
                    }
                },
                columnModel: this.columnModel.items
            }
        );

        this.dataView = Ext.create('Ext.view.View', {
            itemId: 'dataviewRecords',
            store: this.store,
            tpl: tpl,
            multiSelect: true,
            overItemCls: 'x-view-over',
            itemSelector: 'div.thumb-wrap',
            selectedItemCls: 'x-view-selected',
            emptyText: '',
            autoScroll: true,
            simpleSelect: true,
            refresh: function () {
                this.getSelectionModel().deselectAll(false);
                var el = this.getTargetEl();
                var records = this.store.getRange(0, DEFAULT_LIVEGRID_BUFFER_SIZE);

                el.update('');
                if (records.length < 1) {
                    if (!this.deferEmptyText || this.hasSkippedEmptyText) {
                        el.update(this.emptyText);
                    }
                    this.all.clear();
                } else {
                    this.tpl.overwrite(el, this.collectData(records, 0));
                    this.all.fill(Ext.query(this.itemSelector, el.dom));
                    this.updateIndexes(0);

                    if (this.newDataLoaded) {
                        this.newDataLoaded = false;
                        this.fireEvent('newdataloaded');
                    }
                }
                this.hasSkippedEmptyText = true;
            },
            onDataRefresh: function () {
                this.newDataLoaded = true;
                this.refreshView();
            }
        });
        this.dataView.addEvents('newdataloaded');

        this.panelDetail = Ext.create('sitools.user.view.component.datasets.recordDetail.RecordDetailView', {
            title: i18n.get('label.detail'),
            collapseMode: 'mini',
            collapsible: true,
            collapsed: true,
            region: "east",
            split: true,
            fromWhere: "dataView",
            border: false,
            primaryKeyName: this.store.primaryKey,
            grid: this,
            baseUrl: this.dataset.sitoolsAttachementForUsers,
            boxMinWidth: 200,
            width: 500
        });

        var panelDataview = Ext.create('Ext.panel.Panel', {
            autoScroll: true,
            border: false,
            items: [this.dataView],
            region: 'center'
        });


        if (!Ext.isEmpty(this.userPreference) && this.userPreference.datasetView === "sitools.user.view.component.datasets.dataviews.TemplateViewView") {
            this.panelDetail.setWidth(this.userPreference.viewPanelDetailSize.width);
        }

        this.items = [panelDataview, this.panelDetail];
        this.dataviewUtils = sitools.user.utils.DataviewUtils;

        this.bbar = Ext.create('sitools.user.view.component.datasets.dataviews.paging.TemplatePagingToolbar', {
            store: this.store,       // grid and PagingToolbar using same store
            displayInfo: true,
            pageSize: DEFAULT_LIVEGRID_BUFFER_SIZE,
            items: [],
            listeners: {
                scope: this,
                change: function () {
                    if (this.isAllSelected()) {
                        this.selectAll();
                    }
                }
            }
        });

        this.store.load({
            params: {
                start: (this.startIndex) ? this.startIndex : 0,
                limit: DEFAULT_LIVEGRID_BUFFER_SIZE
            },
            scope: this
        });

        this.callParent(arguments);
    },

    //generic method
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons: function () {

        var iconCls = (this.isAllSelected()) ? "checkbox-icon-on" : "checkbox-icon-off";
        var pressed = this.isAllSelected();

        this.selectAllRowsBtn = Ext.create('Ext.button.Button', {
            name: "selectAll",
            iconCls: iconCls,
            enableToggle: true,
            scope: this,
            text: (pressed) ? i18n.get('label.deselectAll') : i18n.get('label.selectAll'),
            cls: 'services-toolbar-btn',
            pressed: pressed,
            handler: function (button, e) {
                if (button.pressed) {
                    this.selectAll();
                } else {
                    this.deselectAll();
                }
            },
            toggleHandler: function (button, pressed) {
                if (pressed) {
                    button.setIconCls("checkbox-icon-on");
                    button.setText(i18n.get('label.deselectAll'));
                } else {
                    button.setIconCls("checkbox-icon-off");
                    button.setText(i18n.get('label.selectAll'));
                }
            }

        });

        var array = [this.selectAllRowsBtn];
        return array;
    },

    getSelectedRecord: function (dataView) {
        var pageNumber = this.getBottomToolbar().inputItem.getValue();
        var selectedIndex = dataView.getSelectedIndexes()[0];
        if (Ext.isEmpty(selectedIndex)) {
            return null;
        }
        var storeIndex = (pageNumber - 1) * this.getBottomToolbar().pageSize + selectedIndex;
        var rec = dataView.getStore().getAt(storeIndex);
        return rec;
    },

    _getSettings: function () {
        return {
            objectName: "TemplateView",
            datasetName: this.datasetName,
            viewPanelDetailSize: this.panelDetail.getSize(),
            datasetView: "sitools.user.view.component.datasets.dataviews.TemplateViewView",
            datasetUrl: this.dataset.sitoolsAttachementForUsers,
            preferencesPath: this.preferencesPath,
            preferencesFileName: this.preferencesFileName
        };

    },

    getColumnModel: function () {
        return extColModelToStorage(this.columnModel);
    },

    getSelections: function () {
        return this.dataView.getSelectionModel().getSelection();
    },

    getSelectionModel: function () {
        return this.dataView.getSelectionModel();
    },

    getNbRowsSelected: function () {
        return this.getSelectionModel().getSelection().length;
    },

    getStore: function () {
        return this.store;
    },

    select: function (record, keepExisting, suppressEvent) {
        return this.dataView.getSelectionModel().select(record, keepExisting, suppressEvent);
    },

    getDataView: function () {
        return this.dataView;
    },

    getFilters: function () {
        return this.store.filters;
    },

    getRequestParam: function () {
        var request = "", formParams = {};

        var selections = this.getSelections();
        // First case : no records selected: build the Query
        if (Ext.isEmpty(selections)) {
            request += this.getRequestParamWithoutSelection();

        }
        else {
            if (this.isAllSelected()) {
                //First Case : all the dataset is selected.
                request = "&ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
                //We have to re-build all the request in case we use a range selection.
                request += this.getRequestParamWithoutSelection();
            } else {
                var recSelected;
                recSelected = selections;
                formParams = this.dataviewUtils.getFormParamsFromRecsSelected(recSelected);
                // use the form API to request the selected records
                request += "&" + Ext.urlEncode(formParams);
            }
        }
        return request;
    },

    /**
     * build the param that will represent the active selection.
     * @param [Ext.data.Record] recSelected the selected records
     * @returns {} this object contains the param that will use FORM API
     */
    getFormParamsFromRecsSelected: function (recSelected) {
        var rec = recSelected[0], result = {};
        var primaryKeyName = "";
        Ext.each(rec.fields.items, function (field) {
            if (field.primaryKey) {
                primaryKeyName = field.name;
            }
        });
        if (Ext.isEmpty(primaryKeyName)) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noPrimaryKey'));
            return;
        }
        // build the primaryKey Value
        var primaryKeyValues = [];
        Ext.each(recSelected, function (record) {
            primaryKeyValues.push(record.get(primaryKeyName));
        });

        // use the form API to request the selected records
        result["p[0]"] = "LISTBOXMULTIPLE|" + primaryKeyName + "|" + primaryKeyValues.join("|");
        return result;
    },

    getRequestParamWithoutSelection: function () {
        var result = "", formParams = {};

        // add the sorting params
        var storeSort = this.getStore().getSortState();
        if (!Ext.isEmpty(storeSort)) {
            var sort;
            if (!Ext.isEmpty(storeSort.sorters)) {
                sort = {
                    ordersList: storeSort.sorters
                };
            } else {
                sort = {
                    ordersList: [storeSort]
                };
            }
            result += "&sort=" + Ext.encode(sort);
        }

        // add the formParams
        var formParamsTmp = this.getStore().getFormParams(), i = 0;
        if (!Ext.isEmpty(formParamsTmp)) {
            Ext.each(formParamsTmp, function (param) {
                formParams["p[" + i + "]"] = param;
                i += 1;
            }, this);
            result += "&" + Ext.urlEncode(formParams);
        }
        return result;
    },

    getRequestParamWithoutColumnModel: function () {
        var request = "", formParams = {};

        var selections = this.getSelections();
        // First case : no records selected: build the Query
        if (Ext.isEmpty(selections)) {
            request += this.getRequestParamWithoutSelection();

        }
        else {
            if (this.isAllSelected()) {
                //First Case : all the dataset is selected.
                request = "&ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
                //We have to re-build all the request in case we use a range selection.
                request += this.getRequestParamWithoutSelection();
            } else {

                var recSelected;

                recSelected = selections;

                formParams = this.dataviewUtils.getFormParamsFromRecsSelected(recSelected);
                // use the form API to request the selected records
                request += "&" + Ext.urlEncode(formParams);
            }

        }
        return request;
    },

    getSelectionsRange: function () {
        var ranges = this.getAllSelections(true);
        return Ext.JSON.encode(ranges);
    },

    getSelectedIndexes: function () {
        var indexes = [],
            selected = this.getSelectionModel().selected.items,
            i = 0,
            len = selected.length;

        for (; i < len; i++) {
            indexes.push(selected[i].index);
        }
        return indexes;
    },

    getAllSelections: function (asRange) {
        var index = 1;
        var ranges = [];
        var currentRange = 0;
        var tmpArray = this.getSelectedIndexes();

        tmpArray.sort(function (o1, o2) {
            if (o1 > o2) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            } else {
                return 0;
            }
        });

        if (!asRange) {
            return tmpArray;
        }

        var max_i = tmpArray.length;

        if (max_i === 0) {
            return [];
        }

        ranges[currentRange] = [tmpArray[0], tmpArray[0]];
        for (var i = 0, max_i = max_i - 1; i < max_i; i++) {
            if (tmpArray[i + 1] - tmpArray[i] == 1) {
                ranges[currentRange][1] = tmpArray[i + 1];
            } else {
                currentRange++;
                ranges[currentRange] = [tmpArray[i + 1], tmpArray[i + 1]];
            }
        }
        return ranges;
    },

    isColumnImage: function (columnRenderer) {
        if (Ext.isEmpty(columnRenderer)) {
            return false;
        }
        switch (columnRenderer.behavior) {
            case ColumnRendererEnum.IMAGE_FROM_SQL:
            case ColumnRendererEnum.DATASET_ICON_LINK:
            case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE:
                return true;
            case ColumnRendererEnum.URL_LOCAL:
            case ColumnRendererEnum.URL_EXT_NEW_TAB:
            case ColumnRendererEnum.URL_EXT_DESKTOP:
                return !Ext.isEmpty(columnRenderer.image);
            default :
                return false;
        }
    },

    getSelectionForPlot: function () {
        return this.getRequestParam();
    },

    /**
     * @public
     * return the sortInfo of the TplView.
     * @return [] Array of sortInfo object
     */
    getSortInfo: function () {
        return this.store.sortInfo;
    },

    selectAll: function () {
        //this.dataView.getSelectionModel().selectRange(0, DEFAULT_LIVEGRID_BUFFER_SIZE, false, true);
        this.dataView.getSelectionModel().selectAll(true);
    },

    deselectAll: function () {
        this.dataView.getSelectionModel().deselectAll();
    },

    isAllSelected: function () {
        return (!Ext.isEmpty(this.selectAllRowsBtn) && this.selectAllRowsBtn.pressed);
    },

    /**
     * Get the unselected row from the list of all selected records and the
     * whole store
     *
     * @param selectedRecs
     *            the selected records
     * @param allRecs
     *            all the records from the store
     * @returns the unselected item, or null if not found
     */
    getUnselectedRow: function (selectedRecs, allRecs) {
        var unselected = null;
        Ext.each(allRecs, function (record) {
            if (selectedRecs.indexOf(record) === -1) {
                unselected = record;
                return;
            }
        }, this);
        return unselected;
    },

    getColumnFromColumnModel: function (columnAlias) {
        var index = Ext.each(this.columnModel.items, function (col) {
            if (col.columnAlias === columnAlias) {
                return false;
            }
        }, this);
        if (!Ext.isEmpty(index)) {
            return this.columnModel.items[index];
        } else {
            return null;
        }
    },

    callbackClickFeatureType: function (e, t, o) {
        e.stopEvent();
        var record = o.record;
        var controller = o.controller;
        var column = o.column;
        sitools.user.utils.DataviewUtils.featureTypeAction(column, record, controller, this.down('serviceToolbarView'));
    },

    selectRangeDataview: function (ranges) {
        Ext.each(ranges, function (range) {
            this.getSelectionModel().selectRange(range[0], range[1], true);
        }, this);
    },

    processFeatureType: function () {
        var nodes = this.dataView.getNodes();
        var controller = this.serviceController;
        Ext.each(nodes, function (node) {
            var record = this.dataView.getRecord(node);
            var featureTypeNodes = Ext.DomQuery.jsSelect(".featureType", node);
            Ext.each(featureTypeNodes, function (featureTypeNode) {
                var featureTypeNodeElement = Ext.get(featureTypeNode);

                var columnAlias = featureTypeNodeElement.getAttribute("column", "sitools");
                var column = this.getColumnFromColumnModel(columnAlias);

                featureTypeNodeElement.addListener("click", this.callbackClickFeatureType, this, {
                    record: record,
                    controller: controller,
                    column: column
                });
            }, this);
        }, this);
    }
});
