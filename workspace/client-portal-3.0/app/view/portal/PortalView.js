/**
 * @class Ext.app.Portal
 * @extends Object
 * A sample portal layout application class.
 */

Ext.define('sitools.clientportal.view.portal.PortalView', {
    extend: 'Ext.container.Viewport',
    
    requires: ['sitools.clientportal.view.portal.PortalPanel',
               'sitools.clientportal.view.portal.PortalColumn',
               'sitools.clientportal.view.portal.GridPortlet',
               'sitools.clientportal.view.portal.Portlet',
               'sitools.clientportal.view.portal.FeedsReaderPortal',
               
               'sitools.public.userProfile.editProfile'],

    layout: {
      type: 'border',
      padding: 10
    },
    border : false,
    bodyBorder : false,
    
    initComponent: function() {
        
        var user;
        var menuLoginLogout;
        
        if (Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
            user = i18n.get('label.guest');
            
            menuLoginLogout = {
                xtype : 'button',
                text : i18n.get('label.connection'),
                name : 'menuLogin',
                cls : 'x-custom-button-color',
                icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/login.png',
                scope : this,
                handler : function () {
                    sitools.public.utils.LoginUtils.connect({
                        closable : true,
                        url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                        register : loadUrl.get('APP_URL') + '/inscriptions/user',
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist'
                    });
                    
                    
                }

            };
        } else {
            user = Ext.util.Cookies.get('userLogin');
            menuLoginLogout = {
                xtype : 'button',
                text : i18n.get('label.logout'),
                itemId : 'menu_logout',
                cls : 'x-custom-button-color',
                icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/logout.png',
                scope : this,
                handler : sitools.public.utils.LoginUtils.logout
            };

        }
        var versionButton = {
            xtype : 'button',
            text : i18n.get('label.version'),
            itemId : 'menu_version',
            icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/version.png',
            cls : 'x-custom-button-color',
            handler : function () {
                Ext.create('sitools.public.version.Version').show();
            }

        };
        
        var menuLangues = Ext.create('Ext.menu.Menu', {
            name : 'menuLangues',
            plain : true,
            border : false,
            bodyBorder : false,
            cls : 'x-custom-button-color'
        });
        
        Ext.each(this.languages, function (language) {
            menuLangues.add({
                text : language.displayName,
                scope : this,
                handler : function () {
                    var callback = function () {
                        Ext.util.Cookies.set('language', language.localName);
                        window.location.reload();
                    };
                    var date = new Date();
                    Ext.util.Cookies.set('language', language.localName, Ext.Date.add(date, Ext.Date.MINUTE, 20));
                    locale.setLocale(language.localName);
                    userPreferences = {};
                    userPreferences.language = language.localName;
                    if (!Ext.isEmpty(userLogin)) {
                        userStorage.set(loadUrl.get('APP_PORTAL_URL'),  "/" + DEFAULT_PREFERENCES_FOLDER + loadUrl.get('APP_PORTAL_URL'), userPreferences, callback);
//                        userStorage.set("portal",  "/" + DEFAULT_PREFERENCES_FOLDER + "/portal", userPreferences, callback);
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
                    xtype : 'button',
                    name : 'editProfileBtn',
                    text : i18n.get('label.editProfile'),
                    itemId : 'menu_editProfile',
                    cls : 'x-custom-button-color',
                    icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/tree_userman.png',
                    identifier : user,
                    edit : loadUrl.get('APP_URL') + '/editProfile/' + user,
                    scope : this
                };
        } else {
            
            editProfileButton = {
                xtype : 'button',
                name : 'editProfileBtn',
                cls : 'x-custom-button-color',
                hidden : true
            };
        }
        
        var toolbar = {
            xtype : 'toolbar',
            id : 'toolbar',
            cls : 'bg-transparent box-shadow',
            region : 'north',
            height : 55,
            border : false,
            bodyBorder : false,
            margin : 10,
            items : [{
                xtype : 'label',
                html : '<img src=' + loadUrl.get('APP_URL') + '/client-public/res/images/cnes.png width=92 height=28>'
            }, {
                xtype : 'label',
                html : '<img src=' + loadUrl.get('APP_URL') + '/client-public/res/images/logo_01_petiteTaille.png width=92 height=28>'
            }, '->', {
                xtype : 'label',
                margins : {
                    top : 0,
                    right : 10,
                    bottom : 0,
                    left : 10
                },
                html : i18n.get('label.welcome') + ' <b>' + user + '</b>'
            }, versionButton, '-', {
                text : i18n.get('label.langues'),
                cls : 'x-custom-button-color',
                menu : menuLangues
            }, '-', editProfileButton, (editProfileButton.hidden) ? null : '-', menuLoginLogout ]
        };
        
        /***************************************************************************
         * Creation du portlet Liste des projets
         */

        var storeProjectPublic = Ext.create('Ext.data.JsonStore', {
            fields : [ 'id', 'name', 'description', 'image', 'authorized', 'maintenance', 'maintenanceText' ],
            sorters : [{
                property : 'name',
                direction : 'ASC'
            }]
        });
        
        var storeProjectPrivate = Ext.create('Ext.data.JsonStore', {
            fields : [ 'id', 'name', 'description', 'image', 'authorized', 'maintenance', 'maintenanceText' ],
            sorters : [{
                property : 'name',
                direction : 'ASC'
            }]
        });

        Ext.each(this.projects, function (project) {
            var record = {
                id : project.id, 
                name : project.name, 
                description : project.description, 
                image : project.image.url || SITOOLS_DEFAULT_PROJECT_IMAGE_URL, 
                authorized : project.authorized,
                maintenance : project.maintenance,
                maintenanceText : project.maintenanceText
            };
            
            if (project.authorized) {
                storeProjectPublic.add(record);
            } else {
                storeProjectPrivate.add(record);
            }
        });
        
        var dataViewProjectPublic = Ext.create('Ext.view.View', {
            name : 'viewProjectPublic',
            store : storeProjectPublic, 
            tpl : new Ext.XTemplate('<ul>', '<tpl for=".">', 
                '<li id="{id}"', 
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
                '', '</li>', '</tpl>', '</ul>', 
                {
                compiled : true, 
                disableFormats : true, 
                isAuthorized : function (authorized) {
                    return authorized === true;
                }
            }),
            cls : 'projectDataView',
            itemSelector : 'li.project',
            overItemCls : 'project-hover',
            mode : 'SINGLE',
            multiSelect : false,
            autoScroll : true,
            listeners : {
                scope : this,
                render : function (view) {
                    view.tip = Ext.create('Ext.tip.ToolTip', {
                        target: view.el,
                        delegate: view.itemSelector,
                        anchor : 'top',
                        dismissDelay: 0,
                        showDelay: 0,
                        renderTo: Ext.getBody(),
                        cls : 'x-custom-button-color',
                        listeners:{
                            beforeshow: function updateTipBody(tip) {
                                var description = view.getRecord(tip.triggerElement).get('description');
                                if (Ext.isEmpty(description)) {
                                    return false;
                                }
                                tip.update(
                                        view.getRecord(tip.triggerElement).get('description')
                                );
                            }
                        }
                    });
                }
            }
        });
        
        var portletProjetPublic = Ext.create('sitools.clientportal.view.portal.Portlet', {
//            id : ID.PORTLET.PROJET,
            title : i18n.get('label.portletProjetPublicTitle'),
            height : 400,
            items : [ dataViewProjectPublic ],
            autoScroll : true
        });

        var portletProjetPrivate;
        
        if (storeProjectPrivate.getCount() > 0) {
            var dataViewProjectPrivate = Ext.create('Ext.view.View', {
                name : 'viewProjectPrivate',
                store : storeProjectPrivate, 
                tpl : new Ext.XTemplate('<ul>', '<tpl for=".">', 
                    '<li id="{id}" ', 
                        'class="project projectUnauthorized"',
                    '>', 
                    '<img width="80" height="80" src="{image}" />', '<strong>{name}</strong>',
                    '<span>{description} </span>', '</li>', '</tpl>', '</ul>'
                ),
                cls : 'projectDataView',
                itemSelector : 'li.project',
                overItemCls : 'project-hover',
                mode : 'SINGLE',
                multiSelect : false,
                autoScroll : true
            });
            
            portletProjetPrivate = Ext.create('sitools.clientportal.view.portal.Portlet', {
//                id : ID.PORTLET.PROJET,
                title : i18n.get('label.portletProjetPrivateTitle'),
                height : 400,
                items : [ dataViewProjectPrivate ],
                autoScroll : true
            });
            
        }

        /***************************************************************************
         * Creation du portlet d'affichage des flux rss/atom
         */

        var panelFluxPortal = Ext.create('sitools.clientportal.view.portal.FeedsReaderPortal', {});

        var portletFluxPortal = {
            xtype : 'portlet',
            layout : 'fit',
            id : ID.PORTLET.FEEDS,
            title : i18n.get('title.portlelFeedsPortal'),
            height : 400,
            items : [ panelFluxPortal ]
        };

        var footerPanel = Ext.create('Ext.container.Container', {
            autoScroll : false,
            layout: 'fit',
            items : [{
                xtype : 'component',
                autoEl: {
                    tag: 'iframe',
                    border : false,
                    bodyBorder : false,
                    src: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PORTAL_URL') + "/resources/html/" + locale.getLocale() + "/footer.html"
                }
            }]
        });
        
        Ext.create('Ext.fx.Animator', {
            target: footerPanel,
            duration: 2000,
            keyframes: {
                0 : {
                    opacity : 0
                },
                100 : {
                    opacity : 1
                }
            }
        });
        
        /***************************************************************************
         * Creation tabPanel Center qui contient le portal
         */

        var itemCenterRegion;
        if (!Ext.isEmpty(portletProjetPrivate)) {
            itemCenterRegion = [{
                columnWidth : 0.35,
                items : [ portletProjetPublic ]
            },{
                columnWidth : 0.35,
                items : [ portletProjetPrivate ]
            }, {
                columnWidth : 0.35,
                items : [ portletFluxPortal ]
            }];
        } else {
            itemCenterRegion = [{
                columnWidth : 0.50,
                items : [ portletProjetPublic ]
            }, {
                columnWidth : 0.50,
                items : [ portletFluxPortal ]
            }];
        }
        
        var northPanel = Ext.create('Ext.Component', {
            region : 'north',
            title : i18n.get('label.freeText'),
            autoScroll : false,
            layout : 'fit',
            height : 200,
            margin : 10,
            autoEl: {
                tag: 'iframe',
                border : false,
                src: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PORTAL_URL') + "/resources/html/" + locale.getLocale() + "/freeText.html",
            }
        });
        
        Ext.create('Ext.fx.Animator', {
            target: northPanel,
            duration: 2000,
            keyframes: {
                0 : {
                    opacity : 0
                },
                100 : {
                    opacity : 1
                }
            }
        });
        
        var centerPanel = Ext.create('Ext.panel.Panel', {
            region : 'center',
            layout : 'border',
            autoScroll : false,
            border : false,
            bodyBorder : false,
            bodyCls : 'bg-transparent-2',
            cls : 'box-shadow',
            margin : 10,
            items : [ northPanel, {
                region : 'center',
                baseCls : 'portalMainPanel',
                xtype : 'portalpanel',
                margins : '35 5 5 0',
                layout : 'fit',
                defaults : {
                    style : 'padding:10px 0 10px 10px'
                },
                items : itemCenterRegion
            }]
        });
        
        var southPanel = {
            region : 'south',
            xtype : 'portalpanel',
            border : false,
            bodyBorder : false,
            cls : 'box-shadow',
            height : 70,
            margin : 10,
            autoScroll : false,
            items : [footerPanel]
        };
        
//        var mainPanel = Ext.create('Ext.tab.Panel', {
//            baseCls : 'portalMainPanel',
//            region : 'center',
//            activeTab : 0,
//            items : [{
//                xtype : 'panel',
//                baseCls : 'portalMainPanel',
//                autoScroll : false,
//                title : i18n.get('label.portalTitle'),
//                layout : 'border',
//                items : [{
//                    region : 'north',
//                    xtype : 'component',
//                    title : i18n.get('label.freeText'),
//                    autoScroll : false,
//                    layout : 'fit',
//                    y : 30,
//                    height : 200,
//                    defaults : {
//                        padding : 30
//                    },
//                    autoEl: {
//                        tag: 'iframe',
//                        border : false,
//                        y : 30,
//                        src: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + "/res/html/" + locale.getLocale() + "/freeText.html",
//                    }
//                }, {
//                    region : 'center',
//                    baseCls : 'portalMainPanel',
//                    bodyStyle : 'background-color: transparent !important;',
//                    xtype : 'portalpanel',
//                    id : 'portalId',
//                    margins : '35 5 5 0',
//                    layout : 'fit',
//                    defaults : {
//                        style : 'padding:10px 0 10px 10px'
//                    },
//                    items : [{
//                        columnWidth : 0.50,
//                        style : 'padding:10px 0 10px 10px',
//                        // baseCls : 'portalMainPanel',
//                        items : [ portletProjet ]
//                    }, {
//                        columnWidth : 0.50,
//                        style : 'padding:10px',
//                        // baseCls : 'portalMainPanel',
//                        items : [ portletFluxPortal/*, portletRecherche*/]
//                    }]
//                }]
//            }, contactPanel, linkPanel, helpPanel ]
//        });
        
        this.items = [ toolbar, centerPanel, southPanel ];

        this.callParent(arguments);
    }

});
