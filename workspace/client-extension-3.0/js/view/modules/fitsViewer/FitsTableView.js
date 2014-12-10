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
Ext.namespace('sitools.extension.view.modules.fitsViewer');
/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/

/**
 * Display metadata from FITS file in a grid
 * 
 * @class sitools.extension.view.modules.fitsViewer.FitsTableView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.extension.view.modules.fitsViewer.FitsTableView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.fitsTableView',
    
    layout : 'border',
    split : true,
    border : false,
    
    initComponent : function () {

    	this.i18nFitsViewer = I18nRegistry.retrieve('fitsViewer');
    	
        var columns = [];
        
        Ext.each(this.data.columns, function (col) {
            var c = Ext.create('Ext.grid.column.Column', {
                header : col,
                dataIndex : col,
                sortable : true,
                width : 80
            });
            columns.push(c);
            
        }, this);
        
        this.gridPanel = Ext.create('Ext.grid.Panel', {
            region : 'center',
            border : false,
            store : Ext.create('Ext.data.JsonStore', {
                fields : this.data.columns // only the columns names
            }),
            colModel : columns,
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : 'SINGLE'
            }),
            listeners : {
                scope : this,
                afterrender : function (grid) {
                	Ext.suspendLayouts();
                    for (var i = 0; i < this.data.rows; i++) {
                        if (i == 300) {
                            Ext.Msg.show({
                                title : i18n.get('label.info'),
                                msg : this.i18nFitsViewer.get('label.only300dataloaded'),
                                icon : Ext.MessageBox.INFO,
                                buttons : Ext.MessageBox.OK
                            });
                            break;
                        }
                        var rec = this.data.getRow(i);
                        grid.getStore().add(rec);
                    }
                    Ext.resumeLayouts(true);
                }
            }
        });

//        this.headerPanel = new sitools.extension.modules.sitoolsFitsHeader({
        this.headerPanel = Ext.create('sitools.extension.view.modules.fitsViewer.FitsHeaderView', {
            title : this.i18nFitsViewer.get('label.headerData'),
            border : false,
            headerData : this.headerData,
            region : 'south',
            height : 450,
            collapsible : true
        });

        this.tbar = {
        	border : false,
        	items : [{
	            text : 'Plot',
	            scope : this,
	            handler : this.plot,
	            icon : "/sitools/common/res/images/icons/plot.png"
	        }] 
        };
        
        this.items = [this.gridPanel, this.headerPanel];
        
        this.callParent(arguments);
    },
    
    afterRender : function () {
    	this.callParent(arguments);
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
        
        var fitsViewerPlot = Ext.create('sitools.extension.view.modules.fitsViewer.FitsViewerPlot', {
        	fitsTable : this,
        	dataColumns : dataColumns,
        	i18nFitsViewer : this.i18nFitsViewer,
        	title : this.i18nFitsViewer.get('label.plotTable')
        });
        
        fitsViewerPlot.show();
    }
});
