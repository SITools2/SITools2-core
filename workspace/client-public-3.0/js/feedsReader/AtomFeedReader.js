/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/* global Ext, sitools, i18n,document */
Ext.namespace('sitools.public.feedsReader');

/**
 * Displays a grid of atom1 format feeds
 * 
 * @class sitools.widget.atom1FeedReader
 * @extends Ext.grid.GridPanel
 * @cfg {string} datasetId The Dataset id,
 * @cfg {string} urlFeed The url to request feed
 * @cfg {string} datasetName The dataset name
 * @cfg {string} feedSource
 * @cfg {boolean} autoLoad store configuration
 */
Ext.define('sitools.public.feedsReader.AtomFeedReader', {
    extend : 'Ext.grid.GridPanel',
    layout : 'fit',
    autoExpandColumn : 'title',
    hideHeaders : true,
    forceFit : true,
    border : false,
    viewConfig : {
//        getRowClass : this.applyRowClass
    },

    initComponent : function () {

        this.store = Ext.create('Ext.data.Store', {
            autoLoad : true,
            sorters : [{
                property : 'pubDate',
                direction : "DESC"
            }],
            proxy : {
                type : 'ajax',
                url : this.feedGrid.urlFeed,
                reader : {
                    type : 'xml',
                    record : 'entry'
                },
                fields : [ 'title', {
                    name : 'author',
                    mapping : "author.name"
                }, {
                    name : 'pubDate',
                    mapping : 'updated',
                    type : 'date'
                }, {
                    name : 'link',
                    mapping : "link@href"
                }, {
                    name : 'description',
                    mapping : 'content'
                }, 'content', {
                    name : 'imageUrl',
                    createAccessor : function (data, field) {
                        var q = Ext.DomQuery;
                        // select node link with attribute type like image%
                        var node = q.selectNode("link[type^=image]", data);
                        var result = {};
                        if (Ext.isEmpty(node)) {
                            return result;
                        }
                        Ext.each(node.attributes, function (attribute) {
                            result[attribute.name] = attribute.value;
                        });
                        return result;
                    }
                }],
                listeners : {
                    scope : this,
                    exception : onRequestFeedException
                }
            },

        });

        var columns = [{
            id : 'image',
            header : "Image",
            dataIndex : 'imageUrl',
            sortable : false,
            width : 120,
            renderer : this.imageRenderer
        }, {
            id : 'title',
            header : "Title",
            dataIndex : 'title',
            sortable : true,
            width : 460,
            scope : this,
            renderer : this.formatTitle
        }, {
            header : "Author",
            dataIndex : 'author',
            width : 100,
            hidden : true,
            sortable : true
        }, {
            id : 'last',
            header : "Date",
            dataIndex : 'pubDate',
            width : 150,
            renderer : this.formatDate,
            sortable : true,
            hidden : true
        }];
        
        this.columns = columns;

        this.loadMask = {
            msg : i18n.get("label.loadingFeed")
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });
        
        this.callParent(arguments);
        
    },
    
    /**
     * Load the feeds with the given url
     * 
     * @param {string}
     *            url
     */
    loadFeed : function (url) {
        this.store.baseParams = {
            feed : url
        };
        this.store.load();
    },

    /**
     * switch from preview to complete view
     * 
     * @param {boolean}
     *            show
     */
    togglePreview : function (show) {
        this.view.showPreview = show;
        this.view.refresh();
    },

    /**
     * override the method getRowClass
     * 
     * @param {Record}
     *            record The {@link Ext.data.Record}
     *            corresponding to the current row.
     * @param {Number}
     *            index The row index.
     * @param {Object}
     *            rowParams A config object that is passed to
     *            the row template during rendering that allows
     *            customization of various aspects of a grid
     *            row.
     *            <p>
     *            If {@link #enableRowBody} is configured <b><tt></tt>true</b>,
     *            then the following properties may be set by
     *            this function, and will be used to render a
     *            full-width expansion row below each grid row:
     *            </p>
     *            <ul>
     *            <li><code>body</code> : String <div
     *            class="sub-desc">An HTML fragment to be used
     *            as the expansion row's body content (defaults
     *            to '').</div></li>
     *            <li><code>bodyStyle</code> : String <div
     *            class="sub-desc">A CSS style specification
     *            that will be applied to the expansion row's
     *            &lt;tr> element. (defaults to '').</div></li>
     *            </ul>
     *            The following property will be passed in, and
     *            may be appended to:
     *            <ul>
     *            <li><code>tstyle</code> : String <div
     *            class="sub-desc">A CSS style specification
     *            that willl be applied to the &lt;table>
     *            element which encapsulates both the standard
     *            grid row, and any expansion row.</div></li>
     *            </ul>
     * @param {Store}
     *            store The {@link Ext.data.Store} this grid is
     *            bound to
     */
    applyRowClass : function (record, rowIndex, p, ds) {
        if (this.showPreview) {
            var xf = Ext.util.Format;
            if (record.data.summary != "" && record.data.summary != undefined) {
                p.body = '<p class=sous-titre-flux>' + xf.ellipsis(xf.stripTags(record.data.summary), 300) + '</p>';
                return 'x-grid3-row-expanded';
            }
        }
        return 'x-grid3-row-collapsed';
    },

    /**
     * Custom date format
     * 
     * @param {Date}
     *            date the input date
     * @return {String} the date formated
     */
    formatDate : function (date) {
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
     * Custom renderer for title columns
     * 
     * @param {}
     *            value the value to format
     * @param {}
     *            p
     * @param {Ext.data.Record}
     *            record
     * @return {String} The title value formatted.
     */
    formatTitle : function (value, p, record) {
        var author = (record.data.author.name !== undefined) ? record.data.author.name : "";
        var link = record.data.link;
        var xf = Ext.util.Format;
        var dateFormat = this.formatDate(record.data.updated);
        var author = (record.data.author.name !== undefined) ? record.data.author.name : "";
        var authorEmail = (record.data.author.email !== undefined) ? record.data.author.email : "";
        var res = "";
        if (link !== undefined && link !== "") {
            res = Ext.String
                    .format(
                            '<div class="topic"><a href="{0}" title="{1}" target="_blank"><span class="rss_feed_title">{2}</span></a><br/><span class="author">{3}</span></div>',
                            link, value, xf.ellipsis(xf.stripTags(value), 50), author);
        } else {
            res = Ext.String.format('<div class="topic"><span class="rss_feed_title">{0}</span><br/><span class="author">{1}</span></div>', xf
                    .ellipsis(xf.stripTags(value), 50), author);
        }
        if (dateFormat != "" && dateFormat != undefined) {
            res += Ext.String.format('<p id="feeds-date">{0}</p>', dateFormat);
        }
        return res;

    },
    
    imageRenderer : function (value, p, record) {
        if (Ext.isEmpty(value) || Ext.isEmpty(value.href)) {
            return "";
        }
        if (value.type.substr(0, 5) != "image") {
            return "";
        }
        return Ext.String.format('<img src="{0}" width="50px">', value.href);
    },

    sortByDate : function (direction) {
        this.store.sort('pubDate', direction);
    }
});
