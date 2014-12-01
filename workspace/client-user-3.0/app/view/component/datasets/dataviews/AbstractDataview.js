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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/datasets/datasets.js"
 * @include "../../components/datasets/projectForm.js"
 */

/**
 * Datasets Module : 
 * Displays All Datasets depending on datasets attached to the project.
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.view.component.datasets.dataviews.AbstractDataview', {

    //generic method
    getNbRowsSelected : Ext.EmptyFn,

    //generic method
    isAllSelected : Ext.EmptyFn,

    //generic method
    /**
     * Return an array containing a button to show or hide columns
     * @returns {Array}
     */
    getCustomToolbarButtons : Ext.EmptyFn,


    // generic method
    getSelections : Ext.EmptyFn,


    getRequestColumnModel : Ext.EmptyFn,


    getRequestParam : Ext.EmptyFn,

    /**
     * Return all request parameters without the column model and selection
     * @return {String}
     */
    getRequestFormFilters : Ext.EmptyFn,

    /**
     * Return all form concept request parameters without the column model and selection
     * @return {String}
     */
    getRequestFormConceptFilters : Ext.EmptyFn,

    /**
     * Return all request parameters without the column model and selection
     * @return {String}
     */
    getRequestFormFilterParams : Ext.EmptyFn,

    /**
     * Return all form concept request parameters without the column model and selection
     * @return {String}
     */
    getRequestFormConceptFilterParams : Ext.EmptyFn,

    /**
     * Return all request parameters without the column model and selection
     * @return {String}
     */
    getRequestGridFilterParams : Ext.EmptyFn,

    getSortParams : Ext.EmptyFn,

    /**
     * @method
     * will check if there is some pendingSelection (no requested records)
     * <li>First case, there is no pending Selection, it will build a form parameter
     * with a list of id foreach record.</li>
     * <li>Second case, there is some pending Selection : it will build a ranges parameter
     * with all the selected ranges.</li>
     * @returns {} Depending on liveGridSelectionModel, will return either an object that will use form API
     * (p[0] = LISTBOXMULTIPLE|primaryKeyName|primaryKeyValue1|primaryKeyValue1|...|primaryKeyValueN),
     * either an object that will contain an array of ranges of selection
     * (ranges=[range1, range2, ..., rangen] where rangeN = [startIndex, endIndex])
     *
     */
    getSelectionParam : Ext.EmptyFn,

     /**
     * Return all request parameters
     * @return {String}
     */
    getRequestUrl : Ext.EmptyFn,

    /**
     * Return all request parameters without the column model
     * @return {String}
     */
    getRequestUrlWithoutColumnModel : Ext.EmptyFn,

    /**
     * Return all request parameters without the column model and selection
     * @return {String}
     */
    getRequestUrlWithoutSelection : Ext.EmptyFn,

    getSelectionsRange : Ext.EmptyFn,


    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings : Ext.EmptyFn
});
