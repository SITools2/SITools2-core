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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl, ColumnRendererEnum*/

Ext.namespace('sitools.admin.datasets.columnRenderer');
/**
 * List of constants
 * @type sitools.admin.datasets.columnRenderer.columnRenderEnum
 */
sitools.admin.datasets.columnRenderer.behaviorEnum = {
    /**
	 * URL_LOCAL constant
	 * @static
	 * @type String
	 */
	URL_LOCAL : "localUrl",
	/**
	 * URL_EXT_NEW_TAB constant
	 * @static
	 * @type String
	 */
	URL_EXT_NEW_TAB : "extUrlNewTab",
	/**
	 * URL_EXT_DESKTOP constant
	 * @static
	 * @type String
	 */
	URL_EXT_DESKTOP : "extUrlDesktop",
	/**
	 * IMAGE_NO_THUMB constant
	 * @static
	 * @type String
	 */
	IMAGE_NO_THUMB : "ImgNoThumb",
	/**
	 * IMAGE_THUMB_FROM_IMAGE constant
	 * @static
	 * @type String
	 */
	IMAGE_THUMB_FROM_IMAGE : "ImgAutoThumb",
	/**
	 * IMAGE_FROM_SQL constant
	 * @static
	 * @type String
	 */
	IMAGE_FROM_SQL : "ImgThumbSQL",
	/**
	 * DATASET_LINK constant
	 * @static
	 * @type String
	 */
	DATASET_LINK : "datasetLink",
	/**
	 * DATASET_ICON_LINK constant
	 * @static
	 * @type String
	 */
	DATASET_ICON_LINK : "datasetIconLink",
	/**
	 * NO_CLIENT_ACCESS constant
	 * @static
	 * @type String
	 */
	NO_CLIENT_ACCESS : "noClientAccess",
    
    
    getColumnRendererCategoryFromBehavior : function (behavior) {
        var category;
        switch (behavior) {
		case ColumnRendererEnum.URL_LOCAL:
		case ColumnRendererEnum.URL_EXT_NEW_TAB:
		case ColumnRendererEnum.URL_EXT_DESKTOP:
            category = "URL";
            break;
        case ColumnRendererEnum.IMAGE_NO_THUMB:
        case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE:
        case ColumnRendererEnum.IMAGE_FROM_SQL:
            category = "Image";
            break;
        case ColumnRendererEnum.DATASET_LINK:
        case ColumnRendererEnum.DATASET_ICON_LINK:
            category = "DataSetLink";
            break;
        case ColumnRendererEnum.NO_CLIENT_ACCESS:
            category = "Other";
            break;
        default :
            category = "";
            break;            
        }
        return category;
    }, 
    /**
     * According to a given column Renderer, return true if the column should be displayed as an image
     * @param {Object} cr the Column Renderer
     * @return {Boolean} true when displaying an image, false otherwise.
     */
    isDisplayingImage : function (cr) {
    	var result = false;
    	switch (cr.behavior) {
        case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE:
        case ColumnRendererEnum.IMAGE_FROM_SQL:
	    case ColumnRendererEnum.DATASET_ICON_LINK:
            result = true;
	        break;
   		case ColumnRendererEnum.URL_LOCAL:
		case ColumnRendererEnum.URL_EXT_NEW_TAB:
		case ColumnRendererEnum.URL_EXT_DESKTOP:
			if (cr.type == "Image")
			result = true;
			break;
    	}
    	return result;
		
    }
};

var ColumnRendererEnum = sitools.admin.datasets.columnRenderer.behaviorEnum;