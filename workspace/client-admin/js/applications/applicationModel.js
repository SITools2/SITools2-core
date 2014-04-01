Ext.define('ApplicationModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name : 'id',
        type : 'string'
    }, {
        name : 'name',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'category',
        type : 'string'
    }, {
        name : 'urn',
        type : 'string'
    }, {
        name : 'type',
        type : 'string'
    }, {
        name : 'url',
        type : 'string'
    }, {
        name : 'author',
        type : 'string'
    }, {
        name : 'owner',
        type : 'string'
    }, {
        name : 'lastUpdate',
        type : 'string'
    }, {
        name : 'status',
        type : 'string'
    }, {
        name : 'wadl',
        type : 'string'
    }],
});