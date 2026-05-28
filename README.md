# Course Share Platform

课程资料共享平台，包含 Spring Boot 后端、Vue 前端、MySQL、Redis、MinIO 和 Nginx 反向代理。

## 一键 Docker 运行

环境要求：

- Docker Desktop
- Docker Compose v2

从项目根目录执行：

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

启动完成后访问：

- 前端页面：`http://localhost`
- 后端 API 统一入口：`http://localhost/api`
- 后端直连调试入口：`http://localhost:18080/api`
- 健康检查：`http://localhost/api/health`
- MinIO 控制台：`http://localhost:9001`

默认测试账号密码均为 `123456`：

| 用户名 | 角色 |
| --- | --- |
| `student` | 学生 |
| `reviewer` | 审核员 |
| `admin` | 管理员 |

## 联调验证

查看容器状态：

```powershell
docker compose ps
```

运行接口冒烟测试：

```powershell
powershell -ExecutionPolicy Bypass -File .\backend\scripts\api-smoke-test.ps1
```

看到 `Smoke test passed.` 表示前端入口、Nginx、后端、MySQL、Redis、MinIO 已经联调通过。

## 文档

第一次运行、开发或排错时，可以按下面的目标查文档：

| 你的身份 | 建议先看 |
| --- | --- |
| 老师或验收者 | `README.md`、`docs/Docker部署教程.md`、`docs/演示流程.md` |
| 第一次运行项目的同学 | `docs/项目使用指南.md`、`docs/Docker部署教程.md` |
| 参与开发的同学 | `docs/项目使用指南.md`、`docs/协作开发规范.md` |
| 项目负责人 | `docs/协作开发规范.md`、`docs/任务分工与进度.md`、`docs/后续完善计划.md` |

| 你要做什么 | 看哪个文档 |
| --- | --- |
| 第一次安装环境、拉取项目、运行项目 | `docs/项目使用指南.md` |
| 只想用 Docker 一键部署 | `docs/Docker部署教程.md` |
| 课堂展示或验收时按步骤演示 | `docs/演示流程.md` |
| 参与小组开发、提交代码、提 PR | `docs/协作开发规范.md` |
| 管理任务分工和后续进度 | `docs/任务分工与进度.md` |
| 查看后端 Docker 配置、环境变量、健康检查 | `backend/DEPLOYMENT.md` |
| 对接接口、导入 Apifox、查看请求和返回字段 | `docs/api接口文档.md` |
| 理解数据库表结构、字段含义、初始化数据 | `docs/数据库设计.md` |
| 理解系统架构、模块划分、业务流程 | `docs/设计文档.md` |
| 了解代码命名、分层、返回格式、异常处理约定 | `docs/代码风格约定.md` |
| 查看后续还要完善什么 | `docs/后续完善计划.md` |

推荐阅读顺序：

```text
docs/项目使用指南.md
-> docs/协作开发规范.md
-> docs/Docker部署教程.md
-> docs/演示流程.md
-> docs/api接口文档.md
-> docs/设计文档.md
-> docs/数据库设计.md
```
