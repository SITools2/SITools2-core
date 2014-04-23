Ext.define('clientuser.store.ProjectStore', {
    requires : ['clientuser.model.Project'],
    extend: 'Ext.data.Store',
    model : 'clientuser.model.Project',
    proxy: {
        type: 'ajax',
        url: '/sitools/portal/projects',
        reader: {
            totalProperty: 'total',
            type: 'json',
            root: 'data',
            idProperty : 'name'
        }
    }
});