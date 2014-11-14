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
/*global Ext, sitools, i18n, alertFailure, window, loadUrl, sql2ext, SITOOLS_DEFAULT_IHM_DATE_FORMAT, ColumnRendererEnum, SITOOLS_DATE_FORMAT*/

Ext.namespace('sitools.user.view.component.datasets.recordDetail');

/**
 * Data detail Panel view. 
 * 
 * @cfg {string} fromWhere (required) :  "Ext.ux.livegrid" or "openSearch", "plot", "dataView"
 *       used to know how to determine the Url of the record
 * @cfg grid : the grid that contains all the datas 
 * @cfg {string} baseUrl  used only in "data" case. 
 *       used to build the url of the record. Contains datasetAttachement + "/records"
 * @cfg {string} datasetId the datasetId
 * @cfg {string} datasetUrl the dataset url attachment
 * @class sitools.user.component.viewDataDetail
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.component.datasets.recordDetail.RecordDetailView', {
    extend : 'Ext.panel.Panel',
    
	datasetColumnModel : null,
	layout : 'fit',
	border : false,
	
    initComponent : function () {
        var rec;

        switch (this.fromWhere) {

			case "openSearch" :
		        this.recSelected = this.grid.getSelectionModel().getSelected();
		        this.url = this.encodeUrlPrimaryKey(this.recSelected.data.guid);
				break;

			case "dataView" :
				break;

		    case "plot" :
		        break;

			default :
				var selectedLines = this.selections;
		        if (Ext.isEmpty(selectedLines) || selectedLines.length === 0) {
					Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noSelection'));
					return;
		        }
		        
		        this.recSelected = this.grid.getStore().getAt(selectedLines[0]);
		        var primaryKeyValue = "", primaryKeyName = "";
		        Ext.each(this.recSelected.fields.items, function (field) {
		            if (field.primaryKey) {
		                this.primaryKeyName = field.name;
		            }
		        }, this);

				this.primaryKeyValue = this.recSelected.get(this.primaryKeyName);

		        this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);

		        this.url = this.baseUrl + this.primaryKeyValue;
				break;
        }

        this.linkStore = Ext.create('Ext.data.Store', {
	        fields : [ 'name', 'value', 'image', 'behavior', 'columnRenderer', 'html']
	    });

        this.dataStore = Ext.create('Ext.data.Store', {
        	proxy : {
        		type : 'memory'
        	},
	        fields : [ 'name', 'value', 'image', 'behavior', 'columnRenderer', 'html']
	    });

        this.gridDataview = Ext.create('Ext.grid.Panel', {
        	store : this.dataStore,
        	padding : 20,
        	border : false,
        	header : false,
        	hideHeaders : true,
        	rowLines : false,
        	disableSelection : true,
        	autoScroll : true,
        	forceFit : true,
        	columns : [{
    			dataIndex: 'name',
    			width : 250
			}, {
    			dataIndex: 'value',
    			flex : 1,
    			resizable : true,
    			renderer : function (value, metaData) {
    				metaData.tdAttr = 'data-qtip="' + value + '"';
    				return value;
    			}
			}],
			viewConfig  : {
				stripeRows : false,
				trackOver : false
			}
        });

        this.metaDataPanel = Ext.create('Ext.panel.Panel', {
        	title : i18n.get('label.metadataInfo'),
        	layout : 'fit',
        	items : [{
        		xtype : 'label',
        		cls : 'recordDetailLabel',
        		text : ''
        	}, this.gridDataview]
        });

        var linkDataview = Ext.create('Ext.view.View', {
	        store : this.linkStore,
	        tpl : new Ext.XTemplate('<ul>', '<tpl for=".">',
                '<li id="{name}" class="img-link"',
                '<tpl if="this.hasToolTip(toolTip)">',
                    'ext:qtip="{toolTip}">',
                '</tpl>',
                '<tpl if="this.hasToolTip(toolTip) == false">',
                    'ext:qtip="{name}">',
                '</tpl>',
                '{html}',
                '</li>', '</tpl>', '</ul>',
                {
                compiled : true,
                disableFormats : true,
                hasToolTip : function (toolTip) {
                    return !Ext.isEmpty(toolTip);
                }
            }),
	        cls : 'linkImageDataView',
	        itemSelector : 'li.img-link',
	        overItemCls : 'nodes-hover',
            selectedClass : '',
	        mode : 'SINGLE',
	        multiSelect : false,
	        autoScroll : true,
	        listeners : {
	            scope : this,
	            click : this.handleClickOnLink
	        }
	    });

        // set the search form
        this.formPanelImg = Ext.create('Ext.form.Panel', {
            autoScroll : true,
            region : "north",
            split : (this.fromWhere !== 'dataView'),
            collapsible : (this.fromWhere !== 'dataView'),
//            collapsed : (this.fromWhere !== 'dataView'),
            collapsed : false,
            flex : 1,
            title : ((this.fromWhere === 'dataView') ? i18n.get("label.formImagePanelTitle") : null),
            listeners : {
                scope : this,
                expand : function (panel) {

                }
            }
        });

        // set the text form
        this.linkPanel = Ext.create('Ext.panel.Panel', {
            title : i18n.get("label.complementaryInformation"),
            items : [linkDataview],
            region : 'south'
        });

        // set the text form
        this.extraMetaPanel = Ext.create('Ext.form.Panel', {
            title : i18n.get('label.extraMetaPanel'),
            layout : 'border',
            padding : 10,
            items : [ this.formPanelImg, this.linkPanel ]
        });

        var centerPanelItems;
        if (this.fromWhere === 'dataView') {
//            centerPanelItems = [this.extraMetaPanel, this.formPanelImg, this.linkPanel];
            centerPanelItems = [this.gridDataview];
        }
        else {
//            centerPanelItems = [this.extraMetaPanel, this.linkPanel];
            centerPanelItems = [this.gridDataview];
        }
        
        //set the center Panel
        this.tabPanel = Ext.create('Ext.tab.Panel', {
            autoScroll : true,
            region : "center",
            border : false,
            items : [ this.metaDataPanel, this.formPanelImg ]
        });

        this.getCmDefAndbuildForm();
        
        this.componentType = 'dataDetail';
        if (this.fromWhere == 'dataView') {
			this.items = [this.tabPanel];
        }
        else {
//			this.items = [ this.tabPanel, this.formPanelImg ];
			this.items = [ this.tabPanel ];
        }

        this.listeners = {
			scope : this, 
			afterrender : function (panel) {
		        this.getCmDefAndbuildForm();
				panel.getEl().on("contextmenu", function (e, t, o) {
					e.stopPropagation();
				}, this);
			}
        };
        
        this.bbar = {
    		xtype : 'toolbar',
    		border : false,
    		items :  [{
	            iconCls : 'arrow-back',
	            scale : 'medium',
	            scope : this,
	            handler : function () {
	                this.goPrevious();
	            }
	        }, {
	            iconCls : 'arrow-next',
	            scale : 'medium',
	            scope : this,
	            handler : function () {
	                this.goNext();
	            }
	        }]
        		
        };
        
        this.callParent(arguments);
    }, 
    
    afterRender : function () {
        this._loadMaskAnchor = Ext.get(this.body.dom);
	    
        this.callParent(arguments);
    },
    
    /**
     * Need to save the window Settings
     * @return {}
     */
    _getSettings : function () {
        return {
            objectName : "viewDataDetail", 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };
    }, 
    /**
     * Go to the Next record of the grid passed into parameters
     */
    goNext : function () {
		if (Ext.isEmpty(this.grid)) {
			return;
		}
		var rec, rowSelect;
		switch (this.fromWhere) {
			case "openSearch" : 
				rowSelect = this.grid.getSelectionModel();
		        if (! rowSelect.selectNext()) {
		            return;
		        }
		        rec = rowSelect.getSelected();
				this.url = this.encodeUrlPrimaryKey(rec.data.guid);
				break;
			case "sitools.user.component.dataviews.tplView.TplView" : 
				var index = this.grid.getStore().indexOf(this.recSelected);
				var nextRec = this.grid.getStore().getAt(index + 1);
				if (Ext.isEmpty(nextRec)) {
					return;
				}
				this.primaryKeyValue = nextRec.get(this.primaryKeyName);
	            this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
	            this.url = this.baseUrl + this.primaryKeyValue;
				this.recSelected = nextRec;
	            this.grid.select(nextRec);
				break;
			default : 
				rowSelect = this.grid.getSelectionModel();
		        if (! rowSelect.selectNext()) {
		            return;
		        }
		        rec = this.grid.getStore().getAt(rowSelect.getSelection()[0]);
	            this.primaryKeyValue = rec.get(this.primaryKeyName);
	            this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
	            this.url = this.baseUrl + this.primaryKeyValue;
				break;
		}

        this.getCmDefAndbuildForm();	
    }, 
    /**
     * Go to the Previous record of the grid passed into parameters
     */
    goPrevious : function () {
		if (Ext.isEmpty(this.grid)) {
			return;
		}
		var rec, rowSelect;
		switch (this.fromWhere) {
			case "openSearch" : 
				rowSelect = this.grid.getSelectionModel();
		        if (! rowSelect.selectPrevious()) {
		            return;
		        }
		        rec = rowSelect.getSelected();
	            this.url = this.encodeUrlPrimaryKey(rec.data.guid);
	            break;
			case "sitools.user.component.dataviews.tplView.TplView" : 
				var index = this.grid.getStore().indexOf(this.recSelected);
				var nextRec = this.grid.getStore().getAt(index - 1);
				if (Ext.isEmpty(nextRec)) {
					return;
				}
				this.primaryKeyValue = nextRec.get(this.primaryKeyName);
	            this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
	            this.url = this.baseUrl + this.primaryKeyValue;
				this.recSelected = nextRec;
	            this.grid.select(nextRec);
				break;
			default : 
				rowSelect = this.grid.getSelectionModel();
		        if (! rowSelect.selectPrevious()) {
		            return;
		        }
		        rec = this.grid.getStore().getAt(rowSelect.getSelection()[0]);
	            this.primaryKeyValue = rec.get(this.primaryKeyName);
	            this.primaryKeyValue = encodeURIComponent(this.primaryKeyValue);
	            this.url = this.baseUrl + this.primaryKeyValue;
				break;
		}

        this.getCmDefAndbuildForm();	    
       
    }, 
    /**
     * Build the form according with the values loaded via the Url
     */
    getCmDefAndbuildForm : function () {
        if (Ext.isEmpty(this.datasetColumnModel)) {
		    Ext.Ajax.request({
	            url : this.datasetUrl,
	            method : 'GET',
	            scope : this,
	            success : function (ret) {
					try {
						var Json = Ext.decode(ret.responseText);
						if (!Json.success) {
							throw Json.message;
						}
						this.datasetColumnModel = Json.dataset.columnModel;
						this.buildForm();
					}
					catch (err) {
						Ext.Msg.alert(i18n.get('label.error'), err);
					}
					
	            }, 
	            failure : alertFailure
	        });        
	    }
	    else {
			this.buildForm();
	    }
    }, 
    buildForm : function () {
        
	    if (!Ext.isEmpty(this._loadMaskAnchor)) {
//            this._loadMaskAnchor.mask(i18n.get('label.waitMessage'), "x-mask-loading");
        }

        if (!Ext.isEmpty(this.url)) {
            this.linkStore.removeAll();
            this.dataStore.removeAll();

            
	        Ext.Ajax.request({
	            url : this.url,
	            method : 'GET',
	            scope : this,
	            success : function (ret) {
	                var data = Ext.decode(ret.responseText);
	                var itemsForm = [];
	                var itemsFormImg = [];
	                if (!data.success) {
	                    Ext.Msg.alert(i18n.get('label.information'), "Server error");
	                    return false;
	                }
	                var record = data.record;
	                var id = record.id;
	                var attributes = record.attributeValues;
	                if (attributes !== undefined) {
	                    var i;
	                    for (i = 0; i < attributes.length; i++) {
	                        var name = attributes[i].name;
	                        
	                        var column = this.findColumn(name);
	                        var value = attributes[i].value;
	                        var valueFormat = value;
	                        
	                        if (sql2ext.get(column.sqlColumnType) == 'dateAsString') {
				                valueFormat = sitools.user.utils.DataviewUtils.formatDate(value, column);
				            }
				            if (sql2ext.get(column.sqlColumnType) == 'boolean') {
				                valueFormat = value ? i18n.get('label.true') : i18n.get('label.false');
				            }
	                        
//	                        var item = Ext.create('Ext.Component', {
//                                fieldLabel : column.header,
//                                labelSeparator : "", 
//                                html : (Ext.isEmpty(valueFormat) || !Ext.isFunction(valueFormat.toString))
//												? valueFormat
//												: valueFormat.toString()
//                            });
				            
				            var item = {
			            		name : name,
			            		value : value
				            };
	                        
				            if (!Ext.isEmpty(column)) {
				            	if (column.primaryKey) {
				            		this.metaDataPanel.down('label').setText(Ext.String.format(i18n.get('label.metaFromRecord'), value));
				            	}
				            }
				            
	                        if (Ext.isEmpty(column) || Ext.isEmpty(column.columnRenderer)) {
		                        this.dataStore.add(item);
//		                        itemsForm.push(item);
		                    }
                            
		                    else {
                                var columnRenderer = column.columnRenderer;
                                var behavior = "";
                                if (!Ext.isEmpty(column.columnRenderer)) {
                                    behavior = column.columnRenderer.behavior;
                                    var html = sitools.user.utils.DataviewUtils.getRendererHTML(column, {});
									switch (behavior) {
									case ColumnRendererEnum.URL_LOCAL :
					                case ColumnRendererEnum.URL_EXT_NEW_TAB :
					                case ColumnRendererEnum.URL_EXT_DESKTOP :
					                case ColumnRendererEnum.DATASET_ICON_LINK :
										if (! Ext.isEmpty(value)) {
	                                        if (!Ext.isEmpty(columnRenderer.linkText)) {
	                                            item = Ext.create('Ext.Component', {
		                                            fieldLabel : column.header,
					                                labelSeparator : "", 
		                                            html : Ext.String.format(html, value)
		                                        });	                                         
	                                            itemsForm.push(item);
							                } else if (!Ext.isEmpty(columnRenderer.image)) {
							                    var rec = {
	                                                name : name,
	                                                value : value,
	                                                image : columnRenderer.image.url,
	                                                behavior : behavior,
                                                    columnRenderer : columnRenderer,
                                                    toolTip : columnRenderer.toolTip,
                                                    html : html
                                                    
	                                            };
	                                            rec = new Ext.data.Record(rec);
	                                            this.linkStore.add(rec);                                            
							                }																	
										}                                    
										break;
	                                case ColumnRendererEnum.IMAGE_FROM_SQL : 
                                    case ColumnRendererEnum.IMAGE_THUMB_FROM_IMAGE :
	                                    if (! Ext.isEmpty(value)) {
	                                        var tooltip = "";
	                                        var imageUrl = "";
	                                        

	                                        if (!Ext.isEmpty(columnRenderer.toolTip)) {
	                                            tooltip = columnRenderer.toolTip;
	                                        }
	                                        else {
	                                            tooltip = column.header;
	                                        }
	                                        
						                    if (!Ext.isEmpty(columnRenderer.url)) {
						                        imageUrl = columnRenderer.url;
						                    } else if (!Ext.isEmpty(columnRenderer.columnAlias)) {
						                        imageUrl = this.findRecordValue(record, columnRenderer.columnAlias);            
						                    }
                                            
	                                        item = Ext.create('Ext.Component', {
                                                width : 100,
	                                            html : Ext.String.format(html, value, imageUrl),
                                                tooltip : tooltip,
	                                            cls : "x-form-item"
	                                        });                                       
	                                    }
	                                    itemsFormImg.push(item);
	                                    break;
                                    case ColumnRendererEnum.NO_CLIENT_ACCESS :
                                        break;
									default : 
                                        item = Ext.create('Ext.Component', {
	                                        fieldLabel : column.header,
			                                labelSeparator : "", 
	                                        html : Ext.String.format(html, value)
	                                    });                                          
	                                    itemsForm.push(item);
	                                    break;
                                    }
		                        }
		                    }
                        }
                        
//	                    this.extraMetaPanel.removeAll();
//	                    this.formPanelImg.removeAll();
//	                    
//                        this.extraMetaPanel.add(itemsForm);
//	                    this.extraMetaPanel.doLayout();
                        
                        if (this.linkStore.getCount() === 0) {
                            this.linkPanel.setVisible(false);
                        } else {
                            this.linkPanel.setVisible(true);
                        }
                        
                        if (itemsFormImg.length === 0) {
                            this.formPanelImg.setVisible(false);
                        } else {
                            this.formPanelImg.add(itemsFormImg);
                            this.formPanelImg.setVisible(true);
                        }
                        
	                    if (this._loadMaskAnchor && this._loadMaskAnchor.isMasked()) {
//							this._loadMaskAnchor.unmask();
						}
	                    
	                    //Register events on column values with featureTypes  
//	                    this.registerClickEvent(attributes);
	                    
                    }
	            },
	            failure : function () {
//	                alertFailure();
                    if (this._loadMaskAnchor && this._loadMaskAnchor.isMasked()) {
//						this._loadMaskAnchor.unmask();
					}
	            }
	        });
	    }
    },
    
    findColumn : function (columnAlias) {
		var result = null;
		Ext.each(this.datasetColumnModel, function (column) {
			if (column.columnAlias == columnAlias) {
				result = column;
				return;
			}
		}, this);
		return result;
    },
    
    findRecordValue : function (record, columnAlias) {
        var result = null;
        Ext.each(record.attributeValues, function (attr) {
            if (attr.name == columnAlias) {
                result = attr.value;
                return;
            }
        }, this);
        return result;
    },
    
    handleClickOnLink : function (dataView, index, node, e) {
        var data = dataView.getRecord(node).data;
        var behavior = data.behavior;
        switch (behavior) {
        case ColumnRendererEnum.URL_LOCAL:
            sitools.user.utils.DataviewUtils.downloadData(data.value);
            break;
        case ColumnRendererEnum.URL_EXT_NEW_TAB  :
            window.open(data.value);
            break;
        case ColumnRendererEnum.URL_EXT_DESKTOP  :
            sitools.user.utils.DataviewUtils.showDisplayableUrl(data.value, data.columnRenderer.displayable);
            break;
        case ColumnRendererEnum.DATASET_ICON_LINK  :
            sitools.user.utils.DataviewUtils.showDetailsData(data.value, data.columnRenderer.columnAlias, data.columnRenderer.datasetLinkUrl);
            break;    
        default : 
            break;
            
        }
    }, 
    
    encodeUrlPrimaryKey : function (url) {
      //get the end of the uri and encode it
        var urlSplited = url.split('/');
        var urlReturn = "";
        for (var i = 0; i < urlSplited.length; i++) {
            if (i < urlSplited.length - 1) {
                urlReturn += urlSplited[i] + "/";
            } else {
                urlReturn += encodeURIComponent(urlSplited[i]);
            }
        }
        return urlReturn;
    },
    
    callbackClickFeatureType : function (e, t, o) {
        e.stopEvent();
        var record = o.record;
        var controller = o.controller;            
        var column = o.column;
        sitools.user.utils.DataviewUtils.featureTypeAction(column, record, controller);
    },
    
    
    registerClickEvent : function (attributes) {
        
        var nodeFormPanel = this.extraMetaPanel.getEl().dom;
        var featureTypeNodes = Ext.DomQuery.jsSelect(".featureType", nodeFormPanel);
        
        var formPanelImgPanel = this.formPanelImg.getEl().dom;
        featureTypeNodes = featureTypeNodes.concat(Ext.DomQuery.jsSelect(".featureType", formPanelImgPanel));

        var linkImagePanel = this.linkPanel.getEl().dom;
        featureTypeNodes = featureTypeNodes.concat(Ext.DomQuery.jsSelect(".featureType", linkImagePanel));
        
        var controller = this.grid.down('toolbar').guiServiceController;
        
        //Create a Record from the attribute Values 
        var jsonObj = {};
        Ext.each(attributes, function (attribute) {
            jsonObj[attribute.name] = attribute.value;
        });
        
        
        Ext.each(featureTypeNodes, function (featureTypeNode) {
            var featureTypeNodeElement = Ext.get(featureTypeNode);
            
            var columnAlias = featureTypeNodeElement.getAttribute("column", "sitools");
            var column = this.findColumn(columnAlias);
            
            featureTypeNodeElement.addListener("click", this.callbackClickFeatureType, this, {
                record : new Ext.data.Record(jsonObj),
                controller : controller,
                column : column
            });
        }, this);
            

        if (this.formPanelImg.collapsible && this.formPanelImg.isVisible()) {    
            this.formPanelImg.collapse();
        }
    }
});
