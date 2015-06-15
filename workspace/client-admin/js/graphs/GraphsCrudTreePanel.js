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
Ext.namespace('sitools.admin.graphs');

Ext.define('sitools.admin.graphs.GraphsCrudTreePanel', { 
    extend : 'Ext.tree.Panel',
    rootVisible : true,
    enableDD: true,           
    layout : 'fit',
    useArrows : true,
    border : false,
    bodyBorder : false,
    idGraph : null,
    
    requires : ["sitools.admin.graphs.GraphsDatasetWin",
                "sitools.admin.graphs.GraphsNodeWin",
                "sitools.admin.graphs.GraphsDatasetWin",
                "sitools.admin.graphs.GraphNodeModel"],    
    
    initComponent : function () {
        
        this.store = Ext.create('Ext.data.TreeStore', {
            model : 'sitools.admin.graphs.GraphNodeModel',
            root : {
                text : this.name,
                expanded : true,
                leaf : false
            },
            proxy : {
                type : 'memory'
            }
        });

        this.menuLeafToolbar =  [{
            id : 'edit-node',
            text : i18n.get("label.modifyNode"),
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'delete-node',
            text : i18n.get("label.deleteNode"), 
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_delete.png',
            scope : this,
            handler : this._cxtMenuHandler
        }];
        
        this.menuRootToolbar = [{
            id : 'create-node',
            text : i18n.get("label.createNode"), 
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/add_folder.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'add-dataset',
            text : i18n.get("label.addDataset"), 
            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/add_datasets.png',
            scope : this,
            handler : this._cxtMenuHandler
        }];
        
        this.menuNodeToolbar = Ext.Array.merge(this.menuLeafToolbar, ['-'], this.menuRootToolbar);
        
        
        this.listeners = {
            scope : this,
            itemclick : function (view, record, item, index) {
                var toolbar = this.graphsCrud.down('toolbar[name=menuTreeToolbar]');
                toolbar.removeAll();
                
                var node = this.getSelectionModel().getSelection()[0];
                
                if (node.isRoot()) {
                    toolbar.add(this.menuRootToolbar);
                } else {
                    if (node.isLeaf()) {
                        toolbar.add(this.menuLeafToolbar);
                    } else {
                        toolbar.add(this.menuNodeToolbar);
                    }
                }
            },
            afterrender : function () {
                this.loadStore();
            },
            viewready : function () {
                Ext.defer(function () {
                    this.on('itemappend', function () {
                        var saveButton = this.graphsCrud.down('button#saveGraphBtnId');
                        saveButton.addCls('not-save-textfield');
                    }, this);
                }, 300, this);

                this.on('itemremove', function () {
                    var saveButton = this.graphsCrud.down('button#saveGraphBtnId');
                    saveButton.addCls('not-save-textfield');
                }, this);
            }
        };
        
        this.callParent(arguments);
    },

    loadStore : function () {
        Ext.Ajax.request({
            method : 'GET',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL') + "/" + this.projectId + "/graph",
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                if (Ext.isEmpty(data.graph)) {
                    return;
                }
                this.idGraph = data.graph.id;
                if (!Ext.isEmpty(data.graph.nodeList)) {
                    this.store.getRootNode().appendChild(data.graph.nodeList);
                    this.expandAll();
                }
            },
            failure : alertFailure
        });
    },
    
    _cxtMenuHandler : function (item) {
        if (this.getSelectionModel().getSelection()[0] == undefined) {
            return;
        }
        
        var node, up;
        switch (item.id) {
        case 'delete-node':
            node = this.getSelectionModel().getSelection()[0];
            Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : Ext.String.format(i18n.get('label.graphs.node.delete'), node.get("text")),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        var n = this.getSelectionModel().getSelection()[0];
                        if (n.parentNode) {
                            n.remove();
                        }
                    }
                }
            });
            break;

        case 'add-dataset':
            node = this.getSelectionModel().getSelection()[0];

            up = Ext.create("sitools.admin.graphs.GraphsDatasetWin", {
                graphTree : this,
                node : node,
                url : loadUrl.get('APP_URL') + '/projects/' + this.projectId + '?media=json',
                mode : 'create'
            });
            up.show(this);

            break;

        case 'create-node':
            node = this.getSelectionModel().getSelection()[0];

            up = Ext.create("sitools.admin.graphs.GraphsNodeWin", {
                graphTree : this,
                node : node,
                mode : 'create'
            });
            up.show(this);

            break;
        case 'edit-node':
            node = this.getSelectionModel().getSelection()[0];
            
            if (node.isLeaf()) {
                up = Ext.create("sitools.admin.graphs.GraphsDatasetWin", {
                    graphTree : this,
                    node : node,
                    url : loadUrl.get('APP_URL') + '/projects/' + this.projectId + '?media=json',
                    mode : 'edit'
                });
            } else {
                up = Ext.create("sitools.admin.graphs.GraphsNodeWin", {
                    graphTree : this,
                    node : node,
                    mode : 'edit'
                });
            }
            up.show(this);
            break;
        }
    },

    getIdGraph : function () {
        return this.idGraph;
    }

});