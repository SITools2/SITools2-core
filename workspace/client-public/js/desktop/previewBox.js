Ext.ns("sitools.common");

sitools.common._PreviewBox = Ext.extend(Ext.BoxComponent, {
	inited : false,
	defaultZIndex : 13000,
	defaultLeft : 0,
	defaultTop : 35,
	hideTop : 25,
	boxWidth : 250,
	cloneWinMaxWidth : 220,
	cloneWinMaxHeight : 116,
	hideDelay : 500,
	showDelay : 500,
	constructor : function() {
		sitools.common._PreviewBox.superclass.constructor.call(this, {
			renderTo : document.body,
			cls : "taskbar-previewbox",
			hidden : true
		});
		this.inited = false;
		this.hoverCount = 0
	},
	createBoxElements : function() {
		var el = this.getEl(), box;
		this.boxMl = el.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-ml"
		});
		this.boxMr = this.boxMl.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-mr"
		});
		this.boxMc = this.boxMr.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-mc"
		});
		this.arrow = el.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-arrow"
		});
		box = this.boxMc;
		this.desc = box.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-desc"
		});
		box.createChild( {
			tag : "hr"
		});
		this.win = box.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-win"
		});
		this.inited = true;
	},
//	onTaskbarClick : function() {
//		this.hideBox(true)
//	},
	showBox : function(boxConfig) {
		if (!this.isEnabled()) {
			return
		}
		this.needShowBox = true;
		this.hoverCount += 1;
		this.doShowBox.defer(300, this, [ boxConfig, this.hoverCount ]);
	},
	doShowBox : function(boxConfig, hoverCount) {
		var win, winEl, previewBox, center;
		if (!boxConfig || !boxConfig.win || !boxConfig.centerX) {
			return;
		}
		if (this.hoverCount !== hoverCount) {
			return;
		}
		if (!this.needShowBox) {
			return;
		}
		if (!this.inited) {
			this.createBoxElements()
		}
		center = Ext.isNumber(boxConfig.centerX) ? boxConfig.centerX : this.defaultLeft;
		win = boxConfig.win;
		winEl = win.getEl();

		this.desc.update(boxConfig.win.title);
		if (this.clonedEl) {
			this.clonedEl.remove()
		}
		this.clonedEl = this.getClonedEl(win);
		
		this.clonedEl.show();
		
		this.win.appendChild(this.clonedEl);
		previewBox = this.getEl();
		var top = Ext.get("ux-taskbar").getTop() - previewBox.getHeight() - 10;
		if (this.isVisible()) {
			previewBox.setTop(top);
			this.show();
			previewBox.shift( {
				left : center - (this.boxWidth / 2),
				opacity : 1,
				duration : 0.3
			})
		} else {
			previewBox.setLeftTop(center - (this.boxWidth / 2), top - 200);
			previewBox.setOpacity(0);
			this.show();
			top = Ext.get("ux-taskbar").getTop() - previewBox.getHeight() - 10;
			previewBox.shift( {
				top : top,
				opacity : 1,
				duration : 0.8
			});
		}
		this.hoverCount = 0;
	},
	hideBox : function(a) {
		if (!this.isEnabled()) {
			return
		}
		this.needShowBox = false;
		(function() {
			if (this.needShowBox) {
				return
			}
			this.doHideBox(a)
		}).defer((a === true) ? 0 : 300, this);
	},
	doHideBox : function(b) {
		var c;
		var a = function() {
			if (this.needShowBox) {
				return
			}
			this.hide();
		};
		if (this.clonedEl) {
			this.clonedEl.remove()
		}
		this.hoverCount = 0;
		if (b === true) {
			a.call(this);
			return
		}
		c = this.getEl();
		var top = Ext.get("ux-taskbar").getTop() - c.getHeight() - 20;
		
		c.shift( {
			top : top,
			opacity : 0,
			duration : 0.2,
			scope : this,
			callback : a
		});
	},
	getClonedEl : function(win) {
		var c = 0;
		var h = 0;
		var el = win.getEl();
		var newHtmlEl = el.dom.cloneNode(true);
		newHtmlEl.removeAttribute("id");
		var newEl = Ext.get(newHtmlEl);
		newEl.visibilityCls = "x-hide-display";
		newEl._previewMask = newEl.createChild( {
			tag : "div",
			cls : "taskbar-previewbox-win-mask"
		});

		var size = el.getSize();
		if (size.height === 0 && size.width === 0) {
			size = SitoolsDesk.getDesktop().getDesktopEl().getSize();
		}
		var d = this.cloneWinMaxWidth / size.width;
		c = (this.cloneWinMaxHeight - size.height * d) / 2;
		if ((size.height * d) > this.cloneWinMaxHeight) {
			d = this.cloneWinMaxHeight / size.height;
			c = 0;
			h = (this.cloneWinMaxWidth - size.width * d) / 2
		}
		d = Math.min(d, 1);
		newEl.addClass("taskbar-previewbox-win-transform");
		newEl.setStyle("-webkit-transform", String.format("scale({0})", d));
		newEl.setStyle("-moz-transform", String.format("scale({0})", d));
		newEl.setStyle("-o-transform", String.format("scale({0})", d));
		newEl.setStyle("transform", String.format("scale({0})", d));
		newEl.setLeftTop(h, c);
		return newEl;
	},
	isEnabled : function() {
		return true;
	}
});