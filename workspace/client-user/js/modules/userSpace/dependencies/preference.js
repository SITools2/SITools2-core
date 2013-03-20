/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, userLogin, document, alertFailure, SitoolsDesk, userLogin, DEFAULT_ORDER_FOLDER, loadUrl, viewFileContent*/

Ext.namespace('sitools.user.modules.userSpaceDependencies');

sitools.user.modules.userSpaceDependencies.preference = Ext.extend(Ext.tree.TreePanel, {
    autoScroll : true, 
    

    initComponent : function () {
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";

        this.buttons = [ {
                text : i18n.get('label.refreshResource'),
                scope : this,
                handler : this._onRefresh
            }, {
                text : i18n.get('label.deleteResource'),
                scope : this,
                handler : this._onDelete
            }, {
                text : i18n.get('label.close'),
                scope : this,
                handler : function () {
                    this.ownerCt.destroy();
                }
            } ];

        sitools.user.modules.userSpaceDependencies.preference.superclass.initComponent.call(Ext.apply(this, {
        	expanded : true,
            useArrows : true,
            autoScroll : true,
            layout : 'fit', 
            animate : true,
            enableDD : false,
            containerScroll : true,
            //frame : true,
            loader : new Ext.tree.TreeLoader(),
            rootVisible : true,
            root : {
                text : userLogin,
                children : [],
                nodeType : 'async',
                url : loadUrl.get('APP_URL') + this.AppUserStorage + "/"
            },

            // auto create TreeLoader
            listeners : {
                scope : this,
                beforeload : function (node) {
                    return node.isRoot || Ext.isDefined(node.attributes.children);
                },
                beforeexpandnode : function (node) {
                    node.removeAll();
                    Ext.Ajax.request({
                        url : node.attributes.url,
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                Ext.each(Json, function (child) {
                                    node.appendChild({
                                        cls : child.cls,
                                        text : child.text,
                                        url : child.url,
                                        leaf : child.leaf,
                                        children : [],
                                        checked : child.checked
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
                }, 
                click : function (n) {
                    if (n.attributes.leaf) {
                        var nodeTmp = n;
                        var tabTmp = [];
                        while (! Ext.isEmpty(nodeTmp.parentNode)) {
                            nodeTmp = nodeTmp.parentNode;
                            tabTmp.push(nodeTmp.attributes.text);
                        }
                        tabTmp.reverse();
                        
                        
                        var url = loadUrl.get('APP_URL') + (loadUrl.get('APP_USERSTORAGE_USER_URL') + "/files").replace('{identifier}', tabTmp.join("/")) + "/" + n.attributes.text;
                        viewFileContent(url, n.attributes.text);
                    }
                }
            }        	
        }));

    },
    _onRefresh : function () {
        this.getRootNode().collapse();
        // this.treePanel.getLoader().load(this.treePanel.getRootNode());
    },

    _onDelete : function () {
        var selNodes = this.getChecked();
        if (selNodes.length === 0) {
            return;
        }

        Ext.each(selNodes, function (node) {
            Ext.Ajax.request({
                method : 'DELETE',
                url : node.attributes.url + "?recursive=true",                
                scope : this,
                success : function (response, opts) {
                    var notify = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('label.resourceDeleted'),
                        autoDestroy : true,
                        hideDelay : 1000
                    });
                    notify.show(document);
                    node.destroy();
                },
                failure : alertFailure
            }, this);
        });
    }

});
