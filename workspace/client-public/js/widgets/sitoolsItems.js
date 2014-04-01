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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/
Ext.namespace('sitools.widget');

/**
 * Basic Box for all admin panels.
 * @class sitools.widget.Box
 * @extends Ext.Panel
 */
Ext.define('sitools.widget.Box', {
    extend : 'Ext.panel.Panel',
	alias : 'widget.s-box',
    width : '98%',
    frame : true,
    baseCls : 'x-box',
    cls : 'x-box-blue module-root centering',
    _title : '',

    constructor : function (cfg) {

	    sitools.widget.Box.superclass.constructor.call(this, Ext.apply({
	        _title : cfg.label,
	        _idItem : cfg.idItem
	    }, cfg));
    },

    initComponent : function () {
	    this.items.unshift({
	        xtype : 'component',
	        html : this._title,
	        cls : 'subtitle icon-' + this.idItem
	    });
	    sitools.widget.Box.superclass.initComponent.call(this);
    } 
    
});


/**
 * A simple json store with new methods
 * @class sitools.widget.JsonStore
 * @extends Ext.data.JsonStore
 */
sitools.widget.JsonStore = Ext.extend(Ext.data.JsonStore, {
    /**
     * dirty : true if one record have been added
     * @type Boolean
     */
    dirty : false,

    /**
     * Setter for dirty
     * @param {Boolean} value
     */
    _setDirty : function (value) {
	    this.dirty = value;
    },
    /**
     * Get the dirty value
     * @return {Boolean}
     */
    _getDirty : function () {
	    return this.dirty;
    },
    /**
     * Set dirty to true
     * @param {Array} records the list of records added
     */
    add : function (records) {
	    this.dirty = true;
	    sitools.widget.JsonStore.superclass.add.call(this, records);
    },
    /**
     * Set dirty to true
     * @param {Ext.data.Record} record the record removed
     */
    remove : function (record) {
	    this.dirty = true;
	    sitools.widget.JsonStore.superclass.remove.call(this, record);
    }
});

/**
 * A simple Ext.Button with a specific iconCls
 * @class sitools.widget.menuButton
 * @extends Ext.Button
 */
Ext.define('sitools.widget.menuButton', {
    extend : 'Ext.Button',
    alias : 'widget.s-menuButton',
	constructor : function (config) {

		config = Ext.apply({
			iconCls : 'menu-button'
		}, config);

		sitools.widget.menuButton.superclass.constructor.call(this, config);
	}
});


/**
 * @class TemplateTreeNode
 * @extends Ext.tree.AsyncTreeNode
 * 
 */
sitools.widget.templateTreeNode = function (attributes) {
	var tpl;
	if (attributes.tpl === undefined) {
		tpl = new Ext.Template(
		        "<div >{text}<b>{nbRecords} records</b><img src='{urlImage}'></img><a style='text-align:right' href='{urlReadme}'>readme</a></div>");
	} else {
		tpl = attributes.tpl;
	}
	attributes.text = tpl.apply(attributes);
	attributes.leaf = false;
	var config = Ext.apply({
		children : []
	}, attributes);

	sitools.widget.templateTreeNode.superclass.constructor.call(this, config);

};

Ext.extend(sitools.widget.templateTreeNode, Ext.tree.AsyncTreeNode, {

});
// add this new node to the list of nodes
// TODO Ext.tree.Panel.nodeTypes.templateTreeNode = sitools.widget.templateTreeNode;
//Ext.tree.Panel.nodeTypes.templateTreeNode = sitools.widget.templateTreeNode;

/**
 * A toolbar with buttons to move rows up or down
 * 
 * @cfg {string} gridId :
 *            the id of the grid. This is not mandatory if the grid is
 *            not the scope of the buttons
 * @class sitools.widget.GridSorterToolbar
 * @extends Ext.Toolbar
 */
Ext.define('sitools.widget.GridSorterToolbar', {
    extend : 'Ext.Toolbar',
	alias : 'sitools.widget.GridSorterToolbar',
	alignRight : true, //default to true
    initComponent : function () {
        
        
        sitools.widget.GridSorterToolbar.superclass.initComponent.call(this);
        
        if (this.alignRight) {
            this.add('->');
        }
        this.add(new sitools.widget.GridTop({
    					gridId : this.gridId
    				}), new sitools.widget.GridUp({
    					gridId : this.gridId
    				}), new sitools.widget.GridDown({
    					gridId : this.gridId
    				}), new sitools.widget.GridBottom({
    					gridId : this.gridId
    				}));
    }
});

/**
 * A RowExpander used for plugin grids to add violation informations 
 * The parameters are the same as the class Ext.ux.grid.RowExpander
 * 
 * @class sitools.admin.resourcesPlugins.violationRowExpander
 * @extends Ext.ux.grid.RowExpander
 */
sitools.widget.ViolationRowExpander = Ext.extend(
   Ext.ux.grid.RowExpander, {
	   alias : 'sitools.widget.ViolationRowExpander',
        getRowClass : function (record, index, rowParams, store) {
            //call the method from the superclass
            var cls = sitools.widget.ViolationRowExpander.superclass.getRowClass.call(this,
                    record, index, rowParams, store);
            //add a class depending on the violation type
            var violation = record.get("violation");
            if (!Ext.isEmpty(violation)) {
                if (violation.level == "CRITICAL") {
                    cls += " red-row";
                } else if (violation.level == "WARNING") {
                    cls += " orange-row";
                }
            }
            return cls;
        }
});


/**
 * Color picker on a triggerField * 
 * @event select when a new color is selected
 * @class sitools.widget.colorField
 * @extends Ext.form.TriggerField
 */
sitools.widget.colorField = Ext.extend(
	Ext.form.TriggerField, {
		onTriggerClick : function (e) {
			var cp = new Ext.menu.ColorMenu({
		        scope : this, 
		        handler: function (cm, color) {
		            this.setValue("#" + color);
		            this.setFontColor("#" + color);
                    this.fireEvent("select", this, color);
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
		    this.el.setStyle({
				'color' : (avg > 128) ? '#000' : '#FFF', 
				'background-color' : color, 
				'background-image' : "none"
		    });
            
		}, 
		listeners : {
			afterrender : function (tf) {
				tf.setFontColor(tf.getValue());
			}
		}
	}
)

Ext.define('sitools.widget.DateFieldWithToday', {
    extend : 'Ext.form.field.Date',
    
	regToday : new RegExp("^\{\\$TODAY\}"), 
    invalidTextWithToday : "Impossible to make a date with {0}. A valid example is {$TODAY} + 1", 
    parseDate : function (value) {
        if(!value || Ext.isDate(value)){
            return value;
        }
		//Ajout d'un test sur la valeur pour sortir s'il y a la valeur {$TODAY}
		if (this.regToday.test(value)) {
        	return value;
        }
        var v = this.safeParse(value, this.format),
            af = this.altFormats,
            afa = this.altFormatsArray;

        if (!v && af) {
            afa = afa || af.split("|");

            for (var i = 0, len = afa.length; i < len && !v; i++) {
                v = this.safeParse(value, afa[i]);
            }
        }
        return v;
    },
    getErrors : function (value) {
        var errors = Ext.form.DateField.superclass.getErrors.apply(this, arguments);

        value = this.formatDate(value || this.processRawValue(this.getRawValue()));

        if (value.length < 1) { // if it's blank and textfield didn't flag it then it's valid
             return errors;
        }

        var svalue = value;
        // Ne pas parser la date en objet Date si {$TODAY} est présent
        var time = false;
        if (this.regToday.test(value)) {
        	try {
				value = sitools.common.utils.Date.stringWithTodayToDate(value);
				if (!sitools.common.utils.Date.isValidDate(value)) {
					throw "";
				}
			}
        	catch (err) {
        		errors.push(String.format(this.invalidTextWithToday, svalue));
        		return errors;	
        	}
        	
        }
        else {
        	value = this.parseDate(value);
        }
        
        if (!value) {
            errors.push(String.format(this.invalidText, svalue, this.format));
            return errors;
        }

        time = value.getTime();
        
        if (this.minValue && time < this.minValue.clearTime().getTime()) {
            errors.push(String.format(this.minText, this.formatDate(this.minValue)));
        }

        if (this.maxValue && time > this.maxValue.clearTime().getTime()) {
            errors.push(String.format(this.maxText, this.formatDate(this.maxValue)));
        }

        if (this.disabledDays) {
            var day = value.getDay();

            for(var i = 0; i < this.disabledDays.length; i++) {
                if (day === this.disabledDays[i]) {
                    errors.push(this.disabledDaysText);
                    break;
                }
            }
        }

        var fvalue = this.formatDate(value);
        if (this.disabledDatesRE && this.disabledDatesRE.test(fvalue)) {
            errors.push(String.format(this.disabledDatesText, fvalue));
        }

        return errors;
    	
    }, 
    setValue : function (date) {
    	if (this.regToday.test(date)) {
    		return Ext.form.DateField.superclass.setValue.call(this, date);
    	}
    	else {
			return Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
    	
    	}
    	
    }
});

//sitools.widget.rootTreeLoader = Ext.extend(Ext.tree.TreeLoader, {
//	createNode : function (attr) {
//		if (Ext.isEmpty(attr.children)) {
//			attr.leaf = true;
//		}
//		return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
//	}, 
//	processResponse : function (response, node, callback, scope) {
//	    var json = response.responseText, children, newNode, i = 0, len;
//	    try {
//	
//	        if (!(children = response.responseData)) {
//	            children = Ext.decode(json);
//	            if (this.root) {
//	                if (!this.getRoot) {
//	                    this.getRoot = Ext.data.JsonReader.prototype.createAccessor(this.root);
//	                }
//	                children = this.getRoot(children);
//	            }
//	        }
//	        node.beginUpdate();
//	        for (len = children.length; i < len; i++) {
//	            newNode = this.createNode(children[i]);
//	            if (newNode) {
//	                node.appendChild(newNode);
//	            }
//	        }
//	        node.endUpdate();
//	        this.runCallback(callback, scope || node, [ node ]);
//	    } catch (e) {
//	        this.handleFailure(response);
//	    }
//	}, 
//	setUrl : function (url) {
//		this.url = url;
//	}
//});