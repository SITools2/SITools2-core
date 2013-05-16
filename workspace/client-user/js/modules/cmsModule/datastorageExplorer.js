/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, loadUrl, locale */

Ext.namespace('sitools.user.modules');
/**
 * Help Module
 * 
 * @class sitools.user.modules.datastorageExplorer
 * @extends Ext.Panel
 */
sitools.user.modules.datastorageExplorer = Ext.extend(Ext.Panel, {

    autoScroll : true,
    id : 'datastorageExpId',
    initComponent : function () {
        
        var noPreviewAvailableUrlTemplate = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/res/html/{locale}/noPreviewAvailable.html";
        var localeStr = locale.getLocale();
        this.noPreviewAvailableUrl = noPreviewAvailableUrlTemplate.replace("{locale}", localeStr); 
        
        // this.datastorageUrl = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site/";

        Ext.each(this.listProjectModulesConfig, function (config) {
            switch (config.name) {
            case "dynamicUrlDatastorage":
                this.datastorageUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.value;
                break;

            case "nameDatastorage":
                this.nameDatastorage = config.value;
                break;
            }
        }, this);
        // this.title = i18n.get('label.dsDirectory') + " : " +
        // this.nameDatastorage;
        this.layout = 'border';

        this.uplLabel = {
            xtype : 'label',
            id : 'uplLabel',
            text : i18n.get('label.uploadFile') + ' :'
        };

        this.uplButton = {
            xtype : 'button',
            id : 'uplButton',
            // iconAlign : 'right',
            iconCls : 'upload-icon',
            text : i18n.get('label.uploadFile'),
            tooltip : i18n.get('label.uploadFile'),
            scope : this,
            handler : function () {
                var nodeSel = this.tree.getSelectionModel().getSelectedNode();
                if (!Ext.isEmpty(nodeSel)) {
                    this.onUpload(nodeSel);
                } else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        };

        this.dwlLabel = {
            xtype : 'label',
            id : 'dwlLabel',
            text : i18n.get('label.downloadFile') + ' :'
        };

        this.dwlButton = {
            xtype : 'button',
            id : 'dwlButton',
            // iconAlign : 'right',
            iconCls : 'download-icon',
            text : i18n.get('label.downloadFile'),
            tooltip : i18n.get('label.downloadFile'),
            scope : this,
            handler : function () {
                var nodeSel = this.tree.getSelectionModel().getSelectedNode();
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

        this.delLabel = {
            xtype : 'label',
            text : i18n.get('label.delete') + ' :'
        };

        this.delButton = {
            xtype : 'button',
            id : 'delButton',
            // iconAlign : 'right',
            iconCls : 'delete-icon',
            text : i18n.get('label.delete'),
            tooltip : i18n.get('label.delete'),
            scope : this,
            handler : function () {
                var node = this.tree.getSelectionModel().getSelectedNode();
                if (!Ext.isEmpty(node)) {
                    Ext.Msg.confirm(i18n.get('label.info'), i18n.get('label.sureDelete') + node.attributes.text + " ?", function (btn) {
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

        this.tbar = new Ext.Toolbar({
            cls : "services-toolbar",
            height : 30,
            items : [ '->']
        });

        this.tree = new Ext.tree.TreePanel({
            region : 'west',
            animate : true,
            width : 300,
            expanded : true,
            autoScroll : true,
            containerScroll : true,
            bodyStyle : 'background-color:white;',
            loader : new Ext.tree.TreeLoader({
                baseParams : {
                    index : ""
                },
                preloadChildren : true,
                requestMethod : 'GET',
                url : this.datastorageUrl,
                createNode : function (attr) {
                    var isPdf = function (text) {
                        var imageRegex = /\.(pdf)$/;
                        return (text.match(imageRegex));
                    };

                    var listeners = {
                        scope : this,
                        beforeappend : function (tree, parent, item) {
                            if (item.attributes.leaf === "true") {
                                if (isPdf(item.attributes.text)) {
                                    item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png');
                                }
                            }
                            return true;
                        }
                    };
                    Ext.apply(attr, {
                        listeners : listeners
                    });
                    // apply baseAttrs, nice idea Corey!
                    if (this.baseAttrs) {
                        Ext.applyIf(attr, this.baseAttrs);
                    }
                    if (this.applyLoader !== false && !attr.loader) {
                        attr.loader = this;
                    }
                    if (Ext.isString(attr.uiProvider)) {
                        attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
                    }
                    if (attr.nodeType) {
                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
                    } else {
                        return attr.leaf ? new Ext.tree.TreeNode(attr) : new Ext.tree.AsyncTreeNode(attr);
                    }
                }
            }),
            rootVisible : true,
            split : true,
            collapsible : true,
            root : {
                text : this.nameDatastorage,
                leaf : false,
                url : this.datastorageUrl
            },            
            listeners : {
                scope : this,
                beforeload : function (node) {
                    return node.isRoot || Ext.isDefined(node.attributes.children);
                },
                beforeexpandnode : function (node) {
                    node.removeAll();
                    Ext.Ajax.request({
                        params : {
                            index : ""
                        },
                        url : node.attributes.url,
                        headers : {
                            'Accept' : 'application/json+sitools-directory'
                        },
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                Ext.each(Json, function (child) {
                                    if (!child.leaf) {
                                        var nodeAdded = node.appendChild({
                                            cls : child.cls,
                                            text : decodeURIComponent(child.text),
                                            url : child.url,
                                            leaf : child.leaf,
                                            size : child.size,
                                            lastmod : child.lastmod,
                                            children : []
                                        });
                                        child.id = nodeAdded.id;
                                    }
                                });
                                Ext.each(Json, function (child) {
                                    if (child.leaf) {
                                        var nodeAdded = node.appendChild({
                                            cls : child.cls,
                                            text : decodeURIComponent(child.text),
                                            url : child.url,
                                            leaf : child.leaf,
                                            size : child.size,
                                            lastmod : child.lastmod,
                                            children : []
                                        });
                                        child.id = nodeAdded.id;
                                    }
                                });
                                this.loadDataview(node);
                                
                                // if there is a node to select prior to the expanding of the node
                                var nodeToSelect = this.tree.getSelectionModel().getSelectedNode(); 
                                if (nodeToSelect && nodeToSelect.leaf === "true") {
                                    var text = nodeToSelect.attributes.text;
                                    var callback = this.callbackForceSelectNodeOtherDirectory.bind(this, text);
                                    this.tree.expandPath(node.getPath(), undefined, callback);
                                } else {
                                    // just expand the current path
                                    this.tree.expandPath(node.getPath());
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
                contextmenu : function (node, e) {
                    this.tree.getSelectionModel().select(node, e, true);
                    var c = node.getOwnerTree().contextMenu;
                    c.contextNode = node;
                    c.showAt(e.getXY());
                },
                click : function (node, e) {
                    var tb = this.getTopToolbar();
                    tb.remove('uplButton');
                    tb.remove('dwlButton');
                    tb.remove('delButton');
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

                    if (this.isOpenable(node.text)) {
                        var rec = this.dataview.getStore().getById(node.id);
                        if (!Ext.isEmpty(rec)) {
                            this.displayFile(rec);
                        } else {
                            this.reloadNode(node.parentNode);
                            
                        }
                    } else {
                        this.detailPanel.setTitle(i18n.get('label.defaultTitleDetailPanel'));
                        this.detailPanel.setSrc(this.noPreviewAvailableUrl);
                        this.detailPanel.doLayout();
                    }
                }
            }
        });

        Ext.QuickTips.init();

        this.store = new Ext.data.JsonStore({
            proxy : new Ext.data.HttpProxy({
                method : 'GET',
                url : this.datastorageUrl,
                headers : {
                    'Accept' : 'application/json+sitools-directory'
                }
            }),
            idProperty : 'id',
            fields : [ {
                name : 'text',
                mapping : 'text'
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
            } ]
        });

        // this.store.load();

        this.tpl = new Ext.XTemplate('<tpl for=".">',
                '<div class="dv-datastorage-wrap" id="{text}">',
                '<div class="dv-datastorage">',
                    '<tpl if="this.isLeaf(leaf)">',
                        '<tpl if="this.isImage(url)">',
                            '<img src="{url}" alt="{text}" title="{text}" width="60" height="60"/>',
                        '</tpl>',
                        '<tpl if="!this.isImage(text)">',
                            '<tpl if="this.isPdf(text)">',
                                '<img src="/sitools/common/res/images/icons/icon-pdf.png" width="60" height="60" alt="{text}" title="{text}">',
                            '</tpl>',
                            '<tpl if="!this.isPdf(text)">',
                                '<img src="/sitools/common/res/images/icons/file-dv.png" width="60" height="60" alt="{text}" title="{text}">',
                            '</tpl>',
                        '</tpl>',
                    '</tpl>',
                    '<tpl if="!this.isLeaf(leaf)">',
                        '<img src="/sitools/common/res/images/icons/folder-icon.png" width="60" height="60" title="{text}" title="{text}">',
                    '</tpl>',
                    '<span class="dv-datastorage">{text}</span>',
                '</div>',
            '</div>',
        '</tpl>',
        '<div class="x-clear"></div>', {
            compiled : true,
            isLeaf : function (leaf) {
                return leaf;
            },
            isImage : function (text) {
                var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
                return (text.match(imageRegex));
            },
            isPdf : function (text) {
                var imageRegex = /\.(pdf)$/;
                return (text.match(imageRegex));
            }

        });

        this.dataview = new Ext.DataView({
            id : 'file-view',
            // autoHeight:true,
            autoScroll : true,
            height : 350,
            // layout: 'fit',
            region : 'center',
            store : this.store,
            tpl : this.tpl,
            // overClass:'x-view-over-ds',
            itemSelector : 'div.dv-datastorage-wrap',
            emptyText : i18n.get('label.nothingToDisplay'),
            listeners : {
                scope : this,
                click : function (dv, ind, node, e) {
                    var rec = dv.getStore().getAt(ind);
                    var treeNode = this.tree.getNodeById(rec.data.id);
                    // if (rec.data.cls){
                    // this.tree.fireEvent('beforeexpandnode', treeNode);
                    // }
                    this.tree.getSelectionModel().select(treeNode, e, true);
                    this.tree.fireEvent('click', treeNode, e);
                }
            }
        });

        this.detailPanel = new Ext.ux.ManagedIFrame.Panel({
            id : 'detail-view',
            region : 'south',
            height : 325,
            collapsible : false,
            collapsed : false,
            autoScroll : true,
            cls : 'detail-panel-datastorage',
            title : i18n.get('label.defaultTitleDetailPanel'),
            defaultSrc : this.noPreviewAvailableUrl,
            tools : [ {
                id : 'plus',
                qtip : i18n.get("label.showInWindow"),
                scope : this,
                handler : function (event, toolEl, panel) {
                    this.detachPanel(panel);
                }
            } ]
        });

        this.contentPanel = new Ext.Panel({
            id : 'content-view',
            layout : 'border',
            region : 'center',
            items : [ this.dataview, this.detailPanel ]
        });

        this.items = [ this.tree, this.contentPanel ];

        sitools.user.modules.datastorageExplorer.superclass.initComponent.call(this);
    },

    isImage : function (text) {
        var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
        return text.match(imageRegex);            
    },

    isOpenable : function (text) {
        var imageRegex = /\.(txt|json|html|css|xml|pdf|png|jpg|jpeg|gif|bmp)$/;
        return text.match(imageRegex);            
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
        this.detailPanel.setTitle(rec.data.text);
        this.detailPanel.setSrc(rec.data.url);
        // this.detailPanel.expand(true);
        this.detailPanel.setHeight(350);
        this.detailPanel.doLayout();
    },

    loadDataview : function (parent) {
        var Json = [];
        Ext.each(parent.childNodes, function (child) {
            Json.push(child.attributes);
        });
        this.dataview.getStore().loadData(Json);
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
        this.tree.getLoader().load(node, function () {
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
    
    callbackForceSelectNodeOtherDirectory : function (text, success, parentNode) {
        var node = parentNode.findChild("text", text);
        this.tree.fireEvent("click", node);
    },
    
    render : function () {
        this.reloadNode(this.tree.getRootNode());
        sitools.user.modules.datastorageExplorer.superclass.render.apply(this, arguments);
    },
    
    isRootNode : function (node) { 
        return this.tree.getRootNode() === node;        
    }

});



/**
 * @static Implementation of the method getParameters to be able to load view
 *         Config Module panel.
 * @return {Array} the parameters to display into administration view.
 */
sitools.user.modules.datastorageExplorer.getParameters = function () {

    return [ {
        jsObj : "Ext.form.TextField",
        config : {
            fieldLabel : i18n.get("label.urlDatastorage"),
            allowBlank : false,
            width : 200,
            listeners : {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "the datastorage url (cf. Storage)"
                    });
                }
            },
            name : "dynamicUrlDatastorage",
            value : undefined
        }
    }, {
        jsObj : "Ext.form.TextField",
        config : {
            fieldLabel : i18n.get("label.nameDatastorage"),
            allowBlank : false,
            width : 200,
            listeners : {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "the label NAME of the datastorage to display (cf. Storage)"
                    });
                }
            },
            name : "nameDatastorage",
            value : undefined
        }
    } ];
};

Ext.reg('sitools.user.modules.datastorageExplorer', sitools.user.modules.datastorageExplorer);
