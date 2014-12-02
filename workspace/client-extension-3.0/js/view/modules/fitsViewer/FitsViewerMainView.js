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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin, DEFAULT_ORDER_FOLDER, userStorage*/
Ext.namespace('sitools.extension.view.modules.fitsViewer');
/**
 * Main class of Fits Explorer
 * 
 * @class sitools.extension.view.modules.fitsViewer.FitsViewerMainView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.extension.view.modules.fitsViewer.FitsViewerMainView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.fitsViewerMainView',

    itemId : 'fitsMain',
    border : false,
    layout : {
    	type : 'hbox',
    	align : 'stretch'
    },
    
    initComponent : function () {

//        this.fitsName = this.url.substring(this.url.lastIndexOf('/') + 1, this.url.lastIndexOf('.'));

    	this.i18nFitsViewer = I18nRegistry.retrieve('fitsViewer');
    	
        this.loader = new FitsLoader();

        var rootNode = {
            "text" : this.i18nFitsViewer.get("label.nothingLoaded"),
            "children" : [],
            "leaf" : true
        };

        this.treeStore =  Ext.create('Ext.data.TreeStore', {
            proxy : {
                type : 'memory'
            },
            fields : ['header', 'data', 'text', 'leaf', 'children', 'value']
        });

        this.tree = Ext.create('Ext.tree.Panel', {
            flex : 1,
            border : false,
            expanded : true,
            useArrows : true,
            autoScroll : true,
            forceFit : true,
            rowLines : true,
            containerScroll : true,
            rootVisible : true,
            root : rootNode,
            store : this.treeStore,
            listeners : {
                scope : this,
                itemclick : function (tree, node, e) {
                    if (!node.isLeaf()) {
                        return;
                    }

                    this.additionalContainer.removeAll();
                    
                    if (node.get('text') == "TABLE" || node.get('text') == "BINTABLE") {
                        this.getEl().mask(this.i18nFitsViewer.get('label.loadingFits'), "x-mask-loading");

                        Ext.defer(function () {
                            this.sitoolsFitsTable = Ext.create('sitools.extension.view.modules.fitsViewer.FitsTableView', {
                                headerData : node.get('header'),
                                data : node.get('data'),
                                fits : this.fits,
                                fitsMainPanel : this
                            });

                            this.centerPanel.removeAll(false);
                            this.centerPanel.add(this.sitoolsFitsTable);
                        }, 5, this);

                    } else if (node.get('text') == "IMAGE") {
                        this.getEl().mask(this.i18nFitsViewer.get('label.loadingFits'), "x-mask-loading");

                        Ext.defer(function () {
                            this.sitoolsFitsViewer = Ext.create('sitools.extension.view.modules.fitsViewer.FitsViewerView', {
                                urlFits : this.url,
                                headerData : node.get('header'),
                                fits : this.fits,
                                fitsArray : this.fitsArray,
                                fitsMainPanel : this
                            });
                            this.centerPanel.removeAll(false);
                            this.centerPanel.add(this.sitoolsFitsViewer);
                        }, 5, this);
                    }
                }
            }
        });

        this.pbar = Ext.create('Ext.ProgressBar', {
            bodyStyle : 'background-color:white;',
            height : 25,
            border : false,
            hidden : true,
            value : 0
        });
        
        this.additionalContainer = Ext.create('Ext.panel.Panel', {
        	height : 250,
        	layout : 'fit',
        	border : false,
        	items : []
        });

        this.treeContainer = Ext.create('Ext.panel.Panel', {
        	collapsible : true,
        	cls : 'x-panel-body-silver',
        	collapseDirection : 'left',
        	layout: {
        	    type: 'vbox',
        	    align : 'stretch'
        	},
            width : 300,
            items : [this.pbar, this.tree, this.additionalContainer],
            tbar : {
                height : 34,
                bodyBorder : false,
                defaults : {
                    scope : this
                },
                items : [{
                    text : "Charger FITS",
                    icon : '/sitools/common/res/images/icons/upload.png',
                    handler : function (btn , evt) {
                        this.loadFitsFromUrl(evt);
                    }
                }]
            }
        });

        Ext.QuickTips.init();

        this.centerPanel = Ext.create('Ext.panel.Panel', {
            layout : 'fit',
            flex : 1,
            border : false,
            items : []
        });

        this.items = [this.treeContainer, this.centerPanel];

        this.callParent(arguments);

    },

    afterRender : function () {
        this.callParent(arguments);

        if (Ext.isEmpty(this.url)) {
            return;
        }
        this.loadFits(this.url);

    },

    loadFitsFromUrl : function (e) {
        var fitsLoaderMenu = Ext.ComponentQuery.query('fitsLoaderMenuView')[0];
        if (Ext.isEmpty(fitsLoaderMenu)) {
            var menu = Ext.create('sitools.extension.view.modules.fitsViewer.FitsLoaderMenuView', {
                fitsMainView : this
            });
            menu.showAt(e.getX(), e.getY());
        } else {
            fitsLoaderMenu.showAt(e.getX(), e.getY());
        }
    },

    loadFits : function (url) {

    	this.pbar.setVisible(true);
    	
        this.url = url;
        this.fits = this.loader.loadFits(url, function (fits) {

            this.fits = fits;

            if (Ext.isEmpty(fits.getHDU().header.cards.FILENAME)) {
                this.fitsName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
            } else {
                this.fitsName = fits.getHDU().header.cards.FILENAME[1];
            }

            this.fits = fits;
            this.url = url;
            this.fitsArray = fits.view.buffer;

            var rootNode = {
                "text" : this.fitsName,
                "children" : [],
                "leaf" : false
            };

            Ext.iterate(fits, function (key, value) {
                var isLeaf = (!Ext.isArray(value));

                var item = {
                    "text" : key,
                    "value" : value,
                    "leaf" : isLeaf
                };

                if (!isLeaf) {
                    item.children = [];
                    Ext.iterate(value, function (k, v) {
                        var type = k.header.extensionType;

                        if (Ext.isEmpty(k.header.extensionType)) {
                            if (!Ext.isEmpty(k.header.TFORM) || !Ext.isEmpty(k.header.TFORM1)) {
                                type = "TABLE";
                            } else if (!Ext.isEmpty(k.header.CRVAL1) && !Ext.isEmpty(k.header.CRPIX1)) {
                                type = "IMAGE";
                            }
                        }

                        var node = {
                            "text" : type,
                            "data" : k.data,
                            "header" : k.header,
                            "leaf" : true
                        };
                        item.children.push(node);
                    }, this);
                    rootNode.children.push(item);
                }
            }, this);

            this.tree.setRootNode(rootNode);
            this.centerPanel.removeAll();
            this.tree.expandAll();

        }.bind(this), this.failLoadFits, this.onprogressFits.bind(this));
    },

    failLoadFits : function (resp) {
        Ext.Msg.show({
            title : i18n.get('label.error'),
            msg : resp.status + " : " + resp.statusText,
            icon : Ext.MessageBox.ERROR,
            buttons : Ext.MessageBox.OK
        });
    },

    onprogressFits : function (prog) {
        var total = Ext.util.Format.fileSize(prog.totalSize);
        var current = Ext.util.Format.fileSize(prog.loaded);

        if (prog.loaded == prog.totalSize) {
            this.pbar.updateProgress(1, this.i18nFitsViewer.get('label.done'));
        } else {
            this.pbar.updateProgress(prog.loaded / prog.totalSize, current + "/" + total);
        }
    },

    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    }
});
