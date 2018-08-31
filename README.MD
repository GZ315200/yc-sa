
## 项目介绍

### 简介
    此项目为流数据分析平台，为用户的数据提供实时处理和批处理
    
### 项目目录
    -config 配置文件相关
    -bin shell脚本相关
    -doc  开发文档相关
    -tamboo-collector 用于开发采集器相关的
    -tamboo-commons 公共服务类
    -tamboo-compute 实时计算框架spark streaming,kstream
    -tamboo-management 管理平台相关
    -tamboo-message 消息队列 kafka 、jms、redis
    -tamboo-thirdparty 第三方工具，
    
### 开发说明
    
    _请各位开发人员clone代码后，务必在develop分支下进行开发_
    
    1、开发人员需要在相应的模块下进行开发，构建目录结构的方式为（以tamboo-message为例）：
        -src
            -main
                -java (为资源文件)
                    -com.unistack.tamboo.message.kafka
                        -....
                        -....
            -test (测试用例代码)
                -java(为test资源文件)
        pom.xml(可在子模块目录下增加POM文件)
    2、可以自己创建module，但要符合命名规范.
    3、开发人员需要在父级项目下pom.xml文件中引入公共依赖jar包.不要重复增加jar包依赖
    4、可以在子项目中引入POM文件，但必须与父项目存在继承关系.
    5、提交代码时，将不提交的目录和文件放在.gitignore里面。
    6. 开发人员进行代码提交时务必，要进行代码检查，书写测试用例。测试通过后方可提交到git服务器上
    7. 如遇到多个开发人员需修改同一个类时，必须提前说明
    8、开发人员必须将公共服务代码和工具类代码放在相应模块下面供各个模块进行引用，
    