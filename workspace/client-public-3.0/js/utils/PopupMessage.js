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
Ext.namespace('sitools.public.utils');

/**
 * An utility class to use in sitools.
 */
Ext.define('sitools.public.utils.PopupMessage', {
    singleton : true,
    msgCt : null,
    createBox : function (t, s, i, iclass) {
        if (i) {
            return '<div class="msg ' + Ext.baseCSSPrefix + 'border-box"><h3>' + t + '</h3><p><span style="padding:6px;"><img src="' + i +'"/></span>' + s + '</p></div>';
        } 
        else if (iclass) {
            return '<div class="msg ' + Ext.baseCSSPrefix + 'border-box"><h3>' + t + '</h3><p><span style="padding:0px 12px 0px 12px;" class="'+iclass+'"></span>' + s + '</p></div>';
        }
        else {
            return '<div class="msg ' + Ext.baseCSSPrefix + 'border-box"><h3>' + t + '</h3><p>' + s + '</p></div>';
        }
    },

    popupMessage : function (title, message, icon, iconClass, delayTime) {
        if(Ext.isObject(title)){
            var object = Ext.clone(title);
            title = object.title;
            message = object.html;
            icon = object.icon;
            iconClass = object.iconCls;
            delayTime = object.hideDelay;            
        }
        
        if(!this.msgCt){
            this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        }
        if(Ext.isEmpty(message)) {
            message = "";
        }
        var s = Ext.String.format.apply(String, [message]);
        var m = Ext.DomHelper.append(this.msgCt, sitools.public.utils.PopupMessage.createBox(title, s, icon, iconClass), true);
        m.hide();
        m.slideIn('t').ghost("t", { delay: delayTime || 1000, remove: true});
    }
});

popupMessage = sitools.public.utils.PopupMessage.popupMessage;
