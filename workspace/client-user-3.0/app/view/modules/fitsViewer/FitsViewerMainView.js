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
Ext.namespace('sitools.user.view.modules.fitsViewer');
/**
 * Main class of Fits Explorer
 * 
 * @class sitools.user.view.modules.fitsViewer.FitsViewerMainView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.modules.fitsViewer.FitsViewerMainView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.fitsViewerMainView',

    layout : 'fit',
    itemId : 'fitsMain',
    border : false,

    initComponent : function () {

//        this.fitsName = this.url.substring(this.url.lastIndexOf('/') + 1, this.url.lastIndexOf('.'));

        this.loader = new FitsLoader();

        var rootNode = {
            "text" : i18n.get("label.nothingLoaded"),
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
            region : 'center',
            layout : 'fit',
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

                    if (node.get('text') == "TABLE" || node.get('text') == "BINTABLE") {
                        this.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");

                        Ext.defer(function () {
                            this.sitoolsFitsTable = Ext.create('sitools.user.view.modules.fitsViewer.FitsTableView', {
                                headerData : node.get('header'),
                                data : node.get('data'),
                                fits : this.fits,
                                fitsMainPanel : this
                            });

                            this.centerPanel.removeAll(false);
                            this.centerPanel.add(this.sitoolsFitsTable);
                            this.centerPanel.doLayout();
                        }, 5, this);

                    } else if (node.get('text') == "IMAGE") {
                        this.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");

                        Ext.defer(function () {
                            this.sitoolsFitsViewer = Ext.create('sitools.user.view.modules.fitsViewer.FitsViewerView', {
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
            region : 'north',
            bodyStyle : 'background-color:white;',
            value : 0
        });

        this.treeContainer = Ext.create('Ext.panel.Panel', {
            layout : 'border',
            region : 'west',
            width : 220,
            items : [this.tree, this.pbar],
            border : false,
            tbar : {
                height : 34,
//                border : false,
                defaults : {
                    scope : this
                },
                items : [{
                    text : "Charger FITS",
                    icon : '/sitools/common/res/images/icons/upload.png',
                    handler : function (btn , evt) {
                        this.loadFitsFromUrl(evt);
                    }
                }, '->', {
                    name : 'collapseIcon',
                    icon : '/sitools/common/res/images/icons/toolbar_remove.png',
                    scope : this,
                    handler : function () {
                        if (this.treeContainer.collapsed) {
                            this.treeContainer.expand();
                            this.treeContainer.down('toolbar > button[name="collapseIcon"]').setIcon('/sitools/common/res/images/icons/toolbar_remove.png');
                        } else {
                            this.treeContainer.collapse();
                            this.treeContainer.down('toolbar > button[name="collapseIcon"]').setIcon('/sitools/common/res/images/icons/toolbar_create.png');
                        }
                    }
                }]
            }
        });

        Ext.QuickTips.init();

        this.centerPanel = Ext.create('Ext.panel.Panel', {
            region : 'center',
            layout : 'fit',
            border : false,
            items : []
        });

        this.panel = Ext.create('Ext.panel.Panel', {
            layout : 'border',
            border : false,
            width : 800,
            height : 600,
            items : [this.treeContainer, this.centerPanel ]
        });

        this.items = [this.panel];

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
            var menu = Ext.create('sitools.user.view.modules.fitsViewer.FitsLoaderMenuView', {
                fitsMainView : this
            });
            menu.showAt(e.getX(), e.getY());
        } else {
            fitsLoaderMenu.showAt(e.getX(), e.getY());
        }
    },

    loadFits : function (url) {

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
            this.tree.expandAll();

            this.centerPanel.removeAll();
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
            this.pbar.updateProgress(1, i18n.get('label.done'));
        } else {
            this.pbar.updateProgress(prog.loaded / prog.totalSize, current + "/" + total);
        }
    }
});
