#!/bin/bash

if [ $# -lt 2 ]
then
    echo "###"
    echo ""
    echo " ### Deploy tool for Mizar ###"
    echo ""
    echo ""
    echo "    Usage : $0 <mizar_git_repository> <sitools_root_directory>"
    echo ""
    echo "              ./deploy_mizar.sh /D/PROJECTS/SITOOLS/CNES-ULISSE-2.0-GIT/extensions/mizar /D/PROJECTS/SITOOLS/CNES-ULISSE-2.0-GIT"
    echo ""
    echo "###"
    exit 0
fi

# Input Parameters
MIZAR=$1
TARGET=$2/workspace/client-extension-3.0/resources/libs/mizar
SITOOLS_OVERRIDES=$2/workspace/client-extension-3.0/resources/libs/overrides/mizar

echo ""
echo "Delete $TARGET directory."
rm -rf $TARGET

echo ""
echo "Deploy Mizar"
cp -r $MIZAR $TARGET

echo ""
echo "Clear git repository"
rm -rf $TARGET/.git

echo ""
echo "Apply Sitools2 specific overrides"
cp -r $SITOOLS_OVERRIDES/* $TARGET

echo ""
echo "done"