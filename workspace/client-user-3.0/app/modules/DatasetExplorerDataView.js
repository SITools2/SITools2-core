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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.modules');
/**
 * datasetExplorerDataview Module
 *
 * @class sitools.user.modules.DatasetExplorerDataview
 * @extends sitools.user.core.Module
 */
Ext.define('sitools.user.modules.DatasetExplorerDataview', {
    extend: 'sitools.user.core.Module',

    requires: ['sitools.user.controller.modules.datasetExplorerDataview.DatasetExplorerDataviewController'],
    controllers: ['sitools.user.controller.modules.datasetExplorerDataview.DatasetExplorerDataviewController'],

    init: function () {
        var view = Ext.create('sitools.user.view.modules.datasetExplorerDataview.DatasetExplorerDataViewView');
        this.setViewCmp(view);

        this.show(this.getViewCmp());
        this.callParent(arguments);
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id
        };

    }
});
