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
Ext.namespace('sitools.public.widget.item');

Ext.define('sitools.public.widget.item.TextFilter', {
    extend : 'Ext.form.field.Trigger',
	alias : 'widget.s-filter',
	baseCls :"s-textfilter",
//	cls:"s-textfilter-text",
	triggerCls : "s-textfilter-trigger",
//	triggerConfig:{tag:"div", cls:"x-form-trigger s-textfilter-trigger"},
	enableKeyEvents:true,
	queryDelay:500,
	queryAction:"find",
	enumAction:"enum",
	queryParam:"query",
	localFilter:false,
	localFilterField:"",
	pageSize:"",
	
	constructor:function(params){
		this.callParent(arguments);
		if(this.store&&!this.localFilter){
			this.mon(this.store,"beforeload",this.onBeforeLoad,this);
		}
		if(this.localFilter===true){
			this.mon(this.store,"load",this.reset,this);
		}
	},
	
	initEvents:function(){
		this.callParent(arguments);
		this.mon(this.el,"keyup",this.filter,this,{buffer:this.queryDelay});
		this.mon(this.el,"keydown",this.keydownevent,this);
	},
	
	setPageSize:function(size){
		this.pageSize=size;
	},
	

	onBeforeLoad : function (store, operation, eOpts) {
        var c = this.getValue();
        if (Ext.isEmpty(operation.params)) {
            operation.params = {};
        }
        if (c) {
            operation.params[this.queryParam] = c;
            operation.params.action = this.queryAction;
        } else {
            operation.params.action = this.enumAction;
        }
        return true;
    },
	
	filter: function(){
		var b=this.getValue();
		var a = {};
		if(!this.store){
			return;
		}
		if(this.localFilter===true){
			if(b){
				this.store.filter(this.localFilterField,b,true);
			}else{
				this.store.clearFilter(false);
			}
			return;
		}
		if (!Ext.isEmpty(this.pageSize)){
			a={start:0,limit:this.pageSize};
		}
		if(b){
			a.action=this.queryAction;
			a[this.queryParam]=b;
			this.store.load({params:a});
		}else{
			a.action=this.enumAction;
			this.store.load({params:a});
		}
	},
	
	reset: function(){
		console.log("reset");
		this.callParent(arguments);
		if (this.localFilterField === false && this.store) {
			this.store.clearFilter(false);
		}
	},
	
	onTriggerClick: function(){
		console.log("onTriggerClick");
		if(this.getValue()){
			this.setValue("");
			this.filter();
		}
	}
});

