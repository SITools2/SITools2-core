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
/*global Ext, ann, alert, document, alertFailure, getDesktop, SitoolsDesk, locale, portal */
/*global DEFAULT_WIN_HEIGHT, DEFAULT_WIN_WIDTH, sitools, loadUrl, includeJs, DEFAULT_PREFERENCES_FOLDER */

/*
 * @include "desktop/desktop.js"
 * @include "components/columnsDefinition/dependencies/columnsDefinition.js"
 * @include "components/forms/forms.js"
 */

Ext.namespace('sitools.env');
var userPreferences = null;
var userLogin = null;
var sql2ext = {

    map : [],
    load : function (url) {

        var i18nRef = this;
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            // params:'formLogin', using autorization instead
            success : function (response, opts) {
                ann(response.responseText, "no response is sent");
                i18nRef.map = i18nRef.transformsPropertiesToMap(response.responseText);
            },
            failure : function (response, opts) {
                alert("Error! Can't read i18n file with url :" + url);
            }
        });

    },
    /**
     * Transforms a properties Text to a map
     * 
     * @param text
     *            raw properties file
     * @returns a map (associative array) TODO check when the raw properties
     */
    transformsPropertiesToMap : function (text) {
        var array = text.split('\n');
        var localMap = [];
        var i;
        for (i = 0; i < array.length; i++) {
            var string = array[i];
            var indexOfEqualsSign = string.indexOf('=');
            if (indexOfEqualsSign >= 1) {
                var key = string.substring(0, indexOfEqualsSign).replace('\r', '');
                var value = string.substring(indexOfEqualsSign + 1).replace('\r', '');
                localMap[key] = value;
            }
        }
        return localMap;
    },
    /**
     * return the i18n value
     * 
     * @param name
     * @returns
     */
    get : function (entry) {
        return !Ext.isEmpty(this.map[entry]) ? this.map[entry] : 'auto';
    }
};
var i18n = {

    map : [],
    /**
     * Load a properties file and and the name/values in a associative array ;
     * Executing this function on multiple properties file increase the size of
     * the array Results can be displayed in the help panel with the display()
     * function
     * 
     * @param url
     *            URL of the i18n file
     * @param callback
     *            No args function that will be executed
     * @returns void
     */
    load : function (url, callback, loopOnFailure) {
        var i18nRef = this;
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            loopOnFailure : (Ext.isEmpty(loopOnFailure)) ? true : loopOnFailure,
            // params:'formLogin', using autorization instead
            success : function (response, opts) {
                ann(response.responseText, "no response is sent");
                i18nRef.map = i18nRef.transformsPropertiesToMap(response.responseText);
                if (Ext.isFunction(callback)) {
                    callback();
                }
            },
            failure : function (response, opts) {
                if (!opts.loopOnFailure) {
                    Ext.Msg.alert("Error! Can't read i18n file with url :" + url);
                } else {
                    locale.restoreDefault();
                    url = '/sitools/res/i18n/' + locale.getLocale() + '/gui.properties';
                    i18n.load(url, callback, false);
                }
            }
        });

    },
    /**
     * Transforms a properties Text to a map
     * 
     * @param text
     *            raw properties file
     * @returns a map (associative array) TODO check when the raw properties
     *          file is rotten
     */
    transformsPropertiesToMap : function (text) {
        var array = text.split('\n');
        var localMap = [];
        var i;
        for (i = 0; i < array.length; i++) {
            var string = array[i];
            var indexOfEqualsSign = string.indexOf('=');
            if (indexOfEqualsSign >= 1) {
                var key = string.substring(0, indexOfEqualsSign).replace('\r', '');
                var value = string.substring(indexOfEqualsSign + 1).replace('\r', '');
                localMap[key] = value;
            }
        }
        return localMap;
    },
    /**
     * return the i18n value
     * 
     * @param name
     * @returns
     */
    get : function (entry) {
        return !Ext.isEmpty(this.map[entry]) ? this.map[entry] : entry;
    }
};

/**
 * To be defined
 */
var componentManager = {
    loadedComponents : [],
    load : function (name) {

    }
};

var data = {
    ret : null,
    /**
     * Fetch a html file in the url, and display its content into the helpPanel. *
     * 
     * @param url
     * @returns
     */
    get : function (url, cbk) {
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            success : function (response, opts) {
                cbk(Ext.decode(response.responseText));
            },
            failure : function (response, opts) {
                Ext.Msg.alert("Warning", "Error! Can't get data with url :" + url);
            }
        });
        return this.ret;
    }

};
userLogin = Ext.util.Cookies.get('userLogin');
var userStorage = {
	set : function (filename, filepath, content, callback, scope) {
	    userStorage.setData(filename, filepath, content, callback, scope, "json");
    },
    setXML : function (filename, filepath, content, callback, scope) {
        userStorage.setData(filename, filepath, content, callback, scope, "xml");
    },
    //private
    setData : function (filename, filepath, content, callback, scope, type) {
        var config = {
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files",
                method : 'POST',
                scope : scope,
                params : {
                    filepath : filepath,
                    filename : filename
                },
                jsonData : content,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        return;
                    } else {
                        var notify = new Ext.ux.Notification({
                            iconCls : 'x-icon-information',
                            title : i18n.get('label.information'),
                            html : Json.message,
                            autoDestroy : true,
                            hideDelay : 1000
                        });
                        notify.show(document);
                    }
                },
                failure : function () {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.warning.savepreference.error'));
                    return;
                },
                callback : callback
            };
        

        if (type === "xml") {
            config.xmlData = content;
        } else {
            config.jsonData = content;
        }
        
        Ext.Ajax.request(config);
    },
    get : function (fileName, filePath, scope, success, failure, callback) {
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files" + filePath + "/" + fileName,
            method : 'GET',
            scope : scope,
            success : success,
            failure : failure, 
            callback : callback
        });
    }
};

/**
 * Global project variable Used to get the projectId from the url
 */
var projectGlobal = {
    /**
     * Get the current projectId from the url url is like :
     * /sitools/client-user/{projectName}/indexproject.html /sitools/client-user/
     * can be changed
     * 
     * @return the projectId
     */
    projectId : null,
    projectName : null,
    preferences : null,
    userRoles : null, 
    isAdmin : false,
    sitoolsAttachementForUsers : null,
    modules : null,
    links : null,
    callback : Ext.emptyFn,

    initProject : function (callback) {
        this.callback = callback;
        this.projectName = this.getProjectName();
        this.getProjectInfo();
    },

    // only load datasetView used by datasets in the current project
    getDataViewsDependencies : function () {
		Ext.Ajax.request({
            url : this.sitoolsAttachementForUsers + "/datasetViews",
            method : "GET",
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.errorloadingdataviews'));
                    return false;
                } else {
                    var data = json.data;                    
                    Ext.each(data, function (datasetViewComponent) {
                    	if (!Ext.isEmpty(datasetViewComponent.dependencies) && !Ext.isEmpty(datasetViewComponent.dependencies.js)) {
                            Ext.each(datasetViewComponent.dependencies.js, function (dependencies) {
                                includeJs(dependencies.url);
                            }, this);
                        }
                        if (!Ext.isEmpty(datasetViewComponent.dependencies) && !Ext.isEmpty(datasetViewComponent.dependencies.css)) {
							Ext.each(datasetViewComponent.dependencies.css, function (dependencies) {
								includeCss(dependencies.url);
							}, this);
						}
                    });
                    
                }
            },
            callback : function () {
                this.getFormDependencies();
            }
        });   
    }, 
    getFormDependencies : function () {
		Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_FORMCOMPONENTS_URL'),
            method : "GET",
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.errorLoadingFormDependencies'));
                    return false;
                } else {
                    var data = json.data;                    
                    Ext.each(data, function (formComponent) {
						includeJs(formComponent.fileUrlUser);
                    });
                }
            },
            callback : function () {
                this.getGUIServicesDependencies();
            }
        });   
    },
    getGUIServicesDependencies : function () {
        Ext.Ajax.request({
            url : this.sitoolsAttachementForUsers + "/guiServices",
            method : "GET",
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.errorLoadingGuiServicesDependencies'));
                    return false;
                } else {
                    var data = json.data;
                    var javascriptDependencies = []
                        Ext.each(data, function (service) {
                            if (!Ext.isEmpty(service.dependencies.js)) {
                                javascriptDependencies = javascriptDependencies.concat(service.dependencies.js);
                            }
                            if (!Ext.isEmpty(service.dependencies.css)) {
                                Ext.each(service.dependencies.css, function (dependencies) {
                                    includeCss(dependencies.url);
                                }, this);
                            }
                        }, this);
                    includeJsForceOrder(javascriptDependencies, 0, this.initLanguages, this);
                }
            }
        });   
    },
    initLanguages : function () {
        Ext.Ajax.request({
            scope : this,
            method : "GET",
            /* /sitools/client-user */
//            url : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + '/tmp/langues.json',
            url : loadUrl.get('APP_URL') + '/client-user/tmp/langues.json',
            success : function (response) {
                var json = Ext.decode(response.responseText);
	            this.languages = json.data;
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
    getProjectName : function () {
        if (this.projectName === null) {
            // get the relative url
            var url = document.location.pathname;
            // split the url to get each part of the url in a tab cell
            var tabUrl = url.split("/");

            var i = 0, index;
            var found = false;
            // search for index.html, the projectName is right before
            // '/index.html'
            while (i < tabUrl.length && !found) {
                if (tabUrl[i] === "project-index.html") {
                    found = true;
                    index = i;
                }
                i++;
            }
            // get the projectName from the tabUrl
            this.projectName = tabUrl[index - 1];

            if (this.projectName === undefined || this.projectName === "") {
                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProject'));
            }
        }
        return this.projectName;
    },
    /**
     * Get the name of a project from the server
     */
    getProjectInfo : function () {
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
                }
//                var topEl = Ext.get('toppanel');
//                topEl.update(Ext.util.Format.htmlDecode(data.project.htmlHeader));
            },
            callback : function (options, success, response) {
                if (success) {
                    this.getDataViewsDependencies();
                }
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
    getPreferences : function (callback) {
        if (!Ext.isEmpty(userLogin)) {
            var filePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + projectGlobal.projectName;
			var fileName = "desktop";
			var success = function (ret) {
                try {
                    this.preferences = Ext.decode(ret.responseText);
	                callback.call();
                } catch (err) {
	                callback.call();
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
    getPublicPreferences : function (callback) {
        var AppPublicStorage = loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files";
        Ext.Ajax.request({
//                url : "/sitools/userstorage/" + userLogin + "/" + DEFAULT_PREFERENCES_FOLDER + "/" + this.projectName + "/desktop?media=json",
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
};

var publicStorage = {
    set : function (filename, filepath, content, callback) {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files";
        Ext.Ajax.request({
            url : this.url,
            method : 'POST',
            scope : this,
            params : {
                filepath : filepath,
                filename : filename
            },
            jsonData : content,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                } else {
                    var notify = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : Json.message,
                        autoDestroy : true,
                        hideDelay : 1000
                    });
                    notify.show(document);
                }
            },
            failure : function () {
                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.warning.savepreference.error'));
                return;
            },
            callback : function () {
                if (!Ext.isEmpty(callback)) {
                    callback.call();
                }
            }
        });

    },
    get : function (fileName, filePath, scope, success, failure) {
        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files" + filePath + "/" + fileName,
            method : 'GET',
            scope : scope,
            success : success,
            failure : failure
        });
    }, 
    remove : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_PUBLIC_STORAGE_URL') + "/files" + "?recursive=true";
        Ext.Ajax.request({
            url : this.url,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var notify = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get("label.publicUserPrefDeleted"),
                    autoDestroy : true,
                    hideDelay : 1000
                });
                notify.show(document);
            },
            failure : function (ret) {
                //cas normal... 
				if (ret.status === 404) {
					var notify = new Ext.ux.Notification({
				        iconCls : 'x-icon-information',
				        title : i18n.get('label.information'),
				        html : i18n.get("label.publicUserPrefDeleted"),
				        autoDestroy : true,
				        hideDelay : 1000
				    });
				    notify.show(document);
				}
				else {
					var notifye = new Ext.ux.Notification({
				        iconCls : 'x-icon-error',
				        title : i18n.get('label.error'),
				        html : ret.responseText,
				        autoDestroy : true,
				        hideDelay : 1000
				    });
				    notifye.show(document);
				}
                
            }
        });
    }
};

function showResponse(ret, notification) {
    try {
        var Json = Ext.decode(ret.responseText);
        if (!Json.success) {
            Ext.Msg.alert(i18n.get('label.warning'), Json.message);
            return false;
        }
        if (notification) {
            var notify = new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : Json.message,
                autoDestroy : true,
                hideDelay : 1000
            });
            notify.show(document);
        }
        return true;
    } catch (err) {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.javascriptError') + " : " + err);
        return false;
    }
};
