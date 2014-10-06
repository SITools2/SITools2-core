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
/* global Ext, sitools, window */

Ext.define('sitools.user.store.CartsSelectionsStore', {
    extend : 'Ext.data.Store',
    model : 'sitools.user.model.CartSelectionModel',
    proxy : {
		type : 'ajax',
		reader : {
			type : 'json',
			root : 'selections',
			idProperty : 'selectionId'
		}
	},
	
	listeners : {
        scope : this,
        exception : function (dataProxy, type, action, options, response, arg) {
            if (response.status === 404) {
                this.cartsSelectionsStore.removeAll();
                return;
            }
            if (response.status === 403) {
                return Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    msg : i18n.get('label.needToBeLogged'),
                    icon : Ext.MessageBox.WARNING,
                    buttons : Ext.MessageBox.OK
                });
            }                    
        }
    },
    
    setCustomUrl : function (url) {
        this.getProxy().url = url;
    }
});