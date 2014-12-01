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
            bodyBorder : false
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
                        UserStorage.set(loadUrl.get('APP_PORTAL_URL'),  "/" + DEFAULT_PREFERENCES_FOLDER + loadUrl.get('APP_PORTAL_URL'), userPreferences, callback);
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
            enableOverflow : true,
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
                menu : menuLangues,
                iconCls : 'languageMenuIcon',
            }, '-', editProfileButton, (editProfileButton.hidden) ? null : '-', menuLoginLogout ]
        };
        
        /***************************************************************************
         * Creation du portlet Liste des projets
         */

        var portletCollection = new Ext.util.MixedCollection();
        
        Ext.each(this.projects, function (project) {
            
            var record = {
                id : project.id, 
                name : project.name, 
                description : project.description, 
                image : project.image.url || SITOOLS_DEFAULT_PROJECT_IMAGE_URL, 
                authorized : project.authorized, 
                maintenance : project.maintenance, 
                maintenanceText : project.maintenanceText,
                priority : project.priority,
                categoryProject : project.categoryProject
            };
            
            // creation of the portletObject if it does not already exist
            
            var portletObject = {};
            if (Ext.isEmpty(project.categoryProject) && project.authorized) {
            	portletObject.category = i18n.get('label.publicProject');
            } else if (!project.authorized){
            	portletObject.category = i18n.get('label.privateProject');
            }
            
            
            if (portletCollection.get(portletObject.category) == undefined && portletCollection.get(project.categoryProject) == undefined) {
                
                if (!project.authorized) {
                	portletObject.category = i18n.get('label.privateProject');
                }
//                else if ((project.categoryProject === "" || project.categoryProject == undefined) && portletCollection.get("") === undefined) {
//                    portletObject.category = "Public";
//                }
                else if (!Ext.isEmpty(project.categoryProject)) {
                    portletObject.category = project.categoryProject;
                }
                
                portletObject.store = this.createStore();
                portletObject.store.add(record);
                portletObject.store.sort({
                	property : 'priority',
                	direction : 'ASC'
                });
                
                portletObject.dataview = this.createDataview(portletObject.store);
                
                portletObject.portlet = this.createPortlet(portletObject);
                
                portletCollection.add(portletObject.category, portletObject);
                
            } else { // just adding record to the portletObject store
                var portletObject = portletCollection.get(portletObject.category) || portletCollection.get(project.categoryProject);
                if (!Ext.isEmpty(portletObject)) {
                    portletObject.store.add(record);
                    portletObject.store.sort({
                    	property : 'priority',
                    	direction : 'ASC'
                    });
                }
            }
        }, this);
        

        /***************************************************************************
         * Creation du portlet d'affichage des flux rss/atom
         */

        var panelFluxPortal = Ext.create('sitools.clientportal.view.portal.FeedsReaderPortal', {});

        var portletFluxPortal = {
            xtype : 'portlet',
            layout : 'fit',
            title : i18n.get('title.portlelFeedsPortal'),
            height : 410,
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

        var onlyPortletTab = [];
        portletCollection.each(function (item, index, length) {
            onlyPortletTab.push(item.portlet);
        }, this);
        
        var itemCenterRegion;
        
        itemCenterRegion = [{
            columnWidth : 0.50,
//            items : [ portletProjetPublic ]
        	items : onlyPortletTab
        }, {
            columnWidth : 0.50,
            items : [ portletFluxPortal ]
        }];
        
        var northPanel = Ext.create('Ext.Component', {
            region : 'north',
            title : i18n.get('label.freeText'),
            autoScroll : false,
            layout : 'fit',
            flex : .4,
//            height : 200,
            margin : 10,
            autoEl: {
                tag: 'iframe',
                border : false,
                autoScroll : false,
                src: loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PORTAL_URL') + "/resources/html/" + locale.getLocale() + "/freeText.html"
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
            	xtype : 'portalpanel',
                region : 'center',
                baseCls : 'portalMainPanel',
                margins : '20 5 5 0',
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
        
        this.items = [ toolbar, centerPanel, southPanel ];

        this.callParent(arguments);
    },
    
    createStore : function () {
        return Ext.create('Ext.data.JsonStore', {
            fields : [ 'id', 'name', 'description', 'image', 'authorized', 'maintenance', 'maintenanceText', 'priority', 'categoryProject' ],
            sorters : {
                property : 'priority',
                direction : 'ASC'
            }
        });
    },
    
    createDataview : function (store) {
        return Ext.create('Ext.view.View', {
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
                    '<img width="80" height="80" src="{image}" />', '<p data-qtip="{name}" class="projectName">{name}</p>',
                    '<p class="projectDescription" data-qtip="{description}">{description} </p>', '</li>', '</tpl>', '</ul>', 
                    {
                    compiled : true, 
                    disableFormats : true, 
                    isAuthorized : function (authorized) {
                        return authorized === true;
                    }
                }),
//            id : 'projectDataView',
            autoScroll : true,
            cls : 'projectDataView',
            itemSelector : 'li.project',
            overItemCls : 'project-hover',
            mode : 'SINGLE'
        });
    },
    
    createPortlet : function (portletObject) {
        return Ext.create('sitools.clientportal.view.portal.Portlet', {
            title : portletObject.category,
            height : 200, 
            boxMaxHeight : 430,
            items : [ portletObject.dataview ],
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
