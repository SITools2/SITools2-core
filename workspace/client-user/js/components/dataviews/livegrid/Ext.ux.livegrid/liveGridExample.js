/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('Ext.ux');

Ext.ux.Livegrid = Ext.extend(Ext.ux.grid.livegrid.GridPanel, {

    initComponent : function()
    {
        /**
         * BufferedJsonReader derives from Ext.data.JsonReader and allows to pass
         * a version value representing the current state of the underlying data
         * repository.
         * Version handling on server side is totally up to the user. The version
         * property should change whenever a record gets added or deleted on the server
         * side, so the store can be notified of changes between the previous and current
         * request. If the store notices a version change, it will fire the version change
         * event. Speaking of data integrity: If there are any selections pending,
         * the user can react to this event and cancel all pending selections.
         */
        var bufferedReader = new Ext.ux.grid.livegrid.JsonReader({
            root            : 'response.value.items',
            versionProperty : 'response.value.version',
            totalProperty   : 'response.value.total_count',
            id              : 'id'
          }, [ {
             name : 'number_field', sortType : 'int'
          },{
             name : 'string_field', sortType : 'string'
          },{
             name : 'date_field',   sortType : 'int'
        }]);

        /**
         * Set up your store.
         * An instance of BufferedJsonReader is needed if you want to listen to
         * <tt>versionchange</tt> events.
         * Make sure you set the config option bufferSize high enough
         * (something between 100 and 300  works good).
         */
        this.store = new Ext.ux.grid.livegrid.Store({
            autoLoad   : true,
            bufferSize : 300,
            reader     : bufferedReader,
            sortInfo   : {field: 'number_field', direction: 'ASC'},
            url        : 'data-proxy.php'
        });

        /**
         * BufferedRowSelectionModel introduces a different selection model and a
         * new <tt>selectiondirty</tt> event.
         * You can keep selections between <b>all</bb> ranges in the grid; records which
         * are currently in the buffer and are selected will be added to the selection
         * model as usual. Rows representing records <b>not</b> loaded in the current
         * buffer will be marked using a predictive index when selected.
         * Selected rows will be successively read into the selection store
         * upon scrolling through the view. However, if any records get added or removed,
         * and selection ranges are pending, the selectiondirty event will be triggered.
         * It is up to the user to either clear the pending selections or continue
         * with requesting the pending selection records from the data repository.
         * To put the whole matter in a nutshell: Selected rows which represent records
         * <b>not</b> in the current data store will be identified by their assumed
         * index in the data repository, and <b>not</b> by their id property.
         * Events such as <tt>versionchange</tt> or <tt>selectiondirty</tt>
         * can help in telling if their positions in the data repository changed.
         */
        this.selModel = new Ext.ux.grid.livegrid.RowSelectionModel();

        /**
         * Here is where the magic happens: BufferedGridView. The nearLimit
         * is a parameter for the predictive fetch algorithm within the view.
         * If your bufferSize is small, set this to a value around a third or a quarter
         * of the store's bufferSize (e.g. a value of 25 for a bufferSize of 100;
         * a value of 100 for a bufferSize of 300).
         * The loadMask is optional but should be used to provide some visual feedback
         * for the user when the store buffers (the loadMask from the GridPanel
         * will only be used for initial loading, sorting and reloading).
         */
        this.view = new Ext.ux.grid.livegrid.GridView({
            nearLimit      : 100,
            loadMask : {
                msg : 'Please wait...'
            }
        });

        /**
         * You can use an instance of BufferedGridToolbar for keeping track of the
         * current scroll position. It also gives you a refresh button and a loading
         * image that gets activated when the store buffers.
         * ...Yeah, I pretty much stole this one from the PagingToolbar!
         */
        this.bbar = new Ext.ux.grid.livegrid.Toolbar({
            view        : this.view,
            displayInfo : true
        });

        Ext.ux.Livegrid.superclass.initComponent.call(this);
    }

});

    function showMe()
    {
        var grid = new Ext.ux.Livegrid({
            enableDragDrop : false,
            cm             : new Ext.grid.ColumnModel([
                new Ext.grid.RowNumberer({header : '#' }),
                {header: "Number", align : 'left',   width: 160, sortable: true, dataIndex: 'number_field'},
                {header: "String", align : 'left',   width: 160, sortable: true, dataIndex: 'string_field'},
                {header: "Date",   align : 'right',  width: 160, sortable: true, dataIndex: 'date_field'}
            ]),
            loadMask       : {
                msg : 'Loading...'
            },
            title          : 'Large table'
        });

        var w = new Ext.Window({
            title       : 'Ext.ux.Livegrid',
            maximizable : true,
            renderTo    : 'content',
            resizable   : true,
            layout      : 'fit',
            items       : [grid],
            width       : 500,
            height      : 400,
            tbar        : new Ext.Toolbar({
                items : [
                    new Ext.Button({
                        text : 'Button 2'
                    })
                ]
            })
        });

        w.show();
    }
