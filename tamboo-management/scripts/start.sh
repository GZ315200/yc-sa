#create log folder if not existing
mkdir -p /var/logs/tamboo-manager

cd `dirname $0`/..

pid=`ps ax | grep -i 'tamboo-management\.jar' | grep java | grep -v grep | awk '{print $1}'`

if [ "$pid" !=  "" ]; then
    echo "tamboo-management is already there with pid $pid"
else
    nohup /opt/apps/jdk/current/bin/java -Dzookeeper.sasl.client=disable -jar tamboo-management.jar 2>&1 >/dev/null &
    
    while [ "$pid" ==  "" ]
    do
        echo "checking the pid ..."
        sleep 3
        pid=`ps ax | grep -i 'tamboo-management\.jar' | grep java | grep -v grep | awk '{print $1}'`
    done 
    
    echo "tamboo-management is started successfully with pid $pid"
fi