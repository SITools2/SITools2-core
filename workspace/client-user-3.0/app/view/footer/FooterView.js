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
/*global Ext, sitools, window, showVersion, publicStorage, userLogin, projectGlobal, SitoolsDesk, showResponse, i18n, extColModelToJsonColModel, loadUrl*/

/**
 * @cfg {Array} modules la liste des modules
 * @class sitools.user.component.entete.NavBar
 * @extends Ext.Toolbar
 */

Ext.define('sitools.user.view.footer.FooterView', {
    extend : 'Ext.panel.Panel',
    itemId : 'footer',
    alias :'widget.footerView',

    heightNormalMode : 0,
    heightMaximizeDesktopMode : 0,
    border : false,
    bodyBorder : false,
    layout : {
        type : "hbox",
        align : 'stretch',
        pack : 'start'
    },
    bodyCls : 'sitools_footer',

    initComponent : function () {

        this.defaultBottom = Desktop.getBottomEl().dom.children.length === 0;

        if (this.defaultBottom) {

            Ext.apply(this, {
                renderTo : 'x-bottom',
                items : [
                        {
                            xtype : 'panel',
                            itemId : 'leftPanel',
                            border : false,
                            flex : 0.5,
                            html : "<img id='sitools_logo' src='" + loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + "/res/images/logo_01_petiteTaille.png' alt='sitools_logo'/>",
                            bodyCls : 'no-background'
                        },
                        {
                            xtype : 'panel',
                            itemId : 'middlePanel',
                            border : false,
                            flex : 1,
                            bodyCls : 'no-background',
                            items : [ {
                                xtype : "panel",
                                itemId : 'sitools_build_by',
                                cls : "sitools_footer_build_by",
                                bodyCls : 'no-background',
                                border : false,
                                items : [ {
                                    itemId : 'credits',
                                    xtype : 'label',
                                    style : 'color:white',
                                    border : false
                                } ]

                            } ]
                        },
                        {   
                            xtype : 'panel',
                            itemId: 'rightPanel',
                            border : false,
                            flex : 0.5,
                            bodyCls : 'no-background',
                            items : [ {
                                xtype : 'dataview',
                                store : Ext.getStore('linkStore'),
                                tpl : new Ext.XTemplate('<div class="sitools_footer_right" id="sitools_footer_right">', '<tpl for=".">',
                                        '<a rel="contents" href="#" onclick="sitools.user.controller.footer.FooterController.showFooterLink(\'{url}\',\'{name}\');">',
                                        '{[this.getLabel(values.name)]}', '</a>', '<tpl if="(xindex < xcount)">', ' | ', '</tpl>', '</tpl>', '</div>', {
                                            compiled : true,
                                            disableFormats : true,
                                            getLabel : function (labelName) {
                                                return i18n.get(labelName);
                                            }
                                        })
                            } ]
                        } ]

            });
        } else {
            var el = Ext.get('x-bottom').createChild({
                tag : 'div'
            });
            this.renderTo = el;
        }

        this.callParent(arguments);
    }

});
