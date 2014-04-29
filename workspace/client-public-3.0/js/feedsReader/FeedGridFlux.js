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
/*global Ext, sitools, i18n,document, window, SitoolsDesk*/
Ext.namespace('sitools.public.feedsReader');

/**
 * @cfg {string} urlFeed The feed URL
 * @cfg {string} feedType the type of the feed ("atom_1.0" or "rss_2")
 * @cfg {string} feedSource the source of the feed (OPENSEARCH or CLASSIC)
 * @requires sitools.user.component.openSearchResultFeed
 */
Ext.define('sitools.public.feedsReader.FeedGridFlux', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.appfeedgridflux',
    componentType : "feeds",
    border : false,
    
    requires : ['sitools.public.feedsReader.AtomFeedReader',
                'sitools.public.feedsReader.RssFeedReader',
                'sitools.public.feedsReader.FeedItemDetails'
                ],
    
    initComponent : function () {

        this.layout = "fit";
        
        var gridPanel;
        if (this.feedSource !== undefined && this.feedSource === "OPENSEARCH") {
            gridPanel = new sitools.user.component.openSearchResultFeed(this);
        } else {
            this.listeners = {
//                rowdblclick : this.clickOnRow,
            };
            if (this.feedType !== undefined && this.feedType === "atom_1.0") {
                gridPanel = Ext.create('sitools.public.feedsReader.AtomFeedReader', {
                    feedGrid : this
                });
            } else {
                gridPanel = Ext.create('sitools.public.feedsReader.RssFeedReader', {
                    feedGrid : this
                });
            }
        }

        this.btnSubscribeRss = Ext.create('Ext.button.Button', {
            text : i18n.get('label.subscribeRss'),
            cls : 'services-toolbar-btn',
            icon : loadUrl.get('APP_URL') + '/client-public/common/res/images/icons/rss.png',
            handler : this.subscribeRss
         });
         
         this.bbar = {
             xtype : 'toolbar',
             cls : "services-toolbar", 
             defaults : {
                 scope : this
             },
             items : [ this.btnSubscribeRss ]
         };
        
        this.items = [ gridPanel ];

        this.callParent(arguments);
    },
    
    clickOnRow : function (self, rowIndex, e) {
        e.stopEvent();
        var rec = self.store.getAt(rowIndex);
        if (Ext.isEmpty(rec)) {
            return;
        }
        // si on est pas sur le bureau
        if (Ext.isEmpty(window) || Ext.isEmpty(window.SitoolsDesk)) {
            
            var component = Ext.create('sitools.public.feedsReader.FeedItemDetails', {
                record : rec
            });
            
            var win = Ext.create('Ext.window.Window', {
                stateful : false,
                title : i18n.get('label.viewFeedDetail'),
                width : 400,
                height : 600,
                shim : false,
                animCollapse : false,
                constrainHeader : true,
                layout : 'fit',
                modal : true
            });
            win.add(component);
            win.show();
        } else {
            var componentCfg = {
                record : rec
            };
            var jsObj = sitools.public.feedsReader.FeedItemDetails;

            var windowConfig = {
                id : "viewFeedDetail",
                title : i18n.get('label.viewFeedDetail'),
                saveToolbar : false
            };
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
        }
    },
    
    _getSettings : function () {
        return {
            objectName : "feedsReader"
        };
    },
    
    subscribeRss : function () {
        window.open(this.urlFeed, '_blank');
    }
});

