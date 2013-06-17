/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.datasets');

/**
 * A simple panel with an {@link sitools.admin.rssFeed.rssFeedCrud} component
 * @class sitools.admin.datasets.rssFeedDatasets
 * @extends Ext.Panel
 */
sitools.admin.datasets.rssFeedDatasets = Ext.extend(Ext.Panel, {
//sitools.component.datasets.rssFeedDatasets = Ext.extend(Ext.Panel, {

    border : false,
    height : 300,
    id : ID.BOX.RSSPROJECT,
    layout : 'fit',

    initComponent : function () {
        var rssFeedCRUD = new sitools.admin.rssFeed.rssFeedCrud({
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'),
            label : i18n.get("label.selectDatasets"),
            urlRef : loadUrl.get('APP_FEEDS_URL')
        });

        this.items = [ rssFeedCRUD ];

        sitools.admin.datasets.rssFeedDatasets.superclass.initComponent.call(this);

    }

});

Ext.reg('s-rssFeedDatasets', sitools.admin.datasets.rssFeedDatasets);
