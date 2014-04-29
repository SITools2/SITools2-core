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
/*global Ext, sitools, initGui, initTreeMenu, utils_logout, i18n, ID, DEFAULT_HELP_WIDTH, DEFAULT_HELP_HEIGHT, helpPanel, LOCALE, loadUrl, showVersion, SHOW_HELP:true, includeJs */
/* list of global objects*/
/*
 * @include "menu/TreeMenu.js"
 */
var user = null;
var viewport = null;
var treePanel = null;
var dataViewMenu = null;
var seeAlsoMenu = null;
var dataViewWin = null;
var mainPanel = null;
var seeAlsoPanel = null;
var helpUrl = null;
var componentsPanel = null;
SHOW_HELP = true;

function onMenuSelect(but) {
    if (Ext.isEmpty(but.getItemId())) {
        return;
    }
    if (but.getItemId() == 'menu_logout') {
        utils_logout();
    } else {
        Ext.Msg.alert('Click on Menu: ', but.getItemId());
    }
}

function showHelp(helpUrl) {
    if (Ext.isEmpty(helpUrl)) {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noHelpUrl'));
        return;
    }
    var winHelp = Ext.getCmp(ID.WIN.HELP);
    if (!winHelp) {
        
        var helpPanel = Ext.create('Ext.container.Container', {
            id : ID.WIN.HELP,
            layout : 'fit',
            defaultSrc : helpUrl,
            items : [{
                xtype : 'component',
                autoEl: {
                    tag: 'iframe',
                    border : false,
                    src: helpUrl
                }
            }],
            defaults : {
                padding : 10
            }
        });

        winHelp = Ext.create('Ext.window.Window', {
            title : i18n.get('label.help'),
            width : DEFAULT_HELP_WIDTH,
//            autoScroll : true,
            modal : true,
            id : 'winHelpId',
            height : DEFAULT_HELP_HEIGHT,
            layout : 'fit',
            items : [ helpPanel ],
            buttons : [{
                text : 'close',
                handler : function () {
                    this.ownerCt.ownerCt.close();
                }
            }],
            listeners : {
                resize : function (win, width, height) {
                    //this.findById(ID.WIN.HELP).setHeight(height - 65);
                }
            }
        });

        winHelp.show();
        winHelp.doLayout(false, true);
        helpPanel.setHeight(winHelp.getHeight() - 65);
    } else {
        winHelp.show();
    }

}



var clientAdmin = {
    initGuiServices : function (callback) {
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_GUI_SERVICES_URL'),
            params : {
                sort : "priority",
                dir : "DESC"
            }, 
            method : "GET",
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectName'));
                    return false;
                } else {
                    var data = json.data;                    
                    Ext.each(data, function (guiservice) {
                        includeJsForceOrder(guiservice.dependencies.js, 0);
                    });
                    
                }
            },
            callback : function () {
                this.initDatasetViews(callback);
            }
        }); 
    }, 
	initDatasetViews : function (callback) {
		Ext.Ajax.request({
	        url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_VIEWS_URL'),
	        params : {
	            sort : "priority",
	            dir : "DESC"
	        }, 
	        method : "GET",
	        scope : this,
	        success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectName'));
                    return false;
                } else {
                    var data = json.data;                    
                    Ext.each(data, function (datasetViewComponent) {
                    	includeJsForceOrder(datasetViewComponent.dependencies.js, 0);
                    });
                    
                }
	        },
	        callback : function () {
	        	this.initProjectsModules(callback);
	        }
	    }); 
	}, 
	initProjectsModules : function (callback) {
		Ext.Ajax.request({
	        url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_MODULES_URL'),
	        params : {
	            sort : "priority",
	            dir : "DESC"
	        }, 
	        method : "GET",
	        scope : this,
	        success : function (ret) {
	        	
	        	// utils.js contains commonTreeUtils from env.js
//	        	includeJs("/sitools/client-user/js/utils.js");
	        	
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectName'));
                    return false;
                } else {
                    var data = json.data;                    
                    Ext.each(data, function (projectModuleComponent) {
                    	if (!Ext.isEmpty(projectModuleComponent.dependencies.js)) {
                    		includeJsForceOrder(projectModuleComponent.dependencies.js, 0);
                    	}
                    });
                }
	        },
	        callback : function () {
				callback.call(this);
	        }
	    }); 
	}, 
	initGui : function (callback) {
	    
	    var menuLogout = {
	        xtype : 'button',
	        tooltip : i18n.get('label.logout'),
	        itemId : 'menu_logout',
	        icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/logout.png',
	        cls : 'x-custom-button-color',
	        handler : function () {
	            sitools.userProfile.LoginUtils.logout();
	        }
	    };
	    // var menuList = { xtype: 'tbbutton', text: 'Menu 2', itemId:'menu_list',
	    // handler: onMenuShow,
	    // menu: [
	    // {text: 'Item 1', itemId:'menu2_item1', handler: onMenuSelect},
	    // {text: 'Item 2', itemId:'menu2_item2', handler: onMenuSelect},
	    // '-',
	    // {text: 'Item 3', itemId:'menu2_item3', handler: onMenuSelect}
	    // ] };
	    // var menuHelp = { xtype: 'tbbutton', text: 'Help', disabled: true };
	    var toolbar = {
	        xtype : 'toolbar',
	        id : ID.CMP.TOOLBAR,
	        cls : 'admin-bg',
	        items : [ {
	            xtype : 'label',
	            html : '<img src=res/images/cnes.png width=92 height=28>'
	        }, {
	            xtype : 'label',
	            style : "text-shadow: 1px 1px #F2F8FF;font-size: 12px; color: #526572; font-weight: bold; padding-left : 15px",
	            text : i18n.get('label.title')
	        }, '->', {
	            xtype : 'label',
	            style : "text-shadow: 1px 1px #F2F8FF; font-size: 12px; color: #526572;",
	            html : i18n.get('label.welcome') + ' <b>' + Ext.util.Cookies.get('userLogin') + '</b>'
	        }, '-', {
	            id : 'quickStart',
	            tooltip : 'Quick Start',
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/quick_start.png',
                style : 'padding:2px',
                cls : 'x-custom-button-color',
                handler : function () {
                    mainPanel.removeAll();
                    
                    var quickStartPanel = Ext.create('sitools.admin.quickStart.qs', {
                        id : ID.PANEL.QUICKSTART,
                        width : "100%",
                        flex : 1
                    });
                    
                    var containerPanel = Ext.create('Ext.panel.Panel', {
                        name : 'containerPanel',
                        width : "100%",
                        layout : 'fit',
                        bodyCls : 'admin-bg',
                        border : false,
                        bodyBorder : false,
                        flex : 1
                    }); 
                    mainPanel.add(containerPanel);
                    mainPanel.add(quickStartPanel);
                    mainPanel.doLayout();
                    
                }
	        }, {
//                text : 'Version',
                id : 'versionButtonId',
                tooltip : 'Version',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/version.png',
                style : 'padding:2px',
                cls : 'x-custom-button-color',
                handler : function () {
                    showVersion();
                }
            }, {
//		        text: 'Show Help',
		        tooltip: 'Show Help',
		        enableToggle: true,
                style : 'padding:2px',
                cls : 'x-custom-button-color',
		        toggleHandler: function (item, checked) {
					var helpPanel = Ext.getCmp(ID.PANEL.HELP);
					SHOW_HELP = checked;
					if (helpPanel == undefined) {
					    return;
					}
				    if (!checked) {
				        helpPanel.toggleCollapse(checked);
				    }
				    else {
				        helpPanel.toggleCollapse(checked);
				    }
//				    mainPanel.doLayout();
			    },
		        pressed: true,
		        icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/help.png'
		    }, /*{
		        tooltip : 'Advanced Mode',
		        id : 'switchModeId',
		        icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/maintenance.png',
		        enableToggle: true,
		        pressed : true,
                style : 'padding:2px',
                cls : 'x-custom-button-color',
		        toggleHandler : function (item, checked) {
		            var main = Ext.getCmp(ID.PANEL.MAIN);
		            var tree = Ext.getCmp(ID.PANEL.TREE);
		            
		            if (checked) {
		                tree.show();
		                main.getTopToolbar().hide();
		                main.doLayout();
		                dataViewWin.hide();
		            }
		            else {
		                tree.hide();
		                if (Ext.isEmpty(dataViewWin)) {
		                    
		                    seeAlsoMenu = new sitools.admin.menu.seeAlso();
		                    seeAlsoPanel = new Ext.Panel({
		                        id : 'seeAlsoId',
		                        flex : 1,
		                        autoScroll : true, 
		                        width : 265,
		                        html : '<div class="title-seeAlso"></div>',
		                        cls : 'background-seeAlso',
		                        height : Ext.getBody().getSize().height - 110,
		                        items : [seeAlsoMenu]
		                    });
		                    
		                    seeAlsoMenu.getStore().loadData({
		                    	links : []	
		                    });
		                    
		                    dataViewMenu = new sitools.admin.menu.dataView();
		                    
		                    var dataViewPanel = new Ext.Panel({
		                        id : 'panDataview',
		                        flex : 2,
		                        items : [dataViewMenu], 
		                        listeners : {
									resize : function (panel) {
										var size = panel.body.getSize();
										dataViewMenu.setSize({
											height : size.height - 5, 
											width : size.width - 5
										});
										seeAlsoPanel.setSize(panel.body.getSize());
									}
		                        }
		                    });
		                    
							dataViewWin = new Ext.Window({
								border : false, 
								id : ID.PANEL.DATAVIEW,
								layout: 'hbox',
								resizable : false, 
								layoutConfig: {
								    align : 'stretch',
								    pack  : 'start'
								},
								closable : false,
								draggable : false, 
								items : [seeAlsoPanel, dataViewPanel],
								height  : Ext.getBody().getSize().height - 82, 
								width : Ext.getBody().getSize().width,
								y : 82,
								allButtons : [], 
								tbar : {
					                cls : 'root-toolbar',
					                id : 'idToolbarView'
					            }
							});
		                }
		                dataViewWin.show();
		                main.getTopToolbar().show();
		                var dataViewEl = dataViewWin.getEl();
		                dataViewEl.fadeIn({
						    opacity: 0.95, //can be any value between 0 and 1 (e.g. .5)
						    easing: 'easeOut',
						    duration: 0.5
						});
		            }
		            viewport.doLayout();
		        }
		    }*/,
		    "-", menuLogout
	
	        // menuList,
	        // '-',
	        // menuHelp
	        ]
	    };
		
	    var menuPanel = Ext.create("Ext.Panel", {
	        id : ID.PANEL.MENU,
	        region : 'north',
	        layout : 'fit',
	        height : 36,
	        border : false,
	        bodyBorder : false,
	        items : [ toolbar ]
	    });
	
	    treePanel = Ext.create("Ext.Panel", {
	        id : ID.PANEL.TREE,
	        region : 'west',
	        title : i18n.get('label.menu'),
//	        split : true,
	        autoScroll : true,
	        width : 295,
	        layout : 'fit',
	        border : false,
            bodyBorder : false,
            header : {
                cls : 'x-toolbar-shadow',
            },
	        defaults : {
	            padding : 10
	        }
	    });
	
        var pan_config = Ext.create("sitools.admin.menu.dataView");
	
        var mainPanelItems = [];
        
        if (Ext.util.Cookies.get('showQuickStart') == "true") {
            var quickStartPanel = Ext.create('sitools.admin.quickStart.qs', {
                id : ID.PANEL.QUICKSTART,
                border : false,
                bodyBorder : false,
                flex : 1
            });
            
            var containerPanel = Ext.create('Ext.container.Container', {
                name : 'containerPanel',
//                width : "100%",
                layout : 'fit',
                bodyCls : 'admin-bg',
                autoScroll : false,
                border : false,
                flex : 1,
                items : [{
                    xtype : 'component',
                    autoEl: {
                        tag: 'iframe',
                        border : false,
                        src: 'res/html/' + LOCALE + '/welcome.html'
                    }
                }]
            }); 
            mainPanelItems.push(containerPanel);
            mainPanelItems.push(quickStartPanel);
        } else {
            var welcomePanel = Ext.create('Ext.container.Container', {
                xtype : 'panel', 
                layout : 'fit',
                flex : 1,
                items : [{
                    xtype : 'component',
                    autoEl: {
                        tag: 'iframe',
                        border : false,
                        src: 'res/html/' + LOCALE + '/welcome.html'
                    }
                }]
            }); 
            mainPanelItems.push(welcomePanel);
        }
        
	    mainPanel = Ext.create('Ext.panel.Panel', {
	        bodyCls : 'admin-bg',
	        header : {
                cls : 'x-toolbar-shadow',
	        },
	        layout : {
	            type : 'vbox',
	            align : 'stretch',
	            pack  : 'start'
	        },
	        border : false,
            bodyBorder : false,
	        xtype : 'panel',
	        id : ID.PANEL.MAIN,
//	        title : i18n.get('label.main'),
	        items : mainPanelItems,
	        region : 'center'
	    });
	
	    /*
	     * contextMenu = new Ext.Window({ title: FILTER_TITLE, layout: 'hbox',
	     * width: 260, autoHeight: true, shadow: 'drop', closable: false,
	     * //closeAction:'hide', plain: true, //buttonAlign: 'center', items: [ ],
	     * buttons: [ { text: 'OK', handler: function() {contextMenu.hide(),
	     * setFilter(Ext.getCmp('filterValueId').getValue())} }, { text: 'Cancel',
	     * handler: function() {contextMenu.hide()} } ] });
	     */
	    
	    // Setting the viewport
	    viewport = Ext.create('Ext.container.Viewport', {
	        layout : 'border',
	        items : [ menuPanel, treePanel, mainPanel
	        // ,
	        // helpPanel
	        ], 
	        listeners : {
				scope : this, 
				resize : function () {
					if (dataViewWin) {
						dataViewWin.setSize({
							height : Ext.getBody().getSize().height - 87, 
							width : Ext.getBody().getSize().width - 5
						});
						dataViewWin.setPosition(0, 82);
//						dataViewWin.doLayout();
					}
				}
	        }
	    });

		var menu = Ext.create("sitools.admin.menu.TreeMenu", 'res/json/menu.json');
		treePanel.add(menu);

		if (Ext.isFunction(callback)) {
            callback.call(this);
        }
//		treePanel.doLayout();

	    // viewport.doLayout();
	
	    // Checking if user is logged
	}
};


/**
 * Init application Gui and logic
 */
function initAppli(callback) {
    //loadUrl.load('/sitools/client-admin/siteMap', clientAdmin.initGui());
    if (Ext.isEmpty(Ext.util.Cookies.get('showQuickStart'))) {
        Ext.util.Cookies.set('showQuickStart', true);
    }
    
	Ext.Ajax.request({
        url : loadUrl.get('APP_URL') + loadUrl.get('APP_FORMCOMPONENTS_URL'),
        params : {
            sort : "priority",
            dir : "DESC"
        }, 
        method : "GET",
        scope : this,
        success : function (ret) {
            var json = Ext.decode(ret.responseText);
            if (!json.success) {
                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectName'));
                return false;
            } else {
                var data = json.data;                    
                Ext.each(data, function (formComponent) {
                    includeJs(formComponent.fileUrlAdmin);
                    includeJs(formComponent.fileUrlUser);
                });
            }
        },
        callback : function () {
			sql2ext.load(loadUrl.get('APP_URL') + "/conf/sql2ext.properties");
            var cb = function () {
				clientAdmin.initGui(callback);
            };
            clientAdmin.initGuiServices(cb);
        }
    });       
    
}

