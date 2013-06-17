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
Ext.namespace('sitools.widget');

/**
 * Text component that allows Highlighting
 */
sitools.widget.HighlightText = Ext.extend(Ext.BoxComponent,{

    /**
     * must be declared at the configuration
     */
    text:undefined,


    initComponent : function(){
        Ext.apply(this, {
            autoEl : {
                //        tag:'div',
                html:this.text
            }

        });
        sitools.widget.HighlightText.superclass.initComponent.apply(this, arguments);
    },

    onRender : function (){
    	sitools.widget.HighlightText.superclass.onRender.apply(this, arguments);

    },
    highlight : function (color){
        clog('try highlight');
        if (color==null) color="yellow";
        this.getEl().applyStyles("background-color:"+color);
    },
    stopHighlight : function(){
        this.getEl().applyStyles("background-color:");
    },
    updateHtml : function(html){
        this.getEl().update(html);
        //this.doLayout();
    }
});

//register type
Ext.reg('s-lighttext', sitools.widget.HighlightText);
