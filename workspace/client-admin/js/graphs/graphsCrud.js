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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.component.graphs');

Ext.define('sitools.component.graphs.graphsCrudPanel', { extend : 'Ext.panel.Panel',
	alias : 'widget.s-graphs',
    border : false,
    height : 300,
    id : ID.BOX.GRAPHS,
    autoScroll: true,
    // loadMask: true,

    initComponent : function () {
        this.urlProjects = loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL');

        this.storeProjects = new Ext.data.JsonStore({
            fields : [ 'id', 'name' ],
            url : this.urlProjects,
            root : "data",
            autoLoad : true
        });

        this.comboProjects = new Ext.form.ComboBox({
            store : this.storeProjects,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectProject'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.loadGraph(rec.data.id);
                }

            }
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboProjects, {
                text : i18n.get('label.save'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
                handler : this._onSave,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.reset'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_refresh.png',
                handler : this._onReset,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
                xtype : 's-menuButton'
            } ]
        };

        this.buttons = [ {
            text : i18n.get('label.save'),
            scope : this,
            handler : this._onSave
        }, {
            text : i18n.get('label.reset'),
            scope : this,
            handler : this._onReset
        }, {
            text : i18n.get('label.delete'),
            scope : this,
            handler : this._onDelete
        } ];

        sitools.component.graphs.graphsCrudPanel.superclass.initComponent.call(this);

    },

    loadGraph : function (projectId) {
        var index = this.storeProjects.find("id", projectId);
        var rec = this.storeProjects.getAt(index);
        var projectName = rec.data.name;

        this.removeAll();

        this.tree = new sitools.component.graphs.graphsCrudTreePanel({
            name : projectName,
            projectId : projectId
        });
        this.tree.getRootNode().expand(true);
        this.add(this.tree);
        this.doLayout();

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
                        var tmp = new Ext.ux.Notification({
                            iconCls : 'x-icon-information',
                            title : i18n.get('label.information'),
                            html : i18n.get('label.graphSaved'),
                            autoDestroy : true,
                            hideDelay : 1000
                        }).show(document);
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
                text : root.text,
                datasetId : root.attributes.datasetId,
                visible : root.attributes.visible, 
                status : root.attributes.status, 
                nbRecord : root.attributes.nbRecord,
                imageDs : root.attributes.imageDs,
                readme : root.attributes.readme,
                url : root.attributes.url,
                leaf : true,
                type : root.attributes.type
            };
            parent.push(node);
        } else {

            node = {
                text : root.text,
                image : root.attributes.image,
                description : root.attributes.description,
                children : [],
                type : root.attributes.type,
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
            var tot = Ext.Msg.show({
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
            root.removeAll(true);
            this.tree.getLoader().load(root, function () {
                root.expand(true);
            }, this);
        }
    },

    _onDelete : function () {
        var projectId = this.comboProjects.getValue();
        if (!Ext.isEmpty(projectId)) {
            var tot = Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : i18n.get('label.graphs.delete'),
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

Ext.define('sitools.component.graphs.graphsCrudTreePanel', { extend : 'Ext.tree.Panel',

    idGraph : null,
    loader : null,
    projectId : null,

    constructor : function (config) {
        this.loader = new sitools.component.graphs.graphTreeLoader({
            requestMethod : 'GET',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL') + "/" + config.projectId + "/graph",
            root : 'graph.nodeList'
        });
        /*
         * this.root = { text : config.name, children : [], nodeType:'async' };
         */
        this.projectId = config.projectId;
        
        config = Ext.apply({
            rootVisible : true,
            loader : this.loader,
            layout : 'fit',
            enableDD: true,           
            root : {
                nodeType : 'async',
                text : config.name
            },
            contextMenuRoot : new Ext.menu.Menu({
                items : [ {
                    id : 'create-node',
                    text : i18n.get("label.createNode"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png'
                }, {
                    id : 'add-dataset',
                    text : i18n.get("label.addDataset"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png'
                } ],
                listeners : {
                    scope : this,
                    itemclick : this._cxtMenuHandler
                }
            }),
            contextMenuNode : new Ext.menu.Menu({
                items : [ {
                    id : 'create-node',
                    text : i18n.get("label.createNode"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png'
                }, {
                    id : 'add-dataset',
                    text : i18n.get("label.addDataset"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png'
                }, {
                    id : 'edit-node',
                    text : i18n.get("label.modify"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png'
                }, {
                    id : 'delete-node',
                    text : i18n.get("label.delete"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png'
                } ],
                listeners : {
                    scope : this,
                    itemclick : this._cxtMenuHandler
                }
            }),
            contextMenuLeaf : new Ext.menu.Menu({
                items : [ {
                    id : 'edit-node',
                    text : i18n.get("label.modify"),
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png'
                }, {
                    id : 'delete-node',
                    text : i18n.get("label.delete"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png'
                } ],
                listeners : {
                    scope : this,
                    itemclick : this._cxtMenuHandler
                }
            }),
            listeners : {
                scope : this,
                contextmenu : function (node, e) {
                    // Register the context node with the menu so that a Menu
                    // Item's handler function can access
                    // it via its parentMenu property.
                    node.select();
                    var c;
                    if (node.id == this.root.id) {
                        c = node.getOwnerTree().contextMenuRoot;
                    } else {
                        if (node.isLeaf()) {
                            c = node.getOwnerTree().contextMenuLeaf;
                        } else {
                            c = node.getOwnerTree().contextMenuNode;
                        }
                    }
                    c.contextNode = node;
                    c.showAt(e.getXY());
                }
            }

        });

        sitools.component.graphs.graphsCrudTreePanel.superclass.constructor.call(this, config);

    },

    onRender : function () {

        sitools.component.graphs.graphsCrudTreePanel.superclass.onRender.apply(this, arguments);

    },

    _cxtMenuHandler : function (item) {
        var node, up;
        switch (item.id) {
        case 'delete-node':
            var tot = Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : i18n.get('label.graphs.node.delete'),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        var n = item.parentMenu.contextNode;
                        if (n.parentNode) {
                            n.remove();
                        }
                    }
                }
            });

            break;

        case 'add-dataset':
            node = item.parentMenu.contextNode;

            // this.doLayout();
            up = new sitools.component.graphs.graphsDatasetWin({
                node : node,
                url : loadUrl.get('APP_URL') + '/projects/' + this.projectId + '?media=json',
                mode : 'create'
            });
            up.show(this);

            break;

        case 'create-node':
            node = item.parentMenu.contextNode;

            // this.doLayout();
            up = new sitools.component.graphs.graphsNodeWin({
                node : node,
                mode : 'create'
            });
            up.show(this);

            break;
        case 'edit-node':
            node = item.parentMenu.contextNode;
            if (node.isLeaf()) {
                up = new sitools.component.graphs.graphsDatasetWin({
                    node : node,
                    url : loadUrl.get('APP_URL') + '/projects/' + this.projectId + '?media=json',
                    mode : 'edit'
                });
            } else {
                up = new sitools.component.graphs.graphsNodeWin({
                    node : node,
                    mode : 'edit'
                });
            }
            up.show(this);
            break;
        }
    },

    getIdGraph : function () {
        return this.loader.getIdGraph();
    }

});

/**
 * Custom TreeLoader to deal with JSON returned from the server
 */
// TODO ExtJS3 Ext.tree.TreeLoader > ?
Ext.define('sitools.component.graphs.graphTreeLoader', { extend : 'Ext.tree.TreeLoader', 

    idGraph : null,

    createNode : function (attr) {
        return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
    },

    processResponse : function (response, node, callback, scope) {
        var json = response.responseText, children, newNode, i = 0, len;
        try {

            if (!(children = response.responseData)) {
                children = Ext.decode(json);
                this.idGraph = children.graph.id;

                if (this.root) {
                    if (!this.getRoot) {
                        this.getRoot = Ext.data.JsonReader.prototype.createAccessor(this.root);
                    }
                    children = this.getRoot(children);
                }
            }
            node.beginUpdate();
            for (len = children.length; i < len; i++) {
                newNode = this.createNode(children[i]);
                if (newNode) {
                    node.appendChild(newNode);
                }
            }
            node.endUpdate();
            this.runCallback(callback, scope || node, [ node ]);
        } catch (e) {
            this.handleFailure(response);
        }
    },

    getIdGraph : function () {
        return this.idGraph;
    }
});

