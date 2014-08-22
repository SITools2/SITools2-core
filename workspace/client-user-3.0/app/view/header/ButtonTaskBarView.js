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
/*global Ext, sitools, window, showVersion, publicStorage, userLogin, projectGlobal, SitoolsDesk, showResponse, i18n, extColModelToJsonColModel, loadUrl*/

/**
 * @cfg {Array} modules la liste des modules
 * @class sitools.user.component.entete.NavBarButtons
 * @extends Ext.Toolbar
 */
Ext.define('sitools.user.view.header.ButtonTaskBarView', {
    extend : 'Ext.Toolbar',
    alias: 'widget.buttonTaskBarView',
    
    requires : ['sitools.public.utils.LoginUtils'],
    /**
     * The id of the button to open the profile window
     */
    profileButtonId : "profileButtonId", 
    
    initComponent : function () {
        
        var itemsButtons = [];
        var userLogin = sitools.public.utils.LoginUtils.getUserLogin();
        
        if (!Ext.isEmpty(userLogin)) {
            // width with save button
            this.width = '204px';
        }
        
        /**
         * The btn to open profileWindow
         */
        this.profilButton = Ext.create("Ext.Button", {
        	name : 'profilBtn',
            scope : this, 
//            cls : 'navBarTransition',
            icon : "/sitools/common/res/images/icons/general/user.png",
            scale : 'medium',
            id : this.profileButtonId,
            handler : function (btn) {
            	this.desktopController.fireEvent('profilBtnClicked', btn);
			},
			listeners : {
				afterrender : function (btn) {
					var label = i18n.get('headers.options');
					var tooltipCfg = {
							html : label,
							target : btn.getEl(),
							anchor : 'bottom',
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        itemsButtons.push(this.profilButton);
        
        if (!Ext.isEmpty(userLogin)) {
            this.saveButton = Ext.create('Ext.Button', {
                scope : this, 
                handler : function (btn, evt) {
                	this.desktopController.fireEvent('saveBtnClicked', btn, evt);
    			}, 
                icon : "/sitools/common/res/images/icons/general/save.png", 
                scale : 'medium',
                id : "saveBtnId",
                listeners : {
    				afterrender : function (btn) {
    					var label = i18n.get('label.snapshotDesktop');
    					var tooltipCfg = {
    							html : label,
    							target : btn.getEl(),
    							anchor : 'bottom',
    							showDelay : 20,
    							hideDelay : 50,
    							dismissDelay : 0
    					};
    					Ext.create('Ext.tip.ToolTip', tooltipCfg);
    				}
    			}
            });
            itemsButtons.push(this.saveButton);
        }
        
        this.helpButton = Ext.create('Ext.Button', {
            scope : this, 
            icon : "/sitools/common/res/images/icons/navBarButtons/help-icon.png", 
//            handler : SitoolsDesk.showHelp,
            handler : function () {
                alert('todo');
            },
            scale : 'medium',
            tooltip : {
                html : i18n.get('label.help'), 
                anchor : 'bottom', 
                trackMouse : false
            }
        });
//        itemsButtons.push(this.helpButton);
        
        /**A specialized btn to switch between normal and maximize mode */
        this.maximizeButton = Ext.create('Ext.Button', {
			name : 'maximizeBtn',
			iconCls : 'navBarButtons-icon',
            icon : (Desktop.getDesktopMaximized() == false) ? "/sitools/common/res/images/icons/navBarButtons/mini.png" : "/sitools/common/res/images/icons/navBarButtons/maxi.png",  
            scale : 'medium',
            scope : this, 
            handler : function (btn) {
            	this.desktopController.fireEvent('maximizedBtnClicked', btn);
			},
			listeners : {
				afterrender : function (btn) {
					var label = (Desktop.getDesktopMaximized() == false) ? i18n.get('label.maximize') : i18n.get('label.manimize');
					var tooltipCfg = {
							html : label,
							target : btn.getEl(),
							anchor : 'bottom',
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        itemsButtons.push(this.maximizeButton);
        
        this.callParent(Ext.apply(this,  {
            id : 'navBarButtonsId',
            cls : 'buttonTaskbar-bg',
//            defaults : {
//                overCls : "x-navBar-items-over", 
//                ctCls : "x-navBar-items-ct"
//            }, 
            items : itemsButtons, 
//            cls : "x-navBar-buttons", 
//            overCls : "x-navBar-over", 
//            ctCls : "x-navBar-ct", 
            width : this.width,
            listeners : {
                scope : this, 
//                maximizeDesktop : this.onMaximizeDesktop, 
//                minimizeDesktop : this.onMinimizeDesktop 
            }, 
            border : false
        }));
    }, 
    
    /**
     * listeners of maximizeDesktop event
     */
    onMaximizeDesktop : function () {
        this.maximizeButton.setIcon("/sitools/common/res/images/icons/navBarButtons/mini-icon.png");
        //SitoolsDesk.desktopMaximizeMode = true;
        this.maximizeButton.tooltip.html = i18n.get('label.minimize');
    },
    
    /**
     * listeners of minimizeDesktop event
     */
    onMinimizeDesktop : function () {
        this.maximizeButton.setIcon("/sitools/common/res/images/icons/navBarButtons/maxi-icon.png");
      //  SitoolsDesk.desktopMaximizeMode = false;
        this.maximizeButton.tooltip.html = i18n.get('label.maximize');
    },
    
    /**
     * Returns the maximizeBtn
     * @returns {Ext.Button}
     */
    getMaximizeButton : function () {
        return this.maximizeButton;
    }
    
});