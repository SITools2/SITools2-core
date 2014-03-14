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
 * @class sitools.admin.resourcesPlugins.resourcesServicesCopyProp
 * @extends Ext.Window
 */
sitools.admin.resourcesPlugins.resourcesServicesCopyProp = Ext.extend(Ext.Window, {
    width : 260,
    height : 230,
    modal : true,
    autoHeight : true,
    autoScroll : true,
    initComponent : function () {
        
    	this.title = (this.parentType == "projet") ? i18n.get('label.duplicateProjectServices') : i18n.get('label.duplicateAppServices');
    	
    	this.fieldset = new Ext.form.FieldSet({
    	    xtype : 'fieldset',
            name : 'fieldsetCopiedServices',
            title : i18n.get('label.servicesCopied'),
            autoHeight : true,
            hidden : true
    	});
    	
    	this.formPanel = new Ext.form.FormPanel({
            xtype : 'form',
            id : 'formCopyId',
            layout : 'fit',
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
                mode : 'local',
                forceSelection : true,
                triggerAction : 'all',
                selectOnFocus : true
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
			icon : loadUrl.get('APP_URL') + '/common/res/images/icons/converter.png',
			copyFinish : false,
			scope : this,
			handler : this.runCopy
		}];
    	
        sitools.admin.resourcesPlugins.resourcesServicesCopyProp.superclass.initComponent.call(this);
    },
    
    runCopy : function () {
    	var f = Ext.getCmp('formCopyId').getForm();
        var idDest = f.findField('projDestId').getValue();
        
        if (Ext.isEmpty(idDest)) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
            
        Ext.each(this.services, function (service) {
            this.fieldset.add({
                xtype : 'label',
                id : service.data.id,
                html : '<img src="/sitools/common/res/images/icons/loading.gif"/> ' + service.data.name + "<br>"
            });
        }, this);
        this.fieldset.setVisible(true);
        this.fieldset.doLayout();
        
        this.url = this.urlParents +  "/" + idDest + this.resourcesUrlPart;
        this.executeCopy();
        
        var buttonToolbar = this.buttons[0]; 
        buttonToolbar.copyFinish = true;
        buttonToolbar.setText(i18n.get('label.close'));
        buttonToolbar.setHandler(function () {
            this.close();
        }, this);
    },
    
    executeCopy : function () {
        if (Ext.isEmpty(this.services)) {
            return;
        }
        
        var service = this.services[0];
        delete service.json.id;
        delete service.json.parent;
        
        Ext.Ajax.request({
            url : this.url,
            method : 'POST',
            jsonData : service.json,
            scope : this,
            success : function (ret) {
                var fieldService = this.fieldset.find('id', service.data.id)[0];
                if (ret.status == 200) {
                    fieldService.el.dom.innerHTML = '<img src="/sitools/common/res/images/icons/valid.png"/> ' + service.data.name + '<br>';
                } else {
                    fieldService.el.dom.innerHTML = '<img src="/sitools/common/res/images/icons/search-cancel.png"/> ' + service.data.name + '<br>';
                }
            },
            callback : function () {
                this.services.shift();
                this.executeCopy();
            },
            failure : function (ret) {
                var fieldService = this.fieldset.find('id', service.data.id)[0];
                fieldService.el.dom.innerHTML = '<img src="/sitools/common/res/images/icons/search-cancel.png"/> ' + service.data.name + '<br>';
            }
        });
    }

});

Ext.reg('s-storage_copy', sitools.admin.resourcesPlugins.resourcesServicesCopyProp);
