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

sitools.widget.TextFilter = Ext.extend(Ext.form.TriggerField, {
	
	ctCls:"s-textfilter",
	cls:"s-textfilter-text",
	triggerConfig:{tag:"div", cls:"x-form-trigger s-textfilter-trigger"},
	enableKeyEvents:true,
	listeners:{
		keyup: { fn:function(tf,a) {tf.trigger.setVisible((tf.getValue()!==""));} },
		render: { fn:function(tf) {tf.trigger.hide();} }
	},
	queryDelay:500,
	queryAction:"find",
	enumAction:"enum",
	queryParam:"query",
	localFilter:false,
	localFilterField:"",
	pageSize:"",
	
	constructor:function(params){
		sitools.widget.TextFilter.superclass.constructor.call(this,params);
		if(this.store&&!this.localFilter){
			this.mon(this.store,"beforeload",this.onBeforeLoad,this);
		}
		if(this.localFilter===true){
			this.mon(this.store,"load",this.reset,this);
		}
	},
	
	initEvents:function(){
		sitools.widget.TextFilter.superclass.initEvents.call(this);
		this.mon(this.el,"keyup",this.filter,this,{buffer:this.queryDelay});
	},
	
	setPageSize:function(size){
		this.pageSize=size;
	},
	
	onBeforeLoad:function(a,b){
		var c=this.getValue();
		if(c){
			b.params[this.queryParam]=c;
			b.params.action=this.queryAction;
		}else{
			b.params.action=this.enumAction;
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
		sitools.widget.TextFilter.superclass.reset.call(this);
		if(this.localFilterField===false&&this.store){
			this.store.clearFilter(false);
		}
	},
	
	onTriggerClick: function(){
		if(this.getValue()){
			this.setValue("");
			this.trigger.hide();
			this.filter();
		}
	}
});

//register type
Ext.reg('s-filter', sitools.widget.TextFilter);
