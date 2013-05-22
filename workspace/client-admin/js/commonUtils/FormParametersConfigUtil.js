/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE,
 * ImageChooser, showHelp, loadUrl
 */
Ext.namespace('sitools.admin.common');

/**
 * A Panel to show parameters retrieve by 'XTYPE'.getParameters() method in a
 * formPanel
 * 
 * @require rec the record with all the parameters
 * @require parametersFieldName the name of field to set with the parameters
 *          filled
 * @require parametersList the list of paramaters to create in the form
 * 
 * @class sitools.admin.common.FormParametersConfigUtil
 * @extends Ext.form.FormPanel
 */
sitools.admin.common.FormParametersConfigUtil = Ext.extend(Ext.form.FormPanel, {
    autoScroll : true,
    frame : true,
    border : false,
    monitorValid : true,
    labelWidth : 150,
    isRenderer : false,
    initComponent : function () {

        this.parametersFieldset = new Ext.form.FieldSet({
            title : i18n.get('label.parameters'),
            padding : 6
        });
        if (!Ext.isEmpty(this.rec)) {
            this.title = i18n.get('label.projectModuleConfig') + " " + this.rec.name;
            this.buildViewConfig(this.rec);
        }

        this.items = [ this.parametersFieldset ],
        this.isRenderer = true;

        sitools.admin.common.FormParametersConfigUtil.superclass.initComponent.call(this);
    },

    buildViewConfig : function (recSelected) {
        try {
            this.parametersFieldset.removeAll();

            var getParametersMethod = eval(recSelected.xtype + ".getParameters");

            if (!Ext.isFunction(getParametersMethod)) {
                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.notImplementedMethod'));
                return;
            }

            var parameters = getParametersMethod();

            if (Ext.isEmpty(parameters)) {
                //this.parametersFieldset.setVisible(false);
                this.parametersFieldset.add({
                    xtype : 'label',
                    html : '<h2 style="text-align:center;">'+ i18n.get('label.noParameter')+ '</h2>'
                });
            } else {
                this.parametersFieldset.setVisible(true);
            }
            Ext.each(parameters, function (param) {

                if (!Ext.isEmpty(this.parametersList)) {
                    // on recharge les parametres définis par l'utilisateur
                    Ext.iterate(this.parametersList, function (cmp) {
                        if (cmp.name == param.config.name) {
                            var customValue = cmp.value;
                            var JsObj = eval(param.jsObj);
                            var config = Ext.applyIf(param.config, {
                                anchor : "100%"
                            });
                            var p = new JsObj(config);

                            p.setValue(customValue);
                            this.parametersFieldset.add(p);
                        }
                    }, this);
                } else {
                    // on charge les parametres par défaut définis dans le
                    // projectModule
                    // var parameterValue =
                    // this.findDefaultParameterValue(param);
                    var JsObj = eval(param.jsObj);
                    var config = Ext.applyIf(param.config, {
                        anchor : "100%"
                    });

                    var p = new JsObj(config);
                    // if (!Ext.isEmpty(parameterValue)) {
                    // p.setValue(parameterValue);
                    // }
                    this.parametersFieldset.add(p);
                }

            }, this);

            this.doLayout();
        } catch (err) {
            throw err;
            // Ext.Msg.alert(i18n.get('label.error'),
            // i18n.get('label.notImplementedMethod'));
            // return;
        }

    },

    getParametersValue : function () {
        var result = [];
        if (Ext.isEmpty(this.parametersFieldset.items)) {
            return result;
        }
        this.parametersFieldset.items.each(function (param) {
            if (Ext.isFunction(param.getValue)) {
                result.push({
                    name : param.name,
                    value : param.getValue()
                });
            }
        }, this);
        return result;
    },

    setRecord : function (rec) {
        this.rec = rec;
    }

});

Ext.reg('s-formParametersConfigUtil', sitools.admin.common.FormParametersConfigUtil);
