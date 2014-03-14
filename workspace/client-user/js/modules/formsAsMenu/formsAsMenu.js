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
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.modules');

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.formsAsMenu
 * @extends Ext.menu.Menu
 * @requires sitools.user.component.forms.mainContainer
 */
sitools.user.modules.formsAsMenu = function () {
    var urlFormsModule = projectGlobal.sitoolsAttachementForUsers + "/forms";
    this.storeFormDs = new Ext.data.JsonStore({
        root : 'data',
        restful : true,
        remoteSort : true,
        url : urlFormsModule,
        // sortField: 'name',
        idProperty : 'id',
        fields : [ {
            name : 'id',
            type : 'string'
        }, {
            name : 'parent',
            type : 'string'
        }, {
            name : 'name',
            type : 'string'
        }, {
            name : 'css',
            type : 'string'
        }, {
            name : 'description',
            type : 'string'
        }, {
            name : 'width',
            type : 'numeric'
        }, {
            name : 'height',
            type : 'numeric'
        }, {
            name : 'parameters'
        }, {
			name : 'authorized'	
        }, {
            name : 'parentUrl',
            type : 'string'
        }, {
            name : 'zones'
        }], 
        autoLoad : true, 
        listeners : {
			scope : this, 
			load : this.onLoadDatasetsForms,
            beforeload : function () {
                var menuItems = [{
                        text : i18n.get("label.loading"),
                        icon: '/sitools/cots/extjs/resources/images/default/grid/loading.gif'
                    }];        
		        this.addItem(menuItems);                
                
            }
           
		}
        
    });
    var urlFormsMultiDs = projectGlobal.sitoolsAttachementForUsers + "/formsProject";
    
    this.storeFormsMultiDs = new Ext.data.JsonStore({
        root : 'data',
        restful : true,
        remoteSort : true,
        url : urlFormsMultiDs,
        // sortField: 'name',
        idProperty : 'id',
        fields : [ {
            name : 'id',
            type : 'string'
        }, {
            name : 'parent',
            type : 'string'
        }, {
            name : 'name',
            type : 'string'
        }, {
            name : 'css',
            type : 'string'
        }, {
            name : 'description',
            type : 'string'
        }, {
            name : 'width',
            type : 'numeric'
        }, {
            name : 'height',
            type : 'numeric'
        }, {
            name : 'parameters'
        }, {
			name : 'authorized'	
        }, {
            name : 'parentUrl',
            type : 'string'
        }, {
            name : 'properties'
        }, {
            name : 'urlServicePropertiesSearch', 
            type : 'string'
        }, {
            name : 'urlServiceDatasetSearch', 
            type : 'string'
        }, {
            name : 'collection'
        }, {
            name : 'dictionary'
        }, {
            name : 'nbDatasetsMax', 
            type : 'numeric'
        }, {
            name : 'zones'
        }], 
        autoLoad : false, 
        listeners : {
			scope : this, 
			load : this.onLoadMultiDSForms
		}
    });

    
    sitools.user.modules.formsAsMenu.superclass.constructor.call(this, Ext.apply({
        menuMultiDsFormLoaded : false, 
        formDsLoaded : false, 
        formMultiDsLoaded : false, 
        cls : "sitools-navbar-menu"
    }));

};

Ext.extend(sitools.user.modules.formsAsMenu, Ext.menu.Menu, {
    enableScrolling : true,
    id : "formAsMenu",
	onLoadDatasetsForms : function () {
		var menuItems = [];
		
		this.storeFormDs.each(function (rec) {
			menuItems.push({
				text : rec.get("name"), 
				rec : rec, 
				scope : this, 
				handler : function (b) {
					this.showDetail(b.rec);                    
				}
			});
		}, this);

		if (menuItems.length > 0) {
		    menuItems.unshift({
		        xtype : 'label',
		        text : i18n.get('label.forms'),
		        labelStyle : 'padding-top:10px;',
		        cls : 'menu-forms-title'
		    }, '-');
//			menuItems.unshift('<span class="menu-forms-title" style="padding-top:10px;">' + i18n.get('label.forms') + '</span>', '-');
		}
		this.addItem(menuItems);
		
		this.storeFormsMultiDs.load();
	}, 
	onLoadMultiDSForms : function () {
		var menuItems = [];
		
		this.storeFormsMultiDs.each(function (rec) {
			menuItems.push({
				text : rec.get("name"), 
				rec : rec, 
				scope : this, 
				handler : function (b) {
					this.showDetailMultiDs(b.rec);                    
				}
			});
		}, this);

		if (menuItems.length > 0) {
			menuItems.unshift('<span class="menu-forms-title">' + i18n.get('label.projectForm') + '</span>', '-');
		}
		this.addItem(menuItems);
		this.formsLoaded = true;
		this.menuMultiDsFormLoaded = true;	
        
        var loadingButton = this.items.get(0);
        this.remove(loadingButton);

	}, 
	
    showDetail : function (rec) {
        if (rec.data.authorized === "false") {
			return;
        }
        Ext.Ajax.request({
            url : rec.data.parentUrl,
            method : 'GET', 
            success : function (response) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

                    var dataset = json.dataset;
                    var jsObj = SitoolsDesk.navProfile.getFormOpenMode();
                    
                    var componentCfg = {
                        dataUrl : dataset.sitoolsAttachementForUsers,
                        dataset : dataset,
                        formId : rec.data.id,
                        formName : rec.data.name,
                        formParameters : rec.data.parameters,
                        formWidth : rec.data.width,
                        formHeight : rec.data.height, 
                        formCss : rec.data.css, 
                        preferencesPath : "/" + dataset.name + "/forms", 
                        preferencesFileName : rec.data.name,
                        formZones : rec.data.zones
                    };
                    
                    SitoolsDesk.navProfile.addSpecificFormParameters(componentCfg, dataset);
                    
                    var windowSettings = {
                        datasetName : dataset.name, 
                        type : "form", 
                        title : i18n.get('label.forms') + " : " + dataset.name + "." + rec.data.name, 
                        id : "form" + dataset.id + rec.data.id, 
                        saveToolbar : true, 
                        iconCls : "form"
                    };
                    
                    SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
                    return;
                }
                catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                    return;
                }
                
            }, 
            failure : function () {
                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noActiveDatasetFound'));
                return;
            }
        });
    }, 
    showDetailMultiDs : function (rec) {
		if (Ext.isEmpty(rec)) {
			return;
		}
        var jsObj = sitools.user.component.forms.projectForm;
        var componentCfg = {
            formId : rec.data.id,
            formName : rec.data.name,
            formParameters : rec.data.parameters,
            formWidth : rec.data.width,
            formHeight : rec.data.height, 
            formCss : rec.data.css, 
            properties : rec.data.properties, 
            urlServicePropertiesSearch : rec.data.urlServicePropertiesSearch, 
            urlServiceDatasetSearch : rec.data.urlServiceDatasetSearch, 
            dictionaryName : rec.data.dictionary.name,
            nbDatasetsMax : rec.data.nbDatasetsMax, 
            preferencesPath : "/formProjects", 
            preferencesFileName : rec.data.name,
            formZones : rec.data.zones
        };
        var windowSettings = {
            type : "formProject", 
            title : i18n.get('label.forms') + " : " + rec.data.name + ", Collection " + rec.data.collection.name, 
            id : "formProject"  + rec.data.id, 
            saveToolbar : true, 
            datasetName : rec.data.name, 
            winWidth : 600, 
            winHeight : 600, 
            iconCls : "form"
        };
        
        SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
        return;
    }
});

Ext.reg('sitools.user.modules.formsAsMenu', sitools.user.modules.formsAsMenu);


sitools.user.modules.formsAsMenu.openModule = function (btn, event) {
	if (Ext.isEmpty(event)) {
		return;
	}
    var menu = Ext.getCmp("formAsMenu");
    if (Ext.isEmpty(menu)) {
	   menu = new sitools.user.modules.formsAsMenu();
    }
    if (btn.getPosition) {
    	menu.showAt([btn.getPosition()[0], SitoolsDesk.getEnteteEl().getHeight()]);
    }
    else 
    {
    	menu.showAt([0, SitoolsDesk.getEnteteEl().getHeight()]);
   	}
};

sitools.user.modules.formsAsMenu.getStaticParameters = function () {
	return {
		showAsMenu : true
	};
};