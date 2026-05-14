下面给你一版 **Apifox 可直接录入的 API 接口文档 V1**。这版按照你们当前项目设计来：用户、资料、问答、AI 审核、举报、后台管理。资料、问答、点赞、举报等模块与原设计一致，数据库也对应你们已经定稿的核心表结构，包括 `sys_user`、`material`、`question`、`answer`、`answer_reply`、`like_record`、`report`，以及最新版新增的 `ai_audit_record`。

---

# 课程资料共享与互助问答平台 API 接口文档 V1

## 1. 基础信息

```text
项目名称：课程资料共享与互助问答平台
接口前缀：http://localhost/api
后端直连调试：http://localhost:18080/api
接口版本：V1
数据格式：JSON
认证方式：Token，第一版可先使用 mock-token
```

---

## 2. 统一响应格式

所有接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码约定

| code | 含义         |
| ---- | ---------- |
| 200  | 请求成功       |
| 400  | 参数错误       |
| 401  | 未登录        |
| 403  | 无权限        |
| 404  | 资源不存在      |
| 500  | 业务异常或服务器异常 |

### 成功示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

### 失败示例

```json
{
  "code": 500,
  "message": "用户名已存在",
  "data": null
}
```

---

## 3. 认证说明

除注册、登录、测试接口外，后续业务接口建议携带请求头：

```http
Authorization: Bearer mock-token-1
```

第一版为了快速联调，可以先返回：

```text
mock-token-{userId}
```

后续再替换成 JWT。

---

# 00-环境测试模块

---

## 00.1 测试后端服务

### 基本信息

```http
GET /api/test
```

### 请求参数

无。

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": "backend dev success"
}
```

### 说明

用于测试 Spring Boot 服务是否正常启动。

---

## 00.2 测试数据库连接

### 基本信息

```http
GET /api/db/time
```

### 请求参数

无。

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "now_time": "2026-05-01T20:00:00"
  }
}
```

### 说明

用于测试 Spring Boot 是否能连接 MySQL。

---

## 00.3 测试 Redis 连接

### 基本信息

```http
GET /api/redis/test
```

### 请求参数

无。

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": "redis success"
}
```

### 说明

用于测试 Spring Boot 是否能连接 Redis。

---

# 01-用户模块

---

## 01.1 用户注册

### 基本信息

```http
POST /api/user/register
```

### 请求头

```http
Content-Type: application/json
```

### 请求参数

| 参数名      | 类型     | 是否必填 | 说明             |
| -------- | ------ | ---- | -------------- |
| username | string | 是    | 用户名，唯一         |
| password | string | 是    | 密码，建议 6 到 30 位 |
| nickname | string | 否    | 昵称             |

### 请求示例

```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三"
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 失败示例

```json
{
  "code": 500,
  "message": "用户名已存在",
  "data": null
}
```

### 说明

注册成功后，用户默认角色为：

```text
STUDENT
```

---

## 01.2 用户登录

### 基本信息

```http
POST /api/user/login
```

### 请求头

```http
Content-Type: application/json
```

### 请求参数

| 参数名      | 类型     | 是否必填 | 说明  |
| -------- | ------ | ---- | --- |
| username | string | 是    | 用户名 |
| password | string | 是    | 密码  |

### 请求示例

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

### 响应参数

| 参数名      | 类型     | 说明                            |
| -------- | ------ | ----------------------------- |
| userId   | number | 用户 ID                         |
| username | string | 用户名                           |
| nickname | string | 昵称                            |
| role     | string | 角色：STUDENT / REVIEWER / ADMIN |
| token    | string | 登录凭证，第一版为 mock token          |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 4,
    "username": "zhangsan",
    "nickname": "张三",
    "role": "STUDENT",
    "token": "mock-token-4"
  }
}
```

### 失败示例

```json
{
  "code": 500,
  "message": "密码错误",
  "data": null
}
```

---

## 01.3 获取当前用户信息

### 基本信息

```http
GET /api/user/me
```

### 请求头

```http
Authorization: Bearer mock-token-4
```

### 请求参数

无。

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 4,
    "username": "zhangsan",
    "nickname": "张三",
    "role": "STUDENT",
    "phone": null,
    "email": null,
    "avatar": null
  }
}
```

---

# 02-分类与标签模块

---

## 02.1 获取课程分类列表

### 基本信息

```http
GET /api/categories
```

### 请求参数

无。

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "操作系统",
      "type": "专业核心课",
      "sortNo": 1
    },
    {
      "id": 2,
      "name": "计算机网络",
      "type": "专业核心课",
      "sortNo": 2
    }
  ]
}
```

---

## 02.2 获取标签列表

### 基本信息

```http
GET /api/tags
```

### 请求参数

| 参数名  | 类型     | 是否必填 | 说明                        |
| ---- | ------ | ---- | ------------------------- |
| type | string | 否    | 标签类型：GRADE / TYPE / SCENE |

### 请求示例

```http
GET /api/tags?type=TYPE
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 5,
      "name": "试卷",
      "type": "TYPE"
    },
    {
      "id": 6,
      "name": "课件",
      "type": "TYPE"
    }
  ]
}
```

---

# 03-文件与资料模块

---

## 03.1 上传文件到 MinIO

### 基本信息

```http
POST /api/files/upload
```

### 请求头

```http
Content-Type: multipart/form-data
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名  | 类型   | 是否必填 | 说明                             |
| ---- | ---- | ---- | ------------------------------ |
| file | file | 是    | 上传文件，支持 PDF / DOC / DOCX / ZIP |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalFilename": "操作系统复习资料.pdf",
    "fileKey": "materials/5f2c3a7e.pdf",
    "fileUrl": "http://localhost:9000/course-material/materials/5f2c3a7e.pdf",
    "fileType": "PDF",
    "fileSize": 123456
  }
}
```

### 说明

该接口只负责上传文件，不负责写入 `material` 表。

---

## 03.2 发布资料

### 基本信息

```http
POST /api/materials
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名              | 类型     | 是否必填 | 说明                     |
| ---------------- | ------ | ---- | ---------------------- |
| title            | string | 是    | 资料标题                   |
| description      | string | 否    | 资料简介                   |
| categoryId       | number | 是    | 课程分类 ID                |
| tagIds           | array  | 否    | 标签 ID 列表               |
| fileUrl          | string | 是    | 文件访问地址                 |
| fileKey          | string | 是    | 文件对象 Key               |
| originalFilename | string | 是    | 原始文件名                  |
| fileType         | string | 是    | PDF / DOC / DOCX / ZIP |
| fileSize         | number | 是    | 文件大小，单位字节              |

### 请求示例

```json
{
  "title": "操作系统期末复习资料",
  "description": "包含进程、线程、内存管理、文件系统等重点内容",
  "categoryId": 1,
  "tagIds": [5, 9, 12],
  "fileUrl": "http://localhost:9000/course-material/materials/5f2c3a7e.pdf",
  "fileKey": "materials/5f2c3a7e.pdf",
  "originalFilename": "操作系统复习资料.pdf",
  "fileType": "PDF",
  "fileSize": 123456
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "materialId": 1,
    "auditStatus": "APPROVED",
    "auditResult": "PASS"
  }
}
```

### 说明

资料发布流程：

```text
保存资料信息
→ 调用 AI 审核
→ 写入 ai_audit_record
→ 更新 material.audit_status
```

第一版中：

```text
PASS → APPROVED
REJECT / RISK → REJECTED
```

---

## 03.3 获取资料列表

### 基本信息

```http
GET /api/materials
```

### 请求参数

| 参数名        | 类型     | 是否必填 | 说明                                       |
| ---------- | ------ | ---- | ---------------------------------------- |
| pageNum    | number | 否    | 页码，默认 1                                  |
| pageSize   | number | 否    | 每页数量，默认 10                               |
| keyword    | string | 否    | 搜索关键词                                    |
| categoryId | number | 否    | 课程分类 ID                                  |
| tagId      | number | 否    | 标签 ID                                    |
| sortBy     | string | 否    | 排序字段：latest / like / favorite / download |

### 请求示例

```http
GET /api/materials?pageNum=1&pageSize=10&keyword=操作系统&categoryId=1&sortBy=latest
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "title": "操作系统期末复习资料",
        "description": "包含进程、线程、内存管理、文件系统等重点内容",
        "categoryName": "操作系统",
        "fileType": "PDF",
        "fileSize": 123456,
        "uploaderName": "张三",
        "viewCount": 10,
        "downloadCount": 3,
        "likeCount": 5,
        "favoriteCount": 2,
        "createTime": "2026-05-01 20:00:00"
      }
    ]
  }
}
```

---

## 03.4 获取资料详情

### 基本信息

```http
GET /api/materials/{id}
```

### 路径参数

| 参数名 | 类型     | 是否必填 | 说明    |
| --- | ------ | ---- | ----- |
| id  | number | 是    | 资料 ID |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "操作系统期末复习资料",
    "description": "包含进程、线程、内存管理、文件系统等重点内容",
    "categoryId": 1,
    "categoryName": "操作系统",
    "tags": [
      {
        "id": 5,
        "name": "试卷",
        "type": "TYPE"
      }
    ],
    "fileUrl": "http://localhost:9000/course-material/materials/5f2c3a7e.pdf",
    "fileType": "PDF",
    "fileSize": 123456,
    "uploaderName": "张三",
    "viewCount": 11,
    "downloadCount": 3,
    "likeCount": 5,
    "favoriteCount": 2,
    "createTime": "2026-05-01 20:00:00"
  }
}
```

---

## 03.5 下载资料

### 基本信息

```http
POST /api/materials/{id}/download
```

### 请求头

```http
Authorization: Bearer mock-token-4
```

### 路径参数

| 参数名 | 类型     | 是否必填 | 说明    |
| --- | ------ | ---- | ----- |
| id  | number | 是    | 资料 ID |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "downloadUrl": "http://localhost:9000/course-material/materials/5f2c3a7e.pdf"
  }
}
```

### 说明

后端需要记录下载行为：

```text
download_record
material.download_count + 1
```

---

## 03.6 点赞资料

### 基本信息

```http
POST /api/materials/{id}/like
```

### 请求头

```http
Authorization: Bearer mock-token-4
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 说明

写入：

```text
like_record(target_type = MATERIAL, target_id = 资料ID)
```

并更新：

```text
material.like_count + 1
```

---

## 03.7 收藏资料

### 基本信息

```http
POST /api/materials/{id}/favorite
```

### 请求头

```http
Authorization: Bearer mock-token-4
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 03.8 取消收藏资料

### 基本信息

```http
DELETE /api/materials/{id}/favorite
```

### 请求头

```http
Authorization: Bearer mock-token-4
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

# 04-问答模块

---

## 04.1 发布问题

### 基本信息

```http
POST /api/questions
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名        | 类型     | 是否必填 | 说明      |
| ---------- | ------ | ---- | ------- |
| title      | string | 是    | 问题标题    |
| content    | string | 是    | 问题内容    |
| categoryId | number | 是    | 课程分类 ID |

### 请求示例

```json
{
  "title": "进程和线程有什么区别？",
  "content": "操作系统里进程和线程的区别是什么？它们共享哪些资源？",
  "categoryId": 1
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "questionId": 1,
    "auditStatus": "APPROVED",
    "auditResult": "PASS"
  }
}
```

---

## 04.2 获取问题列表

### 基本信息

```http
GET /api/questions
```

### 请求参数

| 参数名        | 类型     | 是否必填 | 说明           |
| ---------- | ------ | ---- | ------------ |
| pageNum    | number | 否    | 页码           |
| pageSize   | number | 否    | 每页数量         |
| keyword    | string | 否    | 搜索关键词        |
| categoryId | number | 否    | 课程分类 ID      |
| sortBy     | string | 否    | latest / hot |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "title": "进程和线程有什么区别？",
        "content": "操作系统里进程和线程的区别是什么？",
        "categoryName": "操作系统",
        "userName": "张三",
        "viewCount": 20,
        "answerCount": 2,
        "likeCount": 3,
        "createTime": "2026-05-01 20:00:00"
      }
    ]
  }
}
```

---

## 04.3 获取问题详情

### 基本信息

```http
GET /api/questions/{id}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "进程和线程有什么区别？",
    "content": "操作系统里进程和线程的区别是什么？",
    "categoryName": "操作系统",
    "userName": "张三",
    "viewCount": 21,
    "likeCount": 3,
    "answers": [
      {
        "id": 1,
        "content": "进程是资源分配的基本单位，线程是 CPU 调度的基本单位。",
        "userName": "李四",
        "likeCount": 2,
        "replyCount": 1,
        "replies": [
          {
            "id": 1,
            "content": "那线程共享进程的哪些资源？",
            "userName": "王五",
            "replyToUserName": "李四",
            "likeCount": 0
          }
        ]
      }
    ]
  }
}
```

---

## 04.4 回答问题

### 基本信息

```http
POST /api/questions/{id}/answers
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名     | 类型     | 是否必填 | 说明   |
| ------- | ------ | ---- | ---- |
| content | string | 是    | 回答内容 |

### 请求示例

```json
{
  "content": "进程是资源分配单位，线程是 CPU 调度单位。"
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "answerId": 1,
    "auditStatus": "APPROVED",
    "auditResult": "PASS"
  }
}
```

---

## 04.5 回复回答

### 基本信息

```http
POST /api/answers/{id}/replies
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名           | 类型     | 是否必填 | 说明       |
| ------------- | ------ | ---- | -------- |
| content       | string | 是    | 回复内容     |
| replyToUserId | number | 否    | 被回复用户 ID |

### 请求示例

```json
{
  "content": "这里的资源包括内存空间、文件句柄等吗？",
  "replyToUserId": 5
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "replyId": 1,
    "auditStatus": "APPROVED",
    "auditResult": "PASS"
  }
}
```

---

## 04.6 点赞问题

```http
POST /api/questions/{id}/like
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 04.7 点赞回答

```http
POST /api/answers/{id}/like
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 04.8 点赞回复

```http
POST /api/replies/{id}/like
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

# 05-AI审核模块

---

## 05.1 AI 审核测试

### 基本信息

```http
POST /api/ai-audit/test
```

### 请求头

```http
Content-Type: application/json
```

### 请求参数

| 参数名        | 类型     | 是否必填 | 说明                                   |
| ---------- | ------ | ---- | ------------------------------------ |
| targetType | string | 是    | MATERIAL / QUESTION / ANSWER / REPLY |
| content    | string | 是    | 待审核内容                                |

### 请求示例

```json
{
  "targetType": "QUESTION",
  "content": "操作系统怎么复习？"
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "auditResult": "PASS",
    "riskScore": 5.0,
    "reason": "Mock AI 审核通过"
  }
}
```

### 拒绝示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "auditResult": "REJECT",
    "riskScore": 95.0,
    "reason": "命中敏感词：广告"
  }
}
```

---

# 06-举报模块

---

## 06.1 提交举报

### 基本信息

```http
POST /api/reports
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-4
```

### 请求参数

| 参数名        | 类型     | 是否必填 | 说明                                   |
| ---------- | ------ | ---- | ------------------------------------ |
| targetType | string | 是    | MATERIAL / QUESTION / ANSWER / REPLY |
| targetId   | number | 是    | 被举报对象 ID                             |
| reason     | string | 是    | 举报原因                                 |

### 请求示例

```json
{
  "targetType": "MATERIAL",
  "targetId": 1,
  "reason": "资料内容不完整，疑似广告"
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reportId": 1
  }
}
```

---

## 06.2 获取举报列表

### 基本信息

```http
GET /api/admin/reports
```

### 请求头

```http
Authorization: Bearer mock-token-reviewer
```

### 请求参数

| 参数名          | 类型     | 是否必填 | 说明                            |
| ------------ | ------ | ---- | ----------------------------- |
| pageNum      | number | 否    | 页码                            |
| pageSize     | number | 否    | 每页数量                          |
| handleStatus | string | 否    | PENDING / RESOLVED / REJECTED |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "targetType": "MATERIAL",
        "targetId": 1,
        "targetSnapshot": "操作系统期末复习资料",
        "reportUserName": "张三",
        "reason": "资料内容不完整，疑似广告",
        "handleStatus": "PENDING",
        "createTime": "2026-05-01 20:00:00"
      }
    ]
  }
}
```

---

## 06.3 处理举报

### 基本信息

```http
PUT /api/admin/reports/{id}/handle
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-reviewer
```

### 请求参数

| 参数名          | 类型     | 是否必填 | 说明                  |
| ------------ | ------ | ---- | ------------------- |
| handleStatus | string | 是    | RESOLVED / REJECTED |
| handleResult | string | 是    | 处理说明                |

### 请求示例

```json
{
  "handleStatus": "RESOLVED",
  "handleResult": "举报成立，已下架违规内容"
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

# 07-后台管理模块

---

## 07.1 获取用户列表

### 基本信息

```http
GET /api/admin/users
```

### 请求头

```http
Authorization: Bearer mock-token-admin
```

### 请求参数

| 参数名      | 类型     | 是否必填 | 说明                         |
| -------- | ------ | ---- | -------------------------- |
| pageNum  | number | 否    | 页码                         |
| pageSize | number | 否    | 每页数量                       |
| keyword  | string | 否    | 用户名或昵称                     |
| role     | string | 否    | STUDENT / REVIEWER / ADMIN |

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "username": "student",
        "nickname": "测试学生",
        "role": "STUDENT",
        "status": 1,
        "createTime": "2026-05-01 20:00:00"
      }
    ]
  }
}
```

---

## 07.2 启用 / 禁用用户

### 基本信息

```http
PUT /api/admin/users/{id}/status
```

### 请求头

```http
Content-Type: application/json
Authorization: Bearer mock-token-admin
```

### 请求参数

| 参数名    | 类型     | 是否必填 | 说明        |
| ------ | ------ | ---- | --------- |
| status | number | 是    | 1 正常，0 禁用 |

### 请求示例

```json
{
  "status": 0
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

# 第一阶段必须实现的接口

你们现在不要全部同时做。第一批只做这些：

```text
GET  /api/test
GET  /api/db/time
GET  /api/redis/test

POST /api/user/register
POST /api/user/login
GET  /api/user/me

POST /api/ai-audit/test

POST /api/files/upload
```

第一批完成后，再做：

```text
POST /api/materials
GET  /api/materials
GET  /api/materials/{id}
```

---

# Apifox 建议分组

```text
课程资料共享与互助问答平台
├── 00-环境测试
├── 01-用户模块
├── 02-分类与标签模块
├── 03-文件与资料模块
├── 04-问答模块
├── 05-AI审核模块
├── 06-举报模块
└── 07-后台管理模块
```

你现在可以先把这版文档录进 Apifox。下一步我们就可以正式写 **用户注册 / 登录接口代码**。


