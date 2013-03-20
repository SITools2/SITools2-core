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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
Ext.namespace('sitools.admin.datasource.mongoDb');

/**
 * A tree Panel to represent a metadata coming from mongoDb resourceRepresentation. 
 * @cfg {} collection An object representing the collection mongo DB 
 * @class sitools.admin.datasource.mongoDb.CollectionExplorer
 * @extends Ext.tree.TreePanel
 */
sitools.admin.datasource.mongoDb.CollectionExplorer = Ext.extend(Ext.tree.TreePanel, {
    
    initComponent : function () {
		var url = !Ext.isEmpty(this.collection) ? this.collection.url + "/metadata" : null;
		sitools.admin.datasource.mongoDb.CollectionExplorer.superclass.initComponent.call(Ext.apply(this, {
			title : i18n.get("label.metadata"), 
			useArrows : false,
            autoScroll : true,
            animate : true,
            selModel : new Ext.tree.MultiSelectionModel(), 
            root : {
                nodeType : 'async'
            },
            loader : new sitools.widget.rootTreeLoader({
                requestMethod : 'GET',
                root: "data", 
                url : url, 
                listeners : {
					scope : this,
					load : function (loader, node, response) {
						if (this.observer) {
							this.observer.fireEvent("metadataLoaded", node);
						}
					}
				}
            }),
            rootVisible : false,
            listeners : {
            	scope : this, 
                beforeload : function (node) {
					node.setText(node.attributes.name);
					node.attributes.collection = this.collection.name;
					return node.isRoot || Ext.isDefined(node.attributes.children);
                },
                load : function (node) {
					node.eachChild(function (item) {
						item.setText(item.attributes.name);
					    return true;
					});
                }
            }			
		}));
    }, 
    getLoader : function () {
    	return this.loader;
    }
});
