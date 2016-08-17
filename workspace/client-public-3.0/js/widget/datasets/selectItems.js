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
/*global Ext, sitools, i18n,document*/
Ext.namespace('sitools.public.widget.datasets');

Ext.define('sitools.public.widget.datasets.selectItems', {
    extend : 'Ext.panel.Panel',
    grid1 : null, 
    grid2 : null, 
    defaultRecord : null, 
    defaultFlexGrid : 0.47,
    border : false,
    
    initComponent : function () {

        var commandPanel = Ext.create('Ext.panel.Panel', {
            layout : {
                type : 'vbox',
                align : "center", 
                pack : "center", 
                defaultMargins : {top:10, right:0, bottom:0, left:0}
            },
            flex : 0.06,
            border : false,
            defaults : {
                scope : this
            },
            items : [ {
                xtype : 'button',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/simple-arrow-right-white.png',
                handler : this._onAdd
            }, {
                xtype : 'button',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/double-arrow-right-white.png',
                handler : this._onAddAll
            }, {
                xtype : 'button',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/simple-arrow-left-white.png',
                handler : this._onRemove
            }, {
                xtype : 'button',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/double-arrow-left-white.png',
                handler : this._onRemoveAll
            } ]
        });
        
        Ext.applyIf(this.grid1, {
            flex : this.defaultFlexGrid
        });
        Ext.applyIf(this.grid2, {
            flex : this.defaultFlexGrid
        });
        
        this.items =  [ this.grid1, commandPanel, this.grid2 ];
        this.layout = {
            type : 'hbox',
            flex : 'ratio' , 
            align : 'stretch'
        };
        
        this.callParent(arguments);
    },
    
    _onAdd : function () {
        var store2 = this.grid2.getStore();
        var recs = [];
        
        if (this.grid1 instanceof Ext.grid.Panel) {
            recs = this.grid1.getSelectionModel().getSelection();
        }
        
        if (this.grid1 instanceof Ext.tree.Panel) {
            var treeNodes = this.grid1.getSelectionModel().getSelection();
            
            Ext.each (treeNodes, function (node) {
                var attributes = Ext.apply(node.raw, {
                    columnAlias : node.raw.name.toLowerCase(), 
                    dataIndex : this.buildDataIndex(node), 
                    sqlColumnType : node.raw.type, 
//                    tableName : this.grid1.getRootNode().raw.collection
                    tableName : this.grid1.collection.name
                });
                recs.push(node);
            }, this)
        }
        
        if (recs.length === 0) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        var recTmp;
        Ext.each(recs, function (rec) {
            recTmp = rec.copy();
            recTmp.id = Ext.data.Record.id(recTmp);
            
            Ext.each(this.defaultRecord, function (property) {
                if (!recTmp.get(property.name)) {
                    recTmp.set(property.name, property.value);
                }
            }, this);
            
            // Iterate over raw properties which are not copied automatically
            Ext.iterate(recTmp.raw, function (key, value) {
                recTmp.set(key, value);
            });
            
            store2.add(recTmp);
        }, this);

//        this.grid2.getView().refresh();

    },
    _onAddAll : function () {
        var store2 = this.grid2.getStore();
        var recs = [];
        
        if (this.grid1 instanceof Ext.grid.Panel) {
            var store1 = this.grid1.getStore();
            recs = store1.data.items;
        }
        if (this.grid1 instanceof Ext.tree.Panel) {
            var treeNodes = this.grid1.getRootNode().childNodes;
            var RecType = store2.recordType;
            Ext.each (treeNodes, function (node) {
                var attributes = Ext.apply(node.raw, {
                    columnAlias : node.raw.name.toLowerCase(), 
                    dataIndex : this.buildDataIndex(node), 
                    sqlColumnType : node.raw.type, 
//                    tableName : node.getOwnerTree().getRootNode().raw.collection
                    tableName : this.grid1.collection.name
                });
//                recs.push(new RecType(node.attributes));
                recs.push(node);
            }, this)
        }
        var recTmp;
        Ext.each(recs, function (rec) {
            recTmp = rec.copy();
            recTmp.id = Ext.data.Record.id(recTmp);
            
            Ext.each(this.defaultRecord, function (property) {
                if (!recTmp.get(property.name)) {
                    recTmp.set(property.name, property.value);
                }
            }, this);
            
            // Iterate over raw properties which are not copied automatically
            Ext.iterate(recTmp.raw, function (key, value) {
                recTmp.set(key, value);
            });
            
            store2.add(recTmp);
        }, this);


    },
    _onRemove : function () {
        var store2 = this.grid2.getStore();
        var recs = this.grid2.getSelectionModel().getSelection();
        if (recs.length === 0) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        Ext.each(recs, function (rec) {
            store2.remove(rec);
        });

        this.grid2.getView().refresh();

    },
    _onRemoveAll : function () {
        var store2 = this.grid2.getStore().removeAll();
//        store2.each(function (rec) {
//            store2.remove(rec);
//        });
    }, 
    setFirstGrid : function (grid) {
        this.remove(this.grid1);
        Ext.applyIf(grid, {
            flex : this.defaultFlexGrid
        });
        this.grid1 = grid;
        this.insert(0, this.grid1);
        this.doLayout();
    }, 
    getFirstGrid : function () {
        return this.grid1;
    }, 
    getSecondGrid : function () {
        return this.grid2;
    }, 
    buildDataIndex : function (node, currentName) {
        if (Ext.isEmpty(currentName)) {
            currentName = node.raw.name;
        }
        while (node.parentNode && !node.parentNode.isRoot) {
            node = node.parentNode;
            currentName = node.raw.name + "." + currentName;
            this.buildDataIndex(node, currentName)
        }
        return currentName;
    }
});
