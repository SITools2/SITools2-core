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
var loginErrLength = 'Login minimum 4 character !';
var loginErrUnique = 'Login already in use !';
var loginSuccess = 'Login avaliable';
var emailErrFormat = 'Email not valid !';
var emailErrUnique = 'Email already in use !';
var emailSuccess = 'Email valid & avaliable';

Ext.apply(Ext.form.VTypes, {
    uniqueloginMask : /[a-z0-9_\.\-@\+]/i,
	uniquelogin : function(val) {
        if (val.length < 4) {
            Ext.apply(Ext.form.VTypes, {
                uniqueloginText: loginErrLength
            });
            return false;
        } else {
            /*Ext.Ajax.request({
                url: 'user/ext_is_unique_login',
                method: 'POST',
                params: 'login=' + val,
                success: function(o) {
                    if (o.responseText == 0) {
                        resetLoginValidator(false);
                        Ext.apply(Ext.form.VTypes, {
                            uniqueloginText: loginErrUnique
                        });
                        return false;
                    } else {
                        resetLoginValidator(true);
                    }
                }
            });*/
            return true;
        }
	},
	uniqueloginText : loginErrUnique,

    uniqueemailMask : /[a-z0-9_\.\-@\+]/i,
    uniqueemail : function(val) {
        var uniqueemail = /^(\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6}$/;
        if (uniqueemail.test(val)) {
        	/*
            Ext.Ajax.request({
                url: BASE_URL + 'user/ext_is_unique_email',
                method: 'POST',
                params: 'email=' + val,
                success: function(o) {
                    if (o.responseText == 0) {
                        resetEmailValidator(false);
                        Ext.apply(Ext.form.VTypes, {
                            uniqueemailText: emailErrUnique
                        });
                    } else {
                        resetEmailValidator(true);
                    }
                }
            });*/
            return true;
        } else {
            return false;
        }

    },
    uniqueemailText : emailErrFormat,

    password : function(val, field) {
        if (field.initialPassField) {
            var pwd = Ext.getCmp(field.initialPassField);
            return (val == pwd.getValue());
        }
        return true;
    },
    passwordText : 'Passwords do not match',

    passwordlength : function(val) {
        if (val.length < 6 || val.length > 40) {
            return false;
        } else {
            return true;
        }
    },
    passwordlengthText : 'Invalid Password Length. It must be between 6 and 40'
});

function resetLoginValidator(is_error) {
	Ext.apply(Ext.form.VTypes, {
		uniquelogin : function(val) {
            if (val.length < 4) {
                Ext.apply(Ext.form.VTypes, {
                    uniqueloginText: loginErrLength
                });
                return false;
            } else {
            	/*
                Ext.Ajax.request({
                    url: 'user/ext_is_unique_login',
                    method: 'POST',
                    params: 'login=' + val,
                    success: function(o) {
                        if (o.responseText == 0) {
                            resetLoginValidator(false);
                        } else {
                            resetLoginValidator(true);
                        }
                    }
                });
                return is_error;
                */return true;
            }
		}
	});
}

function resetEmailValidator(value) {
    Ext.apply(Ext.form.VTypes, {
        uniqueemail : function(val) {
            var uniqueemail = /^(\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6}$/;
            if (uniqueemail.test(val)) {
                /*Ext.Ajax.request({
                    url: BASE_URL + 'user/ext_is_unique_email',
                    method: 'POST',
                    params: 'email=' + val,
                    success: function(o) {
                        if (o.responseText == 0) {
                            resetEmailValidator(false);
                            Ext.apply(Ext.form.VTypes, {
                                uniqueemailText: emailErrUnique
                            });
                        } else {
                            resetEmailValidator(true);
                        }
                    }
                });*/return true;
            } else {
                return false;
            }
            return (value);
        }
    });
}
