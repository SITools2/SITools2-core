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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.user.controller.modules.datasets.dataviews');

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 *
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.controller.component.datasets.dataviews.TemplateViewController', {
    extend: 'Ext.app.Controller',

    views: ['component.datasets.dataviews.TemplateViewView',
        'component.datasets.services.ServiceToolbarView'],

    requires: ['sitools.public.widget.datasets.columnRenderer.BehaviorEnum'],


    init: function () {
        this.control({
            '#dataviewRecords': {
                selectionchange: function (selModel, recs) {
                    var templateView = selModel.views[0].up('templateView');
                    if (Ext.isEmpty(recs)) {
                        return;
                    }
                    //get the first selected record
                    var rec = recs[0];
                    var primaryKeyValue = "", primaryKeyName = "";
                    Ext.each(rec.fields.items, function (field) {
                        if (field.primaryKey) {
                            templateView.primaryKeyName = field.name;
                        }
                    }, templateView);
                    templateView.primaryKeyValue = rec.get(templateView.primaryKeyName);

                    templateView.primaryKeyValue = encodeURIComponent(templateView.primaryKeyValue);

                    var url = templateView.urlRecords + "/records/" + templateView.primaryKeyValue;
                    Ext.apply(templateView.panelDetail, {
                        url: url
                    });
                    templateView.panelDetail.getCmDefAndbuildForm();
                    templateView.panelDetail.expand();

                    var pageData = templateView.down('templatePagingToolbar').getPageData();

                    //destroy all selections if all was selected and another row is selected
                    if ((templateView.isAllSelected() && recs.length === DEFAULT_LIVEGRID_BUFFER_SIZE - 1) ||
                        (templateView.isAllSelected() && pageData.currentPage === pageData.pageCount)) { // if last page and less than DEFAULT_LIVEGRID_BUFFER_SIZE number of records
                        templateView.selectAllRowsBtn.toggle();
                        templateView.deselectAll();
                        var unselectedRec = templateView.getUnselectedRow(recs, templateView.store.data.items);
                        templateView.select(unselectedRec);
                    }
                },
                newdataloaded: function () {
                    if (!Ext.isEmpty(this.ranges)) {
                        if (!Ext.isEmpty(this.nbRecordsSelection) && (this.nbRecordsSelection == this.store.getTotalCount())) {
                            this.getCustomToolbarButtons();
                            this.selectAllRowsBtn.toggle(true);
                            delete this.nbRecordsSelection;
                        } else {
                            var ranges = Ext.util.JSON.decode(this.ranges);
                            this.selectRangeDataview(ranges);
                            delete this.ranges;
                        }
                    }
                },
                afterrender: function () {
                    if (!Ext.isEmpty(this.dataView)) {
                        //this._loadMaskAnchor = Ext.get(this.el.dom);
                        //this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
                    }
                }
            }
        });
    },

});