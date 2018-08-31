#!/bin/sh
#stop agent by input httpPort
pid=$(netstat -anp|grep $1|awk '{printf $7}'|cut -d/ -f1)
echo "$pid"
if [ -n "$pid" ] ; then
        kill -9 ${pid}
        echo "agent stopped successfully"
fi