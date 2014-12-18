/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse*/

Ext.namespace('sitools.user.core');

Ext.define('sitools.user.core.Project', {
    /**
     * Get the current projectId from the url url is like :
     * /sitools/client-user/index.html?project={projectName}
     * 
     * @return the projectId
     */
    singleton : true,
    
    config : {
        projectId : null,
        projectName : null,
        preferences : null,
        userRoles : null, 
        isAdmin : false,
        sitoolsAttachementForUsers : null,
        modules : null,
        links : null,
        htmlHeader : null,
        languages : null,
        navigationMode : null,
        modulesInDiv : []
    },
    
    // 1
    init : function (callback, scope) {
        this.projectName = this.initProjectName();
        this.initProjectInfo(callback, scope);
        this.setLanguages(locale.getLanguages());
    },
    
    initProjectName : function () {
        if (this.projectName === null) {
            // get the relative url
            var url = document.location.href;
            
            var reference = new Reference(url);
            this.projectName = reference.getArgumentValue('project');

            if (this.projectName === undefined || this.projectName === "") {
                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProject'));
            }
        }
        return this.projectName;
    },

    // 2
    initProjectInfo : function (callback, scope) {
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL') + '/projects/' + this.projectName,
            method : "GET",
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectFound'));
                    return false;
                } else {
                    this.sitoolsAttachementForUsers = data.project.sitoolsAttachementForUsers;
                    this.projectId = data.project.id;
                    this.projectName = data.project.name;
                    this.htmlHeader = data.project.htmlHeader;
                    this.links = data.project.links;
                    this.navigationMode = data.project.navigationMode;
//                    Ext.callback(callback, scope);
                }
//                    var topEl = Ext.get('toppanel');
//                    topEl.update(Ext.util.Format.htmlDecode(data.project.htmlHeader));
            },
            failure : function (response, opts) {
                Ext.getBody().unmask();
                if (response.status === 403) {
                    Ext.MessageBox.buttonText.ok = i18n.get('label.login');
                    Ext.Msg.show({
                        title : i18n.get('label.information'),
                        msg : i18n.get('label.projectNeedToBeLogged'),
                        width : 350,
                        buttons : Ext.MessageBox.OK,
                        icon : Ext.MessageBox.INFO,
                        fn : function (response) {
                            if (response === 'ok') {
                            	sitools.public.utils.LoginUtils.connect({
                                    url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                                    register : loadUrl.get('APP_URL') + '/inscriptions/user',
                                    reset : loadUrl.get('APP_URL') + '/lostPassword',
                                    unblacklist : loadUrl.get('APP_URL') + '/unblacklist'
                                });
                            }
                        }
                    });
                }
                if (response.status === 503) {
                    Ext.MessageBox.buttonText.ok = i18n.get('label.login');
                    Ext.Msg.show({
                        title : i18n.get('label.information'),
                        msg : i18n.get('label.projectinactive'),
                        width : 350,
                        icon : Ext.MessageBox.WARNING,
                        closable : false
                    });
                }
                else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectError'));
                }
            },
            callback : function (opts, success, response) {
            	if (success) {
                    var project = Ext.decode(response.responseText).project;
                    if(project.maintenance) {
                        this.showMaintenance(project);
                    } else {
                        this.initPreferences(callback, scope);
                    }
            	}
            }
        });
    },
    
    // 3
    initPreferences : function (callback, scope) {
        if (!Ext.isEmpty(userLogin)) {
            var filePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + this.projectName;
            var fileName = "desktop";
            var success = function (ret) {
                try {
                    this.preferences = Ext.decode(ret.responseText);
                    Ext.callback(callback, scope);
                } catch (err) {
                    Ext.callback(callback, scope);
                }
            };
            
            var failure = function (ret) {
            	this.initPublicPreferences(callback, scope);
            };
            UserStorage.get(fileName, filePath, this, success, failure);
            
        } else {
        	this.initPublicPreferences(callback, scope);
        }
    },
    
    // 4
    initPublicPreferences : function (callback, scope) {
        var AppPublicStorage = loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files";
        
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + AppPublicStorage + "/" + DEFAULT_PREFERENCES_FOLDER + "/" + this.projectName + "/desktop?media=json",
            method : 'GET',
            scope : this,
            success : function (ret) {
                try {
                	this.setPreferences(Ext.decode(ret.responseText));
                } catch (err) {
                    this.setPreferences(null);
                }
            }, 
            callback : function () {
                Ext.callback(callback, scope);
            }
        });
    },
    
    getUserRoles : function (cb) {
        if (Ext.isEmpty(userLogin)) {
            cb.call();
        } 
        else {
            Ext.Ajax.request({
                url : loadUrl.get('APP_URL') + loadUrl.get("APP_USER_ROLE_URL"),
                method : "GET",
                scope : this,
                success : function (ret) {
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.errorGettingUserRoles'));
                        return false;
                    } else {
                        this.user = json.user;        
                        if (Ext.isEmpty(this.user.roles)) {
                            return;
                        }
                        for (var index = 0; index < this.user.roles.length; index++) {
                            var role = this.user.roles[index];
                            if (role.name === "Administrator") {
                                this.isAdmin = true;
                            }
                        }
                    }
                },
                callback : cb
            });   
        }
    },

    showMaintenance : function (project) {
        Ext.getBody().unmask();
        var maintenanceText = project.maintenanceText;
        if(Ext.isEmpty(maintenanceText)) {
            maintenanceText = i18n.get("label.defaultMaintenance.text");
        }
        var alertWindow = Ext.create("Ext.window.Window", {
            title : i18n.get('label.maintenance'),
            width : 600,
            height : 400,
            autoScroll : true,
            closable : false,
            items : [{
                border : false,
                xtype : 'panel',
                layout : 'fit',
                autoScroll : true,
                html : maintenanceText,
                padding : "5"
            }],
            modal : true
        });
        alertWindow.show();
        return;
    }
    
});

Project = sitools.user.core.Project;