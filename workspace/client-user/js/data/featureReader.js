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

sitools.user.data.featureReader = Ext.extend(GeoExt.data.FeatureReader, {
    totalRecords : null, 
    
    /**
     * Overrides parent method to call readRecords with entire response (containing the totalRecords)
     * @returns
     */
    read : function (response) {
		return this.readRecords(response);
	}, 
    /** api: method[readRecords]
     *  :param features: ``Array(OpenLayers.Feature.Vector)`` List of
     *      features for creating records
     *  :return: ``Object``  An object with ``records`` and ``totalRecords``
     *      properties.
     *  
     *  Create a data block containing :class:`GeoExt.data.FeatureRecord`
     *  objects from an array of features.
     */
    readRecords : function (response) {
        var result = sitools.user.data.featureReader.superclass.readRecords.call(this, response.features);

        
        return Ext.apply(result, {
            totalRecords : response.totalResults
        });
    }
});
