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
/*global Ext*/
Ext.namespace('sitools.public.utils');

var DEFAULT_LOCALE = "en";

Ext.define('sitools.public.utils.Locale', {
    singleton : true,
    
    config : {
        languages : null,
        locale : DEFAULT_LOCALE
    },
   
    load : function (url, callback, scope) {
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            success : function (response) {
                var json = Ext.decode(response.responseText);
                locale.setLanguages(json.data);
                Project.setLanguages(json.data)
                
                Ext.callback(callback, scope);
            },
            failure : function (response, opts) {
                Ext.Msg.alert("Error! Can't read languages file with url :" + url);
            }
        });

    },
    
    initLocale : function () {
        if (Ext.isEmpty(Ext.util.Cookies.get('language'))) {
            var navigator = window.navigator;
            this.setLocale(navigator.language || navigator.browserLanguage || navigator.userLanguage);
        }
        else {
            this.setLocale(Ext.util.Cookies.get('language'));
        }
    },
    
    restoreDefault : function () {
        this.setLocale(DEFAULT_LOCALE);
    }
   
});

locale = sitools.public.utils.Locale;
