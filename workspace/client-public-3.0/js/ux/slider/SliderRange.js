// create namespace for plugins
Ext.namespace('Ext.ux.plugins');
 
/**
 * Ext.ux.plugins.SliderRange plugin for Ext.Slider
 * Overrided by Sitools team to be compatible with Extjs4.2 
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
Ext.define('Ext.ux.slider.SliderRange', {
    extend : 'Ext.AbstractPlugin',
    init:function(slider) {
        Ext.apply(slider, {
            onResize:Ext.Function.createSequence(slider.onResize, function(ct, position) {
				
				// slider needs more space with range: height="50px"
				this.el.addCls('ux-sliderrange-slider');
				

				if (slider.range) {
					slider.range.remove();
				}
				
				// create range-object, width from sliders-element
				slider.range 	= slider.bodyEl.down(".x-slider-end").createChild({cls: 'ux-sliderrange'});
				
				// create one child per value and show value in html-config
				for (var i = slider.minValue; i <= slider.maxValue; i = i + slider.increment) {
				    var position = slider.calculateThumbPosition(i);
				    
				    slider.range.createChild({
                        html : '' + i,
                        cls : 'ux-sliderrange-val',
                        style : 'left: ' + position + '%;'
                    });
				}	
			
				
			}) // end of function afterRender
        });
    } // end of function init
}); // end of extend
