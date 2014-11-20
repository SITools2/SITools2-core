var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglifyjs');
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
        gulp.src(data)
            // concatenate all files into admin.all.js
            .pipe(concat("app.all.js"))
            .pipe(gulp.dest("dist/"))
            // uglify all files into admin.min.js
            .pipe(uglify("app.min.js"))
            .pipe(gulp.dest("dist/"));
            // both files are created into the dist folder
    });
});