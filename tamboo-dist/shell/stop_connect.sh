#!/bin/bash
PIDS=$(ps ax | grep -i 'etc/kafka/com.unistack.tamboo.sa.dc.connect-distributed' | grep java | grep -v grep | awk '{print $1}')

if [ -z "$PIDS" ]; then
  echo "No kafka com.unistack.tamboo.sa.dc.connect server to stop"
  exit 1
else
  kill -s 9 $PIDS
fi