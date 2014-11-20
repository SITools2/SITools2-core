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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.extension.modules');
/**
 * FitsModule Module
 * 
 * @class sitools.extension.modules.FitsViewer
 * @extends Ext.Panel
 */
Ext.define('sitools.extension.modules.FitsViewer', {
    extend : 'sitools.user.core.Module',
    
    pluginName : 'fitsViewer',
    
    //requires : [],
    

    js : [ '/sitools/client-extension/resources/libs/fitsViewer/fits.js',
           '/sitools/client-extension/resources/libs/fitsViewer/binaryajax.js',
           '/sitools/client-extension/resources/libs/fitsViewer/excanvasCompiled.js',
           '/sitools/client-extension/resources/libs/fitsViewer/FitsLoader.js',
           '/sitools/client-extension/resources/libs/fitsViewer/flotr2Min.js',
           '/sitools/client-extension/resources/libs/fitsViewer/astroFits.js',
           '/sitools/client-extension/resources/libs/fitsViewer/Histogram.js',
           '/sitools/client-extension/resources/libs/fitsViewer/vec3.js',
           '/sitools/client-extension/resources/libs/fitsViewer/wcs.js',
           '/sitools/client-extension/resources/libs/fitsViewer/ColorMap.js'
    ],
    
    css : ['/sitools/client-extension/resources/css/fitsViewer/fits.css'],

    i18nFolderPath : ['/sitools/client-extension/resources/i18n/fitsViewer/'],
    
    init : function () {
        this.callParent(arguments);
        
        var view = Ext.create('sitools.extension.view.modules.fitsViewer.FitsViewerMainView');
        this.show(view);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.itemId
        };

    }
});
