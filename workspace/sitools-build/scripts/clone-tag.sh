#! /bin/bash
#$1 correspond à l'url du dépot 
#$2 correspond au dossier destination du clone
#$3 correspond au nom du tag à récupérer
git clone ${1} ${2}
cd ${2}
#check that the tag exists
[ "`git tag | grep ^${3}$`" != "${3}" ] && echo "TAG ${3} does not exists, aborting" && exit 1;
git checkout ${3}