
 # 1、配置文件实时测试
 - 参数示例
 
 *  参数1: json格式的配置文件
 *  参数2: xml格式的原消息
 * {"code":"错误码","error":"错误原因","description":"描述,返回结果正确时看这个描述，例如消息被过滤时会在这里体现"}
 * 返回结果示例   
        
````
     返回结果完全正确           {"code":"200","error":"","description":""}
     正确但是没有返回计算结果    {"code":"200","description":"该消息被过滤","error":""}
     出现错误时的返回结果:
           {"code":"199","error":"参数为空","description":""}
           {"code":"203","error":"SQL书写错误","description":""}
 ````



 # 2、启动计算任务 
 
 ###   2.1、启动数据清洗任务
 - 参数示例
 ````
 {"type":"clean","template":{"filter":[{"type":"Rename","fields":[{"old":"A","new":"A1"},{"old":"B","new":"B1"}]}]},"kafka":{"from_topic":"t2","to_topic":"t3","group_id":"test202","batchInterval":"2500","auto_offset_reset":"earliest","acl_username":"admin","acl_password":"admin123"}}
 ````
 
 - 参数1 - type: 计算类型,clean表示清洗数据
 - 参数2 - template: 配置模板,根据type类型的不同,template配置信息有所不同
 - 参数3 - kafka: kafka的相关配置
 
 
 -返回值
 ````
  {"code":"错误码,200表示正确,其余表示错误","msg":"错误描述,错误原因","applicationName":"唯一的一个应用程序的名字，以后用这个唯一标识关闭流程序"}
 ````
 
 -  返回值示例:
 ````
       正确开启计算任务的返回值:{"code":"200","msg":"","applicationName":"calc_abcdefghjklmnopqrst"}
       开启中途错误:{"code":"199","msg":"资源不足","applicationName":""}
````




    


 # 3、关闭计算任务(通过唯一标识applicationName)
- {"applicationName":"calc_abcdefghjklmnopqrst"}
- 参数1 - applicationName:代表一个应用的唯一标识

````
- 返回值
   {"code":"错误码,200表示正确,其余表示错误","msg":"错误描述"}
````

````
- 返回值示例
       正确开启计算任务的返回值:{"code":"200","msg":"","applicationName":"calc_abcdefghjklmnopqrst"}
       开启中途错误:{"code":"199","msg":"资源不足","applicationName":""}
 ````