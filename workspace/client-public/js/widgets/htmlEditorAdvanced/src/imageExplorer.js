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
/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var ImageExplorer = function(config){
	this.config = config;
}

ImageExplorer.prototype = {
    // cache data by image name for easy lookup
    lookup : {},

	show : function(el, callback){
		if(!this.win){
			this.initTemplates();

		 this.store = new Ext.data.JsonStore({
        	proxy : new Ext.data.HttpProxy({
        		method : 'GET',
        		url: this.config.directoryImageUrl,
        		headers : {
    				'Accept' : 'application/json+sitools-directory'
    			}
        	}),
	        idProperty: 'text',
	        fields: [
	                 {name : 'text', mapping: 'text'},
	                 {name : 'lastmod', mapping : 'lastmod', type:'date', dateFormat:'timestamp'},
	                 {name : 'leaf', mapping : 'leaf'}, 
	                 {name : 'url', mapping : 'url'},
	                 {name : 'size', mapping : 'size', type: 'float'},
	                 {name : 'cls', mapping : 'cls'}
	                ],
            listeners: {
		    	'load': {fn:function(){
		    		this.view.select(0); 
		    	}, scope: this, single:true}
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
		    	data.shortName = data.text.ellipse(15);
		    	data.sizeString = formatSize(data);
		    	data.dateString = new Date(data.lastmod).format("d/m/Y g:i a");
		    	this.lookup[data.text] = data;
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
					'dblclick'       : {fn: function (dv, ind, node, e){
						this.doCallback(dv, ind, node, e);
					}, scope:this},
					'loadexception'  : {fn:this.onLoadException, scope:this},
					'beforeselect'   : {fn:function(view){
				        return view.store.getRange().length > 0;
				    }}
				},
				prepareData: formatData.createDelegate(this)
			});

		    this.menu = new Ext.menu.Menu({
				id : 'paramMenu',
				items : [{
	                text: 'Initial Size',
	                id : 'initialId',
	                checked: true,
	                checkHandler: function (){
	                	if (this.checked){
	                		this.parentMenu.find('id', 'customId')[0].setChecked(false);
	                	}
	                }
	            }, '-',
	            {
	                text: 'Customisable Size',
	                id : 'customId',
	                checked: false,
	                menu : {
	                	items : [{
	                		xtype: 'spinnerfield',
	                    	emptyText: 'Custom width...',
	                    	name: 'width',
	                    	listeners : {
	                    		scope : this,
	                    		mousedown : function (field, e){
	                    			var ed = ed;
	                    		}
	                    	}
	                	},
	                	{
	                		xtype: 'spinnerfield',
	                		emptyText: 'Custom height...',
	                    	name: 'height',
	                    	listeners : {
	                    		scope : this,
	                    		mousedown : function (field, e){
	                    			var ed = ed;
	                    		}
	                    	}
	                	}]
	                },
	                checkHandler: function (){
	                	if (this.checked){
	                		this.parentMenu.find('id', 'initialId')[0].setChecked(false);
	                	}
	                }
	            }]
			});
		    
		    this.splitButton = new Ext.Toolbar.SplitButton({
		    	id: 'ok-btn',
		    	text: 'OK',
		    	scope : this,
	            handler: function (){
	            	var node = this.view.getSelectedNodes()[0];
	            	var rec = this.view.getStore().getById(node.id);
	            	
	            	if(this.menu.find('id', 'initialId')[0].checked){
	                	this.doCallback2(this.view, rec);
                	}
	            	else {
	            		var subMenu = this.menu.find('id', 'customId')[0];
	            		var width = subMenu.menu.find('name', 'width')[0].getValue();
	            		var height = subMenu.menu.find('name', 'height')[0].getValue();
	            		
	            		this.doCallback2(this.view, rec, width, height);
	            	}
                },
	            menu : this.menu
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
				tbar : [ '->', {
                		xtype : 'label',
                		text : i18n.get('label.uploadFile') + ' :'
            		}, 
            		{
                		xtype : 'button',
                		iconAlign : 'right',
                		iconCls : 'upload-icon',
              			tooltip : i18n.get('label.uploadFile'),
                		scope : this,
                		handler : function () {
			        		var callbackUpload = function (createdNode) {
			        			var rec = new Ext.data.Record(createdNode);
					        	this.add(rec);
					        	this.reload();
				        	}
				        
					        var uploadWin = new sitools.user.modules.datastorageUploadFile({
					        	urlUpload : this.config.directoryImageUrl + "/",
					        	callback : callbackUpload,
					        	scope : this.store
					        }).show();
                		}
            		}
            	],
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
				        valueField: 'text',
				        lazyInit: false,
				        value: 'text',
				        store: new Ext.data.ArrayStore({
					        fields: ['text', 'desc'],
					        data : [['text', 'Name'],['size', 'File Size'],['lastmod', 'Last Modified']]
					    }),
					    listeners: {
							'select': {fn:this.sortImages, scope:this}
					    }
				    }]
				}],
				buttons: [this.splitButton
				,{
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
				'<tpl if="this.isImage(url)">',
					'<div class="thumb-wrap" id="{name}">',
					'<div class="thumb"><img src="{url}" height="96" width="96" title="{name}"></div>',
					'<span>{shortName}</span></div>',
				'</tpl>',
			'</tpl>',
			{
    			compiled : true,
    			isLeaf : function (leaf){
    				return leaf;
    			},
    			isImage : function (text){
    				var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
    				if (text.match(imageRegex))
    					return true;
    				else{
    					return false;
    				}
    			}
            }
		);
		this.thumbTemplate.compile();
	},

	filter : function(){
		var filter = Ext.getCmp('filter');
		this.view.store.filter('text', filter.getValue());
		this.view.select(0);
	},

	sortImages : function(){
		var v = Ext.getCmp('sortSelect').getValue();
    	this.view.store.sort(v, v == 'text' ? 'asc' : 'desc');
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

	doCallback : function(dv, ind, node, e){
		
		var rec = dv.getStore().getById(node.id);
		//Insert Selected Image in HtmlEditor
		Image.insertImage(rec.data, this.config);
		
		this.win.close();
    },
    
    doCallback2 : function (dv, rec, width, height){
    	//Insert Selected Image in HtmlEditor
    	
    	if (width && height){
    		rec.data.width = width;
    		rec.data.height = height;
    	}
    	Image.insertImage(rec.data, this.config);
    	
		this.win.close();
    },

	onLoadException : function(v,o){
	    this.view.getEl().update('<div style="padding:10px;">Error loading images.</div>');
	},
	
	isImage : function (text){
		var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/;
		if (text.match(imageRegex))
			return true;
		else{
			var ind = this.store.find('text', text);
			this.store.remove(this.store.getAt(ind));
			return false;
		}
	}
};

String.prototype.ellipse = function(maxLength){
    if(this.length > maxLength){
        return this.substr(0, maxLength-3) + '...';
    }
    return this;
};
