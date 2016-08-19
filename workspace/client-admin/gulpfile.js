var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var pump = require('pump');
var fs = require("fs");

gulp.task('default', function() {
    var cwd = process.cwd();

    // read file list 
    fs.readFile(cwd + "/conf/files.json", "utf-8", function (err, _data) {
         if (err) {
             console.log("Error: " + err);
             return;
         }
         
         // parse the file list as a JSON Object
         data = JSON.parse(_data);
         
        // Build the sources
        pump([
              gulp.src(data),
            // concatenate all files into admin.all.js
            concat("app.all.js"),
            gulp.dest("dist/"),
            rename("app.min.js"),
            // uglify all files into admin.min.js
            uglify(),
            gulp.dest("dist/")
            // both files are created into the dist folder
        ]);
    });
});