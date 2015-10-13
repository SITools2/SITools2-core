Ext.namespace('sitools.extension.component.datasetExplorerOchart');

Ext.define('sitools.extension.component.datasetExplorerOchart.OChartDropZone', {
    extend: 'Ext.tree.ViewDropZone',

    indicatorCls: Ext.baseCSSPrefix + 'ochart-ddindicator',

    getPosition: function (e, node) {
        var view = this.view,
            record = view.getRecord(node),
            x = e.getPageX(),
            y = e.getPageY(),
            noAppend = false,
            noBelow = false,
            region = Ext.fly(node).getRegion(),
            fragment;

        // If we are dragging on top of the root node of the tree, we always want to append.
        if (record.isRoot() || this.appendOnly) {
            return 'append';
        }

        if (!this.allowParentInserts) {
            noAppend = true;
            noBelow = true;
        }

        fragment = (region.right - region.left) / (noBelow ? 2 : 3);
        if (x >= region.left && x < (region.left + fragment)) {
            return 'before';
        }
        else if (x >= (region.right - fragment) && x <= region.right) {
            return 'after';
        }
        else {
            return 'append';
        }
    },

    isValidDropPoint: function (node, position, dragZone, e, data) {
        if (!node || !data.item) {
            return false;
        }

        var view = this.view,
            targetNode = view.getRecord(node),
            draggedRecords = data.records,
            dataLength = draggedRecords.length,
            ln = draggedRecords.length,
            i, record;

        //view is read only, so no drop allowed
        if (view.readOnly) {
            return false;
        }

        // No drop position, or dragged records: invalid drop point
        if (!(targetNode && position && dataLength)) {
            return false;
        }

        // If the targetNode is within the folder we are dragging
        for (i = 0; i < ln; i++) {
            record = draggedRecords[i];
            if (record.isNode) {
                if (record.contains(targetNode)) {
                    return false;
                }
            }
        }

        // Respect the allowDrop field on Tree nodes
        if (position === 'append' && targetNode.get('allowDrop') === false) {
            return false;
        }
        else if (position != 'append' && targetNode.parentNode.get('allowDrop') === false) {
            return false;
        }

        // If the target record is in the dragged dataset, then invalid drop
        if (Ext.Array.contains(draggedRecords, targetNode)) {
            return false;
        }
        return view.fireEvent('nodedragover', targetNode, position, data, e) !== false;
    },

    onNodeOver: function (node, dragZone, e, data) {
        var position = this.getPosition(e, node),
            returnCls = this.dropNotAllowed,
            view = this.view,
            targetNode = view.getRecord(node),
            indicator = this.getIndicator();

        // auto node expand check
        this.cancelExpand();
        if (position == 'append' && !this.expandProcId && !Ext.Array.contains(data.records, targetNode) && !targetNode.isLeaf() && !targetNode.isExpanded()) {
            this.queueExpand(targetNode);
        }


        if (this.isValidDropPoint(node, position, dragZone, e, data)) {
            this.valid = true;
            this.currentPosition = position;
            this.overRecord = targetNode;

            /*
             * In the code below we show the proxy again. The reason for doing this is showing the indicator will
             * call toFront, causing it to get a new z-index which can sometimes push the proxy behind it. We always
             * want the proxy to be above, so calling show on the proxy will call toFront and bring it forward.
             */
            if (position == 'before') {
                returnCls = targetNode.isFirst() ? Ext.baseCSSPrefix + 'tree-drop-ok-above' : Ext.baseCSSPrefix + 'tree-drop-ok-between';
                indicator.removeCls(['append', 'after']);
                indicator.addCls('before');
                indicator.show();
                indicator.alignTo(node, 'r-l');
                dragZone.proxy.show();
            } else if (position == 'after') {
                returnCls = targetNode.isLast() ? Ext.baseCSSPrefix + 'tree-drop-ok-below' : Ext.baseCSSPrefix + 'tree-drop-ok-between';
                indicator.removeCls(['append', 'before']);
                indicator.addCls('after');
                indicator.show();
                indicator.alignTo(node, 'l-r');
                dragZone.proxy.show();
            } else {
                returnCls = Ext.baseCSSPrefix + 'tree-drop-ok-append';
                indicator.removeCls(['before', 'after']);
                indicator.addCls('append');
                indicator.show();
                indicator.alignTo(node, 't-b', [2, 0]);
            }
        } else {
            this.valid = false;
            indicator.hide();
        }

        this.currentCls = returnCls;
        return returnCls;
    },

    handleNodeDrop: function (data, targetNode, position) {
        var me = this,
            targetView = me.view,
            targetNode = targetNode || targetView.getRootNode(),
            parentNode = targetNode ? targetNode.parentNode : targetView.getRootNode(),
            Model = targetView.getStore().model,
            records, i, len, record,
            insertionMethod, argList,
            needTargetExpand,
            transferData;

        // If the copy flag is set, create a copy of the models
        if (data.copy) {
            records = data.records;
            data.records = [];
            for (i = 0, len = records.length; i < len; i++) {
                record = records[i];
                if (record.isNode) {
                    data.records.push(record.copy(undefined, true));
                } else {
                    // If it's not a node, make a node copy
                    data.records.push(new Model(record.data, record.getId()));
                }
            }
        }

        // Cancel any pending expand operation
        me.cancelExpand();

        if (data.view.endDrag) data.view.endDrag();

        // Grab a reference to the correct node insertion method.
        // Create an arg list array intended for the apply method of the
        // chosen node insertion method.
        // Ensure the target object for the method is referenced by 'targetNode'
        if (position == 'before') {
            insertionMethod = parentNode.insertBefore;
            argList = [null, targetNode];
            targetNode = parentNode;
        }
        else if (position == 'after') {
            if (targetNode.nextSibling) {
                insertionMethod = parentNode.insertBefore;
                argList = [null, targetNode.nextSibling];
            }
            else {
                insertionMethod = parentNode.appendChild;
                argList = [null];
            }
            targetNode = parentNode;
        }
        else {
            if (!(targetNode.isExpanded() || targetNode.isLoading())) {
                needTargetExpand = true;
            }
            insertionMethod = targetNode.appendChild;
            argList = [null];
        }

        // A function to transfer the data into the destination tree
        transferData = function () {
            var color,
                clearOrigin = false,
                n, selected;

            // Coalesce layouts caused by node removal, appending and sorting
            Ext.suspendLayouts();

            targetView.getSelectionModel().deselectAll();

            //target view is not the same as origin and it´s not copy and don't share the same store
            if (targetView != data.view && !data.copy && targetView.store != data.view.store) {
                data.view.getSelectionModel().deselectAll();
                clearOrigin = true;
            }

            // Insert the records into the target node
            for (i = 0, len = data.records.length; i < len; i++) {
                record = data.records[i];
                if (!record.isNode) {
                    if (record.isModel) {
                        record = new Model(record.data, record.getId());
                    } else {
                        record = new Model(record);
                    }
                    data.records[i] = record;
                }
                argList[0] = record;

                insertionMethod.apply(targetNode, argList);
                if (clearOrigin) {
                    if (data.view.removeNodeFromRecord) data.view.removeNodeFromRecord(record);
                }
            }

            // If configured to sort on drop, do it according to the TreeStore's comparator
            if (me.sortOnDrop) {
                targetNode.sort(targetNode.getOwnerTree().store.generateComparator());
            }

            Ext.resumeLayouts(true);

            // Kick off highlights after everything's been inserted, so they are
            // more in sync without insertion/render overhead.
            // Element.highlight can handle highlighting table nodes.
            if (Ext.enableFx && me.dropHighlight) {
                color = me.dropHighlightColor;

                for (i = 0; i < len; i++) {
                    n = targetView.getNode(data.records[i]);
                    if (n) {
                        Ext.fly(n).highlight(color);
                    }
                }
            }
        };

        // If dropping right on an unexpanded node, transfer the data after it is expanded.
        if (needTargetExpand) {
            targetNode.expand(false, transferData);
        }
        // If the node is waiting for its children, we must transfer the data after the expansion.
        // The expand event does NOT signal UI expansion, it is the SIGNAL for UI expansion.
        // It's listened for by the NodeStore on the root node. Which means that listeners on the target
        // node get notified BEFORE UI expansion. So we need a delay.
        // TODO: Refactor NodeInterface.expand/collapse to notify its owning tree directly when it needs to expand/collapse.
        else if (targetNode.isLoading()) {
            targetNode.on({
                expand: transferData,
                delay: 1,
                single: true
            });
        }
        // Otherwise, call the data transfer function immediately
        else {
            transferData();
        }
    },

    onContainerOver: function (dd, e, data) {
        var me = this;
        if (me.view.allowContainerDrop) {
            me.valid = true;
            return Ext.baseCSSPrefix + 'tree-drop-ok-append';
        }
        else {
            return me.dropNotAllowed;
        }
    }
});