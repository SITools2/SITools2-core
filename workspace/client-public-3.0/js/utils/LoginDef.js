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

Ext.define("sitools.public.utils.LoginDef",{
    singleton : true
});

//Duration of the session in minutes, cannot be over than 35000 (35000 * 60 * 1000 > 32 bit signed integer max value)
var COOKIE_DURATION = 20;

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
		        Ext.util.Cookies.set('hashCode', Ext.util.Cookies.get('hashCode'), Ext.Date.add(date, Ext.Date.MINUTE, COOKIE_DURATION));
		        Ext.apply(Ext.Ajax.defaultHeaders, {
					Authorization : Ext.util.Cookies.get('hashCode')
		        });
                var expireDate = Ext.Date.add(date, Ext.Date.MINUTE, COOKIE_DURATION);
                Ext.util.Cookies.set('scheme', Ext.util.Cookies.get('scheme'), expireDate);
		    } else {
		        Ext.destroyMembers(Ext.Ajax.defaultHeaders, "Authorization");
		    }
		}


    }
    if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
        var expireDate = Ext.Date.add(date, Ext.Date.MINUTE, COOKIE_DURATION);
        Ext.util.Cookies.set('userLogin', Ext.util.Cookies.get('userLogin'), expireDate);
        
        taskCheckSessionExpired.cancel();
        taskCheckSessionExpired.delay(COOKIE_DURATION * 1000 * 60);
        
        //use localstorage to store sessionsTime out
        localStorage.setItem("userSessionTimeOut", Ext.Date.format(expireDate, SITOOLS_DATE_FORMAT));
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
        var expireDate = Ext.Date.parse(expire, SITOOLS_DATE_FORMAT);

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

var checkCookieDuration = function () {
    return ((COOKIE_DURATION * 60 * 1000) < ((Math.pow(2,31))-1));
}