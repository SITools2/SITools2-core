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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window, sitoolsUtils, 
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE, GeoExt, 
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT, OpenLayers, 
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, getColumnModel, loadUrl, getApp
*/

Ext.ns("sitools.user.component.dataviews.cartoView");

/**
 * The LiveGrid used to show Dataset Datas.
 * 
 * @cfg {string} dataUrl (Required) The datasetAttachment to request to load the datas 
 * @cfg {string} datasetId (Required)  The DatasetId, 
 * @cfg {Ext.grid.ColumnModel} datasetCm (Required) A definition of a ColumnModel 
 * @cfg {} userPreference {}  {
 *            componentSettings {Array} : Array of the Columns as saved by the user 
 *            windowSettings {Object} : { 
 *            moduleId : String 
 *                position : [xpos, ypos] 
 *                size : { 
 *                    width : w 
 *                    height : h 
 *                } 
 *                specificType : String 
 *            } 
 *      }
 * @cfg {Array} formParams list of the form params used to search thrue
 * the grid ["TEXTFIELD|AliasColumn1|X", "TEXTFIELD|AliasColumn2|Y"] 
 * @cfg {Array} filters Array of Ext.ux.Filter : [{ 
 *          columnAlias : Alias1, 
 *          data : {
 *            comparison : "LIKE", 
 *            type : "string", 
 *            value : "01" 
 *          } 
 *      }, {
 *            columnAlias : Alias2, 
 *            data : { 
 *              comparison : "gt", 
 *              type : "date",
 *              value : "199-11-04" 
 *            } 
 *      }] 
 *      
 * @requires sitools.user.component.columnsDefinition
 * @requires sitools.user.component.viewDataDetail
 * @requires Ext.ux.grid.GridFiltersSpe
 * @requires sitools.user.component.dataPlotter
 * @class sitools.user.component.dataviews.cartoView.cartoView
 * @extends Ext.ux.grid.livegrid.EditorGridPanel
 * 
 * 
 */
Ext.define('sitools.user.component.dataviews.cartoView.cartoView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.sitoolsCartoView',
    layout: "border",
    dataviewUtils : sitools.user.component.dataviews.dataviewUtils, 
    componentType : "data",
    
    initComponent : function () {
        this.urlRecords = this.dataUrl + '/records';
        this.origin = "sitools.user.component.dataviews.cartoView.cartoView";
        
        var dataviewConfig = sitoolsUtils.arrayProperties2Object(this.datasetViewConfig);
        
        //Définir le column Model.
        var colModel;
        if (!Ext.isEmpty(this.userPreference) && this.userPreference.datasetView === "Ext.ux.livegrid" && !Ext.isEmpty(this.userPreference.colModel)) {
            colModel = this.userPreference.colModel;
        }
        else {
            colModel = this.datasetCm; 
        }
        var cm = getColumnModel(colModel, this.dictionaryMappings, dataviewConfig);
        /** 
         * {String} sitoolsAttachementForUsers the attachment url of the dataset
         */
        this.sitoolsAttachementForUsers = this.dataUrl;
        // create map panel
        var mapPanel = new sitools.user.component.dataviews.cartoView.mapPanel(this);
        
        mapPanel.addListener("selectionmodelready", function () {
            this.sm.locked = false;
            if (!Ext.isEmpty(this.ranges)) {
                if (!Ext.isEmpty(this.nbRecordsSelection) && (this.nbRecordsSelection == this.store.getTotalCount())) {
                    this.getSelectionModel().onHdMouseDown(Ext.EventObject, this.getSelectionModel().headerCheckbox.dom.childNodes[1]);
                    delete this.nbRecordsSelection;
                    delete this.ranges;
                } else {
                    var ranges = Ext.util.JSON.decode(this.ranges);
                    this.selectRangeDataview(ranges);
                    delete this.ranges;
                }
            }
        }, this);

        var fields = sitools.user.component.dataviews.storeUtils.getFields(this.datasetCm);

        var vecLayer = mapPanel.getFeaturesLayer();
        
        // create feature store, binding it to the vector layer using the specific objects to load totalProperty
        this.store = new sitools.user.component.dataviews.cartoView.featureStore({
            datasetCm : this.datasetCm,
            userPreference : this.userPreference, 
            bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE, 
            formParams : this.formParams,
            ranges : this.ranges,
            startIndex : this.startIndex,
            formMultiDsParams : this.formMultiDsParams, 
            datasetId : this.datasetId,
            remoteSort: true, 
            layer: vecLayer,
            fields: fields,
            proxy: new sitools.user.data.ProtocolProxy({
                totalProperty : "totalResults",
                url : this.dataUrl + dataviewConfig.jeoResourceUrl
            })
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
        
        this.store.filters = new sitools.widget.FiltersCollection({
            filters : this.filters 
        });
        
        this.store.on("beforeload", function (store, options) {
            //set the nocount param to false.
            //before load is called only when a new action (sort, filter) is applied
            var noCount;
            
            if (!store.isFirstCountDone || store.isNewFilter) {
                options.params.nocount = false;
            } else {
                options.params.nocount = true;
            }

            if (store.isNewFilter) {
                options.params.start = 0;
                options.params.limit = DEFAULT_LIVEGRID_BUFFER_SIZE;
            }
            
            store.isNewFilter = false;        
            
            if (!Ext.isEmpty(store.filters)) {
                var params = store.buildQuery(store.filters.getFilterData());
                Ext.apply(options.params, params);
            }
            
            this.store.storeOptions(options);
            //this.el.mask(i18n.get('label.waitMessage'), "x-mask-loading");
        }, this);
        
        this.store.on("load", function (store, records, options) {
            this.topBar.updateContextToolbar();
            this.removeLoadMask("load");
        }, this);
        
        this.topBar =  new sitools.user.component.dataviews.services.menuServicesToolbar({
            datasetUrl : this.sitoolsAttachementForUsers,
            datasetId : this.datasetId,
            dataview : this,
            origin : this.origin,
            columnModel : this.datasetCm,
            listeners : {
                scope : this,
                allservicesloaded : function () {
                    this.removeLoadMask("allservicesloaded");
                }
            }  
        });

        var bbar = new Ext.PagingToolbar({
            pageSize : DEFAULT_LIVEGRID_BUFFER_SIZE,
            store : this.store,
            refreshText : i18n.get('label.refreshText'),
            displayInfo : true,
            displayMsg : i18n.get('paging.display')
        });
        
        this.sm = new sitools.user.component.dataviews.cartoView.featureSelectionModel({
            listeners : {
                scope : this, 
                gridFeatureSelected : function (g, rowIndex, e) {
                    try {
                        var row = g.getStore().getAt(rowIndex);
                        var feature = row.getFeature();
                        var featurePos = {
                            lon : feature.geometry.x, 
                            lat : feature.geometry.y
                        };
                        var lonlat = new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y);
                        mapPanel.getMap().panTo(lonlat);
                    }
                    catch (err) {
                        return;
                    }
                }
            },
            isSelectionModel : true,
            checkOnly : true        
        });
        
        var gridWidth = 100 - dataviewConfig.mapWidth;
        
        //create a new columnModel with the selectionModel
        var configCol = cm.config;
        configCol.unshift(this.sm);
        cm = new Ext.grid.ColumnModel({
            columns : configCol
        });
        
        
        // create grid panel configured with feature store
        this.gridPanel = new Ext.grid.GridPanel({
            region : "west",
            collapsible : true,
            flotable : true,
            split : true,
            store : this.store,
            width : "" + gridWidth + "%",
            cm : cm,
            sm : this.sm,
            bbar : bbar,
//            view : new Ext.ux.sitoolsGridView({
            view : {
                xtype : 'gridview',
                nearLimit : DEFAULT_NEAR_LIMIT_SIZE, 
                loadMask : {
                    msg : i18n.get('label.waitMessage'),
                    msgCls : "x-mask-loading"
                }
            },
            listeners : {
                scope : this,
                cellclick : function (grid, rowIndex, columnIndex, e) {
                    var columnModel = this.getColumnModel();
                    var column = columnModel.columns[columnIndex];     
                    if (Ext.isEmpty(column.columnRenderer)) {
                        return;
                    }
                    var record = grid.getStore().getAt(rowIndex);  // Get the Record
                    var controller = this.getTopToolbar().guiServiceController;
                    sitools.user.component.dataviews.dataviewUtils.featureTypeAction(column, record, controller);
                    
                }
            }
        });
        
        this.store.load();
        
        this.items = [mapPanel, this.gridPanel];
        this.tbar = this.topBar;

        // -- CONSTRUCTOR --
        sitools.user.component.dataviews.cartoView.cartoView.superclass.initComponent.call(this);    

    },
    
    afterRender : function () {
        sitools.user.component.dataviews.cartoView.cartoView.superclass.afterRender.apply(this, arguments);
        
        this._loadMaskAnchor = Ext.get(this.el.dom);
        this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
    },
    
    /**
     * @private
     * @returns the JsonColModel used to store the userPreferences.
     */
    _getSettings : function () {
        return {
            datasetName : this.datasetName, 
            colModel : extColModelToJsonColModel(this.gridPanel.colModel.config), 
            datasetView : "sitools.user.component.dataviews.cartoView.cartoView",
            datasetUrl : this.sitoolsAttachementForUsers, 
            dictionaryMappings : this.dictionaryMappings,
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };

    }, 
//  ,
//    /**
//     * @private
//     * return the filters of the liveGrid.
//     * @return [] Array of filter object 
//     */
//    getFilters : function () {
//      return this.filters;
//  }, 
//    /**
//     * 
//     * @return {String} 
//     */
    getRequestParam : function () {
        var request = "", formParams = {};
    
        var colModel = extColModelToSrv(this.gridPanel.getColumnModel());
        if (!Ext.isEmpty(colModel)) {
            request += "&colModel=" + Ext.util.JSON.encode(colModel);
        }
    
        request += this.getRequestParamWithoutColumnModel();
        return request;
    }, 
    getSelectionModel : function () {
        return this.gridPanel.getSelectionModel();
    },
    getSelections : function () {
        return this.gridPanel.getSelectionModel().getSelections();
    }, 
    
    getNbRowsSelected : function () {
        return this.gridPanel.getSelectionModel().getSelections().length;
    }, 
    getColumnModel : function () {
        return this.gridPanel.getColumnModel();
    }, 
    
    getRequestParamWithoutColumnModel : function () {
        var request = "";
        // First case : no records selected: build the Query
        if (Ext.isEmpty(this.getSelections())) {
            request += this.getRequestParamWithoutSelection();
        } 
        // Second case : Records are selected
        else {
            request += "&" + this.getRecSelectedParam();
        }
        return request;
    },
    
    getRequestParamWithoutSelection : function () {
        var result = "", formParams = {};
        // Add the filters params
        var filters = this.getFilters();
        if (!Ext.isEmpty(filters)) {
            filters = this.store.buildQuery(filters.getFilterData(filters));
            if (!Ext.isEmpty(Ext.urlEncode(filters))) {
                result += "&" + Ext.urlEncode(filters);
            }

        }
        // add the sorting params
        var storeSort = this.gridPanel.getStore().getSortState();
        if (!Ext.isEmpty(storeSort)) {
            var sort;
            if (!Ext.isEmpty(storeSort.sorters)) {
                sort = {
                    ordersList : storeSort.sorters
                };
            } else {
                sort = {
                    ordersList : [ storeSort ]
                };
            }
            result += "&sort=" + Ext.encode(sort);
        }

        // add the formParams
        var formParamsTmp = this.gridPanel.getStore().getFormParams(), i = 0;
        if (!Ext.isEmpty(formParamsTmp)) {
            Ext.each(formParamsTmp, function (param) {
                formParams["p[" + i + "]"] = param;
                i += 1;
            }, this);
            result += "&" + Ext.urlEncode(formParams);
        }
        return result;
    }, 
    /**
     * @method
     * will check if there is some pendingSelection (no requested records)
     * <li>First case, there is no pending Selection, it will build a form parameter
     * with a list of id foreach record.</li>
     * <li>Second case, there is some pending Selection : it will build a ranges parameter
     * with all the selected ranges.</li>
     * @returns {} Depending on liveGridSelectionModel, will return either an object that will use form API 
     * (p[0] = LISTBOXMULTIPLE|primaryKeyName|primaryKeyValue1|primaryKeyValue1|...|primaryKeyValueN), 
     * either an object that will contain an array of ranges of selection 
     * (ranges=[range1, range2, ..., rangen] where rangeN = [startIndex, endIndex])
     * 
     */
    getRecSelectedParam : function () {
        var sm = this.gridPanel.getSelectionModel(), result;
        var recSelected = sm.getSelections();
        
        if (sm.markAll) {
            //First Case : all the dataset is selected.
            result = "ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
            //We have to re-build all the request in case we use a range selection.
            result += this.getRequestParamWithoutSelection();
        }
        else {
            //Second Case : there is a selection, send the selection.
            result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
        }
        return result;
    },
    
    getSelectionsRange : function () {
        var sm = this.getSelectionModel();
        var dirtyRanges = sm.getAllSelections(true);
        var cleanedRanges = [];
        Ext.each(dirtyRanges, function (range) {
            if (range[0] != -1 || range[1] != -1) {
                cleanedRanges.push(range);
            }
        })
        return Ext.util.JSON.encode(cleanedRanges);
    },
    
    getDatasetView : function () {
        return this.gridPanel.getView();
    },
    
    getStore : function () {
        return this.store;
    },
    
    getFilters : function () {
        return this.store.filters;
    },
    /**
     * @public
     * return the sortInfo of the view.
     * @return [] Array of sortInfo object 
     */
    getSortInfo : function () {
        return this.store.sortInfo;
    },
    
    getSelectionForPlot : function () {
        var sm = this.gridPanel.getSelectionModel(), result;
        var recSelected = sm.getSelections();
        
        if (sm.markAll) {
            //First Case : all the dataset is selected.
            result = "ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
        }
        else {
            //Second Case : there is a selection, send the selection.
            result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
        }
        return result;
    },
    
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons : function () {
        var array = [];
        array.push({
            name : "columnsButton",
            tooltip : i18n.get('label.addOrDeleteColumns'),
            iconCls: 'x-cols-icon',
            menu : sitools.user.component.dataviews.dataviewUtils.createColMenu(this.getDatasetView(), this.getColumnModel())
        });
        array.push(new Ext.Toolbar.Separator());
        this.getDatasetView().hdCtxIndex = 0;
        return array;
    },
    
    isAllSelected : function () {
        return this.getSelectionModel().markAll;
    },
    
    changePage : function (startIndex, callback) {
        if (startIndex != this.store.lastOptions.params.start) {
            this.store.load({
                start : startIndex,
                limit : DEFAULT_LIVEGRID_BUFFER_SIZE
            }, 
            function () {
                callback();
            }, this);
        }
    },
    
    selectRangeDataview : function (ranges) {
        Ext.each(ranges, function (range) {
            this.getSelectionModel().selectRange(range[0], range[1], true);
        }, this);
    },
    
    removeLoadMask : function (eventName) {
        this.allIsLoadedEvent.add(eventName, true);
        var removeLoadMask = true;
        this.allIsLoadedEvent.each(function (value) {
            removeLoadMask &= value;
        });
        if (removeLoadMask) {
            if (this._loadMaskAnchor && this._loadMaskAnchor.isMasked()) {
                this._loadMaskAnchor.unmask();
            }
        }
    }
});

/**
 * @static
 * Implementation of the method getParameters to be able to load view Config panel.
 * @return {Array} the parameters to display into administration view. 
 */
sitools.user.component.dataviews.cartoView.cartoView.getParameters = function () {
    var baseLayer = {
        xtype : 'checkcolumn',
        header : i18n.get('headers.baseLayer'),
        dataIndex : 'baseLayer',
        width : 80
    };

    // create the Data Store
    var store = new Ext.data.JsonStore({
        fields : [{
            name : "layerName",
            type : "string"
        }, {
            name : "url",
            type : "string"
        }, {
            name : "baseLayer", 
            type : "boolean"
        }]
    });

    var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
        pluginId : 'layerEditorId',
        clicksToEdit: 1
    });
    
    return [{
        jsObj : "Ext.form.TextField", 
        config : {
            anchor : "100%", 
            parameterName : "jeoResourceUrl", 
            fieldLabel : i18n.get("label.jeoResourceUrl"), 
            value : "/jeo/opensearch/search"
        }
    },{
        jsObj : "Ext.form.field.Spinner", 
        config : {
            anchor : "100%", 
            parameterName : "mapWidth", 
            fieldLabel : i18n.get("label.mapWidth"),
            minValue : 0,
            maxValue: 100,
            value : 70
        }
    }, {
        jsObj : "Ext.grid.Panel", 
        config : {
            title : i18n.get('label.Layers'),   
            store : store,
            forceFit : true,
            columns : [{
                header: 'Layer Name',
                dataIndex: 'layerName',
                width: 220,
                editor : {
                    xtype : 'textfield',
                    allowBlank: false
                }
            }, {
                header: 'Wms Url',
                dataIndex: 'url',
                width: 270,
                // use shorthand alias defined above
                editor: {
                    xtype : 'textfield',
                    allowBlank: false
                }
            }, baseLayer],
            selModel : Ext.create('Ext.selection.RowModel', {}),
            anchor : "100%", 
            height: 200,
            autoScroll : true, 
//            clicksToEdit: 2,
            plugins : [cellEditing],
            listeners : {
                scope : this,
                afterrender : function (grid) {
                    if (grid.getStore().getCount() === 0) {
//                        var TypeRec = grid.getStore().recordType;
//                        var rec = new TypeRec({
//                            layerName : "openstreetmap", 
//                            url : "http://maps.opengeo.org/geowebcache/service/wms", 
//                            baseLayer : true
//                        });
                        grid.getStore().add({
                            layerName : "openstreetmap", 
                            url : "http://maps.opengeo.org/geowebcache/service/wms", 
                            baseLayer : true
                        });
                        grid.getView().refresh();
                    }
                }
            }, 
            getValue : function () {
                var res = [];
                var store = this.getStore();
                store.each(function (rec) {
                    res.push(rec.data);
                });
                return Ext.encode(res);
            }, 
            setValue : function (value) {
                var values = Ext.decode(value);
                
                var grid = this;
                Ext.each(values, function (value) {
                    var rec = new Ext.data.Record(value);
                    grid.getStore().add(rec);
                });
            }, 
            tbar: [{
                text: i18n.get('label.addLayer'),
                handler : function () {
                    var grid = this.up('gridpanel');
                    
                    var editorPlugin = grid.getPlugin("layerEditorId");
                    
                    editorPlugin.completeEdit();
                    
                    grid.getStore().insert(0, {
                        layerName: '',
                        url: ''
                    });
                    
                    editorPlugin.startEdit(0, 0);
                }
            }, {
                text: i18n.get('label.deleteLayer'),
                handler : function () {
                    var grid = this.up('gridpanel');
                    
                    var rec = grid.getSelectionModel().getSelection()[0];
                    if (Ext.isEmpty(rec)) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noRecordSelected'));
                        return;
                    }
                    grid.getStore().remove(rec);
                }
            }], 
            parameterName : "layers"
        }
    }];
};
