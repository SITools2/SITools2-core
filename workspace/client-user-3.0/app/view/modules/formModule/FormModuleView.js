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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.view.modules.formModule');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.view.modules.formModule.FormModuleView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.formsModuleView',
    layout : 'fit',
    
    initComponent : function () {
        
         var project = Ext.getStore('ProjectStore').getProject();
         
         this.formStore = Ext.create('sitools.user.store.FormStore');
         this.formStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/forms');
    	
         this.formMultiDsStore = Ext.create('sitools.user.store.FormStore');
         this.formMultiDsStore.setCustomUrl(project.get('sitoolsAttachementForUsers') + '/formsProject');
         
         
        var cmFormDs = [{
            header : "",
            dataIndex : 'authorized',
            renderer : function (value) {
                if (value === "false") {
                    return "<img height=\"15\" src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png'>";
                }
            }, 
            width : 20
        }, {
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 100,
            sortable : true
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 350
        }];

        var tbarFormDs = {
            xtype : 'toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [{
                xtype : 'button',
                text : i18n.get('label.viewForm'),
                name : 'btnViewForm',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png'
            } ]
        };
        
        var smFormDs = Ext.create('Ext.selection.RowModel', {});

        this.gridFormsDs = Ext.create('Ext.grid.Panel', {
            title : i18n.get("label.datasetForm"),
            name : 'gridFormsDs',
            store : this.formStore,
            columns : cmFormDs,
            selModel : smFormDs,
            tbar : tbarFormDs,
            forceFit : true,
            flex : 1,
            padding : 5
        });
        
        var cmFormsMultiDs = [{
            header : "",
            dataIndex : 'authorized',
            renderer : function (value) {
                if (value === "false") {
                    return "<img height=\"15\" src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png'>";
                }
            }, 
            width : 20
        }, {
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 100,
            sortable : true
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 350
        }];

        var smFormsMultiDs = Ext.create('Ext.selection.RowModel', {});
        
        var tbarFormsMultiDs = {
            xtype : 'toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [ {
                xtype : 'button',
                text : i18n.get('label.viewForm'),
                name : 'btnViewFormMultiDs',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png'
            } ]
        };
        
        this.gridFormsMultiDs = Ext.create('Ext.grid.Panel', {
            title : i18n.get("label.projectForm"),
            name : 'gridFormMultiDs',
            store : this.formMultiDsStore, 
            columns : cmFormsMultiDs, 
            selModel : smFormsMultiDs,
            tbar : tbarFormsMultiDs, 
            forceFit : true,
            flex : 1,
            padding : 5
        });
        
        this.containerPanel = Ext.create('Ext.panel.Panel', {
            layout : {
                type: 'vbox',
                align : 'stretch',
                pack  : 'start'
            },
            items : [ this.gridFormsDs, this.gridFormsMultiDs ]
        });
        
        this.items = [this.containerPanel];
        
        this.callParent(arguments);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id,
            xtype : this.$className
        };

    }
});
