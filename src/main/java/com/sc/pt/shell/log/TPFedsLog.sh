#!/bin/bash

##################################################
#
# Description: TPFedsLog.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live
LOGDIR=$BASEDIR/logs

FILE="$(ls -ltr $LOGDIR/sail-eod-TPFedsCN*.log | tail -1)"
Log="$(echo ${FILE:0-29})"

if [ ! -f $LOGDIR/$Log ]; then
    echo "The log file $LOGDIR/$Log doesn't exist"
    exit
fi

## backup the previous job log

if [ ! -f $BASEDIR/logs/TPFeds.out ]; then
	mv $BASEDIR/logs/TPFeds.out $BASEDIR/logs/TPFeds$CurrentDate$CurrentTime.out
	echo "Successfully backup the TPFeds.out" >> $BASEDIR/logs/TPFeds.log
fi

if [ ! -f $BASEDIR/logs/TPFeds.err ]; then
	mv $BASEDIR/logs/TPFeds.err $BASEDIR/logs/TPFeds$CurrentDate$CurrentTime.err
	echo "Successfully backup the TPFeds.err" >> $BASEDIR/logs/TPFeds.log
fi

## Generate the TPFeds report
cp -p $LOGDIR/$Log > $BASEDIR/logs/tmpTPFeds.out
