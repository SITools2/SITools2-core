/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, extColModelToStorage, projectId, userStorage, window,   
GeoExt, userLogin, alertFailure, DEFAULT_LIVEGRID_BUFFER_SIZE, projectGlobal, SitoolsDesk, DEFAULT_ORDER_FOLDER, DEFAULT_PREFERENCES_FOLDER, getColumnModel */

Ext.namespace('sitools.user.component.dataviews.cartoView');



/**
 */
sitools.user.component.dataviews.cartoView.featureSelectionModel = function (config) {
    this.width = 20;

    // private
    this.menuDisabled = true;
    this.sortable = false;
    this.fixed = true;
    this.dataIndex = '';
    this.id = 'checker';
    this.headerCheckbox = null;
    this.markAll = false;
   
    this.isColumn = true; // So that ColumnModel doesn't feed this through the
                        // Column constructor
    
    Ext.apply(this, config);
    
    if (!this.header) {
        this.header = Ext.grid.CheckboxSelectionModel.prototype.header;
    }

    if (this.checkOnly) {
        this.handleMouseDown = Ext.emptyFn;
    }

    this.sortable = false;
    
    
    sitools.user.component.dataviews.cartoView.featureSelectionModel.superclass.constructor.call(this, config);
    this.addEvents('gridFeatureSelected');
    
};

Ext.extend(sitools.user.component.dataviews.cartoView.featureSelectionModel, GeoExt.grid.FeatureSelectionModel, {
    
    //private
    initEvents : function () {
        sitools.user.component.dataviews.cartoView.featureSelectionModel.superclass.initEvents.call(this);
    
        this.grid.view.on('refresh', function (gridView, forceReload) {
            this.headerCheckbox = new Ext.Element(gridView.getHeaderCell(this.grid.getColumnModel().getIndexById(this.id)).firstChild);
            if (this.markAll && forceReload === false) {
                this.headerCheckbox.addClass('x-grid3-hd-checker-on');
            }
        }, this);
        
        
    
        // this.grid.on('render', function(){
        // Ext.fly(this.grid.getView().innerHd).on('mousedown',
        // this.onHdMouseDown, this);
        // }, this);
        

        this.grid.getBottomToolbar().on('change', function (tb, pageData) {
            this.locked = false;
            if (this.markAll) {
                this.selectAll();
            }            
        }, this);
        
        this.grid.getBottomToolbar().on('beforechange', function (tb, pageData) {
            this.locked = true;
        }, this);
        
        Ext.grid.CheckboxSelectionModel.prototype.initEvents.call(this);
    },
    
    /**
     * @private Process and refire events routed from the GridView's
     *          processEvent method.
     */
    processEvent : function (name, e, grid, rowIndex, colIndex) {
        if (name == 'mousedown') {
            this.onMouseDown(e, e.getTarget());
            return false;
        } else {
            return Ext.grid.Column.prototype.processEvent.apply(this, arguments);
        }
    },
    
    // private
    onMouseDown : function (e, t) {
        if (e.button === 0 && t.className == 'x-grid3-row-checker') {
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            if (row) {
                var index = row.rowIndex;
                var isSelected = this.isSelected(index);
                var shiftKey = e.shiftKey;
                
                
                if (isSelected) {
                    if (this.markAll) {
                        // Show a dialog using config options:
                        this.selectRow(index, false);
                    }
                    else {
                        if (shiftKey && !this.singleSelect && this.last !== false) {
                            var last = this.last;
                            this.deselectRange(last, index);
                            this.last = last;
                            // view.focusRow(rowIndex);
                            // this.fireEvent('handleMouseDown', this);
                        } else {
                            this.deselectRow(index);
                        }
                    }
                } else {
                    if (shiftKey && !this.singleSelect && this.last !== false) {
                        var last = this.last;
                        this.selectRange(last, index, true);
                        this.last = last;
                        // view.focusRow(rowIndex);
                        // this.fireEvent('handleMouseDown', this);
                    } else {
                        this.selectRow(index, true);
                    }
                    // this.grid.getView().focusRow(index);
                }
                
                if (this.headerCheckbox) {
                    this.markAll = false;
                    this.headerCheckbox.removeClass('x-grid3-hd-checker-on');
                }
                
            }
        }
    },
    
    
    // private
    onHdMouseDown : function (e, t) {

        if (t.className === 'x-grid3-hd-checker' && !this.headerCheckbox) {
            this.headerCheckbox = new Ext.Element(t.parentNode);
        }
    
        if (t.className === 'x-grid3-hd-checker') {
            e.stopEvent();
            var hd = Ext.fly(t.parentNode);
            var isChecked = hd.hasClass('x-grid3-hd-checker-on');
            if (isChecked) {
                hd.removeClass('x-grid3-hd-checker-on');
                this.clearSelections();
            } else {
                hd.addClass('x-grid3-hd-checker-on');
                this.selectAll();
            }
        }
    
    },
    
    // private
    renderer : function (v, p, record) {
        return Ext.grid.CheckboxSelectionModel.prototype.renderer.call(this, v, p, record);
    },
    
    // -------- overrides
    
    /**
     * Overriden to prevent selections by shift-clicking
     */
    handleMouseDown : function (g, rowIndex, e) {
        this.markAll = false;
    
        if (this.headerCheckbox) {
            this.headerCheckbox.removeClass('x-grid3-hd-checker-on');
        }
    
        sitools.user.component.dataviews.cartoView.featureSelectionModel.superclass.handleMouseDown.call(this, g, rowIndex, e);
        this.fireEvent("gridFeatureSelected", g, rowIndex, e);
    },
    
    

    /**
     * Clears all selections.
     */
    clearSelections : function (fast) {
        if (this.isLocked()) {
            return;
        }

        this.markAll = false;

        if (this.headerCheckbox) {
            this.headerCheckbox.removeClass('x-grid3-hd-checker-on');
        }
        
        if (fast !== true) {
            var ds = this.grid.store, s = this.selections;
            this.silent = true;
            s.each(function (r) {
                this.deselectRow(ds.indexOfId(r.id));
            }, this);
            s.clear();
            this.silent = false;
            this.deselectRow(0);
        } else {
            this.selections.clear();
        }
        this.last = false;
    },
    
  
    
    /**
     * Selects all rows if the selection model
     * {@link Ext.grid.AbstractSelectionModel#isLocked is not locked}.
     */
    selectAll : function () {
        if (this.isLocked()) {
            return;
        }
        this.selections.clear();
        
        this.markAll = true;

        this.silent = true;
        for (var i = 0, len = this.grid.store.getCount(); i < len - 1; i++) {
            this.selectRow(i, true);
        }
        this.silent = false;
        this.selectRow(len-1, true);
        
    
        if (this.headerCheckbox) {
            this.headerCheckbox.addClass('x-grid3-hd-checker-on');
        }
    },
    
    /**
     * Selects a range of rows if the selection model
     * {@link Ext.grid.AbstractSelectionModel#isLocked is not locked}.
     * All rows in between startRow and endRow are also selected.
     * @param {Number} startRow The index of the first row in the range
     * @param {Number} endRow The index of the last row in the range
     * @param {Boolean} keepExisting (optional) True to retain existing selections
     */
    selectRange : function(startRow, endRow, keepExisting){
        var i;
        if (this.locked) {
            return;
        }

        if (!keepExisting) {
            this.clearSelections();
        }

        if (startRow <= endRow) {
            this.silent = true;
            for (i = startRow; i < endRow; i++) {
                this.selectRow(i, true);
            }
            this.silent = false;
            this.selectRow(endRow, true);
        } else {
            this.silent = true;
            for (i = startRow; i > endRow; i--) {
                this.selectRow(i, true);
            }
            this.silent = false;
            this.selectRow(endRow, true);
        }
    },
    
    
    //SITOOLS MG, overide deselect row to prevent event calls when the SelectionModel is silent
    /**
     * Deselects a row.  Before deselecting a row, checks if the selection model
     * {@link Ext.grid.AbstractSelectionModel#isLocked is locked}.
     * If this check is satisfied the row will be deselected and followed up by
     * firing the {@link #rowdeselect} and {@link #selectionchange} events.
     * @param {Number} row The index of the row to deselect
     * @param {Boolean} preventViewNotify (optional) Specify <tt>true</tt> to
     * prevent notifying the view (disables updating the selected appearance)
     */
    deselectRow : function (index, preventViewNotify) {
        if (this.isLocked()) {
            return;
        }
        if (this.last == index) {
            this.last = false;
        }
        if (this.lastActive === index) {
            this.lastActive = false;
        }
        var r = this.grid.store.getAt(index);
        if (r) {
            this.selections.remove(r);
            if (!preventViewNotify) {
                this.grid.getView().onRowDeselect(index);
            }
            if (!this.silent) {
                this.fireEvent('rowdeselect', this, index, r);
                this.fireEvent('selectionchange', this);
            }
        }
    },

});
