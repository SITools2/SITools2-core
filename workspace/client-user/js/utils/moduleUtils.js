/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window,
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE,
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT,
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, ColumnRendererEnum, SITOOLS_DATE_FORMAT
*/
Ext.namespace('sitools.user.component.module');

/**
 * A Simple Object to publish common methods to use module in Sitools2.
 */
sitools.user.component.module.moduleUtils = {
        
        openModule : function (moduleId) {
            var module = SitoolsDesk.app.getModule(moduleId);
            if (Ext.isEmpty(module)) {
                return Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.notEnoughtRightModule'));
            }
            module.openModule();
        }
};