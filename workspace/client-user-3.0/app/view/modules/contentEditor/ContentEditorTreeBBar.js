/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.user.view.modules.contentEditor');
/**
 * Toolbar used to display last modified date
 * @class sitools.user.view.modules.contentEditor.ContentEditorTreeBBar
 * @extends Ext.Toolbar
 */
Ext.define('sitools.user.view.modules.contentEditor.ContentEditorTreeBBar', {
	extend : 'Ext.toolbar.Toolbar',
    alias : 'widget.contentEditorTreeBBar',
	
    initComponent : function () {
        
        this.labelUpToDate = Ext.create('Ext.form.Label');
        
        this.items = [ this.labelUpToDate ]
    
        this.callParent(arguments);
    },
    

    setTreeUpToDate : function (upToDate, dateStr) {
        var date = Ext.Date.format(new Date(dateStr), SITOOLS_DEFAULT_IHM_DATE_FORMAT);
        
        if (Ext.isEmpty(this.labelUpToDate)) {
            return;
        }
        
        if (!upToDate) {
            this.labelUpToDate.update(Ext.String.format("<span class='sitools-userProfile-warning-text'>{0} : {1}</span>", i18n.get("label.lastUpdate"), date));
            this.labelUpToDate.addCls("x-status-warning");
        } else {
            this.labelUpToDate.update(Ext.String.format("<span>{0} : {1}</span>", i18n.get("label.lastUpdate"), date));
            this.labelUpToDate.removeCls("x-status-warning");
        }
        
    }
    
    
});