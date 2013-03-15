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
 showHelp, loadUrl*/
Ext.namespace('sitools.component.forms.componentsAdminDef');
/**
 * Object to build form Components administration objects in project Context (multiDatasets)
 * @class sitools.component.forms.componentsAdminDef.ProjectContext
 * 
 */
sitools.component.forms.componentsAdminDef.ProjectContext = function () {
	this.context = "project";
	
	/**
	 * @method buildComboParam1 build a single 
	 * @param {} scope the scope for this method.
	 */
	this.buildComboParam1 = function (scope) {
		return new Ext.form.ComboBox({
            fieldLabel : i18n.get('label.concept'),
            triggerAction : 'all',
            name : "PARAM1",
            specificType : "mapParam", 
            columnIndex : 1, 
            lazyRender : false,
            mode : 'local',
            store : scope.storeConcepts,
            valueField : 'name',
            displayField : 'name',
            anchor : '100%', 
            allowBlank : false
        });
	};
	/**
	 * @method buildCombosConeSearch build 3 combos 
	 * @param {} scope the scope for this method.
	 */
	this.buildCombosConeSearch = function (scope) {
		var labels = ['X/RA', 'Y/DEC', 'Z/ID'];
        for (var i = 1; i <= 3; i++) {
			scope['mapParam' + i] = new Ext.form.ComboBox({
			    fieldLabel : labels[i - 1],
			    triggerAction : 'all',
			    name : "PARAM" + i,
			    specificType : "mapParam", 
			    columnIndex : i, 
			    lazyRender : true,
			    mode : 'local',
			    store : scope.storeConcepts,
			    valueField : 'name',
			    displayField : 'name',
			    anchor : '100%', 
			    allowBlank : false 
			    
			}); 
			if (scope.action == "modify") {
				Ext.apply(scope['mapParam' + i], {
					value : scope.selectedRecord.data.code[i - 1]
				});
			}
			scope.insert(i, scope['mapParam' + i]);
        }		
	};

	this.onChangeColumn = function () {
		return;
	};
	
	this.activeDimension = function () {
		this.dimension.setDisabled(false);
		this.dimension.on("select", this.context.onChangeDimension, this);
	};
	
	this.onChangeDimension = function () {
		this.storeUnits.proxy.setUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension/' + this.dimension.getValue());
		this.storeUnits.load();
	};
	
	this.buildUnit = function () {
        var httpProxyUnits = new Ext.data.HttpProxy({
			url : loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension/' + this.dimensionId, 
			restful : true
		});
        this.storeUnits = new Ext.data.JsonStore({
            root : "dimension.sitoolsUnits", 
            fields : [{
                name : "label", 
                type : "string"
            }, {
                name : "unitName", 
                type : "string"
            }], 
            proxy : httpProxyUnits, 
            autoLoad : this.action == "modify" && (!Ext.isEmpty(this.dimensionId)), 
            listeners : {
                scope : this, 
//                beforeload : this.onBeforeLoad,
                load : this.context._onUnitLoad
            }
        }); 
		
		this.unitCombo = new Ext.form.ComboBox({
            fieldLabel : i18n.get('label.unit'),
            store : this.storeUnits,
            displayField : "label",
            mode : 'local',
            forceSelection : false,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectUnits'),
            selectOnFocus : true,
            valueField : "unitName",
            name : 'unit',
            anchor : '100%' 
        });  
        this.add(this.unitCombo);
        this.doLayout();		
        
	};

	this._onUnitLoad = function () {
		if (this.dimensionId == this.dimension.getValue()) {
			var unit = Ext.isEmpty(this.selectedRecord) ? null : this.selectedRecord.data.unit;
		
			this.unitCombo.setValue(unit ? unit.unitName : null);
		}
		else {
			this.unitCombo.setValue(null);
		}
		
	};
	
};
