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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure*/
Ext.namespace('sitools.component.version');

sitools.component.version.sitoolsVersion = Ext.extend(Ext.Panel, {
    
	layout : 'fit', 
    
    initComponent : function () {
    this.versionUrl = loadUrl.get('APP_URL') + '/version';
        
        var title = new Ext.form.Label({
            html : '<h2>SITools2</h2><br/>' 
        });
        
        var logo = new Ext.form.Label({
            html : '<img src='+loadUrl.get('APP_URL')+'/res/images/logo_02_tailleMoyenne.png>'
        });
        
        var credits = new Ext.form.Label({
            html : '<p>Copyright 2010-2013 CNES</p>'
        });
        
        var website = new Ext.form.Label({
            html : '<a href="http://www.sitools2.sourceforge.net">sitools2.sourceforge.net</>'
        });
        
        this.versionLabel = new Ext.form.Label({            
        });
        
        this.buildDateLabel = new Ext.form.Label({            
        }); 
        
        var panelVersion = new Ext.Panel({
            title : i18n.get("label.version"),
            layout : 'fit',
            padding : 10
        });
        
        var panelLicence = new Ext.ux.ManagedIFrame.Panel({
            title : i18n.get("label.licence"),
            layout : 'fit',
            defaultSrc : loadUrl.get('APP_URL') + "/res/licences/gpl-3.0.txt"
            
        });
        
        panelVersion.add([logo, title, this.versionLabel, this.buildDateLabel, credits, website]);
        
        this.tabs = new Ext.TabPanel({
            activeTab: 0,
            items: [ panelVersion, panelLicence]            
        });
        
        this.items = [this.tabs];
        
            
        this.listeners = {
            scope : this,
            resize : function (window) {
                var size = window.body.getSize();
                this.tabs.setSize(size);
            }
        };
            
        sitools.component.version.sitoolsVersion.superclass.initComponent.call(this);
    },
    
    afterRender : function () {

        sitools.component.version.sitoolsVersion.superclass.afterRender.apply(this, arguments);
        
        Ext.Ajax.request({
                url : this.versionUrl,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), json.message);
                        return false;
                    }
                    var info = json.info;
                    
                    var version = info.version;
                    var buildDate = info.buildDate;
                    
                    this.versionLabel.setText("<h3>Version : " + version + "</h3>", false);                    
                    this.buildDateLabel.setText("<h3>Build date : " + buildDate + "</h3>", false);                    
                    
                    //this.doLayout();
                    
                },
                failure : alertFailure
            });
        
        var size = this.ownerCt.body.getSize();
        this.tabs.setSize(size);
        
    }
});

function showVersion () {
	var versionHelp = Ext.getCmp('winVersionId');
    if (!versionHelp) {
        var panelHelp = new sitools.component.version.sitoolsVersion();
        versionHelp = new Ext.Window({
            title : i18n.get('label.version'),            
            id : 'winVersionId', 
            items : [panelHelp], 
            modal : false, 
			width : 700,
			height : 480,
			resizable : false, 
            modal : true,
			buttons : [{
                text : i18n.get('label.close'),
                
                handler : function () {
                    this.ownerCt.ownerCt.close();
                }
            } ]


        });
        

        versionHelp.show();
        
    } else {
        versionHelp.show();
    }
}

