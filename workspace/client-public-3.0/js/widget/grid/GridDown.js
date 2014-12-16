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
Ext.namespace('sitools.public.widget.grid');	

Ext.define('sitools.public.widget.grid.GridDown', {
    extend: 'Ext.button.Button',
	alias : 'widget.gridDown',

    initComponent : function () {
        this.icon = loadUrl.get('APP_URL') + '/common/res/images/icons/simple-arrow-down.png';
        sitools.public.widget.grid.GridDown.superclass.initComponent.call(this);
    },  
	handler: function (){
	    if (Ext.isEmpty(this.gridId)) {
            var grid = this.down('grid');
        } else {
            var grid = Ext.getCmp(this.gridId);
        }
		 
		if (!grid) {
			var grid = this;
		} 
		
//		var grid = this;
		
		var rec = grid.getSelectionModel().getLastSelected();
        if (!rec){
            return;
        }
		var store = grid.getStore();
		if (!store){
			return;
		}
		var index = store.data.items.indexOf(rec);
		
		if(index < store.getCount()-1){
			store.remove (rec);
			store.insert (index+1, rec);
			grid.getSelectionModel().select(index+1);
			
		}
		grid.getView().refresh();
	}

	
});
