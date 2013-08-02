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

Ext.ux.PersistantSelectionGridPanel = Ext.extend(Ext.grid.GridPanel, {
    selectedRecords: [],
    initComponent : function () {
        
        this.getStore().on('beforeload', this.rememberSelection, this);
        this.getView().on('refresh', this.refreshSelection, this);
        
        Ext.ux.PersistantSelectionGridPanel.superclass.initComponent.call(this);
    }, 
    
    rememberSelection: function(selModel, selectedRecords) {
        if (!this.rendered || Ext.isEmpty(this.el)) {
            return;
        }

        this.selectedRecords = this.getSelectionModel().getSelections();
    },
    
    refreshSelection: function() {
        if (0 >= this.selectedRecords.length) {
            return;
        }

        var newRecordsToSelect = [];
        for (var i = 0; i < this.selectedRecords.length; i++) {
            record = this.getStore().getById(this.selectedRecords[i].id);
            if (!Ext.isEmpty(record)) {
                newRecordsToSelect.push(record);
            }
        }

        this.getSelectionModel().selectRecords(newRecordsToSelect);
//        Ext.defer(this.setScrollTop, 30, this, [this.getView().scrollState.top]);
    }
});

Ext.reg('Ext.ux.PersistantSelectionGridPanel',Ext.ux.PersistantSelectionGridPanel);
