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

Ext.namespace('sitools.user.view.component.datasets.dataviews.paging');

Ext.define('sitools.user.view.component.datasets.dataviews.paging.LivegridPagingToolbar', {
    extend: 'Ext.toolbar.Paging',
    
    alias : 'widget.livegridpagingtoolbar',
    
    config : {
        grid : null,
        renderer : null
    },
    
    // @private
    updateInfo : function(){
        var me = this,
            displayItem = me.child('#displayItem'),
            store = me.store,
            pageData = me.getPageData(),
            count, msg;
        

        if (Ext.isEmpty(me.getGrid())) {
            me.setGrid(me.up("grid"));
        }
        
        if (displayItem && me.getGrid().rendered) {
            
            if (Ext.isEmpty(me.getRenderer())) {
                me.setRenderer(me.getGrid().getPlugin('renderer'));
            }
            var renderer = this.getRenderer() ;
            var firstIndex = renderer.getFirstVisibleRowIndex()+1;
            var lastIndex = renderer.getLastVisibleRowIndex();
            
            count = store.getCount();
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
    getStoreListeners: function() {
        return {
            beforeload: this.beforeLoad,
            prefetch: this.onLoad,
            load: this.onLoad,
            exception: this.onLoadError
        };
    }
    
    
});