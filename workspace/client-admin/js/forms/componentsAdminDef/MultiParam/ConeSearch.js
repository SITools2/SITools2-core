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
 showHelp, loadUrl*/
/*
 * @include "Abstract.js"
 */
Ext.namespace('sitools.admin.forms.multiParam');

/**
 * 
 * @class sitools.admin.forms.multiParam.coneSearch
 * @extends sitools.admin.forms.multiParam.abstractForm
 */
sitools.admin.forms.multiParam.coneSearch = Ext.extend(sitools.admin.forms.multiParam.abstractForm, {
//sitools.component.forms.multiParam.coneSearch = Ext.extend(sitools.admin.forms.multiParam.abstractForm, {
    height : 400,
    id : 'sitools.component.forms.definitionId',
    columnUnitName : "°", 
    //TODO améliorer ça pour que ce ne soit plus statique.
    columnDimensionId : "Angle",
    initComponent : function () {
        this.winPropComponent.specificHeight = 500;
        this.winPropComponent.specificWidth = 500;
        this.labelWidth = 150;
        sitools.admin.forms.multiParam.coneSearch.superclass.initComponent.call(this);
        /*
         * Build all the Columns mapping for the configuration, 
         * this object needs three columns
         */
        this.context.buildCombosConeSearch(this);
        
        this.storeDimension = new Ext.data.JsonStore({
            root : "data", 
            fields : [{
                name : "id", 
                type : "string"
            }, {
                name : "name", 
                type : "string"
            }, {
                name : "description", 
                type : "string"
            }, {
                name : "dimensionHelperName", 
                type : "string"
            }], 
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension',
            restful : true,
            autoLoad : false, 
            listeners : {
                scope : this, 
                beforeload : this.onBeforeLoad,
                load : this._onDimensionLoad
            }
        }); 
        this.on("beforerender", this.onBeforeRender, this);
        this.componentDefaultValue1 = new Ext.form.NumberField({
            fieldLabel : i18n.get('label.defaultValue') + ' (Y/RA)',
            name : 'componentDefaultValue1',
            anchor : '100%'
        });
        this.componentDefaultValue2 = new Ext.form.NumberField({
            fieldLabel : i18n.get('label.defaultValue') + ' (X/DEC)',
            name : 'componentDefaultValue2',
            anchor : '100%'
        });
        this.componentDefaultValue3 = new Ext.form.NumberField({
            fieldLabel : i18n.get('label.defaultValue') + ' (Z/ID)',
            name : 'componentDefaultValue3',
            anchor : '100%'
        });
        this.add(this.componentDefaultValue1);
        this.add(this.componentDefaultValue2);
        this.add(this.componentDefaultValue3);
        
        this.showTargetName = new Ext.form.Checkbox({
            fieldLabel : i18n.get('label.showTargetName'),
            name : 'showTargetName',
            anchor : '100%', 
            listeners : {
				scope : this, 
				check : function (cb, checked) {
					if (checked) {
						this.resolveNameUrl.enable();
						this.coordSystem.enable();
						this.resolverName.enable();
					}
					else {
						this.resolveNameUrl.disable();
						this.coordSystem.disable();
						this.resolverName.disable();
					}
				}
            }
        });
        this.resolveNameUrl = new Ext.form.TextField({
            fieldLabel : i18n.get('label.resolveNameUrl'),
            name : 'resolveNameUrl',
            anchor : '100%'
        });
        this.coordSystem = new Ext.form.ComboBox({
		    typeAhead: true,
		    fieldLabel : i18n.get('label.coordSystem'),
            name : 'coordSystem',
            triggerAction: 'all',
			lazyRender : true,
		    mode: 'local',
		    store: new Ext.data.ArrayStore({
		        id: 0,
		        fields: [
		            'value',
		            'displayText'
		        ],
		        data: [["EQUATORIAL", 'EQUATORIAL'], ["GALACTIC", 'GALACTIC']]
		    }),
		    valueField: 'value',
		    displayField: 'displayText',
            anchor : '100%'
		});
        this.resolverName = new Ext.form.ComboBox({
		    typeAhead: true,
		    fieldLabel : i18n.get('label.resolverName'),
            name : 'resolverName',
            triggerAction: 'all',
			lazyRender : true,
		    mode: 'local',
		    store: new Ext.data.ArrayStore({
		        id: 0,
		        fields: [
		            'value',
		            'displayText'
		        ],
		        data: [["ALL", 'ALL'], ["CDS", 'CDS'], ["IAS", 'IAS'], ["IMCCE", 'IMCCE']]
		    }),
		    valueField: 'value',
		    displayField: 'displayText',
            anchor : '100%'
		});
        var fieldsetResolveName = new Ext.form.FieldSet({
			items : [this.showTargetName, this.resolveNameUrl, this.coordSystem, this.resolverName], 
			title : i18n.get("label.nameResolverOption"), 
			autoHeight : true, 
			collapsible : true, 
			collapsed : true
        });
        this.add(fieldsetResolveName);
    },
    afterRender : function () {
        sitools.admin.forms.multiParam.coneSearch.superclass.afterRender.apply(this, arguments);
        if (this.action == 'modify') {
			if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
                this.componentDefaultValue1.setValue(this.selectedRecord.data.defaultValues[0]);
                this.componentDefaultValue2.setValue(this.selectedRecord.data.defaultValues[1]);
                this.componentDefaultValue3.setValue(this.selectedRecord.data.defaultValues[2]);
                var extraParams = {};
		        Ext.each(this.selectedRecord.data.extraParams, function (param) {
		            extraParams[param.name] = param.value;
		        }, this);

                this.showTargetName.setValue(extraParams.showTargetName);
                this.resolveNameUrl.setValue(extraParams.resolveNameUrl);
                this.coordSystem.setValue(extraParams.coordSystem);
                this.resolverName.setValue(extraParams.resolverName);
                
            }
        }
        

    },
    onBeforeRender : function () {
        if (!this.loaded) {
            this.storeDimension.load();
        }    
    },
    onBeforeLoad : function () {
        this.storeDimension.baseParams = this.baseParams;
        return true;
    },
    _onDimensionLoad : function () {
        this.dimension = new Ext.form.ComboBox({
            fieldLabel : i18n.get('label.dimension'),
            store : this.storeDimension,
            displayField : "name",
            mode : 'local',
            forceSelection : false,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDimension'),
            selectOnFocus : true,
            valueField : "id",
            name : 'dimensionId',
            anchor : '100%', 
            value : Ext.isEmpty(this.selectedRecord) ? null : this.selectedRecord.data.dimensionId
        });
		
		this.dimension.on("select", this.context.onChangeDimension, this);
		
        this.add(this.dimension);
        this.doLayout();
        this.context.buildUnit.call(this);
    },
	_onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var defaultValue1 = Ext.isEmpty(f.findField('componentDefaultValue1')) ? "" : f.findField('componentDefaultValue1').getValue();
        var defaultValue2 = Ext.isEmpty(f.findField('componentDefaultValue2')) ? "" : f.findField('componentDefaultValue2').getValue();
        var defaultValue3 = Ext.isEmpty(f.findField('componentDefaultValue3')) ? "" : f.findField('componentDefaultValue3').getValue();

		var columnObjects = this.find('specificType', 'mapParam');
		var code = [], unitValue, unitObject;
		Ext.each(columnObjects, function (columnObject) {
			code.push(columnObject.getValue());
		});
        var extraParams = [{
            name : "showTargetName",
            value : this.showTargetName.getValue()
        }, {
            name : "coordSystem",
            value : this.coordSystem.getValue()
        }, {
            name : "resolverName",
            value : this.resolverName.getValue()
        }, {
            name : "resolveNameUrl",
            value : this.resolveNameUrl.getValue()
        }, {
            name : "columnDimensionId",
            value : this.columnDimensionId
        }, {
			name : "columnUnitName", 
			value : this.columnUnitName
		}];       
        if (!Ext.isEmpty(this.unitCombo)) {
			unitValue = this.unitCombo.getValue();
			try {
				unitObject = this.unitCombo.getStore().getAt(this.unitCombo.getStore().find("unitName", unitValue)).data;
			}
			catch (err) {
				unitObject = null;
			}
        }
        if (action == 'modify') {
            var rec = this.selectedRecord;
            
            if (Ext.isEmpty(rec)) {
				Ext.Msg.alert(i18n.get("label.error"), i18n.get('label.impossibleToEdit'));
				return;
            }
            var labelParam1 = Ext.isEmpty(f.findField('LABEL_PARAM1')) ? "" : f.findField('LABEL_PARAM1').getValue();
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();
            
            rec.set('label', labelParam1);
            rec.set('code', code);
            rec.set('css', css);
//            rec.set('componentDefaultHeight', f.findField('componentDefaultHeight').getValue());
//            rec.set('componentDefaultWidth', f.findField('componentDefaultWidth').getValue());

            rec.set('dimensionId', this.dimension.getValue());
            rec.set('unit', unitObject);
            rec.set('extraParams', extraParams);
            rec.set('defaultValues', [ defaultValue1, defaultValue2, defaultValue3]);
        } else {
            // Génération de l'id
            var lastId = 0;
//            var greatY = 0;
            formComponentsStore.each(function (component) {
                if (component.data.id > lastId) {
                    lastId = parseInt(component.data.id, 10);
                }
//                if (component.data.ypos > greatY) {
//                    greatY = parseInt(component.data.ypos, 10)  + parseInt(component.data.height, 10);
//                }

            });
            var componentId = lastId + 1;
            componentId = componentId.toString();
//            var componentYpos = greatY + 10;
            
            formComponentsStore.add(new Ext.data.Record({
                label : f.findField('LABEL_PARAM1').getValue(),
                type : this.ctype,
                code : code,
                width : f.findField('componentDefaultWidth').getValue(),
                height : f.findField('componentDefaultHeight').getValue(),
                id : componentId,
                ypos : this.xyOnCreate.y,
                xpos : this.xyOnCreate.x, 
                css : f.findField('CSS').getValue(),
                jsAdminObject : this.jsAdminObject,
                jsUserObject : this.jsUserObject,
                dimensionId : this.dimension.getValue(), 
                unit : unitObject, 
                extraParams : extraParams, 
                defaultValues : [ defaultValue1, defaultValue2, defaultValue3],
                containerPanelId : this.containerPanelId
            }));
        }
        return true;
    }

});
