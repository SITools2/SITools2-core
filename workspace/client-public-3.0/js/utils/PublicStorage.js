/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.public.utils');

Ext.define('sitools.public.utils.PublicStorage', {
    singleton : true,
    
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
                    popupMessage(i18n.get('label.information'), Json.message, null, 'x-icon-information');;
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
                popupMessage(i18n.get('label.information'), i18n.get("label.publicUserPrefDeleted"), null, 'x-icon-information');;
            },
            failure : function (ret) {
                //cas normal... 
				if (ret.status === 404) {
                    popupMessage(i18n.get('label.information'), i18n.get("label.publicUserPrefDeleted"), null, 'x-icon-information');;
				}
				else {
                    popupMessage(i18n.get('label.error'), ret.responseText, null, 'x-icon-error');;
				}
                
            }
        });
    }
});


PublicStorage = sitools.public.utils.PublicStorage;