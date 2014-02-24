/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, Digest, window*/

// GLOBAL BEHAVIOUR
var DEFAULT_NATIVEJSON = false; // can be set to true if greater than FF 3.5, IE
// 8, Opera 10.5, Webkit-based browsers
var DEFAULT_TIMEOUT = 30000; // server request timeout (msec)
var DEFAULT_TIMEBUF = 10; // time to wait before sending request (msec)
var DEFAULT_NBRETRY = 0;// 3; // nb of request retries (if failure)
var LOCALE = 'en';
var DEFAULT_HELP_WIDTH = 600;
var DEFAULT_HELP_HEIGHT = 400;
var ADMIN_PANEL_HEIGHT = 300;
var ADMIN_PANEL_NB_ELEMENTS = 10;
var SHOW_HELP = true;
var COOKIE_DURATION = 20;
var SITOOLS_DATE_FORMAT = 'Y-m-d\\TH:i:s.u';
var SITOOLS_DEFAULT_IHM_DATE_FORMAT = 'Y-m-d H:i:s.u';
var JAVA_TYPES = [{
	name : "String"
}, {
	name : "Date"
}, {
	name : "Double"
}, {
	name : "Float"
}, {
	name : "Integer"
}, {
	name : "Boolean"
}, {
	name : "BasicDBObject"
}, {
    name : "BasicDBList"
}];
Ext.Ajax.defaultHeaders = {
    "Accept" : "application/json",
    "X-User-Agent" : "Sitools"
};


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
		    }
		}
    }
    if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
        var expireDate = date.add(Date.MINUTE, COOKIE_DURATION);
        Ext.util.Cookies.set('userLogin', Ext.util.Cookies.get('userLogin'), expireDate);
        Ext.util.Cookies.set('userSessionTimeOut', expireDate.format(SITOOLS_DATE_FORMAT), expireDate);
        
        taskCheckSessionExpired.cancel();
        taskCheckSessionExpired.delay(COOKIE_DURATION * 1000 * 60);
    }
};

Ext.Ajax.on('beforerequest', onBeforeRequest, this);

/**
 * HANDLE SESSION TIMEOUT
 */

var checkSessionExpired = function () {
    
    console.log("checkSessionExpired");
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
        var expire = Ext.util.Cookies.get('userSessionTimeOut');
        var date = new Date();
        var expireDate = Date.parseDate(expire, SITOOLS_DATE_FORMAT);
        
        var sessionTimeLeftMs = (expireDate.getTime() - date.getTime()) + 1000;
        taskCheckSessionExpired.delay(sessionTimeLeftMs);
    }
};

var taskCheckSessionExpired = new Ext.util.DelayedTask(checkSessionExpired);

/**
 * Method called if any Ajax Request has an error status.
 * The attribute doNotHandleRequestexception of the options parameter can be used to true in order to do nothing in this method
 * @param {Connection} conn the connection object
 * @param {Object] response The XHR object containing the response data. See The XMLHttpRequest Object for details.
 * @param {Object} options The options config object passed to the request method.
 */
var onRequestException = function (conn, response, options) {
    // if the parameter doNotHandleRequestexception was set in the options, do nothing
    if (!options.doNotHandleRequestexception) {	    
	    // si on a un cookie de session et une erreur 403
		if ((response.status == 403) && !Ext.isEmpty(Ext.util.Cookies.get('hashCode'))) {
			Ext.MessageBox.minWidth = 360;
			Ext.MessageBox.alert(i18n.get('label.session.expired'), response.responseText);
		} else {
			Ext.MessageBox.minWidth = 360;
			Ext.MessageBox.alert(i18n.get('label.error'), response.responseText);
		}
    }

};

Ext.Ajax.on('requestexception', onRequestException, this);

var helpUrl;
Ext.BLANK_IMAGE_URL = '/sitools/cots/extjs/resources/images/default/s.gif';
Ext.USE_NATIVE_JSON = DEFAULT_NATIVEJSON;
Ext.Ajax.timeout = DEFAULT_TIMEOUT;
// Default headers to pass in every request


// Application d'exception sur tous les JsonStores :
// Ext.override (Ext.data.JsonStore, {
// listeners : {
// exception : function (dataProxy, type, action, options, response){
// Ext.Msg.alert (i18n.get('label.warning'), response.responseText);
// }
// }
// });

// GLOBAL PANELS
var treePanel, mainPanel, helpPanel;

var SERVER_OK = 200;

var predicatOperators = {
    operators : [[ 'EQ', '=' ], [ 'GT', '>' ], [ 'GTE', '>=' ], [ 'LT', '<' ], [ 'LTE', '<=' ], [ 'LIKE', 'like' ],
                        [ 'NE', '!=' ]],
                        
    getOperatorValueForServer : function (clientValue){
        var value = null;
        Ext.each(this.operators, function (operator) {
			if (operator[1] == clientValue) {
				value = operator[0];
				return false;
			}
		}, this);
        return value;
    },
    
    getOperatorValueForClient : function(serverValue) {
        var value = null;
        Ext.each(this.operators, function (operator) {
            if (operator[0] == serverValue) {
                value = operator[1];
                return false;
            }
        }, this);
        return value;
    }                   
};

/**
 * Open a basic alert popup that shows the repsonse Message 
 * @param {string} response the server response
 * @param {} opts the options of the Ajax Call
 */
function alertFailure(response, opts) {
	var txt;
    if (response.status == SERVER_OK) {
        var ret = Ext.decode(response.responseText).message;
        txt = i18n.get('msg.error') + ': ' + i18n.get(ret);
    } else {
        txt = i18n.get('warning.serverError') + ': ' + i18n.get(response.statusText);
    }
    Ext.Msg.alert(i18n.get('label.warning'), txt);
}

function showResponse(ret) {
    try {
        var Json = Ext.decode(ret.responseText);
        if (!Json.success) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get(Json.message));
            return false;
        }

        var tmp = new Ext.ux.Notification({
            iconCls : 'x-icon-information',
            title : i18n.get('label.information'),
            html : i18n.get(Json.message),
            autoDestroy : true,
            hideDelay : 1000
        }).show(document);
        return true;
    } catch (err) {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.javascriptError') + " : " + err);
        return false;
    }
}

//Basic formFields Validation
Ext.apply(Ext.form.VTypes, {
    'name': function () {
        var re = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~/]+.*$");
        return function (v) {
            return ! re.test(v);
        };
    }(),
    'nameText' : "Invalid caracters : should not contain . * [ ! \" # $ % & ' ( ) * + , : ; < = > ? @ \ ` { } | ~ / ] + . * $", 
    'image' : function () {
        var re = new RegExp("^[http://]+[/:a-zA-Z0-9-_]+.*(jpg|gif|png|bmp|ico)$");
        return function (v) {
            return re.test(v);
        };
    }(),
    'imageText' : "Invalid image URL : should start with http and finish with a image extension", 
    'attachment' : function () {
        var re1 = new RegExp("^/.*$");
        var re2 = new RegExp("^.*//.*$");
        var re3 = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~]+.*$");
        return function (v) {
            return (re1.test(v) && ! re2.test(v) && !re3.test(v));
        };
    }(),
    'attachmentText' : "invalid Attachment : should not contain ! \" # $ % & ' ( ) * + , : ; < = > ? @ \ ` { } | ~ ] + . * and must begin with /", 
    'nameWithoutSpace': function () {
        var re = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~ ]+.*$");
        return function (v) {
            return ! re.test(v);
        };
    }(),
    'nameWithoutSpaceText' : i18n.get('label.invalidCharacters'), 
    'withoutSpace': function () {
        var re = new RegExp("^.*[ ]+.*$");
        return function (v) {
            return ! re.test(v);
        };
    }(),
    'withoutSpaceText' : i18n.get('label.invalidCharacters'),
    'uri' : function () {
        var re = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
		var regEspace = new RegExp("^.*[ ]+$");
        return function (v) {
			return re.test(v) && !regEspace.test(v);
		};
    }(),
    'uriText' : "The format of the URL is invalid, should start with http, ftp or https and finish without space"
     
     

    
});

Ext.override(Ext.grid.GridPanel, {
    stripeRows : true
});

//override the spinnerField to use validation when spining
Ext.override(Ext.ux.Spinner, {
    spin: function (down, alternate) {
        var v = parseFloat(this.field.getValue());
        var incr = (alternate === true) ? this.alternateIncrementValue : this.incrementValue;
        if (down === true) {
			v -= incr;
        }
        else {
			v += incr;
        }

        v = (isNaN(v)) ? this.defaultValue : v;
        v = this.fixBoundries(v);
        //use setValue instead of setRawValue to use validator
        this.field.setValue(v);
    }
});

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



// 
function onClickOption(urloption) {
// CHROME : OK	
	
//	Ext.Ajax.request({
//			url : urloption + "?media=text/html",
//			method : "OPTIONS",
//			success : function(response) {
//					OpenWindow = window.open("", urloption);
//					OpenWindow.document.write(response.responseText);
//					OpenWindow.document.close();
//			},
//			failure : function(response) { },
//			scope:this
//			});

//  SOUS FIREFOX PB : preferer le tunneling si autorisation.
			
	var urltunnel = urloption + "?method=options&media=text/html";
	var saved = Ext.Ajax.defaultHeaders.Authorization;
	Ext.Ajax.defaultHeaders.Authorization = '';
	var savedScheme = Ext.util.Cookies.get('scheme');
	var savedHashCode = Ext.util.Cookies.get('hashCode');
	Ext.util.Cookies.set('scheme', null);
	Ext.util.Cookies.set('hashCode', null);
	
	Ext.Ajax.un('requestexception', onRequestException, this);

	Ext.Ajax.request({
        url : urltunnel,
        headers : {
            'Authorization' : 'none'
        },
        method : "GET",
        success : function (response) {
            if (response.status == 200) {
                var OpenWindow = window.open(urltunnel);
            }
            // SINON ? ...
            Ext.Ajax.defaultHeaders.Authorization = saved;
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        callback : function () {
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        failure : function (response) {
            Ext.Ajax.defaultHeaders.Authorization = saved;
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            if (response.status == 403) {
                Ext.Ajax.request({
                    url : urloption + "?media=text/html",
                    method : "OPTIONS",
                    success : function (response) {
                        var OpenWindow = window.open("", urloption);
                        OpenWindow.document.write(response.responseText);
                        OpenWindow.document.close();
                    },
                    failure : function (response) {
                    },
                    scope : this
                });
            } // SINON ? ...
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        scope : this
    //the scope in which to execute the callbacks  
    });		

}

function includeJs(url) {
	if (Ext.isEmpty(url)) {
		return;
	}
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
        var head = document.getElementsByTagName('head')[0];
        script = document.createElement('script');
        script.setAttribute('src', url);
        script.setAttribute('type', 'text/javascript');
        head.appendChild(script);
    }
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


Ext.override(Ext.PagingToolbar, {
    initComponent : function () {
        var T = Ext.Toolbar;
        var pagingItems = [this.first = new T.Button({
            tooltip: this.firstText,
            overflowText: this.firstText,
            iconCls: 'x-tbar-page-first',
            disabled: true,
            handler: this.moveFirst,
            scope: this
        }), this.prev = new T.Button({
            tooltip: this.prevText,
            overflowText: this.prevText,
            iconCls: 'x-tbar-page-prev',
            disabled: true,
            handler: this.movePrevious,
            scope: this
        }), '-', this.beforePageText,
        this.inputItem = new Ext.form.NumberField({
            cls: 'x-tbar-page-number',
            allowDecimals: false,
            allowNegative: false,
            enableKeyEvents: true,
            selectOnFocus: true,
            submitValue: false,
            listeners: {
                scope: this,
                keydown: this.onPagingKeyDown,
                blur: this.onPagingBlur
            }
        }), this.afterTextItem = new T.TextItem({
            text: String.format(this.afterPageText, 1)
        }), '-', this.next = new T.Button({
            tooltip: this.nextText,
            overflowText: this.nextText,
            iconCls: 'x-tbar-page-next',
            disabled: true,
            handler: this.moveNext,
            scope: this
        }), this.last = new T.Button({
            tooltip: this.lastText,
            overflowText: this.lastText,
            iconCls: 'x-tbar-page-last',
            disabled: true,
            handler: this.moveLast,
            scope: this
        }), '-'];


        var userItems = this.items || this.buttons || [];
        if (this.prependButtons) {
            this.items = userItems.concat(pagingItems);
        } else {
            this.items = pagingItems.concat(userItems);
        }
        delete this.buttons;
        if (this.displayInfo) {
            this.items.push('->');
            this.items.push(this.displayItem = new T.TextItem({}));
        }
        Ext.PagingToolbar.superclass.initComponent.call(this);
        this.addEvents(
            /**
             * @event change
             * Fires after the active page has been changed.
             * @param {Ext.PagingToolbar} this
             * @param {Object} pageData An object that has these properties:<ul>
             * <li><code>total</code> : Number <div class="sub-desc">The total number of records in the dataset as
             * returned by the server</div></li>
             * <li><code>activePage</code> : Number <div class="sub-desc">The current page number</div></li>
             * <li><code>pages</code> : Number <div class="sub-desc">The total number of pages (calculated from
             * the total number of records in the dataset as returned by the server and the current {@link #pageSize})</div></li>
             * </ul>
             */
            'change',
            /**
             * @event beforechange
             * Fires just before the active page is changed.
             * Return false to prevent the active page from being changed.
             * @param {Ext.PagingToolbar} this
             * @param {Object} params An object hash of the parameters which the PagingToolbar will send when
             * loading the required page. This will contain:<ul>
             * <li><code>start</code> : Number <div class="sub-desc">The starting row number for the next page of records to
             * be retrieved from the server</div></li>
             * <li><code>limit</code> : Number <div class="sub-desc">The number of records to be retrieved from the server</div></li>
             * </ul>
             * <p>(note: the names of the <b>start</b> and <b>limit</b> properties are determined
             * by the store's {@link Ext.data.Store#paramNames paramNames} property.)</p>
             * <p>Parameters may be added as required in the event handler.</p>
             */
            'beforechange'
        );
        this.on('afterlayout', this.onFirstLayout, this, {single: true});
        this.cursor = 0;
        this.bindStore(this.store, true);
    }, 
    onFirstLayout : function () {
	    this.refresh = new Ext.Toolbar.Button({
            tooltip: i18n.get('label.refreshText'),
            overflowText: i18n.get('label.refreshText'),
            iconCls: 'x-tbar-loading',
            handler: this.doRefresh,
            scope: this
        });
        this.insert(10, this.refresh);
        if (this.dsLoaded) {
            this.onLoad.apply(this, this.dsLoaded);
        }
    }
});

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
            minText : String.format(this.minText, this.formatDate(this.minValue)),
            maxText : String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
        this.menuEvents('on');
    }
});

Ext.override(Ext.slider.SingleSlider, {
	listeners : {
		afterrender : function (slider) {
			slider.syncThumb();
		}
	}, 
	doSnap : function (value) {
        if (!(this.increment && value)) {
            return value;
        }
        var newValue = value,
            inc = this.increment,
            m = value % inc;
        if (m !== 0) {
            newValue -= m;
            if (m * 2 >= inc) {
                newValue += inc;
            } else if (m * 2 < -inc) {
                newValue -= inc;
            }
        }
        if (Ext.isString(newValue)) {
			newValue = parseFloat(newValue);
        }
        return newValue.constrain(this.minValue,  this.maxValue);
    }
});
