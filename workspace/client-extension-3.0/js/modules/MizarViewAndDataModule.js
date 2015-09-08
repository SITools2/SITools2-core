Ext.namespace('sitools.extension.modules');

Ext.define('sitools.extension.modules.MizarViewAndDataModule', {
    extend: 'sitools.user.core.Module',

    pluginName: 'mizarViewAndDataModule',
    requires: ['sitools.user.utils.I18nRegistry',
                'sitools.extension.utils.MizarUtils'],

    // PRODUCTION MODE
    //js: ["/sitools/client-extension/js/controller/modules/MizarViewAndDataModule/src/mizar/MizarWidget.min.js"],

    css: ["/sitools/client-extension/resources/libs/mizar/src/mizar/css/style.min.css",
        "/sitools/client-extension/resources/css/mizarModule/mizarModule.css"],

    i18nFolderPath: ['/sitools/client-extension/resources/i18n/mizarModule/'],

    controllers: ['sitools.extension.controller.modules.mizarModule.MizarModuleController',
                    'sitools.user.controller.component.datasets.dataviews.LivegridController',
                    'sitools.user.controller.component.datasets.services.ServicesController'],

    init: function () {
        var viewDataAndMizar = Ext.create('sitools.extension.view.modules.mizarModule.MizarViewAndDataModuleView', {
            moduleModel: this.getModuleModel()
        });

        this.show(viewDataAndMizar);

        this.callParent(arguments);
    },

    statics: {
        getParameters: function () {

            return [{
                jsObj: "Ext.form.TextField",
                config: {
                    allowBlank: true,
                    width: 200,
                    hidden : true,
                    itemId: "datasetName",
                    name : "datasetName",
                    value: undefined
                }
            }, {
                jsObj : "Ext.form.ComboBox",
                config : {
                    fieldLabel : i18n.get("label.dataset"),
                    itemId : 'comboDatasets',
                    allowBlank : false,
                    editable : false,
                    queryMode: 'local',
                    width : 200,
                    valueField : 'url',
                    displayField : 'name',
                    store : Ext.create('Ext.data.JsonStore', {
                        proxy : {
                            type : 'memory'
                        },
                        idProperty : 'id',
                        fields : [{
                            name : 'id',
                            type : 'string'
                        }, {
                            name : 'name',
                            type : 'string'
                        }, {
                            name : 'url',
                            type : 'string'
                        }]
                    }),
                    listeners: {
                        boxready : function (c) {
                            var datasetsGrid = Ext.ComponentQuery.query('grid[name=gridDataSets]')[0];
                            //c.getStore().loadData(datasetsGrid.getStore().data.items);
                            datasetsGrid.getStore().each(function (dataset) {
                                c.getStore().add({
                                    id : dataset.data.id,
                                    name : dataset.data.name,
                                    url : dataset.data.url
                                });
                            });
                        },
                        select : function (combo, recs, ind) {
                            var dsName = this.up('form').down('#datasetName');
                            dsName.setValue(recs[0].get('name'));
                        }
                    },
                    name : "datasetAttachment",
                    value : undefined
                }
            }, {
                jsObj: "Ext.form.TextField",
                config: {
                    anchor: "100%",
                    name: "datasetWidth",
                    allowBlank: false,
                    fieldLabel: i18n.get("label.datasetWidth"),
                    value: 35
                }
            }, {
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
            }, {
                jsObj: 'Ext.form.TextField',
                config: {
                    name: 'configFile',
                    fieldLabel: i18n.get('label.configFile'),
                    labelWidth: 170,
                    allowBlank: true,
                    width: 200,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "The Mizar configuration file"
                            });
                        }
                    }
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

