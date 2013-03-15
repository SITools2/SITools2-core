/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, loadUrl, projectGlobal */

Ext.namespace('sitools.user.modules');
/**
 * Toolbar used in the cms
 * @class sitools.user.modules.cmsContextMenu
 * @cfg callback the function to call after a language have been chosen 
 * @extends Ext.Toolbar
 */
sitools.user.modules.cmsViewerTreeToolbar = Ext.extend(Ext.Toolbar, {
    
    initComponent : function () {
        
        var storeLanguage = new Ext.data.JsonStore({
            fields : [ 'text', 'locale'],
            autoLoad : false
        });
        
        var jsonLanguage = [];
        Ext.each(projectGlobal.languages, function (language) {
            jsonLanguage.push({
                text : language.displayName,
                locale : language.localName
            });
        });
        
        storeLanguage.loadData(jsonLanguage);
        
        this.comboLanguage = new Ext.form.ComboBox({
            store : storeLanguage,
            displayField : 'text',
            valueField : 'locale',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectLanguage'),
            selectOnFocus : true,
            width : 100,
            listeners : {
                scope : this.scope,
                select : this.callback
            }
        });
        
        this.comboLanguage.setValue(locale.getLocale());
        
        this.items = [ i18n.get("label.langues"), this.comboLanguage ];
    
    
        sitools.user.modules.cmsViewerTreeToolbar.superclass.initComponent.call(this);
    }
    
    
});