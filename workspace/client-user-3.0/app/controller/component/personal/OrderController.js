/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/**
 * Populate the div x-headers of the sitools Desktop. 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.component.personal.OrderController', {
    extend : 'Ext.app.Controller',
    
    views : ['component.personal.OrderView'],
    
    init : function () {
        
        this.control({
            "orderview gridpanel#listorder pagingtoolbar" : {
                change : function (pagingToolbar, pageData) {
                    var me = pagingToolbar.up("orderview");
                    me.detailPanel.removeAll();
                    me.detailPanel.setVisible(false);
                }
            },

            "orderview gridpanel#listorder":{
                itemclick : this._onDetail
            },

            'orderview' : {
                render : function (me) {
                    me.store.load({
                        start : 0,
                        limit : me.pageSize
                    });
                }
            },

            "orderview toolbar#servicetoolbar button#delete" : {
                click : this._onDelete
            }

        });
        this.callParent(arguments);
    },


    _onDetail : function (grid, rec) {
        var me = grid.up("orderview");
        var orderDetailView = Ext.create('sitools.user.view.component.personal.OrderDetailView', {
            action : 'detail',
            orderRec : rec
        });

        me.detailPanel.removeAll();
        me.detailPanel.add(orderDetailView);
        me.detailPanel.setVisible(true);
    },

    _onDelete : function (deleteBtn) {
        var me = deleteBtn.up("orderview");
        var rec = me.down("gridpanel#listorder").getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            icon : Ext.Msg.QUESTION,
            msg : Ext.String.format(i18n.get('orderCrud.delete'), rec.get('id')),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec, me);
                }
            }
        });

    },
    doDelete : function (rec, view) {
        Ext.Ajax.request({
            url : view.url + "/" + rec.getId(),
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    view.store.reload();
                }
            },
            failure : alertFailure
        });
    }

});