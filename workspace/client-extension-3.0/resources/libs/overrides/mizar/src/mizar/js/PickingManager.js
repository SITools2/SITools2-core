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
 * PickingManager module
 */
define( [ "jquery", "gw/FeatureStyle", "gw/OpenSearchLayer", "./FeaturePopup", "./ImageManager", "./CutOutViewFactory", "./Utils" ],
		function($, FeatureStyle, OpenSearchLayer, FeaturePopup, ImageManager, CutOutViewFactory, Utils) {

var mizar;
var context;
var sky; // TODO: refactor it to use always the context
var self;

var selection = [];
var stackSelectionIndex = -1;
var selectedStyle = new FeatureStyle( {
	strokeColor: [1., 1., 0., 1.],
	fillColor: [1., 1., 0., 1.],
	zIndex: 1
} );
var pickableLayers = [];

var mouseXStart;
var mouseYStart;
var timeStart;

var isMobile;

/**************************************************************************************************************/

/**
 *	Event handler for mouse down
 */
function _handleMouseDown(event)
{
	if ( isMobile && event.type.search("touch") >= 0 )
	{
		event.layerX = event.changedTouches[0].clientX;
		event.layerY = event.changedTouches[0].clientY;
	}

	timeStart = new Date();
	mouseXStart = event.layerX;
	mouseYStart = event.layerY;
	clearSelection();
}

/**************************************************************************************************************/

/**
 * Event handler for mouse up
 */
function _handleMouseUp(event)
{
	var timeEnd = new Date();
	var epsilon = 5;
	var diff = timeEnd - timeStart;

	if ( isMobile && event.type.search("touch") >= 0 )
	{
		event.layerX = event.changedTouches[0].clientX;
		event.layerY = event.changedTouches[0].clientY;
	}

	var globe = mizar.activatedContext.globe;
	// If not pan and not reverse name resolver call
	if ( diff < 500 && Math.abs(mouseXStart - event.layerX) < epsilon && Math.abs(mouseYStart - event.layerY) < epsilon )
	{
		var pickPoint = globe.getLonLatFromPixel(event.layerX, event.layerY);

		// Remove selected style for previous selection
		clearSelection();

		var newSelection = computePickSelection(pickPoint);
		
		if ( newSelection.length > 0 )
		{
			var navigation = context.navigation;
			// Hide previous popup if any
			FeaturePopup.hide( function() {
				// View on center
				if ( navigation.inertia )
				{
					navigation.inertia.stop();
				}

				var showPopup = function() {
					selection = newSelection;
					
					// Add selected style for new selection
					focusSelection(selection);
					FeaturePopup.createFeatureList( selection );
					if ( selection.length > 1 )
					{
						// Create dialogue for the first selection call
						FeaturePopup.createHelp();
						stackSelectionIndex = -1;
					}
					else
					{
						// only one layer, no pile needed, create feature dialogue
						self.focusFeatureByIndex( 0, {isExclusive: true} );
						$('#featureList div:eq(0)').addClass('selected');
						FeaturePopup.showFeatureInformation( selection[stackSelectionIndex].layer, selection[stackSelectionIndex].feature )
					}
					var offset = $(globe.renderContext.canvas).offset();
					FeaturePopup.show(offset.left + globe.renderContext.canvas.width/2, offset.top + globe.renderContext.canvas.height/2);
				}

				// TODO: harmonize astro&globe navigations
				if ( navigation.moveTo )
				{
					// Astro
					navigation.moveTo( pickPoint, 800, showPopup );
				}
				else
				{
					navigation.zoomTo( pickPoint, 1800000, 3000, null, showPopup );
				}
			});
		} else {
			FeaturePopup.hide();
		}
		globe.refresh();
	}
}

/**************************************************************************************************************/

/**
 *	Activate picking
 */
function activate()
{
	context.globe.renderContext.canvas.addEventListener("mousedown", _handleMouseDown);
	context.globe.renderContext.canvas.addEventListener("mouseup", _handleMouseUp);

	if ( isMobile )
	{
		context.globe.renderContext.canvas.addEventListener("touchstart", _handleMouseDown);
		context.globe.renderContext.canvas.addEventListener("touchend", _handleMouseUp);
	}

	// Hide popup and blur selection when pan/zoom or animation
	context.navigation.subscribe("modified", hidePopupAndBlurSelection);
}

/**************************************************************************************************************/

function hidePopupAndBlurSelection() {
	clearSelection();
	FeaturePopup.hide();
}

			/**
 *	Deactivate picking
 */
function deactivate()
{
	context.globe.renderContext.canvas.removeEventListener("mousedown", _handleMouseDown);
	context.globe.renderContext.canvas.removeEventListener("mouseup", _handleMouseUp);

	if ( isMobile )
	{
		context.globe.renderContext.canvas.removeEventListener("touchstart", _handleMouseDown);
		context.globe.renderContext.canvas.removeEventListener("touchend", _handleMouseUp);
	}

	// Hide popup and blur selection when pan/zoom or animation
	context.navigation.unsubscribe("modified", hidePopupAndBlurSelection);
}

/**************************************************************************************************************/

/**
 * 	Revert style of selection
 */
function blurSelection()
{
	for ( var i=0; i < selection.length; i++ ) {
		var selectedData = selection[i];
		var style = new FeatureStyle( selectedData.feature.properties.style );
		switch ( selectedData.feature.geometry.type )
		{
			case "Polygon":
			case "MultiPolygon":
				style.strokeColor = selectedData.layer.style.strokeColor;
				break;
			case "Point":
				// Use stroke color while reverting
				style.fillColor = selectedData.feature.properties.style.strokeColor;
				break;
			default:
				break;
		}
		style.zIndex = selectedData.layer.style.zIndex;

		if ( selectedData.layer.globe )
		{
			// Layer is still attached to globe
			selectedData.layer.modifyFeatureStyle( selectedData.feature, style );
		}
	}
}

/**************************************************************************************************************/

/**
 * 	Apply selectedStyle to selection
 */
function focusSelection(newSelection)
{
	var style;
	for ( var i=0; i < newSelection.length; i++ ) {
		var selectedData = newSelection[i];

		if ( selectedData.feature.properties.style )
		{
			style = new FeatureStyle( selectedData.feature.properties.style );	
		}
		else
		{
			style = new FeatureStyle( selectedData.layer.style );
		}

		switch ( selectedData.feature.geometry.type )
		{
			case "Polygon":
			case "MultiPolygon":
				style.strokeColor = selectedStyle.strokeColor;
				break;
			case "Point":
				style.fillColor = selectedStyle.fillColor;
				break;
			default:
				break;
		}
		style.zIndex = selectedStyle.zIndex;
		selectedData.layer.modifyFeatureStyle( selectedData.feature, style );
	}
}

/**************************************************************************************************************/

/**
 *	Clear selection
 */
function clearSelection()
{
	blurSelection();
	selection = [];
}

/**************************************************************************************************************/

/**
 *	Check if a geometry crosses the date line
 */
function fixDateLine(pickPoint, coords)
{		
	var crossDateLine = false;
	var startLon = coords[0][0];
	for ( var i = 1; i < coords.length && !crossDateLine; i++) {
		var deltaLon = Math.abs( coords[i][0] - startLon );
		if ( deltaLon > 180 ) {
			// DateLine!
			crossDateLine =  true;
		}
	}
	
	if ( crossDateLine )
	{
		var fixCoords = [];
		
		if ( pickPoint[0] < 0. )
		{
			// Ensure coordinates are always negative
			for ( var n = 0; n < coords.length; n++) {
				if ( coords[n][0] > 0 ) {
					fixCoords[n] = [ coords[n][0] - 360, coords[n][1] ];
				} else {
					fixCoords[n] = [ coords[n][0], coords[n][1] ];
				}
			}
		}
		else
		{
			// Ensure coordinates are always positive
			for ( var n = 0; n < coords.length; n++) {
				if ( coords[n][0] < 0 ) {
					fixCoords[n] = [ coords[n][0] + 360, coords[n][1] ];
				} else {
					fixCoords[n] = [ coords[n][0], coords[n][1] ];
				}
			}
		}
		
		return fixCoords;
	}
	else
	{
		return coords;
	}
}

/**************************************************************************************************************/

/**
 *	Picking test for feature
 */
function featureIsPicked( feature, pickPoint )
{
	switch ( feature['geometry'].type )
	{
		case "Polygon":
			var ring = fixDateLine(pickPoint, feature['geometry']['coordinates'][0]);
			return Utils.pointInRing( pickPoint, ring );
		case "MultiPolygon":
			for ( var p=0; p<feature['geometry']['coordinates'].length; p++ )
			{
				var ring = fixDateLine(pickPoint, feature['geometry']['coordinates'][p][0]);
				if( Utils.pointInRing( pickPoint, ring ) )
				{
					return true;
				}
			}
			return false;
		case "Point":
			var point = feature['geometry']['coordinates'];
			// Do not pick the labeled features
			var isLabel = feature.properties.style && feature.properties.style.label;
			return Utils.pointInSphere( pickPoint, point, feature['geometry']._bucket.textureHeight ) && !isLabel;
		default:
			console.log("Picking for " + feature['geometry'].type + " is not implemented yet");
			return false;
	}
}

/**************************************************************************************************************/

/**
 * 	Compute the selection at the picking point
 */
function computePickSelection( pickPoint )
{
	var newSelection = [];
	for ( var i=0; i<pickableLayers.length; i++ )
	{
		var selectedTile = sky.tileManager.getVisibleTile(pickPoint[0], pickPoint[1]);
		var pickableLayer = pickableLayers[i];
		if ( pickableLayer.visible() && pickableLayer.globe === mizar.activatedContext.globe )
		{
			if ( pickableLayer instanceof OpenSearchLayer )
			{
				// Extension using layer
				// Search for features in each tile
				var tile = selectedTile;
				var tileData = tile.extension[pickableLayer.extId];

				if ( !tileData || tileData.state != OpenSearchLayer.TileState.LOADED )
				{
					while ( tile.parent && (!tileData || tileData.state != OpenSearchLayer.TileState.LOADED) )
					{
						tile = tile.parent;
						tileData = tile.extension[pickableLayer.extId];
					}
				}

				if ( tileData )
				{
					for ( var j=0; j<tileData.featureIds.length; j++ )
					{
						var feature = pickableLayer.features[pickableLayer.featuresSet[tileData.featureIds[j]].index];
						if ( featureIsPicked(feature, pickPoint) )
						{
							newSelection.push( { feature: feature, layer: pickableLayer } );
						}
					}
				}
			}
			else
			{
				// Vector layer
				// Search for picked features
				for ( var j=0; j<pickableLayer.features.length; j++ )
				{
					var feature = pickableLayer.features[j];
					if ( featureIsPicked(feature, pickPoint) )
					{
						newSelection.push( { feature: feature, layer: pickableLayer } );
					}
				}
			}
		}

		// Add selected tile to selection to be able to make the requests by tile
		// (actually used for asteroids search)
		newSelection.selectedTile = selectedTile;
	}
	
	return newSelection;
}

/**************************************************************************************************************/

return {
	/**
	 *	Init picking manager
	 */
	init: function( m, configuration ) 
	{
		mizar = m;
		// Store the sky in the global module variable
		sky = mizar.sky;
		self = this;
		isMobile = configuration.isMobile;
		this.updateContext();
		activate();

		mizar.subscribe("mizarMode:toggle", this.updateContext);
	
		// Initialize the fits manager
		ImageManager.init(mizar, this, configuration);

		if ( configuration.cutOut )
		{
			// CutOutView factory ... TODO : move it/refactor it/do something with it...
			CutOutViewFactory.init(sky, context.navigation, this);
		}
		FeaturePopup.init(this, ImageManager, sky, configuration);
	},

	/**************************************************************************************************************/
	
	/**
 	 *	Update picking context
	 */
	updateContext: function()
	{
		if ( context )
			deactivate();
		context = mizar.activatedContext;
		activate();
	},

	/**************************************************************************************************************/
	
	/**
	 *	Add pickable layer
	 */
	addPickableLayer: function( layer )
	{
		if ( pickableLayers.indexOf(layer) == -1 )
		{
			pickableLayers.push( layer );
		}
		else
		{
			console.log("WARN:" + layer.name + " has been already added");
		}
	},

	/**************************************************************************************************************/
	
	/**
	 *	Remove pickable layers
	 */
	removePickableLayer: function( layer )
	{
		for ( var i=0; i<pickableLayers.length; i++ )
		{
			if( layer.id == pickableLayers[i].id )
				pickableLayers.splice( i, 1 );
		}
	},

	/**************************************************************************************************************/

	/**
	 * 	Revert style of selected feature
	 */
	blurSelectedFeature: function()
	{
		var selectedData = selection[stackSelectionIndex];
		if ( selectedData )
		{
			var style = new FeatureStyle( selectedData.feature.properties.style );
			switch ( selectedData.feature.geometry.type )
			{
				case "Polygon":
				case "MultiPolygon":
					style.strokeColor = selectedData.layer.style.strokeColor; 
					break;
				case "Point":
					// Use stroke color while reverting
					style.fillColor = selectedData.feature.properties.style.strokeColor; 
					break;
				default:
					break;
			}
			style.zIndex = selectedData.layer.style.zIndex;
			selectedData.layer.modifyFeatureStyle( selectedData.feature, style );
		}
	},

	/**************************************************************************************************************/

	/**
	 * 	Apply selected style to the feature by the given index in selection array
	 * 
	 * 	@param index Index of feature in selection array
	 *	@param options
	 *		<li>isExclusive : Boolean indicating if the focus is exclusive</li>
	 *		<li>color : Highlight color</li>
	 */
	focusFeatureByIndex: function(index, options)
	{
		if ( options && options.isExclusive )
			blurSelection();
		
		// Update highlight color
		var strokeColor = options.color ? FeatureStyle.fromStringToColor(options.color) : selectedStyle.strokeColor;
		var fillColor = options.color ? FeatureStyle.fromStringToColor(options.color) : selectedStyle.fillColor;

		var selectedData = selection[index];
		if ( selectedData )
		{
			stackSelectionIndex = index;
			var style = new FeatureStyle( selectedData.feature.properties.style );
			switch ( selectedData.feature.geometry.type )
			{
				case "Polygon":
				case "MultiPolygon":
					style.strokeColor = strokeColor;
					break;
				case "Point":
					style.fillColor = fillColor;
					break;
				default:
					break;
			}
			style.zIndex = selectedStyle.zIndex;
			selectedData.layer.modifyFeatureStyle( selectedData.feature, style );
		}
		sky.refresh();
	},

	/**************************************************************************************************************/

	/**
	 *	Apply selected style to the given feature
	 */
	focusFeature: function(selectedData, options)
	{
		selection.push(selectedData);
		this.focusFeatureByIndex(selection.length - 1, options);
	},

	/**************************************************************************************************************/

	getSelectedData: function()
	{
		return selection[stackSelectionIndex];
	},

	/**************************************************************************************************************/

	getSelection: function()
	{
		return selection;
	},

	/**************************************************************************************************************/

	doClearSelection: function()
	{
		clearSelection();
	},

	computePickSelection: computePickSelection,
	blurSelection: blurSelection,
	activate: activate,
	deactivate: deactivate
};

});
