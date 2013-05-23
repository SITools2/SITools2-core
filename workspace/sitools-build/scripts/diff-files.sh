#! /bin/bash
#${1} correspond au commit d'origine
#${2} correspond au commit de fin
#${3} correspond au dossier destination du clone

cd ${3}

git diff --name-only --diff-filter=MCR ${1} ${2}> modified_list-diff.txt
git diff --name-only --diff-filter=A  ${1} ${2}> added_list-diff.txt
git diff --name-only --diff-filter=D  ${1} ${2}> deleted_list-diff.txt

git ls-tree -r --name-only ${2} > list-file.txt