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
/*
 * global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin,
 * DEFAULT_PREFERENCES_FOLDER, loadUrl
 */
/*
 * @include "../../sitoolsProject.js" @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js" @include
 * "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.controller.modules.formModule');

/**
 * Forms Module : Displays All Forms depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.controller.modules.formModule.FormModuleController', {
	extend : 'Ext.app.Controller',
    
    views : ['modules.formModule.FormModuleView'],
    
    init : function () {
        this.control({
            'formsModuleView button[name="btnViewForm"]' : {
                click : function (btn) {
                    var rec = btn.up('formsModuleView').gridFormsDs.getSelectionModel().getSelection()[0];
                    if (Ext.isEmpty(rec)) {
                        return;
                    }
                    this.showDetail(rec);
                }
            },
            
            'formsModuleView button[name="btnViewFormMultiDs"]' : {
                click : function (btn) {
                    var rec = btn.up('formsModuleView').gridFormsMultiDs.getSelectionModel().getSelection()[0];
                    if (Ext.isEmpty(rec)) {
                        return;
                    }
                    this.showDetailMultiDs(rec);
                }
            }, 
            
            'formsModuleView grid[name="gridFormsDs"]' : {
                itemdblclick : function (grid, record) {
                    this.showDetail(record);
                }
            },
            
            'formsModuleView grid[name="gridFormMultiDs"]' : {
                itemdblclick : function (grid, record) {
                    this.showDetailMultiDs(record);
                }
            },
            'formsModuleView' : {
            	afterrender : function (fmView) {
            		fmView.formStore.load();
            		fmView.formMultiDsStore.load();
            	}
            }
        });
    },
    
    /** CONTROL VIEW METHODS * */
    
    showDetail : function (rec) {
        if (rec.data.authorized === "false") {
            return;
        }
        
        Ext.Ajax.request({
            url : rec.data.parentUrl,
            method : 'GET',
            scope : this,
            success : function (response) {
//                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = json.dataset;
                    
                    var formComponent = Ext.create('sitools.user.component.form.FormComponent');
                    formComponent.create(this.getApplication());
                    formComponent.init(rec.getData(true), dataset);
//                    formController.openForm(rec.getData(true), dataset);
                    
                    return;
//                }
//                catch (err) {
//                    Ext.Msg.alert(i18n.get('label.error'), err);
//                    return;
//                }
                
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

        var formController = Ext.create('sitools.user.component.form.FormComponent');
        formController.openProjectForm(rec.getData(true));
        
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

    }
    
    
});
