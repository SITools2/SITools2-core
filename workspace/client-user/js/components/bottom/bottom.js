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
/*global Ext, sitools, showResponse, i18n, extColModelToJsonColModel, loadUrl, projectGlobal, SitoolsDesk */

Ext.namespace('sitools.user.component.bottom');

//sitools.component.users.datasets.columnsDefinition = function (config) {
/**
 * Create the desktop footer component with the sitools footer by default or with the template footer of the project (if it's not empty)
 * 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.bottom.Bottom
 * @extends Ext.Panel
 */
sitools.user.component.bottom.Bottom = Ext.extend(Ext.Panel, {
    heightNormalMode : 0, 
    heightMaximizeDesktopMode : 0, 
    forceLayout : true, 
    layout : "hbox",
    border : false, 
    layoutConfig : {
        align : 'stretch',
        pack : 'start'
    },
    bodyCssClass : 'sitools_footer',
	initComponent : function () {
		this.defaultBottom = Ext.get('x-bottom').dom.children.length === 0 ;
		
		if (this.defaultBottom){
			
			this.renderTo = 'x-bottom';
			
			this.panelLeft = new Ext.Panel({
				border : false,
				flex : 0.5,
				html : "<img id='sitools_logo' src='" + loadUrl.get("APP_URL") + "/res/images/logo_01_petiteTaille.png' alt='sitools_logo'/>",
				bodyCssClass : 'no-background',
	            listeners :  {
	                scope : this,
	                afterRender : function () {
	                    Ext.get("sitools_logo").on('load', function () {
	                        Ext.get("sitools_logo").alignTo(this.panelLeft.getEl(), "c-c");
	                    }, this);
	                }
	            }
			});
			
			this.panelMiddle = new Ext.Panel({
				border : false,
				flex : 1,
				bodyCssClass : 'no-background',
				items : [{
				    xtype : "panel",
			        html : "<span style='color:white'>" + i18n.get("label.build_by_sitools2") + "</span>",
				    id : 'sitools_build_by',
				    cls : "sitools_footer_build_by",
				    bodyCssClass : 'no-background'

				}]	
			});
			
			this.linkStore = new Ext.data.Store({
	            fields : [ 'name', 'url']
	        }); 
			
			var linkDataview = new Ext.DataView({
	            store : this.linkStore, 
	            tpl : new Ext.XTemplate('<div class="sitools_footer_right" id="sitools_footer_right">',
	                    '<tpl for=".">', 
	                        '<a rel="contents" href="#" onclick="sitools.user.component.bottom.Bottom.showFooterLink(\'{url}\',\'{name}\');">',
	                            '{[this.getLabel(values.name)]}',
	                        '</a>',
	                        '<tpl if="(xindex < xcount)">',
	                        ' | ',
	                        '</tpl>',
	                    '</tpl>', '</div>', 
	                    {
	                    compiled : true, 
	                    disableFormats : true,
	                    getLabel : function (labelName) {
	                        return i18n.get(labelName);
	                    }
	                })
	        });
			
			this.panelRight = new Ext.Panel({
				border : false,
				flex : 0.5,
				bodyCssClass : 'no-background',
				items : [linkDataview]
			});
			
			this.items = [this.panelLeft, this.panelMiddle, this.panelRight];
		}
		else {
			var el = Ext.get('x-bottom').createChild({
				tag :'div'
			});
			this.renderTo = el;
		}
		


		sitools.user.component.bottom.Bottom.superclass.initComponent.call(Ext.apply(this, {

            // html : this.htmlContent,
            listeners : {
                scope : this,
                afterRender : function (me) {

                    if (!this.defaultBottom) {
                        me.setHeight(0);
                    } else {
                        var bottomEl = SitoolsDesk.getBottomEl();
                        me.fillLinks();
                        me.setHeight(bottomEl.getHeight());
                        me.heightNormalMode = bottomEl.getHeight();
                        me.doLayout();
                        Ext.get("sitools_build_by").alignTo(this.panelMiddle.getEl(), "bl-bl");

                        var fr = Ext.get("sitools_footer_right");
                        if (Ext.isDefined(fr) && !Ext.isEmpty(fr)) {
                            fr.alignTo(this.panelRight.getEl(), "c-c");
                        }
                    }
                },
                resize : function (me) {
                    if (!this.defaultBottom) {
                        me.setHeight(0);
                    } else {
                        me.setSize(SitoolsDesk.getBottomEl().getSize());
                        me.doLayout();
                        Ext.get("sitools_logo").alignTo(this.panelLeft.getEl(), "c-c");
                        Ext.get("sitools_build_by").alignTo(this.panelMiddle.getEl(), "bl-bl");

                        var fr = Ext.get("sitools_footer_right");
                        if (Ext.isDefined(fr) && !Ext.isEmpty(fr)) {
                            fr.alignTo(this.panelRight.getEl(), "c-c");
                        }
                    }
                },

                maximizeDesktop : this.onMaximizeDesktop,
                minimizeDesktop : this.onMinimizeDesktop
            }

        }));
	}, 
	onMaximizeDesktop : function () {
		this.container.setHeight(0);
		this.hide();
		this.doLayout();
	}, 
	onMinimizeDesktop : function () {
		this.container.dom.style.height = "";
		this.setSize(SitoolsDesk.getBottomEl().getSize());
		this.show();
		this.doLayout();
	},
	fillLinks : function () {
	    var projectLinks = projectGlobal.links;
	    Ext.each(projectLinks, function (value) {
	        this.linkStore.add(new Ext.data.Record(value));
	    }, this);
	}
    
});

/**
 * @static
 */
sitools.user.component.bottom.Bottom.showFooterLink = function (url, linkName) {
    var windowConfig = {
        title : i18n.get(linkName),
        id : linkName, 
        iconCls : "version"
    };
        
    var jsObj = Ext.ux.ManagedIFrame.Panel;
    var componentCfg = {
        defaults : {
            padding : 10
        },
        layout : 'fit',
        region : 'center',
        defaultSrc : url
    };
    
    SitoolsDesk.addDesktopWindow(
            windowConfig, componentCfg,
            jsObj);
    
};


Ext.reg('sitools.user.component.bottom.Bottom', sitools.user.component.bottom.Bottom);
