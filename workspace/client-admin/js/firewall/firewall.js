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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.component.firewall');

sitools.component.firewall.FirewallPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.FIREWALL,
    sm : new Ext.grid.RowSelectionModel({
        singleSelect : true
    }),
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        /*
         * // The new DataWriter component. var writer = new
         * Ext.data.JsonWriter({ encode: false // <-- don't return encoded JSON --
         * causes Ext.Ajax#request to send data using jsonData config rather
         * than HTTP params });
         */
        // create the restful Store
        // Method url action
        // POST /firewalls create
        // GET /firewalls read
        // PUT /firewalls/id update
        // DESTROY /firewalls/id delete
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            // url: '/admin/security/firewalls',
            url : loadUrl.get('APP_URL') + '/firewalls',
            // sortField: 'name',
            idProperty : 'name',
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'desc',
                type : 'string'
            } ]
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'desc',
                width : 200
            } ]
        });

        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.upload'),
                icon : 'res/images/icons/toolbar_firewall_add.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.config'),
                icon : 'res/images/icons/toolbar_firewall_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : 'res/images/icons/toolbar_firewall_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        sitools.component.firewall.FirewallPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.component.firewall.FirewallPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    onCreate : function () {
        return Ext.Msg.alert(i18n.get('label.information'), i18n.get('msg.notavailable'));
    },

    onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        // var up = new
        // sitools.component.firewall.FirewallPanel({url:'/admin/security/firewalls/'+rec.identifier});
        var up = new sitools.component.firewall.FirewallPanel({
            url : loadUrl.get('APP_URL') + '/firewall/' + rec.id
        });
        up.show(ID.BOX.FIREWALL);
    },

    onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }

        this.store.remove(rec);
    }

});

Ext.reg('s-firewall', sitools.component.firewall.FirewallPanel);
