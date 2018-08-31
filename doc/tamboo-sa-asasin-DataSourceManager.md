# DataSourceManager Api

	正式服地址: {{host}} -> http://www.unistack.com.cn
	测试服地址: {{host}} -> http://192.168.1.202
	Heaters:
	    key->Content-Type
        value->application/com.unistack.tamboo.commons.json

- <a href="#list"><b>分页查询数据源列表</b></a>
- <a href="#add"><b>添加数据源</b></a>
- <a href="#delete"><b>删除数据源</b></a>
- <a href="#start"><b>启动数据源</b></a>
- <a href="#stop"><b>停止数据源</b></a>
- <a href="#queryDataSource"><b>数据源查看</b></a>
- <a href="#getDataSourceTopic"><b>数据源Topic和开放Topic统计</b></a>
- <a href="#getTopicByType"><b>数据源Topic,开放Topic,临时Topic</b></a>
- <a href="#getTopicByType"><b>DashBoard dataSource饼图</b></a>


<a id="list" name="分页查询数据源列表"> ____ </a>

### 分页查询数据源列表
	请求接口: {{host}}:5080/tamboo-mgt/dataSource/list
	    请求方式: get
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- | ------| ----- | ---- |
| queryType | String | 是 | 查询采集类型关键字 |
| queryName | String | 是 | 查询采集名称关键字 |
| orderBy | String | 是 | 排序方式 |
| pageIndex | int | 是 | 页数 |
| pageSize | int | 是 | 每页数量 |

##### 返回格式
```
{
    "data": {
        "pagination": {
            "pageIndex": 1, //当前页数
            "totalPage": 1, //总页数
            "resultSize": 2, //总条数
            "pageSize": 10  //每页条数
        },
        "pageData": [
            {
                "dataSourceId": 2,
                "dataSourceType": "SYSLOG",
                "dataSourceName": "torato",
                "topicId": 2,
                "dataSourceDsc": "fsfd",
                "dataIp": "192.168.1.201",
                "dataModel": "fsfs",
                "createTime": null,
                "flag": 0,
                "useNum": 0,
                "dataPath": "/opt/apps/",
                "dataUser": "root",
                "dataPwd": "hzq@2017",
                "httpPort": 6046
            },
            {
                "dataSourceId": 1,
                "dataSourceType": "EXEC",
                "dataSourceName": "zc",
                "topicId": 1,
                "dataSourceDsc": "fsfs",
                "dataIp": "192.168.1.202",
                "dataModel": "fsf",
                "createTime": null,
                "flag": 1,
                "useNum": 0,
                "dataPath": "/opt/apps/",
                "dataUser": "root",
                "dataPwd": "hzq@2017",
                "httpPort": 6045
            }
        ]
    },
    "status": 200
}
```

<a id="add" name="添加数据源"> ____ </a>

### 添加数据源
	请求接口: {{host}}:5080/tamboo-mgt/dataSource/add
	    请求方式: post

##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| DataSourceListAS| Object | 是 | 用户输入数据源包装对象|

DataSourceListAS
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| dataSourceList| Object | 是 | 数据源对象|
| jsonObject| Object | 是 | 不同采集类型输入信息参数|

dataSourceList
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| dataSourceType| String | 是 | 数据源类型|
| dataSourceName| String | 是 | 数据源名称|
| dataSourceDsc| String | 是 | 数据源描述信息|
| dataIp| String | 是 | 远程服务器ip|
| dataUser| String | 是 | 远程服务器用户名|
| dataPwd| String | 是 | 远程服务器密码|
| dataModel| String | 是 | 数据样例|

jsonObject
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| command| String | 是 | 采集文件命令(EXEC)|
| postUrl| String | 是 | webService服务ip(WEBSERVICE)|
| soapXml| String | 是 | soapXml信息(WEBSERVICE)|
| intervalMillis| String | 是 | 时间间隔(WEBSERVICE)|
| soapAction| String | 是 | 调用方法(WEBSERVICE)|

##### 输入格式
```
    EXEC样例:
	{	
    		"dataSourceList":{
    		"dataSourceType":"EXEC",
    		"dataSourceName":"test",
    		"dataSourceDsc":"test的文件采集",
    		"dataIp":"192.168.1.202",
    		"dataUser":"root",
    		"dataPwd":"hzq@2018",
    		"dataModel":"fsfsffsf"
    	
    		},
    		"jsonObject":{
    		"command":"tail -f /home/test.txt"
    		}
    }
    WEBSERVICE样例:
    {
     "dataSourceList": {
      "dataSourceType": "WESERVICE",
      "dataSourceName": "test",
      "dataSourceDsc": "fdsf",
      "dataModel": "fdsf",
      "dataIp": "192.168.1.202",
      "dataUser": "root",
      "dataPwd": "hzq@2017"
     },
     "jsonObject": {
     "postUrl":"http://192.168.0.105:8888/myservice",
     "soapXml":"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server/\"><soapenv:Header/><soapenv:Body><ser:getContent/></soapenv:Body></soapenv:Envelope>",
     "intervalMillis":"1000",
     "soapAction":"getContent"
     }
    }
```

##### 返回格式
```
	{
        "status": 200
    }
```


<a id="delete" name="删除数据源"> ____ </a>

### 删除数据源

	请求接口: {{host}}:5080/tamboo-mgt/dataSource/delete
	    请求方式: get

##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ---- |
| dataSourceId| Long | 是 | 数据源id |

##### 返回格式

```
	{
        "status": 200
    }
```

<a id="start" name="启动数据源"> ____ </a>

### 启动数据源

	请求接口: {{host}}:5080/tamboo-mgt/dataSource/start
	    请求方式: get
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| dataSourceId| Long | 是 | 数据源id|

##### 返回格式
```
	{
        "status": 200
    }
```

<a id="stop" name="停止数据源"> ____ </a>

### 停止数据源

	请求接口: {{host}}:5080/tamboo-mgt/dataSource/stop
	    请求方式: get
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| dataSourceId| Long | 是 | 数据源id|

##### 返回格式
```
	{
        "status": 200
    }
```

<a id="queryDataSource" name="数据源查看"> ____ </a>

### 数据源查看

	请求接口: {{host}}:5080/dataSource/queryDataSource
	    请求方式: post
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| dataSourceId| Long | 是 | 数据源id|

##### 返回格式
```
{
    "status": 200,
    "data":{
            "dataSourceType": 1,//数据源类型
            "dataSourceName": 16,//数据源名称
            "dataSourceDsc": 100,//数据源描述信息
            "dataIp": "test",//ip
            "dataModel": 100,//数据model
            "useNum": "dfsf"//使用次数
    },
    "msg": null
}
```

<a id="getDataSourceTopic" name="数据源Topic和开放Topic统计"> ____ </a>

### 数据源Topic和开放Topic统计

	请求接口: {{host}}:5080/dataSource/getDataSourceTopic
	    请求方式: get

##### 返回格式
```
{
    "status": 200,
    "data":{
           "dataSourceCount" :"100"
    },
    "msg": null
}
```

<a id="getTopicByTopicType" name="统计源数据topic和开放topic"> ____ </a>

### 统计源数据topic和开放topic

	请求接口: {{host}}:5080/dataSource/getDataSourceTopic
	    请求方式: get
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| topicType| int | 是 | topic类型|

##### 返回格式
```
{
    "status": 200,
    "data":[
    {
        "topicId" :"100",
        "topicPatition":"1",
        "topicReplication":"1",
        "topicAclType":"true",
        "topicInfo":"fsfs",
        "topicAclUserName":"admin",
        "topicAclPass":"123456",
        "topicName":"test",
        "topicMsg":"fsdfs",
        "conf":"fdsf",
        "topicType":"source"
    },
    ...
    ],
    "msg": null
}
```


<a id="countByType" name="DashBoard dataSource饼图"> ____ </a>

### DashBoard dataSource饼图

	请求接口: {{host}}:5080/dataSource/countByType
	    请求方式: get
	    
##### 返回格式
```
{
    "status": 200,
    "data":
    {
     "log":"64.65%",
     "DB":"50.02%",
     "其他":"28.32%"
    }
    "msg": null
}
```