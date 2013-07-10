/**
 * Ext.ux.grid.livegrid.CheckboxSelectionModel Copyright (c) 2007-2012,
 * http://www.siteartwork.de
 * 
 * Ext.ux.grid.livegrid.CheckboxSelectionModel is licensed under the terms of
 * the GNU Open Source GPL 3.0 license.
 * 
 * Commercial use is prohibited. Visit <http://ext-livegrid.com> if you need to
 * obtain a commercial license.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 * 
 */
/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window, 
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE,
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT,
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, getColumnModel, loadUrl, getApp
*/
Ext.namespace('Ext.ux.grid.livegrid');

/**
 * @class Ext.ux.grid.livegrid.CheckboxSelectionModel
 * @extends Ext.ux.grid.livegrid.RowSelectionModel
 * @constructor
 * @param {Object}
 *            config
 * 
 * @author Thorsten Suckow-Homberg <ts@siteartwork.de>
 */
Ext.ux.grid.livegrid.CheckboxSelectionModel = Ext.extend(Ext.ux.grid.livegrid.RowSelectionModel, {

    /**
     * @cfg {Boolean} checkOnly <tt>true</tt> if rows can only be selected by
     *      clicking on the checkbox column (defaults to <tt>false</tt>).
     */
    /**
     * @cfg {Number} width The default width in pixels of the checkbox column
     *      (defaults to <tt>20</tt>).
     */
    width : 20,

    // private
    menuDisabled : true,
    sortable : false,
    fixed : true,
    dataIndex : '',
    id : 'checker',
    headerCheckbox : null,
    markAll : false,

    isColumn : true, // So that ColumnModel doesn't feed this through the
                        // Column constructor

    constructor : function (config) {
        Ext.apply(this, config);
        this.headerChecked = '<div id="qtip-checker" ext:qtip="' + i18n.get('label.deselectAll') + '" class="x-grid3-hd-checker">&#160;</div>';
        this.headerUnchecked = '<div id="qtip-checker" ext:qtip="' + i18n.get('label.selectAll') + '" class="x-grid3-hd-checker">&#160;</div>';

        if (!this.header) {
            this.header = this.headerUnchecked;
           //this.header = Ext.grid.CheckboxSelectionModel.prototype.header;
        }

        if (this.checkOnly) {
            this.handleMouseDown = Ext.emptyFn;
        }

        this.sortable = false;

        Ext.ux.grid.livegrid.CheckboxSelectionModel.superclass.constructor.call(this);

    },

    // private
    initEvents : function () {
        Ext.ux.grid.livegrid.CheckboxSelectionModel.superclass.initEvents.call(this);

        this.grid.view.on('reset', function (gridView, forceReload) {
            this.headerCheckbox = new Ext.Element(gridView.getHeaderCell(this.grid.getColumnModel().getIndexById(this.id)).firstChild);
            if (this.markAll && forceReload === false) {
                this.headerCheckbox.addClass('x-grid3-hd-checker-on');
            }
        }, this);
        
        
        this.grid.view.addListener('buffer', function() {
            if(this.markAll){
                this.selectAll();
            }
        }, this);

        // this.grid.on('render', function(){
        // Ext.fly(this.grid.getView().innerHd).on('mousedown',
        // this.onHdMouseDown, this);
        // }, this);

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
                if (this.isSelected(index)) {
                    if (this.markAll) {
                        // Show a dialog using config options:
                        this.selectRow(index, false);
                    } else {
                        if (e.shiftKey && !this.singleSelect && this.last !== false) {
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
                    if (e.shiftKey && !this.singleSelect && this.last !== false) {
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
        if (t.className == 'x-grid3-hd-checker' && !this.headerCheckbox) {
            this.headerCheckbox = new Ext.Element(t.parentNode);
        }

        if (t.className == 'x-grid3-hd-checker') {
            e.stopEvent();
            var hd = Ext.fly(t.parentNode);
            var isChecked = hd.hasClass('x-grid3-hd-checker-on');
            if (isChecked) {
                hd.removeClass('x-grid3-hd-checker-on');
                hd.child('div').dom.outerHTML = this.headerUnchecked;
                this.clearSelections();
            } else {
                hd.addClass('x-grid3-hd-checker-on');
                hd.child('div').dom.outerHTML = this.headerChecked;
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
        // if (e.shiftKey) {
        // return;
        // }

//        this.markAll = false;

        if (this.headerCheckbox) {
            this.headerCheckbox.removeClass('x-grid3-hd-checker-on');
        }

        Ext.ux.grid.livegrid.CheckboxSelectionModel.superclass.handleMouseDown.call(this, g, rowIndex, e);
    },

    /**
     * Overriden to clear header sort state
     */
    clearSelections : function (fast) {
        if (this.isLocked()) {
            return;
        }

        this.markAll = false;

        if (this.headerCheckbox) {
            this.headerCheckbox.removeClass('x-grid3-hd-checker-on');
        }

        //always clearSelections in fast mode
        Ext.ux.grid.livegrid.CheckboxSelectionModel.superclass.clearSelections.call(this, true);
        
        if (!fast) {
            //done only on user change selection, not at livegrid startup
            this.fireEvent("selectionchange", this);
            this.grid.getView().refresh();
            this.grid.getView().processRows(0, false);
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
    } 

});