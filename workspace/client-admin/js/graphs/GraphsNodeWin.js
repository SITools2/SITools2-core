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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
Ext.namespace('sitools.component.projects');

Ext.define('sitools.admin.graphs.graphsNodeWin', { 
    extend : 'Ext.Window',
    width : 350,
    modal : true,
    closable : false,

    initComponent : function () {
        this.title = i18n.get('label.nodeDescription');

        /* paramétres du formulaire */
        this.itemsForm = [{
            fieldLabel : i18n.get('label.name'),
            name : 'name',
            anchor : '100%',
            allowBlank : false
        }, {
            fieldLabel : i18n.get('label.description'),
            name : 'description',
            anchor : '100%'
        }, {
            xtype : 'sitoolsSelectImage',
            name : 'image',
            fieldLabel : i18n.get('label.image'),
            anchor : '100%',
            growMax : 400
        } ];

        this.bbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ '->', {
                text : i18n.get('label.ok'),
                handler : this._onOK
            }, {
                text : i18n.get('label.cancel'),
                handler : this._onCancel
            } ]
        };

        this.formPanel = new Ext.FormPanel({
            labelWidth : 100,
            border : false,
            bodyBorder : false,
            padding : '5 5 5 5',
            defaultType : 'textfield',
            items : this.itemsForm

        });

        this.items = [ this.formPanel ];
        // this.relayEvents(this.store, ['destroy', 'save', 'update']);
        sitools.admin.graphs.graphsNodeWin.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.graphs.graphsNodeWin.superclass.onRender.apply(this, arguments);
    },

    afterRender : function () {

        sitools.admin.graphs.graphsNodeWin.superclass.afterRender.apply(this, arguments);

        if (this.mode == 'edit') {
            var node = this.node;
            var form = this.formPanel.getForm();
            var rec = {};
            rec.name = node.get('text');
            rec.image = node.get('image').url;
            rec.description = node.get('description');

            form.setValues(rec);
        }
    },

    _onOK : function () {
        var form = this.formPanel.getForm();
        var values = form.getValues();
        var image = {};
        
        if (!Ext.isEmpty(values.image)) {
            image.url = values.image;
            image.type = "Image";
            image.mediaType = "Image";
        }

        if (this.mode == 'edit') {
            this.node.set('text', values.name);
            this.node.set('description', values.description);
            this.node.set('image', image);

        } else {
            var newNode = Ext.create('sitools.admin.graphs.graphNodeModel', {
                text : values.name,
                image : image,
                description : values.description,
                type : "node",
                children : []
            });
            
            if (!this.node.isExpanded()) {
                this.node.expand();
            }
            this.node.appendChild(newNode);
        }

        this.close();
    },

    _onCancel : function () {
        this.destroy();
    },

    _onUpload : function () {
        Ext.msg.alert("Information", "TODO");
    }

});
