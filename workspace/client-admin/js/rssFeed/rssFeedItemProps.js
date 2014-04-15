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
 showHelp*/
Ext.namespace('sitools.admin.rssFeed');

/**
 * Create a new feed item for a specific feed
 * 
 * @param store, the RSS feed store
 * @param parent, the grid parent
 * @param action, the type of action ( create or modify )
 * @param rec, the record to load in case of edit ( optional )
 * @class sitools.admin.rssFeed.rssFeedItemProps
 * @extends Ext.Window
 */
Ext.define('sitools.admin.rssFeed.rssFeedItemProps', { 
    extend : 'Ext.Window',
    width : 700,
    height : 480,
    modal : true,
    resizable : false,
    layout : 'fit',
    
    initComponent : function () {
        this.title = i18n.get('title.feedItemDetails');
        
        /* paramétres du formulaire */
        var itemsForm = [ {
            fieldLabel : i18n.get('label.titleRss'),
            name : 'title',
            anchor : '100%'
        }, {
            fieldLabel : i18n.get('label.description'),
            name : 'description',
            anchor : '100%',
            xtype : "textarea"
        }, {
            fieldLabel : i18n.get('label.linkTitle'),
            name : 'link',
            anchor : '100%'
        }, {
            xtype : "sitoolsSelectImage", 
            fieldLabel : i18n.get('label.image'),
            name : 'image',
            anchor : '100%'
        }, {
            fieldLabel : i18n.get('label.authorName'),
            name : 'name',
            anchor : '100%'
        }, {
            fieldLabel : i18n.get('label.authorEmail'),
            name : 'email',
            anchor : '100%'
        }/*
             * , { fieldLabel : i18n.get('label.guid'), name : 'guid', anchor :
             * '100%' }
             */, {
            xtype : 'fieldcontainer',
            fieldLabel : i18n.get('label.updatedDate'),
            items : [ {
                fieldLabel : i18n.get("label.day"),
                name : 'date',
                xtype : "datefield"
            }, {
                name : 'hours',
                xtype : 'numberfield',
                width : 50,
                maxValue : 23,
                minValue : 0
            }, {
                xtype : 'displayfield',
                value : i18n.get("label.hours")
            }, {
                name : 'minutes',
                xtype : 'numberfield',
                width : 50,
                maxValue : 59,
                minValue : 0

            }, {
                xtype : 'displayfield',
                value : i18n.get("label.minutes")
            }, {
                xtype : 'button',
                name : 'nowUpdated',
                text : i18n.get("label.now"),
                width : 50,
                flex : 0,
                scope : this,
                handler : this.nowDate
            } ]
        }, {
            xtype : 'fieldcontainer',
            fieldLabel : i18n.get('label.publishedDate'),
            items : [ {
                fieldLabel : i18n.get("label.day"),
                name : 'datePub',
                xtype : "datefield",
                allowBlank : false
            }, {
                name : 'hoursPub',
                xtype : 'numberfield',
                width : 50,
                maxValue : 23,
                minValue : 0,
                allowBlank : false
            }, {
                xtype : 'displayfield',
                value : i18n.get("label.hours")
            }, {
                name : 'minutesPub',
                xtype : 'numberfield',
                width : 50,
                maxValue : 59,
                minValue : 0,
                allowBlank : false

            }, {
                xtype : 'displayfield',
                value : i18n.get("label.minutes")
            }, {
                xtype : 'button',
                name : 'nowPublished',
                text : i18n.get("label.now"),
                width : 50,
                flex : 0,
                scope : this,
                handler : this.nowDate
            }]
        } ];

        this.formPanel = Ext.create('Ext.form.Panel', {
            labelWidth : 100, // label settings here cascade unless overridden
            defaultType : 'textfield',
            items : itemsForm,
            border : false,
            bodyBorder : false,
            padding : 10
        });

        this.items = [ this.formPanel ];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        sitools.admin.rssFeed.rssFeedItemProps.superclass.initComponent.call(this);
    },

    /**
     * If the "action" is "modify", fill fields with the record data
     */
    afterRender : function () {
        sitools.admin.rssFeed.rssFeedItemProps.superclass.afterRender.apply(this, arguments);
        if (this.action == "modify") {
            var form = this.formPanel.getForm();

            var record = this.rec.copy();
            Ext.data.Record.id(record); // automatically generate a unique
            // sequential id

            var dateStr = this.rec.get("updatedDate");

            var date = new Date(dateStr);
            record.set("date", Ext.Date.format(date, 'm/d/Y'));
            record.set("hours", Ext.Date.format(date, 'H'));
            record.set("minutes", Ext.Date.format(date, 'i'));

            var dateStrPub = this.rec.get("publishedDate");

            var datePub = new Date(dateStrPub);
            record.set("datePub", Ext.Date.format(datePub, 'm/d/Y'));
            record.set("hoursPub", Ext.Date.format(datePub, 'H'));
            record.set("minutesPub", Ext.Date.format(datePub, 'i'));

            var author = record.get("author");
            if (!Ext.isEmpty(author)) {
                record.set('name', author.name);
                record.set('email', author.email);
            }
            if (!Ext.isEmpty(record.get('image'))) {
				record.set("image", record.get('image').url);
            }
            
            form.setValues(record.data);
        }
    },

    /**
     * Save dates fields 
     */
    onValidate : function () {
        var frm = this.down('form').getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var rec;
        if (this.action == "create") {
            rec = {};
        } else {
            rec = this.rec;
        }
        // store the form fields
        var form = this.formPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var mins = null;
        var hours = null;
        var dateStr = null;

        var minsPub = null;
        var hoursPub = null;
        var dateStrPub = null;

        var author = {};
        rec.author = author;
        
        Ext.iterate(form.getValues(), function (key, value) {
            if (key == "date") {
                dateStr = value;
            } else if (key == "hours") {
                hours = value;
            } else if (key == "minutes") {
                mins = value;
            } else if (key == "datePub") {
                dateStrPub = value;
            } else if (key == "hoursPub") {
                hoursPub = value;
            } else if (key == "minutesPub") {
                minsPub = value;
            } else if (key == "name" || key == "email") {
                author[key] = value;
            } else if (key == "image") {
				if (!Ext.isEmpty(value)) {
					var type = this.getTypeFromUrl(value);
					rec[key] = {
						url : value, 
						type : type
					};
				}
            } else {
                rec[key] = value;
            }

        }, this);
        var date;
        if (dateStr !== null && dateStr !== "") {

            if (hours !== null && mins !== null) {
                dateStr += " " + hours + ":" + mins;
            }
            date = new Date(dateStr);
            rec.updatedDate = date;
        }
        if (dateStrPub !== null && dateStrPub !== "") {

            if (hoursPub !== null && hoursPub !== "" && minsPub !== null && minsPub !== "") {
                dateStrPub += " " + hoursPub + ":" + minsPub;
            }
            date = new Date(dateStrPub);
            rec.publishedDate = date;
        }

        if (this.action == "create") {
            this.store.add(rec);
        }

        this.parent.getView().refresh();
        this.close();

    },

    /**
     * Set dates fields with date of the current day
     * @param button, the button clic
     * @param e, the event click
     */
    nowDate : function (button, e) {
        var date = new Date(), form, record;
        if (button.name == "nowUpdated") {
            form = this.formPanel.getForm();
            record = form.getValues();
            
            record.date = Ext.Date.format(date, 'm/d/Y');
            record.hours = Ext.Date.format(date, 'H');
            record.minutes = Ext.Date.format(date, 'i');
            form.setValues(record);
        }
        
        if (button.name == "nowPublished") {
            form = this.formPanel.getForm();
            record = form.getValues();
            
            record.datePub = Ext.Date.format(date, 'm/d/Y');
            record.hoursPub = Ext.Date.format(date, 'H');
            record.minutesPub = Ext.Date.format(date, 'i');
            form.setValues(record);
        }

    },
    
    /**
     * Get the image extension from the url 
     * @param url, the image url
     */
    getTypeFromUrl : function (url) {
		var tmp = url.split(".");
		var type = tmp[tmp.length - 1];
		switch (type.toLowerCase()) {
		case "jpg" :
		case "jpeg" : 
		case "jpe" : 
			type = "image/jpeg";
			break;
		case "gif" : 
		case "man" : 
			type = "image/gif";
			break;
		case "png" : 
			type = "image/png";
			break;	
		case "tif" : 
		case "tiff" : 
			type = "image/tiff";
			break;	
		case "pbm" : 
			type = "image/x-portable-bitmap";
			break;	
		case "pgm" : 
			type = "image/x-portable-graymap";
			break;	
		case "ppm" : 
			type = "image/x-portable-pixmap";
			break;
		default : 
			type = null;
		}
		return type;
		
    }
});
