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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.modules');
/**
 * datasetExplorer Module
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.FormAsMenuModule', {
    extend : 'sitools.user.core.Module',
    
    controllers : ['sitools.user.controller.modules.formModule.FormAsMenuModuleController'],

    init : function (componentConfig) {
        var event = componentConfig.event;
        var triggerComponent = componentConfig.triggerComponent;
        var view = Ext.create('sitools.user.view.modules.formModule.FormAsMenuModuleView');

        if (!Ext.isEmpty(triggerComponent)) {
            this.show(view, triggerComponent.getX(), triggerComponent.getY());
        } else {
            this.show(view, event.getX(), event.getY());
        }
        this.callParent(arguments);
    },

    show : function (view, x, y) {
    	view.showAt(x, y);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };
    },
    
    openMe : function (dataview, module, event) {
    	
    	this.storeDataview = dataview.store;
    	this.storeDataview.removeAll();
    	
    	var project = Ext.getStore('ProjectStore').getProject();
        
        this.formStore = Ext.create('sitools.user.store.FormStore', {
    		 autoLoad : true, 
        	 listeners : {
     			scope : this, 
     			load : this.onLoadDatasetsForms
     		}
        });
        this.formStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/forms');
   	
        this.formMultiDsStore = Ext.create('sitools.user.store.FormStore', {
        	autoLoad : true, 
            listeners : {
    			scope : this, 
    			load : this.onLoadMultiDSForms
    		}
        });
        this.formMultiDsStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/formsProject');
    	
//    	this.formAsMenu = Ext.create('sitools.user.view.modules.formModule.FormAsMenuModuleView', {
//    		storeDataview : store,
//    		moduleXtype : this.$className
//    	});
	},
	
	openMyComponent : function (moduleRecord, event) {
		
		if (moduleRecord.get('formType') == 'simpleDs') {
			this.showDetail(moduleRecord);
		}
		else if (moduleRecord.get('formType') == 'multiDs') {
			this.showDetailMultiDs(moduleRecord);
		}
		
	},
	
	onLoadDatasetsForms : function (store, records, successful) {
		var menuItems = [];
		
		Ext.each(records, function (record) {
			record.set('type', 'component');
			record.set('formType', 'simpleDs');
			record.set('xtype', this.$className);
			record.set('icon', 'formIcon');
			
			this.storeDataview.add(record);
		}, this);
	},
	
	onLoadMultiDSForms : function (store, records, successful) {
		var menuItems = [];
		
		Ext.each(records, function (record) {
			record.set('type', 'component');
			record.set('formType', 'multiDs'); // used to open form from moduleDataview
			record.set('xtype', this.$className);
			record.set('icon', 'formIcon');
			
			this.storeDataview.add(record);
		}, this);
	},
	
	showDetail : function (rec) {
   	 if (rec.get('authorized') === "false") {
            return;
        }
        
        Ext.Ajax.request({
            url : rec.get('parentUrl'),
            method : 'GET',
            scope : this,
            success : function (response) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = json.dataset;
                    
                    var formComponent = Ext.create('sitools.user.component.form.FormComponent');
                    formComponent.create(Desktop.getApplication());
                    formComponent.init(rec.getData(true), dataset);
                    
                    return;
                }
                catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                    return;
                }
            }, 
            failure : function () {
                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noActiveDatasetFound'));
                return;
            }
        });
   },
   
   showDetailMultiDs : function (rec) {
   	 if (Ext.isEmpty(rec)) {
            return;
        }

        var formComponent = Ext.create('sitools.user.component.form.FormComponent');
        formComponent.create(Desktop.getApplication());
        formComponent.openProjectForm(rec.getData(true));
   }
	
});

