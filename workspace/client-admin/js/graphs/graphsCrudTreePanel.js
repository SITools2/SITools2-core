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

Ext.define('sitools.component.graphs.graphNodeModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name :'type'
    }, {
        name : 'description'
    }, {
        name : 'image'
    }, {
        name : 'nbRecord'
    }, {
        name : 'datasetId'
    }, {
        name : 'imageDs'
    }, {
        name : 'readme'
    }, {
        name : 'status'
    }, {
        name : 'visible'
    }, {
        name : 'url'
    }, {
        name : 'text'
    }]
});

Ext.define('sitools.component.graphs.graphsCrudTreePanel', { 
    extend : 'Ext.tree.Panel',
    rootVisible : true,
    enableDD: true,           
    layout : 'fit',
    useArrows : true,
    idGraph : null,
    initComponent : function () {
        
        this.store = Ext.create('Ext.data.TreeStore', {
            model : 'sitools.component.graphs.graphNodeModel',
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
            text : i18n.get("label.modify"),
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'delete-node',
            text : i18n.get("label.delete"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png',
            scope : this,
            handler : this._cxtMenuHandler
        }];
        
        this.menuRootToolbar = [{
            id : 'create-node',
            text : i18n.get("label.createNode"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'add-dataset',
            text : i18n.get("label.addDataset"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png',
            scope : this,
            handler : this._cxtMenuHandler
        }];
        
        this.menuNodeToolbar = [{
            id : 'create-node',
            text : i18n.get("label.createNode"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_folder.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'add-dataset',
            text : i18n.get("label.addDataset"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/add_datasets.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'edit-node',
            text : i18n.get("label.modify"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_edit.png',
            scope : this,
            handler : this._cxtMenuHandler
        }, {
            id : 'delete-node',
            text : i18n.get("label.delete"), 
            icon : loadUrl.get('APP_URL') + '/res/images/icons/toolbar_delete.png',
            scope : this,
            handler : this._cxtMenuHandler
        }];
        
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
            }
        };
        
        
        sitools.component.graphs.graphsCrudTreePanel.superclass.initComponent.call(this);
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
                this.store.getRootNode().appendChild(data.graph.nodeList);
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
            var tot = Ext.Msg.show({
                title : i18n.get('label.warning'),
                buttons : Ext.Msg.YESNO,
                msg : i18n.get('label.graphs.node.delete'),
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

            up = new sitools.component.graphs.graphsDatasetWin({
                node : node,
                url : loadUrl.get('APP_URL') + '/projects/' + this.projectId + '?media=json',
                mode : 'create'
            });
            up.show(this);

            break;

        case 'create-node':
            node = this.getSelectionModel().getSelection()[0];

            up = new sitools.component.graphs.graphsNodeWin({
                node : node,
                mode : 'create'
            });
            up.show(this);

            break;
        case 'edit-node':
            node = this.getSelectionModel().getSelection()[0];
            
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
        return this.idGraph;
    }

});