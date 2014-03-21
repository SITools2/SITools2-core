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
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/*global Ext, sitools*/

/**
 * A simple Observable class to instanciate module App
 * @class Ext.app.Module
 * @extends Ext.util.Observable
 */
Ext.app.Module = function (config) {
	Ext.apply(this, config);
	Ext.app.Module.superclass.constructor.call(this);
	this.init();
};

Ext.extend(Ext.app.Module, Ext.util.Observable, {
	init : Ext.emptyFn
});
