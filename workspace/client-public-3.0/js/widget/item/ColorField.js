/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 ******************************************************************************/
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/

/**
 * Color picker on a triggerField * 
 * @event select when a new color is selected
 * @class sitools.widget.colorField
 * @extends Ext.form.TriggerField
 */
Ext.define('sitools.public.widget.item.ColorField', {
    alterclassNames : ['sitools.widget.ColorField'],
    extend : 'Ext.form.TriggerField',
    alias : 'widget.colorfield',
    onTriggerClick : function (e) {
        var cp = Ext.create("Ext.picker.Color", {
            scope : this,
            floating : true,
            handler: function (cm, color) {
                this.setValue("#" + color);
                this.setFontColor("#" + color);
                this.fireEvent("select", this, color);
                cm.destroy();
            }
        });
        cp.showAt(e.getXY());

    },
    setFontColor : function (color) {
        var h2d = function (d) {
            return parseInt(d, 16);
        };
        var value = [
            h2d(color.slice(1, 3)),
            h2d(color.slice(3, 5)),
            h2d(color.slice(5))
        ];
        var avg = (value[0] + value[1] + value[2]) / 3;
        this.inputEl.setStyle({
            'color' : (avg > 128) ? '#000' : '#FFF', 
            'background-color' : color, 
            'background-image' : "none"
        });
        
    }, 
    listeners : {
        afterrender : function (tf) {
            tf.setFontColor(tf.getValue());
        },
        change : function (tf) {
            tf.setFontColor(tf.getValue());
        }
    }
});