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

Ext.namespace('sitools.extension.view.component.datasets.services.fitsService');
/**
 * Open images inside a fits file
 * @class sitools.extension.view.component.datasets.services.FitsServiceView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.extension.view.component.datasets.services.fitsService.FitsServiceView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.fitsServiceView',

    border: false,
    header : false,

    initComponent: function () {

        //this.i18nFitsService = I18nRegistry.retrieve('fitsService');

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
                    title: result.name,
                    msg: result.message,
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.MessageBox.OK
                });
            }

            if (this.jsFits.depth > 1) {
                this.sliderFrameGroupBtn.setVisible(true);
                this.sliderFrame.setMaxValue(this.jsFits.depth - 1);
            }

            this.getEl().unmask();
        }.bind(this));

        this.sliderTip = Ext.create('Ext.slider.Tip', {
            i18nFitsService: this.i18nFitsService,
            getText: function (thumb) {
                return Ext.String.format(this.i18nFitsService.get('label.fitsFrame'), thumb.value, thumb.slider.maxValue);
            }
        });

        this.sliderFrame = Ext.create('Ext.slider.SingleSlider', {
            title: this.i18nFitsService.get('label.frames'),
            height: 30,
            width: 100,
            value: 0,
            increment: 1,
            minValue: 0,
            maxValue: 1,
            plugins: this.sliderTip,
            listeners: {
                scope: this,
                change: this.manageFrames
            }
        });

        this.sliderFrameGroupBtn = Ext.create('Ext.container.ButtonGroup', {
            title: this.i18nFitsService.get('label.frames'),
            hidden: true,
            items: [this.sliderFrame]
        });

        this.functionsGroupBtn = Ext.create('Ext.container.ButtonGroup', {
            title: this.i18nFitsService.get('label.functions'),
            columns: 3,
            defaults: {
                scope: this,
                enableToogle: true,
                toggleGroup: 'functions',
                toggleHandler: this.manageFunctions,
                allowDepress: false
            },
            items: [{
                value: 'loglog',
                name: 'log2',
                cls: 'testButton',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.loglog'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/log2.1.png"
            }, {
                value: 'linear',
                name: 'linear',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.linear'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/linear1.png"
            }, {
                value: 'sqrtlog',
                name: 'sqrtlog',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.squareLog'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/vlog1.png"
            }, {
                value: 'sqrt',
                name: 'sqrt',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.square'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/square1.png"
            }, {
                value: 'cuberoot',
                name: 'cuberoot',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.cubeRoot'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/v3.1.png"
            }, {
                value: 'log',
                name: 'log',
                width: 123,
                height: 25,
                text: this.i18nFitsService.get('label.log'),
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/log1.png"
            }]
        });

        this.colorsGroupBtn = Ext.create('Ext.container.ButtonGroup', {
            title: this.i18nFitsService.get('label.colors'),
            activeItem: 0,
            hideBorders: true,
            columns: 2,
            defaults: {
                scope: this,
                enableToogle: true,
                toggleGroup: 'colors',
                toggleHandler: this.manageColors,
                allowDepress: false
            },
            items: [{
                text: this.i18nFitsService.get('label.gray'),
                name: 'gray',
                value: 'gray',
                width: 75,
                height: 25,
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/grey.png"
            }, {
                text: this.i18nFitsService.get('label.red'),
                name: 'red',
                value: 'heat',
                width: 75,
                height: 25,
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/red.png"
            }, {
                text: this.i18nFitsService.get('label.green'),
                name: 'green',
                value: 'A',
                width: 75,
                height: 25,
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/green.png"
            }, {
                text: this.i18nFitsService.get('label.blue'),
                name: 'blue',
                value: 'B',
                width: 75,
                height: 25,
                icon: loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_EXTENSION_URL") + "/resources/img/fitsViewer/blue.png"
            }]
        });

        this.tbar = {
            xtype: 'toolbar',
            enableOverflow: true,
            border : false,
            defaults: {
                scope: this
            },
            items: [this.sliderFrameGroupBtn, this.functionsGroupBtn, '-', this.colorsGroupBtn, '-']
        };

        var advancedModeAvailable = false;

        var moduleStore = Ext.StoreManager.lookup('ModulesStore');
        var moduleIndex = moduleStore.find('xtype', 'sitools.extension.modules.FitsViewer');

        if (moduleIndex != -1) {
            advancedModeAvailable = true;
            this.fitsModule = moduleStore.getAt(moduleIndex);
        }

//        Ext.each(SitoolsDesk.app.getModules(), function(module) {
//            if (module.xtype == "sitools.user.modules.sitoolsFitsMain") {
//                advancedModeAvailable = true;
//                this.fitsModule = module;
//            }
//        },this);

        if (advancedModeAvailable) {
            var openModuleFitsLabel = Ext.create('Ext.button.Button', {
                cls: 'link-style',
                text: this.i18nFitsService.get('label.openAdvancedMode'),
                scope: this,
                handler: this.openAdvancedMode
            });
            this.tbar.items.push(openModuleFitsLabel);
        }

        this.canvasPanel = Ext.create('Ext.panel.Panel', {
            layout: 'fit',
            border : false,
            name: 'canvasPanel',
            autoScroll: true,
            bodyCls: 'canvas-background',
            padding: '10px 15px 10px 10px',
            html: '<canvas class="shadow-canvas" id="' + this.idFitsImage + '"></canvas>'
        });

        this.items = [this.canvasPanel];
        this.callParent(arguments);
    },

    afterRender: function () {
        this.callParent(arguments);

        // done here because "pressed" property is Read-Only
        var btnLinear = this.functionsGroupBtn.down('button[name="linear"]');
        btnLinear.pressed = true;
        btnLinear.addCls('activeButton');

        var btnGray = this.colorsGroupBtn.down('button[name="gray"]');
        btnGray.pressed = true;
        btnGray.addCls('activeButton');

        this.getEl().mask(this.i18nFitsService.get('label.loadingFits'), "x-mask-loading");

        Ext.defer(function () {
            this.jsFits.load(this.urlFits, null, null, function (resp) {
                this.getEl().unmask();
                var msgBox = Ext.Msg.show({
                    title: i18n.get('label.error'),
                    msg: this.i18nFitsService.get('label.errorFitsLoading'),
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.MessageBox.OK,
                    scope: this
                });
                Ext.defer(function () {
                    Ext.WindowManager.bringToFront(msgBox);
                    this.getEl().mask();
                }, 500, this);
            }.bind(this));
        }, 5, this);
    },

    manageFunctions: function (btn, pressed) {
        if (pressed) {
            btn.addCls("activeButton");

            this.canvasPanel.getEl().mask(this.i18nFitsService.get('label.loadingFits'), "x-mask-loading");

            Ext.defer(function () {
                this.jsFits.update({
                    stretch: btn.value,
                    min: this.min,
                    max: this.max
                }, function () {
                    this.canvasPanel.getEl().unmask();
                }.bind(this));
            }, 5, this);

        } else {
            btn.removeCls("activeButton");
        }
    },

    manageColors: function (btn, pressed) {
        if (pressed) {
            btn.addCls("activeButton");
            this.canvasPanel.getEl().mask(this.i18nFitsService.get('label.loadingFits'), "x-mask-loading");

            Ext.defer(function () {
                this.jsFits.update({
                    color: btn.value,
                    min: this.min,
                    max: this.max
                }, function () {
                    this.canvasPanel.getEl().unmask();
                }.bind(this));
            }, 5, this);

        } else {
            btn.removeCls("activeButton");
        }
    },

    manageFrames: function (slider, newValue, thumb) {
        this.jsFits.update({
            index: newValue
        });
    },

    openAdvancedMode: function () {

        var componentConfig = {
            url : this.urlFits
        }

        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openModule(this.fitsModule, componentConfig);

    }

});
