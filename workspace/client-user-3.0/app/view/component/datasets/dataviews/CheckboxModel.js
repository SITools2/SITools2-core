/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.view.component.datasets.dataviews');

Ext.define('sitools.user.view.component.datasets.dataviews.CheckboxModel', {
    extend: 'Ext.selection.CheckboxModel',
    
    // Provides differentiation of logic between MULTI, SIMPLE and SINGLE
    // selection modes. Requires that an event be passed so that we can know
    // if user held ctrl or shift.
    
    constructor: function(){
        var me = this;
        me.callParent(arguments);   
    },
    
    /**
     * Add the header checkbox to the header row
     * @private
     * @param {Boolean} initial True if we're binding for the first time.
     */
    addCheckbox: function(view, initial){
        var me = this,
            checkbox = me.injectCheckbox,
            headerCt = view.headerCt;

        // Preserve behaviour of false, but not clear why that would ever be done.
        if (checkbox !== false) {
            if (checkbox == 'first') {
                checkbox = 0;
            } else if (checkbox == 'last') {
                checkbox = headerCt.getColumnCount();
            }
            Ext.suspendLayouts();
//            if (view.getStore().buffered) {
//                me.showHeaderCheckbox = false;
//            }
            headerCt.add(checkbox,  me.getHeaderConfig());
            Ext.resumeLayouts();
        }

        if (initial !== true) {
            view.refresh();
        }
    },
    
    //Suppress all events on selection
    selectWithEvent: function(record, e) {
        var me = this,
            isSelected = me.isSelected(record),
            shift = e.shiftKey,
            ctrl = e.ctrlKey,
            start = me.selectionStart,
            selected = me.getSelection(),
            len = selected.length,
            allowDeselect = me.allowDeselect,
            toDeselect, i, item;

        switch (me.selectionMode) {
            case 'MULTI':
                if (shift && start) {
                    me.selectRange(start, record, ctrl);
                } else if (ctrl && isSelected) {
                    me.doDeselect(record, false);
                } else if (ctrl) {
                    me.doSelect(record, true, false);
                } else if (isSelected && !shift && !ctrl && len > 1) {
                    toDeselect = [];
                    
                    for (i = 0; i < len; ++i) {
                        item = selected[i];
                        if (item !== record) {
                            toDeselect.push(item);    
                        }
                    }
                    
                    me.doDeselect(toDeselect, true);
                } else if (!isSelected) {
                    me.doSelect(record, false, true);
                }
                break;
            case 'SIMPLE':
                if (isSelected) {
                    me.doDeselect(record, true);
                } else {
                    me.doSelect(record, true, true);
                }
                break;
            case 'SINGLE':
                if (allowDeselect && !ctrl) {
                    allowDeselect = me.toggleOnClick;
                }
                if (allowDeselect && isSelected) {
                    me.doDeselect(record, true);
                } else {
                    me.doSelect(record, false, true);
                }
                break;
        }

        // selectionStart is a start point for shift/mousedown to create a range from.
        // If the mousedowned record was not already selected, then it becomes the
        // start of any range created from now on.
        // If we drop to no records selected, then there is no range start any more.
        if (!shift) {
            if (me.isSelected(record)) {
                me.selectionStart = record;
            } else {
                me.selectionStart = null;
            }
        }
    },
    
    /**
     * Selects all rows if the selection model
     * {@link Ext.grid.AbstractSelectionModel#isLocked is not locked}.
     */
    selectAll : function () {
        this.clearSelections();
        this.markAll = true;

        if (this.headerCheckbox) {
            this.headerCheckbox.addClass('x-grid3-hd-checker-on');
        }
        Ext.ux.grid.livegrid.CheckboxSelectionModel.superclass.selectAll.call(this, true);
    },
});
