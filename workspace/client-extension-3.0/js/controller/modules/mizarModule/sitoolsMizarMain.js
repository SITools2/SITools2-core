/******************************************************************************* 
* Copyright 2012, 2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES 
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
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
* 
* You should have received a copy of the GNU General Public License 
* along with SITools2. If not, see <http://www.gnu.org/licenses/>. 
******************************************************************************/

/**
 * Configuration for require.js
 */
require.config({
	baseUrl : '/sitools/client-extension/resources/libs/mizar/src/mizar/js',
	paths: {
		"jquery": "../externals/jquery-1.11.1.min",
		"jquery.ui": "../externals/jquery-ui-1.11.0.min",
		"underscore-min": "../externals/underscore-1.6.0.min",
		"jquery.nicescroll.min": "../externals/jquery.nicescroll-3.5.4.min",
		"fits": "../externals/fits",
		"samp": "../externals/samp",
		"gzip": "../externals/gzip",
		"crc32": "../externals/crc32",
		"deflate-js": "../externals/deflate",
		"inflate-js": "../externals/inflate",
		"wcs": "../externals/wcs",
		"jquery.ui.timepicker": "../externals/jquery.ui.timepicker",
		"gw": "../externals/GlobWeb/src"
	},
	shim: {
		'jquery': {
			deps: [],
			exports: 'jQuery'
		},
		'jquery.ui': {
			deps: ['jquery'],
			exports: 'jQuery'
		},
		'jquery.ui.timepicker': {
			deps: ['jquery.ui'],
			exports: 'jQuery'
		},
		'underscore-min': {
			deps: ['jquery'],
			exports: '_'
		},
		'jquery.nicescroll.min': {
			deps: ['jquery'],
			exports: ''
		}
	},
	waitSeconds: 0
});

/**
 * Mizar widget main
 */
require(["MizarWidget", "PickingManager", "Utils"], function(mizarWidget, pickingManager, utils) {
	MizarWidget = mizarWidget;
	PickingManager = pickingManager;
	Utils = utils;
});
