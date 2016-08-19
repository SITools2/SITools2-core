@ECHO off

:: %1 is the directory of the client-portal project

if "%1"=="" goto usage

cd %1
echo "install gulp into client-portal"
call npm install gulp gulp-concat gulp-uglify pump gulp-rename
echo "build of client-portal javascript files"
:: build des fichiers javascripts de client-portal
call gulp
EXIT /B


:usage
echo "Usage : minifyjs.bat <client-portal-directory>"