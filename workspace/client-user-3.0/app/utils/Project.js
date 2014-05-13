Ext.define("sitools.user.utils.Project", {
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
        languages : null
    },
    
    init : function (callback, scope) {
        this.projectName = this.initProjectName();
        this.initProjectInfo(callback, scope);
    },

    initLanguages : function () {
        Ext.Ajax.request({
            scope : this,
            method : "GET",
            /* /sitools/client-user */
//                url : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + '/tmp/langues.json',
            url : loadUrl.get('APP_URL') + '/client-user/tmp/langues.json',
            success : function (response) {
                var json = Ext.decode(response.responseText);
                this.languages.setLanguages(json.data);
            },
            failure : function (response) {
                Ext.Msg.alert('Status', i18n.get('warning.serverError'));
            }, 
            callback : function () {
                this.getPreferences(this.callback);
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
    /**
     * Get the name of a project from the server
     */
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
                    Ext.callback(callback, scope);
                }
//                    var topEl = Ext.get('toppanel');
//                    topEl.update(Ext.util.Format.htmlDecode(data.project.htmlHeader));
            },
            failure : function (response, opts) {
                if (response.status === 403) {
                    Ext.getBody().unmask();
                    Ext.MessageBox.buttonText.ok = i18n.get('label.login');
                    Ext.Msg.show({
                        title : i18n.get('label.information'),
                        msg : i18n.get('label.projectNeedToBeLogged'),
                        width : 350,
                        buttons : Ext.MessageBox.OK,
                        icon : Ext.MessageBox.INFO,
                        fn : function (response) {
                            if (response === 'ok') {
                                sitools.userProfile.LoginUtils.connect({
                                    url : loadUrl.get('APP_URL') + loadUrl.get('APP_LOGIN_PATH_URL') + '/login',
                                    register : loadUrl.get('APP_URL') + '/inscriptions/user',
                                    reset : loadUrl.get('APP_URL') + '/lostPassword',
                                    unblacklist : loadUrl.get('APP_URL') + '/unblacklist'
                                });
                            }
                        }
                    });
                }
                else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProjectError'));
                }
            }            
        });
    },    
    initPreferences : function (callback) {
        if (!Ext.isEmpty(userLogin)) {
            var filePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + projectGlobal.projectName;
            var fileName = "desktop";
            var success = function (ret) {
                try {
                    this.preferences = Ext.decode(ret.responseText);
                    Ext.callback(callback);
                } catch (err) {
                    Ext.callback(callback);
                }
            };
            
            var failure = function (ret) {
                this.getPublicPreferences(callback);
            };
            
            userStorage.get(fileName, filePath, this, success, failure);
        } else {
            this.getPublicPreferences(callback);
        }
    }, 
    initPublicPreferences : function (callback) {
        var AppPublicStorage = loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files";
        Ext.Ajax.request({
//                    url : "/sitools/userstorage/" + userLogin + "/" + DEFAULT_PREFERENCES_FOLDER + "/" + this.projectName + "/desktop?media=json",
            url : loadUrl.get('APP_URL') + AppPublicStorage + "/" + DEFAULT_PREFERENCES_FOLDER + "/" + this.projectName + "/desktop?media=json",
            method : 'GET',
            scope : this,
            success : function (ret) {
                try {
                    this.preferences = Ext.decode(ret.responseText);
                } catch (err) {
                    this.preferences = null;
                }
            }, 
            callback : callback
        });
    }
});

Project = sitools.user.utils.Project;