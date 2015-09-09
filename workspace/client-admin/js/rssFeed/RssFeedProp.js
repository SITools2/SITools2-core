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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.rssFeed');

/**
 * A Panel to show the rss properties
 * 
 * @cfg url, the url where the Rss API is attached
 * @cfg urlRef, the relative url of the RSS API
 * @cfg id, the id of the DataSet, Project or Archive
 * @cfg store, the CRUD store
 * @cfg action, the type of action ( create or modify )
 * @cfg idFeed, the id of the Feed to load in case of edit ( optional )
 * @class sitools.admin.rssFeed.RssFeedProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.rssFeed.RssFeedProp', { 
    extend : 'Ext.Window',
    width : 700,
    height : 480,
    modal : true,
    urlValid : false,
    layout : 'fit',
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.rssFeed.RssFeedItemProp'],

    initComponent : function () {

        if (this.action == "create") {
            this.title = i18n.get("title.createFeed");
        } else {
            this.title = i18n.get("title.editFeed");
        }
        this.crudStore = this.store;
        
        /**
         * *********************************************** TAB1 FORMULAIRE
         * ***********************************************
         */
        
        this.buttonValidUrl =  Ext.create('Ext.button.Button', {
            text : i18n.get('label.validateUrl'),
            scope : this,
            handler : function () {
                this.validateUrl();
            },
            width : 150,
            iconCls : "img-help",
            iconAlign : "left"            
        });
        
        this.boxComponent = Ext.create('Ext.Component', {
            hidden : true,
            labelSeparator : "",
            fieldLabel : " ",
            id : "boxComponentValid",
            cls : "sitools-form-valid-msg",
            setHtml : function (html) {
                var el = this.getEl();
                el.update(html);
            }
        });
        

        this.formPanel = Ext.create("Ext.FormPanel", {
            labelWidth : 100, // label settings here cascade unless overridden
            defaultType : 'textfield',
            padding : 10,
            height : 165,
            title : i18n.get("title.feedDetails"),
            trackResetOnLoad : true,
            border : false,
            bodyBorder : false,
            items : [{
		        xtype : 'fieldset',
				title : i18n.get('title.feedDetailsCommon'),
				autoHeight : true,
				defaultType : 'textfield',
				items : [{
		            name : 'id',
		            xtype : 'hidden'
		        }, {
		            fieldLabel : i18n.get('label.name'),
		            name : 'name',
		            anchor : '100%',
		            allowBlank : false            
		        }, {
		            fieldLabel : i18n.get('label.feedSource'),
		            name : 'feedSource',
		            xtype : 'radiogroup',
		            columns : 1,
		            items : [ {
		                boxLabel : 'CLASSIC',
		                name : 'feedSource',
		                inputValue : "CLASSIC",
		                checked : true
		            }, {
		                boxLabel : 'EXTERNAL',
		                name : 'feedSource',
		                inputValue : "EXTERNAL"
		            } ],
		            listeners : {
		                scope : this,
		                change : function (radioGroup, newValue, oldValue, opts) {
		                    this.toogleSpecificForm(newValue.feedSource);
		                    if (newValue.feedSource == "CLASSIC") {                        
		                        this.gridPanel.setDisabled(false);
		                    } else {
		                        this.gridPanel.setDisabled(true);
		                    }
		                }
		            }
                }]  
            }, {
                xtype : 'fieldset',
				title : i18n.get('title.feedDetailsSpecific'),
				autoHeight : true,
				defaultType : 'textfield',
                items : [{
	                fieldLabel : i18n.get('label.titleRss'),
	                name : 'title',
	                anchor : '100%'
	            }, {
	                fieldLabel : i18n.get('label.description'),
	                name : 'description',
	                anchor : '100%'
	            }, {
	                fieldLabel : i18n.get('label.linkTitle'),
	                name : 'link',
	                anchor : '100%'
	            }, {
	                fieldLabel : i18n.get('label.authorName'),
	                name : 'authorName',
	                anchor : '100%'
	            }, {
	                fieldLabel : i18n.get('label.authorEmail'),
	                name : 'authorEmail',
	                anchor : '100%'
	            }, {
	                fieldLabel : i18n.get('label.feedType'),
	                name : 'feedType',
	                xtype : 'radiogroup',
	                columns : 1,
	                items : [ {
	                    boxLabel : 'RSS',
	                    name : 'feedType',
	                    inputValue : "rss_2.0",
	                    checked : true
	                }, {
	                    boxLabel : 'ATOM',
	                    name : 'feedType',
	                    inputValue : "atom_1.0"
	                } ]
	            },  {
                        xtype: 'fieldcontainer',
                        anchor : "100%",
                        fieldLabel : i18n.get('label.url'),
                        msgTarget: 'under',
                        hidden : true,
                        layout : 'hbox',
                        id : 'compositeFieldExternalUrl',
                        items: [{                            
				            name : 'externalUrl',
                            xtype: 'textfield',
                            anchor : "100%",                            
                            flex : 4,
                            status : "pending",
                            allowBlank : "false",
                            vtype : "uri",
                            validator : function (value) {
                                var status = this.status;
                                if (status == "invalid") {
                                    return this.invalidText;
								} else {
									return true;
								}
                            },
                            listeners : {
                                scope : this,
                                change : function (textField, newValue, oldValue) {
                                    textField.fireEvent("changeStatus", textField, "pending");
                                },                                
                                changeStatus : function (textField, status, errorMessage) {
                                    textField.status = status;
                                    textField.clearInvalid();
                                    var imgClass;
                                    switch (status) {
                                    case "pending":
                                        imgClass = "img-help";    
                                        this.boxComponent.setVisible(false);
                                        break;
                                    case "valid":
                                        imgClass = "img-valid";                                        
                                        break;
                                    case "invalid":
                                        this.boxComponent.setVisible(false);
                                        imgClass = "img-error";
                                        if (Ext.isEmpty(errorMessage)) {
										    textField.markInvalid();                                            
										} else {
										    textField.markInvalid(errorMessage);
                                            textField.invalidText = errorMessage;
										}
                                        break;
                                    default :
                                        this.boxComponent.setVisible(false);
                                        imgClass = "img-error";
                                        break;
                                    }
                                    this.buttonValidUrl.setIconCls(imgClass);
                                }
                            }
                        }, this.buttonValidUrl                            
                        ]
                    }, this.boxComponent
                ]
            }]
        });
        
        
        /**
         * *********************************************** TAB2 GRID LIST ITEMS
         * ***********************************************
         */

        this.storeItem = Ext.create("Ext.data.JsonStore", {
//            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string',
                convert : function () {
                    return Ext.id();
                }
            }, {
                name : 'title',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'link',
                type : 'string'
            }, {
                name : 'author'
            }, {
                name : 'image'
            }, {
                name : 'publishedDate',
                type : 'date'
            }, {
                name : 'updatedDate',
                type : 'date'
            }]
        });

        var columns = {
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            items : [ {
                header : i18n.get('label.titleRss'),
                dataIndex : 'title',
                width : 150,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.linkTitle'),
                dataIndex : 'link',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.updatedDate'),
                dataIndex : 'updatedDate',
                width : 100,
                sortable : true
            } ]
        };

        var tbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.add'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }]
        };

        this.gridPanel = Ext.create("Ext.grid.GridPanel", {
            forceFit : true,
            layout : 'fit',
            title : i18n.get("title.feedItems"),
            store : this.storeItem,
            columns : columns,
            tbar : tbar

        });

        /**
         * *********************************************** TAB PANEL
         * ***********************************************
         */
        
        this.tabPanel = Ext.create("Ext.TabPanel", {
            height : 450,
            activeTab : 0,
            items : [ this.formPanel, this.gridPanel ]
        });

        this.items = [ this.tabPanel ];
        
        this.saveButton = Ext.create("Ext.Button", {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate
        });
        
        this.buttons = [ this.saveButton, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];

        this.callParent(arguments);
    },
    
    /**
     * done a specific render to load rss informations. 
     */
    afterRender : function () {
        this.callParent(arguments);
        if (this.action == "modify") {
            Ext.Ajax.request({
                url : this.url + "/" + this.id + this.urlRef + "/" + this.idFeed,
                method : "GET",
                scope : this,
                success : function (ret) {
                    // check for the success of the request
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), json.message);
                        return false;
                    }
                    var data = json.FeedModel;
                    this.populateForm(data);
                    this.populateStore(data);

                    if (data.feedSource == "OPENSEARCH") {
                        this.gridPanel.setDisabled(true);
                        var feedTypeField = this.formPanel.getForm().findField("feedType");
                        feedTypeField.setDisabled(true);
                    } else if (data.feedSource == "EXTERNAL") {
                        this.gridPanel.setDisabled(true);
                        var externalUrlField = this.formPanel.getForm().findField("externalUrl");
                        if (!Ext.isEmpty(externalUrlField.getValue())) {
                            externalUrlField.fireEvent("changeStatus", externalUrlField, "valid");
                        }
                    }
                },
                failure : alertFailure
            });
        }

        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this.onValidate();
            }
        }, this);

    },

    /**
     * Fill rss fields properties 
     * 
     * @param data, the data containing the feed model
     */
    populateForm : function (data) {
        if (data !== undefined) {
            var form = this.formPanel.getForm();
            
            var rec = {};
            rec.id = data.id;
            rec.name = data.name;
            rec.feedType = data.feedType;
            rec.feedSource = data.feedSource;
			rec.title = data.title;
	        rec.description = data.description;
            
	        var radioGroupFeedType = this.down('fieldset > radiogroup[name=feedType]');
	        
	        if (data.feedType == "rss_2.0") {
	            radioGroupFeedType.down('radiofield[inputValue=rss_2.0]').setValue(true);
	        } else {
	            radioGroupFeedType.down('radiofield[inputValue=atom_1.0]').setValue(true);
	        }
	        
	        var radioGroupFeedSource = this.down('fieldset > radiogroup[name=feedSource]');
	        radioGroupFeedSource.down('radiofield[inputValue=' + data.feedSource + ']').setValue(true);
	        
            if (data.feedSource == "EXTERNAL") {
                rec.externalUrl = data.externalUrl;
			} else {
	            rec.link = data.link;
	            if (data.author !== undefined) {
	                rec.authorName = data.author.name;
	                rec.authorEmail = data.author.email;
	            }
            }

            form.setValues(rec);
            
        }
    },

    /**
     * Fill the grid store with data 
     * 
     * @param data, the data containing the feed model
     */
    populateStore : function (data) {
        this.storeItem.removeAll();
        if (data !== undefined && data.entries !== undefined) {
            var i;
            for (i = 0; i < data.entries.length; i++) {
                var entry = data.entries[i];
                var date = new Date(entry.updatedDate);
                entry.updatedDate = date;
                this.storeItem.add(entry);

            }
        }

    },

    /**
     * Check rss name property and save all rss properties
     */
    onValidate : function () {
        // gets the RSS details from the form
        var form = this.formPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }
        var feedSource = form.findField("feedSource").getValue().inputValue;
        var externalUrlField = form.findField("externalUrl");
        var externalUrlStatus = externalUrlField.status;
        if (feedSource == "EXTERNAL" && ("pending" == externalUrlStatus)) {
			this.validateUrl(this.onValidateModify);
		}
		else if (feedSource == "EXTERNAL" && "invalid" == externalUrlStatus) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
		}
        else {        
            this.onValidateModify();
        }
        
    },
    
    onValidateModify : function () {
        var form = this.formPanel.getForm();
        if (this.action == 'modify') {
            var name = form.findField("name").getValue();
            var originalName = form.findField("name").originalValue;
            if (originalName != name) {
                Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    buttons : Ext.Msg.YESNO,
                    msg : i18n.get('rssFeedProp.warning.feedName.changed'),
                    scope : this,
                    fn : function (btn, text) {
                        if (btn == 'yes') {
                            this.onSaveFeed();
                        }
                    }
                });
            } else {
                this.onSaveFeed();
            }
        } else {
            this.onSaveFeed();
        }
    },
    
    /**
     * Save rss properties fields
     */
    onSaveFeed : function () {
        var form = this.formPanel.getForm();
        var feedTypeField = form.findField("feedType");
        feedTypeField.setDisabled(false);        
                
        var json = {};
        json.author = {};
        Ext.iterate(form.getValues(), function (key, value) {
            if (key == "authorName") {
                json.author.name = value;
            } else if (key == "authorEmail") {
                json.author.email = value;
            } else {
                json[key] = value;
            }
        }, this);
        
        if (json.feedSource == "EXTERNAL") {
            json.title = form.findField("title").getValue();
            json.description = form.findField("description").getValue();
            
        }
                
//        // if the id is empty (create) we assign the name to it
//        if (Ext.isEmpty(json.id)) {
//			json.id = json.name;
//		}
        // gets the value from the grid
        json.entries = [];
        if (json.feedSource == "CLASSIC") {
	        
	        var i;
	        for (i = 0; i < this.storeItem.getCount(); i++) {
	            var rec = this.storeItem.getAt(i).copy().data;
	            var date = rec.updatedDate;
	            if (date !== null && date !== undefined) {
	                var updatedDate = new Date(date);
	                rec.updatedDate = Ext.Date.format(updatedDate,'Y-m-d\\TH:i:s.u') + Ext.Date.getGMTOffset(updatedDate);
	            }
	            date = rec.publishedDate;
	            if (date !== null && date !== undefined) {
	                var publishedDate = new Date(date);
	                rec.publishedDate = Ext.Date.format(publishedDate,'Y-m-d\\TH:i:s.u') + Ext.Date.getGMTOffset(publishedDate);
	
	            }
	            image = rec.image;
	            if(Ext.isEmpty(image)){
	                delete rec.image;
	            }
	            
	            delete rec.id;
	            json.entries.push(rec);
	        }
        }

        var url = this.url + "/" + this.id + this.urlRef;
        var method;
        if (this.action == "create") {
            method = "POST";
        } else {
            method = "PUT";
            url += "/" + this.idFeed;
        }

        Ext.Ajax.request({
            url : url,
            method : method,
            scope : this,
            jsonData : json,
            success : function (ret) {
                // check for the success of the request
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    return false;
                }
                this.store.reload();
                this.close();

            },
            failure : alertFailure
        });
    },
    
    toogleSpecificForm : function (feedSource) {
        var isClassic = (feedSource == "CLASSIC");
        var form = this.formPanel.getForm(); 
        form.findField("title").setDisabled(!isClassic);
        form.findField("title").setVisible(isClassic);
        
        form.findField("description").setDisabled(!isClassic);
        form.findField("description").setVisible(isClassic);
        
        form.findField("link").setDisabled(!isClassic);
        form.findField("link").setVisible(isClassic);
        
        form.findField("authorName").setDisabled(!isClassic);
        form.findField("authorName").setVisible(isClassic);        
        
        form.findField("authorEmail").setDisabled(!isClassic);
        form.findField("authorEmail").setVisible(isClassic);
        
        form.findField("authorEmail").setDisabled(!isClassic);
        form.findField("authorEmail").setVisible(isClassic);
        
        form.findField("feedType").setVisible(isClassic);
        
        Ext.getCmp('boxComponentValid').setDisabled(isClassic);
        Ext.getCmp('boxComponentValid').setVisible(!isClassic);
        
        Ext.getCmp('compositeFieldExternalUrl').setDisabled(isClassic);
        Ext.getCmp('compositeFieldExternalUrl').setVisible(!isClassic);
    }, 

    /**
     * Create a {sitools.admin.rssFeed.RssFeedItemProp} rss item property field
     *  to create a property
     */
    onCreate : function () {
        var up = Ext.create("sitools.admin.rssFeed.RssFeedItemProp", {
            store : this.storeItem,
            parent : this.gridPanel,
            action : "create"
        });
        up.show();
    },
    
    /**
     * Open a {sitools.admin.rssFeed.RssFeedItemProp} rss item property field
     *  to modify a property
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord(this.gridPanel);
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.rssFeed.RssFeedItemProp", {
            store : this.storeItem,
            parent : this.gridPanel,
            action : "modify",
            rec : rec
        });
        up.show();
    },
    
    /**
     * Delete the selected rss property from the store
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord(this.gridPanel);
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        this.storeItem.remove(rec);
    },
    
    validateUrl : function (successCallback) {
        var externalUrlField = this.formPanel.getForm().findField("externalUrl");
        if (externalUrlField.isValid()) {
	        var externalUrl = externalUrlField.getValue();
	        var url = "/sitools/proxy";
	        this.saveButton.disable();
	        this.buttonValidUrl.disable();
	        this.buttonValidUrl.setIconCls("img-loading");
	        Ext.Ajax.request({
	            url : url,
	            method : "GET",
	            scope : this,
	            //custom parameters to avoid calling the requestexception event if there is an error
	            doNotHandleRequestexception : true,
	            params : {
	                external_url : externalUrl
	            },
	            success : function (ret) {
	                var responseXML = ret.responseXML;
	                var isRss = false, isAtom = false;
	                var rss = null, atom = null;
	                if (!Ext.isEmpty(responseXML)) {
	//                    isRss = responseXML.firstChild.localName == "rss";
	//                    isAtom = responseXML.firstChild.localName == "feed";
	                    rss = Ext.DomQuery.selectNode("rss", responseXML);
	                    atom = Ext.DomQuery.selectNode("feed", responseXML);
	                    isRss = !Ext.isEmpty(rss);
	                    isAtom = !Ext.isEmpty(atom);
	                }
	                
	                var form = this.formPanel.getForm();
	                var feedField = form.findField("feedType");
	                var externalUrlField = form.findField("externalUrl");
	                if (isRss) {
						feedField.setValue({feedType : "rss_2.0"});                    
					} else if (isAtom) {
						feedField.setValue({feedType : "atom_1.0"});                    
					} 
	                
	                if (isRss || isAtom) {
                        this.boxComponent.setHtml(Ext.String.format(i18n.get("label.feedTypeDetected"), feedField.getValue().feedType));
                        this.boxComponent.setVisible(true);
                        
                        var xml = (isRss) ? rss : atom;
                        var titleNode  = Ext.DomQuery.selectNode("title", xml);
                        var descriptionNode  = Ext.DomQuery.selectNode("description", xml);
                        
                        if (titleNode) {
                            form.findField("title").setValue(titleNode.textContent);
                        }
                        if (descriptionNode) {
                            form.findField("description").setValue(descriptionNode.textContent);
                        }
                        
	                    externalUrlField.fireEvent("changeStatus", externalUrlField, "valid");
	                    if (Ext.isFunction(successCallback)) {
	                        successCallback.call(this);
	                    }
	                } else {
	                    externalUrlField.fireEvent("changeStatus", externalUrlField, "invalid", i18n.get("label.incompatible.feed.type"));
	                }
	                
	            },
	            failure : function (response, options) {
	                this.urlValid = false;
	                var externalUrlField = this.formPanel.getForm().findField("externalUrl");
	                externalUrlField.fireEvent("changeStatus", externalUrlField, "invalid", i18n.get("label.urlInvalid"));                
	            },
	            callback : function (options, success, response) {
	                this.saveButton.enable();
	                this.buttonValidUrl.enable();
	            }
	        });
        }
        
    }

});
