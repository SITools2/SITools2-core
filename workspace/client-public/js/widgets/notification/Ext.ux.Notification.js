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
Ext.namespace("Ext.ux");
Ext.ux.NotificationMgr = {
positions: []
};
Ext.ux.Notification = Ext.extend(Ext.Window, {
    initComponent: function(){
        Ext.apply(this, {
            iconCls: this.iconCls || 'x-icon-information',
            cls: 'x-notification',
            width: 200,
            autoHeight: true,
            plain: false,
            draggable: false,
            shadow:false,
            bodyStyle: 'text-align:center'
        });
        if(this.autoDestroy) {
            this.task = new Ext.util.DelayedTask(this.hide, this);
        } else {
            this.closable = true;
        }
        Ext.ux.Notification.superclass.initComponent.apply(this);
    },
    setMessage: function(msg){
        this.body.update(msg);
    },
    setTitle: function(title, iconCls){
        Ext.ux.Notification.superclass.setTitle.call(this, title, iconCls||this.iconCls);
    },
    onDestroy: function(){
        Ext.ux.NotificationMgr.positions.remove(this.pos);
        Ext.ux.Notification.superclass.onDestroy.call(this);   
    },
    cancelHiding: function(){
        this.addClass('fixed');
        if(this.autoDestroy) {
            this.task.cancel();
        }
    },
    afterShow: function(){
        Ext.ux.Notification.superclass.afterShow.call(this);
        Ext.fly(this.body.dom).on('click', this.cancelHiding, this);
        if(this.autoDestroy) {
            this.task.delay(this.hideDelay || 5000);
       }
    },
    animShow: function(){
        this.pos = 0;
        while(Ext.ux.NotificationMgr.positions.indexOf(this.pos)>-1)
            this.pos++;
        Ext.ux.NotificationMgr.positions.push(this.pos);
        this.setSize(200,100);
        this.el.alignTo(document, "br-br", [ -20, -140-((this.getSize().height+10)*this.pos) ]);
        this.el.slideIn('b', {
            duration: 1,
            callback: this.afterShow,
            scope: this
        });
    },
    animHide: function(){
        this.el.ghost("b", {
            duration: 1,
            remove: false,
            callback : function () {
                Ext.ux.NotificationMgr.positions.remove(this.pos);
                this.destroy();
            }.createDelegate(this)

        });
    },
    focus: Ext.emptyFn
});
