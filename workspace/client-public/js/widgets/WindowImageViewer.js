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
/*global Ext, sitools, getDesktop*/
Ext.ns('sitools.widget');

/**
 * A specific window to display preview Images in user desktop. 
 * @class sitools.widget.WindowImageViewer
 * @config {boolean} resizeImage true to resize the image according to the desktop, image ratio is also keeped. 
 * false to keep the image size with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
Ext.define('sitools.widget.WindowImageViewer', {
    extend : 'Ext.Window',
    resizeImage : false, 
    maximizable : true, 
    modal : true, 
    minHeight : 0,
    minWidth : 0,
    initComponent : function () {
//        this.renderTo = SitoolsDesk.getDesktop().getDesktopEl();
    
        if (!this.resizeImage) {
	        this.panel = new Ext.Panel({                
	            bodyCfg : {
	                tag : 'img',
	                src : this.src
	            },
	            listeners :  {
	                scope : this,
	                render : function () {
		                this.panel.body.on('load', this.onImageLoad, this, {
				            single : true
				        });
		            }
	            }
	        });
            this.items = [this.panel];
            this.autoScroll=true;
        } else {
            this.bodyCfg = {
                tag : 'img',
                src : this.src
            };
        }
        this.constrain = true;
        sitools.widget.WindowImageViewer.superclass.initComponent.apply(this, arguments);
    },
    
    onRender : function () {
        sitools.widget.WindowImageViewer.superclass.onRender.apply(this, arguments);
        if(this.resizeImage){
            this.body.on('load', this.onImageLoad, this, {
                single : true
            });
        }
    },
    /**
     * Method called when the image is loaded. It is in charge of resizing the image according to the desktop size
     */
    onImageLoad : function () {
        var imgTag = Ext.DomQuery.select("div[id='"+this.id+"'] img")[0];
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
	},
    
    setSrc : function (src) {
        var panel;
        if(resizeImage){
            panel = this;
        }else {
            panel = this.panel;
        }
        panel.body.on('load', this.onImageLoad, this, {
            single : true
        });
        panel.body.dom.style.width = panel.body.dom.style.width = 'auto';
        panel.body.dom.src = src;
    },
    
    initEvents : function () {
        sitools.widget.WindowImageViewer.superclass.initEvents.apply(this, arguments);
        if (this.resizer && this.resizeImage) {
            this.resizer.preserveRatio = true;
        }
    }
});

/**
 * A specific panel to display preview Images in user desktop. 
 * @class sitools.widget.WindowImageViewer
 * @extends Ext.Window
 */
Ext.define('sitools.widget.PanelImageViewer', {
    extend : 'Ext.Panel',

    initComponent : function () {
        this.bodyCfg = {
            tag : 'img',
            src : this.src
        };
        sitools.widget.PanelImageViewer.superclass.initComponent.apply(this, arguments);
    },

    onRender : function () {
        sitools.widget.PanelImageViewer.superclass.onRender.apply(this, arguments);
        this.body.on('load', this.onImageLoad, this, {
            single : true
        });
    },

    onImageLoad : function () {
        var h = this.getFrameHeight(), w = this.getFrameWidth();
        this.setSize(this.body.dom.offsetWidth + w, this.body.dom.offsetHeight + h);
        if (Ext.isIE) {
            this.center();
        }
    },

    setSrc : function (src) {
        this.body.on('load', this.onImageLoad, this, {
            single : true
        });
        this.body.dom.style.width = this.body.dom.style.width = 'auto';
        this.body.dom.src = src;
    },

    initEvents : function () {
        sitools.widget.PanelImageViewer.superclass.initEvents.apply(this, arguments);
        if (this.resizer) {
            this.resizer.preserveRatio = true;
        }
    }, 
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
    	me.show();
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
    	me.show();
    }
    
});

