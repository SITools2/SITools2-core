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
/*global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.user.view.modules.contentViewer');
/**
 * Help Module
 * @class sitools.user.modules.contentViewerModule
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.modules.contentViewer.ContentViewerView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.contentViewerView',
    
    activeNode : null,
    layout : 'border',
    
    initComponent : function () {
        
        // DATASTORAGE POSTEL PROD URL
//        this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + "/datastorage/user/postelprod/postel-site";
        
        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config){
            switch (config.get('name')) {
            case "dynamicUrlDatastorage" :
                this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.get('value');
                break;
            }
        }, this);
        
        this.jsonUrlTemplate = this.CONST_URLDATASTORAGE + '/json/data_{locale}.json';
        
        var localeStr = locale.getLocale();
        this.jsonUrl = this.jsonUrlTemplate.replace("{locale}", localeStr);         
        
//        if (SitoolsDesk.app.language == "en"){
//            this.url = this.CONST_URLDATASTORAGE + "/en/welcome.html";
//        }else {
//            this.url = this.CONST_URLDATASTORAGE + "/fr/accueil.html";
//            this.jsonUrl = this.CONST_URLDATASTORAGE + "/json/postel_fr.json";
//        }
        
        this.treeStore = Ext.create('sitools.user.store.ContentEditorTreeStore', {
            listeners : {
                beforeappend : function (nodeParent, nodeToAppend) {
                    if (!nodeToAppend.isLeaf())
                        return;
                    
                    if (!Ext.isEmpty(nodeToAppend.get('sync')) && !nodeToAppend.get('sync'))
                        return false;
                }
            }
        });
        this.treeStore.setCustomUrl(this.jsonUrl);          

        this.tree = Ext.create('Ext.tree.Panel', {
            title : i18n.get('label.sitePlan'),
            store : this.treeStore,
            region : 'west',
            animate : true,
            width : 320,
            autoScroll : true,
            collapsible : true,
            border : false,
            enableDD : true,
            forceFit : true,
            rowLines : true,
            rootVisible : false,
            split : true,
            root : {
                text : "root",
                url : this.jsonUrl,
                leaf: false,
                link: "",
                children : []
            },
            tbar : Ext.create('sitools.user.view.modules.contentViewer.ContentViewerTreeToolbar', {
                scope : this,
                hidden : false,
                showLanguage : false,
                callback : function (combo, rec, index) {
                    var locale = rec.data.locale;
                    this.changeLanguage(locale);
                }
            }),
//            loader : new Ext.tree.TreeLoader({
//                requestMethod : 'GET',
//                url : this.jsonUrl,
//                createNode : function(attr){
//                    var isPdf = function (text) {
//                        var imageRegex = /\.(pdf)$/;
//                        if (!Ext.isEmpty(text)) {
//                            return (text.match(imageRegex));
//                        } else {
//                            return false;
//                        }
//                    };
//                    var listeners = {
//                        scope : this,
//                        beforeappend : function (tree, parent, item ){
//                            if (item.isLeaf()){
//                                if (item.attributes.sync){
//                                    if (isPdf(item.attributes.link)) {
//                                        item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png');
//                                    }
//                                    else {
//                                        item.setIcon(loadUrl.get('APP_URL') + '/cots/extjs/resources/images/default/tree/leaf.gif');
//                                    }
//                                }
//                                else {
//                                    item.hidden = true;
//                                }
//                            }
//                            return true;
//                        }
//                    };
//                    Ext.apply(attr, {
//                        listeners : listeners
//                    });
//                    // apply baseAttrs, nice idea Corey!
//                    if(this.baseAttrs){
//                        Ext.applyIf(attr, this.baseAttrs);
//                    }
//                    if(this.applyLoader !== false && !attr.loader){
//                        attr.loader = this;
//                    }
//                    if(Ext.isString(attr.uiProvider)){
//                       attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
//                    }
//                    if(attr.nodeType){
//                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
//                    }else{
//                        return attr.leaf ?
//                                    new Ext.tree.TreeNode(attr) :
//                                    new Ext.tree.AsyncTreeNode(attr);
//                    }
//                },
//                listeners : {
//                    scope : this,
//                    load : function () {
//                        if (!Ext.isEmpty(this.activeNode)) {
//                            var node = this.tree.getNodeById(this.activeNode);
//                            if (!Ext.isEmpty(node)) {
//                                this.tree.selectPath(node.getPath());
//                                this.treeAction(node);
//                            } else {
//                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
//                            }
//                        }
//                        else {
//                            var rootNode = this.tree.getRootNode();
//                            if (!Ext.isEmpty(rootNode) && rootNode.childNodes.length > 0) {
//		                        var rootLink = rootNode.childNodes[0].attributes.link;
//						        if (!Ext.isEmpty(rootLink)) {
//						            var defaultUrl;
//						            if (rootLink.search("http://") == -1){
//						                defaultUrl = this.CONST_URLDATASTORAGE + rootLink;
//						            }
//						            else {
//						                defaultUrl = rootLink;
//						            }
//						            this.htmlReader.remove();
//						            this.htmlReader.setSrc(defaultUrl);
//						            this.tree.selectPath(rootNode.childNodes[0].getPath());
//						        }
//                            }
//                        }
//                    },
//                    loadexception : function ( treeloader, node, response) { 
//                        if (response.status == 404) {
//                            this.tree.getTopToolbar().setVisible(true);
//                             Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.nocontentavailable'));
//                        }
//                    }
//                }
//            }),
            listeners : {
                scope : this,
                itemclick : function (tree, node, item) {
                    this.treeAction(node);
                },
                itemcontextmenu : function (tree, node, item, index, e) {
                    e.stopEvent();
                }
            }
        });

        var htmlReaderCfg = {
            defaults : {
                padding : 10
            },
            style : 'background-color: white;',
            border : false,
            layout : 'fit',
            region : 'center'
        };
        
//        if (this.checkHtmlUrlValidation()){
//            htmlReaderCfg.defaultSrc = this.htmlUrl;
//        }
        
        this.htmlReader = Ext.create('Ext.ux.IFrame', htmlReaderCfg);
        this.items = [ this.tree, this.htmlReader ];
        
        this.callParent(arguments);
    },

    onRender : function () {
        this.callParent(arguments);
        
        this.treeStore.load({
            callback : function (nodes, operation) {
                if (operation.success) {
	                this.tree.getRootNode().expand(true);
                } else {
                     if (operation.error.status === 404) {
	                      var msg = Ext.Msg.alert(i18n.get('label.warning'),
	                          i18n.get('label.noJsonFileFound'));
                              
	                      Ext.defer(function () {
	                          Ext.WindowManager.bringToFront(msg);
	                      }, 300);
	                  } else if (operation.error.status === 403) {
	                      var msg = Ext.Msg.alert(i18n.get("warning.serverError"), i18n.get("label.forbiddenResource"));
	                      Ext.defer(function () {
	                          Ext.WindowManager.bringToFront(msg);
	                          this.getEl().mask();
	                      }, 300, this);
	                  }
                 }
            }
        })
    },
    
    /**
     * Refresh the tree, reload the tree and print show it again
     */    
    refreshTree : function (){
        this.tree.store.load({
            callback : function () {
	            this.tree.getRootNode().expand(true);                        
            }
        }, this);
    },
    
    /**
     * Change the language, reload the tree with the new language
     * @param {String} language the language locale
     */
    changeLanguage : function (language) {
        var urlJson = this.jsonUrlTemplate.replace("{locale}", language);
        this.tree.store.setCustomUrl(urlJson);
        this.refreshTree();     
    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        var nodeLink = node.get('link');

        if (!Ext.isDefined(nodeLink)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noLinkDefined'));
            return;
        }
        
        var url = nodeLink;
        if (this.isLocal(url)) {
            url = this.CONST_URLDATASTORAGE + "/" + nodeLink;
            if(this.isPdf(url)){
                url = Ext.urlAppend(url, "dc=" + new Date().getTime());
            } else {
                url = Ext.urlAppend(url, "processTemplate=true");
            }
        }
        
        this.htmlReader.load(url);
    },
    
    checkHtmlUrlValidation : function () {
        var res = false;
        Ext.Ajax.request({
            url : this.htmlUrl,
            method : 'GET',
            scope : this,
            failure : function (response, opts){
                res = false;
            },
            success : function (ret){
                res = true;
            }
        });
        return res;
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    },
    isPdf : function (text) {
        var imageRegex = /\.(pdf)$/;
        if (!Ext.isEmpty(text)) {
            return (text.match(imageRegex));
        } else {
            return false;
        }
    },
    
    isLocal : function (url) {
        return (url.indexOf("http://") == -1 && url.indexOf("https://") == -1); 
    }
    
});
