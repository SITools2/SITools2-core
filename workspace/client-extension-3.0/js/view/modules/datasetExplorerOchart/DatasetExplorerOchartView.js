Ext.namespace('sitools.extension.view.modules.datasetExplorerOchart');

Ext.define('sitools.extension.view.modules.datasetExplorerOchart.DatasetExplorerOchartView', {
    extend: 'sitools.extension.view.modules.datasetExplorerOchart.DatasetExplorerOchartViewSimple',

    requires: 'sitools.user.model.TaskModel',
    alias: 'widget.DatasetExplorerOchart',

    initComponent: function () {
        var me = this;

        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
                case "datasetOGraphName" :
                    this.datasetOGraphName = config.get('value');
                    break;
            }
        }, this);

        me.store = Ext.create("sitools.extension.store.DatasetExplorerOchartTreeStore");

        /*me.store = Ext.create('Ext.data.TreeStore',{
         model: 'sitools.user.view.modules.datasetExplorerOchart.TaskModel',
         root: {
         "text": "Solar Projects",
         "leaf": false,
         expanded: true
         },
         autoLoad: true
         });*/
        //me.store = Ext.create("sitools.user.view.modules.datasetExplorerOchart.NodesStore");
        me.chartConfig = me.chartConfig || {};
        Ext.applyIf(me.chartConfig, {
            itemTpl: [
                '<div class="item-body" style="text-align: center">',
                '<tpl if="type == \'dataset\'">',
                '<img src="{imageDs}" class="item-img">',
                '<div class="item-title-ds item-title">{text}</div>',
                '<a href="#" class="overDatasetService" onClick="sitools.user.utils.DatasetUtils.clickDatasetIcone(\'{url}\', \'data\');return false;">',
                Ext.String.format('<img class="datasetochart_icon" src="{0}" data-qtip="{1}">', loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/tree_datasets_32.png", i18n.get('label.dataTitle')),
                '</a>',
                '<a href="#" class="overDatasetService" onClick="sitools.user.utils.DatasetUtils.clickDatasetIcone(\'{url}\', \'forms\');return false;">',
                Ext.String.format('<img class="datasetochart_icon" src="{0}" data-qtip="{1}">', loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/openSearch_32.png", "Query form"),
                '</a>',
                '<div class="item-nb">({nbRecord} records)</div>',
                '</tpl>',
                '<tpl if="type == \'node\'">',
                '<tpl if="icon">',
                '<img src="{icon}" style="height: 25px">',
                '</tpl>',
                '<div class="item-title-node item-title">{text}</div>',
                '</tpl>',
                '<tpl if="type == \'rootnode\'">',
                '<div class="item-title-rootnode item-title">{text}</div>',
                '</tpl>',
                '</div>'

            ],

            itemCls: 'task-item'
        });

        me.listeners = {
            afterrender: function (ochartViewPanel) {
                var elems = document.querySelectorAll('.task-item');
                ochartViewPanel.store.getRootNode().set('text', ochartViewPanel.datasetOGraphName);
                //me.onInlineExpanderClick(null, elems);
            }
        };

        me.callParent(arguments);
    },

    onItemDblClick: Ext.emptyFn
});

