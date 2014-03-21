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

/**
 * A window to visualize a Mongo DB database. 
 * show 2 panels :  
 *  - sitools.admin.datasource.mongoDb.CollectionExplorer
 *  - sitools.admin.datasource.mongoDb.RecordsPanel
 * @cfg {} database An object representing the Database mongo DB 
 * @class sitools.admin.datasource.mongoDb.DataBaseExplorer
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasource.mongoDb.DataBaseExplorer', { extend : 'Ext.Window',
    width : 800,
    height : 500,
    closable : true,
    layout : 'hbox', 
    layoutConfig : {
		pack : "start", 
		align : "stretch"
	},
	
    initComponent : function () {
        this.title = i18n.get('label.databaseExplorer');
        
        var storeCombo = new Ext.data.JsonStore({
            fields : [ 'name', 'url' ],
			url : this.database.sitoolsAttachementForUsers + "/collections",
			root : "mongodbdatabase.collections",
			autoLoad : true,
			listeners : {
				scope : this,
				load : function (store, recs) {
					if (recs.length !== 0) {
						this.combobox.setValue(recs[0]
								.get('name'));
						this.combobox.fireEvent("select",
								this.combobox, recs[0], 0);
					}
				}
			}
        });

        /**
         * The collection ComboBox
         */
        this.combobox = new Ext.form.ComboBox({
            store : storeCombo,
            fieldLabel : i18n.get('label.selectCollection'), 
            displayField : 'name',
            valueField : 'name',
            typeAhead : true,
            mode : 'local',
            emptyText : i18n.get('label.selectACollection'), 
            forceSelection : true,
            triggerAction : 'all',
			selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.collection = rec.data;
                    this.loadCollection();
                }

            }
        });
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
				xtype : "label", 
				text : i18n.get('label.selectCollection') + " : " 
            }, this.combobox]
        };

        
        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];
        this.listeners = {
			scope : this, 
			metadataLoaded : function (node) {
				this.displayRecords(node);
			}
		};
        sitools.admin.datasource.mongoDb.DataBaseExplorer.superclass.initComponent.call(this);
    }, 
    /**
     * Called when a collection is choosen. 
     * Builds a sitools.admin.datasource.mongoDb.CollectionExplorer panel
     * @returns
     */
    loadCollection : function () {
		if (!Ext.isEmpty(this.metadataPanel)) {
			this.remove(this.metadataPanel);
		}
		this.metadataPanel = new sitools.admin.datasource.mongoDb.CollectionExplorer({
			collection : this.collection,
			observer : this, 
			flex : 1
		});
		
		this.add(this.metadataPanel);
		this.doLayout();
    }, 
    /**
     * Called when event metadataLoaded is fired. 
     * Builds a sitools.admin.datasource.mongoDb.RecordsPanel if possible. 
     * If any errors occurs, remove all children. 
     * @param {Ext.tree.TreeNode} node The ExtJs Node containing metadata to build the columnModel and store.
     * @returns
     */
    displayRecords : function (node) {
		if (!Ext.isEmpty(this.recordsPanel)) {
			this.remove(this.recordsPanel);
		}
		try {
			this.recordsPanel = new sitools.admin.datasource.mongoDb.RecordsPanel({
				collection : this.collection,
				node : node, 
				flex : 2
			});
			this.add(this.recordsPanel);
			this.doLayout();			
		}
		catch (err) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.displayRecordsProblem'));
			this.removeAll();
			this.doLayout();
		}

    }
});
