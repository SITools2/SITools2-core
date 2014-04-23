Ext.define('clientuser.model.Project', {
    extend: 'Ext.data.Model',
    fields : ['name', 'description', {
        name : 'image',
        mapping : 'image.url'
    }],
    idProperty: 'name'
});