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
/**
 * shortcut for console.assert(object is not null neither undefined neither
 * empty string)
 */
/*global cerr,ctrace,isFirebugConsoleIsActive,console */
function ann(obj, message) {

    if (obj === true || obj === false) {
    	return; 
    }
    if (obj === undefined) {
        cerr('Object is undefined - ' + message);
        ctrace();
        return;
    }
    if (obj === null) {
        cerr('Object is null - ' + message);
        ctrace();
        return;
    }
    if (obj === "") {
        cerr('String seems empty - ' + message);
        ctrace();
        return;   
    }

    if (obj == NaN) {
        cerr('Object equals NaN - ' + message);
        ctrace();
        return;
    }

}


/**
 * shortcut for console.assert(object is not null neither undefined neither
 * empty string)
 */
function assert(condition, message) {

    if (!condition) {
    	cerr('Condition is not valid : ' + message);
    	ctrace();
    	return;
    }
}

/**
 * Log on the console
 */
function clog(message) {
	if (isFirebugConsoleIsActive()) {
		console.log(message);
	}	
}

/**
 * Display an error on the console
 */
function cerr(message) {
	if (isFirebugConsoleIsActive()) {
		console.trace();
	}	
}

/**
 * Trace the Javascript stack to this point
 */
function ctrace() {
	if (isFirebugConsoleIsActive()) {
		console.trace();
	}
}

/**
 * Trace the Javascript stack to this point
 */
function cdir(obj) {
	if (isFirebugConsoleIsActive()) {
		console.dir(obj);
	}
}

/**
 * Return true if the firebug console is active, false elsewhere
 */
function isFirebugConsoleIsActive() {
	try {
		if (console !== null && console !== undefined)
		{
			return true;
		} else {
			return false;
		}
	}
	catch (e) {
		return false;
	}
}




