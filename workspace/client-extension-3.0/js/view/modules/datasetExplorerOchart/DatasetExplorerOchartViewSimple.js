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
/*global Ext, sitools, projectGlobal, commonTreeUtils, showResponse, document, i18n, loadUrl, SITOOLS_DEFAULT_PROJECT_IMAGE_URL, SitoolsDesk*/
/*
 * @include "../../env.js" 
 */

Ext.namespace('sitools.extension.view.modules.datasetExplorerOchart');

Ext.define('sitools.extension.view.modules.datasetExplorerOchart.DatasetExplorerOchartViewSimple', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.datasetExplorerOchartViewSimple',

    layout: {type: 'fit'},
    border: false,
    requires: [
        'sitools.extension.component.datasetExplorerOchart.OChartDragDrop',
        'sitools.extension.component.datasetExplorerOchart.OChart'
    ],
    autoDestroyStore: true,

    initComponent: function () {
        var me = this;
        if (!me.store) {
            Ext.apply(me, {
                store: me.buildStore()
            });
        }

        me.chartConfig = me.chartConfig || {};

        Ext.applyIf(me.chartConfig, {
            xtype: 'ochart',
            autoScroll: true,
            simpleSelect: true,
            rootVisible: false,
            store: me.store,
            plugins: [
                {
                    ptype: 'ochartdragdrop',
                    allowParentInserts: true,
                    containerScroll: true,
                    ddGroup: 'ochart-dd'
                }
            ],

            listeners: {
                scope: me,
                additem: me.onAddItem,
                removeitem: me.onRemoveItem,
                itemdblclick: me.onItemDblClick
            }
        });

        Ext.apply(me, {
            items: me.chartConfig
        });
        me.callParent(arguments);
    },

    buildStore: function () {
        var store = Ext.create('sitools.extension.view.modules.datasetExplorerOchart.NodesStore');
        return store;
    },

    onRender: function () {
        //console.log(this);
        var chart = this.down('ochart');
//       var view = this.up('viewport');
//        console.log(chart);
        chart.lineWeight = 2; //view.lineWeight;
        chart.lineColor = '#888888';//'#' + view.lineColor;
        chart.levelSpacing = 20;//view.levelSpacing;
        chart.nodeSpacing = 10;//view.nodeSpacing;
        chart.readOnly = true;//view.readOnly;
        chart.rootVisible = true;//view.rootVisible;
        chart.allowContainerDrop = false;//view.allowContainerDrop;
        chart.toolsVisible = true;//view.toolsVisible;

        this.callParent(arguments);
    },

    afterRender: function () {
        var me = this;
        var view = me.up('#mainView');
        //me.mon(view,'changelineweight', me.onChangeLineWeight, me);
        //me.mon(view,'changelinecolor', me.onChangeLineColor, me);
        //me.mon(view,'changelevelspace', me.onChangeLevelSpace, me);
        //me.mon(view,'changeitemspace', me.onChangeItemSpace, me);
        //me.mon(view,'changereadonly', me.onChangeReadOnly, me);
        //me.mon(view,'changerootvisible', me.onChangeRootVisible, me);
        //me.mon(view,'changecontainerdrop', me.onChangeContainerDrop, me);
        //me.mon(view,'changetools', me.onChangeTools, me);
        me.view = view;
        me.callParent(arguments);
    },

    onDestroy: function () {
        var me = this,
            view = me.view;
        me.mun(view, 'changelineweight', me.onChangeLineWeight, me);
        me.mun(view, 'changelinecolor', me.onChangeLineColor, me);
        me.mun(view, 'changelevelspace', me.onChangeLevelSpace, me);
        me.mun(view, 'changeitemspace', me.onChangeItemSpace, me);
        me.mun(view, 'changereadonly', me.onChangeReadOnly, me);
        me.mun(view, 'changerootvisible', me.onChangeRootVisible, me);
        me.mun(view, 'changecontainerdrop', me.onChangeContainerDrop, me);
        me.mun(view, 'changetools', me.onChangeTools, me);
        me.view = null;
        if (me.autoDestroyStore) {
            me.store.destroyStore();
        }
        me.store = null;
        me.callParent(arguments);
    },

    onItemDblClick: function (view, record, item, index, e) {
        if (view.readOnly) return;

        Ext.Msg.prompt('Edit Node', 'Type the node name', function (btn, text) {
            if (btn == 'ok') {
                record.set('text', text);
            }
        }, window, false, record.get('text'));
    },

    onAddItem: function (view, record, where, nodeEl) {
        Ext.Msg.prompt('New Node', 'Type the node name', function (btn, text) {
            if (btn == 'ok') {
                var newrec = {text: text, leaf: true};
                switch (where) {
                    case 'before':
                        var parent = record.parentNode;
                        newrec = parent.insertBefore(newrec, record);
                        break;
                    case 'after':
                        var node = record.nextSibling;
                        var parent = record.parentNode;
                        newrec = parent.insertBefore(newrec, node);
                        break;
                    case 'child':
                        newrec = record.appendChild(newrec);
                        record.expand(function () {
                            view.focusNode(newrec);
                        });
                        break;
                }
            }
        });
    },

    onRemoveItem: function (view, record, nodeEl) {
        Ext.Msg.confirm('Remove Item', 'Do you really want\'s remove this items?', function (btn, text) {
            if (btn == 'yes') {
                record.remove();
            }
        });
    },

    onChangeLineWeight: function (panel, weight) {
        var chart = this.down('ochart');
        chart.lineWeight = weight;
        chart.refresh();
    },

    onChangeLineColor: function (panel, color) {
        var chart = this.down('ochart');
        chart.lineColor = '#' + color;
        chart.refresh();
    },

    onChangeLevelSpace: function (panel, space) {
        var chart = this.down('ochart');
        chart.levelSpacing = space;
        chart.refresh();
    },

    onChangeItemSpace: function (panel, space) {
        var chart = this.down('ochart');
        chart.nodeSpacing = space;
        chart.refresh();
    },

    onChangeReadOnly: function (panel, value) {
        var chart = this.down('ochart');
        chart.readOnly = value;
    },

    onChangeRootVisible: function (panel, value) {
        var chart = this.down('ochart');
        chart.rootVisible = value;
        chart.refresh();
    },

    onChangeContainerDrop: function (panel, value) {
        var chart = this.down('ochart');
        chart.allowContainerDrop = value;
    },

    onChangeTools: function (panel, value) {
        var chart = this.down('ochart');
        chart.toolsVisible = value;
    },

    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id
        };
    }

});
