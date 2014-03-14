
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/
Ext.namespace('sitools.admin.menu');

/**
 * Create A dynamic Dataview in function of the sitools.admin.menu.dataView Dataview
 * 
 * @cfg {Ext.XTemplate} the template to display in the dataView
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.admin.menu.seeAlso
 * @extends Ext.DataView
 */
sitools.admin.menu.seeAlso = Ext.extend(Ext.DataView, {
    
    id: 'links',
    
    itemSelector: 'div.links',
    overClass   : 'links-hover',
    singleSelect: true,
    multiSelect : false,
    autoScroll  : false,
    
    initComponent : function () {
        
        var tpl = new Ext.XTemplate(
           '<div class="title-seeAlso">See Also...</div>',
           '<tpl for=".">',
               '<tpl if="this.isNotEmpty(links)">',
                   '<tpl for="links">',
                            '<div id="{nodeName}" name="{text}" class="links">',
                                '<img class="icon-seeAlso" width="16" height="16" src="{iconMenu}" />',
                                '<span id="{nodeName}">{label}</span>',
                            '</div>',
                    '</tpl>',
                '</tpl>',
            '</tpl>',
            {
                compiled : true,
                isNotEmpty : function (children) {
                    return !Ext.isEmpty(children);
                }, 
                isEmpty : function (children) {
                    return Ext.isEmpty(children);
                }
            }
        );
        this.tpl = tpl;
        this.height = Ext.getBody().getSize().height - 220;
        
        this.listeners = {
            click : function (t, ind, node, e) {
                var nodeName = node.id;
                var title = node.attributes[1].textContent;
                var rec = seeAlsoMenu.getStore().getAt(0);
                var path = this.getPath(rec, title);
                this.openSubCategory(t, title, e, nodeName, path);
            }
        };
        
        this.store = new Ext.data.JsonStore({
            root : '',
            restful : true,
            remoteSort : false,
            fields : [
                {name: 'nodeName'},
                {name : 'id'},
                {name : 'parentNode'},
                {name : 'children'}, 
                {name : 'iconMenu'},
                {name : 'category'},
                {name : 'links'},
                {name : 'label'},
                {name : 'path'}
            ]
        });
        
        this.allButtons = [];
        
        sitools.admin.menu.seeAlso.superclass.initComponent.call(this);
    },
    
    /**
     * Get the path from the split of a string
     * 
     * @param rec the record
     * @param title the title of the item to get the path
     * @returns pathTab a tab of strings
     */
    getPath : function (rec, title) {
        var reg = new RegExp("/");
        
        for (var i = 0; i < rec.data.links.length; i++){
            if (rec.data.links[i].text == title){
                var pathTab = rec.data.links[i].path.split(reg);
                return pathTab;
            }
        }
    },

    /**
     * Display children of a node
     * 
     * @param t the parent element
     * @param title the title
     * @param e the event
     * @param nodeName the nodeId
     * @param path
     */
    openSubCategory : function (t, title, e, nodeName, path) {
        dataViewMenu.allButtons = [];

        var isAddable = this.populateStore(t, title);
        var toolbar = mainPanel.toolbars[0];
        var textRoot = toolbar.items.items[0].text;
        // On ajoute le noeud sur lequel on vient de cliquer en tant que bouton de la toolbar
        toolbar.removeAll();
        toolbar.addText(textRoot);
        toolbar.addSeparator();
        this.addRootButton();
        
        for (var i = 0; i < path.length; i++){
            var button = {
                    xtype : 'button', 
                    text : path[i],
                    id : path[i] + 'Id',
                    scope : t,
                    path : path,
                    handler : function (btn, e) {
                        this.populateStore(this, btn.getText());
                        
                        dataViewMenu.deleteItemsFromToolbar(mainPanel.toolbars[0], btn.getText(), path);
                        dataViewWin.show();
                        mainPanel.getTopToolbar().show();
                        var dataViewEl = dataViewWin.getEl();
                        dataViewEl.fadeIn({
                            endOpacity: 0.95, //can be any value between 0 and 1 (e.g. .5)
                            easing: 'easeOut',
                            duration: 0.5
                        });
                    }
            };
            this.addButton (button, toolbar);
        }
        mainPanel.doLayout();
        
        if (!isAddable) {
            this.treeAction(title, nodeName);
        }
    },
    
    /**
     * Add a button to the a toolbar
     * 
     * @param btn the button to add
     * @param toolbar which receive the button
     */
    addButton : function (btn, toolbar) {
        toolbar.add(btn);
        toolbar.addSeparator();
        dataViewMenu.allButtons.push(btn);
    },
    
    /**
     * Add the Root button to the top toolbar
     */
    addRootButton : function () {
        var button = {
                xtype : 'button', 
                text : 'root',
                id : 'rootId',
                scope : this,
                handler : function (btn, e) {
                    this.populateStore(this, btn.getText());
                    dataViewMenu.deleteItemsFromToolbar(mainPanel.toolbars[0], btn.getText());
                    dataViewWin.show();
                    mainPanel.getTopToolbar().show();
                    var dataViewEl = dataViewWin.getEl();
                    dataViewEl.fadeIn({
                        endOpacity: 0.95, //can be any value between 0 and 1 (e.g. .5)
                        easing: 'easeOut',
                        duration: 0.5
                    });
                }
            };
            
            this.addButton (button, mainPanel.toolbars[0]);
            mainPanel.doLayout();
    },
    
    /**
     * Populate the store with children of a node
     * 
     * @param t
     * @param title the title of the item to populate
     * @returns {Boolean}
     */
    populateStore : function (t, title) {
        var children = t.getChildrenByParentNode(dataViewMenu.allData, title);
        if (!Ext.isEmpty(children)) {
            t.store.removeAll();
            
            dataViewMenu.store.removeAll();
            dataViewMenu.store.loadData(children);
            return true;
        }
        else {
            return false;
        }
    },
    
    /**
     * Get All the children of a parent node
     * 
     * @param data all the node
     * @param parentNode the name of the parentNode
     * @returns
     */
    getChildrenByParentNode : function (data, parentNode) {
        if (Ext.isEmpty(data)) {
            return null;
        }
        
        if (parentNode == 'root') {
            return data;
        }
        else {
            for (var i = 0; i < data.length; i++) {
                if (data[i].text == parentNode) {
                    return data[i].children;
                    
                }
                else {
                    var tmp = this.getChildrenByParentNode(data[i].children, parentNode);
                    if (!Ext.isEmpty(tmp)) {
                        return tmp;
                    }
                }
            }
        }
    },
    
    /**
     * Create a component from his component Registry name
     * 
     * @param title
     * @param nodeName
     */
    treeAction : function (title, nodeName) {
        // Getting nodeName
        var nodeId = title + 'id';
        
        if (!Ext.isDefined(nodeName)) {
            Ext.Msg.alert('warning', 'Undefined');
            return;
        }
        var selectedNode = this.getSelectedRecords()[0];
        
        if (!Ext.ComponentMgr.isRegistered('s-' + nodeName)) {
            Ext.Msg.alert('warning', 'label.component' + ' \'' + 's-' + nodeName + '\' ' + 'msg.undefined');
            return;
        }
        
        var pan_config = new sitools.admin.menu.dataView();
        
        
        // Loading component 's-nodeName'
       mainPanel.removeAll();
       mainPanel.add({
            width: "100%", 
            items : [ {
                xtype : 's-box',
                label : i18n.get('label.' + nodeName),
                items : [ {
                    xtype : 's-' + nodeName, 
                    sitoolsType : "mainAdminPanel"
                } ],
                idItem : nodeId //selectedNode.get('id')
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
        var helpPanel = new Ext.ux.ManagedIFrame.Panel({
            id : ID.PANEL.HELP,
            width : "100%", 
            flex : 1,
            // autoScroll:true,
            defaultSrc : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html",
            defaults : {
                padding : 10
            }
        });
        mainPanel.add(
            helpPanel
        );
        mainPanel.doLayout();
        helpPanel.setVisible(SHOW_HELP);
        
        helpUrl = loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html";
        mainPanel.getTopToolbar().show();
        dataViewWin.hide();
       
    }
});