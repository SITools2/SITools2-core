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
 * @class sitools.user.controller.component.datasets.dataviews.TemplateViewController
 * @extends Ext.app.Controller
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
                    templateView.down('serviceToolbarView').updateContextToolbar();

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
                    //templateView.panelDetail.setWidth(450);
                    templateView.panelDetail.expand();

                    var pageData = templateView.down('pagingtoolbar').getPageData();

                    //destroy all selections if all was selected and another row is selected
                    if ((templateView.isAllSelected() && recs.length === DEFAULT_LIVEGRID_BUFFER_SIZE - 1) ||
                        (templateView.isAllSelected() && pageData.currentPage === pageData.pageCount)) { // if last page and less than DEFAULT_LIVEGRID_BUFFER_SIZE number of records
                        templateView.selectAllRowsBtn.toggle();
                        templateView.deselectAll();
                        var unselectedRec = templateView.getUnselectedRow(recs, templateView.store.data.items);
                        templateView.select(unselectedRec);
                    }
                },
                newdataloaded: function (dataView) {
                    var templateView = dataView.up('templateView');
                    templateView.ranges = Ext.decode(templateView.ranges);
                    if (!Ext.isEmpty(templateView.ranges)) {
                        if (!Ext.isEmpty(templateView.nbRecordsSelection) && (templateView.nbRecordsSelection == templateView.store.getTotalCount())) {
                            templateView.getCustomToolbarButtons();
                            templateView.selectAllRowsBtn.toggle(true);
                            delete templateView.nbRecordsSelection;
                        } else {
                            var ranges = Ext.decode(templateView.ranges);
                            templateView.selectRangeDataview(ranges);
                            delete templateView.ranges;
                        }
                    }
                },
                afterrender: function (dataView) {
                    dataView.getSelectionModel().getSelectedRanges = this.getSelectedRanges;
                }
            },
            'templateView': {
                afterrender: function (templateView) {
                    templateView.store.load({
                        params: {
                            start: 0,
                            limit: DEFAULT_LIVEGRID_BUFFER_SIZE
                        },
                        scope: this
                    });
                }
            }
        });
    },

    getSelectedRanges: function () {

        var index = 1,
            ranges = [],
            currentRange = 0,
            tmpSelected = this.selected.clone();

        if (Ext.isEmpty(this.selected) && this.store.getTotalCount() == 0) {
            return [];
        }

        if (this.markAll) {
            return [[0, this.store.getTotalCount() - 1]];
        }


        var lastIndex;
        tmpSelected.sort(function (o1, o2) {
                if (o1 > o2) {
                    return 1;
                } else if (o1 < o2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        );
        tmpSelected.each(function (rec) {
            if (Ext.isEmpty(lastIndex)) {
                ranges[currentRange] = [rec.index, rec.index];
            }
            else {

                if (rec.index - lastIndex === 1) {
                    ranges[currentRange][1] = rec.index;
                } else {
                    currentRange++;
                    ranges[currentRange] = [rec.index, rec.index];
                }
            }

            lastIndex = rec.index;
        }, this);

        return ranges;
    }

});