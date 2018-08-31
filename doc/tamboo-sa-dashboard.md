### 获取采集、收集、处理总量
	请求接口: {{host}:5080/tamboo-mgt/dashboard/summary?summary_type=
	    请求方式: GET
	   
	   
### 请求参数格式
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| summaryType | int | 是 | 查询类型 1：数据采集 2: 数据总线 3:数据处理 4:数据下发|



##### 返回格式
```
 {
    "data": {
        "total_message_rate":1231214124,
        "total_message":12312312312
        }
    "status": 200
}
```
返回类型说明

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| total_message_rate | String | message发送总速率|
| total_message | String | message的总数量|



### 获取历史offset
	请求接口: {{host}:5080/tamboo-mgt/dashboard/compare?type=
	    请求方式: GET
	   
	   
### 请求参数格式
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| type | int | 是 | 查询类型 DAY 60 * 24 * 2 ： WEEK :60 * 24 * 14 |



##### 返回格式
```
 {
 	"data": {
 		"yesterday": [{
 			"messageRate"："2342343／s"
 			"timestamp": 1527494000,
 			"offsetAdd": 31234
 		}, {
 			"messageRate"："2342343／s"
 			"timestamp": 1527494000,
 			"offsetAdd": 31234
 		}, ...],
 		"today": [{
 			"messageRate"："2342343／s"
 			"timestamp": 1527494000,
 			"offsetAdd": 31234
 		}, {
 			"messageRate"："2342343／s"
 			"timestamp": 1527494000,
 			"offsetAdd": 31234
 		}, ...]
 	},
 	"status": 200
 }
```
返回类型说明

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| total_message_rate | String | message发送总速率|
| total_message | String | message的总数量|



