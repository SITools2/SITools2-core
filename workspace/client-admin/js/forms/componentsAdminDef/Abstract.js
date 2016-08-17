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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/

/**
 * An abstract form to define MultiParam Components
 * @class sitools.admin.forms.componentsAdminDef.multiParam.Abstract
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.forms.componentsAdminDef.Abstract', {
    extend : 'Ext.form.Panel',
    autoScroll : true, 
    border : false,
    bodyBorder : false,
    padding : 10,
    initComponent : function () {
        this.componentType = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.component'),
            name : 'FORM_COMPONENT',
            anchor : '100%',
            tooltip : i18n.get("label.component"),
            disabled : true,
            value : this.ctype
        });
        this.items = [this.componentType];
        this.padding = 10;

        this.callParent(arguments);
    },

    _onValidate : function (action, formComponentsStore) {
        /**
         * Chaque classe étandant cet objet doit redéfinir cette méthode
         */
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.OnvalidateNotDefined'));
    }
});
