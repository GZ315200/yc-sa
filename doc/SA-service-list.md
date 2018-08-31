# data service

	正式服地址: {{host}} -> http://www.unistack.com.cn
	测试服地址: {{host}} -> http://192.168.1.210/tamboo-sa
	Heaters:
		key->Content-Type
		value->application/com.unistack.tamboo.commons.json
       

- <a href="#listWf"><b>获取数据流列表</b></a>
- <a href="#query"><b>查看数据流详情</b></a>
- <a href="#queryNode"><b>查看节点信息</b></a>

<a id="listWf" name="listWf"> ____ </a>
### 获取数据流列表
	请求接口: {{host}}:7080/tamboo-sa/df/list
	    请求方式: get
##### 输入参数
```

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
```
##### 输入格式
```
	        pageIndex->页数（int）
			pageSize->每页条数（int）
			query->查询关键字（string）
```
##### 输入格式
```
```
##### 返回格式(依据李耀辉给的格式)
```
    {
        "data":[{
            "wf_name":"appLogin",
            "totalRecords":234224,
            "peak":3415,
            "latency":"22ms",
            "workers":10,
            "wf_id":11
        
        },{
            "wf_name":"appOpen",
            "totalRecords":24224,
            "peak":345,
            "latency":"21ms",
            "workers":3,
            "wf_id":12
        },...
        ]
   
            "wf_id":12,
            "wf_name":"appLogin",
            "rate":2331,
            "totalRecords":34234234,
            "latency":"23ms",
            "workers":12
        },{
            "wf_id":13,
            "wf_name":"appLogin",
            "rate":2331,
            "totalRecords":34234234,
            "latency":"23ms",
            "workers":2
        },{
           "wf_id":14,
            "wf_name":"appLogin",
            "rate":2331,
            "totalRecords":34234234,
            "latency":"23ms",
            "workers":12 
        }],
        "status": 200
    }
```

<a id="query" name="query"> ____ </a>
### 查看数据流详情
	请求接口: {{host}}:7080/tamboo-sa/df/query
	    请求方式: get
##### 输入参数
```
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| wf_id | int | 是 | 工作流id |
```
##### 输入格式
```

{
    "wf_id":11
}

```
##### 返回格式
```
  {
    "data": {
        "summary": {
            "source": 23423,
            "calc": 23432,
            "dist": 3333
        },
        "wf": {
            "nodeDataArray": [{
                    "category": "Comment",
                    "loc": "360 -10",
                    "text": "Kookie Brittle",
                    "key": 0,
                    "type": 0,
                    "data": {
                        "topic_name": "appLogin",
                        "topic_id": 3434
                    }
                },
                {
                    "key": 1,
                    "category": "Start",
                    "loc": "175 0",
                    "text": "Start",
                    "type": 11,
                    "data": {
                        "data_wf": 22,
                        "conf": {
                            "filter": ""
                        }
                    }
                },
                {
                    "key": 1,
                    "category": "MQ",
                    "loc": "175 0",
                    "text": "MQ",
                    "type": 21,
                    "data": {
                        "topic_id": 22,
                        "topic_name": "group_user_app_1",
                        "is_open": true,
                        "group_list": [1, 2, 3]
                    }
                },
                {
                    "key": 1,
                    "category": "MQ",
                    "loc": "175 0",
                    "text": "MQ",
                    "type": 31,
                    "data": {
                        "type": "mysql",
                        "url": "192.168.1.191",
                        "username": "",
                        "passwd": "",
                        "table": ""
                    }
                }
            ],
            "linkDataArray": [{
                    "from": 1,
                    "to": 2,
                    "fromPort": "B",
                    "toPort": "T"
                },
                {
                    "from": 2,
                    "to": 3,
                    "fromPort": "B",
                    "toPort": "T"
                },
                {
                    "from": 3,
                    "to": 4,
                    "fromPort": "B",
                    "toPort": "T"
                }
            ]
        },
        "flow":{
            "calc":[{
                "timestamp":1527494000,
                "offset":31234
            },{
                "timestamp":1527495000,
                "offset":31224
            },{
                "timestamp":1527496000,
                "offset":31244
            }],
            "dist":[{
                "timestamp":1527494000,
                "offset":31234
            },{
                "timestamp":1527495000,
                "offset":31224
            },{
                "timestamp":1527496000,
                "offset":31244
            }]
        }
        
    },
    "status": 200
}

```

<a id="queryNode" name="queryNode"> ____ </a>
### 查看节点信息
	请求接口: {{host}}:7080/tamboo-sa/df/queryNode
	    请求方式: get
##### 输入参数
```
```
##### 输入格式
```

{
   "type":"calc",
   "data_wf":21
}

```
##### 返回格式（不同类型的返回不同的数据）
```
    {
        "data":{
<a id="showWf" name="showWf"> ____ </a>
### 数据流详情
	请求接口: {{host}}:7080/tamboo-sa/df/show
	    请求方式: get
##### 输入参数
```
{
    "wf_id":12
}
```
##### 输入格式
```
```
##### 返回格式
```
    {
        "data":{
            "wf_id":12,
            "wf_name":"appLogin",
            "rate":{
                "source":2333,
                "calc":2324,
                "dist":3432
            },
            "flow":[{
                "timestamp":15034223000,
                "source":2313,
                "dist":321
            },{
                "timestamp":15034224000,
                "source":2323,
                "dist":324
            },{
                "timestamp":15034225000,
                "source":2343,
                "dist":332
            }]
        },
        "status": 200
    }
```
