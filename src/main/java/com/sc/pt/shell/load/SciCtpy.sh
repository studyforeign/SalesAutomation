#!/bin/bash

##################################################
# 
# Description: TPFeds.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

FileDate=$1

## assume script under /apps/sail/sail_pt/sail-eod/live
BASEDIR=/apps/sail/sail_pt/sail-eod/live

CurrentDate=$(date +%Y%m%d)
CurrentTime=$(date +%H%M%S)

JAVA_HOME=/apps/sail/jdk1.7.0_51 

## main - program starts here
echo "TPFeds start..." > $BASEDIR/logs/sci.log
date >> $BASEDIR/logs/sci.log

## backup the previous job log

#if [ !-f $BASEDIR/log.file.path_IS_UNDEFINED/sail-eod-SciCtpyEod*.log ]; then
	mv $BASEDIR/log.file.path_IS_UNDEFINED/sail-eod-SciCtpyEod*.log $BASEDIR/logs/
	echo "Successfully backup the sail-eod-SciCtpyEod" >> $BASEDIR/logs/sci.log
#fi

cd $BASEDIR/bin
## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPFeds
./start_sail_eod.sh -jobs=SciCtpyEod -date=$FileDate -sail

if [ $?==0 ]; then
	echo "Successfully loading the TPFeds data to CMS" >> $BASEDIR/logs/sci.log
fi

## Generate the TPFeds report
cp -p $BASEDIR/log.file.path_IS_UNDEFINED/sail-eod-SciCtpyEod*.log $BASEDIR/logs/SciCtpy.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/sci.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/SciCtpy.out

## program end here
echo "TPFeds completion..." >> $BASEDIR/logs/sci.log
date >> $BASEDIR/logs/sci.log
