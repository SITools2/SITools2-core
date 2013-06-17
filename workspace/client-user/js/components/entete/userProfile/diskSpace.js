/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.user.component.entete.userProfile');

/**
 * @class sitools.user.component.entete.userProfile.diskSpace
 * @extends Ext.tree.TreePanel
 */
sitools.user.component.entete.userProfile.diskSpace = Ext.extend(Ext.tree.TreePanel, {
    autoScroll : true, 
    

    initComponent : function () {
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";

        
        this.tbar = new Ext.ux.StatusBar({
            statusAlign: 'right'              
        });
        
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

        sitools.user.component.entete.userProfile.diskSpace.superclass.initComponent.call(Ext.apply(this, {
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
                    var reference = new Reference(node.attributes.url);
                    var url = reference.getFile();
                    Ext.Ajax.request({
                        url : url,
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                Ext.each(Json, function (child) {
                                    var text = child.text;
                                    if (child.leaf) {
                                        text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
                                    }
                                    var reference = new Reference(child.url);
                                    var url = reference.getFile();
                                    node.appendChild({
                                        cls : child.cls,
                                        text : text,
                                        url : url,
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
                        var url = n.attributes.url;
                        viewFileContent(url, n.attributes.text);
                    }
                }
            }
        }));

    },
    onRender : function () {
        sitools.user.component.entete.userProfile.diskSpace.superclass.onRender.apply(this, arguments);
        this.setUserStorageSize();
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
                var str = String.format(i18n.get('label.diskSpaceLong'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
                
                this.getTopToolbar().setText(str);
                this.getTopToolbar().setIcon(cls);
                this.doLayout();
            }
        });
    },
    
    _onRefresh : function () {
        this.getRootNode().collapse();
        this.setUserStorageSize();
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
