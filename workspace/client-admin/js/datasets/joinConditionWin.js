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
 showHelp, predicatOperators*/
Ext.namespace('sitools.admin.datasets');

/**
 * Window used to define a joinCondition between two tables of a dataset.
 * @cfg {Ext.tree.TreeNode} node (required) the parent node where to add join Condition.
                storeColumnDataset 
 * @cfg {String} mode (required) should be "edit" or "create"
 * @cfg {Ext.data.Store} storeColumnDataset (required) the store of dataset columns
 * @class sitools.admin.datasets.joinConditionWin
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.joinConditionWin', { extend : 'Ext.Window',
    // url + mode + storeref
    width : 650,
    modal : true,
    closable : false,
    pageSize : 10,
	resizable : false, 
    id : 'joinConditionWin', 
    initComponent : function () {
        this.storeColumnDataset.filterBy(function (rec) {
            return rec.data.specificColumnType == "DATABASE";
        });
        
        var defaultPredicat;
        try {
			defaultPredicat = this.node.attributes.predicat || {};
        }
        catch (err) {
			defaultPredicat = {};
        }
        this.logicOperator = new Ext.form.ComboBox({
		    typeAhead : false,
		    triggerAction : 'all',
		    lazyRender : true,
		    mode : 'local',
		    width : 50, 
		    store : new Ext.data.ArrayStore({
		        id : 0,
		        fields : [ 'myId', 'displayText' ],
		        data : [ [ '', '' ], [ 'on', 'on' ], [ 'and', 'and' ], [ 'or', 'or' ] ]
		    }),
		    valueField : 'myId',
		    displayField : 'displayText', 
		    value : Ext.isEmpty(defaultPredicat.logicOperator) ? "on" : defaultPredicat.logicOperator
		});
		var myTpl = new Ext.XTemplate('<tpl for=".">', 
			'<div class="x-combo-list-item">', 
				'<tpl if="this.isNull(tableAlias)">', 
					'<tpl if="this.isNotNull(tableName)">{[values.tableName.toUpperCase()]}.</tpl>',
				'</tpl>',
				'<tpl if="this.isNotNull(tableAlias)">[values.tableAlias.toUpperCase()].</tpl>',
				'{columnAlias}</div>',
		'</tpl>', 
		{
			compiled : true, 
			isNull : function (value) {
				return Ext.isEmpty(value);
			}, 
			isNotNull : function (value) {
				return !Ext.isEmpty(value);
			}, 
			isDatabase : function (value) {
				return value == "DATABASE";
			}
		});
		this.leftAttribute = new Ext.form.ComboBox({
			fieldLabel : "left",
			typeAhead : false,
		    triggerAction : 'all',
		    forceSelection : true, 
		    id : "leftAttributeField", 
		    flex : 2, 
		    lazyRender : true,
		    mode : 'local',
		    displayField : 'columnAlias', 
		    valueField : 'columnAlias', 
		    store : this.storeColumnDataset, 
		    value : defaultPredicat.leftAttribute ? defaultPredicat.leftAttribute.columnAlias : null, 
		    tpl : myTpl
			
		});
		this.compareOperator = new Ext.form.ComboBox({
		    typeAhead : false,
		    forceSelection : true, 
		    triggerAction : 'all',
		    lazyRender : true,
		    mode : 'local',
		    width : 50, 
		    store : new Ext.data.ArrayStore({
		        id : 1,
		        fields : [ 'myId', 'displayText' ],
		        data : predicatOperators.operators
		    }),
		    valueField : 'myId',
		    displayField : 'displayText', 
            hiddenName : 'compareOperator',
		    value : Ext.isEmpty(defaultPredicat.compareOperator) ? "EQ" : defaultPredicat.compareOperator
		});

		this.rightAttribute = new Ext.form.ComboBox({
			fieldLabel : "left",
			typeAhead : false,
		    triggerAction : 'all',
		    forceSelection : true, 
		    lazyRender : true,
		    id : "rightAttributeField", 
		    mode : 'local',
		    flex : 2, 
		    displayField : 'columnAlias',
		    valueField : 'columnAlias', 
            
		    store : this.storeColumnDataset, 
		    value : defaultPredicat.rightAttribute ? defaultPredicat.rightAttribute.columnAlias : null, 
		    tpl : myTpl
		});
		var form = new Ext.form.FormPanel({
	        labelWidth : 100, // label settings here cascade unless overridden
	        bodyStyle : 'padding:5px 5px 0',
	        width : 640, 
			items : [{
				xtype : 'compositefield', 
				fieldLabel : i18n.get('label.joinCondition'),
				defaults : {
					flex : 1
				}, 
				items : [this.logicOperator, this.leftAttribute, this.compareOperator, this.rightAttribute]
			}]

		});
		this.title = i18n.get('label.joinCondition');
        this.items = [form];
        this.buttons = [ {
            text : i18n.get('label.ok'),
            handler : this._onOK, 
            scope : this
        }, {
            text : i18n.get('label.cancel'),
            handler : this._onCancel, 
            scope : this
        } ]
		;
        // this.relayEvents(this.store, ['destroy', 'save', 'update']);
        sitools.admin.datasets.joinConditionWin.superclass.initComponent.call(this);
    },

    /**
     * When click on ok button. 
     * Depending on mode, edit or create a node 
     */
    _onOK : function () {
        this.storeColumnDataset.clearFilter();
        var rightColumn = this.rightAttribute.getStore().getAt(this.rightAttribute.getStore().findExact("columnAlias", this.rightAttribute.getValue())).data;
        var leftColumn = this.leftAttribute.getStore().getAt(this.leftAttribute.getStore().findExact("columnAlias", this.leftAttribute.getValue())).data;
        
//        var compareOperatorStore = this.compareOperator.getStore();
//        var compareOperatorIndex = compareOperatorStore.find("displayText", this.compareOperator.getValue());
//        var compareOperator = compareOperatorStore.getAt(compareOperatorIndex).get("myId");
//        
        var predicat = {
			logicOperator : this.logicOperator.getValue(), 
			rightAttribute : {
                id : rightColumn.id,
                dataIndex : rightColumn.dataIndex,
                header : rightColumn.header,
                toolTip : rightColumn.toolTip,
                width : rightColumn.width,
                sortable : rightColumn.sortable,
                visible : rightColumn.visible,
                filter : rightColumn.filter,
                sqlColumnType : rightColumn.sqlColumnType,
                columnOrder : rightColumn.columnOrder,
                primaryKey : rightColumn.primaryKey,
                schema : rightColumn.schemaName,
                tableAlias : rightColumn.tableAlias,
                tableName : rightColumn.tableName,
                specificColumnType : rightColumn.specificColumnType,
                columnAlias : rightColumn.columnAlias, 
                datasetDetailUrl : rightColumn.datasetDetailUrl, 
                columnAliasDetail : rightColumn.columnAliasDetail, 
                notion : rightColumn.notion,
                javaSqlColumnType : rightColumn.javaSqlColumnType,
                columnClass : rightColumn.columnClass,
                image : rightColumn.image, 
                dimensionId : rightColumn.dimensionId, 
                unit : rightColumn.unit
			}, 
			leftAttribute : {
                id : leftColumn.id,
                dataIndex : leftColumn.dataIndex,
                header : leftColumn.header,
                toolTip : leftColumn.toolTip,
                width : leftColumn.width,
                sortable : leftColumn.sortable,
                visible : leftColumn.visible,
                filter : leftColumn.filter,
                sqlColumnType : leftColumn.sqlColumnType,
                columnOrder : leftColumn.columnOrder,
                primaryKey : leftColumn.primaryKey,
                schema : leftColumn.schemaName,
                tableAlias : leftColumn.tableAlias,
                tableName : leftColumn.tableName,
                specificColumnType : leftColumn.specificColumnType,
                columnAlias : leftColumn.columnAlias, 
                datasetDetailUrl : leftColumn.datasetDetailUrl, 
                columnAliasDetail : leftColumn.columnAliasDetail, 
                notion : leftColumn.notion,
                javaSqlColumnType : leftColumn.javaSqlColumnType,
                columnClass : leftColumn.columnClass,
                image : leftColumn.image, 
                dimensionId : leftColumn.dimensionId, 
                unit : leftColumn.unit
			}, 
			compareOperator : this.compareOperator.getValue()
        };
        if (this.mode == 'edit') {
            this.node.attributes.predicat = predicat;
            this.node.setText(this.getNodeText(predicat));
            
        } else {
            var newNode = {
                leaf : true, 
                predicat : predicat, 
                text : this.getNodeText(predicat), 
                type : "join"
            };

            if (!this.node.isExpanded()) {
                this.node.expand();
            }
			if (this.node.childNodes.length !== 0) {
				this.node.insertBefore(newNode, this.node.findChild("leaf", false));	
			}
            else {
				this.node.appendChild(newNode);
            }
            
        }
        this.destroy();
    },
	/**
	 * return a string from a predicat.
	 * @param {} predicat
	 * @return {String}
	 */
	getNodeText : function (predicat) {
		predicat.leftAttribute = predicat.leftAttribute || {};
		predicat.rightAttribute = predicat.rightAttribute || {};
        
        var compareOperator = predicatOperators.getOperatorValueForClient(predicat.compareOperator);
		
		return String.format("{0} {1} {2} {3}", 
			predicat.logicOperator, 
			this.getDisplayName(predicat.leftAttribute), 
			compareOperator, 
			this.getDisplayName(predicat.rightAttribute));
		
	},
    /**
     * Close this window
     */
    _onCancel : function () {
        this.storeColumnDataset.clearFilter();
        this.destroy();
    }, 
    /**
     * get the string to display from a column
     * @param {} column
     * @return {String}
     */
    getDisplayName : function (column) {
		if (column.specificColumnType == "DATABASE") {
			return String.format("{0}.{1}", 
			Ext.isEmpty(column.tableAlias) ? column.tableName: column.tableAlias, 
			column.columnAlias);
		}
		else {
			return column.columnAlias;
		}
    }

});
