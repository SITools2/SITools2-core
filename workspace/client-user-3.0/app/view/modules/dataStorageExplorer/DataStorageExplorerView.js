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

Ext.namespace('sitools.user.view.modules.dataStorageExplorer');
/**
 * Datastorage Explorer Module
 * 
 * @class sitools.user.view.modules.dataStorageExplorer.DataStorageExplorerView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.modules.dataStorageExplorer.DataStorageExplorerView', {
    extend : 'Ext.panel.Panel',
    requires : ["sitools.user.model.DataStorageExplorerTreeModel"],
    alias : 'widget.dataStorageExplorer',

    autoScroll : true,
    layout : {
        type : 'hbox',
        align : 'stretch'
    },
    
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
            itemId : 'uplButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/image_add.png',
            text : i18n.get('label.uploadFile'),
            tooltip : i18n.get('label.uploadFile')
        };

        this.dwlButton = {
            xtype : 'button',
            itemId : 'dwlButton',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/download.png',
            text : i18n.get('label.downloadFile'),
            tooltip : i18n.get('label.downloadFile')
        };

        this.delButton = {
            xtype : 'button',
            itemId : 'delButton',
            iconCls : 'delete-icon',
            text : i18n.get('label.delete'),
            tooltip : i18n.get('label.delete')
        };
        
        this.createFolderButton = {
                xtype : 'button',
                itemId : 'createFolderButton',
                icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/createFolder.png',
                text : i18n.get('label.createFolder')
            };

        this.tbar = Ext.create('Ext.toolbar.Toolbar', {
            cls : "services-toolbar",
            items : [ '->']
        });

        
        this.treeStore = Ext.create('sitools.user.store.DataStorageTreeStore');
        
        this.tree = Ext.create('Ext.tree.Panel', {
            region : 'west',
            width : 300,
            autoScroll : true,
            bodyStyle : 'background-color:white;',
            store : this.treeStore,
            root : {
                text : this.nameDatastorage,
                leaf : false,
                url : this.datastorageUrl,
                name : this.nameDatastorage,
                commonStoreId : Ext.id(),
                id : Ext.id()
            },
            collapsible : true,
            collapseDirection : "left",
            forceFit : true,
            rowLines : true,
            selModel : Ext.create("Ext.selection.TreeModel", {
                allowDeselect : false,
                mode : "SINGLE"
            })
        });

        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
            	type : 'ajax',
                method : 'GET',
                url : this.datastorageUrl,
                headers : {
                    'Accept' : 'application/json+sitools-directory'
                }
            },
            model : 'sitools.user.model.DataStorageExplorerTreeModel'
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
            itemId : 'file-view',
            autoScroll : true,
            flex : 1,
            region : 'center',
            store : this.store,
            mode : 'SINGLE',
            tpl : this.tpl,
            selectedItemCls : "datastorageSelectionClass",
//            overItemCls:'x-view-over-ds',
            itemSelector : 'div.dv-datastorage-wrap',
            emptyText : i18n.get('label.nothingToDisplay'),
            style : 'background-color: #9DC6E4;'
        });
        
        
        
        this.detailPanel = Ext.create('Ext.ux.IFrame', {
        	title : i18n.get('label.defaultTitleDetailPanel'),
            itemId : 'detail-view',
            layout : 'fit',
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
            layout : 'fit',
            items : [this.detailPanel],
            border : false,
            bodyBorder : false,
            collapsible : true,
            collapsed : true,
            collapseDirection : 'bottom',
            height : 350
        });

        this.contentPanel = Ext.create('Ext.panel.Panel', {
            itemId : 'content-view',
            layout : {
                type :'vbox',
                align : 'stretch'
            },
            flex : 1,
            autoScroll : false,
            border : false,
            bodyBorder : false,
            items : [ this.dataview, {
    			xtype : 'splitter',
    			style : 'background-color:#EBEBEB;'
    		}, this.detailPanelContainer ]
        });

        this.items = [ this.tree, {
			xtype : 'splitter',
			style : 'background-color:#EBEBEB;'
		}, this.contentPanel];

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
                var ind = this.store.find('id', node.id);
                var parent = node.parentNode;
                node.remove();
                this.store.removeAt(ind);

                this.dataview.refresh();
                this.reloadNode(parent);
                
                popupMessage(i18n.get('label.information'), i18n.get('label.fileDeleted'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
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
        this.detailPanelContainer.expand(true);
        this.detailPanelContainer.setHeight(350);
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
    	
//        this.tree.getStore().load(options, function () {
//            node.expand(true);
//        }, this);
    	if (!node.isLeaf()) {
    	    node.collapse(false, function() {
    	        node.expand();
    	    });
    	}
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
    }
});
