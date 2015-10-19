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
/*global Ext, sitools, window, SITOOLS_DATE_FORMAT */

Ext.define('sitools.public.feedsReader.model.AtomItemModel', {
    extend: 'Ext.data.Model',

    fields: ['title',
        {
            name: 'author',
            mapping: "author.name"
        }, {
            name: 'pubDate',
            mapping: 'updated',
            type: 'date'
        },
        {
            name: 'link',
            mapping: "link@href"
        }, {
            name: 'description',
            mapping: 'content'
        }, 'content', {
            name: 'imageUrl',
            mapping: 'link[rel=enclosure]@href'
        }, {
            name: 'imageType',
            mapping: 'link[rel=enclosure]@type'
        }]
});
