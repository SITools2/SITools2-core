/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse*/

/**
 * DatasetExplorerOchart Module Controller
 * @class sitools.extension.controller.modules.datasetExplorerOchart.DatasetExplorerOchartController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.extension.controller.modules.datasetExplorerOchart.DatasetExplorerOchartController', {
    extend: 'Ext.app.Controller',

    views: ['modules.datasetExplorerOchart.DatasetExplorerOchartView'],

    init: function () {
        this.control({
            "DatasetExplorerOchart": {
                render: function (ochart) {
                    var project = Ext.getStore('ProjectStore').getProject();
                    var projectAttachment = project.get("sitoolsAttachementForUsers");

                    Ext.Ajax.request({
                        method: 'GET',
                        url: projectAttachment + "/graph",
                        scope: this,
                        success: function (ret) {
                            var Json = Ext.decode(ret.responseText);
                            if (!Json.success) {
                                Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                                return;
                            }
                            if (Json.graph && Json.graph.nodeList) {
                                ochart.store.getRootNode().appendChild(Json.graph.nodeList);
                                ochart.store.getRootNode().collapseChildren();
                                //ochart.store.setRootNode(Json.graph.nodeList);
                                //console.log(ochart.store.getRootNode());
                                var root = ochart.store.getRootNode();
                                for (var i = 0; i < root.childNodes.length; i++) {
                                    this.collapseRecursive(root.childNodes[i]);
                                }
                            }
                        }
                    });


                }
            }
        });

        this.listen({
            store: {
                "#datasetExplorerOchartTreeStore": {
                    append: function (store, record) {
                        //record.set("id", Ext.id());
                        if (record.get("type") === "dataset") {
                            var icon = record.get("authorized") === "true" ? loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets.png" : loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png";
                            record.set('icon', icon);
                            //record.set("leaf", true);

                        }
                        if (record.get("type") === "node") {
                            if (!Ext.isEmpty(record.get("image"))) {
                                record.set("icon", record.get("image").url);
                            }
                            //record.set('iconCls', "graphNodeType");
                            //record.set("readme", record.get("description"));
                            //record.set("expanded", false);
                            //record.set("leaf", false);
                            //console.log(record);
                            //record.collapseChildren();
                        }
                    }

                }
            }
        });
    },
    collapseRecursive: function (node) {
        if (node.childNodes != []) {
            for (var i = 0; i < node.childNodes.length; i++) {
                this.collapseRecursive(node.childNodes[i]);
                node.childNodes[i].collapse();
            }
        }
    }
});
