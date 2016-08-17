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
/*global Ext, i18n, sitools, window, loadUrl */

Ext.namespace('sitools.clientportal.view.portal');

Ext.define('sitools.clientportal.view.portal.FeedsReaderPortal', {
    extend : 'Ext.panel.Panel',
//    alias : 'widget.feedsReaderPortal',
    
    requires : ['sitools.public.feedsReader.FeedGridFlux'],
    
    portalId : 'idPortal',
    layout : 'fit',
    border : false,
    
    initComponent : function () {
        
        var storeFeeds = Ext.create('Ext.data.JsonStore', {
            autoLoad : true,
            fields : [ 'id', 'name', 'feedType', 'title', 'feedSource', {
                name : 'visible',
                type : 'boolean'
            }],
            proxy : {
                type : 'ajax',
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL') + '/' + this.portalId + '/listFeeds',
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
            listeners : {
                scope : this, 
                load : function (store, records, options) {
                    if (store.getCount() !== 0) {
						this.comboFeeds.setValue(store.getAt(0).data.id);
						this.selectFeeds(this.comboFeeds, store.getAt(0));
			        }
                }
            }
        });

        this.comboFeeds = Ext.create('Ext.form.field.ComboBox', {
            // all of your config options
            store : storeFeeds,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectAFeed'),
            selectOnFocus : true,
            scope : this,
            listeners : {
                scope : this,
                select : this.selectFeeds
            }
        });

        this.buttonDate = this.createSorterButton({
            text: i18n.get("label.feedDate"),
            sortData: {
                direction: 'ASC'
            }
        });
        
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ this.comboFeeds, this.buttonDate ]
        };

        this.callParent(arguments);
    },

    selectFeeds : function (combo, records, index) {
        var rec = records[0] || records;
        
        this.remove(this.feedsReader);
        var url = loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL') + "/" + this.portalId + "/clientFeeds/" + rec.data.name;

        this.feedsReader = Ext.create('sitools.public.feedsReader.FeedGridFlux', {
            urlFeed : url,
            feedType : rec.data.feedType,
            feedSource : rec.data.feedSource
        });
        
        this.add(this.feedsReader);
        this.doSort();
    },
    
    /**
     * Tells the store to sort itself according to our sort data
     */
    doSort : function () {
        if (Ext.isFunction(this.feedsReader.items.items[0].sortByDate)) {
            this.feedsReader.items.items[0].sortByDate(this.buttonDate.sortData.direction);
        }
    },
    
    /**
     * Convenience function for creating Toolbar Buttons that are tied to sorters
     * @param {Object} config Optional config object
     * @return {Ext.Button} The new Button object
     */
    createSorterButton : function (config) {
        config = config || {};
              
        Ext.applyIf(config, {
            listeners: {
                scope : this,
                click: function (button, e) {
                    this.changeSortDirection(button, true);                    
                }
            },
            iconCls: 'sort-' + config.sortData.direction.toLowerCase(),
            reorderable: true
        });
        
        return Ext.create('Ext.button.Button', config);
    },
    
    /**
     * Callback handler used when a sorter button is clicked or reordered
     * @param {Ext.Button} button The button that was clicked
     * @param {Boolean} changeDirection True to change direction (default). Set to false for reorder
     * operations as we wish to preserve ordering there
     */
    changeSortDirection : function (button, changeDirection) {
        var sortData = button.sortData,
            iconCls  = button.iconCls;

        if (sortData != undefined) {
            if (changeDirection == true) {
                var direction = (sortData.direction == "ASC") ? "DESC" : "ASC";
                var cls = (iconCls == "sort-asc") ? "sort-desc" : "sort-asc";

                //button.toggle("ASC", "DESC");
                button.sortData.direction = direction;
                button.setIconCls(cls);
            }
            this.doSort();
        }
    },
    
    /**
     * Returns an array of sortData from the sorter buttons
     * @return {Array} Ordered sort data from each of the sorter buttons
     */
    getSorters : function () {
        var sorters = [];
        
        Ext.each(this.getTopToolbar().findByType('button'), function (button) {
            if (!Ext.isEmpty(button.sortData)) {
                sorters.push(button.sortData);
            }
        }, this);
        
        return sorters;
    }

});
