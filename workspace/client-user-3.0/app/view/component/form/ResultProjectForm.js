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
/*global Ext, MULTIDS_TIME_DELAY, sitools, i18n, commonTreeUtils, projectGlobal, showResponse, document, SitoolsDesk, alertFailure, loadUrl*/
/*
 * @include "../../components/forms/forms.js"  
 */

Ext.namespace('sitools.user.view.component.form');

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
Ext.define('sitools.user.view.component.form.ResultProjectForm', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.resultProjectForm',
    
    forceFit : true,
    layout : 'fit',
	bodyBorder : false,
	border : false,

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
        
        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
            	type : 'ajax',
            	url : this.urlTask,
            	reader : {
            		type : 'json',
            		root : 'TaskModel.properties'
            	}
            },
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
					if (store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_RUNNING" ||
							store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_PENDING") {
						this.down('toolbar').setStatus({
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
//								var callerCmp = Ext.getCmp(this.callerId);
//								callerCmp.fireEvent("multiDsSearchDone");
							},
							failure : alertFailure
						});
						if (store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_FAILURE") {
							this.down('toolbar').setStatus({
			                    text : store.getProxy().reader.jsonData.TaskModel.customStatus,
			                    iconCls : 'x-status-error'
			                });
						}
						else {
						    this.down('toolbar').setStatus({
								text : i18n.get("label.requestDone"),
			                    iconCls : 'x-status-valid'
			                });
						}
					}
                    store.each(function (record) {
                        var error = record.get("errorMessage");
                        if (!Ext.isEmpty(error)) {
						    var index = store.indexOf(record);
						    var htmlLineEl = this.getView().getNode(index);

						    Ext.create("Ext.tip.ToolTip", {
						        html : error,
						        dismissDelay : 0,
						        target : htmlLineEl,
						        cls : "x-form-invalid-tip"
						    });

						}
                    }, this);
				}
			}
        });
        
        this.columns = [{
			width : 25, 
			dataIndex : 'image', 
			header : "", 
			renderer : function (value) {
				return ! Ext.isEmpty(value) && ! Ext.isEmpty(value.url) ? Ext.String.format("<img src='{0}' width=20 height=20>", value.url) : "";
			}
		}, {
			width : 100, 
			dataIndex : 'name', 
			header : i18n.get('label.name'),
			renderer : function (value, metaData, record, rowIndex, colIndex, store) {
				if (record.get('status') == "REQUEST_ERROR") {
					metaData.tdCls+="red-row";
				}
				if (record.get('status') == "UNAUTHORIZED") {
					metaData.tdCls+="orange-row";
				}
				return value;
			}
		}, {
			width : 100,
			dataIndex : 'nbRecord',
			header : i18n.get('label.nbRecords')
		}, {
            width : 150,
            dataIndex : 'description',
            header : i18n.get('label.description'),
            renderer : function (value, metaData, record, rowIndex, colIndex, store) {
				if (record.get('status') == "REQUEST_ERROR") {
					metaData.tdCls+="red-row";
				}
				if (record.get('status') == "UNAUTHORIZED") {
					metaData.tdCls+="orange-row";
				}
				metaData.attr = "data-qtip='" + value + "'";
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
                handler: Desktop.getNavMode().multiDataset.showDataset
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
                    var url = rec.get('url');
                    sitools.user.utils.DatasetUtils.openDefinition(url);
                    
//                    sitools.user.clickDatasetIcone(rec.get("url"), "defi", {
//						formMultiDsParams : this.formMultiDsParams
//                    });
                }
            }]
		}];
        
        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid'
        });

        this.listeners = {
        	scope : this, 
        	viewready : function (grid) {
//        		grid.up('projectformview').fireEvent('multiDsSearchDone');
        	}
        };
        
		Ext.apply(this, {
//			viewConfig : {
//				
//				getRowClass : function (rec) {
//					if (rec.get('status') == "REQUEST_ERROR") {
//						return "red-row";
//					}
//					if (rec.get('status') == "UNAUTHORIZED") {
//						return "orange-row";
//					}
//				}
//			}
		});
		
		this.callParent(arguments);
    } 
});

