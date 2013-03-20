/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, MULTIDS_TIME_DELAY, sitools, i18n, commonTreeUtils, projectGlobal, showResponse, document, SitoolsDesk, alertFailure, loadUrl*/
/*
 * @include "../../components/forms/forms.js"  
 */

Ext.namespace('sitools.user.component.forms');

/**
 * Displays The result of a multiDatasets Reasearch. 
 * @cfg {string} urlTask The url to request 
 * @cfg {string} formId The form Id
 * @cfg {string} formName The form Name
 * @cfg {Array} formMultiDsParams an array of formParams (represent concepts selection)
 * @cfg {Array} datasets Array of Datasets Ids
 * @class sitools.user.component.forms.resultsProjectForm
 * @extends Ext.grid.GridPanel
 */
sitools.user.component.forms.resultsProjectForm = Ext.extend(Ext.grid.GridPanel, {
    initComponent : function () {
        var params = {};
        params.datasetsList = this.datasets.join("|");

	    var i = 0;
	    
	    if (!Ext.isEmpty(this.formMultiDsParams)) {
	        Ext.each(this.formMultiDsParams, function (param) {
	            params["c[" + i + "]"] = param;
	            i += 1;
	        }, this);
	    }
        var task = new Ext.util.DelayedTask(function () {
			return;
		});
        var store = new Ext.data.JsonStore({
            url : this.urlTask,
            baseParams : params,
            restful : true, 
            root : 'TaskModel.properties',
			fields :  [{
				name : "id", 
				type : "string"
			}, {
				name : "name", 
				type : "string"
			}, {
				name : "description", 
				type : "string"
			}, {
				name : "image"
			}, {
				name : "nbRecord"
			}, {
				name : "url", 
				type : "string"
			}, {
				name : "status", 
				type : "string"
			}, {
				name : "errorMessage", 
				type : "string"
			}], 
			autoLoad : true, 
			listeners : {
				scope : this, 
				load : function (store, recs, options) {
					task.cancel();
					if (store.reader.jsonData.TaskModel.status == "TASK_STATUS_RUNNING" ||
						store.reader.jsonData.TaskModel.status == "TASK_STATUS_PENDING") {
						this.getBottomToolbar().setStatus({
		                    // text: ret.error ? ret.error :
		                    // i18n.get('warning.serverUnreachable'),
		                    text : i18n.get('label.loading'),
		                    iconCls : 'x-status-busy'
		                });
                    
		                task.delay(MULTIDS_TIME_DELAY, function () {
							store.load();
						});
					}
					else {
						Ext.Ajax.request({
							scope : this, 
							url : this.urlTask, 
							method : "DELETE", 
							success : function (ret) {
								var callerCmp = Ext.getCmp(this.callerId);
								callerCmp.fireEvent("multiDsSearchDone");
							},
							failure : alertFailure
						});
						if (store.reader.jsonData.TaskModel.status == "TASK_STATUS_FAILURE") {
							this.getBottomToolbar().setStatus({
			                    text : store.reader.jsonData.TaskModel.customStatus,
			                    iconCls : 'x-status-error'
			                });
						}
						else {
						    this.getBottomToolbar().setStatus({
								text : i18n.get("label.requestDone"),
			                    iconCls : 'x-status-valid'
			                });
						}
					}
                    store.each(function (record) {
                        var error = record.get("errorMessage");
                        if (!Ext.isEmpty(error)) {
                            var index = store.indexOf(record);
                            var htmlLineEl = this.getView().getRow(index);
                            var el = Ext.get(htmlLineEl);
                            
                            var cls = "x-form-invalid-tip";
                            
                            var ttConfig = {
                                html : error,
                                dismissDelay : 0,
                                target : el,
                                cls : cls
                            };
    
                            var ttip = new Ext.ToolTip(ttConfig);
                        }
                    }, this);					

				}
			}
        
        });
        
        var cm = new Ext.grid.ColumnModel({
			columns : [{
				width : 25, 
				dataIndex : 'image', 
				header : "", 
				renderer : function (value) {
					return ! Ext.isEmpty(value) && ! Ext.isEmpty(value.url) ? String.format("<img src='{0}' width=20 height=20>", value.url) : "";
				}
			}, {
				width : 100, 
				dataIndex : 'name', 
				header : i18n.get('label.name')
			}, {
				width : 100, 
				dataIndex : 'nbRecord', 
				header : i18n.get('label.nbRecords')
			}, {
                width : 150, 
                dataIndex : 'description', 
                header : i18n.get('label.description'),
                renderer : function (value, metaData, record, rowIndex, colIndex, store) {
					metaData.attr = "ext:qtip='" + value + "'";
					return value;

				}
            }, {
		        xtype: 'actioncolumn',
                header : i18n.get('label.showData'),
		        width: 100,
                
		        items: [{
	                getClass : function (value, meta, rec) {
						if (rec.get('status') == "REQUEST_ERROR") {
							return "multids-error";
						}
	                }, 
	                icon   : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_datasets.png',                // Use a URL in the icon config
	                tooltip: i18n.get('label.showData'),
	                scope : this, 
	                handler: SitoolsDesk.navProfile.multiDataset.showDataset
	            }]
			}, {
		        xtype: 'actioncolumn',
                header : i18n.get('label.showDefinition'),
		        width: 100,
		        items: [{
	                icon   : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_dictionary.png',
	                tooltip: i18n.get('label.showDefinition'),
	                scope : this, 
	                handler: function (grid, rowIndex, colIndex) {
	                    var rec = grid.getStore().getAt(rowIndex);
	                    if (Ext.isEmpty(rec)) {
							return;
	                    }
	                    sitools.user.clickDatasetIcone(rec.get("url"), "defi", {
							formMultiDsParams : this.formMultiDsParams
	                    });
	                }
	            }]
			}]
        });
        var bbar = new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid'
        });

		Ext.apply(this, {
			cm : cm, 
			store : store, 
			layout : "fit", 
			bbar : bbar, 
			listeners : {
				scope : this, 
				viewready : function (grid) {
					var callerCmp = Ext.getCmp(this.callerId);
					callerCmp.fireEvent("multiDsSeachDone");
				}
			}, 
			viewConfig : {
				forceFit : true, 
				getRowClass : function (rec) {
					if (rec.get('status') == "REQUEST_ERROR") {
						return "red-row";
					}
					if (rec.get('status') == "UNAUTHORIZED") {
						return "orange-row";
					}
				}
			}
		});
		sitools.user.component.forms.resultsProjectForm.superclass.initComponent.call(this);
    } 
    

});

Ext.reg('sitools.user.component.forms.resultsProjectForm', sitools.user.component.forms.resultsProjectForm);

