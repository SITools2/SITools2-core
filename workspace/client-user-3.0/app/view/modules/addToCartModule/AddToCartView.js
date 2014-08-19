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

Ext.namespace('sitools.user.modules.');
/**
 * AddToCart Module
 * @class AddToCartModuleView
 * @extends Ext.Panel
 */
Ext.define('sitools.user.modules.AddToCartModuleView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.addToCartModule',
    
    bodyBorder : false,
    layout : 'fit',
    
    initComponent : function () {
        (Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', this.user) + "/files";
        this.urlCartFileForServer = this.AppUserStorage + getCartFolder(Project.projectName) + "/" + this.user + "_CartSelections.json";
        this.urlCartFile = loadUrl.get('APP_URL') + this.urlCartFileForServer;
        
        Ext.each(this.listProjectModulesConfig, function (config) {
            if (!Ext.isEmpty(config.value)) {
                switch (config.name) {
                case "orderServices" :
                    this.orderServices = Ext.util.JSON.decode(config.value);
                    break;
                }
            }
        }, this);
        
        this.tbarMenu = Ext.create('Ext.menu.Menu');
        this.tbarMenu.add('<b class="menu-title"><i>' + i18n.get('label.broadcastMode') + '</i></b>', '-');
        
        Ext.each(this.orderServices, function (service) {
            this.tbarMenu.add({
                text : service.label,
                serviceName : service.name,
                serviceId : service.id,
                serviceUrl : service.url,
                scope : this,
                handler : function (btn) {
                    this.setCurrentServiceName(btn);
                    this.downloadSelection();
                    
                }
            });
        }, this);
        
        this.tbar = {
            xtype : 'toolbar',
            cls : 'services-toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [{
                xtype : 'button',
                text : '<b>' + i18n.get('label.downloadOrder') + '</b>',
                name : 'orderBtn',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/download.png',
                tooltip : i18n.get('label.downloadOrder'),
                cls : 'sitools-btn-green',
                menu : this.tbarMenu
            }, '->', 
            {
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
                tooltip : i18n.get('label.refreshOrder'),
                cls : 'button-transition',
                scope : this,
                handler : this.onRefresh
            }, {
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                tooltip : i18n.get('label.deleteOrder'),
                cls : 'button-transition',
                scope : this,
                handler : function () {
                    var selected = this.gridPanel.getSelectionModel().getSelection();
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
                        buttons : {
                            yes : i18n.get('label.yes'),
                            no : i18n.get('label.no')
                        },
                        icon : Ext.MessageBox.WARNING,
                        msg : i18n.get('label.deleteCartOrder'),
                        scope : this,
                        fn : function (btn, text) {
                            if (btn == 'yes') {
                                this.onDelete();
                            }
                        }
                    });
                }
            }]
        };
        
        this.cartsSelectionsStore = Ext.create('sitools.user.store.CartsSelectionsStore', {
        	url : this.urlCartFile
        });
        
        var columns =  [{
            header : i18n.get('label.datasetName'),
            width : 150,
            sortable : true,
            dataIndex : 'datasetName'
        }, {
            header : i18n.get('label.orderAddToCart'),
            width : 150,
            sortable : true,
            dataIndex : 'orderDate',
            format : SITOOLS_DEFAULT_IHM_DATE_FORMAT,
            xtype : 'datecolumn'
        }, {
            header : i18n.get('label.nbArticles'),
            width : 150,
            sortable : true,
            dataIndex : 'nbRecords'
        }];
        
        this.gridPanel = Ext.create('Ext.grid.Panel', {
        	name : 'cartGridPanel',
            region : 'center',
            sm : Ext.create('Ext.selection.RowModel'),
            columns : columns,
            store : this.cartsSelectionsStore,
            viewConfig : {
                preserveScrollOnRefresh : true,
                listeners : {
                    scope : this,
                    refresh : function (view) {
                        this.callbackOrderFile();
                    }
                }
            }
        });
        
        this.containerArticlesDetailsPanel = Ext.create('Ext.panel.Panel', {
            region : 'south',
//            frame : true,
            bodyBorder : false,
            collapsible : true,
            collapsed : true,
            forceLayout : true,
            layout : 'fit',
            height : 300,
            split : true
        });
        
        var cartModuleItems = [this.gridPanel, this.containerArticlesDetailsPanel];
        
        var description = i18n.get('label.descriptionAddToCartModule');
        
        if (description !== "label.descriptionAddToCartModule") {
            var descriptionPanel = Ext.create('Ext.panel.Panel', {
                height : 120,
                html : description, 
                padding : "10px", 
                region : "north", 
                collapsible : true, 
                autoScroll : true, 
                title : i18n.get('label.information')
            });
            cartModuleItems.push(descriptionPanel);
        }
        
        
        this.hboxPanel = Ext.create('Ext.panel.Panel', {
            id : 'cartModuleHBox',
            layout : 'border',
            items : cartModuleItems
        });
        
        this.items = [ this.hboxPanel ];
        
		this.callParent(arguments);
    }, 
    
    loadOrderFile : function () {
        UserStorage.get(this.user + "_CartSelections.json", getCartFolder(Project.projectName), this, this.setCartOrdersFile);
    },
    
    /**
     * Set the file order for the current user from responseText
     * @param response the responseText to use as cartOrder
     */
    setCartOrdersFile : function (response) {
        if (Ext.isEmpty(response.responseText)) {
            return;
        }
        try {
            var json = Ext.decode(response.responseText);
            this.cartOrderFile = json;
            this.cartsSelectionsStore.loadData(json);
        } catch (err) {
            return;
        }
    },
    
    callbackOrderFile : function () {
        var orderBtn = this.down('toolbar').down('button[name=orderBtn]');
        orderBtn.setDisabled(false);
        var selectionsToCheck = new Ext.util.MixedCollection();
        var arraySelections = [];
        this.cartsSelectionsStore.each(function (record) {
            arraySelections.push(record); 
        });
        selectionsToCheck.addAll(arraySelections);
        
        var isDatasetUpdated = this.checkDatasetExpirationDate(selectionsToCheck, false);
        
        if (!isDatasetUpdated) {
            var warningLabel = this.down('toolbar').down('button[name=warningDateLabel]');
            if (!Ext.isEmpty(warningLabel)) {
                this.down('toolbar').remove(warningLabel);
//                this.down('toolbar').doLayout();
            }
        }
    },
    
    checkDatasetExpirationDate : function (selectionsToCheck, isDatasetUpdated) {
        if (selectionsToCheck.getAt(0)) {
            this.compareDate(selectionsToCheck, selectionsToCheck.itemAt(0), isDatasetUpdated);
        }
        return isDatasetUpdated;
    },
    
    compareDate : function (allSelectionsToCheck, selectionToCheck, isDatasetUpdated) {
        if (Ext.isEmpty(selectionToCheck)) {
            return;
        }
        
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
                
                var rowInd = this.cartsSelectionsStore.indexOf(this.cartsSelectionsStore.getById(dataSelection.selectionId));
                var htmlRow = this.gridPanel.getView().getRow(rowInd);
                
                var dateDataset = Date.parseDate(json.dataset.expirationDate, SITOOLS_DATE_FORMAT);
                if (dateDataset > dataSelection.orderDate) {
                    isDatasetUpdated = true;
                    htmlRow.className += " x-grid3-row-warningDate";
                    var labelWarning = Ext.create('Ext.form.Label', {
                        name : 'warningDateLabel',
                        html : "<img src='/sitools/common/res/images/ux/warning.gif'/> " + i18n.get('warning.datasetUptated') + ""
                    });
                    if (!this.down('toolbar').down('button[name=warningDateLabel]')) {
                        this.down('toolbar').insert(1, labelWarning);
//                        this.down('toolbar').doLayout();
                    }
                } else {
                    htmlRow.className = htmlRow.className.replace('x-grid3-row-warningDate', '');
                }
            },
            callback : function (opts, success, response) {
                if (!success) {
                    var indRecToDisable = this.cartsSelectionsStore.find('dataUrl', opts.url);
                    var htmlRow = this.gridPanel.getView().getRow(indRecToDisable);
                    var elRow = Ext.get(htmlRow);
                    elRow.setVisible(false, true);
                    elRow.mask();
                }
                
                allSelectionsToCheck.remove(selectionToCheck);
                this.checkDatasetExpirationDate(allSelectionsToCheck, isDatasetUpdated);
            },
            failure : function (response, opts) {
                if (response.status == 404) {
                    var orderBtn = this.down('toolbar').down('button[name=orderBtn]');
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
    
    /**
     * Download orders (metadata + records)
     * @returns
     */
    downloadSelection : function () {
        
        var selections = [];
        
        this.gridPanel.getStore().each(function (rec) {
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
            url : projectGlobal.sitoolsAttachementForUsers + "/services",
            method : 'GET',
            scope : this,
            success : function (response) {
                var json = Ext.decode(response.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.error'), json.message);
                    return;
                }
                var services = json.data;
                var index = Ext.each(services, function (service) {
                    if (service.name === this.currentServiceName) {
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
                            url = projectGlobal.sitoolsAttachementForUsers + param.value;
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
                    
                    this.serviceServerUtil = new sitools.user.component.dataviews.services.serverServicesUtil({
                        datasetUrl : projectGlobal.sitoolsAttachementForUsers,
                        datasetId : projectGlobal.projectId, 
                        origin : "sitools.user.modules.projectServices"
                    });
                    
                    var cb = function (success) {
                        if (!Ext.isEmpty(success) && !success) {
                            this.down('toolbar').enable();
                        } else {
                            //close the module
                            SitoolsDesk.navProfile.taskbar.closeWin(null, null, this.ownerCt);
                            
                            var notify = new Ext.ux.Notification({
                                iconCls : 'x-icon-information',
                                title : i18n.get('label.info'),
                                html : i18n.get('label.orderWasRun'),
                                autoDestroy : true,
                                hideDelay : 1000
                            });
                            notify.show(document);
                            
                        }
                    };
                    var callback = cb.createDelegate(this);
                    
                    this.down('toolbar').disable();
                    this.serviceServerUtil.resourceClick(resource, url, method, runTypeUserInput, parameters, null, callback);
                }
            }
        });
        
    },
    
    viewArticlesDetails : function () {
        var selected = this.gridPanel.getSelectionModel().getSelections()[0];
        
        this.containerArticlesDetailsPanel.removeAll();
        var detailsSelectionPanel = Ext.create('sitools.user.modules.cartSelectionDetails', {
            selection : selected,
            columnModel : selected.data.colModel,
            url : selected.data.dataUrl + "/records",
            selections : selected.data.selections, 
            cartOrderFile : this.cartOrderFile,
            cartModule : this
        });
        
        this.containerArticlesDetailsPanel.add(detailsSelectionPanel);
        this.containerArticlesDetailsPanel.setTitle(Ext.String.format(i18n.get('label.orderDetails'), selected.data.selectionName));
//        this.containerArticlesDetailsPanel.doLayout();
    },
    
    onRefresh : function () {
        this.cartsSelectionsStore.reload();
        this.gridPanel.getSelectionModel().clearSelections();
        this.containerArticlesDetailsPanel.collapse(true);
        this.containerArticlesDetailsPanel.setTitle('');
        this.containerArticlesDetailsPanel.removeAll();
        
        var modifySelBtn = this.down('toolbar').down('button[name=modifySelectionBtn]');
        if (!Ext.isEmpty(modifySelBtn)) {
            this.down('toolbar').remove(modifySelBtn);
//            this.down('toolbar').doLayout();
        }
    },
    
    onDelete : function () {
        UserStorage.get(this.user + "_CartSelections.json", getCartFolder(projectGlobal.projectName), this, this.setCartOrdersFile, Ext.emptyFn, this.deleteOrder);
    },
    
    /**
     * Delete articles file(s) for the selected order(s)
     * @param listRecordsFilesToRemove the list all order selectionID to remove
     */
    deleteArticlesOrder : function (listRecordsFilesToRemove) {
        var fileToRemove = listRecordsFilesToRemove[0];
        
        if (Ext.isEmpty(fileToRemove)){
            return;
        }
        
        // gettings url file to remove
        var urlRecords = loadUrl.get('APP_URL') + this.AppUserStorage + "/" + 
            DEFAULT_ORDER_FOLDER + "/records/" + this.user + "_" + fileToRemove + "_" + "records.json";
        
        Ext.Ajax.request({
            url : urlRecords,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                listRecordsFilesToRemove.shift();
                
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.fileDeleted'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
            },
            callback : function () {
                this.deleteArticlesOrder(listRecordsFilesToRemove);
            },
            failure : function (response, opts) {
                Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
            }
        });
    },
    
    /**
     * Delete selected orders
     */
    deleteOrder : function () {
        if (!this.cartOrderFile) {
            return;
        }
        var selections = this.gridPanel.getSelectionModel().getSelections();
        
        if (selections.length == 0) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        
        var collSelections = new Ext.util.MixedCollection();
        collSelections.addAll(selections);
        
        var collCartSelections = new Ext.util.MixedCollection();
        collCartSelections.addAll(this.cartOrderFile.selections);
        
        collCartSelections.each(function (cartSelection, indCart) {
            collSelections.each(function (delSelection, indDel) {
                if (cartSelection.selectionId == delSelection.data.selectionId) {
                    collCartSelections.remove(cartSelection);
                }
            });
        });
        this.cartOrderFile.selections = collCartSelections.items;
        
        UserStorage.set(this.user + "_CartSelections.json", getCartFolder(projectGlobal.projectName), this.cartOrderFile, this.onRefresh, this);
    },
    
    /**
     * Set the download mode
     * @param btn the broadcast mode chosen
     */
    setCurrentServiceName : function (btn) {
        this.currentServiceName = btn.serviceName;
    },
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    },
    
    modifySelection : function () {
        var selected = this.gridPanel.getSelectionModel().getSelections()[0];
        var url = selected.data.dataUrl;
        var params = {
            ranges : selected.get('ranges'),
            startIndex : selected.get('startIndex'),
            nbRecordsSelection : selected.get('nbRecords'),
            filters : selected.get('filters'),
            storeSort : selected.get('storeSort'),
            formParams : selected.get('formParams'),
            filtersCfg : selected.get('filtersCfg'),
            isModifySelection : true
        };
        sitools.user.clickDatasetIcone(url, 'data', params);
    }
});
