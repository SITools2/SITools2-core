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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.view.component.datasets.dataviews.paging');

Ext.define('sitools.user.view.component.datasets.dataviews.paging.LivegridPagingToolbar', {
    extend: 'Ext.toolbar.Paging',
    
    alias : 'widget.livegridpagingtoolbar',
    
    config : {
        grid : null,
        renderer : null
    },

    selectedMessage : "(selected : {0})",

    initComponent : function () {
        var me = this;
        this.callParent(arguments);

        me.getGrid().getSelectionModel().on("selectionchange", me.updateSelectionInfo, me);
    },
    
    // @private
    updateInfo : function(){
        var me = this,
            displayItem = me.child('#displayItem'),
            store = me.store,
            pageData = me.getPageData(),
            count, msg;

        if (displayItem && me.getGrid().rendered) {
            
            if (Ext.isEmpty(me.getRenderer())) {
                me.setRenderer(me.getGrid().getPlugin('renderer'));
            }
            var renderer = this.getRenderer() ;
            var firstIndex = renderer.getFirstVisibleRowIndex()+1;
            var lastIndex = renderer.getLastVisibleRowIndex();

            count = store.getTotalCount();

            if (lastIndex > count) {
                lastIndex = count;
            }
            if (count === 0) {
                msg = me.emptyMsg;
            } else {
                msg = Ext.String.format(
                    me.displayMsg,
                    firstIndex,
                    lastIndex,
                    pageData.total
                );
            }
            displayItem.setText(msg);
        }
    },

    getPagingItems : function () {
        var me = this;
        return [{
            itemId: 'refresh',
            tooltip: me.refreshText,
            overflowText: me.refreshText,
            iconCls: Ext.baseCSSPrefix + 'tbar-loading',
            handler: me.doRefresh,
            scope: me
        }, {
            xtype: 'tbtext',
            itemId: 'selected',
            text: ""
        }];
    },

    updateSelectionInfo : function () {
        var me = this,
            selectedItem = me.child('#selected');

        if (selectedItem && me.getGrid().rendered) {

            var nbRowsSelected = me.getGrid().getNbRowsSelected();
            if(Ext.isEmpty(nbRowsSelected) || nbRowsSelected === 0) {
                selectedItem.setText("");
            } else {
                selectedItem.setText(Ext.String.format(
                    me.selectedMessage,
                    nbRowsSelected
                ));
            }
        }
    },

    getStoreListeners: function() {
        return {
            beforeload: this.beforeLoad,
            prefetch: this.onLoad,
            load: this.onLoad,
            exception: this.onLoadError
        };
    }
    
    
});