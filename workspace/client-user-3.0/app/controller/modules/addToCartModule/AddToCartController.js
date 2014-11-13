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
                		this.loadOrderFile();
                    }
            		
            		
            		cartView.gridPanel.getStore().on("load", this.callbackOrderFile, this);
            	},
            	
            	'refresh' : this.onRefresh
            },
            
            'addToCartModule toolbar#staticCartToolbar button#refresh' : {
                click : this.onRefresh
            },

            'addToCartModule toolbar#staticCartToolbar button#deleteOrder' : {
                click : function(btn) {
                    var me = btn.up('addToCartModule');
                    var selected = me.gridPanel.getSelectionModel().getSelection();
                    if (Ext.isEmpty(selected)) {
                        return Ext.Msg.show({
                            title : i18n.get('label.warning'),
                            msg : i18n.get('warning.selectionToRemove'),
                            icon : Ext.MessageBox.INFO,
                            buttons : Ext.MessageBox.OK
                        });
                    }
                    Ext.Msg.show({
                        title : i18n.get('label.delete'),
                        icon : Ext.MessageBox.WARNING,
                        buttons : Ext.MessageBox.YESNO,
                        msg : i18n.get('label.deleteCartOrder'),
                        scope : this,
                        fn : function (btn, text) {
                            if (btn == 'yes') {
                                this.onDelete(me);
                            }
                        }
                    });
                }
            },
            
            'addToCartModule grid[name=cartGridPanel]' : {
                itemclick : function (grid, record, item, ind) {
            		var cartView = grid.up('addToCartModule');
                    
                    var modifyBtn = cartView.down('toolbar#mainMenuToolbar').down('button[name=modifySelectionBtn]');
                    if (Ext.isEmpty(record)) {
                        if (!Ext.isEmpty(modifyBtn)) {
                        	cartView.down('toolbar#mainMenuToolbar').remove(modifyBtn);
                        }
                        return;
                    } else {
                        if (Ext.isEmpty(modifyBtn)) {
                        	cartView.down('toolbar#mainMenuToolbar').insert(1, {
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
            
            'addToCartModule toolbar#mainMenuToolbar button#modifySelection' : {
                click : this.modifySelection
            },
            
            'addToCartModule cartSelectionDetails' : {
                render : function (grid) {
                    this.loadRecordsFile(grid);
                },
                
                afterrender : function (grid) {
                    grid.getEl().mask(i18n.get('label.loadingArticles'));
                },
            },

            'addToCartModule menu#orderMenu menuitem' : {
                click : function (btn) {
                    this.downloadSelection(btn);
                }
            }
            

        });
    },
    /**
     * Modify the selected selection
     */
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
            sortInfo : selected.get('sortInfo'),
            formParams : selected.get('formParams'),
            isModifySelection : true
        };
        sitools.user.utils.DatasetUtils.clickDatasetIcone(url, 'data', params);
    },
    
    /**
     * Display the records grid
     */
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
    
    /**
     * Refresh the list of selections
     */
    onRefresh : function () {
        var me = Ext.ComponentQuery.query("addToCartModule")[0];
        this.loadOrderFile();
        me.gridPanel.getSelectionModel().deselectAll();
        me.containerArticlesDetailsPanel.collapse();
        me.containerArticlesDetailsPanel.setTitle('');
        me.containerArticlesDetailsPanel.removeAll();
        
        var modifySelBtn = me.down('toolbar#mainMenuToolbar').down('button[name=modifySelectionBtn]');
        if (!Ext.isEmpty(modifySelBtn)) {
            me.down('toolbar#mainMenuToolbar').remove(modifySelBtn);
//            this.down('toolbar').doLayout();
        }
    },
    
    /**
     * Load records on the grid
     */
    loadRecordsFile : function (grid) {
        var params = Ext.apply({
            start : 0,
            limit : 300
        }, grid.params);
        
        grid.store.load({
            params : params
        });
    },
    
    /**
     * Download orders (metadata + records)
     * @returns
     */
    downloadSelection : function (btn) {
        
        var me = btn.up("addToCartModule"),
        serviceName = btn.serviceName,
        selections = [];
        
        me.gridPanel.getStore().each(function (rec) {
            selections.push(rec); 
        });
        
        if (selections.length === 0) {
            return Ext.Msg.show({
                title : i18n.get('label.warning'),
                msg : i18n.get('warning.noSelectionToOrder'),
                icon : Ext.MessageBox.INFO,
                buttons : Ext.MessageBox.OK
            });
        }
        
        var putObject = {};
        putObject.selections = [];
        
        Ext.each(selections, function (selection) {
            var tmpSelection = {};
           // delete selection.data.colModel;
            delete selection.data.records;
            delete selection.data.startIndex;
            Ext.apply(tmpSelection, selection.data);
            putObject.selections.push(tmpSelection);
        });
        
        Ext.Ajax.request({
            url : Project.sitoolsAttachementForUsers + "/services",
            method : 'GET',
            scope : me,
            success : function (response) {
                var json = Ext.decode(response.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.error'), json.message);
                    return;
                }
                var services = json.data;
                var index = Ext.each(services, function (service) {
                    if (service.name === serviceName) {
                        return false;
                    }
                }, this);
                var resource = services[index];
                if(!Ext.isEmpty(resource)) {
                    var parameters = resource.parameters;
                    var url = null, icon = null, method = null, runTypeUserInput = null;
                    parameters.each(function (param) {
                        switch (param.name) {
                        case "methods":
                            method = param.value;
                            break;
                        case "url":
                            url = Project.sitoolsAttachementForUsers + param.value;
                            break;
                        case "runTypeUserInput":
                            runTypeUserInput = param.value;
                            break;
                        case "image":
                            icon = param.value;
                            break;
                        }
                    }, this);
                    
                    // add a parameter with the cart File URL
                    parameters.push({
                        description : "cart file URL",
                        name : "cartFile",
                        type: "PARAMETER_IN_QUERY",
                        value : this.urlCartFileForServer
                    });
                    
                    var serviceServerUtil = Ext.create("sitools.user.utils.ServerServiceUtils", {
                        datasetUrl : Project.sitoolsAttachementForUsers,
                        datasetId : Project.projectId, 
                        origin : "sitools.user.modules.projectServices"
                    });
                    
                    var cb = function (success) {
                        if (!Ext.isEmpty(success) && !success) {
                            this.down('toolbar').enable();
                        } else {
                            //close the module
                            this.up("component[specificType=moduleWindow]").close();
                            
                            popupMessage({
                                iconCls : 'x-icon-information',
                                title : i18n.get('label.info'),
                                html : i18n.get('label.orderWasRun'),
                                autoDestroy : true,
                                hideDelay : 1000
                            });
                            
                        }
                    };
                    var callback = Ext.bind(cb, this);
                    
                    this.down('toolbar').disable();
                    serviceServerUtil.resourceClick(resource, url, method, runTypeUserInput, parameters, null, callback);
                }
            }
        });
        
    },
    
    // DELETE SELECTION
    
    onDelete : function (addToCartView) {
        UserStorage.get(addToCartView.user + "_CartSelections.json", getCartFolder(Project.projectName), this, this.deleteOrder, this.deleteOrderError, Ext.emptyFn );
    },
    
    deleteOrderError : function (response) {
        Ext.Msg.show({
            title : i18n.get('label.warning'),
            msg : i18n.get('label.cannotRemoveSelection'),
            icon : Ext.MessageBox.ERROR,
            buttons : Ext.MessageBox.OK
        });
    },
    
    /**
     * Delete selected orders
     */
    deleteOrder : function (response) {
        if (Ext.isEmpty(response.responseText)) {
            return;
        }
        var json = Ext.decode(response.responseText);
        var cartOrderFile = json;
        var cb = Ext.bind(this.doDeleteOrder, this, [cartOrderFile]);
        this.loadOrderFile(cb, this);
    },
    
    doDeleteOrder : function (cartOrderFile) {
        var addToCartView = Ext.ComponentQuery.query("addToCartModule")[0];
        var selections = addToCartView.gridPanel.getSelectionModel().getSelection();
        
        if (selections.length === 0) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        
        var collSelections = Ext.create("Ext.util.MixedCollection");
        collSelections.addAll(selections);
        
        var collCartSelections = Ext.create("Ext.util.MixedCollection");
        collCartSelections.addAll(cartOrderFile.selections);
        
        collCartSelections.each(function (cartSelection, indCart) {
            collSelections.each(function (delSelection, indDel) {
                if (cartSelection.selectionId == delSelection.data.selectionId) {
                    collCartSelections.remove(cartSelection);
                }
            });
        });
        cartOrderFile.selections = collCartSelections.items;
        
        UserStorage.set(addToCartView.user + "_CartSelections.json", getCartFolder(Project.projectName), cartOrderFile, this.onRefresh, this);
    },
    
    loadOrderFile : function (callback, scope) {
        var addToCartView = Ext.ComponentQuery.query("addToCartModule")[0];
        addToCartView.cartsSelectionsStore.load({
            callback : callback,
            scope : scope
        });
    },
    
    
    // check dataset date 
    callbackOrderFile : function () {
        var addToCartView = Ext.ComponentQuery.query("addToCartModule")[0];
        var orderBtn = addToCartView.down('toolbar').down('button[name=orderBtn]');
        orderBtn.setDisabled(false);
//        var selectionsToCheck = Ext.create("Ext.util.MixedCollection");
//        var arraySelections = [];
//        addToCartView.cartsSelectionsStore.each(function (record) {
//            arraySelections.push(record);
//        });
//        selectionsToCheck.addAll(arraySelections);
        
        //Get all records from the store
        var selectionsToCheck = addToCartView.cartsSelectionsStore.getRange();
        
        var isDatasetUpdated = this.checkDatasetExpirationDate(selectionsToCheck, false);
        
        if (!isDatasetUpdated) {
            var warningLabel = addToCartView.down('toolbar#mainMenuToolbar').down('label[name=warningDateLabel]');
            if (!Ext.isEmpty(warningLabel)) {
                addToCartView.down('toolbar#mainMenuToolbar').remove(warningLabel);
//                this.down('toolbar').doLayout();
            }
        }
    },
    
    checkDatasetExpirationDate : function (selectionsToCheck, isDatasetUpdated) {
        if (selectionsToCheck.length > 0) {
            this.compareDate(selectionsToCheck, selectionsToCheck[0], isDatasetUpdated);
        }
        return isDatasetUpdated;
    },
    
    compareDate : function (allSelectionsToCheck, selectionToCheck, isDatasetUpdated) {
        if (Ext.isEmpty(selectionToCheck)) {
            return;
        }
        var addToCartView = Ext.ComponentQuery.query("addToCartModule")[0];
        var dataSelection = selectionToCheck.data;
        Ext.Ajax.request({
            url : dataSelection.dataUrl,
            method : 'GET',
            scope : this,
            success : function (response) {
                var json = Ext.decode(response.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.error'), json.message);
                    return;
                }
                
//                var rowInd = addToCartView.cartsSelectionsStore.indexOf(addToCartView.cartsSelectionsStore.getById(dataSelection.selectionId));
//                var htmlRow = addToCartView.gridPanel.getView().getRow(rowInd);
                
                var dateDataset = Ext.Date.parse(json.dataset.expirationDate, SITOOLS_DATE_FORMAT);
                if (dateDataset > dataSelection.orderDate) {
                    isDatasetUpdated = true;
//                    htmlRow.className += " x-grid3-row-warningDate";
                    addToCartView.gridPanel.getView().addRowCls(selectionToCheck, "x-grid3-row-warningDate");
                    var labelWarning = Ext.create('Ext.form.Label', {
                        name : 'warningDateLabel',
                        html : "<img src='/sitools/common/res/images/ux/warning.gif'/> " + i18n.get('warning.datasetUptated') + ""
                    });
                    if (!addToCartView.down('toolbar#mainMenuToolbar').down('label[name=warningDateLabel]')) {
                        addToCartView.down('toolbar#mainMenuToolbar').insert(1, labelWarning);
//                        this.down('toolbar').doLayout();
                    }
                } else {
                    addToCartView.gridPanel.getView().removeRowCls(selectionToCheck, "x-grid3-row-warningDate");
                }
            },
            callback : function (opts, success, response) {
                if (!success) {
                    var indRecToDisable = addToCartView.cartsSelectionsStore.find('dataUrl', opts.url);
                    var htmlRow = addToCartView.gridPanel.getView().getRow(indRecToDisable);
                    var elRow = Ext.get(htmlRow);
                    elRow.setVisible(false, true);
                    elRow.mask();
                }
                
                Ext.Array.remove(allSelectionsToCheck, selectionToCheck);
                this.checkDatasetExpirationDate(allSelectionsToCheck, isDatasetUpdated);
            },
            failure : function (response, opts) {
                if (response.status == 404) {
                    var orderBtn = addToCartView.down('toolbar').down('button[name=orderBtn]');
                    orderBtn.setDisabled(true);
                    return Ext.Msg.show({
                        title : i18n.get('label.warning'),
                        msg : i18n.get('label.orderCancelDatasetInactive'),
                        icon : Ext.MessageBox.ERROR,
                        buttons : Ext.MessageBox.OK
                    });
                }
            }
        });
    },
});
