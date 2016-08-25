/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.define("sitools.admin.gui",{
    singleton : true
});

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
        sitools.public.utils.Logout.logout();
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
            }]
        });

        winHelp.show();
        winHelp.doLayout(false, true);
        helpPanel.setHeight(winHelp.getHeight() - 65);
    } else {
        winHelp.show();
    }

}



var clientAdmin = {
	initGui : function (callback) {
	    
	    var menuLogout = {
	        xtype : 'button',
	        tooltip : i18n.get('label.logout'),
			text : i18n.get('label.logout'),
	        itemId : 'menu_logout',
	        icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/logout.png',
			cls : 'x-custom-button-color',
	        handler : function () {
				sitools.public.utils.LoginUtils.logout();
	        }
	    };
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
				text : 'Quick Start',
	            icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/quick_start.png',
                style : 'padding:2px',
                cls : 'x-custom-button-color',
                handler : function () {
                    mainPanel.removeAll();
                    
                    var quickStartPanel = Ext.create('sitools.admin.quickstart.Qs', {
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
				text : 'Version',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/version.png',
                style : 'padding:2px',
                cls : 'x-custom-button-color',
                handler : function () {
					Ext.create('sitools.public.version.Version').show();
                }
            },
            {
                tooltip : 'Portal',
				text : 'Portal',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/logo_fav_icone.png',
                style : 'padding:2px',
                cls : 'x-custom-button-color',
                handler : function () {
					window.open(loadUrl.get('APP_URL')+ loadUrl.get("APP_CLIENT_PORTAL_URL") + "/");
                }
            },
		    "|", menuLogout
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
	        autoScroll : true,
	        width : 300,
	        layout : 'fit',
	        border : false,
            bodyBorder : false,
            header : {
                cls : 'x-toolbar-shadow'
            },
	        defaults : {
	            padding : 10
	        }
	    });
	
        var pan_config = Ext.create("sitools.admin.menu.dataView");
	
        var mainPanelItems = [];
        
        if (Ext.util.Cookies.get('showQuickStart') == "true") {
            var quickStartPanel = Ext.create('sitools.admin.quickstart.Qs', {
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
                cls : 'x-toolbar-shadow'
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
	        items : mainPanelItems,
	        region : 'center'
	    });
	
	    // Setting the viewport
	    viewport = Ext.create('Ext.container.Viewport', {
	        layout : 'border',
	        items : [ menuPanel, treePanel, mainPanel],
	        listeners : {
				scope : this, 
				resize : function () {
					if (dataViewWin) {
						dataViewWin.setSize({
							height : Ext.getBody().getSize().height - 87, 
							width : Ext.getBody().getSize().width - 5
						});
						dataViewWin.setPosition(0, 82);
					}
				}
	        }
	    });

		var menu = Ext.create("sitools.admin.menu.TreeMenu", 'res/json/menu.json');
		treePanel.add(menu);

		if (Ext.isFunction(callback)) {
            callback.call(this);
        }
	}
};


/**
 * Init application Gui and logic
 */
function initAppli(callback) {
    if (Ext.isEmpty(Ext.util.Cookies.get('showQuickStart'))) {
        Ext.util.Cookies.set('showQuickStart', true);
    }
    
	sql2ext.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') +  "/conf/sql2ext.properties");
	clientAdmin.initGui(callback);
}

