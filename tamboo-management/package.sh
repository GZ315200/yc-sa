#!/bin/bash


###################
# bash only
# usage: ./package.sh
#####################


install () {
    installedPackage=$1
    #install $installedPackage with latest code
    if [[ $installedPackage == 'tamboo-sa' ]]; then
        cd ..
        mvn clean install --non-recursive -Dmaven.test.skip=true
    else
        cd ../$installedPackage
        mvn clean install -Dmaven.test.skip=true
    fi

    if [ $? -ne 0 ]; then
      echo "[ERROR] failed to package and install $installedPackage.jar"
      exit 1
    fi
    cd -
}

############## define vars
#declare -a : define an array
#declare -i : define an integer
declare -i index=1
declare -a envMap

CURRENT_DIR=`dirname $0`
cd $CURRENT_DIR

#scan the envs from conf/*
index=1
for i in conf/*; do
  if [ -d ${i} ]; then
    envMap["${index}"]="`basename ${i}`"
    index=index+1
  fi
done




#getVersion

install tamboo-sa
install tamboo-commons


echo "mvn clean package -Dmaven.test.skip=true"
mvn clean package -Dmaven.test.skip=true
cd target
mkdir tamboo-management
cp tamboo-management.jar tamboo-management
cp -r ../../tamboo-collector/flume.tar.gz tamboo-management
cp -r ../scripts tamboo-management
cp -r ../config/ tamboo-management/config
cp -r ././../bin/ tamboo-management/bin
cp -r ../../tmp/ tamboo-management/tmp
tar -zcvf tamboo-management.tar.gz tamboo-management
