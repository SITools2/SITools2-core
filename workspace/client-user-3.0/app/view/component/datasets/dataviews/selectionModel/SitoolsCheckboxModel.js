/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.define('sitools.user.view.component.datasets.dataviews.selectionModel.SitoolsCheckboxModel', {
    extend: 'sitools.user.view.component.datasets.dataviews.selectionModel.CheckboxModel',
    
    constructor: function(){
        var me = this;
        me.callParent(arguments);   
    },
    
    /**
     * Add the header checkbox to the header row
     * @private
     * @param {Boolean} initial True if we're binding for the first time.
     * SITOOLS PATCH : patched to enable header checkbox even if we have a buffered store 
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
    
    /**
     * SITOOLS PATCH :
     * Handle MULTI mode without ctrl key. It is now possible to select multiple ranges and also to deselect using ranges
     */
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
                if (shift && !Ext.isEmpty(start)) {
                	if (me.isSelected(start)) {
                		me.selectRange(start, record, true);
                	} else {
                		me.deselectRange(start, record);
                	}
                } 
                else if (isSelected) {
                	//if all records were selected, deselect everything and select the record clicked
                	if (me.markAll) {
                		me.suspendEvents(false);
                		me.deselectAll();
                		me.resumeEvents();
                        me.doSelect(record, true);
                	}
                	else {
                		me.doDeselect(record);
                	}
                } else {
                    me.doSelect(record, true);
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
        	me.selectionStart = record;
        }
    },
    
    /**
     * SITOOLS PATCH do nothing, handle in the onHeaderClick method
     */
    updateHeaderState: Ext.emptyFn,
    
    /**
     * Toggle between selecting all and deselecting all when clicking on
     * a checkbox header.
     * SITOOLS PATCH : Handle the header click
     */
    onHeaderClick: function(headerCt, header, e) {
        if (header.isCheckerHd) {
            e.stopEvent();
            var me = this,
                isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                
            // Prevent focus changes on the view, since we're selecting/deselecting all records
            me.preventFocus = true;
            if (isChecked) {
                me.deselectAll(true);
            } else {
                me.selectAll(true);
            }
            delete me.preventFocus;
        }
    },
    
    /**
     * Update selection to deal with simulated selection of all records 
     */
    updateSelection : function () {
    	if(this.markAll) {
    		var renderer = this.gridView.getPlugin('renderer');
            var firstIndex = renderer.getFirstVisibleRowIndex();
            var lastIndex = renderer.getLastVisibleRowIndex();
            this.selectRange(firstIndex, lastIndex, false, true);
    	}
    },
    
    /**
     * Selects all rows if the selection model
     * {@link Ext.grid.AbstractSelectionModel#isLocked is not locked}.
     */
    selectAll : function () {
        this.markAll = true;
        this.updateSelection();
        this.toggleUiHeader(this.markAll);
        this.maybeFireSelectionChange(true);
    },
    
    deselectAll : function () {
    	this.markAll = false;
    	this.toggleUiHeader(this.markAll);
    	this.callParent([true]);
    	this.maybeFireSelectionChange(true);
    },
    
    /**
     * SITOOLS PATCH : patched to enable MULTI selection mode (not enable by default)
     */
    processSelection: function(view, record, item, index, e){
        var me = this,
            checker = e.getTarget(me.checkSelector),
            mode;
            
        // checkOnly set, but we didn't click on a checker.
        if (me.checkOnly && !checker) {
            return;
        }

        if (checker) {
            mode = me.getSelectionMode();
            // dont change the mode if its single otherwise
            // we would get multiple selection
//            if (mode !== 'SINGLE') {
//                me.setSelectionMode('SIMPLE');
//            }
            me.selectWithEvent(index, e);
            me.setSelectionMode(mode);
        } else {
            me.selectWithEvent(index, e);
        }
    },
    
    
    getSelectedRanges : function () {
        
        var index = 1,
        ranges = [],
        currentRange = 0,
        tmpSelected = this.selected.clone();
        
        if(Ext.isEmpty(this.selected) && this.store.getTotalCount() == 0) {
            return [];
        }

        if (this.markAll) {
            return [ [ 0, this.store.getTotalCount() - 1 ] ];
        } 
        
        
        var lastIndex;
        tmpSelected.sort(function(o1, o2){
                if (o1 > o2) {
                    return 1;
                } else if (o1 < o2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        );
        tmpSelected.each(function(index) {
            if(Ext.isEmpty(lastIndex)){
                ranges[currentRange] = [index, index];
            }
            else {

                if (index - lastIndex === 1) {
                    ranges[currentRange][1] = index;
                } else {
                    currentRange++;
                    ranges[currentRange] = [index, index];
                }
            }
            
            lastIndex = index;
        }, this);
        
        return ranges;
    },
    
    clearSelections : function () {
        this.markAll = false;
        this.toggleUiHeader(this.markAll);
        this.callParent(arguments);
    }
    
    
});
