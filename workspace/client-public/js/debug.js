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
/*global getXPath */
function initDebug() {
	//
}

function getCmpLocator(c) {
	if (c === null || c === undefined || c === '') {
		throw "Locator: given component is undefined";
	}
	var el;
	if (c.el) {
		el = c.el;
		if (el.id) {
			return el.id;
		} else if (el.dom) {
			return el.dom;
		} else {
			return getXPath(el);
		}
	}
	if (c.ui && c.ui.elNode) {
		el = c.ui.elNode;
		if (el.id) {
			return el.id;
		} else if (el.dom) {
			return el.dom;
		} else {
			return getXPath(el);
		}

	}
	if (c.ui && c.ui.node) {
		el = c.ui.node;
		if (el.id) {
			return el.id;
		}
	}
}
