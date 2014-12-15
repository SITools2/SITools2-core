@ECHO off

:: %1 is the directory of the client-admin project

if "%1"=="" goto usage

cd %1
echo "install gulp into client-admin"
call npm install gulp gulp-concat gulp-uglifyjs
echo "build of client-admin javascript files"
:: build des fichiers javascripts de client-admin
call gulp


:usage
echo "Usage : minifyjs.bat <client-admin-directory>"