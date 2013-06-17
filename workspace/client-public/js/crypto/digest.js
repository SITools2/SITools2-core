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
function Digest (config) {
	this.url = config.url;
	this.usr = config.usr;
	this.pwd = config.pwd;
	this.realm = config.realm;
	this.algorithm = config.algorithm;
	this.nonce = config.nonce;
	this.method = config.method;
	this.mode = config.mode;
	this.A1 = config.A1;
	
 	this.getDigestAuth = function () {
		this.resetHeaders();
		return this.buildAuthenticationRequest();
	};
	this.getA1 = function () {
		this.resetHeaders();
		return this.digest(this.usr + ':' + this.realm + ':' + this.pwd);
	};

	this.resetHeaders = function () {
		if ( typeof( this.headers) != "undefined" ) {
		  delete this.headers;
		}
		this.headers = {
		  'uri' : this.url,
		  'username' : this.usr,
		  'algorithm' : this.algorithm,
		  'realm' : this.realm, 
		  'nonce' : this.nonce
		};
	}; 

	this.digest = function (s) {
// Fallback to MD5 if requested algorithm is unavilable.
		if (typeof ( window[this.headers.algorithm] ) != 'function') {
		    if (typeof ( window['MD5'] ) != 'function') {
		      	alert('Votre navigateur ne supporte pas l\'authentification HTTP Digest !');
		      	return false;
		    } else {
		      	return MD5(s);
		    }
  		}
  		return window[this.headers.algorithm](s);
	};  

	this.buildResponseHash = function () {
		if (this.headers.salt) {
			auth.secret = auth.secret + ':' + auth.headers.salt;
		    delete auth.headers.salt;
		}
		if (this.headers.migrate) {
			auth.secret = this.digest(auth.secret);
		}
		
		var A1;
		if (Ext.isEmpty(this.A1)){
			A1 = this.getA1();
		}
		else {
			A1 = this.A1;
		}
		//delete this.secret;
		var A2 = this.digest(this.method + ':' + this.headers.uri);
		
		if (this.mode == 'digest') {
			return this.digest(A1 + ":" + this.headers.nonce + ":" + A2);
		}
		//TODO : voir s'il y a d'autres encodages possibles
		return null;
	};  

	this.buildAuthenticationRequest = function () {
	    var request = "Digest";
	    
	    var comma = ' ';
	    for (name in this.headers) {
	      request += comma + name + '="' + this.headers[name] + '"';
	      comma = ',';
	    }
	//    request += ' username="'+ auth.headers.username+ '"';
	    
	    // don't continue further if there is no algorithm yet.
	    if (typeof( this.headers.algorithm ) == 'undefined') {
	      return request;
	    }
	    
	    var r = this.buildResponseHash();
	    
	    if (r) {
	      request += ", response=\"" + r + "\"";
	      return request;
	    }
	
	    return false;
	};
  
};
