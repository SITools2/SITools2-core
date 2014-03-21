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
/*!
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/*global Ext, sitools*/
Ext.ux.Portlet = Ext.extend(Ext.Panel, {
	alias : 'widget.portlet',
    anchor : '100%',
    frame : true,
    collapsible : true,
    draggable : true,
    cls : 'x-portlet',
    // resizer properties
    heightIncrement : 16,
    pinned : false,
    duration : 0.6,
    easing : 'backIn',
    transparent : false,
    minHeight : 10,

    onRender : function (ct, position) {
	    Ext.ux.Portlet.superclass.onRender.call(this, ct, position);

	    // 2008.1.11 xm
	    var createProxyProtoType = Ext.Element.prototype.createProxy;
	    Ext.Element.prototype.createProxy = function (config) {
		    return Ext.DomHelper.append(this.dom, config, true);
	    };

	    this.resizer = new Ext.Resizable(this.el, {
	        animate : true,
	        duration : this.duration,
	        easing : this.easing,
	        handles : 's',
	        transparent : this.transparent,
	        heightIncrement : this.heightIncrement,
	        minHeight : this.minHeight || 100,
	        pinned : this.pinned
	    });
	    this.resizer.on('resize', this.onResizer, this);

	    Ext.Element.prototype.createProxy = createProxyProtoType;
	    // 2008.1.11 xm
    },

    onResizer : function (oResizable, iWidth, iHeight, e) {
	    this.setHeight(iHeight);
    },

    onCollapse : function (doAnim, animArg) {
	    this.el.setHeight('');
	    Ext.ux.Portlet.superclass.onCollapse.call(this, doAnim, animArg);
    }

});

