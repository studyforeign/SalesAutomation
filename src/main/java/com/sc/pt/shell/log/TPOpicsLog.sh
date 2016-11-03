#!/bin/bash

##################################################
#
# Description: TPOpicsLog.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live
LOGDIR=$BASEDIR/logs

FILE="$(ls -ltr $LOGDIR/sail-eod-TPOpics*.log | tail -1)"
Log="$(echo ${FILE:0-28})"

if [ ! -f $LOGDIR/$Log ]; then
    echo "The log file $LOGDIR/$Log doesn't exist"
    exit
fi

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPOpics.out ]; then
	mv $BASEDIR/logs/TPOpics.out $BASEDIR/logs/TPOpics$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPOpics.out"
fi

if [ ! -f $BASEDIR/logs/TPOpics.err ]; then
	mv $BASEDIR/logs/TPFeds.err $BASEDIR/logs/TPOpics$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPOpics.err"
fi

## Generate the TPOpics report
cp -p $LOGDIR/$Log > $BASEDIR/logs/TPOpics.out
