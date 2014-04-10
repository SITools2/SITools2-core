Ext.define('js.utils.utils', {

    getLastSelectedRecord : function (grid) {
        if (!Ext.isEmpty(grid)) {
            if (Ext.isEmpty(grid.getSelectionModel().getLastSelected())) {
                return;
            }
            return grid.getStore().getById(grid.getSelectionModel().getLastSelected().getId());
        } else {
            if (Ext.isEmpty(this.getSelectionModel().getLastSelected())) {
                return;
            }
            return this.getStore().getById(this.getSelectionModel().getLastSelected().getId());
        }
    }

});