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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure*/
Ext.namespace('sitools.admin.quickStart');

Ext.define('sitools.admin.quickStart.QsDatasource', { 
    extend : 'Ext.panel.Panel',
    forceLayout : true, 
    layout : {
        type : "vbox",
        align : 'center',
        pack : 'start'
    },
    border : false, 
    bodyCls : 'quickStart',
    
    initComponent : function () {
        
        var title = new Ext.form.Label({
            cls : 'qs-h1',
            html : i18n.get('label.qsDatasourceTitle'),
            listeners : {
                render : function (img) {
                    img.getEl().fadeIn({
                        easing : 'easeIn',
                        duration: 1
                    });
                }
            }
        });
        
        var desc = new Ext.form.Label({
            cls : 'qs-div',
            id : "start-desc",
            html : i18n.get('label.qsDatasourceDesc'),
            listeners : {
                render : function (desc) {
                   desc.getEl().fadeIn({
                       easing : 'easeIn',
                       duration: 1
                   });
               }
            }
        });
        
        var img = new Ext.form.Label({
            html : '<img id="qs-datasource" class="qs-image" src="/sitools/client-admin/res/html/quickStart/screenshots/datasources.png"/>',
            tooltip : 'Open Datasource',
            listeners : {
                scope : this,
                afterrender : function (img) {
                    Ext.get("qs-datasource").on('load', function () {
                        
                        img.getEl().fadeIn({
                            opacity: 1,
                            easing : 'easeIn',
                            duration: 1,
                            useDisplay : true
                        });
                        img.getEl().alignTo("start-desc", "tl-bl");
                        
                        var imgProjet = new Ext.form.Label({
                            html : '<img id="qs-datasource-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/database-logo.png"/>',
                            listeners : {
                                scope : this,
                                render : function (imgProjet) {
                                    imgProjet.getEl().fadeIn({
                                        opacity: 1,
                                        easing : 'easeIn',
                                        duration: 1.5,
                                        useDisplay : true
                                    });
                                },
                                afterrender : function (imgProjet) {
                                    Ext.get("qs-datasource-logo").on('load', function () {
                                        
                                        imgProjet.getEl().on('mouseleave', function (e, t, o) {
                                           Ext.get(t).update("<img id='qs-projet-logo' class='bouton_action' src='/sitools/client-admin/res/html/quickStart/icons/database-logo.png'/>");
                                        });
                                        imgProjet.getEl().on('mouseenter', function (e, t, o) {
                                            Ext.get(t).update("<img id='qs-projet-logo' class='bouton_action' src='/sitools/client-admin/res/html/quickStart/icons/database-logo-hover.png'/>");
                                        });
                                        imgProjet.getEl().on('click', function (e, t, o) {
                                            this.qs.openFeature("DatabaseJDBCNodeId");
                                        }, this);
                                        
                                        imgProjet.getEl().alignTo("qs-datasource", "br-br",  [82, 75]);
                                    }, this);
                                    
                                    new Ext.ToolTip({
                                        target : 'qs-datasource-logo',
                                        anchor: 'left',
                                        autoShow : true,
                                        showDelay : 0,
                                        html : "<b>Open Datasource</b>"
                                    });
                                    
                                }
                            }
                        });
                        this.add(imgProjet);
                        this.doLayout();
                        
                    }, this);
                }
            }
        });
        
        this.items = [title, desc, img];
        
        sitools.admin.quickStart.QsDatasource.superclass.initComponent.call(this);
    }
});

