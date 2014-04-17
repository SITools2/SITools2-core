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
Ext.namespace('sitools.component.graphs');

Ext.define('sitools.component.graphs.graphsDatasetWin', { 
    extend : 'Ext.Window',
    width : 350,
    modal : true,
    closable : false,
    pageSize : 10,

    initComponent : function () {
        this.title = i18n.get('label.selectDataset');
        
        this.store = Ext.create("Ext.data.JsonStore", {
            proxy : {
                type : 'ajax',
                url : this.url,
                simpleSortMode : true,
                reader : {
                    type : 'json',
                    root : 'project.dataSets'
                }
            },
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
                name : 'url',
                type : 'string'
            }, {
                name : 'properties'
            } ]
        });
        
        this.grid = Ext.create("Ext.grid.GridPanel", {
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : 'SINGLE'
            }),
            forceFit : true,
            store : this.store,
            height : 200,
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name'
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description'
            } ],
            listeners : {
                scope : this,
                viewready : function () {
                    if (this.mode == 'edit') {
                        var index = this.store.find('id', this.node.get("datasetId"));
                        this.grid.getSelectionModel().select(index);
                    }
                }
            }
        });
        this.items = [ {
            xtype : 'panel',
            items : [ this.grid ],
            bbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ '->', {
                    text : i18n.get('label.ok'),
                    handler : this._onOK
                }, {
                    text : i18n.get('label.cancel'),
                    handler : this._onCancel
                } ]
            }
        } ];
        // this.relayEvents(this.store, ['destroy', 'save', 'update']);
        sitools.component.graphs.graphsDatasetWin.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.component.graphs.graphsDatasetWin.superclass.onRender.apply(this, arguments);
        this.store.load({
            scope : this,
            params : {
                start : 0,
                limit : this.pageSize
            },
            callback : function (r, options, success) {
                if (!success) {
                    this.close();
                    Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.error.project.nodataset'));
                }
            }
        });
    },

    _onOK : function () {
        var dataset = this.grid.getSelectionModel().getSelection()[0];
        
        if (Ext.isEmpty(dataset)) {
            return;
        }
        
		var properties = dataset.get("properties");
		var nbRecord = 0;
        var imageDs;
        var descriptionHTML;
		if (!Ext.isEmpty(properties)) {
			Ext.each(properties, function (property) {
				if (property.name == "nbRecord") {
					nbRecord = property.value;
				}
                if (property.name == "imageUrl") {
                    imageDs = property.value;
                }
                if (property.name == "descriptionHTML") {
                    descriptionHTML = property.value;
                }
			});
		}
		var node;
		if (this.mode == 'create') {
		     node = Ext.create('sitools.component.graphs.graphNodeModel', {
		         leaf : true
		     });
		}
		else {
		    node = this.node;
		}
		
		node.set("text", dataset.get("name"));
		node.set("datasetId", dataset.get("id"));
		node.set("visible", dataset.get("visible"));
		node.set("status", dataset.get("status"));
		node.set("nbRecord", nbRecord);
		node.set("type", "dataset");
		node.set("imageDs", imageDs);
		node.set("readme", descriptionHTML);
		node.set("url", dataset.get("url"));
		

		if (!this.node.isExpanded()) {
            this.node.expand();
        }
		
		if (this.mode == 'create') {
		    this.node.appendChild(node);
		}
        
        this.close();
    },

    _onCancel : function () {
        this.destroy();
    }

});
