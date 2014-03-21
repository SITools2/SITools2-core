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
/*
The MIT License

Copyright (c) 2009-2010 Niko Ni (bluepspower@163.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

// namespace
Ext.ns('Ext.ux');

/**
 * @class Ext.ux.FisheyeMenu
 * @extends-ext Ext.util.Observable
 * @author Niko Ni (bluepspower@163.com)
 * @demo http://cz9908.com/showcase/?item=fisheye-menu&p=1
 * @demo-extra http://cz9908.com/showcase/?item=desktop-fisheye&p=1
 * @version v0.3
 * @create 2009-06-26
 * @update 2010-02-14
 * 
 * Evolution : 2010-06
 * 
 * @author a.labeau
 * 
 * // Example object item:
 * {
 *     text: 'some text',
 *     imagePath: 'some image path',
 *     url: 'some url (optional)',
 *     tip: 'some tip (optional)',
 *     target: 'target element (optional)'
 * }
 * 
 */
Ext.ux.FisheyeMenu = Ext.extend(Ext.util.Observable, {
    //------------------------------------------------------------
    // config options
    //------------------------------------------------------------
    /**
     * @cfg {Array} items Array of fisheye menu config object items.
     */
    items : [],

	/**
	 * @cfg {Mixed} renderTo The container element.
	 */
	renderTo : document.body,

    /**
     * @cfg {Number} itemWidth The minimum width for each menu item.
     */
    itemWidth : 50,

    /**
     * @cfg {Number} vicinity The distance from element that make item interaction.
     */    
    vicinity : 35,

    /**
     * @cfg {String} hAlign Horizontal alignment (left|center|right).
     */
    hAlign : 'center',

    /**
     * @cfg {String} vAlign Vertical alignment (top|bottom).
     */
    vAlign : 'bottom',

	/**
	 * @cfg {Boolean} showTitle To show menu item title or not.
	 */
	showTitle : true,

    //------------------------------------------------------------
    // class constructor
    //------------------------------------------------------------
    /**
     * @constructor
     * @param config
     * @private
     */
    constructor : function(config) {
        Ext.apply(this, config);
        Ext.ux.FisheyeMenu.superclass.constructor.call(this);

        // add custom event
        this.addEvents(
            /**
             * @event change
             * Fires when fisheye menu container is clicked
             * @param {Ext.ux.FisheyeMenu} this
             * @param {Object} targetItem
             * @param {Number} index
             */
            'change'
        );

        // initialize
        this.init();
    },

    //------------------------------------------------------------
    // public/private methods
    //------------------------------------------------------------
    /**
     * @private
     */
    init : function() {
		// properties
        this.el = Ext.get(this.renderTo);               

        // init markup
        this.initMarkup();

        // init events
        this.initEvents();
    },

    /**
     * @private
     */
    initMarkup : function() {
        // set necessary css class
        this.setClass();

        // for wrap class
        this.el.addClass(this.wrapCls);         

        // fisheye menu container
        this.containerEl = this.el.createChild({
            tag : 'div',
            cls : 'ux-fisheye-menu-container ' + this.vAlignCls
        });

        var sId = this.el.getAttribute('id') || Ext.id();

        // build fisheye menu items
        Ext.each(this.items, function(item) {
        	
            var sTitle = this.showTitle === true ? (item.tip || item.text) : '';
            var arr = [{
                tag : 'span',
                html : item.text
            }, {
                tag : 'img',
                src : item.imagePath,
                alt : sTitle
            }];
            if(this.vOrient == 'top') {
                arr = arr.reverse();
            }

            this.containerEl.createChild({
				tag : 'a',
				//id fisheyeMenuItem = fisheye... + module ID 
				id : sId + '-' + item.id,
				cls : 'ux-fisheye-menu-item ' + this.vAlignCls,
				//href : item.url || '#',
				href : '#',
				onClick : "javascript : " + item.fct || "void(0);",
				title : sTitle,
				//target : item.target || '_blank',
				children : arr
            });
        }, this);
                
        this.menuItems = this.containerEl.select('a.ux-fisheye-menu-item');
        this.itemCount = this.menuItems.getCount();

		// render UI
		this.onRender();
    },
    
    /**
     * @private
     */
    initEvents : function() {
        // hover or not
        this.menuItems.on('mouseover', this.onItemHover, this);
        this.menuItems.on('mouseout', this.onItemOut, this);

		// for viewport mousemove event
		Ext.getBody().on('mousemove', this.onItemMove, this);

		// for viewport resize event
		Ext.EventManager.on(window, 'resize', this.onRender, this);

        // for custom event
        this.containerEl.on('click', function(ev, t) {
            if(t.href.slice(-1) == '#') {
                ev.preventDefault();
            }
            var index = parseInt(t.id.split('-').pop(), 10);
            this.fireEvent('change', t, index);
        }, this, {
            delegate : 'a'
        });
    },

	/**
	 * @private
	 */
	setClass : function() {
		this.vOrient = this.vAlign.toLowerCase();
		this.wrapCls = 'menu-wrap-' + this.vOrient;
		this.vAlignCls = 'menu-align-' + this.vOrient;
	},

    /**
     * @private
     */
    onItemMove : function(ev, t) {
        // pointer
        var p = ev.getXY(),
			posX,
			posY,
			increment = 0;

		switch(this.hAlign.toLowerCase()) {
			case 'left':
				posX = p[0] - this.pos[0];
				break;
			case 'right':
				posX = p[0] - this.pos[0] - this.el.getWidth() + this.itemWidth * this.itemCount;
				break;
			default:
				posX = p[0] - this.pos[0] - (this.el.getWidth() - this.itemWidth * this.itemCount)/2 - this.itemWidth/2;
				break;
		}

        posY = Math.pow(p[1] - this.pos[1] - this.el.getHeight()/2, 2);

        this.menuItems.each(function(item, all, index) {
            // distance mathematical calculation reference from http://interface.eyecon.ro
            var d = Math.sqrt(Math.pow(posX - index * this.itemWidth, 2) + posY);
            d -= this.itemWidth/2;
            d = d < 0 ? 0 : d;
            d = d > this.vicinity ? this.vicinity : d;
            d = this.vicinity - d;
            var extraWidth = this.itemWidth * d / this.vicinity;
            item.setStyle({
                left : (this.itemWidth + 3) * index + increment + 'px',
                width : this.itemWidth + extraWidth + 'px'
            });
            increment += extraWidth;
        }, this);

        this.setPosContainer(increment);
    },

    /**
     * @private
     */
    onItemHover : function(ev, t) {
        var target = Ext.get(t);
        target = target.is('img') ? target.up('a') : target;
        var itemText = target.child('span');
        if(itemText) {
            itemText.show();
        }
    },

    /**
     * @private
     */
    onItemOut : function(ev, t) {
        var target = Ext.get(t);
        target = target.is('img') ? target.up('a') : target;
        var itemText = target.child('span');
        if(itemText) {
            itemText.hide();
        }
    },

	/**
	 * @private
	 */
	onRender : function() {
		this.pos = this.el.getXY();
		this.setPosContainer(0);
		this.setPosMenuItems();
	},

	/**
	 * @private
	 */
	doAlignment : function() {
		var aWrapCls = ['menu-wrap-top', 'menu-wrap-bottom'],
			aAlignCls = ['menu-align-top', 'menu-align-bottom'];

		this.setClass();
		this.el.removeClass(aWrapCls).addClass(this.wrapCls);
		this.containerEl.removeClass(aAlignCls).addClass(this.vAlignCls);

		this.menuItems.each(function(item, all, index) {
			var itemText = item.child('span');
			var itemTextCfg = {
				tag : 'span',
				html : itemText.dom.innerHTML
			};
			item.removeClass(aAlignCls).addClass(this.vAlignCls);

			if(this.vAlign.toLowerCase() == 'top') {
				if(!item.last().is('span')) {
					item.createChild(itemTextCfg);
					itemText.remove();
				}
			} else {
				if(!item.first().is('span')) {
					item.insertFirst(itemTextCfg);
					itemText.remove();
				}
			}                       
		}, this);
	},
                
        
	/**
	 * Set alignment
	 * @param {Object} cfg Config options
	 */
	setAlign : function(cfg) {
		var isChange = false;
		// for horizontal alignment
		if(cfg.hAlign) {
			var sHAlign = cfg.hAlign.toLowerCase();
			if(sHAlign != this.hAlign.toLowerCase()) {
				this.hAlign = sHAlign;
				isChange = true;
			}
		}
		// for vertical alignment
		if(cfg.vAlign) {
			var sVAlign = cfg.vAlign.toLowerCase();                 
			if(sVAlign != this.vAlign.toLowerCase()) {
				this.vAlign = sVAlign;
				isChange = true;
			}
		}

		if(isChange) {
			this.doAlignment();
			this.onRender();
		}
	},

    /**
     * @private
     */
    setPosContainer : function(increment) {
		var iLeft;
		switch(this.hAlign.toLowerCase()) {
			case 'left':
				iLeft = - increment/this.itemCount;
				break;
			case 'right':
				iLeft = (this.el.getWidth() - this.itemWidth * this.itemCount) - increment/2;
				break;
			default:
				iLeft = (this.el.getWidth() - this.itemWidth * this.itemCount)/2 - increment/2;
				break;
		}

		this.containerEl.setStyle({
			left : iLeft + 'px',
			width : this.itemWidth * this.itemCount + increment + 'px'
		});
    },

    /**
     * @private
     */    
    setPosMenuItems : function() {
        this.menuItems.each(function(item, all, index) {
            item.setStyle({
                left : (this.itemWidth + 3) * index + 'px',
                width : this.itemWidth + 'px'
            });
        }, this);
    }

});  // end of Ext.ux.FisheyeMenu
