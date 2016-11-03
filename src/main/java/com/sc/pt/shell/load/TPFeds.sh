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
echo "TPFeds start..." > $BASEDIR/logs/TPFeds.log
date >> $BASEDIR/logs/TPFeds.log

## backup the previous job log

if [ !-f $BASEDIR/logs/TPFeds.out ]; then
	mv $BASEDIR/logs/TPFeds.out $BASEDIR/logs/TPFeds$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPFeds.out" >> $BASEDIR/logs/TPFeds.log
fi

if [ !-f $BASEDIR/logs/TPFeds.err ]; then
	mv $BASEDIR/logs/TPFeds.err $BASEDIR/logs/TPFeds$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPFeds.err" >> $BASEDIR/logs/TPFeds.log
fi

## Job name: TPOpics, TPFeds, TPMurex2, TPMurex3, TPFeds
$BASEDIR/bin/start_sail_eod.sh -sail -jobs=TPFeds -date=$FileDate 1>$BASEDIR/logs/TPFeds.out 2>$BASEDIR/logs/TPFeds.err

if [ $?==0 ]; then
	echo "Successfully loading the TPFeds data to CMS" >> $BASEDIR/logs/TPFeds.log
fi

## Generate the TPFeds report
cp -p $BASEDIR/logs/TPFeds.out > $BASEDIR/logs/tmpTPFeds.out

## FTP the file to the log file
echo "Start transfer the log file by FTP"
echo "Start transfer the log file by FTP" >>  $BASEDIR/logs/TPFeds.log
$BASEDIR/load/ftp.sh $BASEDIR/logs/tmpTPFeds.out

## program end here
echo "TPFeds completion..." >> $BASEDIR/logs/TPFeds.log
date >> $BASEDIR/logs/TPFeds.log
