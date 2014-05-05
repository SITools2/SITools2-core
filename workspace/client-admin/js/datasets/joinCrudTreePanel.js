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

Ext.define('sitools.admin.datasets.joinCrudTreePanel', {
    extend : 'Ext.tree.Panel',
    loader : null,
    projectId : null,
    autoScroll : true, 
    rootVisible : true,
    layout : 'fit',
    useArrows: true,
    
    requires : ['sitools.admin.datasets.joinConditionWin','sitools.admin.datasets.joinTableWin'],
    
    initComponent : function () {
            
//        this.root = Ext.create('Ext.data.NodeInterface', {
//            text : this.name, 
//            leaf : false, 
//            expanded : true
//        }); 
        
        this.store = Ext.create('Ext.data.TreeStore', {
            root : {
                text : this.name,
                leaf : false,
                expanded : true
            },
            proxy : {
                type : 'memory',
                reader : {
                    type : 'json'
                }
            }
        });
        
        this.rootToolbarButtons = [{
            id : 'create-node',
            text : i18n.get("Add Table"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png',
            menu : {
                defaults: {
                    scope : this,
                    handler: this._cxtMenuHandler
                },
                items : [{
                    id : 'INNER_JOIN',
                    action : "addTable",
                    text : i18n.get("label.innerJoin")
                }, {
                    id : 'CROSS_JOIN',
                    action : "addTable",
                    text : i18n.get("label.crossJoin")
                }, {
                    id : 'LEFT_JOIN',
                    action : "addTable",
                    text : i18n.get("label.leftJoin")
                }, {
                    id : 'LEFT_OUTER_JOIN',
                    action : "addTable",
                    text : i18n.get("label.leftOuterJoin")
                }, {
                    id : 'RIGHT_JOIN',
                    action : "addTable",
                    text : i18n.get("label.rightJoin")
                }, {
                    id : 'RIGHT_OUTER_JOIN',
                    action : "addTable",
                    text : i18n.get("label.rightOuterJoin")
                }]
            }
        }, {
            id : 'edit-root',
            text : i18n.get("label.modify"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler: this._cxtMenuHandler
        }];
        
        this.nodeToolbarButtons = [{
            id : 'add-joinCondition',
            text : i18n.get("Add Join Condition"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png',
            scope : this,
            handler: this._cxtMenuHandler
        }, {
            id : 'edit-node',
            text : i18n.get("label.modify"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler: this._cxtMenuHandler
        }, {
            id : 'edit-jointure',
            text : i18n.get("editJointure"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler: this._cxtMenuHandler,
            menu : {
                defaults: {
                    scope : this,
                    handler: this._cxtMenuHandler
                },
                items : [ {
                    id : 'INNER_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.innerJoin")
                }, {
                    id : 'CROSS_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.crossJoin")
                }, {
                    id : 'LEFT_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.leftJoin")
                }, {
                    id : 'LEFT_OUTER_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.leftOuterJoin")
                }, {
                    id : 'RIGHT_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.rightJoin")
                }, {
                    id : 'RIGHT_OUTER_JOIN',
                    action : "editJointure",
                    text : i18n.get("label.rightOuterJoin")
                }]
            }
        }, {
            id : 'delete-node',
            text : i18n.get("label.delete"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png',
            scope : this,
            handler: this._cxtMenuHandler
        }];
        
        this.leafToolbarButtons = [{
            id : 'edit-node',
            text : i18n.get("label.modify"),
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler: this._cxtMenuHandler
        }, {
            id : 'delete-node',
            text : i18n.get("label.delete"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png',
            scope : this,
            handler: this._cxtMenuHandler
        }];
        
        this.tbar = {
            xtype : 'toolbar',
            items : this.rootToolbarButtons
        };
        
        this.listeners = {
            scope : this,
            afterrender : function () {
                this.getSelectionModel().select(0);
            },
            itemclick : function (view, record, item, index) {
                var toolbar = this.down('toolbar');
                toolbar.removeAll();
                
                var node = this.getSelectionModel().getSelection()[0];
                if (node.isRoot()) {
                    toolbar.add(this.rootToolbarButtons);
                } else {
                    if (node.isLeaf()) {
                        toolbar.add(this.leafToolbarButtons);
                    } else {
                        toolbar.add(this.nodeToolbarButtons);
                    }
                }
            },
            beforenodedrop : function (dropEvent) {
                if (dropEvent.target.raw.type == dropEvent.data.node.raw.type) {
                    return false;
                }
                return true;
            }
        };
        
        sitools.admin.datasets.joinCrudTreePanel.superclass.initComponent.call(this);
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
                        var node = this.getSelectionModel().getSelection()[0];
                        if (!node.isRoot()) {
                            node.remove();
                        }
                    }
                }
            });

            break;

        case 'add-joinCondition':
//            node = item.parentMenu.contextNode;
            node = this.getSelectionModel().getSelection()[0];
            
            // this.doLayout();
            up = Ext.create("sitools.admin.datasets.joinConditionWin", {
                node : node,
                mode : 'create', 
                storeColumnDataset : this.storeColumnDataset
            });
            up.show(this);

            break;

        case 'INNER_JOIN':
        case 'LEFT_JOIN':
        case 'CROSS_JOIN':
        case 'LEFT_OUTER_JOIN':
        case 'RIGHT_JOIN':
        case 'RIGHT_OUTER_JOIN':
            if (item.action == "addTable") {
//                node = item.parentMenu.parentMenu.contextNode;
                node = this.getSelectionModel().getSelection()[0];

                // this.doLayout();
                up = Ext.create("sitools.admin.datasets.joinTableWin", {
                    node : node,
                    mode : 'create',
                    datasetSelectTables : this.datasetSelectTables, 
                    typeJointure : item.id
                });
                up.show();
            }
            if (item.action == "editJointure") {
//                node = item.parentMenu.parentMenu.contextNode;
                node = this.getSelectionModel().getSelection()[0];
                
                node.raw.typeJointure = item.id;
                node.set('text', this.getNodeText(node.raw));
            }
            break;

        case 'edit-node':
//            node = item.parentMenu.contextNode;
            node = this.getSelectionModel().getSelection()[0];
            
            if (node.isLeaf()) {
                up = Ext.create("sitools.admin.datasets.joinConditionWin", {
                    node : node,
                    mode : 'edit', 
                    storeColumnDataset : this.storeColumnDataset
                });
            } else {
                up = Ext.create("sitools.admin.datasets.joinTableWin", {
                    node : node,
                    mode : 'edit', 
                    datasetSelectTables : this.datasetSelectTables, 
                    typeJointure : node.raw.typeJointure
                });
            }
            up.show();
            break;
        case 'edit-root' : 
//            node = item.parentMenu.contextNode;
            node = this.getSelectionModel().getSelection()[0];
            
            up = Ext.create("sitools.admin.datasets.joinTableWin", {
                node : node,
                mode : 'edit-root', 
                datasetSelectTables : this.datasetSelectTables
            });
            up.show();
            break;
        }
    },

    getIdGraph : function () {
        return this.loader.getIdGraph();
    }, 
    
    buildDefault : function () {
        //load the first table as main table and the others as children
        var storeTables = this.scope.panelSelectTables.getStoreSelectedTables();
        if (storeTables.getCount() !== 0 && Ext.isEmpty(this.getRootNode().data.table)) {
            var rec = storeTables.getAt(0);
            var rootNode = this.getRootNode();
            Ext.apply(rootNode, {
                text : rec.data.name,
                leaf : false, 
                children : [], 
                type : "table"
            });
            rootNode.text = rec.data.name;
            Ext.apply(rootNode.raw, {
                table : {
                    name : rec.data.name,
                    alias : rec.data.alias,
                    schema : rec.data.schemaName
                }
            });
            
            var i = 0;
            storeTables.each(function (rec) {
                if (i !== 0) {
                    rootNode.appendChild({
                        typeJointure : "INNER_JOIN",
                        text : "INNER_JOIN " + rec.data.name,
                        table : {
                            name : rec.data.name,
                            alias : rec.data.alias,
                            schema : rec.data.schemaName
                        },
                        leaf : false, 
                        type : "table", 
                        children : []
                    });
                }
                i++;
            }, this);
        }
    }, 
    
    loadTree : function (dataset) {
        var rootNode = this.getRootNode();
        var mainTable = dataset.structure.mainTable;
        if (!Ext.isEmpty(mainTable)) {
            Ext.apply(rootNode, {
                text : mainTable.name,
                leaf : false, 
                expanded : true
            });
            Ext.apply(rootNode.raw, {
                table : mainTable
            });
        }
        Ext.each(dataset.structure.nodeList, function (node) {
            this.loadNode(node, rootNode);
        }, this);
    }, 
    
    loadNode : function (node, parent) {
        var treeNode;
        if (node.leaf) {
            Ext.apply(node, {
                text : this.getNodeText(node), 
                nodeType : "sync", 
                expanded : true
            });
//            treeNode = new Ext.tree.TreeNode(node);
            treeNode = node;
            node.children = [];

            parent.appendChild(treeNode);
        }
        else {
            Ext.apply(node, {
                text : this.getNodeText(node), 
                nodeType : "sync", 
                iconCls : "x-tree-node-folder", 
                expanded : true
            });
            
            var children = node.children;
            
            parent.appendChild(node);
            var nodeInserted = parent.lastChild;
            
            Ext.each(children, function (nodeChildren) {
                this.loadNode(nodeChildren, nodeInserted);
            }, this);
            
        }
    }, 
    getNodeText : function (node) {
        if (node.leaf) {
            var predicat = node.predicat || {};
            predicat.leftAttribute = predicat.leftAttribute || {};
            predicat.rightAttribute = predicat.rightAttribute || {};
            
            var compareOperator = predicatOperators.getOperatorValueForClient(predicat.compareOperator);
            
            return Ext.String.format("{0} {1} {2} {3}", 
                predicat.logicOperator, 
                this.getDisplayName(predicat.leftAttribute), 
                compareOperator, 
                this.getDisplayName(predicat.rightAttribute));

        }
        else {
            var table = ! Ext.isEmpty(node.table) ? node.table : node.raw.table;
            if (!Ext.isEmpty(table)) {
                return node.typeJointure + " " + table.name;
            }
            else {
                return "wrong node";
            }
        }
    }, 
    getDisplayName : function (column) {
        if (column.specificColumnType == "DATABASE") {
            return Ext.String.format("{0}.{1}", 
            Ext.isEmpty(column.tableAlias) ? column.tableName: column.tableAlias, 
            column.columnAlias);
        }
        else {
            return column.columnAlias;
        }
    }, 
    deleteJoinPanelItems : function () {
        var root = Ext.create("Ext.tree.TreeNode", {
            text : this.name, 
            leaf : false, 
            expanded : true
        }); 
        this.setRootNode(root);

    }
});