#!/bin/bash

##################################################
# 
# Description: TPMurex3.sh
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
echo "TPMurex3 start..."
date
echo "TPMurex3 start..." > $BASEDIR/logs/TPMurex3.log
date >> $BASEDIR/logs/TPMurex3.log

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPMurex3.out ]; then
	mv $BASEDIR/logs/TPMurex3.out $BASEDIR/logs/TPMurex3$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPMurex3.out"
	echo "Successfully backup the TPMurex3.out" >> $BASEDIR/logs/TPMurex3.log
fi

if [ ! -f $BASEDIR/logs/TPMurex3.err ]; then
	mv $BASEDIR/logs/TPMurex3.err $BASEDIR/logs/TPMurex3$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPMurex3.err"
	echo "Successfully backup the TPMurex3.err" >> $BASEDIR/logs/TPMurex3.log
fi

cd $BASEDIR/bin

## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPMurex3
./start_sail_eod.sh -sail -jobs=TPMurex3 -date=$FileDate 1>$BASEDIR/logs/TPMurex3.out 2>$BASEDIR/logs/TPMurex3.err

if [ $? == 0 ]; then
	echo "Successfully loding the TPMurex3 data to CMS"
	echo "Successfully loding the TPMurex3 data to CMS" >> $BASEDIR/logs/TPMurex3.log
fi

## Generate the TPMurex3 report
cp -p $BASEDIR/logs/TPMurex3.out > $BASEDIR/logs/tmpTPMurex3.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/TPMurex3.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/tmpTPMurex3.out

## program end here
echo "TPMurex3 completion..." >> $BASEDIR/logs/TPMurex3.log
date >> $BASEDIR/logs/TPMurex3.log
echo "TPMurex3 completion..."
date

