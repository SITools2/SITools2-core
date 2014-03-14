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
/*global Ext, i18n, sitools, formPanel, result*/
/*
 * @include "../../components/viewDataDetail/simpleViewDataDetails.js"
 */

Ext.namespace('sitools.component.users.portal');
/**
 * @cfg {string} dataUrl the uri of the opensearch
 * @cfg {boolean} suggest true to activate autosuggest, false otherwise
 * @cfg {boolean} pagging true to activate the pagging, false otherwise
 * @class sitools.component.users.portal.portalOpensearch
 * @requires sitools.user.component.simpleViewDataDetail
 * @extends Ext.Panel
 * @requires sitools.user.component.openSearchResultFeed
 */
sitools.component.users.portal.portalOpensearch = function (config) {

    // set the uri for the opensearch engine
    // exemple de requete avec pagination
    // http://localhost:8182/sitools/solr/db?q=fu*&start=10&rows=20
    var uri = config.dataUrl + "/opensearch/search";
    var uriSuggest = config.dataUrl + "/opensearch/suggest";
    var suggest = true;
    if (!Ext.isEmpty(config.suggest)) {
        suggest = config.suggest;
    }

    var pagging = true;
    if (!Ext.isEmpty(config.pagging)) {
        pagging = config.pagging;
    }

    /**
     * click handler for the search button gets the search query and update the
     * RSS feed URI to display the results
     */
    function _clickOnSearch() {
        // create the opensearch url
        var searchQuery = formPanel.getForm().getValues().searchQuery;
        var nbResults = formPanel.getForm().getValues().nbResults;
        result.updateStore(uri + "?q=" + searchQuery + "&nbResults=" + nbResults);
    }

   /* var search = new Ext.form.TextField({
        fieldLabel : i18n.get("label.search"),
        name : 'searchQuery',
        anchor : "100%",
        listeners : {
            scope : this,
            specialkey : function (field, e) {
                if (e.getKey() == e.ENTER) {
                    _clickOnSearch();
                }
            }
        }
    });*/
    
    var ds = new Ext.data.JsonStore({
        url : uriSuggest,
        restful : true,
        root : 'data',
        fields : [ {
            name : 'field',
            type : 'string'
        }, {
            name : 'name',
            type : 'string'
        }, {
            name : 'nb',
            type : 'string'
        } ]
    });
    
    
    // Custom rendering Template
    var resultTpl = new Ext.XTemplate('<tpl for="."><div class="search-item">', '<h3>{name}<span> ({field} / {nb} results ) </span></h3>', '</div></tpl>');

    var search = new Ext.form.ComboBox({
        store : ds,
        displayField : 'name',
        typeAhead : false,
        loadingText : i18n.get("label.searching"),
        hideTrigger : true,
        fieldLabel : i18n.get("label.search"),
        name : 'searchQuery',
        anchor : "100%",
        tpl : resultTpl,
        itemSelector : 'div.search-item',
        minChars : 2,
        queryParam : 'q',
        enableKeyEvents : true,
        scope : this,
        listeners : {
            scope : this,
            beforequery : function (queryEvent) {
                if (queryEvent.query.indexOf(" ") == -1) {
                    return true;
                } else {
                    return false;
                }
            },
            specialkey : function (field, e) {
                if (e.getKey() == e.ENTER) {
                    _clickOnSearch();
                }
            },
            beforeselect : function (self, record, index) {
                record.data.name = record.data.field + ":" + record.data.name;
                return true;
            }

        }

    });
    
    
    this.storeCb = new Ext.data.ArrayStore({
        id: 0,
        fields: [            
            'nbResults'
        ],
        data: [[10], [20], [30], [40], [50]]
    });
    
 // create the combo instance
    var combo = new Ext.form.ComboBox({
        name: "nbResults",
        typeAhead: true,
        triggerAction: 'all',
        lazyRender: true,
        mode: 'local',
        store: this.storeCb,
        valueField: 'nbResults',
        displayField: 'nbResults',
        fieldLabel: i18n.get("label.nbResults"),
        value: this.storeCb.getAt(0).get("nbResults")
    });



    // set the items of the form
    var items = [ search, combo ];

    // set the search button
    var buttonForm = [ {
        text : i18n.get("label.search"),
        scope : this,
        handler : _clickOnSearch
    } ];

    // set the search form
    var formPanel = new Ext.FormPanel({
        labelWidth : 75, // label settings here cascade unless overridden
        height : 90,
        frame : true,
        defaultType : 'textfield',
        items : items,
        buttons : buttonForm

    });

    function clickOnRow(self, rowIndex, e) {
        var rec = self.store.getAt(rowIndex);
        var guid = rec.get("guid");
        if (Ext.isEmpty(guid)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noGuidFieldDefined') + "<br/>" + i18n.get('warning.noPrimaryKeyDefinedOSNotice'));
            return;
        }
        var component = new sitools.user.component.simpleViewDataDetail({
            urlDataDetail : guid
        });
        var win = new Ext.Window({
            stateful : false,
            title : i18n.get('label.viewDataDetail'),
            width : 400,
            height : 600,
            shim : false,
            animCollapse : false,
            constrainHeader : true,
            layout : 'fit'
        });
        win.add(component);
        win.show();
    }


    // instanciate the RSS feed component
    var result = new sitools.user.component.openSearchResultFeed({
        input : search,
        dataUrl : config.dataUrl,
        pagging : false,
        listeners : {
            rowdblclick : clickOnRow
        }
    });

    // instanciate the panel component
    sitools.component.users.portal.portalOpensearch.superclass.constructor.call(this, Ext.apply({
        items : [ formPanel, result ],
        layout : 'vbox',
        layoutConfig : {
            align : 'stretch',
            pack : 'start'
        }

    }, config));

};

Ext.extend(sitools.component.users.portal.portalOpensearch, Ext.Panel, {});

Ext.reg('sitools.component.users.portal.portalOpensearch', sitools.component.users.portal.portalOpensearch);
