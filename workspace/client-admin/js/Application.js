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
Ext.define('sitools.admin.Application', {
    name : 'clientadmin',

    extend : 'Ext.app.Application',
    
    requires : [ 
        'sitools.admin.guiservices.GuiServicesCrudPanel',
        'sitools.admin.applications.applicationsCrud'
    ],
    
    isReady: false,
    modules: null,
    useQuickTips: true,
    

    launch : function () {
        i18n.load('/sitools/common/res/i18n/' + LOCALE + '/gui.properties', function () {
            loadUrl.load('/sitools/client-admin/siteMap', function () {
                Ext.MessageBox.buttonText.yes = i18n.get('label.yes');
                Ext.MessageBox.buttonText.no = i18n.get('label.no');
                Ext.QuickTips.init();
                if (Ext.isEmpty(Ext.util.Cookies.get('scheme')) || Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
                    /*new sitools.userProfile.Login({
                        url:'${appUrl}/authentication/login',
                        handler : initAppli
                    }).show();*/
                    sitools.userProfile.LoginUtils.connect({
                        url : loadUrl.get('APP_URL') + '/authentication/login',
                        handler : initAppli,
                        reset : loadUrl.get('APP_URL') + '/lostPassword',
                        unblacklist : loadUrl.get('APP_URL') + '/unblacklist'
                    });
                } else {
                    initAppli();
                }
            });
        });

    }
    
    
    
    
});
