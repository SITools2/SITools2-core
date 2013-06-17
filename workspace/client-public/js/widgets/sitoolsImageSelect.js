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
/*global Ext, sitools, ID, i18n,ImageChooser,document, validate*/

/**
 * This triggerField is loading a new ImageChooser onTriggerClick.
 * @class Ext.form.SitoolsSelectImage
 * @extends Ext.form.TriggerField
 */
Ext.form.SitoolsSelectImage = Ext.extend(Ext.form.TriggerField, {
	onTriggerClick : function () {
        if (!this.disabled) {
			function validate(data, config) {
	            config.fieldUrl.setValue(data.url);  
                config.fieldUrl.fireEvent('change', this, data.url, config.fieldUrl.startValue);
	        }
	        // console.dir (this);
	        var chooser = new ImageChooser({
	            url : loadUrl.get('APP_URL') + '/upload/?media=json',
	            width : 515,
	            height : 450,
	            fieldUrl : this
	        });
	        chooser.show(document, validate);
        }
	} 

});
Ext.reg('sitoolsSelectImage', Ext.form.SitoolsSelectImage);
