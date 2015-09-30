/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

sitools.user.data.ProtocolHttp = function (config) {
	sitools.user.data.ProtocolHttp.superclass.constructor.call(this, Ext.apply({
	    url: config.url,
	    totalProperty : config.totalProperty, 
	    format: new sitools.user.data.FormatGeoJson({
	    	totalProperty : config.totalProperty
	    })
	}));

};

Ext.extend(sitools.user.data.ProtocolHttp, OpenLayers.Protocol.HTTP, {
    /**
     * Method: handleResponse
     * Overrides the OpenLayers.Protocol.HTTP method : 
     * As we use a sitools.user.data.FormatGeoJson, the return of parseFeatures will be different : 
     * 
     * initial return of this.parseFeatures : Array of features
     * 
     * sitools.user.data.FormatGeoJson return : {
     * 		features : [], 
     * 		totalResults : {integer} (should be null if no totalProperty defined)
     * }
     * 
     * Called by CRUD specific handlers.
     *
     * Parameters:
     * resp - {<OpenLayers.Protocol.Response>} The response object to pass to
     *     any user callback.
     * options - {Object} The user options passed to the create, read, update,
     *     or delete call.
     */
    handleResponse: function(resp, options) {
        var request = resp.priv;
        if(options.callback) {
            if(request.status >= 200 && request.status < 300) {
                // success
                if(resp.requestType != "delete") {
                    //Here is the main changes : 
                	//resp.features = this.parseFeatures(request);
                	Ext.apply (resp, this.parseFeatures(request));
                }
                resp.code = OpenLayers.Protocol.Response.SUCCESS;
            } else {
                // failure
                resp.code = OpenLayers.Protocol.Response.FAILURE;
            }
            options.callback.call(options.scope, resp);
        }
    }	
});
