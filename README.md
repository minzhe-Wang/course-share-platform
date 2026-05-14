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

- Docker 部署教程：`docs/Docker部署教程.md`
- 后端部署细节：`backend/DEPLOYMENT.md`
- API 接口文档：`docs/api接口文档.md`
- 数据库设计：`docs/数据库设计.md`
