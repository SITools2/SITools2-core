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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window,
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE,
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT,
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, ColumnRendererEnum, SITOOLS_DATE_FORMAT
*/
Ext.namespace('sitools.user.component.dataviews');

/**
 * A Simple Object to publish common methods to use dataviews in Sitools2.
 * @type 
 */
sitools.user.component.dataviews.dataviewUtils = {
//sitools.user.component.liveGrid.dataviewUtils = {
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
    

    /**
     * Get the renderer for a column from its featureType for the DataView
     * @param {Object} item col the Column definition
     * @param {Object} dataviewConfig the specific dataview Configuration.
     * @return {function} the renderer for a column
     */
    getRendererLiveGrid : function (item, dataviewConfig) {
        var renderer;
        if (!Ext.isEmpty(item.columnRenderer)) {
            renderer = function (value, metadata, record, rowIndex, colIndex,
                    store) {
                if (!Ext.isEmpty(value)) {
                    if (!Ext.isEmpty(item.columnRenderer.toolTip)){
                        metadata.attr = 'ext:qtip="' + item.columnRenderer.toolTip + '"';
                    }
                    
                    var imageStyle = "max-width:" + (item.width - 10) + "px;";
                    if (!Ext.isEmpty(dataviewConfig) && !Ext.isEmpty(dataviewConfig.lineHeight)) {
                        imageStyle += "max-height: " + (dataviewConfig.lineHeight - 10) + "px;";
                    }
                    var html = sitools.user.component.dataviews.dataviewUtils.getRendererHTML(item, imageStyle );
                    var str;
                    if (!Ext.isEmpty(html)) {
                        if (item.columnRenderer.behavior == ColumnRendererEnum.IMAGE_FROM_SQL) {
                            var imageUrl = record.get(item.columnRenderer.columnAlias);                     
                            str = Ext.String.format(html, value, imageUrl);
                        } else {
                            str = Ext.String.format(html, value);
                        }
                    }
                    return str;
                } else {
                    return value;
                }                
            };
        } else {
            renderer = function (value) {
                var valueFormat = value;
                if (sql2ext.get(item.sqlColumnType) == 'dateAsString') {
                    valueFormat = sitools.user.component.dataviews.dataviewUtils.formatDate(
                            value, item);
                }
                if (sql2ext.get(item.sqlColumnType) == 'boolean') {
                    valueFormat = value ? i18n.get('label.true') : i18n
                            .get('label.false');
                }
                return valueFormat;
            };
        }
        return renderer;
    },
    

    
    /**
     * Get the template to render a column from its featureType for the DataView
     * @param {Object} col the Column definition
     * @param {String} style the style to add to the label part
     * @param {Object} dataviewConfig the specific dataview Configuration.
     * @return {String} a template to render a column from its featureType for the DataView
     */
    getRendererDataView : function (col, style, dataviewConfig) {
            var tplString = "", value, behavior, label, valueDisplayed;
            var columnRenderer = col.columnRenderer;
            if (!Ext.isEmpty(columnRenderer)) {
                behavior = columnRenderer.behavior;
                var html = sitools.user.component.dataviews.dataviewUtils.getRendererHTML(col, dataviewConfig);
                switch (behavior) {
                case ColumnRendererEnum.URL_LOCAL :
                case ColumnRendererEnum.URL_EXT_NEW_TAB :
                case ColumnRendererEnum.URL_EXT_DESKTOP :
               
                case ColumnRendererEnum.DATASET_ICON_LINK :
                    if (!Ext.isEmpty(columnRenderer.linkText)) {
                        tplString += Ext.String.format("<tpl if=\"this.isNotEmpty({0})\">", col.columnAlias);              
                        value = Ext.String.format(html, "{" + col.columnAlias + "}");
                        tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> {1}</span>', col.header, value, style);
                        tplString += "</tpl>";            
                        tplString += Ext.String.format("<tpl if=\"this.isEmpty({0})\">", col.columnAlias);
                        value = "";
                        tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> {1}</span>', col.header, value, style);
                        tplString += "</tpl>";
                    } else if (!Ext.isEmpty(columnRenderer.image)) {
                        tplString += Ext.String.format("<tpl if=\"this.isNotEmpty({0})\">", col.columnAlias);
                        tplString += Ext.String.format('<li  class="img-link" ext:qtip="{0}">', col.header);
                        tplString += Ext.String.format(html, "{" + col.columnAlias + "}");
                        tplString += '</li></tpl>';
                    }
                    break;
                case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE :
                    tplString += Ext.String.format("<tpl if=\"this.isNotEmpty({0})\">", col.columnAlias);
                    tplString += Ext.String.format('<li  class="img-link" ext:qtip="{0}">', col.header);
                    tplString += Ext.String.format(html, "{" + col.columnAlias + "}");
                    tplString += '</li></tpl>';
                    break;
                case ColumnRendererEnum.IMAGE_FROM_SQL :
                    var imageUrl = "";
                    if (!Ext.isEmpty(columnRenderer.url)) {
                        imageUrl = columnRenderer.url;
                    } else if (!Ext.isEmpty(columnRenderer.columnAlias)) {
                        imageUrl = "{" + columnRenderer.columnAlias + "}";            
                    }
                    tplString += Ext.String.format("<tpl if=\"this.isNotEmpty({0})\">", col.columnAlias);
                    tplString += Ext.String.format('<li  class="img-link" ext:qtip="{0}">', col.header, imageUrl);
                    tplString += Ext.String.format(html, "{" + col.columnAlias + "}", imageUrl);
                    tplString += '</li></tpl>';
                    break;
                default :                                 
                    tplString += Ext.String.format("<tpl if=\"this.isNotEmpty({0})\">", col.columnAlias);
                    value = Ext.String.format(html, "{" + col.columnAlias + "}");
                    tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> {1}</span>', col.header, value, style);
                    tplString += "</tpl>";            
                    tplString += Ext.String.format("<tpl if=\"this.isEmpty({0})\">", col.columnAlias);
                    value = "";
                    tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> {1}</span>', col.header, value, style);
                    tplString += "</tpl>";                    
                    break;
                }
            } else {
                if (sql2ext.get(col.sqlColumnType) == 'dateAsString') {
                    tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> <tpl if=\"this.isValidDate({1})\">{[Date.parseDate(values.{1}, SITOOLS_DATE_FORMAT).format("{3}")]}</tpl></span>', 
                        col.header, 
                        col.columnAlias, 
                        style, 
                        Ext.isEmpty(col.format) ? SITOOLS_DEFAULT_IHM_DATE_FORMAT : col.format);
                }
                else {
                    tplString += Ext.String.format('<span class="dataview_columnValue"><div class=x-view-entete style="{2}">{0} </div> {{1}}</span>', col.header, col.columnAlias, style);
                }
            }
            return tplString;
        },
    
    /**
     * Get the HTML specific part to render a column corresponding to its featureType (columnRenderer)
     * It is a formated date where {0} must be replaced by the column value and {1} by the imageUrl to display in big only for ColumnRendererEnum.IMAGE_FROM_SQL
     * @param {Object} item the column definition
     * @param {Object} dataviewConfig the specific dataview Configuration.
     * @return {String} a formated HTML String 
     */
    getRendererHTML : function (item, imageStyle) {
        var renderer, valueDisplayed, imageUrl;
        var html;
        if (!Ext.isEmpty(item.columnRenderer) && !Ext.isEmpty(item.columnRenderer.behavior)) {
            
            var columnRenderer = item.columnRenderer;
            switch (columnRenderer.behavior) {
            case ColumnRendererEnum.URL_LOCAL :
            case ColumnRendererEnum.URL_EXT_NEW_TAB :
            case ColumnRendererEnum.URL_EXT_DESKTOP :
                if (!Ext.isEmpty(columnRenderer.linkText)) {
                    html = "<span class='link featureType' sitools:column='"+item.columnAlias+"'>" + columnRenderer.linkText + "</span>";
                } else if (!Ext.isEmpty(columnRenderer.image)) {
                    html = "<div class='image-link featureType' sitools:column='"+item.columnAlias+"'><img src=\"" + columnRenderer.image.url + "\" class='sitools-display-image' style ='" + imageStyle + "' ></img></div>";
                }
                break;
            case ColumnRendererEnum.IMAGE_NO_THUMB :
                html = "<span class='link featureType' sitools:column='"+item.columnAlias+"'>" + columnRenderer.linkText + "</span>"; 
                break;
            case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE :
                html = "<div class='image-link featureType' sitools:column='"+item.columnAlias+"'><img class='sitools-display-image' src='{0}' style ='" + imageStyle + "'></img></div>";  
                break;
            case ColumnRendererEnum.IMAGE_FROM_SQL :
                html = "<div class='image-link featureType' sitools:column='"+item.columnAlias+"'><img class='sitools-display-image image-link' src='{1}' style ='" + imageStyle + "'></div>"; 
                break;
            case ColumnRendererEnum.DATASET_LINK :
                html = "<span class='link featureType' sitools:column='"+item.columnAlias+"'>{0}</span>"; 
                break;
            case ColumnRendererEnum.DATASET_ICON_LINK :
                if (!Ext.isEmpty(columnRenderer.image)) {
                    imageUrl = columnRenderer.image.url;                    
                }
                html = "<div class='image-link featureType' sitools:column='"+item.columnAlias+"'><img style ='" + imageStyle + "' class='sitools-display-image' src='" + imageUrl + "'></div>";
                break;
            default : 
                html = "{0}"; 
                break;
            }
        } 

        return html;
    },
    
    getRendererViewDataDetails : function (item) {
        
        
    },
    

    /**
     * Execute the action on a featureType column. It can be either a
     * Gui_Service action if one is configured, or a classic featureType
     * action
     * 
     * @param column
     *            {Object} the column
     * @param record
     *            {Ext.data.Record} the record
     * @param controller
     *            {sitools.user.component.dataviews.services.GuiServiceController}
     *            the current Gui_Service controller
     */
    featureTypeAction : function (column, record, controller) {
        var service = controller.getService(column.columnAlias);
        if (!Ext.isEmpty(service)) {
            controller.callGuiService(service.id, record, column.columnAlias);
        }
        else {
            this.executeFeatureType(column, record);
        }
    },
    /**
     * Execute the featureType action depending on the given column and record
     * 
     * @param column
     *            {Object} the column
     * @param record
     *            {Ext.data.Record} the record
     */
    executeFeatureType : function (column, record) {
        if (!Ext.isEmpty(column.columnRenderer) && !Ext.isEmpty(column.columnRenderer.behavior)) {
            var value = record.get(column.columnAlias);
            var columnRenderer = column.columnRenderer;
            switch (columnRenderer.behavior) {
            case ColumnRendererEnum.URL_LOCAL :
                sitools.user.component.dataviews.dataviewUtils.downloadData(value);
                break;
            case ColumnRendererEnum.URL_EXT_NEW_TAB :
                window.open(value); 
                break;
            case ColumnRendererEnum.URL_EXT_DESKTOP :
                sitools.user.component.dataviews.dataviewUtils.showDisplayableUrl(value, columnRenderer.displayable); 
                break;
            case ColumnRendererEnum.IMAGE_NO_THUMB :
                sitools.user.component.dataviews.dataviewUtils.showPreview(value, columnRenderer.linkText); 
                break;
            case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE :
            case ColumnRendererEnum.IMAGE_FROM_SQL :
                sitools.user.component.dataviews.dataviewUtils.showPreview(value, column.header); 
                break;
            case ColumnRendererEnum.DATASET_LINK :
            case ColumnRendererEnum.DATASET_ICON_LINK :
                sitools.user.component.dataviews.dataviewUtils.showDetailsData(value, columnRenderer.columnAlias, columnRenderer.datasetLinkUrl); 
                break;
            default : 
                break;
            }
        } 
    },
    
    
    
    formatDate : function (value, item) {
        var valueFormat;
        var result = Date.parseDate(value, SITOOLS_DATE_FORMAT, true);                
        // try to build Date with "Y-m-d" format
        if (Ext.isEmpty(result)) {
            valueFormat = "";
        }
        else {
            if (Ext.isEmpty(item.format)) {
                valueFormat = result.format(SITOOLS_DEFAULT_IHM_DATE_FORMAT);
            }
            else {
                try {
                    valueFormat = result.format(item.format);
                }
                catch (err) {
                    valueFormat = "unable to format Date";
                }
            }
        }
        return valueFormat;    
    }, 
    /**
     * @static
     * Execute a REST OPTION request to the value url. 
     * Switch on Content-Type value to determine if we open a new iframe, or a window. 
     * @param {} value the url to request 
     */
    downloadData : function (value) {
    //    value = encodeURIComponent(value);
       //build first request to get the headers
        Ext.Ajax.request({
            url : value,
            method : 'HEAD',
            scope : this,
            success : function (ret) {
                try {
                    var headerFile = ret.getResponseHeader("Content-Type")
                            .split(";")[0].split("/")[0];
                    if (headerFile == "text") {
                        Ext.Ajax.request({
                            url : value,
                            method : 'GET',
                            scope : this,
                            success : function (ret) {
                                var windowConfig = {
                                    id : "winPreferenceDetailId",
                                    title : value, 
                                    iconCls : "version"
                                };
                                var jsObj = Ext.Panel;
                                var componentCfg = {
                                    layout : 'fit',
                                    autoScroll : true,
                                    html : ret.responseText
                                };
                                SitoolsDesk.addDesktopWindow(
                                        windowConfig, componentCfg,
                                        jsObj);
                            }
                        });
                    } else if (headerFile == "image") {
                        sitools.user.component.dataviews.dataviewUtils.showPreview(value, item.header);
                    } else {
                        sitools.user.component.dataviews.dataviewUtils.downloadFile(value);         
                    }
                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }
            },
            failure : function (ret) {
                return null;
            }
        });
    }, 
    /**
     * @static Build a MIF panel with a given url and load it into the desktop
     * @param {}
     *            value the url to request
     * @param {boolean}
     *            true if the url is displayable in a window, false otherwise
     */
    showDisplayableUrl : function (value, isDisplayable, customConfig) {
        if (isDisplayable) {
            
            if (customConfig) {
                var windowConfig = customConfig;
            }
            else {
                var windowConfig = {
                    title : value,
                    id : value, 
                    iconCls : "version"
                };
            }
            
            var jsObj = Ext.ux.ManagedIFrame.Panel;
            var componentCfg = {
                defaults : {
                    padding : 10
                },
                layout : 'fit',
                region : 'center',
                defaultSrc : value,
                listeners : {
                    documentloaded : function (iframe){
                        this.ownerCt.syncSize();
                    }
                }
            };
            
        SitoolsDesk.addDesktopWindow(
                windowConfig, componentCfg,
                jsObj);
        } else {             
            sitools.user.component.dataviews.dataviewUtils.downloadFile(value);                
        }
        
    }, 
    /**
     * Use a spcialized MIF to download datas...
     * @param {String} url the url to request.
     */
    downloadFile : function (url) {
        if (Ext.getCmp("mifToDownload")) {
            Ext.getCmp("mifToDownload").destroy();
        }
        
        var forceDlParam = "forceDownload=true";
        var defaultSrc = url + ((url.indexOf("?") === -1) ? "?" : "&") + forceDlParam;
        
        var mifToDownload = new Ext.ux.ManagedIFrame.Panel({
            layout : 'fit',
            id : "mifToDownload", 
            region : 'center',
            defaultSrc : defaultSrc, 
            renderTo : Ext.getBody(), 
            cls : 'x-hidden'
        });
        
    }, 
    /**
     * @static 
     * Definition of the showDetailData method used by the columnRenderer. Calls the
     * Livegrid corresponding to the dataset linked to the column. To filter the
     * data : use the form API : ["RADIO|" + columnAlias + "|'" + value + "'"]
     * @param {string} value
     * @param {string} columnAlias
     * @param {string} datasetUrl
     */
    showDetailsData : function (value, columnAlias, datasetUrl) {
        var desktop = getDesktop();
    
        // récupération des données du dataset
        Ext.Ajax.request({
            scope : this,
            method : 'GET',
            url : datasetUrl,
            success : function (response, opts) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }
                    var formParams = [ "RADIO|" + columnAlias + "|" + value ];
                    var dataset = json.dataset;
                    var jsObj = eval(dataset.datasetView.jsObject);
                    var componentCfg = {
                        dataUrl : dataset.sitoolsAttachementForUsers,
                        datasetId : dataset.id,
                        datasetCm : dataset.columnModel,
                        formParams : formParams, 
                        datasetName : dataset.name, 
                        dictionaryMappings : dataset.dictionaryMappings, 
                        datasetViewConfig : dataset.datasetViewConfig, 
                        preferencesPath : "/" + dataset.name, 
                        preferencesFileName : "datasetView"
                        
                    };
                    
                    var windowConfig = {
                        id : "wind" + dataset.id + columnAlias + value,
                        title : i18n.get('label.dataTitle') + " : " + dataset.name,
                        datasetName : dataset.name,
                        type : "data",
                        saveToolbar : true, 
                        iconCls : "dataDetail"
                    };
                    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
    
                } catch (err) {                
                }
            }
        });

    }, 
    /**
     * @static 
     * Definition of the showPreview method used by the columnRenderer.
     * @param {string} value The img src
     */
    showPreview : function (value, title) {
        var previewWin = new sitools.widget.WindowImageViewer({            
            title : title,
            src : value,
            hideAction : 'close',
            resizeImage : false
        });
        
        previewWin.show();
        previewWin.toFront();
    }, 
    /**
     * Return true if the column is NoClientAccess
     * @param {Object} column the column object
     * @return {boolean} true if the column should not be used in client
     */
    isNoClientAccess : function (column) {
        return !Ext.isEmpty(column.columnRenderer) &&  ColumnRendererEnum.NO_CLIENT_ACCESS == column.columnRenderer.behavior;
    }, 
    /**
     * @param {Array} listeColonnes
     *            ColumnModel of the grid
     * @param {Array} activeFilters
     *            Definition of the filters used to build the grid
     * 
     * @returns {Array} The filters configuration for the grid
     */
    getFilters : function (listeColonnes, activeFilters) {

        var filters = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            // First loop on all the columns
            Ext.each(listeColonnes, function (item, index, totalItems) {
                if (item.filter) {
                    var boolActiveFilter = false, activeFilterValue = "", activeComparison = "";
                    // loop on active filters to determine if there is an active
                    // filter on the column
                    Ext.each(activeFilters, function (activeFilter) {
                        if (item.columnAlias == activeFilter.columnAlias) {
                            boolActiveFilter = true;
                            // construct the value for the specific filter
                            if (activeFilter.data.type == 'numeric') {
                                if (!Ext.isObject(activeFilterValue)) {
                                    activeFilterValue = {};
                                }
                                activeFilterValue[activeFilter.data.comparison] = activeFilter.data.value;
                            } else if (activeFilter.data.type == 'date') {
                                var date = new Date();
                                var tmp = activeFilter.data.value.split('-');
                                date.setFullYear(tmp[0], tmp[1] - 1, tmp[2]);

                                if (!Ext.isObject(activeFilterValue)) {
                                    activeFilterValue = {};
                                }
                                if (activeFilter.data.comparison == 'eq') {
                                    activeFilterValue.on = date;
                                }
                                if (activeFilter.data.comparison == 'gt') {
                                    activeFilterValue.after = date;
                                }
                                if (activeFilter.data.comparison == 'lt') {
                                    activeFilterValue.before = date;
                                }
                            } else {
                                activeFilterValue = activeFilter.data.value;
                            }
                        }
                    });
                    var filter = {
                        type : sql2ext.get(item.sqlColumnType),
                        active : boolActiveFilter,
                        dataIndex : item.columnAlias,
                        columnAlias : item.columnAlias,
                        value : activeFilterValue
                    };

                    filters.push(filter);
                }
                i++;

            }, this);
        }
        return filters;

    },
    
    createColMenu : function (view, columnModel) {
        var colCount = columnModel.getColumnCount();
        var menu = new Ext.menu.Menu();
        
        for (var i = 0; i < colCount; i++) {
            if (columnModel.config[i].hideable !== false && !columnModel.config[i].isSelectionModel) {
                menu.add(new Ext.menu.CheckItem({
                    itemId : 'col-' + columnModel.getColumnId(i),
                    text : columnModel.getColumnHeader(i),
                    checked : !columnModel.isHidden(i),
                    hideOnClick : false,
                    disabled : columnModel.config[i].hideable === false,
                    listeners : {
                        scope : view,
                        checkchange : function (ci, checked) {
                            if (checked) {
                                var colModel = extColModelToSrv(columnModel);
                                view.grid.getStore().load({
                                    params : {
                                        colModel : Ext.util.JSON.encode(colModel)
                                    }
                                });
                            }
                        }
                    }
                }));
            }
        }
        menu.on('itemclick', view.handleHdMenuClick, view);
        
        return menu;
    },
    
    copyImageToClipboard : function CopyToClip(img) {
       
    }
};