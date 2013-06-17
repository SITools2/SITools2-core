/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var ImageChooser = function(config){
	this.config = config;
}

ImageChooser.prototype = {
    // cache data by image name for easy lookup
    lookup : {},

	show : function(el, callback){
		if(!this.win){
			this.initTemplates();

			this.store = new Ext.data.JsonStore({
			    url: this.config.url,
			    root: 'items',
			    fields: [
			        'name', 'url',
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
			this.store.load();

			var formatSize = function(data){
		        if(data.size < 1024) {
		            return data.size + " bytes";
		        } else {
		            return (Math.round(((data.size*10) / 1024))/10) + " KB";
		        }
		    };

			var formatData = function(data){
		    	data.shortName = data.name.ellipse(15);
		    	data.sizeString = formatSize(data);
		    	data.dateString = new Date(data.lastmod).format("d/m/Y g:i a");
		    	this.lookup[data.name] = data;
		    	return data;
		    };

		    this.view = new Ext.DataView({
				tpl: this.thumbTemplate,
				id : 'imageChooserDataViewId', 
				singleSelect: true,
				overClass:'x-view-over',
				itemSelector: 'div.thumb-wrap',
				emptyText : '<div style="padding:10px;">No images match the specified filter</div>',
				store: this.store,
				listeners: {
					'selectionchange': {fn:this.showDetails, scope:this, buffer:100},
					'dblclick'       : {fn:this.doCallback, scope:this},
					'loadexception'  : {fn:this.onLoadException, scope:this},
					'beforeselect'   : {fn:function(view){
				        return view.store.getRange().length > 0;
				    }}
				},
				prepareData: formatData.createDelegate(this)
			});

		    if (!Ext.isEmpty(fp)){
		    	fp.destroy();
		    }
		    var fp = new Ext.FormPanel({
		        fileUpload: true,
		        frame: true,
		        formId : 'formUploadId', 
		        autoHeight: true,
		        bodyStyle: 'padding: 10px 10px 0 10px;',
		        labelWidth: 50,
		        defaults: {
		            anchor: '95%',
		            allowBlank: false,
		            msgTarget: 'side'
		        },
		        items: [{
		            xtype: 'fileuploadfield',
		            id: 'form-file',
//		            emptyText: 'Select an image',
		            fieldLabel: 'Photo',
		            name: 'image',
		            buttonText: '',
		            buttonCfg: {
		                iconCls: 'upload-icon'
		            }
		        }],
		        buttons: [{
		            text: 'upload',
//		            scope : this, 
		            handler: function(){
		                if(fp.getForm().isValid()){
		                	Ext.Ajax.request ({
		                		url : loadUrl.get('APP_URL') + '/upload/?media=json',
		                		form : 'formUploadId', 
//		                		isUpload : true, 
		                		waitMsg : "wait...", 
		                		method : 'PUT', 
		                		success : function (response) {
			                		new Ext.ux.Notification({
			    						iconCls:	'x-icon-information',
			    						title:	  i18n.get('label.information'),
			    						html:	  i18n.get ('label.imageUploaded'),
			    						autoDestroy: true,
			    						hideDelay:  1000
			    					}).show(document); 
		                			Ext.getCmp('imageChooserDataViewId').refresh();
		                			
		                		}, 
		                		failure : function (response){
		                			Ext.Msg.alert (i18n.get('label.error'));
		                		}, 
		                		callback : function (){
		                			Ext.getCmp('imageChooserDataViewId').getStore().load();
		                			Ext.getCmp('imageChooserDataViewId').refresh();
		                		}
		                	});
		                }
		            }
		        }]
		    });

		    var cfg = {
		    	title: i18n.get('label.chooseImage'),
		    	id: 'img-chooser-dlg',
		    	layout: 'border',
				minWidth: 500,
				minHeight: 450,
				modal: true,
//				closeAction: 'hide',
				border: false,
				items:[{
					id: 'img-chooser-view',
					region: 'center',
					autoScroll: true,
					items: this.view,
                    tbar:[{
                    	text: i18n.get ('label.filter')
                    },{
                    	xtype: 'textfield',
                    	id: 'filter',
                    	selectOnFocus: true,
                    	width: 100,
                    	listeners: {
                    		'render': {fn:function(){
						    	Ext.getCmp('filter').getEl().on('keyup', function(){
						    		this.filter();
						    	}, this, {buffer:500});
                    		}, scope:this}
                    	}
                    }, ' ', '-', {
                    	text: i18n.get ('label.sortBy')
                    }, {
                    	id: 'sortSelect',
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
					id: 'img-detail-panel',
					region: 'east',
					split: true,
					width: 150,
					minWidth: 150,
					maxWidth: 250
				},{
					id: 'img-upload-panel', 
					title : i18n.get('label.upload'),
					region: 'south', 
					collapsible : true, 
					autoHeight : true, 
//					height : 100, 
					items: [fp]
				}],
				buttons: [{
					id: 'ok-btn',
					text: 'OK',
					handler: this.doCallback,
					scope: this
				},{
					text: 'Cancel',
					handler: function(){ this.win.close(); },
					scope: this
				}],
				keys: {
					key: 27, // Esc key
					handler: function(){ this.win.close(); },
					scope: this
				}
			};
			Ext.apply(cfg, this.config);
		    this.win = new Ext.Window(cfg);
		}

		this.reset();
	    this.win.show(el);
		this.callback = callback;
		this.animateTarget = el;
	},

	initTemplates : function(){
		this.thumbTemplate = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="thumb-wrap" id="{name}">',
				'<div class="thumb"><img src="{[this.formatUrl(values.url)]}" height="96" width="96" title="{name}"></div>',
				'<span>{shortName}</span></div>',
			'</tpl>', {
			formatUrl : function(url) {
				var url = new Reference(url);
    			return url.getFile();
	        }
		});
		this.thumbTemplate.compile();

		this.detailsTemplate = new Ext.XTemplate(
			'<div class="details">',
				'<tpl for=".">',
					'<img height="96" width="96" src="{url}"><div class="details-info">',
					'<b>Image Name:</b>',
					'<span>{name}</span>',
					'<b>Size:</b>',
					'<span>{sizeString}</span>',
					'<b>Last Modified:</b>',
					'<span>{dateString}</span></div>',
				'</tpl>',
			'</div>'
		);
		this.detailsTemplate.compile();
	},

	showDetails : function(){
	    var selNode = this.view.getSelectedNodes();
	    var detailEl = Ext.getCmp('img-detail-panel').body;
		if(selNode && selNode.length > 0){
			selNode = selNode[0];
			Ext.getCmp('ok-btn').enable();
		    var data = this.lookup[selNode.id];
            detailEl.hide();
            this.detailsTemplate.overwrite(detailEl, data);
            detailEl.slideIn('l', {stopFx:true,duration:.2});
		}else{
		    Ext.getCmp('ok-btn').disable();
		    detailEl.update('');
		}
	},

	filter : function(){
		var filter = Ext.getCmp('filter');
		this.view.store.filter('name', filter.getValue());
		this.view.select(0);
	},

	sortImages : function(){
		var v = Ext.getCmp('sortSelect').getValue();
    	this.view.store.sort(v, v == 'name' ? 'asc' : 'desc');
    	this.view.select(0);
    },

	reset : function(){
		if(this.win.rendered){
			Ext.getCmp('filter').reset();
			this.view.getEl().dom.scrollTop = 0;
		}
	    this.view.store.clearFilter();
		this.view.select(0);
	},

	doCallback : function(){
        var selNode = this.view.getSelectedNodes()[0];
		var callback = this.callback;
		var lookup = this.lookup;
		var config = this.config;
		if(selNode && callback){
			var data = lookup[selNode.id];
			callback(data, config);
		}; 
		this.win.close();
    },

	onLoadException : function(v,o){
	    this.view.getEl().update('<div style="padding:10px;">Error loading images.</div>');
	}
};

String.prototype.ellipse = function(maxLength){
    if(this.length > maxLength){
        return this.substr(0, maxLength-3) + '...';
    }
    return this;
};
