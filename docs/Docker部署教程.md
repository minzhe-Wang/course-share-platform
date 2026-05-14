# Docker 部署教程

本文档用于课程作业提交和本地演示，目标是让其他人拉取仓库后，可以用 Docker 一次性启动前端、后端、数据库和依赖服务。

如果你还没有安装 Git、Docker、Node.js、JDK 或 Maven，或者想了解开发模式，请先看：

```text
docs/项目使用指南.md
```

## 1. 项目部署结构

本项目 Docker 环境包含以下服务：

| 服务 | 容器名 | 作用 | 对外端口 |
| --- | --- | --- | --- |
| `nginx` | `course_nginx` | 前端静态资源服务、后端 API 反向代理 | `80` |
| `frontend` | `course_frontend` | 构建 Vue 前端产物，并复制到共享卷 | 不暴露 |
| `backend` | `course_backend` | Spring Boot 后端接口服务 | `18080` |
| `mysql` | `course_mysql` | 业务数据库 | `3306` |
| `redis` | `course_redis` | 缓存服务 | `6379` |
| `minio` | `course_minio` | 文件对象存储 | `9000`、`9001` |
| `minio-init` | `course_minio_init` | 初始化 MinIO bucket | 不暴露 |

正常访问时推荐走 Nginx：

```text
前端页面：http://localhost
后端 API：http://localhost/api
```

后端直连端口 `18080` 只用于调试：

```text
http://localhost:18080/api
```

## 2. 环境要求

本机需要安装：

- Docker Desktop
- Docker Compose v2

验证命令：

```powershell
docker --version
docker compose version
```

## 3. 首次启动

进入项目根目录：

```powershell
cd D:\dev\Projects\course-share-platform
```

复制环境变量模板：

```powershell
Copy-Item .env.example .env
```

启动全部服务：

```powershell
docker compose up -d --build
```

查看服务状态：

```powershell
docker compose ps
```

看到 `course_mysql`、`course_redis`、`course_minio`、`course_backend`、`course_nginx` 都是 `healthy`，说明基础服务已经启动完成。

## 4. 访问地址

| 功能 | 地址 |
| --- | --- |
| 前端页面 | `http://localhost` |
| 后端 API 统一入口 | `http://localhost/api` |
| 后端直连调试 | `http://localhost:18080/api` |
| 健康检查 | `http://localhost/api/health` |
| MinIO 控制台 | `http://localhost:9001` |

MinIO 默认账号：

```text
minioadmin / minioadmin
```

## 5. 默认测试账号

数据库初始化脚本会创建三个测试账号，密码都是 `123456`：

| 用户名 | 角色 | 说明 |
| --- | --- | --- |
| `student` | `STUDENT` | 学生用户，可发布资料、提问、收藏、下载 |
| `reviewer` | `REVIEWER` | 审核员，可查看和处理内容审核相关数据 |
| `admin` | `ADMIN` | 管理员，可管理用户、分类、标签等基础数据 |

## 6. 联调验证

健康检查：

```powershell
Invoke-RestMethod http://localhost/api/health
```

数据库连通性：

```powershell
Invoke-RestMethod http://localhost/api/db/time
```

Redis 连通性：

```powershell
Invoke-RestMethod http://localhost/api/redis/test
```

运行后端冒烟测试：

```powershell
powershell -ExecutionPolicy Bypass -File .\backend\scripts\api-smoke-test.ps1
```

成功时输出：

```text
Smoke test passed.
```

这表示登录、公开接口、学生端接口、AI 审核 Mock、审核/管理接口都能通过 Nginx 调用后端。

## 7. 常用运维命令

查看容器：

```powershell
docker compose ps
```

查看日志：

```powershell
docker compose logs -f backend
docker compose logs -f nginx
docker compose logs -f mysql
```

重启服务：

```powershell
docker compose restart backend
docker compose restart nginx
```

停止服务：

```powershell
docker compose down
```

重新构建并启动：

```powershell
docker compose up -d --build
```

## 8. 重置数据库

MySQL 初始化脚本 `docker/mysql/init.sql` 只会在数据库卷首次创建时执行。

如果修改了初始化 SQL，或者本地数据库数据已经旧了，需要重置数据卷：

```powershell
docker compose down -v
docker compose up -d --build
```

注意：`docker compose down -v` 会删除 MySQL、MinIO 等 Docker volume 中的数据，只适合本地开发或课程演示环境。

## 9. 常见问题

### 9.1 端口被占用

如果 `80`、`3306`、`6379`、`9000`、`9001`、`18080` 端口被占用，可以修改 `.env`：

```text
NGINX_PORT=8088
MYSQL_PORT=13306
REDIS_PORT=16379
MINIO_API_PORT=19000
MINIO_CONSOLE_PORT=19001
BACKEND_PORT=18080
```

改完后重新启动：

```powershell
docker compose up -d
```

### 9.2 后端健康检查里 MySQL DOWN

先查看后端日志：

```powershell
docker compose logs -f backend
```

如果看到和 `characterEncoding` 相关的错误，应确认 JDBC URL 使用的是：

```text
characterEncoding=UTF-8
```

数据库表字符集仍然使用 `utf8mb4`，两者不是同一个概念。

### 9.3 登录提示密码错误

如果本地曾经用旧版 SQL 初始化过数据库，`sys_user.password` 可能还是明文 `123456`，而后端现在使用 BCrypt 校验。

本地演示环境可以直接重置数据库：

```powershell
docker compose down -v
docker compose up -d --build
```

### 9.4 Nginx unhealthy 但页面能访问

健康检查应使用：

```text
http://127.0.0.1/
```

不要使用 `http://localhost/`，部分容器环境中 `localhost` 会优先解析到 IPv6 `::1`，导致健康检查误判。

## 10. 提交 GitHub 前检查清单

提交前建议执行：

```powershell
docker compose up -d --build
docker compose ps
powershell -ExecutionPolicy Bypass -File .\backend\scripts\api-smoke-test.ps1
```

确认：

- 前端页面可以打开
- `/api/health` 返回 `UP`
- smoke test 通过
- `.env` 不提交到仓库
- `.env.example`、`docker-compose.yml`、`docker/mysql/init.sql`、`nginx/nginx.conf` 已提交
