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
Ext.namespace('sitools.admin.quickstart');

Ext.define('sitools.admin.quickstart.QsDatasource', {
    extend: 'Ext.panel.Panel',
    widget: 'widget.qsDatasource',

    forceLayout: true,
    layout: {
        type: "vbox",
        align: 'center',
        pack: 'start'
    },
    border: false,
    bodyCls: 'quickStart',

    initComponent: function () {

        var title = Ext.create('Ext.form.Label', {
            cls: 'qs-h1',
            html: i18n.get('label.qsDatasourceTitle')
        });

        var desc = Ext.create('Ext.form.Label', {
            cls: 'qs-div',
            id: "start-desc",
            html: i18n.get('label.qsDatasourceDesc')
        });

        var img = Ext.create('Ext.form.Label', {
            html: '<img id="qs-datasource" class="qs-image" src="/sitools/client-admin/res/html/quickStart/screenshots/datasources.png"/>',
            listeners: {
                scope: this,
                afterrender: function (img) {
                    Ext.get("qs-datasource").on('load', function () {

                        img.getEl().alignTo("start-desc", "tl-bl");

                        var imgProjet = Ext.create('Ext.form.Label', {
                            html: '<img id="qs-datasource-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/database-logo.png"/>',
                            listeners: {
                                scope: this,
                                afterrender: function (imgProjet) {
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

                                        imgProjet.getEl().alignTo("qs-datasource", "br-br", [82, 75]);
                                    }, this);

                                    //new Ext.ToolTip({
                                    //    target: 'qs-datasource-logo',
                                    //    anchor: 'left',
                                    //    autoShow: true,
                                    //    showDelay: 0,
                                    //    html: "<b>Open Datasource</b>"
                                    //});

                                }
                            }
                        });
                        this.add(imgProjet);

                    }, this);
                }
            }
        });

        this.items = [title, desc, img];
        this.callParent(arguments);
    }
});

