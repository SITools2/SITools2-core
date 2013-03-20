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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.guiServices');




sitools.admin.guiServices.guiServicesCrudModule = Ext.extend(Ext.util.Observable, {

    constructor : function (config) {
        //init store
        this.store = new sitools.admin.guiServices.guiServicesStore({
        	name : 'crud',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_GUI_SERVICES_URL')
        });
        
        this.storeDependencies = new Ext.data.JsonStore({
        	name : 'dependencies',
            fields : [ {
                name : 'url',
                type : 'string'
            }],
            autoLoad : false
        });
        
        //init views
        this.viewCrud = new sitools.admin.guiServices.guiServicesCrudView({
        	name : 'crud',
            store : this.store            
        });
        
        this.views = [this.viewCrud];
        
        //init controller
        this.controller = new sitools.admin.guiServices.guiServicesCrudController({
        	id : Ext.id(),
            views : [this.viewCrud],
            stores : [this.store, this.storeDependencies]
        });
        console.log(this.controller.id);
        
        sitools.admin.guiServices.guiServicesCrudModule.superclass.constructor.call(this, config);

     },
     
     getView : function(name) {
     	var viewResult = null;
     	Ext.each(this.views, function (view){
     		if (view.name === name){
     			viewResult = view;
     			return;
     		}
     	});
        return viewResult;
     },
     
     destroy : function (){
     	console.log('destroy');
     	this.viewCrud.destroy();
     	this.store.destroy();
     	this.storeDependencies.destroy();
     	sitools.util.EventBus.uncontrol(this.controller);
     	Ext.destroy(this.controller);			
     	this.controller = null;
     }
     
});


