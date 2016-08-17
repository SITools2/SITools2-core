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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse*/

Ext.namespace('sitools.user.modules');
/**
 * ProjectDescription Module
 * @class sitools.user.view.modules.contentViewer.ContentViewer
 * @extends sitools.user.core.Module
 */
Ext.define('sitools.user.modules.ContentViewerModule', {
    extend : 'sitools.user.core.Module',
    
    controllers : ['sitools.user.controller.modules.contentViewer.ContentViewerController'],

	statics : {
		getParameters : function () {
			return [{
				jsObj : "Ext.form.ComboBox",
				config : {
					fieldLabel : i18n.get("label.urlDatastorage"),
					allowBlank : false,
					typeAhead : true,
					editable : false,
					triggerAction : 'all',
					width : 200,
					valueField : 'name',
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
							var urlAttachField = this.up('form').down('#urlDatastorageId');
							urlAttachField.setValue(recs[0].data.attachUrl);
						}
					},
					name : "nameDatastorageSrc",
					value : ""
				}
			}, {
				jsObj : "Ext.form.TextField",
				config : {
					fieldLabel : i18n.get("label.urlDatastorage"),
					allowBlank : false,
					id : "urlDatastorageId",
					hidden : true,
					name : "dynamicUrlDatastorage",
					value : ""
				}
			}];
		}
	},

    init : function () {
    	
        var contentViewerView = Ext.create('sitools.user.view.modules.contentViewer.ContentViewerView', {
            moduleModel : this.getModuleModel()
        });
        this.show(contentViewerView);
    },
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }

});