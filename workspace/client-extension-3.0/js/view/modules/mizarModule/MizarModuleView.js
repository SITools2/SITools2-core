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


Ext.namespace('sitools.extension.view.modules.mizarModule');


Ext.define('sitools.extension.view.modules.mizarModule.MizarModuleView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mizarModuleView',
    layout: 'fit',
    requires: ['sitools.extension.model.MizarModuleModel'],

    initComponent: function () {

        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
                case "startingLong" :
                    this.startingLong = config.get('value');
                    break;
                case "startingLat" :
                    this.startingLat = config.get('value');
                    break;
                case "startingZoom" :
                    this.startingZoom = config.get('value');
                    break;
                case "angleDistance" :
                    this.angleDistance = config.get('value');
                    break;
                case "samp" :
                    this.samp = config.get('value');
                    break;
                case "shortenerUrl" :
                    this.shortenerUrl = config.get('value');
                    break;
                case "2dMap" :
                    this.twoDMap = config.get('value');
                    break;
                case "reverseNameResolver" :
                    this.reverseNameResolver = config.get('value');
                    break;
                case "nameResolver" :
                    this.nameResolver = config.get('value');
                    break;
                case "category" :
                    this.category = config.get('value');
                    break;
                case "compass" :
                    this.compass = config.get('value');
                    break;
                case "showCredits" :
                    this.showCredits = config.get('value');
                    break;
                case "imageViewer" :
                    this.imageViewer = config.get('value');
                    break;
            }
        }, this);

        this.listeners = {

        }

        this.items = [{
            layout: 'fit',
            region: 'center',
            id: "mizarModule",
            autoEl: {
                tag: 'div'
            },
            xtype: 'box'
        }
        ];

        this.callParent(arguments);

    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id,
            xtype: this.$className
        };
    }

});

