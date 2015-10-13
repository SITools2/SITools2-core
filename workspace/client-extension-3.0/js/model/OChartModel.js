Ext.define('sitools.extension.model.OChartModel', {
    extend: 'Ext.selection.DataViewModel',

    requires: [
        'Ext.util.KeyNav',
        'Ext.util.KeyMap'
    ],

    destroy: function () {
        var me = this;
        if (me.keyMap) {
            me.keyMap.destroy();
        }

        me.callParent(arguments);
    },

    /**
     * Overwrites model refresh so it handles a Tree Store
     */
    refresh: function () {
        var me = this,
            store = me.store,
            rec,
            toBeSelected = [],
            toBeReAdded = [],
            oldSelections = me.getSelection(),
            len = oldSelections.length,
            view = me.view,
            selection,
            change,
            i = 0,
            lastFocused = me.getLastFocused();

        // Not been bound yet.
        if (!store) {
            return;
        }

        // Add currently records to the toBeSelected list if present in the Store
        // If they are not present, and pruneRemoved is false, we must still retain the record
        for (; i < len; i++) {
            selection = oldSelections[i];
            if (view.getNode(selection)) {
                toBeSelected.push(selection);
            }

            // Selected records no longer represented in Store must be retained
            else if (!me.pruneRemoved) {
                // See if a record by the same ID exists. If so, select it
                rec = store.getById(selection.getId());
                if (rec) {
                    toBeSelected.push(rec);
                }
                // If it does not exist, we have to re-add it to the selection
                else {
                    toBeReAdded.push(selection)
                }
            }

            // In single select mode, only one record may be selected
            if (me.mode === 'SINGLE' && toBeReAdded.length) {
                break;
            }
        }

        // there was a change from the old selected and
        // the new selection
        if (me.selected.getCount() != (toBeSelected.length + toBeReAdded.length)) {
            change = true;
        }

        me.clearSelections();

        if (view.getNode(lastFocused)) {
            // restore the last focus but suppress restoring focus
            me.setLastFocused(lastFocused, true);
        }

        if (toBeSelected.length) {
            // perform the selection again
            me.doSelect(toBeSelected, false, true);
        }

        // If some of the selections were not present in the Store, but pruneRemoved is false, we must add them back
        if (toBeReAdded.length) {
            me.selected.addAll(toBeReAdded);

            // No records reselected.
            if (!me.lastSelected) {
                me.lastSelected = toBeReAdded[toBeReAdded.length - 1];
            }
        }

        me.maybeFireSelectionChange(change);
    },

    /**
     * Overwrites model deselectAll so it handles a Tree Store
     */
    deselectAll: function (suppressEvent) {
        var me = this,
            selections = me.getSelection(),
            selIndexes = {},
            store = me.store,
            start = selections.length,
            i, l, rec;

        // Cache selection records' indexes first to avoid
        // looking them up on every sort comparison below.
        // We can't rely on store.indexOf being fast because
        // for whatever reason the Store in question may force
        // sequential index lookup, which will result in O(n^2)
        // sort performance below.
        for (i = 0, l = selections.length; i < l; i++) {
            rec = selections[i];

            selIndexes[rec.internalId] = rec.get('index');
        }

        // Sort the selections so that the events fire in
        // a predictable order like selectAll
        selections = Ext.Array.sort(selections, function (r1, r2) {
            var idx1 = selIndexes[r1.internalId],
                idx2 = selIndexes[r2.internalId];

            // Don't check for equality since indexes will be unique
            return idx1 < idx2 ? -1 : 1;
        });

        if (me.suspendChanges) me.suspendChanges();
        me.doDeselect(selections, suppressEvent);
        if (me.resumeChanges) me.resumeChanges();
        // fire selection change only if the number of selections differs
        if (!suppressEvent) {
            me.maybeFireSelectionChange(me.getSelection().length !== start);
        }
    },

    initKeyNav: function (view) {
        var me = this;

        if (!view.rendered) {
            view.on({
                render: Ext.Function.bind(me.initKeyNav, me, [view]),
                single: true
            });
            return;
        }

        view.el.set({
            tabIndex: -1
        });

        me.keyNav = new Ext.util.KeyNav({
            target: view.el,
            ignoreInputFields: true,
            down: Ext.pass(me.onNavKey, ['down'], me),
            right: Ext.pass(me.onNavKey, ['right'], me),
            left: Ext.pass(me.onNavKey, ['left'], me),
            up: Ext.pass(me.onNavKey, ['up'], me),
            scope: me
        });

        me.keyMap = Ext.create('Ext.util.KeyMap', {
            target: view.el,
            binding: [
                {
                    key: Ext.EventObject.NUM_PLUS,
                    ctrl: false,
                    shift: false,
                    fn: me.onKeyExpand,
                    scope: me
                }, {
                    key: Ext.EventObject.NUM_MINUS,
                    ctrl: false,
                    shift: false,
                    fn: me.onKeyCollapse,
                    scope: me
                }
            ]
        });
    },

    onNavKey: function (direction) {
        var me = this,
            view = me.view,
            store = view.store,
            selected = me.getLastSelected(),
            root = view.getRootNode(),
            record, node;

        if (!selected) {
            if (!root) return;

            selected = root;
            if (!view.rootVisible) {
                selected = root.firstChild;
            }

            if (selected) {
                me.select(selected);
            }

            return;
        }

        direction = direction || 'right';
        switch (direction) {
            case 'left':
                record = selected.previousSibling;
                break;
            case 'right':
                record = selected.nextSibling;
                break;
            case 'up':
                if (selected == root) {
                    record = null;
                    break;
                }

                record = selected.parentNode;

                if (!view.rootVisible && record == root) {
                    record = null;
                }
                break;
            case 'down':
                record = selected.firstChild;
                if (!record && !selected.isLeaf()) {
                    record = selected;
                }
                break;
        }

        if (!record) return;

        if (direction == 'down' && !selected.isExpanded()) {
            selected.expand(false, function (rec) {
                me.select(record);
                view.hideTools();
                if (Ext.versions.extjs.isLessThan('4.2.0')) {
                    view.focusNode(record);
                }
            }, me);
        }
        else {
            me.select(record);
            view.hideTools();
            if (Ext.versions.extjs.isLessThan('4.2.0')) {
                view.focusNode(record);
            }
        }
    },

    onKeyExpand: function () {
        var me = this,
            view = me.view,
            selected = me.getSelection(),
            len = selected.length,
            i;

        for (i = 0; i < len; ++i) {
            selected[i].expand();
        }
    },

    onKeyCollapse: function () {
        var me = this,
            view = me.view,
            selected = me.getSelection(),
            len = selected.length,
            i;

        for (i = 0; i < len; ++i) {
            selected[i].collapse();
        }
    },

    onContainerClick: function () {
        if (!this.view.dragging && this.deselectOnContainerClick) {
            this.deselectAll();
        }
    }
});

