/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.user.modules');

/**
 * Dataset Explorer Module.
 * Displays each dataset of the Project.
 * @class sitools.user.modules.datasetExplorerDataView
 * @extends Ext.tree.TreePanel
 */
sitools.user.modules.datasetExplorerDataView = Ext.extend(Ext.Panel, {
    layout : "border",
    
    initComponent : function () {
	    /**
	     * INDEX JPB var projectId = Ext.util.Cookies.get('projectId'); if
	     * (Ext.isEmpty(projectId)){ Ext.Msg.alert(i18n.get ('warning.noProject'));
	     * return; }
	     */
	
	//    var projectId = projectGlobal.getProjectId();
	    var projectAttachment = projectGlobal.sitoolsAttachementForUsers;
	    this.layout = "border";
	    
	    this.store = new Ext.data.JsonStore({
	        url : projectAttachment + '/datasets?media=json',
            root : "data",
	        autoLoad : true,
	        fields : [ 'id', 'name', 'url', 'description', 'image', 'authorized', 'properties', 'status', 
                {
						name : 'nbRecords',
						type : "int"
					}],
            listeners : {
                scope : this,
                load : function (store, records, options) {
                    Ext.each(records, function (record) {
                        if (record.get("status") === "ACTIVE") {
	                        var properties = record.get("properties");
	                        var img = null;
                            var nbRecords;
	                        Ext.each(properties, function (property) {
	                            if (property.name === "imageUrl") {
	                                img = property.value;	                                
	                            }
                                if (property.name === "nbRecord") {
                                    record.set("nbRecords", parseInt(property.value));
                                }
	                        });
	                        if (!Ext.isEmpty(img)) {
								record.set("image", img);
							} else {
								record.set("image", SITOOLS_DEFAULT_PROJECT_IMAGE_URL);
							}
                        } else {
							store.remove(record);
						}
                    });
                    store.clearFilter();
                    this.doSort();
                }
            }
	    });
	    
	    var myDataView = new Ext.DataView({
	        store : this.store, 
            id : "datasetDataView",
            region : 'center',
            itemSelector : 'li.dataset',
	        singleSelect : true,
	        multiSelect : false,
	        autoScroll : true,
	        tpl : new Ext.XTemplate(
		            '<ul>',
			            '<tpl for=".">', 
                            '<li id="{id}" ',
	                            '<tpl if="authorized==\'true\'">',
				                    'class="dataset"',
				                '</tpl>', 
				                '<tpl if="authorized==\'false\'">',
				                    'class="dataset datasetUnauthorized"',
				                '</tpl>',
                            '>',
			                    '<img width="80" height="80" src="{image}" />', '<strong>{name}</strong>',
			                    '<span>({nbRecords} records)</span><br/>',      
                                '<div class="dataset_services">',
                                    '<tpl if="authorized==\'true\'">',
	                                   '{url:this.getIconeData}',
	                                   '{url:this.getIconeForm}',
	                                   '{url:this.getIconeOpensearch}',
	                                   '{url:this.getIconeFeeds}',
                                    '</tpl>',
                                '</div>',
                                '<div class="dataset_description">{description}</div>',
                                '<tpl if="authorized==\'true\'">',
                                    '{url:this.getIconeDescription}',
                                '</tpl>',
			                '</li>', 
			            '</tpl>', 
		            '</ul>', 
	                {
                    getIconeData : function (value) {
	                    return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
	                                                            + "\", \"data\"); return false;'><img src='" + loadUrl.get('APP_URL')
	                                                            + "/common/res/images/icons/32x32/tree_datasets_32.png'></a>";
	                },
                    getIconeForm : function (value) {
                        return SitoolsDesk.navProfile.manageDatasetViewAlbumIconForm(value);
                    },
                    getIconeOpensearch : function (value) {
                        return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                                + "\", \"openSearch\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                                + "/common/res/images/icons/toolbar_open_search.png'></a>";
                    },
                    getIconeFeeds : function (value) {
	                    return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
	                                                            + "\", \"feeds\"); return false;'><img src='" + loadUrl.get('APP_URL')
	                                                            + "/common/res/images/icons/32x32/rss_32.png'></a>";
	                },
                    getIconeDescription : function (value) {
	                    return "<a  href='#' class='align-right' ext:qtip='"+i18n.get("label.more")+"' onClick='sitools.user.clickDatasetIcone(\"" + value
	                                                            + "\", \"desc\"); return false;'>"+i18n.get("label.more")+"</a>";
	                },
	                compiled : true, 
	                disableFormats : false                                      
	            })
	
	        // plugins : [
	        // new Ext.ux.DataViewTransition({
	        // duration : 550,
	        // idProperty: 'id'
	        // })
	        // ],
	    });
        
        var buttonName = this.createSorterButton({
            text: i18n.get("label.name"),
            sortData: {
                field: 'name',
                direction: 'ASC'
            }
        });
                
        var buttonNbRecords = this.createSorterButton({
            text: i18n.get("label.nbRecords"),
            sortData: {
                field: 'nbRecords',
                direction: 'ASC'
            }
        });
        
        
        this.tbar = new Ext.Toolbar({
            xtype : 'toolbar',
            cls : 'services-toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [''],
	        plugins: [new Ext.ux.ToolbarReorderer()],	        
	        listeners: {
	            scope    : this,
	            reordered: function (button) {
	                this.changeSortDirection(button, false);
	            }
	        }
	    });
        var moduleGraph = SitoolsDesk.app.getModule("projectGraph");
        if (!Ext.isEmpty(moduleGraph)) {
            this.tbar.add({
                    text : i18n.get("label.projectGraph"),
                    iconCls : "graphModule",
                    listeners: {
                        scope : this,
                        click: function (button, e) {
                            this.openProjectGraph();                    
                        }
                    }
                }, '->', '-');
        }
        
        this.tbar.add(i18n.get("label.sortOnTheseFields"), buttonName, buttonNbRecords);
        
        this.items = [myDataView];
        
        var description = i18n.get('label.descriptionExplorerDataview');
        
        if (description !== "label.descriptionExplorerDataview") {
            this.items.unshift({
                xtype : 'panel',
                height : 100,
                html : description, 
                padding : "10px", 
                region : "north", 
                collapsible : true, 
                autoScroll : true, 
                title : i18n.get('label.description')
            });
        }
        
        sitools.user.modules.datasetExplorerDataView.superclass.initComponent.call(this);
        
    },
    
    afterRender : function () {
        sitools.user.modules.datasetExplorerDataView.superclass.afterRender.apply(this, arguments);
        
    },
    
    /**
     * Convenience function for creating Toolbar Buttons that are tied to sorters
     * @param {Object} config Optional config object
     * @return {Ext.Button} The new Button object
     */
    createSorterButton : function (config) {
        config = config || {};
              
        Ext.applyIf(config, {
            listeners: {
                scope : this,
                click: function (button, e) {
                    this.changeSortDirection(button, true);                    
                }
            },
            iconCls: 'sort-' + config.sortData.direction.toLowerCase(),
            reorderable: true
        });
        
        return new Ext.Button(config);
    },
    
    /**
     * Tells the store to sort itself according to our sort data
     */
    doSort : function () {
        this.store.sort(this.getSorters(), "ASC");
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
                button.sortData.direction = button.sortData.direction.toggle("ASC", "DESC");
                button.setIconClass(iconCls.toggle("sort-asc", "sort-desc"));
            }
            
            this.store.clearFilter();
            this.doSort();
        }
    },
    
    /**
     * Returns an array of sortData from the sorter buttons
     * @return {Array} Ordered sort data from each of the sorter buttons
     */
    getSorters : function () {
        var sorters = [];
        
        Ext.each(this.getTopToolbar().findByType('button'), function (button) {
            if (!Ext.isEmpty(button.sortData)) {
				sorters.push(button.sortData);
			}
        }, this);
        
        return sorters;
    },
    
    openProjectGraph : function () {
        var module = SitoolsDesk.app.getModule("projectGraph");
        if (Ext.isEmpty(module)) {
			Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.moduleProjectGraphUnavailable"));
		}
        else {
            module.openModule();
        }
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
    
});

Ext.reg('sitools.user.modules.datasetExplorerDataView', sitools.user.modules.datasetExplorerDataView);
