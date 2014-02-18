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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.modules');

/**
 * OpenSearch Module : 
 * displays all OpenSearch defined on any dataset attached to the project. 
 * @class sitools.user.modules.openSearchModule
 * @extends Ext.grid.GridPanel
 */
sitools.user.modules.openSearchModule = function () {
    var urlOpenSearchModule = projectGlobal.sitoolsAttachementForUsers + "/opensearch";
    this.store = new Ext.data.JsonStore({
        root : 'data',
        restful : true,
        remoteSort : true,
        url : urlOpenSearchModule,
        // sortField: 'name',
        idProperty : 'id',
        fields : [ {
            name : 'id',
            type : 'string'
        }, {
            name : 'parent',
            type : 'string'
        }, {
            name : 'name',
            type : 'string'
        }, {
            name : 'css',
            type : 'string'
        }, {
            name : 'description',
            type : 'string'
        }, {
            name : 'width',
            type : 'numeric'
        }, {
            name : 'height',
            type : 'numeric'
        }, {
            name : 'parameters'
        }, {
			name : 'authorized'	
        }, {
            name : 'parentUrl',
            type : 'string'
        }], 
        autoLoad : true
    });

    this.cm = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults : {
            sortable : false
        // columns are not sortable by default
        },
        columns : [{
            header : "",
            dataIndex : 'authorized',
            renderer : function (value) {
				if (value === "false") {
					return "<img height=\"15\" src='" + loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png'>";
				}
            }, 
            width : 20
        },  {
            header : i18n.get('label.name'),
            dataIndex : 'name',
            width : 100,
            sortable : true
        }, {
            header : i18n.get('label.description'),
            dataIndex : 'description',
            width : 350
        } ]
    });

    this.tbar = {
        xtype : 'toolbar',
        defaults : {
            scope : this
        },
        items : [ {
            text : i18n.get('label.viewOpenSearch'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
            handler : function () {
                var rec = this.getSelectionModel().getSelected();
                if (Ext.isEmpty(rec)) {
                    return;
                }
                this.showDetail(rec);
            },
            xtype : 's-menuButton'
        } ]
    };
    this.sm = Ext.create('Ext.selection.RowModel',{
        
    });
    sitools.user.modules.openSearchModule.superclass.constructor.call(this, Ext.apply({
        layout : 'fit', 
        listeners : {
            'rowdblClick' : function (grid, rowIndex) {
                this.showDetail(grid.getStore().getAt(rowIndex));
            }
        } 
    }));

};

Ext.extend(sitools.user.modules.openSearchModule, Ext.grid.GridPanel, {
    showDetail : function (rec) {
        if (rec.data.authorized === "false") {
			return;
        }
        Ext.Ajax.request({
            url : rec.data.parentUrl,
            method : 'GET', 
            success : function (response) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = {
                        sitoolsAttachementForUsers : json.dataset.sitoolsAttachementForUsers, 
                        id : json.dataset.id, 
                        name : json.dataset.name, 
                        columnModel : json.dataset.columnModel
                    };
                    var jsObj = sitools.user.component.datasetOpensearch;
                    var componentCfg = {
                        dataUrl : json.dataset.sitoolsAttachementForUsers,
                        datasetId : json.dataset.id,
                        datasetName : json.dataset.name, 
                        preferencesPath : "/" + json.dataset.name, 
                        preferencesFileName : "openSearch"
                    };
                    var windowSettings = {
                        datasetName : dataset.name, 
                        type : "form", 
                        title : i18n.get('label.openSearch') + " : " + dataset.name + "." + rec.data.name, 
                        id : "form" + dataset.id + rec.data.id, 
                        saveToolbar : true, 
                        iconCls : "openSearch"
                    };
                    
                    SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
                    return;
                }
                catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                    return;
                }
                
            }, 
            failure : function () {
                Ext.Msg.alert(i18n.get('label.error'));
                return;
            }
        });
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
});

Ext.reg('sitools.user.modules.openSearchModule', sitools.user.modules.openSearchModule);
