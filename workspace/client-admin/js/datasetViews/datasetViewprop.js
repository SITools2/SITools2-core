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
 showHelp, includeJs*/
Ext.namespace('sitools.admin.datasetView');

/**
 * A panel to edit Dataviews. 
 * @class sitools.admin.datasetView.DatasetViewPropPanel
 * @extends Ext.Window
 */
sitools.admin.datasetView.DatasetViewPropPanel = Ext.extend(Ext.Window, {
//sitools.component.datasetView.DatasetViewPropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    id : ID.PROP.DATAVIEW,
    layout : 'fit',
    initComponent : function () {
        if (this.action == 'create') {
            this.title = i18n.get('label.createDatasetView');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyDatasetView');
        }
        
        var storeDependencies = new Ext.data.JsonStore({
            fields : [ {
                name : 'url',
                type : 'string'
            }],
            autoLoad : false
        });
        
        var tbar = {
			xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateDependencies
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteDependencies
            }]
        };
        
        this.gridDependencies = new Ext.grid.EditorGridPanel({
            title : i18n.get('title.dependencies'),
            height : 180,
            store : storeDependencies,
            tbar : tbar,
            cm : new Ext.grid.ColumnModel({
                columns : [ {
                    header : i18n.get('label.url'),
                    dataIndex : 'url',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                } ]
            }),
            sm : new Ext.grid.RowSelectionModel({
                singleSelect : true
            }),
            viewConfig : {
                forceFit : true
            }
        });
        this.formPanel = new Ext.form.FormPanel({
        	title : i18n.get('label.datasetViewInfo'),
            border : false,
            labelWidth : 150,
            padding : 10,
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
                    xtype : 'spinnerfield',
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
        
        sitools.admin.datasetView.DatasetViewPropPanel.superclass.initComponent.call(this);
    },
    onRender : function () {
        sitools.admin.datasetView.DatasetViewPropPanel.superclass.onRender.apply(this, arguments);
        if (this.action == 'modify') {
            var f = this.findByType('form')[0].getForm();
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
                            storeDependencies.add(new Ext.data.Record(item));
                        }, this);
                    }
                    if (!Ext.isEmpty(dependencies.css)) {
                        Ext.each(dependencies.css, function (item) {
                            storeDependencies.add(new Ext.data.Record(item));
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
        var frm = this.findByType('form')[0].getForm();
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
        var e = new Ext.data.Record();
        this.gridDependencies.getStore().insert(this.gridDependencies.getStore().getCount(), e);
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteDependencies : function () {
        var s = this.gridDependencies.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.gridDependencies.getStore().remove(r);
        }
    }

});

Ext.reg('s-datasetViewprop', sitools.admin.datasetView.DatasetViewPropPanel);
