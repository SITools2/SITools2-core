Ext.define('js.utils.utils', {
    
    getLastSelectedRecord : function () {
        return this.getStore().getById(this.getSelectionModel().getLastSelected().getId());
     }
    
});