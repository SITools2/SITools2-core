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
Ext.namespace('sitools.admin.datasource.mongoDb');

Ext.define('sitools.admin.datasource.mongoDb.FieldsModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name : 'type'
    }, {
        name : 'text'
    }, {
        name : 'name'
    }]
});

/**
 * A tree Panel to represent a metadata coming from mong    oDb resourceRepresentation. 
 * @cfg {} collection An object representing the collection mongo DB 
 * @class sitools.admin.datasource.mongoDb.CollectionExplorer
 * @extends Ext.tree.TreePanel
 */
Ext.define('sitools.admin.datasource.mongoDb.CollectionExplorer', {
    extend : 'Ext.tree.Panel',
    useArrows : false,
    autoScroll : true,
    animate : true,
    rootVisible : false,
    
    initComponent : function () {
        this.title = i18n.get("label.metadata");
        
		this.url = !Ext.isEmpty(this.collection) ? this.collection.url + "/metadata" : null;
		
		this.store = Ext.create('Ext.data.TreeStore', {
            model : 'sitools.admin.datasource.mongoDb.FieldsModel',
//            root : {
//                text : this.name,
//                expanded : true,
//                leaf : false
//            },
            proxy : {
                type : 'memory',
//                url : this.url,
                reader : {
                    type : 'json',
                    root : 'children'
                }
            },
            listeners : {
                scope : this
//                load : function ( store, node, records, successful, eOpts ) {
//                    Ext.each(records, function (record) {
//                        this.createNode(record);                
//                    }, this);
//                }
            }
        });
		
		this.listeners = {
            scope : this,
            afterrender : function () {
                this.loadStore();
//                this.store.load();
            }
//            beforeappend : function (rootNode, node) {
//                node.set('leaf', Ext.isEmpty(node.get('children')));  
//            },
        };
		
//        loader : new sitools.widget.rootTreeLoader({
//            requestMethod : 'GET',
//            root: "data", 
//            url : url, 
//            listeners : {
//                scope : this,
//                load : function (loader, node, response) {
//                    if (this.observer) {
//                        this.observer.fireEvent("metadataLoaded", node);
//                    }
//                }
//            }
//        }),
        
//        listeners : {
//          scope : this, 
//            beforeload : function (node) {
//              node.setText(node.attributes.name);
//              node.attributes.collection = this.collection.name;
//              return node.isRoot || Ext.isDefined(node.attributes.children);
//            },
//            load : function (node) {
//              node.eachChild(function (item) {
//                  item.setText(item.attributes.name);
//                  return true;
//              });
//            }
//        } 
		
		sitools.admin.datasource.mongoDb.CollectionExplorer.superclass.initComponent.call(this);
    }, 
    
    loadStore : function () {
        Ext.Ajax.request({
            method : 'GET',
            url : this.url,
            scope : this,
            success : function (ret) {
                var jsonData = Ext.decode(ret.responseText).children;
                
                if (ret.status != 200) {
                    Ext.Msg.alert(i18n.get('label.warning'), ret.message);
                    return false;
                }
                
                Ext.each(jsonData, function (node) {
                    this.createNode(node);
                }, this);
                
                this.store.getRootNode().appendChild(jsonData);
            },
            callback : function () {
                if (this.observer) {
                    this.observer.fireEvent("metadataLoaded", this.getRootNode());
                }
            },
            failure : alertFailure
        });
    },
    
    loadNodeStore : function () {
        
    },
    
    createNode : function (node) {
        
        node.text = node.name;
        
        if (Ext.isEmpty(node.children)) {
            return;
        } else {
            Ext.each(node.children, function (record) {
                this.createNode(record);
            }, this);
        }
    },
    
    getLoader : function () {
    	return this.loader;
    }
});
