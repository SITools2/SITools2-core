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

sitools.admin.guiServices.guiServicesCrudController = Ext.extend(sitools.util.abstractController, {
    pageSize : 10,
    constructor : function (config) {
        sitools.admin.guiServices.guiServicesCrudController.superclass.constructor.call(this, config);

        this.control({
            /***** GUI SERVICES CRUD EVENTS *****/
            'guiServicesCrudView-button-create' : {
                 click : function(e){
                    this._createPropView();
                }
            },
            'guiServicesCrudView-button-modify' : {
                 click : function (e){
                    this._modifyPropView();
                }
            },
            'guiServicesCrudView-button-delete' : {
                 click : function (e){
                    this._deleteGuiService();
                }
            },
            'guiServicesCrudView' : {
                afterrender : function (view) {
                    this.getStore('crud').load({params : {
                        start : 0,
                        limit : this.pageSize
                    }});
                },
                rowdblclick : function (e){
                	this._modifyPropView();
                }
            },
            /***** GUI SERVICES PROP EVENTS *****/
            'guiServicesPropView-button-dependencies-create' : {
                click : function (e){
                    this._createGuiServiceDependencie();
                }
            },'guiServicesPropView-button-dependencies-delete' : {
                click : function (e){
                    this._deleteGuiServiceDependencie();
                }
            },
            'guiServicesPropView-button-ok' : {
                click : function (e){
                    this._createGuiService();
                }
            },
            'guiServicesPropView-button-cancel' : {
                click : function (e){
                    this.getView('prop').destroy();
                }
            },
            'guiServicesPropView' : {
                render : function (e){
                    this.onViewPropRender();
                }
            }
        });
        
     },
     
     onViewPropRender : function () {
        var rec = this.getView('crud').getSelectionModel().getSelected();
        
        if (!rec){
        	return;
        }
        
     	var view = this.getView('prop');
        if (Ext.isDefined(view) && view.action == 'modify') {
        	var f = this.getView('prop').formPanel.getForm();
	        var data = rec.data;
	        f.setValues(data);
	        
	        var dependencies = data.dependencies;
	        var storeDependencies = this.getStore('dependencies');
	        if (!Ext.isEmpty(dependencies.js)) {
	            Ext.each(dependencies.js, function (item) {
	                storeDependencies.add(new Ext.data.Record(item));
	            }, this);
	        }
	        if (!Ext.isEmpty(dependencies.css)) {
	            Ext.each(dependencies.css, function (item) {
	                storeDependencies.add(new Ext.data.Record(item));
	            }, this);
	        }
        }
    },

     
     _createPropView : function (){
        var guiServicesPropView = new sitools.admin.guiServices.guiServicesPropView({
            name : 'prop',
            action : 'create',
            store : this.getStore('dependencies')           
        });
        console.log(this.id);

        
        this.registerView(guiServicesPropView);
        this.getStore('dependencies').removeAll();
        guiServicesPropView.show();
     },
     
     _modifyPropView : function (){
     	
     	var rec = this.getView('crud').getSelectionModel().getSelected();
        
        if (!rec){
        	return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
     	
        var guiServicesPropView = new sitools.admin.guiServices.guiServicesPropView({
            name : 'prop',
            action : 'modify',
            store : this.getStore('dependencies')           
        });
        
        this.registerView(guiServicesPropView);
        this.getStore('dependencies').removeAll();
        guiServicesPropView.show();
     },
     
     _createGuiService : function (){
        var form = this.getView('prop').formPanel.getForm();
        
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        
        var jsonObject = form.getFieldValues();
            
        jsonObject.dependencies = {};
        jsonObject.dependencies.js = [];
        jsonObject.dependencies.css = [];
        this.getStore('dependencies').each(function (item) {
            if (!Ext.isEmpty(item.data.url)) {
                if (item.data.url.indexOf(".css") != -1) {
                    jsonObject.dependencies.css.push({
                        url : item.data.url
                    });
                }
                if (item.data.url.indexOf(".js") != -1) {
                    jsonObject.dependencies.js.push({
                        url : item.data.url
                    });
                }
            }
        });
        
         this.getStore('crud').saveRecord(jsonObject, this.getView('prop').action);
         this.getView('prop').destroy();
     },
     
     _deleteGuiService : function () {
        var rec = this.getView('crud').getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }

        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('projectModulesCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.getStore('crud').deleteRecord(rec);
                }
            }
        });

    },
     
     _createGuiServiceDependencie : function (){
        var e = new Ext.data.Record();
        this.getStore('dependencies').insert(this.getStore('dependencies').getCount(), e);
     },
     
     _deleteGuiServiceDependencie : function (){
        var s = this.getView('prop').gridDependencies.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.getStore('dependencies').remove(r);
        }
     }
     
});


