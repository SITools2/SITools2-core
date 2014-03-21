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
/*global Ext, sitools, i18n, alert, showResponse, alertFailure, SitoolsDesk, window, userLogin, loadUrl */

Ext.namespace('sitools.user.modules.userSpaceDependencies');

sitools.user.modules.userSpaceDependencies.tasksDetails = Ext.extend(Ext.FormPanel, {

    
    labelWidth : 120,
    frame : true,
    autoScroll : true,
	initComponent : function () {
		//this.svaIntern = this.sva;
        
        var itemsForm = [];
        
        Ext.iterate(this.sva, function (key, value) {
            itemsForm.push({
                xtype : 'label',
                name : key,
                fieldLabel : i18n.get('label.' + key),
                anchor : '100%',
                value : value
            });
        });
        
		this.items = itemsForm;
            
        sitools.user.modules.userSpaceDependencies.tasksDetails.superclass.initComponent.call(this);
	}
});
