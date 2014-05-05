/*******************************************************************************
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
 ******************************************************************************/
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/

/**
 * A RowExpander used for plugin grids to add violation informations 
 * The parameters are the same as the class Ext.ux.grid.RowExpander
 * 
 * @class sitools.admin.resourcesPlugins.violationRowExpander
 * @extends Ext.ux.grid.RowExpander
 */
Ext.define('sitools.public.widget.item.ViolationRowExpander', {
    alternateClassName : ['sitools.widget.ViolationRowExpander'],
    extend : 'Ext.ux.grid.RowExpander',
    alias : 'sitools.widget.ViolationRowExpander',
    getRowClass : function (record, index, rowParams, store) {
        //call the method from the superclass
        var cls = sitools.widget.ViolationRowExpander.superclass.getRowClass.call(this, record, index, rowParams, store);
        //add a class depending on the violation type
        var violation = record.get("violation");
        if (!Ext.isEmpty(violation)) {
            if (violation.level == "CRITICAL") {
                cls += " red-row";
            } else if (violation.level == "WARNING") {
                cls += " orange-row";
            }
        }
        return cls;
    }
});