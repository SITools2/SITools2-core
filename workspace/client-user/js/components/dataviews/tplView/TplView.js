/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
    
    if (!Ext.isEmpty(config.storeSort)) {
        if(!Ext.isEmpty(config.storeSort.sorters)){
            this.store.hasMultiSort = true;
            this.store.multiSortInfo = config.storeSort;
        }else {
            this.store.sortInfo = config.storeSort;
        }
    }
    
    //list of events to consider that everything is loaded
    this.allIsLoadedEvent = new Ext.util.MixedCollection();
    this.allIsLoadedEvent.add("load", false);
    this.allIsLoadedEvent.add("allservicesloaded", false);
    
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
			options.params.start = (config.startIndex) ? config.startIndex : 0;
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
    
	this.store.on('load', function (store, records, options) {
        var plotComp = Ext.getCmp("plot" + config.datasetId);
        if (plotComp) {
            var rightPanel = plotComp.findById('plot-right-panel');
            var success = rightPanel.fireEvent('buffer', store);
        }
        store.isFirstCountDone = true;
        this.getTopToolbar().updateContextToolbar();
        this.processFeatureType();
        this.removeLoadMask("load");
	}, this);

	
    
    
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
        selectedClass : 'x-view-selected-datasetView',
        emptyText: '', 
        simpleSelect : true,
        ready : false,
        refresh : function() {
            this.clearSelections(false, true);
            var el = this.getTemplateTarget(),
                records = this.store.getRange();
                
            el.update('');
            if(records.length < 1){
                if(!this.deferEmptyText || this.hasSkippedEmptyText) {
                    el.update(this.emptyText);
                }
                this.all.clear();
            }else {
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
        onDataChanged: function() {
            this.newDataLoaded = true;
            if (this.blockRefresh !== true) {
                this.refresh.apply(this, arguments);
            }
        },
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
				
				//destroy all selections if all was selected and another row is selected
				if (this.isAllSelected() && recNodes.length === DEFAULT_LIVEGRID_BUFFER_SIZE - 1) {
				    this.selectAllRows.toggle();
				    this.deselectAll();
				    var unselectedRec = this.getUnselectedRow(recs, this.store.data.items);
				    this.select(unselectedRec);
				}
			},
			newdataloaded : function () {
		        if (!Ext.isEmpty(this.ranges)) {
					if (!Ext.isEmpty(this.nbRecordsSelection) && (this.nbRecordsSelection == this.store.getTotalCount())) {
					    this.getCustomToolbarButtons();
					    this.selectAllRows.toggle(true);
                        delete this.nbRecordsSelection;
                    } else {
                        var ranges = Ext.util.JSON.decode(this.ranges);
                        this.selectRangeDataview(ranges);
                        delete this.ranges;
                    }
		        }
			},
			afterrender : function () {
			    if (!Ext.isEmpty(this.dataView)) {
		            this._loadMaskAnchor = Ext.get(this.el.dom);
		            this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
		        }   
			}
        }
	});
	
	this.dataView.addEvents('newdataloaded');
	
	var configDataViewPanel = {
		autoScroll : true,
		items : [this.dataView], 
		region : 'center'
	};

	var panelWest = new Ext.Panel(configDataViewPanel);
	
	this.panelDetail = new sitools.user.component.viewDataDetail({
		fromWhere : "dataView",
		grid : this, 
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
	
	if (! Ext.isEmpty(config.userPreference) && config.userPreference.datasetView === "sitools.user.component.dataviews.tplView.TplView") {
		this.panelDetail.setWidth(config.userPreference.viewPanelDetailSize.width);
	}
	
	this.tbar = new sitools.user.component.dataviews.services.menuServicesToolbar({
        datasetUrl : this.sitoolsAttachementForUsers,
        datasetId : this.datasetId,
        dataview : this,
        origin : this.origin,
        columnModel : config.datasetCm,
        listeners : {
            scope : this,
            allservicesloaded : function () {
                this.removeLoadMask("allservicesloaded");
            }
        }  
    });
    
	this.items = [panelWest, this.panelDetail];
	this.dataviewUtils = sitools.user.component.dataviews.dataviewUtils;
	        
	this.bbar = new sitools.user.component.dataviews.tplView.dataViewPagingToolbar({
        store: this.store,       // grid and PagingToolbar using same store
        displayInfo: true,
        pageSize: DEFAULT_LIVEGRID_BUFFER_SIZE,
        items : [],
        listeners : {
            scope : this,
            change : function () {
                if (this.isAllSelected()) {
                    this.selectAll();
                }
            }
        } 
    });
	
	this.store.load({
        params : {
//          start : 0, 
            start : (config.startIndex) ? config.startIndex : 0, 
            limit : DEFAULT_LIVEGRID_BUFFER_SIZE
        },
        scope : this
    });
	
	Ext.apply(config, this);
	sitools.user.component.dataviews.tplView.TplView.superclass.constructor.call(this, config);
};

Ext.extend(sitools.user.component.dataviews.tplView.TplView, Ext.Panel, {
	layout : 'border',
	tbar : this.tbar,
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
    getSelectionModel : function () {
        return this.dataView;
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
	
	getRequestParamWithoutColumnModel : function () {
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
	
	getSelectionsRange : function () {
		//var sm = this.getSelectionModel();
//		var ranges = sm.getSelectedIndexes();
		var ranges = this.getAllSelections(true);
		return Ext.util.JSON.encode(ranges);
	},
	
	getAllSelections : function (asRange) {
        var index = 1;
        var ranges = [];
        var currentRange = 0;
        var tmpArray =  this.getSelectionModel().getSelectedIndexes();

        //for (var i in this.getSelectionModel().getSelectedIndexes()) {
            //tmpArray.push(parseInt(i));
        //}

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

        ranges[currentRange] = [ tmpArray[0], tmpArray[0] ];
        for (var i = 0, max_i = max_i - 1; i < max_i; i++) {
            if (tmpArray[i + 1] - tmpArray[i] == 1) {
                ranges[currentRange][1] = tmpArray[i + 1];
            } else {
                currentRange++;
                ranges[currentRange] = [ tmpArray[i + 1], tmpArray[i + 1] ];
            }
        }

        return ranges;
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
    },
    
    /**
     * Return an empty array (can be modifiable)
     * @returns {Array}
     */
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons : function () {
        var array = [];
        var iconCls = (this.isAllSelected()) ? "checkbox-icon-on" : "checkbox-icon-off";
        var pressed = this.isAllSelected();
        
        
        this.selectAllRows = new Ext.Button({
            name : "selectAll",
            iconCls: iconCls,
            enableToggle : true,
            scope : this,
            text : (pressed) ? i18n.get('label.deselectAll') : i18n.get('label.selectAll'),
            cls : 'services-toolbar-btn',
            pressed : pressed,
            handler :  function (button, e) {
                if (button.pressed) {
                    this.selectAll();
                } else {
                    this.deselectAll();
                }
            },
            toggleHandler : function (button, pressed) {
                if (pressed) {
                    button.setIconClass("checkbox-icon-on");
                } else {
                    button.setIconClass("checkbox-icon-off");
                }
            }      
            
        });
        array.push(this.selectAllRows);
        array.push(new Ext.Toolbar.Separator());
        return array;
    },
    
    selectAll : function () {
        this.dataView.selectRange(0, DEFAULT_LIVEGRID_BUFFER_SIZE);
    },
    
    deselectAll : function () {
        this.dataView.clearSelections();
    },
    
    isAllSelected : function () {
        return (!Ext.isEmpty(this.selectAllRows) && this.selectAllRows.pressed);
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
    getUnselectedRow : function (selectedRecs, allRecs) {
        var unselected = null;
        Ext.each(allRecs, function (record) {
            if (selectedRecs.indexOf(record) === -1) {
                unselected = record;
                return;
            }
        }, this);
        return unselected;
    },
    
    getColumnFromColumnModel : function (columnAlias) {
        var index = Ext.each(this.columnModel.columns, function (col) {
            if (col.columnAlias === columnAlias) {
                return false;
            }
        }, this);
        if (!Ext.isEmpty(index)) {
            return this.columnModel.columns[index];
        } else {
            return null;
        }
    },
    
    callbackClickFeatureType : function (e, t, o) {
        e.stopEvent();
        var record = o.record;
        var controller = o.controller;        
        var column = o.column;
        sitools.user.component.dataviews.dataviewUtils.featureTypeAction(column, record, controller);
    },

    selectRangeDataview : function (ranges) {
        Ext.each(ranges, function (range) {
            this.getSelectionModel().selectRange(range[0], range[1], true);
        }, this);
    },
    
    processFeatureType : function () {
        var nodes = this.dataView.getNodes();
        var controller = this.getTopToolbar().guiServiceController;
        Ext.each(nodes, function (node) {
            var record = this.dataView.getRecord(node);
            var featureTypeNodes = Ext.DomQuery.jsSelect(".featureType", node);
            Ext.each(featureTypeNodes, function (featureTypeNode) {
                var featureTypeNodeElement = Ext.get(featureTypeNode);

                var columnAlias = featureTypeNodeElement.getAttribute("column", "sitools");
                var column = this.getColumnFromColumnModel(columnAlias);

                featureTypeNodeElement.addListener("click", this.callbackClickFeatureType, this, {
                    record : record,
                    controller : controller,
                    column : column
                });
            }, this);
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
sitools.user.component.dataviews.tplView.TplView.getParameters = function () {
	return [];
};

