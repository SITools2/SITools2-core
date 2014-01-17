/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.quickStart');

sitools.admin.quickStart.qs = Ext.extend(Ext.Panel, {
    layout : 'border',
    bodyCssClass : 'quickStart',
    initComponent : function () {
        
        this.stepsBtn = [{
            id : 'qsStart',
            value : 'qsStart',
            icon : '/sitools/common/res/images/icons/logo_fav_icone.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            pressed : true,
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsStart',
                        anchor: 'top',
                        html : "Start"
                    });
                }
            }
        }, {
            id : 'qsProject',
            value : 'qsProject',
            icon : '/sitools/common/res/images/icons/tree_projects.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsProject',
                        anchor: 'top',
                        html : "Project"
                    });
                }
            }
        }, {
            value : 'qsDatasource',
            id : 'qsDatasource',
            icon : '/sitools/common/res/images/icons/tree_databases.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsDatasource',
                        anchor: 'top',
                        html : "Datasource"
                    });
                }
            }
        }, {
            id : 'qsDataset',
            value : 'qsDataset',
            icon : '/sitools/common/res/images/icons/tree_datasets.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsDataset',
                        anchor: 'top',
                        html : "Dataset"
                    });
                }
            }
        }, {
            id : 'qsForm',
            value : 'qsForm',
            icon : '/sitools/common/res/images/icons/tree_forms.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsForm',
                        anchor: 'top',
                        html : "Form"
                    });
                }
            }
        }, {
            id : 'qsSecurity',
            value : 'qsSecurity',
            icon : '/sitools/common/res/images/icons/tree_userman.png',
            scope : this,
            enableToogle: true,
            toggleGroup : 'steps',
            toggleHandler : this.manageSteps,
            allowDepress : false,
            scale : 'medium',
            cls : 'services-toolbar-btn',
            listeners : {
                afterrender : function (b) {
                    new Ext.ToolTip({
                        target : 'qsSecurity',
                        anchor: 'top',
                        html : "Security"
                    });
                }
            }
        }, '->', {xtype : 'label', html : '<i>Show QuickStart on startup<i>'},
        {
           xtype : 'checkbox',
           listeners : {
               check : function (combo, checked) {
                   Ext.util.Cookies.set('showQuickStart', checked);
               },
               afterrender : function (cb) {
                   cb.setValue(Ext.util.Cookies.get('showQuickStart'));
               }
           }
        }];
        
        this.currentPanel = new sitools.admin.quickStart.qsStart({
            qs : this
        });
        
        this.welcomePanel = new Ext.Panel({
            region : 'center',
            id : "startPanel",
            layout : 'fit',
//            autoLoad : "/sitools/client-admin/res/html/quickStart/qs-start.html"
            bodyCssClass : 'quickStart',
            items : [this.currentPanel]
        });
        
        this.stepsPanel = new Ext.Panel({
            region : 'south',
            bodyCssClass : 'quickStart',
            buttonAlign : 'center',
            height : 0,
            buttons : [this.stepsBtn]
        });
        
        this.items = [this.welcomePanel, this.stepsPanel];
        
        sitools.admin.quickStart.qs.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.admin.quickStart.qs.superclass.afterRender.apply(this, arguments);
    },
    
    manageSteps : function (btn, pressed) {
        if (pressed) {
//            this.welcomePanel.autoLoad = "/sitools/client-admin/res/html/quickStart/qs-" + btn.value +".html";
//            this.welcomePanel.doAutoLoad();
            var xtype = "sitools.admin.quickStart." + btn.value;
            var step = eval(xtype);
            
            this.currentPanel =  new step();
            this.currentPanel.qs = this;
            
            this.welcomePanel.removeAll();
            this.welcomePanel.add(this.currentPanel);
            this.welcomePanel.doLayout();
        }
    },
    
    openFeature : function (nodeId) {
        var containerP = mainPanel.find('name', 'containerPanel')[0];
        var tree = treePanel.items.items[0];
        
        var node = tree.getNodeById(nodeId);
        tree.getSelectionModel().select(node);
        
        containerP.removeAll();
        containerP.add(
            {
                width: "100%", 
                items : [ {
                    xtype : 's-box',
                    label : i18n.get('label.' + node.attributes.nodeName),
                    items : [ {
                        xtype : 's-' + node.attributes.nodeName,
                        sitoolsType : "mainAdminPanel"
                    } ],
                    idItem : node.attributes.nodeId
                } ], 
                listeners : {
                    resize : function (panel, width, height) {
                        var size = panel.items.items[0].body.getSize();
                        var sBoxTitle = panel.items.items[0].items.items[0].getEl();
                        size = {
                            height : size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargins("t") + sBoxTitle.getMargins("b")), 
                            width : size.width - 8
                        };
                        var mainAdminPanel = panel.find("sitoolsType", "mainAdminPanel");
                        mainAdminPanel[0].setSize(size);
                    }
                }
            });
        containerP.doLayout();
    }
    
});

