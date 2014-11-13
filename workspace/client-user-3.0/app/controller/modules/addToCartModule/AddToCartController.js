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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.controller.modules.addToCartModule');
/**
 * datasetExplorer Module
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.addToCartModule.AddToCartController', {
    extend : 'Ext.app.Controller',
    
    views : [ 'sitools.user.view.modules.addToCartModule.AddToCartModuleView',
              'sitools.user.view.modules.addToCartModule.CartSelectionDetailsView'
          ],

    init : function () {
    	
        this.control({
            'addToCartModule' : {
            	afterrender : function (cartView) {
            		if (cartView.user == "public") {
                        var orderBtn = cartView.down('toolbar').down('button[name=orderBtn]');
                        orderBtn.setDisabled(true);
                        cartView.down('toolbar').insert(1, {
                            xtype : 'label',
                            html : "<img src='/sitools/common/res/images/ux/warning.gif'/> <b>" + i18n.get('label.needToBeLogged') + "</b>"
                        });
                        
                    } else {
                		cartView.loadOrderFile();
                    }
            	}
            },
            
            'addToCartModule button#refresh' : {
                click : this.onRefresh
            },
            
            'addToCartModule grid[name=cartGridPanel]' : {
            	itemclick : function (grid, record, item, ind) {
            		var cartView = grid.up('addToCartModule');
                    
                    var modifyBtn = cartView.down('toolbar').down('button[name=modifySelectionBtn]');
                    if (Ext.isEmpty(record)) {
                        if (!Ext.isEmpty(modifyBtn)) {
                        	cartView.down('toolbar').remove(modifyBtn);
                        }
                        return;
                    } else {
                        if (Ext.isEmpty(modifyBtn)) {
                        	cartView.down('toolbar').insert(1, {
                                text : i18n.get('label.modifySelection'),
                                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                                name : 'modifySelectionBtn',
                                cls : 'services-toolbar-btn',
                                scope : cartView,
                                handler : cartView.modifySelection,
                                itemId : 'modifySelection'
                            });
                        }
//                        cartView.down('toolbar').doLayout();
                        cartView.containerArticlesDetailsPanel.expand();
                        this.viewArticlesDetails(cartView);
                    }
                }
            },
            
            'addToCartModule button#modifySelection' : {
                click : this.modifySelection
            }

        });
    },
    
    modifySelection : function (btn) {
        var me = btn.up("addToCartModule");
        var gridPanel = me.down("grid#selectionsPanel");
        var selected = gridPanel.getSelectionModel().getSelection();
        if (Ext.isEmpty(selected)) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')
                    + '/res/images/msgBox/16/icon-info.png');
        }
        selected = selected[0];
        var url = selected.data.dataUrl;
        var params = {
            ranges : selected.get('ranges'),
            startIndex : selected.get('startIndex'),
            nbRecordsSelection : selected.get('nbRecords'),
            gridFilters : selected.get('gridFilters'),
            gridFiltersCfg : selected.get('gridFiltersCfg'),
            storeSort : selected.get('storeSort'),
            formParams : selected.get('formParams'),
            isModifySelection : true
        };
        sitools.user.utils.DatasetUtils.clickDatasetIcone(url, 'data', params);
    },
    
    viewArticlesDetails : function (me) {
        var gridPanel = me.down("grid#selectionsPanel");
        var selected = gridPanel.getSelectionModel().getSelection();
        if (Ext.isEmpty(selected)) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')
                    + '/res/images/msgBox/16/icon-info.png');
        }
        selected = selected[0];
        
        me.containerArticlesDetailsPanel.removeAll();
        var detailsSelectionPanel = Ext.create('sitools.user.view.modules.addToCartModule.CartSelectionDetailsView', {
            selection : selected,
            columnModel : selected.data.colModel,
            url : selected.data.dataUrl + "/records",
            selections : selected.data.selections, 
            cartOrderFile : this.cartOrderFile,
            cartModule : this
        });
        
        me.containerArticlesDetailsPanel.add(detailsSelectionPanel);
        me.containerArticlesDetailsPanel.setTitle(Ext.String.format(i18n.get('label.orderDetails'), selected.data.selectionName));
//        this.containerArticlesDetailsPanel.doLayout();
    },
    
    onRefresh : function (btn) {
        var me = btn.up("addToCartModule");
        me.cartsSelectionsStore.reload();
        me.gridPanel.getSelectionModel().clearSelections();
        me.containerArticlesDetailsPanel.collapse();
        me.containerArticlesDetailsPanel.setTitle('');
        me.containerArticlesDetailsPanel.removeAll();
        
        var modifySelBtn = me.down('toolbar').down('button[name=modifySelectionBtn]');
        if (!Ext.isEmpty(modifySelBtn)) {
            me.down('toolbar').remove(modifySelBtn);
//            this.down('toolbar').doLayout();
        }
    },
});
