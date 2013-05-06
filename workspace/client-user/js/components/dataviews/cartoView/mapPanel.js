/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window, sitoolsUtils, 
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE, GeoExt, 
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT, OpenLayers, 
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, getColumnModel, loadUrl, getApp
*/

Ext.ns("sitools.user.component.dataviews.cartoView");

/**
 * The LiveGrid used to show Dataset Datas.
 * 
 * @cfg {string} dataUrl (Required) The datasetAttachment to request to load the datas 
 * @cfg {string} datasetId (Required)  The DatasetId, 
 * @cfg {Ext.grid.ColumnModel} datasetCm (Required) A definition of a ColumnModel 
 * @cfg {} userPreference {}  {
 *            componentSettings {Array} : Array of the Columns as saved by the user 
 *            windowSettings {Object} : { 
 *            moduleId : String 
 *                position : [xpos, ypos] 
 *                size : { 
 *                    width : w 
 *                    height : h 
 *                } 
 *                specificType : String 
 *            } 
 *      }
 * @cfg {Array} formParams list of the form params used to search thrue
 * the grid ["TEXTFIELD|AliasColumn1|X", "TEXTFIELD|AliasColumn2|Y"] 
 * @cfg {Array} filters Array of Ext.ux.Filter : [{ 
 *          columnAlias : Alias1, 
 *          data : {
 *            comparison : "LIKE", 
 *            type : "string", 
 *            value : "01" 
 *          } 
 *      }, {
 *            columnAlias : Alias2, 
 *            data : { 
 *              comparison : "gt", 
 *              type : "date",
 *              value : "199-11-04" 
 *            } 
 *      }] 
 *      
 * @requires sitools.user.component.columnsDefinition
 * @requires sitools.user.component.viewDataDetail
 * @requires Ext.ux.grid.GridFiltersSpe
 * @requires sitools.user.component.dataPlotter
 * @class sitools.user.component.dataviews.cartoView.cartoView
 * @extends Ext.ux.grid.livegrid.EditorGridPanel
 * 
 * 
 */
sitools.user.component.dataviews.cartoView.mapPanel = function (config) {
	this.map = new OpenLayers.Map();
	var layer;

    var dataviewConfig = sitoolsUtils.arrayProperties2Object(config.datasetViewConfig);

    var layersDef = Ext.decode(dataviewConfig.layers), mapLayers = [], baseLayer;
    
    //Définir le column Model.
    var colModel = config.datasetCm; 
    var cm = getColumnModel(colModel, config.dictionaryMappings, dataviewConfig);

    
    Ext.each(layersDef, function (layerDef) {
		layer = new OpenLayers.Layer.WMS(
			layerDef.layerName,
			layerDef.url, 
	        {
				layers: layerDef.layerName, 
				format : "image/png"
			},
	        {
				isBaseLayer: layerDef.baseLayer ? true : false,
				opacity : layerDef.baseLayer ? 1 : 0.5
	        }
	    );
	    this.map.addLayer(layer);
	}, this);
    
    // create vector layer
    this.featureLayer = new OpenLayers.Layer.Vector(config.datasetName);
    
    var selectCtrl = new OpenLayers.Control.SelectFeature(this.featureLayer, {
    	id : "selectCtrl", 
    	onSelect : function (feature) {
    		console.log(feature);
    	}
    });
    
    //Ajout d'un controle pour choisir les layers
    this.map.addControl(new OpenLayers.Control.LayerSwitcher());
    //Ajout d'un controle pour la souris
    this.map.addControl(new OpenLayers.Control.MousePosition());

    this.featureLayer.events.on({
		featureselected: function (e) {
			var feature = e.feature;
			var selectCtrl = this.map.getControlsByClass("OpenLayers.Control.SelectFeature");
			if (!Ext.isEmpty(selectCtrl)) {
				selectCtrl = selectCtrl[0];
			}
			
//			var popup = new GeoExt.Popup({
//		        title: 'My Popup',
//		        location: feature,
//		        manager : getDesktop().getManager(), 
//		        width: 200,
//		        html: "toto",
//		        maximizable: true,
//		        collapsible: true, 
//		        layer : feature.layer, 
//		        feature : feature, 
//		        selectCtrl : selectCtrl
//		    });
		    // unselect feature when the popup
		    // is closed
//		    popup.on({
//		        close: function () {
//		            if (OpenLayers.Util.indexOf(this.layer.selectedFeatures, this.feature) > -1) {
//		                this.selectCtrl.unselect(this.feature);
//	                }
//	            }
//	        });
//	        popup.show();
        }, 
        scope : this
    });
    
    this.map.addLayers([this.featureLayer]);
    
    // -- CONSTRUCTOR --
    sitools.user.component.dataviews.cartoView.mapPanel.superclass.constructor.call(Ext.apply(this, {
        region: "center",
        map: this.map,
        center: new OpenLayers.LonLat(5, 45),
        zoom: 6
	}));    

};

Ext.extend(sitools.user.component.dataviews.cartoView.mapPanel, GeoExt.MapPanel, {
	getFeaturesLayer : function () {
		return this.featureLayer;
	}, 
	getMap : function () {
		return this.map;
	}
});