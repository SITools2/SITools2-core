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
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/datasets/datasets.js"
 * @include "../../components/datasets/projectForm.js"
 */

/**
 * Datasets Module :
 * Displays All Datasets depending on datasets attached to the project.
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.view.component.datasets.dataviews.LivegridView', {
    extend: 'Ext.grid.Panel',

    requires: ['sitools.user.view.component.datasets.dataviews.selectionModel.CheckboxModel',
    	'sitools.user.view.component.datasets.services.ServiceToolbarView',
        'sitools.user.view.component.datasets.dataviews.paging.LivegridPagingToolbar'],

    mixins: {
        datasetView: 'sitools.user.view.component.datasets.dataviews.AbstractDataview'
    },

    alias: 'widget.livegridView',
    layout: 'fit',
    autoScroll: true,
    bodyBorder: false,
    border: false,
    plugins: {
        pluginId: 'renderer',
        ptype: 'bufferedrenderer',
        trailingBufferZone: 20,  // Keep 20 rows rendered in the table behind scroll
        leadingBufferZone: 50  // Keep 50 rows rendered in the table ahead of scroll,
    },
    viewConfig: {
        enableTextSelection: true,
        getRowClass: function (record, index) {
            if (Ext.isEmpty(this.gridId)) {
                var grid = this.up('livegridView');
                this.gridId = grid.id;
            }
            return 'rowHeight_' + this.gridId;
        }
    },

    config: {
        ranges: null,
        nbRecordsSelection: null,
        isModifySelection: null
    },

    initComponent: function () {
        //add a custom css class if the lineHeight is configured (will be removed upon component destroy)
        if (!Ext.isEmpty(this.dataviewConfig) && !Ext.isEmpty(this.dataviewConfig.lineHeight)) {
            var css = Ext.String.format(".rowHeight_{0} {height : {1}px;}", this.id, this.dataviewConfig.lineHeight);
            Ext.util.CSS.createStyleSheet(css, this.id);
        }

        if (!Ext.isEmpty(this.dataviewConfig.autoFitColumn)) {
            if (JSON.parse(this.dataviewConfig.autoFitColumn)) {
                Ext.apply(this.viewConfig, {
                    listeners: {
                        refresh: function (dataview) {
                            Ext.each(dataview.panel.columns, function (column) {
                                column.autoSize();
                            });
                        }
                    }
                });
            }

        }

        this.selModel = new Ext.create("sitools.user.view.component.datasets.dataviews.selectionModel.SitoolsCheckboxModel", {
            checkOnly: true,
            pruneRemoved: false,
            gridView: this,
            showHeaderCheckbox: true,
            mode: 'MULTI'
        });

        this.bbar = Ext.create("sitools.user.view.component.datasets.dataviews.paging.LivegridPagingToolbar", {
            xtype: 'livegridpagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: i18n.get('paging.display'),
            emptyMsg: i18n.get('paging.empty'),
            selectedMessage: i18n.get("paging.selectedMsg"),
            grid: this
        });

        this.tbar = Ext.create("sitools.user.view.component.datasets.services.ServiceToolbarView", {
            enableOverflow: true,
            datasetUrl: this.dataset.sitoolsAttachementForUsers,
            columnModel: this.dataset.columnModel
        });

        this.callParent(arguments);
    },

    getGrid: function () {
        return this;
    },

    //generic method
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons: function () {
        var array = [];
        var colMenu = this.headerCt.getColumnMenu(this.headerCt);

        var columnStore = Ext.create('Ext.data.JsonStore', {
            fields: ['text', 'menuitem'],
            remoteFilter: false
        });

        Ext.each(colMenu, function (menuitem) {
            columnStore.add({
                text: menuitem.text,
                menuitem: menuitem
            })
        });

        var columnItemId = 'columnItem-' + Ext.id();
        var columnItem = {
            itemId: columnItemId,
            text: this.headerCt.columnsText,
            cls: this.headerCt.menuColsIcon,
            hideOnClick: false,
            tooltip: i18n.get('label.addOrDeleteColumns'),
            menu: {
                xtype: 'menu',
                border: false,
                plain: true,
                items: colMenu
            },
            name: "columnsButton"
        };
        array.push(columnItem);

        var searchField = Ext.create('Ext.form.field.Text', {
            emptyText: i18n.get('label.searchAColumn'),
            enableKeyEvents: true,
            store: columnStore,
            columnItemId: columnItemId
        });

        searchField.on('keyup', function (textfield, e) {
            var value = textfield.getValue();
            textfield.store.clearFilter(true);
            var menu = Ext.ComponentQuery.query('#' + columnItemId + ' menu')[0];

            if (value.length <= 1) {
                Ext.each(menu.items.items, function (menuitemMenu) {
                    menuitemMenu.setVisible(true);
                });
                return;
            }

            textfield.store.filter("text", new RegExp(value));

            Ext.suspendLayouts();
            Ext.each(menu.items.items, function (menuitemMenu) {
                if (menuitemMenu.xtype != 'textfield') {
                    menuitemMenu.setVisible(false);
                }

                textfield.store.each(function (menuitemData) {
                    var menuitemStore = menuitemData.get('menuitem');
                    if (menuitemStore.text == menuitemMenu.text) {
                        menuitemMenu.setVisible(true);
                    }
                });
            });
            Ext.resumeLayouts(true);
        }, this, {
            delay: 0
        });

        colMenu.splice(0, 0, searchField);

        array.push({
            name: "tipsLivegrid",
            icon: loadUrl.get('APP_URL') + '/common/res/images/icons/information.gif'
        });

        return array;
    }
});
