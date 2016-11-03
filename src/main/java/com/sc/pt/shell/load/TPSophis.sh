#!/bin/bash

##################################################
# 
# Description: TPSophis
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
echo "TPSophis start..."
date
echo "TPSophis start..." > $BASEDIR/logs/TPSophis.log
date >> $BASEDIR/logs/TPSophis.log

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPSophis.out ]; then
	mv $BASEDIR/logs/TPSophis.out $BASEDIR/logs/TPSophis$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPSophis.out" >> $BASEDIR/logs/TPSophis.log
fi

if [ ! -f $BASEDIR/logs/TPSophis.err ]; then
	mv $BASEDIR/logs/TPSophis.err $BASEDIR/logs/TPSophis$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPSophis.err" >> $BASEDIR/logs/TPSophis.log
fi

cd $BASEDIR/bin

## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPSophis
./start_sail_eod.sh -sail -jobs=TPSophis -date=$FileDate 1>$BASEDIR/logs/TPSophis.out 2>$BASEDIR/logs/TPSophis.err

if [ $? == 0 ]; then
	echo "Successfully loding the TPSophis data to CMS"
	echo "Successfully loding the TPSophis data to CMS" >> $BASEDIR/logs/TPSophis.log
fi

## Generate the TPSophis report
cp -p $BASEDIR/logs/TPSophis.out > $BASEDIR/logs/tmpTPSophis.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/TPSophis.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/tmpTPSophis.out

## program end here
echo "TPSophis completion..." >> $BASEDIR/logs/TPSophis.log
date >> $BASEDIR/logs/TPSophis.log
echo "TPSophis completion..."
date

