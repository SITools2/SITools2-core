/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.define('sitools.public.utils.PackagingUtils', {
	
    singleton : true,
    
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

var PackagingUtils = sitools.public.utils.PackagingUtils;