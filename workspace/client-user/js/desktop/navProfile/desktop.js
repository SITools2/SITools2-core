/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, locale, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.user.desktop.navProfile');

/**
 * Object to expose methods in desktop Mode
 * @class sitools.user.desktop.navProfile.desktop
 * 
 */
sitools.user.desktop.navProfile.desktop = {
	/**
	 * The name of the context
	 */
	context : "desktop", 
    /**
     * method called by SitoolsDesk. 
     * It should create all components in Sitools except modules. 
     * the default comportement is create a new window in desktop and add a taskButton in desktop TaskBar 
     * @cfg {} componentCfg the component Configuration. 
     * @cfg {} windowSettings the window Settings
     * @cfg {boolean} reloadComp true to rebuild component if exists
     * @cfg {string} JsObj The jsObject to instanciate. 
     */
	createComponent : function (config) {
    	var desktop = getDesktop();
        var componentCfg = config.componentCfg;
        var component = new config.JsObj(componentCfg);
        var windowSettings = config.windowSettings;
        // déléguer au composant l'ouverture
        if (Ext.isFunction(component.showMeInDesktopNav)) {
            component.showMeInDesktopNav(component, config);
            return;
        }

        var reloadComp = config.reloadComp;

        if (Ext.isEmpty(component._getSettings)) {
            component._getSettings = function () {
                return {};
            };
        }
        
        var newwin;
        var winHeight = windowSettings.winHeight || DEFAULT_WIN_HEIGHT;
        var winWidth = windowSettings.winWidth || DEFAULT_WIN_WIDTH;

        var x = windowSettings.x;
        var y = windowSettings.y;

        var desktopEl = getDesktop().getDesktopEl();
        
        if (x < desktopEl.dom.offsetLeft) {
            x = desktopEl.dom.offsetLeft;
        }
        if (y < desktopEl.dom.offsetTop) {
            y = desktopEl.dom.offsetTop;
        }
        if (x + winWidth > desktopEl.dom.offsetLeft + desktopEl.dom.offsetWidth) {
            winWidth = desktopEl.dom.offsetLeft + desktopEl.dom.offsetLeft - x;
        }
        if (y + winHeight > desktopEl.dom.offsetTop + desktopEl.dom.offsetHeight) {
            winHeight = desktopEl.dom.offsetTop + desktopEl.dom.offsetHeight - y;
        }
        
        var tbar;
        if (!Ext.isEmpty(windowSettings.toolbarItems)) {
            // Create the toolbar with the windowSettings.toolbarItems
            tbar = new Ext.Toolbar({
                xtype : 'toolbar',
                items : windowSettings.toolbarItems,
                cls : 'toolbar'
            });
        }
        
        var fileName = "";
        var title = windowSettings.title;
        if (windowSettings.type === "form") {
            fileName = windowSettings.type + componentCfg.formName;
        }
        else if (windowSettings.type === "data" && Ext.isDefined(component.getWindowTitle)) {
            title = component.getWindowTitle(windowSettings.datasetDescription, windowSettings.datasetName);
        }
        else {
            fileName = windowSettings.type;
        }
        
        var winListeners = {};
        Ext.apply(winListeners, windowSettings.listeners);
        Ext.apply(winListeners, {
		    render : function (me){
		        me.getEl().fadeIn({
		            duration: .5
		        });
		    }
        });

        newwin = desktop.createWindow({
            id : windowSettings.id,
            stateful : false,
            title : title,             
            width : winWidth,
            iconCls : windowSettings.iconCls,
            height : winHeight,
            shim : false,
            tbar : tbar,
            animCollapse : false,
            x : x, 
            y : y, 
            constrainHeader : true,
            layout : 'fit',
            specificType : 'componentWindow',
            datasetName : windowSettings.datasetName,
            datasetDescription : windowSettings.datasetDescription,
            fileName : fileName, 
            component : component, 
            autoscroll : true,
            typeWindow : windowSettings.type,
            maximized : windowSettings.maximized,
            listeners : winListeners,
        	tools : [ {
                id : "save",
                scope : this, 
                qtip : i18n.get('label.saveSettings'),
                handler : function (event, toolEl, window) {
			        if (projectGlobal.isAdmin) {
						var ctxMenu = new Ext.menu.Menu({
							items: ['<b class="menu-title">' + i18n.get('label.chooseSave') + '</b>',
			                {
			                    text: i18n.get("label.myself"),
			                    handler : function () {
									window.saveSettings(window.component._getSettings(), false);
			                    }
			                }, {
			                    text: i18n.get("label.publicUser"),
			                    handler : function () {
									window.saveSettings(window.component._getSettings(), true);
			                    }
			                }] 
						});
						ctxMenu.showAt(event.getXY());
						
			        }
			        else {
						window.saveSettings(window.component._getSettings());
			        }
				},
                hidden : Ext.isEmpty(userLogin) || !windowSettings.saveToolbar
            } ]
        });
        var pos, size;

        pos = windowSettings.position;
        size = windowSettings.size;
        if (size !== null) {
            size = Ext.decode(size);
            newwin.setSize(size);
        }
        else {
            size = newwin.getSize();
            size.width = size.width + 1;

            newwin.setSize(size);
        }
        newwin.show();

        
        newwin.add(component);

        if (!Ext.isEmpty(pos)) {
            pos = Ext.decode(pos);
            newwin.setPosition(pos);
        }
        
        // Permet d'ajuster les fenêtres qui sont maximisées à l'espace du bureau
        if (SitoolsDesk.desktopMaximizeMode) {
            SitoolsDesk.getDesktop().minimize();    
            SitoolsDesk.getDesktop().maximize();    
        }
        else {
            SitoolsDesk.getDesktop().maximize();    
            SitoolsDesk.getDesktop().minimize();    
        }
        
        newwin.doLayout();
    	
    },  
    /**
     * method called by SitoolsDesk. 
     * It should create all modules in Sitools. 
     * the default comportement is create a new window in desktop.
     * @param {} The module description
     * @returns {Ext.app.Module} the created window. 
     */
    openModule : function (module) {
    	var desktop = getDesktop();

        var win = desktop.getWindow(module.id);
        if (!win) {

            win = desktop.createWindow({
                id : module.id,
                stateful : false,
                title : i18n.get(module.title),
                width : module.defaultWidth,
                height : module.defaultHeight,
                iconCls : module.icon,
                x : module.x,
                y : module.y,
                shim : false,
                animCollapse : false,
                constrainHeader : true,
                layout : 'fit',
                specificType : "moduleWindow",
                listeners : {
                    render : function (me) {
                        me.getEl().fadeIn({
                            duration : .5
                        });
                    }
                }, 
                items : [ {
			        id : module.id + "_module", 
                   	noBorder : true, 
					layout : 'fit',
			        xtype : module.xtype, 
			        moduleProperties : module.properties,
			        listProjectModulesConfig : module.listProjectModulesConfig
                } ]
            });
            if (!Ext.isEmpty(projectGlobal.preferences)) {
                Ext.each(projectGlobal.preferences.windowSettings, function (preference) {
                    if (preference.windowSettings.moduleId == module.id) {
                        if (preference.windowSettings.maximized) {
                            win.minimize();
                        	win.maximize();
                        }
                        else {
                        	var pos = preference.windowSettings.position;
                            var size = preference.windowSettings.size;

                            if (pos !== null && size !== null) {
                                pos = Ext.decode(pos);
                                size = Ext.decode(size);

                                win.setPosition(pos);
                                win.setSize(size);
                            }
                        	
                        }
                    }
                });
            }

        } else {
            desktop.getManager().bringToFront(win);
        }

        win.show();
        return win;    	
    },
    

        
    /**
     * Do nothing in fixed mode
     */
    initNavbar : function () {
        return;
    }, 
    


    /**
     * Specific multiDataset search context methods
     */
    multiDataset : {
        /**
         * Returns the right object to show multiDs results
         * 
         * @returns
         */
        getObjectResults : function () {
            return sitools.user.component.forms.resultsProjectForm;
	    }, 
	    /**
	     * Handler of the button show data in the {sitools.user.component.forms.resultsProjectForm} object 
	     * @param {Ext.grid.GridPanel} grid the grid results
	     * @param {int} rowIndex the index of the clicked row
	     * @param {int} colIndex the index of the column
	     * @returns
	     */
	    showDataset : function (grid, rowIndex, colIndex) {
            var rec = grid.getStore().getAt(rowIndex);
            if (Ext.isEmpty(rec)) {
				return;
            }
            if (rec.get('status') == "REQUEST_ERROR") {
				return;
            }
            sitools.user.clickDatasetIcone(rec.get("url"), "data", {
				formMultiDsParams : this.formMultiDsParams
            });
        }
    }, 
    taskbar : {
    	getContextMenuItems : function () {
    		return [ {
    	        text : 'Restore',
    	        handler : function () {
    		        if (!this.win.isVisible()) {
    			        this.win.show();
    		        } else {
    			        this.win.restore();
    		        }
    	        },
    	        scope : this
    	    }, {
    	        text : 'Minimize',
    	        handler : this.win.minimize,
    	        scope : this.win
    	    }, {
    	        text : 'Maximize',
    	        handler : this.win.maximize,
    	        scope : this.win
    	    }, '-', {
    	        text : 'Close',
    	        handler : this.closeWin.createDelegate(this, this.win, true),
    	        scope : this.win
    	    } ];
        
    	}, 
        handleTaskButton : function (btn) {
    	    var win = btn.win;
        	if (win.minimized || win.hidden) {
    		    win.show();
    	    } else if (win == win.manager.getActive()) {
    		    win.minimize();
    	    } else {
    		    win.toFront();
    	    }
        }, 
        closeWin : function (cMenu, e, win) {
	    	if (!win.isVisible()) {
			    win.show();
		    } else {
			    win.restore();
		    }
		    win.close();
		}, 
		beforeShowCtxMenu : function () {
			var items = this.cmenu.items.items;
		    var w = this.win;
		    items[0].setDisabled(w.maximized !== true && w.hidden !== true);
		    items[1].setDisabled(w.minimized === true);
		    items[2].setDisabled(w.maximized === true || w.hidden === true);
		}, 
	    /**
	     * Add home and remove panel button to taskbar in desktop mode
	     */
	    initTaskbar : function (){
			SitoolsDesk.getDesktop().taskbar.setEnableWarning(false); 
			var showDesktopButton = new Ext.Button({
	            action : "minimize", 
				handler : function (btn) {
	        		if (btn.action === "minimize") {
	        			SitoolsDesk.minifyAllWindows();
	        			btn.action = "maximize";	        		
	        		}
	        		else {
	        			SitoolsDesk.openAllWindows();
	        			btn.action = "minimize";
	        		}
	            }, 
	            scale : "medium", 
	            tooltip : {
	                html : i18n.get("label.showDesktopButton"), 
	                anchor : 'bottom', 
	                trackMouse : false
	            },
	            template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
	                    '<td><em class="{5} unselectable="on">',
	                    '<button type="{1}" id="btn-showDesk" style="height:29px; width:18px;">{0}</button>', '</em></td>',
	                    '<td><i>&#160;</i></td>', "</tr></tbody></table>")
	        });
	        var removeActivePanel = new Ext.Button({
	            scope : this, 
	            handler : function () {
		        	SitoolsDesk.removeAllWindows();
	            }, 
	            scale : "medium", 
	            icon : "/sitools/common/res/images/taskbar/black/close-icon.png", 
	            iconCls : 'taskbarButtons-icon',
	            tooltip : {
	                html : i18n.get('label.removeActiveModule'), 
	                anchor : 'bottom', 
	                trackMouse : false
	            }, 
	            template : new Ext.Template('<table cellspacing="0" class="x-btn {3}" style="padding-left: 25px;"><tbody><tr>',
	                    '<td><i>&#160;</i></td>',
	                    '<td><em class="{5} unselectable="on">',
	                    '<button type="{1}" style="height:28px; width:28px; padding-left:12px;">{0}</button>', '</em></td>',
	                    '<td><i>&#160;</i></td>', "</tr></tbody></table>")

	        });
	        SitoolsDesk.getDesktop().taskbar.staticButtonPanel.addStaticButton(showDesktopButton);
	        SitoolsDesk.getDesktop().taskbar.staticButtonPanel.addStaticButton(removeActivePanel);
	    }		
    },
    
    /**
     * @return the specific JS View to display form
     */
    getFormOpenMode : function (){
        return sitools.user.component.forms.mainContainer;
    },
    
    /**
     * @return the specific JS View to display dataset
     */
    getDatasetOpenMode : function (dataset){
        return dataset.datasetView.jsObject;
    },
    
    /**
     * @param componentCfg
     *      the component to add the property
     * @param dataset
     *      the object which contains properties to add
     * @return the component with the new properties
     */
    addSpecificFormParameters : function (componentCfg, dataset){
        return componentCfg;
    },
    
    /**
     * Add specifics columns to project graph module
     *  in function of the navigation mode
     * 
     * @param columnsModel
     *          the columns to display in project graph module
     * @return the coumns with specifics columns added (or not)
     */
    manageProjectGraphColumns : function (columnsModel) {
       return columnsModel.push({
            width : 90,
            header : Ext.util.Format.ellipsis(i18n.get("label.forms"), 12),
            //cls : "grid-column-color",
            tpl : new Ext.XTemplate('{[datasetId=""]}', '<tpl if="this.exists(datasetId) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                getIcone : function (value) {
                    return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"forms\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/form_list_small.png'></img></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        });
    },
    
    /**
     * Add icon form to datasetView Album depending to navigation mode
     * 
     * @param value
     * @return nu
     */
    manageDatasetViewAlbumIconForm : function (value){
        return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
        + "\", \"forms\"); return false;'><img src='" + loadUrl.get('APP_URL')
        + "/common/res/images/icons/32x32/form_list_32.png'></a>";
    },
    
    /**
     * Add icon definition and form to dataset Explorer depending to navigation mode
     * @param commonTreeUtils
     */
    manageDatasetExplorerShowDefinitionAndForms : function (commonTreeUtils, node, dataset){
        commonTreeUtils.addShowDefinition(node, dataset);
        commonTreeUtils.addForm(node, dataset);
        
        return commonTreeUtils;
    }, 
    /**
     * Called when desktop is saved. 
     * Loop through all windows of the desktop and save settings for each window. 
     * @param forPublicUser
     * @returns {Array} an Array containing all window settings
     */
    getDesktopSettings : function (forPublicUser) {
    	var desktopSettings = [];
    	getDesktop().getManager().each(function (window) {
	        var componentSettings;
	        if (!Ext.isEmpty(window.specificType) && (window.specificType === 'componentWindow' || window.specificType === 'moduleWindow')) {
	            //Bug 3358501 : add a test on Window.saveSettings.
	            if (Ext.isFunction(window.saveSettings)) {
		            var component = window.get(0);
		            
		            componentSettings = component._getSettings();
					desktopSettings.push(window.saveSettings(componentSettings, forPublicUser));
	            }
	        } 
	    });
    	return desktopSettings;
    }, 
    /**
     * Load the module Window corresponding to the project Preference. 
     * 1 - load the module Windows
     * 2 - load the Component windows (actually only "data", "form"  && "formProject" type window) 
     */
    loadPreferences : function (scope) {
    	//Chargement des composants ouverts. 
		Ext.each(projectGlobal.preferences.windowSettings, function (pref) {
	        //1° cas : les fenêtres de modules
	        if (Ext.isEmpty(pref.windowSettings.typeWindow)) {
				var moduleId = pref.windowSettings.moduleId;
	        
	            var module = SitoolsDesk.app.getModule(moduleId);
	            if (!Ext.isEmpty(module) && Ext.isEmpty(module.divIdToDisplay)) {
	                var win = module.openModule();
	                var pos = pref.windowSettings.position;
	                var size = pref.windowSettings.size;
	
	                if (pos !== null && size !== null) {
	                    pos = Ext.decode(pos);
	                    size = Ext.decode(size);
	
	                    win.setPosition(pos[0], pos[1]);
	                    win.setSize(size);
	                }
	            }
	        }
	        //les autres fenêtres : on nne traite que les cas windowSettings.typeWindow == "data"
	        else {
	        	var type = pref.windowSettings.typeWindow;
				var componentCfg, jsObj, windowSettings;
				if (type === "data") {
				    var datasetUrl = pref.componentSettings.datasetUrl;
					Ext.Ajax.request({
						method : "GET", 
						url : datasetUrl, 
						success : function (ret) {
					        var Json = Ext.decode(ret.responseText);
					        if (showResponse(ret)) {
					            var dataset = Json.dataset;
					            var componentCfg, javascriptObject;
					            var windowConfig = {
					                datasetName : dataset.name,
					                datasetDescription : dataset.description,
					                type : type, 
					                saveToolbar : true, 
					                toolbarItems : [], 
					                iconCls : "dataviews"
					            };
					            
					            javascriptObject = eval(dataset.datasetView.jsObject);
				
					            //add the toolbarItems configuration
				                Ext.apply(windowConfig, {
				                    id : type + dataset.id
				                });
				                
				                if (dataset.description !== "") {
									windowConfig.title = dataset.description;
				                }
				                else {
									windowConfig.title = "Diplay data :" + dataset.name;
				                }
				                componentCfg = {
				                    dataUrl : dataset.sitoolsAttachementForUsers,
				                    datasetId : dataset.id,
				                    datasetCm : dataset.columnModel, 
				                    datasetName : dataset.name, 
				                    datasetViewConfig : dataset.datasetViewConfig,
				                    dictionaryMappings : dataset.dictionaryMappings, 
				                    preferencesPath : "/" + dataset.name, 
				                    preferencesFileName : "datasetOverview"
				                };
				                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
				
					        }
						}, 
						failure : alertFailure
					});	
				}
				if (type === "formProject") {
			        jsObj = sitools.user.component.forms.projectForm;
			        componentCfg = {
			            formId : pref.componentSettings.formId,
			            formName : pref.componentSettings.formName,
			            formParameters : pref.componentSettings.formParameters,
			            formWidth : pref.componentSettings.formWidth,
			            formHeight : pref.componentSettings.formHeight, 
			            formCss : pref.componentSettings.formCss, 
			            properties : pref.componentSettings.properties, 
			            urlServicePropertiesSearch : pref.componentSettings.urlServicePropertiesSearch, 
			            urlServiceDatasetSearch : pref.componentSettings.urlServiceDatasetSearch, 
		                preferencesPath : pref.componentSettings.preferencesPath, 
		                preferencesFileName : pref.componentSettings.preferencesFileName
			        };
			        windowSettings = {
			            type : "formProject", 
			            title : i18n.get('label.forms') + " : " + pref.componentSettings.formName, 
			            id : "formProject"  + pref.componentSettings.formId, 
			            saveToolbar : true, 
			            datasetName : pref.componentSettings.formName, 
			            winWidth : 600, 
			            winHeight : 600, 
			            iconCls : "form"
			        };
			        SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
					
				}
				if (type === "form") {
					jsObj = sitools.user.component.forms.mainContainer;
					componentCfg = {
			            dataUrl : pref.componentSettings.dataUrl,
		                dataset : pref.componentSettings.dataset, 
			            formId : pref.componentSettings.formId,
		                formName : pref.componentSettings.formName,
		                formParameters : pref.componentSettings.formParameters,
		                formWidth : pref.componentSettings.formWidth,
		                formHeight : pref.componentSettings.formHeight, 
		                formCss : pref.componentSettings.formCss, 
		                preferencesPath : pref.componentSettings.preferencesPath, 
		                preferencesFileName : pref.componentSettings.preferencesFileName
			        };
										
			        windowSettings = {
		                datasetName : pref.componentSettings.dataset.name, 
		                type : "form", 
		                title : i18n.get('label.forms') + " : " + pref.componentSettings.dataset.name + "." + pref.componentSettings.formName, 
		                id : "form" + pref.componentSettings.dataset.id + pref.componentSettings.formId, 
		                saveToolbar : true, 
		                iconCls : "form"
			        };	
			        SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
				}
	        }
	    }, scope);
	    
    },
    
	/**
	 * Close every window in desktop mode. 
	 */
	removeAllWindows : function() {
		SitoolsDesk.getDesktop().getManager().each(function(win) {
			win.close();
		});
	}

    
};
