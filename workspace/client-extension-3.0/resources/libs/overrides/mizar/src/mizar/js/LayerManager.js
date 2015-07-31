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
/*global define: false */

/**
 * LayerManager module
 */
define( [ "jquery", "underscore-min", "gw/FeatureStyle", "gw/HEALPixLayer", "gw/VectorLayer", "gw/CoordinateGridLayer", "gw/TileWireframeLayer", "gw/OpenSearchLayer", "gw/WMSLayer", "./ClusterOpenSearchLayer", "./MocLayer", "./PlanetLayer", "./HEALPixFITSLayer", "./PickingManager", "./Utils", "./JsonProcessor", "gw/TileWireframeLayer","./AtmosphereLayer","jquery.ui"], 
	function($, _, FeatureStyle, HEALPixLayer, VectorLayer, CoordinateGridLayer, TileWireframeLayer, OpenSearchLayer, WMSLayer,
			ClusterOpenSearchLayer, MocLayer, PlanetLayer, HEALPixFITSLayer, PickingManager, Utils, JsonProcessor, TileWireframeLayer, AtmosphereLayer) {

/**
 * Private variables
 */
var sky;
var gwLayers = [];
var planetLayers = [];
var configuration;

// GeoJSON data providers
var dataProviders = {};


/**
 * Private functions
 */

/**************************************************************************************************************/

/**
 *	Create simple vector layer
 */
function createSimpleLayer(name)
{
	// Generate random color
	var rgb = Utils.generateColor();
	var rgba = rgb.concat([1]);
	
	// Create style
	var options = {
		name: name,
		style: new FeatureStyle({
			iconUrl: configuration.mizarBaseUrl + "css/images/star.png",
			fillColor: rgba,
			strokeColor: rgba,
			visible: true
		})
	};

	// Create vector layer
	var gwLayer = new VectorLayer( options );
	// Add the type GeoJSON to be able to zoom on the layer ! (cf HTML generation of additional layer)
	gwLayer.type = "GeoJSON";
	gwLayer.deletable = true;
	gwLayer.pickable = true;

	return gwLayer;
}

/**************************************************************************************************************/

/**
 *	Create layer from configuration file
 */
function createLayerFromConf(layerDesc) {
	var gwLayer;

	// Ensure that the attribution link will be opened in new tab
	if ( layerDesc.attribution && layerDesc.attribution.search('<a') >= 0 && layerDesc.attribution.search('target=') < 0 )
	{
		layerDesc.attribution = layerDesc.attribution.replace(' ', ' target=_blank ');
	}
	
	// Update layer color
	if ( layerDesc.color )
	{
		layerDesc.color = FeatureStyle.fromStringToColor( layerDesc.color );
	}
	else
	{
		// Generate random color
		var rgb = Utils.generateColor();
		layerDesc.color = rgb.concat([1]);
	}

	// Layer opacity must be in range [0, 1] 
	if ( layerDesc.opacity ) {
		layerDesc.opacity /= 100;
	}
	// Layers are not visible by default
	if ( !layerDesc.visible ) {
		layerDesc.visible = false;
	}

	// Create style if needed
	if ( !layerDesc.style ) {
		var defaultVectorStyle = new FeatureStyle({ 
			rendererHint: "Basic", 
			opacity: layerDesc.opacity,
			iconUrl: layerDesc.icon ? layerDesc.icon : configuration.mizarBaseUrl + "css/images/star.png",
			fillColor: layerDesc.color,
			strokeColor: layerDesc.color
		});
		layerDesc.style = defaultVectorStyle;
	}

	// Depending on type of layer, create layer
	switch(layerDesc.type){
		case "atmosphere":
			gwLayer = new AtmosphereLayer( layerDesc);
			break;
		case "tileWireframe":
			gwLayer = new TileWireframeLayer( layerDesc );
			break;
		case "healpix":

			if ( layerDesc.fitsSupported )
			{
				gwLayer = new HEALPixFITSLayer(layerDesc);
			}
			else
			{
				gwLayer = new HEALPixLayer(layerDesc);
			}
			if ( layerDesc.availableServices )
			{
				gwLayer.availableServices = layerDesc.availableServices;
				gwLayer.healpixCutFileName = layerDesc.healpixCutFileName;
			}

			break;
		
		case "coordinateGrid":
			gwLayer = new CoordinateGridLayer( layerDesc );
			break;
			
		case "healpixGrid":
			gwLayer = new TileWireframeLayer( layerDesc );
			break;
			
		case "GeoJSON":
			gwLayer = new VectorLayer(layerDesc);
			gwLayer.pickable = layerDesc.hasOwnProperty('pickable') ? layerDesc.pickable : true;

			break;
			
		case "DynamicOpenSearch":

			if ( layerDesc.useCluster )
			{
				gwLayer = new ClusterOpenSearchLayer( layerDesc );
			}
			else
			{
				gwLayer = new OpenSearchLayer( layerDesc );
			}

			if (layerDesc.displayProperties) {
				gwLayer.displayProperties = layerDesc.displayProperties;
			}
			gwLayer.pickable = layerDesc.hasOwnProperty('pickable') ? layerDesc.pickable : true;
			gwLayer.availableServices = layerDesc.availableServices;
			break;

		case "Moc":
			layerDesc.style.fill = true;
			layerDesc.style.fillColor[3] = 0.3; // make transparent
			gwLayer = new MocLayer( layerDesc );
			gwLayer.dataType = "line";
			break;
		case "Vector":
			gwLayer = createSimpleLayer(layerDesc.name);
			gwLayer.pickable = layerDesc.hasOwnProperty('pickable') ? layerDesc.pickable : true;
			gwLayer.deletable = false;
			break;
		case "Planet":
			gwLayer = new PlanetLayer( layerDesc );
			break;
                case "WMS":
                        gwLayer = new WMSLayer( layerDesc );
                        break;
		default:
			console.error(layerDesc.type+" isn't not implemented");
			return null;
	}
	gwLayer.type = layerDesc.type;
	gwLayer.dataType = layerDesc.dataType;
	// Store category name on GlobWeb layer object to be able to restore it later
	gwLayer.category = layerDesc.background ? "background" : layerDesc.category;

	return gwLayer;
}

/**************************************************************************************************************/

/**
 *	Fill the LayerManager table
 */
function initLayers(layers) 
{
	for (var i=0; i<layers.length; i++) {
		var layer = layers[i];
		this.addLayerFromDescription(layer);
	}
}

/**************************************************************************************************************/

return {
	/**
	 *	Init
	 *
	 *	@param mizar
	 *		Mizar API object
	 *	@param conf
	 *		Mizar configuration
 	 */
	init: function(mizar, conf) {
		this.mizar = mizar;
		configuration = conf;
		// Store the sky in the global module variable
		sky = mizar.sky;

		// TODO : Call init layers
		//initLayers(configuration.layers);
	},
	
	/**
	 *	Register data provider
	 *
	 *	@param type Type of data
	 *	@param loadFunc Callback function loading and adding data to the layer
	 */
	registerDataProvider: function( type, loadFunc )
	{
		dataProviders[type.toString()] = loadFunc;
	},

	/**
	 *	Get current layers
	 */
	 getLayers: function()
	 {
	 	return gwLayers;
	 },

	 /**
	  *	Add layer to activated globe(could be globe or sky)
	  *	Triggers events createion events
	  *
	  *	@param gwLayer
	  *		GlobWeb layer to add
	  */
	 addLayerToGlobe: function(gwLayer)
	 {
	 	var globe = this.mizar.activatedContext.globe;
	 	if( gwLayer.category === "background" )
		{
			// Add to engine
			if ( gwLayer.visible() )
			{
				// Change visibility's of previous layer(maybe GlobWeb should do it ?)
				if ( globe.baseImagery )
				{
					globe.baseImagery.visible(false);
				}

				globe.setBaseImagery( gwLayer );
				gwLayer.visible(true);
			}

			// Publish the event
			this.mizar.publish("backgroundLayer:add", gwLayer);
		}
		else
		{
			// Add to engine
			if ( !(gwLayer instanceof PlanetLayer) ) {
				globe.addLayer( gwLayer );
			}

			// Publish the event
			this.mizar.publish("additionalLayer:add", gwLayer);
		}
	 },

	 /**
	  *	Add layer to glbe depending on Mizar's mode
	  *
	  *	@param gwLayer
	  *		GlobWeb layer
	  *	@param planetLayer
	  *		Planet layer, if described layer must be added to planet (optional)
	  */
	 addLayer: function(gwLayer, planetLayer)
	 {
		if ( planetLayer )
		{
			// Add layer to planet
			planetLayer.layers.push(gwLayer);
		 	if ( this.mizar.mode === "planet" )
		 	{
		 		this.addLayerToGlobe( gwLayer );
		 	}
		}
		else
		{
			// Store planet base imageries to be able to set background from name
			if ( gwLayer instanceof PlanetLayer ) {
				for (var i=0; i<gwLayer.baseImageries.length; i++) {
					planetLayers.push( gwLayer.baseImageries[i] );
				}
			}

			// Add layer to sky
			gwLayers.push(gwLayer);
		 	if ( this.mizar.mode === "sky" )
		 	{
		 		this.addLayerToGlobe( gwLayer );
		 	}
		}
	 },

	 /**
	  *	Create layer from layer description and add it engine
	  *
	  *	@param layerDesc
	  *		Layer description
	  *	@param planetLayer
	  *		Planet layer, if described layer must be added to planet (optional)
	  *	@return
	  *		Created layer if doesn't already exist, existing layer otherwise
	  */
	 addLayerFromDescription: function(layerDesc, planetLayer) {

		var gwLayer = _.findWhere(gwLayers, {name: layerDesc.name});
		if ( !gwLayer )
		{
			gwLayer = createLayerFromConf(layerDesc);
			if ( gwLayer )
			{
				this.addLayer(gwLayer, planetLayer);
			}

			// Fill data-provider-type layer by features coming from data object
			if ( layerDesc.data && dataProviders[layerDesc.data.type] )
			{
				var callback = dataProviders[layerDesc.data.type];
				var data = callback(gwLayer, layerDesc.data);
			}

			if ( gwLayer.pickable ) {
				PickingManager.addPickableLayer(gwLayer);
			}
		}

		return gwLayer;
	 },

	 /**
	  *	Remove the given layer
	  *	@param gwLayer
	  *		GlobWeb layer
	  */
	 removeLayer: function(gwLayer) {

	 	var index = gwLayers.indexOf(gwLayer);
	 	gwLayers.splice( index, 1 );
	 	if ( gwLayer.pickable ) {
			PickingManager.removePickableLayer(gwLayer);
		}

		this.mizar.publish("layer:remove", gwLayer);
	 	sky.removeLayer(gwLayer);
	 },

	 /**
	  *	Set background survey from its name
	  *	@param survey
	  *		Survey name
	  */
	 setBackgroundSurvey: function(survey) {
		
		var globe = this.mizar.activatedContext.globe;
		var gwLayer;
	 	if ( this.mizar.mode === "sky" )
	 	{
			// Find the layer by name among all the layers
		 	gwLayer = _.findWhere(gwLayers, {name: survey});
		 	if ( gwLayer )
		 	{
				// Check if is not already set
			 	if ( gwLayer !== globe.baseImagery )
			 	{
				 	// Change visibility's of previous layer, because visibility is used to know the active background layer in the layers list (layers can be shared)
				 	if ( globe.baseImagery ) {
						globe.baseImagery.visible(false);
					}
					globe.setBaseImagery( gwLayer );
					gwLayer.visible(true);

					// Clear selection
					PickingManager.getSelection().length = 0;

					for ( var i=0; i<gwLayers.length; i++ )
					{
						var currentLayer = gwLayers[i];
						if ( currentLayer.subLayers )
						{
							var len = currentLayer.subLayers.length;
							for ( var j=0; j<len; j++ )
							{
								var subLayer = currentLayer.subLayers[j];
								if (subLayer.name === "SolarObjectsSublayer" )
								{
									PickingManager.removePickableLayer( subLayer );
									globe.removeLayer( subLayer );
									currentLayer.subLayers.splice(j,1);
								}
							}
						}
					}
					this.mizar.publish("backgroundLayer:change", gwLayer);
				}
		 	} else {
		 		this.mizar.publish("backgroundSurveyError", "Survey " + survey + " hasn't been found");
		 	}
	 	}
	 	else
	 	{
	 		// Planet mode
		 	gwLayer = _.findWhere(planetLayers, {name: survey});
		 	if ( globe.baseImagery ) {
				globe.baseImagery.visible(false);
			}
			globe.setBaseImagery( gwLayer );
			gwLayer.visible(true);
			this.mizar.publish("backgroundLayer:change", gwLayer);
	 	}

	},

	/**
	 *	Create layer from FITS
	 */
	createLayerFromFits: function(name, fits) {
	 	var gwLayer = createSimpleLayer(name);
		gwLayer.dataType = "line";

		// Create feature
		var coords = Utils.getPolygonCoordinatesFromFits(fits);
		var feature = {
			"geometry": {
				"gid": name,
				"coordinates": [coords],
				"type": "Polygon"
			},
			"properties": {
				"identifier": name
			},
			"type": "Feature"
		};

		gwLayer.addFeature( feature );
		PickingManager.addPickableLayer( gwLayer );
		this.addLayer( gwLayer, mizar.activatedContext.planetLayer );
		return gwLayer;
	},

	/**
	 *	Create layer from GeoJSON
	 */
	createLayerFromGeoJson: function(name, geoJson) {
	 	// Add feature collection
		var gwLayer = createSimpleLayer(name);

		// Add feature collection
		JsonProcessor.handleFeatureCollection( gwLayer, geoJson );
		gwLayer.addFeatureCollection( geoJson );
		PickingManager.addPickableLayer( gwLayer );

		this.addLayer( gwLayer, mizar.activatedContext.planetLayer );
		return gwLayer;
	 },

	 createSimpleLayer: createSimpleLayer
};

});
