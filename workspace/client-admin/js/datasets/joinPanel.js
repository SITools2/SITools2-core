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
 
 /*
  * @include "joinConditionWin.js"
  * @include "joinTableWin.js"
  * 
  */
Ext.namespace('sitools.admin.datasets');

/**
 * The panel that displays the sql join wizard
 * @cfg {String} datasetId (required) the dataset Id
 * @cfg {Ext.grid.GridPanel} datasetSelectTables (required) The Panel that shows dataset tables
 * @cfg {string} action (required) 
 * @cfg {Ext.data.Store} storeColumnDataset (required) The store of the dataset columns
 * @class sitools.admin.datasets.joinPanel
 * @extends Ext.Panel
 */
//sitools.component.datasets.joinPanel = Ext.extend(Ext.Panel, {
sitools.admin.datasets.joinPanel = Ext.extend(Ext.Panel, {

    border : false,
    urlJDBC : loadUrl.get('APP_URL') + "/",
    autoScroll: true,
	layout : 'fit', 
	height : 180,
	
    initComponent : function () {
        this.title = "Join Configuration";
        this.tree = new sitools.component.datasets.joinCrudTreePanel(this);

        this.items = [this.tree];
		this.addEvents('contextmenu');  
        
        sitools.admin.datasets.joinPanel.superclass.initComponent.call(this);
    },

    loadGraph : function () {

        this.removeAll();

        this.tree = new sitools.component.datasets.joinCrudTreePanel({
            datasetId : this.datasetId
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
                predicat : root.predicat, 
                leaf : root.leaf
            };
            parent.push(node);
        } else {

            node = {
                text : root.text,
                children : [],
                type : root.attributes.type,
                typeJointure : root.attributes.typeJointure, 
                table : root.attributes.table, 
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


    buildDefault : function () {
        // if (this.action == "create") {
        this.tree.buildDefault();
        // }
    },
    deleteJoinPanelItems : function () {
        this.tree.deleteJoinPanelItems();
    }

});

sitools.component.datasets.joinCrudTreePanel = Ext.extend(Ext.tree.TreePanel, {

    loader : null,
    projectId : null,
    layout : "fit", 
	autoScroll : true, 
    initComponent : function () {
        var root;
			
        root = new Ext.tree.TreeNode({
            text : this.name, 
            leaf : false, 
            expanded : true
        }); 
	        
	        
        Ext.apply(this, {
            rootVisible : true,
            layout : 'fit',
            enableDD: false,           
            root : root,
            contextMenuRoot : new Ext.menu.Menu({
				items : [{
                    id : 'create-node',
                    text : i18n.get("Add Table"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png', 
                    menu : {
						items : [ {
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
		                }],
		                listeners : {
		                    scope : this,
		                    itemclick : this._cxtMenuHandler
		                }
		            }
                }, {
                    id : 'edit-root',
                    text : i18n.get("label.modify"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png'
                }],
                listeners : {
                    scope : this,
                    itemclick : this._cxtMenuHandler
                }
            }), 
            contextMenuNode : new Ext.menu.Menu({
                items : [ {
                    id : 'add-joinCondition',
                    text : i18n.get("Add Join Condition"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png'
                }, {
                    id : 'edit-node',
                    text : i18n.get("label.modify"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png'
                }, {
                    id : 'edit-jointure',
                    text : i18n.get("editJointure"), 
                    icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png', 
                    menu : {
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
		                }],
		                listeners : {
		                    scope : this,
		                    itemclick : this._cxtMenuHandler
		                }
		            }
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
                    e.stopEvent();
                    // Register the context node with the menu so that a Menu
                    // Item's handler function can access
                    // it via its parentMenu property.
                    node.select();
                    var c;
					if (node == this.getRootNode()) {
						c = node.getOwnerTree().contextMenuRoot;
						c.contextNode = this.getRootNode();
                    }
                    else {
	                    if (node.isLeaf()) {
	                        c = node.getOwnerTree().contextMenuLeaf;
	                    } else {
	                        c = node.getOwnerTree().contextMenuNode;
	                    }
	                    c.contextNode = node;
                    }
                    
	                c.showAt(e.getXY());
                }, 
                beforenodedrop : function (dropEvent) {
					if (dropEvent.target.attributes.type == dropEvent.data.node.attributes.type) {
						return false;
					}
					
					return true;
                }
            }

        });

        sitools.component.datasets.joinCrudTreePanel.superclass.initComponent.call(this);

    },

    onRender : function () {

        sitools.component.datasets.joinCrudTreePanel.superclass.onRender.apply(this, arguments);
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

        case 'add-joinCondition':
            node = item.parentMenu.contextNode;
			
            // this.doLayout();
            up = new sitools.admin.datasets.joinConditionWin({
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
                node = item.parentMenu.parentMenu.contextNode;

	            // this.doLayout();
	            up = new sitools.admin.datasets.joinTableWin({
	                node : node,
	                mode : 'create',
					datasetSelectTables : this.datasetSelectTables, 
					typeJointure : item.id
	            });
	            up.show();
            }
			if (item.action == "editJointure") {
				node = item.parentMenu.parentMenu.contextNode;
				node.attributes.typeJointure = item.id;
				node.setText(this.getNodeText(node.attributes));
			}
            break;

        case 'edit-node':
            node = item.parentMenu.contextNode;
            if (node.isLeaf()) {
                up = new sitools.admin.datasets.joinConditionWin({
                    node : node,
                    mode : 'edit', 
					storeColumnDataset : this.storeColumnDataset
				});
            } else {
                up = new sitools.admin.datasets.joinTableWin({
                    node : node,
                    mode : 'edit', 
					datasetSelectTables : this.datasetSelectTables, 
					typeJointure : node.attributes.typeJointure
                });
            }
            up.show();
            break;
        case 'edit-root' : 
			node = item.parentMenu.contextNode;
            up = new sitools.admin.datasets.joinTableWin({
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
		if (storeTables.getCount() !== 0 && Ext.isEmpty(this.getRootNode().attributes.table)) {
			var rec = storeTables.getAt(0);
			var rootNode = this.getRootNode();
			Ext.apply(rootNode, {
				text : rec.data.name,
			    leaf : false, 
			    children : [], 
			    type : "table"
			});
			rootNode.setText(rec.data.name);
			Ext.apply(rootNode.attributes, {
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
			Ext.apply(rootNode.attributes, {
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
			treeNode = new Ext.tree.TreeNode(node);
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
            
			return String.format("{0} {1} {2} {3}", 
				predicat.logicOperator, 
				this.getDisplayName(predicat.leftAttribute), 
				compareOperator, 
				this.getDisplayName(predicat.rightAttribute));

		}
		else {
			var table = ! Ext.isEmpty(node.table) ? node.table : node.attributes.table;
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
			return String.format("{0}.{1}", 
			Ext.isEmpty(column.tableAlias) ? column.tableName: column.tableAlias, 
			column.columnAlias);
		}
		else {
			return column.columnAlias;
		}
    }, 
    deleteJoinPanelItems : function () {
		var root = new Ext.tree.TreeNode({
            text : this.name, 
            leaf : false, 
            expanded : true
        }); 
        this.setRootNode(root);

    }


});


Ext.apply(Ext.tree.TreePanel.nodeTypes, {
	"sync" : Ext.tree.TreeNode
});
