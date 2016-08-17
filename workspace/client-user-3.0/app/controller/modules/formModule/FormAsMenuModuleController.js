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

Ext.namespace('sitools.user.controller.modules.formModule');

/**
 * Forms Module : Displays All Forms depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.controller.modules.formModule.FormAsMenuModuleController', {
	extend : 'sitools.user.controller.modules.formModule.FormModuleController',
    
    views : ['modules.formModule.FormAsMenuModuleView'],
    
    init : function () {
        this.control({

            "formsAsMenuModuleView menuitem[sitoolsType=datasetForm]" : {
                click : function(item) {
                    this.showDetail(item.rec);
                }
            },

            "formsAsMenuModuleView menuitem[sitoolsType=projectForm]" : {
                click : function(item) {
                    this.showDetailMultiDs(item.rec);
                }
            }
        });
    },

    _getSettings : Ext.emptyFn
    
});
