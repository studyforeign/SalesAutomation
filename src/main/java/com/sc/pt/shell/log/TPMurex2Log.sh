#!/bin/bash

##################################################
#
# Description: TPMurex2Log.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live
LOGDIR=$BASEDIR/logs

FILE="$(ls -ltr $LOGDIR/sail-eod-TPMurex2*.log | tail -1)"
Log="$(echo ${FILE:0-29})"

if [ ! -f $LOGDIR/$Log ]; then
    echo "The log file $LOGDIR/$Log doesn't exist"
    exit
fi

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPMurex2.out ]; then
	mv $BASEDIR/logs/TPMurex2.out $BASEDIR/logs/TPMurex2$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPMurex2.out" >> $BASEDIR/logs/TPMurex2.log
fi

if [ ! -f $BASEDIR/logs/TPMurex2.err ]; then
	mv $BASEDIR/logs/TPMurex2.err $BASEDIR/logs/TPMurex2$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPMurex2.err" >> $BASEDIR/logs/TPMurex2.log
fi

## Generate the TPMurex2 report
cp -p $LOGDIR/$Log > $BASEDIR/logs/tmpTPMurex2.out
