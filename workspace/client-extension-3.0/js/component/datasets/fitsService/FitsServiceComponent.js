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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.extension.component.datasets.fitsService');

/**
 * 
 * @class sitools.extension.component.datasets.fitsService.FitsServiceComponent
 * @extends sitools.user.core.Component
 */
Ext.define('sitools.extension.component.datasets.fitsService.FitsServiceComponent', {
    extend : 'sitools.user.core.Component',
    
    init : function (componentConfig, windowConfig) {

		var windowBaseConfig = {
			layout: 'fit',
			title: componentConfig.i18nFitsService.get('label.fitsViewer'),
			border : false,
			bodyCls: 'canvas-background',
			iconCls: 'fitsService',
			autoScroll: true,
			saveToolbar: true
		};

        //var windowBaseConfig = {
			//id : "dataDetail" + componentConfig.datasetId,
        //    title :i18n.get('label.viewDataDetail') + " : " + componentConfig.datasetName,
        //    name : 'recordDetailComponent', /* REQUIRE */
        //    width : 500,
        //    height : 450,
        //    datasetName : componentConfig.datasetName,
	     //   saveToolbar : true,
	     //   iconCls : "dataDetail",
	     //   type : "dataDetail",
	     //   shadow : true,
	     //   shadowOffset : 5,
	     //   toolbar : {
	     //   	xtype : 'toolbar',
	     //   	items : [{
		 //           iconCls : 'arrow-back',
		 //           handler : function () {
		 //               this.ownerCt.ownerCt.items.items[0].goPrevious();
		 //           }
		 //       }, {
		 //           iconCls : 'arrow-next',
		 //           handler : function () {
		 //               this.ownerCt.ownerCt.items.items[0].goNext();
		 //           }
		 //       }]
	     //   }
        //};
    	Ext.applyIf(windowBaseConfig, windowConfig);
        
        var view = Ext.create('sitools.extension.view.component.datasets.services.fitsService.FitsServiceView', componentConfig);

        this.setComponentView(view);
        this.show(view, windowBaseConfig);
    }

});