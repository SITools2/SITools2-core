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

Ext.define("sitools.admin.id",{
    singleton : true,
    
    PANEL : {
        MENU : 'menuPanelId',
        TREE : 'treePanelId',
        MAIN : 'mainPanelId',
        HELP : 'helpPanelId',
        DATAVIEW : 'dataviewPanelId',
        QUICKSTART : 'quickStartId'
    },
    CMP : {
        TOOLBAR : 'toolbarId',
        MENU : 'menuId'
    },
    WIN : {
        LOGIN : 'loginWinId', 
        HELP : 'helpWinId'
    },
    BOX : {
        REG : 'regBoxId',
        USER : 'userBoxId',
        GROUP : 'groupBoxId',
        ROLE : 'roleBoxId',
        FIREWALL : 'firewallBoxId',
        PROJECTS : 'projectsBoxId',
        DATASETS : 'datasetsBoxId',
        DICTIONARY : 'dictionaryBoxId',
        TEMPLATE : 'templateBoxId',
        DATABASE : 'databaseBoxId',
        FORMS : 'formsBoxId',
        APPLICATION : 'applicationsBoxId',
        USERSTORAGE : 'userStorageBoxId',
        GRAPHS : 'graphsBoxId',
        CONVERTERS : 'convertersBoxId',
        RSSPROJECTS : "rssProjectsId", 
        STORAGES : "storagesBoxId",
        PROJECTPLUGIN : "projectPluginBoxId",
        UNITS : "unitsBoxId", 
        DATASETVIEW : "datasetViewBoxId",
        PROJECTMODULE : "projectModuleBoxId",
        PROJECTMODULECONFIG : "projectModuleConfigBoxId",
        FILEEDITORFTL : "fileEditorFtlBoxId",
        FILEEDITORCSS : "fileEditorCssBoxId",
        FILEEDITORLICENCE : "fileEditorLicenceBoxId", 
        COLLECTIONS : "collectionBoxId", 
        MULTIDS : "multiDsBoxId", 
        APPLICATIONPLUGIN : "applicationPluginBoxId",
        GUISERVICES : "guiServicesBoxId",
        USERSTORAGE : "userStorageBoxId",
        ORDER : 'orderBoxId',
        AUTHORIZATION : 'authorizationBoxId',
        MONGODB : 'mongoDBBoxId',
        RSSFEED : 'rssFeedBoxId'
    },
    PROP : {
        DATASETVIEW : "datasetViewPropId",
        PROJECTMODULE : "projectModulePropId",
        FILEEDITORPROP : "fileEditorBoxId", 
        MULTIDSPROP : "multiDsPropId",
        GUISERVICES : "guiServicesPropId"
    },
    COMPONENT_SETUP : {
        DATABASE : 'cmpSetupDatabaseId', 
        PROJECT : 'cmpSetupProjectId', 
        OPENSEARCH : 'cmpOpenSearchId', 
        COLLECTIONS : "cmpCollectionsId", 
        MULTIDS : "cmpMultiDsId"
    },
    ITEM : {
        OKBUTTON : 'okButtonId',
        CANCELBUTTON : 'cancelButtonId'
    }
});

var ID = sitools.admin.id;