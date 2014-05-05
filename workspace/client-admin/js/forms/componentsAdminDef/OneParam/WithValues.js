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
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.forms.componentsAdminDef.oneParam');

/**
 * @requires sitools.admin.forms.winParent
 * @class sitools.admin.forms.componentsAdminDef.oneParam.withValues
 * @extends sitools.admin.forms.componentsAdminDef.oneParam.abstract
 */
Ext.define('sitools.admin.forms.componentsAdminDef.oneParam.withValues', { 
    extend : 'sitools.admin.forms.componentsAdminDef.oneParam.abstract',
    alias : 'widget.sitools.admin.forms.componentsAdminDef.oneParam.withValues',
    requires : ['sitools.admin.forms.winParent'],
//    id : "sitools.component.forms.definitionId",
    height : 450,
    initComponent : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.withValues.superclass.initComponent.call(this);
        var urlFormulaire = this.winPropComponent.urlFormulaire;
        var formComponentsStore = this.formComponentsStore;
        
        this.parentParam = Ext.create("Ext.form.Hidden", {
            name : 'PARENT_PARAM'
        });
        this.parentParamDisplay = Ext.create("Ext.form.TriggerField", {
            fieldLabel : i18n.get('label.parentParam'),
            name : 'PARENT_PARAM_DISPLAY',
            anchor : '100%', 
            editable : false, 
            disabled : this.context.context == "project"
        });
        this.parentParamDisplay.onTriggerClick = function (event) {
            var winParent = Ext.create("sitools.admin.forms.winParent", {
                parentParamField : this.ownerCt.parentParam, 
                parentParamFieldDisplay : this,
                store : formComponentsStore,
                urlFormulaire : urlFormulaire
            });
            winParent.show();
        };
        this.add(this.parentParam);
        this.add(this.parentParamDisplay);
        
        this.winPropComponent.specificHeight = 550;
        this.winPropComponent.specificWidth = 400;
        
        var storeValues = Ext.create("Ext.data.JsonStore", {
            fields : [ {
                name : 'code',
                type : 'string'
            }, {
                name : 'defaultValue',
                type : 'boolean'
            }, {
                name : 'value',
                type : 'string'
            }, {
                name : 'availableFor'
            } ],
            autoLoad : false
        });
        var smValues = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });

        var tbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete
            } ]
        };

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        this.gridValues = Ext.create('Ext.grid.Panel', {
            title : i18n.get('title.values'),
            id : 'componentGridValues',
            height : 180,
            store : storeValues,
            tbar : tbar,
            forceFit : true,
            columns : [{
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : {
                    xtype : 'textfield',
                    allowBlank : false
                }
            }, { 
                xtype : 'checkcolumn',
                header : i18n.get('headers.defaultValue'),
                dataIndex : 'defaultValue'
            }],
            selModel : smValues,
            plugins : [cellEditing]
        });

        this.radio = Ext.create('Ext.form.RadioGroup', {
            fieldLabel : 'Values ',
            items : [ {
                checked : true,
                id : 'radioSpecificId',
                boxLabel : 'Specific',
                name : 'valuesType',
                inputValue : 'S'
            }, {
                id : 'radioFromDataId',
                boxLabel : 'From Data',
                name : 'valuesType',
                inputValue : 'D', 
                disabled : this.context.context == "project"
            } ],
            listeners : {
                scope : this,
                change : function (rg, newValue, oldValue, opts) {
                    var gridEl;
                    if (newValue.valuesType == 'D') {
                        gridEl = Ext.get('componentGridValues');
                        gridEl.mask();
                        // this.mask = this.gridValues().loadMask();
                    } else {
                        gridEl = Ext.get('componentGridValues');
                        gridEl.unmask();
                    }
                }
            }
        });
        var fieldSet = Ext.create('Ext.form.FieldSet', {
            xtype : 'fieldset',
            title : 'Values Selections',
            anchor : '100%',
            autoHeight : true,
            defaultType : 'radio', // each item will be a radio button
            items : [ this.radio ]
        });
        
        this.add(fieldSet);
        if (this.ctype == 'LISTBOX' || this.ctype == 'LISTBOXMULTIPLE' || this.ctype == 'DROPDOWNLIST') {
            fieldSet.setVisible(true);
        }
        else {
            fieldSet.setVisible(false);
        }

        this.add(this.gridValues);
    },
    onCreate : function () {
        this.gridValues.getStore().insert(0, {});
    },
    onDelete : function () {
        var recs = this.gridValues.getSelectionModel().getSelection();
        if (Ext.isEmpty(recs)) {
            popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
            return;
        }
        this.gridValues.getStore().remove(recs);
    },
    afterRender : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.withValues.superclass.afterRender.apply(this, arguments);
        
        if (this.action == 'modify') {
            // gridEl = Ext.get ('componentGridValues');
            this.radio.setValue({'valuesType' : this.selectedRecord.data.valueSelection}); 
            var store = this.formComponentsStore;
            var recTmp;
            var parentParam = this.selectedRecord.data.parentParam;
            if (!Ext.isEmpty(parentParam)) {
                store.each(function (rec) {
                    
                    if (rec.data.id == parentParam) {
                        recTmp = rec;
                    }
                });
                this.parentParam.setValue(recTmp.get("id"));
                this.parentParamDisplay.setValue(recTmp.get("label"));
//                
            }
            
            Ext.each(this.selectedRecord.data.values, function (value) {
                var rec = {
                    code : value.code,
                    defaultValue : value.defaultValue,
                    value : value.value,
                    availableFor : value.availableFor || []
                };
                this.gridValues.getStore().add(rec);
            }, this);
        }

    },
    _onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        if (Ext.getCmp('componentGridValues') && Ext.getCmp('componentGridValues').getStore().getCount() <= 0 && this.radio.getValue().inputValue == "S") {
            Ext.Msg.alert(i18n.get('warning.error'), i18n.get('warning.noValues'));
            return false; 
        }
        var grid, store, defaultValue, values = [], param1, valueSelection, code;
        if (action == 'modify') {
            var rec = this.selectedRecord;
            param1 = Ext.isEmpty(f.findField('PARAM1')) ? "" : f.findField('PARAM1').getValue();
            code = [param1];
            var labelParam1 = Ext.isEmpty(f.findField('LABEL_PARAM1')) ? "" : f.findField('LABEL_PARAM1').getValue();
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();
            defaultValue = Ext.isEmpty(f.findField('componentDefaultValue')) ? "" : f.findField('componentDefaultValue').getValue();
            valueSelection = this.radio.getValue().valuesType;
            
            rec.set('label', labelParam1);
            // selectedRecord.set ('type', this.ctype);
            rec.set('code', code);
            rec.set('css', css);
            rec.set('valueSelection', valueSelection);
            rec.set('defaultValue', defaultValue);
            rec.set('parentParam', Ext.isEmpty(this.parentParam.getValue()) ? null : this.parentParam.getValue());
            // this.selectedRecord.set ('width',
            // f.findField('width').getValue());
            grid = Ext.getCmp('componentGridValues');
            if (grid) {
                store = grid.getStore();
                store.each(function (rec) {
                    values.push(rec.data);
                });
                this.selectedRecord.set('values', values);
            } else {
                this.selectedRecord.set('defaultValue', defaultValue);
            }
        } else {
            grid = Ext.getCmp('componentGridValues');
            if (grid) {
                store = grid.getStore();
                store.each(function (rec) {
                    values.push(rec.data);
                });
            } else {
                defaultValue = Ext.isEmpty(f.findField('componentDefaultValue')) ? "" : f.findField('componentDefaultValue').getValue();
            }
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
            valueSelection = this.radio.getValue().valuesType;
            
            var parentParam = (Ext.isEmpty(this.parentParam.getValue())) ? null:this.parentParam.getValue();
            
            formComponentsStore.add({
                label : f.findField('LABEL_PARAM1').getValue(),
                type : this.ctype,
                code : code,
                values : values,
                defaultValue : defaultValue,
                width : f.findField('componentDefaultWidth').getValue(),
                height : f.findField('componentDefaultHeight').getValue(),
                id : componentId,
                ypos : this.xyOnCreate.y,
                xpos : this.xyOnCreate.x, 
                css : f.findField('CSS').getValue(),
                jsAdminObject : this.jsAdminObject,
                jsUserObject : this.jsUserObject,
                valueSelection : valueSelection, 
                parentParam : parentParam,
                containerPanelId : this.containerPanelId
            });
        }
        return true;
    }

});