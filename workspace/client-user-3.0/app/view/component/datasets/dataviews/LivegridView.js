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
    
    alias : 'widget.livegridView',
    layout : 'fit',
    autoScroll : true,
    bodyBorder : false,
    border : false,
    plugins: {
        ptype : 'bufferedrenderer',
        trailingBufferZone: 20,  // Keep 20 rows rendered in the table behind scroll
        leadingBufferZone: 50   // Keep 50 rows rendered in the table ahead of scroll
    },
    
    
    initComponent : function () {
        
        this.selModel = new Ext.create("Ext.selection.CheckboxModel", {
            checkOnly : true,
            pruneRemoved : false,
            gridView : this
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
            datasetUrl : this.dataset.sitoolsAttachementForUsers
        });
        
        this.callParent(arguments);
    },
    
    getNbRowsSelected : function () {
        return this.getSelectionModel().getSelection().length;
    },
    
    isAllSelected : function () {
        var nbRowsSelected = this.getNbRowsSelected();
        return nbRowsSelected === this.getStore().getTotalCount() || this.getSelectionModel().markAll;
    },
    
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
        array.push(new Ext.toolbar.Separator());
        return array;
    },
    
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
