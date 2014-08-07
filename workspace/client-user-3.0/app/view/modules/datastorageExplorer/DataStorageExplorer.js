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
/*global Ext, sitools, i18n, loadUrl, locale, Reference */

Ext.namespace('sitools.user.view.modules.datastorageExplorer');
/**
 * Datastorage Explorer Module
 * 
 * @class sitools.user.view.modules.datastorageExplorer.DataStorageExplorer
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.modules.datastorageExplorer.DataStorageExplorer', {
    extend : 'Ext.panel.Panel',
    requires : ["sitools.user.model.DataStorageExplorerTreeModel"],
    alias : 'widget.datastorageExplorer',

    autoScroll : true,
    layout : 'border',
    initComponent : function () {
        
        var noPreviewAvailableUrlTemplate = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/resources/html/{locale}/noPreviewAvailable.html";
        var localeStr = locale.getLocale();
        this.noPreviewAvailableUrl = noPreviewAvailableUrlTemplate.replace("{locale}", localeStr); 
        
        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
            case "dynamicUrlDatastorage":
                this.datastorageUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.get('value');
                break;

            case "nameDatastorage":
                this.nameDatastorage = config.get('value');
                break;
            }
        }, this);
        
        this.uplButton = {
            xtype : 'button',
            id : 'uplButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/image_add.png',
            text : i18n.get('label.uploadFile'),
            tooltip : i18n.get('label.uploadFile'),
            scope : this,
            handler : function () {
                var nodeSel = this.tree.getSelectionModel().getSelection()[0];
                if (!Ext.isEmpty(nodeSel)) {
                    this.onUpload(nodeSel);
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };

        this.dwlButton = {
            xtype : 'button',
            id : 'dwlButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/download.png',
            text : i18n.get('label.downloadFile'),
            tooltip : i18n.get('label.downloadFile'),
            scope : this,
            handler : function () {
                var nodeSel = this.tree.getSelectionModel().getSelection()[0];
                if (!Ext.isEmpty(nodeSel)) {
                    var rec = this.dataview.getStore().getById(nodeSel.id);
                    // the record exists in the dataview, lets show it
                    if (nodeSel.leaf === "true") {
                        if (!Ext.isEmpty(rec)) {
                            sitools.user.component.dataviews.dataviewUtils.downloadFile(rec.data.url);
                        } else {
                            this.loadDataview(nodeSel.parentNode);
                            rec = this.dataview.getStore().getById(nodeSel.id);
                            sitools.user.component.dataviews.dataviewUtils.downloadFile(rec.data.url);
                        }
                    }
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };

        this.delButton = {
            xtype : 'button',
            id : 'delButton',
            iconCls : 'delete-icon',
            text : i18n.get('label.delete'),
            tooltip : i18n.get('label.delete'),
            scope : this,
            handler : function () {
                var node = this.tree.getSelectionModel().getSelection()[0];
                if (!Ext.isEmpty(node)) {
                    Ext.Msg.confirm(i18n.get('label.info'), i18n.get('label.sureDelete') + node.attributes.name + " ?", function (btn) {
                        if (btn === 'yes') {
                            if (!this.isRootNode(node)) {
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
                id : 'createFolderButton',
                icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/createFolder.png',
                text : i18n.get('label.createFolder'),
                scope : this,
                handler : function () {
                    var node = this.tree.getSelectionModel().getSelection()[0];
                    if (!Ext.isEmpty(node)) {
                        this.onCreateFolder(node);
                    } else {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                    }
                } 
            };

        this.tbar = Ext.create('Ext.toolbar.Toolbar', {
            cls : "services-toolbar",
            items : [ '->']
        });

        
        Ext.data.NodeInterface.decorate(sitools.user.model.DataStorageExplorerTreeModel);
        
        this.treeStore = Ext.create('Ext.data.TreeStore', {
        	model : 'sitools.user.model.DataStorageExplorerTreeModel'
        });
        
        this.tree = Ext.create('Ext.tree.Panel', {
            region : 'west',
            animate : true,
            width : 300,
            expanded : true,
            autoScroll : true,
            bodyStyle : 'background-color:white;',
            store : this.treeStore,
            root : {
                text : this.nameDatastorage,
                leaf : false,
                url : this.datastorageUrl,
                name : this.nameDatastorage
            },
            rootVisible : true,
            split : true,
            collapsible : true,
            listeners : {
                scope : this,
//                beforeload : function (node) {
//                    return node.isRoot || Ext.isDefined(node.attributes.children);
//                },
                beforeitemexpand : function (node, opts) {
                    this.tree.getSelectionModel().clearSelections();
                    node.removeAll();
                    
                    var reference = new Reference(node.raw.url);
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
                              		  child.children = [];
                              	  }
                              	  
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
                                
                                this.loadDataview(node);
                                
                                // if there is a node to select prior to the expanding of the node
                                var nodeToSelect = this.tree.getSelectionModel().getSelection()[0]; 
                                if (nodeToSelect && nodeToSelect.get('leaf') === "true") {
                                    var name = nodeToSelect.get('name');
                                    var callback = this.callbackForceSelectNodeOtherDirectory.bind(this, name);
//                                    this.tree.expandPath(node.getPath(), undefined, '/', callback);
                                    node.expand(false);
                                } else {
                                	node.expand(false);
                                    // just expand the current path
//                                    this.tree.expandPath(node.getPath());
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
                itemclick : function (view, node, item, e) {
                    
                    this.manageToolbar(node);
                    if (node.get('text').match(/\.(fits)$/)) {
                        var sitoolsFitsViewer = new sitools.user.component.dataviews.services.sitoolsFitsViewer({
                            nodeFits : node
                        });
                        sitoolsFitsViewer.show();
                    }
                    
                    var rec = this.dataview.getStore().getById(node.get('commondStoreId'));
                    this.dataview.select(rec, false);
                    
                    if (this.isOpenable(node.get('text'))) {
                        if (!Ext.isEmpty(rec)) {
                            this.displayFile(rec);
                        } else {
                            this.reloadNode(node.parentNode);
                            
                        }
                    } else {
//                        this.detailPanel.setTitle(i18n.get('label.defaultTitleDetailPanel'));
                        this.detailPanel.setSrc(this.noPreviewAvailableUrl);
//                        this.detailPanel.doLayout();
                    }
                }
            }
        });

        Ext.QuickTips.init();

        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
            	type : 'ajax',
                method : 'GET',
                url : this.datastorageUrl,
                headers : {
                    'Accept' : 'application/json+sitools-directory'
                }
            },
            fields : [ {
                name : 'text',
                mapping : 'text'
            },
            {
                name : 'name',
                mapping : 'name'
            }, {
                name : 'lastmod',
                mapping : 'lastmod',
                type : 'date',
                dateFormat : 'timestamp'
            }, {
                name : 'leaf',
                mapping : 'leaf'
            }, {
                name : 'url',
                mapping : 'url'
            }, {
                name : 'size',
                mapping : 'size',
                type : 'float'
            }, {
                name : 'cls',
                mapping : 'cls'
            }, {
                name : 'id',
                mapping : 'id'
            }, {
            	name : 'commonStoreId'
            } ]
        });

        this.tpl = new Ext.XTemplate('<tpl for=".">',
            '<div class="dv-datastorage-wrap" id="{name}">',
                '<div class="dv-datastorage">',
                    '<tpl if="this.isLeaf(leaf)">',
                        '<tpl if="this.isImage(url)">',
                            '<img src="{url}" alt="{name}" title="{[this.formatTitle(values)]}" width="60" height="60"/>',
                        '</tpl>',
                        '<tpl if="!this.isImage(name)">',
                            '<img src="/sitools/client-user/resources/images/cmsModule/{[this.getIcon(values.name)]}" width="60" height="60" alt="{name}" title="{[this.formatTitle(values)]}">',
                        '</tpl>',
                    '</tpl>',
                    '<tpl if="!this.isLeaf(leaf)">',
                        '<img src="/sitools/client-user/resources/images/cmsModule/folder.png" width="60" height="60" title="{[this.formatTitle(values)]}">',
                    '</tpl>',
                    '<span class="dv-datastorage">{name}</span>',
                '</div>',
            '</div>',
        '</tpl>',
        '<div class="x-clear"></div>', {
            isLeaf : function (leaf) {
                return leaf;
            },
            isImage : function (name) {
                var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
                return (name.match(imageRegex));
            },
            isPdf : function (name) {
                var imageRegex = /\.(pdf)$/;
                return (name.match(imageRegex));
            },
            formatDate : function (dateText) {
                var date = new Date(dateText);
                return Ext.Date.format(date,SITOOLS_DEFAULT_IHM_DATE_FORMAT);
            },
            formatTitle : function(values) {
                var str = values.name + "\n" + i18n.get("label.lastModif") + " : " + this.formatDate(values.lastmod);
                if(values.leaf){
                    str += "\n" + i18n.get("label.fileSize") + " : " + Ext.util.Format.fileSize(values.size);
                }
                return str;
            },
            isPpt : function (text) {
                var imageRegex = /\.(ppt|pptx)$/;
                return (text.match(imageRegex));
            },
            isWord : function (text) {
                var imageRegex = /\.(doc|docx)$/;
                return (text.match(imageRegex));
            },
            isExcel : function (text) {
                var imageRegex = /\.(xls|xlsx)$/;
                return (text.match(imageRegex));
            },
            isHtml : function (text) {
                var imageRegex = /\.(html)$/;
                return (text.match(imageRegex));
            },
            isTxt : function (text) {
                var imageRegex = /\.(txt)$/;
                return (text.match(imageRegex));
            },
            isLink : function (type) {
                return (type == "LINK");
            },
            isFile : function (type) {
                return (type == "FILE");
            },
            getIcon : function (name) {
                var icon = "file-dv.png";
                if (!Ext.isEmpty(name)) {
                    if (this.isPdf(name)) {
                        icon = "pdf.png";
                    } else if (this.isPpt(name)) {
                        icon = "powerpoint.png";
                    } else if (this.isWord(name)) {
                        icon = "word.png";
                    } else if (this.isExcel(name)) {
                        icon = "excel.png";
                    }
                    else if (this.isHtml(name)) {
                        icon = "html.png";
                    }
                    else if (this.isTxt(name)) {
                        icon = "text.png";
                    }
                }
                return icon;
            }
        });

        this.dataview = Ext.create('Ext.view.View', {
            id : 'file-view',
            autoScroll : true,
            height : 350,
            region : 'center',
            store : this.store,
            mode : 'SINGLE',
            tpl : this.tpl,
            selectedClass : "datastorageSelectionClass",
//            overItemCls:'x-view-over-ds',
            itemSelector : 'div.dv-datastorage-wrap',
            emptyText : i18n.get('label.nothingToDisplay'),
            style : 'background-color: #9DC6E4;',
            listeners : {
                scope : this,
                itemclick : function (dv, record, item, index, e) {
                    dv.select(record, false);
                    
                    var treeNode = this.treeStore.getRootNode().findChild('commonStoreId', record.get('commonStoreId'));
                    // if (rec.data.cls){
                    // this.tree.fireEvent('beforeexpandnode', treeNode);
                    // }
                    this.tree.getSelectionModel().select(treeNode, e, true);
                    this.tree.fireEvent('itemclick', this.tree.getView(), treeNode, e);
                }
            }
        });

        this.detailPanel = Ext.create('Ext.ux.IFrame', {
        	title : i18n.get('label.defaultTitleDetailPanel'),
            id : 'detail-view',
            region : 'south',
            collapsible : true,
//            collapsed : true,
            height : 350,
//            flex : 1,
            autoScroll : true,            
            split : true,
            cls : 'detail-panel-datastorage',
            src : this.noPreviewAvailableUrl,
            tools : [{
                id : 'plus',
                qtip : i18n.get("label.showInWindow"),
                scope : this,
                handler : function (event, toolEl, panel) {
                    this.detachPanel(panel);
                }
            } ]
        });

        this.contentPanel = Ext.create('Ext.panel.Panel', {
            id : 'content-view',
            layout : 'border',
            region : 'center',
            items : [ this.dataview, this.detailPanel ]
        });

        this.items = [ this.tree, this.contentPanel ];

        this.callParent(arguments);
    },
    
    appendChild : function (child, parent) {
        var reference = new Reference(child.url);
        var url = reference.getFile();
        
        
        var name = decodeURIComponent(child.text);
        var text = name;
        if (child.leaf) {
            text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
        }
        
        return parent.appendChild({
        	commonStoreId : Ext.id(),
            cls : child.cls,
            text : text,
            name : name,
            url : url,
            leaf : child.leaf,
            size : child.size,
            lastmod : child.lastmod
        });
    },

    isImage : function (name) {
        var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
        return name.match(imageRegex);            
    },

    isOpenable : function (name) {
        var imageRegex = /\.(txt|json|html|css|xml|pdf|png|jpg|jpeg|gif|bmp)$/;
        return name.match(imageRegex);            
    },

    onUpload : function (node) {

        var urlUpload = null;
        if (node.attributes.leaf === "true") {
            urlUpload = node.parentNode.attributes.url;
        } else if (node.attributes.cls) {
            urlUpload = node.attributes.url;
        } else if (node.isRoot) {
            urlUpload = this.datastorageUrl;
            if (urlUpload.charAt(urlUpload.length - 1) != "/") {
                urlUpload = urlUpload + "/";
            }
        }
        
        var uploadWin = new sitools.user.modules.datastorageUploadFile({
            urlUpload : urlUpload,
            scope : this,
            callback : function () {
                this.reloadNode(node);
            }
        });
        
        uploadWin.show();

    },
    
    onCreateFolder : function (node) {

        var url = null;
        if (node.attributes.leaf == true) {
            url = node.parentNode.attributes.url;
        } else if (node.attributes.cls) {
            url = node.attributes.url;
        } else if (node.isRoot) {
            url = this.datastorageUrl;
            if (url.charAt(url.length - 1) != "/") {
                url = url + "/";
            }
        }
        
        var createFolderWin = new sitools.user.modules.datastorageCreateFolder({
            url : url,
            scope : this,
            callback : function () {
                this.reloadNode(node);
            }
        });
        createFolderWin.show();
    },

    deleteNode : function (node) {

        var deleteUrl = "";
        if (node.attributes.leaf === "true") {
            deleteUrl = node.attributes.url;
        } else if (node.attributes.cls) {
            deleteUrl = node.attributes.url;
        }
        
        Ext.Ajax.request({
            url : deleteUrl + "?recursive=true",
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var ind = this.store.find('id', node.id);
                this.store.removeAt(ind);
                node.remove(true);

                this.dataview.refresh();

                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.fileDeleted'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
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
//        this.detailPanel.setTitle(rec.data.name);
        this.detailPanel.setSrc(rec.data.url);
         this.detailPanel.expand(true);
        this.detailPanel.setHeight(350);
        this.detailPanel.doLayout();
    },

    loadDataview : function (parent) {
        var Json = [];
        Ext.each(parent.childNodes, function (child) {
//            Json.push(child.attributes);
            Json.push(child.raw);
        });
        
//        this.dataview.getStore().loadData(Json);
        this.dataview.getStore().removeAll();
        Ext.each(Json, function (rec) {
        	this.dataview.getStore().add(rec);
        }, this);
        
        
        //clear any file already displayed
//        this.detailPanel.setTitle(i18n.get('label.defaultTitleDetailPanel'));
        this.detailPanel.setSrc(this.noPreviewAvailableUrl);
    },

    detachPanel : function (panel) {
        var customConfig = {
            title : panel.title,
            id : panel.title,
            modal : true,
            iconCls : 'dataDetail'
        };
    
        sitools.user.component.dataviews.dataviewUtils.showDisplayableUrl(panel.frameEl.src, true, customConfig);
    },
    
    reloadNode : function (node) {
    	var options = {
    			node : node
    	};
    	
        this.tree.getStore().load(options, function () {
            node.expand(true);
        }, this);
    },

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    },
    
    callbackForceSelectNodeOtherDirectory : function (name, success, parentNode) {
        var node = parentNode.findChild("name", name);
        this.tree.fireEvent("click", node);
    },
    
    render : function () {
        this.reloadNode(this.tree.getRootNode());
        this.callParent(arguments);
    },
    
    isRootNode : function (node) { 
        return this.tree.getRootNode() === node;        
    },
    
    manageToolbar : function (node) {
        var tb = this.down('toolbar');
        tb.removeAll();
        tb.add(this.createFolderButton);
        
        if (node.leaf !== "true") {
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
    }
});
