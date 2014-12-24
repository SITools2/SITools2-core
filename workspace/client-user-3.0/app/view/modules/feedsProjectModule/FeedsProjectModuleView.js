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

Ext.namespace('sitools.user.view.modules.feedsProjectModule');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.view.modules.feedsProjectModule.FeedsProjectModuleView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.feedsProjectModuleView',
    requires : ['sitools.user.model.FeedModel'],

    layout : 'fit',
    border : false,
    config : {
        feedsReader : null
    },
    
    initComponent : function () {
        
         var project = Ext.getStore('ProjectStore').getProject();
         
         this.storeFeeds = Ext.create("Ext.data.JsonStore", {
             model : 'sitools.user.model.FeedModel',
             proxy : {
                 type : 'ajax',
                 url : project.get("sitoolsAttachementForUsers") + "/feeds",
                 reader : {
                     type : 'json',
                     root : "data"
                 }
             },
             autoLoad : true,
             listeners : {
                 scope : this, 
                 load : function (store, records, options) {
                     this.fireEvent("feedsLoaded",this, store, records);                     
                 }
             }
         });

         this.cb = Ext.create("Ext.form.ComboBox", {
             // all of your config options
             store : this.storeFeeds,
             displayField : 'name',
             valueField : 'name',
             queryMode : 'local',
             forceSelection : true,
             triggerAction : 'all',
             emptyText : i18n.get('label.selectAFeed'),
             selectOnFocus : true,
             scope : this
         });

         this.buttonDate = Ext.create("Ext.Button", {
             text: i18n.get("label.feedDate"),
             sortData: {
                 direction: 'DESC'
             },
             iconCls: 'sort-desc'
         });
         
         this.tbar = {
             xtype : 'toolbar',
             cls : 'services-toolbar',
             defaults : {
                 scope : this,
                 cls : 'services-toolbar-btn'
             },
             items : [ this.cb, '-' , this.buttonDate ]
         };
        
        this.callParent(arguments);
    },
    
    clearFeedsReader : function () {
        this.remove(this.getFeedsReader());
    },
    
    addFeedsReader : function (feedsReader) {
        this.add(feedsReader);
        this.setFeedsReader(feedsReader);
    }
});
