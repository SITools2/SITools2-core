#!/bin/bash

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Building tool for Mizar ###"
    echo ""
    echo ""
    echo " If <mizar_git_repository> parameter is not set, the script will get Mizar from Github repository."
    echo " If <mizar_git_repository> parameter is set, the script will get Mizar from the given repository parameter."
    echo ""
    echo "    Usage : $0 <sitools_root_directory> (<mizar_git_repository>)"
    echo ""
    echo "              $0 /D/PROJECTS/SITOOLS/CNES-ULISSE-2.0-GIT /D/PROJECTS/SITOOLS/CNES-ULISSE-2.0-GIT/extensions/mizar"
    echo ""
    echo "###"
    exit 0
fi

# Input Parameters
TARGET=$1/workspace/client-extension-3.0/resources/libs/mizar
SITOOLS_OVERRIDES=$1/workspace/client-extension-3.0/resources/libs/overrides/mizar

MIZAR_PARAM=$2

echo ""
echo "Delete ${TARGET} directory."
rm -rf ${TARGET}

if [ -z ${MIZAR_PARAM} ]; then
    echo ""
    echo "Git clone Mizar."
    rm -rf mizar
    git clone https://github.com/SITools2/MIZAR.git mizar

    echo ""
    echo "Get Mizar submodules."
    cd mizar
    git submodule init
    git submodule update
    cd ..
    MIZAR=`pwd`/"mizar"
else
    MIZAR=${MIZAR_PARAM}
fi

echo ""
echo "Deploy Mizar"
if [ ! -d "${TARGET}" ]; then
    mkdir ${TARGET}
fi
cp -r ${MIZAR}/* ${TARGET}

echo ""
echo "Clear git repository"
rm -rf ${TARGET}/.git

echo ""
echo "Apply Sitools2 specific overrides"
cp -r ${SITOOLS_OVERRIDES}/* ${TARGET}

#Build mizar for Sitools
cd ${TARGET}/build
./buildSitools.sh

if [ -z ${MIZAR_PARAM} ]; then
    echo ""
    echo "Clear temporary mizar directory."
    rm -rf ${MIZAR}
fi;


echo ""
echo "done"