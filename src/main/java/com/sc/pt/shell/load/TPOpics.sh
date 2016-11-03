#!/bin/bash

##################################################
# 
# Description: TPOpics.sh
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
echo "TPOpics start..."
date
echo "TPOpics start..." > $BASEDIR/logs/TPOpics.log
date >> $BASEDIR/logs/TPOpics.log

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPOpics.out ]; then
	mv $BASEDIR/logs/TPOpics.out $BASEDIR/logs/TPOpics$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPOpics.out"
	echo "Successfully backup the TPOpics.out" >> $BASEDIR/logs/TPOpics.log
fi

if [ ! -f $BASEDIR/logs/TPOpics.err ]; then
	mv $BASEDIR/logs/TPOpics.err $BASEDIR/logs/TPOpics$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPOpics.err"
	echo "Successfully backup the TPOpics.err" >> $BASEDIR/logs/TPOpics.log
fi

cd $BASEDIR/bin

## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPOpics
./start_sail_eod.sh -sail -jobs=TPOpics -date=$FileDate 1>$BASEDIR/logs/TPOpics.out 2>$BASEDIR/logs/TPOpics.err

if [ $? == 0 ]; then
	echo "Successfully loding the TPOpics data to CMS"
	echo "Successfully loding the TPOpics data to CMS" >> $BASEDIR/logs/TPOpics.log
fi

## Generate the TPOpics report
cp -p $BASEDIR/logs/TPOpics.out > $BASEDIR/logs/tmpTPOpics.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/TPOpics.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/tmpTPOpics.out

## program end here
echo "TPOpics completion..." >> $BASEDIR/logs/TPOpics.log
date >> $BASEDIR/logs/TPOpics.log
echo "TPOpics completion..."
date
