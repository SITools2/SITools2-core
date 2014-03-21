/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*global Ext, sitools, i18n, SitoolsDesk */

Ext.namespace('sitools.user.component.dataviews');
/**
 * @cfg {Ext.menu.menu} contextMenu the contextMenu that call the plugin and will execute onResourceCallClick method
 * @cfg {Ext.data.Record} resource the resource record
 * @cfg {string} url the base url of the resource
 * @cfg {string} methods the methods allowed with the format method1|method2|...|methodN
 * @cfg {string} runType the runTypeUserInput defined in the resource
 * @cfg {boolean} withSelection true if there was a selection, false otherwise
 * @cfg {Array} parameters the parameters
 * @class sitools.user.component.dataviews.resourcePluginParamsPanel
 * @extends Ext.Window
 */
sitools.user.component.dataviews.resourcePluginParamsPanel = Ext.extend(Ext.Panel, {
//sitools.user.component.livegrid.resourcePluginParamsWindow = Ext.extend(Ext.Window, {
    width : "450",
    
    showMethod : false, 
    defaultMethod : "",
    showRunType : false, 
    initComponent : function () {
        var methodsArray = this.methods.split("|");
        this.showMethod = methodsArray.length > 1;
        this.defaultMethod = methodsArray[0];
        
        this.methodsStore = new Ext.data.ArrayStore({
            fields: ["method"],
            idIndex: 0
        });
    
        Ext.each(methodsArray, function (item, index) {
            this.methodsStore.add(new Ext.data.Record({
                method : item
            }));
        }, this);
        
        var formCommonParametersFields = [];
        var comboMethod = new Ext.form.ComboBox({                
            xtype : 'combo',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
            name : 'method',
            fieldLabel : i18n.get('label.method'),
            width : 100,
            store : this.methodsStore,
            valueField : 'method',
            displayField : 'method',
            anchor : "100%",
            value : this.defaultMethod,
            forceSelection : true
        });
        this.items = [];
        if (this.showMethod) {
			formCommonParametersFields.push(comboMethod);
			this.formParams = new Ext.form.FormPanel({
	            padding: 5,
	//            title : "Request parameters",
	            items : [{
	                xtype : 'fieldset',
	                title : i18n.get("label.commonParameters"),
	                items : formCommonParametersFields
	            }]
	        });
	        
	        this.items.push(this.formParams);			
        }
        
        var userInputParams = [];
        Ext.each(this.resource.parameters, function (value, index) {
            if (value.type == "PARAMETER_USER_INPUT" && value.userUpdatable) {
                var item = this.buildFormItemFromParam(value);
                userInputParams.push(item);
		        if (value.name == "runTypeUserInput") {
					this.showRunType = true;
				}
            }
        }, this);

        if (!Ext.isEmpty(userInputParams)) {
            this.formParamsUserInput = new Ext.form.FormPanel({
                padding: 5,
                labelWidth : 150, 
                items : {
                    xtype : 'fieldset',
                    title : i18n.get("label.specificParameter"),
                    items : userInputParams
                }
            });  
            this.items.push(this.formParamsUserInput);
        }
        
        this.buttons = [{
            text : i18n.get('label.submit'),
            scope : this,
            handler : this.onCall            
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.ownerCt.close();
                this.callback.call(undefined, false);
            }
        }];
        sitools.user.component.dataviews.resourcePluginParamsPanel.superclass.initComponent.call(this);
    },
    
    onCall : function () {        
        var method;
        if (this.showMethod) {
	        var form = this.formParams.getForm();
	        method = form.findField("method").getValue();
        }
        else {
			method = this.defaultMethod;	
        }
		
        var runTypeUserInput;
        if (this.showRunType) {
			runTypeUserInput = this.formParamsUserInput.getForm().findField("runTypeUserInput").getValue();
        }
        else {
			runTypeUserInput = this.runType;
        }
        var limit;

        var userParameters = {};
        if (!Ext.isEmpty(this.formParamsUserInput)) {
            var formParams = this.formParamsUserInput.getForm();
            Ext.iterate(formParams.getValues(), function (key, value) {
                userParameters[key] = value;                
            });
        }
        
        Ext.each(this.parameters, function (param) {
            if (param.type == "PARAMETER_IN_QUERY") {
                userParameters[param.name] = param.value;
            }
        });
        
        this.contextMenu.onResourceCallClick(this.resource, this.url, method, runTypeUserInput, limit, userParameters, this.postParameter, this.callback);
        this.ownerCt.close();
    }, 
    buildFormItemFromParam : function (value, userInputParams) {
		var valueType = value.valueType;
        var item = {};
        //specific case for boolean
        if (valueType.indexOf("xs:boolean") != -1) {
            valueType = "xs:enum[true,false]";
        }
        if (valueType.indexOf("xs:enum") != -1) {
	        var enumeration = valueType.split("[");
	        enumeration = enumeration[1].split("]");
	        enumeration = enumeration[0].split(",");

			var multiple = false;
			if (valueType.indexOf("xs:enum-multiple") >= 0 || valueType.indexOf("xs:enum-editable-multiple") >= 0) {
				multiple = true;
			}
			
			var storeItems = [];
			for (var i = 0; i < enumeration.length; i++) {
				var tmp = enumeration[i].trim();
				storeItems.push([ tmp, tmp]);
			}
			var store = new Ext.data.ArrayStore({
                fields : ['value', 'text'],
                data : storeItems, 
                valueField : 'value', 
                displayField : 'text'
            });
			
			if (multiple) {
				item = {
					store : store,
					name : value.name, 
					xtype : "multiselect", 
					values : value.value, 
					delimiter : '|', 
					fieldLabel : value.name, 
					width : 235, 
					tooltip : value.description
				};
			}
			else {
				item = {
					store : store,
					name : value.name, 
					xtype : "combo", 
					value : value.value, 
					valueField : "value", 
					displayField : "text", 
					mode: 'local', 
					fieldLabel : value.name, 
					triggerAction : 'all',
					selectOnFocus : true,
					editable : false, 
					anchor : "100%", 
					tooltip : value.description
				};
			}
        }
        else {
			item = {
                name : value.name,
                xtype : 'textfield',
                value : value.value,
                fieldLabel : value.name,
                anchor : "100%", 
				tooltip : value.description
            };
        }
        return item;
    }, 
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }

});