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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
/*
 * @include "absoluteLayoutProp.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A window to present all form Components type
 * @cfg {Ext.data.JsonStore} storeConcepts the store with concepts
 * @class sitools.admin.forms.ComponentsListPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.forms.AdvancedFormPanel', {
    extend : 'sitools.public.ux.form.ToolFieldSet',
    border : true,
    closable : true,
    style : 'background-color : white;',
    layout : "absolute",
    autoScroll : true, 
    enableDragDrop : true,
    requires : ['sitools.admin.forms.ComponentProp'],
    
    initComponent : function (config) {

        Ext.apply(this, config);
        
        this.padding = '0 0 5 0';
//        this.iconCls = 'dark-icons';
        
        this.tools = [{
            type : 'close',
            tooltip: i18n.get('label.remove'),
            cls : 'dark-icons',
            shrinkWrap : true,
            autoShow : true,
            scope : this,
            handler : function (event, toolEl, header, tool) {
                Ext.Msg.show({
                    title : i18n.get('label.delete'),
                    buttons : Ext.Msg.YESNO,
                    icon: Ext.window.MessageBox.QUESTION,
                    msg : i18n.get('fieldset.delete'),
                    scope : this,
                    fn : function (btn, text, opts) {
                        if (btn == 'yes') {
                            this.deletePanel();
                        }
                    }
                });
            }
        }, {
            type : 'gear',
            cls : 'dark-icons',
            tooltip : i18n.get('label.editFieldsetProps'),
            shrinkWrap : true,
            autoShow : true,
            scope : this,
            handler : function (event, toolEl, panel) {
                this.editPanel();
            }
        }];
        
//        this.toolTemplate = new Ext.XTemplate(
//            '<tpl if="id==\'close\'">',
//                '<div class="x-tool x-tool-close-fieldset">&nbsp;</div>',
//            '</tpl>',
//            '<tpl if="id==\'gear\'">',
//                '<div class="x-tool x-tool-gear-fieldset">&nbsp;</div>',
//            '</tpl>',
//            '<tpl if="id==\'toggle\'">',
//                '<div class="x-tool x-tool-toggle-fieldset">&nbsp;</div>',
//            '</tpl>'
//        );
        
        this.listeners = {
            scope : this,
            activate : this.onActivate
        };
        
        sitools.admin.forms.AdvancedFormPanel.superclass.initComponent.call(this);
    },

    afterRender : function () {
        sitools.admin.forms.AdvancedFormPanel.superclass.afterRender.apply(this, arguments);

        var ddGroup = this.ddGroup;
        var datasetColumnModel = this.datasetColumnModel;
        var formComponentsStore = this.formComponentsStore;
        var storeConcepts = this.storeConcepts;
        var absoluteLayout = this.absoluteLayout;
        
        var mypanel = this;
        var advPanelDropTargetEl =  this.body.dom;
		var bodyEl = this.body;
		
		var context = this.context;
		
		var advPanelDropTarget = new Ext.dd.DropTarget(advPanelDropTargetEl, {
			ddGroup : ddGroup,
            notifyDrop : function (ddSource, e, data) {

				var xyDrop = e.xy;
				var xyRef = Ext.get(bodyEl).getXY();
				
				var xyOnCreate = {
					x : xyDrop[0] - xyRef[0], 
					y : xyDrop[1] - xyRef[1]
				};
				// Reference the record (single selection) for readability
				var rec = ddSource.dragData.records[0];
				
		        var ComponentWin = Ext.create("sitools.admin.forms.ComponentProp", {
		            urlAdmin : rec.data.jsonDefinitionAdmin,
		            datasetColumnModel : datasetColumnModel,
		            ctype : rec.data.type,
		            action : "create",
		            componentDefaultHeight : rec.data.componentDefaultHeight,
		            componentDefaultWidth : rec.data.componentDefaultWidth,
		            dimensionId : rec.data.dimensionId,
		            unit : rec.data.unit,
		            extraParams : rec.data.extraParams, 
		            jsAdminObject : rec.data.jsAdminObject, 
		            jsUserObject : rec.data.jsUserObject, 
		            context : context, 
		            xyOnCreate : xyOnCreate, 
		            storeConcepts : storeConcepts, 
		            absoluteLayout : absoluteLayout, 
		            record : rec, 
		            formComponentsStore : formComponentsStore,
		            containerPanelId : mypanel.containerPanelId,
		            ddSource : ddSource
		        });
		        
		        ComponentWin.show();
		        this.endDrag();
			},
			overClass : 'over-dd-form'
		});
    },
    
    onActivate : function () {

        var y = 0;
        var x = 25;
        
        this.removeAll();
        var mypanel = this;
        
        this.formComponentsStore.each(function (component) {

            var containerId = component.data.containerPanelId;

//            if (containerId == this.id) {
              if (containerId == this.containerPanelId) {

                y = Ext.isEmpty(component.data.ypos) ? y + 50 : component.data.ypos;
                x = Ext.isEmpty(component.data.xpos) ? x : component.data.xpos;
                // height = Ext.isEmpty (component.data.height) ?
                // height :
                // component.data.height;
                var containerItems = [ sitools.public.forms.formParameterToComponent.getComponentFromFormParameter(component.data, null, null, this.datasetColumnModel,
                        this.context).component ];
                containerItems[0].setDisabled(true);
                containerItems[0].maskOnDisable = false;
                // containerItems[0].initialConfig.overCls =
                // 'over-form-component';
                var container = Ext.create("Ext.Container", {
                    width : parseInt(component.get("width"), 10),
                    height : parseInt(component.get("height"), 10),
                    bodyCssClass : "noborder",
                    cls : component.data.css,
                    x : x,
                    y : y,
                    id : component.get("id"),
                    componentData : component.getData(),
                    labelWidth : 100,
                    items : containerItems,
                    displayPanel : this,
                    record : component,
                    listeners : {
                        scope : this,
                        afterrender : function (container) {
                            this.addResizer(container);
                            this.addDragDrop(container);
                            container.doLayout();
                        }
                    },
                    onEdit : function () {
                        var rec = this.record;
                        if (!rec) {
                            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
                        }
                        var propComponentPanel = Ext.create("sitools.admin.forms.ComponentProp", {
                            datasetColumnModel : this.displayPanel.datasetColumnModel,
                            action : 'modify',
                            urlFormulaire : this.displayPanel.urlFormulaire,
                            context : this.displayPanel.context,
                            storeConcepts : this.displayPanel.storeConcepts,
                            record : this.record,
                            formComponentsStore : this.displayPanel.formComponentsStore,
                            absoluteLayout : this.displayPanel,
                            containerPanelId : mypanel.containerPanelId
                        });
                        propComponentPanel.show();
                    },

                    onDelete : function () {
                        var rec = this.record;
                        if (!rec) {
                            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
                        }
                        var childrenExists = false, childrens = [];
                        this.displayPanel.formComponentsStore.each(function (record) {
                            if (rec.data.id === record.data.parentParam) {
                                childrenExists = true;
                                childrens.push(record.data.label);
                            }
                        });
                        if (childrens.length > 0) {
                            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.atLeastOneChildren') + childrens.join(", "));
                            return;
                        }
                        this.displayPanel.formComponentsStore.remove(rec);
                        this.displayPanel.fireEvent("activate");
                    }
                });
                
                this.add(container);
            }
        }, this);

        this.doLayout();
//        this.addResizers(this.items.items);
//        this.addDragDrop(this.items.items);
    },
    
    onContextMenu : function (event, htmlEl, options) {
		//ici le this est le container sur lequel on a cliqu�. 
		event.stopEvent();
		var ctxMenu = new Ext.menu.Menu({
			items : [{
				text : i18n.get('label.edit'), 
				scope : this, 
				handler : this.onEdit
			}, {
				text : i18n.get('label.delete'), 
				scope : this, 
				handler : this.onDelete
			} ]
        });
		var xy = event.getXY();
		ctxMenu.showAt(xy);
    },
    
    /**
     * Add a resizer on the given component
     * @param component
     */
    addResizer : function (component) {
//            var resizer = new Ext.Resizable(component.getId(), {
        Ext.create('Ext.resizer.Resizer', {
            el : component.getEl(),
//                minWidth : 150,
//                maxWidth : 1000,
//                constrainTo : this.body,
            constrainTo : this.getEl(),
//                resizeChild : true,
            pinned : true,
            dynamic : false,
            component : component,
            heightIncrement : 5,
            widthIncrement : 5,
            listeners : {
                scope : this,
                resize : function (resizable, width, height, e) {
                    var store = this.formComponentsStore;

                    var rec = store.getAt(store.find('id', component.getId()));
                    rec.set("width", width);
                    rec.set("height", height);
                    
                    component.down("component").setSize(width - component.getEl().getPadding('l') - component.getEl().getPadding('r'), height);
                    // redimensionner dans le cas de listbox :
                    if (rec.get("type") === "LISTBOX" || rec.get("type") === "LISTBOXMULTIPLE") {
                        var multiselect = component.down('multiselect');
//                            multiselect.view.component.setHeight(height - component.getEl().getPadding('b') - component.getEl().getPadding('t') - 40);
                        multiselect.setHeight(height - component.getEl().getPadding('b') - component.getEl().getPadding('t') - 40);
                    }
                    if(rec.get("type") === "IMAGE") {
                        component.getEl().down("img").setSize(width, height);
                    }
                    //unlock drag&drop after resize
                    resizable.component.ddProxy.unlock();
                },
                beforeResize : function (resizable, width, height, e) {
                    //lock drag&drop when resizing
                    resizable.component.ddProxy.lock();
                }
            }
        });
    },
    
    /**
     * Add drag drop event and contextMenu action on each components of the fielset
     * @param components
     */
    addDragDrop : function (component) {
        component.getEl().on('contextmenu', this.onContextMenu, component);
        var dd = Ext.create("Ext.dd.DDProxy", component.getEl().dom.id, 'group', {
            isTarget : false
        });
        component.ddProxy = dd;
        Ext.apply(dd, {
            fieldSet : this,
            startDrag : function (x, y) {
                this.constrainTo(this.fieldSet.body);
                this.setXTicks(this.initPageX, 10);
                this.setYTicks(this.initPageY, 10);
            },
            afterDrag : function () {
                var dragEl = Ext.get(this.getDragEl());
                var component = Ext.get(this.getEl());

                var x = dragEl.getX();
                var y = dragEl.getY();

                var store = this.fieldSet.formComponentsStore;

                var rec = store.getAt(store.find('id', component.id));
                var PanelPos = Ext.get(this.fieldSet.body).getAnchorXY();

                rec.set("xpos", x - PanelPos[0]);
                rec.set("ypos", y - PanelPos[1]);
            }
        });
    },
    
    editPanel : function () {
        var zoneToRemove;
        if (!Ext.isEmpty(this.containerPanelId)) {
            zoneToRemove = this.absoluteLayout.zoneStore.find('containerPanelId', this.containerPanelId);
        } else {
            zoneToRemove = this.absoluteLayout.zoneStore.find('position', 0);
        }
        var zoneRec = this.absoluteLayout.zoneStore.getAt(zoneToRemove);
        
        var setupAdvancedPanel = Ext.create("sitools.admin.forms.SetupAdvancedFormPanel", {
            parentContainer : this.absoluteLayout,
            action : 'modify',
            zone : zoneRec.data
        });
        setupAdvancedPanel.show();
    },
    
    deletePanel : function () {
        var parentContainer = this.absoluteLayout;

        var zoneToRemove = parentContainer.zoneStore.find('containerPanelId', this.containerPanelId);
        parentContainer.zoneStore.removeAt(zoneToRemove);

        this.formComponentsStore.each(function (component) {
            if (!Ext.isEmpty(component)) {
                if (component.data.containerPanelId == this.containerPanelId) {
                    this.formComponentsStore.remove(component);
                }
            }
        }, this);

        parentContainer.remove(this, true);
//        this.destroy();
//        parentContainer.fireEvent('activate');
//        parentContainer.doLayout();
    }
});