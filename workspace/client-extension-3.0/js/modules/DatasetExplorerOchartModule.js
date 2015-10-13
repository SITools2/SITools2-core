Ext.namespace('sitools.extension.modules');

Ext.define('sitools.extension.modules.DatasetExplorerOchartModule', {
    extend: 'sitools.user.core.Module',

    pluginName: 'datasetExplorerOchartModule',

    requires: ['sitools.extension.controller.modules.datasetExplorerOchart.DatasetExplorerOchartController'],
    controllers: ['sitools.extension.controller.modules.datasetExplorerOchart.DatasetExplorerOchartController'],

    css: ["/sitools/client-extension/resources/css/datasetExplorerOchart/Ochart.css"],

    init: function (moduleModel) {

        var view = Ext.create('sitools.extension.view.modules.datasetExplorerOchart.DatasetExplorerOchartView', {
            moduleModel: this.getModuleModel()
        });
        this.setViewCmp(view);

        this.show(this.getViewCmp());

        this.callParent(arguments);
    },

    statics: {
        getParameters: function () {

            return [{
                jsObj: "Ext.form.TextField",
                config: {
                    fieldLabel: i18n.get('label.datasetExplorerGraphRootName'),
                    allowBlank: true,
                    labelWidth: 140,
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: "Root node name"
                            });
                        }
                    },
                    name: "datasetOGraphName",
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
