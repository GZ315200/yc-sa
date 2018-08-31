#  告警相关接口


### 修改alert items 接口
	请求接口: {{host}}:5080/tamboo-mgt/alert/update
	    请求方式: POST
### com.unistack.tamboo.commons.json 请求样例
```
[{
	"alertName":"KafkaBroker",
	"className":"KafkaBroker",
	"alertLevel":3,
	"threshold":"{}",
	"alertInterval":20,
	"active":true
}]
```

##### 请求格式
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| alertName | String | 是 | 告警名称 |
| className | String | 是 | 告警类 |
| active | boolean | 是 | 是否激活 |
| alertInterval | Integer | 是 | 检查时间间隔 |
| threshold | String | 是 |  阈值 |
| alertLevel | Integer | 是 |  告警级别 |
##### 返回格式
```
{
    "status": 200
}

```


### 获取alert items接口
	请求接口: {{host}}:5080/tamboo-mgt/alert/getAll
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        {
            "alertName": "ActiveConnectDist",
            "className": "ActiveConnectDist",
            "alertInterval": 10,
            "active": true,
            "alertLevel": 3,
            "threshold": "{}",
            "createTime": 1529892571000,
            "updateTime": 1530082668933
        },
        {
            "alertName": "ActiveFlumeCollector",
            "className": "ActiveFlumeCollector",
            "alertInterval": 10,
            "active": true,
            "alertLevel": 3,
            "threshold": "{}",
            "createTime": 1529891912000,
            "updateTime": 1530082668933
        },
        {
            "alertName": "ActiveSparkCluster",
            "className": "ActiveSparkCluster",
            "alertInterval": 10,
            "active": true,
            "alertLevel": 3,
            "threshold": "{}",
            "createTime": 1529891909000,
            "updateTime": 1530082668934
        },
        {
            "alertName": "KafkaBroker",
            "className": "KafkaBroker",
            "alertInterval": 20,
            "active": true,
            "alertLevel": 2,
            "threshold": "{}",
            "createTime": 1530082561000,
            "updateTime": 1530082561000
        }
    ],
    "status": 200
}

```

### 获取alert records接口
	请求接口: {{host}}:5080/tamboo-mgt/alertRecords?size=100
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        {
            "categoryId": 1,
            "message": "this connector sink is down.Please check it & fix it.",
            "level": 3,
            "category": "connectDist",
            "ip": null,
            "clusterName": null,
            "collectName": null,
            "alertName": "ActiveConnectDist",
            "resolved": false,
            "flag": null,
            "alertCount": 19,
            "timestamp": 1530083566499
        },
        {
            "categoryId": 4,
            "message": "broker is down.hosts = [192.168.1.193:9093]",
            "level": 3,
            "category": "brokers",
            "ip": null,
            "clusterName": null,
            "collectName": null,
            "alertName": "KafkaBroker",
            "resolved": false,
            "flag": null,
            "alertCount": 0,
            "timestamp": 1530062999578
        }
    ],
    "status": 200
}

```

### 修改alert records 接口
	请求接口: {{host}}:5080/tamboo-mgt/alertRecords/update
	    请求方式: POST
### com.unistack.tamboo.commons.json 请求样例
```
[1,3,4]
```

##### 请求格式
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| ids | Long | 是 | 告警id |

##### 返回格式
```
{
    "status": 200
}

```

