#  登录相关接口


### 登录接口
	请求接口: {{host}}:5080/tamboo-mgt/login
	    请求方式: POST
### com.unistack.tamboo.commons.json 请求样例
```
{
  "username":"admin",
  "password":"2241883"
}
```




##### 请求格式
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
##### 返回格式
```
{
    "data": [
        {
            "userId": 2, /
            "username": "admin",
            "userGroup": null,
            "roles": [
                {
                    "createTime": 1526884523000,
                    "updateTime": 1526884523000,
                    "createBy": null,
                    "updateBy": null,
                    "remarks": null,
                    "id": 3,
                    "roleName": "ROLE_ADMIN",
                    "users": [],
                    "resource": []
                }
            ],
            "permission": []
        }
    ],
    "status": 200
}
```


### 登出
	请求接口: {{host}}:5080/tamboo-mgt/user/logout
	    请求方式: GET

##### 返回格式
```
{
    "data": "logout successfully.",
    "status": 200
}
```


# 用户、角色、权限、资源相关接口

### 创建用户
	请求接口: {{host}}:5080/tamboo-mgt/system/user/create
	    请求方式: POST

json请求样例: 

```
{
	"username":"zc",
	"password":"admin123",
	"userGroup":"role_group",
	"role": [{"roleName":"ROLE_ADMIN"}],
	"isActive":1
}
```

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| username | String | 是 | 用户名 |
| password | String | 是 |  密码 |
| userGroup | String | 否 |  用户组 |
| role | List | 是 |  角色 | 角色信息|
| isActive | int | 是 | | 是否激活|

##### 返回格式
```
{
    "data": {
        "createTime": 1527493794984,
        "updateTime": 1527493794984,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 4,
        "userGroup": "role_group",
        "username": "zc",
        "password": "",
        "role": []
    },
    "status": 200
}
```
返回错误时：
```
{
    "status": 0,
    "msg": "username is exist."
}
```

### 更新用户
	请求接口: {{host}}:5080/tamboo-mgt/system/user/update
	    请求方式: POST

json请求样例: 

```
{
    "id":1,
	"username":"zc",
	"password":"admin123",
	"userGroup":"role_group",
	"role": [{"roleName":"ROLE_ADMIN"}]
	....
}
```

##### 返回格式
```
{
    "data": {
        "createTime": 1527493794984,
        "updateTime": 1527493794984,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 4,
        "userGroup": "role_group",
        "username": "zc",
        "password": "",
        "role": []
    },
    "status": 200
}
```


### 逻辑删除用户
	请求接口: {{host}}:5080/tamboo-mgt/system/user/delete
	    请求方式: POST

##### 请求样例
localhost:5080/tamboo-mgt/system/user/delete?user_id=1&is_active=1
##### 返回格式
```
{
    "status": 200
}
```


### 获取所有用户的列表
	请求接口: {{host}}:5080/tamboo-mgt/system/users/get
	    请求方式: GET

无请求参数
##### 返回格式
```
{
    "data": [
        {
            "userId": 383,
            "userGroup": "admin",
            "username": "admin",
            "email": "l2241883@gmail.com",
            "phone": "18911200150",
            "isActive": 1
        }
    ],
    "status": 200
}
```

### 根据用户信息获取用户

请求接口: {{host}}:5080/tamboo-mgt/system/users/get?username=
	    请求方式: GET

#### 请求样例
    {{host}}:5080/tamboo-mgt/system/users/get?username=mazean
##### 请求参数
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| username | String | 是 | 用户名 |

##### 返回格式
```
{
    "data": [
        {
            "userId": 383,
            "userGroup": "admin",
            "username": "admin",
            "email": "l2241883@gmail.com",
            "phone": "18911200150",
            "isActive": 1
        }
    ],
    "status": 200
}
```







### 创建角色
	请求接口: {{host}}:5080/tamboo-mgt/system/role/create
	    请求方式: POST

### 请求样例
```
{
	"roleName":"ROLE_USER",
	"resource":[{"url":"/system/user/create"}]
}
```

##### 返回格式
```
{
    "data": {
        "createTime": null,
        "updateTime": 1527498815947,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 10,
        "roleName": "ROLE_USER",
        "users": [],
        "resource": [
            {
                "createTime": 1527498815914,
                "updateTime": 1527498815914,
                "createBy": null,
                "updateBy": null,
                "remarks": null,
                "id": 13,
                "roles": [],
                "url": "/system/user/create",
                "permission": null,
                "isShow": null
            }
        ]
    },
    "status": 200
```

### 更新角色
	请求接口: {{host}}:5080/tamboo-mgt/system/role/update
	    请求方式: POST

### 请求样例
```
{
	"id":"10",
	"roleName":"ROLE_USER",
	"resource":[{"url":"/system/user/create"}]
}
```

##### 返回格式
```
{
    "data": {
        "createTime": null,
        "updateTime": 1527498815947,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 10,
        "roleName": "ROLE_USER",
        "users": [],
        "resource": [
            {
                "createTime": 1527498815914,
                "updateTime": 1527498815914,
                "createBy": null,
                "updateBy": null,
                "remarks": null,
                "id": 13,
                "roles": [],
                "url": "/system/user/create",
                "permission": null,
                "isShow": null
            }
        ]
    },
    "status": 200
```


### 删除角色
	请求接口: {{host}}:5080/tamboo-mgt/system/role/delete/{role_id}
	    请求方式: POST

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| role_id | long | 是 | 角色id |

##### 返回格式
```
```
{
    "status": 200
}
```
```


### 获取所有角色的列表
	请求接口: {{host}}:5080/tamboo-mgt/system/allRoles/get
	    请求方式: GET

无请求参数
##### 返回格式
```
{
    "data": [
        {
            "createTime": null,
            "updateTime": 1526884523000,
            "createBy": null,
            "updateBy": null,
            "remarks": null,
            "id": 2,
            "userGroup": null, //用户组
            "username": "admin", //用户名
            "role": [
                {
                    "createTime": 1526884523000,
                    "updateTime": 1526884523000,
                    "createBy": null,
                    "updateBy": null,
                    "remarks": null,
                    "id": 3,
                    "roleName": "ROLE_ADMIN",
                    "users": [],
                    "resource": []
                }
            ]
        }
    ],
    "status": 200
}
```



## 创建资源
	请求接口: {{host}}:5080/tamboo-mgt/system/resource/create
	    请求方式: POST

### 请求Json样例
```
{
	"url":"/system/create",
	"pemission":"view",
	"isShow":"1"
}
```
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| url | String | 是 | url |
| pemission | String | 是 | 权限 |
| isShow | int | 是 | 是否展示此页面的URL |

##### 返回格式
```
{
    "data": {
        "createTime": 1527499471713,
        "updateTime": 1527499471713,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 14,
        "roles": [],
        "url": "/system/create",
        "permission": null,
        "isShow": 1
    },
    "status": 200
}
```


### 更新资源
	请求接口: {{host}}:5080/tamboo-mgt/system/resource/update
	    请求方式: POST


### 请求Json样例
```
{
    “id”:"1",
	"url":"/system/create",
	"pemission":"view",
	"isShow":"1"
}
```
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| id | int | 是 | 资源id |
| url | String | 是 | url |
| pemission | String | 是 | 权限 |
| isShow | int | 是 | 是否展示此页面的URL |

##### 返回格式
```
{
    "data": {
        "createTime": 1527499471713,
        "updateTime": 1527499471713,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "id": 14,
        "roles": [],
        "url": "/system/create",
        "permission": null,
        "isShow": 1
    },
    "status": 200
}
```






### 删除资源
	请求接口: {{host}}:5080/tamboo-mgt/system/resource/resource/delete/{resource_id}
	    请求方式: POST



##### 返回格式
```
{
  "status": 200
}
```


### 获取所有资源的列表

	请求接口: {{host}}:5080/tamboo-mgt/system/allResources/get?isShow=?
	    请求方式: GET
| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| isShow | int | 否 | 默认为展示url |

##### 返回格式
```
{
    "data": [
        {
            "createTime": null,
            "updateTime": 1527500838000,
            "createBy": null,
            "updateBy": null,
            "remarks": null,
            "id": 12,
            "roles": [],
            "url": "/system/update",
            "permission": null,
            "isShow": 1
        },
        {
            "createTime": 1527499471000,
            "updateTime": 1527499471000,
            "createBy": null,
            "updateBy": null,
            "remarks": null,
            "id": 14,
            "roles": [],
            "url": "/system/create",
            "permission": null,
            "isShow": 1
        }
    ],
    "status": 200
}
```


### 根据userId查询用户权限

	请求接口: {{host}:5080/tamboo-mgt/system/userPermissions/get/{user_id}
	    请求方式: GET


| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| user_id | long | 是 | 用户id |

##### 返回格式
```
{
    "data": [
        {
            "userId": 2,
            "username": "zc",
            "userGroup": "role_group",
            "roles": [
                {
                    "createTime": 1527496614000,
                    "updateTime": 1527496614000,
                    "createBy": null,
                    "updateBy": null,
                    "remarks": null,
                    "id": 7,
                    "roleName": "ROLE_ADMIN",
                    "users": [],
                    "resource": []
                }
            ],
            "permission": []
        }
    ],
    "status": 200
}
```



### 查询所有角色

	请求接口: {{host}:5080/tamboo-mgt/system/roleNames/get
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        "ROLE_ADMIN"
    ],
    "status": 200
}
```


### 获取所有权限列表

	请求接口: {{host}:5080/tamboo-mgt/system/permission/get
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        "view"
    ],
    "status": 200
}
```

### 获取所有url列表

	请求接口: {{host}:5080/tamboo-mgt/system/urls/get
	    请求方式: GET

##### 返回格式
```
{
    "data": [
        "/system/create",
        "/system/update"
    ],
    "status": 200
}
```



### 获取所有用户组列表

	请求接口: {{host}:5080/tamboo-mgt/system/userGroups/get
	    请求方式: GET


##### 返回格式
```
{
    "data": [
        "tom",
        "test",
        "torato"
    ],
    "status": 200
}
```






### 获取所有用户组信息
	请求接口: {{host}}:5080/tamboo-mgt/system/userGroup/getAll
	    请求方式: GET


##### 返回格式
```
{
    "data": [
        "test",
        "admin"
    ],
    "status": 200
}
```




### 创建用户租
	请求接口: {{host}}:5080/tamboo-mgt/system/userGroup/create
	    请求方式: POST

json请求样例: 

```
{
	"groupName":"zc"
}
```

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| groupName | String | 是 | 用户组 |

##### 返回格式
```
{
    "data": {
        "createTime": 1528804266470,
        "updateTime": 1528804266470,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "groupId": 871,
        "groupName": "公益",
        "aclUsername": "tu_sauydfo",
        "aclPassword": "tp_fgbtbbj"
    },
    "status": 200
}
```

### 更新用户租
	请求接口: {{host}}:5080/tamboo-mgt/system/userGroup/update
	    请求方式: POST

json请求样例: 

```
{
	"groupId":"1"
}
```

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| groupId | int  | 是 | 用户组Id |

##### 返回格式
```
{
    "data": {
        "createTime": 1528804266470,
        "updateTime": 1528804266470,
        "createBy": null,
        "updateBy": null,
        "remarks": null,
        "groupId": 871,
        "groupName": "公益",
        "aclUsername": "tu_sauydfo",
        "aclPassword": "tp_fgbtbbj"
    },
    "status": 200
}
```


### 删除用户租
	请求接口: {{host}}:5080/tamboo-mgt/system/userGroup/delete
	    请求方式: GET

json请求样例: 

```
{
	"groupId":"1"
}
```

| 名称 | 数据类型 | 必要 | 说明 |
| ----- |------| -----| ----|
| groupId | int | 是 | 用户组Id |

##### 返回格式
```
{
    "status": 200
}
```





