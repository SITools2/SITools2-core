Ext.namespace('sitools.extension.component.datasetExplorerOchart');

Ext.define('sitools.extension.component.datasetExplorerOchart.OChartDragDrop', {
    extend: 'Ext.tree.plugin.TreeViewDragDrop',
    alias: 'plugin.ochartdragdrop',

    uses: [
        'sitools.extension.component.datasetExplorerOchart.OChartDragZone',
        'sitools.extension.component.datasetExplorerOchart.OChartDropZone'
    ],

    onViewRender: function (view) {
        var me = this,
            scrollEl;

        if (me.enableDrag) {
            if (me.containerScroll) {
                scrollEl = view.getEl();
            }
            me.dragZone = Ext.create('sitools.extension.component.datasetExplorerOchart.OChartDragZone', {
                view: view,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText,
                displayField: me.displayField,
                repairHighlightColor: me.nodeHighlightColor,
                repairHighlight: me.nodeHighlightOnRepair,
                scrollEl: scrollEl
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('sitools.extension.component.datasetExplorerOchart.OChartDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                allowContainerDrops: me.allowContainerDrops,
                appendOnly: me.appendOnly,
                allowParentInserts: me.allowParentInserts,
                expandDelay: me.expandDelay,
                dropHighlightColor: me.nodeHighlightColor,
                dropHighlight: me.nodeHighlightOnDrop,
                sortOnDrop: me.sortOnDrop,
                containerScroll: me.containerScroll
            });
        }
    }
});