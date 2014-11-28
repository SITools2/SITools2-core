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
	    
        this.store = Ext.create('sitools.user.store.projectform.ResultProjectFormStore',{
			grid : this
		});

		this.store.setCustomUrl(this.urlTask);
		this.store.load();
        
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
			itemId : 'showData',
	        items: [{
                getClass : function (value, meta, rec) {
					if (rec.get('status') == "REQUEST_ERROR") {
						return "multids-error";
					}
                }, 
                icon   : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_datasets.png',                // Use a URL in the icon config
                tooltip: i18n.get('label.showData')
            }]
		}, {
	        xtype: 'actioncolumn',
            header : i18n.get('label.showDefinition'),
	        width: 100,
			itemId : 'showDefinition',
	        items: [{
                icon   : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_dictionary.png',
                tooltip: i18n.get('label.showDefinition')
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
		this.callParent(arguments);
    } 
});

