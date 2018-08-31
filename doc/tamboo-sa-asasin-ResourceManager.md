# ResourceManager Api

	正式服地址: {{host}} -> http://www.unistack.com.cn
	测试服地址: {{host}} -> http://192.168.1.101
	Heaters:
	    key->Content-Type
        value->application/com.unistack.tamboo.commons.json
        
- <a href="#list"><b>集群计算和存储资源列表</b></a>
- <a href="#edit"><b>用户组修改资源分配</b></a>
- <a href="#getAll"><b>用户组分配资源list</b></a> 
- <a href="#getUsed"><b>用户组使用资源统计</b></a>
- <a href="#getSurplus"><b>用户组剩余资源统计</b></a>
- <a href="#getAllAndUsed"><b>用户组配置和使用资源统计展示</b></a>
- <a href="#countByType"><b>DashBoard resource百分比展示</b></a>


<a id="list" name="集群计算和存储资源列表"> ____ </a>

### 集群计算和存储资源列表

	请求接口: {{host}}:5080/tamboo-mgt/resource/list
	    请求方式: get

##### 返回格式

```
{
    "data": [
        {
            "id": 1,
            "cpu": 100,
            "mem": 100,
            "topicNum": 100,
            "topicSize": 100
        }
    ],
    "status": 200
}
```


<a id="edit" name="用户组修改资源分配"> ____ </a>

### 用户组修改资源分配
	请求接口: {{host}}:5080/tamboo-mgt/resource/edit
	    请求方式: post
	    
##### 输入参数

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| DataSourceGroup | Object | 是 | 资源分配对象 |

DataSourceGroup：

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| groupName | String | 是 | 用户组名称 |
| sourceCpu | int | 是 | 资源分配cpu |
| sourceMem | int | 是 | 资源分配内存 |
| topicNum | int | 是 | 资源分配topicNum |
| topicSize | int | 是 | 资源分配topicSize |
| desc | String | 否 | 资源分配描述信息 |


##### 返回格式
```
{
    "data": {
        "groupName": "admin",
        "calcSourceCpu": 25,
        "calcSourceMem": 30,
        "topicNum": 40,
        "topicSize": 30,
        "description": "hh"
    },
    "status": 200
}
```

<a id="getAll" name="用户组分配资源list"> ____ </a>

### 用户组分配资源list
	请求接口: {{host}}:5080/tamboo-mgt/resource/getAll
	    请求方式: get
##### 返回格式
```
{
    "data": [
        {
            "groupName": "admin",
            "calcSourceCpu": 25,
            "calcSourceMem": 30,
            "topicNum": 40,
            "topicSize": 30,
            "description": "hh"
        },
        {
            "groupName": "test",
            "calcSourceCpu": 30,
            "calcSourceMem": 40,
            "topicNum": 30,
            "topicSize": 40,
            "description": "rfs"
        }
    ],
    "status": 200
}
```

<a id="getUsed" name="用户组使用资源统计"> ____ </a>

### 用户组使用资源统计
	请求接口: {{host}}:5080/tamboo-mgt/resource/getUsed
	    请求方式: get
##### 返回格式
```
{
    "data": [
        {
            "groupName": "admin",
            "usedCpu": 20,
            "usedMem": 20,
            "usedTopicNum": 20
        },
        {
            "groupName": "test",
            "usedCpu": 10,
            "usedMem": 10,
            "usedTopicNum": 10
        }
    ],
    "status": 200
}
```

<a id="getSurplus" name="用户组剩余资源统计"> ____ </a>

### 用户组剩余资源统计
	请求接口: {{host}}:5080/tamboo-mgt/resource/getSurplus
	    请求方式: get
##### 返回格式
```
{
    "data": [
        {
            "groupName": "admin",
            "surplusCpu": 5,
            "surplusMem": 10,
            "surplusTopicNum": 20
        },
        {
            "groupName": "test",
            "surplusCpu": 20,
            "surplusMem": 30,
            "surplusTopicNum": 20
        }
    ],
    "status": 200
}
```

<a id="getAllAndUsed" name="用户组配置和使用资源统计展示"> ____ </a>

### 用户组配置和使用资源统计展示
	请求接口: {{host}}:5080/tamboo-mgt/resource/getAllAndUsed
	    请求方式: get
##### 返回格式
```
{
    "data": [
        {
            "groupName": "admin",
            "topicNum": 40,
            "sourceCpu": 25,
            "sourceMem": 30,
            "usedCpu": 5,
            "usedMem": 10,
            "usedTopicNum": 20
        },
        {
            "groupName": "test",
            "topicNum": 30,
            "sourceCpu": 30,
            "sourceMem": 40,
            "usedCpu": 20,
            "usedMem": 30,
            "usedTopicNum": 20
        }
    ],
    "status": 200
}
```

<a id="countByType" name="DashBoard resource百分比展示"> ____ </a>

### DashBoard resource百分比展示

	请求接口: {{host}}:5080/tamboo-mgt/resource/countByType
	    请求方式: get
	    
##### 返回格式
```
{
    "status": 200,
    "data":
    {
     "CPU":"64.65%",
     "内存":"50.02%",
     "磁盘":"28.32%"
    }
}
```