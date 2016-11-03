#!/bin/bash

##################################################
#
# Description: TPMurex3Log.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live
LOGDIR=$BASEDIR/logs

FILE="$(ls -ltr $LOGDIR/sail-eod-TPMurex3*.log | tail -1)"
Log="$(echo ${FILE:0-29})"

if [ ! -f $LOGDIR/$Log ]; then
    echo "The log file $LOGDIR/$Log doesn't exist"
    exit
fi

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPMurex3.out ]; then
	mv $BASEDIR/logs/TPMurex3.out $BASEDIR/logs/TPMurex3$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPMurex3.out"
fi

if [ ! -f $BASEDIR/logs/TPMurex3.err ]; then
	mv $BASEDIR/logs/TPMurex3.err $BASEDIR/logs/TPMurex3$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPMurex3.err"
fi

## Generate the TPMurex3 report
cp -p $LOGDIR/$Log > $BASEDIR/logs/TPMurex3.out
