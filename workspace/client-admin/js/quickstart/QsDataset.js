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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure*/
Ext.namespace('sitools.admin.quickstart');

Ext.define('sitools.admin.quickstart.QsDataset', { 
    extend : 'Ext.panel.Panel',
    widget : 'widget.qsDataset',

    forceLayout : true, 
    layout : {
        type : "vbox",
        align : 'center',
        pack : 'start'
    },
    border : false, 
    bodyCls : 'quickStart',
    
    initComponent : function () {
        
        var title = Ext.create('Ext.form.Label', {
            cls : 'qs-h1',
            html : i18n.get('label.qsDatasetTitle')
        });
        
        var desc = Ext.create('Ext.form.Label', {
            cls : 'qs-div',
            id : "start-desc",
            html : i18n.get('label.qsDatasetDesc')
        });
        
        var img = Ext.create('Ext.form.Label', {
            html : '<img id="qs-dataset" class="qs-image" src="/sitools/client-admin/res/html/quickStart/screenshots/datasets.png"/>',
            tooltip : 'Open Dataset',
            listeners : {
                scope : this,
                afterrender : function (img) {
                    Ext.get("qs-dataset").on('load', function () {
                        
                        img.getEl().alignTo("start-desc", "tl-bl");
                        
                        var imgProjet = Ext.create('Ext.form.Label', {
                            html : '<img id="qs-dataset-logo" class="bouton_action" src="/sitools/client-admin/res/html/quickStart/icons/dataset-logo.png"/>',
                            listeners : {
                                scope : this,
                                afterrender : function (imgProjet) {
                                    Ext.get("qs-dataset-logo").on('load', function () {
                                        
                                        imgProjet.getEl().on('mouseleave', function (e, t, o) {
                                           Ext.get(t).update("<img id='qs-projet-logo' class='bouton_action' src='/sitools/client-admin/res/html/quickStart/icons/dataset-logo.png'/>");
                                        });
                                        imgProjet.getEl().on('mouseenter', function (e, t, o) {
                                            Ext.get(t).update("<img id='qs-projet-logo' class='bouton_action' src='/sitools/client-admin/res/html/quickStart/icons/dataset-logo-hover.png'/>");
                                        });
                                        imgProjet.getEl().on('click', function (e, t, o) {
                                            this.qs.openFeature("datasetsSqlNodeId");
                                        }, this);
                                        
                                        imgProjet.getEl().alignTo("qs-dataset", "br-br",  [82, 75]);
                                        
                                        new Ext.ToolTip({
                                            target : 'qs-dataset-logo',
                                            anchor: 'left',
                                            autoShow : true,
                                            showDelay : 0,
                                            html : "<b>Open Dataset</b>"
                                        });
                                        
                                    }, this);
                                }
                            }
                        });
                        this.add(imgProjet);

                    }, this);
                }
            }
        });
        
        this.items = [title, desc, img];
        this.callParent(arguments);
    }
});

