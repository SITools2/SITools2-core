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
Ext.namespace('sitools.component.graphs');

/**
 * Custom TreeLoader to deal with JSON returned from the server
 */
// TODO ExtJS3 Ext.tree.TreeLoader > ?
Ext.define('sitools.component.graphs.graphsTreeLoader', { 
    extend : 'Ext.tree.TreeLoader', 

    idGraph : null,

    createNode : function (attr) {
        return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
    },

    processResponse : function (response, node, callback, scope) {
        var json = response.responseText, children, newNode, i = 0, len;
        try {

            if (!(children = response.responseData)) {
                children = Ext.decode(json);
                this.idGraph = children.graph.id;

                if (this.root) {
                    if (!this.getRoot) {
                        this.getRoot = Ext.data.JsonReader.prototype.createAccessor(this.root);
                    }
                    children = this.getRoot(children);
                }
            }
            node.beginUpdate();
            for (len = children.length; i < len; i++) {
                newNode = this.createNode(children[i]);
                if (newNode) {
                    node.appendChild(newNode);
                }
            }
            node.endUpdate();
            this.runCallback(callback, scope || node, [ node ]);
        } catch (e) {
            this.handleFailure(response);
        }
    },

    getIdGraph : function () {
        return this.idGraph;
    }
});

