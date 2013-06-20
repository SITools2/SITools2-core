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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
Ext.namespace('sitools.admin.forms.noParam');

/**
 * A basic form panel to define an image Component
 * @class sitools.admin.forms.noParam.image
 * @extends Ext.form.FormPanel
 */
sitools.admin.forms.noParam.image = Ext.extend(Ext.form.FormPanel, {
//sitools.component.forms.noParam.image = Ext.extend(Ext.form.FormPanel, {
    height : 100,
    id : "sitools.component.forms.definitionId",
    initComponent : function () {
        this.css = new Ext.form.TextField({
            fieldLabel : i18n.get('label.css'),
            name : 'CSS',
            anchor : '100%'
        });
        this.urlImage = new Ext.form.SitoolsSelectImage({
            fieldLabel : i18n.get('label.image'),
            name : 'image',
            anchor : '100%'
        });
        this.componentDefaultHeight = new Ext.form.TextField({
            fieldLabel : i18n.get('label.height'),
            name : 'componentDefaultHeight',
            anchor : '100%',
            value : this.componentDefaultHeight
        });
        this.componentDefaultWidth = new Ext.form.TextField({
            fieldLabel : i18n.get('label.width'),
            name : 'componentDefaultWidth',
            anchor : '100%',
            value : this.componentDefaultWidth
        });
        this.winPropComponent.specificHeight = 190;
        this.winPropComponent.specificWidth = 400;
        this.items = [ this.urlImage, this.css ];
        if (this.action == "create") {
			this.items.push(this.componentDefaultHeight, this.componentDefaultWidth); 
		}
        sitools.admin.forms.noParam.image.superclass.initComponent.call(this);
    },
    onRender : function () {
        sitools.admin.forms.noParam.image.superclass.onRender.apply(this, arguments);
        if (this.action == 'modify') {
            this.css.setValue(this.selectedRecord.data.css);
            this.urlImage.setValue(this.selectedRecord.data.label);
        }
    },
    _onValidate : function (action, formComponentsStore) {
        var f = this.getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        if (action == 'modify') {
            var rec = this.selectedRecord;
            var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();
            rec.set('label', f.findField('image').getValue());
            rec.set('css', css);
//            rec.set('componentDefaultHeight', f.findField('componentDefaultHeight').getValue());
//            rec.set('componentDefaultWidth', f.findField('componentDefaultWidth').getValue());
        } else {
            // Génération de l'id
            var lastId = 0;
            var greatY = 0;
            formComponentsStore.each(function (component) {
                if (component.data.id > lastId) {
                    lastId = parseInt(component.data.id, 10);
                }
                if (component.data.ypos > greatY) {
                    greatY = parseInt(component.data.ypos, 10)  + parseInt(component.data.height, 10);
                }

            });
            var componentId = lastId + 1;
            componentId = componentId.toString();
            var componentYpos = greatY + 10;

            formComponentsStore.add(new Ext.data.Record({
                label : f.findField('image').getValue(),
                type : this.ctype,
                width : f.findField('componentDefaultWidth').getValue(),
                height : f.findField('componentDefaultHeight').getValue(),
                id : componentId,
                ypos : componentYpos,
                css : f.findField('CSS').getValue(),
                jsAdminObject : this.jsAdminObject, 
                jsUserObject : this.jsUserObject,
                containerPanelId : this.containerPanelId
            }));
        }
        return true;
    }

});
