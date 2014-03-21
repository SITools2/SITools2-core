/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*global Ext, sitools, OpenLayers*/

Ext.ns("sitools.user.data");

sitools.user.data.ProtocolProxy = function (config) {
	sitools.user.data.ProtocolProxy.superclass.constructor.call(this, Ext.apply({
		api : {
			create : Ext.emptyFn, 
			destroy : Ext.emptyFn, 
			read : Ext.emptyFn, 
			update : Ext.emptyFn
		}, 
		protocol: new sitools.user.data.ProtocolHttp({
		    url: config.url,
		    totalProperty : config.totalProperty, 
		    format: new OpenLayers.Format.GeoJSON()
		})
	}));

};

Ext.extend(sitools.user.data.ProtocolProxy, GeoExt.data.ProtocolProxy, {});
	