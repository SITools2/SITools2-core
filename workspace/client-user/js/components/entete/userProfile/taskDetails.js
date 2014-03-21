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
/*global Ext, sitools, i18n, alert, showResponse, alertFailure, SitoolsDesk, window, userLogin, loadUrl, DEFAULT_WIN_WIDTH, DEFAULT_WIN_HEIGHT */

Ext.namespace('sitools.user.component.entete.userProfile');

/**
 * @class sitools.user.component.entete.userProfile.tasksDetails
 * @extends Ext.FormPanel
 */
sitools.user.component.entete.userProfile.tasksDetails = Ext.extend(Ext.FormPanel, {

    
    labelWidth : 120,
    frame : true,
    autoScroll : true,
	initComponent : function () {
		//this.svaIntern = this.sva;
        
        var itemsForm = [];
        
        Ext.iterate(this.sva, function (key, value) {
            if (value != undefined && value != ""){
                itemsForm.push({
                    xtype : 'textfield',
                    name : key,
                    fieldLabel : i18n.get('label.' + key),
                    disabled : true,
                    disabledClass : 'x-item-disabled-custom',
                    labelStyle : 'font-weight:bold;',
                    anchor : '100%',
                    value : value                
                });
            }
        });
        
		this.items = itemsForm;
            
        sitools.user.component.entete.userProfile.tasksDetails.superclass.initComponent.call(this);

	},
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }
});
