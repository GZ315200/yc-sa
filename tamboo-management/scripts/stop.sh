pid=`ps ax | grep -i 'tamboo-management\.jar' | grep java | grep -v grep | awk '{print $1}'`
kill -9 $pid
while [ "$pid" !=  "" ]
do
    echo "stopping pid $pid ..."
    sleep 3
    pid=`ps ax | grep -i 'tamboo-management\.jar' | grep java | grep -v grep | awk '{print $1}'`
done 
echo "tamboo-management is stopped successfully"

