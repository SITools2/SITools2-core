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
Ext.namespace('sitools.component.dictionary.gridPanel');
/**
 * @param template
 *            the dictionary template
 * @param editable
 *            true if the grid is editable, false otherwise
 * @param url
 *            the url of the store
 * @class sitools.component.dictionary.gridPanel
 * @extends Ext.grid.GridPanel
 */
sitools.component.dictionary.gridPanel = Ext.extend(Ext.grid.EditorGridPanel, {
    template : null,
    initComponent : function () {
        this.store = this.getStoreConcepts(this.template, this.url);
        this.cm = this.getColumnModelConcepts(this.template, this.editable);
        
        sitools.component.dictionary.gridPanel.superclass.initComponent.call(this);
    },
    
    getStoreConcepts : function (template, url) {
        
        var fields = [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            } ];
        
        for (var i = 0; i < template.properties.length; i++) {
            var property = template.properties[i];
            
            fields.push({
                name : property.name,
                type : 'string'
            });
        }
        
        var storeConcept = new Ext.data.JsonStore({
			idProperty : 'id',
			fields : fields,
			url : url,
			// on surchage la fonction load
			loadConcepts : function (options) {
                Ext.Ajax.request({
                    url : this.url,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var data = Ext.decode(ret.responseText).dictionary;
                        var nbConcepts = 0;
                        if (!Ext.isEmpty(data.concepts)) {
							for (i = 0; i < data.concepts.length; i++) {
								nbConcepts++;
								var conceptIn = data.concepts[i];
								var conceptOut = {
									id : conceptIn.id,
									name : conceptIn.name,
									description : conceptIn.description
								};
			
								for (var j = 0; j < conceptIn.properties.length; j++) {
									var property = conceptIn.properties[j];
									conceptOut[property.name] = property.value;
								}
			
								var rec = new Ext.data.Record(conceptOut, conceptOut.id);
								this.add(rec);
							}
                            
                        }
                        this.fireEvent("load", this);
                    }, 
                    failure : alertFailure
                });  
			}
		});
        
        return storeConcept;
        
        
    },
    
    getColumnModelConcepts : function (template, editable) {
        var columns = [];
        columns.push(this.createColumn(i18n.get("headers.name"), 'name', 100,
			editable, false));
        columns.push(this.createColumn(i18n.get("headers.description"), 'description', 120,
            editable, true));
        
        for (var i = 0; i < template.properties.length; i++) {
            var property = template.properties[i];
            columns.push(this.createColumn(property.name, property.name, 80,
            editable, true));            
        }
        
        var cmConcepts = new Ext.grid.ColumnModel({
            columns : columns,
            defaults : {
                sortable : true,
                width : 100,
                editor : new Ext.form.TextField({
                    allowBlank : false
                })
            }
        });
        return cmConcepts;
    },
    
    createColumn : function (header, dataIndex, width, editable, allowBlank) {
        var column = {
			header : header,
			dataIndex : dataIndex,
			width : width,
			editor : (editable === true) ? (new Ext.form.TextField({
				allowBlank : allowBlank
			})) : null
		};
		return column;
	},
    
    getProxy : function () {
        return this.proxyGrid;
    }
        
});

