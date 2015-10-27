#!/bin/bash

if [ $# -lt 1 ]
then
    echo "###"
    echo ""
    echo " ### Deploy tool for Mizar ###"
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
TARGET=$1
DATA=$1/data
CLIENT_EXT_DIR=$TARGET/workspace/client-extension-3.0

echo ""
echo "Deploy Mizar specific data files."
cp -r ${CLIENT_EXT_DIR}/data/* ${DATA}

echo ""
echo "Update projectIndex.flt file."
SCRIPT_DEFINITION=$(cat ${CLIENT_EXT_DIR}/resources/conf/mizarModule/projectIndex.mizar.ftl)

TMP_FILE=tmp.file
if [ -f "$TMP_FILE" ]; then
    rm ${TMP_FILE}
fi

while read line
do
    # Check if the lines aven't already been added to the file
    echo ${line} | grep -q "<!-- START MIZAR -->"
    [ $? -eq 0 ] && OK=false && break
    #Insert projectIndex.mizar.ftl before <!-- Opensearch list -->
    echo ${line} | grep -q "<!-- Opensearch list -->"
    [ $? -eq 0 ] && OK=true && echo "${SCRIPT_DEFINITION}" >> ${TMP_FILE}
     echo "${line}" >> ${TMP_FILE}
     done < ${DATA}/freemarker/projectIndex.ftl

if [ "$OK" = true ] ; then
    mv ${TMP_FILE} ${DATA}/freemarker/projectIndex.ftl
    echo "Mizar includes successfully added"
else
    rm ${TMP_FILE}
    echo "Mizar includes already added, do nothing"
fi

echo ""
echo "done"