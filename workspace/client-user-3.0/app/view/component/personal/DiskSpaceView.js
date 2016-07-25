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
/*global Ext, sitools, i18n, userLogin, document, alertFailure, SitoolsDesk, userLogin, DEFAULT_ORDER_FOLDER, loadUrl, viewFileContent, Reference*/

Ext.namespace('sitools.user.view.component.personal');

/**
 * @class sitools.user.component.entete.userProfile.diskSpace
 * @extends Ext.tree.TreePanel
 */
Ext.define('sitools.user.view.component.personal.DiskSpaceView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.diskSpace',
    requires : ['sitools.user.model.DataStorageExplorerTreeModel'],

	autoScroll : true,
	border : false,
	layout : {
		type : 'hbox',
		pack : 'start',
		align : 'stretch'
	},
    padding: 10,

    initComponent : function () {
        this.AppUserStorage = loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";
        
        var localeStr = locale.getLocale();
        var noPreviewAvailableUrlTemplate = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/resources/html/{locale}/noPreviewAvailable.html";
        this.noPreviewAvailableUrl = noPreviewAvailableUrlTemplate.replace("{locale}", localeStr); 
        
        this.uplButton = {
            xtype : 'button',
            itemId : 'uplButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/image_add.png',
            text : i18n.get('label.uploadFile'),
            tooltip : i18n.get('label.uploadFile'),
            scope : this,
            handler : function (button, e) {
                var nodeSel = this.tree.getSelectionModel().getSelection()[0];
                
                if (!Ext.isEmpty(nodeSel)) {
                    this.onUpload(this, nodeSel, e);
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };

        this.dwlButton = {
            xtype : 'button',
            itemId : 'dwlButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/download.png',
            text : i18n.get('label.downloadFile'),
            tooltip : i18n.get('label.downloadFile'),
            scope : this,
            handler : function (button) {
                var record = this.tree.getSelectionModel().getSelection()[0];
                
                if (!Ext.isEmpty(record)) {
                    if (record.isLeaf()) {
                        sitools.user.utils.DataviewUtils.downloadFile(record.get('url'));
                    }
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };

        this.delButton = {
            xtype : 'button',
            itemId : 'delButton',
            iconCls : 'delete-icon',
            text : i18n.get('label.delete'),
            tooltip : i18n.get('label.delete'),
            scope : this,
            handler : function (button) {
                var node = this.tree.getSelectionModel().getSelection()[0];
                
                if (!Ext.isEmpty(node)) {
                    Ext.Msg.confirm(i18n.get('label.info'), i18n.get('label.sureDelete') + '<b>' + node.get('name') + '</b>' + " ?", function (btn) {
                        if (btn === 'yes') {
                            if (!node.isRoot()) {
                                this.deleteNode(node);
                            } else {
                                Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.cannotDeleteRootNode'));
                            }
                        }
                    }, this);
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };
        
        this.createFolderButton = {
                xtype : 'button',
                itemId : 'createFolderButton',
                icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/createFolder.png',
                text : i18n.get('label.createFolder'),
                scope : this,
                handler : function (button, e) {
                    var node = this.tree.getSelectionModel().getSelection()[0];

                    var url = null;
                    if (node.isLeaf()) {
                        url = node.parentNode.get('url');
                    } else if (node.get('cls')) {
                        url = node.get('url');
                    } else if (node.isRoot()) {
                        url = this.AppUserStorage;
                        if (url.charAt(url.length - 1) != "/") {
                            url = url + "/";
                        }
                    }
                    
                    var datastorageCreateFolder = Ext.ComponentQuery.query('datastorageCreateFolder')[0];
            		if (Ext.isEmpty(datastorageCreateFolder) || !datastorageCreateFolder.isVisible()) {
            			var menu = Ext.create('sitools.user.view.modules.dataStorageExplorer.DataStorageCreateFolderView', {
            				url : url,
                            scope : this,
                            callback : function () {
                            	this.reloadNode(node);
                            }
            			});
            			menu.showAt(e.getX(), e.getY(), true);
            		}
                }
            };

        this.tbar = Ext.create('Ext.toolbar.Toolbar', {
            cls : "services-toolbar",
            items : [ '->']
        });
        
        this.treeStore = Ext.create('sitools.user.store.DataStorageTreeStore');
        
        this.tree = Ext.create('Ext.tree.Panel', {
            width : 200,
            autoScroll : true,
            bodyStyle : 'background-color:white;',
            store : this.treeStore,
            root : {
                text : "UserStorage",
                leaf : false,
                url : this.AppUserStorage,
                name : this.nameDatastorage,
                id : Ext.id()
            },
            split : true,
            collapsible : true,
            collapseDirection : "left",
            forceFit : true,
            rowLines : true,
            selModel : Ext.create("Ext.selection.TreeModel", {
                allowDeselect : false,
                mode : "SINGLE"
            }),
            listeners : {
            	scope : this,
            	beforeitemexpand : function (node, opts) {
                    
                    node.removeAll();
                    
                    var reference = new Reference(node.get("url"));
                    var url = reference.getFile();
                    Ext.Ajax.request({
                        params : {
                            index : ""
                        },
                        url : url,
                        headers : {
                            'Accept' : 'application/json+sitools-directory'
                        },
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                
                                //first append the folders
                                Ext.each(Json, function (child) {
                                    if (Ext.isEmpty(child.leaf)) {
                                        child.leaf = false;
                                    }
                                    child.children = [];
                                    if (!child.leaf) {
                                        var nodeAdded = this.appendChild(child, node);
                                        child.id = nodeAdded.id;
                                    }
                                }, this);
                                
                                //then append the files
                                Ext.each(Json, function (child) {
                                    if (child.leaf) {
                                        var nodeAdded = this.appendChild(child, node);
                                        child.id = nodeAdded.id;
                                    }
                                }, this);
                                
                                this.detailPanel.setSrc(this.noPreviewAvailableUrl);
                                
                                // if there is a node to select prior to the expanding of the node
                                var nodeToSelect = this.tree.getSelectionModel().getLastSelected(); 
                                if (nodeToSelect && nodeToSelect.get('leaf') === "true") {
                                    var name = nodeToSelect.get('name');
                                    var callback = this.callbackForceSelectNodeOtherDirectory.bind(this, name);
                                    node.expand(false);
                                } else {
                                  node.expand(false);
                                }
                                return true;
                                
                            } catch (err) {
                                throw err;
                            }
                        },
                        failure : function (ret) {
                            return null;
                        }
                    });
                    return true;
                },
                
                itemclick : function (tree, record, item, index, e) {
                    
                    this.manageToolbar(record);
                    
                    if (this.isOpenable(record.get('name'))) {
                        if (!Ext.isEmpty(record)) {
                        	this.displayFile(record);
                        } else {
                        	this.reloadNode(record.parentNode);
                        }
                    } else {
                    	this.detailPanelContainer.setTitle(i18n.get('label.defaultTitleDetailPanel'));
                    	this.detailPanel.load(this.noPreviewAvailableUrl);
                    }
                }
            }
        });

        this.detailPanel = Ext.create('Ext.ux.IFrame', {
        	title : i18n.get('label.defaultTitleDetailPanel'),
            itemId : 'detail-view',
            autoScroll : true,            
            src : this.noPreviewAvailableUrl,
            border : false,
            bodyBorder : false,
            tools : [{
                id : 'plus',
                qtip : i18n.get("label.showInWindow"),
                scope : this,
                handler : function (event, toolEl, panel) {
                    this.detachPanel(panel);
                }
            } ]
        });
        
        this.detailPanelContainer = Ext.create("Ext.panel.Panel", {
            items : [this.detailPanel],
            layout : 'fit',
            flex : 1,
            border : false,
            bodyBorder : false,
            bbar : []
        });

        this.items = [ this.tree, {
			xtype : 'splitter',
			style : 'background-color:#EBEBEB;'
		}, this.detailPanelContainer];

        this.callParent(arguments);

    },
    
    isImage : function (name) {
        var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
        return name.match(imageRegex);            
    },

    isOpenable : function (name) {
        var imageRegex = /\.(txt|json|html|css|xml|pdf|png|jpg|jpeg|gif|bmp)$/;
        return name.match(imageRegex);            
    },

    deleteNode : function (node) {

        var deleteUrl = "";
        if (node.isLeaf()) {
            deleteUrl = node.get('url');
        } else if (node.get('cls')) {
            deleteUrl = node.get('url');
        }
        
        Ext.Ajax.request({
            url : deleteUrl + "?recursive=true",
            method : 'DELETE',
            scope : this,
            success : function (ret) {
            	this.reloadNode(node.parentNode);
            	
                popupMessage(i18n.get('label.information'), i18n.get('label.fileDeleted'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
            },
            callback : function () {
            	this.setUserStorageSize();
            },
            failure : function (response, opts) {
                Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
            }
        });

    },

    formatSize : function (size) {
        if (size < 1024) {
            return size + " bytes";
        } else {
            return (Math.round(((size * 10) / 1024)) / 10) + " KB";
        }
    },

    formatLastModified : function (lastmod) {
        return new Date(lastmod).format("d/m/Y g:i a");
    },

    displayFile : function (rec) {
        this.detailPanelContainer.setTitle(rec.data.name);
        this.detailPanel.load(rec.get("url"));
    },

    detachPanel : function (panel) {
        var customConfig = {
            title : panel.title,
            id : panel.title,
            modal : true,
            iconCls : 'dataDetail'
        };

        sitools.user.utils.DataviewUtils.showDisplayableUrl(panel.frameEl.src, true, customConfig);
    },
    
    reloadNode : function (node) {
    	var options = {
    			node : node
    	};
    	
//        this.tree.getStore().load(options, function () {
//            node.expand(true);
//        }, this);
    	if (!node.isLeaf()) {
    	    node.collapse(false, function() {
    	        node.expand();
    	    });
    	}
    },

    callbackForceSelectNodeOtherDirectory : function (name, success, parentNode) {
        var node = parentNode.findChild("name", name);
        this.tree.fireEvent("click", node);
    },
    
    render : function () {
        this.reloadNode(this.tree.getRootNode());
        this.setUserStorageSize();
        
        this.callParent(arguments);
    },
    
    afterRender : function () {
    	this.tree.getSelectionModel().select(0);
    	this.manageToolbar(this.tree.getRootNode());
    	
    	this.callParent(arguments);
    },
    
    isRootNode : function (node) { 
        return this.tree.getRootNode() === node;        
    },
    
    manageToolbar : function (node) {
        var tb = this.down('toolbar');
        tb.removeAll();
        tb.add(this.createFolderButton);
        
        if (!node.isLeaf()) {
            tb.insert(1, this.uplButton);
            if (!this.isRootNode(node)) {
                tb.insert(2, this.delButton);
            }
            this.reloadNode(node);
        } else {
            tb.insert(1, this.dwlButton);
            tb.insert(2, this.delButton);
        }
        tb.doLayout();
    },
    
    appendChild : function (child, parent) {
        var reference = new Reference(child.url);
        var url = reference.getFile();
        
        var name = decodeURIComponent(child.text);
        var text = name;
        if (child.leaf) {
            text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
        }
        
        var result = parent.appendChild({
            commonStoreId : Ext.id(),
            cls : child.cls,
            text : text,
            name : name,
            url : url,
            leaf : child.leaf,
            size : child.size,
            lastmod : child.lastmod,
            expandable : !child.leaf
        });
        
        return result;
    },
    
    onUpload : function (view, node, e) {

        var urlUpload = null;
        if (node.isLeaf()) {
            urlUpload = node.parentNode.get('url');
            
        } else if (node.get('cls')) {
            urlUpload = node.get('url');
            
        } else if (node.isRoot()) {
            urlUpload = this.AppUserStorage;
            if (urlUpload.charAt(urlUpload.length - 1) != "/") {
                urlUpload = urlUpload + "/";
            }
        }
        
        var datastorageUploadFile = Ext.ComponentQuery.query('datastorageUploadFile')[0];
		if (Ext.isEmpty(datastorageUploadFile) || !datastorageUploadFile.isVisible()) {
			var menu = Ext.create('sitools.user.view.modules.dataStorageExplorer.DataStorageUploadFileView', {
				urlUpload : urlUpload,
	            scope : view,
	            callback : function () {
	            	view.reloadNode(node);
	            	view.setUserStorageSize
	            }
			});
			menu.showAt(e.getX(), e.getY(), true);
		}
        
    },
    
    setUserStorageSize : function () {
        Ext.Ajax.request({
            method : "GET",
            scope : this,
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace("{identifier}", userLogin) + "/status", 
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    return;
                }
                var storage = json.userstorage.storage;
                var totalSpace = storage.quota;
                var usedSpace = storage.busyUserSpace;
                var pourcentage = usedSpace / totalSpace * 100;
                var cls = null; 
                
                if (pourcentage >= 90 && pourcentage < 100) {
                    cls = "x-status-warning";
                }
                else if (pourcentage > 100) {
                    cls = "x-status-error";
                }
                var str = Ext.String.format(i18n.get('label.diskSpaceLong'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
                
                var label = Ext.create('Ext.form.Label', {
                	html : str,
                	componentCls : cls
                });
                
                var toolbar = this.detailPanelContainer.down('toolbar');
                toolbar.removeAll();
                toolbar.add(label);
            }
        });
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/components",
            preferencesFileName : this.id
        };

    }

});
