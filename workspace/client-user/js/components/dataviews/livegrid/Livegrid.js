/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window, 
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE,
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT,
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, getColumnModel, loadUrl, getApp
*/

/*
 * @include "Ext.ux.livegrid/Ext.ux.livegrid-all-debug.js"
 * @include "storeLiveGrid.js"
 * @include "../../plot.dataPlotter.js"
 * @include "contextMenu.js"
 * @include "../../columnsDefinition/dependencies/columnsDefinition.js"
 * @include "../../viewDataDetail/viewDataDetail.js" 
 * @include "../../../../../client-public/js/widgets/gridfilters/GridFilters.js"
 * @include "../../plot/dataPlotter.js"
 */

Ext.override(Ext.ux.grid.livegrid.GridView, {
    /**
     * Override this method to add a specific line Height
     * @param {Ext.grid.GridPanel}
     *            grid The grid panel this view is attached to
     */
    init : function (grid) {
        this._gridViewSuperclass.init.call(this, grid);
		
        this.rowHeight = this.calcLineHeight();
        
        grid.on('expand', this._onExpand, this);
    },
    /**
     * Returns the height of the livegrid rows.
     * @return {int} 
     */
    calcLineHeight : function () {
		//default Value : 23
		var lineHeight = 23;
		if (Ext.isEmpty(this.showImage)) {
			this.calcShowImage(this.cm.columns);
		}
		if (this.showImage && !Ext.isEmpty(this.datasetViewConfig.lineHeight)) {
			lineHeight = this.datasetViewConfig.lineHeight;
		}
		return lineHeight;
    },
	/**
     * Determines if an image should be displayed in liveGrid
     * @return void
     */
    calcShowImage : function () {
		this.showImage = false;
		Ext.each(this.cm.columns, function (column) {
			if (! column.hidden && !Ext.isEmpty(column.columnRenderer)) {
				if (sitools.admin.datasets.columnRenderer.behaviorEnum.isDisplayingImage(column.columnRenderer)) {
					this.showImage = true;
				}
			}
		}, this);
    }, 
    /**
     * Overwritten so the rowIndex can be changed to the absolute index.
     * 
     * If the third parameter equals to <tt>true</tt>, the method will also
     * repaint the selections.
     */
    // private
    processRows : function (startRow, skipStripe, paintSelections) {
        //DA : calculer si une image doit être affichée
//		if (Ext.isEmpty(this.showImage)) {
//			this.calcShowImage(this.cm.columns);
//		}
        if (!this.ds || this.ds.getCount() < 1) {
            return;
        }

        skipStripe = skipStripe || !this.grid.stripeRows;

        var cursor = this.rowIndex;
        var rows = this.getRows();
        var index = 0;

        var row = null;
        for (var idx = 0, len = rows.length; idx < len; idx++) {
            row = rows[idx];

            row.rowIndex = index = cursor + idx;
            row.className = row.className.replace(this.rowClsRe, ' ');
            if (!skipStripe && (index + 1) % 2 === 0) {
                row.className += ' x-grid3-row-alt';
            }
			
			//DA Fix the line Height
			this.fly(row).setHeight(this.rowHeight);

            if (paintSelections !== false) {
                if (this.grid.selModel.isSelected(this.ds.getAt(index)) === true) {
                    this.addRowClass(index, this.selectedRowClass);
                } else {
                    this.removeRowClass(index, this.selectedRowClass);
                }
                this.fly(row).removeClass("x-grid3-row-over");
            }
        }

        // add first/last-row classes
        if (cursor === 0) {
            Ext.fly(rows[0]).addClass(this.firstRowCls);
        } else if (cursor + rows.length == this.ds.totalLength) {
            Ext.fly(rows[rows.length - 1]).addClass(this.lastRowCls);
        }
    }, 
     /**
     * Recomputes the number of visible rows in the table based upon the height
     * of the component. The method adjusts the <tt>rowIndex</tt> property as
     * needed, if the sum of visible rows and the current row index exceeds the
     * number of total data available.
     */
    // protected
    adjustVisibleRows : function () {
        if (Ext.isEmpty(this.showImage)) {
            this.calcShowImage();
        }
        if (this.rowHeight == -1) {
            this.rowHeight = this.calcLineHeight();
        }

        var g = this.grid, ds = g.store;

        var c = g.getGridEl();
        var cm = this.cm;
        var size = c.getSize();
        var width = size.width;
        var vh = size.height;

        var vw = width - this.getScrollOffset();
        // horizontal scrollbar shown?
        if (cm.getTotalWidth() > vw) {
            // yes!
            vh -= this.horizontalScrollOffset;
        }

        vh -= this.mainHd.getHeight();

        var totalLength = ds.totalLength || 0;

        var visibleRows = Math.max(1, Math.floor(vh / this.rowHeight));

        this.rowClipped = 0;
        // only compute the clipped row if the total length of records
        // exceeds the number of visible rows displayable
        if (totalLength > visibleRows && this.rowHeight / 3 < (vh - (visibleRows * this.rowHeight))) {
            visibleRows = Math.min(visibleRows, totalLength);
            this.rowClipped = 1;
        }

        // if visibleRows didn't change, simply void and return.
        if (this.visibleRows == visibleRows) {
            return;
        }

        this.visibleRows = visibleRows;

        // skip recalculating the row index if we are currently buffering, but
        // not if we
        // are just pre-buffering
        if (this.isBuffering && !this.isPrebuffering) {
            return;
        }

        // when re-rendering, doe not take the clipped row into account
        if (this.rowIndex + (visibleRows - this.rowClipped) > totalLength) {
            this.rowIndex = Math.max(0, totalLength - (visibleRows - this.rowClipped));
            this.lastRowIndex = this.rowIndex;
        }

        this.updateLiveRows(this.rowIndex, true);
    }
    
});
Ext.ns("sitools.user.component.dataviews.livegrid");

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
 * @requires sitools.user.component.dataviews.livegrid.LiveGrid.storeLiveGrid
 * @requires sitools.user.component.columnsDefinition
 * @requires sitools.user.component.viewDataDetail
 * @requires Ext.ux.grid.GridFiltersSpe
 * @requires sitools.user.component.dataPlotter
 * @class sitools.user.component.dataviews.livegrid.LiveGrid
 * @extends Ext.ux.grid.livegrid.EditorGridPanel
 * 
 * 
 */
sitools.user.component.dataviews.livegrid.LiveGrid = function (config) {
    Ext.apply(this, config);
    
    /**
     * {String} urlRecords the url to request the API to get the records
     */
    this.urlRecords = config.dataUrl + '/records';
    /** 
     * {String} sitoolsAttachementForUsers the attachment url of the dataset
     */
    this.sitoolsAttachementForUsers = config.dataUrl;
    /** {String} datasetName the dataset Name */
    this.datasetName = config.datasetName;
    
    var xg = Ext.grid;

    var dataviewConfig = sitoolsUtils.arrayProperties2Object(config.datasetViewConfig);
    /*
	 * Construction of the column Model : user preferences have priority on the
	 * initial definition of the model column in the dataset
	 */
    var colModel;

    this.origin = "Ext.ux.livegrid";
    
    if (!Ext.isEmpty(config.userPreference) && config.userPreference.datasetView == this.origin && !Ext.isEmpty(config.userPreference.colModel)) {
        colModel = config.userPreference.colModel;
    }
    else {
		colModel = config.datasetCm; 
    }
    var cm = getColumnModel(colModel, config.dictionaryMappings, dataviewConfig, this.getId());
    
//    /*
//	 * the filters of the grid
//	 */
//    var filters = sitools.user.component.dataviews.dataviewUtils.getFilters(config.datasetCm, config.filters);
//    // Using the extended gridFilter to filter with the columnAlias
//    var filtersSimple = new Ext.ux.grid.GridFiltersSpe({
//        encode : false, // json encode the filter query
//        local : false, // defaults to false (remote filtering)
//        filters : filters
//    });

    this.store = new sitools.user.component.dataviews.livegrid.StoreLiveGrid({
		datasetCm : config.datasetCm,
		urlRecords : this.urlRecords,
		sitoolsAttachementForUsers : this.sitoolsAttachementForUsers,
		userPreference : config.userPreference, 
		bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE, 
		formParams : config.formParams, 
		formMultiDsParams : config.formMultiDsParams, 
		mainView : this, 
		datasetId : config.datasetId
	});
    
    this.store.filters = new sitools.widget.FiltersCollection({
        filters : config.filters 
    });
    
    this.store.addListener("beforeload", function (store, options) {
        //set the nocount param to false.
        //before load is called only when a new action (sort, filter) is applied
        if (!store.isInSort || store.isNewFilter) {
            options.params.nocount = false;
        } else {
            options.params.nocount = true;
        }
        store.isInSort = false;
        store.isNewFilter = false;
	    
        if (!Ext.isEmpty(store.filters)) {
            var params = store.buildQuery(store.filters.getFilterData());
            Ext.apply(options.params, params);
        }
	    
	    this._loadMaskAnchor = Ext.get(this.getView().mainBody.dom.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode);
	    
	    this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
	    
	    this.store.storeOptions(options);
        //this.el.mask(i18n.get('label.waitMessage'), "x-mask-loading");
    }, this);
    
    this.store.addListener("load", function (store, records, options) {
		if (this._loadMaskAnchor && this._loadMaskAnchor.isMasked()) {
			this._loadMaskAnchor.unmask();
		}
		this.topBar.updateContextToolbar();
    }, this);    
    
   
	/*
	 * Here is where the magic happens: BufferedGridView. The nearLimit is a
	 * parameter for the predictive fetch algorithm within the view. If your
	 * bufferSize is small, set this to a value around a third or a quarter of
	 * the store's bufferSize (e.g. a value of 25 for a bufferSize of 100; a
	 * value of 100 for a bufferSize of 300). The loadMask is optional but
	 * should be used to provide some visual feedback for the user when the
	 * store buffers (the loadMask from the GridPanel will only be used for
	 * initial loading, sorting and reloading).
	 */
    this.view = new Ext.ux.grid.livegrid.GridView({
        nearLimit : DEFAULT_NEAR_LIMIT_SIZE, 
		loadMask : {
			msg : i18n.get('label.waitMessage'),
			msgCls : "x-mask-loading"
        }, 
        datasetViewConfig : dataviewConfig
    });
    
//    this.view.addListener('buffer', function(view, store, rowIndex, visibleRows, totalCount) {
//        var columnModel = this.getColumnModel();
//        Ext.each(columnModel.columns, function (column, colIndex) {
//            if (!Ext.isEmpty(column.columnRenderer) && !columnModel.isHidden(colIndex)) {
//                for ( var row = 0; row <= visibleRows; row++) {
//                    var cell = view.getCell(row + rowIndex, colIndex);
//                    
//                    var element = Ext.get(cell);
//                    var link = element.child("a");
//                    link.addListener("click", function() {
//                        alert("ta mere");
//                    });
//                }
//                
//                var featureType = sitools.admin.datasets.columnRenderer.behaviorEnum.getColumnRendererCategoryFromBehavior(column.columnRenderer.behavior);
//            }
//        },this);        
//        
//    }, this);
	
    /*
	 * BufferedRowSelectionModel introduces a different selection model and a
	 * new <tt>selectiondirty</tt> event. You can keep selections between
	 * <b>all</bb> ranges in the grid; records which are currently in the
	 * buffer and are selected will be added to the selection model as usual.
	 * Rows representing records <b>not</b> loaded in the current buffer will
	 * be marked using a predictive index when selected. Selected rows will be
	 * successively read into the selection store upon scrolling through the
	 * view. However, if any records get added or removed, and selection ranges
	 * are pending, the selectiondirty event will be triggered. It is up to the
	 * user to either clear the pending selections or continue with requesting
	 * the pending selection records from the data repository. To put the whole
	 * matter in a nutshell: Selected rows which represent records <b>not</b>
	 * in the current data store will be identified by their assumed index in
	 * the data repository, and <b>not</b> by their id property. Events such as
	 * <tt>versionchange</tt> or <tt>selectiondirty</tt> can help in telling
	 * if their positions in the data repository changed.
	 */
    var selModelSimple = new Ext.ux.grid.livegrid.CheckboxSelectionModel({
        checkOnly : true,
        isSelectionModel : true
    });

    this.topBar = new sitools.user.component.dataviews.services.menuServicesToolbar({
        datasetUrl : this.sitoolsAttachementForUsers,
        datasetId : this.datasetId,
        dataview : this,
        origin : this.origin,
        columnModel : config.datasetCm
    });
    

    
    /**
     * {Ext.ux.grid.livegrid.Toolbar} Bottom bar of the liveGrid
     */
    this.bottomBar = new Ext.ux.grid.livegrid.Toolbar({
        view : this.view,
        enableOverflow: true,
        displayInfo : true,
        refreshText : i18n.get('label.refreshText')
    });
    
    
    //create a new columnModel with the selectionModel
    var configCol = cm.config;
    configCol.unshift(selModelSimple);
    cm = new Ext.grid.ColumnModel({
        columns : configCol
    }); 
    
    
    // -- CONSTRUCTOR --
	sitools.user.component.dataviews.livegrid.LiveGrid.superclass.constructor.call(this, Ext.apply({
	        view : this.view,
	        store : this.store,
	        layout : 'fit',
	        cm : cm,
	        sm : selModelSimple,
	        bbar : this.bottomBar,
	        dataviewUtils : sitools.user.component.dataviews.dataviewUtils, 
	        tbar : this.topBar, 
	        columnLines : true,
	        datasetId : config.datasetId,
	        componentType : "data",
	        listeners : {
	            scope : this,
	            sortchange: function (store, sortInfo) {
	                //force to deselect every rows after the sort as changed
	                this.getSelectionModel().clearSelections();
	            },
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
//	        plugins : [ filtersSimple ]
	    }, config));    

};

Ext.extend(sitools.user.component.dataviews.livegrid.LiveGrid, Ext.ux.grid.livegrid.EditorGridPanel, {
    /**
     * @private
	 * @returns the JsonColModel used to store the userPreferences.
	 */
    _getSettings : function () {
		return {
			datasetName : this.datasetName, 
			colModel : extColModelToJsonColModel(this.colModel.config), 
			datasetView : "Ext.ux.livegrid",
			datasetUrl : this.sitoolsAttachementForUsers, 
			dictionaryMappings : this.dictionaryMappings, 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };

    },
    /**
     * @private
     * return the filters of the liveGrid.
     * @return [] Array of filter object 
     */
    getFilters : function () {
	    return this.store.filters;
	}, 
	/**
     * @private
     * return the filters of the liveGrid.
     * @return [] Array of filter object 
     */
    getSortInfo : function () {
        return this.store.sortInfo;
    }, 
    /**
     * 
     * @return {String} 
     */
    getRequestParam : function () {
        var request = "", formParams = {};
    
        var colModel = extColModelToSrv(this.getColumnModel());
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
            request += "&" + this.getRecSelectedParamForLiveGrid();
        }
        return request;
    }, 
    getSelections : function () {
		return this.getSelectionModel().getSelections();
    }, 
    
    getNbRowsSelected : function () {
		return this.getSelectionModel().getAllSelections(false).length;
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
	getRecSelectedParamForLiveGrid : function () {
		var sm = this.getSelectionModel(), result;
		
		if (this.isAllSelected()) {
		    //First Case : all the dataset is selected.
		    result = "ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
            //We have to re-build all the request in case we use a range selection.
            result += this.getRequestParamWithoutSelection();
		}
		else if (Ext.isEmpty(sm.getPendingSelections())) {
			//second Case : no pending Selections.
			var recSelected = sm.getSelections();
			result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
		}
		else {
			//Second Case : there is a pending Selection, send all the ranges.
			var ranges = sm.getAllSelections(true);
			result = "ranges=" + Ext.util.JSON.encode(ranges);
			//We have to re-build all the request in case we use a range selection.
            result += this.getRequestParamWithoutSelection();
		}
		return result;
	},
    
    getSelectionForPlot : function () {
        var sm = this.getSelectionModel(), result;
        

        if (this.isAllSelected()) {
            //First Case : all the dataset is selected.
            result = "ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
        }
        else if (Ext.isEmpty(sm.getPendingSelections())) {
            //second Case : no pending Selections.
            var recSelected = sm.getSelections();
            result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
        }
        else {
            //Second Case : there is a pending Selection, send all the ranges.
            var ranges = sm.getAllSelections(true);
            result = "ranges=" + Ext.util.JSON.encode(ranges);
        }
        return result;
    },
    
    getDatasetView : function () {
        return this.getView();
    },
    
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons : function () {
        var array = [];
        array.push(new Ext.Toolbar.Separator());
        array.push({
            name : "columnsButton",
            tooltip : i18n.get('label.addOrDeleteColumns'),
            iconCls: 'x-cols-icon',
            menu : sitools.user.component.dataviews.dataviewUtils.createColMenu(this.getDatasetView(), this.getColumnModel())
        });
        this.getDatasetView().hdCtxIndex = 0;
        return array;
    },
    
    isAllSelected : function () {
        var nbRowsSelected = this.getNbRowsSelected();
        return nbRowsSelected === this.getStore().getTotalCount() || this.getSelectionModel().markAll;
    }
    
    
});

/**
 * @static Implementation of the method getParameters to be able to load view
 *         Config panel.
 * @return {Array} the parameters to display into administration view.
 */
sitools.user.component.dataviews.livegrid.LiveGrid.getParameters = function () {
	return [{
		jsObj : "Ext.slider.SingleSlider", 
		config : {
			minValue : 20, 
			maxValue : 100, 
			increment : 5, 
			width : 400,
			value : 25, 
			fieldLabel : i18n.get("label.lineHeight"), 
			plugins: [new Ext.ux.plugins.SliderRange(), new Ext.slider.Tip()], 
			parameterName : "lineHeight"
		}
	}];
};

 

Ext.reg('sitoolsLiveGrid', sitools.user.component.dataviews.livegrid.LiveGrid);
