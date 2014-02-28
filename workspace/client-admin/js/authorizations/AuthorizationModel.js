Ext.define('AuthorizationModel', {
    idProperty : 'role',
    extend : 'Ext.data.Model',
    fields : [{
        name : 'role',
        type : 'string'
    }, {
        name : 'allMethod',
        type : 'boolean'
    }, {
        name : 'postMethod',
        type : 'bool'
    }, {
        name : 'getMethod',
        type : 'bool'
    }, {
        name : 'putMethod',
        type : 'bool'
    }, {
        name : 'deleteMethod',
        type : 'bool'
    }, {
        name : 'headMethod',
        type : 'bool'
    }, {
        name : 'optionsMethod',
        type : 'bool'
    }]
});