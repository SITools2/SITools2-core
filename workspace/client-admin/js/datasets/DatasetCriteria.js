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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser*/
Ext.namespace('sitools.admin.datasets');


/**
 * Define the window of the dataset Configuration
 * @cfg {String} url the Url to save the data (only when modify)
 * @cfg {String} action (required) "active", "modify" "view"
 * @cfg {Ext.data.Store} store (required) : the datasets store 
 * @cfg {String} urlDatasources The url of the JDBC datasources
 * @cfg {String} urlDatasourcesMongoDB The url of the MongoDB datasources
 * @class sitools.admin.datasets.DatasetForm
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.DatasetCriteria', { 
    extend: 'Ext.panel.Panel',
    
    requires : ['sitools.admin.datasets.JoinPanel',
                'sitools.admin.datasets.PredicatsPanel'],

    border : false,
    bodyBorder : false,

    layout : 'fit',
    
	initComponent : function () {
		/**
         * The panel that displays the join conditions.
         * @type sitools.admin.datasets.JoinPanel
         */
        this.wizardJoinCondition = Ext.create('sitools.admin.datasets.JoinPanel', {
			datasetId : this.scope.datasetId, 
			datasetSelectTables : this.scope.panelSelectTables, 
			action : this.scope.action,
			storeColumnDataset : this.scope.gridFields.getStore(), 
			scope : this.scope
        });

        /**
         * the panel that displays the where clause
         * @type sitools.admin.datasets.PredicatsPanel
         */
        this.wizardWhereClause = Ext.create('sitools.admin.datasets.PredicatsPanel', {
			gridId : 'whereClauseId', 
			title : i18n.get('label.wizardWhereClause'), 
			storeSelectFields : this.scope.gridFields.getStore(), 
			type : 'where',
            padding : 5
        });

        /**
         * the panel that displays the SQL specific query.
         * @type Ext.Panel
         */
        this.SqlWhereClause = Ext.create('Ext.form.Panel', {
            height : 350,
            border : false,
            bodyBorder : false,
            padding : '7 7 7 7',
            items : [{
                xtype : 'label',
                html : i18n.get('label.enterQuery')
            }, {
                xtype : 'textarea',
                id : "sqlQuery",
                autoScroll : true,
                height : 350,
                anchor : '100%',
                name : "sqlQuery", 
                invalidText : i18n.get('label.invalidSQl'),
                validator : function (value) {
                    if (value.toLowerCase().match("where") || (value.toLowerCase().match("group by") && value.toLowerCase().match("having"))) {
                        if (value.toLowerCase().match("from")) {
                            return true;
                        }
                        else {
                            return i18n.get('label.invalidSQl');
                        }
                    }
                    else {
                        return i18n.get('label.invalidSQl');
                    }
                }
            }]
        });
        var selecteur = Ext.create('Ext.form.Panel', {
//            height : 60, 
//            flex : 0.1,
            padding : '7 7 7 7',
            id : "selecteurId",
            bodyBorder : false,
            border : false,
            layout: {
                type: 'hbox',
                align: 'right'
            },
            items : [{
                xtype : 'radiogroup',
                id : 'radioQueryType',
                fieldLabel : i18n.get('label.queryType'),
                width : 300,
                height : 30,
                items : [ {
					disabled : this.action === 'view' ? true : false, 
                    boxLabel : i18n.get('label.assistant'),
                    name : 'queryType',
                    inputValue : "W",
                    checked : true
                }, {
		            disabled : this.action === 'view' ? true : false, 
                    boxLabel : i18n.get('label.sql'),
                    name : 'queryType',
                    inputValue : "S"
                } ],
                listeners : {
                    scope : this,
                    change : function (radioGroup, radio) {
                        if (!Ext.isEmpty(radio.queryType)) {
							this.scope.queryType = radio.queryType;
                        }
                        if (this.scope.queryType === 'W') {
	                        this.whereClausePanel.remove(this.SqlWhereClause, false);
	                        this.SqlWhereClause.hide();
	                        
	                        this.whereClausePanel.add(this.wizardJoinCondition);
	                        this.whereClausePanel.add(this.wizardWhereClause);
	                        this.wizardJoinCondition.show();
	                        this.wizardWhereClause.show();
	                        
	                        this.whereClausePanel.doLayout();
                        } else {
	                        this.whereClausePanel.remove(this.wizardJoinCondition, false);
	                        this.whereClausePanel.remove(this.wizardWhereClause, false);
	                        this.wizardJoinCondition.hide();
	                        this.wizardWhereClause.hide();
	                        
	                        this.whereClausePanel.add(this.SqlWhereClause);
	                        this.SqlWhereClause.show();
	                        this.whereClausePanel.doLayout();
                        }
                    }
                }
            }, {
                xtype: 'fieldcontainer',
                itemId : 'containerDistinct',
                labelWidth: 120,
                fieldLabel: i18n.get('label.distinctRequest'),
                defaultType: 'checkboxfield',
                items: [{
                    name : 'distinct',
                    value : false,
                    id : 'distinctId'
                }]
            }]
        });
        /**
         * A single container with a flex layout. 
         * @type Ext.Panel
         */
        this.whereClausePanel = Ext.create('Ext.panel.Panel', {
			flex : 0.9,
            border : false,
            bodyBorder : false,
			layout : {
			    type : 'vbox', 
				align : "stretch"
			}
		});   
        
        this.items = [selecteur, this.whereClausePanel];
        
        this.listeners = {
            scope : this,
            activate : function () {
                if (this.scope.action === 'view') {
                    this.getEl().mask();
                }
                if (!Ext.isEmpty(this.scope.datasourceUtils)) {
                    this.wizardJoinCondition.setVisible(this.scope.datasourceUtils.isJdbc);
                    selecteur.setVisible(this.scope.datasourceUtils.isJdbc);
                    
                    if (this.scope.queryType === 'W') {
                        this.scope.datasourceUtils.loadColumnsBDD();
                        this.wizardJoinCondition.buildDefault();
                        this.whereClausePanel.add([this.wizardJoinCondition, this.wizardWhereClause]);
                        this.whereClausePanel.doLayout();
                    } else {
                        this.whereClausePanel.add(this.SqlWhereClause);
                        this.whereClausePanel.doLayout();
                    }
                    
                    selecteur.down('radiogroup').setValue({
                        queryType : this.scope.queryType
                    });
                    
                    this.down('fieldcontainer[itemId=containerDistinct] > checkboxfield').setValue(this.scope.distinct);
                }
            }
        };
        
       this.callParent(arguments);
    }, 
    /**
     * Returns the wizard panel 
     * @returns {sitools.admin.datasets.PredicatsPanel}
     */
    getWizardWhereClause : function () {
        return this.wizardWhereClause;
    }, 
    /**
     * Returns the join Panel
     * @returns
     */
    getWizardJoinCondition : function () {
        return this.wizardJoinCondition;
    },
    
    getQueryType : function () {
        return this.scope.queryType;
    },
    
    getDistinctRequest : function () {
    	return this.down('fieldcontainer[itemId=containerDistinct] > checkboxfield').getValue();
    }
	
});

