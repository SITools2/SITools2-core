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
// namespace
//Ext.ns('Ext.ux');

/**
 * @class Ext.ux.FisheyeMenuExtention
 * @extend Ext.ux.FisheyeMenu
 * @author hello2008
 * @version v0.2
 * @create 2010-04-22
 * @update 2010-04-26 
 * 
 * Evolution
 * @author a.labeau
 * @update 2010-06
 */
Ext.ux.FisheyeMenuExtention = Ext.extend(Ext.ux.FisheyeMenu, {
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
        Ext.ux.FisheyeMenuExtention.superclass.constructor.call(this);
    },

    //------------------------------------------------------------
    // public/private methods
    //------------------------------------------------------------
    /**
	 * Add menu item
     * @param {Object} item Config option
     */
	addItem : function(item) {
		var sId = this.el.getAttribute('id') || Ext.id();

		// build fisheye menu item
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
			id : sId + '-' + item.id,
			cls : 'ux-fisheye-menu-item ' + this.vAlignCls,
			href : item.url || '#',
			title : sTitle,
			target : item.target || '_blank',
			onClick : "javascript : "+item.fct,
			children : arr
		});

        this.menuItems = this.containerEl.select('a.ux-fisheye-menu-item');
        this.itemCount = this.menuItems.getCount();
		// render UI
		this.onRender();
		
        // reset events, hover or not
        this.menuItems.on('mouseover', this.onItemHover, this);
        this.menuItems.on('mouseout', this.onItemOut, this);
	},

	/**
	 * Remove specific menu item
	 * @param {Number} index The specific item index
	 */
	removeItem : function(idModule) {
		var sId = 'fisheye-menu-bottom-' + idModule;
			menuItem = Ext.get(sId);

		if(!menuItem) {
			alert('cannot find the menu item!');
		} else {
			menuItem.remove();

			this.menuItems = this.containerEl.select('a.ux-fisheye-menu-item');
			this.itemCount = this.menuItems.getCount();

			// render UI
			this.onRender();
			
		}
	}

});  // end of Ext.ux.FisheyeMenuExtention
