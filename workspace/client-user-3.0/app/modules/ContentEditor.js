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

Ext.namespace('sitools.user.modules');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.projectDescription
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.ContentEditor', {
    extend : 'sitools.user.core.Module',
    
    controllers : ['sitools.user.controller.modules.contentEditor.ContentEditorController',
                   'sitools.user.controller.modules.dataStorageExplorer.DataStorageExplorerBrowserController'],
    
    init : function () {
    	
        var contentEditorView = Ext.create('sitools.user.view.modules.contentEditor.ContentEditorView', {
            moduleModel : this.getModuleModel()
        });
        this.show(contentEditorView);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.itemId
        };

    },
    
});

sitools.user.modules.ContentEditor.getParameters = function () {
	return [{
		jsObj : "Ext.form.ComboBox", 
		config : {
			fieldLabel : i18n.get("label.urlDatastorage"),
			allowBlank : false,
			typeAhead : true,
			editable : false,
			triggerAction : 'all',
			width : 200,
			valueField : 'attachUrl',
			displayField : 'name',
			store : Ext.create('Ext.data.JsonStore', {
				proxy : {
					type : 'ajax',
					url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories',
					reader : {
						type : 'json',
						root : 'data'
					}
				},
				remoteSort : true,
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'string'
				}, {
					name : 'name',
					type : 'string'
				}, {
					name : 'attachUrl',
					type : 'string'
				}]
			}),
			listeners: {
				render : function (c) {
					Ext.QuickTips.register({
						target : c,
						text : "the datastorage url (cf. Storage)"
					});
				},
				select : function (combo, recs, ind) {
					var dsName = this.up('form').down('#nameDatastorageId');
					dsName.setValue(recs[0].get('name'));
				}
			},
			name : "dynamicUrlDatastorage",
			value : undefined
		}
	}, {
		jsObj : "Ext.form.TextField",
		config : {
			itemId : "nameDatastorageId",
			fieldLabel : i18n.get("label.nameDatastorage"),
			allowBlank : true,
			hidden : true,
			width : 200,
			listeners : {
				render : function (c) {
					Ext.QuickTips.register({
						target : c,
						text : "the label NAME of the datastorage to display (cf. Storage)"
					});
				}
			},
			name : "nameDatastorage",
			value : undefined
		}
	} ];
};
