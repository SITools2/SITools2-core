/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n,document*/
/*!
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/**
 * @class Ext.ux.TaskBar
 * @extends Ext.util.Observable
 */
Ext.ux.TaskBar = function (app, enableWarning) {
	this.app = app;
	this.init(enableWarning);
};

Ext.extend(Ext.ux.TaskBar, Ext.util.Observable, {
    init : function (enableWarning) {
//	    this.startMenu = new Ext.ux.StartMenu(Ext.apply({
//	        iconCls : 'user',
//	        height : 300,
//	        shadow : true,
//	        title : 'Jack Slocum',
//	        width : 300
//	    }, this.app.startConfig));

//	    this.startBtn = new Ext.Button({
//	        text : i18n.get('label.start'),
//	        id : 'ux-startbutton',
//	        iconCls : 'start',
//	        menu : this.startMenu,
//	        menuAlign : 'bl-tl',
//	        renderTo : 'ux-taskbar-start',
//	        clickEvent : 'mousedown',
//	        template : new Ext.Template('<table cellspacing="0" class="x-btn"><tbody class="{1}"><tr>',
//	                '<td class="ux-startbutton-left"><i>&#160;</i></td>',
//	                '<td class="ux-startbutton-center"><em class="{2} unselectable="on">',
//	                '<button class="x-btn-text" type="{0}" style="height:30px;"></button>', '</em></td>',
//	                '<td class="ux-startbutton-right"><i>&#160;</i></td>', '</tr></tbody></table>')
//	    });

//	    var width = this.startBtn.getEl().getWidth() + 10;

//	    var sbBox = new Ext.BoxComponent({
//	        el : 'ux-taskbar-start',
//	        id : 'TaskBarStart',
//	        minWidth : width,
//	        region : 'west',
//	        split : true,
//	        width : width
//	    });

        this.staticButtonPanel = new Ext.ux.TaskButtonsPanel({
            el : 'ux-staticbuttons-panel',
            id : 'TaskBarStaticButtons',
            region : 'center',
            style : 'overflow:hidden !important; height: 30px; width: 100% !important;', 
            listeners : {
        		addNewWindow : function () {
        			this.items[0].action = "minimize";
        		}
        	}
        });
        
	    this.tbPanel = new Ext.ux.TaskButtonsPanel({
	        el : 'ux-taskbuttons-panel',
	        id : 'TaskBarButtons',
	        region : 'center',
            style : 'overflow:hidden !important', 
            enableWarning : enableWarning
	    });

	    var container = new Ext.ux.TaskBarContainer({
	        el : 'ux-taskbar',
	        layout : 'border',
	        items : [ this.staticButtonPanel, this.tbPanel ]
	    });
	    
	    return this;
    },

    addTaskButton : function (win) {
	    var btn = this.tbPanel.addButton(win, 'ux-taskbuttons-panel');
	    this.setActiveButton(btn);
	    return btn;
	    this.staticButtonPanel.fireEvent("addNewWindow");
    },

    removeTaskButton : function (btn) {
	    this.tbPanel.removeButton(btn);
    },

    setActiveButton : function (btn) {
	    this.tbPanel.setActiveButton(btn);
    }, 
    getActiveButton : function () {
	    return this.tbPanel.getActiveButton();
    }, 

    getPreviousBtn : function (btn) {
    	return this.tbPanel.getPreviousBtn(btn);
    }, 
    getNextBtn : function (btn) {
    	return this.tbPanel.getNextBtn(btn);
    }, 
    getAllTaskButtons : function () {
    	return this.tbPanel.items;
    }, 
    setEnableWarning : function (enableWarning) {
    	this.tbPanel.enableWarning = enableWarning;
    }
});

/**
 * @class Ext.ux.TaskBarContainer
 * @extends Ext.Container
 */
Ext.ux.TaskBarContainer = Ext.extend(Ext.Container, {
    initComponent : function () {
	    Ext.ux.TaskBarContainer.superclass.initComponent.call(this);

	    this.el = Ext.get(this.el) || Ext.getBody();
	    this.el.setHeight = Ext.emptyFn;
	    this.el.setWidth = Ext.emptyFn;
	    this.el.setSize = Ext.emptyFn;
	    this.el.setStyle({
	        overflow : 'hidden',
	        margin : '0',
	        border : '0 none'
	    });
	    this.el.dom.scroll = 'no';
	    this.allowDomMove = false;
	    this.autoWidth = true;
	    this.autoHeight = true;
	    Ext.EventManager.onWindowResize(this.fireResize, this);
	    this.renderTo = this.el;
	    this.listeners = {
    		maximizeDesktop : this.onMaximizeDesktop
    	}
    },

    fireResize : function (w, h) {
	    this.onResize(w, h, w, h);
	    this.fireEvent('resize', this, w, h, w, h);
    }, 
    onMaximizeDesktop : function () {
    }
});

/**
 * @class Ext.ux.TaskButtonsPanel
 * @extends Ext.BoxComponent
 */
Ext.ux.TaskButtonsPanel = Ext.extend(Ext.BoxComponent, {
    activeButton : null,
    enableScroll : true,
    scrollIncrement : 0,
    scrollRepeatInterval : 400,
    scrollDuration : .35,
    animScroll : true,
    resizeButtons : false,
    buttonWidth : 168,
    minButtonWidth : 118,
    buttonMargin : 2,
    buttonWidthSet : false,

    initComponent : function () {
	    Ext.ux.TaskButtonsPanel.superclass.initComponent.call(this);
	    this.on('resize', this.delegateUpdates);
	    this.items = [];

	    this.stripWrap = Ext.get(this.el).createChild({
	        cls : 'ux-taskbuttons-strip-wrap',
	        cn : {
	            tag : 'ul',
	            cls : 'ux-taskbuttons-strip'
	        }
	    });
	    this.stripSpacer = Ext.get(this.el).createChild({
		    cls : 'ux-taskbuttons-strip-spacer'
	    });
	    this.strip = new Ext.Element(this.stripWrap.dom.firstChild);

	    this.edge = this.strip.createChild({
	        tag : 'li',
	        cls : 'ux-taskbuttons-edge'
	    });
	    this.strip.createChild({
		    cls : 'x-clear'
	    });
	    if (this.enableWarning) {
		    this.warningCt = this.edge.createChild({
		        tag : 'div', 
		        cls : "ux-taskbar-warningCt"
		    });
	    }
    },

    addButton : function (win) {
	    var li = this.strip.createChild({
		    tag : 'li'
	    }, this.edge); // insert before the edge
	    var btn = new Ext.ux.TaskBar.TaskButton(win, li);

	    this.items.push(btn);

	    if (!this.buttonWidthSet) {
		    this.lastButtonWidth = btn.container.getWidth();
	    }

	    this.setActiveButton(btn);
	    return btn;
    },
    
    addStaticButton : function (button) {
        var li = this.strip.createChild({
            tag : 'li'
        }, this.edge); // insert before the edge
        button.render(li);
        this.items.push(button);

    },
    
    removeButton : function (btn) {
	    var li = document.getElementById(btn.container.id);
	    Ext.fly(li).ghost('b', {
	    	duration : .5,
	    	remove : true, 
	    	callback : function () {
	    		btn.destroy();
	    	}
	    });
	    

	    var s = [];
	    for (var i = 0, len = this.items.length; i < len; i++) {
		    if (this.items[i] != btn) {
			    s.push(this.items[i]);
		    }
	    }
	    this.items = s;

	    this.delegateUpdates();
    },

    setActiveButton : function (btn) {
    	if (this.activeButton) {
    		Ext.fly(this.activeButton.el).removeClass('active-win');
        }
    	Ext.fly(btn.el).addClass('active-win');
		
    	this.activeButton = btn;
	    this.delegateUpdates();
    },
    getActiveButton : function() {
    	return this.activeButton;
    },
    getNextBtn : function (btn) {
    	var result;
    	try {
        	for (var i = 0, len = this.items.length; i < len; i++) {
    		    if (this.items[i] == btn) {
    			    result = this.items[i+1];
    		    }
    	    }
        	return result;
    	}
    	catch (err) {
    		return null;
    	}
    }, 
    
    getPreviousBtn : function (btn) {
    	var result;
    	try {
        	for (var i = 0, len = this.items.length; i < len; i++) {
    		    if (this.items[i] == btn) {
    			    result = this.items[i-1];
    		    }
    	    }
        	return result;
    	}
    	catch (err) {
    		return null;
    	}
    }, 
    
    delegateUpdates : function () {
	    /*
		 * if(this.suspendUpdates){ return; }
		 */
	    if (this.resizeButtons && this.rendered) {
		    this.autoSize();
	    }
	    if (this.enableScroll && this.rendered) {
		    this.autoScroll();
	    }
	    if (this.enableWarning) {
		    if (this.items.length > 10) {
		    	this.removeButton(this.items[0]);
		    }
		    if (this.items.length == 10) {
		    	this.warningCt.addClass('x-warning');
		        this.warningTT = new Ext.ToolTip({
		            target: this.warningCt,
		            width: 200,
		            anchor : "bottom", 
		            cls : "x-form-invalid-tip", 
		            html : i18n.get('label.tooManyPanelsOpened'), 
		            dismissDelay: 5000 // auto hide after 15 seconds
		        });

		    }
		    else {
		    	if (this.warningTT) {
		    		this.warningTT.destroy();
		    	}
		    	this.warningCt.removeClass("x-warning");
		    }
	    }
    },

    autoSize : function () {
	    var count = this.items.length;
	    var ow = this.el.dom.offsetWidth;
	    var aw = this.el.dom.clientWidth;

	    if (!this.resizeButtons || count < 1 || !aw) { // !aw for display:none
		    return;
	    }

	    var each = Math.max(Math.min(Math.floor((aw - 4) / count) - this.buttonMargin, this.buttonWidth),
	            this.minButtonWidth); // -4 for float errors in IE
	    var btns = this.stripWrap.dom.getElementsByTagName('button');

	    this.lastButtonWidth = Ext.get(btns[0].id).findParent('li').offsetWidth;

	    for (var i = 0, len = btns.length; i < len; i++) {
		    var btn = btns[i];

		    var tw = Ext.get(btns[i].id).findParent('li').offsetWidth;
		    var iw = btn.offsetWidth;

		    btn.style.width = (each - (tw - iw)) + 'px';
	    }
    },

    autoScroll : function () {
	    var count = this.items.length;
	    var ow = this.el.dom.offsetWidth;
	    var tw = this.el.dom.clientWidth;

	    var wrap = this.stripWrap;
	    var cw = wrap.dom.offsetWidth;
	    var pos = this.getScrollPos();
	    var l = this.edge.getOffsetsTo(this.stripWrap)[0] + pos;

	    if (!this.enableScroll || count < 1 || cw < 20) { // 20 to prevent
															// display:none
															// issues
		    return;
	    }

	    wrap.setWidth(tw); // moved to here because of problem in Safari

	    if (l <= tw) {
		    wrap.dom.scrollLeft = 0;
		    // wrap.setWidth(tw); moved from here because of problem in Safari
		    if (this.scrolling) {
			    this.scrolling = false;
			    this.el.removeClass('x-taskbuttons-scrolling');
			    this.scrollLeft.hide();
			    this.scrollRight.hide();
		    }
	    } else {
		    if (!this.scrolling) {
			    this.el.addClass('x-taskbuttons-scrolling');
		    }
		    tw -= wrap.getMargins('lr');
		    wrap.setWidth(tw > 20 ? tw : 20);
		    if (!this.scrolling) {
			    if (!this.scrollLeft) {
				    this.createScrollers();
			    } else {
				    this.scrollLeft.show();
				    this.scrollRight.show();
			    }
		    }
		    this.scrolling = true;
		    if (pos > (l - tw)) { // ensure it stays within bounds
			    wrap.dom.scrollLeft = l - tw;
		    } else { // otherwise, make sure the active button is still
						// visible
			    this.scrollToButton(this.activeButton, true); // true to
																// animate
		    }
		    this.updateScrollButtons();
	    }
    },

    createScrollers : function () {
	    var h = this.el.dom.offsetHeight; // var h =
											// this.stripWrap.dom.offsetHeight;

	    // left
	    var sl = this.el.insertFirst({
		    cls : 'ux-taskbuttons-scroller-left'
	    });
	    sl.setHeight(h);
	    sl.addClassOnOver('ux-taskbuttons-scroller-left-over');
	    this.leftRepeater = new Ext.util.ClickRepeater(sl, {
	        interval : this.scrollRepeatInterval,
	        handler : this.onScrollLeft,
	        scope : this
	    });
	    this.scrollLeft = sl;

	    // right
	    var sr = this.el.insertFirst({
		    cls : 'ux-taskbuttons-scroller-right'
	    });
	    sr.setHeight(h);
	    sr.addClassOnOver('ux-taskbuttons-scroller-right-over');
	    this.rightRepeater = new Ext.util.ClickRepeater(sr, {
	        interval : this.scrollRepeatInterval,
	        handler : this.onScrollRight,
	        scope : this
	    });
	    this.scrollRight = sr;
    },

    getScrollWidth : function () {
	    return this.edge.getOffsetsTo(this.stripWrap)[0] + this.getScrollPos();
    },

    getScrollPos : function () {
	    return parseInt(this.stripWrap.dom.scrollLeft, 10) || 0;
    },

    getScrollArea : function () {
	    return parseInt(this.stripWrap.dom.clientWidth, 10) || 0;
    },

    getScrollAnim : function () {
	    return {
	        duration : this.scrollDuration,
	        callback : this.updateScrollButtons,
	        scope : this
	    };
    },

    getScrollIncrement : function () {
	    return (this.scrollIncrement || this.lastButtonWidth + 2);
    },

    /*
	 * getBtnEl : function(item){ return document.getElementById(item.id); },
	 */

    scrollToButton : function (item, animate) {
	    item = item.el.dom.parentNode; // li
	    if (!item) {
		    return;
	    }
	    var el = item; // this.getBtnEl(item);
	    var pos = this.getScrollPos(), area = this.getScrollArea();
	    var left = Ext.fly(el).getOffsetsTo(this.stripWrap)[0] + pos;
	    var right = left + el.offsetWidth;
	    if (left < pos) {
		    this.scrollTo(left, animate);
	    } else if (right > (pos + area)) {
		    this.scrollTo(right - area, animate);
	    }
    },

    scrollTo : function (pos, animate) {
	    this.stripWrap.scrollTo('left', pos, animate ? this.getScrollAnim() : false);
	    if (!animate) {
		    this.updateScrollButtons();
	    }
    },

    onScrollRight : function () {
	    var sw = this.getScrollWidth() - this.getScrollArea();
	    var pos = this.getScrollPos();
	    var s = Math.min(sw, pos + this.getScrollIncrement());
	    if (s != pos) {
		    this.scrollTo(s, this.animScroll);
	    }
    },

    onScrollLeft : function () {
	    var pos = this.getScrollPos();
	    var s = Math.max(0, pos - this.getScrollIncrement());
	    if (s != pos) {
		    this.scrollTo(s, this.animScroll);
	    }
    },

    updateScrollButtons : function () {
	    var pos = this.getScrollPos();
	    this.scrollLeft[pos === 0 ? 'addClass' : 'removeClass']('ux-taskbuttons-scroller-left-disabled');
	    this.scrollRight[pos >= (this.getScrollWidth() - this.getScrollArea()) ? 'addClass' : 'removeClass']
	            ('ux-taskbuttons-scroller-right-disabled');
    }
});

/**
 * @class Ext.ux.TaskBar.TaskButton
 * @extends Ext.Button
 */
Ext.ux.TaskBar.TaskButton = function (win, el) {
	this.win = win;
	var iconCls = win.iconCls;
	if(Ext.isEmpty(iconCls)){
		iconCls = 'smallLogo';
	}
	
	
	Ext.ux.TaskBar.TaskButton.superclass.constructor.call(this, {
	    iconCls : iconCls,
//	    text : Ext.util.Format.ellipsis(win.title, 12),
	    renderTo : el,
	    handler : function (btn) {
			SitoolsDesk.navProfile.taskbar.handleTaskButton.call(this, btn);
		},
//	    clickEvent : 'mousedown',
	    template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
	            '<td class="ux-taskbutton-center"><em class="{5} unselectable="on">',
	            '<button class="x-btn-text {2}" type="{1}" style="height:28px;">{0}</button>', '</em></td>',
	            "</tr></tbody></table>")
	});
};

Ext.extend(Ext.ux.TaskBar.TaskButton, Ext.Button, {
	scale : "medium", 
	width : 50, 
	initButtonEl : function() {
		Ext.ux.TaskBar.TaskButton.superclass.initButtonEl.apply(this, arguments);
//		this.mon(this.el, "contextmenu", this.onContextMenu, this);
		this.mon(this.el, "mouseover", this.onMouseOverHandler, this);
		this.mon(this.el, "mouseout", this.onMouseOutHandler, this);
		
	},
    onRender : function () {
	    Ext.ux.TaskBar.TaskButton.superclass.onRender.apply(this, arguments);

	    this.cmenu = new Ext.menu.Menu({
		    items : SitoolsDesk.navProfile.taskbar.getContextMenuItems.call(this)
	    });

	    this.cmenu.on('beforeshow', function () {
	    	SitoolsDesk.navProfile.taskbar.beforeShowCtxMenu.call(this);
	    }, this);

	    this.el.on('contextmenu', function (e) {
		    e.stopEvent();
		    if (!this.cmenu.el) {
			    this.cmenu.render();
		    }
		    var xy = e.getXY();
		    xy[1] -= this.cmenu.el.getHeight();
		    this.cmenu.showAt(xy);
	    }, this);
    },
	
    onMouseOverHandler : function(d) {
		if (this.state === "normal") {
			return;
		}

		var ca = this.container.getAlignToXY(this.container, "?");
		var cw = this.container.getWidth();
		var boxConfig = {
			win : this.win,
			centerX : ca[0] + (cw / 2)
		};
		sitools.PreviewBox.showBox(boxConfig);
	},
	onMouseOutHandler : function(a) {
		sitools.PreviewBox.hideBox();
	}, 

    closeWin : function (cMenu, e, win) {
		SitoolsDesk.navProfile.taskbar.closeWin.call(this, cMenu, e, win);
    }
});
