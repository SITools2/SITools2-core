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
/*global Ext, utils_logout, sitools, SitoolsDesk, window, userLogin, showResponse, projectGlobal, 
  userStorage, DEFAULT_PREFERENCES_FOLDER, i18n, extColModelToJsonColModel, loadUrl*/

Ext.namespace('sitools.user.view.header');

/**
 * @cfg {String} buttonId the id of the button that displays the window
 * @class sitools.user.component.entete.UserProfile
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.header.UserProfileView', {
    extend : 'Ext.window.Window',
    alias: 'widget.userProfileWindow',
    
    width : 450,
    id : "userProfileWindow", 
    header : false,
    stateful : false, 
    shadow : false,
    hideBorders : true, 
    closable : false, 
    resizable : false, 
    border : false, 
    bodyBorder : false, 
    
    initComponent : function () {
        
        var UserStore = Ext.data.StoreManager.get('UserStore');
        this.user = UserStore.getAt(0);
        
        this.userPublic = this.user.get('identifier');
        var userLanguage = locale.getLocale(), userLargeIcon;
        
        Ext.each(locale.getLanguages(), function (language) {
            if (userLanguage === language.localName) {
                userLargeIcon = language.largeIcon;
            }
        });
        var freeDisk = 0;
        var totalDisk = 0;
        var userTasksRunning = 0;
        var userTotalTasks = 0;
        var data = [{
            xtype : 'button',
            identifier : "language", 
            name: i18n.get("label.langues"), 
            icon : userLargeIcon,
            iconCls : 'usrProfileIcon',
            action : "changeLanguage"
        }];
        
        this.height =  this.user.get('identifier') === "public" ? 140 : 220;
        this.layout = {
            type : this.user.get('identifier') === "public" ? 'hbox' : 'vbox',
            align : "stretch"
        };
        
//        this.x = Ext.getBody().getWidth() - this.width;
//        this.y = SitoolsDesk.getEnteteEl().getHeight(); 
        
        if (this.user.get('identifier') !== "public") {
            data.push({
                xtype : 'button',
                identifier : "editProfile", 
                name: i18n.get("label.editProfile"), 
                icon : '/sitools/common/res/images/icons/menu/regcrud.png', 
                action : "editProfile",
                comment : ""
            }, {
                xtype : 'button',
                width : 30,
                height : 30,
                identifier : "userDiskSpace", 
                name: i18n.get('label.userDiskSpace'), 
                icon : '/sitools/common/res/images/icons/menu/dataAccess.png',
                action : "showDisk", 
                comment : Ext.String.format(i18n.get("label.userDiskUse"), freeDisk, totalDisk)
            }, {
                xtype : 'button',
                width : 30,
                height : 30,
                identifier : "tasks", 
                name: i18n.get("label.Tasks"), 
                icon : "/sitools/common/res/images/icons/menu/applications2.png",
                action : "showTasks", 
                comment : Ext.String.format(i18n.get("label.taskRunning"), userTasksRunning, userTotalTasks)
            }, {
                xtype : 'button',
                width : 30,
                height : 30,
                identifier : "orders", 
                name: i18n.get("label.orders"), 
                icon : "/sitools/common/res/images/icons/menu/order.png",
                action : "showOrders"
            });
            
        }

        var store = Ext.create('Ext.data.JsonStore', {
            fields : ['name', 'url', 'action', 'comment', 'identifier'],
            data : data
        });
        
        var tpl = new Ext.XTemplate('<tpl for=".">',
                '<div class="userButtons" id="{identifier}">',
                '<div class="userButtons-thumb"><img src="{url}" title="{name}"></div>',
                '<span class="userButtons-name">{name}</span>', 
                '<span class="userButtons-comment">{comment}</span></div>',
            '</tpl>',
            '<div class="x-clear"></div>'
        );
        
        var buttonsDataView = Ext.create('Ext.view.View', {
            store: store,
            cls : "userButtonsDataview", 
            tpl: tpl,
            autoHeight : true,
            width : this.userPublic ? 100 : this.width, 
            multiSelect: true,
            overItemCls: 'x-view-over',
            overCls : 'userButtonsPointer',
            emptyText: 'No images to display', 
            itemSelector: 'div.userButtons',
            listeners : {
                scope : this, 
//                click : this.actionItemClick, 
                afterRender : function () {
                    this.fillDiskInformations();
                    this.fillTaskInformations();
                }
            }
        });
        
        
        var userInfoStore = Ext.create('Ext.data.JsonStore', {
            fields : ['firstName', 'lastName', 'image', 'email', 'identifier'],
            data : [Ext.apply(this.user, {
                "image" : "/sitools/client-public/res/images/icons/menu/usersGroups.png"
            })]
        });
        
        var logout = Ext.create('Ext.button.Button', {
            text  : i18n.get('label.logout'),
            name : 'usrProfileLogout'
        });
        
        var login = Ext.create('Ext.button.Button', {
            text  : i18n.get('label.login'),
            name : 'usrProfileLogin',
            cls : "userProfileBtn"
        });
        
        var register = Ext.create('Ext.button.Button', {
            text  : i18n.get('label.register'),
            name : 'usrProfileRegister',
            cls : "userProfileBtn"
        });
        var closeBtn = Ext.create('Ext.button.Button', {
            scope : this,
            icon : "/sitools/common/res/images/icons/close-icon.png",
            cls : 'button-transition',
            handler : function () {
                this.destroy();
            }, 
            x : this.width - 30, 
            y : this.height * -1 + 1, 
            style : {
                "position" : "relative", 
                "height" : 16,
                "width" : 16,
                "z-index" : 200
            }
        });
        
        var displayInfo = Ext.create('Ext.view.View', {
            flex : 1,
            logoutBtn : logout, 
            loginBtn : login,
            closeBtn : closeBtn,
            registerBtn : register,
            cls : "x-panel-body",
            tpl : new Ext.XTemplate('<tpl for=".">',
                    '<div class="userProfileItem" id="{identifier}">',
                    '<div class="userProfile userProfileItem-thumb"><img style="height:60px;" src="{image}" title="{name}"></div>',
                    '<div class="userProfile"><span class="userProfileName">{firstName} {lastName}</span>', 
                    '<span class="userProfileEmail">{email}</span>',
                    '<div id="logBtn"></div>', 
                    '</div>', 
                '</tpl>',
                '<div class="x-clear"></div>'
            ), 
            store : userInfoStore, 
            listeners : {
                scope : this, 
                afterRender : function (me) {
//                    if (this.user.get('identifier') !== "public") {
//                        me.logoutBtn.render("logBtn");
//                    }
//                    else {
//                        me.loginBtn.render("logBtn");
//                        me.registerBtn.render("logBtn");
//                    }
//                    me.closeBtn.render(this.id);
                }
            }
        });
        
        this.infoPanel = Ext.create('Ext.view.View', {
            flex : 1,
            store : userInfoStore,
            tpl : new Ext.XTemplate('<tpl for=".">',
                    '<div class="userProfileItem" id="{identifier}">',
                    '<div class="userProfile userProfileItem-thumb"><img style="height:60px;" src="{image}" title="{name}"></div>',
                    '<div class="userProfile"><span class="userProfileName">{firstName} {lastName}</span>', 
                    '<span class="userProfileEmail">{email}</span>',
                    '<div id="logBtn"></div>', 
                    '</div>', 
                '</tpl>',
                '<div class="x-clear"></div>'
            )
        });
        
        this.buttonsPanel = Ext.create('Ext.panel.Panel', {
            layout : {
                type : 'hbox',
                pack : 'end',
                align : 'stretchmax',
                defaultMargins : {
                    left : 10
                }
            },
            padding : 10,
            bodyPadding : 10,
            border : true,
            items : [logout, login, register]
        });
        
//        this.items = [displayInfo, {
//            xtype : 'panel', 
//            items : [buttonsDataView]
//        }];
        
        this.items = [this.infoPanel, this.buttonsPanel];
//        
//        this.listeners = {
//            scope : this, 
//            beforeRender : function () {
//                Ext.getBody().on("click", this.interceptOnClick, this);
//            }, 
//            beforeDestroy : function (me) {
//                Ext.getBody().un("click", this.interceptOnClick, this);
//                Ext.getCmp(this.buttonId).enable();
//            }
//        };
        
        this.callParent(arguments);
        
    }, 
//    /**
//     * while this window is active, checked if any click is done on this window, or somewhere Else. 
//     * 
//     * @param {Ext.event} evt the clic Event. 
//     * @param {HtmlElement} target the Html target element. 
//     * @returns
//     */
//    interceptOnClick : function (evt, target) {
//        //le click est sur le bouton pour ouvrir la fenêtre : Désactiver le bouton... et fin de l'action.
//        if (Ext.DomQuery.select("table[id=" + this.buttonId + "] button[id=" + target.id + "]").length === 1) {
//            Ext.getCmp(this.buttonId).disable();
//            return;
//        }
//        
//        //Le clic est sur un élément de la fenêtre : rien à faire. 
//        if (this.isDescendant(Ext.DomQuery.select("div[id=userProfileWindow]")[0], target)) {
//            if (evt.shiftKey && evt.ctrlKey) {
//                breakout().getBackToDesktop();
//            }
//            return;
//        }
//        
//        //Le clic est quelque part en dehors de la fenêtre, on détruit la fenêtre (-> beforeDestroy est exécuté)
//        this.destroy();
//    }, 
    /**
     * Handler of any click on the dataview used to display actions btn : 
     * Execute the method specified in each store.action attribute. 
     * 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     * @returns
     */
//    actionItemClick : function (dataView, index, node, e) {
//        try {
//            var data = dataView.getSelectedRecords()[0].data;   
//            eval("this." + data.action).call(this, dataView, index, node, e);
//        }
//        catch (err) {
//            return;
//        }
//        
//    }, 
    
//    isDescendant : function (parent, child) {
//        var node = child.parentNode;
//        while (node !== null) {
//            if (node === parent) {
//                return true;
//            }
//            node = node.parentNode;
//        }
//        return false;
//    }, 
    /**
     * Open a Ext.Menu.menu containing all projectGlobal.languages options. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    changeLanguage : function (dataView, index, node, e) {
        var menuLangues = new Ext.menu.Menu({
            plain : true
        });
        Ext.each(projectGlobal.languages, function (language) {
            menuLangues.add({
                text : language.displayName,
                scope : this,
                handler : function () {
                    var callback = function () {
                        Ext.util.Cookies.set('language', language.localName);
                        window.location.reload();
                    };
                    var date = new Date();
                    Ext.util.Cookies.set('language', language.localName, date.add(Date.MINUTE, 20));
                    var userPreferences = {};
                    userPreferences.language = language.localName;
                    if (!Ext.isEmpty(userLogin)) {
                        userStorage.set(loadUrl.get('APP_PORTAL_URL'),  "/" + DEFAULT_PREFERENCES_FOLDER + loadUrl.get('APP_PORTAL_URL'), userPreferences, callback);
                    } else {
                        window.location.reload();
                    }

                },
                icon : language.image
            });
        }, this);
        menuLangues.showAt([Ext.get(node.id).getLeft(), Ext.get(node.id).getBottom()]);
        
    }, 
    /**
     * Edit the profile of the user depending on the server configuration
    * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    editProfile : function (dataView, index, node, e) {
        if (this.user.identifier === "public") {
            return;
        }
        
        var callback = Ext.createDelegate(this.onEditProfile, this);        
        sitools.userProfile.LoginUtils.editProfile(callback);
        
    }, 
    
    /**
     * Open a window in the desktop with the sitools.userProfile.editProfile object. 
     */
    onEditProfile : function () {
        var componentCfg = {
            identifier : this.user.identifier,
            url : '/sitools/editProfile/' + this.user.identifier,
            handler : function (user) {
                projectGlobal.user = user;
            }
        };
        var jsObj = sitools.userProfile.editProfile;

        var windowConfig = {
            title : i18n.get('label.editProfile'),
            saveToolbar : false,
            iconCls : "editProfile"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
        this.destroy();
    },
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.tasks object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showTasks : function () {
        var jsObj = sitools.user.component.entete.userProfile.tasks;
        var windowConfig = {
            title : i18n.get('label.Tasks'),
            saveToolbar : false, 
            iconCls : 'tasks'
        };
        SitoolsDesk.addDesktopWindow(windowConfig, {}, jsObj, true);
        this.destroy();
        
    }, 
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.diskSpace object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showDisk : function () {
        var jsObj = sitools.user.component.entete.userProfile.diskSpace;
        var windowConfig = {
            title : i18n.get('label.userSpace'),
            saveToolbar : false, 
            iconCls : "diskSpace"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, {}, jsObj, true);
        this.destroy();
    }, 
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.diskSpace object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showOrders : function () {
        var jsObj = sitools.user.component.entete.userProfile.viewOrderPanel;
        var windowConfig = {
            title : i18n.get('label.ordersHistory'),
            saveToolbar : false, 
//            iconCls : "orders"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, {}, jsObj, true);
        this.destroy();
    }, 
    /**
     * Send a request on user task resource to determine how many tasks are launched and finished.
     * Update the task comment div with the formated result. 
     */
    fillDiskInformations : function () {
        var el = Ext.DomQuery.select("div[id='userDiskSpace'] span[class='userButtons-comment']")[0];
        if (Ext.isEmpty(el)) {
            return;
        }
        Ext.Ajax.request({
            method : "GET",

            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace("{identifier}", this.user.identifier) + "/status", 
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
                    Ext.get("userDiskSpace").addClass("sitools-userProfile-warning-icon");
                    cls = "sitools-userProfile-warning-text";
                }
                else if (pourcentage > 100) {
                    Ext.get("userDiskSpace").addClass("sitools-userProfile-error-icon");
                    cls = "sitools-userProfile-error-text";
                }
                var str = "";
                if (!Ext.isEmpty(cls)) {
                    str += "<span class='" + cls + "'>";
                }
                str += Ext.String.format(i18n.get('label.diskSpace'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
                if (!Ext.isEmpty(cls)) {
                    str += "</span>";
                }
                el.update(str);
            }
        });
    }, 
    /**
     * Send a request on user userstorage resource to determine the space allowed and consumed. 
     * Update the diskSpace comment div with the formated result. 
     */
    fillTaskInformations : function () {
        var el = Ext.DomQuery.select("div[id='tasks'] span[class='userButtons-comment']")[0];
        if (Ext.isEmpty(el)) {
            return;
        }
        Ext.Ajax.request({
            method : "GET",
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERRESOURCE_ROOT_URL') +  "/" + this.user.identifier + "/tasks", 
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    return;
                }
                var runningTasks = 0, totalTasks = 0;
                Ext.each(json.data, function (task) {
                    if (task.status === "TASK_STATUS_RUNNING" || task.status === "TASK_STATUS_PENDING") {
                        runningTasks++;
                    }
                    totalTasks++;
                });
                if (runningTasks > 0) {
                    el.update(Ext.String.format(i18n.get('label.taskRunning'), runningTasks));
                } else {
                    el.update("");
                }
            }
        });
        
    }
});