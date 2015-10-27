#!/bin/bash

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Building tool for Mizar ###"
    echo ""
    echo ""
    echo ""
    echo "    Usage : $0 <sitools_root_directory>"
    echo ""
    echo "              $0 /D/PROJECTS/SITOOLS/CNES-ULISSE-2.0-GIT"
    echo ""
    echo "###"
    exit 0
fi

# Input Parameters
TARGET=$1/workspace/client-extension-3.0/resources/libs/mizar
SITOOLS_OVERRIDES=$1/workspace/client-extension-3.0/resources/libs/overrides/mizar


echo ""
echo "Delete ${TARGET} directory."
rm -rf ${TARGET}

echo ""
echo "Git clone Mizar."
rm -rf mizar
git clone https://github.com/SITools2/MIZAR.git mizar
echo ""
echo -e " -> Checkout revision 465c9608259cc5947a20529a557ab5b11a3dadf7"
cd mizar
git checkout 465c9608259cc5947a20529a557ab5b11a3dadf7

echo ""
echo "Get Mizar submodules."
git submodule init
git submodule update
cd ..
MIZAR=`pwd`/"mizar"

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