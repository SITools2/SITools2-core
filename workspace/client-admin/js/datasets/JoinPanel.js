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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/

/*
 * @include "joinConditionWin.js"
 * @include "joinTableWin.js"
 *
 */
/**
 * The panel that displays the sql join wizard
 * @cfg {String} datasetId (required) the dataset Id
 * @cfg {Ext.grid.GridPanel} datasetSelectTables (required) The Panel that shows dataset tables
 * @cfg {string} action (required)
 * @cfg {Ext.data.Store} storeColumnDataset (required) The store of the dataset columns
 * @class sitools.admin.datasets.JoinPanel
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.JoinPanel', {
    extend: 'Ext.panel.Panel',
    border: false,
    urlJDBC: loadUrl.get('APP_URL') + "/",
    autoScroll: true,
    layout: 'fit',
    height: 180,

    requires: ['sitools.admin.datasets.JoinCrudTreePanel'],

    initComponent: function () {
        this.title = "Join Configuration";
        this.tree = Ext.create("sitools.admin.datasets.JoinCrudTreePanel", this);
        this.padding = 5;
        this.items = [this.tree];
        this.addEvents('contextmenu');

        this.callParent(arguments);
    },

    loadGraph: function () {

        this.removeAll();

        this.tree = Ext.create('sitools.admin.datasets.JoinCrudTreePanel', {
            datasetId: this.datasetId
        });

        this.tree.getRootNode().expand(true);
        this.add(this.tree);
    },

    _onSave: function () {

        var projectId = this.comboProjects.getValue();
        if (!Ext.isEmpty(projectId)) {
            var root = this.tree.getRootNode();
            var tree = [];

            var childs = root.childNodes;
            var i;
            for (i = 0; i < childs.length; i++) {
                this.getAllNodes(childs[i], tree);
            }

            var idGraph = this.tree.getIdGraph();

            var jsonReturn = {
                nodeList: tree,
                id: idGraph
            };
            // var tree = this.getAllNodes(root,array);

            var method = (!Ext.isEmpty(idGraph)) ? "PUT" : "POST";

            Ext.Ajax.request({
                url: this.urlProjects + "/" + projectId + "/graph",
                method: method,
                scope: this,
                jsonData: jsonReturn,
                success: function (ret) {
                    // check for the success of the request
                    var data = Ext.decode(ret.responseText);
                    if (!data.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), data.message);
                        return false;
                    } else {
                        popupMessage("",
                            i18n.get('label.graphSaved'),
                            loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_active.png');

                        this.loadGraph(projectId);
                    }
                },
                failure: alertFailure
            });
        } else {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("warning.noselection"));
        }
    },

    getAllNodes: function (root, parent) {
        var node = {};
        if (Ext.isEmpty(root)) {
            return;
        } else if (root.isLeaf()) {
            node = {
                text: root.text,
                predicat: root.predicat,
                leaf: root.leaf
            };
            parent.push(node);
        } else {

            node = {
                text: root.text,
                children: [],
                type: root.attributes.type,
                typeJointure: root.attributes.typeJointure,
                table: root.attributes.table,
                leaf: false
            };
            parent.push(node);

            // we call recursively getAllNodes to get all childNodes
            var childs = root.childNodes;
            var i;
            for (i = 0; i < childs.length; i++) {
                this.getAllNodes(childs[i], node.children);
            }
        }
    },


    buildDefault: function () {
        // if (this.action == "create") {
        this.tree.buildDefault();
        // }
    },
    deleteJoinPanelItems: function () {
        this.tree.deleteJoinPanelItems();
    }

});

//Ext.apply(Ext.tree.TreePanel.nodeTypes, {
//	"sync" : Ext.tree.TreeNode
//});
