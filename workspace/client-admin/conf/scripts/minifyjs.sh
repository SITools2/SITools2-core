#! /bin/bash
#${1} is the client-admin project directory

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Minify sources for client-admin ###"
    echo ""
    echo "" 
    echo "    Usage : $0 <client-admin-folder>"
    echo ""
    echo "              $0 ."
    echo ""
    echo "###"
    exit 0
fi 

cd ${1}
echo "install gulp into client-admin"
npm install gulp gulp-concat gulp-uglify pump gulp-rename
echo "build client-admin javascript files"
## build des fichiers javascripts
gulp