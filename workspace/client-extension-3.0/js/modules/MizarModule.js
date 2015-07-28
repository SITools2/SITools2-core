Ext.namespace('sitools.extension.modules');

Ext.define('sitools.extension.modules.MizarModule', {
    extend: 'sitools.user.core.Module',

    pluginName: 'mizarModule',
    requires: ['sitools.user.utils.I18nRegistry'],

    // PRODUCTION MODE
    //js: ["/sitools/client-extension/js/controller/modules/mizarModule/src/mizar/MizarWidget.min.js"],

    css: ["/sitools/client-extension/resources/libs/mizar/src/mizar/css/style.min.css",
        "/sitools/client-extension/resources/css/mizarModule/mizarModule.css"],

    i18nFolderPath: ['/sitools/client-extension/resources/i18n/mizarModule/'],

    controllers: ['sitools.extension.controller.modules.mizarModule.MizarModuleController'],

    init: function () {
        var view = Ext.create('sitools.extension.view.modules.mizarModule.MizarModuleView', {
            moduleModel: this.getModuleModel()
        });

        this.show(view);

        this.callParent(arguments);
    },

    statics: {
        getParameters: function () {

            return [{
                jsObj: "Ext.form.TextField",
                config: {
                    fieldLabel: i18n.get('label.startingLong'),
                    allowBlank: true,
                    width: 200,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "The longitutude of the starting point"
                            });
                        }
                    },
                    name: "startingLong",
                    value: undefined
                }
            }, {
                jsObj: "Ext.form.TextField",
                config: {
                    fieldLabel: i18n.get('label.startingLat'),
                    allowBlank: true,
                    width: 200,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "The latitude of the starting point"
                            });
                        }
                    },
                    name: "startingLat",
                    value: undefined
                }
            }, {
                jsObj: "Ext.form.TextField",
                config: {
                    fieldLabel: i18n.get('label.startingZoom'),
                    allowBlank: true,
                    width: 200,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "The starting zoom level"
                            });
                        }
                    },
                    name: "startingZoom",
                    value: undefined
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'angleDistance',
                    fieldLabel: i18n.get('label.angleDistance'),
                    labelWidth: 170,
                    checked: true
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'samp',
                    fieldLabel: i18n.get('label.samp'),
                    labelWidth: 170,
                    checked: false
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'shortenerUrl',
                    fieldLabel: i18n.get('label.shortenerUrl'),
                    labelWidth: 170,
                    checked: false
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: '2dMap',
                    fieldLabel: i18n.get('label.2dMap'),
                    labelWidth: 170,
                    checked: true
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'reverseNameResolver',
                    fieldLabel: i18n.get('label.reverseNameResolver'),
                    labelWidth: 170,
                    checked: true
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'nameResolver',
                    fieldLabel: i18n.get('label.nameResolver'),
                    labelWidth: 170,
                    checked: true
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'category',
                    fieldLabel: i18n.get('label.category'),
                    labelWidth: 170,
                    checked: false
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'compass',
                    fieldLabel: i18n.get('label.compass'),
                    labelWidth: 170,
                    checked: true
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'showCredits',
                    fieldLabel: i18n.get('label.showCredits'),
                    labelWidth: 170,
                    checked: false
                }
            }, {
                jsObj: 'Ext.form.field.Checkbox',
                config: {
                    name: 'imageViewer',
                    fieldLabel: i18n.get('label.imageViewer'),
                    labelWidth: 170,
                    checked: true
                }
            }];
        }
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id
        };
    }
});

