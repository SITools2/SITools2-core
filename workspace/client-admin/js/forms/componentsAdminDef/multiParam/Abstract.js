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
 showHelp*/
Ext.namespace('sitools.admin.forms.componentsAdminDef.multiParam');

/**
 * An abstract form to define MultiParam Components
 * @class sitools.admin.forms.componentsAdminDef.multiParam.Abstract
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.forms.componentsAdminDef.multiParam.Abstract', { 
    extend : 'Ext.form.Panel',
    autoScroll : true, 
    border : false,
    bodyBorder : false,
    padding : 10,
    initComponent : function () {
        this.context = sitools.admin.forms.componentsAdminDef.ComponentFactory.getContext(this.context);
        
        this.storeColumn = Ext.create("Ext.data.JsonStore", {
            id : 'storeColumnSelect',
            proxy : {
                type : 'memory',
                reader : {
                    type : 'json',
                    root : "ColumnModel", 
                    idProperty : 'header'
                }                
            },
            remoteSort : false,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'dataIndex',
                type : 'string'
            }, {
                name : 'schema',
                type : 'string'
            }, {
                name : 'tableAlias',
                type : 'string'
            }, {
                name : 'tableName',
                type : 'string'
            }, {
                name : 'header',
                type : 'string'
            }, {
                name : 'toolTip',
                type : 'string'
            }, {
                name : 'width',
                type : 'int'
            }, {
                name : 'sortable',
                type : 'boolean'
            }, {
                name : 'visible',
                type : 'boolean'
            }, {
                name : 'filter',
                type : 'boolean'
            }, {
                name : 'columnOrder',
                type : 'int'
            }, {
                name : 'columnType',
                type : 'string'
            }, {
                name : 'notion',
                type : 'string',
                mapping : 'notion.name'
            }, {
                name : 'notionUrl',
                type : 'string',
                mapping : 'notion.url'
            }, {
                name : 'columnAlias',
                type : 'string'
            }

            ]
        });
        Ext.each(this.datasetColumnModel, function (column) {
            if (column.specificColumnType != 'VIRTUAL') {
                this.storeColumn.add(column);
            }
        }, this);

        
        this.buttonAdd = Ext.create("Ext.Button", {
			text : i18n.get('label.addColumn'), 
			scope : this, 
			handler : function (button) {
				this.addColumn(button);
			}
		});
		this.buttonDel = Ext.create("Ext.Button", {
			text : i18n.get('label.deleteColumn'), 
			scope : this, 
			handler : function (button) {
				this.deleteColumn(button);
			}
		});
        this.labelParam1 = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.label'),
            name : 'LABEL_PARAM1',
            anchor : '100%'
        });
        this.css = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.css'),
            name : 'CSS',
            anchor : '100%'
        });
        this.componentDefaultHeight = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.height'),
            name : 'componentDefaultHeight',
            anchor : '100%',
            value : this.componentDefaultHeight, 
            allowBlank : false
        });
        this.componentDefaultWidth = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.width'),
            name : 'componentDefaultWidth',
            anchor : '100%',
            value : this.componentDefaultWidth, 
            allowBlank : false
        });
        this.items = [ this.labelParam1, this.css ];
        if (this.action == "create") {
			this.items.push(this.componentDefaultHeight, this.componentDefaultWidth); 
		}

        this.padding = 10;

        this.callParent(arguments);
    },
    afterRender : function () {
        this.callParent(arguments);
        if (this.action == 'modify') {
            this.labelParam1.setValue(this.selectedRecord.data.label);
            this.css.setValue(this.selectedRecord.data.css);
        }

    },
    _onValidate : function (action, formComponentsStore) {
        /**
         * Chaque classe étandant cet objet doit redéfinir cette méthode
         */
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.OnvalidateNotDefined'));
    }
});
