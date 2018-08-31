#!/bin/sh
#need {home_path , config_name , agent name , port}
nohup sh $FLUME_HOME/bin/flume-ng agent --conf conf --conf-file $1 --name agent -Dflume.monitoring.type=http -Dflume.monitoring.port=$2 -Dflume.root.logger=INFO,console 2>&1 &
echo $!

