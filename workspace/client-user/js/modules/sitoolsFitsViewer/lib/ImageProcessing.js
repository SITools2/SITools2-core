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
 *	ImageProcessing module
 */
function ImageProcessing(DynamicImageView, FeatureStyle) {

/**************************************************************************************************************/

var feature;
var layer;
var disable;
var unselect;
var $dialog;
var histogramElement;


/**************************************************************************************************************/

/**
 *	Toggle visibility of dialog
 */
function toggle()
{
	if ( $dialog.dialog( "isOpen" ) )
	{
		$dialog.dialog("close");
	}
	else
	{
		$dialog.dialog("open");
	}
}


/**
 *  Toggle visibility of dialog
 */
function open()
{
    
        $dialog.dialog("open");
}

/**************************************************************************************************************/

/**
 *	Remove view
 */
function remove()
{
	if ( unselect )
	{
		unselect();
	}

	if( disable )
	{
		disable();
	}

	if ( histogramElement )
		histogramElement.remove();

	
	$dialog.remove();
}

/**************************************************************************************************************/

/**
 *	Set data to process
 *
 *	@param selectedData Object containing feature and layer extracted by <PickingManager>
 */
function setData(selectedData)
{
    if ( feature && feature.properties.identifier == selectedData.feature.properties.identifier )
    {
        this.toggle();
    }
    else
    {
       if ( !$dialog.dialog( "isOpen" ) )
       {
            this.toggle();
       }
    }
    
    feature = selectedData.feature;
	layer = selectedData.layer;

	

    var image = selectedData.feature.properties.style.uniformValues;
	if ( !image )
    {
        $dialog.find('.histogramContent').children('div').fadeOut(function(){
				$(this).siblings('p').fadeIn();
		});
    }
    else
    {
    	this.setImage(image);
    }
}

/**************************************************************************************************************/

return {

	/**
	 *	Init
	 *
	 *	@param options
	 *		<ul>
	 *			<li>feature: The feature to process
	 *			<li>layer: The layer to which the feature belongs to
	 *			<li>disable: Disable callback</li>
	 *			<li>unselect: Unselect callback</li>
	 *		</ul>
	 */
	init: function(jsFits, options)
	{
	    if (jsFits) {
	        this.jsFits = jsFits;
	    }
		if ( options )
		{
			//this.id = options.id;
			feature = options.feature || null;
			layer = options.layer || null;

			// Callbacks
			disable = options.disable || null;
			unselect = options.unselect || null;
		}

		var dialog =
			'<div>\
				<div class="imageProcessing" id="imageProcessing" title="Image processing">\
					<h3>Histogram</h3>\
					<div class="histogramContent">\
						<p> Fits isn\'t loaded, thus histogram information isn\'t available</p>\
		                <div style="display: none;" id="histogramView"></div>\
					</div>\
				</div>\
			</div>';

		$dialog = $(dialog).appendTo('body').dialog({
			title: 'Image processing',
			autoOpen: true,
			show: {
				effect: "fade",
				duration: 300
			},
			hide: {
				effect: "fade",
				duration: 300
			},
			width: 500,
			resizable: false,
			minHeight: 'auto',
			position: 
			    { 
			        my: "right middle", 
			        at: "right middle", 
			        of: "body"
		        },
			close: function(event, ui)
			{
				if ( unselect )
				{
					unselect();
				}
				
				$(this).dialog("close");

			}, 
			open : function (even, ui) {
			    $(this).addClass('ui-front');			    
			}
		}).find(".imageProcessing").accordion({
            autoHeight: false,
            active: 0,
            collapsible: true,
            heightStyle: "content"
        }).end();
		
		
		histogramElement = new DynamicImageView( "histogramView", this.jsFits, {
			id: "featureImageProcessing",
			changeShaderCallback: function(contrast){
				if ( contrast == "raw" )
				{
					var targetStyle = new FeatureStyle(null);
//					targetStyle.fillShader = {
//						fragmentCode: null,
//						updateUniforms: null
//					};
//					layer.modifyFeatureStyle( feature, targetStyle );
				}
				else
				{
//					var targetStyle = new FeatureStyle( feature.properties.style );
//					var targetStyle = new FeatureStyle(null);
//					targetStyle.fillShader = {
//						fragmentCode: this.image.fragmentCode,
//						updateUniforms: this.image.updateUniforms
//					};
//					layer.modifyFeatureStyle( feature, targetStyle );
				}
			}
		})
		
	},

	setData: setData,
	setImage: function(image)
	{
		histogramElement.setImage(image);
		
		
		$dialog.find('.histogramContent').children('p').fadeOut(function(){
			$(this).siblings('div').fadeIn();
		});
	},
	toggle: toggle,
	isOpened: function()
	{
		return $dialog.dialog( "isOpen" );
	},
	removeData: function(data)
	{
		if ( feature && data.feature.properties.identifier == feature.properties.identifier )
		{
			if ( this.isOpened() )
			{
				this.toggle();
			}
			$dialog.find('.histogramContent').children('div').fadeOut(function(){
				$(this).siblings('p').fadeIn();
			});
			feature = null;
			layer = null;
		}
	},
	open : open
};

};