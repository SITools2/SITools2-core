/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
sitools.user.component.dataviews.cartoView.cartoView = function (config) {
    this.urlRecords = config.dataUrl + '/records';
    this.origin = "sitools.user.component.dataviews.cartoView.cartoView";
    
	var dataviewConfig = sitoolsUtils.arrayProperties2Object(config.datasetViewConfig);
	
    //Définir le column Model.
    var colModel;
    if (!Ext.isEmpty(config.userPreference) && config.userPreference.datasetView === "Ext.ux.livegrid" && !Ext.isEmpty(config.userPreference.colModel)) {
        colModel = config.userPreference.colModel;
    }
    else {
		colModel = config.datasetCm; 
    }
    var cm = getColumnModel(colModel, config.dictionaryMappings, dataviewConfig);
    /** 
     * {String} sitoolsAttachementForUsers the attachment url of the dataset
     */
    this.sitoolsAttachementForUsers = config.dataUrl;
    // create map panel
    var mapPanel = new sitools.user.component.dataviews.cartoView.mapPanel(config);

    var fields = sitools.user.component.dataviews.storeUtils.getFields(config.datasetCm);

    var vecLayer = mapPanel.getFeaturesLayer();
    
    // create feature store, binding it to the vector layer using the specific objects to load totalProperty
    this.store = new sitools.user.component.dataviews.cartoView.featureStore({
		datasetCm : config.datasetCm,
		userPreference : config.userPreference, 
		bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE, 
		formParams : config.formParams, 
		formMultiDsParams : config.formMultiDsParams, 
		datasetId : config.datasetId,
		remoteSort: true, 
        layer: vecLayer,
        fields: fields,
        proxy: new sitools.user.data.ProtocolProxy({
			totalProperty : "totalResults",
			url : config.dataUrl + dataviewConfig.jeoResourceUrl
        }),
        autoLoad: false
    });
    
    this.store.filters = new sitools.widget.FiltersCollection({
        filters : config.filters 
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
        
        if (!Ext.isEmpty(this.dataView)) {
            this._loadMaskAnchor = Ext.get(this.el.dom);
        
            this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
        }
        this.store.storeOptions(options);
        //this.el.mask(i18n.get('label.waitMessage'), "x-mask-loading");
    }, this);
    
    /*
	 * the filters of the grid
	 */
//    var filters = sitools.user.component.dataviews.dataviewUtils.getFilters(config.datasetCm, config.filters);
//    // Using the extended gridFilter to filter with the columnAlias
//    var gridFilters = new Ext.ux.grid.GridFiltersSpe({
//        encode : false, // json encode the filter query
//        local : false, // defaults to false (remote filtering)
//        filters : filters
//    });

    /*
	 * PlotXY button for launching numeric data preview as a plot
	 */
//    var plotButton = new Ext.Button({
//        text : 'Plot',
//        icon : loadUrl.get('APP_URL') + "/res/images/icons/plot.png",
//        scope : this,
//        cm : cm, 
//        listeners : {
//	    	scope : this,
//	    	click : function (button, e) {
//                e.stopEvent();
//                var jsObj = sitools.user.component.dataPlotter;
//                var componentCfg = {
//                    columnModel :  config.datasetCm,          
//                    formParams : config.formParams,
//                    formMultiDsParams : config.formMultiDsParams,
//                    dataUrl :  config.dataUrl,
//                    datasetName : config.datasetName, 
//                    datasetId : config.datasetId, 
//                    componentType : "plot", 
//                    preferencesPath : "/" + config.datasetName, 
//                    preferencesFileName : "plot",
//                    filters : this.filters,
//                    selections : Ext.isEmpty(this.getSelections())
//                            ? undefined
//                            : this.getRecSelectedParam()
//                    
//                };
//                var windowConfig = {
//                    id : "plot" + config.datasetId,
//                    title : "Data plot : " + config.datasetName,
//                    datasetName : config.datasetName,
//                    type : "plot",
//                    iconCls : "plot", 
//                    saveToolbar : true,
//                    winHeight : 600
//                };
//                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
//            }
//        }
//    });
//
//	/**
//	 * {Ext.Toolbar} Top toolbar with services
//	 */
//	this.topBar = new Ext.Toolbar({
//		items : [{
//			text : 'Services', 
//			menu : ctxMenu
//        }, "-", {
//            text : i18n.get("label.multiSort"),
//            scope : this, 
//            handler : function () {
//                var pos = this.getPosition();
//
//                //this.ownerCt.ownerCt reprensents the Window
//                //this.ownerCt.ownerCt.items.items[0] reprensents the first (and only child of the window) -> the future component
//                var up = new sitools.widget.sortersTool({
//                    pos : pos,
//                    store : this.gridPanel.getStore(),
//                    columnModel : this.gridPanel.getColumnModel()
//                });
//                up.show();
//            }, 
//            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/hmenu-asc-all.png"
//        }, "-",
//        plotButton, "-",
//        {
//            text : i18n.get('label.definitionTitle'),
//            icon :  loadUrl.get('APP_URL') + "/common/res/images/icons/tree_dictionary.png",
//            scope : this,
//            handler : function () {
//                
//                var windowConfig = {
//                    title : i18n.get('label.definitionTitle') + " : " + this.datasetName, 
//                    datasetName : this.datasetName, 
//                    iconCls : "semantic", 
//                    datasetDescription : this.datasetDescription,
//                    type : "defi",
//                    saveToolbar : true, 
//                    toolbarItems : []
//                };
//                
//                var javascriptObject = sitools.user.component.columnsDefinition;
//                Ext.apply(windowConfig, {
//                    id : "defi" + this.datasetId
//                });
//                var componentCfg = {
//                    datasetId : this.datasetId,
//                    datasetCm : config.datasetCm, 
//                    datasetName : this.datasetName,
//                    dictionaryMappings : config.dictionaryMappings, 
//                    preferencesPath : "/" + this.datasetName, 
//                    preferencesFileName : "semantic"
//                };
//                
//                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
//
//            }
//        }]
//	});
    
    this.topBar = new sitools.user.component.dataviews.services.menuServicesToolbar({
        datasetUrl : this.sitoolsAttachementForUsers,
        datasetId : this.datasetId,
        dataview : this,
        origin : this.origin
    });

    
    var bbar = new Ext.PagingToolbar({
        pageSize: 300,
        store: this.store,
        refreshText : i18n.get('label.refreshText'), 
        displayInfo: true,
        displayMsg: i18n.get('paging.display'), 
        listeners : {
			scope : this, 
			change : function (tb, pageData) {
		        var plotComp = Ext.getCmp("plot" + this.datasetId);
		        if (plotComp) {
		            var rightPanel = plotComp.findById('plot-right-panel');
		            var success = rightPanel.fireEvent('buffer', tb.store, pageData.activePage, pageData.activePage, pageData.pages);
		        }
			}
		}
    });
    
    var sm = new sitools.user.component.dataviews.cartoView.featureSelectionModel({
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
		}
	});
	
	var gridWidth = 100 - dataviewConfig.mapWidth;
	
    // create grid panel configured with feature store
    this.gridPanel = new Ext.grid.GridPanel({
//        tbar : this.topBar,
        region : "west",
        collapsible : true,
        flotable : true,
        split : true,
        store : this.store,
//        plugins : [ gridFilters ],
        width : "" + gridWidth + "%",
        cm : cm,
        sm : sm,
        bbar : bbar,
//        listeners : {
//            afterrender : function (grid) {
//                grid.getView().hdCtxIndex = 0;
//                this.topBar.add('-');
//                this.topBar.add('->');
//                this.topBar.add('-');
//                this.topBar.add({
//                    tooltip : i18n.get('label.addOrDeleteColumns'),
//                    icon : '/sitools/cots/extjs/resources/images/default/grid/columns.gif',
//                    menu : this.getDatasetView().colMenu
//                });
//            }, 
//            scope : this
//        }
    });
    
    

    // -- CONSTRUCTOR --
	sitools.user.component.dataviews.cartoView.cartoView.superclass.constructor.call(this, Ext.apply({
        layout: "border",
        dataviewUtils : sitools.user.component.dataviews.dataviewUtils, 
        items: [mapPanel, this.gridPanel],
	    componentType : "data",
	    tbar : this.topBar
	}, config));    

};

Ext.extend(sitools.user.component.dataviews.cartoView.cartoView, Ext.Panel, {
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
//	,
//    /**
//     * @private
//     * return the filters of the liveGrid.
//     * @return [] Array of filter object 
//     */
//    getFilters : function () {
//	    return this.filters;
//	}, 
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
    
        // First case : no records selected: build the Query
        if (Ext.isEmpty(this.getSelections())) {
			request += this.getRequestParamWithoutSelection();
        } 
        // Second case : Records are selected
        else {
            var recSelected;
            request += "&" + this.getRecSelectedParam();
        }
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
		result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
		
		return result;
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
        return this.getRecSelectedParam();
    },
    
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    createColumnsButton : function () {
        var array = [];
        array.push(new Ext.Toolbar.Separator());
        array.push({
            id : "columnsButtonId",
            tooltip : i18n.get('label.addOrDeleteColumns'),
            icon : '/sitools/cots/extjs/resources/images/default/grid/columns.gif',
            menu : this.getDatasetView().colMenu
        });
        this.getDatasetView().hdCtxIndex = 0;
        return array;
    }
    
});

/**
 * @static
 * Implementation of the method getParameters to be able to load view Config panel.
 * @return {Array} the parameters to display into administration view. 
 */
sitools.user.component.dataviews.cartoView.cartoView.getParameters = function () {
    var baseLayer = new Ext.grid.CheckColumn({
        header : i18n.get('headers.baseLayer'),
        dataIndex : 'baseLayer',
        width : 80
    });

	var cm = new Ext.grid.ColumnModel({
        columns: [{
            header: 'Layer Name',
            dataIndex: 'layerName',
            width: 220,
            // use shorthand alias defined above
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }, {
            header: 'Wms Url',
            dataIndex: 'url',
            width: 270,
            // use shorthand alias defined above
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }, baseLayer]
    });

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

    return [{
		jsObj : "Ext.form.TextField", 
		config : {
			anchor : "100%", 
	        parameterName : "jeoResourceUrl", 
	        fieldLabel : i18n.get("label.jeoResourceUrl"), 
	        value : "/jeo/opensearch/search"
		}
	},{
		jsObj : "Ext.ux.form.SpinnerField", 
		config : {
			anchor : "100%", 
	        parameterName : "mapWidth", 
	        fieldLabel : i18n.get("label.mapWidth"), 
	        value : "70"
		}
	}, {
		jsObj : "Ext.grid.EditorGridPanel", 
		config : {
			title : i18n.get('label.Layers'),	
			store: store,
	        cm: cm,
	        sm : new Ext.grid.RowSelectionModel(),
	        anchor : "100%", 
	        height: 200,
	        autoScroll : true, 
	        plugins : [baseLayer], 
	        clicksToEdit: 2,
	        listeners : {
				scope : this,
				afterrender : function (grid) {
					if (grid.getStore().getCount() === 0) {
						var TypeRec = grid.getStore().recordType;
						var rec = new TypeRec({
							layerName : "openstreetmap", 
							url : "http://maps.opengeo.org/geowebcache/service/wms", 
							baseLayer : true
						});
						grid.getStore().add(rec);
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
	            handler : function (btn) {
	                var grid = btn.ownerCt.ownerCt;

	                var Layer = grid.getStore().recordType;
	                var l = new Layer({
	                    layerName: '',
	                    url: ''
	                });
	                grid.stopEditing();
	                store.insert(0, l);
	                grid.startEditing(0, 0);
	            }
	        }, {
	            text: i18n.get('label.deleteLayer'),
	            handler : function (btn) {
	                var grid = btn.ownerCt.ownerCt;
	                var rec = grid.getSelectionModel().getSelected();
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

 

Ext.reg('sitoolsCartoView', sitools.user.component.dataviews.cartoView.cartoView);