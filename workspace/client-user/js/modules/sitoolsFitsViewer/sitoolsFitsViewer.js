/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/

//Ext.namespace('sitools.user.modules');
/**
 * sitools.user.modules.sitoolsFitsViewer
 * @class sitools.user.modules.sitoolsFitsViewer
 * @extends Ext.Panel
 */
sitools.user.modules.sitoolsFitsViewer = Ext.extend(Ext.Panel, {
    bodyBorder : false,
    layout : 'border',
    id : 'sitoolsFitsViewer',
    initComponent : function () {
        
        this.sliderTip = new Ext.slider.Tip({
            getText: function(thumb){
                return String.format(i18n.get('label.fitsFrame'), thumb.value, thumb.slider.maxValue);
            }
        });
        
        this.sliderFrame = new Ext.slider.SingleSlider({
            title: i18n.get('label.frames'),
            height : 30,
            width: 100,
            value: 0,
            increment: 1,
            minValue: 0,
            maxValue: 1,
            plugins: this.sliderTip,
            listeners : {
                scope : this,
                change : this.manageFrames
            }
        });
        
        this.sliderFrameGroupBtn = new Ext.ButtonGroup({
            title: 'Frames',
            hidden : true,
            items : [this.sliderFrame]
        });
        
        this.functionsGroupBtn = new Ext.ButtonGroup({
            title: i18n.get('label.functions'),
            defaults: {
                scope : this,
                enableToogle: true,
                toggleGroup : 'functions',
                toggleHandler : this.manageFunctions,
                allowDepress : false,
                cls : 'services-toolbar-btn'
            },
            items: [{
                value : 'loglog',
                text : i18n.get('label.loglog'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/log2.1.png"
            }, {
                value : 'linear',
                text : i18n.get('label.linear'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/linear1.png"
            }, {
                value : 'sqrtlog',
                text : i18n.get('label.squareLog'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/vlog1.png"
            }, {
                value : 'sqrt',
                text : i18n.get('label.square'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/square1.png"
            }, {
                value : 'cuberoot',
                text : i18n.get('label.cubeRoot'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/v3.1.png"
            }, {
                value : 'log',
                text : i18n.get('label.log'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/log1.png"
            }]
        });
        
        this.colorsGroupBtn =  new Ext.ButtonGroup({
            title: i18n.get('label.colors'),
            activeItem : 0,
            hideBorders : true,
            defaults: {
                scope : this,
                enableToogle: true,
                toggleGroup : 'colors',
                toggleHandler : this.manageColors,
                allowDepress : false,
                cls : 'services-toolbar-btn'
            },
            items: [{
                text: i18n.get('label.gray'),
                value : 'gray',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/grey.png"
            },{
                text: i18n.get('label.red'),
                value : 'heat',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/red.png"
            },{
                text: i18n.get('label.green'),
                value : 'A',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/green.png"
            },{
                text: i18n.get('label.blue'),
                value : 'B',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/blue.png"
            }]
        });
        
        this.histoGroupBtn = new Ext.Button({
            title: i18n.get('label.processing'),
            hideBorders : true,
            scope : this,
            enableToogle: true,
            handler : this.manageHistogram,
            text : 'Histogram',
            id : 'histoBtnId',
            icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/function.png",
            cls : 'sitools-btn-green-bold',
            pressed : true
        });
        
        this.tbar = {
            xtype : 'toolbar',
            autoHeight : true,
            cls : 'services-toolbar',
            enableOverflow : true,
            defaults : {
                scope : this
            },
            items : [this.sliderFrameGroupBtn, this.functionsGroupBtn, this.colorsGroupBtn, '-' , this.histoGroupBtn]
        };
        
        this.canvasPanel = new Ext.Panel({
           region : 'center',
           name : 'canvasPanel',
           autoScroll : true,
           bodyCssClass : 'canvas-background',
           padding : '10px 15px 10px 10px',
           html : '<canvas class="shadow-canvas" style="float:left;" id="FITSimage"></canvas>'
        });
        
        // Value define later
        this.thresoldSlider = new Ext.slider.MultiSlider({
            width   : 214,
            minValue: 0,
            maxValue: 0,
            values  : [0, 0],
            fieldLabel : "Thresholds",
            plugins : new Ext.slider.Tip(),
            listeners : {
                scope : this,
                changecomplete : this.manageThresholds
            }
        });
        
        this.thresoldLabel = new Ext.form.Label({
            text : i18n.get('label.manageThreshold'),
            style : 'padding : 0px 7px 0px 0px; font-weight:bold;'
        });
        
        this.processingWindow = new Ext.Window({
            title : 'Histogram',
            padding : '30px',
            bodyCssClass : 'histogram-background',
            autoWidth : true,
            autoHeight : true,
            closable : false,
            name : 'processingWindow',
            html : '<canvas class="noSelect" style="float:left;" id="processingWindow"></canvas>',
            tbar : {
                autoHeight : true,
                cls : 'services-toolbar',
                defaults : {
                    scope : this
                },
                items : [this.thresoldLabel, this.thresoldSlider]
            },
            listeners : {
                scope : this,
                afterrender : function (window) {
                    var x = this.getInnerWidth() - window.getWidth() - 20;
                    this.processingWindow.setPosition(x, 20);
                    this.processingWindow.render(this.canvasPanel.getEl());
                    this.processingWindow.show();
                }
            }
        });
        
        this.headerPanel = new sitools.user.modules.sitoolsFitsHeader({
            title : i18n.get('label.headerData'),
            headerData : this.headerData,
            region : 'south',
            height : 260,
            collapsible : true
        });
        
        this.items = [this.canvasPanel, this.processingWindow, this.headerPanel];
        
        sitools.user.modules.sitoolsFitsViewer.superclass.initComponent.call(this);
    }, 
    
    afterRender : function () {
        sitools.user.modules.sitoolsFitsViewer.superclass.afterRender.apply(this, arguments);
        
     // library to display image
        this.jsFits = new FITS();
        
        this.jsFits.bind("load", function () {
            if (this.jsFits.depth > 1) {
                this.sliderFrameGroupBtn.setVisible(true);
            }
            this.sliderFrame.setMaxValue(this.jsFits.depth - 1);
            
            var result = this.jsFits.draw("FITSimage");
            
            if (result != true) {
                Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    msg : result,
                    icon : Ext.MessageBox.ERROR,
                    buttons : Ext.MessageBox.OK
                });
            }
        }.bind(this));
        
        var btnLinear = this.functionsGroupBtn.find('value', 'linear')[0];
        btnLinear.pressed = true;
        btnLinear.addClass('sitools-btn-green-bold');
        
        var btnGray = this.colorsGroupBtn.find('value', 'gray')[0];
        btnGray.pressed = true;
        btnGray.addClass('sitools-btn-green-bold');
        
        // Load an initial FITS file
//        this.jsFits.viewer = this;
        
        this.jsFits.load(this.urlFits, null, function (fits) {
            var allWcsCards = this.fits.hdus[0].header.cards;
            var wcsCards = {
                    SIMPLE : "T",
                    NAXIS : 2,
                    BITPIX: (allWcsCards.BITPIX) ? allWcsCards.BITPIX[1] : null,
                    BUNIT: (allWcsCards.BUNIT) ? allWcsCards.BUNIT[1] : null,
                    NAXIS1 : (allWcsCards.NAXIS1) ? allWcsCards.NAXIS1[1] : null,
                    NAXIS2 : (allWcsCards.NAXIS2) ? allWcsCards.NAXIS2[1] : null,
                    CRPIX1 : (allWcsCards.CRPIX1) ? allWcsCards.CRPIX1[1] : null,
                    CRPIX2 : (allWcsCards.CRPIX2) ? allWcsCards.CRPIX2[1] : null,
                    CRVAL1 : (allWcsCards.CRVAL1) ? allWcsCards.CRVAL1[1] : null,
                    CRVAL2 : (allWcsCards.CRVAL2) ? allWcsCards.CRVAL2[1] : null,
                    CDELT1 : (allWcsCards.CDELT1) ? allWcsCards.CDELT1[1] : null,
                    CDELT2 : (allWcsCards.CDELT2) ? allWcsCards.CDELT2[1] : null,
                    CD1_1 : (allWcsCards.CD1_1) ? allWcsCards.CD1_1[1] : null,
                    CD1_2 : (allWcsCards.CD1_2) ? allWcsCards.CD1_2[1] : null,
                    CD2_1 : (allWcsCards.CD2_1) ? allWcsCards.CD2_1[1] : null,
                    CD2_2 : (allWcsCards.CD2_2) ? allWcsCards.CD2_2[1] : null,
                    CTYPE1 : (allWcsCards.CTYPE1) ? allWcsCards.CTYPE1[1] : null,
                    CTYPE2 : (allWcsCards.CTYPE2) ? allWcsCards.CTYPE2[1] : null,
                    LONPOLE : (allWcsCards.LONPOLE) ? allWcsCards.LONPOLE[1] : null,
                    LATPOLE : (allWcsCards.LATPOLE) ? allWcsCards.LATPOLE[1] : null,
                    BMAJ : (allWcsCards.BMAJ) ? allWcsCards.BMAJ[1] : null,
                    BMIN : (allWcsCards.BMIN) ? allWcsCards.BMIN[1] : null,
                    BPA : (allWcsCards.BPA) ? allWcsCards.BPA[1] : null,
                    RESTFRQ : (allWcsCards.RESTFRQ) ? allWcsCards.RESTFRQ[1] : null,
                    EQUINOX : (allWcsCards.EQUINOX) ? allWcsCards.EQUINOX[1] : null
            };
            
            var wcsCardsSorted = {};
            Ext.iterate(wcsCards, function (key, value) {
                if (value != null) {
                    wcsCardsSorted[key] = value;
                }
            });
            
            var header = this.toHeader(wcsCardsSorted);
            
            this.w = new wcs();
            this.w.init(header);
            this.world = this.w.pix2sky(0, 0);
            
            this.tipRaDec = new Ext.ToolTip({
                target : this.jsFits.canvas,
                dismissDelay : 0,
                hideDelay : 0,
                showDelay : 0,
                trackMouse : true,
                autoShow : true,
                minWidth : 110,
                text : 'Ra :  Dec :',
                templ : 'Ra : <b>{0}</b> <br> Dec : <b>{1}</b>',
                html: 'Ra :  Dec :'
            });
            
            // Scope = canvas
            if (!Ext.isEmpty(this.jsFits.canvas)) {
                this.addEvent(this.jsFits.canvas, "mousemove", function(e) {
                    var world = this.w.pix2sky(e.offsetX, this.jsFits.canvas.height - e.offsetY);
                    
                    this.tipRaDec.update(String.format(this.tipRaDec.templ, world[0].toFixed(6), world[1].toFixed(6)));
                        this.tipRaDec.doLayout();
                    
                }.bind(this));
            }
            
            this.initHistogram(this.fits);
            this.fitsMainPanel.getEl().unmask();
        }.bind(this));
        
            
    },
    
    manageFunctions : function (btn, pressed) {
        
        if (pressed) {
            btn.addClass("sitools-btn-green-bold");
            this.canvasPanel.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
            
            Ext.defer(function () {
                this.jsFits.update({
                    stretch : btn.value,
                    min : this.min,
                    max : this.max
                });
                
                this.histogram.image.transferFn = btn.value;
                this.histogram.compute();
                this.histogram.draw();
                this.canvasPanel.getEl().unmask();
                
            }, 5, this);
            
        } else {
            btn.removeClass("sitools-btn-green-bold");
        }
        
    },
    
    manageColors : function (btn, pressed) {
        if (pressed) {
            btn.addClass("sitools-btn-green-bold");
            this.canvasPanel.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
            
            Ext.defer(function () {
                this.jsFits.update({
                    color:btn.value,
                    min : this.min,
                    max : this.max
                }, function () {
                    this.canvasPanel.getEl().unmask();
                }.bind(this));
            }, 5, this);
            
        } else {
            btn.removeClass("sitools-btn-green-bold");
        }
        
        
    },
    
    manageFrames : function (slider, newValue, thumb) {
        this.jsFits.update({index:newValue});
        var arrayData = this.jsFits.ctx.getImageData();
        
        if (!Ext.isEmpty(this.histogram)) {
            
            this.histogram.image.frame = newValue;
            this.histogram.image.pixels = arrayData.data;
//            this.histogram.image.pixels = new Float32Array(this.fitsData.view.buffer, begin, this.fitsData.length / 4);
            
            this.image.width = this.fitsData.width;
            this.image.height = this.fitsData.height;
            this.image.transferFn = "log";
            this.image.inverse = false;
            
            this.image.min = this.image.tmin;
            this.image.max = this.image.tmax;
            
            this.histogram.updateThreshold(this.image.min, this.image.max);
            
//            this.computeMinMax(this.image.pixels, this.image);
            
            this.histogram = Histogram.init({
                viewer : this,
                jsFits : this.jsFits,
                image : this.image,
                canvas: 'processingWindow'
            });
            
            this.histogram.compute();
            this.histogram.draw();
        }
    },
    
    manageThresholds : function (slider, newValue, thumb) {
        this.canvasPanel.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
        
        Ext.defer(function () {
            var values = slider.getValues();
            this.min = values[0];
            this.max = values[1];
            
            this.histogram.updateThreshold(this.min, this.max);
            this.canvasPanel.getEl().unmask();
            
        }, 5, this);
    },
    
    manageHistogram : function (btn) {
        if (btn.pressed) {
            btn.removeClass("sitools-btn-green-bold");
            btn.pressed = false;
        } else {
            btn.addClass("sitools-btn-green-bold");
            btn.pressed = true;
        }
        
        var btnEl = btn.getEl();
        if (this.processingWindow.isVisible()) {
            this.processingWindow.hide(btnEl);
        } else {
            this.processingWindow.show(btnEl);
        }
    },
    
    initHistogram : function (fits) {
        this.fitsData = fits.getHDU().data;
        this.image = {};
        this.image.pixels = new Float32Array(this.fitsData.view.buffer, this.fitsData.begin, this.fitsData.length / 4);
       
        this.image.width = this.fitsData.width;
        this.image.height = this.fitsData.height;
        this.image.transferFn = "log";
        this.image.inverse = false;
        this.image.updateColormap = this.updateColormap;
        
        this.computeMinMax(this.image.pixels, this.image);
        
        
        this.histogram = Histogram.init({
            viewer : this,
            jsFits : this.jsFits,
            image : this.image,
            vec3 : new vec3(),
            canvas: 'processingWindow'
        });
        
        this.histogram.compute();
        this.histogram.draw();
    },
    
    updateColormap : function(transferFn, colormap, inverse) {
        if ( transferFn != "raw" )
        {
            var colorMap = new ColorMap();
            this.colormapTex = colorMap.generateColormap(null, transferFn, colormap, inverse);
        }
        else
        {
            this.fragmentCode = null;
            this.updateUniforms = null;
        }
        this.transferFn = transferFn;
        this.inverse = inverse;
        
        return this.colormapTex;
    },
    
    computeMinMax : function (pixels, image)
    {
        var max = Number.MIN_VALUE;
        var min = Number.MAX_VALUE;
        for ( var i=1; i<pixels.length; i++ )
        {
            var val = pixels[i];
            if ( isNaN(val) )
                continue;
            if ( max < val )
                max = val;
            if ( min > val )
                min = val;
        }
        image.min = min;
        image.max = max;
        image.tmax = max;
        image.tmin = min;
        
        this.thresoldSlider.setMinValue(min);
        this.thresoldSlider.setValue(0, min, true);
        
        
        this.thresoldSlider.setMaxValue(max);
        this.thresoldSlider.setValue(1, max, true);
    },
    
    toHeader : function (wcsObj) {
        String.prototype.splice = function (index, remove, string) {
            return (this.slice(0, index) + string + this.slice(index + Math.abs(remove)));
        };
        
        var header = [];

        line = "                                                                                ";
        for ( var card in wcsObj) {
            var value = wcsObj[card];

            entry = line.splice(0, card.length, card);
            entry = entry.splice(8, 1, "=");

            entry = entry.splice(10, value.toString().length, value);
            header.push(entry);
        }

        return header.join('\n');
    },

    addEvent : function (oElement, strEvent, fncHandler) {
        if (oElement.addEventListener)
            oElement.addEventListener(strEvent, fncHandler, false);
        else if (oElement.attachEvent)
            oElement.attachEvent("on" + strEvent, fncHandler);
    }
    
});
