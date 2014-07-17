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
Ext.define('sitools.admin.datasets.DatasetServicesConfig', { 
    extend : 'Ext.form.Panel', 
    padding : 10, 
    
    initComponent : function () {
        this.title = i18n.get('label.viewConfig');
        var action = this.action;
        //Store of the comboDatasetsViews.
        this.storeDatasetViews = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'description', 'jsObject', 'fileUrl', 'priority' ],
            url : this.urlDatasetViews,
            autoLoad : false,
            root : "data", 
            sortInfo : {
                field : 'priority',
                direction : 'ASC'
            },
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
                            
                            tabRec = [];
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
        this.comboDatasetViews = new Ext.form.ComboBox({
            disabled : this.action == 'view' ? true : false, 
            id : "comboDatasetViews",
            store : this.storeDatasetViews,
            fieldLabel : i18n.get('label.datasetViews'),
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            name : 'comboDatasetViews',
            forceSelection : true,
            triggerAction : 'all',
            editable : false,
            emptyText : i18n.get('label.datasetViewsSelect'),
            selectOnFocus : true,
            anchor : '95%',    
            itemSelector : 'div.search-item',
            allowBlank : false,
            autoSelect : true, 
            maxHeight : 200,
            validator : function (value) {
                if (Ext.isEmpty(value)) {
                    return false;
                } else {
                    return true;
                }
            },
            tpl : new Ext.XTemplate(
                '<tpl for=".">',
                '<div class="search-item combo-datasetview"><div class="combo-datasetview-name">{name}</div>',
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

        this.parametersFieldset = new Ext.form.FieldSet({
            title : i18n.get('label.parameters'), 
            anchor : "95%"
        });
        Ext.apply(this, {
            items : [this.comboDatasetViews, this.parametersFieldset], 
            listeners : {
                "activate" : function () {
                    if (action == 'view') {
                        this.getEl().mask();
                    }
                }
            }
        });
        
        sitools.admin.datasets.DatasetServicesConfig.superclass.initComponent.call(this);


    }, 
    getDatasetViewsCombo : function () {
        return this.comboDatasetViews;  
    }, 
    setViewConfigParamsValue : function (data) {
        this.viewConfigParamsValue = data;
    },
    buildViewConfig : function (recSelected) {
        try {
            this.parametersFieldset.removeAll();
            var getParametersMethod = eval(recSelected[0].data.jsObject + ".getParameters");
            if (!Ext.isFunction(getParametersMethod)) {
                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.notImplementedMethod <br/>' + getParametersMethod));
                return;
            }
            var parameters = getParametersMethod();
            if (Ext.isEmpty(parameters)) {
                this.parametersFieldset.setVisible(false);
            }
            else {
                this.parametersFieldset.setVisible(true);
            }
            Ext.each(parameters, function (param) {
                var parameterValue = this.findDefaultParameterValue(param);
                var JsObj = eval(param.jsObj); 
                var config = Ext.applyIf(param.config, {
                    anchor : "95%"
                });

                var p = new JsObj(config);
                if (!Ext.isEmpty(parameterValue)) {
                    p.setValue(parameterValue);
                }
                this.parametersFieldset.add(p);
                
            }, this);
            
//            this.doLayout();
        }
        catch (err) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.notImplementedMethod <br/>') + err);
            return;
        }
        
        
    }, 
    getParametersValue : function () {
        var result = [];
        if (Ext.isEmpty(this.parametersFieldset.items)) {
            return result;
        }
        this.parametersFieldset.items.each(function (param) {
            result.push({
                name : param.parameterName, 
                value : param.getValue()
            });
        }, this);
        return result;
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

