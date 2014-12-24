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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.view.modules.projectService');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.projectServices
 * @extends Ext.grid.GridPanel
 */

Ext.define('sitools.user.view.modules.projectService.ProjectServiceView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.projectService',

	layout : 'fit',
	border : false,
	bodyBorder : false,
	
	initComponent : function () {
		this.url = Project.sitoolsAttachementForUsers + "/services";
		
		this.store = Ext.create('Ext.data.JsonStore', {
			autoLoad : true,
			proxy :  {
				type : 'ajax',
				url : this.url,
				reader : {
					idProperty : 'id',
					type : 'json',
					root : 'data'
				}
			},
			fields : [ {
				name : 'id',
				type : 'string'
			}, {
				name : 'name',
				type : 'string'
			}, {
				name : 'descriptionAction',
				type : 'string'
			}, {
				name : 'description',
				type : 'string'
			}, {
				name : 'parameters'
			}, {
				name : 'dataSetSelection',
				type : 'string'
			}, {
				name : 'behavior',
				type : 'string'
			}, {
				name : 'image',
				type : 'string'
			} ],
			listeners : {
				scope : this,
				load : function (store, records, options) {
					Ext.each(records, function (record) {
						var parameters = record.get("parameters");
						if (!Ext.isEmpty(parameters)) {
							Ext.each(parameters, function (parameter) {
								if (parameter.name === "image") {
									record.set("image", parameter.value);
									return;
								}
							}, this);
						}
					}, this);
				}
			}
		});
		
		this.columns = {
			defaults : {
				sortable : true
			},
			items : [ {
				header : i18n.get('label.icon'),
				dataIndex : 'image',
				width : 50,
				sortable : false,
				renderer : function (value, metadata, record, rowIndex, colIndex, store) {
					if (!Ext.isEmpty(value)) {
						value = '<img src="' + value + '" height=15 width=18 style="margin:auto; display: block;"/>';
					}
					return value;
				}
			}, {
				header : i18n.get('label.name'),
				dataIndex : 'name',
				width : 150
			}, {
				header : i18n.get('label.description'),
				dataIndex : 'description',
				width : 200,
				sortable : false
			}, {
				header : i18n.get('label.descriptionAction'),
				dataIndex : 'descriptionAction',
				width : 200,
				sortable : false
			}, {
				xtype : 'actioncolumn',
				width : 30,
				items : [{
					name : 'actionButton',
					icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_application_resource.png",
					tooltip : i18n.get('label.runService')
				}]
			}]
		};
		
		this.grid = Ext.create('Ext.grid.Panel', {
			store : this.store,
			columns : this.columns, 
			forceFit : true
		});
		
		this.serviceServerUtil = Ext.create('sitools.user.utils.ServerServiceUtils', {
			grid : this.grid, 
			datasetUrl : Project.sitoolsAttachementForUsers, 
			datasetId : Project.projectId, 
			origin : "sitools.user.modules.ProjectService"
		});
		
		this.items = [this.grid];
		
		this.callParent(arguments);
	},
	
	getNbRowsSelected : function () {
		return;
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

