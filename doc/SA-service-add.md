# data service

	正式服地址: {{host}} -> http://www.unistack.com.cn
	测试服地址: {{host}} -> http://192.168.1.210/tamboo-sa
	Heaters:
		key->Content-Type
		value->application/com.unistack.tamboo.commons.json
       

- <a href="#getType"><b>获取标签类型</b></a>
- <a href="#addWf"><b>添加数据源</b></a>
- <a href="#testWf"><b>测试数据流程</b></a>

<a id="metrics" name="metrics"> ____ </a>
### 获取标签类型
	请求接口: {{host}}:7080/tamboo-sa/df/queryType
	    请求方式: get
##### 输入参数
```
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| type | string | 是 | [source,calc_clean,calc_join,calc_sql，calc_split,mq_kafka,dist] |
```
##### 输入格式
```
    {
        "type": "source"
  }

```
##### 返回格式(依据李耀辉给的格式)
```
    {
        "data":{
            
        },
        "status": 200
    }
```

<a id="addWf" name="addWf"> ____ </a>
### 添加数据源
	请求接口: {{host}}:7080/tamboo-sa/df/add
	    请求方式: post
##### 输入参数
```
```
##### 输入格式
```
{
  
  "nodeDataArray": [{
    "key": "A1",
    "text": "输入",
    "source": "images/DB-进.png",
    "type_code": "DATASOURCE",
    "key_code": "0",
    "loc": "-390 -180",
    "data": {
      "topic_name": "123",
       "topic_id": "123"
    },
    "__gohashid": 2016
  }, {
    "key": "B1",
    "text": "清洗",
    "source": "images/连接.png",
    "type_code": "CALC_CLEAN",
    "key_code": "11",
    "loc": "-280 -100",
    "data": [{
      "type": "Remove",
      "fields": [{
        "removeFiled": "dsdasd"
      }]
     
    }, {
      "type": "Add",
      "fields": [{
        "addKey": "sdfsdf",
        "addValue": "fsdfsf",
        "preserve_existing": "fsdfs"
      }]
     
    }],
    "__gohashid": 2604,
    "cpu": 2,
    "mem": 3
  }, {
    "key": "B2",
    "text": "MQ",
    "source": "images/MQ.png",
    "type_code": "CALC_KAFKA",
    "key_code": "21",
    "loc": "-180 -100",
    "data": {},
    "__gohashid": 2620
  }],
  "linkDataArray": [{
    "from": "B1",
    "to": "B2",
    "__gohashid": 2634,
    "points": {
      "__gohashid": 2709,
      "J": true,
      "o": [{
        "L": -246,
        "M": -100,
        "J": true
      }, {
        "L": -236,
        "M": -100,
        "J": true
      }, {
        "L": -230,
        "M": -100,
        "J": true
      }, {
        "L": -230,
        "M": -100,
        "J": true
      }, {
        "L": -224,
        "M": -100,
        "J": true
      }, {
        "L": -214,
        "M": -100,
        "J": true
      }],
      "G": 8,
      "Wb": null,
      "dj": null
    }
  }, {
    "__gohashid": 2932,
    "from": "A1",
    "to": "B1",
    "fromPort": "R",
    "toPort": "",
    "points": {
      "__gohashid": 2944,
      "J": true,
      "o": [{
        "L": -356,
        "M": -180,
        "J": true
      }, {
        "L": -346,
        "M": -180,
        "J": true
      }, {
        "L": -280,
        "M": -180,
        "J": true
      }, {
        "L": -280,
        "M": -163,
        "J": true
      }, {
        "L": -280,
        "M": -146,
        "J": true
      }, {
        "L": -280,
        "M": -136,
        "J": true
      }],
      "G": 8,
      "Wb": null,
      "dj": null
    }
  }],
  "taskName": "fsdfsdf",
  "taskInfo": "fsdfsfsd",
  "creater": "admin",
  "createDate": "2018-06-05"
}


```
##### 返回格式
```
    {
    
        "status": 200
    }
```

<a id="addWf" name="addWf"> ____ </a>
### 添加数据源
	请求接口: {{host}}:7080/tamboo-sa/df/test
	    请求方式: post
##### 输入参数
```
```
##### 输入格式
```

{
    "data":"",
    "conf":{
    	"nodeDataArray": [{
    			"category": "Comment",
    			"loc": "360 -10",
    			"text": "Kookie Brittle",
    			"key": 0,
    			"type":0,
    			"data":{
    				"topic_id":3434
    			}
    		},
    		{
    			"key": 1,
    			"category": "Start",
    			"loc": "175 0",
    			"text": "Start",
    			"type":11,
    			"data":{
    				"filter":"",
    				
    			}
    		},	
    		{
    			"key": 1,
    			"category": "MQ",
    			"loc": "175 0",
    			"text": "MQ",
    			"type":21,
    			"data":{
    				"is_open":true,
    				"group_list":[1,2,3]
    			}
    		},
    		{
    			"key": 1,
    			"category": "MQ",
    			"loc": "175 0",
    			"text": "MQ",
    			"type":31,
    			"data":{
    				"type":"mysql",   //oracle sqlserver postgresql 
    				"url":"192.168.1.191",
    				"username":"",
    				"passwd":"",
    				"table":""
    			}
    		}],
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
    		}]
		}
}

```
##### 返回格式
```
    {
    
        "status": 200
    }
```
