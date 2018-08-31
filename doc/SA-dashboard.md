# dashboard

	正式服地址: {{host}} -> http://www.unistack.com.cn
	测试服地址: {{host}} -> http://192.168.1.210
	Heaters:
		key->Content-Type
		value->application/com.unistack.tamboo.commons.json
       

- <a href="#metrics"><b>dashboard metrics</b></a>
- <a href="#compare"><b>数据量对比</b></a>
- <a href="#platform"><b>平台服务</b></a>
- <a href="#summary"><b>平台概况</b></a> 
- <a href="#platMetrics"><b>具体集群详情</b></a>

<a id="metrics" name="metrics"> ____ </a>
### dashboard metrics
	请求接口: {{host}}:7080/tamboo-sa/dashboard/metrics
	    请求方式: get
##### 输入参数
```
```
##### 输入格式
```
```
##### 返回格式
```
    {
        "data":{
            "collector": {
                "real":1234,
                "peak":31234
            },
            "dataBus": {
                "real":1234,
                "peak":31234 
            },
            "dataCalc": {
                "real":1234,
                "peak":31234 
            },
            "dataDist": {
                "real":1234,
                "peak":31234 
            }
        },
        "status": 200
    }
```

<a id="compare" name="compare"> ____ </a>
### 数据量对比
	请求接口: {{host}}:7080/tamboo-sa/dashboard/compare
	    请求方式: get
	    返回说明：昨天的返回全量数据，今天的返回00：00 到当前时间数据
##### 输入参数
```
```
##### 输入格式
```
```
##### 返回格式
```
    {
        "data":{
            "yesterday": [{
                "timestamp":1527494000,
                "offset":31234
            },{
                "timestamp":1527495000,
                "offset":32342
            },...
            ],
            "today":  [{
                "timestamp":1527494000,
                "offset":31234
            },{
                "timestamp":1527495000,
                "offset":32342
            },...
            ]
        },
        "status": 200
    }
```


<a id="platform" name="platform"> ____ </a>
### 平台服务
	请求接口: {{host}}:7080/tamboo-sa/df/platform
	    请求方式: get
##### 输入参数
```
```
##### 输入格式
```
```
##### 返回格式
{
    "data": {
        "countWf": 10,
        "running": 10,
        "warning": 0,
        "unrunning": 0
    },
    "status": 200
}
```

<a id="summary" name="summary"> ____ </a>
### 平台概况
	请求接口: {{host}}:7080/tamboo-sa/dashboard/summary
	    请求方式: get
##### 输入参数
```
```
##### 输入格式
```
```
##### 返回格式
```
    {
        "data":{
            "datasource":{
                "log":123,
                "db":23,
                "other":21
            },
            "sourceUsed":{
                "cpu":0.7,
                "mem":0.8,
                "disk":0.87
            },
            "cluster"{
                "collectors":64,
                "brokers":64,
                "zookeeper":3,
                "calc":12
            }
            
        },
        "status": 200
    }
```


<a id="platMetrics" name="platMetrics"> ____ </a>
### 具体集群详情
	请求接口: {{host}}:7080/tamboo-sa/dashboard/platMetrics/{type}
	    请求方式: get
	    参数说明：[collector,brokers,zookeeper,calc]
##### 输入参数
```
```
##### 输入格式
```
```
##### 返回格式
```
返回数据类型参考，dashboard->集群服务（点击）->详情展示页组装
    {
        "data":{
            "type":"collectors",
            "values":{
            
            }
        },
        "status": 200
    }
```


