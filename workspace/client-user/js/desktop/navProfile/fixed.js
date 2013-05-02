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
 * Object to expose methods in fixed Mode
 * @class sitools.user.desktop.navProfile.fixed
 * 
 */
sitools.user.desktop.navProfile.fixed = {
    /**
     * The name of the context
     */
    context : "fixed", 
    /**
     * method called by SitoolsDesk. 
     * It should create all components in Sitools except modules. 
     * the default comportement is create a new panel in desktop. 
     * @cfg {} componentCfg the component Configuration. 
     * @cfg {} windowSettings the window Settings (contains the title)
     * @cfg {boolean} reloadComp not used in this context
     * @cfg {string} JsObj The jsObject to instanciate. 
     */
    createComponent : function (config) {
        var component = new config.JsObj(config.componentCfg);
        var windowSettings = config.windowSettings;
        
        //déléguer au composant l'ouverture
        if (Ext.isFunction(component.showMeInFixedNav)) {
            component.showMeInFixedNav(component, config);
            return;
        }
        
        SitoolsDesk.removeActivePanel();
        var desktop = getDesktop();
        
        var desktopEl = desktop.getDesktopEl();
        Ext.apply(component, {
            height : desktopEl.getHeight()
        });
        
       
        desktop.activePanel = desktop.createPanel({
            title : windowSettings.title, 
            border : false, 
//          cls : "sitools-module-panel", 
            layout : 'fit', 
            height : desktop.getDesktopEl().getHeight(), 
            width : desktop.getDesktopEl().getWidth(), 
            iconCls : windowSettings.iconCls,
            renderTo : desktop.getDesktopEl(), 
            noBorder : true, 
            items : [component], 
            type : windowSettings.type,
            specificType : "component",
            listeners : {
                render : function (me){
                    me.getEl().fadeIn({
                        duration: .5
                    });
                }, 
                resizeDesktop : function (me) {
                    me.setSize(desktop.getDesktopEl().getSize());
                    
                    var child = me.items.items[0];
                    if (child && child.getEl()) {
                        child.fireEvent("resize", child, me.body.getWidth(), me.body.getHeight(), child.getWidth(), child.getHeight());
                    }
                }, 
                maximizeDesktop : function (me) {
                    me.fireEvent("resize", me);
                }, 
                minimizeDesktop : function (me) {
                    me.fireEvent("resize", me);
                }
            }
            
        });
//        SitoolsDesk.addToHistory(desktop.activePanel);
        
    }, 
    /**
     * method called by SitoolsDesk. 
     * It should create all modules in Sitools. 
     * the default comportement is create a new panel in desktop.
     * @param {} The module description
     * @returns {Ext.app.Module} the created panel. 
     */
    openModule : function (module) {
        var desktop = getDesktop();
        SitoolsDesk.removeActivePanel();
        //Minimiser toutes les fenêtres actives ou les détruire si elles ne peuvent pas être minimisées.
        SitoolsDesk.minifyAllWindows();
        
        var panelToOpen = Ext.getCmp(module.id);
        if (panelToOpen) {
            panelToOpen.setSize(SitoolsDesk.getDesktop().getDesktopEl().getSize());
            SitoolsDesk.getDesktop().activePanel = panelToOpen;
            panelToOpen.show();
            return;
        }
        
        desktop.activePanel = desktop.createPanel({
            id : module.id,
            border : false, 
            title : i18n.get(module.title), 
            cls : "sitools-module-panel", 
            layout : 'fit', 
            specificType : "module", 
            iconCls : module.icon,
            height : desktop.getDesktopEl().getHeight(), 
            width : desktop.getDesktopEl().getWidth(), 
            renderTo : desktop.getDesktopEl(), 
            noBorder : true, 
            items : [ {
                noBorder : true, 
                layout : 'fit',
                xtype : module.xtype, 
                moduleProperties : module.properties,
                listProjectModulesConfig : module.listProjectModulesConfig
            }], 
            listeners : {
                render : function (me){
                    me.getEl().fadeIn({
                        duration: .5
                    });
                }, 
                resizeDesktop : function (me) {
                    me.setSize(desktop.getDesktopEl().getSize());
                    
                    var child = me.items.items[0];
                    if (child && child.getEl()) {
                        child.fireEvent("resize", child, me.body.getWidth(), me.body.getHeight(), child.getWidth(), child.getHeight());
                    }
                }, 
                maximizeDesktop : function (me) {
                    me.fireEvent("resize", me);
                }, 
                minimizeDesktop : function (me) {
                    me.fireEvent("resize", me);
                }
            }
            
        });
//        SitoolsDesk.addToHistory(desktop.activePanel);
        return desktop.activePanel;
        
    },
    
    /**
     * Specific multiDataset search context methods
     */
    multiDataset : {
        /**
         * Returns the right object to show multiDs results
         * @returns
         */
        getObjectResults : function () {
            return sitools.user.component.forms.overviewResultsProjectForm;
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
            
            Ext.Ajax.request({
                method : "GET", 
                url : rec.get('url'), 
                scope : this, 
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (showResponse(ret)) {
                        var dataset = Json.dataset;
                        var componentCfg, JsObj;
                        
                        JsObj = eval(dataset.datasetView.jsObject);
                        componentCfg = {
                            title : dataset.name, 
                            closable : true, 
                            dataUrl : dataset.sitoolsAttachementForUsers,
                            datasetId : dataset.Id,
                            datasetCm : dataset.columnModel, 
                            datasetName : dataset.name,
                            dictionaryMappings : dataset.dictionaryMappings,
                            datasetViewConfig : dataset.datasetViewConfig, 
                            sitoolsAttachementForUsers : dataset.sitoolsAttachementForUsers, 
                            formMultiDsParams : this.formMultiDsParams
                        };
                        var dataview = new JsObj(componentCfg);
                        
                        this.ownerCt.southPanel.add(dataview);
                        this.ownerCt.southPanel.setVisible(true);
                        this.ownerCt.southPanel.expand();
                        this.ownerCt.southPanel.setActiveTab(this.ownerCt.southPanel.items.length - 1);
                        this.ownerCt.doLayout();
                    }
                }, 
                failure : alertFailure
            });

        }
        
    },
    taskbar : {
        getContextMenuItems : function () {
            return [ {
                text : 'Close',
                handler : this.closeWin.createDelegate(this, this.win, true),
                scope : this.win
            } ];
        
        }, 
        handleTaskButton : function (btn, event) {
            var panel = btn.win;
            SitoolsDesk.removeActivePanel();
            panel.setSize(SitoolsDesk.getDesktop().getDesktopEl().getSize());
            SitoolsDesk.getDesktop().activePanel = panel;
            panel.show();
        }, 
        closeWin : function (cMenu, e, win) {
            var tb = getDesktop().taskbar;
            if (getDesktop().activePanel == win) {
                SitoolsDesk.removeActivePanel();
            }
            var btn = win.taskButton;
            var btnToActive = tb.getPreviousBtn(btn) || tb.getNextBtn(btn);
            if (btnToActive) {
                SitoolsDesk.navProfile.taskbar.handleTaskButton.call(this, btnToActive);
            }
            
            win.destroy();
            tb.removeTaskButton(btn);
        }, 
        beforeShowCtxMenu : function () {
            return true;

        }, 


        initTaskbar : function () {
            SitoolsDesk.getDesktop().taskbar.setEnableWarning(true); 
            var removeActivePanel = new Ext.Button({
                scope : this, 
                handler : function () {
                    SitoolsDesk.removeAllWindows(false);
                },
                scale : "medium", 
                icon : "/sitools/common/res/images/taskbar/black/close-icon.png", 
                iconCls : 'taskbarButtons-icon',
                tooltip : {
                    html : i18n.get('label.removeAllPanels'), 
                    anchor : 'bottom', 
                    trackMouse : false
                }, 
                template : new Ext.Template('<table cellspacing="0" style="padding-right:0px;" class="x-btn {3}"><tbody><tr>',
                        '<td><i>&#160;</i></td>',
                        '<td><em class="{5} unselectable="on">',
                        '<button type="{1}" style="height:28px; width:28px;">{0}</button>', '</em></td>',
                        '<td><i>&#160;</i></td>', "</tr></tbody></table>")

            });
            var previous = new Ext.Button({
                scope : this, 
                handler : function () {
                    var tb = SitoolsDesk.getDesktop().taskbar;
                    var activeBtn = tb.getActiveButton();
                    var btnToActive = tb.getPreviousBtn(activeBtn);
                    if (btnToActive) {
                        SitoolsDesk.navProfile.taskbar.handleTaskButton.call(this, btnToActive);
                    }
                }, 
                scale : "medium", 
                icon : "/sitools/common/res/images/taskbar/black/arrow-left-black.png", 
                iconCls : 'taskbarButtons-icon',
                tooltip : {
                    html : i18n.get('label.previous'), 
                    anchor : 'bottom', 
                    trackMouse : false
                }, 
                template : new Ext.Template('<table cellspacing="0" style="padding-right:0px;" class="x-btn {3}"><tbody><tr>',
                        '<td><i>&#160;</i></td>',
                        '<td><em class="{5} unselectable="on">',
                        '<button type="{1}" style="height:28px; width:28px;">{0}</button>', '</em></td>',
                        '<td><i>&#160;</i></td>', "</tr></tbody></table>")

            });
            var next = new Ext.Button({
                scope : this, 
                handler : function () {
                    var tb = SitoolsDesk.getDesktop().taskbar;
                    var activeBtn = tb.getActiveButton();
                    var btnToActive = tb.getNextBtn(activeBtn);
                    if (btnToActive) {
                        SitoolsDesk.navProfile.taskbar.handleTaskButton.call(this, btnToActive);
                    }
                }, 
                scale : "medium", 
                icon : "/sitools/common/res/images/taskbar/black/arrow-right-black.png", 
                iconCls : 'taskbarButtons-icon',
                tooltip : {
                    html : i18n.get('label.next'), 
                    anchor : 'bottom', 
                    trackMouse : false
                }, 
                template : new Ext.Template('<table cellspacing="0" style="padding-right:0px;" class="x-btn {3}"><tbody><tr>',
                        '<td><i>&#160;</i></td>',
                        '<td><em class="{5} unselectable="on">',
                        '<button type="{1}" style="height:28px; width:28px;">{0}</button>', '</em></td>',
                        '<td><i>&#160;</i></td>', "</tr></tbody></table>")

            });

            SitoolsDesk.getDesktop().taskbar.staticButtonPanel.addStaticButton(previous);
            SitoolsDesk.getDesktop().taskbar.staticButtonPanel.addStaticButton(next);
            SitoolsDesk.getDesktop().taskbar.staticButtonPanel.addStaticButton(removeActivePanel);
        }
    },
    
    /**
     * Add home and remove panel button to navbar in fixed mode
     */
    initNavbar : function () {
        return;
    },
    
    /**
     * @return the specific JS View to display form
     */
    getFormOpenMode : function (){
        return sitools.user.component.DatasetOverview;
    },
    
    /**
     * @return the specific JS View to display dataset
     */
    getDatasetOpenMode : function (dataset){
        return sitools.user.component.DatasetOverview;
    },
    
    /**
     * @param componentCfg
     *      the component to add the property
     * @param dataset
     *      the object which contains properties to add
     * @return the component with the new properties
     */
    addSpecificFormParameters : function (componentCfg, dataset){
        componentCfg.sitoolsAttachementForUsers = dataset.sitoolsAttachementForUsers;
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
       return columnsModel;
    },
    
    /**
     * Add icon form to datasetView Album depending to navigation mode
     * 
     * @param value
     * @return nu
     */
    manageDatasetViewAlbumIconForm : function (value){
        return null;
        return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
        + "\", \"forms\"); return false;'><img src='" + loadUrl.get('APP_URL')
        + "/common/res/images/icons/form_list_small.png'></a>";
    },
    
    /**
     * Add icon definition to dataset Explorer depending to navigation mode
     * @param commonTreeUtils
     */
    manageDatasetExplorerShowDefinitionAndForms : function (commonTreeUtils, node, dataset){
        return;
    }, 
    /**
     * Called when desktop is saved. 
     * Add the active panel id to the desktopSettings 
     * @param forPublicUser
     * @returns {Array} an array containing the activePanelId. 
     */
    getDesktopSettings : function (forPublicUser) {
        var windowSettings = {}, activePanel = getDesktop().activePanel;
        if (activePanel) {
            if (activePanel.specificType === 'module') {
                windowSettings = {
                    activeModuleId : activePanel.id,
                    specificType : 'module'
                };
            } else {
                //get the component, first item of the activePanel
                var component = activePanel.getComponent(0);                    
                componentSettings = component._getSettings();
                windowSettings = {
                    componentSettings : componentSettings,
                    specificType : 'component',
                    type : activePanel.type
                };
                                
            }
        }
        return windowSettings;
    }, 
    /**
     * Load the module Window corresponding to the project Preference. 
     * 1 - load the module Windows
     */
    loadPreferences : function (scope) {
        //Chargement du composant ouvert.
        var pref = projectGlobal.preferences.windowSettings; 
        if (pref.specificType == "module") {
            var module = SitoolsDesk.app.getModule(pref.activeModuleId);
            if (!Ext.isEmpty(module) && Ext.isEmpty(module.divIdToDisplay)) {
                module.openModule();
            }
        } else {
//            var type = pref.type;
            
            // En mode fixed, on n'ouvre jamais des data directement, passe toujours pas form
            
//            var type = "form";
            var type = pref.type;
            var jsObj = sitools.user.component.DatasetOverview;
            
            if (type === "form" || type === "data") {
                var componentCfg = {
                    dataUrl : pref.componentSettings.dataUrl,
                    dataset : pref.componentSettings.dataset, 
                    formId : pref.componentSettings.formId,
                    formName : pref.componentSettings.formName,
                    formParameters : pref.componentSettings.formParameters,
                    formWidth : pref.componentSettings.formWidth,
                    formHeight : pref.componentSettings.formHeight, 
                    formCss : pref.componentSettings.formCss, 
                    preferencesPath : pref.componentSettings.preferencesPath,
                    activeTab : pref.componentSettings.formsActivePanel,
                    customFormWidth : pref.componentSettings.formsPanelWidth,
                    preferencesFileName : pref.componentSettings.preferencesFileName
                };

                componentCfg.sitoolsAttachementForUsers = pref.componentSettings.sitoolsAttachementForUsers;

                var title;
                if (!Ext.isEmpty(pref.componentSettings.formId)) {
                    title = i18n.get('label.forms') + " : " + pref.componentSettings.datasetName + "." + pref.componentSettings.formName;                     
                } else {
                    title = i18n.get('label.dataTitle') + " : " + pref.componentSettings.datasetName;                    
                }                
                
                var windowSettings = {
                    datasetName : pref.componentSettings.datasetName, 
                    type : type, 
                    title :  title,
                    id : type + pref.componentSettings.datasetId, 
                    saveToolbar : true, 
                    iconCls : "form"
                };  
                SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
            }
        }
    },
    
    /**
     * Close every panel in fixed mode. 
     */
    removeAllWindows : function (quietParam) {
        var quiet = Ext.isEmpty(quietParam) ? true : quietParam;
        var btns = SitoolsDesk.getDesktop().taskbar.getAllTaskButtons();
        if (Ext.isEmpty(btns) && !quiet) {
            var tmp = new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get("label.desktopAlreadyEmpty"),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
            return;
        }
        Ext.each(btns, function (btn) {
            SitoolsDesk.navProfile.taskbar.closeWin(null, null, btn.win);
        });
    }
    
};
