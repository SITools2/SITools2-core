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

Ext.namespace('sitools.user.controller.header');

/**
 * Populate the div x-headers of the sitools Desktop. 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define("sitools.user.controller.header.HeaderController", {
    
    extend : 'Ext.app.Controller',
    
    views : ['header.Header'],
    
    heightNormalMode : 0, 
    heightMaximizeDesktopMode : 0, 
    
    init : function () {
        
        this.getApplication().on('projectLoaded', this.onProjectLoaded, this);
        

        this.control({
            'moduleTaskBar button' : {
                click : this.openModule
            }
        });
        
        this.callParent(arguments);
    },
    
    
    onProjectLoaded : function () {
        var project = Ext.getStore('ProjectStore').getAt(0);
        Ext.create('sitools.user.view.header.Header', {
            renderTo : "x-headers",
            id : "headersCompId",
            htmlContent : project.get('htmlHeader'),
            modules : project.modules(),
            listeners : {
                resize : function (me) {
                    me.setSize(SitoolsDesk.getEnteteEl().getSize());
                }
            }
        });
    },
    
    openModule : function (button, e, opts) {
//        alert('TODO open ' + button.text);
        console.log('TODO open ' + button.text);
        
        
    }
});