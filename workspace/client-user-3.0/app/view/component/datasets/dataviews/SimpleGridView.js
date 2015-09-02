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
Ext.define('sitools.user.view.component.datasets.dataviews.SimpleGridView', {
    extend: 'Ext.grid.Panel',

    requires: ['Ext.selection.CheckboxModelView'],

    mixins: {
        datasetView: 'sitools.user.view.component.datasets.dataviews.AbstractDataview'
    },

    alias: 'widget.livegridView',
    layout: 'fit',
    autoScroll: true,
    bodyBorder: false,
    border: false,
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

        this.selModel = new Ext.create("Ext.selection.CheckboxModel", {
            checkOnly: true,
            pruneRemoved: false,
            gridView: this,
            showHeaderCheckbox: true,
            mode: 'MULTI'
        });

        this.bbar = Ext.create("Ext.toolbar.Paging", {
            //xtype: 'livegridpagingtoolbar',
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

        array.push({
            itemId: 'columnItem',
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
        });

        array.push({
            name: "tipsLivegrid",
            icon: loadUrl.get('APP_URL') + '/common/res/images/icons/information.gif'
        });

        return array;
    }
});
