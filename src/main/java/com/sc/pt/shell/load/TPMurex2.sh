#!/bin/bash

##################################################
# 
# Description: TPMurex2.sh
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
echo "TPMurex2 start..."
date
echo "TPMurex2 start..." > $BASEDIR/logs/TPMurex2.log
date >> $BASEDIR/logs/TPMurex2.log

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPMurex2.out ]; then
	mv $BASEDIR/logs/TPMurex2.out $BASEDIR/logs/TPMurex2$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPMurex2.out"
	echo "Successfully backup the TPMurex2.out" >> $BASEDIR/logs/TPMurex2.log
fi

if [ ! -f $BASEDIR/logs/TPMurex2.err ]; then
	mv $BASEDIR/logs/TPMurex2.err $BASEDIR/logs/TPMurex2$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPMurex2.err"
	echo "Successfully backup the TPMurex2.err" >> $BASEDIR/logs/TPMurex2.log
fi

cd $BASEDIR/bin

## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPMurex2
./start_sail_eod.sh -sail -jobs=TPMurex2 -date=$FileDate 1>$BASEDIR/logs/TPMurex2.out 2>$BASEDIR/logs/TPMurex2.err

if [ $? == 0 ]; then
	echo "Successfully loding the TPMurex2 data to CMS" >> $BASEDIR/logs/TPMurex2.log
	echo "Successfully loding the TPMurex2 data to CMS"
fi

## Generate the TPMurex2 report
cp -p $BASEDIR/logs/TPMurex2.out > $BASEDIR/logs/tmpTPMurex2.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/TPMurex2.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/tmpTPMurex2.out

## program end here
echo "TPMurex2 completion..." >> $BASEDIR/logs/TPMurex2.log
date >> $BASEDIR/logs/TPMurex2.log
echo "TPMurex2 completion..."
date
