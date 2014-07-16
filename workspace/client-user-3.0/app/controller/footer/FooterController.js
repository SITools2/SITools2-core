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

/**
 * Populate the div x-headers of the sitools Desktop.
 * 
 * @cfg {String} htmlContent html content of the headers,
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define("sitools.user.controller.footer.FooterController", {

    extend : 'Ext.app.Controller',

    views : ['footer.Footer'],
    

    heightNormalMode : 0,
    heightMaximizeDesktopMode : 0,
    
    statics : {
        showFooterLink : function (url, linkName) {
//            var windowConfig = {
//                title : i18n.get(linkName),
//                id : linkName,
//                iconCls : "version"
//            };
//
//            var jsObj = Ext.ux.ManagedIFrame.Panel;
//            var componentCfg = {
//                defaults : {
//                    padding : 10
//                },
//                layout : 'fit',
//                region : 'center',
//                defaultSrc : url
//            };
//
//            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
            
            alert("todo open window with link " + url + " with name : " + linkName);
        }
    },

    init : function () {
        
        this.getApplication().on('projectLoaded', this.onProjectLoaded, this);

        Ext.create('Ext.data.Store', {
            fields : [ 'name', 'url' ],
            storeId : 'linkStore'
        });
        
        this.control({
            'container#panelLeft' : {
                afterrender : function (panel) {
                    Ext.get("sitools_logo").on('load', function () {
                        Ext.get("sitools_logo").alignTo(panel.getEl(), "c-c", [ -60, 2 ]);
                    }, this);
                }
            },
            'panel#footer' : {
                afterrender : function (me) {
                    if (!me.defaultBottom) {
                        me.setHeight(0);
                    } else {
                        Ext.Ajax.request({
                            url : this.versionUrl,
                            method : 'GET',
                            scope : this,
                            success : function (ret) {
                                var json = Ext.decode(ret.responseText);
                                if (!json.success) {
                                    Ext.Msg.alert(i18n.get('label.warning'), json.message);
                                    return false;
                                }
                                var info = json.info;

                                var copyright = info.copyright;

                                me.down('label#credits').setText(Ext.String.format(i18n.get("label.build_by_sitools2"), copyright), false);

                                var bottomEl = Ext.get(me.renderTo);
                                me.setHeight(bottomEl.getHeight());
                                me.heightNormalMode = bottomEl.getHeight();
                                

                            }
                        });
                    }

                },
                resize : function (me) {
                    if (!me.defaultBottom) {
                        me.setHeight(0);
                    } else {
                        me.setSize(Ext.get(me.renderTo).getSize());
                        Ext.get("sitools_logo").alignTo(me.down('container#panelLeft').getEl(), "c-c");
                        me.down("panel#sitools_build_by").alignTo(me.down('panel#panelMiddle').getEl(), "c-c");

                        var fr = Ext.get("sitools_footer_right");
                        if (Ext.isDefined(fr) && !Ext.isEmpty(fr)) {
                            fr.alignTo(me.down('panel#panelRight').getEl(), "c-c");
                        }
                    }
                },
                maximizeDesktop : this.onMaximizeDesktop,
                minimizeDesktop : this.onMinimizeDesktop
            }
        });
        
        
        this.callParent(arguments);
    },
    onProjectLoaded : function () {
        this.fillLinks();
        this.versionUrl = loadUrl.get('APP_URL') + '/version';
        Ext.create('sitools.user.view.footer.Footer', {
        });
       
    },
    onMaximizeDesktop : function () {
        this.container.setHeight(0);
        this.hide();
    },
    onMinimizeDesktop : function () {
        this.container.dom.style.height = "";
        // this.setSize(SitoolsDesk.getBottomEl().getSize());
        this.show();
    },
    fillLinks : function () {
        var project = Ext.getStore('ProjectStore').getAt(0);
        var projectLinks = project.links();
        var linkStore = Ext.getStore('linkStore');
        projectLinks.each(function (link) {
            linkStore.add(link);
        }, this);
    },
});