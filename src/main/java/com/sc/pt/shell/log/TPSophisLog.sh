#!/bin/bash

##################################################
#
# Description: TPSophisLog.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live
LOGDIR=$BASEDIR/logs

FILE="$(ls -ltr $LOGDIR/sail-eod-TPSophis*.log | tail -1)"
Log="$(echo ${FILE:0-29})"

if [ ! -f $LOGDIR/$Log ]; then
    echo "The log file $LOGDIR/$Log doesn't exist"
    exit
fi

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPSophis.out ]; then
	mv $BASEDIR/logs/TPSophis.out $BASEDIR/logs/TPSophis$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPSophis.out" >> $BASEDIR/logs/TPSophis.log
fi

if [ ! -f $BASEDIR/logs/TPSophis.err ]; then
	mv $BASEDIR/logs/TPSophis.err $BASEDIR/logs/TPSophis$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPSophis.err" >> $BASEDIR/logs/TPSophis.log
fi

## Generate the TPSophis report
cp -p $LOGDIR/$Log > $BASEDIR/logs/tmpTPSophis.out
