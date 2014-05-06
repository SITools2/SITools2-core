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

Ext.namespace('sitools.user.modules');
/**
 * Help Module
 * @class sitools.user.modules.contentViewerModule
 * @extends Ext.Panel
 */
sitools.user.modules.contentViewerModule = Ext.extend(Ext.Panel, {
    /**
     * the node to activate
     * @type Ext.tree.TreeNode
     */
    activeNode : null,
    initComponent : function () {
        
        // DATASTORAGE POSTEL PROD URL
//        this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + "/datastorage/user/postelprod/postel-site";
        
        Ext.each(this.listProjectModulesConfig, function (config){
            switch (config.name){
            case "dynamicUrlDatastorage" :
                this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.value;
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
        
        
        this.layout = "border";
        
        this.tree = new Ext.tree.TreePanel({
            region : 'west',
            animate : true,
            width : 200,
            rootVisible : false,
            autoScroll : true,
            split : true,
            collapsible : true,
            collapsed : false,
            title : i18n.get('label.sitePlan'),
            root : {
                text : "root"
            },
            tbar : new sitools.user.modules.cmsViewerTreeToolbar({
                scope : this,
                hidden : false,
                showLanguage : false,
                callback : function (combo, rec, index) {
                    var locale = rec.data.locale;
                    this.changeLanguage(locale);
                }
            }),
            loader : new Ext.tree.TreeLoader({
                requestMethod : 'GET',
                url : this.jsonUrl,
                createNode : function(attr){
                    var isPdf = function (text) {
                        var imageRegex = /\.(pdf)$/;
                        if (!Ext.isEmpty(text)) {
                            return (text.match(imageRegex));
                        } else {
                            return false;
                        }
                    };
                    var listeners = {
                        scope : this,
                        beforeappend : function (tree, parent, item ){
                            if (item.isLeaf()){
                                if (item.attributes.sync){
                                    if (isPdf(item.attributes.link)) {
                                        item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png');
                                    }
                                    else {
                                        item.setIcon(loadUrl.get('APP_URL') + '/cots/extjs/resources/images/default/tree/leaf.gif');
                                    }
                                }
                                else {
                                    item.hidden = true;
                                }
                            }
                            return true;
                        }
                    };
                    Ext.apply(attr, {
                        listeners : listeners
                    });
                    // apply baseAttrs, nice idea Corey!
                    if(this.baseAttrs){
                        Ext.applyIf(attr, this.baseAttrs);
                    }
                    if(this.applyLoader !== false && !attr.loader){
                        attr.loader = this;
                    }
                    if(Ext.isString(attr.uiProvider)){
                       attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
                    }
                    if(attr.nodeType){
                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
                    }else{
                        return attr.leaf ?
                                    new Ext.tree.TreeNode(attr) :
                                    new Ext.tree.AsyncTreeNode(attr);
                    }
                },
                listeners : {
                    scope : this,
                    load : function () {
                        if (!Ext.isEmpty(this.activeNode)) {
                            var node = this.tree.getNodeById(this.activeNode);
                            if (!Ext.isEmpty(node)) {
                                this.tree.selectPath(node.getPath());
                                this.treeAction(node);
                            } else {
                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
                            }
                        }
                        else {
                            var rootNode = this.tree.getRootNode();
                            if (!Ext.isEmpty(rootNode) && rootNode.childNodes.length > 0) {
		                        var rootLink = rootNode.childNodes[0].attributes.link;
						        if (!Ext.isEmpty(rootLink)) {
						            var defaultUrl;
						            if (rootLink.search("http://") == -1){
						                defaultUrl = this.CONST_URLDATASTORAGE + rootLink;
						            }
						            else {
						                defaultUrl = rootLink;
						            }
						            this.htmlReader.remove();
						            this.htmlReader.setSrc(defaultUrl);
						            this.tree.selectPath(rootNode.childNodes[0].getPath());
						        }
                            }
                        }
                    },
                    loadexception : function ( treeloader, node, response) { 
                        if (response.status == 404) {
                            this.tree.getTopToolbar().setVisible(true);
                             Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.nocontentavailable'));
                        }
                    }
                }
            }),
            listeners : {
                scope : this,
                click : function (node) {
//                    if (node.isLeaf()){
                        this.treeAction(node);
//                    }
                }
            }
        });

        var htmlReaderCfg = {
                defaults : {
                    padding : 10
                },
                layout : 'fit',
                region : 'center'
        };
        
//        if (this.checkHtmlUrlValidation()){
//            htmlReaderCfg.defaultSrc = this.htmlUrl;
//        }
        
        this.htmlReader = new Ext.ux.ManagedIFrame.Panel(htmlReaderCfg);

        this.items = [ this.tree, this.htmlReader ];
        
        this.tbar = {
                xtype : 'toolbar',
                cls : 'services-toolbar',
                height : 15,
                defaults : {
                    scope : this,
                    cls : 'services-toolbar-btn'
                },
                items : [ ]
        };

        sitools.user.modules.contentViewerModule.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.user.modules.contentViewerModule.superclass.onRender.apply(this, arguments);
        this.tree.getRootNode().expand(true);
    },
    
    /**
     * Refresh the tree, reload the tree and print show it again
     */    
    refreshTree : function (){
        this.tree.getLoader().load(this.tree.getRootNode(), function () {
            this.tree.getRootNode().expand(true);                        
        }, this);
    },
    
    /**
     * Change the language, reload the tree with the new language
     * @param {String} language the language locale
     */
    changeLanguage : function (language) {
        var urlJson = this.jsonUrlTemplate.replace("{locale}", language);
        this.tree.getLoader().url = urlJson;
        this.refreshTree();     
    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        var nodeLink = node.attributes.link;

        if (!Ext.isDefined(nodeLink)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noLinkDefined'));
            return;
        }
        
        nodeLink += "?processTemplate=true";
        
        var url = nodeLink;
        if (url.indexOf("http://") === -1) {
            url = this.CONST_URLDATASTORAGE + "/" + nodeLink;    
            
        }
        
        this.htmlReader.remove();
        this.htmlReader.setSrc(url);
    },
    
    checkHtmlUrlValidation : function (){
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

    }
    
});

/**
 * @static
 * Implementation of the method getParameters to be able to load view Config Module panel.
 * @return {Array} the parameters to display into administration view. 
 */
sitools.user.modules.contentViewerModule.getParameters = function () {
    
    return [{
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.urlDatastorage"),
            allowBlank : false,
            id : "urlDatastorageId",
            hidden : true,
            name : "dynamicUrlDatastorage",
            value : ""
        }
    }, {
        jsObj : "Ext.form.ComboBox", 
        config : {
            fieldLabel : i18n.get("label.urlDatastorage"),
            allowBlank : false,
            typeAhead : true,
            editable : false,
            triggerAction : 'all',
            width : 200,
            valueField : 'name',
            displayField : 'name',
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories',
                remoteSort : true,
                idProperty : 'id',
                fields : [ {
                    name : 'id',
                    type : 'string'
                }, {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'attachUrl',
                    type : 'string'
                }]
            }),
            listeners: {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "the datastorage url attachment (cf. Storage)"
                    });
                },
                select : function (combo, rec, ind) {
                    var urlAttachField = this.ownerCt.getComponent("urlDatastorageId");
                    urlAttachField.setValue(rec.data.attachUrl);
                }
            },
            name : "nameDatastorageSrc",
            value : ""
        }
    }];
};

Ext.reg('sitools.user.modules.contentViewerModule', sitools.user.modules.contentViewerModule);
