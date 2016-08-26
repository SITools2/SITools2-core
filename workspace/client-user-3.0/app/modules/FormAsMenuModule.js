/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/* global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.modules');
/**
 * datasetExplorer Module
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.FormAsMenuModule', {
    extend : 'sitools.user.core.Module',

    controllers : [ 'sitools.user.controller.modules.formModule.FormAsMenuModuleController' ],
    areSingleDatasetFormsLoaded : false,
    areMultiDatasetFormsLoaded : false,
    isLoaded : function () {
        return this.areSingleDatasetFormsLoaded && this.areMultiDatasetFormsLoaded;
    },
    load : function () {
        this.prepareStores();
    },
    prepareStores : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        this.formStore = Ext.create('sitools.user.store.FormStore', {
            autoLoad : false,
            storeId : 'formAsMenuModule_FormStore',
            listeners : {
                scope : this,
                load : function () {
                    this.areSingleDatasetFormsLoaded = true;
                }
            }
        });
        this.formStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/forms');
        this.formStore.load();
        this.formMultiDsStore = Ext.create('sitools.user.store.FormProjectStore', {
            autoLoad : false,
            storeId : 'formAsMenuModule_MultiDsFormStore',
            listeners : {
                scope : this,
                load : function () {
                    this.areMultiDatasetFormsLoaded = true;
                }
            }
        });
        this.formMultiDsStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/formsProject');
        this.formMultiDsStore.load();
    },
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

    openMyComponent : function (moduleRecord, event) {

        if (moduleRecord.get('formType') == 'simpleDs') {
            this.showDetail(moduleRecord);
        } else if (moduleRecord.get('formType') == 'multiDs') {
            this.showDetailMultiDs(moduleRecord);
        }

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
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = json.dataset;

                    var formComponent = Ext.create('sitools.user.component.form.FormComponent');
                    formComponent.create(Desktop.getApplication());
                    formComponent.init(rec.getData(true), dataset);

                    return;
                } catch (err) {
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
