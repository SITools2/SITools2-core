Ext.define('sitools.admin.utils.utils', {
	
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
    },
    
    generateListOfFile : function() {
    	
    	var loader = Ext.Loader;
    	var fileMap = loader.classNameToFilePathMap;
    	
    	var jsFile = [];
    	
    	Ext.each(loader.history, function(file) {
    		var filePath = fileMap[file];
    		filePath = filePath.replace("client-public", "client-public-3.0");
    		jsFile.push(filePath);
    	});
    	
    	this.downloadFileFromObject(JSON.stringify(jsFile, null, "\t"), "files.json", "text/json");
    },
    
    downloadFileFromObject : function (object, name, type) {
        var a = document.createElement("a");
        var file = new Blob([object], {type: type});
        a.href = URL.createObjectURL(file);
        a.download = name;
        a.click();
    }
});