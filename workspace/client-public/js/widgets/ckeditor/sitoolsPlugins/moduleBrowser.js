/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/* global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.widget.sitoolsEditorPlugins');
/**
 * datasetLink widget
 * 
 * @class sitools.widget.sitoolsEditorPlugins.moduleBrowser
 * @extends Ext.util.Observable
 */
sitools.widget.sitoolsEditorPlugins.moduleBrowser = Ext.extend(Ext.grid.GridPanel, {
    layout : 'fit',
    initComponent : function () {
        
        this.urlModule = projectGlobal.sitoolsAttachementForUsers + loadUrl.get('APP_PROJECTS_MODULES_URL');
        
        this.store = new Ext.data.JsonStore({
            root : 'data',
            url : this.urlModule,
            autoLoad : true,
            fields : [{
                name : 'id'
            }, {
                name : 'name'
            },{
                name : 'title'
            }, {
                name : 'description'
            }, {
                name : 'imagePath'
            },{
                name : 'icon'
            }, {
                name : 'xtype'
            }]
        });
        
        this.viewConfig = {
            forceFit: true,
            autoFill : true
        };
        
        this.sm = new Ext.grid.RowSelectionModel({
            singleSelect:true,
            listeners : {
                scope : this,
                rowselect : function (grid, rowInd, rec) {
                    var btn = this.getBottomToolbar().find('name', 'selectButton')[0];
                    btn.setDisabled(false);
                },
                rowdeselect : function (grid, rowInd, rec) {
                    var btn = this.getBottomToolbar().find('name', 'selectButton')[0];
                    btn.setDisabled(true);
                }
            }
                
        });
        
        this.cm = new Ext.grid.ColumnModel({
            columns : [{
                width : 35,
                sortable : true,
                dataIndex : 'icon',
                renderer : function(value, metaData, record, rowIndex, colIndex, store) {
                    if (Ext.isEmpty(value)) {
                        return "";
                    }
//                    var imageReference = new Reference(value);
                    metaData.css += " " + value;
                    return "";
//                    return "<img src=" + imageReference.getFile() + " height=16 width=16 />";
                }
            }, {
                header : i18n.get('headers.name'),
                width : 170,
                sortable : true,
                dataIndex : 'name'
            }, {
                header : i18n.get('header.title'),
                width : 170,
                sortable : true,
                dataIndex : 'title'
            }]
        });
        
        this.bbar = ['->', {
            xtype : 'button',
            name : 'selectButton',
            text : i18n.get('label.select'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
            scope : this,
            disabled : true,
            handler : this.onValidate
        }];
        
        sitools.widget.sitoolsEditorPlugins.moduleBrowser.superclass.initComponent.call(this);
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function() {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };
    },

    onValidate : function() {
        var module = this.getSelectionModel().getSelected();
        if (Ext.isEmpty(module)) {
            return Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.selectModule'));
        }
        
        this.browseField.moduleTitle = module.data.title;
        this.browseField.moduleComponent = String.format("parent.sitools.user.component.module.moduleUtils.openModule(\"{0}\"); return false;", module.data.id);
        
        
        this.browseField.setValue('Module : ' + module.data.title);
        this.textField.setValue(module.data.title);
        
        this.ownerCt.close();
    }
});
Ext.reg('sitools.widget.sitoolsEditorPlugins.moduleBrowser', sitools.widget.sitoolsEditorPlugins.moduleBrowser);
