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
/*
 * global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin,
 * DEFAULT_PREFERENCES_FOLDER, loadUrl
 */
/*
 * @include "../../sitoolsProject.js" @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js" @include
 * "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.controller.modules.opensearch');

/**
 * Feeds project Module controller : Displays All Feeds on a project
 *
 */
Ext.define('sitools.user.controller.component.datasets.opensearch.OpensearchController', {
    extend: 'Ext.app.Controller',

    views : ['sitools.user.view.component.datasets.opensearch.OpensearchView',
        'sitools.user.view.component.datasets.opensearch.OpensearchResultFeedView'
    ],

    init: function () {
        this.control({
            "opensearchView combo": {
                beforequery: function (queryPlan) {
                    if (queryPlan.query.indexOf(" ") == -1) {
                        return true;
                    } else {
                        return false;
                    }
                },
                specialkey: function (field, e) {
                    if (e.getKey() == e.ENTER) {
                        this._clickOnSearch(field.up("opensearchView"));
                    }
                },
                beforeselect: function (combo, record, index) {
                    var tabName = record.get("name").split(':');
                    if (tabName.length > 1) {
                        record.set("name", tabName[1]);
                    }

                    record.data.name = record.get("field") + ":" + record.get("name");
                    return true;
                }
            },

            "opensearchView button#search": {
                click: function (button) {
                    this._clickOnSearch(button.up("opensearchView"));
                }
            },

            "opensearchView button#help": {
                click: function (button) {
                    var helpModule = Ext.create('sitools.public.utils.Help', {
                        listeners : {
                            boxready : function (help) {
                                var urlHelp = loadUrl.get('APP_URL') + "/client-user/resources/help/fr/User_Guide.html";
                                help.htmlReader.load(urlHelp + "#" + "Recherche_OpenSearch");
                            }
                        }
                    });
                    helpModule.show();
                }
            },

            "opensearchResultFeedView": {
                itemdblclick: this.clickOnRow
            }
        });
    },

    /**
     * click handler for the search button gets the search query and update the
     * RSS feed URI to display the results
     */
    _clickOnSearch: function (view) {
        var searchQuery = view.down("form").getForm().getValues().searchQuery;
        view.down("grid").updateStore(view.getUri() + "?q=" + searchQuery);
    },

    clickOnRow: function (grid, rec, item, index, e) {
        var guid = rec.get("guid");
        if (Ext.isEmpty(guid)) {
            var msg = i18n.get('warning.noGuidFieldDefined') + "<br/>" + i18n.get('warning.noPrimaryKeyDefinedOSNotice');
            Ext.Msg.alert(i18n.get('label.warning'), msg);
            return;
        }
        // si on est pas sur le bureau
        if (Desktop == undefined) {
            var component = Ext.create("sitools.user.view.component.datasets.opensearch.SimpleOpensearchDetailView", {
                fromWhere : "openSearch",
                urlDataDetail : guid
            });
            var win = Ext.create("Ext.Window", {
                stateful : false,
                title : i18n.get('label.viewDataDetail'),
                width : 400,
                height : 600,
                shim : false,
                animCollapse : false,
                constrainHeader : true,
                layout : 'fit'
            });
            win.add(component);
            win.show();

        } else {

            var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
            var openSearchView = grid.up('opensearchView');

            var config = {
                grid : grid,
                fromWhere : "openSearch",
                datasetId : openSearchView.datasetId,
                datasetUrl : openSearchView.dataUrl,
                datasetName : openSearchView.datasetName,
                preferencesPath : "/" + openSearchView.datasetName,
                preferencesFileName : "dataDetails"
            };

            var serviceObj = sitoolsController.openComponent("sitools.user.component.datasets.recordDetail.RecordDetailComponent", config);
        }
    }
});
