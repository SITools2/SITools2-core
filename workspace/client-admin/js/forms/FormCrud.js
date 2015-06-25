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
/*
 * @include "../id.js"
 * @include "FormProp.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A GridPanel to display all forms. 
 * @class sitools.admin.forms.FormCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.forms.FormCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-forms',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    urlFormulaires : "/tmp",
    forceFit : true,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    id : ID.BOX.FORMS,
    
    requires : ['sitools.admin.forms.FormProp'],

    initComponent : function () {
        this.baseUrlFormulaires = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        
        var storeDatasets = Ext.create("Ext.data.JsonStore", {
            fields : [ 'id', 'name', 'columnModel' ],
            proxy : {
                type : 'ajax',
                url : this.urlDatasets,
                limitParam : undefined,
                startParam : undefined,
                reader : {
                    type :'json',
                    root : "data"
                }
            },
            autoLoad : true,
            listeners : {
            	scope : this,
            	load : function (store, records) {
            		if (this.comboDatasets.rendered) {
            			record = this.comboDatasets.getStore().getAt(0);
            			this.comboDatasets.setValue(record.get(this.comboDatasets.valueField), true);
            			this.comboDatasets.fireEvent('select', this.comboDatasets, [record]);
            		}
            	}
            }
        });
        this.comboDatasets = Ext.create("Ext.form.ComboBox", {
            store : storeDatasets,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDatasets'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, records, index) {
                    var rec = records[0];
                    this.datasetId = rec.data.id;
                    this.datasetColumnModel = rec.data.columnModel;
                    
                    this.getStore().setProxy({
                        type : 'ajax',
                        simpleSortMode : true,
                        url : this.baseUrlFormulaires + "/" + rec.get("id") + "/forms",
                        reader : {
                            type : 'json',
                            idProperty : 'id',
                            root : 'data'
                        }
                    });
                    this.loadFormulaires(rec.get("id"));
                }

            }
        });
        
        this.store = Ext.create("Ext.data.JsonStore", {
            remoteSort : true,
            pageSize : this.pageSize,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            } ]
        });

        

        this.columns = {
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            items : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 400,
                sortable : false
            } ]
        };

        this.bbar = {
            xtype : 'pagingtoolbar',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboDatasets, {
                text : i18n.get('label.create'),
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
            }
            // { text: i18n.get('label.members'), icon:
            // 'res/images/icons/toolbar_group_add.png', handler: this.onMembers
            // },
            // '->',
            // {xtype:'s-filter', emptyText:i18n.get('label.search'),
            // store:this.store, pageSize:this.pageSize}
            ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : "SINGLE"
        });
        
        this.callParent(arguments);

    },
    loadFormulaires : function (datasetId) {
        // alert (dictionaryId);
        this.urlFormulaires = this.baseUrlFormulaires + "/" + datasetId + "/forms";
        this.getStore().load();
    },

    onCreate : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        var up = Ext.create("sitools.admin.forms.FormProp", {
            urlFormulaire : this.urlFormulaires,
            action : 'create',
            store : this.getStore(),
            datasetColumnModel : this.datasetColumnModel
        });
        up.show(ID.BOX.FORMS);
    },

    onModify : function () {
        
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.forms.FormProp", {
            urlFormulaire : this.baseUrlFormulaires + '/' + this.datasetId + '/forms/' + rec.get("id"),
            action : 'modify',
            store : this.getStore(),
            datasetColumnModel : this.datasetColumnModel
        });
        up.show(ID.BOX.FORMS);
    },

    onDelete : function () {
        var rec = this.getLastSelectedRecord();;
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('formsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        // var rec = this.getLastSelectedRecord();;
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.baseUrlFormulaires + "/" + this.datasetId + "/forms/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    }

});

