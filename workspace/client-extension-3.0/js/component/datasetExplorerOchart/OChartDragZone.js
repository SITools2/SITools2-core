Ext.namespace('sitools.extension.component.datasetExplorerOchart');

Ext.define('sitools.extension.component.datasetExplorerOchart.OChartDragZone', {
    extend: 'Ext.tree.ViewDragZone',

    afterRepair: function () {
        var me = this,
            view = me.view,
            selectedRowCls = view.selectedItemCls,
            records = me.dragData.records,
            r,
            rLen = records.length,
            fly = Ext.fly,
            item;

        if (Ext.enableFx && me.repairHighlight) {
            // Roll through all records and highlight all the ones we attempted to drag.
            for (r = 0; r < rLen; r++) {
                // anonymous fns below, don't hoist up unless below is wrapped in
                // a self-executing function passing in item.
                item = view.getNode(records[r]);

                // We must remove the selected row class before animating, because
                // the selected row class declares !important on its background-color.
                fly(item).highlight(me.repairHighlightColor, {
                    listeners: {
                        beforeanimate: function () {
                            if (view.isSelected(item)) {
                                fly(item).removeCls(selectedRowCls);
                            }
                        },
                        afteranimate: function () {
                            if (view.isSelected(item)) {
                                fly(item).addCls(selectedRowCls);
                            }
                        }
                    }
                });
            }

        }
        me.dragging = false;
        if (this.view.endDrag) this.view.endDrag();
    },

    onInitDrag: function (x, y) {
        this.view.startDrag();
        this.callParent(arguments);
    }
});
