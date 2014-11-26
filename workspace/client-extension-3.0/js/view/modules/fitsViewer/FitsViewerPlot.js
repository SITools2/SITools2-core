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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/
Ext.namespace('sitools.extension.view.modules.fitsViewer');
/**
 * sitools.extension.modules.sitoolsFitsPlot
 * 
 * @class sitools.extension.modules.sitoolsFitsPlot
 * @extends Ext.Panel
 */
Ext.define('sitools.extension.view.modules.fitsViewer.FitsViewerPlot', {
//    extend : 'Ext.panel.Panel',
    extend : 'Ext.window.Window',
    alias : 'widget.fitsViewerPlot',
            
    modal : true,
    layout : 'fit',
    border : false,
    
    initComponent : function () {
        
    	this.i18nFitsViewer = I18nRegistry.retrieve('fitsViewer');
    	
        this.comboColX = Ext.create('Ext.form.field.ComboBox', {
            xtype : 'combo',
            name : 'colX',
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'local',
            width : 120,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: [ 'columnName' ],
                data: this.dataColumns
            }),
            valueField: 'columnName',
            displayField: 'columnName'
        });
        
        this.comboColY = Ext.create('Ext.form.field.ComboBox', {
            xtype : 'combo',
            name : 'colY',
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'local',
            width : 120,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: [ 'columnName' ],
                data: this.dataColumns
            }),
            valueField: 'columnName',
            displayField: 'columnName'
        });
        
        this.btnDrawPlot = Ext.create('Ext.button.Button', {
            text : this.i18nFitsViewer.get('label.plotDraw'),
            icon : "/sitools/common/res/images/icons/plot.png",
            scope : this,
            handler : this.draw
        });
        
        this.panel = Ext.create('Ext.panel.Panel', {
            xtype : 'panel',
            layout : 'fit',
            autoScroll : true,
            fitsTable : this,
            html : '<div style="width :590px; height:350px;" id="flotr-graph"></div>',
            tbar : Ext.create('Ext.toolbar.Toolbar', {
                cls : 'services-toolbar',
                items : [{
                    xtype : 'label',
                    html : '<b>y</b> ',
                    style : "padding-right : 5px"
                }, this.comboColY , {
                    xtype : 'label',
                    html : '<b>= f(x </b> ',
                    style : "padding: 0px 5px 0px 5px"
                }, this.comboColX ,{
                    xtype : 'label',
                    html : '<b>)</b> ',
                    style : "padding-left: 5px"
                }, '->', this.btnDrawPlot] 
            })
        });
        
        this.items = [this.panel];
        this.callParent(arguments);
    },
    
    draw : function () {
        var valueX = this.comboColX.getValue();
        var valueY = this.comboColY.getValue();
        
        if (Ext.isEmpty(valueX) || Ext.isEmpty(valueY)) {
            this.comboColX.markInvalid(this.i18nFitsViewer.get('label.chooseColumn'));
            this.comboColY.markInvalid(this.i18nFitsViewer.get('label.chooseColumn'));
            return;
        }
        
        var data = [];
        this.fitsTable.gridPanel.getStore().each(function (rec) {
            var x = rec.get(valueX); 
            var y = rec.get(valueY);
            
            if (x != undefined && y != undefined) {
                var d = [x, y];
                data.push(d);
            }
            
        }, this);
        
      var container = document.getElementById("flotr-graph");
      // Draw the graph
      graph = Flotr.draw(
        container, [ 
          { data : data, label : 'y ('+ valueY +') = f(x ' +valueX+ ')', points : { show : true } }
        ],
        {
          legend : { position : 'sw', backgroundColor : '#D2E8FF' },
          title : 'Plot',
          mouse: {
              track: true,
              position: 'se',
              relative: false,
              trackFormatter: Flotr.defaultTrackFormatter,
              margin: 5,
              lineColor: '#FF3F19',
              trackDecimals: 2,
              sensibility: 2,
              trackY: true,
              radius: 3,
              fillOpacity: 0.4
            }
        }
      );
    }
});
