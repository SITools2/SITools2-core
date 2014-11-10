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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/datasets/datasets.js"
 * @include "../../components/datasets/projectForm.js"
 */

Ext.namespace('sitools.user.view.component.datasets.dataviews');

/**
 * Datasets Module : 
 * Displays All Datasets depending on datasets attached to the project.
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.view.component.datasets.dataviews.LivegridView', {
    extend : 'Ext.grid.Panel',
    
    requires : ['sitools.user.view.component.datasets.dataviews.CheckboxModel'],
    
    alias : 'widget.livegridView',
    layout : 'fit',
    autoScroll : true,
    bodyBorder : false,
    border : false,
    plugins: {
        pluginId : 'renderer',
        ptype : 'bufferedrenderer',
        trailingBufferZone: 20,  // Keep 20 rows rendered in the table behind scroll
        leadingBufferZone: 50,   // Keep 50 rows rendered in the table ahead of scroll,
        rowHeight : 25
    },
    viewConfig : {
        getRowClass : function (record, index) {
            return 'rowHeight'; 
        }
    },
    
    initComponent : function () {
        this.selModel = new Ext.create("sitools.user.view.component.datasets.dataviews.CheckboxModel", {
            checkOnly : true,
            pruneRemoved : false,
            gridView : this,
            showHeaderCheckbox : true,
            mode : 'MULTI'
        });
        
        this.bbar = {
            xtype : 'pagingtoolbar',
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty'),
            getPagingItems : function () {
                var me = this;
                return [{
                    itemId: 'refresh',
                    tooltip: me.refreshText,
                    overflowText: me.refreshText,
                    iconCls: Ext.baseCSSPrefix + 'tbar-loading',
                    handler: me.doRefresh,
                    scope: me
                }];
            }
        };
        
        this.tbar = Ext.create("sitools.user.view.component.datasets.services.ServiceToolbarView", {
            enableOverflow: true,
            datasetUrl : this.dataset.sitoolsAttachementForUsers,
            columnModel : this.dataset.columnModel
        });
        
        this.callParent(arguments);
    },
    
    //generic method
    getNbRowsSelected : function () {
        return this.getSelectionModel().getSelection().length;
    },
    
    //generic method
    isAllSelected : function () {
        var nbRowsSelected = this.getNbRowsSelected();
        return nbRowsSelected === this.getStore().getTotalCount() || this.getSelectionModel().markAll;
    },
    
    //generic method
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons : function () {
        var array = [];
        
        var colMenu = this.headerCt.getColumnMenu(this.headerCt);
        array.push({
            itemId: 'columnItem',
            text: this.headerCt.columnsText,
            cls: this.headerCt.menuColsIcon,
            hideOnClick: false,
            tooltip : i18n.get('label.addOrDeleteColumns'),
            menu : colMenu,
            name : "columnsButton"
        });
        
        
        array.push({
            name : "tipsLivegrid",
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/information.gif'
        });
        array.push("-");
        return array;
    },
    

    // generic method
    getSelections : function () {
        return this.getSelectionModel().getSelection();
    },
    

    getRequestColumnModel : function () {
        var params = {};

        var colModel = extColModelToSrv(this.columns);
        if (!Ext.isEmpty(colModel)) {
            params["colModel"] = Ext.JSON.encode(colModel);
        }
        return params;
    },
    

    getRequestParam : function () {
        var params = {};

        Ext.apply(params, this.getRequestColumnModel());
        Ext.apply(params, this.getSelectionParam());
        // If a simple selection is done, don't add the form params as the
        // selection is done on the ids
        if (Ext.isEmpty(params["p[0]"]) && Ext.isEmpty(params["c[0]"])) {
            Ext.apply(params, this.getRequestFormFilterParams());
            Ext.apply(params, this.this.getRequestGridFilterParams());
            Ext.apply(params, this.getSortParams());
        }

        return params;
    },
    
    /**
     * Return all request parameters without the column model and selection
     * @return {String} 
     */
    getRequestFormFilterParams : function () {
        //add the form params
        return this.store.getFormParams();
    },

    /**
     * Return all request parameters without the column model and selection
     * @return {String} 
     */
    getRequestGridFilterParams : function () {
        var params = {};
        // Add the filters params
        if (!Ext.isEmpty(this.getStore().servicefilters) && Ext.isFunction(this.getStore().servicefilters.getFilterData)) {
            var gridFiltersParam = this.store.servicefilters.getFilterData();
            if (!Ext.isEmpty(gridFiltersParam)) {
                params.filter = [];
                Ext.each(gridFiltersParam, function (filter, index) {
                    params.filter[index] = filter;
                });
            }
        }
        return params;
    },

    getSortParams : function () {
        // add the sorters
        var sortersCfg = this.store.sorters;

        var sorters = [];
        this.store.sorters.each(function (sorter) {
            sorters.push({
                field : sorter.property,
                direction : sorter.direction
            });
        }, this);

        return {
            sort : Ext.JSON.encode(sorters)
        };
    },
    
    /**
     * @method
     * will check if there is some pendingSelection (no requested records)
     * <li>First case, there is no pending Selection, it will build a form parameter
     * with a list of id foreach record.</li>
     * <li>Second case, there is some pending Selection : it will build a ranges parameter
     * with all the selected ranges.</li>
     * @returns {} Depending on liveGridSelectionModel, will return either an object that will use form API 
     * (p[0] = LISTBOXMULTIPLE|primaryKeyName|primaryKeyValue1|primaryKeyValue1|...|primaryKeyValueN), 
     * either an object that will contain an array of ranges of selection 
     * (ranges=[range1, range2, ..., rangen] where rangeN = [startIndex, endIndex])
     * 
     */
    getSelectionParam : function () {
        var sm = this.getSelectionModel(), param = {};

        if (this.isAllSelected()) {
            // First Case : all the dataset is selected.
            param.ranges = "[[0," + (this.store.getTotalCount() - 1) + "]]";
        } else /* if (Ext.isEmpty(sm.getPendingSelections())) */{
            // second Case : no pending Selections
            var recSelected = sm.getSelection();
            param = sitools.user.utils.DataviewUtils.getParamsFromRecsSelected(recSelected);
        }
        /*
         * else { //TODO //Third Case : there is a pending Selection, send all
         * the ranges. // var ranges = sm.getAllSelections(true); // result =
         * "ranges=" + Ext.JSON.encode(ranges); // //We have to re-build all the
         * request in case we use a range selection. // result +=
         * this.getRequestUrlWithoutSelection();
         *  }
         */
        return param;
    },
    
     /**
     * Return all request parameters
     * @return {String} 
     */
    getRequestUrl : function () {
        var params = this.getRequestParam();
        return Ext.Object.toQueryString(params, true);
    }, 
    
    /**
     * Return all request parameters without the column model
     * @return {String} 
     */
    getRequestUrlWithoutColumnModel : function () {
        var params = {};

        Ext.apply(params, this.getSelectionParam());
        // If a simple selection is done, don't add the form params as the
        // selection is done on the ids
        if (Ext.isEmpty(params["p[0]"]) && Ext.isEmpty(params["c[0]"])) {
            Ext.apply(params, this.getRequestFormFilterParams());
            Ext.apply(params, this.getRequestGridFilterParams());
            Ext.apply(params, this.getSortParams());
        }
        return Ext.Object.toQueryString(params, true);
    },
    
    /**
     * Return all request parameters without the column model and selection
     * @return {String} 
     */
    getRequestUrlWithoutSelection : function () {
        var params = {};

        Ext.apply(params, this.getRequestGridFilterParams());
        Ext.apply(params, this.getRequestFormFilterParams());
        Ext.apply(params, this.getSortParams());

        return Ext.Object.toQueryString(params, true);
    },
    
    getSelectionsRange : function () {
        //TODO finish the method implementation when the selection model is correct
        var sm = this.getSelectionModel();
        return "[]";
    },
    
// /**
//     * @method
//     * will check if there is some pendingSelection (no requested records)
//     * <li>First case, there is no pending Selection, it will build a form parameter
//     * with a list of id foreach record.</li>
//     * <li>Second case, there is some pending Selection : it will build a ranges parameter
//     * with all the selected ranges.</li>
//     * @returns {} Depending on liveGridSelectionModel, will return either an object that will use form API 
//     * (p[0] = LISTBOXMULTIPLE|primaryKeyName|primaryKeyValue1|primaryKeyValue1|...|primaryKeyValueN), 
//     * either an object that will contain an array of ranges of selection 
//     * (ranges=[range1, range2, ..., rangen] where rangeN = [startIndex, endIndex])
//     * 
//     */
//    getSelectionUrl : function () {
//        var sm = this.getSelectionModel(), result;
//        
//        if (this.isAllSelected()) {
//            //First Case : all the dataset is selected.
//            result = "ranges=[[0," + (this.store.getTotalCount() - 1) + "]]";
//            //We have to re-build all the request in case we use a range selection.
//            result += this.getRequestUrlWithoutSelection();
//        }
////        else if (Ext.isEmpty(sm.getPendingSelections())) {
//        else if (Ext.isEmpty(sm.getSelection())) {
//            //second Case : no pending Selections.
//            var recSelected = sm.getSelection();
//            result = Ext.urlEncode(this.dataviewUtils.getFormParamsFromRecsSelected(recSelected));
//        }
//        else {
//            //Second Case : there is a pending Selection, send all the ranges.
//            var ranges = sm.getAllSelections(true);
//            result = "ranges=" + Ext.JSON.encode(ranges);
//            //We have to re-build all the request in case we use a range selection.
//            result += this.getRequestUrlWithoutSelection();
//        }
//        return result;
//    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/components",
            preferencesFileName : this.id,
            componentClazz : this.componentClazz,
            datasetUrl : this.dataset.sitoolsAttachementForUsers
        };
    }
});
