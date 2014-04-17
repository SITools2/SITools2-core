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
Ext.namespace('sitools.component.graphs');

Ext.define('sitools.component.graphs.graphsCrudPanel', { 
    extend : 'Ext.panel.Panel',
	alias : 'widget.s-graphs',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.GRAPHS,
//    autoScroll: true,
    // loadMask: true,
    initComponent : function () {
        this.urlProjects = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');

        this.storeProjects = Ext.create("Ext.data.JsonStore", {
            fields : [ 'id', 'name' ],
            proxy : {
                type : 'ajax',
                url : this.urlProjects,
                simpleSortMode : true,
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
            autoLoad : true
        });

        this.comboProjects = Ext.create("Ext.form.ComboBox", {
            store : this.storeProjects,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectProject'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    var tree = this.down('treepanel');
                    if (!Ext.isEmpty(tree)) {
                        tree.getSelectionModel().deselectAll();
                    }
                    
                    this.loadGraph(rec[0].get("id"));
                }

            }
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboProjects, {
                text : i18n.get('label.saveGraph'),
                scope : this,
                handler : this._onSave,
                xtype : 's-menuButton',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png'
            }, {
                text : i18n.get('label.deleteGraph'),
                scope : this,
                handler : this._onDelete,
                xtype : 's-menuButton',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png'
            } ]
        };
        
        this.lbar = {
            xtype : 'toolbar',
            name : 'menuTreeToolbar',
            width : 125,
            defaults : {
                scope : this
            },
            layout : {
                pack : 'start',
                align : 'stretch'
            },
            items : []
        };

        
        this.buttons = [ ];
        
        
        sitools.component.graphs.graphsCrudPanel.superclass.initComponent.call(this);

    },

    loadGraph : function (projectId) {
        var index = this.storeProjects.find("id", projectId);
        var rec = this.storeProjects.getAt(index);
        var projectName = rec.data.name;

        this.removeAll();

        this.tree = Ext.create("sitools.component.graphs.graphsCrudTreePanel", {
            name : projectName,
            projectId : projectId,
            graphsCrud : this
        });
        
        
        this.add(this.tree);
//        this.doLayout();

    },

    _onSave : function () {

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
//            var idGraph = projectId;

            var jsonReturn = {
                nodeList : tree,
                id : idGraph
            };
            // var tree = this.getAllNodes(root,array);

            var method = (!Ext.isEmpty(idGraph)) ? "PUT" : "POST";

            Ext.Ajax.request({
                url : this.urlProjects + "/" + projectId + "/graph",
                method : method,
                scope : this,
                jsonData : jsonReturn,
                success : function (ret) {
                    // check for the success of the request
                    var data = Ext.decode(ret.responseText);
                    if (!data.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), data.message);
                        return false;
                    } else {
                        popupMessage("", i18n.get('label.graphSaved'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
                        this.loadGraph(projectId);
                    }
                },
                failure : alertFailure
            });
        } else {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("warning.noselection"));
        }
    },

    getAllNodes : function (root, parent) {
        var node = {};
        if (Ext.isEmpty(root)) {
            return;
        } else if (root.isLeaf()) {
            node = {
                text : root.get('text'),
                datasetId : root.get('datasetId'),
                visible : root.get('visible'), 
                status : root.get('status'), 
                nbRecord : root.get('nbRecord'),
                imageDs : root.get('imageDs'),
                readme : root.get('readme'),
                url : root.get('url'),
                leaf : true,
                type : root.get('type')
            };
            parent.push(node);
        } else {
            node = {
                text : root.get('text'),
                image : root.get('image'),
                description : root.get('description'),
                children : [],
                type : root.get('type'),
                leaf : false
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

    _onReset : function () {
        var projectId = this.comboProjects.getValue();
        if (!Ext.isEmpty(projectId)) {
            Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : i18n.get('label.graphs.reset'),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        this.doReset();
                    }
                }
            });
        } else {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("warning.noselection"));
        }

    },

    doReset : function () {
        if (!Ext.isEmpty(this.tree)) {
            var root = this.tree.getRootNode();
            root.removeAll();
            this.tree.loadStore();
        }
    },

    _onDelete : function () {
        var projectId = this.comboProjects.getValue();
        if (!Ext.isEmpty(projectId)) {
            var projectName = this.storeProjects.getById(projectId).get("name");
            Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : Ext.String.format(i18n.get('label.graphs.delete'), projectName),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        this.doDelete(projectId);
                    }
                }
            });
        } else {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("warning.noselection"));
        }
    },

    doDelete : function (projectId) {
        Ext.Ajax.request({
            url : this.urlProjects + "/" + projectId + "/graph",
            method : "DELETE",
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.loadGraph(projectId);
                }
            }
        });

    }

});