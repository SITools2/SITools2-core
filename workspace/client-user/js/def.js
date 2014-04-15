/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
var URL_CGU = "/sitools/res/licences/cgu.html";
var COOKIE_DURATION = 20;
var MULTIDS_TIME_DELAY = 2000;
var SITOOLS_DEFAULT_PROJECT_IMAGE_URL = "/sitools/res/images/sitools2_logo.png";
/**
 * The nearLimit is a
     * parameter for the predictive fetch algorithm within the view. If your
     * bufferSize is small, set this to a value around a third or a quarter of
     * the store's bufferSize (e.g. a value of 25 for a bufferSize of 100;
 */
var DEFAULT_NEAR_LIMIT_SIZE = 100;

Ext.BLANK_IMAGE_URL = '/sitools/cots/extjs/resources/images/default/s.gif';
Ext.USE_NATIVE_JSON  = DEFAULT_NATIVEJSON;
Ext.Ajax.timeout = DEFAULT_TIMEOUT;
var onBeforeRequest = function (conn, options) {
    var date = new Date();
    if (!Ext.isEmpty(Ext.util.Cookies.get('scheme'))) {
		if (Ext.util.Cookies.get('scheme') == "HTTP_Digest") {
			var tmp = null;
			var method = "GET";
			if (!Ext.isEmpty(options.method)) {
				method = options.method;
			}
			var url = options.url;
            if (Ext.isEmpty(options.url) && !Ext.isEmpty(options.scope)) {
                if (!Ext.isEmpty(options.scope.url)) {
                    url = options.scope.url;
                }
            }
			
			var A1 = Ext.util.Cookies.get("A1");
			var auth = new Digest({
				usr : Ext.util.Cookies.get("userLogin"),
				algorithm : Ext.util.Cookies.get("algorithm"),
				realm : Ext.util.Cookies.get("realm"),
				url : url,
				nonce : Ext.util.Cookies.get('nonce'), 
				method : method, 
				mode : "digest", 
				A1 : A1
			});
			Ext.apply(Ext.Ajax.defaultHeaders, {
				Authorization : auth.getDigestAuth()
		    });

		}
		else {
		    if (!Ext.isEmpty(Ext.util.Cookies.get('hashCode'))) {
		        Ext.util.Cookies.set('hashCode', Ext.util.Cookies.get('hashCode'), date.add(Date.MINUTE, COOKIE_DURATION));
		        Ext.apply(Ext.Ajax.defaultHeaders, {
					Authorization : Ext.util.Cookies.get('hashCode')
		        });
		    } else {
		        Ext.destroyMembers(Ext.Ajax.defaultHeaders, "Authorization");
		    }
		}
    }
    if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
        var expireDate = date.add(Date.MINUTE, COOKIE_DURATION);
        Ext.util.Cookies.set('userLogin', Ext.util.Cookies.get('userLogin'), expireDate);
        
        taskCheckSessionExpired.cancel();
        taskCheckSessionExpired.delay(COOKIE_DURATION * 1000 * 60);
        
        //use localstorage to store sessionsTime out
        localStorage.setItem("userSessionTimeOut", expireDate.format(SITOOLS_DATE_FORMAT));
        Ext.EventManager.un(window, "storage");
        Ext.EventManager.on(window, "storage", function() {
            if (Ext.isEmpty(localStorage.getItem("userSessionTimeOut"))) {
                checkSessionExpired();                
            }
        });
    }
};

Ext.Ajax.on('beforerequest', onBeforeRequest, this);

/**
 * HANDLE SESSION TIMEOUT
 */

var checkSessionExpired = function () {
    
    taskCheckSessionExpired.cancel();
    
    if (Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
     // Notify user its session timed out.
        Ext.Msg.alert(
                i18n.get('title.session.expired'),
                i18n.get("label.session.expired"),
                function (btn, text) {
                    window.location.reload();
                }
        );
    } else {
        //extend the timer for the remaining session time
        var expire = localStorage.getItem("userSessionTimeOut");
        var date = new Date();
        var expireDate = Date.parseDate(expire, SITOOLS_DATE_FORMAT);
        
        var sessionTimeLeftMs = (expireDate.getTime() - date.getTime()) + 1000;
        taskCheckSessionExpired.delay(sessionTimeLeftMs);
    }
};

var taskCheckSessionExpired = new Ext.util.DelayedTask(checkSessionExpired);

var DEFAULT_LOCALE = "en";
var SITOOLS_DATE_FORMAT = 'Y-m-d\\TH:i:s.u';
var SITOOLS_DEFAULT_IHM_DATE_FORMAT = 'Y-m-d H:i:s.u';

var locale = {
    locale : DEFAULT_LOCALE,
    isInit : false,
    getLocale : function () {
        if (!this.isInit) {
            if (Ext.isEmpty(Ext.util.Cookies.get('language'))) {
                var navigator = window.navigator;
                this.locale = navigator.language || navigator.browserLanguage || navigator.userLanguage;
            }
            else {
                this.locale = Ext.util.Cookies.get('language');
            }
            this.isInit = true;
        }
        return this.locale;                
        
    },
    setLocale : function (locale) {
        this.locale = locale;
    },
    restoreDefault : function () {
        this.setLocale(DEFAULT_LOCALE);
    }
};

var onRequestFeedException = function (proxy, type, action, options, response, args) {
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
	    if (!column.isSelectionModel) {
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
                // columnAliasDetail : column.columnAliasDetail,
                columnRenderer : column.columnRenderer,
                // datasetDetailId : column.datasetDetailId,
                specificColumnType : column.specificColumnType,
                javaSqlColumnType : column.javaSqlColumnType,
                format : column.format
            //			image : column.image,
            //			datasetDetailUrl : column.datasetDetailUrl

            });
        }
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
                format : column.format
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
Ext.override(Ext.Window, {
    initEvents : function () {
	    Ext.Window.superclass.initEvents.call(this);
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

Ext.override(Ext.grid.GridPanel, {
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

//Ext.define('Ext.PagingToolbar', {
//    extend : 'Ext.PagingToolbar',
//    alias : 'widget.paging',
//    
//    initComponent : function () {
//        var T = Ext.Toolbar;
//        var pagingItems = [this.first = new T.Button({
//            tooltip: this.firstText,
//            overflowText: this.firstText,
//            iconCls: 'x-tbar-page-first',
//            disabled: true,
//            handler: this.moveFirst,
//            scope: this
//        }), this.prev = new T.Button({
//            tooltip: this.prevText,
//            overflowText: this.prevText,
//            iconCls: 'x-tbar-page-prev',
//            disabled: true,
//            handler: this.movePrevious,
//            scope: this
//        }), '-', this.beforePageText,
//        this.inputItem = new Ext.form.NumberField({
//            cls: 'x-tbar-page-number',
//            allowDecimals: false,
//            allowNegative: false,
//            enableKeyEvents: true,
//            selectOnFocus: true,
//            submitValue: false,
//            listeners: {
//                scope: this,
//                keydown: this.onPagingKeyDown,
//                blur: this.onPagingBlur
//            }
//        }), this.afterTextItem = new T.TextItem({
//            text: Ext.String.format(this.afterPageText, 1)
//        }), '-', this.next = new T.Button({
//            tooltip: this.nextText,
//            overflowText: this.nextText,
//            iconCls: 'x-tbar-page-next',
//            disabled: true,
//            handler: this.moveNext,
//            scope: this
//        }), this.last = new T.Button({
//            tooltip: this.lastText,
//            overflowText: this.lastText,
//            iconCls: 'x-tbar-page-last',
//            disabled: true,
//            handler: this.moveLast,
//            scope: this
//        }), '-'];
//
//
//        var userItems = this.items || this.buttons || [];
//        if (this.prependButtons) {
//            this.items = userItems.concat(pagingItems);
//        } else {
//            this.items = pagingItems.concat(userItems);
//        }
//        delete this.buttons;
//        if (this.displayInfo) {
//            this.items.push('->');
//            this.items.push(this.displayItem = new T.TextItem({}));
//        }
//        Ext.PagingToolbar.superclass.initComponent.call(this);
//        this.addEvents(
//            /**
//             * @event change
//             * Fires after the active page has been changed.
//             * @param {Ext.PagingToolbar} this
//             * @param {Object} pageData An object that has these properties:<ul>
//             * <li><code>total</code> : Number <div class="sub-desc">The total number of records in the dataset as
//             * returned by the server</div></li>
//             * <li><code>activePage</code> : Number <div class="sub-desc">The current page number</div></li>
//             * <li><code>pages</code> : Number <div class="sub-desc">The total number of pages (calculated from
//             * the total number of records in the dataset as returned by the server and the current {@link #pageSize})</div></li>
//             * </ul>
//             */
//            'change',
//            /**
//             * @event beforechange
//             * Fires just before the active page is changed.
//             * Return false to prevent the active page from being changed.
//             * @param {Ext.PagingToolbar} this
//             * @param {Object} params An object hash of the parameters which the PagingToolbar will send when
//             * loading the required page. This will contain:<ul>
//             * <li><code>start</code> : Number <div class="sub-desc">The starting row number for the next page of records to
//             * be retrieved from the server</div></li>
//             * <li><code>limit</code> : Number <div class="sub-desc">The number of records to be retrieved from the server</div></li>
//             * </ul>
//             * <p>(note: the names of the <b>start</b> and <b>limit</b> properties are determined
//             * by the store's {@link Ext.data.Store#paramNames paramNames} property.)</p>
//             * <p>Parameters may be added as required in the event handler.</p>
//             */
//            'beforechange'
//        );
//        this.on('afterlayout', this.onFirstLayout, this, {single: true});
//        this.cursor = 0;
//        this.bindStore(this.store, true);
//    }, 
//    onFirstLayout : function () {
//        this.refresh = new Ext.Toolbar.Button({
//            tooltip: i18n.get('label.refreshText'),
//            overflowText: i18n.get('label.refreshText'),
//            iconCls: 'x-tbar-loading',
//            handler: this.doRefresh,
//            scope: this
//        });
//        this.insert(10, this.refresh);
//        if (this.dsLoaded) {
//            this.onLoad.apply(this, this.dsLoaded);
//        }
//    }
//});






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
           
            var renderer = sitools.user.component.dataviews.dataviewUtils.getRendererLiveGrid(item, dataviewConfig, dataviewId);
            var hidden;
            if (Ext.isEmpty(item.visible)) {
                hidden = item.hidden;
            } else {
                hidden = !item.visible;
            }
            if (Ext.isEmpty(item.columnRenderer) ||  ColumnRendererEnum.NO_CLIENT_ACCESS != item.columnRenderer.behavior) {
	            columns.push(new Ext.grid.Column({
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
	                format : item.format
	            }));
            }
            
        }, this);
    }

    var cm = new Ext.grid.ColumnModel({
        columns : columns
    });
    return cm;
}


//Date.formatFunctions['sitoolsGrid Y-m-d H:i:s'] = function () {
//	if (this.getHours() === 0 && this.getMinutes() === 0 && this.getSeconds() === 0) {
//		return this.format(BDD_DATE_FORMAT);
//	}
//	else {
//		return this.format(BDD_DATE_FORMAT_WITH_TIME);
//	}
//};


Ext.override(Ext.menu.DateMenu, {
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
        Ext.menu.DateMenu.superclass.initComponent.call(this);
        
        this.relayEvents(this.picker, ['select']);
        this.on('show', this.picker.focus, this.picker);
        this.on('select', this.menuHide, this);
        if (this.handler) {
            this.on('select', this.handler, this.scope || this);
        }
    }
});

Ext.override(Ext.form.DateField,  {
    
    showTime : false,
    
    onTriggerClick : function () {
        if (this.disabled) {
            return;
        }
        if (Ext.isEmpty(this.menu)) {
            this.menu = new Ext.menu.DateMenu({
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
                    sitools.user.component.dataviews.dataviewUtils.showPreview(url, title);

                }
                else {
                    sitools.user.component.dataviews.dataviewUtils.downloadFile(url);
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

Ext.override(Ext.data.XmlReader, {
    buildExtractors : function () {
        if (this.ef) {
            return;
        }
        var s       = this.meta,
            Record  = this.recordType,
            f       = Record.prototype.fields,
            fi      = f.items,
            fl      = f.length;

        if (s.totalProperty) {
            this.getTotal = this.createAccessor(s.totalProperty);
        }
        if (s.successProperty) {
            this.getSuccess = this.createAccessor(s.successProperty);
        }
        if (s.messageProperty) {
            this.getMessage = this.createAccessor(s.messageProperty);
        }
        this.getRoot = function (res) {
            return (!Ext.isEmpty(res[this.meta.record])) ? res[this.meta.record] : res[this.meta.root];
        };
        if (s.idPath || s.idProperty) {
            var g = this.createAccessor(s.idPath || s.idProperty);
            this.getId = function (rec) {
                var id = g(rec) || rec.id;
                return (id === undefined || id === '') ? null : id;
            };
        } else {
            this.getId = function () {
                return null;
            };
        }
        var ef = [];
        for (var i = 0; i < fl; i++) {
            f = fi[i];
            var map = (f.mapping !== undefined && f.mapping !== null) ? f.mapping : f.name;
            if (f.createAccessor !== undefined && f.createAccessor !== null) {
				ef.push(f.createAccessor);
			}
			else {
				ef.push(this.createAccessor(map));
			}
        }
        this.ef = ef;
    }
});

Ext.override(Ext.layout.BorderLayout.Region, {
    getCollapsedEl : function () {
        if (!this.collapsedEl) {
            if (!this.toolTemplate) {
                var tt = new Ext.Template(
                     '<span class="x-panel-collapsed-text">{title}</span>', 
					 '<div class="x-tool x-tool-{id}">&#160;</div>'
                );
				
                tt.disableFormats = true;
                tt.compile();
                Ext.layout.BorderLayout.Region.prototype.toolTemplate = tt;
            }
            this.collapsedEl = this.targetEl.createChild({
                cls: "x-layout-collapsed x-layout-collapsed-" + this.position,
                id: this.panel.id + '-xcollapsed'
            });
			
            this.collapsedEl.enableDisplayMode('block');

            if (this.collapseMode == 'mini') {
                this.collapsedEl.addClass('x-layout-cmini-' + this.position);
                this.miniCollapsedEl = this.collapsedEl.createChild({
					cls : "x-layout-mini x-layout-mini-" + this.position, 
					html : "&#160;"
                });
                this.miniCollapsedEl.addClassOnOver('x-layout-mini-over');
                this.collapsedEl.addClassOnOver("x-layout-collapsed-over");
                this.collapsedEl.on('click', this.onExpandClick, this, {stopEvent : true});
            }
            else {
                if (this.collapsible !== false && !this.hideCollapseTool) {
                    var t = this.expandToolEl = this.toolTemplate.append(
                        this.collapsedEl.dom,
                        {
							id: 'expand-' + this.position, 
							title : this.panel.collapsedTitle
						}, true);
                    t.addClassOnOver('x-tool-expand-' + this.position + '-over');
                    t.on('click', this.onExpandClick, this, {
						stopEvent: true
                    });
                }
                if (this.floatable !== false || this.titleCollapse) {
					this.collapsedEl.addClassOnOver("x-layout-collapsed-over");
					this.collapsedEl.on("click", this[this.floatable ? 'collapseClick' : 'onExpandClick'], this);
                }
            }
        }
        return this.collapsedEl;
    }

});

Ext.ns("sitools.user");

/**
 * A méthod call when click on dataset Icon. Request the dataset, and open a window depending on type
 * 
 * @static
 * @param {string} url the url to request the dataset
 * @param {string} type the type of the component.
 * @param {} extraCmpConfig an extra config to apply to the component.
 */
sitools.user.clickDatasetIcone = function (url, type, extraCmpConfig) {
	Ext.Ajax.request({
		method : "GET", 
		url : url, 
		success : function (ret) {
            var Json = Ext.decode(ret.responseText);
            if (showResponse(ret)) {
                var dataset = Json.dataset;
	            var componentCfg, javascriptObject;
	            var windowConfig = {
	                datasetName : dataset.name, 
	                type : type, 
	                saveToolbar : true, 
	                toolbarItems : []
	            };
                switch (type) {
				case "desc" : 
					Ext.apply(windowConfig, {
						title : i18n.get('label.description') + " : " + dataset.name, 
						id : "desc" + dataset.id, 
						saveToolbar : false, 
						iconCls : "version"
					});
					
					componentCfg = {
						autoScroll : true,
						html : dataset.descriptionHTML
					};
					Ext.applyIf(componentCfg, extraCmpConfig);
					javascriptObject = Ext.Panel;
					SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
					
					break;
				case "data" : 
                    javascriptObject = eval(SitoolsDesk.navProfile.getDatasetOpenMode(dataset));
                
	                Ext.apply(windowConfig, {
	                    winWidth : 900, 
	                    winHeight : 400,
                        title : i18n.get('label.dataTitle') + " : " + dataset.name, 
                        id : type + dataset.id, 
                        iconCls : "dataviews"
	                });
                    
	                componentCfg = {
	                    dataUrl : dataset.sitoolsAttachementForUsers,
	                    datasetId : dataset.Id,
	                    datasetCm : dataset.columnModel, 
	                    datasetName : dataset.name,
	                    dictionaryMappings : dataset.dictionaryMappings,
	                    datasetViewConfig : dataset.datasetViewConfig, 
	                    preferencesPath : "/" + dataset.datasetName, 
	                    preferencesFileName : "datasetOverview", 
	                    sitoolsAttachementForUsers : dataset.sitoolsAttachementForUsers
	                };
                
                
	                Ext.applyIf(componentCfg, extraCmpConfig);
					SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

					break;
				case "forms" : 
		            var menuForms = new Ext.menu.Menu();
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/forms", 
						success : function (ret) {
							try {
								var Json = Ext.decode(ret.responseText);
								if (! Json.success) {
									throw Json.message;
								}
								if (Json.total === 0) {
									throw i18n.get('label.noForms');
								}
				                javascriptObject = sitools.user.component.forms.mainContainer;
								if (Json.total == 1) {
						            var form = Json.data[0];
						            Ext.apply(windowConfig, {
						                title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name, 
						                iconCls : "forms"
						            });
						            
						
					                Ext.apply(windowConfig, {
					                    id : type + dataset.id + form.id
					                });
					                componentCfg = {
					                    dataUrl : dataset.sitoolsAttachementForUsers,
					                    dataset : dataset, 
					                    formId : form.id,
					                    formName : form.name,
					                    formParameters : form.parameters,
					                    formZones : form.zones,
					                    formWidth : form.width,
					                    formHeight : form.height, 
					                    formCss : form.css, 
				                        preferencesPath : "/" + dataset.name + "/forms", 
				                        preferencesFileName : form.name
					                };
					                Ext.applyIf(componentCfg, extraCmpConfig);
									SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

				                }
								else {
									
									var handler = null;
									Ext.each(Json.data, function (form) {
										handler = function (form, dataset) {
											Ext.apply(windowConfig, {
												title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name, 
												iconCls : "forms"
								            });
								
							                Ext.apply(windowConfig, {
							                    id : type + dataset.id + form.id
							                });
							                componentCfg = {
							                    dataUrl : dataset.sitoolsAttachementForUsers,
							                    formId : form.id,
							                    formName : form.name,
							                    formParameters : form.parameters,
							                    formWidth : form.width,
							                    formHeight : form.height, 
							                    formCss : form.css, 
							                    dataset : dataset
							                };
							                Ext.applyIf(componentCfg, extraCmpConfig);
											SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
										};
										menuForms.addItem({
											text : form.name, 
											handler : function () {
												handler(form, dataset);
											}, 
											icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_forms.png"
										});
						                
									}, this);
									menuForms.showAt(Ext.EventObject.xy);
								}
					            
				
								
							}
							catch (err) {
								var tmp = new Ext.ux.Notification({
						            iconCls : 'x-icon-information',
						            title : i18n.get('label.information'),
						            html : i18n.get(err),
						            autoDestroy : true,
						            hideDelay : 1000
						        }).show(document);
							}
						}
		            });

					break;
				case "feeds" : 
		            var menuFeeds = new Ext.menu.Menu();
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/feeds", 
						success : function (ret) {
							try {
								var Json = Ext.decode(ret.responseText);
								if (! Json.success) {
									throw Json.message;
								}
								if (Json.total === 0) {
									throw i18n.get('label.noFeeds');
								}
				                javascriptObject = sitools.widget.FeedGridFlux;
								if (Json.total == 1) {
						            var feed = Json.data[0];
						            Ext.apply(windowConfig, {
						                title : i18n.get('label.feeds') + " : (" + dataset.name + ") " + feed.title, 
						                id : type + dataset.id + feed.id, 
						                iconCls : "feedsModule"
						            });
						
					                componentCfg = {
					                    datasetId : dataset.id,
					                    urlFeed : dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name,
					                    feedType : feed.feedType, 
					                    datasetName : dataset.name,
					                    feedSource : feed.feedSource,
					                    autoLoad : true
					                };
						            Ext.applyIf(componentCfg, extraCmpConfig);
									SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

				                }
								else {
									var handler = null;
									Ext.each(Json.data, function (feed) {
										handler = function (feed, dataset) {
											Ext.apply(windowConfig, {
												title : i18n.get('label.feeds') + " : (" + dataset.name + ") " + feed.title, 
												id : type + dataset.id + feed.id, 
												iconCls : "feedsModule"
								            });
								
							                
							                componentCfg = {
							                    datasetId : dataset.id,
							                    urlFeed : dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name,
							                    feedType : feed.feedType, 
							                    datasetName : dataset.name,
							                    feedSource : feed.feedSource,
							                    autoLoad : true
							                };
							                Ext.applyIf(componentCfg, extraCmpConfig);
											SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
										};
										menuFeeds.addItem({
											text : feed.name, 
											handler : function () {
												handler(feed, dataset);
											}, 
											icon : loadUrl.get('APP_URL') + "/common/res/images/icons/rss.png"
										});
						                
									}, this);
									menuFeeds.showAt(Ext.EventObject.xy);
								}
					            
				
								
							}
							catch (err) {
								var tmp = new Ext.ux.Notification({
						            iconCls : 'x-icon-information',
						            title : i18n.get('label.information'),
						            html : i18n.get(err),
						            autoDestroy : true,
						            hideDelay : 1000
						        }).show(document);
							}
						}
		            });

					break;
				case "defi" : 
		            Ext.apply(windowConfig, {
		                title : i18n.get('label.definitionTitle') + " : " + dataset.name, 
		                id : type + dataset.id, 
		                iconCls : "semantic"
		            });
		
	                javascriptObject = sitools.user.component.columnsDefinition;
	                
	                componentCfg = {
	                    datasetId : dataset.id,
	                    datasetCm : dataset.columnModel, 
	                    datasetName : dataset.name,
                        dictionaryMappings : dataset.dictionaryMappings, 
                        preferencesPath : "/" + dataset.name, 
                        preferencesFileName : "semantic"
	                };
	                Ext.applyIf(componentCfg, extraCmpConfig);
					SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

					break;
				case "openSearch" : 
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/opensearch.xml", 
						success : function (ret) {
                            var xml = ret.responseXML;
                            var dq = Ext.DomQuery;
                            // check if there is a success node
                            // in the xml
                            var success = dq.selectNode('OpenSearchDescription ', xml);
							if (!success) {
								var tmp = new Ext.ux.Notification({
						            iconCls : 'x-icon-information',
						            title : i18n.get('label.information'),
						            html : i18n.get("label.noOpenSearch"),
						            autoDestroy : true,
						            hideDelay : 1000
						        }).show(document);
								return;
							}
							
							Ext.apply(windowConfig, {
				                title : i18n.get('label.opensearch') + " : " + dataset.name, 
				                id : type + dataset.id, 
				                iconCls : "openSearch"
				            });
				
			                javascriptObject = sitools.user.component.datasetOpensearch;
			                
			                componentCfg = {
			                    datasetId : dataset.id,
			                    dataUrl : dataset.sitoolsAttachementForUsers, 
			                    datasetName : dataset.name, 
		                        preferencesPath : "/" + dataset.name, 
		                        preferencesFileName : "openSearch"
			                };
			                Ext.applyIf(componentCfg, extraCmpConfig);
							SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
                            
                        }
		            });

					break;
				}
            }
		}, 
		failure : alertFailure
	});
};

/**
 * Add a tooltip on every form field: tooltip could be an object like tooltip : {
 * text : string width : number }, or a simple string
 */
Ext.override(Ext.form.Field, {
	tooltip : null, 
	listeners : {
		render: function () {
			Ext.form.Field.superclass.render.apply(this, arguments);
			
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
Ext.override(Ext.BoxComponent, {
    tooltip : null, 
    listeners : {
        render: function () {
            Ext.BoxComponent.superclass.render.apply(this, arguments);
            
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
Ext.override(Ext.Button, {
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

Ext.override(Ext.Window, {
    /**
     * Fit a window to its container (desktop)
     * Resizing and repositionning
     */
    fitToDesktop : function () {
        //resize windows to fit desktop
        var vs = this.container.getViewSize(false);
        var winSize = this.getSize();
        var winPos = this.getPosition();
        
        var outputWinSize = winSize;
        var outputWinPos = winPos;
        
        
        if(winSize.width > vs.width) {
            outputWinSize.width = vs.width - 5;
        }

        if(winSize.height > vs.height) {
            outputWinSize.height = vs.height - 5;
        }
        this.setSize(outputWinSize.width, outputWinSize.height);
        
        
        if(winPos[0] + outputWinSize.width > vs.width) {
            outputWinPos.x = 0;
        }

        if(winPos[1] + outputWinSize.height > vs.height) {
            outputWinPos.y = 0;
        }
        this.setPosition(outputWinPos.x, outputWinPos.y);
    }
});
