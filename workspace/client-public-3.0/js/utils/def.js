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
/*!
 * Ext JS Library 3.0+
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */

/*global Ext, i18n, date, Digest, sql2ext, SitoolsDesk, loadUrl, sitools, showResponse, ColumnRendererEnum, document, localStorage, SITOOLS_DATE_FORMAT, window*/

Ext.define("sitools.public.utils.def",{
    singleton : true
});

// GLOBAL BEHAVIOUR
var DEFAULT_NATIVEJSON = false; //must be set to false to constrain Ext.doEncode (if true, doesn't work with prototype library)
var DEFAULT_TIMEOUT = 30000; // server request timeout (msec)
var DEFAULT_TIMEBUF = 10; // time to wait before sending request (msec)
var DEFAULT_NBRETRY = 0;// 3; // nb of request retries (if failure)
var SERVER_OK = 200;
var DEFAULT_WIN_HEIGHT = 400;
var DEFAULT_WIN_WIDTH = 600;
var DEFAULT_WINORDER_WIDTH = 400;
var DEFAULT_ORDER_FOLDER = "dataSelection";
var DEFAULT_PREFERENCES_FOLDER = "preferences";
var DEFAULT_LIVEGRID_BUFFER_SIZE = 300; 
//Duration of the session in minutes, cannot be over than 35000 (35000 * 60 * 1000 > 32 bit signed integer max value)
var MULTIDS_TIME_DELAY = 2000;
var SITOOLS_DEFAULT_PROJECT_IMAGE_URL = "/sitools/res/images/sitools2_logo.png";
var EXT_JS_VERSION=Ext.versions.extjs.version;
var EXT_JS_FOLDER="extjs4/ext-"+EXT_JS_VERSION;
/**
 * The nearLimit is a
     * parameter for the predictive fetch algorithm within the view. If your
     * bufferSize is small, set this to a value around a third or a quarter of
     * the store's bufferSize (e.g. a value of 25 for a bufferSize of 100;
 */
var DEFAULT_NEAR_LIMIT_SIZE = 100;

Ext.BLANK_IMAGE_URL = '/sitools/client-public/cots/'+EXT_JS_FOLDER+'/resources/themes/images/default/tree/s.gif';
Ext.USE_NATIVE_JSON  = DEFAULT_NATIVEJSON;
Ext.Ajax.timeout = DEFAULT_TIMEOUT;

var DEFAULT_LOCALE = "en";
var SITOOLS_DATE_FORMAT = 'Y-m-d\\TH:i:s.u';
var SITOOLS_DEFAULT_IHM_DATE_FORMAT = 'Y-m-d H:i:s.u';

var onRequestFeedException = function (proxy, response, operation, eOpts) {
    // si on a un cookie de session et une erreur 403
    if ((response.status == 403) && !Ext.isEmpty(Ext.util.Cookies.get('hashCode'))) {
        Ext.MessageBox.minWidth = 360;
        Ext.MessageBox.alert(i18n.get('label.session.expired'), response.responseText);
    } else {
        Ext.MessageBox.minWidth = 360;
        Ext.MessageBox.alert(i18n.get('label.error'), response.responseText);
    }
    return false;
};

// GLOBAL VARIABLES
var desktop;
var projectGlobal;
var projectId;

// Application d'exception sur tous les JsonStores :
// Ext.override (Ext.data.JsonStore, {
// listeners : {
// exception : function (dataProxy, type, action, options, response){
// Ext.Msg.alert (i18n.get('label.warning'), response.responseText);
// }
// }
// });

// GLOBAL FUNCTIONS
function alertFailure(response, opts) {
	var txt;
	if (response.status == SERVER_OK) {
		var ret = Ext.decode(response.responseText).message;
		txt = i18n.get('msg.error') + ': ' + ret;
	} else {
		txt = i18n.get('warning.serverError') + ': ' + response.statusText;
	}
	Ext.Msg.alert(i18n.get('label.warning'), txt);
// Ext.WindowMgr.bringToFront(alert);
}

function extColModelToJsonColModel(ExtColModel) {
	var colModel = [];
	var columns;
	if (!Ext.isEmpty(ExtColModel.columns)) {
		columns = ExtColModel.columns;
	}
	else {
		columns = ExtColModel;
	}
	Ext.each(columns, function (column) {
        colModel.push({
            columnAlias : column.columnAlias,
            dataIndex : column.dataIndexSitools,
            dataIndexSitools : column.dataIndexSitools,
            header : column.header,
            filter : column.filter,
            hidden : column.hidden,
            id : column.id,
            previewColumn : column.previewColumn,
            primaryKey : column.primaryKey,
            schema : column.schema,
            sortable : column.sortable,
            sqlColumnType : column.sqlColumnType,
            tableAlias : column.tableAlias,
            tableName : column.tableName,
            toolTip : column.tooltip,
            urlColumn : column.urlColumn,
            width : column.width,
            columnRenderer : column.columnRenderer,
            specificColumnType : column.specificColumnType,
            javaSqlColumnType : column.javaSqlColumnType,
            format : column.format,
            category : column.category
        });
	});
	return colModel;
}

function extColModelToSrv(ExtColModel) {
	var colModel = [];
	var columns;
	if (!Ext.isEmpty(ExtColModel.columns)) {
		columns = ExtColModel.columns;
	}
	else {
		columns = ExtColModel;
	}
	Ext.each(columns, function (column) {
		if (!column.hidden && !column.isSelectionModel) {
			colModel.push(column.columnAlias);
		}
	});
	return colModel.join(", ");
}

function extColModelToStorage(ExtColModel) {
    var colModel = [];
    var columns;
    if (!Ext.isEmpty(ExtColModel.columns)) {
        columns = ExtColModel.columns;
    }
    else {
        columns = ExtColModel;
    }
    Ext.each(columns, function (column) {
        if (!column.hidden && !column.isSelectionModel) {
            colModel.push({
                columnAlias : column.columnAlias, 
                dataIndex : column.dataIndex, 
                dataIndexSitools : column.dataIndexSitools, 
                editor : column.editor, 
                filter : column.filter, 
                header : column.header, 
                hidden : column.hidden, 
                id : column.id, 
                isColumn : column.isColumn, 
                previewColumn : column.previewColumn, 
                primaryKey : column.primaryKey, 
                schema : column.schema, 
                sortable : column.sortable, 
                sqlColumnType : column.sqlColumnType, 
                tableAlias : column.tableAlias, 
                tableName : column.tableName, 
                toolTip : column.tooltip, 
                urlColumn : column.urlColumn, 
                width : column.width, 
//				columnAliasDetail : column.columnAliasDetail,
				columnRenderer : column.columnRenderer, 
//				datasetDetailId : column.datasetDetailId, 
				specificColumnType : column.specificColumnType, 
				javaSqlColumnType : column.javaSqlColumnType,
                unit : column.unit,
                format : column.format,
                category : column.category
//                image : column.image,
//                datasetDetailUrl : column.datasetDetailUrl
            });
        }
    });
    return colModel;
}
/**
 * Get the Sitools Desktop
 * @returns the sitools Desktop
 */
function getDesktop() {
	if (Ext.isEmpty(this.SitoolsDesk)) {
		return null;
	}
	else {
		return this.SitoolsDesk.app.desktop;
	}
}

/**
 * Get the Sitools Application
 * @returns the sitools Desktop
 */
function getApp() {
	if (Ext.isEmpty(SitoolsDesk)) {
		return null;
	}
	else {
		return SitoolsDesk.app;
	}
}

// Ext.WindowMgr = getDesktop().getManager();
// Override de la méthode initEvents pour que le windowManager utilisé soit
// toujours le même
Ext.override(Ext.window.Window, {
    initEvents : function () {
	    Ext.window.Window.superclass.initEvents.call(this);
	    if (this.animateTarget) {
	        this.setAnimateTarget(this.animateTarget);
	    }
	
	    if (this.resizable) {
	        this.resizer = new Ext.Resizable(this.el, {
	            minWidth: this.minWidth,
	            minHeight: this.minHeight,
	            handles: this.resizeHandles || 'all',
	            pinned: true,
	            resizeElement : this.resizerAction,
	            handleCls: 'x-window-handle'
	        });
	        this.resizer.window = this;
	        this.mon(this.resizer, 'beforeresize', this.beforeResize, this);
	    }
	
	    if (this.draggable) {
	        this.header.addClass('x-window-draggable');
	    }
	    this.mon(this.el, 'mousedown', this.toFront, this);
// this.manager = this.manager || Ext.WindowMgr;
	    var tmp = getDesktop();
	    if (Ext.isEmpty(tmp)) {
	        this.manager = Ext.WindowMgr;
	    }
	    else {
		    this.manager = getDesktop().getManager() || Ext.WindowMgr;
	    }
	    this.manager.register(this);
	    if (this.maximized) {
	        this.maximized = false;
	        this.maximize();
	    }
	    if (this.closable) {
	        var km = this.getKeyMap();
	        km.on(27, this.onEsc, this);
	        km.disable();
	    }
	}
});

Ext.override(Ext.grid.Panel, {
    stripeRows : true
});

Ext.data.Types.DATEASSTRING = {
	convert : function (v, data) {
		return v;
	}, 
	sortType : function (v) {
		return v;
	}, 
	type : "dateAsString"
	
};


function includeJs(url) {
	if (Ext.isEmpty(url)) {
		return;
	}
	var head = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.setAttribute('src',	url);
	script.setAttribute('type', 'text/javascript');
	head.appendChild(script);
}

/**
 * Include JS scripts in the given order and trigger callback when all scripts are loaded  
 * @param ConfUrls {Array} the list of scripts to load
 * @param indexAInclure {int} the index during the iteration
 * @param callback {function} the callback
 * @param scope {Object} the scope of the callback
 */
function includeJsForceOrder(ConfUrls, indexAInclure, callback, scope) {
    //Test if all inclusions are done for this list of urls
    if (indexAInclure < ConfUrls.length) {
        var url = ConfUrls[indexAInclure].url;
        
        var trouve = false;
        var targetEl = "script";
        var targetAttr = "src";
        var scripts = document.getElementsByTagName(targetEl);
        var script;
        for (var i = scripts.length; i > 0; i--) {
            script = scripts[i - 1];
            if (script && script.getAttribute(targetAttr) !== null && script.getAttribute(targetAttr).indexOf(url) != -1) {
                trouve = true;
            }
        }
        if (!trouve) {
            // if not : include the Js Script
            var DSLScript = document.createElement("script");
            DSLScript.type = "text/javascript";
            DSLScript.onload = includeJsForceOrder.createDelegate(this, [ ConfUrls, indexAInclure + 1, callback, scope ]);
            DSLScript.onreadystatechange = includeJsForceOrder.createDelegate(this, [ ConfUrls, indexAInclure + 1, callback, scope ]);
            DSLScript.onerror = includeJsForceOrder.createDelegate(this, [ ConfUrls, indexAInclure + 1, callback, scope ]);
            DSLScript.src = url;

            var headID = document.getElementsByTagName('head')[0];
           headID.appendChild(DSLScript);           
        } else {
            includeJsForceOrder(ConfUrls, indexAInclure + 1, callback, scope);
        }
    } else {
        if (!Ext.isEmpty(callback)) {
            if (Ext.isEmpty(scope)) {
                callback.call();
            } else {
                callback.call(scope);
            }
        }
    }
}

function includeCss(url) {
	var headID = document.getElementsByTagName("head")[0];
	var newCss = document.createElement('link');
	newCss.type = 'text/css';
	newCss.rel = 'stylesheet';
	newCss.href = url;
	newCss.media = 'screen';
	// pas possible de monitorer l'evenement onload sur une balise link
	headID.appendChild(newCss);
}

/**
 * Build a {Ext.grid.ColumnModel} columnModel with a dataset informations
 * @param {Array} listeColonnes Array of dataset Columns
 * @param {Array} dictionnaryMappings Array of Dataset dictionnary mappings 
 * @param {Object} dataviewConfig the specific dataview Configuration.
 * @return {Ext.grid.ColumnModel} the builded columnModel
 */
function getColumnModel(listeColonnes, dictionnaryMappings, dataviewConfig, dataviewId) {
    var columns = [];
    if (!Ext.isEmpty(listeColonnes)) {
        Ext.each(listeColonnes, function (item, index, totalItems) {
            
            var tooltip = "";
            if (item.toolTip) {
                tooltip = item.toolTip;
            } else {
                if (Ext.isArray(dictionnaryMappings) && !Ext.isEmpty(dictionnaryMappings)) {
                    var dico = dictionnaryMappings[0];
                    var dicoMapping = dico.mapping || [];
                    dicoMapping.each(function (mapping) {
                        if (item.columnAlias == mapping.columnAlias) {
                            var concept = mapping.concept || {};
                            if (!Ext.isEmpty(concept.description)) {
                                tooltip += concept.description.replace('"', "''") + "<br>";
                            }
                        }
                    });
                }
            }
           
            var renderer = sitools.user.utils.DataviewUtils.getRendererLiveGrid(item, dataviewConfig, dataviewId);
            var hidden;
            if (Ext.isEmpty(item.visible)) {
                hidden = item.hidden;
            } else {
                hidden = !item.visible;
            }
            if (Ext.isEmpty(item.columnRenderer) ||  ColumnRendererEnum.NO_CLIENT_ACCESS != item.columnRenderer.behavior) {
	            columns.push(Ext.create('Ext.grid.column.Column', {
	                columnAlias : item.columnAlias,
	                dataIndexSitools : item.dataIndex,
	                dataIndex : item.columnAlias,
	                header : item.header,
	                width : item.width,
	                sortable : item.sortable,
	                hidden : hidden,
	                tooltip : tooltip,
	                renderer : renderer,
	                schema : item.schema,
	                tableName : item.tableName,
	                tableAlias : item.tableAlias,
	                id : item.id,
	                // urlColumn : item.urlColumn,
	                primaryKey : item.primaryKey,
	                previewColumn : item.previewColumn,
	                filter : item.filter,
	                sqlColumnType : item.sqlColumnType, 
//	                columnAliasDetail : item.columnAliasDetail,
					columnRenderer : item.columnRenderer, 
//					datasetDetailId : item.datasetDetailId, 
					specificColumnType : item.specificColumnType,
//	                image : item.image,
//	                datasetDetailUrl : item.datasetDetailUrl,
					menuDisabled : true,
	                format : item.format,
	                category : item.category
	            }));
            }
        }, this);
    }

    var cm = {
        items : columns
    };
    return cm;
}


Ext.override(Ext.menu.DatePicker, {
    initComponent : function () {
        this.on('beforeshow', this.onBeforeShow, this);
        if (this.strict == (Ext.isIE7 && Ext.isStrict)) {
            this.on('show', this.onShow, this, {single: true, delay: 20});
        }
        Ext.apply(this, {
            plain: true,
            showSeparator: false,
            items: this.picker = new Ext.SitoolsDatePicker(Ext.applyIf({
                internalRender: this.strict || !Ext.isIE,
                ctCls: 'x-menu-date-item',
                id: this.pickerId
            }, this.initialConfig))
        });
        this.picker.purgeListeners();
        Ext.menu.DatePicker.superclass.initComponent.call(this);
        
        this.relayEvents(this.picker, ['select']);
        this.on('show', this.picker.focus, this.picker);
        this.on('select', this.menuHide, this);
        if (this.handler) {
            this.on('select', this.handler, this.scope || this);
        }
    }
});

Ext.override(Ext.form.field.Date,  {
    
    showTime : false,
    
    onTriggerClick : function () {
        if (this.disabled) {
            return;
        }
        if (Ext.isEmpty(this.menu)) {
            this.menu = new Ext.menu.DatePicker({
                hideOnClick: false,
                showTime : this.showTime, 
                focusOnSelect: false
            });
        }
        this.onFocus();
        Ext.apply(this.menu.picker,  {
            minDate : this.minValue,
            maxDate : this.maxValue,
            disabledDatesRE : this.disabledDatesRE,
            disabledDatesText : this.disabledDatesText,
            disabledDays : this.disabledDays,
            disabledDaysText : this.disabledDaysText,
            format : this.format,
            showToday : this.showToday,
            minText : Ext.String.format(this.minText, this.formatDate(this.minValue)),
            maxText : Ext.String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
        this.menuEvents('on');
    }
    
    
    
});
/**
 * Display the content of the file located at the given Url depending on its
 * content type
 * 
 * @param url
 *            the url of the file
 * @param title
 *            the title of the window to open
 */
function viewFileContent(url, title) {
  // build first request to get the headers
    Ext.Ajax.request({
        url : url,
        method : 'HEAD',
        scope : this,
        success : function (ret) {            
            try {
                var headerFile = ret.getResponseHeader("Content-Type").split(";")[0].split("/")[0];
                if (headerFile == "text" || ret.getResponseHeader("Content-Type").indexOf("application/json") >= 0) {
                    Ext.Ajax.request({
                        url : url,
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            var windowConfig = {
                                id : "winPreferenceDetailId", 
                                title : title, 
                                iconCls : "version"
                            };
                            var jsObj = sitools.user.component.entete.userProfile.viewTextPanel;
                            var componentCfg = {
                                url : url,
                                text : ret.responseText,
                                formatJson : (ret.getResponseHeader("Content-Type").indexOf("application/json") >= 0)
						    };
                            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
                        }
                    });
                }
                else if (headerFile == "image") {
                    sitools.user.utils.DataviewUtils.showPreview(url, title);

                }
                else {
                    sitools.user.utils.DataviewUtils.downloadFile(url);
                }
            } catch (err) {
                Ext.Msg.alert(i18n.get('label.error'), err);
            }
        },
        failure : function (ret) {
            return null;
        }
    });
}

Ext.namespace('sitools.public.utils');

/**
 * A méthod call when click on dataset Icon. Request the dataset, and open a window depending on type
 * 
 * @static
 * @param {string} url the url to request the dataset
 * @param {string} type the type of the component.
 * @param {} extraCmpConfig an extra config to apply to the component.
 */

/**
 * Add a tooltip on every form field: tooltip could be an object like tooltip : {
 * text : string width : number }, or a simple string
 */
Ext.override(Ext.form.field.Base, {
	tooltip : null, 
	listeners : {
		render: function () {
//			Ext.form.field.Base.superclass.render.apply(this, arguments);
			
			if (!Ext.isEmpty(this.tooltip)) {
				var ttConfig = {};
				if (Ext.isString(this.tooltip)) {
					ttConfig = {
						html : this.tooltip, 
						width : 200, 
						dismissDelay : 5000
					};
				} 
				else if (Ext.isObject(this.tooltip)) {
                    ttConfig = this.tooltip;
                } else {
                    return;
                }
                Ext.apply(ttConfig, {
                    target : this.el
                });
				this.tTip = new Ext.ToolTip(ttConfig);
			}
		}
	}
});

/**
 * Add a tooltip on every boxcomponent : tooltip could be an object like tooltip : {
 * text : string width : number }, or a simple string
 */
Ext.override(Ext.button.Button, {
    setTooltip : function() {
		return;
	}
});

/**
 * Get the folder name to store the cart file name depending on the project name
 * @param projectName the project name
 * @returns {String} the folder name to store the cart file
 */
function getCartFolder (projectName) {
    return "/" + DEFAULT_ORDER_FOLDER + "/cart/" + projectName;
}
