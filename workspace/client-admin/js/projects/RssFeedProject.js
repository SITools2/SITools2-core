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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.projects');

Ext.define('sitools.admin.projects.RssFeedProject', { 
	alias : 'widget.s-rssFeedProjects',
    border : false,
    layout : 'fit',
    extend : 'sitools.admin.rssFeed.RssFeedCrud',

    constructor : function () {
        Ext.apply(this, {
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL'),
            label : i18n.get("label.selectProject"),
            urlRef : loadUrl.get('APP_FEEDS_URL'),
            height : ADMIN_PANEL_HEIGHT,
            id : ID.BOX.RSSPROJECT
        });
        
        this.callParent(arguments);
    }

});


