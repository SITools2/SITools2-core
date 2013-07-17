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
/* global Ext, sitools, ID, i18n, showResponse, alertFailure,clog,window,Base64 */
Ext.namespace('sitools.userProfile');

/*
 * defurl: default page url to load if click on Cancel button url: url to
 * request if click on Login button handler: if request is OK then is called
 * register: url to set to Register button reset: url to set to Reset Password
 * button
 */

sitools.userProfile.LoginUtils = {

    connect : function (config) {
        var url = loadUrl.get('APP_URL') + "/login-details";
        Ext.Ajax.request({
            method : "GET",
            url : url,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    var data = Json.data;
                    var delegateLogin = false;
                    var delegateLoginUrl = null;

                    Ext.each(data, function (property) {
                        if (property.name === "Starter.SECURITY.DELEGATE_LOGIN") {
                            delegateLogin = (property.value === "true");
                        }
                        if (property.name === "Starter.SECURITY.DELEGATE_LOGIN_URL") {
                            delegateLoginUrl = property.value;
                        }
                    });

                    if (delegateLogin) {
                        if (Ext.isEmpty(delegateLoginUrl)) {
                            Ext.Msg.alert(i18n.get("label.warning"), "No Logout url defined");
                            return;
                        }
                        sitools.userProfile.LoginUtils.delegateLoginLogout(delegateLoginUrl);
                    } else {
                        sitools.userProfile.LoginUtils.sitoolsLogin(config);
                    }

                } else {
                    // if the parameters are not available perform classic login
                    sitools.userProfile.LoginUtils.sitoolsLogin(config);
                }
            },
            failure : function () {
                // if the parameters are not available perform classic login
                sitools.userProfile.LoginUtils.sitoolsLogin(config);
            }

        });
    },

    logout : function () {
        var url = loadUrl.get('APP_URL') + "/login-details";
        Ext.Ajax.request({
            method : "GET",
            url : url,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    var data = Json.data;
                    var delegateLogout = false;
                    var delegateLogoutUrl = null;

                    Ext.each(data, function (property) {
                        if (property.name === "Starter.SECURITY.DELEGATE_LOGOUT") {
                            delegateLogout = (property.value === "true");
                        }
                        if (property.name === "Starter.SECURITY.DELEGATE_LOGOUT_URL") {
                            delegateLogoutUrl = property.value;
                        }
                    });
                        
                    utils_logout(!delegateLogout);
                    if (delegateLogout) {
                        if (Ext.isEmpty(delegateLogoutUrl)) {
                            Ext.Msg.alert(i18n.get("label.warning"), "No Logout url defined");
                            return;
                        }
                        sitools.userProfile.LoginUtils.delegateLoginLogout(delegateLogoutUrl);
                    }

                } else {
                    // if the parameters are not available perform classic
                    // logout
                    utils_logout(true);
                }
            },
            failure : function () {
                // if the parameters are not available perform classic logout
                utils_logout(true);
            }

        });

    },
    /**
     * 
     * @param config
     */
    editProfile : function (callback) {
        var url = loadUrl.get('APP_URL') + "/login-details";
        Ext.Ajax.request({
            method : "GET",
            url : url,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    var data = Json.data;
                    var delegateUserManagment = false;
                    var delegateUserManagmentUrl = null;

                    Ext.each(data, function (property) {
                        if (property.name === "Starter.SECURITY.DELEGATE_USER_MANAGMENT") {
                            delegateUserManagment = (property.value === "true");
                        }
                        if (property.name === "Starter.SECURITY.DELEGATE_USER_MANAGMENT_URL") {
                            delegateUserManagmentUrl = property.value;
                        }
                    });
                    
                    if (delegateUserManagment) {
                        if (Ext.isEmpty(delegateUserManagmentUrl)) {
                            Ext.Msg.alert(i18n.get("label.warning"), "No user managment url defined");
                            return;
                        }
                        sitools.userProfile.LoginUtils.delegateLoginLogout(delegateUserManagmentUrl);
                    } else {
                        // if the parameters are not available perform classic
                        // user managment
                        callback.call();
                    }

                } else {
                    // if the parameters are not available perform classic
                    // user managment
                    callback.call();
                }
            },
            failure : function () {
                // if the parameters are not available perform classic logout
                callback.call();
            }

        });

    },

    sitoolsLogin : function (config) {
        new sitools.userProfile.Login(config).show();
    },

    delegateLoginLogout : function (urlTemplate) {
        var url = urlTemplate.replace("{goto}", document.URL);
        window.open(url, "_self");
    },
    
    

};
