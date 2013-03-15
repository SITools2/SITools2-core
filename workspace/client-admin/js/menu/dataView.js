
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/
Ext.namespace('sitools.admin.menu');

//sitools.component.dataViewNodes = Ext.extend(Ext.DataView, {

/**
 * Create A Dataview from the file menu.json
 * 
 * @cfg {Ext.XTemplate} the template to display in the dataView
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.admin.menu.dataView
 * @extends Ext.DataView
 */
sitools.admin.menu.dataView = Ext.extend(Ext.DataView, {
	
    id: 'nodes',
    
    itemSelector: 'div.nodes',
    overClass   : 'nodes-hover',
    singleSelect: true,
    multiSelect : false,
    autoScroll  : true,
    
    initComponent : function () {
        
	    var tpl = new Ext.XTemplate(
            '<div id="adminMenu">', 
	            '<div class="title">Choose a Task...</div>',
	            '<div class="float columns">', 
				    '<tpl for=".">', 
				        '<tpl if="(this.round(xcount /2) - xindex) < 0"></div>', // Pour revenir à la ligne tout les 2 icones
				            '<div>', 
				        '</tpl>', 
				        '<div id="{nodeName}" class="nodes">',
				            '<img class="float"',
				                '<tpl if="this.isNotEmpty(children)">',
				                    'ext:qtip="',
				                        '<tpl for="children">',
				                            '{textChildren}<br>',
				                        '</tpl>',    
				                    '" ',
				                '</tpl>',
				            'width="96" height="96" src="',
				            '<tpl if="this.isNotEmpty(iconMenu)">',
				                '{iconMenu}',
				            '</tpl>',
				            '<tpl if="this.isEmpty(iconMenu)">',
				                '/sitools/common/res/images/icons/menu/collections.png',
				            '</tpl>',
				            '" />',
				            '<span>{name}</span>',
				        '</div>',
				    '</tpl>',
				'</div>',
				'<img class="icon-sitools" src="/sitools/common/res/images/icons/menu/logoSitools.png" />',
			'</div>',
        {
            compiled : true,
            isNotEmpty : function (children) {
                return !Ext.isEmpty(children);
            }, 
            isEmpty : function (children) {
                return Ext.isEmpty(children);
            },
            round : function (xcount) { // arrondi à l'entier supérieur
                return Math.round(xcount);
                
            }
        });
	    this.tpl = tpl;
	    this.height = Ext.getBody().getSize().height - 100;
	    
	    this.listeners = {
            dblclick : function (t, ind, node, e) {
                var nodeName = node.id;
                var title = node.childNodes[1].textContent;
                this.openSubCategory(t, title, e, nodeName);
            },
            click : function (t, ind, node, e) {
				var rec = dataViewMenu.getStore().getAt(ind);
				seeAlsoMenu.getStore().loadData(rec.data);
            },
            
			afterrender : function (view) {
			    Ext.Ajax.request({
			        url : 'res/json/menu.json',
			        method : 'GET',
			        success: function (response) {
			            view.allData = Ext.decode(response.responseText);
			            
			            mainPanel.toolbars[0].addText('Path : / Menu');
			            mainPanel.toolbars[0].addSeparator();
			            
			            view.openSubCategory(view, 'root');
			            
			        },
			        failure: function () {
			            Ext.Msg.alert('Error', 'Cannot load nodes from menu.json');
			        },
			        scope : view
			    });
			}
		};
	    
	    this.store = new Ext.data.JsonStore({
			root : '',
			restful : true,
			remoteSort : false,
			fields : [
				{name: 'name', mapping : 'text'},
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
	    
	    sitools.admin.menu.dataView.superclass.initComponent.call(this);
    },
    
    /**
     * Display children of a node
     * 
     * @param t the parent element
     * @param title the title
     * @param e the event
     * @param nodeName the nodeId
     */
    openSubCategory : function (t, title, e, nodeName) {
		
		var isAddable = this.populateStore(t, title);
		
		// On ajoute le noeud sur lequel on vient de cliquer en tant que bouton de la toolbar
		if (isAddable) {
			var button = {
				xtype : 'button',
				overCls : 'nodes-hover',
				labelStyle: 'font-weight:bold !important;',
                text : title,
				id : title + 'Id',
				nodeName : nodeName,
				scope : t,
				handler : function (btn, e) {
					this.populateStore(this, btn.getText());
					
					this.deleteItemsFromToolbar(mainPanel.toolbars[0], btn.getText());
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
			
			this.allButtons.push(button);
			
			mainPanel.toolbars[0].addButton(button);
			mainPanel.toolbars[0].add(['-']);
			mainPanel.doLayout();
			
		}
		else {
			this.treeAction(title, nodeName);
		}
    },
    
    /**
     * Delete the item from the top toolbar
     * 
     * @param toolbar
     * @param title the title of the item to delete
     */
    deleteItemsFromToolbar : function (toolbar, title) {
		var saveToolbar = [];
		var textRoot = toolbar.items.items[0].text;
		
		toolbar.removeAll();
		
		toolbar.addText(textRoot);
		toolbar.addSeparator();
		
		for (var i = 0; i < this.allButtons.length; i++) {
			toolbar.addButton(this.allButtons[i]);
			toolbar.addSeparator();
			saveToolbar[i] = this.allButtons[i];
			
			if (this.allButtons[i].text == title) {
				break;
			}
		}
		this.allButtons = saveToolbar;
		
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
		var children = t.getChildrenByParentNode(t.allData, title);
		if (!Ext.isEmpty(children)) {
			t.store.removeAll();

			//Internationalisation des noeuds.
			Ext.each(children, function (child) {
				child.text = i18n.get('label.' + child.nodeName);
				Ext.each(child.children, function (child) {
					child.textChildren = i18n.get('label.' + child.nodeName);
				});
			});
			t.store.loadData(children);
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
                idItem : selectedNode.get('id')
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