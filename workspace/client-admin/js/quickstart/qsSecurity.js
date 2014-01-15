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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure*/
Ext.namespace('sitools.admin.quickStart');

sitools.admin.quickStart.qsSecurity = Ext.extend(Ext.Panel, {
    forceLayout : true, 
    layout : "vbox",
    border : false, 
    layoutConfig : {
        align : 'center',
        pack : 'start'
    },
    bodyCssClass : 'quickStart',
    initComponent : function () {
        
        var title = new Ext.form.Label({
            cls : 'qs-h1',
            html : i18n.get('label.qsUserTitle'),
            listeners : {
                render : function (img) {
                    img.getEl().fadeIn({
                        easing : 'easeIn',
                        duration: 1
                    });
                }
            }
        });
        
        var desc = new Ext.form.Label({
            cls : 'qs-div',
            id : "start-desc",
            html : i18n.get('label.qsUserDesc'),
            listeners : {
                render : function (desc) {
                   desc.getEl().fadeIn({
                       easing : 'easeIn',
                       duration: 1
                   });
               }
            }
        });
        
        var imgUser = new Ext.form.Label({
            html : '<img id="qs-user-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/user-logo.png"/>',
            listeners : {
                scope : this,
                render : function (user) {
                    user.getEl().fadeIn({
                        endOpacity: 1,
                        easing : 'easeIn',
                        duration: 1,
                        useDisplay : true
                    });
                },
                afterrender : function (user) {
                    Ext.get("qs-user-logo").on('load', function () {
                        
                        user.getEl().alignTo("start-desc", "tl", [50, 30]);
                        
                        user.getEl().on('mouseleave', function (e, t, o) {
                            t.src = "/sitools/client-admin/res/html/quickStart/icons/user-logo.png"; 
                         });
                        user.getEl().on('mouseenter', function (e, t, o) {
                             t.src = "/sitools/client-admin/res/html/quickStart/icons/user-logo-hover.png"; 
                         });
                        user.getEl().on('click', function (e, t, o) {
                            this.qs.openFeature("usrNodeId");
                         }, this);
                        
                        new Ext.ToolTip({
                            target : 'qs-user-logo',
                            anchor: 'right',
                            autoShow : true,
                            html : "<b>Open User</b>"
                        });
                        
                    }, this);
                }
            }
        });
        
        var imgShare = new Ext.form.Label({
            html : '<img id="qs-share-logo" src="/sitools/client-admin/res/html/quickStart/icons/share-logo.png"/>',
            listeners : {
                scope : this,
                render : function (imgShare) {
                    imgShare.getEl().fadeIn({
                        endOpacity: 1,
                        easing : 'easeIn',
                        duration: 1,
                        useDisplay : true
                    });
                },
                afterrender : function (group) {
                    Ext.get("qs-share-logo").on('load', function () {
                        group.getEl().alignTo("qs-user-logo", "tr",  [82, 50]);
                    }, this);
                }
            }
        });
        
        var imgGroup = new Ext.form.Label({
            html : '<img id="qs-group-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/group-logo.png"/>',
            listeners : {
                scope : this,
                render : function (imgProjet) {
                    imgProjet.getEl().fadeIn({
                        endOpacity: 1,
                        easing : 'easeIn',
                        duration: 1.5,
                        useDisplay : true
                    });
                },
                afterrender : function (group) {
                    Ext.get("qs-group-logo").on('load', function () {
                        
                        group.getEl().on('mouseleave', function (e, t, o) {
                           t.src = "/sitools/client-admin/res/html/quickStart/icons/group-logo.png"; 
                        });
                        group.getEl().on('mouseenter', function (e, t, o) {
                            t.src = "/sitools/client-admin/res/html/quickStart/icons/group-logo-hover.png"; 
                        });
                        group.getEl().on('click', function (e, t, o) {
                            this.qs.openFeature("grpNodeId");
                        }, this);
                        
                        group.getEl().alignTo("qs-share-logo", "bl",  [-35, 28]);
                        
                        new Ext.ToolTip({
                            target : 'qs-group-logo',
                            anchor: 'bottom',
                            autoShow : true,
                            html : "<b>Open Group</b>"
                        });
                        
                    }, this);
                }
            }
        });
        
        var imgRole = new Ext.form.Label({
            html : '<img id="qs-role-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/role-logo.png"/>',
            listeners : {
                scope : this,
                render : function (imgProjet) {
                    imgProjet.getEl().fadeIn({
                        endOpacity: 1,
                        easing : 'easeIn',
                        duration: 1.5,
                        useDisplay : true
                    });
                },
                afterrender : function (group) {
                    Ext.get("qs-role-logo").on('load', function () {
                        
                        group.getEl().on('mouseleave', function (e, t, o) {
                           t.src = "/sitools/client-admin/res/html/quickStart/icons/role-logo.png"; 
                        });
                        group.getEl().on('mouseenter', function (e, t, o) {
                            t.src = "/sitools/client-admin/res/html/quickStart/icons/role-logo-hover.png"; 
                        });
                        group.getEl().on('click', function (e, t, o) {
                            this.qs.openFeature("roleNodeId");
                        }, this);
                        
                        group.getEl().alignTo("qs-user-logo", "tr",  [255, 0]);
                        
                        new Ext.ToolTip({
                            target : 'qs-role-logo',
                            anchor: 'left',
                            autoShow : true,
                            html : "<b>Open Role</b>"
                        });
                        
                    }, this);
                }
            }
        });
//        this.add(imgGroup);
//        this.doLayout();
        
        this.items = [title, desc, imgUser, imgShare, imgGroup, imgRole];
        
        sitools.admin.quickStart.qsSecurity.superclass.initComponent.call(this);
    },
    
    openUser : function () {
        var containerP = mainPanel.find('name', 'containerPanel')[0];
        var tree = treePanel.items.items[0];
        
        var node = tree.getNodeById("datasetsSqlNodeId");
        tree.getSelectionModel().select(node);
        
        containerP.removeAll();
        containerP.add(
            {
                width: "100%", 
                items : [ {
                    xtype : 's-box',
                    label : i18n.get('label.' + node.attributes.nodeName),
                    items : [ {
                        xtype : 's-' + node.attributes.nodeName, 
                        sitoolsType : "mainAdminPanel"
                    } ],
                    idItem : node.attributes.nodeId
                } ], 
                listeners : {
                    resize : function (panel, width, height) {
                        var size = panel.items.items[0].body.getSize();
                        var sBoxTitle = panel.items.items[0].items.items[0].getEl();
                        size = {
                            height : size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargins("t") + sBoxTitle.getMargins("b")), 
                            width : size.width - 8
                        };
                        var mainAdminPanel = panel.find("sitoolsType", "mainAdminPanel");
                        mainAdminPanel[0].setSize(size);
                    }
                }
            });
        containerP.doLayout();
    },
    
    openGroup : function () {
        
    }
    
});

