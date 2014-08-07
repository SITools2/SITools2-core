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
	extend : 'Ext.app.Controller',
    
    init : function () {
        this.control({
            "opensearchView combo" : {
                beforequery : function (queryPlan) {
                    if (queryPlan.query.indexOf(" ") == -1) {
                        return true;
                    } else {
                        return false;
                    }
                },
                specialkey : function (field, e) {
                    if (e.getKey() == e.ENTER) {
                       this._clickOnSearch(field.up("opensearchView"));
                    }
                },
                beforeselect : function (combo, record, index) {
                    var tabName = record.get("name").split(':');
                    if (tabName.length > 1) {
                        record.set("name", tabName[1]);
                    }

                    record.data.name = record.get("field") + ":" + record.get("name");
                    return true;
                }
            },
            
            "opensearchView button#search" : {
                click : function (button) {
                    this._clickOnSearch(button.up("opensearchView"));
                }
            },
            
            "opensearchView button#help" : {
                click : function (button) {
                    //          var helpModule = SitoolsDesk.app.findModule("helpWindow");
                    //          if (!Ext.isEmpty(helpModule.getWindow())) {
                    //              helpModule.getWindow().close();
                    //          }
                    //          helpModule.openModule({
                    //              activeNode : "Recherche_OpenSearch"
                    //          });
                    alert("TODO HELP MODULE");
                }
            },
            
            "opensearchResultFeedView" : {
                itemdblclick : this.clickOnRow
            }
            
            
        });
    },
    
    /**
     * click handler for the search button gets the search query and update the
     * RSS feed URI to display the results
     */
    _clickOnSearch : function (view) {
        var searchQuery = view.down("form").getForm().getValues().searchQuery;
        view.down("grid").updateStore(view.getUri() + "?q=" + searchQuery);
    },
    
    clickOnRow : function (grid, rec, item, index, e) {
        var guid = rec.get("guid");
        if (Ext.isEmpty(guid)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n
                            .get('warning.noGuidFieldDefined')
                            + "<br/>"
                            + i18n.get('warning.noPrimaryKeyDefinedOSNotice'));
            return;
        }
        // si on est pas sur le bureau
        if (Ext.isEmpty(window) || Ext.isEmpty(window.SitoolsDesk)) {
//            var component = Ext.create("sitools.user.component.simpleViewDataDetail", {
//                fromWhere : "openSearch",
//                urlDataDetail : guid
//            });
//            var win = Ext.create("Ext.Window", {
//                stateful : false,
//                title : i18n.get('label.viewDataDetail'),
//                width : 400,
//                height : 600,
//                shim : false,
//                animCollapse : false,
//                constrainHeader : true,
//                layout : 'fit'
//            });
//            win.add(component);
//            win.show();
            
            var component = Ext.create('sitools.public.feedsReader.FeedItemDetails', {
                record : rec
            });
            
            var win = Ext.create('Ext.window.Window', {
                stateful : false,
                title : i18n.get('label.viewFeedDetail'),
                width : 400,
                height : 600,
                shim : false,
                animCollapse : false,
                constrainHeader : true,
                layout : 'fit',
                modal : true
            });
            win.add(component);
            win.show();
            
        } else {
//            var componentCfg = {
//                grid : this,
//                fromWhere : "openSearch",
//                datasetId : config.datasetId,
//                datasetUrl : config.dataUrl,
//                datasetName : config.datasetName,
//                preferencesPath : "/" + config.datasetName,
//                preferencesFileName : "dataDetails"
//            };
//            var jsObj = sitools.user.component.viewDataDetail;
//
//            var windowConfig = {
//                id : "dataDetail" + config.datasetId,
//                title : i18n.get('label.viewDataDetail') + " : "
//                        + config.datasetName,
//                datasetName : config.datasetName,
//                iconCls : "openSearch",
//                saveToolbar : true,
//                type : "dataDetail",
//                toolbarItems : [{
//                            iconCls : 'arrow-back',
//                            handler : function() {
//                                this.ownerCt.ownerCt.items.items[0]
//                                        .goPrevious();
//                            }
//                        }, {
//                            iconCls : 'arrow-next',
//                            handler : function() {
//                                this.ownerCt.ownerCt.items.items[0].goNext();
//                            }
//                        }]
//            };
//            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj,
//                    true);
            
            
        }

    },
    
    /** CONTROL VIEW METHODS * */
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
    
    
});
