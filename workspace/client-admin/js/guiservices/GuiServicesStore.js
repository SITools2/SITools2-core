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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.guiServices');

Ext.define('sitools.admin.guiservices.GuiServicesStore', { 
    extend : 'Ext.data.JsonStore',
    
    constructor : function (config) {
        
        Ext.apply(config, {
            pageSize : config.pageSize,
            proxy : {
                type : 'ajax',
                url : config.url,
                simpleSortMode : true,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            remoteSort : true,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
                name : 'author',
                type : 'string'
            }, {
                name : 'version',
                type : 'string'
            }, {
                name : 'icon',
                type : 'string'
            }, {
                name : 'xtype',
                type : 'string'
            }, {
                name : 'priority',
                type : 'string'
            }, {
                name : 'dependencies'
            }, {
                name : 'dataSetSelection'
            }, {
                name : 'defaultGuiService',
                type : 'boolean'

            }, {
                name : 'defaultVisibility',
                type : 'boolean'

            }]        
        });
        
        sitools.admin.guiservices.GuiServicesStore.superclass.constructor.call(this, config);
    },
     

    saveRecord : function (rec, action) {
        var met = action === 'modify' ? 'PUT' : 'POST';
        var url = this.url + ((action === 'modify') ? '/' + rec.id : "");

        Ext.Ajax.request({
            url : url,
            method : met,
            scope : this,
            jsonData : rec,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    return false;
                }
                this.reload();
            },
            failure : alertFailure
        });
    },
     

    updateRecord : function (rec) {
        this.saveRecord(rec, "modify");
    },

    deleteRecord : function (rec) {
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.remove(rec);
                }
            },
            failure : alertFailure
        });
    }
     
});


