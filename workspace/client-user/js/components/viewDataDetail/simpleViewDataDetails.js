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
/*global Ext, sitools, i18n, alertFailure*/

Ext.namespace('sitools.user.component');

/**
 * A simple form view to visualize a record. 
 * Builds a form Panel with a form item for each field of the record.
 * @cfg {string} urlDataDetail the url to request the Record.
 * @class sitools.user.component.simpleViewDataDetail
 * @extends Ext.Panel
 */
sitools.user.component.simpleViewDataDetail = Ext.extend(Ext.Panel, {
//sitools.component.users.simpleViewDataDetail = Ext.extend(Ext.Panel, {

    initComponent : function () {
        this.url = this.urlDataDetail;
        //get the end of the uri and encode it
        var urlSplited = this.url.split('/');
        this.url = "";
        for (var i = 0; i < urlSplited.length; i++) {
            if (i < urlSplited.length - 1) {
                this.url += urlSplited[i] + "/";
            } else {
                this.url += encodeURIComponent(urlSplited[i]);
            }
        }

        
        this.layout = "fit";
        this.autoScroll = true;
        /*
         * var store = new Ext.data.JsonStore({ // store configs autoDestroy:
         * true, url: this.url, // reader configs root:
         * 'record.attributeValues', fields: ['name', 'value'], autoLoad : true
         * 
         * });
         */

        // set the search form
        this.formPanel = new Ext.FormPanel({
            frame : true,
            autoScroll : true,
            labelWidth : 150,
            labelAlign : "top"
        });

        var itemsForm = [];

        Ext.Ajax.request({
            url : this.url,
            method : 'GET',
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.information'), "Server error");
                    return false;
                }
                var record = data.record;
                var id = record.id;
                var attributes = record.attributeValues;
                if (attributes !== undefined) {
                    var i;
                    for (i = 0; i < attributes.length; i++) {
                        var name = attributes[i].name;
                        var value = attributes[i].value;
                        var item;
                        if (value !== null && value.length > 100) {

                            item = new Ext.form.TextArea({
                                fieldLabel : name,
                                value : value,
                                anchor : "90%",
                                readOnly : true
                            });
                        } else {
                            item = new Ext.form.TextField({
                                fieldLabel : name,
                                value : value,
                                anchor : "90%",
                                readOnly : true
                            });
                        }

                        itemsForm.push(item);

                    }
                    this.formPanel.add(itemsForm);
                    this.formPanel.doLayout();
                }
            },
            failure : alertFailure
        });

        this.componentType = 'detail';
        this.items = [ this.formPanel ];

        sitools.user.component.simpleViewDataDetail.superclass.initComponent.call(this);
    }, 
    _getSettings : function () {
        return {};
    }
});
