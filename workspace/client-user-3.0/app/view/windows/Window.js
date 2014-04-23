Ext.define('clientuser.view.windows.Window', {
   requires : ['clientuser.store.ProjectStore'],
    extend: 'Ext.Window',
    height : 300,
    width : 300,
    initComponent : function(){
        
        
      this.grid = Ext.create('Ext.grid.Panel', {
          layout : 'fit',
          forceFit : true,
         store : Ext.create('clientuser.store.ProjectStore'),
         columns : [{
             header : 'name',
             dataIndex : 'name'
         },
         {
             header : 'description',
             dataIndex : 'description'
         },
         {
             header : 'image',
             dataIndex : 'image'
         }]
      });
        
        Ext.apply(this, {
            items : [this.grid],
            buttons: [ {text: 'mon bouton' } ] 
          });
        
        this.callParent(arguments);
      
        
    },
    
});