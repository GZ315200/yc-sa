##### 1.启动connect任务：
前端定义的接口参数

##### jdbc页面传递到后台的参数
```
{
    "type": "mysql/oracle",
    "fields": [{
		"jdbc_url":"数据库连接url#string",
  		"jdbc_username":"数据库用户名#string",
		"jdbc_password":"数据库密码#string",
		"table_name":"表名#string",
		"tasks_max":"最大任务数#int",
		"topics":"topic名称#string",
		"whitelist":"字段白名单#string",
		"pk_fields":"主键字段#string"
	      }]
}
```

##### hdfs页面传递到后台的参数
```
{
    "type": "hdfs",
    "fields": [{
		"hdfs_url":"hdfs存储路径#string",
		"flush_size":"刷新hdfs文件的频率#int",
		"tasks_max":"最大任务数#int",
		"topics":"topic名称#string"
	      }]
}
```

##### file页面传递到后台的参数
```
{
    "type": "file",
    "fields": [{
		"file":"file名称#string",
		"topics":"topic名称#string"
	      }]
}
```

##### IBM_MQ页面传递到后台的参数
```
{
    "type": "ibm_mq",
    "fields": [{
		"mq_hostname":"mq服务器所在的主机名称#string",
		"mq_port":"监听器监听的端口#string",
		"channel_name":"下发MQ的通道名称#string",
		"queue_name":"下发的队列名称#string",
		"CCSID":"传输编码类型#string",
		"topics":"topic名称#string",
	      }]
}
```


调用DdAction.startKafkaSinkConnector(JSONObject args, String kafkaIp, String kafkaConnectPort)
##### 启动方法输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| args| JSONObject | 是 | 页面传递的参数|
| kafkaIp| String | 是 | 连接connector的ip|
| kafkaConnectPort| String | 是 | 连接connector的端口|


args为前端页面表单提交的参数，kafkaIp和kafkaConnectPort为kafka节点ip和connector端口(默认8083)

##### 返回格式
```
{
    "isSucceed":boolean,  //操作是否成功
    "msg":string,           //操作返回信息
    "connectorName":"string"    //启动的connector名称(删除时需要提供)
}
```


##### 2.停止connect任务
调用DdAction.stopKafkaSinkConnectorByName(String connectorName,String kafkaIp, String kafkaConnectPort)

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| connectorName| String | 是 | 停止的连接器名称|
| kafkaIp| String | 是 | 连接connector的ip|
| kafkaConnectPort| String | 是 | 连接connector的端口|

##### 返回格式
```
{
    "isSucceed":boolean,  //操作是否成功
    "msg":string          //操作返回信息
}
```

##### 3.获得所有运行中的connector名称
调用DdAction.showRunningConnector(String kafkaIp, String kafkaConnectPort)

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| kafkaIp| String | 是 | 连接connector的ip|
| kafkaConnectPort| String | 是 | 连接connector的端口|

##### 返回格式
```
{
    "isSucceed":boolean,  //操作是否成功
    "msg":Object          //成功返回jsonarray(有可能为空[]),失败返回string错误信息
}
```
##### 4.获得指定的connector运行状态
调用DdAction.getTaskStatusByConnectorName(String kafkaIp, String kafkaConnectPort, String name)

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| kafkaIp| String | 是 | 连接connector的ip|
| kafkaConnectPort| String | 是 | 连接connector的端口|
| name| string | 是 | 需要获取状态的connector名称|

##### 返回格式
```
{
    "isSucceed":true,
    "connector_name": string,         //获取到的connector名称
    "status":{
                "state":string          //有RUNNING、FAILED
    }
    
}
```

##### 5.获得所有connector运行状态
调用DdAction.getTaskStatus(String kafkaIp, String kafkaConnectPort)

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| kafkaIp| String | 是 | 连接connector的ip|
| kafkaConnectPort| String | 是 | 连接connector的端口|

##### 返回格式
```
{
    "isSucceed":true,
    "msg": [{
        "connector_name": string,         //获取到的connector名称
        "status":{
                "state":string          //有RUNNING、FAILED
        }  
    },{...}]
}
```
