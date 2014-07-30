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
    
    views : [ 'modules.addToCartModule.AddToCartView' ],

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
            
            'addToCartModule gridPanel[name=cartGridPanel]' : {
            	rowclick : function (grid, ind) {
            		var cartView = grid.up('addToCartModule');
                    var selected = grid.selModel.getSelections()[0];
                    
                    var modifyBtn = cartView.down('toolbar').down('buton[name=modifySelectionBtn]');
                    if (Ext.isEmpty(selected)) {
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
                                handler : cartView.modifySelection
                            });
                        }
//                        cartView.down('toolbar').doLayout();
                        cartView.containerArticlesDetailsPanel.expand();
                        cartView.viewArticlesDetails();
                    }
                }
            }

        });
    }
});
