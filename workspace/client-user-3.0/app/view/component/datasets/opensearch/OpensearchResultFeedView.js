/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Service used to build a window to define filter on a store using Filter API.
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.opensearch.OpensearchResultFeedView', {
    extend: 'Ext.grid.GridPanel',
    alias: 'widget.opensearchResultFeedView',
    componentType: "feeds",

    /**
     * @param
     * dataUrl the url of the root container
     */
    initComponent: function () {
        var urlParam = this.urlFeed;
        var pagging = this.pagging;

        var exceptionHttpHandler = (Ext.isEmpty(this.exceptionHttpHandler))
            ? onRequestFeedException
            : this.exceptionHttpHandler;

        this.store = Ext.create("Ext.data.Store", {
            model: 'sitools.public.feedsReader.FeedItemModel',
            pageSize: 10,
            proxy: {
                type: 'ajax',
                url: urlParam,
                limitParam: 'rows',
                startParam: 'start',
                reader: {
                    type: "xml",
                    record: 'item',
                    root: 'channel',
                    totalProperty: 'totalResults'
                },
                listeners: {
                    scope: this,
                    exception: exceptionHttpHandler
                }
            },
            sorters: {
                property: 'pubDate',
                direction: 'DESC'
            },
            //autoLoad : this.autoLoad,
            autoLoad: true,
            listeners: {
                scope: this,
                load: function (self, records, index) {
                    if (!pagging && !Ext.isEmpty(this.displayNbResults)) {
                        this.displayNbResults
                            .setText('Total number of results : '
                            + this.store.getTotalCount());
                        // this.getBottomToolbar().doLayout();
                    }
                    return true;
                },
                exception: function (proxy, type, action, options,
                                     response, arg) {
                    var data = Ext.decode(response.responseText);
                    if (!data.success) {
                        this.input.markInvalid(i18n.get(data.message));
                        this.store.removeAll();
                    }
                    return true;
                }
            }
        });

        var columns = [{
            header: "Title",
            dataIndex: 'title',
            sortable: true,
            renderer: this.formatTitle
        }, {
            header: "Date",
            dataIndex: 'pubDate',
            sortable: true,
            format: SITOOLS_DEFAULT_IHM_DATE_FORMAT,
            xtype: 'datecolumn'
        }];

        if (pagging) {
            this.bbar = {
                xtype: 'pagingtoolbar',
                pageSize: this.pageSize,
                store: this.store,
                displayInfo: true,
                displayMsg: i18n.get('paging.display'),
                emptyMsg: i18n.get('paging.empty'),
                totalProperty: 'totalCount'
            };
        } else {
            this.displayNbResults = Ext.create("Ext.form.Label", {
                text: 'Total number of results : '
            });
            this.bbar = {
                items: ['->', this.displayNbResults]
            };
        }

        Ext.apply(this, {
            columns: columns,
            layout: 'fit',
            flex: 1,
            store: this.store,
            sm: Ext.create('Ext.selection.RowModel', {
                mode: 'SINGLE'
            }),
            forceFit: true,
            viewConfig: {
                showPreview: true
//                getRowClass : this.applyRowClass
            }
        });

        this.callParent(arguments);
    },

    updateStore: function (url) {
        this.store.getProxy().url = url;
        this.store.load({
            scope: this,
            callback: function () {
                this.getView().refresh();
            }
        });
    },

    // within this function "this" is actually the GridView
    applyRowClass: function (record, rowIndex, p, ds) {
        if (this.showPreview) {
            var xf = Ext.util.Format;
            p.body = '<p class=sous-titre-flux>'
            + xf.ellipsis(xf.stripTags(record.data.description), 200)
            + '</p>';
            return 'x-grid3-row-expanded';
        }
        return 'x-grid3-row-collapsed';
    },

    formatDate: function (date) {
        if (!date) {
            return '';
        }
        var now = new Date();
        var d = now.clearTime(true);
        if (date instanceof Date) {
            var notime = date.clearTime(true).getTime();
            if (notime == d.getTime()) {
                return 'Today ' + date.dateFormat('g:i a');
            }
            d = d.add('d', -6);
            if (d.getTime() <= notime) {
                return date.dateFormat('D g:i a');
            }
            return date.dateFormat('n/j g:i a');
        } else {
            return date;
        }
    },

    /**
     * Specific renderer for title Column
     *
     * @param {}
     *            value
     * @param {}
     *            p
     * @param {Ext.data.Record}
     *            record
     * @return {string}
     */
    formatTitle: function (value, p, record) {
        var link = record.data.link;
        var xf = Ext.util.Format;
        var res = "";
        if (link !== undefined && link !== "") {
            res = Ext.String.format(
                '<div class="topic"><a href="{0}" title="{1}"><span class="rss_feed_title">{2}</span></a></div>',
                link, value, xf.ellipsis(xf.stripTags(value), 30));
        } else {
            res = Ext.String.format(
                '<div class="topic"><span class="rss_feed_title">{0}</span></div>',
                xf.ellipsis(xf.stripTags(value), 30));
        }

        return res;
    }
});
