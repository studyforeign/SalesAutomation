#!/bin/bash

##################################################
#
# Description: tp_loading.sh
# Author: 1518008
# Date: 2015/11/24
# History:
#
##################################################

## assume script under /apps/sail/sail_pt/sail-eod/live
BASEDIR=/apps/sail/sail_pt/sail-eod/live

CurrentDate=$(date +%Y%m%d)
CurrentTime=$(date +%H%M%S)

JAVA_HOME=/apps/sail/jdk1.7.0_51

tpopics=x
tpsophis=x
tpmurex2=x
tpmurex3=x
tpfeds=x

cd $BASEDIR/load

for arg in $*; do

	if [[ $arg == -TPOpics=* ]]; then
		tpopics=$(echo $arg | sed "s/-TPOpics=//" | tr "," "-" | tr "*" " ")
		./TPOpics.sh $tpopics
        ./TPOpicsLog.sh
	elif [[ $arg == -TPSophis=* ]]; then
		tpsophis=$(echo $arg | sed "s/-TPSophis=//" | tr "," "-" | tr "*" " ")
		./TPSophis.sh $tpsophis
		./TPSophisLog.sh
	elif [[ $arg == -TPMurex2=* ]]; then
		tpmurex2=$(echo $arg | sed "s/-TPMurex2=//" | tr "," "-" | tr "*" " ")
		./TPMurex2.sh $tpmurex2
		./TPMurex2Log.sh
	elif [[ $arg == -TPMurex3=* ]]; then
		tpmurex3=$(echo $arg | sed "s/-TPMurex3=//" | tr "," "-" | tr "*" " ")
		./TPMurex3.sh $tpmurex3
		./TPMurex3Log.sh
	elif [[ $arg == -TPFeds=* ]]; then
        tpfeds=$(echo $arg | sed "s/-TPFeds=//" | tr "," "-" | tr "*" " ")
		./TPFeds.sh $tpfeds
		./TPFedsLog.sh
	fi
done

if [ $tpopics=="x" -a $tpsophis=="x" -a $tpmurex2=="x" -a $tpmurex3=="x" -a $tpfeds=="x" ]; then
	echo "Please give the date parameter as below: "
	echo "-TPOpics=YYYYMMDD"
	echo "-TPSophis=YYYYMMDD"
	echo "-TPMurex2=YYYYMMDD"
	echo "-TPMurex3=YYYYMMDD"
	echo "-TPFeds=YYYYMMDD"
fi

