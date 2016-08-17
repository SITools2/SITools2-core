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
/*global Ext, sitools, ID, i18n, document, Digest, window*/
Ext.define("sitools.admin.def", {
    singleton: true,
    require: ["sitools.public.utils.LoginDef"],

    init: function () {
        Ext.Ajax.on('requestexception', onRequestException, this);
    },

    initLocalizedVariables: function () {
        //Basic formFields Validation
        Ext.apply(Ext.form.VTypes, {
            'name': function () {
                var re = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~/]+.*$");
                return function (v) {
                    return !re.test(v);
                };
            }(),
            'nameText': "Invalid caracters : should not contain . * [ ! \" # $ % & ' ( ) * + , : ; < = > ? @ \ ` { } | ~ / ] + . * $",
            'image': function () {
                var re = new RegExp("^[http://]+[/:a-zA-Z0-9-_]+.*(jpg|gif|png|bmp|ico)$");
                return function (v) {
                    return re.test(v);
                };
            }(),
            'imageText': "Invalid image URL : should start with http and finish with a image extension",
            'attachment': function () {
                var re1 = new RegExp("^/.*$");
                var re2 = new RegExp("^.*//.*$");
                var re3 = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~]+.*$");
                return function (v) {
                    return (re1.test(v) && !re2.test(v) && !re3.test(v));
                };
            }(),
            'attachmentText': "invalid Attachment : should not contain ! \" # $ % & ' ( ) * + , : ; < = > ? @ \ ` { } | ~ ] + . * and must begin with /",
            'nameWithoutSpace': function () {
                var re = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~ ]+.*$");
                return function (v) {
                    return !re.test(v);
                };
            }(),
            'nameWithoutSpaceText': i18n.get('label.invalidCharacters'),
            'withoutSpace': function () {
                var re = new RegExp("^.*[ ]+.*$");
                return function (v) {
                    return !re.test(v);
                };
            }(),
            'withoutSpaceText': i18n.get('label.invalidCharacters'),
            'uri': function () {
                var re = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
                var regEspace = new RegExp("^.*[ ]+$");
                return function (v) {
                    return re.test(v) && !regEspace.test(v);
                };
            }(),
            'uriText': "The format of the URL is invalid, should start with http, ftp or https and finish without space"
        });
    }
});


// GLOBAL BEHAVIOUR
var DEFAULT_NATIVEJSON = false; // can be set to true if greater than FF 3.5, IE
// 8, Opera 10.5, Webkit-based browsers
var DEFAULT_TIMEOUT = 30000; // server request timeout (msec)
var DEFAULT_TIMEBUF = 10; // time to wait before sending request (msec)
var DEFAULT_NBRETRY = 0;// 3; // nb of request retries (if failure)
var LOCALE = 'en';
var DEFAULT_HELP_WIDTH = 600;
var DEFAULT_HELP_HEIGHT = 400;
var ADMIN_PANEL_HEIGHT = 360;
var ADMIN_PANEL_NB_ELEMENTS = 10;
var SHOW_HELP = true;
var SITOOLS_DATE_FORMAT = 'Y-m-d\\TH:i:s.u';
var SITOOLS_DEFAULT_IHM_DATE_FORMAT = 'Y-m-d H:i:s.u';
var EXT_JS_VERSION = Ext.versions.extjs.version;
var EXT_JS_FOLDER = "extjs4/ext-" + EXT_JS_VERSION;

var JAVA_TYPES = [{
    name: "String"
}, {
    name: "Date"
}, {
    name: "Double"
}, {
    name: "Float"
}, {
    name: "Integer"
}, {
    name: "Boolean"
}, {
    name: "BasicDBObject"
}, {
    name: "BasicDBList"
}];
Ext.Ajax.defaultHeaders = {
    "Accept": "application/json",
    "X-User-Agent": "Sitools"
};

var helpUrl;
Ext.BLANK_IMAGE_URL = '/sitools/client-public/cots/' + EXT_JS_FOLDER + '/resources/themes/images/default/tree/s.gif';
Ext.USE_NATIVE_JSON = DEFAULT_NATIVEJSON;
Ext.Ajax.timeout = DEFAULT_TIMEOUT;
// Default headers to pass in every request

// GLOBAL PANELS
var treePanel, mainPanel, helpPanel;

var SERVER_OK = 200;

var predicatOperators = {
    operators: [['EQ', '='], ['GT', '>'], ['GTE', '>='], ['LT', '<'], ['LTE', '<='], ['LIKE', 'like'],
        ['NE', '!=']],

    getOperatorValueForServer: function (clientValue) {
        var value = null;
        Ext.each(this.operators, function (operator) {
            if (operator[1] == clientValue) {
                value = operator[0];
                return false;
            }
        }, this);
        return value;
    },

    getOperatorValueForClient: function (serverValue) {
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
        popupMessage("", i18n.get(Json.message), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/msgBox/16/icon-info.png');

        return true;
    } catch (err) {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.javascriptError') + " : " + err);
        return false;
    }
}

Ext.override(Ext.grid.GridPanel, {
    stripeRows: true
});

/**
 * Add a tooltip on every form field: tooltip could be an object like tooltip : {
 * text : string width : number }, or a simple string
 */
//TODO search to resolve bug tooltip on fields
Ext.override(Ext.form.field.Base, {
    tooltip: null,
    listeners: {
        afterrender: function () {
            if (!Ext.isEmpty(this.tooltip)) {
                var ttConfig = {};
                if (Ext.isString(this.tooltip)) {
                    ttConfig = {
                        html: this.tooltip,
                        width: 200,
                        dismissDelay: 5000
                    };
                }
                else if (Ext.isObject(this.tooltip)) {
                    ttConfig = this.tooltip;
                } else {
                    return;
                }
                Ext.apply(ttConfig, {
                    target: this.el
                });
                this.tTip = new Ext.ToolTip(ttConfig);
            }
//			this.callParent(arguments);
        }
    }
});

//
function onClickOption(urloption) {
// CHROME : OK	

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
        url: urltunnel,
        headers: {
            'Authorization': 'none'
        },
        method: "GET",
        //Do not display error page
        doNotHandleRequestexception : true,
        success: function (response) {
            if (response.status == 200) {
                var OpenWindow = window.open(urltunnel);
            }
            // SINON ? ...
            Ext.Ajax.defaultHeaders.Authorization = saved;
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        callback: function () {
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        failure: function (response) {
            Ext.Ajax.defaultHeaders.Authorization = saved;
            Ext.util.Cookies.set('scheme', savedScheme);
            Ext.util.Cookies.set('hashCode', savedHashCode);
            if (response.status == 403) {
                Ext.Ajax.request({
                    url: urloption + "?media=text/html",
                    method: "OPTIONS",
                    success: function (response) {
                        var OpenWindow = window.open("", urloption);
                        OpenWindow.document.write(response.responseText);
                        OpenWindow.document.close();
                    },
                    failure: function (response) {
                    },
                    scope: this
                });
            } // SINON ? ...
            Ext.Ajax.on('requestexception', onRequestException, this);
        },
        scope: this
        //the scope in which to execute the callbacks
    });

}


Ext.override(Ext.menu.DatePicker, {
    initComponent: function () {
        this.on('beforeshow', this.onBeforeShow, this);
        if (this.strict == (Ext.isIE7 && Ext.isStrict)) {
            this.on('show', this.onShow, this, {single: true, delay: 20});
        }
        this.callParent();
    }
});

//DA Fix a bug on ExtJs 4.2.1 on grouping feature selection
Ext.define('App.overrides.view.Table', {
    override: 'Ext.view.Table',
    getRecord: function (node) {
        var me = this;
        if (me.dataSource.buffered) {
            node = this.getNode(node);
            if (node) {
                var recordIndex = node.getAttribute('data-recordIndex');
                if (recordIndex) {
                    recordIndex = parseInt(recordIndex, 10);
                    if (recordIndex > -1) {
                        // The index is the index in the original Store, not in
                        // a GroupStore
                        // The Grouping Feature increments the index to skip
                        // over unrendered records in collapsed groups
                        return this.store.data.getAt(recordIndex);
                    }
                }
                return this.dataSource.data.get(node.getAttribute('data-recordId'));
            }
        } else {
            node = this.getNode(node);
            if (node) {
                return this.dataSource.data.get(node.getAttribute('data-recordId'));
            }
        }
    },
    indexInStore: function (node) {
        var me = this;
        if (me.dataSource.buffered) {
            node = this.getNode(node, true);
            if (!node && node !== 0) {
                return -1;
            }
            var recordIndex = node.getAttribute('data-recordIndex');
            if (recordIndex) {
                return parseInt(recordIndex, 10);
            }
            return this.dataSource.indexOf(this.getRecord(node));
        } else {
            node = this.getNode(node, true);
            if (!node && node !== 0) {
                return -1;
            }
            return this.dataSource.indexOf(this.getRecord(node));
        }
    }
});
