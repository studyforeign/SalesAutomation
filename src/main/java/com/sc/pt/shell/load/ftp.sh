#!/bin/bash

##################################################
#
# Description: ftp.sh
# Author: 1518008
# Date: 2016/1/18
# History:
#
##################################################

SOURCE=$1

sftp rtest@uklpaurzr01a.uk.standardchartered.com << EOF
cd /razorusr/home/rtest/apache-tomcat-mrtest1/webapps/tpLogs/
put $SOURCE ./
quit
EOF
