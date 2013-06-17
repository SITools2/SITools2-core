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
 showHelp, SITOOLS_DEFAULT_IHM_DATE_FORMAT, SITOOLS_DATE_FORMAT*/
Ext.namespace('sitools.admin.forms.oneParam');

/**
 * 
 * @class sitools.admin.forms.oneParam.DateBetween
 * @extends sitools.admin.forms.oneParam.abstractForm
 */
sitools.admin.forms.oneParam.DateBetween = Ext.extend(sitools.admin.forms.oneParam.abstractForm, {
//sitools.component.forms.oneParam.DateBetween = Ext.extend(sitools.admin.forms.oneParam.abstractForm, {
    height : 330,
    id : "sitools.component.forms.definitionId",
    initComponent : function () {
        //formattage de extraParams : 
        this.winPropComponent.specificHeight = 430;
        this.winPropComponent.specificWidth = 400;
        sitools.admin.forms.oneParam.DateBetween.superclass.initComponent.call(this);
        this.componentDefaultValueFrom = new sitools.widget.DateFieldWithToday({
            fieldLabel : i18n.get('label.defaultValueFrom'),
            name : 'componentDefaultValueFrom',
            anchor : '100%', 
            format : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
            showTime : true, 
            tooltip : i18n.get('label.explainDateDefault')
        });
        this.componentDefaultValueTo = new sitools.widget.DateFieldWithToday({
            fieldLabel : i18n.get('label.defaultValueTo'),
            name : 'componentDefaultValueTo',
            anchor : '100%', 
            format : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
            showTime : true, 
            tooltip : i18n.get('label.explainDateDefault')
        });
//        this.add(this.componentDefaultValueFrom);
//        this.add(this.componentDefaultValueTo);
        
        this.truncateDefaultValue = new Ext.form.Checkbox({
            fieldLabel : i18n.get('label.truncateDefaultValue'),
            name : 'truncateDefaultValue',
            anchor : '100%'
        });
//        this.add(this.truncateDefaultValue);
        var defaultValuesFieldset = new Ext.form.FieldSet({
			items : [this.componentDefaultValueFrom, this.componentDefaultValueTo, this.truncateDefaultValue], 
			title : i18n.get("label.defaultValue"), 
			autoHeight : true
		});
        this.add(defaultValuesFieldset);
        
        this.showTime = new Ext.form.Checkbox({
            fieldLabel : i18n.get('label.showTime'),
            name : 'showTime',
            anchor : '100%'
        });
        this.add(this.showTime);
        
        this.componentDateFormat = new Ext.form.TextField({
            fieldLabel : i18n.get("label.dateFormat"),
            name : "format",
            anchor : '100%',
            value : SITOOLS_DEFAULT_IHM_DATE_FORMAT
        });
        this.add(this.componentDateFormat);

    },
    onRender : function () {
        sitools.admin.forms.oneParam.DateBetween.superclass.onRender.apply(this, arguments);
        if (this.action == 'modify') {
            var regToday = new RegExp("^\{\\$TODAY\}");
            if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
                if (!regToday.test(this.selectedRecord.data.defaultValues[0])) {
                    this.componentDefaultValueFrom.setValue(Date.parseDate(this.selectedRecord.data.defaultValues[0], SITOOLS_DATE_FORMAT));
                }
                else {
                    this.componentDefaultValueFrom.setValue(this.selectedRecord.data.defaultValues[0]);
                }
                if (!regToday.test(this.selectedRecord.data.defaultValues[1])) {
                    this.componentDefaultValueTo.setValue(Date.parseDate(this.selectedRecord.data.defaultValues[1], SITOOLS_DATE_FORMAT));
                }
                else {
                    this.componentDefaultValueTo.setValue(this.selectedRecord.data.defaultValues[1]);
                }
                
                
		        var extraParams = {};
		        Ext.each(this.selectedRecord.data.extraParams, function (param) {
		            extraParams[param.name] = param.value;
		        }, this);

                this.showTime.setValue(extraParams.showTime);
                this.truncateDefaultValue.setValue(extraParams.truncateDefaultValue);
                this.componentDateFormat.setValue(extraParams.format);
//	              this.componentDefaultValueTo.setValue(this.selectedRecord.data.defaultValues[1].split('T')[0]);
            }
        }
    },
    _onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var param1, defaultValueFrom, defaultValueTo, code;
        var extraParams = [{
			name : "showTime", 
			value : f.findField('showTime').getValue()
        }, {
			name : "truncateDefaultValue", 
			value : f.findField('truncateDefaultValue').getValue()
        }, {
            name : "format",
            value : f.findField('format').getValue()
        }
        ];
        
        if (action == 'modify') {
            var rec = this.selectedRecord;
            param1 = Ext.isEmpty(f.findField('PARAM1')) ? "" : f.findField('PARAM1').getValue();
            code = [param1];
            var labelParam1 = Ext.isEmpty(f.findField('LABEL_PARAM1')) ? "" : f.findField('LABEL_PARAM1').getValue();
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();
            defaultValueFrom = Ext.isEmpty(f.findField('componentDefaultValueFrom')) ? "" : f.findField('componentDefaultValueFrom').getValue();
            defaultValueTo = Ext.isEmpty(f.findField('componentDefaultValueTo')) ? "" : f.findField('componentDefaultValueTo').getValue();

            rec.set('label', labelParam1);
            rec.set('code', code);
            rec.set('css', css);
            
            defaultValueFrom = Ext.isDate(defaultValueFrom) ? defaultValueFrom.format(SITOOLS_DATE_FORMAT) : defaultValueFrom;
            defaultValueTo = Ext.isDate(defaultValueTo) ? defaultValueTo.format(SITOOLS_DATE_FORMAT) : defaultValueTo;
            
            rec.set('defaultValues', [ Ext.isEmpty(defaultValueFrom) ? null : defaultValueFrom, Ext.isEmpty(defaultValueTo) ? null : defaultValueTo ]);
            rec.set('extraParams', extraParams);
        } else {
            defaultValueFrom = Ext.isEmpty(f.findField('componentDefaultValueFrom').getValue()) ? "" : f.findField('componentDefaultValueFrom').getValue();
            defaultValueTo = Ext.isEmpty(f.findField('componentDefaultValueTo').getValue()) ? "" : f.findField('componentDefaultValueTo').getValue();
            
            defaultValueFrom = Ext.isDate(defaultValueFrom) ? defaultValueFrom.format(SITOOLS_DATE_FORMAT) : defaultValueFrom;
            defaultValueTo = Ext.isDate(defaultValueTo) ? defaultValueTo.format(SITOOLS_DATE_FORMAT) : defaultValueTo;
            
            var defaultValues = [ defaultValueFrom, defaultValueTo];
            
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
                defaultValues : defaultValues, 
                extraParams : extraParams
            }));
        }
        return true;
    }
});
