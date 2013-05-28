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
/*global Ext, sitools, projectGlobal, commonTreeUtils, showResponse, DEFAULT_PREFERENCES_FOLDER, 
 document, i18n, $, Flotr, userLogin, SitoolsDesk, sql2ext, loadUrl,
 SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, DEFAULT_LIVEGRID_BUFFER_SIZE, WARNING_NB_RECORDS_PLOT*/
/*
 * @include "../viewDataDetail/viewDataDetail.js"
 * @include "../../sitoolsProject.js"
 */
/**
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&aid=3313793&group_id=531341&atid=2158259">[3313793]</a><br/>
 * 16/06/2011 m.gond {Display the right number of data plotted} <br/>
 * 
 * ExtJS layout for plotting data
 */

Ext.namespace('sitools.user.component');

/**
 * @cfg {String} dataUrl the dataset url attachment
 * @cfg {} columnModel the dataset's column model
 * @cfg {} filters the list of filters to apply (filters from the dataview)
 * @cfg {String} selections the selections as a String to add to the URL (selections from the dataview) 
 * @cfg {String} datasetName the name of the dataset
 * @cfg {} formParam list of parameters from a query form
 * @cfg {} formMultiDsParams list of parameters from a multidataset query form
 * @cfg {String} datasetId the id of the dataset
 * @cfg {string} componentType Should be "plot"
 * @requires sitools.user.component.viewDataDetail
 * @class sitools.user.component.dataPlotter
 * @extends Ext.Panel
 */
sitools.user.component.dataPlotter = function (config) {
//sitools.component.users.datasets.dataPlotter = function (config) {

    Ext.apply(this, config);
    
    this.dataUrl = config.dataUrl;
    this.datasetName = config.datasetName;
    this.datasetId = config.datasetId;
	
	/** Variable to know if the plot has been done once */
    this.hasPlotted = false;

    /** function to get numeric fields */
    function getNumericFields(arrayFields) {
        var numericFields = [];
        var store = new Ext.data.JsonStore({
			fields : [{
				name : "columnAlias", 
				type : "string"
			}, {
				name : "sqlColumnType", 
				type : "string"
			}]
        });
        Ext.each(arrayFields, function (field) {
            if (!Ext.isEmpty(field.sqlColumnType)) {
                var extType = sql2ext.get(field.sqlColumnType);
                if (extType.match(/^(numeric)+[48]?$/gi) !== null && !field.hidden) {
                    store.add(new Ext.data.Record(field));
                }
                if (extType.match(/dateAsString/gi) !== null && !field.hidden) {
                    store.add(new Ext.data.Record(field));
                }
            }  
        }, this);
        
        return store;
    }
    
    /** function to get numeric fields */
    function getVisibleFields(arrayFields) {
        var visibleFields = [];
        Ext.each(arrayFields, function (field) {
            if (!field.hidden) {
                visibleFields.push(field.columnAlias);
            }
        }, this);
        return visibleFields;
    }

    /**
     * Buffer range for display in the bottom bar
     */
    this.bufferRange = 300;

    /**
     * Dataset url for data details
     */
    var dataUrl = config.dataUrl;

    this.columnModel = config.columnModel;
    
    /** Initial fields list */
    var initialFields = getNumericFields(this.columnModel);
    /** Point tag field list */
    var pointTagFields = getVisibleFields(this.columnModel);
    /**
     * Whether or not there was a selection
     */
    this.isSelection = !Ext.isEmpty(config.selections); 
    
    /** Initial data */
//    var rawData = config.dataplot.data.items;

    /** field for x axis label */
    this.titleX = new Ext.form.Field({
        fieldLabel : i18n.get('label.plot.form.xlabel'), 
        anchor : "95%",
        name : "titleX",
        listeners : {
            scope : this,
            change : this.handlePlotLayout
        }
        
    });
    
    /** field for x axis label */
    this.xFormat = null;

    /** field for y axis label */
    this.titleY = new Ext.form.Field({
        fieldLabel : i18n.get('label.plot.form.ylabel'), 
        anchor : "95%",
        name : "titleY",
        listeners : {
            scope : this,
            change : this.handlePlotLayout
        }
    });
    /** field for x axis label */
    this.yFormat = null;

    
    /** combobox for x field */
    this.comboX = new Ext.form.ComboBox({
        store : initialFields, 
        anchor : "95%",
        name : "comboX",
        allowBlank : false,
        emptyText : i18n.get('label.plot.select.xaxis'),
        fieldLabel : i18n.get('label.plot.select.xcolumn'),
        selectOnFocus : true,
        triggerAction : 'all',
        valueField : "columnAlias", 
        displayField : "columnAlias", 
        editable : false,
        mode : 'local',
        listeners : {
            scope : this, 
            select : function (combo, record, index) {
                this.titleX.setValue(combo.getValue());
                var extType = sql2ext.get(record.get("sqlColumnType"));
                if (extType.match(/dateAsString/gi) !== null) {
                    if (Ext.isEmpty(this.xFormat)) {
						this.xFormat = new Ext.form.Field({
	                        fieldLabel : i18n.get('label.plot.form.xFormat'), 
					        anchor : "95%",
							name : "xFormat",
							value : config.userPreference && config.userPreference.xFormat ? config.userPreference.xFormat : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
                            listeners : {
					            scope : this,
					            change : this.handlePlotLayout
					        }
					    });
	                    this.fieldSetX.insert(1, this.xFormat);
                    }
                }
                else {
					this.fieldSetX.remove(this.xFormat);
					this.xFormat = null;
                }
                this.fieldSetX.doLayout();
            },
            expand : function (combo) {
                combo.store.clearFilter(true);
                if (this.comboY.getValue() !== '' && this.comboY.getValue() !== null) {
                    combo.store.filterBy(function (record, id) {
                        return record.get('field1') !== this.comboY.getValue();
                    }, this);
                }

            }
        }
    });
    
    /** combo box for y data */
    this.comboY = new Ext.form.ComboBox({
        store : initialFields, 
        name : "comboY",
        allowBlank : false,
        anchor : "95%",
        emptyText : i18n.get('label.plot.select.yaxis'),
        fieldLabel : i18n.get('label.plot.select.ycolumn'),
        selectOnFocus : true,
        editable : false,
        valueField : "columnAlias", 
        displayField : "columnAlias", 
        triggerAction : 'all',
        mode : 'local',
        listeners : {
            scope : this, 
            select : function (combo, record, index) {
                this.titleY.setValue(combo.getValue());
                var extType = sql2ext.get(record.get("sqlColumnType"));
                if (extType.match(/dateAsString/gi) !== null) {
                    if (Ext.isEmpty(this.yFormat)) {
						this.yFormat = new Ext.form.Field({
	                        fieldLabel : i18n.get('label.plot.form.yFormat'), 
					        anchor : "95%",
							name : "yFormat",
							value : config.userPreference && config.userPreference.yFormat ? config.userPreference.yFormat : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
                            listeners : {
                                scope : this,
                                change : this.handlePlotLayout
                            }
					    });
//	                    if (Ext.isEmpty(xFormat)) {
//							this.leftPanel.insert(2, yFormat);
//	                    }
//	                    else {
//							this.leftPanel.insert(3, yFormat);
//	                    }
					    this.fieldSetY.insert(1, this.yFormat);
                    }
                }
                else {
					this.fieldSetY.remove(this.yFormat);
					this.yFormat = null;
                }
                this.fieldSetY.doLayout();
            },
            expand : function (combo) {
                combo.store.clearFilter(true);
                if (this.comboX.getValue() !== '' && this.comboX.getValue() !== null) {
                    combo.store.filterBy(function (record, id) {
                        return record.get('field1') !== this.comboX.getValue();
                    }, this);
                }

            }
        }
    });

    /** field for x axis label */
    this.titlePlot = new Ext.form.Field({
        anchor : "95%",
        fieldLabel : i18n.get('label.plot.form.title'), 
        name : "titlePlot",
        listeners : {
            scope : this,
            change : this.handlePlotLayout
        }
    });
    
     /** field for x axis label */
    var numberRecords = new Ext.form.NumberField({
        anchor : "95%",
        fieldLabel : i18n.get('label.plot.form.nbRecords'), 
        name : "nbRecords",
        value : DEFAULT_LIVEGRID_BUFFER_SIZE,
        disabled : this.isSelection,
        allowBlank : false
    });

    /** checkbox for drawing line */
    this.checkLine = new Ext.form.Checkbox({
        fieldLabel : i18n.get('label.plot.form.drawline'), 
        name : "checkLine",
        scope : this,
        listeners : {
            scope : this,
            check : this.handlePlotLayout            
        }
    });
    
   

    /** Combo box for tag title */
    this.comboTag = new Ext.form.ComboBox({
        store : pointTagFields,
        name : "this.comboTag",
        anchor : "95%",
        allowBlank : true,
        emptyText : i18n.get('label.plot.select.tag'),
        fieldLabel : i18n.get('label.plot.select.tagcolumn'),
        selectOnFocus : true,
        triggerAction : 'all',
        mode : 'local',
        scope : this,
        listeners : {
            scope : this,
            select : this.handlePlotLayout
        }
    });
    
    this.comboXColor = new sitools.widget.colorField({
		fieldLabel : i18n.get('label.plot.label.color'),
        anchor : "95%",
		name : "comboXColor",
        value : "#000000",
        listeners : {
            scope : this,
            select : this.handlePlotLayout
        }
	});
    
    this.comboYColor = new sitools.widget.colorField({
		fieldLabel : i18n.get('label.plot.label.color'),
        anchor : "95%",
		name : "comboYColor", 
		value : "#000000",
        listeners : {
            scope : this,
            select : this.handlePlotLayout
        }
	});
	this.fieldSetX = new Ext.form.FieldSet({
		title : i18n.get('title.plot.xAxis'), 
		items : [this.comboX, this.titleX, this.comboXColor], 
		collapsible : true
	});
	this.fieldSetY = new Ext.form.FieldSet({
		title : i18n.get('title.plot.yAxis'), 
		items : [this.comboY, this.titleY, this.comboYColor], 
		collapsible : true
	});
    
   
    
    var urlRecords = config.dataUrl + '/records';
    //if there was a selection let's add the selection string to the urlRecords
    if (this.isSelection) {
        urlRecords += "?1=1&" + decodeURIComponent(config.selections);   
    }
    var sitoolsAttachementForUsers = config.dataUrl;
    
    this.storeData = new sitools.user.component.dataviews.tplView.StoreTplView({
        datasetCm : this.columnModel,         
        urlRecords : urlRecords,
        sitoolsAttachementForUsers : sitoolsAttachementForUsers,
        userPreference : config.userPreference, 
        formParams : (!this.isSelection ? config.formParams : undefined), 
        formMultiDsParams : (!this.isSelection ? config.formMultiDsParams : undefined), 
        mainView : this,
        datasetId : config.datasetId,
        isFirstCountDone : false,
        autoLoad : false,
        filters : config.filters,
        sortInfo : config.sortInfo
    });
    
    
    this.storeData.addListener("load", function (store, records, options) {
        this.displayPlot(records);
    }, this);
    
    
    this.storeData.on("beforeload", function (store, options) {
        //set the nocount param to false.
        //before load is called only when a new action (sort, filter) is applied
        var noCount;
        
        if (!store.isFirstCountDone) {
            options.params.nocount = false;
        } else {
            options.params.nocount = true;
        }
        
        if (!Ext.isEmpty(store.filters)) {
            var params = store.buildQuery(store.filters.getFilterData());
            Ext.apply(options.params, params);
        }
        
        this.storeData.storeOptions(options);
        
    }, this);
    
    
        /** button to draw the plot */
    this.drawPlotButton = new Ext.Button({
        text : i18n.get('label.plot.draw'),
        disabled : true,
        listeners : {
            scope : this, 
			click : function (button, e) {
                var form = this.leftPanel.getForm();
                var pageSize = form.findField("nbRecords").getValue();
                
                if (pageSize > WARNING_NB_RECORDS_PLOT) {   
                    Ext.Msg.show({
						title: i18n.get("label.warning"),
						msg: String.format(i18n.get("label.plot.toManyRecordsAsked"), pageSize, WARNING_NB_RECORDS_PLOT) ,
						buttons: Ext.Msg.YESNO,
						icon: Ext.MessageBox.WARNING,
						scope : this,
						fn : function (buttonId) {
                            if (buttonId === 'yes') {
                                this.loadPlot(pageSize);
                            }						
						}
					});
                } else {
                    this.loadPlot(pageSize);  
                }
            }
        }
    });
    
    var bbar;
    if (this.isSelection) {
        bbar = new Ext.Toolbar({
            hidden : true,
	        items : [ '->', {
	            id : 'plot-tb-text',
	            xtype : 'tbtext'
	//            text : 'Displaying ' + bufferSize + ' record' + (bufferSize > 1 ? 's' : '') + ' from ' + (bufferRange[0] + 1) + ' to ' + (bufferRange[1] + 1)
	        } ]
	    });
    } else {
        bbar = new Ext.PagingToolbar({
            hidden : true,
	        store: this.storeData,       // grid and PagingToolbar using same store
	        displayInfo: true,
	        pageSize: DEFAULT_LIVEGRID_BUFFER_SIZE,
            items : []
	    });        
    }
    
    
    
    /** right panel is the plot place */
    this.rightPanel = new Ext.Panel({
        id : 'plot-right-panel',
        title : i18n.get('title.plot.panel'),
        region : 'center',
        margins : '2 2 2 1',
        scope : this,
        listeners : {
            scope : this,
            bodyresize : function (window, width, height) {
                if (this.isVisible() && this.hasPlotted) {
                    if (!Ext.isEmpty(this.storeData.data)) {
	                    this.displayPlot(this.storeData.data.items);
                    }
                }
            },
            afterRender : function () {
                 // create a new loadingMask
		        this.loadMask = new Ext.LoadMask(this.rightPanel.getEl(), {
		            msg : i18n.get("label.plot.waitForPlot"),
                    store : this.storeData
		        });
                
            }
        },
        bbar : bbar
    });
    
   

    /** left panel is a form */
    this.leftPanel = new Ext.FormPanel({
        title : i18n.get('title.plot.form'),
        region : 'west',
        split : true,
        width : 300,
        autoScroll : true, 
        collapsible : true,
        margins : '2 1 2 2',
        cmargins : '2 2 2 2',
        padding : '5',
        monitorValid : true,
        items : [ this.titlePlot, numberRecords, this.checkLine, this.comboTag, this.fieldSetX, this.fieldSetY],
        buttons : [this.drawPlotButton],
        listeners : {
            scope : this,
            clientvalidation : function (panel, valid) {
                if (valid && (this.comboX.getValue() !== this.comboY.getValue())) {
                    this.drawPlotButton.setDisabled(false);
                } else {
                    this.drawPlotButton.setDisabled(true);
                }
            }
        }
    });
    
    
    /** Automatic plot refresh when buffering */
//    rightPanel.addListener('buffer', 
//		function (storage, rowindex, min, total) {
//			if (this.isVisible() && this.hasPlotted) {
//				rawData = storage.data.items;
//				bufferSize = storage.bufferSize;
//				bufferRange = storage.bufferRange;
//				bbar.findById('plot-tb-text').setText(
//                'Displaying ' + bufferSize + ' record' + (bufferSize > 1 ? 's' : '') + ' from ' + (bufferRange[0] + 1) + ' to ' + (bufferRange[1] + 1));
//				var plotConfig = getPlotConfig(columnModel, rawData);
//				this.plot = Flotr.draw($(rightPanel.body.id), [ plotConfig.data ], plotConfig.config);
//			}
//		}, 
//		this
//	);

    

    // /** Function to transform log checks in plot styles */
    // function scaleTypeFromCheckBox (checkbox) {
    // var style = 'linear';
    // if (checkbox.getValue()) {
    // style = 'logarithmic';
    // }
    // return style;
    // }

    
    
    /*
     * Constructor call
     */
    sitools.user.component.dataPlotter.superclass.constructor.call(this, Ext.apply({
        id : 'plot-panel',
        datasetName : config.datasetName, 
        layout : 'border',
        items : [ this.leftPanel, this.rightPanel ]
//        bbar : bbar
    }, config));
    
};

Ext.extend(sitools.user.component.dataPlotter, Ext.Panel, {
	/** 
     * Must be implemented to save window Settings
     * @return {}
     */
    _getSettings : function () {
        return {
			datasetName : this.datasetName, 
			leftPanelValues : this.leftPanel.getForm().getValues(), 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };
    }, 
    /**
     * Load the userPreferences...
     */
    afterRender : function () {
		sitools.user.component.dataPlotter.superclass.afterRender.call(this);
		this.el.on("contextmenu", function (e, t, o) {
			e.stopEvent();
            if (this.hasPlotted) {
				var ctxMenu = new Ext.menu.Menu({
					items : [{
						text : i18n.get('label.plot.savePng'), 
						scope : this, 
						handler : function () {
							this.plot.download.saveImage("png");
						}
					}]
				});
				ctxMenu.showAt(e.getXY());
	//			this.plot.saveImage("png");
            }
		}, this);
        
        
        
		if (Ext.isEmpty(this.userPreference)) {
			return;
		}
		var record, idx;
		//load the preference a first Time...
		this.leftPanel.getForm().loadRecord(new Ext.data.Record(this.userPreference.leftPanelValues));
		//fire select to create optional fields...
		var comboX = this.leftPanel.find("name", "comboX")[0];
		if (!Ext.isEmpty(this.comboX.getValue())) {
			idx = this.comboX.getStore().find("columnAlias", this.userPreference.comboX); 
			record = this.comboX.getStore().getAt(idx);
			if (record) {
				this.comboX.fireEvent("select", this.comboX, record, idx);
			}
		}
		var comboY = this.leftPanel.find("name", "comboY")[0];
		if (!Ext.isEmpty(this.comboY.getValue())) {
			idx = this.comboY.getStore().find("columnAlias", this.userPreference.comboY);
			record = this.comboY.getStore().getAt(idx);
			if (record) {
				this.comboY.fireEvent("select", this.comboY, record, idx);
			}
		}
		//reload the preference with all fields...
		this.leftPanel.getForm().loadRecord(new Ext.data.Record(this.userPreference));
    
        
    },
    /**
     * @private
     * Main function to display the plot directly from its data. Without a server call
     * @param {Array} records the list records to show in the plot
     */
    displayPlot : function (records) {
        this.storeData.isFirstCountDone = true;
        var plotConfig = this.getPlotConfig(this.columnModel, records);
        this.plot = Flotr.draw($(this.rightPanel.body.id), [ plotConfig.data ], plotConfig.config);
        $(this.rightPanel.body.id).stopObserving('flotr:click');
        $(this.rightPanel.body.id).observe('flotr:click', this.handleClick.bind(this, plotConfig));
        $(this.rightPanel.body.id).stopObserving('flotr:select');
        $(this.rightPanel.body.id).observe('flotr:select', function (evt) {
            var area = evt.memo[0];
            var options = plotConfig.config;
            options.xaxis.min = area.x1;
            options.xaxis.max = area.x2;
            options.yaxis.min = area.y1;
            options.yaxis.max = area.y2;
            this.plot = Flotr.draw($(this.id), [ plotConfig.data ], options);
        });
        this.hasPlotted = true;        
        
        if (this.isSelection) {
            this.rightPanel.getBottomToolbar().findById('plot-tb-text').setText(String.format(i18n.get("label.plot.displayNbRecords"),
                    records.length));
            
        } 
        this.rightPanel.getBottomToolbar().setVisible(true);
        this.doLayout();
        
        
    },
    /**
     * @private
     * Main function to display the plot by getting the data from the server
     * @param {int} pageSize the number of records to get
     */
    loadPlot : function (pageSize) {
        var form = this.leftPanel.getForm();
        
        if (!this.isSelection) {
            this.rightPanel.getBottomToolbar().pageSize = pageSize;
        }
        
        this.storeData.load({
            params : {
                start : 0,
                limit : pageSize
            }
            
        });
    },
    
    /**
     * 
     */
    
    
    /**
     * @private
     * Create plot-able dataset from the store
     * @param {} columnModel the columnModel of the dataset
     * @param {} storeItems the records from the store
     * @return the plot-able data 
     */
    createData : function (columnModel, storeItems) {
        var outData = [];
        Ext.each(storeItems, function (item) {
            var tag = this.comboTag.getValue() !== '' ? item.get(this.comboTag.getValue()) : null;
            var colXType = sql2ext.get(this.getColumn(columnModel, this.comboX.getValue()).sqlColumnType);
            var colYType = sql2ext.get(this.getColumn(columnModel, this.comboY.getValue()).sqlColumnType);
            var xValue, yValue;
            switch (colXType) {
            case "dateAsString" : 
                xValue = Date.parseDate(item.get(this.comboX.getValue()), SITOOLS_DATE_FORMAT, true);
                if (!Ext.isEmpty(xValue)) {
                    xValue = xValue.getTime();  
                }
                break;
            case "numeric" : 
                xValue = parseFloat(item.get(this.comboX.getValue()));
                break;
            default : 
                xValue = item.get(this.comboX.getValue());
                break;
            }
            switch (colYType) {
            case "dateAsString" : 
                var value = item.get(this.comboY.getValue());
                yValue = Date.parseDate(value, SITOOLS_DATE_FORMAT, true);
                
                if (!Ext.isEmpty(yValue)) {
                    yValue = yValue.getTime();  
                }
                break;
            case "numeric" : 
                yValue = parseFloat(item.get(this.comboY.getValue()));
                break;
            default : 
                yValue = item.get(this.comboY.getValue());
                break;
            }
            outData.push([ xValue, yValue, item.id, tag ? tag : item.id ]);
        }, this);
        return outData;
    },
    /**
     * Get a column from the given columnModel corresponding to the given columnAlias
     * @param {} columnModel the list of columns
     * @param {} columnAlias the column alias to search for
     * @return {} the columnFound or undefined if not found 
     */
    getColumn : function (columnModel, columnAlias) {
        var result;
        for (var i = 0; i < columnModel.length; i++) {
            if (columnModel[i].columnAlias == columnAlias) {
                result = columnModel[i];
            }
        }
        return result;
    },
    

    /**
     * @private
     * Create the config for a plot and add the given data to it 
     * @param {} columnModel the column model
     * @param {} newdata the list of records to plot
     * @return {} the config of the plot
     */
    getPlotConfig : function (columnModel, newdata) {
        var d1 = this.createData(columnModel, newdata);
        var yAxisFormat = "Normal";
        var colY = this.getColumn(columnModel, this.comboY.getValue());
        var colYType = sql2ext.get(colY.sqlColumnType);
        
        var xAxisFormat = "Normal";
        var colX = this.getColumn(columnModel, this.comboX.getValue());
        var colXType = sql2ext.get(colX.sqlColumnType);
        /**
         * Formater for the X axis
         */
        var colXFormater = function  (value) {
            if (colXType == "dateAsString") {
                var dt = new Date();
                dt.setTime(value);
                return dt.format(this.xFormat ? this.xFormat.getValue() : SITOOLS_DEFAULT_IHM_DATE_FORMAT);   
            }
            return value;
        };
        /**
         * Formater for the Y axis
         */
        var colYFormater = function (value) {
            if (colYType == "dateAsString") {
                var dt = new Date();
                dt.setTime(value);
                return dt.format(this.yFormat ? this.yFormat.getValue() : SITOOLS_DEFAULT_IHM_DATE_FORMAT);   
            }
            return value;
        }
        
        var plotConfig = {
            HtmlText : false,
            colors : [ '#00A8F0', '#C0D800', '#cb4b4b', '#4da74d', '#9440ed' ], // =>
            // The
            // default
            // colorscheme.
            // When
            // there
            // are
            // > 5
            // series,
            // additional
            // colors
            // are
            // generated.
            title : this.titlePlot.getValue(),
            legend : {
                show : true, // => setting to true will show the legend, hide
                // otherwise
                noColumns : 1, // => number of colums in legend table
                labelFormatter : null, // => fn: string -> string
                labelBoxBorderColor : '#ccc', // => border color for the
                // little label boxes
                container : null, // => container (as jQuery object) to put
                // legend in, null means default on top of
                // graph
                position : 'ne', // => position of default legend container
                // within plot
                margin : 5, // => distance from grid edge to default legend
                // container within plot
                backgroundColor : '#CCCCCC', // => null means auto-detect
                backgroundOpacity : 1.0
            // => set to 0 to avoid background, set to 1 for a solid background
            },
            xaxis : {
                ticks : null, // => format: either [1, 3] or [[1, 'a'], 3]
                noTicks : 5, // => number of ticks for automagically
                color : this.comboXColor.getValue() ? this.comboXColor.getValue() : "#000000", 
                //bind the formater to have the keed the correct scope
                tickFormatter : colXFormater.bind(this), 
                // generated ticks
                tickDecimals : null, // => no. of decimals, null means auto
                min : null, // => min. value to show, null means set
                // automatically
                max : null, // => max. value to show, null means set
                // automatically
                autoscaleMargin : 0, // => margin in % to add if auto-setting
                // min/max
                title : this.titleX.getValue(), 
                mode : colXType == "dateAsString" ? "time" : "Normal", 
                labelsAngle : colXType == "dateAsString" ? 45 : 0, 
                timeFormat : this.xFormat ? this.xFormat.getValue() : SITOOLS_DATE_FORMAT

            // ,
            // scale : scaleTypeFromCheckBox(logX)
            },
            yaxis : {
                ticks : null, // => format: either [1, 3] or [[1, 'a'], 3]
                color : this.comboYColor.getValue() ? this.comboYColor.getValue() : "#000000", 
                noTicks : 5, // => number of ticks for automagically
                // generated ticks
                tickDecimals : null, // => no. of decimals, null means auto
                //bind the formater to have the keed the correct scope
                tickFormatter : colYFormater.bind(this), 
                min : null, // => min. value to show, null means set
                // automatically
                max : null, // => max. value to show, null means set
                // automatically
                autoscaleMargin : 0, // => margin in % to add if auto-setting
                // min/max
                title : this.titleY.getValue(), 
                mode : colYType == "dateAsString" ? "time" : "Normal", 
                labelsAngle : 0, 
                timeFormat : SITOOLS_DATE_FORMAT
                
            // ,
            // scale : scaleTypeFromCheckBox(logY)
            },
            y2axis : {
                title : ' '
            },
            points : {
                show : true, // => setting to true will show points, false
                // will hide
                radius : 3, // => point radius (pixels)
                lineWidth : 2, 
                fill : true, // => true to fill the points with a color,
                // false for (transparent) no fill
                fillColor : '#ffffff' // => fill color
            },
            lines : {
                show : this.checkLine.getValue(), // => setting to true will show
                // lines, false will hide
                lineWidth : 0.1, // => line width in pixels
                fill : false, // => true to fill the area from the line to the
                // x axis, false for (transparent) no fill
                fillColor : null
            // => fill color
            },
            grid : {
                color : '#545454', // => primary color used for outline and
                // labels
                backgroundColor : '#FFFFFF', // => null for transparent, else
                // color
                tickColor : '#dddddd', // => color used for the ticks
                labelMargin : 3
            // => margin in pixels
            },
            selection : {
                mode : 'xy', // => one of null, 'x', 'y' or 'xy'
                color : '#B6D9FF', // => selection box color
                fps : 10
            // => frames-per-second
            },
            spreadsheet : {
                show : false
            },
            mouse : {
                track : true, // => true to track the mouse, no tracking
                // otherwise
                position : 'se', // => position of the value box (default
                // south-east)
                margin : 2, // => margin in pixels of the valuebox
                color : '#ff3f19', // => line color of points that are drawn
                // when mouse comes near a value of a series
                trackDecimals : 1, // => decimals for the track values
                sensibility : 2, // => the lower this number, the more
                // precise you have to aim to show a value
                radius : 3,
                trackFormatter  : (function (o) {return this.getTagValueFromObject(o); }).bind(this)
            // => radius of the track point
            },
            shadowSize : 4
        // => size of the 'fake' shadow
        };
        var out = {
            data : d1,
            config : plotConfig
        };
        return out;
    },
    
    /**
     * Function to show the details of a record
     * @param {} evt the event calling the function
     */
    showDataDetail : function (primaryKey) {
        
        
        var idx = encodeURIComponent(primaryKey);
        
        var jsObj = sitools.user.component.viewDataDetail;
        var componentCfg = {
            datasetUrl : this.dataUrl, 
            baseUrl : this.dataUrl + '/records',
            datasetId : this.datasetId, 
            fromWhere : "plot",
            url : this.dataUrl + '/records/' + idx, 
            preferencesPath : "/" + this.datasetName, 
            preferencesFileName : "dataDetails"
        };
        
        var windowConfig = {
            id : "simpleDataDetail" + this.datasetId, 
            title : i18n.get('label.viewDataDetail') + " : " + primaryKey,
            datasetName : this.datasetName, 
            saveToolbar : false, 
            type : "simpleDataDetail", 
            iconCls : "dataDetail"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
    },
    
    /**
     * Handler to change the plot layout.
     * Should be used in a form param event
     */
    handlePlotLayout : function () {
        if (this.hasPlotted) {
            this.displayPlot(this.storeData.data.items);
        }
    },
    
    handleClick : function (plotConfig, evt) {
        var memo = evt.memo[1];
        var object = memo.prevHit;
        if (Ext.isEmpty(object)) {
            var id = memo.el.id;
            var options = plotConfig.config;
            options.xaxis.min = null;
            options.xaxis.max = null;
            options.yaxis.min = null;
            options.yaxis.max = null;
            this.plot = Flotr.draw($(id), [ plotConfig.data ], options);
        } else {
            var primaryKey = this.getIdFromObject(object);
            this.showDataDetail(primaryKey);
        }
    },
    
    getIdFromObject : function (object) {
        var index = object.index;
        return object.series.data[index][2];
    },
    
    getTagValueFromObject : function (object) {
        var index = object.index;
        return object.series.data[index][3];
    } 
	
});

Ext.reg('sitools.user.component.dataPlotter', sitools.user.component.dataPlotter);
