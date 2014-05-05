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
 showHelp, includeJs*/
Ext.namespace('sitools.admin.datasetViews');

/**
 * A panel to edit Dataviews. 
 * @class sitools.admin.datasetViews.DatasetViewProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasetViews.DatasetViewsProp', { 
    extend : 'Ext.Window',
	alias : 'widget.s-datasetViewprop',
    width : 700,
    height : 480,
    modal : true,
    id : ID.PROP.DATAVIEW,
    layout : 'fit',
    requires : ['sitools.admin.utils.DependenciesPanel'],
    
    initComponent : function () {
        if (this.action == 'create') {
            this.title = i18n.get('label.createDatasetView');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyDatasetView');
        }
        
        this.gridDependencies = Ext.create("sitools.admin.utils.DependenciesPanel");
        
        this.formPanel = Ext.create("Ext.form.FormPanel", {
        	title : i18n.get('label.datasetViewInfo'),
            border : false,
            bodyBorder : false,
            labelWidth : 150,
            padding : 5,
            items : [  {
                xtype : 'textfield',
                name : 'id',
                hidden : true
            }, {
                xtype : 'textfield',
                name : 'name',
                fieldLabel : i18n.get('label.name'),
                anchor : '100%', 
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'description',
                fieldLabel : i18n.get('label.description'),
                anchor : '100%', 
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'jsObject',
                fieldLabel : i18n.get('label.jsObject'),
                anchor : '100%', 
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'fileUrl',
                id : 'fileUrlId', 
                fieldLabel : i18n.get('label.fileUrl'),
                anchor : '100%'
            }, {
                xtype : 'sitoolsSelectImage',
                name : 'imageUrl',
                fieldLabel : i18n.get('label.image'),
                anchor : '100%', 
                allowBlank : true
            }, {
                    xtype : 'numberfield',
                    name : 'priority',
                    id : 'priorityId', 
                    fieldLabel : i18n.get('label.priority'),
                    minValue : 0,
                    maxValue : 10,
                    allowDecimals : false,
                    incrementValue : 1,
                    accelerate : true,
                    anchor : "50%", 
                    allowBlank : false
                }]
        });
        
        this.tabPanel = new Ext.TabPanel({
            activeTab : 0,
            items : [this.formPanel, this.gridDependencies ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this._onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]
        });
        
        
        this.items = [this.tabPanel];
        
        this.callParent(arguments);
    },
    onRender : function () {
        this.callParent(arguments);
        if (this.action == 'modify') {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.datasetView);
                    
                    var dependencies = data.datasetView.dependencies;
                    var storeDependencies = this.gridDependencies.getStore();
                    if (!Ext.isEmpty(dependencies.js)) {
                        Ext.each(dependencies.js, function (item) {
                            storeDependencies.add(item);
                        }, this);
                    }
                    if (!Ext.isEmpty(dependencies.css)) {
                        Ext.each(dependencies.css, function (item) {
                            storeDependencies.add(item);
                        }, this);
                    }
                },
                failure : alertFailure
            });
        }
    },

    /**
     * Called when ok Button is pressed. 
     * Send a request (POST or PUT depending on action param) to the server with a definition of a datasetView JSON object.
     */
    _onValidate : function () {
        var frm = this.down('form').getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        var met = this.action == 'modify' ? 'PUT' : 'POST';
        var jsonObject = frm.getFieldValues();
        
        jsonObject.dependencies = {};
        jsonObject.dependencies.js = [];
        jsonObject.dependencies.css = [];
        this.gridDependencies.getStore().each(function (item) {
            if (!Ext.isEmpty(item.data.url)) {
                if (item.data.url.indexOf(".css") != -1) {
                    jsonObject.dependencies.css.push({
                        url : item.data.url
                    });
                }
                if (item.data.url.indexOf(".js") != -1) {
                    jsonObject.dependencies.js.push({
                        url : item.data.url
                    });
                }
            }
        });

        Ext.Ajax.request({
            url : this.url,
            method : met,
            scope : this,
            jsonData : jsonObject,
            success : function (ret) {
                //load the scripts defined in this component. 
//				includeJs(frm.items.get('fileUrlId').getValue());
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });
    },
    
    /**
     * Add a new Record to the dependencies property of a project module
     */
    onCreateDependencies : function () {
        this.gridDependencies.getStore().insert(this.gridDependencies.getStore().getCount(), {});
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteDependencies : function () {
        var recs = this.gridDependencies.getSelectionModel().getSelection();
        this.gridDependencies.getStore().remove(recs);        
    }

});

