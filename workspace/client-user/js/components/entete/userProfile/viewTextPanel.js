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
/*global Ext, sitools, i18n, SitoolsDesk, alertFailure, window, loadUrl, DEFAULT_WIN_WIDTH, DEFAULT_WIN_HEIGHT */

Ext.namespace('sitools.user.component.entete.viewTextPanel');

/**
 * A simple panel that displays text
 * 
 * @cfg {String} text The text to display 
 * @cfg {Boolean} formatJson true to format the text as json, false otherwise
 * @class sitools.user.component.entete.userProfile.viewTextPanel
 * @extends Ext.Panel
 */
sitools.user.component.entete.userProfile.viewTextPanel = Ext.extend(Ext.Panel, {
    layout : 'fit', 
    autoScroll : true,
    initComponent : function () {
        if (this.formatJson) {
            try {
                if (Ext.isFunction(JSON.parse) && Ext.isFunction(JSON.stringify)) {
                    var obj = JSON.parse(this.text);
                    this.html = JSON.stringify(obj, null, 4);
                    this.style = "white-space: pre";
                }
            } catch (err) {
                this.html = this.text;
            }
        }
        else {
            if (this.isOpenable(this.url)) {
                this.items = [{
                    xtype : 'textarea',
                    readOnly : true,
                    value : this.text
                    
                }];
            } else {
                this.html = this.text;            
            }
            
        }
        
        sitools.user.component.entete.userProfile.viewTextPanel.superclass.initComponent.call(this);
    },
    
    isOpenable : function (text) {
        var textRegex = /\.(txt|json|css|xml)$/;
        return text.match(textRegex);            
    },

    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }
});
