#!/bin/bash

CURRENT_FOLDER=`dirname $0`
cd $CURRENT_FOLDER
CURRENT_FOLDER1=`pwd`

CURRENT_FOLDER=$CURRENT_FOLDER1"/target"

APPVERSION=`cat conf/version.txt`
if [ -z $APPVERSION ]; then
	echo "version is not provided. use current datetime as version."
	APPVERSION=`date +%Y%m%d-%H%M%S`
fi

LOG=/tmp/postinstall-dc.dataCollector-${APPVERSION}.log

#define function with detail log as param
recordLog () {
  dt=`date +%Y-%m-%d\ %H:%M:%S`
  echo ${dt}: ${1}
  if [ "${LOG}" != "" ]; then
    echo ${dt}: ${1} >> ${LOG}
  fi
}

recordLog "[INFO] Begin postinstall.sh"
recordLog "[INFO] current folder is ${CURRENT_FOLDER}"

#/opt/apps will be replaced when building package
APPROOT=/opt/apps
APPNAME=tamboo-management
APPHOME=${APPROOT}/${APPNAME}
HOSTNAME=`hostname`


CLUSTER_NODES="192.168.0.222"

for i in ${CLUSTER_NODES}
do
  recordLog ""
  recordLog "-----------------------------"
  recordLog "[INFO] Connecting to host ${i}"


  #create app root folder is not existing
  theCommand="ssh -oStrictHostKeyChecking=no root@${i} mkdir -p ${APPROOT} 2>&1"
  echo "[COMMAND] ${theCommand}"
  ${theCommand}
  recordLog "[INFO] Folder ${APPROOT} is created successfully on host <<${i}>>"

  #create app home folder is not existing
  theCommand="ssh -oStrictHostKeyChecking=no root@${i} mkdir -p ${APPHOME} 2>&1"
  echo "[COMMAND] ${theCommand}"
  ${theCommand}
  recordLog "[INFO] Folder ${APPHOME} is created successfully on host <<${i}>>"

  #copy files from current host to another node by rsync command
  #rsync can be faster than scp if specified with proper args, and it support ssh if it is avaliable
  #-a means preserve group/user/modified tme metadata and so on
  #-z means compress content
  #-P show tranport progress
  #be noticed that the '/' behind ${CURRENT_FOLDER}. it means all the content of the folder ${CURRENT_FOLDER}
  recordLog "[INFO] current folder is ${CURRENT_FOLDER}"
  recordLog "[INFO] copying files to host <<${i}>> by rsync command"
  theCommand="scp -r ${CURRENT_FOLDER}/${APPNAME}/ root@${i}:${APPHOME}/${APPVERSION}"
  recordLog "[COMMAND] ${theCommand}"
  ${theCommand}
  recordLog "[INFO] Copy is done!"
  #change owner and group for app package
  recordLog "[INFO] changing owner and group recursively"
  theCommand="ssh -oStrictHostKeyChecking=no root@${i} chown -R root:root ${APPHOME}/${APPVERSION} 2>&1"
  recordLog ${theCommand}
  recordLog "[COMMAND] ${theCommand}"
  recordLog "[INFO] Owner and group set up done!"

  #create soft link named 'current', -s:soft link -v:debug mode -n:treate link as normal file if it linking to a folder
  #-f:force create it is already there
  recordLog "[INFO] Create soft link named 'current'"
  theCommand="ssh -oStrictHostKeyChecking=no root@${i} ln -svnf ${APPHOME}/${APPVERSION} ${APPHOME}/current 2>&1"
  recordLog "[COMMAND] ${theCommand}"
  ${theCommand}
  recordLog "[INFO] Link creation is done!"
  recordLog "--------------------------------"

  if [  -d "${CURRENT_FOLDER}/${APPNAME}" ];then
    theCommand="rm -rf ${CURRENT_FOLDER}/${APPNAME}/"
    recordLog "[COMMAND] ${theCommand}"
    ${theCommand}
    recordLog "[INFO] delete ${CURRENT_FOLDER}/${APPNAME} is done!"
  fi

done
