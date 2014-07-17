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
 * @class sitools.admin.resourcesPlugins.ResourcesServicesCopyProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.resourcesPlugins.ResourcesServicesCopyProp', { extend : 'Ext.Window',
	alias : 'widget.s-storage_copy',
    width : 350,
    height : 250,
    modal : true,
    autoHeight : true,
    autoScroll : true,
    layout : 'fit',
    initComponent : function () {
        
    	this.title = (this.parentType == "projet") ? i18n.get('label.duplicateProjectServices') : i18n.get('label.duplicateAppServices');
    	
    	this.fieldset = Ext.create("Ext.form.FieldSet", {
    	    xtype : 'fieldset',
            name : 'fieldsetCopiedServices',
            title : i18n.get('label.servicesCopied'),
            autoHeight : true,
            hidden : true
    	});
    	
    	this.formPanel = Ext.create("Ext.form.FormPanel", {
            xtype : 'form',
            id : 'formCopyId',
            frame: false,
            border: false,
            autoHeight : true,
            buttonAlign: 'center',
            bodyStyle: 'padding:15px 10px 15px 15px;',
            items : [{
                xtype : 'label',
                text : (this.parentType == "projet") ? i18n.get('label.projectDestination') : i18n.get('label.appDestination')
            },{
                xtype : 'combo',
                id: 'projDestId',
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
        });
    	
    	if (this.parentType == "application") {
    	    this.formPanel.add({
    	        xtype : 'label',
    	        html : i18n.get('label.warningCopyAppServices')
    	    });
    	}
    	
    	this.items = [this.formPanel];
    	
    	
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
    	
        sitools.admin.resourcesPlugins.ResourcesServicesCopyProp.superclass.initComponent.call(this);
    },
    
    runCopy : function () {
    	var f = Ext.getCmp('formCopyId').getForm();
        var idDest = f.findField('projDestId').getValue();
        
        if (Ext.isEmpty(idDest)) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
            
        Ext.each(this.services, function (service) {
            this.fieldset.add({
                xtype : 'label',
                id : service.get("id"),
                html : '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/loading.gif"/> ' + service.get("name") + "<br>"
            });
        }, this);
        this.fieldset.setVisible(true);
        this.fieldset.doLayout();
        
        this.url = this.urlParents +  "/" + idDest + this.resourcesUrlPart;
        this.executeCopy();
        
       
    },
    
    executeCopy : function () {
        if (Ext.isEmpty(this.services)) {
            var buttonToolbar = this.down("button"); 
            buttonToolbar.copyFinish = true;
            buttonToolbar.setText(i18n.get('label.close'));
            buttonToolbar.setHandler(function () {
                this.storeServices.load();
                this.close();
            }, this);
            return;
        }
        
        var service = this.services[0].getData();
        var idService = service.id;
        delete service.id;
        delete service.parent;
        
        Ext.Ajax.request({
            url : this.url,
            method : 'POST',
            jsonData : service,
            scope : this,
            success : function (ret) {
                var fieldService = this.fieldset.down('label[id='+idService+']');
                if (ret.status == 200) {
                    fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/valid.png"/> ' + service.name + '<br>';
                } else {
                    fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/search-cancel.png"/> ' + service.name + '<br>';
                }
            },
            callback : function () {
                this.services.shift();
                this.executeCopy();
            },
            failure : function (ret) {
                var fieldService = this.fieldset.down('label[id='+idService+']');
                fieldService.el.dom.innerHTML = '<img src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/search-cancel.png"/> '
                        + service.name + '<br>';
            }
        });
    }

});

