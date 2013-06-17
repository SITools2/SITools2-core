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
/*global Ext, sitools, i18n, window*/
Ext.namespace('sitools.component.users.datasets');

sitools.component.users.datasets.XmlReader = function (meta, recordType) {
    meta = meta || {};

    // backwards compat, convert idPath or id / success
    Ext.applyIf(meta, {
        idProperty : meta.idProperty || meta.idPath || meta.id,
        successProperty : meta.successProperty || meta.success
    });

    sitools.component.users.datasets.XmlReader.superclass.constructor.call(this, meta, recordType || meta.fields);
};
Ext.extend(sitools.component.users.datasets.XmlReader, Ext.data.XmlReader, {

    /**
     * Creates a function to return some particular key of data from a response.
     * 
     * @param {String}
     *            key
     * @return {Function}
     * @private
     * @ignore
     */
    createAccessor : function () {
        var q = sitools.component.users.datasets.DomQuery;
        return function (key) {
            if (Ext.isFunction(key)) {
                return key;
            }
            switch (key) {
            case this.meta.totalProperty:
                return function (root, def) {
                    return q.selectNumber(key, root, def);
                };
                break;
            case this.meta.successProperty:
                return function (root, def) {
                    var sv = q.selectValue(key, root, true);
                    var success = sv !== false && sv !== 'false';
                    return success;
                };
                break;
            default:
                return function (root, def) {
                    return q.selectValue(key, root, def);
                };
                break;
            }
        };
    }()

});
