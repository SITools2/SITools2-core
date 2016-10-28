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
/*global Ext, sitools, i18n, alertFailure, window, loadUrl, sql2ext, SITOOLS_DEFAULT_IHM_DATE_FORMAT, ColumnRendererEnum, SITOOLS_DATE_FORMAT*/

Ext.namespace('sitools.user.view.component.datasets.recordDetail');

/**
 * Data detail Panel view.
 *
 * @cfg {string} fromWhere (required) :  "Ext.ux.livegrid" or "openSearch", "plot", "dataView"
 *       used to know how to determine the Url of the record
 * @cfg grid : the grid that contains all the datas
 * @cfg {string} baseUrl  used only in "data" case.
 *       used to build the url of the record. Contains datasetAttachement + "/records"
 * @class sitools.user.component.viewDataDetail
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.component.datasets.recordDetail.RecordDetailView', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.recordDetailView',
    datasetColumnModel: null,
    layout: 'fit',
    border: false,

    initComponent: function () {
        var rec;

        switch (this.fromWhere) {

            case "openSearch" :
                var osRecord = this.grid.getSelectionModel().getSelection()[0];
                this.url = this.encodeUrlPrimaryKey(osRecord.get('guid'));
                this.getRecordFromOpenSearch(osRecord);
                break;

            case "dataView" :
                break;

            case "plot" :
                this.selections = this.grid.storeData.data.items;
                this.recSelected = this.selections[0];

                var primaryKeyValue = "", primaryKeyName = "";
                Ext.each(this.recSelected.fields.items, function (field) {
                    if (field.primaryKey) {
                        this.primaryKeyName = field.name;
                    }
                }, this);

                this.primaryKeyValue = this.recSelected.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + "/" + this.primaryKeyValue;

                break;

            default :
                var selectedLines = this.selections;
                if (Ext.isEmpty(selectedLines) || selectedLines.length === 0) {
                    Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noSelection'));
                    return;
                }

                var record = selectedLines[0];
                if (Ext.isNumber(record)) {
                    this.recSelected = this.grid.getStore().getAt(record);
                } else {
                    this.recSelected = record;
                }

                var primaryKeyValue = "", primaryKeyName = "";
                Ext.each(this.recSelected.fields.items, function (field) {
                    if (field.primaryKey) {
                        this.primaryKeyName = field.name;
                    }
                }, this);

                this.primaryKeyValue = this.recSelected.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + this.primaryKeyValue;
                break;
        }

        // data with feature type link url and dataset link
        this.linkStore = Ext.create('Ext.data.Store', {
            proxy: {
                type: 'memory'
            },
            fields: ['name', 'value', 'image', 'toolTip', 'behavior', 'columnRenderer', 'html', 'column']
        });

        // data with feature type image from sql, column, no thumb
        this.imageStore = Ext.create('Ext.data.Store', {
            proxy: {
                type: 'memory'
            },
            fields: ['name', 'value', 'image', 'toolTip', 'behavior', 'columnRenderer', 'html', 'column']
        });

        // data without feature type or dataset link (text only)
        this.dataStore = Ext.create('Ext.data.Store', {
            proxy: {
                type: 'memory'
            },
            fields: ['name', 'value', 'image', 'behavior', 'columnRenderer', 'html', 'column']
        });

        this.gridDataview = Ext.create('Ext.grid.Panel', {
            store: this.dataStore,
            padding: 20,
            flex: 1,
            border: false,
            header: false,
            hideHeaders: true,
            rowLines: false,
            disableSelection: true,
            autoScroll: true,
            forceFit: true,
            columns: [{
                dataIndex: 'name',
                width: 120,
                renderer: function (value, metaData) {
                    metaData.style += 'font-weight:bold;';
                    value += ' :';
                    return value;
                }
            }, {
                dataIndex: 'value',
                flex: 1,
                resizable: true,
                renderer: function (value, metaData) {
                    //metaData.tdAttr = 'data-qtip="' + value + '"';
                    metaData.tdCls += 'x-selectable x-custom-td-grid-inner';
                    return value;
                }
            }],
            viewConfig: {
                stripeRows: false,
                trackOver: false
            },
            listeners: {
                scope: this,
                itemclick: function (grid, record, item, index) {
                    var column = record.get('column');
                    if (Ext.isEmpty(column) || Ext.isEmpty(column.columnRenderer))
                        return;

                    sitools.user.utils.DataviewUtils.executeFeatureType(column, this.recSelected);
                }
            }
        });

        this.metaDataPanel = Ext.create('Ext.panel.Panel', {
            title: i18n.get('label.metadataInfo'),
            padding: 10,
            layout: {
                type: 'vbox',
                align: 'stretch',
                defaultMargins: {
                    top: 5,
                    bottom: 5
                }
            },
            border: false,
            items: [{
                xtype: 'label',
                cls: 'recordDetailLabel',
                text: '',
                height: 30
            }, this.gridDataview]
        });

        var linkDataview = Ext.create('Ext.view.View', {
            store: this.linkStore,
            tpl: new Ext.XTemplate(
                '<ul>',
                '<tpl for=".">',
                '<li id="{name}" class="img-link">',
                '{html}',
                '<tpl if="this.hasToolTip(toolTip)">',
                '<p data-qtip="{toolTip}">{toolTip}</p>',
                '<tpl else>',
                '<p data-qtip="{name}">{name}</p>',
                '</tpl>',
                '</li>',
                '</tpl>',
                '</ul>',
                {
                    compiled: true,
                    disableFormats: true,
                    hasToolTip: function (toolTip) {
                        return !Ext.isEmpty(toolTip);
                    }
                }),
            mode: 'SINGLE',
            cls: 'linkImageDataView',
            itemSelector: 'li.img-link',
            overItemCls: 'nodes-hover',
            selectedClass: '',
            multiSelect: false,
            autoScroll: true,
            listeners: {
                scope: this,
                itemclick: this.handleClickOnFeature
            }
        });

        var imageDataview = Ext.create('Ext.view.View', {
            store: this.imageStore,
            tpl: new Ext.XTemplate(
                '<ul>',
                '<tpl for=".">',
                '<li id="{name}" class="img-link">',
                '{html}',
                '<tpl if="this.hasToolTip(toolTip)">',
                '<p data-qtip="{toolTip}">{toolTip}</p>',
                '<tpl else>',
                '<p data-qtip="{name}">{name}</p>',
                '</tpl>',
                '</li>',
                '</tpl>',
                '</ul>',
                {
                    compiled: true,
                    disableFormats: true,
                    hasToolTip: function (toolTip) {
                        return !Ext.isEmpty(toolTip);
                    }
                }),
            mode: 'SINGLE',
            cls: 'linkImageDataView',
            itemSelector: 'li.img-link',
            overItemCls: 'nodes-hover',
            selectedClass: '',
            multiSelect: false,
            autoScroll: true,
            listeners: {
                scope: this,
                itemclick: this.handleClickOnFeature

            }
        });

        // set the search form
        this.imagePanel = Ext.create('Ext.panel.Panel', {
            //title : ((this.fromWhere === 'dataView') ? i18n.get("label.formImagePanelTitle") : null),
            layout: 'fit',
            title: ((this.fromWhere === '') ? i18n.get("label.formImagePanelTitle") : i18n.get("label.imagesFromColumns")),
            //flex : 1,
            height: 190,
            autoScroll: true,
            items: [imageDataview]
            //split : (this.fromWhere !== 'dataView'),
            //collapsible : (this.fromWhere !== 'dataView'),
//            collapsed : (this.fromWhere !== 'dataView'),
        });

        // set the text form
        this.linkPanel = Ext.create('Ext.panel.Panel', {
            title: i18n.get("label.linkedMetaData"),
            layout: 'fit',
            items: [linkDataview],
            autoScroll: true,
            height: 190
        });

        // set the text form
        this.extraMetaPanel = Ext.create('Ext.panel.Panel', {
            title: i18n.get('label.complementaryInformation'),
            border: false,
            padding: 10,
            autoScroll : true,
            layout: {
                type: 'vbox',
                align: 'stretch',
                defaultMargins: {
                    top: 5,
                    bottom: 5
                }
            },
            items: [{
                xtype: 'label',
                cls: 'recordDetailLabel',
                text: '',
                height: 30
            }, this.linkPanel, this.imagePanel]
        });

//        var centerPanelItems;
//        if (this.fromWhere === 'dataView') {
////            centerPanelItems = [this.extraMetaPanel, this.imagePanel, this.linkPanel];
//            centerPanelItems = [this.gridDataview];
//        }
//        else {
////            centerPanelItems = [this.extraMetaPanel, this.linkPanel];
//            centerPanelItems = [this.gridDataview];
//        }

        this.tabPanel = Ext.create('Ext.tab.Panel', {
            border: false,
            items: [this.metaDataPanel]
        });

        // ???
        //this.componentType = 'dataDetail';

        if (this.fromWhere == 'dataView') {
            this.items = [this.tabPanel];
        }
        else {
//			this.items = [ this.tabPanel, this.imagePanel ];
            this.items = [this.tabPanel];
        }

        if (this.fromWhere != "plot") {
            this.bbar = {
                xtype: 'toolbar',
                border: false,
                items: [{
                    iconCls: 'arrow-back',
                    scale: 'medium',
                    scope: this,
                    listeners: {
                        afterrender: function (btn) {
                            var label = i18n.get('label.previousRecord');
                            var tooltipCfg = {
                                html: label,
                                target: btn.getEl(),
                                anchor: 'bottom',
                                showDelay: 20,
                                hideDelay: 50,
                                dismissDelay: 0
                            };
                            Ext.create('Ext.tip.ToolTip', tooltipCfg);
                        }
                    },
                    handler: function () {
                        this.goPrevious();
                    }
                }, {
                    iconCls: 'arrow-next',
                    scale: 'medium',
                    scope: this,
                    listeners: {
                        afterrender: function (btn) {
                            var label = i18n.get('label.nextRecord');
                            var tooltipCfg = {
                                html: label,
                                target: btn.getEl(),
                                anchor: 'bottom',
                                showDelay: 20,
                                hideDelay: 50,
                                dismissDelay: 0
                            };
                            Ext.create('Ext.tip.ToolTip', tooltipCfg);
                        }
                    },
                    handler: function () {
                        this.goNext();
                    }
                }]

            };
        }

        this.callParent(arguments);
    },

    afterRender: function (panel) {
        this.getCmDefAndbuildForm();
        this.callParent(arguments);
    },

    /**
     * Need to save the window Settings
     * @return {}
     */
    _getSettings: function () {
        return {
            objectName: "viewDataDetail",
            preferencesPath: this.preferencesPath,
            preferencesFileName: this.preferencesFileName
        };
    },

    /**
     * Selects the record immediately following the currently selected record.
     * @param {Boolean} [keepExisting] True to retain existing selections
     * @param {Boolean} [suppressEvent] Set to false to not fire a select event
     * @return {Boolean} `true` if there is a next record, else `false`
     */
    selectNext: function (keepExisting, suppressEvent) {
        var me = this.grid.getSelectionModel(),
            store = this.grid.store,
            selection = me.getSelection(),
            record = selection[selection.length - 1],
            index = me.views[0].indexOf(record) + 1,
            success;

        if (index === store.getCount() || index === 0) {
            success = false;
        } else {
            me.doSelect(index, keepExisting, suppressEvent);
            success = true;
        }
        return success;
    },

    /**
     * Selects the record that precedes the currently selected record.
     * @param {Boolean} [keepExisting] True to retain existing selections
     * @param {Boolean} [suppressEvent] Set to false to not fire a select event
     * @return {Boolean} `true` if there is a previous record, else `false`
     */
    selectPrevious: function (keepExisting, suppressEvent) {
        var me = this.grid.getSelectionModel(),
            selection = me.getSelection(),
            record = selection[0],
            index = me.views[0].indexOf(record) - 1,
            success;

        if (index < 0) {
            success = false;
        } else {
            me.doSelect(index, keepExisting, suppressEvent);
            success = true;
        }
        return success;
    },

    /**
     * Go to the Next record of the grid passed into parameters
     */
    goNext: function () {
        if (!this.grid.isVisible()) {
            return Ext.Msg.show({
                title: i18n.get('label.warning'),
                msg: i18n.get('label.gridNotFound'),
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }
        var rec, rowSelect;
        switch (this.fromWhere) {
            case "openSearch" :
                rowSelect = this.grid.getSelectionModel();
                //if (!rowSelect.selectNext()) {
                if (!this.selectNext()) {
                    return;
                }
                rec = rowSelect.getSelection()[0];
                this.url = this.encodeUrlPrimaryKey(rec.get('guid'));
                break;
            case "dataView" :
                this.recSelected = this.grid.getSelectionModel().getSelection()[0];
                var index = this.grid.getStore().indexOf(this.recSelected);
                var nextRec = this.grid.getStore().getAt(index + 1);
                if (Ext.isEmpty(nextRec)) {
                    return;
                }
                this.primaryKeyValue = nextRec.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + '/records/' + this.primaryKeyValue;
                this.recSelected = nextRec;
                this.grid.select(nextRec, false, true);
                //this.expand();
                break;
            default :
                rowSelect = this.grid.getSelectionModel();
                //if (!rowSelect.selectNext()) {
                if (!this.selectNext()) {
                    return;
                }
                rec = this.grid.getStore().getAt(rowSelect.getSelection()[0]) || rowSelect.getSelection()[0];
                this.primaryKeyValue = rec.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + this.primaryKeyValue;
                break;
        }

        this.getCmDefAndbuildForm();
    },

    /**
     * Go to the Previous record of the grid passed into parameters
     */
    goPrevious: function () {
        if (!this.grid.isVisible()) {
            return Ext.Msg.show({
                title: i18n.get('label.warning'),
                msg: i18n.get('label.gridNotFound'),
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }
        var rec, rowSelect;
        switch (this.fromWhere) {
            case "openSearch" :
                rowSelect = this.grid.getSelectionModel();
                if (!rowSelect.selectPrevious()) {
                    return;
                }
                rec = rowSelect.getSelection()[0];
                this.url = this.encodeUrlPrimaryKey(rec.get('guid'));
                break;
            case "dataView" :
                this.recSelected = this.grid.getSelectionModel().getSelection()[0];
                var index = this.grid.getStore().indexOf(this.recSelected);
                var nextRec = this.grid.getStore().getAt(index - 1);
                if (Ext.isEmpty(nextRec)) {
                    return;
                }
                this.primaryKeyValue = nextRec.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + '/records/' + this.primaryKeyValue;
                this.recSelected = nextRec;
                this.grid.select(nextRec, false, true);
                this.expand();
                break;
            default :
                rowSelect = this.grid.getSelectionModel();
                if (!rowSelect.selectPrevious()) {
                    return;
                }
                rec = this.grid.getStore().getAt(rowSelect.getSelection()[0]);
                this.primaryKeyValue = rec.get(this.primaryKeyName);
                this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
                this.url = this.baseUrl + this.primaryKeyValue;
                break;
        }

        this.getCmDefAndbuildForm();
    },
    /**
     * Build the form according with the values loaded via the Url
     */
    getCmDefAndbuildForm: function () {
        this.getEl().mask(i18n.get('label.loadingRecord'), "x-mask-loading");
        if (Ext.isEmpty(this.datasetColumnModel)) {
            Ext.Ajax.request({
                url: this.baseUrl,
                method: 'GET',
                scope: this,
                success: function (ret) {
                    try {
                        var Json = Ext.decode(ret.responseText);
                        if (!Json.success) {
                            throw Json.message;
                        }
                        this.datasetColumnModel = Json.dataset.columnModel;
                        this.buildForm();
                    }
                    catch (err) {
                        Ext.Msg.alert(i18n.get('label.error'), err);
                    }
                },
                failure: alertFailure
            });
        }
        else {
            this.buildForm();
        }
    },

    buildForm: function () {

        if (!Ext.isEmpty(this.url)) {

            this.linkStore.removeAll();
            this.dataStore.removeAll();
            this.imageStore.removeAll();

            Ext.Ajax.request({
                url: this.url,
                method: 'GET',
                scope: this,
                success: this.buildFormSuccess,
                callback: function () {
                    this.getEl().unmask();
                }
            });
        }
    },

    buildFormSuccess: function (ret) {
        var data = Ext.decode(ret.responseText);
        if (!data.success) {
            Ext.Msg.alert(i18n.get('label.information'), "Server error");
            return false;
        }
        var record = data.record;
        var id = record.id;
        var attributes = record.attributeValues;

        if (attributes !== undefined) {
            var i;
            for (i = 0; i < attributes.length; i++) {
                var name = attributes[i].name;

                var column = this.findColumn(name);
                var value = attributes[i].value;
                var valueFormat = value;

                // setting name of the record with its primary key value
                if (!Ext.isEmpty(column)) {
                    if (column.primaryKey) {
                        this.metaDataPanel.down('label').setText(Ext.String.format(i18n.get('label.metaFromRecord'), value));
                        this.extraMetaPanel.down('label').setText(Ext.String.format(i18n.get('label.metaFromRecord'), value));
                    }
                }

                if (sql2ext.get(column.sqlColumnType) == 'dateAsString') {
                    valueFormat = sitools.user.utils.DataviewUtils.formatDate(value, column);
                }
                if (sql2ext.get(column.sqlColumnType) == 'boolean') {
                    valueFormat = value ? i18n.get('label.true') : i18n.get('label.false');
                }

                var item = {
                    name: name,
                    value: value
                };

                if (Ext.isEmpty(column) || Ext.isEmpty(column.columnRenderer)) {
                    this.dataStore.add(item);
                }
                else {
                    if (Ext.isEmpty(column.columnRenderer)) {
                        return;
                    }
                    this.populateStoreFromColumnRenderer(column, value);
                }
            }
            if (this.linkStore.getCount() === 0) {
                this.linkPanel.setVisible(false);
            } else {
                this.linkPanel.setVisible(true);
            }

            if (this.imageStore.getCount() === 0) {
                this.imagePanel.setVisible(false)
            } else {
                this.imagePanel.setVisible(true);
            }

            if (this.linkStore.getCount() === 0 && this.imageStore.getCount() === 0) {
                this.tabPanel.remove(this.extraMetaPanel);
            } else if (!this.tabPanel.getComponent(this.extraMetaPanel)) {
                this.tabPanel.add(this.extraMetaPanel);
            }
        }
    },

    populateStoreFromColumnRenderer: function (column, value) {
        var columnRenderer = column.columnRenderer;
        var behavior = "";

        behavior = column.columnRenderer.behavior;
        var html = sitools.user.utils.DataviewUtils.getRendererHTML(column, "");

        if (Ext.isEmpty(value)) {
            return;
        }

        switch (behavior) {
            case ColumnRendererEnum.URL_LOCAL :
            case ColumnRendererEnum.URL_EXT_NEW_TAB :
            case ColumnRendererEnum.URL_EXT_DESKTOP :
            case ColumnRendererEnum.DATASET_ICON_LINK :
                if (!Ext.isEmpty(columnRenderer.linkText)) {
                    var item = {
                        name: column.header,
                        value: Ext.String.format(html, value),
                        column: column,
                        behavior: behavior,
                        columnRenderer: columnRenderer,
                        toolTip: columnRenderer.toolTip
                    };
                    this.dataStore.add(item);

                } else if (!Ext.isEmpty(columnRenderer.image)) {
                    var rec = {
                        name: column.header,
                        value: value,
                        image: columnRenderer.image.url,
                        behavior: behavior,
                        columnRenderer: columnRenderer,
                        toolTip: columnRenderer.toolTip,
                        html: html,
                        column: column

                    };
                    this.linkStore.add(rec);
                }
                break;
            case ColumnRendererEnum.DATASET_LINK :
                var item = {
                    name: column.header,
                    value: Ext.String.format(html, value),
                    column: column
                };
                this.dataStore.add(item);
                break;
            case ColumnRendererEnum.IMAGE_NO_THUMB :
                var rec = {
                    name: column.header,
                    value: value,
                    behavior: behavior,
                    columnRenderer: columnRenderer,
                    toolTip: column.header,
                    html: html,
                    column: column
                };
                this.imageStore.add(rec);
                break;
            case ColumnRendererEnum.IMAGE_FROM_SQL :
            case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE :
                var htmlImage = sitools.user.utils.DataviewUtils.getRendererHTML(column, "width:100px; border:none;");

                var rec = {
                    name: column.header,
                    value: Ext.String.format(htmlImage, value),
                    behavior: behavior,
                    columnRenderer: columnRenderer,
                    toolTip: column.header,
                    html: Ext.String.format(htmlImage, value),
                    column: column

                };

                this.imageStore.add(rec);
                break;
            default :
                //item = {
                //	name : column.header,
                //	value : Ext.String.format(html, value),
                //	column : column
                //};
                //this.dataStore.add(item);
                break;
        }
    },

    findColumn: function (columnAlias) {
        var result = null;
        Ext.each(this.datasetColumnModel, function (column) {
            if (column.columnAlias == columnAlias) {
                result = column;
                return;
            }
        }, this);
        return result;
    },

    findRecordValue: function (record, columnAlias) {
        var result = null;
        Ext.each(record.attributeValues, function (attr) {
            if (attr.name == columnAlias) {
                result = attr.value;
                return;
            }
        }, this);
        return result;
    },

    handleClickOnFeature: function (dataView, record, item, index, node, e) {
        var behavior = record.get('behavior');

        var column = record.get('column');
        var serviceToolbarView = Ext.ComponentQuery.query('serviceToolbarView')[0];

        if (!Ext.isEmpty(serviceToolbarView)) { // try to execute featureType from service
            var serviceController = Desktop.getApplication().getController('sitools.user.controller.component.datasets.services.ServicesController');
            this.recSelected = this.grid.getSelectionModel().getSelection()[0];
            sitools.user.utils.DataviewUtils.featureTypeAction(column, this.recSelected, serviceController, serviceToolbarView);
        }
        else {
            sitools.user.utils.DataviewUtils.executeFeatureType(column, this.recSelected);
        }
    },

    getRecordFromOpenSearch: function (osRecord) {
        var urlRecord = osRecord.get('guid');

        if (Ext.isEmpty(urlRecord))
            return

        this.recSelected = undefined;

        Ext.Ajax.request({
            url: urlRecord,
            method: 'GET',
            scope: this,
            success: function (response) {
                var data = Ext.decode(response.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.information'), "Server error");
                    return false;
                }
                var coll = Ext.create('Ext.util.MixedCollection');
                coll.add('id', data.record.id);
                Ext.each(data.record.attributeValues, function (column) {
                    coll.add(column.name, column.value);
                });
                this.recSelected = coll;
            },
            failure: function (response) {
                Ext.Msg.alert(i18n.get('label.error', response.responseText));
            }
        })
    },

    encodeUrlPrimaryKey: function (url) {
        //get the end of the uri and encode it
        var urlSplited = url.split('/');
        var urlReturn = "";
        for (var i = 0; i < urlSplited.length; i++) {
            if (i < urlSplited.length - 1) {
                urlReturn += urlSplited[i] + "/";
            } else {
                urlReturn += encodeURIComponent(urlSplited[i]);
            }
        }
        return urlReturn;
    }
});
