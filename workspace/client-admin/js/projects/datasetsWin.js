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
Ext.namespace('sitools.admin.projects');

/**
 * A Panel to show all the datasets in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {String}, the type of mode
 * @cfg {Ext.data.JsonStore} storeDatasets, the store with all datasets of the project
 * @class sitools.admin.projects.datasetsWin
 * @extends Ext.Window
 */
Ext.define('sitools.admin.projects.datasetsWin', { 
    extend : 'Ext.Window',
    // url + mode + storeref
    width : 500,
    modal : true,
    closable : false,
    pageSize : 10,
//    id : 'projectsDatasetWinId',

    initComponent : function () {
        this.title = i18n.get('label.datasets');

        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            autoSave : false,
            idProperty : 'id',
            totalProperty : 'total',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'),
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'mediaType',
                type : 'string'
            }, {
                name : 'visible',
                type : 'boolean'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'nbRecords', 
                type : "string"
            }, {
                name : 'sitoolsAttachementForUsers',
                type : 'string'
            }, {
                name : 'image'
            }, {
                name : 'descriptionHTML', 
                type : "string"
            } ]
        });
        this.grid = new Ext.grid.GridPanel({
            selModel : Ext.create('Ext.selection.RowModel'),
            store : this.store,
            height : 200,
            forceFit : true,
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description'
            } ]
        });
        
        this.items = [ {
            xtype : 'panel',
            title : i18n.get('label.selectDataset'),
            items : [ this.grid ],
            bbar : {
                xtype : 'pagingtoolbar',
				pageSize : this.pageSize,
	            store : this.store,
	            displayInfo : true,
	            displayMsg : i18n.get('paging.display'),
	            emptyMsg : i18n.get('paging.empty')
            }
        } ];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this, 
            handler : this._onOK
        }, {
            text : i18n.get('label.cancel'),
            scope : this, 
            handler : this._onCancel
        } ];
        // this.relayEvents(this.store, ['destroy', 'save', 'update']);
        sitools.admin.projects.datasetsWin.superclass.initComponent.call(this);
    },

    /**
     * done a specific render to load datasets informations. 
     */
    onRender : function () {
        sitools.admin.projects.datasetsWin.superclass.onRender.apply(this, arguments);
        this.store.load({
            scope : this,
            params : {
                start : 0,
                limit : this.pageSize
            },
            callback : function (r, options, success) {
                if (!success) {
                    this.close();
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.loadError'));
                }
            }
        });
    },

    /**
     * Add selected Datasets to the project
     */
    _onOK : function () {
        Ext.each(this.grid.getSelectionModel().getSelections(), function (dataset) {
            if (this.storeDatasets.find('id', dataset.data.id) == -1) {
                var image = dataset.data.image;
                
                var properties = [{
					name : "nbRecord", 
					value : dataset.data.nbRecords
                }, {
                    name : "descriptionHTML", 
                    value : dataset.data.descriptionHTML
                }];
                
                if (!Ext.isEmpty(image)) {
                    properties.push({
                        name : "imageUrl",
                        value : image.url                       
                    });
                
                }
                this.storeDatasets.add({
                    id : dataset.data.id,
                    name : dataset.data.name,
                    description : dataset.data.description,
                    type : dataset.data.type,
                    mediaType : dataset.data.mediaType, 
                    visible : dataset.data.visible, 
                    status : dataset.data.status, 
                    properties : properties,
                    url : dataset.data.sitoolsAttachementForUsers
                });
            }
        }, this);
        this.destroy();
    },

    /**
     * Close the window
     */
    _onCancel : function () {
        this.destroy();
    }

});
