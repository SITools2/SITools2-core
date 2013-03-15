/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, commonTreeUtils, projectGlobal, showResponse, document, SitoolsDesk, alertFailure, XMLSerializer, loadUrl, window */

Ext.namespace('sitools.user.modules');

/**
 * poster Module
 * @class sitools.user.modules.extPoster
 * @extends Ext.Panel
 */
sitools.user.modules.extPoster = Ext.extend(Ext.Panel, {
    
    initComponent : function () {
    
        var panelInputClassicRequest = new Ext.FormPanel({
            title : i18n.get('label.contentToSend'),
            region : "center",
            frame : true,
            items : [ {
                xtype : 'textfield',
                name : 'contentType',
                fieldLabel : i18n.get('label.contentType'),
                anchor : '100%',
                allowBlank : true,
                value : "application/json"                
            }, {
                xtype : 'textarea',
                name : 'input',
                anchor : '100% 90%',
                allowBlank : true,
                hideLabel : true,
                hideLabels : true
                
            } ]
        });

        var formClassicRequest = new Ext.FormPanel({
            labelWidth : 75, // label settings here cascade unless overridden
            frame : true,
            title : i18n.get('label.requestParameters'),
            height : 180,
            region : "north",
            defaults : {
                width : 230
            },
            defaultType : 'textfield',
            items : [ {
                xtype : 'textfield',
                name : 'url',
                fieldLabel : i18n.get('label.url'),
                anchor : '100%',
                allowBlank : false,
                value : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL')
            }, {
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                name : "method",
                xtype : "combo",
                fieldLabel : i18n.get('label.method'),
                store : new Ext.data.ArrayStore({
                    id : 0,
                    fields : [ 'id', 'value' ],
                    data : [ [ "GET", "GET" ], [ "PUT", "PUT" ], [ "POST", "POST" ], [ "DELETE", "DELETE" ], [ "OPTIONS", "OPTIONS" ] ]
                }),
                valueField : 'id',
                displayField : 'value',
                value : "GET",
                listeners : {
                    scope : this,
                    select : function (combo, record, index) {
                        var f = formClassicRequest.getForm();
                        var containerType = f.findField("containerType").enable();
                        if (record.id == "GET") {
                            containerType.enable();
                        } else {
                            containerType.disable();
                        }
                    }
                }
            }, {
                typeAhead : true,
                lazyRender : true,
                xtype : 'checkbox',
                id : 'containerType',
                boxLabel : 'Open in a new window',
                name : 'containerType',
                value : 'Window',
                checked : true
            }, {
                xtype : 'textfield',
                name : 'mediaType',
                fieldLabel : i18n.get('label.mediaType'),
                anchor : '100%',
                allowBlank : false,
                value : "application/json"
            } ],

            buttons : [ {
                text : 'Send',
                scope : this,
                handler : function () {
                    if (formClassicRequest.getForm().isValid()) {
                        var myMask = new Ext.LoadMask(this.panelClassic.getEl(), {
                            msg : "Query sent"
                        });
                        myMask.show();
                        var f = formClassicRequest.getForm();
                        var url = f.findField("url").getValue();
                        var method = f.findField("method").getValue();
                        var mediaType = f.findField("mediaType").getValue();
                        var bodyContent = null;
                        var contentType = null;
                        
                        if (method == "PUT" || method == "POST") {
                            var fm = panelInputClassicRequest.getForm();
                            bodyContent =  fm.findField("input").getValue();
                            contentType = fm.findField("contentType").getValue();                           
                        }
                        
                        this.defaultMediaType = Ext.Ajax.defaultHeaders.Accept; 
                        
                        Ext.apply(Ext.Ajax.defaultHeaders, {
                            Accept : mediaType
                        });
                        
                        Ext.Ajax.request({
							url : url,
							method : method,
							jsonData : (contentType == "application/json")
									? bodyContent
									: null,
							xmlData : (contentType == "text/xml" || contentType == "application/xml")
									? bodyContent
									: null,
							params : (contentType != "application/json"
									&& contentType != "text/xml" && contentType != "application/xml")
									? bodyContent
									: null,
							scope : this,
							success : this.handleClassicResponse,
							failure : this.handleClassicResponse,
							headers : {
								contentType : contentType
							}
						});
                    }
                }
            } ]
        });

        var formPostFile = new Ext.FormPanel({
            fileUpload : true,
            labelWidth : 75, // label settings here cascade unless overridden
            frame : true,
            title : i18n.get('label.requestParameters'),
            height : 150,
            region : "north",
            defaults : {
                width : 230
            },
            formId : 'formUploadId',
            defaultType : 'textfield',
            items : [ {
                xtype : 'textfield',
                name : 'url',
                fieldLabel : i18n.get('label.url'),
                anchor : '100%',
                allowBlank : false,
                value : "/jeo_entry/jeo/upload?media=json"
            }, {
                typeAhead : true,
                triggerAction : 'all',
                lazyRender : true,
                mode : 'local',
                name : "method",
                xtype : "combo",
                fieldLabel : i18n.get('label.method'),
                store : new Ext.data.ArrayStore({
                    id : 0,
                    fields : [ 'id', 'value' ],
                    data : [ [ "PUT", "PUT" ], [ "POST", "POST" ]]
                }),
                valueField : 'id',
                displayField : 'value',
                value : "POST"
            }, {
                xtype : 'fileuploadfield',
                id : 'form-file',
                // emptyText: 'Select an image',
                fieldLabel : 'File',
                name : 'image',
                buttonText : '',
                buttonCfg : {
                    iconCls : 'upload-icon'
                }
            } ],

            buttons : [ {
                text : 'Send',
                scope : this,
                handler : function () {
                    if (formPostFile.getForm().isValid()) {
                         var myMask = new Ext.LoadMask(this.panelPostFile.getEl(), {
                            msg : "Query sent"
                         });
                         myMask.show();
                        var f = formPostFile.getForm();
                        var url = f.findField("url").getValue();
                        var method = f.findField("method").getValue();
                        Ext.Ajax.request({
                            url : url,
                            form : 'formUploadId',
                            // isUpload : true,
                            method : method,
                            scope : this,
                            success : this.handleUploadResponse,
                            failure : this.handleUploadResponse
                        });
                    }
                }
            } ]
        });

        this.panelClassic = new Ext.Panel({
            layout : 'border',
            title : i18n.get("label.classicRequest"),
            layoutConfig : {
                align : 'stretch',
                pack : 'start'
            },
            items : [ formClassicRequest, panelInputClassicRequest ]
        });

        this.panelPostFile = new Ext.Panel({
            layout : 'border',
            title : i18n.get("label.postFileRequest"),
            layoutConfig : {
                align : 'stretch',
                pack : 'start'
            },
            items : [ formPostFile ]
        });

        var tabs = new Ext.TabPanel({
            activeTab : 0,
            items : [ this.panelClassic, this.panelPostFile ]
        });

        this.items = [ tabs ];

        sitools.user.modules.extPoster.superclass.initComponent.call(this);

    },
    
    handleUploadResponse : function (response, options) {
        var windowRes = new sitools.user.modules.extPoster.windowResult({
            type : "txt",
            content : response.responseText,
            url : options.url,
            method : options.method
        });
        windowRes.show();
        this.panelPostFile.getEl().unmask();
    },
    
    
    handleClassicResponse : function (response, options) {
        var strResponse;
        var type = "txt";
        var contentType = response.getResponseHeader("Content-Type");
        if (!Ext.isEmpty(contentType) && contentType.indexOf("text/html") != -1) {
            type = "html";
        }
        if (!Ext.isEmpty(response.responseText)) {
            strResponse = response.responseText;
        } else if (!Ext.isEmpty(response.responseXML)) {
            strResponse = response.responseText;
        } else if (!Ext.isEmpty(response.responseJSON)) {
            strResponse = response.responseJSON;
        }
        
        var newW = Ext.getCmp("containerType");
        if (!newW.disabled && newW.getValue() === true) {
            this.panelClassic.getEl().unmask();
            window.open(options.url);
        } else {
            var windowRes = new sitools.user.modules.extPoster.windowResult({
                type : type,
                content : strResponse,
                url : options.url,
                method : options.method
            });
            windowRes.show();

        }
        this.panelClassic.getEl().unmask();

        Ext.apply(Ext.Ajax.defaultHeaders, {
            Accept : this.defaultMediaType
        });
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
    
    

});

Ext.reg('sitools.user.modules.extPoster', sitools.user.modules.extPoster);

/**
 * sitools.user.modules.extPoster.windowResult Window to display html or text
 * 
 * @cfg {String} type (required)
 *            html or text
 * @cfg {String} content
 *            the text or html to display
 */
sitools.user.modules.extPoster.windowResult = Ext.extend(Ext.Window, {
    modal : false,
    width : 800,
    height : 500,
    autoScroll : true,
    layout : "fit",    
    initComponent : function () {
        this.title =  this.method + " : " + this.url; 
        if (this.type == "html") {
            this.html = this.content;
        } else {
            var form = new Ext.FormPanel({
                hideLabel : true,
                hideLabels : true,
                items : [ {
                    xtype : 'textarea',
                    name : 'result',
                    anchor : '100% 100%',
                    value : this.content                    
                } ]
                
            });
            
            this.items = [ form ];
        }

        sitools.user.modules.extPoster.windowResult.superclass.initComponent.call(this);

    }

});
