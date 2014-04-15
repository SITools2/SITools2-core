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

Ext.namespace('sitools.user.component.dataviews.services');
/**
 * sitools.user.component.dataviews.services.sitoolsFitsService
 * @class sitools.user.component.dataviews.services.sitoolsFitsService
 * @extends Ext.Panel
 */
Ext.define('sitools.user.component.dataviews.services.sitoolsFitsService', {
    extend : 'Ext.panel.Panel',
    alias : 'sitools.user.component.dataviews.services.sitoolsFitsService',
    initComponent : function () {

        this.bodyCls = 'canvas-background';
        this.urlFits = this.record.data.fits;
        this.autoScroll = true;
        this.layout = 'fit';
        
        
        // library to display image
        this.jsFits = new FITS();
        
        this.idFitsImage = Ext.id();
        
        this.jsFits.bind("click", function (e) {
            e.y = this.height - e.y;
            var value = this.image[e.y * this.width + e.x];
            document.getElementById('status').innerHTML = 'click=(' + e.x + ',' + e.y + ')=' + value;
        }).bind("mousemove", function (e) {
            e.y = this.height - e.y;
            var value = this.image[e.y * this.width + e.x];
            document.getElementById('status').innerHTML = 'move=(' + e.x + ',' + e.y + ')=' + value;
        }).bind("load", function () {
            // diplaying frame selector if multi-frame
            var result = this.jsFits.draw(this.idFitsImage);
            if (result != true) {
                Ext.Msg.show({
                    title : result.name,
                    msg : result.message,
                    icon : Ext.MessageBox.ERROR,
                    buttons : Ext.MessageBox.OK
                });
            }
            
            if (this.jsFits.depth > 1) {
                this.sliderFrameGroupBtn.setVisible(true);
                this.sliderFrame.setMaxValue(this.jsFits.depth - 1);
            }
            
            this.getEl().unmask();
            
        }.bind(this));
        
        this.sliderTip = new Ext.slider.Tip({
            getText: function(thumb){
                return Ext.String.format(i18n.get('label.fitsFrame'), thumb.value, thumb.slider.maxValue);
            }
        });
        
        this.sliderFrame = new Ext.slider.SingleSlider({
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
            activeItem : 0,
            defaults: {
                scope : this,
                enableToogle: true,
                toggleGroup : 'functions',
                toggleHandler : this.manageFunctions,
                allowDepress : false,
                cls : 'services-toolbar-btn button-transition'
            },
            items: [{
                value : 'loglog',
                tooltip : i18n.get('label.loglog'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/log2.1.png"
            }, {
                value : 'linear',
                tooltip : i18n.get('label.linear'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/linear1.png"
            }, {
                value : 'sqrtlog',
                tooltip : i18n.get('label.squareLog'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/vlog1.png"
            }, {
                value : 'sqrt',
                tooltip : i18n.get('label.square'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/square1.png"
            }, {
                value : 'cuberoot',
                tooltip : i18n.get('label.cubeRoot'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/v3.1.png"
            }, {
                value : 'log',
                tooltip : i18n.get('label.log'),
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/log1.png",
            }]
        });
        
        this.colorsGroupBtn =  new Ext.ButtonGroup({
            activeItem : 0,
            hideBorders : true,
            defaults: {
                scope : this,
                enableToogle: true,
                toggleGroup : 'colors',
                toggleHandler : this.manageColors,
                allowDepress : false,
                cls : 'services-toolbar-btn button-transition'
            },
            items: [{
                value : 'gray',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/grey.png"
            },{
                value : 'heat',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/red.png"
            },{
                value : 'A',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/green.png"
            },{
                value : 'B',
                icon : loadUrl.get("APP_URL") + "/client-user/js/modules/sitoolsFitsViewer/images/blue.png"
            }]
        });
        
        this.tbar = {
            xtype : 'toolbar',
            autoHeight : true,
            cls : 'services-toolbar',
            enableOverflow : true,
            defaults : {
                scope : this
            },
            items : [this.functionsGroupBtn, '-' , this.colorsGroupBtn, '-', this.sliderFrameGroupBtn, '->'  /*this.histoGroupBtn*/]
        };
        
        var advancedModeAvailable = false;
        Ext.each(SitoolsDesk.app.getModules(), function(module) {
            if (module.xtype == "sitools.user.modules.sitoolsFitsMain") {
                advancedModeAvailable = true;
                this.fitsModule = module;
            }
        },this);
        
        if (advancedModeAvailable) {
            var openModuleFitsLabel = new Ext.Button({
                cls : 'link-style',
                text : i18n.get('label.openAdvancedMode'),
                scope : this,
                handler : this.openAdvancedMode
            });
            this.tbar.items.push(openModuleFitsLabel);
        }
        
        
        this.canvasPanel = new Ext.Panel({
            layout :  'fit',
//           region : 'center',
           name : 'canvasPanel',
           autoScroll : true,
           bodyCls : 'canvas-background',
           padding : '10px 15px 10px 10px',
           html : '<canvas class="shadow-canvas" style="float:left;" id="' + this.idFitsImage + '"></canvas>'
        });
        
        this.items = [this.canvasPanel/*, this.processingWindow*/, /*this.headerPanel*/];
        
        sitools.user.component.dataviews.services.sitoolsFitsService.superclass.initComponent.call(this);
    }, 
    
    afterRender : function () {
        sitools.user.component.dataviews.services.sitoolsFitsService.superclass.afterRender.apply(this, arguments);
        
        // done here because "pressed" property is Read-Only
        var btnLinear = this.functionsGroupBtn.find('value', 'linear')[0];
        btnLinear.pressed = true;
        btnLinear.addClass('sitools-btn-green-bold');
        
        var btnGray = this.colorsGroupBtn.find('value', 'gray')[0];
        btnGray.pressed = true;
        btnGray.addClass('sitools-btn-green-bold');
        
        this.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
        
        Ext.defer(function () {
            this.jsFits.load(this.urlFits, null, null, function (resp) {
                this.getEl().unmask();
                Ext.Msg.show({
                    title : i18n.get('label.error'),
                    msg : i18n.get('label.errorFitsLoading'),
                    icon : Ext.MessageBox.ERROR,
                    buttons : Ext.MessageBox.OK,
                    scope : this,
                    fn : function () {
//                        SitoolsDesk.navProfile.removeWindow(this.ownerCt);
                    }
                });
            }.bind(this));
        }, 5, this);
    },
    
    /**
     * Method called when the image is loaded. It is in charge of resizing the image according to the desktop size
     */
    onImageLoad : function () {
//        var imgTag = Ext.DomQuery.select("div[id='FITSimage'] canvas")[0];
        var imgTag = Ext.DomQuery.select("canvas")[0];
        var hi = this.body.dom.offsetHeight;
        var wi = this.body.dom.offsetWidth;
        
        var hiImg = imgTag.offsetHeight;
        var wiImg = imgTag.offsetWidth;
        
//        this.setSize(wiImg, hiImg);

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

        if (!reduce) {
            this.setSize(wiImg + this.getFrameWidth(), hiImg + this.getFrameHeight());
        } else {
            this.setSize(wi + this.getFrameWidth(), hi + this.getFrameHeight());
        }
        
        
        // if (Ext.isIE) {
        this.center();
        this.doLayout();
        // }
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
                }, function () {
                    this.canvasPanel.getEl().unmask();
                }.bind(this));
            }, 5, this);
            
        } else {
            btn.removeClass("sitools-btn-green-bold");
        }
        
//        
//        this.histogram.image.transferFn = btn.value;
//        this.histogram.compute();
//        this.histogram.draw();
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
        var arrayData = this.jsFits.update({index:newValue});
        
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
                canvas: 'processingWindow',
                nbBeans: 256
            });
            
            this.histogram.compute();
            this.histogram.draw();
        }
    },
    
    manageThresholds : function (slider, newValue, thumb) {
        this.canvasPanel.getEl().mask("Applying...");
        
        var values = slider.getValues();
        this.min = values[0];
        this.max = values[1];
        
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
            canvas: 'processingWindow',
            nbBeans: 256
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

    getCoordinateExtent : function (w, width, height) {
        var s1, s2;

        s1 = w.pix2sky(0, 0);
        s2 = w.pix2sky(width - 1, height - 1);

        return {
            ra : [ s1[0], s2[0] ],
            dec : [ s1[1], s2[1] ]
        };
    },

    addEvent : function (oElement, strEvent, fncHandler) {
        if (oElement.addEventListener)
            oElement.addEventListener(strEvent, fncHandler, false);
        else if (oElement.attachEvent)
            oElement.attachEvent("on" + strEvent, fncHandler);
    },
    
    openAdvancedMode : function () {
        var instanceFitsModule = this.fitsModule.openModule().items.items[0];
        instanceFitsModule.url = this.urlFits;
        instanceFitsModule.loadFits(this.urlFits);
        
        SitoolsDesk.navProfile.removeWindow(this.ownerCt);
    }
    
});

sitools.user.component.dataviews.services.sitoolsFitsService.getDefaultParameters = function() {
    return [{
                name : "featureType",
                value : "Image"
            }, {
                name : "columnAlias",
                value : ""
    }];
};

sitools.user.component.dataviews.services.sitoolsFitsService.getParameters = function() {
    return [{
        jsObj : "Ext.form.ComboBox",
        config : {
            fieldLabel : i18n.get('headers.previewUrl'),
            width : 200,
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            valueField : 'display',
            displayField : 'display',
            value : 'Image',
            store : new Ext.data.ArrayStore({
                        autoLoad : true,
                        fields : ['value', 'display', 'tooltip'],
                        data : [
                                ['', ''],
                                ['Image', 'Image',
                                        i18n.get("label.image.tooltip")],
                                ['URL', 'URL', i18n.get("label.url.tooltip")],
                                ['DataSetLink', 'DataSetLink',
                                        i18n.get("label.datasetlink.tooltip")]]
                    }),
            listeners : {
                scope : this,
                change : function(combo, newValue, oldValue) {
                }
            },
            name : "featureType",
            id : "featureType"
        }
    }, {
        jsObj : "Ext.form.ComboBox",
        config : {
            fieldLabel : i18n.get('label.columnImage'),
            width : 200,
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            store : new Ext.data.JsonStore({
                        fields : ['columnAlias'],
                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                        root : "dataset.columnModel",
                        autoLoad : true,
                        listeners : {
                            load : function(store) {
                                store.add({
                                    columnAlias : ""
                                });
                            }

                        }
                    }),
            valueField : 'columnAlias',
            displayField : 'columnAlias',
            listeners : {
                render : function(c) {
                    Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.columnImageTooltip')
                            });
                }
            },
            name : "columnAlias",
            id : "columnAlias",
            value : ""
        }
    }];
};

sitools.user.component.dataviews.services.sitoolsFitsService.executeAsService = function(config) {
    
    var selected;
    if (Ext.isEmpty(config.record)) {
        selected = config.dataview.getSelections()[0];
    } else {
        selected = config.record;
    }
    
    if (Ext.isEmpty(selected.data.fits)) {
        return Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.noFitsFile'));
    }
    
    config.record = selected;
    
    var jsObj = sitools.user.component.dataviews.services.sitoolsFitsService;
    
    var windowConfig = {
        layout : 'fit',
        title : i18n.get('label.fitsViewer'),
        bodyCls : 'canvas-background',
        iconCls : 'fitsService',
        autoScroll : true,
        saveToolbar : true,
    };
    SitoolsDesk.addDesktopWindow(windowConfig, config, jsObj);
};
