:: %1 correspond to the project to build

cd %1
echo "install gulp into %1"
npm install gulp gulp-concat gulp-uglifyjs
echo "build of %1 javascript files"
:: build des fichiers javascripts
gulp