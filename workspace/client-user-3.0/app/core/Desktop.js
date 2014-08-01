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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, Desktop*/

Ext.namespace('sitools.user.core');
/**
 * Core class containing all Sitools proper informations
 */
Ext.define('sitools.user.core.Desktop', {
	singleton : true,

	config : {
		activePanel : null,
		modulesInDiv : [],
		mainDesktop : Ext.get("x-main"),
		
		desktopEl : Ext.get('x-desktop'),
		desktopAndTaskBarEl : Ext.get('x-desktop-taskbar'),
		
		enteteEl : Ext.get('x-headers'),
		enteteComp : Ext.getCmp('headersCompId'),
		
		bottomEl : Ext.get('x-bottom'),
		bottomComp : Ext.getCmp('bottomCompId'),
		
		taskbarEl : Ext.get('ux-taskbar'),
		
		shortcutsEl : Ext.get('x-shortcuts'),
		
		desktopSize : 'minimize'
	}

});

Desktop = sitools.user.core.Desktop;