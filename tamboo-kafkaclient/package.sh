#!/bin/bash


###################
# bash only
# usage: ./package.sh
#####################

CURRENT_DIR=`dirname $0`


################ define functions
getVersion () {
    versionStr=`grep "^VERSION: " $CURRENT_DIR/CHANGELOG | tail -1`
    version=`echo ${versionStr:9:5}`
    if [ "${version}X" == "X" ]; then
      echo [ERROR] cannot get version
      exit 1
    fi
}

getVersion
CMD="mvn clean package -Dmaven.test.skip=true -DtheVersion=$version"
echo $CMD
eval $CMD

