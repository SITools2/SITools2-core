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
 * @class sitools.widget.WindowImageZoomViewer
 * @config {boolean} resizeImage true to resize the image according to the desktop, image ratio is also keeped. 
 * false to keep the image size with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
sitools.widget.WindowImageZoomViewer = Ext.extend(Ext.Window, {
	resizeImage : false,
	maximizable : true,
	modal : true,
	minHeight : 0,
	minWidth : 0,
	
	initComponent : function() {
		this.isMoving = false;
	    this.imageWidth = null;
	    this.imageHeight = null;
	    this.originalImageWidth = null;
	    this.originalImageHeight = null;
	    this.clickX = null;
	    this.clickY = null;
	    this.lastMarginX = null;
	    this.lastMarginY = null;
	    this.rotation = 0;

		this.toolbar = new Ext.Toolbar({
			items : [{
				xtype : 'button',
				tooltip : 'zoomIn',
				icon : loadUrl.get('APP_URL')
						+ '/common/res/multizoom/icons/zoom_in.png',
				listeners : {
					click : this.zoomIn,
					scope : this
				}
			}, {
				xtype : 'button',
				tooltip : 'zoomOut',
				icon : loadUrl.get('APP_URL')
						+ '/common/res/multizoom/icons/zoom_out.png',
				listeners : {
					click : this.zoomOut,
					scope : this
				}
			}]
		});

		this.container = new Ext.Container({
            itemId: 'imagecontainer',
            flex: 1,
            style: {
                overflow: 'hidden',
                backgroundColor: '#f2f1f0',
                padding: '10px',
                cursor: 'move'
            }
		});
		
		if (!this.resizeImage) {
			this.panel = new Ext.Panel({
				tbar : this.toolbar,
				bodyCfg : {
					tag : 'img',
					src : this.src
				},
				style: {
                    boxShadow: '0 0 5px 5px #888'
                },
				listeners : {
					scope : this,
					render : function() {
						this.panel.body.on('load', this.onImageLoad, this, {
							single : true
						});
					}
				}
			});
			this.container = new Ext.Container({
            itemId: 'imagecontainer',
            flex: 1,
            style: {
                overflow: 'hidden',
                backgroundColor: '#f2f1f0',
                padding: '10px',
                cursor: 'move'
	            },
	            items : [this.panel]
			});
			
			this.items = [this.container];
			
			this.autoScroll = true;
		} else {
			this.bodyCfg = {
				tag : 'img',
				src : this.src
			};
		}
		
		this.constrain = true;
		
		sitools.widget.WindowImageZoomViewer.superclass.initComponent.apply(this, arguments);
	},

	onRender : function() {
		sitools.widget.WindowImageZoomViewer.superclass.onRender.apply(this, arguments);
		if (this.resizeImage) {
			this.body.on('load', this.onImageLoad, this, {
				single : true
			});
		}
	},
	/**
	 * Method called when the image is loaded. It is in charge of resizing the image according to the desktop size
	 */
	onImageLoad : function() {
		this.imgTag = Ext.DomQuery.select("div[id='" + this.id + "'] img")[0];
		this.imgEl = Ext.get(this.imgTag);
		
		var hi = this.body.dom.offsetHeight;
		var wi = this.body.dom.offsetWidth;

		var hiImg = this.imgTag.offsetHeight;
		var wiImg = this.imgTag.offsetWidth;

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

		this.rotation = 0;
        this.originalImageWidth = this.imgTag.naturalWidth;
        this.originalImageHeight = this.imgTag.naturalHeight;
        this.imageWidth = hiImg;
        this.imageHeight = wiImg;
        this.stretchOptimally();
		
		this.setSize(wi + this.getFrameWidth(), hi + this.getFrameHeight());
		this.center();
	},

	setSrc : function(src) {
		var panel;
		if (resizeImage) {
			panel = this;
		} else {
			panel = this.panel;
		}
		panel.body.on('load', this.onImageLoad, this, {
			single : true
		});
		panel.body.dom.style.width = panel.body.dom.style.width = 'auto';
		panel.body.dom.src = src;
	},

	initEvents : function() {
		if (this.resizer && this.resizeImage) {
			this.resizer.preserveRatio = true;
		}
		
		this.panel.mon(this.panel.getEl(), {
            mouseup: this.mouseup,
            mousedown: this.mousedown,
            mousemove: this.mousemove,
            scope: this
        });
        
			this.panel.addListener('mousewheel', function (){
				console.log('toto');
			});
        
		sitools.widget.WindowImageZoomViewer.superclass.initEvents.apply(this, arguments);
	},
	
	
    stretchHorizontally: function () {
        var me = this,
            imageContainerWidth = me.getImageContainer().getWidth();

        me.setImageSize({
            width: imageContainerWidth - 20,
            height: me.originalImageHeight * (imageContainerWidth - 20) / me.originalImageWidth
        });

        me.centerImage();
    },

    stretchVertically: function () {
        var me = this,
            imageContainerHeight = me.getImageContainer().getHeight();

        me.setImageSize({
            width: me.originalImageWidth * (imageContainerHeight - 20) / me.originalImageHeight,
            height: imageContainerHeight - 20
        });

        me.centerImage();
    },

    stretchOptimally: function () {
        var me = this,
            imageContainer = me.getImageContainer(),
            adjustedImageSize = me.getAdjustedImageSize();

        if (adjustedImageSize.width * imageContainer.getHeight() / adjustedImageSize.height > imageContainer.getWidth()) {
            me.stretchHorizontally();
        } else {
            me.stretchVertically();
        }
    },

    centerImage: function () {
        var me = this,
            imageContainer = me.getImageContainer(),
            adjustedImageSize = me.getAdjustedImageSize();

        me.setMargins({
            top: (imageContainer.getHeight() - adjustedImageSize.height - 20) / 2,
            left: (imageContainer.getWidth() - adjustedImageSize.width - 20) / 2
        });
    },

	zoomOut : function(btn, event, opts) {
		var me = this, 
		margins = me.getMargins(), 
		adjustedImageSize = me.getAdjustedImageSize();

		me.setMargins({
					top : margins.top + adjustedImageSize.height * 0.05,
					left : margins.left + adjustedImageSize.width * 0.05
				});

		me.setImageSize({
					width : adjustedImageSize.width * 0.9,
					height : me.originalImageHeight
							* adjustedImageSize.width * 0.9
							/ me.originalImageWidth
				});

		event.stopEvent();
	},

	zoomIn : function(btn, event, opts) {
		var me = this,
		margins = me.getMargins(),
		adjustedImageSize = me.getAdjustedImageSize();

		me.setMargins({
					top : margins.top - adjustedImageSize.height * 0.05,
					left : margins.left - adjustedImageSize.width * 0.05
				});

		me.setImageSize({
					width : adjustedImageSize.width * 1.1,
					height: me.originalImageHeight * adjustedImageSize.width * 1.1 / me.originalImageWidth
//					height : me.imageHeight * adjustedImageSize.width * 1.1 / me.imageWidth
				});

		event.stopEvent();
	},

	getAdjustedImageSize : function() {
		var me = this,
      	rotation = me.rotation;

		if (rotation === 90 || rotation === 270) {
			return {
				width : me.imageHeight,
				height : me.imageWidth
			};
		} else {
			return {
				width : me.imageWidth,
				height : me.imageHeight
			};
		}
	},

	getMargins : function() {
		var me = this;
      	rotation = me.rotation;
		imageEl = me.getImage();

		var margins = {
			top : parseInt(imageEl.getStyle('margin-top'), 10),
			left : parseInt(imageEl.getStyle('margin-left'), 10)
		};

		if (rotation === 90 || rotation === 270) {
			var marginAdjustment = (me.imageHeight - me.imageWidth)
					/ 2;
			margins.top = margins.top + marginAdjustment;
			margins.left = margins.left - marginAdjustment;
		}

		return margins;
	},

	setMargins : function(margins) {
		var me = this,
	    rotation = me.rotation,
	      
		adjustedImageSize = me.getAdjustedImageSize(),
		imageContainer = me.getImageContainer(),
		imageContainerWidth = imageContainer.getWidth(),
		imageContainerHeight = imageContainer.getHeight();

		if (adjustedImageSize.width > imageContainerWidth - 20) {
			if (margins.left > 0) {
				margins.left = 0;
			} else if (margins.left < imageContainerWidth
					- adjustedImageSize.width - 20) {
				margins.left = imageContainerWidth - adjustedImageSize.width
						- 20;
			}
		} else {
			if (margins.left < 0) {
				margins.left = 0;
			} else if (margins.left > imageContainerWidth
					- adjustedImageSize.width - 20) {
				margins.left = imageContainerWidth - adjustedImageSize.width
						- 20;
			}
		}

		if (adjustedImageSize.height > imageContainerHeight - 20) {
			if (margins.top > 0) {
				margins.top = 0;
			} else if (margins.top < imageContainerHeight
					- adjustedImageSize.height - 20) {
				margins.top = imageContainerHeight - adjustedImageSize.height
						- 20;
			}
		} else {
			if (margins.top < 0) {
				margins.top = 0;
			} else if (margins.top > imageContainerHeight
					- adjustedImageSize.height - 20) {
				margins.top = imageContainerHeight - adjustedImageSize.height
						- 20;
			}
		}

		if (rotation === 90 || rotation === 270) {
			var marginAdjustment = (me.imageHeight - me.imageWidth)
					/ 2;
			margins.top = margins.top - marginAdjustment;
			margins.left = margins.left + marginAdjustment;
		}

		me.getImage().setStyle('margin-left', margins.left + 'px');
		me.getImage().setStyle('margin-top', margins.top + 'px');
	},

	setImageSize : function(size) {
		var me = this,
		rotation = me.rotation;

		if (rotation === 90 || rotation === 270) {
			me.imageWidth = size.height;
			me.imageHeight = size.width;
		} else {
			me.imageWidth = size.width;
			me.imageHeight = size.height;
		}
	},
	
	mousedown: function (event) {
        var me = this,
        margins = me.getMargins();

        event.stopEvent();

        me.clickX = event.getPageX();
        me.clickY = event.getPageY();
        me.lastMarginY = margins.top;
        me.lastMarginX = margins.left;

        me.isMoving = true;
    },

    mousemove: function (event) {
        var me = this;
        if (me.isMoving) {
            me.setMargins({
                top: me.lastMarginY - me.clickY + event.getPageY(),
                left: me.lastMarginX - me.clickX + event.getPageX()
            });
        }
    },

    mouseup: function () {
        var me = this;

 		if (me.isMoving) {
            me.clickX = null;
            me.clickY = null;
            me.lastMarginX = null;
            me.lastMarginY = null;
            me.isMoving = false;
        }
    },

	getImage : function() {
		//        return this.query('image')[0];
//		return this.imgTag;
		return this.imgEl;
	},

	getImageContainer : function() {
		return this.panel.getEl();
	}
});
