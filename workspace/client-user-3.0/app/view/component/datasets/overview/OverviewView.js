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

/**
 * Dataset Overview to display dataset on Fixed mode
 * @class sitools.user.view.component.datasets.overview.OverviewView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.component.datasets.overview.OverviewView', {
    extend: 'Ext.panel.Panel',

    requires: [],

    alias: 'widget.overviewView',

    layout : {
        type : 'hbox',
        align : 'stretch'
    },

    bodyBorder: false,
    border: false,

    config: {
        DEFAULT_HEIGHT: 600,
        DEFAULT_WIDTH: 800,
        DEFAULT_WIDTH_FORMS_PANEL: 400,
        DEFAULT_HEIGHT_SEMANTIC_PANEL: 200,
        DEFAULT_WIDTH_EAST_PANEL: 300,
        dataset : null,
        forceShowDataset : false,
        outsideConfig : null,
        formId : null,
        formsPanelWidth : null
    },

    /**
     *
     */
    initComponent: function () {
        this.setDataset(this.dataset);
        this.setForceShowDataset(this.isModifySelection);

        var semanticPanel = Ext.create("Ext.panel.Panel", {
            title: i18n.get('label.semantic'),
            layout: "fit",
            collapsible: true,
            split: true,
            border : false,
            collapseDirection : "bottom",
            hidden: true,
            height: this.DEFAULT_HEIGHT_SEMANTIC_PANEL,
            itemId : 'semantic'
        });

        var eastPanel = Ext.create("Ext.panel.Panel", {
            layout : {
                type : 'vbox',
                align : 'stretch'
            },
            width: this.DEFAULT_WIDTH_EAST_PANEL,
            border : false,
            collapsible: true,
            collapseDirection : "right",
            items : [semanticPanel],
            itemId : 'eastPanel'
        });

        var eastSplitter = Ext.create("Ext.resizer.Splitter", {
            style : 'background-color:#EBEBEB;',
            itemId : 'eastSplitter'
        });

        var westSplitter = Ext.create("Ext.resizer.Splitter", {
            style : 'background-color:#EBEBEB;',
            itemId : 'westSplitter',
            hidden : true
        });

        var mainPanel = Ext.create("Ext.panel.Panel", {
            flex : 1,
            layout: "fit",
            itemId : 'mainPanel',
            border : false
        });

        var westPanelConf = {
            layout: "fit",
            itemId : 'westPanel',
            border : false,
            hidden : true,
            collapseDirection : "left",
            collapsible : true
        };

        if(this.getFormsPanelWidth() && !this.getForceShowDataset()){
            westPanelConf.width = this.getFormsPanelWidth();
            eastPanel.flex = 1;
        }
        else {
            westPanelConf.flex = 1;
        }

        var westPanel = Ext.create("Ext.panel.Panel", westPanelConf);

        this.items = [westPanel, westSplitter, mainPanel, eastSplitter, eastPanel];


        this.callParent(arguments);
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return this.component._getSettings();
    }
});
