Ext.define('DatasetModel', {
    extend : 'Ext.data.Model',
    fields : [{
        name : 'id',
        type : 'string'
    }, {
        name : 'dataIndex',
        type : 'string'
    }, {
        name : 'schemaName',
        mapping : 'schema',
        type : 'string'
    }, {
        name : 'tableAlias',
        type : 'string'
    }, {
        name : 'tableName',
        type : 'string'
    }, {
        name : 'header',
        type : 'string'
    }, {
        name : 'toolTip',
        type : 'string'
    }, {
        name : 'width',
        type : 'int'
    }, {
        name : 'sortable',
        type : 'boolean'
    }, {
        name : 'visible',
        type : 'boolean'
    }, {
        name : 'filter',
        type : 'boolean'
    }, {
        name : 'columnOrder',
        type : 'int'
    },
    // {name : 'urlColumn', type : 'boolean'},
    // {name : 'previewColumn', type : 'boolean'},
    {
        name : 'columnRendererCategory',
        type : 'String'
    }, {
        name : 'columnRenderer',
        type : 'object'
    }, {
        name : 'primaryKey',
        type : 'boolean'
    }, {
        name : 'sqlColumnType',
        type : 'string'
    }, {
        name : 'columnAlias',
        type : 'string'
    }, {
        name : 'specificColumnType',
        type : 'string'
    }, {
        name : 'javaSqlColumnType',
        type : 'int'
    }, {
        name : 'columnClass',
        type : 'int'
    }, {
        name : 'dimensionId', 
        type : 'string'
    }, {
        name : 'unit'            
    }, {
        name : 'format', 
        type : 'string'
    }, {
        name : 'orderBy', 
        type : 'string'
    }]
});