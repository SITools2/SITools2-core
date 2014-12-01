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
/*global Ext, sitools*/
Ext.ns('sitools.common.forms');

/**
 * Abstract class to build Sitools form components with units.
 * @class sitools.common.forms.AbstractWithUnit
 * @extends Ext.Container
 */
sitools.common.forms.AbstractWithUnit = Ext.extend(Ext.Container, {
//sitools.component.users.SubSelectionParameters.AbstractComponentsWithUnit = Ext.extend(Ext.Container, {
   dimensionId : null,
   userUnit : null, 
   userDimension : null,
   
   initComponent : function () {
		this.userDimension = this.dimensionId;
		this.userUnit = this.unit;
		
		this.storeUnits = new Ext.data.JsonStore({
            id : 'storeUnitSelect',
            root : 'dimension.units',
            idProperty : 'id',
            fields : [ {
                name : 'unitName',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }]
        });
                    
        sitools.common.forms.AbstractWithUnit.superclass.initComponent.call(this);
   },
   /**
    * Load all units available with a given dimension.
    * On Callback : show a {Ext.Window} with the result in a gridPanel
    * @param {} event The click event (to get coordinates) 
    */
   loadUnits : function (button, event) {
        if (!this.unitsLoaded) {
            this.storeUnits.removeAll();
            Ext.Ajax.request({
                method : "GET",
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension/' + this.dimensionId,
                scope : this, 
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        return;
                    }
                    var units = Json.dimension.sitoolsUnits;
                    this.dimensionName = Json.dimension.name;
                    for (var i = 0; i < units.length; i++) {
                        this.storeUnits.add(new Ext.data.Record(units[i]));
                    }
                }, 
                failure : alertFailure,
                callback : function () {
                    this.unitsLoaded = true;
                    this.showWinUnits(button, event);
                }
            });
        }
        else {
            this.showWinUnits(button, event);
        }
    }, 
    
    /**
     * Create and show a {Ext.Window} window with the loaded units 
     * build the gridUnits. 
     * @param {} event The click event to get coordinates for the window
     */
    showWinUnits : function (button, event) {

        var cmUnits = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'label',
                width : 100
            }],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        var smUnits = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        this.gridUnits = new Ext.grid.GridPanel({
            autoScroll : true,
            store : this.storeUnits,
            cm : cmUnits,
            sm : smUnits, 
            layout : 'fit', 
            listeners : {
                scope : this, 
                rowClick : function (grid, rowIndex) {
                    this.onValidateUnits(button);
                }
            }
        });
        var winUnit = new Ext.Window({
            layout : 'fit', 
            width : 200, 
            title : i18n.get('title.unitList'),
            modal : true,
            height : 300, 
            items : [this.gridUnits], 
            buttons : [{
                text : i18n.get('label.ok'), 
                handler : function () {
                    this.onValidateUnits(button) 
                },
                scope : this
            }, {
                text : i18n.get('label.cancel'), 
                scope : winUnit, 
                handler : function () {
                    this.ownerCt.ownerCt.close();                
                }
            }]
        }); 
        
        winUnit.show();
        winUnit.setPosition(event.getXY()[0], event.getXY()[1]);
    }, 
    /**
     * update property this.userDimension and this.userUnit, depending on the selected record in this.gridUnits
     * update the label of the button withe the new unit
     */
    onValidateUnits : function (btn) {
        var rec = this.gridUnits.getSelectionModel().getSelected();
        if (Ext.isEmpty(rec)) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noSelection'));
            return;
        }
        this.userUnit = rec.data;
        this.userDimension = this.dimensionName;
        if (! Ext.isEmpty(btn)) {
            btn.setText(rec.get('label'));
        }
        this.gridUnits.ownerCt.close();
    }, 
//    /**
//     * Return the unit corresponding to a given column Alias
//     * @param {String} columnAlias the column Alias to retrieve
//     * @return {} the unit founded
//     */
//    getColumnUnit : function (columnAlias) {
//    	if (Ext.isEmpty(this.datasetCm)) {
//    		return;
//    	}
//    	var result;
//    	Ext.each(this.datasetCm, function(column){
//    		if (column.columnAlias == columnAlias) {
//    			result = column.unit;
//    		}
//    	});
//    	return result;
//    }, 
    /**
     * build a {Ext.Container} container with 
     * <ul><li>a {Ext.Button} button if column unit is not null and administrator defines a dimension</li>
     * <li>A simple text if column unit is not null and administrator not defines a dimension</li>
     * <li>null when column unit is null</li></ul> 
     * @return {} null or the builded container
     */
    getUnitComponent : function () {
	    var columnUnit = this.context.getRefUnit(this);
        //the administrator defines a dimension for this component
	    // and the column unit is not null
        if (!Ext.isEmpty(this.dimensionId)) {
            var btn = new Ext.Button({
                scope : this, 
                text : Ext.isEmpty(columnUnit) ? "    " : columnUnit.label, 
                width : 90,
                sitoolsType : 'unitbutton',
                handler : function (b, e) {
                    this.loadUnits(b, e);
                    //this.userDimension = this.dimensionId;
                }
            });
            unit = new Ext.Container({
            	layout : "hbox", 
            	layoutConfig : {
            		pack : "center", 
            		align : "middle"
            	},
            	margins : {top:0, right:0, bottom:0, left:10}, 
	        	width : 100, 
            	items : [btn]
            });
        }
        else {
            if (!Ext.isEmpty(columnUnit)) {
                unit = new Ext.Container({
		    		html : columnUnit.label, 
		    		margins : {top:0, right:0, bottom:0, left:10}, 
	        		flex : 1
		    	});
            }
            else {
                unit = null;
            }
            
        }
        return unit;
    	
    }
});


