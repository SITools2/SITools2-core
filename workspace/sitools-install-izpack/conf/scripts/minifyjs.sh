#! /bin/bash
#${1} correspond au root directory

cd ${1}

## ${1} correspond au root directory

cd ${1}/workspace/client-admin
echo "install gulp into client-admin"
npm install gulp gulp-concat gulp-uglifyjs
echo "build client-admin javascript files"
## build des fichiers javascripts
gulp

cd ${1}/workspace/client-user-3.0
echo "install gulp into client-user-3.0"
npm install gulp gulp-concat gulp-uglifyjs
echo "build client-user javascript files"
## build des fichiers javascripts
gulp

cd ${1}/workspace/client-portal-3.0
echo "install gulp into client-portal-3.0"
npm install gulp gulp-concat gulp-uglifyjs
echo "build client-portal javascript files"
## build des fichiers javascripts
gulp
