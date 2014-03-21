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
Ext.namespace('sitools.component.projects');

/**
 * Create, or Edit a Project 
 * @cfg {String} url the url to request the project,
 * @cfg {string} action should be "view", "modify", "create"
 * @cfg {Ext.data.Store} store the store that contains all projects
 * @cfg {string} projectName the name of the selected project if action != "create"
 * @cfg {string} projectAttachement the sitoolsAttachement of the edit project. 
 * @class sitools.component.projects.ProjectsPropPanel
 * @extends Ext.Window
 */
Ext.define('sitools.component.projects.ProjectsPropPanel', { 
    extend : 'Ext.Window',
	alias : 'widget.s-projectsprop',
    /** Default Width */
    width : 700,
    /** Default height */
    height : 600,
    /** Default modal */
    modal : true,
    /** Default pageSize for Dataset Store */
    pageSize : 10,
    /** Default id */
    id : ID.COMPONENT_SETUP.PROJECT,
    /** Default Value */
    allModulesDetached : false, 
    /** Default Value when creating a project. */
    defaultValueTpl : "default.project.ftl", 
    /** Default Value when creating a project. */
    defaultDescription : "SITools2 est une plate-forme web conviviale permettant de mettre en place un système de recherche et d'accès aux données à partir d'une ou plusieurs bases de données existantes. SiTools2 permet de prendre en compte et de s'adapter aux structures de nombreuses bases de données qui sont gérées dans divers centres scientifiques, et permet d'éviter des processus lourds et complexes de migration de données. <div class='field-items'> <p>L'architecture de cette plate-forme est composée&nbsp;:</p> <ol> <li>d'un serveur de données exposant des ressources,&nbsp; </li><li>d'une interface web pour l'administrateur permettant de configurer l'ensemble des fonctionnalités du serveur,&nbsp; </li><li>d'une interface web pour les utilisateurs comportant un portail qui liste les projets, avec un bureau pour chaque projet qui expose l'ensemble des services mis à disposition par l'administrateur,&nbsp; </li><li>d'un mécanisme de plugins permettant aux développeurs d'ajouter des fonctionnalités métiers aussi bien au niveau du serveur qu'au niveau du client et de les partager avec une communauté d'utilisateurs.&nbsp; </li></ol> <p>SITools2 s'articule autour de trois concepts importants&nbsp;:</p> <ul> <li>la source de données&nbsp;: infrastructure contenant les données (actuellement une base de données relationnelle accessible via l'API JDBC), </li><li>le jeu de données&nbsp;: exposition d'un sous-ensemble de la source de données par l'intermédiaire d'un service web, </li><li>le projet&nbsp;: ensemble de jeux de données. </li></ul> <p>Des services peuvent être ensuite définis à partir de ces trois concepts&nbsp;:</p> <ul> <li>définition et exposition du formulaire de recherche, </li><li>définition et exposition de la recherche OpenSearch, </li><li>définition et exposition des fonctions de conversion (unité, fonction de transfert), </li><li>définition et exposition des fonctions de filtrage, </li><li>définition et exposition de dictionnaires de données, </li><li>définition et exposition de flux RSS, </li><li>définition et exposition des plugins. </li></ul> <p>Comme tout système d'accès, il est important de pouvoir sécuriser l'accès à certaines ressources selon le profil de l'utilisateur. C'est pourquoi SITools2 implémente une gestion complète des utilisateurs (information personnalisable, espace de stockage sur le serveur de données) et permet de sécuriser l'ensemble des ressources en fonction du rôle de chaque utilisateur.</p></div>", 
    /** Default Value when creating a project. */
    defaultHeader : '<div id="top-header" style="background-color: black;"> <a href="http://www.cnes.fr" target="_blank"> <img src="/sitools/common/res/images/entete_cnes.png" style="border: none;"> </a> </div>', 
    /** Default Value when creating a project. */
    defaultLinks : [{
        name : "label.legalInformations", 
        url : "/sitools/common/html/legalInformations.html"
    }, {
        name : "label.personalInformations", 
        url : "/sitools/common/html/personalInformations.html"
    }, {
        name : "label.contacts", 
        url : "/sitools/common/html/contacts.html"
    }, {
        name : "label.help", 
        url : "/sitools/common/html/help.html"
    }, {
        name : "label.editorialInformations", 
        url : "/sitools/common/html/editorialInformations.html"
    }], 
    initComponent : function () {
        var action = this.action;
        if (this.action === 'view') {
            this.title = i18n.get('label.viewProject');
        }
        if (this.action === 'modify') {
            this.title = i18n.get('label.modifyProject');            
        }
        if (this.action === 'create') {
            this.title = i18n.get('label.createProject');
        }
        if (this.action === 'duplicate') {
            this.title = i18n.get('label.duplicateProject');
        }

        var storeDataSets = new Ext.data.JsonStore({
            id : 'storeDataSets',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'mediaType',
                type : 'string'
            }, {
                name : 'visible',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'properties'
            }, {
                name : 'url',
                type : 'string'
            } ]
        });

        var cmDataSets = [{
            header : i18n.get('headers.name'),
            dataIndex : 'name',
            width : 100,
            sortable : true
        }, {
            header : i18n.get('headers.description'),
            dataIndex : 'description',
            width : 100,
            sortable : true
        }];

        var smDataSets = Ext.create('Ext.selection.RowModel', {
            singleSelect : false
        });
        var tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.add'),
                hidden : this.mode === 'select',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this._onCreate
            }, {
                text : i18n.get('label.remove'),
                hidden : this.mode === 'select',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete
            }, '->', {
                xtype : 's-filter',
                hidden : this.mode === 'list',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        /**
         * {Ext.grid.EditorGridPanel} gridDataSets The grid that displays datasets
         */
        this.gridDataSets = Ext.create('Ext.grid.Panel', {
            name : 'gridDataSets',
            title : i18n.get('title.gridDataSets'),
            store : storeDataSets,
            tbar : tbar,
            columns : cmDataSets,
            selModel : smDataSets,
            forceFit : true,
            listeners : {
                "activate" : function () {
                    if (action === 'view') {
                        this.getEl().mask();
                    }
                }
            }
        });

        /**
         * {Ext.FormPanel} formProject The main Form 
         */
        this.formProject = new Ext.FormPanel({
            title : i18n.get('label.projectInfo'),
            name : 'formProperties',
            xtype : 'form',
            border : false,
            autoScroll : true, 
            padding : 10,
            trackResetOnLoad : true,
            items : [ {
                xtype : 'hidden',
                name : 'id'
            }, {
                xtype : 'hidden',
                name : 'maintenance'
            }, {
                xtype : 'textfield',
                name : 'name',
                fieldLabel : i18n.get('label.name'),
                anchor : '100%',
                maxLength : 30,
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'description',
                fieldLabel : i18n.get('label.description'),
                anchor : '100%'
//                ,
//                maxLength : 100
            }, {
                xtype : 'textfield',
                vtype : "attachment",
                name : 'sitoolsAttachementForUsers',
                fieldLabel : i18n.get('label.userAttach'),
                anchor : '100%',
                maxLength : 100
            }, {
                xtype : 'sitoolsSelectImage',
                name : 'image',
                fieldLabel : i18n.get('label.image'),
                anchor : '100%',
                growMax : 400
            }, {
                xtype : 'checkbox',
                name : 'visible',
                fieldLabel : i18n.get('label.isVisible'),
                anchor : '100%',
                maxLength : 100
            }, {
                xtype : 'textarea',
                name : 'htmlDescription',
                cls : 'ckeditor',
                fieldLabel : i18n.get('label.descriptionHTML'),
                height : 150,
                anchor : '95%', 
                value : this.defaultDescription
            }, {
                xtype : 'textarea',
                cls : 'ckeditor',
                name : 'maintenanceText',
                fieldLabel : i18n.get('label.maintenanceText'),
                height : 150,
                anchor : '95%'
            } ], 
            listeners : {
                "activate" : function () {
                    if (action === 'view') {
                        this.getEl().mask();
                    }
                }
            }

        });
        
        /**
         * {Ext.FormPanel} formProject The main Form 
         */
        this.ihmProfile = new Ext.FormPanel({
            title : i18n.get('label.ihmProfile'),
            xtype : 'form',
            border : false,
            autoScroll : true, 
            padding : 10,
            trackResetOnLoad : true,
            items : [ {
                xtype: 'radiogroup',
                fieldLabel: i18n.get('label.navigationMode'),
                items: [
                    {boxLabel: i18n.get('label.desktop'), name: 'navigationMode', inputValue: "desktop", checked : true},
                    {boxLabel: i18n.get('label.fixed'), name: 'navigationMode', inputValue: "fixed"}
                ]
            }, {
                xtype : 'textfield',
                name : 'css',
                fieldLabel : i18n.get('label.css'),
                anchor : '100%',
                maxLength : 100
            }, {
                xtype : 'textfield',
                name : 'ftlTemplateFile',
                fieldLabel : i18n.get('label.ftlTemplateFile'),
                anchor : '100%', 
                tooltip : i18n.get("label.projectTemplateHelp"), 
                value : this.defaultValueTpl
            }, {
                xtype : 'textarea',
                name : 'htmlHeader',
                cls : 'ckeditor',
                fieldLabel : i18n.get('label.htmlHeader'),
                height : 150,
                anchor : '100%', 
                value : this.defaultHeader
            } ], 
            listeners : {
                "activate" : function () {
                    if (action === 'view') {
                        this.getEl().mask();
                    }
                }
            }

        });
        
        this.allModulesAttachedBtn = new Ext.Button({
            text : this.allModulesDetached ? i18n.get('label.allDetached') : i18n.get('label.allAttached'),
            hidden : this.mode === 'select',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
            handler : this._onAllModulesAttached, 
            scope : this
        });
        
        
        this.listRolesBtn = new Ext.Button({
            text : i18n.get('label.rolecrud'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_role.png',
            scope : this,
            handler : this._onRoles
        });
        
        var tbarModules = {
            xtype : 'sitools.widget.GridSorterToolbar',
            gridId : "gridModules", 
            items: [this.allModulesAttachedBtn, this.listRolesBtn]
        };
        
        // The store that contains all distinct Modules
        var storeAllModules = new Ext.data.JsonStore({
            root : 'data',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_MODULES_URL'),
            restful : true,
            remoteSort : false,
            autoSave : false,
            autoLoad : true, 
            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : "attached", 
                type : "boolean"
            }, {
                name : "listRoles"
            }, {
                name : "categoryModule"
            }, {
                name : "divIdToDisplay"
            }, {
                name : 'xtype',
                type : 'string'
            }, {
                name : 'moduleConfig'
            }, {
                name : 'label'
            }, {
                name : 'dependencies'
            }], 
            listeners : {
                scope : this, 
                load : function (store) {
                    this.loadNonAvailableProjects(store);
                    if (this.action === "create") {
                        this._onAllModulesAttached();
                        var rec;
                        //renseigner artificiellement les deux specific div du template
                        var storeProject = this.modulePanel.getStore();
                        var recDsExplorerIdx = storeProject.find("name", "DataSetExplorer");
                        if (recDsExplorerIdx !== -1) {
                            rec = storeProject.getAt(recDsExplorerIdx);
                            rec.set("divIdToDisplay", "news");
                        }
                        var recNewsIdx = storeProject.find("name", "ProjectsFeeds");
                        if (recNewsIdx !== -1) {
                            rec = storeProject.getAt(recNewsIdx);
                            rec.set("divIdToDisplay", "shortcuts");
                        }
                    }
                }
            }
        });
        
        // The store that contains modules for this project. 
        this.storeProjectModules = new Ext.data.JsonStore({
            autoLoad : false, 
            idProperty : 'id',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : "attached", 
                type : "boolean"
            }, {
                name : "listRoles"
            }, {
                name : "categoryModule"
            }, {
                name : "divIdToDisplay"
            }, {
                name : 'xtype',
                type : 'string'
            }, {
                name : 'listProjectModulesConfig'
            }, {
                name : 'label',
                type : 'string'
            },
            {
                name : 'dirty',
                type : 'boolean'
            }], 
            listeners : {
                scope : this,
                add : function (store, records, index) {
                    if (store.allAttached()) {
                        this.allModulesAttachedBtn.setText(i18n.get("label.Detached"));
                        this.allModulesDetached = true;
                    }
                    if (store.allDetached()) {
                        this.allModulesAttachedBtn.setText(i18n.get("label.Attached"));
                        this.allModulesDetached = false;
                    }
                },
                update : function (store, record) {
                    var index = store.indexOf(record);
                    if (store.allAttached()) {
                        this.allModulesAttachedBtn.setText(i18n.get("label.Detached"));
                        this.allModulesDetached = true;
                    }
                    if (store.allDetached()) {
                        this.allModulesAttachedBtn.setText(i18n.get("label.Attached"));
                        this.allModulesDetached = false;
                    }
                }
            }, 
            allAttached : function () {
                var result = true;
                this.each(function (record) {
                    if (record.get("attached") === false) {
                        result = false;
                        return false;
                    }
                });
                return result;
            }, 
            allDetached : function () {
                var result = true;
                this.each(function (record) {
                    if (record.get("attached") === true) {
                        result = false;
                        return false;
                    }
                });
                return result;
            }
        });
        
        var attached = Ext.create('Ext.grid.column.CheckColumn', {
            header : i18n.get('headers.attached'),
            dataIndex : "attached",
            width : 55
        });
        var visible = {
            xtype : 'checkcolumn',
            header : i18n.get('headers.visible'),
            dataIndex : "visible",
            width : 55
        };
        
        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 2
        });
        
        var columnsProjectModules = [/*visible,*/ {
            header : i18n.get('headers.name'),
            dataIndex : 'name',
            width : 100
        }, {
            header : i18n.get('headers.description'),
            dataIndex : 'description',
            width : 100
        }, {
            header : i18n.get('headers.label'),
            dataIndex : 'label',
            width : 100, 
            editor : {
                xtype : 'textfield'
            }
        }, {
            header : i18n.get('headers.category') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'categoryModule',
            width : 100, 
            editor : {
                xtype : 'textfield'
            }
        }, {
            header : i18n.get('headers.divIdToDisplay') + ' <img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
            dataIndex : 'divIdToDisplay',
            width : 100, 
            editor : {
                xtype : 'textfield'
            }
        }, {
            xtype : 'actioncolumn',
            header : i18n.get('headers.projectModuleParameters'),
            width : 100,
            items : [{
                icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_projects_resources.png",
                scope : this,
                handler : function (grid, row, col, item, e) {
                    this.modulePanel.getSelectionModel().selectRow(row);
                    var rec = this.modulePanel.getSelectionModel().getLastSelected();
                    this._onModuleConfig(rec);
                }
            }],
            scope : this,
            renderer : function (value, metadata, record, rowInd, colInd, store) {
                if (record.data.xtype) {
                    try {
                        var getParametersMethod = eval(record.data.xtype + ".getParameters");
                        if (!Ext.isFunction(getParametersMethod)) {
                            metadata.style = 'display:none;';
                        }
                    }
                    catch (err) {
                        metadata.style = 'display:none;';
                        return;
                    }
                }
                return;
            }
        }];
        
        /**
         * {Ext.grid.GridPanel} modulePanel The grid that displays modules
         */
        this.modulePanel = Ext.create('Ext.grid.Panel', {
            id : 'gridModules',
            title : i18n.get('title.modules'),
            store : this.storeProjectModules,
            columns : columnsProjectModules,
            forceFit : true,
            selModel : Ext.create('Ext.selection.CheckboxModel', {
                listeners : {
                    select : function (selModel, record, index) {
                        record.data.attached = true;
                    },
                    deselect : function (selModel, record, index) {
                        record.data.attached = false;
                    }
                }
            }),
            columnLines: true,
            tbar : tbarModules, 
            viewConfig : {
//                getRowClass : function (row, index) { 
//                    var cls = ''; 
//                    var data = row.data; 
//                    if (data.dirty) {
//                        cls = "red-row";
//                    }
//                    return cls; 
//                } 
            }, 
            listeners : {
                "activate" : function () {
                    if (action == 'view') {
                        this.getEl().mask();
                    }
                }
            }, 
            plugins : [cellEditing]
        });
        
        var storeLinks = new Ext.data.JsonStore({
            idProperty : 'name',
            fields : [ {
                name : 'name',
                type : 'string'
            }, {
                name : 'url',
                type : 'string'
            }]
        });
        
        var linksPanelTbar = {
                xtype : 'sitools.widget.GridSorterToolbar',
                gridId : "linksPanelGrid",
                items : [ {
                    text : i18n.get('label.create'),
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                    handler : this.onCreateLink,
                    scope : this
                }, {
                    text : i18n.get('label.delete'),
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                    handler : this.onDeleteLink,
                    scope : this
                }]
            };
        
        this.linksPanel = Ext.create('Ext.grid.Panel', {
            title : i18n.get('label.links'),
            store : storeLinks,
            tbar : linksPanelTbar, 
            id : "linksPanelGrid",
            forceFit : true,
            plugins : [
               Ext.create('Ext.grid.plugin.CellEditing', {
                   clicksToEdit: 2
            })],
            columns : [{            
                    header : i18n.get('headers.name'),
                    dataIndex : 'name',
                    width : 100,
                    editor : {
                        xtype : 'textfield',
                        allowBlank : false
                    }
                }, {
                    header : i18n.get('headers.url'),
                    dataIndex : 'url',
                    width : 100,
                    editor : {
                        xtype : 'textfield',
                        allowBlank : false
                    }
            }],
            selModel : Ext.create('Ext.selection.RowModel', {
                singleSelect : true
            }),
            listeners : {
                "activate" : function () {
                    if (action == 'view') {
                        this.getEl().mask();
                    }
                }
            }
        });
        
        /**
         * {Ext.TabPanel} tabPanel the main Item of the window
         */
        this.tabPanel = new Ext.TabPanel({
            height : 550,
            activeTab : 0,
            items : [this.formProject, this.ihmProfile, this.gridDataSets, this.modulePanel, this.linksPanel ],
            deferredRender : false,
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate, 
                hidden : this.action == "view"
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]        
        });
        
        this.items = [this.tabPanel];
        
        this.listeners = {
            scope : this, 
            resize : function (window, width, height) {
                var size = window.body.getSize();
                this.tabPanel.setSize(size);
            },
            beforedestroy : function () {
                this.destroyCkeditor();
            }
        };        
        sitools.component.projects.ProjectsPropPanel.superclass.initComponent.call(this);
    },
    /**
     * Create a {sitools.admin.projects.datasetsWin} datasetWindow to add datasets
     */
    _onCreate : function () {
        var up = new sitools.admin.projects.datasetsWin({
            mode : 'select',
            url : loadUrl.get('APP_URL') + '/datasets',
            storeDatasets : this.gridDataSets.getStore()
        });
        up.show(this);

    },
    /**
     * Delete a selected Dataset
     * @return {}
     */
    _onDelete : function () {
        var recs = this.gridDataSets.getSelectionModel().getSelections();
        if (recs.length === 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        this.gridDataSets.getStore().remove(recs);
    },
    
    _onRoles : function () {
        var rec = this.modulePanel.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var gp = new sitools.admin.usergroups.RolesPanel({
            mode : 'list',
            rec : rec
        });
        gp.show(ID.BOX.ROLE);
    },
    
    _onModuleConfig : function () {
        var rec = this.modulePanel.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var mc = new sitools.admin.projects.modules.ProjectModuleConfig({
            module : rec
        });
        mc.show();
    },
    
    onUpload : function () {
        function validate(data, config) {
            config.fieldUrl.setValue(data.url);
        }
        var chooser = new ImageChooser({
            url : loadUrl.get('APP_URL') + '/client-admin/res/json/componentList.json',
            width : 515,
            height : 350,
            fieldUrl : this.ownerCt.items.items[0]
        });
        chooser.show(document, validate);
    },
    /**
     * Check the validity of form
     * and call onSaveProject method
     * @return {Boolean}
     */
    onValidate : function () {
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        this.refreshTextAreaValues();
        if (this.action == 'modify') {
            var name = f.findField("name").getValue();
            var originalName = f.findField("name").originalValue;
            if (originalName != name) {
                Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    buttons : Ext.Msg.YESNO,
                    msg : i18n.get('projectProp.warning.projectName.changed'),
                    scope : this,
                    fn : function (btn, text) {
                        if (btn == 'yes') {
                            this.onSaveProject();
                        }
                    }
                });
            } else {
                this.onSaveProject();
            }
        } else {
            this.onSaveProject();
        }

    },
    /**
     * Build the object to save project. 
     * Then call a PUT or POST request (depending on action) to save project.
     */        
    onSaveProject : function () {
        
        var f = this.down('form').getForm();
        var putObject = {};
        Ext.iterate(f.getValues(), function (key, value) {
            if (key == 'image') {
                // TODO : definir une liste de mediaType et type
                putObject.image = {};
                putObject.image.url = value;
                putObject.image.type = "Image";
                putObject.image.mediaType = "Image";
            } else if (key != "visible") {
                putObject[key] = value;
            }
        }, this);
        
        //visible field handling
        var visibleField = f.findField("visible");
        putObject.visible = visibleField.getValue();
        
        f = this.ihmProfile.getForm();
        
        Ext.iterate(f.getValues(), function (key, value) {
            putObject[key] = value;
        }, this);
//        //navigation Mode 
//        var navigationMode = f.findField("visible");
//        putObject.visible = visibleField.getValue();

        
        var store = this.down('grid[name=gridDataSets]').getStore();
        if (store.getCount() > 0) {
            putObject.dataSets = [];
            store.each(function (record) {
                putObject.dataSets.push({
                    id : record.data.id,
                    description : record.data.description,
                    name : record.data.name,
                    mediaType : 'DataSet',
                    type : 'DataSet',
                    visible : record.data.visible, 
                    status : record.data.status, 
                    properties : record.data.properties, 
                    url : record.data.url
                });
            });
        }
        var storeModule = this.modulePanel.getStore();
        var correctlyParamModule = true;
        if (storeModule.getCount() > 0) {
            putObject.modules = [];
            var i = 0;
            storeModule.each(function (record) {
                if (record.data.attached) {
                    
                    if (Ext.isEmpty(record.data.listRoles)) {
                        record.data.listRoles = undefined;
                    }
                    record.set('dirty', false);

                    // Si la méthode getParameter du projectModule est implémenté on récupère les paramètres
                    if (record.data.xtype) {
                        var getParametersMethod;
                        try {
                            if (!Ext.isFunction(eval(record.data.xtype))) {
                                record.set('dirty', true);
                                Ext.Msg.alert(i18n.get('label.warning'), String.format(i18n.get('label.undefinedModule'), record.data.name));
                                correctlyParamModule = false;
                            }
                            getParametersMethod = eval(record.data.xtype + ".getParameters");
                        }
                        catch (err) {
                            var tmp = null;
                        }
                        if (Ext.isFunction(getParametersMethod)) {
                            var listProjectModulesConfig;
                            if (record.data.listProjectModulesConfig != undefined && record.data.listProjectModulesConfig.length != 0){
                                listProjectModulesConfig = record.data.listProjectModulesConfig;

                                putObject.modules.push({
                                    description : record.data.description,
                                    id : record.data.id,
                                    name : record.data.name,
                                    visible : record.data.visible,
                                    priority : i, 
                                    listRoles : record.data.listRoles, 
                                    categoryModule : record.data.categoryModule, 
                                    divIdToDisplay : record.data.divIdToDisplay,
                                    xtype : record.data.xtype,
                                    listProjectModulesConfig : listProjectModulesConfig,
                                    label : record.data.label
                                });
                            }
                            else {
//                                var ind = storeModule.indexOf(record);
//                                var row = this.modulePanel.getView().getRow(ind);
//                                row.style.color = "#FF0033";
                                record.set('dirty', true);
                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.needToParamModule'));
                                correctlyParamModule = false;
                                
                                var htmlLineEl = this.modulePanel.getView().getNode(record);
                                var el = Ext.get(htmlLineEl);
                                el.addCls('red-row');
                            }
                            
                        }
                        else {
                            putObject.modules.push({
                                description : record.data.description,
                                id : record.data.id,
                                name : record.data.name,
                                visible : record.data.visible,
                                priority : i, 
                                listRoles : record.data.listRoles, 
                                categoryModule : record.data.categoryModule, 
                                divIdToDisplay : record.data.divIdToDisplay,
                                xtype : record.data.xtype,
                                label : record.data.label
                            });
                        }
                    }
                    else {
                        putObject.modules.push({
                            description : record.data.description,
                            id : record.data.id,
                            name : record.data.name,
                            visible : record.data.visible,
                            priority : i, 
                            listRoles : record.data.listRoles, 
                            categoryModule : record.data.categoryModule, 
                            divIdToDisplay : record.data.divIdToDisplay,
                        });
                    }
                    
                    i++;
                }
            }, this);
        }
        
        var storeLinks = this.linksPanel.getStore();
        if (storeLinks.getCount() > 0) {
            putObject.links = [];
            storeLinks.each(function (record) {
                putObject.links.push({
                    name : record.get("name"),
                    url : record.get("url")
                });                
            });
        }
                
        var method = (this.action == 'modify') ? "PUT" : "POST";
        
        if (correctlyParamModule) {
            Ext.Ajax.request({
                url : this.url,
                method : method,
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    if (data.success === false) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    } else {
                        this.close();
                        this.store.reload();
                    }
                    // Ext.Msg.alert(i18n.get('label.information'),
                    // i18n.get('msg.uservalidate'));
                },
                failure : alertFailure
            });
        }
        

    },
    /**
     * do a specific render to fill informations from the project.
     */
    afterRender : function () {
        sitools.component.projects.ProjectsPropPanel.superclass.afterRender.apply(this, arguments);
        if (this.url) {
            var url = this.url;
            if (this.action == 'duplicate') {
                url = this.projectUrlToCopy;
            }
            
            // var gs = this.groupStore, qs = this.quotaStore;
            if (this.action == 'modify' || this.action == "view" || this.action == "duplicate") {
                Ext.Ajax.request({
                    url : url,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var f = this.down('form').getForm();
                        var grid = this.down('grid[name=gridDataSets]');
                        var store = grid.getStore();
                        var data = Ext.decode(ret.responseText).project;
                        var dataSets = data.dataSets;


                        // Chargement des dataSets disponible et mise a jour de
                        Ext.each(dataSets, function (dataSet) {
                            var rec = {};
                            rec.id = dataSet.id;
                            rec.name = dataSet.name;
                            rec.description = dataSet.description;
                            rec.type = dataSet.description;
                            rec.mediaType = dataSet.mediaType;  
                            rec.status = dataSet.status;
                            rec.visible = dataSet.visible;
                            rec.properties = dataSet.properties;
                            rec.url = dataSet.url;

                            store.add(rec);
                        });
                        // ceuw attaches au projet

                        var rec = {};
                        
                        if (this.action == "duplicate") {
                            rec.name = data.name + "_copy";
                        } else {
                            rec.id = data.id;
                            rec.name = data.name;
                            rec.sitoolsAttachementForUsers = data.sitoolsAttachementForUsers;
                        }
                        
                        rec.description = data.description;
                        rec.image = data.image.url;
                        rec.visible = data.visible;
                        rec.htmlDescription = data.htmlDescription;
                        rec.maintenanceText = data.maintenanceText;
                        rec.maintenance = data.maintenance;
                        
                        f.setValues(rec);
                        
                        rec = {};
                        rec.css = data.css;
                        rec.htmlHeader = data.htmlHeader;
                        rec.ftlTemplateFile = data.ftlTemplateFile;
                        rec.navigationMode = data.navigationMode;
                        f = this.ihmProfile.getForm();
                        f.setValues(rec);
                        //Mise a jour manuelle de la combo 
                        try {
                            this.ihmProfile.find('xtype', 'radiogroup')[0].setValue(rec.navigationMode);
                        }
                        catch (err) {
                            var tmp;
                        }
                        
                        //Chargement manuel du store de project Module
                        if (!Ext.isEmpty(data.modules)) {
                            Ext.each(data.modules, function (module) {
                                var rec = Ext.apply(module, {
                                    attached : true
                                });
                                
                                this.modulePanel.getStore().add(rec);
                            }, this);
                            this.modulePanel.getSelectionModel().select(this.modulePanel.getStore().data.items);
                        }
                        
                        var links = data.links;
                        var storeLinks = this.linksPanel.getStore();
                        if (!Ext.isEmpty(links)) {
                            Ext.each(links, function (item) {
                                storeLinks.add(item);
                            }, this);
                        }
                        this.applyCkeditor();
                        
                    },
                    failure : function (ret) {
                        var data = Ext.decode(ret.responseText);
                        Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                    }
                });
            }
            
            if (this.action === "create") {
                this.fillDefaultLinks();
                this.applyCkeditor();
            }
        }
    }, 
    fillDefaultLinks : function () {
        Ext.each(this.defaultLinks, function (link) {
            this.linksPanel.getStore().add(link);
        }, this);
    },
    /**
     * Will fill information from the project to the modulesRecords. 
     * Then will call a sort on the store  
     * @param {[Ext.data.Records]} modulesRecords an Array of records loaded from request to modules application
     * @param {} project the project object containing projectModules
     */
    displayProjectModules : function (modulesRecords, project) {
        if (this.action === 'create') {
            this.modulePanel.getStore().each(function (rec) {
                rec.set("attached", true);
            });
        }
        else {
            Ext.each(modulesRecords, function (moduleRec) {
                moduleRec.set("priority", modulesRecords.length + 1);
                Ext.each(project.modules, function (projectModule) {
                    if (modulerec.data.id == projectModule.id) {
                        moduleRec.set("attached", true);
                        moduleRec.set("priority", projectModule.priority);
                        moduleRec.set("listRoles", projectModule.listRoles);
                        moduleRec.set("divIdToDisplay", projectModule.divIdToDisplay);
                        moduleRec.set("categoryModule", projectModule.categoryModule);
                        moduleRec.set("xtype", projectModule.xtype);
                        return false;
                    }
                });            
            });
        }
        this.modulePanel.getStore().sort('priority');
    }, 
    /**
     * Called when this.allModulesAttachedBtn is pressed
     */
    _onAllModulesAttached : function () {
        var attach = this.allModulesDetached;
        this.modulePanel.getStore().each(function (rec) {
            rec.set("attached", !attach);
        }, this);
        if (!attach) {
            this.modulePanel.getSelectionModel().selectAll();
        } else {
            this.modulePanel.getSelectionModel().deselectAll();
        }
    }, 
    
    /**
     * Add a new Record to the dependencies property of a project module
     */
    onCreateLink : function () {
        this.linksPanel.getStore().insert(this.linksPanel.getStore().getCount(), {});
    },
    
    /**
     * Delete the selected dependency of a project module
     */
    onDeleteLink : function () {
        var s = this.linksPanel.getSelectionModel().getSelections();
        var i, r;
        for (i = 0; s[i]; i++) {
            r = s[i];
            this.linksPanel.getStore().remove(r);
        }
    }, 
    loadNonAvailableProjects : function (store) {
        var listDependencies = [];
        store.each(function (rec) {
            // Chargement des dependances pour permettre le parametrage du module dans le projet 
            // pour eviter de recharger la page
            
                if (!Ext.isEmpty(rec.get('dependencies') && !Ext.isEmpty(rec.get('dependencies').js))) {
                    listDependencies = listDependencies.concat(rec.get('dependencies').js);
                }
                
                if (this.storeProjectModules.findExact("name", rec.get('name')) === -1) {
                    this.storeProjectModules.add(rec);
                }
            }, this);
        
        if (!Ext.isEmpty(listDependencies)) {
            includeJsForceOrder(listDependencies, 0, function () {
//                this.modulePanel.getView().refresh();
            }, this);
        }
    },
    

    applyCkeditor : function () {
        // Selectively replace <textarea> elements, based on
        // custom assertions.
        CKEDITOR.replaceAll(function (textarea, config) {
            if (!Ext.isEmpty(textarea.classList) && textarea.classList.contains("ckeditor")) {
                config.customConfig = 'config-basic.js';
                config.width = "95%";
                // false to hide datasetLink label in link.js combo
                config.displayDatasetLink = false;
                return true;
            } else {
                return false;
            }
        });
    },
    
    refreshTextAreaValues : function () {
        Ext.iterate(CKEDITOR.instances, function(key, instance){
            instance.updateElement();
        });
    },

    destroyCkeditor : function () {
        Ext.iterate(CKEDITOR.instances, function (key, instance) {
            if (!Ext.isEmpty(instance)) {
                instance.destroy();
                CKEDITOR.remove(instance);
            }
            instance.updateElement();
        });
    }
});
