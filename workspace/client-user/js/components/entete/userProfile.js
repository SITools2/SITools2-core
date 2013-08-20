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
/*global Ext, utils_logout, sitools, SitoolsDesk, window, userLogin, showResponse, projectGlobal, 
  userStorage, DEFAULT_PREFERENCES_FOLDER, i18n, extColModelToJsonColModel, loadUrl*/

Ext.namespace('sitools.user.component.entete');

/**
 * @cfg {String} buttonId the id of the button that displays the window
 * @class sitools.user.component.entete.UserProfile
 * @extends Ext.Window
 */
sitools.user.component.entete.UserProfile = Ext.extend(Ext.Window, {
	
	width : 450,
	
	initComponent : function () {
	    
		this.header = false;
		
		this.user = projectGlobal.user || {
			firstName : "public", 
			identifier : "public", 
			email : "&nbsp;"
		};
		this.userPublic = this.user.identifier === "public";
		var userLanguage = SitoolsDesk.app.language, userLargeIcon;
		
		Ext.each(projectGlobal.languages, function (language) {
			if (userLanguage === language.localName) {
				userLargeIcon = language.largeIcon;
			}
		});
		var freeDisk = 0;
		var totalDisk = 0;
		var userTasksRunning = 0;
		var userTotalTasks = 0;
		var data = [{
			identifier : "language", 
			name: i18n.get("label.langues"), 
			url : userLargeIcon, 
			action : "changeLanguage"
		}];
		
		this.height =  this.user.identifier === "public" ? 140 : 220; 
		
		if (this.user.identifier !== "public") {
			data.push({
				identifier : "editProfile", 
				name: i18n.get("label.editProfile"), 
				url : '/sitools/common/res/images/icons/menu/regcrud.png', 
				action : "editProfile", 
				comment : ""
			}, {
				identifier : "userDiskSpace", 
				name: i18n.get('label.userDiskSpace'), 
				url : '/sitools/common/res/images/icons/menu/dataAccess.png', 
				action : "showDisk", 
				comment : String.format(i18n.get("label.userDiskUse"), freeDisk, totalDisk)
			}, {
				identifier : "tasks", 
				name: i18n.get("label.Tasks"), 
				url : "/sitools/common/res/images/icons/menu/applications2.png", 
				action : "showTasks", 
				comment : String.format(i18n.get("label.taskRunning"), userTasksRunning, userTotalTasks)
			}, {
				identifier : "orders", 
				name: i18n.get("label.orders"), 
				url : "/sitools/common/res/images/icons/menu/order.png", 
				action : "showOrders"
			});
			
		}

		var store = new Ext.data.JsonStore({
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
		
		var buttonsDataView = new Ext.DataView({
	        store: store,
	        cls : "userButtonsDataview", 
	        tpl: tpl,
	        autoHeight : true,
	        width : this.userPublic ? 100 : this.width, 
	        multiSelect: true,
	        overClass: 'x-view-over',
	        overCls : 'userButtonsPointer',
	        emptyText: 'No images to display', 
	        itemSelector: 'div.userButtons',
	        listeners : {
				scope : this, 
				click : this.actionItemClick, 
				afterRender : function () {
					this.fillDiskInformations();
					this.fillTaskInformations();
				}
			}
	    });
		
		
		var userInfoStore = new Ext.data.JsonStore({
			fields : ['firstName', 'lastName', 'image', 'email', 'identifier'],
			data : [Ext.apply(this.user, {
				"image" : "/sitools/common/res/images/icons/menu/usersGroups.png"
			})]
		});
		var logout = new Ext.Button({
			scope : this, 
			text  : i18n.get('label.logout'), 
			handler : function () {
			    sitools.userProfile.LoginUtils.logout();
			}
		});
		var login = new Ext.Button({
			scope : this, 
			cls : "userProfileBtn", 
			text  : i18n.get('label.login'), 
			handler : function () {
			    sitools.userProfile.LoginUtils.connect({
			        closable : true,
                    url : loadUrl.get('APP_URL') + '/login',
                    register : loadUrl.get('APP_URL') + '/inscriptions/user',
                    reset : loadUrl.get('APP_URL') + '/resetPassword'
                });			    
			}
		});
		
		var register = new Ext.Button({
			scope : this, 
			cls : "userProfileBtn", 
			text  : i18n.get('label.register'), 
			handler : function () {
				var register = new sitools.userProfile.Register({
	                closable : true,
	                url : "/sitools/inscriptions/user",
	                login : "/sitools/login"
	            });
	            register.show();
			}
		});
		var closeBtn = new Ext.Button({
			scope : this,
			icon : "/sitools/common/res/images/icons/close-icon.png", 
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
		
		var displayInfo = new Ext.DataView({
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
					if (this.user.identifier !== "public") {
						me.logoutBtn.render("logBtn");
					}
					else {
						me.loginBtn.render("logBtn");
						me.registerBtn.render("logBtn");
					}
					me.closeBtn.render(this.id);
				}
			}
		});
		sitools.user.component.entete.UserProfile.superclass.initComponent.call(Ext.apply(this, {
			id : "userProfileWindow", 
			header : false,
			stateful : false, 
			shadow : false, 
			layout : this.user.identifier === "public" ? 'hbox' : 'vbox',
			layoutConfig : {
				align : "stretch"
			},
			border : false, 
			hideBorders : true, 
			closable : false, 
			x : Ext.getBody().getWidth() - this.width, 
			y : SitoolsDesk.getEnteteEl().getHeight(), 
			resizable : false, 
			bodyBorder : false, 
			listeners : {
				scope : this, 
				beforeRender : function () {
					Ext.getBody().on("click", this.interceptOnClick, this);
				}, 
				beforeDestroy : function (me) {
					Ext.getBody().un("click", this.interceptOnClick, this);
					Ext.getCmp(this.buttonId).enable();
				}
			}, 
			items : [displayInfo, {
				xtype : "panel", 
				items : [buttonsDataView]
			}]
		}));
		
	}, 
	/**
	 * while this window is active, checked if any click is done on this window, or somewhere Else. 
	 * 
	 * @param {Ext.event} evt the clic Event. 
	 * @param {HtmlElement} target the Html target element. 
	 * @returns
	 */
	interceptOnClick : function (evt, target) {
		//le click est sur le bouton pour ouvrir la fenêtre : Désactiver le bouton... et fin de l'action.
		if (Ext.DomQuery.select("table[id=" + this.buttonId + "] button[id=" + target.id + "]").length === 1) {
			Ext.getCmp(this.buttonId).disable();
			return;
		}
		
		//Le clic est sur un élément de la fenêtre : rien à faire. 
		if (this.isDescendant(Ext.DomQuery.select("div[id=userProfileWindow]")[0], target)) {
			if (evt.shiftKey && evt.ctrlKey) {
				breakout().getBackToDesktop();
			}
			return;
		}
		
		//Le clic est quelque part en dehors de la fenêtre, on détruit la fenêtre (-> beforeDestroy est exécuté)
		this.destroy();
	}, 
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
	actionItemClick : function (dataView, index, node, e) {
		try {
			var data = dataView.getSelectedRecords()[0].data;	
			eval("this." + data.action).call(this, dataView, index, node, e);
		}
		catch (err) {
			return;
		}
		
	}, 
	isDescendant : function (parent, child) {
		var node = child.parentNode;
		while (node !== null) {
		    if (node === parent) {
		        return true;
		    }
		    node = node.parentNode;
		}
		return false;
	}, 
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
			    str += String.format(i18n.get('label.diskSpace'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
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
				    el.update(String.format(i18n.get('label.taskRunning'), runningTasks));
                } else {
                    el.update("");
                }
			}
		});
		
	}
	
});