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
 showHelp, loadUrl*/
/*
 * @include "../id.js"
 * @include "formPropPanel.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A GridPanel to display all forms. 
 * @class sitools.admin.forms.formsCrudPanel
 * @extends Ext.grid.GridPanel
 */
sitools.admin.forms.formsCrudPanel = Ext.extend(Ext.grid.GridPanel, {
//sitools.component.forms.formsCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.FORMS,
    pageSize : 10,
    urlFormulaires : "/tmp",
    // loadMask: true,

    initComponent : function () {
        this.baseUrlFormulaires = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        this.urlDatasets = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL');
        
        this.httpProxyForms = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            proxy : this.httpProxyForms,
            remoteSort : true,
            idProperty : 'id',
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

        var storeDatasets = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'columnModel' ],
            url : this.urlDatasets,
            root : "data",
            autoLoad : true
        });
        this.comboDatasets = new Ext.form.ComboBox({
            store : storeDatasets,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDatasets'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.datasetId = rec.data.id;
                    this.datasetColumnModel = rec.data.columnModel;
                    this.httpProxyForms.setUrl(this.baseUrlFormulaires + "/" + rec.data.id + "/forms", true);
                    this.loadFormulaires(rec.data.id);
                }

            }
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
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
        });

        this.bbar = {
            xtype : 'paging',
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
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
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

        this.view = new Ext.grid.GridView({
            forceFit : true
        });
        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };
        sitools.admin.forms.formsCrudPanel.superclass.initComponent.call(this);

    },
    loadFormulaires : function (datasetId) {
        // alert (dictionaryId);
        this.urlFormulaires = this.baseUrlFormulaires + "/" + datasetId + "/forms";
        this.httpProxyForms.setUrl(this.urlFormulaires, true);
        this.getStore().load({
            scope : this,
            callback : function () {
                this.getView().refresh();
            }
        });
    },

    // onRender : function (){
    // sitools.admin.forms.formsCrudPanel.superclass.onRender.apply(this,
    // arguments);
    // this.store.load({params:{start:0, limit:this.pageSize}});
    // },

    onCreate : function () {
        if (Ext.isEmpty(this.comboDatasets.getValue())) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        
        var up = new sitools.admin.forms.formPropPanel({
            urlFormulaire : this.urlFormulaires,
            action : 'create',
            store : this.getStore(),
            datasetColumnModel : this.datasetColumnModel
        });
        up.show(ID.BOX.FORMS);
    },

    onModify : function () {
        
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.forms.formPropPanel({
            urlFormulaire : this.baseUrlFormulaires + '/' + this.datasetId + '/forms/' + rec.data.id,
            action : 'modify',
            store : this.getStore(),
            datasetColumnModel : this.datasetColumnModel
        });
        up.show(ID.BOX.FORMS.COMPONENTLIST);
    },

    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var tot = Ext.Msg.show({
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
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.baseUrlFormulaires + "/" + this.datasetId + "/forms/" + rec.id,
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

Ext.reg('s-forms', sitools.admin.forms.formsCrudPanel);
