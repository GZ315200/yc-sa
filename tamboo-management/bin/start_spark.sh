#$1->deploy_model   发布模式 standalone / yarn
#$2->appName        程序的名字
#$3->acl_username
#$4->acl_password

#standalone
    #$5->主机ip eg:192.168.1.201
    #$6->spark jar包的部署目录 /opt/calc
#yarn
    #$5->主机ip eg:192.168.1.201
    #$6->spark jar包的部署目录 /opt/calc
    #$7->queueName 对列名

if [ $1 == "standalone" ]; then
    ssh root@$5 "cd $6  && sh start_spark.sh $1 $2 $3 $4 $5"
elif [ $1 == "yarn" ]; then
     echo "$1 $2 $3 $4 $5 $6 $7"  >> record.txt
	ssh root@$5 "cd $6 && source /etc/profile && sh start_spark.sh $1 $2 $3 $4 $7"
else
	echo "other_deploy_model" >> 1.txt
fi