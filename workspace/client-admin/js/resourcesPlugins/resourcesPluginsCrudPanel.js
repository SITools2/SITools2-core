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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.admin.resourcesPlugins');

/**
 * A Generic Panel to display resources plugin informations in Sitools2
 * 
 * @param urlParents, the url of the parent object
 * @param resourcesUrlPart, the url of thes resources part
 * @param urlResources, the url of the resources listing
 * @param parentType, the type of the parent, string used only for i18n label
 * @class sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel
 * @extends Ext.grid.GridPanel
 */
//sitools.component.resourcesPlugins.resourcesPluginsCrudPanel = Ext.extend(Ext.grid.GridPanel, {
sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel = Ext.extend(Ext.grid.GridPanel, {

	urlParentsParams : '',
    border : false,
    height : 300,
    pageSize : 10,
    modify : false,
    urlGrid : null,    
    
    // Warning for version conflicts
    conflictWarned : false,
    viewConfig : {
        forceFit : true,
        autoFill : true, 
        getRowClass : function (row, index) { 
            var cls = ''; 
            var data = row.data;
            if (data.classVersion !== data.currentClassVersion
                && data.currentClassVersion !== null 
                && data.currentClassVersion !== undefined) {
                if (!this.conflictWarned) {
                    Ext.Msg.alert("warning.version.conflict", "Resources " 
                    + data.name 
                    + " definition (v" 
                    + data.classVersion 
                    + ") may conflict with current class version : " 
                    + data.currentClassVersion);
                    this.conflictWarned = true;
                }
                cls = "red-row";
            }
            return cls; 
        } 
        
    },
    
    initComponent : function () {
        //LIST OF PARENTS
        var storeParents = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'type' ],
            url : this.urlParents + this.urlParentsParams,
            root : "data",
            autoLoad : true
        });
        
        this.comboParents = new Ext.form.ComboBox({
            store : storeParents,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.select' + this.parentType + 's'),
            selectOnFocus : true,            
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.parentId = rec.data.id;
                    if (!Ext.isEmpty(rec.data.type)) {
                        this.appClassName = rec.data.type;
                    }                    
                    
                    var url = this.urlParents + "/" + this.parentId + this.resourcesUrlPart + this.urlParentsParams;
                    this.httpProxyResources.setUrl(url, true);
                    this.getStore().load();
                }
            }
        });
        
        
        
        this.httpProxyResources = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
            
            
        });
        this.store = new Ext.data.JsonStore({
            idProperty : 'id',
            root : "data",
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'descriptionAction',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'classVersion',
                type : 'string'
            }, {
                name : 'classAuthor',
                type : 'string'
            }, {
                name : 'currentClassVersion',
                type : 'string'
            }, {
                name : 'currentClassAuthor',
                type : 'string'
            }, {
                name : 'resourceClassName',
                type : 'string'
            }, {
                name : 'classOwner',
                type : 'string'
            }, {
                name : 'parameters'
            }, {
                name : 'parent',
                type : 'string'
            }, {
                name : 'className',
                type : 'string'
            }, {
                name : 'dataSetSelection',
                type : 'string'
            }, {
                name : 'behavior',
                type : 'string'
            } ],
            proxy : this.httpProxyResources
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
                width : 150
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.resourceClassName'),
                dataIndex : 'resourceClassName',
                width : 150,
                sortable : false
            }, {
                header : i18n.get('label.classVersion'),
                dataIndex : 'classVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.currentClassVersion'),
                dataIndex : 'currentClassVersion',
                width : 50,
                sortable : false
            }, {
                header : i18n.get('label.classAuthor'),
                dataIndex : 'classAuthor',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.descriptionAction'),
                dataIndex : 'descriptionAction',
                width : 200,
                sortable : false
            }  ]
        });

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboParents, {
                text : i18n.get('label.add'),
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
            } ]
        };

        this.bbar = {
                xtype : 'paging',
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : i18n.get('paging.display'),
                emptyMsg : i18n.get('paging.empty')
            };
        
        this.listeners = {
            scope : this, 
            rowDblClick : this.onModify
        };
        sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel.superclass.initComponent.call(this);

    },

    /**
     * done a specific render to load resources plugins from the store. 
     */ 
    onRender : function () {
        sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel.superclass.onRender.apply(this, arguments);        
    },

    /**
     * Open a {sitools.admin.resourcesPlugins.resourcesPluginsProp} resource plugin property window
     *  to create a new resource for the selected dataset in the comboBox
     */
    onCreate : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var urlParent = this.urlParents + "/" + parentId;    
        
        var up = new sitools.admin.resourcesPlugins.resourcesPluginsProp({
            action : 'create',            
            parentPanel : this,          
            urlResources : this.urlResources,
            urlResourcesCRUD : this.httpProxyResources.url,
            urlParent : urlParent,
            parentType : this.parentType,
            appClassName : this.appClassName,
            idParent : parentId
        });
        up.show();
    },

    /**
     * Open a {sitools.admin.resourcesPlugins.resourcesPluginsProp} resource plugin property window
     *  to modify a resource for the selected dataset in the comboBox
     */
    onModify : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var rec = this.getSelectionModel().getSelected();
        
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        if ("ACTIVE" === rec.data.status) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.wrongStatus'));
            return;
        }
        var urlParent = this.urlParents + "/" + parentId;
        var up = new sitools.admin.resourcesPlugins.resourcesPluginsProp({
            action : 'modify',
            record : rec,
            parentPanel : this,          
            urlResources : this.urlResources,
            urlResourcesCRUD : this.httpProxyResources.url,
            urlParent : urlParent,
            appClassName : this.appClassName,
            idParent : parentId,
            parentType : this.parentType
        });
        up.show();

    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        if (Ext.isEmpty(this.comboParents.getValue())) {
            return;
        }
        var parentId = this.comboParents.getValue();
        
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('resourcesPlugins' + this.parentType + 'Crud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec, parentId);
                }
            }

        });
    },

    /**
     * done the delete of the passed record
     * @param rec the record to delete
     */
    doDelete : function (rec, parentId) {
        Ext.Ajax.request({
            url : this.urlParents + "/" + parentId + this.resourcesUrlPart + "/" + rec.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }
});

Ext.reg('s-plugins_resources', sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel);
