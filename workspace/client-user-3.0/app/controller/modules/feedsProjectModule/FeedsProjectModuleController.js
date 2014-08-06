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

Ext.namespace('sitools.user.controller.modules.feedsProjectModule');

/**
 * Feeds project Module controller : Displays All Feeds on a project
 * 
 */
Ext.define('sitools.user.controller.modules.feedsProjectModule.FeedsProjectModuleController', {
	extend : 'Ext.app.Controller',
    
    views : ['modules.feedsProjectModule.FeedsProjectModuleView'],
    
    init : function () {
        this.control({
            "feedsProjectModuleView" : {
                feedsLoaded : function (view, store, records) {
                    if (store.getCount() !== 0) {
                        view.down("combo").setValue(store.getAt(0).get("name"));
                        this.selectFeed(view.down("combo"), store.getAt(0));
                    }
                }
            },
            "feedsProjectModuleView combo" : {
                select : function (combo, records) {
                    this.selectFeed(combo, records[0]);
                }
            },
            "feedsProjectModuleView toolbar button" : {
              click : function (button) {
                  this.changeSortDirection(button, true);
              } 
            }
        });
    },
    
    /** CONTROL VIEW METHODS * */
    
    selectFeed : function (combo, rec) {
        var view = combo.up('feedsProjectModuleView');
        view.clearFeedsReader();
        
        var project = Ext.getStore('ProjectStore').getProject();
        var url = project.get("sitoolsAttachementForUsers") + "/clientFeeds/" + rec.get("name");
        var feed = rec.getData();
        
        var feedsReader = Ext.create('sitools.public.feedsReader.FeedGridFlux', {
            urlFeed : url,
            feedType : feed.feedType,
            feedSource : feed.feedSource
        });
        
        view.addFeedsReader(feedsReader);
    },
    
    /**
     * Callback handler used when a sorter button is clicked or reordered
     * @param {Ext.Button} button The button that was clicked
     * @param {Boolean} changeDirection True to change direction (default). Set to false for reorder
     * operations as we wish to preserve ordering there
     */
    changeSortDirection : function (button, changeDirection) {
        var sortData = button.sortData,
            iconCls  = button.iconCls;
        
        if (sortData != undefined) {
            if (changeDirection !== false) {
                button.sortData.direction = Ext.String.toggle(button.sortData.direction, "ASC", "DESC");
                button.setIconCls( Ext.String.toggle(iconCls, "sort-asc", "sort-desc"));
            }
            this.doSort(button.up("feedsProjectModuleView"));
        }
    },
    
    /**
     * Tells the store to sort itself according to our sort data
     */
    doSort : function (view) {
        view.down("grid").sortByDate(view.down("toolbar button").sortData.direction);
    },
    
    
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
