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
Ext.namespace('sitools.user.modules');
/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/

/**
 * sitools.user.modules.sitoolsFitsTable
 * 
 * @class sitools.user.modules.sitoolsFitsTable
 * @extends Ext.Panel
 */
sitools.user.modules.sitoolsFitsTable = Ext.extend(Ext.Panel, {
    layout : 'border',
    split : true,
    initComponent : function () {

        var columns = [];
        
        Ext.each(this.data.columns, function (col) {
            var c = new Ext.grid.Column({
                header : col,
                dataIndex : col,
                sortable : true,
                width : 80
            });
            columns.push(c);
            
        }, this);
        
        this.gridPanel = new Ext.grid.GridPanel({
            region : 'center',
            store : new Ext.data.JsonStore({
//                fields : [ 'name', 'value', 'description' ]
                fields : this.data.columns // only the columns names
            }),
            colModel : new Ext.grid.ColumnModel(columns),
            selModel : new Ext.grid.RowSelectionModel({
                singleSelect : true
            }),
            listeners : {
                scope : this,
                afterrender : function (grid) {
                    for (var i = 0; i < this.data.rows; i++) {
                        if (i == 300) {
                            Ext.Msg.show({
                                title : i18n.get('label.info'),
                                msg : i18n.get('label.only300dataloaded'),
                                icon : Ext.MessageBox.INFO,
                                buttons : Ext.MessageBox.OK
                            });
                            break;
                        }
                        var row = this.data.getRow(i);
                        var rec = new Ext.data.Record(row);
                        grid.getStore().add(rec);
                    }
                }
            }
        });

        this.headerPanel = new sitools.user.modules.sitoolsFitsHeader({
            title : i18n.get('label.headerData'),
            headerData : this.headerData,
            region : 'south',
            height : 500,
            collapsible : true
        });

        this.tbar = {
            autoHeight : true,
            cls : 'services-toolbar',
            defaults : {
                scope : this
            },
            items : [{
                text : 'Plot',
                handler : this.plot,
                icon : "/sitools/res/images/icons/plot.png"
            }]
        };
        
        this.items = [this.gridPanel, this.headerPanel];
        
        sitools.user.modules.sitoolsFitsTable.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.user.modules.sitoolsFitsTable.superclass.afterRender.apply(this, arguments);
        this.fitsMainPanel.getEl().unmask();
    },
    
    plot : function () {
        var dataColumns = [];
        for (var i=1; i < this.data.columns.length; i++) {
            
            var indForm = this.headerPanel.getStore().find('name', "TFORM" + i);
            var recForm = this.headerPanel.getStore().getAt(indForm);
            
            // get's only the first letter
            var type = recForm.data.value.substring(0, 1);
            
            // if column type is numeric
            if (type == "I" || type == "J" || type == "K" || type == "E" || type == "D") {
                var indType = this.headerPanel.getStore().find('name', "TTYPE" + i);
                var recType = this.headerPanel.getStore().getAt(indType);
                
                var data = [];
                data.push(recType.data.value);
                dataColumns.push(data);
            }
        }
        
        var jsObj = sitools.user.modules.sitoolsFitsPlot;
        
        var config = {
            fitsTable : this,
            dataColumns : dataColumns
        };
        
        var windowConfig = {
            layout : 'fit',
            title : 'Plot Table',
            autoScroll : true
        };
        
        SitoolsDesk.addDesktopWindow(windowConfig, config, jsObj);
    }
});
Ext.reg('sitools.user.modules.sitoolsFitsTable', sitools.user.modules.sitoolsFitsTable);