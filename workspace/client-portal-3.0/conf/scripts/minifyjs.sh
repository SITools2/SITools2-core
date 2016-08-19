#! /bin/bash
#${1} is the client-portal project directory

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Minify sources for client-portal ###"
    echo ""
    echo "" 
    echo "    Usage : $0 <client-portal-folder>"
    echo ""
    echo "              $0 ."
    echo ""
    echo "###"
    exit 0
fi 

cd ${1}
echo "install gulp into client-portal"
npm install gulp gulp-concat gulp-uglify pump gulp-rename
echo "build client-portal javascript files"
## build des fichiers javascripts
gulp