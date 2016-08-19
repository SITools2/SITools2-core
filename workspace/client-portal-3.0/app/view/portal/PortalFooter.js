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

Ext.namespace('sitools.clientportal.view.portal');

Ext.define('sitools.clientportal.view.portal.PortalFooter', {
    extend : 'Ext.panel.Panel',
    region: 'center',
    height: 70,
    bodyStyle: "background-image:url(/sitools/client-portal/resources/images/footer.png) !important",
    border: false,
    
    initComponent : function () {
    	
    	this.items = [{
            xtype: 'toolbar',
            cls: 'bg-transparent-3',
            items: [{
                xtype: 'button',
                id: 'footerContactBtn',
                text: i18n.get('label.contact'),
                iconCls: 'contactIcon',
                textAlign: 'left',
                handler: function() {
                    Ext.create('sitools.clientportal.view.portal.PortalContact', {});
                }
            }, {
                xtype: 'button',
                text: i18n.get("label.links"),
                icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/logo_fav_icone.png',
                textAlign: 'left',
                handler: function (button, event) {
                    Ext.util.openLink('/sitools/client-portal/resources/html/fr/link.html');
                }
            }, {
                xtype: 'button',
                text: i18n.get("label.help"),
                icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/wadl.png',
                textAlign: 'left',
                handler: function (button, event) {
                    Ext.util.openLink('/sitools/client-portal/resources/html/fr/help.html');
                }
            }]
        }];
    	
    	this.callParent(arguments);
    }
});

