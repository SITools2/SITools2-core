Ext.namespace('sitools.extension.modules');

Ext.define('sitools.extension.modules.MizarModule', {
    extend: 'sitools.user.core.Module',

    // PRODUCTION MODE
    //js: ["/sitools/client-extension/js/controller/modules/mizarModule/src/mizar/MizarWidget.min.js"],

    css: ["/sitools/client-extension/resources/libs/mizar/src/mizar/css/style.min.css",
        "/sitools/client-extension/resources/css/mizarModule/mizarModule.css"],

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
//                                        fieldLabel : i18n.get("label.urlDatastorage"),
                    fieldLabel: "name of configuration file of Mizar",
                    allowBlank: true,
                    width: 200,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "URI to the configuration file"
                            });
                        }
                    },
                    name: "configFile",
                    value: undefined
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

