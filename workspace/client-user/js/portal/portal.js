/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*
 * global Ext, sitools, i18n, locale, utils_logout, window,
 * userPreferences:true, userStorage, DEFAULT_PREFERENCES_FOLDER, ID, portal,
 * userLogin, loadUrl, showVersion
 */

sitools.Portal = function (projectsList, languages, preferences) {
    /***************************************************************************
     * Creation de la barre d'outil
     */
    var user;
    var menuLoginLogout;
    if (Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
        user = i18n.get('label.guest');
        menuLoginLogout = {
            xtype : 'tbbutton',
            text : i18n.get('label.connection'),
            itemId : 'menu_login',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/login.png',
            scope : this,
            handler : function () {
                sitools.userProfile.LoginUtils.connect({
                    closable : true,
                    url : loadUrl.get('APP_URL') + '/login',
                    register : loadUrl.get('APP_URL') + '/inscriptions/user',
                    reset : loadUrl.get('APP_URL') + '/resetPassword',
                    handler : function () {
                        portal.initAppliPortal({
                            siteMapRes : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL')
                        });
                    }
                });
            }

        };
    } else {
        user = Ext.util.Cookies.get('userLogin');
        menuLoginLogout = {
            xtype : 'tbbutton',
            text : i18n.get('label.logout'),
            itemId : 'menu_logout',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/logout.png',
            scope : this,
            handler : sitools.userProfile.LoginUtils.logout
        };

    }
    var versionButton = {
        xtype : 'tbbutton',
        text : i18n.get('label.version'),
        itemId : 'menu_version',
        icon : loadUrl.get('APP_URL') + '/common/res/images/icons/version.png',
        handler : function () {
            showIngcsVersion();
        }

    };
    var menuLangues = new Ext.menu.Menu({
        plain : true
    });
    Ext.each(languages, function (language) {
        menuLangues
                .add({
                    text : language.displayName,
                    scope : this,
                    handler : function () {
                        var callback = function () {
                            Ext.util.Cookies.set('language', language.localName);
                            window.location.reload();
                        };
                        var date = new Date();
                        Ext.util.Cookies.set('language', language.localName, date.add(Date.MINUTE, 20));
                        userPreferences = {};
                        userPreferences.language = language.localName;
                        if (!Ext.isEmpty(userLogin)) {
                            userStorage.set(loadUrl.get('APP_PORTAL_URL'), "/" + DEFAULT_PREFERENCES_FOLDER + loadUrl.get('APP_PORTAL_URL'), userPreferences,
                                    callback);
                            // userStorage.set("portal", "/" +
                            // DEFAULT_PREFERENCES_FOLDER + "/portal",
                            // userPreferences, callback);
                        } else {
                            window.location.reload();
                        }

                    },
                    icon : language.image
                });
    }, this);

    var editProfileButton;
    if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
        editProfileButton = {
            xtype : 'tbbutton',
            text : i18n.get('label.editProfile'),
            itemId : 'menu_editProfile',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_userman.png',
            identifier : user,
            edit : loadUrl.get('APP_URL') + '/editProfile/' + user,
            scope : this,
            handler : function (button, e) {
                var callback = Ext.createDelegate(this.onEditProfile, this, [ user, button.edit ]);
                sitools.userProfile.LoginUtils.editProfile(callback);
            }

        };
    } else {
        editProfileButton = {
            xtype : 'tbbutton',
            text : i18n.get('label.editProfile'),
            itemId : 'menu_editProfile',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_userman.png',
            disabled: true
        };
    }

    var toolbar = {
        xtype : 'toolbar',
        id : 'toolbar',
        items : [ /*
                     * { xtype : 'label', html : '<img src=' +
                     * loadUrl.get('APP_URL') + '/common/res/images/cnes.png
                     * width=92 height=28>' }, { xtype : 'label', html : '<img
                     * src=' + loadUrl.get('APP_URL') +
                     * '/common/res/images/logo_01_petiteTaille.png width=92
                     * height=28>' },
                     */'->', {
            xtype : 'label',
            margins : {
                top : 0,
                right : 10,
                bottom : 0,
                left : 10
            },
            text : i18n.get('label.welcome') + ' ' + user
        }, '-', versionButton, '-', {
            text : i18n.get('label.langues'),
            menu : menuLangues
        }, '-', editProfileButton, '-', menuLoginLogout

        /*
         * , {xtype : 'button', text : i18n.get('label.connection'), handler :
         * this.connect}
         */
        ]
    };

    var menuPanel = new Ext.Panel({
        id : 'north',
        region : 'north',
        layout : 'fit',
        height : 28,
        items : [ toolbar ]
    });

    /***************************************************************************
     * Creation du menu d'affichage des portlets
     */
    /*
     * var treePanel = new Ext.Panel({ id : 'tree', region : 'west', title :
     * i18n.get('label.components'), split : true, collapsible : true,
     * autoScroll : true, width : 200, layout : 'fit', defaults : { padding : 10 },
     * collapsed : true });
     */

    /***************************************************************************
     * Creation du portlet Liste des projets
     */

//    var data = [];
//    var store = new Ext.data.JsonStore({
//        fields : [ 'id', 'name', 'description', 'image', 'authorized', 'maintenance', 'maintenanceText' ],
//        sortInfo : {
//            field : 'name',
//            direction : 'ASC'
//        }
//    });

var portletCollection = new Ext.util.MixedCollection();
    
    Ext.each(projectsList, function (project) {
        
        var record = new Ext.data.Record({
            id : project.id, 
            name : project.name, 
            description : project.description, 
            image : project.image.url || SITOOLS_DEFAULT_PROJECT_IMAGE_URL, 
            authorized : project.authorized, 
            maintenance : project.maintenance, 
            maintenanceText : project.maintenanceText,
            priority : project.priority,
            categoryProject : project.categoryProject
        });
        
        // creation of the portletObject if it does not already exist
        if (portletCollection.get(project.categoryProject) === undefined) {
            
            var portletObject = {};
            if (project.categoryProject === "" && portletCollection.get("") === undefined) {
                portletObject.category = "Public";
            } else {
                portletObject.category = project.categoryProject;
            }
            
            portletObject.store = this.createStore();
            portletObject.store.add(record);
            portletObject.store.singleSort('priority', 'ASC');
            
            portletObject.dataview = this.createDataview(portletObject.store);
            
            portletObject.portlet = this.createPortlet(portletObject);
            
            portletCollection.add(project.categoryProject, portletObject);
            
        } else { // just adding record to the portletObject store
            var portletObject = portletCollection.get(project.categoryProject);
            if (!Ext.isEmpty(portletObject)) {
                portletObject.store.add(record);
                portletObject.store.singleSort('priority', 'ASC');
            }
        }
    }, this);
    console.dir(portletCollection);

//    var portletProjet = new Ext.ux.Portlet({
//        id : ID.PORTLET.PROJET,
//        title : i18n.get('label.portletProjetTitle'),
//        height : 460,
//        // tbar : tbar,
//        items : [ myDataView ],
//        autoScroll : true
//    });

    /***************************************************************************
     * Creation du portlet d'affichage des flux de l'archive
     */

    /*
     * var panelFluxPortal = { xtype :
     * 'sitools.component.users.portal.feedsReaderPortal' };
     *  // panelFlux.loadFeed( '/sitools/client-user/tmp/feed-proxy.xml');
     * 
     * var portletFluxPortal = new Ext.ux.Portlet({ layout : 'fit', id :
     * ID.PORTLET.FEEDS, title : i18n.get('title.portlelFeedsPortal'), height :
     * 460, items : [ panelFluxPortal ] });
     */

    // Contenu HTML du portlet
//    var htmlAbout = '<div class="containerAbout"><div class="titleAbout">Groundwater</div><b>Pesticides in GroundWater</b> This application is about finding areas where there are high concentrations of pesticides in the groundwater. The user is able to search for specific pesticides and restrict the output to pesticides found at a certain depth interval and/or from certain geology (lithology or lithostratigraphy). \n\
//    <br/><br/><div class="titleAbout">Shakemaps</div><b>ShakeMaps</b> This application is about viewing and downloading shaking intensity distribution maps for important earthquakes. The application produces automatically a new shakemap dataset a few minutes after the earthquake. Data is available to the user for viewing over a mapping interface and for downloading and re-using through the use of WMS and WFS services.\n\
//    <br/><br/><div class="titleAbout">GeoHazards</div><b>Landslides Susceptibility Maps</b> This application provides automatically produced susceptibility maps of triggering landslides due to higher precipitation levels. The endangered zones will be predicted using the combination of the landslide susceptibility model, the precipitation forecast and the landslide triggering threshold values. \n\
//    <br/>Data is available to the user for viewing over a mapping interface and for downloading and re-using through the use of WMS and WFS services.\n\
//    <br/><br/><div class="titleAbout">GeoPublication</div><b>An integrated Platform/Infrastructure for data dissemination</b> The Inspire Compliant Data publication is a data-provider service that allows to publish your own geo-data and to create maps. It allows you to simplify the re-use of data, without worrying about all technical, legal and Information Technology (IT) issues.</div>'

    var portletAbout = new Ext.ux.Portlet({
        layout : 'fit',
        height : 460,
        autoLoad : 'res/html/' + locale.getLocale() + '/projectsDescription.html'
    }); 
    
//   var portletAbout = new Ext.ux.Portlet({
//        layout : 'fit',
//        id : ID.PORTLET.FEEDS,
//        /* title : i18n.get('label.portletAbout'), */
//        height : 460,
//        // items : [ /*panelFluxPortal*/ ]
//        html : htmlAbout
//    });

    /***************************************************************************
     * Creation du portlet Open Search
     */
    /*
     * var osPanel = new sitools.component.users.portal.portalOpensearch({
     * dataUrl : loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL'), suggest :
     * false, pagging : false
     * 
     * });
     * 
     * 
     * var portletRecherche = new Ext.ux.Portlet({ collapsed : true,
     * bodyCssClass : 'portletRecherche', id : ID.PORTLET.RECHERCHE, title :
     * i18n.get('label.portletRechercheTitle'), items : [ osPanel ], layout:
     * "fit", height : 400 });
     */

    /***************************************************************************
     * Creation des autres composants du tabPanel
     */
    /*
     * var helpPanel = new Ext.ux.ManagedIFrame.Panel({ id : 'helpPanelId',
     * title : i18n.get('label.helpTitle'), // split: true, // collapsible:
     * true, autoScroll : true, // layout: 'fit', defaults : { padding : 10 },
     * defaultSrc : "res/html/" + locale.getLocale() + "/help.html" });
     */
    var linkPanel = new Ext.ux.ManagedIFrame.Panel({
        id : 'link',
        title : i18n.get('label.linkTitle'),
        // split: true,
        // collapsible: true,
        autoScroll : true,
        // layout: 'fit',
        defaults : {
            padding : 10
        },
        defaultSrc : "res/html/" + locale.getLocale() + "/link.html"
    });

    var contactPanel = new Ext.ux.ManagedIFrame.Panel({
        id : 'help',
        title : i18n.get('label.contactTitle'),
        // split: true,
        // collapsible: true,
        autoScroll : true,
        // layout: 'fit',
        defaults : {
            padding : 10
        },
        defaultSrc : "res/html/" + locale.getLocale() + "/contact.html"
    });

    /***************************************************************************
     * Creation tabPanel Center qui contient le portal
     */

    var onlyPortletTab = [];
    portletCollection.each(function (item, index, length) {
        onlyPortletTab.push(item.portlet);
    }, this);
    
    var mainPanel = new Ext.TabPanel({
        baseCls : 'portalMainPanel',
        region : 'center',
        activeTab : 0,
        // title: i18n.get('label.portalTitle'),
        // layout:'fit',
        items : [ {
            xtype : 'panel',
            baseCls : 'portalMainPanel',
            autoScroll : true,
            title : i18n.get('label.portalTitle'),
            items : [ {
                region : 'north',
                xtype : 'iframepanel',
                // title : i18n.get('label.freeText'),
                autoScroll : true,
                defaults : {
                    padding : 10
                },
                defaultSrc : "res/html/" + locale.getLocale() + "/freeText.html",
                height : 200
            }, {
                region : 'center',
                baseCls : 'portalMainPanel',
                xtype : 'portal',
                id : 'portalId',
                margins : '35 5 5 0',
                // layout : 'fit',
                defaults : {
                    style : 'padding:10px 0 10px 10px'
                },
                items : [ {
                    columnWidth : 0.50,
                    style : 'padding:10px 0 10px 10px;',
                    // baseCls : 'portalMainPanel',
//                    items : [ portletProjet ]
                    items : onlyPortletTab
                }, {
                    columnWidth : 0.50,
                    style : 'padding:10px',
                    // baseCls : 'portalMainPanel',
                    items : [ portletAbout /*
                                             * portletFluxPortal,
                                             * portletRecherche
                                             */]
                } ]
            }, {
                region : 'south',
                xtype : 'iframepanel',
                // style : 'padding-left : 10px; padding-right : 10px;',
                // style : 'margin-top : 10px;',
                // title : i18n.get('label.freeText'),
                autoScroll : true,
                // margins : '35 5 5 0',
                defaults : {
                    padding : 10
                },
                defaultSrc : "res/html/" + locale.getLocale() + "/footer.html",
                height : 160
            } ]
        }, contactPanel, linkPanel /* , helpPanel */]

    /*
     * Uncomment this block to test handling of the drop event. You could use
     * this to save portlet position state for example. The event arg e is the
     * custom event defined in Ext.ux.Portal.DropZone.
     */
    });

    /***************************************************************************
     * Creation du viewport
     */
    sitools.Portal.superclass.constructor.call(this, Ext.apply({
        layout : 'border',
        items : [ menuPanel, /* treePanel, */mainPanel ]
    }));

    var treeNav = new Ext.tree.TreePanel({
        id : 'panelNav',
        useArrows : true,
        autoScroll : true,
        animate : true,
        enableDD : false,
        containerScroll : true,
        rootVisible : false,
        width : 200,
        root : new Ext.tree.AsyncTreeNode({
            expanded : true,
            children : [ {
                id : ID.PORTALTREENAV.PROJET,
                panelId : ID.PORTLET.PROJET,
                icon : 'res/images/icons/portlet.png',
                text : i18n.get('label.portletProjetTitle'),
                leaf : true,
                checked : true,
                listeners : {
                    checkchange : function (node) {
                        if (!node.attributes.checked) {
                            Ext.get(node.attributes.panelId).hide();
                            // Pour que le panel n'ait plus de place reservee
                            // dans le portal
                            Ext.get(node.attributes.panelId).addClass('x-hide-display');
                        } else {
                            Ext.get(node.attributes.panelId).show();
                            Ext.get(node.attributes.panelId).removeClass('x-hide-display');
                        }
                    }
                }
            }, {
                id : ID.PORTALTREENAV.RECHERCHE,
                icon : 'res/images/icons/portlet.png',
                panelId : ID.PORTLET.RECHERCHE,
                text : i18n.get('label.portletRechercheTitle'),
                leaf : true,
                checked : true,
                listeners : {
                    checkchange : function (node) {
                        if (!node.attributes.checked) {
                            Ext.get(node.attributes.panelId).hide();
                            // Pour que le panel n'ait plus de place reservee
                            // dans le portal
                            Ext.get(node.attributes.panelId).addClass('x-hide-display');
                        } else {
                            Ext.get(node.attributes.panelId).show();
                            Ext.get(node.attributes.panelId).removeClass('x-hide-display');
                        }
                    }
                }
            }, {
                id : ID.PORTALTREENAV.FEEDS,
                icon : 'res/images/icons/portlet.png',
                panelId : ID.PORTLET.FEEDS,
                text : i18n.get('label.portletFeedsTitle'),
                leaf : true,
                checked : true,
                listeners : {
                    checkchange : function (node) {
                        if (!node.attributes.checked) {
                            Ext.get(node.attributes.panelId).hide();
                            // Pour que le panel n'ait plus de place reservee
                            // dans le portal
                            Ext.get(node.attributes.panelId).addClass('x-hide-display');
                        } else {
                            Ext.get(node.attributes.panelId).show();
                            Ext.get(node.attributes.panelId).removeClass('x-hide-display');
                        }
                    }
                }
            } ]
        }),
        listeners : {
            'checkchange' : function (node, checked) {
                if (checked) {
                    node.getUI().addClass('complete');
                } else {
                    node.getUI().removeClass('complete');
                }
            }
        }
    });
    // treePanel.add(treeNav);
    // treePanel.doLayout();
    // portletFlux.doLayout();

};

Ext.extend(sitools.Portal, Ext.Viewport, {
    onRender : function () {
        sitools.Portal.superclass.onRender.apply(this, arguments);
        // this.
        // this.doLayout();
    },

    onEditProfile : function (user, url) {
        var win = new Ext.Window({
            items : [],
            modal : true,
            width : 400,
            height : 405,
            resizable : false
        });

        win.show();
        var edit = new sitools.userProfile.editProfile({
            closable : true,
            identifier : user,
            url : url,
            height : win.body.getHeight()
        });
        win.add(edit);
        win.doLayout();
    },
    
    createStore : function () {
        return new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'description', 'image', 'authorized', 'maintenance', 'maintenanceText', 'priority', 'categoryProject' ],
            sortInfo : {
                field : 'priority',
                direction : 'ASC'
            }
        });
    },
    
    createDataview : function (store) {
        return new Ext.DataView({
            store : store,
            tpl : new Ext.XTemplate('<ul>', '<tpl for=".">', 
                    '<li id="{id}" ', 
                    '<tpl if="authorized == true">',
                        'class="project',
                        '<tpl if="maintenance">',
                            ' sitools-maintenance-portal',
                        '</tpl>',
                        '"', 
                    '</tpl>', 
                    '<tpl if="authorized == false">',
                        'class="project projectUnauthorized"',
                    '</tpl>', 
                    '>', 
                    '<img width="80" height="80" src="{image}" />', '<strong>{name}</strong>',
                    '<span>{description} </span>', '</li>', '</tpl>', '</ul>', 
                    {
                    compiled : true, 
                    disableFormats : true, 
                    isAuthorized : function (authorized) {
                        return authorized === true;
                    }
                }),
            id : 'projectDataView',
            itemSelector : 'li.project',
            overClass : 'project-hover',
            singleSelect : true,
            multiSelect : false,
            autoScroll : true,
            listeners : {
                scope : this,
                click : function (dataView, index, node, e) {
                    // get the projectId
                    var data = dataView.getRecord(node).data;
                    var projectName = data.name;
                    var authorized = data.authorized;
                    var maintenance = data.maintenance;
                    var maintenanceText = data.maintenanceText;
                    if (authorized) {
                        if (!maintenance) {
                            window.open(projectName + "/project-index.html");
                        }
                        else {
                            var alertWindow = new Ext.Window({
                                title : i18n.get('label.maintenance'),
                                width : 600, 
                                height : 400, 
                                autoScroll : true, 
                                items : [{
                                    xtype : 'panel', 
                                    layout : 'fit', 
                                    autoScroll : true, 
                                    html : maintenanceText, 
                                    padding : "5"
                                }], 
                                modal : true
                            });
                            alertWindow.show();
                        }
                    } else {
                        sitools.userProfile.LoginUtils.connect({
                            closable : true,
                            url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                            register : loadUrl.get('APP_URL') + '/inscriptions/user',
                            reset : loadUrl.get('APP_URL') + '/lostPassword',
                            unblacklist : loadUrl.get('APP_URL') + '/unblacklist',
                            handler : function () {
                                if (!maintenance) {
                                    window.open(projectName + "/project-index.html");
                                }
                            }
                        });
                    }
                }
            }
        });
    },
    
    createPortlet : function (portletObject) {
        return new Ext.ux.Portlet({
            title : portletObject.category,
//            height : 430,
            boxMaxHeight : 430,
            autoHeight : true,
            items : [ portletObject.dataview ],
            autoScroll : true,
            resizable : false,
            listeners : {
                scope : this,
                afterrender : function (portlet) {
                    if (portlet.getHeight() > portlet.boxMaxHeight) {
                        portlet.autoHeight = false;
                        portlet.setHeight(portlet.boxMaxHeight);
                        portlet.doLayout();
                    }
                }
            }
        });
    }
});

Ext.reg('sitools.Portal', sitools.Portal);
