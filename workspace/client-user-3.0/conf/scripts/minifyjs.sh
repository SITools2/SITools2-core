#! /bin/bash
#${1} is the client-user project directory

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Minify sources for client-user ###"
    echo ""
    echo "" 
    echo "    Usage : $0 <client-user-folder>"
    echo ""
    echo "              $0 ."
    echo ""
    echo "###"
    exit 0
fi 

cd ${1}
echo "install gulp into client-user"
npm install gulp gulp-concat gulp-uglify pump gulp-rename
echo "build client-user javascript files"
## build des fichiers javascripts
gulp
gulp build-with-plugins