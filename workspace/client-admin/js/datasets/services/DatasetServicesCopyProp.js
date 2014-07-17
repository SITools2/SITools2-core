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
 * @include "../def.js"
 */

Ext.namespace('sitools.admin.resourcesPlugins');

/**
 * A window that displays Storages properties.
 * 
 * @cfg {string} url The url to Save the data
 * @cfg {string} action The action should be modify or create
 * @cfg {Ext.data.Store} store The storages store 
 * @class sitools.admin.datasets.services.DatasetServicesCopyProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.services.DatasetServicesCopyProp', { 
    extend : 'Ext.Window', 
	alias : 'widget.s-storage_copy',
	width : 350,
	height : 250,
	layout : "fit",
    modal : true,
    autoHeight : true,
    autoScroll : true,
    initComponent : function () {
        
    	this.title = i18n.get('label.duplicateDatasetServices');
    	
    	this.fieldset = Ext.create("Ext.form.FieldSet", {
    	    xtype : 'fieldset',
            name : 'fieldsetCopiedServices',
            title : i18n.get('label.servicesCopied'),
            hidden : true,
            autoScroll : true
    	});
    	
    	this.items = [{
    		xtype : 'form',
    		id : 'formCopyId',
    		frame: false,
			border: false,
			bodyBorder : false,
			padding : 5,
			buttonAlign: 'center',
    		items : [{
    			xtype : 'label',
    			text : i18n.get('label.datasetDestination')
    		},{
    			xtype : 'combo',
    			id: 'datasetDestId',
    			store : this.storeCombo,
                displayField : 'name',
                valueField : 'id',
                typeAhead : true,
                queryMode : 'local',
                forceSelection : true,
                triggerAction : 'all',
                selectOnFocus : true,
                anchor : "100%"
    		}, this.fieldset]
    	}];
    	
    	this.buttons = [
	    {
			xtype : 'button',
			iconAlign : 'right',
			text : i18n.get('label.copy'),
			icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/converter.png',
			copyFinish : false,
			scope : this,
			handler : this.runCopy
		}];
    	
    	this.servicesIHM = [];
    	this.servicesSERVER = [];
    	Ext.each(this.services, function (service) {
    	    if (service.data.type == 'GUI') {
    	        this.servicesIHM.push(service);
    	    } else {
    	        this.servicesSERVER.push(service);
    	    }
    	}, this);
    	
    	
        sitools.admin.datasets.services.DatasetServicesCopyProp.superclass.initComponent.call(this);
    },
    
    afterRender : function () {
        sitools.admin.datasets.services.DatasetServicesCopyProp.superclass.afterRender.apply(this, arguments);        
        this.getAllServicesDescription();
    },
    
    runCopy : function () {
    	var f = Ext.getCmp('formCopyId').getForm();
        this.idDest = f.findField('datasetDestId').getValue();
        
        if (Ext.isEmpty(this.idDest)) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        
        Ext.each(this.services, function (service) {
            this.fieldset.add({
                xtype : 'label',
                id : service.data.id,
                html : '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/loading.gif"/> ' + service.data.name + "<br>"
            });
        }, this);
        this.fieldset.setVisible(true);
        this.fieldset.doLayout();
        
        this.executeCopy();
       
    },
    
    executeCopy : function () {
        if (Ext.isEmpty(this.services)) {
            var buttonToolbar = this.down('button'); 
            buttonToolbar.copyFinish = true;
            buttonToolbar.setText(i18n.get('label.close'));
            buttonToolbar.setHandler(function () {
                this.storeServices.load();
                this.close();
            }, this);
            return;
        }
        
        var service = this.services[0];
        
        var url;
        if (service.data.type == 'GUI') {
            url = this.urlDatasetServiceGUI.replace('{idDataset}', this.idDest);
        } else {
            url = this.urlDatasetServiceSERVER.replace('{idDataset}', this.idDest);
        }
        
        delete service.data.id;
        delete service.data.parent;
        delete service.data.type;
        delete service.data.category;
        
        Ext.Ajax.request({
            url : url,
            method : 'POST',
            jsonData : service.data,
            scope : this,
            success : function (ret) {
                var fieldService = this.fieldset.down('label[id=' + service.internalId + ']');
                if (ret.status == 200) {
                    fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/valid.png"/> ' + service.data.name + '<br>';
                } else {
                    fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/search-cancel.png"/> ' + service.data.name + '<br>';
                }
            },
            callback : function () {
                this.services.shift();
                this.executeCopy();
            },
            failure : function (ret) {
                var fieldService = this.fieldset.down('label[id=' + service.internalId + ']');
                fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/search-cancel.png"/> ' + service.data.name + '<br>';
                
            }
        });
    },
    
    getAllServicesDescription : function () {
        
        if (!Ext.isEmpty(this.servicesIHM)) {
            this.getServicesIHMDescription();
        }
        
        if (!Ext.isEmpty(this.servicesSERVER)) {
            this.getServicesSERVERDescription();
        }
        
    },
    
    getServicesSERVERDescription : function () {
        Ext.Ajax.request({
            url : this.urlDatasetServiceSERVER.replace('{idDataset}', this.parentDatasetId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                this.servicesSERVERDescription = Ext.decode(ret.responseText);
                Ext.each(this.services, function (service) {
                   Ext.each(this.servicesSERVERDescription.data, function (serviceSERVER) {
                       if (service.data.id == serviceSERVER.id) {
                        service.data = serviceSERVER;
                        service.data.type = "SERVER"; // On rajoute le type qui n'est pas dans la définition de base du service   
                       }
                   }, this); 
                }, this);
            },
            callback : function () {
            },
            failure : function (ret) {
                
            }
        });
    },
    
    getServicesIHMDescription : function () {
        Ext.Ajax.request({
            url : this.urlDatasetServiceGUI.replace('{idDataset}', this.parentDatasetId),
            method : 'GET',
            scope : this,
            success : function (ret) {
                this.servicesIHMDescription = Ext.decode(ret.responseText);
                Ext.each(this.services, function (service) {
                    Ext.each(this.servicesIHMDescription.data, function (serviceIHM) {
                        if (service.data.id == serviceIHM.id) {
                         service.data = serviceIHM;   
                         service.data.type = "GUI"; // On rajoute le type qui n'est pas dans la définition de base du service   
                        }
                    }, this); 
                 }, this);
            },
            callback : function () {
            },
            failure : function (ret) {
                
            }
        });
    }

});

