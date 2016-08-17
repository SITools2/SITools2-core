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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.forms.componentsAdminDef.oneParam');

Ext.define('sitools.admin.forms.componentsAdminDef.oneParam.NumericBetween', { 
    extend : 'sitools.admin.forms.componentsAdminDef.oneParam.AbstractWithUnit', 
    height : ADMIN_PANEL_HEIGHT,
//    id : "sitools.component.forms.definitionId",
    
    initComponent : function () {
        this.winPropComponent.specificHeight = 400;
        this.winPropComponent.specificWidth = 400;
        sitools.admin.forms.componentsAdminDef.oneParam.NumericBetween.superclass.initComponent.call(this);
        this.componentDefaultValueFrom = new Ext.form.NumberField({
            fieldLabel : i18n.get('label.defaultValueMin'),
            name : 'componentDefaultValueFrom',
            anchor : '100%'
        });
        this.componentDefaultValueTo = new Ext.form.NumberField({
            fieldLabel : i18n.get('label.defaultValueMax'),
            name : 'componentDefaultValueTo',
            anchor : '100%'
        });
		this.on("beforerender", this.onBeforeRender, this);
		this.mapParam1.on("select", this.onChangeColumn, this);

        this.add(this.componentDefaultValueFrom);
        this.add(this.componentDefaultValueTo);
    },
    afterRender : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.NumericBetween.superclass.afterRender.apply(this, arguments);
        if (this.action == 'modify') {
			if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
                this.componentDefaultValueFrom.setValue(this.selectedRecord.data.defaultValues[0]);
                this.componentDefaultValueTo.setValue(this.selectedRecord.data.defaultValues[1]);
                
            }
        }
    },
    _onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var param1, code, unitValue, unitObject;
        var extraParams = [];
        var defaultValueFrom = Ext.isEmpty(f.findField('componentDefaultValueFrom')) ? "" : f.findField('componentDefaultValueFrom').getValue();
        var defaultValueTo = Ext.isEmpty(f.findField('componentDefaultValueTo')) ? "" : f.findField('componentDefaultValueTo').getValue();
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
            param1 = Ext.isEmpty(f.findField('PARAM1')) ? "" : f.findField('PARAM1').getValue();
            code = [param1];
            var labelParam1 = Ext.isEmpty(f.findField('LABEL_PARAM1')) ? "" : f.findField('LABEL_PARAM1').getValue();
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();

            rec.set('label', labelParam1);
            rec.set('code', code);
            rec.set('css', css);

            rec.set('defaultValues', [ defaultValueFrom, defaultValueTo ]);
            rec.set('dimensionId', this.dimension.getValue());
            rec.set('unit', unitObject);
            rec.set('extraParams', extraParams);
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
            param1 = Ext.isEmpty(f.findField('PARAM1')) ? "" : f.findField('PARAM1').getValue();
            code = [param1];
            
            formComponentsStore.add({
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
                defaultValues : [ defaultValueFrom, defaultValueTo ], 
                dimensionId : this.dimension.getValue(), 
                unit : unitObject, 
                extraParams : extraParams,
                containerPanelId : this.containerPanelId
            });
        }
        return true;
    }
});
