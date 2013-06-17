/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.widget');

/**
 * @cfg {string} urlFeed The feed URL
 * @cfg {string} feedType the type of the feed ("atom_1.0" or "rss_2")
 * @cfg {string} feedSource the source of the feed (OPENSEARCH or CLASSIC)
 * @requires sitools.user.component.openSearchResultFeed
 */
sitools.widget.FeedGridFlux = function (config) {
    
    this.datasetName = config.datasetName;
    function clickOnRow(self, rowIndex, e) {
        e.stopEvent();
        var rec = self.store.getAt(rowIndex);
        if (Ext.isEmpty(rec)) {
            return;
        }
        // si on est pas sur le bureau
        if (Ext.isEmpty(window) || Ext.isEmpty(window.SitoolsDesk)) {
            var component = new sitools.widget.feedItemDetails({
                record : rec
            });
            var win = new Ext.Window({
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
            var jsObj = sitools.widget.feedItemDetails;

            var windowConfig = {
                id : "viewFeedDetail",
                title : i18n.get('label.viewFeedDetail'),
                saveToolbar : false
            };
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);

        }
    }

    Ext.apply(this);
    this.layout = "fit";
    var gridPanel;
    if (config.feedSource !== undefined && config.feedSource === "OPENSEARCH") {
        gridPanel = new sitools.user.component.openSearchResultFeed(config);
    } else {
        config.listeners = {
            rowdblclick : clickOnRow
        };
        if (config.feedType !== undefined && config.feedType === "atom_1.0") {
            gridPanel = new sitools.widget.atom1FeedReader(config);
        } else {
            gridPanel = new sitools.widget.rss2FeedReader(config);
        }
    }

    this.items = [ gridPanel ];

    sitools.widget.FeedGridFlux.superclass.constructor.call(this);
};

Ext.extend(sitools.widget.FeedGridFlux, Ext.Panel, {
    componentType : "feeds",
    _getSettings : function () {
        return {
        	objectName : "feedsReader"
        };
    },
    border : false

});

Ext.reg('appfeedgridflux', sitools.widget.FeedGridFlux);
