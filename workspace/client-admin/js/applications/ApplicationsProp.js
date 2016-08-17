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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/
Ext.namespace('sitools.admin.applications');

/**
 * A window to view application details; 
 * @class sitools.admin.applications.ApplicationsProp
 * @cfg {Ext.data.Record} applicationRecord the selected record 
 * @extends Ext.Window
 */
Ext.define('sitools.admin.applications.ApplicationsProp', {
    extend : 'Ext.Window', 
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    dataSets : "",
    layout : 'fit',

    initComponent : function () {
        this.title = i18n.get('label.details');

        this.items = [{
            xtype : 'form',
            border : false,
            bodyBorder : false,
            padding : 10,
            defaults : {
                disabled : true
            },
            items : [{
                xtype : 'hidden',
                name : 'id'
            }, {
                xtype : 'textfield',
                name : 'name',
                fieldLabel : i18n.get('label.name'),
                anchor : '100%',
                maxLength : 30
            }, {
                xtype : 'textarea',
                name : 'description',
                fieldLabel : i18n.get('label.description'),
                anchor : '100%'               
            }, {
                xtype : 'textfield',
                name : 'urn',
                fieldLabel : i18n.get('label.urn'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'type',
                fieldLabel : i18n.get('label.type'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'url',
                fieldLabel : i18n.get('label.url'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'author',
                fieldLabel : i18n.get('label.author'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'owner',
                fieldLabel : i18n.get('label.owner'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'lastUpdate',
                fieldLabel : i18n.get('label.lastUpdate'),
                anchor : '100%'
            }, {
                xtype : 'textfield',
                name : 'status',
                fieldLabel : i18n.get('label.status'),
                anchor : '100%'
            }]
        }];
        
        this.buttons = [{
            text : i18n.get('label.close'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        sitools.admin.applications.ApplicationsProp.superclass.initComponent.call(this);
    },

    /**
     * Load the selected record in the main form. 
     */
    afterRender : function () {
        this.callParent(arguments);
        if (this.applicationRecord) {
            var f = this.down('form').getForm();
            f.loadRecord(this.applicationRecord);
        }
    }

});
