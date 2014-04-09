// create namespace for plugins
Ext.namespace('Ext.ux.plugins');
 
/**
 * Ext.ux.plugins.SliderRange plugin for Ext.Slider
 *
 * @author  Dorothea Loew
 * @date    Februar 15, 2009
 *
 * @class 	Ext.ux.plugins.SliderRange
 * @extends Ext.util.Observable
 */
 
Ext.ux.plugins.SliderRange = function(config) {
    Ext.apply(this, config);
};
 
// plugin code
Ext.define('Ext.ux.plugins.SliderRange', {
    extend : 'Ext.util.Observable',
    init:function(slider) {
        Ext.apply(slider, {
            onResize:slider.onResize.createSequence(function(ct, position) {
				
				// slider needs more space with range: height="50px"
				this.el.addClass ('ux-sliderrange-slider');
				
				if (slider.range)
					slider.range.remove();
				
				// create range-object, width from sliders-element
				slider.range 	= slider.el.createChild({cls: 'ux-sliderrange'});
				
				// get position of two joining values
				var startPos 	= slider.translatePoints(slider.minValue);
				var nextPos 	= slider.translatePoints((slider.minValue+slider.increment));
				// get diff of positions
				var diff 		= nextPos - startPos;
				
				var counter;
				counter = 0;
				// create one child per value and show value in html-config
				for (var i=slider.minValue; i<=slider.maxValue; i=i+slider.increment) {
					slider.range.createChild({html: ''+i, cls: 'ux-sliderrange-val', style: 'left: '+(startPos + (diff * counter)) +'px;'});
					counter++;
				}	
			
				
			}) // end of function afterRender
        });
    } // end of function init
}); // end of extend
