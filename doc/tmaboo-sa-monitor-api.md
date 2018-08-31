


# 监控相关接口


### 获取正在运行的brokers

	请求接口: {{host}:5080/tamboo-mgt/monitor/getAllActive/brokers
	    请求方式: GET

##### 返回格式
```
{
    "data": {
        "active": [
            "192.168.1.101:9092"
        ],
        "down": [
            "192.168.1.102:9092"
        ]
    },
    "status": 200
}
```

active:启动的broker节点
down：宕机的broker节点

### 获取所有的topics

	请求接口: {{host}:5080/tamboo-mgt/monitor/topics/getAll
	    请求方式: GET

##### 返回格式
```
{
    "data": {
        "topics": [
            "zean",
            "tamboo_sanitycheck",
            "tamboo_consumer_metrics",
            "tamboo_producer_metrics",
            "test",
            "tamboo_client_metrics",
            "zc",
            "test1"
        ]
    },
    "status": 200
}
```
topics:当前运行的所有topics


### 获取所有的brokers的logDir描述信息

	请求接口: {{host}:5080/tamboo-mgt/monitor/getAll/brokers/logDirDesc
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        {
            "brokerId": 0,
            "host": "192.168.1.101:9092",
            "topicOffsetVo": [
                {
                    "topic":"tamboo_consumer_metr ics",
                    "size": 0,
                    "offsetLag": 0,
                    "partition": 0
                },
                {
                    "topic": "test",
                    "size": 54147,
                    "offsetLag": 0,
                    "partition": 0
                },
                {
                    "topic": "zean-test",
                    "size": 0,
                    "offsetLag": 0,
                    "partition": 0
                }
            ]
        }
    ],
    "status": 200
}
```





### 获取单个topic的描述信息

	请求接口: {{host}:5080/tamboo-mgt/monitor/getTopic/{topic}/desc
	    请求方式: GET
描述topic的parition数，以及它的leader、replicas、isr坐落在哪个节点上

##### 返回格式
```
{
    "data": {
        "internal": false,
        "partitions": [
            {
                "partition": 0,
                <!-- 主节点 -->
                "leader": {
                    "hosts":  "192.168.1.101:9092", ／／主机
                    "idString": "0", ／／broker_id
                    "rack": null
                },
                <!-- 副本节点 -->
                "replicas": [
                    {
                        "hosts": "192.168.1.101:9092",
                        "idString": "0",
                        "rack": null
                    }
                ],
                <!-- isr节点 -->
                "isr": [
                    {
                        "hosts": "192.168.1.101:9092",
                        "idString": "0",
                        "rack": null
                    }
                ]
            }
        ]
    },
    "status": 200
}
```


### 获取单个brokerId的logDir描述信息
	请求接口: {{host}:5080/tamboo-mgt/getBrokerId/{brokerId}/desc
	    请求方式: GET

##### 返回格式
```
{
    "data": {
        "brokerId": 0,
        "host": "192.168.1.101:9092",
        "topicOffsetVo": [
            {
                "topic": "zean-test",
                "size": 0,
                "offsetLag": 0,
                "partition": 0
            },
            {
                "topic": "tamboo_consumer_metrics",
                "size": 0,
                "offsetLag": 0,
                "partition": 0
            },
            {
                "topic": "test", ／／topic
                "size": 54147, ／／topic消息size大小
                "offsetLag": 0, ／／未被消费的offset
                "partition": 0 ／／分区
            }
        ]
    },
    "status": 200
}
```



### 获取正在运行的job状态名称
	请求接口: {{host}:5080/tamboo-mgt/runnerName/get
	    请求方式: GET

##### 返回格式
```
{
    "data": "1527515682113@-2121761647",
    "status": 200
}
```


### 根据运行的job名称查询当前job状态
	请求接口: {{host}:5080/tamboo-mgt/{runnerName}/collectorState/
	    请求方式: GET


##### 返回格式
```
{
    "data":{
        "runnerId":1527515682113@-2121761647,
        "state":RUNNER,
        "msg":"offset collector is running"
    }
    "status": 200
}
```





### 根据topic的id查询峰值
	请求接口: {{host}:5080/tamboo-mgt/{topicId}/getPeak
	    请求方式: GET

##### 返回格式
```
{
  "data": {
      "topic_id":1,
      "offset_total":234343,
      "message_rate":45.67
      "peak_num":"245454",
      "peak_time":"1527515682113",
      "create_time":"1527515682113"
  },
  "status": 200
}
```


### 获取历史offset信息.
	请求接口: {{host}:5080/tamboo-mgt/getOffsetHistoryRecord
	    请求方式: GET



##### 返回格式
```
 {
    "data": [
        "monitorRecordVo":{
            "message_rate":"45.67",
            "offset_add":"123213",
            "timestamp":"1527515682113"
        },
         "monitorRecordVo":{
            "message_rate":"44.67",
            "offset_add":"324234",
            "timestamp":"1527515682112"
        },
        ...
    }],
    "status": 200
}
```
返回类型说明

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| message_rate | double | 是 | 消息的速率 条／s |
| offset_add | long | 是 | 当前时间的offset总数 |
| timestamp | long | 是 | 时间戳|
