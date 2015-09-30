/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, document, userLogin, loadUrl*/

Ext.namespace('sitools.user.modules');

/**
 * 
 * @param {} config
 */
sitools.user.modules.userSpace = function (config) {
    var urlOrder = "/";
	this.cfgCmp = config.cfgCmp || {};
	
    var orderMenu = new Ext.menu.Menu({
        plain : true,
        items : [ {
            text : i18n.get('label.doOrder'),
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_orders.png", 
            scope : this,
            handler : function () {
                this.doOrder();
            },
            disabled : Ext.isEmpty(userLogin)
        }, {
            text : i18n.get('label.viewOrder'),
            scope : this,
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/view_order.png", 
            handler : function () {
                this.viewOrder();
            },
            disabled : Ext.isEmpty(userLogin)
        } ]
    });
    
    // var preferenceMenu = new Ext.menu.Menu ({
    // plain : true,
    // items : [{
    // text : i18n.get ('label.viewCart'),
    // scope : this,
    // handler : function (){
    // this.viewCart();
    // }
    // }, {
    // text : i18n.get ('label.viewPreference'),
    // scope : this,
    // handler : function (){
    // this.viewPreference();
    // }
    // }]
    // });

    var tbar = new Ext.Toolbar({
        items : [ {
            text : i18n.get('label.orders'),
            menu : orderMenu,
            disabled : Ext.isEmpty(userLogin)
        }, {
            scope : this,
            text : i18n.get('label.viewPreference'),
            handler : this.viewPreference,
            disabled : Ext.isEmpty(userLogin)
        }, 
        /** TASK RESOURCE PART */
        {
            scope : this,
            text : i18n.get('label.Tasks'),
            handler : this.tasks,
            disabled : Ext.isEmpty(userLogin)
        }
        /** END OF TASK RESOURCE PART */
        ]
    });
    sitools.user.modules.userSpace.superclass.constructor.call(this, Ext.apply({
        tbar : tbar
    }));

};

Ext.extend(sitools.user.modules.userSpace, Ext.Panel, {
    panel : null,
    layout : 'fit',
    doOrder : function () {
        if (!Ext.isEmpty(this.panel)) {
            this.panel.destroy();
        }
        this.panel = new sitools.user.modules.userSpaceDependencies.orderPanel({
            urlOrder : this.urlOrder
        });
        this.add(this.panel);
        this.doLayout();

    },
    viewOrder : function () {
        if (!Ext.isEmpty(this.panel)) {
            this.panel.destroy();
        }
        this.panel = new sitools.user.modules.userSpaceDependencies.viewOrderPanel({
            urlOrder : this.urlOrder
        });
        this.add(this.panel);
        this.doLayout();
    },
    viewPreference : function () {
        if (!Ext.isEmpty(this.panel)) {
            this.panel.destroy();
        }
        this.panel = new sitools.user.modules.userSpaceDependencies.preference({
            urlOrder : this.urlOrder
        });
        this.add(this.panel);
        this.doLayout();
    },
    svaTasks : function () {
        if (!Ext.isEmpty(this.panel)) {
            this.panel.destroy();
        }
        this.panel = new sitools.user.modules.userSpaceDependencies.svaTasks({
            urlOrder : this.urlOrder
        });
        this.add(this.panel);
        this.doLayout();
    },
    viewCart : function () {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.notYet'));
        return;
    },
    /** TASK RESOURCE PART */
    tasks : function () {
        if (!Ext.isEmpty(this.panel)) {
            this.panel.destroy();
        }
        this.panel = new sitools.user.modules.userSpaceDependencies.tasks({
            urlOrder : this.urlOrder
        });
        this.add(this.panel);
        this.doLayout();
    }, 
    /** END OF TASK RESOURCE PART */
    afterRender : function () {
		sitools.user.modules.userSpace.superclass.afterRender.call(this);
		if (this.cfgCmp.activePanel == "task") {
			this.svaTasks();
		}
		/** TASK RESOURCE PART */
		if (this.cfgCmp.activePanel == "taskResource") {
            this.tasks();
        }
		/** END OF TASK RESOURCE PART */
    }

});

Ext.reg('sitools.user.modules.userSpace', sitools.user.modules.userSpace);
