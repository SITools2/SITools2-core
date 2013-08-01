/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin*/

Ext.namespace('sitools.user.modules');
/**
 * ProjectDescription Module
 * @class sitools.user.modules.cartSelectionDetails
 * @extends Ext.Panel
 */
sitools.user.modules.cartSelectionDetails = Ext.extend(Ext.grid.GridPanel, {
    initComponent : function () {

        this.layout = 'fit';
        this.forceLayout = true;
        
        var fields = [];
        
        Ext.each(this.columnModel, function (col) {
            var field = {name : col.dataIndex};
            fields.push(field);
        }, this);
        
        this.store = new Ext.data.JsonStore({
            data : this.records,
            fields : fields
        });
        
        this.colModel = getColumnModel(this.columnModel);
        
//        this.gridPanel = new Ext.grid.GridPanel({
//            colModel : getColumnModel(this.columnModel),
//            store : this.store,
//            tbar : [ {
//                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/delete.png',
//                tooltip : i18n.get('label.deleteSelection'),
//                scope : this,
//                handler : function () {
//                    Ext.Msg.show({
//                        title : i18n.get('label.delete'),
//                        buttons : Ext.Msg.YESNO,
//                        msg : i18n.get('label.deleteCartSelection'),
//                        scope : this,
//                        fn : function (btn, text) {
//                            if (btn == 'yes') {
//                                this.onDelete();
//                            }
//                        }
//                    });
//                }
//            } ]
//
//        });
//        
//        this.items = [ this.gridPanel ];
        
        
        sitools.user.modules.cartSelectionDetails.superclass.initComponent.call(this);
    },
    
    onDelete : function () {
        
    }
    
});

Ext.reg('sitools.user.modules.cartSelectionDetails',sitools.user.modules.cartSelectionDetails);
