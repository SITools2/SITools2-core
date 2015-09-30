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
// Submit XML
/*global Ext, sitools, ID, i18n, showResponse, alertFailure,window,XMLHttpRequest,ActiveXObject,Base64*/
function AjaxHTTPInit() {
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	}
	if (window.ActiveXObject) {
		var names = [ "Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.3.0", "Msxml2.XMLHTTP", "Microsoft.XMLHTTP" ];
		for (var i in names) {
			try {
				return new ActiveXObject(names[i]);
			} catch (e) {
			}
		}
	}
	window.alert("Votre navigateur ne supporte pas AJAX!");
	return null; // non supporte
}

function AjaxSendRequest(url, param, on_handler) {
	var ajax = AjaxHTTPInit();
	ajax.open("GET", url, true);
	ajax.onreadystatechange = function () {
		on_handler(ajax);
	};
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.send(param);
}

function GetBasicAuth(user, password) {
	var tok = user + ':' + password;
	var hash = Base64.encode(tok);
	return "Basic " + hash;
}

function AjaxBasicConnect(url, usr, pwd, on_handler) {
	var ajax = AjaxHTTPInit();
	ajax.open("GET", url, true, usr, pwd);
	ajax.onreadystatechange = function () {
		on_handler(ajax);
	};
}

// ExtJS
function ExtJSBasicConnect(url, usr, pwd, on_handler) {
	var auth = GetBasicAuth(usr, pwd);
	Ext.Ajax.request({
	    url : url,
	    method : 'GET',
	    headers : {
		    Authorization : auth
	    },
	    callback : on_handler
	});
}

// ExtJS
function ExtJSDigestConnect(url, usr, pwd, on_handler) {
	var auth = GetDigestAuth(url, usr, pwd);
	Ext.Ajax.request({
	    url : url,
	    method : 'GET',
	    headers : {
		    Authorization : auth
	    },
	    callback : on_handler
	});
}
