/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * ProjectGraph Module Controller
 * @class sitools.user.controller.modules.projectGraph.ProjectGraphController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.user.controller.modules.projectGraph.ProjectGraphController', {
    extend : 'Ext.app.Controller',
    
    views : ['modules.projectGraph.ProjectGraphView'],
    

    init : function () {
        this.control({
            "projectGraph" : {
                render : function (treepanel) {
                    var project = Ext.getStore('ProjectStore').getProject();
                    var projectAttachment = project.get("sitoolsAttachementForUsers");
                    
                    Ext.Ajax.request({
                        method : 'GET',
                        url : projectAttachment + "/graph",
                        scope : this,
                        success : function (ret) {
                            var Json = Ext.decode(ret.responseText);
                            if (!Json.success) {
                                Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                                return;
                            }
                            if (Json.graph && Json.graph.nodeList) {
                                treepanel.getRootNode().appendChild(Json.graph.nodeList);
                            }
                        },
                        callback : function () {
                            var color;
                            Ext.each(treepanel.view.getHeaderCt().items.items, function (columnHeader) {
                                if (treepanel.columnsConfig.containsKey(columnHeader.name)) {
                                    color = treepanel.columnsConfig.get(columnHeader.name).color;
                                    columnHeader.textEl.setStyle('color', color);
                                }
                            }, this);
                        }
                    });

                    //treepanel.view.getHeaderCt().items.items[2].textEl.setStyle('color', 'red');

                }
            } 
        });
        
        this.listen({
            store : {
                "#projectGraphTreeStore" : {
                    append : function (store, record) {
                        record.set("id", Ext.id());
                        
                        if (record.get("type") === "dataset") {
                            var icon = record.get("authorized") === "true" ? loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets.png" : loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png";
                            record.set('icon', icon);
                            record.set("leaf", true);

                        }
                        if (record.get("type") === "node") {
                            if(!Ext.isEmpty(record.get("image"))) {
                                record.set("icon", record.get("image").url);
                            }
                            record.set('iconCls', "graphNodeType");
                            record.set("readme", record.get("description"));
                            record.set("leaf", false);
                        }
                    }
                }
            }
        });
    }
});
