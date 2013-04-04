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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.modules');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.projectServices
 * @extends Ext.grid.GridPanel
 */
sitools.user.modules.projectServices = function () {

	this.url = projectGlobal.sitoolsAttachementForUsers + "/services";
	this.layout = 'fit';
	this.store = new Ext.data.JsonStore({
        idProperty : 'id',
        autoLoad : true,
        root : "data",
        url : this.url,
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
        } ]
    });


    this.cm = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults : {
            sortable : true
        // columns are not sortable by default
        },
        columns : [ {
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
        },{
        	xtype : 'actioncolumn',
        	width : 30,
        	items : [{
        		icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_application_resource.png",
        		tooltip : i18n.get('label.runService'),
        		scope : this,
        		handler : function (grid, row){
        			this.grid.getSelectionModel().selectRow(row);
        			var job = this.grid.getSelectionModel().getSelected();
        			this.runJob(job.data);
        		}
        		}]
        }]
    });

    this.grid = new Ext.grid.GridPanel({
		title : i18n.get("label.projectServices"), 
		store : this.store,
		
		cm : this.cm, 
		viewConfig : {
			forceFit : true
		}
    });
    
    this.ctxMenu = new sitools.user.component.dataviews.ctxMenu({
		grid : this.grid, 
		event : null, 
		dataUrl : projectGlobal.sitoolsAttachementForUsers, 
		datasetId : projectGlobal.projectId, 
		datasetName : projectGlobal.projectName, 
		origin : "sitools.user.modules.projectServices",
        urlDetail : this.sitoolsAttachementForUsers 
    });
    
    sitools.user.modules.projectServices.superclass.constructor.call(this, Ext.apply({
        items : [this.grid]
    }));

};

Ext.extend(sitools.user.modules.projectServices, Ext.Panel, {
	
	runJob : function (resource) {
		
		var parameters = resource.parameters;
        var url, icon, method, runTypeUserInput;
        parameters.each(function (param) {
            switch (param.name) {
            case "methods":
                method = param.value;
                break;
            case "url":
                url = projectGlobal.sitoolsAttachementForUsers + param.value;
                break;
            case "runTypeUserInput":
                runTypeUserInput = param.value;
                break;
            case "image":
                icon = param.value;
                break;
			}
        }, this);

//        this.ctxMenu.handleResourceClick(resource, url, method, parameters);
        this.ctxMenu.resourceClick(resource, url, method, runTypeUserInput, parameters);
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

Ext.reg('sitools.user.modules.projectServices', sitools.user.modules.projectServices);
