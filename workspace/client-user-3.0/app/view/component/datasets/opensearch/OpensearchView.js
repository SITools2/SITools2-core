/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Service used to build a window to define filter on a store using Filter API.  
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.opensearch.OpensearchView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.opensearchView',
    border : false,
    bodyBorder : false,
    
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    
    config : {
        uri : null,
        uriSuggest : null
    },
    /**
     * @param
     * dataUrl the url of the root container
     * datasetName the name of the dataset
     * datasetId the id of the dataset
     */
    initComponent : function () {

        this.setUri(this.dataUrl + "/opensearch/search");
        this.setUriSuggest(this.dataUrl + "/opensearch/suggest");

        var ds = Ext.create('Ext.data.JsonStore', {
            proxy : {
                url : this.getUriSuggest(),
                type  :'ajax',
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
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

        var search = Ext.create('Ext.form.field.ComboBox', {
            store : ds,
            displayField : 'name',
            typeAhead : false,
            hideTrigger : true,
            name : 'searchQuery',
            anchor : "90%",
            minChars : 2,
            queryParam : 'q',
            enableKeyEvents : true,
            scope : this,
            flex: 1,
            listConfig : {
                itemSelector : 'div.search-item',
                loadingText : i18n.get("label.searching"),
                getInnerTpl: function() {
                    return '<a class="search-item"><h3>{name}<span> ({field} / {nb} results ) </span></h3></a>'
                }
            }
        });

        var link = Ext.create('Ext.button.Button', {
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/help.png',
            itemId : 'help',
            width : 25
        });

        var field = Ext.create('Ext.form.FieldContainer', {
            fieldLabel : i18n.get("label.search"),
            anchor : '100%',
            defaults : {
                margin : 2
            },
            layout : {
                type : 'hbox',
                align : 'stretch',
                pack : 'start'
            },
            items : [ search, link ]
        });

        var items = field;

        var buttonForm = [{
            text : i18n.get("label.search"),
            itemId : 'search'
        }];

        var formPanel = Ext.create('Ext.form.Panel', {
            labelWidth : 75, // label settings here cascade unless
            height : 75,
            defaultType : 'textfield',
            items : items,
            border : false,
            bodyBorder : false,
            padding : 5,
            buttons : {
                xtype : 'toolbar',
                style : 'background-color:white;',
                items : buttonForm
            }
        });

        var result = Ext.create('sitools.user.view.component.datasets.opensearch.OpensearchResultFeedView', {
            input : search,
            dataUrl : this.dataUrl,
            urlFeed : this.dataUrl + "/clientFeeds/" + this.datasetId,
            pagging : true,
            datasetName : this.datasetName,
            datasetId : this.datasetId,
            padding : 10,
            exceptionHttpHandler : function (proxy, response, operation, args) {
                // si on a un cookie de session et une erreur 403
                if ((response.status == 403) && !Ext.isEmpty(Ext.util.Cookies.get('hashCode'))) {
                    Ext.MessageBox.minWidth = 360;
                    Ext.MessageBox.alert(i18n.get('label.session.expired'), response.responseText);
                    return false;
                }
                return true;
            }
        });

        this.items = [ formPanel , result];
    	this.callParent(arguments);
    }
    
});
