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
/*global Ext, sitools, window */

Ext.define('sitools.user.store.DatasetDataviewStore', {
    extend: 'Ext.data.Store',
    model: 'sitools.user.model.DatasetExplorerDataviewModel',
    proxy: {
        type: 'ajax',
        reader: {
            type: 'json',
            root: "data"
        }
    },
    listeners: {
        scope: this,
        load: function (store, records, options) {
            Ext.each(records, function (record) {
                if (record.get("status") === "ACTIVE") {
                    var properties = record.get("properties");
                    var img = null;
                    var nbRecords;
                    Ext.each(properties, function (property) {
                        if (property.name === "imageUrl") {
                            img = property.value;
                        }
                        if (property.name === "nbRecord") {
                            record.set("nbRecords", parseInt(property.value));
                        }
                    });
                    if (!Ext.isEmpty(img)) {
                        record.set("image", img);
                    } else {
                        record.set("image", SITOOLS_DEFAULT_PROJECT_IMAGE_URL);
                    }
                } else {
                    store.remove(record);
                }
            });
            store.clearFilter();
            store.sort();
        }
    },

    setCustomUrl : function (url) {
        this.getProxy().url = url;
    }

    //doSort: function (sorters, direction) {
    //    this.sort(sorters, direction);
    //}
});