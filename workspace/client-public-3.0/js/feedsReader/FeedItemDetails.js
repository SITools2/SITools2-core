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
/*global Ext, sitools, i18n,document*/
Ext.namespace('sitools.public.feedsReader');

/**
 * @param urlFeed :
 *            The feed URL
 */
Ext.define('sitools.public.feedsReader.FeedItemDetails', {
    extend : 'Ext.panel.Panel',

    initComponent : function () {
        this.layout = "fit";

        var record = this.record;
        
        if (!Ext.isEmpty(record)) {
            
            this.store = Ext.create('Ext.data.JsonStore', {
                fields: [
                    {name : 'title'},
                    {name : 'pubDate', type: 'date', dateFormat: 'timestamp'},
                    {name : 'published', type: 'date', dateFormat: 'timestamp'},
                    {name : 'author'}, 
                    {name : 'link'},
                    {name : 'description'},
                    {name : 'imageUrl'},
                    {name : 'image'}
                ],
                proxy : {
                    type : 'memory',
                    reader : {
                        type : 'json',
                        idProperty: 'title'
                    }
                },
                listeners : {
                    scope : this,
                    add : function (store, records, ind) {
                        if (record.data.imageUrl == undefined && record.data.image != undefined){
                            record.data.image = record.data.imageUrl;
                        }
                        if (records[0].data.pubDate != ""){
                            records[0].data.pubDate = this.formatDate(records[0].data.pubDate);
                        }
                    }
                }
            });
            
            this.store.add(record);
            
            this.tpl = new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="feed-article">',
                        '<tpl if="this.isDisplayable(imageUrl)">',
                            '<div class="feed-img">',
                                '<img src="{imageUrl}" title="{title}" width="70" height="70"/>',
                            '</div>',
                        '</tpl>',
                        '<p class="feed-title"> {title} </p>',
                        '<tpl if="this.isDisplayable(pubDate)">',
                            '<div class="feed-date-detail">',
                                '<b> Date : </b> {pubDate} ',
                            '</div>',
                        '</tpl>',
                        '<tpl if="this.isDisplayable(author)">',
                            '<div class="feed-author">',
                                '<b> Author : </b> {author} ',
                            '</div>',
                        '</tpl>',
                        '<div class="feed-description">',
                            '{description}',
                        '</div>',
                        '<div class="feed-complementary">',
                            '<p style="padding-bottom: 3px;"> <b> Link : </b> <a href="{link}" target="_blank" title="{title}">{link}</a> </p>',
                            '<tpl if="this.isDisplayable(imageUrl)">',
                                '<p> <b> Image Url : </b> <a href="{imageUrl}" target="_blank">{imageUrl}</a> </p>',
                            '</tpl>',
                        '</div>',
                    '</div>',
                '</tpl>',
                {
                    compiled : true,
                    isDisplayable : function (item) {
                        if (item != "" && item != undefined){
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                }
            );
            
            this.feedsDataview = Ext.create('Ext.view.View', {
              id: 'detailFeed-view',
              autoScroll : true,
              layout: 'fit',
              store : this.store,
              tpl : this.tpl,
              cls : 'detailFeed-view',
              emptyText: i18n.get('label.nothingToDisplay')
            });

            this.componentType = 'feedDetails';
            this.items = [ this.feedsDataview ];
        }

        this.callParent(arguments);
    },
    
    formatDate : function (date) {
        if (!date) {
            return '';
        }
        var now = new Date();
        var d = Ext.Date.clearTime(now, true);
        if (date instanceof Date) {
            var notime = Ext.Date.clearTime(date, true).getTime();
            if (notime == d.getTime()) {
                return 'Today ' + Ext.Date.format(date, 'g:i a');
            }
            d = Ext.Date.add(d, Ext.Date.DAY, -6);
//            d = d.add('d', -6);
            if (d.getTime() <= notime) {
                return Ext.Date.format(date, 'D g:i a');
            }
            return Ext.Date.format(date, 'n/j g:i a');
        }
        else {
            return date;
        }
    }, 
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }
    
    
    
});
