/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, projectGlobal, commonTreeUtils, showResponse, document, i18n, loadUrl, SITOOLS_DEFAULT_PROJECT_IMAGE_URL, SitoolsDesk*/
/*
 * @include "../../env.js" 
 */
Ext.namespace('sitools.user.view.modules.datasetExplorerDataview');

/**
 * Dataset Explorer Module.
 * Displays each dataset of the Project.
 * @class sitools.user.modules.datasetExplorerDataView
 * @extends Ext.tree.TreePanel
 */
Ext.define('sitools.user.view.modules.datasetExplorerDataview.DatasetExplorerDataViewView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.datasetExplorerDataViewView',

    layout: {
        type : 'vbox',
        align : 'stretch',
        margin : 10
    },
    border : false,

    initComponent: function () {
        /**
         * INDEX JPB var projectId = Ext.util.Cookies.get('projectId'); if
         * (Ext.isEmpty(projectId)){ Ext.Msg.alert(i18n.get ('warning.noProject'));
	     * return; }
         */


        var project = Ext.getStore('ProjectStore').getProject();
        var url = project.get('sitoolsAttachementForUsers') + '/datasets?';

        this.store = Ext.create('sitools.user.store.DatasetDataviewStore');
        this.store.setCustomUrl(url);

        var myDataView = Ext.create('Ext.view.View', {
            flex : 1,
            store: this.store,
            cls: "datasetExplorerDataview",
            region: 'center',
            itemSelector: 'li.dataset',
            mode: 'SINGLE',
            multiSelect: false,
            autoScroll: true,
            tpl: new Ext.XTemplate(
                '<ul>',
                    '<tpl for=".">',
                        '<li id="{id}" class="dataset">',
                            '<img id="dsImage" width="80" height="80" src="{image}" />',
                            '<strong>{name}</strong>',
                            '<span>({nbRecords} records)</span>' +
                            //'<hr style="border-style: ridge;"/>',
                            '<div class="dataset_services">',
                                '<tpl if="authorized==\'true\'">',
                                    '{[this.getIconeData(values.url)]}',
                                    '{[this.getIconeForm(values.url)]}',
                                    '{[this.getIconeOpensearch(values.url)]}',
                                    '{[this.getIconeFeeds(values.url)]}',
                                '</tpl>',
                            '</div>',
                            '<div class="dataset_description">{description}</div>',
                            '<tpl if="authorized==\'true\'">',
                                '{url:this.getIconeDescription}',
                            '</tpl>',
                        '</li>',
                    '</tpl>',
                '</ul>', {
                getIconeData: function (value) {
                    var icon = loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/tree_datasets_32.png";
                    return Ext.String.format("<a href='#' class=\"overDatasetService\" onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"data\"); " +
                        "return false;'><img src='{1}' data-qtip='{2}'\></a>", value, icon, i18n.get('label.dataTitle'));
                },
                getIconeForm: function (value) {
                    var icon = loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/form_list_32.png";
                    return Ext.String.format("<a href='#' class=\"overDatasetService\" onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"forms\"); " +
                    "return false;'><img src='{1}' data-qtip='{2}'\></a>", value, icon, i18n.get('label.viewForm'));

                    //return SitoolsDesk.navProfile.manageDatasetViewAlbumIconForm(value);
                },
                getIconeOpensearch: function (value) {
                    var icon = loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/openSearch_32.png";
                    return Ext.String.format("<a href='#' class=\"overDatasetService\" onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"openSearch\"); " +
                        "return false;'><img src='{1}' data-qtip='{2}'\></a>", value, icon, i18n.get('label.searchOpenSearch'));
                },
                getIconeFeeds: function (value) {
                    var icon = loadUrl.get('APP_URL') + "/common/res/images/icons/32x32/rss_32.png";
                    return Ext.String.format("<a href='#' class=\"overDatasetService\" onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"feeds\"); " +
                        "return false;'><img src='{1}' data-qtip='{2}'\></a>", value, icon, i18n.get('label.rssFeedDatasets'));
                },
                getIconeDescription: function (value) {
                    return "<a  href='#' class='align-right overMoreDescription' data-qtip='" + i18n.get("label.moreDescription") + "' onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"" + value
                        + "\", \"desc\"); return false;'>" + i18n.get("label.more") + "</a>";
                }
            })
        });

        var buttonName = this.createSorterButton({
            itemId : 'sorterNameBtn',
            text: i18n.get("label.name"),
            sortData: {
                property: 'name',
                direction: 'ASC'
            }
        });

        var buttonNbRecords = this.createSorterButton({
            itemId : 'sorterNbRecordsBtn',
            text: i18n.get("label.nbRecords"),
            sortData: {
                property: 'nbRecords',
                direction: 'ASC'
            }
        });

        this.tbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [],
            border : false,
            listeners: {
                scope: this,
                reordered : function (button) {
                    this.changeSortDirection(button, false);
                }
            }
        });

        var moduleStore = Ext.StoreManager.lookup('ModulesStore');
        var moduleIndex = moduleStore.find('xtype', 'sitools.user.modules.ProjectGraphModule');

        if (moduleIndex != -1) {
            this.projectGraphModule = moduleStore.getAt(moduleIndex);
            this.tbar.add({
                itemId : "openGraphFromDsExplorer",
                text: i18n.get("label.projectGraph"),
                iconCls: "graphModule"
            }, '->', '-');
        }

        this.tbar.add(i18n.get("label.sortOnTheseFields"), buttonName, buttonNbRecords);
        this.items = [myDataView];

        var description = i18n.get('label.descriptionExplorerDataview');
        if (description !== "label.descriptionExplorerDataview") {
            this.items.unshift({
                xtype: 'panel',
                height: 100,
                html: description,
                padding: 10,
                bodyPadding : 10,
                collapsible: true,
                autoScroll: true,
                title: i18n.get('label.description')
            });
        }

        this.callParent(arguments);
    },

    /**
     * Convenience function for creating Toolbar Buttons that are tied to sorters
     * @param {Object} config Optional config object
     * @return {Ext.Button} The new Button object
     */
    createSorterButton: function (config) {
        config = config || {};

        Ext.applyIf(config, {
            iconCls: 'sort-' + config.sortData.direction.toLowerCase(),
            reorderable: true
        });

        return Ext.create('Ext.button.Button', config);
    },

    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id
        };
    }
});
