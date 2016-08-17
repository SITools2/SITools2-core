/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.public.widget.imageChooser.ImageChooser');
/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
/**
 * Possibilité de paramétrer le zindex de cette fenêtre pour qu'elle reste toujours au dessus d'une autre fenêtre.
 * C'est utile dans le cas ou on utilise ce composant dans l'éditeur HTML CKEDITOR puisque ses fenêtres sont ouvertes
 * en dehors du windowManager de Sitools 
 */
Ext.define('sitools.public.widget.imageChooser.ImageChooser', {
   alternateClassName : ['ImageChooser'], 
   extend : 'Ext.window.Window',
   
   layout: 'border',
   minWidth: 550,
   minHeight: 450,
   width : 600,
   modal: true,
   border: false,
   cls : 'img-chooser-dlg',
   
   initComponent : function () {

       this.title = i18n.get('label.chooseImage');
       this.initTemplates();

       this.store = Ext.create("Ext.data.JsonStore", {
           proxy : {
               url : this.url,
               type : 'ajax',
               reader : {
                   type : 'json',
                   root : 'items',
                   idProperty : 'id'
               }
           },
           fields: [
               {name:'id',type :'string',convert : function() {
                   return Ext.id();
               }},
               {name:'name',type:'string'},
               {name:'url',type:'string'},
               {name:'size', type: 'float'},
               {name:'lastmod', type:'date', dateFormat:'timestamp'}
           ],
           listeners: {
               scope : this,
               load : function (store, records, options) {
                   Ext.each(records, function(record){
                       var url = new Reference(record.get('url'));
                       var recordUrl = url.getFile();
                       record.set("url", recordUrl);
                   });
                   this.view.select(0);
               }
           }
       });
       
       // S'il y a une exception et que c'est une datatstorage, on essaye de créer le dossier de l'url
       if (!Ext.isEmpty(this.urlToUpload) && this.isDatastorage == true) {
           this.store.addListener('exception', function (misc) {
               Ext.Ajax.request({
                   url : this.urlToUpload,
                   method : 'PUT',
                   success : function (ret) {
                   },
                   failure : alertFailure
               });
           }, this);
       }
       
       this.store.load();

       this.view = Ext.create("Ext.view.View", {
           tpl: this.thumbTemplate,
//           id : 'imageChooserDataViewId', 
           singleSelect: true,
           overItemCls:'x-view-over',
           selectedItemCls : 'x-view-selected',
           itemSelector: 'div.thumb-wrap',
           emptyText : '<div style="padding:10px;">No images match the specified filter</div>',
           store: this.store,
           listeners: {
               'selectionchange': {fn:this.showDetails, scope:this, buffer:100},
               'itemdblclick'       : {fn:this.doCallback, scope:this},
               'loadexception'  : {fn:this.onLoadException, scope:this},
               'beforeselect'   : {fn:function(view){
                   return view.store.getRange().length > 0;
               }}
           }
       });

       this.fp = Ext.create("Ext.form.Panel", {
           fileUpload: true,
           formId : 'formUploadId', 
           height: 100,
           padding: 10,
           labelWidth: 50,
           border : false,
           bodyBorder : false,
           defaults: {
               allowBlank: false,
               msgTarget: 'side'
           },
           items: [{
               xtype: 'filefield',
               fieldLabel: 'Photo',
               name: 'image',
               anchor: '100%',
               buttonText: i18n.get("label.browse"),
               iconCls: 'upload-icon'
           }, {
               xtype : 'button',
               text: i18n.get('label.uploadFile'),
               scope : this,
               anchor: '30%',
               handler : this.uploadFile
           }]
       });

       this.items = [{
           id: 'img-chooser-view',
           region: 'center',
           autoScroll: true,
           items: this.view,
           tbar:[{
               text: i18n.get ('label.filter')
           },{
               xtype : 'textfield',
               name : 'filter',
               selectOnFocus: true,
               width: 100,
               listeners: {
                   scope : this,
                   change : function (textfield, newValue, oldValue, opts) {
                       this.filter();
                   }
               }
           }, ' ', '-', {
               text: i18n.get ('label.sortBy')
           }, {
               name : 'sortSelect',
               xtype: 'combo',
               typeAhead: true,
               triggerAction: 'all',
               width: 100,
               editable: false,
               mode: 'local',
               displayField: 'desc',
               valueField: 'name',
               lazyInit: false,
               value: 'name',
               store: new Ext.data.ArrayStore({
                   fields: ['name', 'desc'],
                   data : [['name', 'Name'],['size', 'File Size'],['lastmod', 'Last Modified']]
               }),
               listeners: {
                   'select': {fn:this.sortImages, scope:this}
               }
           }]
       },{
           name :'detailPanel',
           region: 'east',
           split: true,
           width: 150,
           minWidth: 150,
           maxWidth: 250
       },{
//           id: 'img-upload-panel', 
           title : i18n.get('label.uploadFile'),
           region: 'south', 
           collapsible : true, 
           autoHeight : true,
           border : false,
           items: [this.fp]
       }];
       
       this.buttons = [{
           id: 'ok-btn',
           text: 'OK',
           handler: this.doCallback,
           scope: this
       },{
           text: 'Cancel',
           handler: function(){ this.close(); },
           scope: this
       }];
       
       this.callParent(arguments);
   },
   
   initTemplates : function() {
       this.thumbTemplate = new Ext.XTemplate(
           '<tpl for=".">',
               '<div class="thumb-wrap" id="{name}">',
               '<div class="thumb"><img src="{[this.formatUrl(values.url)]}" height="96" width="96" title="{name}"></div>',
               '<span>{[this.formatName(values.name)]}</span></div>',
           '</tpl>', {
           formatUrl : function(url) {
               var url = new Reference(url);
               return url.getFile();
           },
           formatName : function (name) {
               return Ext.String.ellipsis(decodeURIComponent(name),15);
           }
       });
       this.thumbTemplate.compile();
       
       this.detailsTemplate = new Ext.XTemplate(
           '<div class="details">',
               '<tpl for=".">',
                   '<img height="96" width="96" src="{url}"><div class="details-info">',
                   '<b>Image Name:</b>',
                   '<span>{[decodeURIComponent(values.name)]}</span>',
                   '<b>Size:</b>',
                   '<span>{[this.formatSize(values.size)]}</span>',
                   '<b>Last Modified:</b>',
                   '<span>{[this.formatDate(values.lastmod)]}</span></div>',
               '</tpl>',
           '</div>', {
               formatSize : function(size) {
                   return Ext.util.Format.fileSize(size);
               },
               formatDate : function (date) {
                   return Ext.Date.format(date, SITOOLS_DEFAULT_IHM_DATE_FORMAT);
               }
           }
       );
       this.detailsTemplate.compile();
   },

   showDetails : function (self, selected) {
       var selNode = selected;
       var detailEl = this.down('panel[name=detailPanel]').body;
       if(selNode && selNode.length > 0){
           selNode = selNode[0];
           Ext.getCmp('ok-btn').enable();
           var data = selNode.getData();
           detailEl.hide();
           this.detailsTemplate.overwrite(detailEl, data);
           detailEl.slideIn('l', {stopFx:true,duration:.2});
       }else{
           Ext.getCmp('ok-btn').disable();
           detailEl.update('');
       }
   },
   
   uploadFile : function () {
       var form = this.fp.getForm();
       if(this.fp.getForm().isValid()) {
           var urlUpload;
           
           if (!Ext.isEmpty(this.urlToUpload)) {
               urlUpload = this.urlToUpload;
           } else {
               urlUpload = loadUrl.get('APP_URL') + '/upload/';
           }
           
           form.submit({
               url: urlUpload,
               waitMsg: 'Uploading your photo...',
               scope : this,
               success: function(form, action) {
                   // as the server sends no content back, it is considered by extjs as a failure.
               },
               failure : function (form, action) {
                   popupMessage("", i18n.get('label.imageUploaded'), loadUrl.get('APP_URL') + '/common/res/images/icons/image_add.png');
                   var dataview = this.down('dataview');
                   dataview.getStore().load();
                   dataview.refresh();

//                   this.setZIndex(22000);
                   Ext.WindowManager.bringToFront(this);
               }
           });
           
       }
   },

   filter : function () {
       var filter = this.down('textfield[name=filter]');
       this.view.store.clearFilter(true);
       this.view.store.filter('name', filter.getValue());
   },

   sortImages : function() {
       var v = this.down('textfield[name=sortSelect]').getValue();
       this.view.store.sort(v, v == 'name' ? 'asc' : 'desc');
       this.view.select(0);
   },

   reset : function() {
       if(this.win.rendered){
           Ext.getCmp('filter').reset();
           this.view.getEl().dom.scrollTop = 0;
       }
       this.view.store.clearFilter();
       this.view.select(0);
   },

   doCallback : function () {
       var selRecords = this.view.getSelectedNodes();
       if(Ext.isEmpty(selRecords)){
           popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
           return;
       }
       var callback = this.callback;
       var config = this;
       var selNode = this.view.getRecord(selRecords[0])
       if(selNode && callback){
           var data = selNode.getData();
           callback(data, config);
       }; 
       this.close();
   },

   onLoadException : function(v,o){
       this.view.getEl().update('<div style="padding:10px;">Error loading images.</div>');
   },
   
   bringToFront : function (win) {
       if (win && !Ext.isEmpty(win.zindex) && win.isVisible()) {
           win.focus();
           win.setZIndex(win.zindex);
       }
   }
   
});