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
Ext.define('sitools.admin.rssFeed.rssFeedItemProps', { extend : 'Ext.Window',
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
            xtype : 'compositefield',
            fieldLabel : i18n.get('label.updatedDate'),
            items : [ {
                fieldLabel : i18n.get("label.day"),
                name : 'date',
                xtype : "datefield"
            }, {
                name : 'hours',
                xtype : 'numberfield',
                width : 48,
                maxValue : 23,
                minValue : 0
            }, {
                xtype : 'displayfield',
                value : i18n.get("label.hours")
            }, {
                name : 'minutes',
                xtype : 'numberfield',
                width : 48,
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
            xtype : 'compositefield',
            fieldLabel : i18n.get('label.publishedDate'),
            items : [ {
                fieldLabel : i18n.get("label.day"),
                name : 'datePub',
                xtype : "datefield",
                allowBlank : false
            }, {
                name : 'hoursPub',
                xtype : 'numberfield',
                width : 48,
                maxValue : 23,
                minValue : 0,
                allowBlank : false
            }, {
                xtype : 'displayfield',
                value : i18n.get("label.hours")
            }, {
                name : 'minutesPub',
                xtype : 'numberfield',
                width : 48,
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

        this.formPanel = new Ext.FormPanel({
            labelWidth : 100, // label settings here cascade unless overridden
            defaultType : 'textfield',
            items : itemsForm,
            border : false,
            padding : 10
        });

        this.items = [ this.formPanel ];
        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];
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
            record.set("date", date.format('m/d/Y'));
            record.set("hours", date.format('H'));
            record.set("minutes", date.format('i'));

            var dateStrPub = this.rec.get("publishedDate");

            var datePub = new Date(dateStrPub);
            record.set("datePub", datePub.format('m/d/Y'));
            record.set("hoursPub", datePub.format('H'));
            record.set("minutesPub", datePub.format('i'));

            var author = record.get("author");
            if (!Ext.isEmpty(author)) {
                record.set('name', author.name);
                record.set('email', author.email);
            }
            if (!Ext.isEmpty(record.get('image'))) {
				record.set("image", record.get('image').url);
            }
            
            form.loadRecord(record);
            

        }
    },

    /**
     * Save dates fields 
     */
    onValidate : function () {
        var frm = this.findByType('form')[0].getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var rec;
        if (this.action == "create") {
            rec = new Ext.data.Record();
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
        rec.set("author", author);
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
				if (! Ext.isEmpty(value)) {
					var type = this.getTypeFromUrl(value);
					rec.set(key, {
						url : value, 
						type : type
					});
				}
            } else {
                rec.set(key, value);
            }

        }, this);
        var date;
        if (dateStr !== null && dateStr !== "") {

            if (hours !== null && mins !== null) {
                dateStr += " " + hours + ":" + mins;
            }
            date = new Date(dateStr);
            rec.set("updatedDate", date);
        }
        if (dateStrPub !== null && dateStrPub !== "") {

            if (hoursPub !== null && hoursPub !== "" && minsPub !== null && minsPub !== "") {
                dateStrPub += " " + hoursPub + ":" + minsPub;
            }
            date = new Date(dateStrPub);
            rec.set("publishedDate", date);
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
            record = new Ext.data.Record(form.getValues());
            record.set("date", date.format('m/d/Y'));
            record.set("hours", date.format('H'));
            record.set("minutes", date.format('i'));
            form.loadRecord(record);
        }
        if (button.name == "nowPublished") {
            form = this.formPanel.getForm();
            record = new Ext.data.Record(form.getValues());
            record.set("datePub", date.format('m/d/Y'));
            record.set("hoursPub", date.format('H'));
            record.set("minutesPub", date.format('i'));
            form.loadRecord(record);
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
