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
/* global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.public.widget.ckeditor.sitoolsPlugins');
/**
 * datasetLink widget
 * 
 * @class sitools.public.widget.ckeditor.sitoolsPlugins.DatasetBrowser
 * @extends Ext.util.Observable
 */
Ext.define('sitools.public.widget.ckeditor.sitoolsPlugins.DatasetBrowser', {
    extend : 'Ext.tree.Panel',
    alias : 'widget.datasetBrowser',   
    
    requires : ['sitools.user.utils.CommonTreeUtils',
                'sitools.user.component.datasets.columnsDefinition.ColumnsDefinition',
                'sitools.user.component.form.FormComponent',
                'sitools.user.component.feeds.FeedComponent',
                'sitools.user.component.datasets.opensearch.Opensearch',
                'sitools.user.view.component.form.FormView'],
    
    initComponent : function () {
        
        this.store = Ext.create("sitools.user.store.DatasetTreeStore");
        
        this.setRootNode({
            text : i18n.get('label.datasets'),
            leaf : false
        });  
        
        Ext.Ajax.request({
            url : Project.sitoolsAttachementForUsers + '/datasets',
            method : 'GET',
            scope : this,
            success : function (response) {
                var datasets = Ext.decode(response.responseText).data;
                Ext.each(datasets, function(dataset){
                    dataset.text = dataset.name;
                    this.getRootNode().appendChild(dataset);
                }, this);
            }
        });
        
        this.bbar = ['->', {
            xtype : 'button',
            text : i18n.get('label.select'),
            scope : this,
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
            handler : this.onValidate
        }];
        
        this.listeners = {
            beforeitemexpand : function (node, opts) {
                if (node.isRoot()) {
                    return;
                }
                
                if(node.get("type") == "DataSet") {
                    node.removeAll();

                    Ext.Ajax.request({
                        url : node.get('url'),
                        scope : this,
                        success : function (response) {
                            var dataset = Ext.decode(response.responseText).dataset;
                            commonTreeUtils.addShowData(node, dataset);
                            commonTreeUtils.addShowDefinition(node, dataset);

                            Ext.Ajax.request({
                                url : dataset.sitoolsAttachementForUsers + "/opensearch.xml",
                                scope : this,
                                success : function (response) {
                                    var xml = response.responseXML;
                                    var dq = Ext.DomQuery;
                                    // check if there is a success node
                                    // in the xml
                                    var success = dq.selectNode('OpenSearchDescription ', xml);

                                    if (!Ext.isEmpty(success)) {
                                        commonTreeUtils.addOpensearch(node, dataset);
                                    }
                                }
                            });
                            commonTreeUtils.addForm(node, dataset);
                            commonTreeUtils.addFeeds(node, dataset);
                        }
                    });
                } else {
                    commonTreeUtils.handleBeforeExpandNode(node);
                }
                return true;

            }
        }
        
        this.callParent(arguments);

    },
    
    onValidate : function() {
        var selNode = this.selModel.getSelection()[0];
        if (Ext.isEmpty(selNode)) {
            return Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.noDatasetSelected'));
        }
        
        var nodeType = selNode.get('type');
        if (selNode.isLeaf() && nodeType != "defi") {
                var urlLink, displayValue;
                urlLink = selNode.get('properties').dataset.sitoolsAttachementForUsers;
                displayValue = selNode.get('text');
//                      this.browseField.setValue(displayValue);
                
                var datasetName = selNode.get('properties').dataset.name;
                this.browseField.datasetName = datasetName;
                
            if (nodeType == "data") {
                this.browseField.setValue('Data : ' + datasetName);
                this.textField.setValue(datasetName);
                
                this.browseField.dataLinkComponent = Ext.String.format("parent.sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", 'data'); return false;", urlLink);
            }
            else if (nodeType == "form"){
                this.browseField.setValue('Form : ' + datasetName);
                this.textField.setValue(datasetName);
//                this.browseField.dataLinkComponent = Ext.String.format('parent.SitoolsDesk.showFormFromEditor(\'{0}/forms\'); return false;', urlLink);
                this.browseField.dataLinkComponent = Ext.String.format("parent.sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", 'forms'); return false;", urlLink);
            }
            this.up('window').close();
        }
    }

});
