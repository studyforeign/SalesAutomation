#!/bin/bash

##################################################
#
# Description: log_gen.sh
# Author: 1518008
# Date: 2015/11/18
# History:
#
##################################################

BASEDIR=/apps/sail/sail_pt/sail-eod/live

#change the current dir
cd $BASEDIR/load

echo "Start the log generation..."
date

#Generate Feds Log
echo "Start the TPFedsLog generation..."
./TPFedsLog.sh

#Generate TPMurex2 Log
echo "Start the TPMurex2Log generation..."
./TPMurex2Log.sh

#Generate TPMurex3 Log
echo "Start the TPMurex3Log generation..."
./TPMurex3Log.sh

#Generate TPOpics Log
echo "Start the TPOpicsLog generation..."
./TPOpicsLog.sh

#Generate TPSophis Log
echo "Start the TPSophisLog generation..."
./TPSophisLog.sh

echo "Complete the log generation..."
date
