/***************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, getDesktop*/
Ext.ns('sitools.widget');

/**
 * A specific window to display preview Images in user desktop. 
 * @class sitools.widget.WindowImageZoomer
 * @config {boolean} resizeImage true to resize the image according to the desktop, image ratio is also keeped. 
 * false to keep the image size with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
sitools.widget.WindowImageZoomer = Ext.extend(Ext.Window, {
	resizeImage : false,
	maximizable : true,
	modal : true,
	minHeight : 0,
	minWidth : 0,
	
	initComponent : function() {

		viewer.onload =  viewer.toolbar;
            		
		this.viewer  = new viewer({
//		    parent : panel.getEl(),
		    imageSource: this.src,
		    frame: ['600px','300px'],
		    zoomFactor: '20%',
		    maxZoom: '400%'
		});
		
		this.panel = new Ext.Panel({
			layout : 'fit',
			bodyCfg : {
				tag : 'img'
			},
			style: {
				overflow: 'hidden',
                backgroundColor: '#f2f1f0',
                cursor: 'move',
                boxShadow: '0 0 2px 2px #888'
            },
            listeners : {
            	scope : this,
            	render : function (panel) {
					panel.getEl().appendChild(this.viewer.frameElement);
            	}
            }
		});
		
		this.items = [this.panel];
		this.autoScroll = true;
		this.constrain = true;
		
		sitools.widget.WindowImageZoomer.superclass.initComponent.apply(this, arguments);
	},
	
	/**
     * Method called when the image is loaded. It is in charge of resizing the image according to the desktop size
     */
    onImageLoad : function () {
        var imgTag = Ext.get(this.viewer.frameElement).dom;
		var hi = this.body.dom.offsetHeight;
		var wi = this.body.dom.offsetWidth;
        
        var hiImg = imgTag.offsetHeight;
        var wiImg = imgTag.offsetWidth;
        
        if (!this.resizeImage) {
            this.panel.setSize(wiImg, hiImg);
        }

		var desktop = getDesktop();
		var ww = desktop.getWinWidth();
		var hw = desktop.getWinHeight();

		var reduce = false;

		if (hi > hw) {
			wi = wi * hw / hi;
			hi = hw;
			reduce = true;
		}
		if (wi > ww) {
			hi = hi * ww / wi;
			wi = ww;
			reduce = true;
		}

		if (reduce) {
			hi *= 0.9;
			wi *= 0.9;
		}

		this.setSize(wi + this.getFrameWidth(), hi + this.getFrameHeight());
        
		// if (Ext.isIE) {
		this.center();
		// }
	}
});
