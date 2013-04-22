/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, userLogin, DEFAULT_ORDER_FOLDER, document, alertFailure, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, sql2ext, 
getDesktop, getColumnModel, extColModelToStorage, SitoolsDesk, projectGlobal, DEFAULT_PREFERENCES_FOLDER, DEFAULT_LIVEGRID_BUFFER_SIZE, loadUrl, ColumnRendererEnum*/
/*
 * @include "contextMenu.js"
 * @include "../../viewDataDetail/viewDataDetail.js"
 * @include "../../plot/dataPlotter.js"
 * @include "contextMenu.js"
 * @include "storeLiveGrid.js"
 */
Ext.namespace('sitools.user.component.dataviews.tplView');

/**
 * Define the sitools DataView to display Datasets via an Ext.Dataview
 * Build an Ext.DataView corresponding to the dataset Definition.
 * @requires sitools.user.component.dataviews.ctxMenu
 * @requires sitools.user.component.dataviews.tplView.StoreTplView
 * @requires sitools.user.component.viewDataDetail
 * @requires sitools.user.component.dataPlotter
 * @requires sitools.user.component.dataviews.tplView.dataViewPagingToolbar
 * @class sitools.user.component.dataviews.tplView.TplView
 * @extends Ext.Panel
 */
sitools.user.component.dataviews.tplView.TplView = function (config) {
//sitools.user.component.dataView = function (config) {

//    this.autoScroll = true;
    Ext.apply(this, config);
	
	this.urlRecords = config.dataUrl + '/records';
    this.sitoolsAttachementForUsers = config.dataUrl;
    
    this.columnModel = getColumnModel(config.datasetCm);
    this.componentType = "data";
    
    this.origin = "sitools.user.component.dataviews.tplView.TplView";

    this.store = new sitools.user.component.dataviews.tplView.StoreTplView({
		datasetCm : config.datasetCm,
		urlRecords : this.urlRecords,
		sitoolsAttachementForUsers : this.sitoolsAttachementForUsers,
		userPreference : config.userPreference, 
		bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE, 
		formParams : config.formParams, 
		formMultiDsParams : config.formMultiDsParams, 
		mainView : this, 
		datasetId : config.datasetId,
		filtersCfg : config.filtersCfg,
        isFirstCountDone : false
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
    
    
	this.store.on('load', function (store, records, options) {
		if (this._loadMaskAnchor && this._loadMaskAnchor.isMasked()) {
			this._loadMaskAnchor.unmask();
		}
        var plotComp = Ext.getCmp("plot" + config.datasetId);
        if (plotComp) {
            var rightPanel = plotComp.findById('plot-right-panel');
            var success = rightPanel.fireEvent('buffer', store);
        }
        store.isFirstCountDone = true;
	
	}, this);
	

    
    
	this.store.load({
		params : {
			start : 0, 
			limit : DEFAULT_LIVEGRID_BUFFER_SIZE
		}
	});
    
    
	var tplString = '<tpl for="."><div class="thumb-wrap">';
	var style, maxColLength = 0, dataviewConfig = {};
	Ext.each(this.columnModel.config, function (col) {
		if (!col.hidden) {
			if (col.header.length > maxColLength) {
				maxColLength = col.header.length;
			}
		}
	});
    
    var tplStringImg = '<ul>';
    
	maxColLength  = (maxColLength + 3) * 7;
	style = String.format("width :{0}px", maxColLength);
	
	dataviewConfig = sitoolsUtils.arrayProperties2Object(config.datasetViewConfig);

	Ext.each(this.columnModel.config, function (col) {
		if (!col.hidden) {
            if (this.isColumnImage(col.columnRenderer)) {
                tplStringImg += sitools.user.component.dataviews.dataviewUtils.getRendererDataView(col, style, dataviewConfig);
            } else {
                tplString += sitools.user.component.dataviews.dataviewUtils.getRendererDataView(col, style, dataviewConfig);
            }
		}
	}, this);
    tplStringImg += '</ul>';
    tplString += String.format('<span><div class=x-view-entete style="{0}">{1} </div></span>', style, i18n.get("label.imagesAndServices"));
	tplString += '<span class="linkImageDataView"> ' + tplStringImg + '</span></div></tpl><div class="x-clear"></div>';
	var tpl = new Ext.XTemplate(
	    tplString, 
	    {
			compiled : true, 
			isEmpty : function (value) {
				return Ext.isEmpty(value);
			}, 
			isNotEmpty : function (value) {
				return ! Ext.isEmpty(value);
			}, 
			isValidDate : function (value) {
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
			
		    columnModel : this.columnModel.config
	    }
	);
	this.dataView = new Ext.DataView({
		store: this.store, 
		tpl : tpl, 
        autoHeight: true,
        multiSelect: true,
        columnModel : this.columnModel.config, 
        overClass: 'x-view-over',
        itemSelector: 'div.thumb-wrap',
        emptyText: '', 
        listeners : {
			scope : this, 
			selectionchange : function (dataView, recNodes) {
				var recs = dataView.getRecords(recNodes);
				if (Ext.isEmpty(recs)) {
					return;
				}
				//get the first selected record
				var rec = recs[0];
		        var primaryKeyValue = "", primaryKeyName = "";
		        Ext.each(rec.fields.items, function (field) {
		            if (field.primaryKey) {
		                this.primaryKeyName = field.name;
		            }
		        }, this);
		        //this.primaryKeyName = primaryKeyName;
		        this.primaryKeyValue = rec.get(this.primaryKeyName);
		        
		        this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
		        
		        var url = this.urlRecords + "/" + this.primaryKeyValue;
				Ext.apply(this.panelDetail, {
					url : url
				});
				this.panelDetail.getCmDefAndbuildForm();
				this.panelDetail.expand();
				
			}, 
            contextmenu : function (dataView, index, node, e) {
                e.stopEvent();
                
                var selections = dataView.getRecords(dataView.getSelectedNodes());
                var ctxMenuLeVrai = new sitools.user.component.dataviews.ctxMenu({
					grid : this, 
					selections : selections, 
					event : e, 
					dataUrl : this.sitoolsAttachementForUsers, 
					datasetId : this.datasetId, 
					datasetName : this.datasetName, 
					origin : this.origin,
                    urlDetail : this.sitoolsAttachementForUsers
                });
				var xy = e.getXY();
				ctxMenuLeVrai.showAt(xy);

				//console.log (ctxMenuLeVrai);
            }
			
			
        }
	});
	
	var configDataViewPanel = {
		autoScroll : true,
		items : [this.dataView], 
		region : 'center'
	};

	var panelWest = new Ext.Panel(configDataViewPanel);
	
	

	this.panelDetail = new sitools.user.component.viewDataDetail({
		fromWhere : "dataView",
		grid : this.dataView, 
		baseUrl : this.sitoolsAttachementForUsers, 
		datasetId : config.datasetId, 
		datasetUrl : this.sitoolsAttachementForUsers, 
		boxMinWidth : 200
	});
	Ext.apply(this.panelDetail, {
		title : "detail",
		collapsible : true, 
		collapsed : true, 
		region : "east",
        split : true		
	});
	
	if (! Ext.isEmpty(config.userPreference) && config.userPreference.datasetView == "sitools.user.component.dataviews.tplView.TplView") {
		this.panelDetail.setWidth(config.userPreference.viewPanelDetailSize.width);
	}
	
//    var ctxMenu = new sitools.user.component.dataviews.ctxMenu({
//		grid : this, 
//		event : null, 
//		dataUrl : this.sitoolsAttachementForUsers, 
//		datasetId : this.datasetId, 
//		datasetName : this.datasetName, 
//		origin : this.origin,
//        urlDetail : this.sitoolsAttachementForUsers, 
//        listeners : {
//			scope : this, 
//			beforeShow : function (menu) {
//				//Refresh the grid associated to the menu.
//				menu.setGrid(Ext.getCmp(this.id));
//				menu.setSelections(this.getSelections());
//			}
//        }
//    });
//
//    var plotButton = new Ext.Button({
//        text : 'Plot',
//        icon : loadUrl.get('APP_URL') + "/res/images/icons/plot.png",
//        listeners : {
//            scope : this,
//			click : function (button, e) {
//                e.stopEvent();
//                var jsObj = sitools.user.component.dataPlotter;
//                var componentCfg = {
////                    dataplot : Ext.apply(this.store, {
////                        datasetId : this.datasetId,
////                        columnModel : this.columnModel
////                    }),                     
//                    columnModel :  config.datasetCm,          
//                    formParams : config.formParams,
//                    formMultiDsParams : config.formMultiDsParams,
//                    dataUrl :  config.dataUrl,
//                    datasetName : config.datasetName, 
//                    datasetId : config.datasetId, 
//                    componentType : "plot", 
//                    preferencesPath : "/" + config.datasetName, 
//                    preferencesFileName : "plot",
//                    filters : this.getFilters(),
//                    selections : Ext.isEmpty(this.getSelections())
//                            ? undefined
//                            : this.getRequestParam()
//                };
//                
//                var windowConfig = {
//                    id : "plot" + this.datasetId,
//                    iconCls : "plot", 
//                    title : "Data plot : " + this.datasetName,
//                    datasetName : this.datasetName,
//                    type : "plot",
//                    saveToolbar : true
//                };
//                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
//            }
//        }
//    });
    
    this.tbar = new sitools.user.component.dataviews.services.menuServicesToolbar({
        datasetUrl :  this.sitoolsAttachementForUsers,
        datasetId : this.datasetId,
        dataview : this,
        origin : this.origin
    });
    
//    this.tbar = new Ext.Toolbar({
//		items : [{
//				text : 'Services', 
//				menu : ctxMenu
//	        }, {
//            text : i18n.get("label.multiSort"),
//            scope : this, 
//            handler : function () {
//                var pos = this.getPosition();
//
//                //this.ownerCt.ownerCt reprensents the Window
//                //this.ownerCt.ownerCt.items.items[0] reprensents the first (and only child of the window) -> the future component
//                var up = new sitools.widget.sortersTool({
//                    pos : pos,
//                    store : this.store,
//                    columnModel : this.columnModel
//                });
//                up.show();
//            }, 
//            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/hmenu-asc-all.png"
//        }, {
//            text : i18n.get("label.filter"),
//            scope : this, 
//            handler : function () {
//                var pos = this.getPosition();
//
//                //this.ownerCt.ownerCt reprensents the Window
//                //this.ownerCt.ownerCt.items.items[0] reprensents the first (and only child of the window) -> the future component
//                var up = new sitools.widget.filterTool({
//                    pos : pos,
//                    store : this.store,
//                    columnModel : this.columnModel
//                });
//                up.show();
//            }, 
//            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/hmenu-asc-all.png"
//        },
//        plotButton,
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
    
	this.items = [panelWest, this.panelDetail];
	this.dataviewUtils = sitools.user.component.dataviews.dataviewUtils;
	        
	this.bbar = new sitools.user.component.dataviews.tplView.dataViewPagingToolbar({
        store: this.store,       // grid and PagingToolbar using same store
        displayInfo: true,
        pageSize: DEFAULT_LIVEGRID_BUFFER_SIZE,
        items : []
    });
	Ext.apply(config, this);
	sitools.user.component.dataviews.tplView.TplView.superclass.constructor.call(this, config);
};

Ext.extend(sitools.user.component.dataviews.tplView.TplView, Ext.Panel, {
	layout : 'border', 
	getSelectedRecord : function (dataView) {
		var pageNumber = this.getBottomToolbar().inputItem.getValue();
		var selectedIndex = dataView.getSelectedIndexes()[0];
		if (Ext.isEmpty(selectedIndex)) {
			return null;
		}
		var storeIndex = (pageNumber - 1) * this.getBottomToolbar().pageSize + selectedIndex;
		var rec = dataView.getStore().getAt(storeIndex);
		return rec;
	}, 

	_getSettings : function () {
		return {
			objectName : "TplView", 
			datasetName : this.datasetName, 
			viewPanelDetailSize : this.panelDetail.getSize(), 
			datasetView : "sitools.user.component.dataviews.tplView.TplView",
            datasetUrl : this.sitoolsAttachementForUsers, 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
		};

    }, 
    getColumnModel : function () {
		return extColModelToStorage(this.columnModel);
    }, 
    getSelections : function () {
		return this.dataView.getSelectedRecords();
    },
    getNbRowsSelected : function () {
		return this.getSelections().length;
    }, 

    getStore : function () {
		return this.store;
    }, 
    select : function (record) {
		return this.dataView.select(record);	
    }, 
    getDataView : function () {
		return this.dataView;
    },
    
    getFilters : function () {
        return this.store.filters;
    }, 
    getRequestParam : function () {
        var request = "", formParams = {};
        
		var selections = this.getSelections();
        // First case : no records selected: build the Query
        if (Ext.isEmpty(selections)) {
			request += this.getRequestParamWithoutSelection();
            
        } 
        // Second case : Records are selected
        else {
            var recSelected;
            
            recSelected = selections;
            
            formParams = this.dataviewUtils.getFormParamsFromRecsSelected(recSelected);
            // use the form API to request the selected records
            request += "&" + Ext.urlEncode(formParams);
        
        }
        return request;
    },
	/**
	 * build the param that will represent the active selection.
	 * @param [Ext.data.Record] recSelected the selected records
	 * @returns {} this object contains the param that will use FORM API 
	 */
	getFormParamsFromRecsSelected : function (recSelected) {
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
	getRequestParamWithoutSelection : function () {
		var result = "", formParams = {};

        // add the sorting params
        var storeSort = this.getStore().getSortState();
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
    
    isColumnImage : function (columnRenderer) {
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
    
    getSelectionForPlot : function () {
        return this.getRequestParam();
    },
    
    /**
     * @public
     * return the sortInfo of the TplView.
     * @return [] Array of sortInfo object 
     */
    getSortInfo : function () {
        return this.store.sortInfo;
    }
    
    
    

});

/**
 * @static
 * Implementation of the method getParameters to be able to load view Config panel. 
 * @return {Array} the parameters to display into administration view. 
 */
sitools.user.component.dataviews.tplView.TplView.getParameters = function () {
	return [];
};

