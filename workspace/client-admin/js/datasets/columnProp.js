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
//
// Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
//
// This file is part of SITools2.
//
// SITools2 is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// SITools2 is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
//

/*global Ext, sitools, ID, i18n, document, showResponse*/
Ext.namespace('sitools.admin.datasets');

/**
 * @cfg {String} action (required) should be 'create' or 'modifiy'
 * @cfg {Ext.data.JsonStore} store (required) the store of the columns of the dataset
 * @cfg {Ext.data.Record} recordColumn (required) the selected record
 * @class sitools.admin.datasets.columnsPropPanel
 * @extends Ext.Window
 */
sitools.admin.datasets.columnsPropPanel = Ext.extend(Ext.Window, {
    width : 600,
    height : 300,
    modal : true,
    id : 'columnPropId',
    dataSets : "",

    initComponent : function () {
        this.title = i18n.get('label.details');

        var data = this.datasourceUtils.getColumnsType();
        
        var comboStore = new Ext.data.ArrayStore({
            fields : [ 'value', 'display' ],
            data : data
        });
        
		this.comboColumnType = new Ext.form.ComboBox({
            store : comboStore,
            mode : 'local',
            fieldLabel : i18n.get('label.specificColumnType'),
            typeAhead : true,
            triggerAction : 'all',
            forceSelection : true,
            selectOnFocus : true,
            lazyRender : true,
            listClass : 'x-combo-list-small',
            valueField : 'value',
            displayField : 'display',
            anchor : '90%',
            value : this.datasourceUtils.isJdbc ? 'SQL' : 'DATABASE',
            listeners : {
                scope : this,
                change : function (field, newValue, oldValue) {
                    if (newValue === 'SQL' || newValue === "DATABASE") {
                        this.sqlDef.setDisabled(false);
                    } else {
                        this.sqlDef.setDisabled(true);
                    }
                }
            }
        });
        this.columnAlias = new Ext.form.TextField({
            fieldLabel : i18n.get('label.columnAlias'),
            anchor : '90%', 
            validator : function (v) {
                var re = new RegExp("^.*[!\"#$%&\'/()*+,:;<=>?@\\`{}|~]+.*$");
                if (!re.test(v)) {
                    return !re.test(v);
                }
                else {
                    return i18n.get('label.invalidColumnAlias');
                }
                
            }
        });
        this.sqlDef = new Ext.form.TextArea({
            fieldLabel : i18n.get('label.SQLDefinition'),
            anchor : '90%'
        });

        var storeColumnType = new Ext.data.JsonStore({
			fields : ['name'], 
        	data : JAVA_TYPES
        });
        
        this.sqlColumnType = new Ext.form.ComboBox({
            store : storeColumnType,
            mode : 'local',
            fieldLabel : i18n.get('label.columnType'),
            typeAhead : true,
            triggerAction : 'all',
            forceSelection : true,
            selectOnFocus : true,
            lazyRender : true,
            listClass : 'x-combo-list-small',
            valueField : 'name',
            displayField : 'name',
            anchor : '90%',
            value : "String"
        });
        
        this.items = [ {
            xtype : 'panel',
            height : 250,
            items : [ {
                xtype : 'form',
                border : false,
                labelWidth : 150,
                padding : 10,
                defaults : {
                    disabled : false
                },
                items : [ this.comboColumnType, this.columnAlias, this.sqlDef, this.sqlColumnType]
            } ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : function () {
                    this.onValidate();
                }
            }, {
                text : i18n.get('label.close'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        } ];
        sitools.admin.datasets.columnsPropPanel.superclass.initComponent.call(this);
    },

    /**
     * load value depends on action value
     * @inheritdoc
     * @method
     */
    onRender : function () {
        sitools.admin.datasets.columnsPropPanel.superclass.onRender.apply(this, arguments);
        if (this.action == 'modify') {
            this.comboColumnType.setValue(this.recordColumn.get('specificColumnType'));
            this.columnAlias.setValue(this.recordColumn.get('columnAlias'));
            this.sqlDef.setValue(this.recordColumn.get('dataIndex'));
            this.sqlColumnType.setValue(this.recordColumn.get('sqlColumnType'));
            // f.loadRecord (this.recordColumn);
        }
    },
    /**
     * Validate the changes. 
     * <li>on modify mode : change the record value</li>
     * <li>on create mode : create a new Record in the store given as config</li>
     * @method
     */
    onValidate : function () {
        if (this.action == 'modify') {
            this.recordColumn.set('specificColumnType', this.comboColumnType.getValue());
            this.recordColumn.set('columnAlias', this.columnAlias.getValue());
            this.recordColumn.set('dataIndex', this.sqlDef.getValue());
            this.recordColumn.set('sqlColumnType', this.sqlColumnType.getValue());
        } else {
            var rec;
            if (this.comboColumnType.getValue() == 'DATABASE') {
                rec = new Ext.data.Record({
                    dataIndex : this.sqlDef.getValue(),
                    specificColumnType : this.comboColumnType.getValue(),
                    header : this.columnAlias.getValue(),
                    columnAlias : this.columnAlias.getValue(),
                    width : 100,
                    visible : true,
                    sortable : true, 
                    tableName : "", 
                    tableAlias : "", 
                    sqlColumnType : this.sqlColumnType.getValue()
                });
            } 
            
            if (this.comboColumnType.getValue() == 'SQL') {
                rec = new Ext.data.Record({
                    dataIndex : this.sqlDef.getValue(),
                    specificColumnType : this.comboColumnType.getValue(),
                    header : this.columnAlias.getValue(),
                    columnAlias : this.columnAlias.getValue(),
                    width : 100,
                    visible : true,
                    sortable : true, 
                    tableName : "", 
                    tableAlias : "", 
                    sqlColumnType : this.sqlColumnType.getValue()
                });
            } 
            
            if (this.comboColumnType.getValue() === "VIRTUAL") {
                rec = new Ext.data.Record({
                    specificColumnType : this.comboColumnType.getValue(),
                    header : this.columnAlias.getValue(),
                    columnAlias : this.columnAlias.getValue(),
                    width : 100,
                    visible : true,
                    sortable : true, 
                    tableName : "", 
                    tableAlias : ""
                });
            }
            this.store.add(rec);
        }
        this.close();
    }

});
