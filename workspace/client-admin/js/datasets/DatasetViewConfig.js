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
 * @cfg {String} action (required) "active", "modify" "view"
 * @cfg {String} urlDatasetViews the url to request all dataviews available
 * @cfg {Array} viewConfigParamsValue An array containing all params value for view Config
 * @class sitools.admin.datasets.DatasetForm
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.DatasetViewConfig', { 
    extend : 'Ext.form.Panel',
    autoScroll : true,
    border : false,
    bodyBorder : false,
    initComponent : function () {
        this.title = i18n.get('label.viewConfig');

        this.storeDatasetViews = Ext.create('Ext.data.JsonStore', {
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.urlDatasetViews,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            fields : [ 'id', 'name', 'description', 'jsObject', 'fileUrl', 'priority' ],
            sorters : [{
                property : 'priority',
                direction : 'ASC'
            }],
            listeners : {
                scope : this, 
                load : function (store, recs) {
                    if (Ext.isEmpty(this.comboDatasetViews.getValue())) {
                        if (!Ext.isEmpty(recs) && Ext.isArray(recs) && recs.length > 0) {
                            var minPriorityRec = null;
                            for (var i = 0; i < recs.length; i++) {
                                if (Ext.isEmpty(minPriorityRec)	|| recs[i].data.priority < minPriorityRec.data.priority) {
                                    minPriorityRec = recs[i];
                                }                                
                            }
                            var tabRec = [];
                            tabRec[0] = minPriorityRec;
                            
                            this.comboDatasetViews.setValue(minPriorityRec.data.id);
                            this.comboDatasetViews.fireEvent("select", this.comboDatasetViews, tabRec);
                        }
                    }
                }
            }
        });
        
        /**
         * Combo to select Datasets Views.
         * Uses the storeDatasetViews. 
         */
        this.comboDatasetViews = Ext.create('Ext.form.field.ComboBox', {
            disabled : this.action == 'view' ? true : false, 
            id : "comboDatasetViews",
            store : this.storeDatasetViews,
            fieldLabel : i18n.get('label.datasetViews'),
            emptyText : i18n.get('label.datasetViewsSelect'),
            displayField : 'name',
            valueField : 'id',
            name : 'comboDatasetViews',
            forceSelection : true,
            editable : false,
            anchor : '100%',
            allowBlank : false,
            maxHeight : 200,
            labelWidth : 150,
            padding : 7,
            validator : function (value) {
                if (Ext.isEmpty(value)) {
                    return false;
                } else {
                    return true;
                }
            },
            tpl : new Ext.XTemplate(
                '<tpl for=".">',
                '<div class="x-boundlist-item search-item combo-datasetview"><div class="combo-datasetview-name">{name}</div>',
                '<tpl if="this.descEmpty(description) == false" ><div class="sitoolsDescription-datasetview"><div class="sitoolsDescriptionHeader">Description :&nbsp;</div><p class="sitoolsDescriptionText"> {description} </p></div></tpl>',
                '</div></tpl>',
                {
                    compiled : true, 
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }
            ), 
            listeners : {
                scope : this, 
                select : function (combo, rec, index) {
                    this.buildViewConfig(rec);
                }
            }
        });

        Ext.apply(this, {
            items : [this.comboDatasetViews],
            listeners : {
                "activate" : function () {
                    if (this.action == 'view') {
                        this.getEl().mask();
                    }
                }
            }
        });
        
        this.callParent(arguments);


    }, 
    getDatasetViewsCombo : function () {
        return this.comboDatasetViews;  
    }, 
    setViewConfigParamsValue : function (data) {
        this.viewConfigParamsValue = data;
    },
    buildViewConfig : function (recSelected) {
        if(this.formParametersPanel) {
            this.remove(this.formParametersPanel);
        }
        
        var record = recSelected[0].copy().data;
        record.xtype = record.jsObject;
        
        this.formParametersPanel = Ext.create("sitools.admin.common.FormParametersConfigUtil", {
            rec : record,
            parametersList : this.viewConfigParamsValue,
            padding : 5
        });
        this.add(this.formParametersPanel);
    }, 
    getParametersValue : function () {
        return this.formParametersPanel.getParametersValue();
    }, 
    findDefaultParameterValue : function (param) {
        var result;
        Ext.each(this.viewConfigParamsValue, function (paramValue) {
            if (paramValue.name == param.config.parameterName) {
                result = paramValue.value;
            }
        });
        return result;
    }
});

