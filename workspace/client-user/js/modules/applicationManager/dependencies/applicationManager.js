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
/*global Ext, SitoolsDesk, i18n, sitools */

Ext.namespace('sitools.user.modules.applicationManager.dependencies');

/**
 * Desktop Manager grid panel.
 * @class sitools.user.modules.applicationManager.dependencies.applicationManager
 * @extends Ext.grid.GridPanel
 * @author a.labeau
 */
sitools.user.modules.applicationManager.dependencies.applicationManager = function (config) {
    /*
     * Attributs prives
     */
    var idApp = 0;
    var Application = Ext.data.Record.create([ {
        name : 'id',
        type : 'string'
    }, {
        name : 'name',
        type : 'string'
    }, {
        name : 'author',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'url',
        type : 'string'
    }, {
        name : 'active',
        type : 'bool'
    } ]);

    /*
     * Methodes privees
     */

    var genData = function () {
        var data = [];
        var modulesExistants = [];
        modulesExistants = SitoolsDesk.app.getModules();
        // s'il existe des modules
        // on charge les donnees dans le store
        if (modulesExistants) {
            Ext.each(modulesExistants, function (m) {
                data.push({
                    id : m.id,
                    name : m.name,
                    author : m.author,
                    description : m.description,
                    url : m.url,
                    active : true
                });
            });
        }
        return data;
    };

    var gstore = new Ext.data.GroupingStore({
        reader : new Ext.data.JsonReader({
            fields : Application
        }),
        data : genData()
    });

    var view = new Ext.grid.GroupingView({
        markDirty : false
    });
    var selectionModel = this.getSelectionModel();

    var addAppHandler = function (recordSld) {
        // Gestion des identifiants a voir
        // var newID = recordSld.data.id+idApp
        var newId = recordSld.get('id');
        if (gstore.find('id', newId) > -1) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.modulePresent'));
            return false;
        } else {
            var e = new Application({
                id : recordSld.data.id,
                name : recordSld.data.name,
                author : recordSld.data.author,
                description : recordSld.data.description,
                url : recordSld.data.url,
                active : true
            });
            // editor.stopEditing();
            gstore.insert(0, e);
            view.refresh();
            this.getSelectionModel().selectRow(0);
            // editor.startEditing(0);
            idApp += 1;

            return true;

        }
    };

    var delAppHandler = function () {
//        var s = selectionModel.getSelections();
//        var i, r;
//        for (i = 0; i < s.size(); i++) {
//            r = s[i];
//            gstore.remove(r);
//        }
//        view.refresh();
        var r = this.getSelectionModel().getSelected();
        gstore.remove(r);
        view.refresh();
    };

    var columns = [ new Ext.grid.RowNumberer(),
    {
        header : i18n.get('label.name'),
        dataIndex : 'name',
        width : 150,
        sortable : true,
        editor : {
            xtype : 'textfield',
            allowBlank : false
        }
    }, {
        header : i18n.get('label.author'),
        dataIndex : 'author',
        width : 150,
        sortable : true,
        editor : {
            xtype : 'textfield',
            allowBlank : false
        }
    }, {
        header : i18n.get('label.description'),
        dataIndex : 'description',
        width : 150,
        sortable : true,
        editor : {
            xtype : 'textfield',
            allowBlank : true
        }
    }, {
        id : i18n.get('label.url'),
        header : 'Url',
        dataIndex : 'url',
        width : 100,
        sortable : true,
        editor : {
            xtype : 'textfield',
            allowBlank : false

        }
    } ];

    sitools.user.modules.applicationManager.dependencies.applicationManager.superclass.constructor.call(this, Ext.apply({
        store : gstore,
        // plugins : [editor],
        view : view,
        // autoExpandColumn: 'url',
        columns : columns,
        // on rend visible les attributs et methodes suivantes
        addAppHandler : addAppHandler,
        delAppHandler : delAppHandler,
        
        sm : new Ext.grid.RowSelectionModel({
            singleSelect : true
        })

    }, config));
};

Ext.extend(sitools.user.modules.applicationManager.dependencies.applicationManager, Ext.grid.GridPanel, {});

Ext.reg('sitools.user.modules.applicationManager.dependencies.applicationManager', sitools.user.modules.applicationManager.dependencies.applicationManager);
