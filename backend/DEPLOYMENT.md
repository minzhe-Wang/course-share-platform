# Backend Deployment

This backend is designed to run with MySQL, Redis, and MinIO through Docker Compose.

## Build And Start

Run from the project root:

```powershell
docker compose up -d --build
```

Optional local environment file:

```powershell
Copy-Item .env.example .env
```

Backend API:

```text
http://localhost/api
```

Direct backend API:

```text
http://localhost:18080/api
```

Health check:

```text
GET http://localhost/api/health
```

MinIO console:

```text
http://localhost:9001
```

Default MinIO account:

```text
minioadmin / minioadmin
```

## Environment Variables

The `docker` profile reads these variables and provides local defaults:

| Name | Default |
| --- | --- |
| `BACKEND_PORT` | `18080` |
| `NGINX_PORT` | `80` |
| `SERVER_PORT` | `8080` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/course_share?...` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | `123456` |
| `MYSQL_DATABASE` | `course_share` |
| `MYSQL_ROOT_PASSWORD` | `123456` |
| `MYSQL_PORT` | `3306` |
| `SPRING_DATA_REDIS_HOST` | `redis` |
| `SPRING_DATA_REDIS_PORT` | `6379` |
| `REDIS_PORT` | `6379` |
| `MINIO_ENDPOINT` | `http://minio:9000` |
| `MINIO_ACCESS_KEY` | `minioadmin` |
| `MINIO_SECRET_KEY` | `minioadmin` |
| `MINIO_API_PORT` | `9000` |
| `MINIO_CONSOLE_PORT` | `9001` |
| `MINIO_ROOT_USER` | `minioadmin` |
| `MINIO_ROOT_PASSWORD` | `minioadmin` |
| `MINIO_BUCKET_NAME` | `course-material` |
| `JAVA_OPTS` | empty |

Example JVM tuning:

```yaml
JAVA_OPTS: "-Xms256m -Xmx512m"
```

## Smoke Test

After all containers are healthy:

```powershell
powershell -ExecutionPolicy Bypass -File .\backend\scripts\api-smoke-test.ps1
```

The smoke test validates the health endpoint, login, public APIs, personal center APIs, AI audit mock, and admin APIs.

## Notes

- MySQL seed data is loaded from `docker/mysql/init.sql` only when the MySQL volume is first created.
- If you change seed data and need to reinitialize locally, remove the `mysql_data` volume first.
- The backend container health check calls `/api/health`, so MySQL, Redis, and MinIO bucket readiness all matter.
