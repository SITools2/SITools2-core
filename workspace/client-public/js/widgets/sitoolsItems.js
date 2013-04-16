/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
sitools.widget.Box = Ext.extend(Ext.Panel, {
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
// register type
Ext.reg('s-box', sitools.widget.Box);

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
sitools.widget.menuButton = Ext.extend(Ext.Button, {

	constructor : function (config) {

		config = Ext.apply({
			iconCls : 'menu-button'
		}, config);

		sitools.widget.menuButton.superclass.constructor.call(this, config);
	}
});
Ext.reg('s-menuButton', sitools.widget.menuButton);

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
Ext.tree.TreePanel.nodeTypes.templateTreeNode = sitools.widget.templateTreeNode;

/**
 * Window that contains a tools to sort a store
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you can do a sort. 
 * @class sitools.widget.sortersTool
 * @extends Ext.Window
 */
sitools.widget.sortersTool = Ext.extend(Ext.Window, {
    
    initComponent : function () {
        
		var dataCombo = [ [ "", "" ] ];
	    var columns;
	    if (! Ext.isEmpty(this.columnModel.columns)) {
	    	var columns = this.columnModel.columns;
	    }
	    else {
	    	columns = this.columnModel;
	    }
	    Ext.each(columns, function (column) {
		    dataCombo.push([ column.columnAlias, column.header ]);
	    });

	    this.storeCombo = new Ext.data.ArrayStore({
	        fields : [ "dataIndex", "columnHeader" ],
	        data : dataCombo
	    });

	    this.f = new Ext.form.FormPanel({
            padding : 5
        });
	    var sorters = this.store.getSortState(), len;
	    if (! Ext.isEmpty(sorters)) {
		    if (Ext.isArray(sorters.sorters)) {
		    	this.sorters = sorters.sorters;
		    }
		    else {
		    	this.sorters = [sorters];
		    }
		    len = this.sorters.length;
	    }	   
	    else {
	    	this.sorter = [];
	    	len = 3;
	    }
	    
	    for (var i = 0; i < len; i++) {
		    var compositeField = this.buildCompositeField(i);
		    this.f.add(compositeField);
	    }

	    Ext.apply(this, {
	        title : i18n.get("label.multiSort"),
	        autoScroll : true, 
	        width : 400,
	        modal : true, 
	        items : [ this.f ],
	        buttons : [ {
	            text : i18n.get('label.add'),
	            scope : this,
	            handler : this.onAdd
	        }, {
	            text : i18n.get('label.remove'),
	            scope : this,
	            handler : this.onRemove
	        }, {
	            text : i18n.get('label.ok'),
	            scope : this,
	            handler : this.onValidate
	        }, {
	            text : i18n.get('label.cancel'),
	            scope : this,
	            handler : function () {
		            this.close();
	            }
	        } ]
	    });
	    sitools.widget.sortersTool.superclass.initComponent.call(this);
    },
    /**
     * Build a sorters objetct to apply it to a store.
     */
    onValidate : function () {
	    
    	var sorters = [];
    	Ext.each(this.f.items.items, function (compositeField) {
    		if (!Ext.isEmpty(compositeField.items.items[0].getValue())) {
	    		sorters.push({
	    			field : compositeField.items.items[0].getValue(),
	    			direction : compositeField.items.items[1].getValue().getGroupValue()
	    		});
	    	}
	    }, this); 
    
        if (sorters.length < 1) {
    		this.close();
       		return;
    	}
    	else if (sorters.length == 1) {
    		this.store.sort(sorters[0].field, sorters[0].direction);
    	}
    	else {
        	this.store.sort(sorters);
    	}
	    this.close();
    },
    /**
     * Add a new sort option. 
     * @param {numeric} i the index of the sort option
     * @return {Ext.form.CompositeField} the composite field with a comboBox to choose the column and a sort information.
     */
    buildCompositeField : function (i) {
	    var combo = new Ext.form.ComboBox({
	        typeAhead : true,
	        name : "field" + 1,
	        triggerAction : 'all',
	        lazyRender : true,
	        mode : 'local',
	        store : this.storeCombo,
	        valueField : 'dataIndex',
	        displayField : 'columnHeader',
	        flex : 0.6
	    });

	    var direction = new Ext.form.RadioGroup({
	        fieldLabel : i18n.get('label.direction'),
	        flex : 0.4,
	        value : 'ASC',
	        items : [ {
	            boxLabel : 'ASC',
	            name : 'direction' + i,
	            inputValue : 'ASC'
	        }, {
	            boxLabel : 'DESC',
	            name : 'direction' + i,
	            inputValue : 'DESC'
	        } ]
	    });
	    var compositeField = new Ext.form.CompositeField({
	        labelWidth : 100,
	        anchor : '100%',
	        fieldLabel : i18n.get('label.sortingOrder'),
	        items : [ combo, direction ]
	    });
	    return compositeField;
    }, 
    /**
     * Called on add button : 
     * Adds a new sort option. 
     */
    onAdd : function () {
    	var i = this.f.items.length;
    	var compositeField = this.buildCompositeField(i);
	    this.f.add(compositeField);
	    this.f.doLayout();
	    this.doLayout();
    }, 
    /**
     * Called when remove button is pressed.
     * Remove the last sort option.
     */
    onRemove : function () {
    	var i = this.f.items.length - 1;
    	if (i < 0) {
    		return;
    	}
    	this.f.remove(this.f.getComponent(i));
	    this.f.doLayout();
	    this.doLayout();
    }, 
    /**
     * Fill the panel with the sort configuration of the store.
     */
    afterRender : function () {
    	sitools.widget.sortersTool.superclass.afterRender.call(this);
    	this.setPosition(this.dataview.pos);
    	var i = 0;
    	Ext.each(this.sorters, function (sorter) {
    		var compositeField = this.f.getComponent(i);
    		compositeField.items.items[0].setValue(sorter.field);
    		compositeField.items.items[1].setValue(sorter.direction);
    		i++;
    	}, this); 
    }
});
Ext.reg('sitools.widget.sortersTool', sitools.widget.sortersTool);
sitools.widget.sortersTool.getParameters = function () {
	return [];
}
sitools.widget.sortersTool.executeAsService = function (config){
	var sorterTool = new sitools.widget.sortersTool(config);
	sorterTool.show();
}

/**
 * Build a window to define filter on a store using Filter API.  
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
sitools.widget.filterTool = Ext.extend(Ext.Window, {
    paramPrefix : 'filter', 
    
    initComponent : function () {

		var dataCombo = [ [ "", "" ] ];
	    var columns;
	    if (! Ext.isEmpty(this.columnModel.columns)) {
	    	var columns = this.columnModel.columns;
	    }
	    else {
	    	columns = this.columnModel;
	    }
	    Ext.each(columns, function (column) {
		    dataCombo.push({
		    	columnAlias : column.columnAlias, 
		    	columnHeader : column.header, 
		    	columnType : sql2ext.get(column.sqlColumnType)
		    });
	    });

	    this.storeCombo = new Ext.data.JsonStore({
	        fields : [ "columnAlias", "columnHeader", "columnType" ],
	        data : dataCombo
	    });

	    this.f = new Ext.Panel();
	    this.filters = this.store.filtersCfg;
	    var len;
	    if (! Ext.isEmpty(this.filters)) {
		    len = this.filters.length;
	    }	   
	    else {
	    	this.filters = [];
	    	len = 3;
	    }
        
        
        for (var i = 0; i < len; i++) {
            var compositeField = this.buildCompositeField(i);
            this.f.add(compositeField);
        }    

	    Ext.apply(this, {
	        title : i18n.get("label.filter"),
	        autoScroll : true, 
	        width : 400,
	        modal : true, 
	        items : [ this.f ],
	        buttons : [ {
	            text : i18n.get('label.add'),
	            scope : this,
	            handler : this.onAdd
	        }, {
	            text : i18n.get('label.remove'),
	            scope : this,
	            handler : this.onRemove
	        }, {
	            text : i18n.get('label.ok'),
	            scope : this,
	            handler : this.onValidate
	        }, {
	            text : i18n.get('label.cancel'),
	            scope : this,
	            handler : function () {
		            this.close();
	            }
	        } ]
	    });
	    sitools.widget.filterTool.superclass.initComponent.call(this);
    },
    getCompositeField : function (index) {
		return this.f.items.items[index];		
    }, 
    onValidate : function () {
	    
        if(Ext.isEmpty(this.store.filters)){
            this.store.filters = new sitools.widget.FiltersCollection();
        }
        
        this.store.filters.clear();
        
    	var filters = [];
    	var filtersCfg = [];
        var i = 0;
        var store = this.store;
    	Ext.each(this.f.items.items, function (compositeField) {
            if (!Ext.isEmpty(compositeField.items.items[0].getValue())) {
	    		var filterCmp = compositeField.findBy(function(cmp) {
	    			return cmp.specificType == "filter";
	    		});
	    		var filterValue;
	    		if (!Ext.isEmpty(filterCmp) && ! Ext.isEmpty(filterCmp[0])) {
	    			var filter = filterCmp[0];
	    			//Build the filters for the store query
	    			filterValue = filter.getValue();                    
	    			Ext.each(filterValue, function (filterValueItem) {
                        store.filters.add(i++, filterValueItem);        
						if (!Ext.isEmpty(filterValueItem)) {
	    					filters.push (filterValueItem);
						}
    				});
	    			if (!Ext.isEmpty(filter.getConfig())) {
	    				filtersCfg.push(filter.getConfig());
	    			}
	    			
	    		}
	    	}
	    }, this);
	    
        this.store.filtersCfg = filtersCfg;
//	    this.store.filters = filters;
        
        // var options = this.store.storeOptions() || {};
        var options = this.store.lastOptions || {};
        
//	    
	    options.params = options.params || {};
        
        this.cleanParams(options.params);
          
        //var params = this.store.buildQuery(filters);
        //set that this a new filter configuration
        this.store.isNewFilter = true;
        //Ext.apply(options.params, params);
        //save the options has last options in the store
        //this.store.storeOptions(options);
        this.store.load(options);
	    this.close();
    },
     
    cleanParams : function (p) {
        // if encoding just delete the property
        if (this.encode) {
            delete p[this.paramPrefix];
        // otherwise scrub the object of filter data
        } else {
            var regex, key;
            regex = new RegExp('^' + this.paramPrefix + '\[[0-9]+\]');
            for (key in p) {
                if (regex.test(key)) {
                    delete p[key];
                }
            }
        }
    },
    buildCompositeField : function (i) {
	    var combo = new Ext.form.ComboBox({
	        index : i,
	        typeAhead : true,
	        name : "field" + 1,
	        triggerAction : 'all',
	        lazyRender : true,
	        mode : 'local',
	        store : this.storeCombo,
	        valueField : 'columnAlias',
	        displayField : 'columnHeader',
	        flex : 0.4, 
	        listeners : {
	        	"select" : function (combo, rec, index) {
	        		if (Ext.isEmpty(rec.data.columnAlias)) {
	        			return;
	        		}
        			var compositeField = combo.ownerCt;
        			var containerFilter = compositeField.items.items[1];
        			containerFilter.removeAll();
	        		switch (rec.data.columnType) {
	        		case ("numeric") :
	        			var filter = new sitools.widget.NumericFilter({
	        				columnAlias : rec.data.columnAlias
	        			});
	        			containerFilter.add(filter);
	        			compositeField.setHeight(filter._getHeight());
	        			containerFilter.setHeight(filter._getHeight());
	        			//containerFilter.syncSize();
	        			break;
	        		case ("string") :
	        			var filter = new sitools.widget.StringFilter({
	        				columnAlias : rec.data.columnAlias
	        			});
	        			
	        			containerFilter.add(filter);
	        			compositeField.setHeight(filter._getHeight());
	        			containerFilter.setHeight(filter._getHeight());
	        			break;
	        		case ("dateAsString") :
	        			var filter = new sitools.widget.DateFilter({
	        				columnAlias : rec.data.columnAlias
	        			});
	        			
	        			containerFilter.add(filter);
	        			compositeField.setHeight(filter._getHeight());
	        			containerFilter.setHeight(filter._getHeight());
	        			break;
	        		
	        		default :
						var filter = new sitools.widget.StringFilter({
	        				columnAlias : rec.data.columnAlias
	        			});
	        			
	        			containerFilter.add(filter);
	        			compositeField.setHeight(filter._getHeight());
	        			containerFilter.setHeight(filter._getHeight());
	        			break;
	        		}
	        		
	        	}
	        }
	    });

	    var filter = new Ext.Container({
	        flex : 0.6
	    });
	    var compositeField = new Ext.Container({
	        layout : "hbox",
	        items : [ combo, filter ], 
	        style: {
	            padding: '5px'
	        }
	    });
	    return compositeField;
    },
    onAdd : function () {
    	var i = this.f.items.length;
    	var compositeField = this.buildCompositeField(i);
	    this.f.add(compositeField);
	    this.f.doLayout();
	    this.doLayout();
    }, 
    onRemove : function () {
    	var i = this.f.items.length - 1;
    	if (i < 0) {
    		return;
    	}
    	this.f.remove(this.f.getComponent(i));
	    this.f.doLayout();
	    this.doLayout();
    }, 
    afterRender : function () {
    	sitools.widget.filterTool.superclass.afterRender.call(this);
    	this.setPosition(this.pos);
    	var i = 0;
    	Ext.each(this.filters, function (filter) {
    		var compositeField = this.f.getComponent(i);
    		this.updateFilterUI (compositeField, filter);
    		i++;
    	}, this); 
    }, 
    updateFilterUI : function (container, filter) {
    	var combo = container.items.items[0];
    	var store = combo.getStore();
    	var index = store.find("columnAlias", filter.columnAlias);
    	if (index)
    	var rec = store.getAt(index);
    	
    	combo.setValue(filter.columnAlias);
    	combo.fireEvent('select', combo, rec, index);
    	
    	
    	var filterCmp = container.findBy(function(cmp) {
			return cmp.specificType == "filter";
		});
		filterCmp[0].setValue(filter.value);
		
//	    console.log(container);
//    	console.log(filterCmp);
    }
});
Ext.reg('sitools.widget.filterTool', sitools.widget.filterTool);
sitools.widget.filterTool.getParameters = function () {
	return [];
}
sitools.widget.filterTool.executeAsService = function (config){
	var filterTool = new sitools.widget.filterTool(config);
	filterTool.show();
}


/**
 * A toolbar with buttons to move rows up or down
 * 
 * @cfg {string} gridId :
 *            the id of the grid. This is not mandatory if the grid is
 *            not the scope of the buttons
 * @class sitools.widget.GridSorterToolbar
 * @extends Ext.Toolbar
 */
sitools.widget.GridSorterToolbar = Ext.extend(Ext.Toolbar, {
    initComponent : function () {
        sitools.widget.GridSorterToolbar.superclass.initComponent.call(this);
        this.add('->', new sitools.widget.GridTop({
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
Ext.reg('sitools.widget.GridSorterToolbar', sitools.widget.GridSorterToolbar);

/**
 * A RowExpander used for plugin grids to add violation informations 
 * The parameters are the same as the class Ext.ux.grid.RowExpander
 * 
 * @class sitools.admin.resourcesPlugins.violationRowExpander
 * @extends Ext.ux.grid.RowExpander
 */
sitools.widget.ViolationRowExpander = Ext.extend(
   Ext.ux.grid.RowExpander, {
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

Ext.reg('sitools.widget.ViolationRowExpander', sitools.widget.ViolationRowExpander);

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

sitools.widget.DateFieldWithToday = Ext.extend(Ext.form.DateField, {
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

        value = this.formatDate(value || this.processValue(this.getRawValue()));

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

sitools.widget.rootTreeLoader = Ext.extend(Ext.tree.TreeLoader, {
	createNode : function (attr) {
		if (Ext.isEmpty(attr.children)) {
			attr.leaf = true;
		}
		return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
	}, 
	processResponse : function (response, node, callback, scope) {
	    var json = response.responseText, children, newNode, i = 0, len;
	    try {
	
	        if (!(children = response.responseData)) {
	            children = Ext.decode(json);
	            if (this.root) {
	                if (!this.getRoot) {
	                    this.getRoot = Ext.data.JsonReader.prototype.createAccessor(this.root);
	                }
	                children = this.getRoot(children);
	            }
	        }
	        node.beginUpdate();
	        for (len = children.length; i < len; i++) {
	            newNode = this.createNode(children[i]);
	            if (newNode) {
	                node.appendChild(newNode);
	            }
	        }
	        node.endUpdate();
	        this.runCallback(callback, scope || node, [ node ]);
	    } catch (e) {
	        this.handleFailure(response);
	    }
	}, 
	setUrl : function (url) {
		this.url = url;
	}
});