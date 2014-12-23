:: %1 correspond au root directory

cd %1/workspace/client-admin
echo "install gulp into client-admin"
call npm install gulp gulp-concat gulp-uglifyjs
echo "build of client-admin javascript files"
:: build des fichiers javascripts de client-admin
call gulp

cd %1/workspace/client-user-3.0
echo "install gulp into client-user-3.0"
call npm install gulp gulp-concat gulp-uglifyjs
echo "build of client-user-3.0 javascript files"
:: build des fichiers javascripts
call gulp
call gulp build-with-plugins

cd %1/workspace/client-portal-3.0
echo "install gulp into client-portal-3.0"
call npm install gulp gulp-concat gulp-uglifyjs
echo "build of client-portal-3.0 javascript files"
:: build des fichiers javascripts
call gulp