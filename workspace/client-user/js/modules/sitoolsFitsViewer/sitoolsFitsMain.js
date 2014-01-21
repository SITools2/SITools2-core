/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.user.modules');
/**
 * sitools.user.modules.sitoolsFitsMain
 * 
 * @class sitools.user.modules.sitoolsFitsMain
 * @extends Ext.Panel
 */
sitools.user.modules.sitoolsFitsMain = Ext.extend(Ext.Panel, {
    layout : 'fit',
    id : 'fitsMain',
    initComponent : function () {

//        this.url = "/sitools/datastorage/user/fits_viewer/images/new4.fits";
        
//        this.fitsName = this.url.substring(this.url.lastIndexOf('/') + 1, this.url.lastIndexOf('.'));

        this.loader = new FitsLoader();
        
        var rootNode = {
            "text" : i18n.get("label.nothingLoaded"),
            "children" : [],
            "leaf" : true
        };
    
        this.tree = new Ext.tree.TreePanel({
            region : 'center',
            layout : 'fit',
            expanded : true,
            useArrows : true,
            autoScroll : true,
            containerScroll : true,
            bodyStyle : 'background-color:white;',
            rootVisible : true,
            root : rootNode,
            listeners : {
                scope : this,
                click : function (node, e) {
                    if (!node.attributes.leaf) {
                        return;
                    }

                    if (node.attributes.text == "TABLE" || node.attributes.text == "BINTABLE") {
                        this.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
                        this.doLayout();
                        
                        Ext.defer(function () {
                            this.sitoolsFitsTable = new sitools.user.modules.sitoolsFitsTable({
                                headerData : node.attributes.header,
                                data : node.attributes.data,
                                fits : this.fits,
                                fitsMainPanel : this
                            });
                            
                            this.centerPanel.removeAll(false);
                            this.centerPanel.add(this.sitoolsFitsTable);
                            this.centerPanel.doLayout();
                        }, 5, this);

                    } else if (node.attributes.text == "IMAGE") {
                        this.getEl().mask(i18n.get('label.loadingFits'), "x-mask-loading");
                        
                        Ext.defer(function () {
                            this.sitoolsFitsViewer = new sitools.user.modules.sitoolsFitsViewer({
                                urlFits : this.url,
                                headerData : node.attributes.header,
                                fits : this.fits,
                                fitsArray : this.fitsArray,
                                fitsMainPanel : this
                            });
                            this.centerPanel.removeAll(false);
                            this.centerPanel.add(this.sitoolsFitsViewer);
                            this.centerPanel.doLayout();
                        }, 5, this);
                    }
                }
            }
        });
        

        this.pbar = new Ext.ProgressBar({
            region : 'north',
            value : 0,
        });
        
        this.treeContainer = new Ext.Panel({
            layout : 'border',
            region : 'west',
            width : 220,
            bodyStyle : 'background-color:white;',
            items : [this.tree, this.pbar],
            tbar : {
                cls : 'services-toolbar',
                height : 34,
                defaults : {
                    scope : this
                },
                items : [{
                    text : "Charger FITS",
                    icon : '/sitools/common/res/images/icons/upload.png',
                    handler : function () {
                        this.loadFitsFromUrl();
                    }
                }, '->', {
                    name : 'collapseIcon',
                    icon : '/sitools/common/res/images/icons/toolbar_remove.png',
                    scope : this,
                    handler : function () {
                        if (this.treeContainer.collapsed) {
                            this.treeContainer.expand();
                            this.treeContainer.getTopToolbar().find('name', 'collapseIcon')[0].setIcon('/sitools/common/res/images/icons/toolbar_remove.png');
                        } else {
                            this.treeContainer.collapse();                
                            this.treeContainer.getTopToolbar().find('name', 'collapseIcon')[0].setIcon('/sitools/common/res/images/icons/toolbar_create.png');
                        }
                    }
                }]
            }
        });
        
        Ext.QuickTips.init();

        this.centerPanel = new Ext.Panel({
            region : 'center',
            layout : 'fit',
            items : []
        });

        this.panel = new Ext.Panel({
            layout : 'border',
            width : 800,
            height : 600,
            items : [this.treeContainer, this.centerPanel ]
        });

        this.items = [this.panel];
        
        sitools.user.modules.sitoolsFitsMain.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.user.modules.sitoolsFitsMain.superclass.afterRender.apply(this, arguments);
        
        if (Ext.isEmpty(this.url)) {
            return;
        }
        
        this.loadFits(this.url);
        
    },
    
    loadFitsFromUrl : function () {
        new Ext.Window({
            title : 'Load Fits',
            id : 'formWindId',
            height : 120,
            width : 400,
            items : [{
                xtype : 'form',
                name : 'form',
                items : [{
                    xtype : 'textfield',
                    fieldLabel : 'Fits URL',
                    anchor: '100%',
                    padding : 5,
                    allowBlank : false,
                    name : 'fitsUrl'
                }],
                buttons : [{
                    text : i18n.get("label.load"),
                    scope : this,
                    handler : function (btn) {
                        var wind =  Ext.getCmp('formWindId');
                        var formPanel = wind.find('name', 'form')[0];
                        var form = formPanel.getForm();
                        if (form.isValid()) {
                            var url = "/sitools/proxy?external_url=" + form.getValues().fitsUrl;
                            this.loadFits(url);
//                            this.getEl().mask(i18n.get("label.loadingFits"));
                            wind.close();
                        }
                    }
                }]
            }]
        }).show();
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
            this.centerPanel.doLayout();
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
Ext.reg('sitools.user.modules.sitoolsFitsMain', sitools.user.modules.sitoolsFitsMain);
