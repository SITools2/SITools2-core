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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, extColModelToSrv*/
/**
 * Redefine the method beforeColMenuShow to request the store when adding a column
 * @class Ext.ux.sitoolsGridView
 * @extends Ext.grid.GridView
 */
Ext.define('sitools.public.widget.grid.SitoolsView', {
    extend : 'Ext.grid.View',
    alternateClassNames : 'Ext.ux.sitoolsGridView',
	// surcharge de la méthode pour que l'ajout d'une colonne relance une
	// interrogation du store
	// avec comme parametre le nouveau columnModel
//	beforeColMenuShow : function () {
//		var cm = this.cm, colCount = cm.getColumnCount();
//		this.colMenu.removeAll();
//		for (var i = 0; i < colCount; i++) {
//			if (cm.config[i].hideable !== false) {
//				this.colMenu.add(new Ext.menu.CheckItem({
//				    itemId : 'col-' + cm.getColumnId(i),
//				    text : cm.getColumnHeader(i),
//				    checked : !cm.isHidden(i),
//				    hideOnClick : false,
//				    disabled : cm.config[i].hideable === false,
//				    listeners : {
//				        scope : this,
//				        checkchange : function (ci, checked) {
//					        if (checked) {
//						        var colModel = extColModelToSrv(this.cm);
//						        this.grid.getStore().load({
//							        params : {
//								        colModel : Ext.util.JSON.encode(colModel)
//							        }
//						        });
//					        }
//				        }
//				    }
//				}));
//			}
//		}
//	}, 
	
    doRender : function(columns, records, store, startRow, colCount, stripe) {
        var templates    = this.templates,
            cellTemplate = templates.cell,
            rowTemplate  = templates.row,
            last         = colCount - 1;

        var tstyle = 'width:' + this.getTotalWidth() + ';';

        
        var rowBuffer = [],
            colBuffer = [],
            rowParams = {tstyle: tstyle},
            meta      = {},
            column,
            record;

        
        for (var j = 0, len = records.length; j < len; j++) {
            record    = records[j];
            colBuffer = [];

            var rowIndex = j + startRow;

            
            for (var i = 0; i < colCount; i++) {
                column = columns[i];

                meta.id    = column.id;
                meta.css   = i === 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
                meta.attr  = meta.cellAttr = '';
                meta.style = column.style;
                meta.value = column.renderer.call(column.scope, record.data[column.name], meta, record, rowIndex, i, store);

                if (Ext.isEmpty(meta.value)) {
                    meta.value = '&#160;';
                }

                if (this.markDirty && record.dirty && Ext.isDefined(record.modified[column.name])) {
                    meta.css += ' x-grid3-dirty-cell';
                }

                colBuffer[colBuffer.length] = cellTemplate.apply(meta);
            }

            
            var alt = [];

            if (stripe && ((rowIndex + 1) % 2 === 0)) {
                alt[0] = 'x-grid3-row-alt';
            }

            if (record.dirty) {
                alt[1] = ' x-grid3-dirty-row';
            }

            rowParams.cols = colCount;

            if (this.getRowClass) {
                alt[2] = this.getRowClass(record, rowIndex, rowParams, store);
            }

            rowParams.alt   = alt.join(' ');
            rowParams.cells = colBuffer.join('');

            rowBuffer[rowBuffer.length] = rowTemplate.apply(rowParams);
        }
		//Ajout du cas où il n'y a pas de records.
		if (records.length == 0) {
			var alt = [];

            rowParams.cols = colCount;

            rowParams.cells = " ";

            rowBuffer[0] = rowTemplate.apply(rowParams);
		}
        return rowBuffer.join('');
    }, 
    renderRows : function(startRow, endRow){
        
        var g = this.grid, cm = g.colModel, ds = g.store, stripe = g.stripeRows;
        var colCount = cm.getColumnCount();

        //if(ds.getCount() < 1){
          //  return '';
        //}

        var cs = this.getColumnData();

        startRow = startRow || 0;
        endRow = !Ext.isDefined(endRow) ? ds.getCount()-1 : endRow;

        
        var rs = ds.getRange(startRow, endRow);

        return this.doRender(cs, rs, ds, startRow, colCount, stripe);
    }
});
