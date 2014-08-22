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

Ext.namespace('sitools.user.view.header.userProfile');

/**
 * @class sitools.user.component.entete.userProfile.diskSpace
 * @extends Ext.tree.TreePanel
 */
Ext.define('sitools.user.view.header.userProfile.DiskSpaceView', {
	extend : 'Ext.panel.Panel',
    requires : ["sitools.user.model.DataStorageExplorerTreeModel"],

	autoScroll : true,
	border : false,
	height : 400,
	layout : {
		type : 'hbox',
		align : 'stretch'
	},

    initComponent : function () {
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";

        
//        this.tbar = new Ext.ux.StatusBar({
//            statusAlign: 'right'              
//        });
        
        this.tbar = {
    		xtype : 'toolbar',
    		border : false,
    		items : [{
                text : i18n.get('label.refreshResource'),
                scope : this,
                handler : this._onRefresh
            }, {
                text : i18n.get('label.deleteResource'),
                scope : this,
                handler : this._onDelete
            }]
        };
        
        this.treeStore = Ext.create('sitools.user.store.DatastorageTreeStore');
        
        this.tree = Ext.create('Ext.tree.Panel', {
            region : 'west',
            width : 300,
            autoScroll : true,
            bodyStyle : 'background-color:white;',
            store : this.treeStore,
            root : {
                text : "UserStorage",
                leaf : false,
                url :this.AppUserStorage,
                commonStoreId : Ext.id(),
                id : Ext.id()
            },
            split : true,
            collapsible : true,
            collapseDirection : "left",
            forceFit : true,
            rowLines : true,
            selModel : Ext.create("Ext.selection.TreeModel", {
                mode : "SINGLE"
            }),
            listeners : {
//               beforeload : function (node) {
//                   return node.isRoot || Ext.isDefined(node.attributes.children);
//               },
               beforeitemexpand : function (node) {
                   node.removeAll();
                   var reference = new Reference(node.get('url'));
                   var url = loadUrl.get('APP_URL') + reference.getFile();
                   Ext.Ajax.request({
                       url : url,
                       method : 'GET',
                       scope : this,
                       success : function (ret) {
                           try {
                               var Json = Ext.decode(ret.responseText);
                               Ext.each(Json, function (child) {
                                   var text = child.text;
                                   if (child.cls == "folder") {
                                	   child.leaf = false;
                                   } else {
                                	   child.leaf = true;
                                   }
                                   
                                   if (child.leaf) {
                                       text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
                                   }
                                   var reference = new Reference(child.url);
                                   var url = reference.getFile();
                                   node.appendChild({
                                       cls : child.cls,
                                       text : decodeURIComponent(text),
                                       url : url,
                                       leaf : child.leaf,
                                       expandable : true,
                                       children : []
                                   });
                               });
                               return true;
                           } catch (err) {
                               Ext.Msg.alert(i18n.get('warning'), err);
                               return false;
                           }
                       },
                       failure : function (ret) {
                           return null;
                       }
                   });
                   return true;
               }
            }
        });

        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
            	type : 'ajax',
                method : 'GET',
                url :this.AppUserStorage,
                headers : {
//                    'Accept' : 'application/json+sitools-directory'
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
        
        this.items = [ this.tree ];
        
//        Ext.apply(this, {
//            expanded : true,
//            useArrows : true,
//            autoScroll : true,
//            layout : 'fit', 
//            animate : true,
//            enableDD : false,
//            containerScroll : true,
//            //frame : true,
////            loader : new Ext.tree.TreeLoader(),
//            rootVisible : true,
//            root : {
//                text : userLogin,
//                children : [],
//                nodeType : 'async',
//                url : loadUrl.get('APP_URL') + this.AppUserStorage + "/"
//            },
//
//            // auto create TreeLoader
//            listeners : {
//                scope : this,
//                beforeload : function (node) {
//                    return node.isRoot || Ext.isDefined(node.attributes.children);
//                },
//                beforeexpandnode : function (node) {
//                    node.removeAll();
//                    var reference = new Reference(node.attributes.url);
//                    var url = reference.getFile();
//                    Ext.Ajax.request({
//                        url : url,
//                        method : 'GET',
//                        scope : this,
//                        success : function (ret) {
//                            try {
//                                var Json = Ext.decode(ret.responseText);
//                                Ext.each(Json, function (child) {
//                                    var text = child.text;
//                                    if (child.leaf) {
//                                        text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
//                                    }
//                                    var reference = new Reference(child.url);
//                                    var url = reference.getFile();
//                                    node.appendChild({
//                                        cls : child.cls,
//                                        text : decodeURIComponent(text),
//                                        url : url,
//                                        leaf : child.leaf,
//                                        children : [],
//                                        checked : child.checked
//                                    });
//                                });
//                                return true;
//                            } catch (err) {
//                                Ext.Msg.alert(i18n.get('warning'), err);
//                                return false;
//                            }
//                        },
//                        failure : function (ret) {
//                            return null;
//                        }
//                    });
//                    return true;
//                }, 
//                click : function (n) {
//                    if (n.attributes.leaf) {
//                        var url = n.attributes.url;
//                        viewFileContent(url, n.attributes.text);
//                    }
//                }
//            }
//        });

        this.callParent(arguments);

    },
    
    render : function () {
    	this.reloadNode(this.tree.getRootNode());
    	this.callParent(arguments);
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
    
    isRootNode : function (node) { 
        return this.tree.getRootNode() === node;        
    },
    
//    onRender : function () {
//        this.callParent(arguments);
//        this.setUserStorageSize();
//    },
//    
//    setUserStorageSize : function () {
//        Ext.Ajax.request({
//            method : "GET",
//            scope : this,
//            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace("{identifier}", userLogin) + "/status", 
//            success : function (ret) {
//                var json = Ext.decode(ret.responseText);
//                if (!json.success) {
//                    return;
//                }
//                var storage = json.userstorage.storage;
//                var totalSpace = storage.quota;
//                var usedSpace = storage.busyUserSpace;
//                var pourcentage = usedSpace / totalSpace * 100;
//                var cls = null; 
//                
//                if (pourcentage >= 90 && pourcentage < 100) {
//                    cls = "x-status-warning";
//                }
//                else if (pourcentage > 100) {
//                    cls = "x-status-error";
//                }
//                var str = Ext.String.format(i18n.get('label.diskSpaceLong'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
//                
//                this.getTopToolbar().setText(str);
//                this.getTopToolbar().setIcon(cls);
//                this.doLayout();
//            }
//        });
//    },
//    
//    _onRefresh : function () {
//        this.getRootNode().collapse();
//        this.setUserStorageSize();
//        // this.treePanel.getLoader().load(this.treePanel.getRootNode());
//    },
//
//    _onDelete : function () {
//        var selNodes = this.getChecked();
//        if (selNodes.length === 0) {
//            return;
//        }
//
//        Ext.each(selNodes, function (node) {
//            Ext.Ajax.request({
//                method : 'DELETE',
//                url : node.attributes.url + "?recursive=true",                
//                scope : this,
//                success : function (response, opts) {
//                    var notify = new Ext.ux.Notification({
//                        iconCls : 'x-icon-information',
//                        title : i18n.get('label.information'),
//                        html : i18n.get('label.resourceDeleted'),
//                        autoDestroy : true,
//                        hideDelay : 1000
//                    });
//                    notify.show(document);
//                    node.destroy();
//                },
//                failure : alertFailure
//            }, this);
//        });
//    }

});
