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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure,clog*/
Ext.namespace('sitools.widget');

/**
 * Common component for creating a dynamic link
 */
Ext.define('sitools.widget.ActionLink', {
    extend : 'Ext.Component',
	alias : 'widget.s-actionlink',
    /**
	 * must be declared at the configuration
	 */
    text : undefined,

    initComponent : function () {
	    Ext.apply(this, {
		    autoEl : {
		        tag : 'div',
		        html : '<a class="small-link">' + this.text + "</a>"
		    }

	    });
	    sitools.widget.ActionLink.superclass.initComponent.apply(this, arguments);
    },

    onRender : function () {
	    sitools.widget.ActionLink.superclass.onRender.apply(this, arguments);
	    Ext.get(this.id).child('a').on('click', this._bubbleClick);
	    Ext.get(this.id).child('a').on('mouseover', this._bubbleOver);
	    Ext.get(this.id).child('a').on('mouseout', this._bubbleOut);

    },

    _bubbleClick : function () {
	    // we are not in the Component, but in the <a> tag inside this
		// component.
	    // finding the adder Wrapper
	    var actionLink = Ext.getCmp(this.parent().id);
	    actionLink.onClick();
    },
    _bubbleOver : function () {
	    var actionLink = Ext.getCmp(this.parent().id);
	    actionLink.onMouseOver();
    },

    _bubbleOut : function () {
	    var actionLink = Ext.getCmp(this.parent().id);
	    actionLink.onMouseOut();
    },
    onClick : function () {
	    clog("actionLink " + this + " onClick() function is not yet implemented !");
    },
    onMouseOver : function () {
	    // To be implemented by extension
    },
    onMouseOut : function () {
	    // To be implemented by extension
    },
    toString : function () {
	    return "ActionLink " + this.text;
    }
});

