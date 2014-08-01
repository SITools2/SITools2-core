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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl
 showHelp*/
/*
 * @include "WithoutValues.js"
 */
Ext.namespace('sitools.admin.forms.componentsAdminDef.oneParam');

/**
 * A Form Panel to display Administration for for form Components without Values with Properties. 
 * @class sitools.admin.forms.componentsAdminDef.oneParam.NoValuesWithProperties
 * @extends 
 */
Ext.define('sitools.admin.forms.componentsAdminDef.oneParam.NoValuesWithProperties', { 
    extend : 'sitools.admin.forms.componentsAdminDef.oneParam.WithoutValues',
    height : 400,
//    id : "sitools.component.forms.definitionId",
    
    initComponent : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.NoValuesWithProperties.superclass.initComponent.call(this);
        this.winPropComponent.specificHeight = 500;
        this.winPropComponent.specificWidth = 400;
        var storeProperties = Ext.create("Ext.data.JsonStore", {
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'value',
                type : 'string'
            } ],
            autoLoad : false
        });
        var smProperties = Ext.create('Ext.selection.RowModel',{
            mode : 'SINGLE'
        });

        var columns = {
            items : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                editor : Ext.create("Ext.form.TextField")
            }, {
                header : i18n.get('headers.value'),
                dataIndex : 'value',
                editor : Ext.create("Ext.form.TextField")
            }],
            defaults : {
                sortable : false,
                width : 100
            }
        };
        
        var tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
				scope : this, 
			    text : i18n.get('label.create'),
			    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
			    handler : function () {
			        var rowIndex = 0;
			        
			        this.gridProperties.getStore().insert(rowIndex, {});
			        
					this.gridProperties.getView().focusRow(rowIndex);

					this.gridProperties.getPlugin('cellEditing').startEditByPosition({
					    row: rowIndex, 
					    column: 0
					});
			    }
			}, {
				scope : this, 
			    text : i18n.get('label.delete'),
			    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
			    handler : function () {
			        var recs = this.gridProperties.getSelectionModel().getSelection();
			        if (Ext.isEmpty(recs)) {
			            popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
			            return;
			        }
			        this.gridProperties.getStore().remove(recs);
			    }
            } ]
        };
        
        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1,
            pluginId : 'cellEditing'
        });

        this.gridProperties = Ext.create('Ext.grid.Panel', {
            title : i18n.get('title.properties'),
            id : 'componentGridProperties',
            height : 180,
            tbar : tbar, 
            store : storeProperties,
            columns : columns,
            selModel : smProperties,
            forceFit : true,
            plugins : [cellEditing]
        });
        this.add(this.gridProperties);

    },
    /**
     * Load the component Values if action = modify. 
     * Load the default ExtraParams. 
     */
    afterRender : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.NoValuesWithProperties.superclass.afterRender.apply(this, arguments);
        if (this.action == 'modify') {
            Ext.each(this.selectedRecord.data.extraParams, function (prop) {
                var rec = {
                    name : prop.name,
                    value : prop.value
                };
                this.gridProperties.getStore().add(rec);
            }, this);

        }
        else {
			this.addDefaultExtraParams();
        }
    },
    /**
     * Overrides the parent _onValidate  : Edit or create the component in the formComponentsStore store. 
     * @param {string} action
     * @param {Ext.data.Store} formComponentsStore
     * @return {Boolean} false if an error occurs.
     */
    _onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var param1, defaultValue, code;
        var extraParams = [];
        this.gridProperties.getStore().each(function (prop) {
			extraParams.push(prop.data);
        });
        if (action == 'modify') {
            var rec = this.selectedRecord;
            param1 = Ext.isEmpty(f.findField('PARAM1')) ? "" : f.findField('PARAM1').getValue();
            code = [param1];
            var labelParam1 = Ext.isEmpty(f.findField('LABEL_PARAM1')) ? "" : f.findField('LABEL_PARAM1').getValue();
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();
            defaultValue = Ext.isEmpty(f.findField('componentDefaultValue')) ? "" : f.findField('componentDefaultValue').getValue();

            rec.set('label', labelParam1);
            rec.set('code', code);
            rec.set('css', css);
			rec.set('extraParams', extraParams);
            
            rec.set('defaultValues', [ defaultValue ]);
        } else {
            defaultValue = Ext.isEmpty(f.findField('componentDefaultValue')) ? "" : f.findField('componentDefaultValue').getValue();

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
                defaultValue : defaultValue,
                width : f.findField('componentDefaultWidth').getValue(),
                height : f.findField('componentDefaultHeight').getValue(),
                id : componentId,
                ypos : this.xyOnCreate.y,
                xpos : this.xyOnCreate.x, 
                css : f.findField('CSS').getValue(),
                jsAdminObject : this.jsAdminObject,
                jsUserObject : this.jsUserObject,
                extraParams : extraParams, 
                defaultValues : [ defaultValue ],
                containerPanelId : this.containerPanelId
            });
        }
        return true;
    },
    /**
     * Adds the Defaults Extra Params items to the gridProperties store.
     */
    addDefaultExtraParams : function () {
		switch (this.ctype) {
		case "MAPPANEL" : 
			var rec = {
		        name : "url",
		        value : ""
		    };
		    this.gridProperties.getStore().add(rec);
			rec = {
		        name : "layer",
		        value : ""
		    };
		    this.gridProperties.getStore().add(rec);
			break;
		}
    }

});
