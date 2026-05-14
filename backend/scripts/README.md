# Backend API Smoke Test

Run this after the backend and dependencies are started:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\api-smoke-test.ps1
```

Optional base URL:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\api-smoke-test.ps1 -BaseUrl "http://localhost:8080"
```

The script expects seeded users from `docker/mysql/init.sql`:

- `student / 123456`
- `reviewer / 123456`
- `admin / 123456`

It validates the health check, environment checks, login, public lists, personal center, AI audit mock, reviewer/admin content APIs, and admin-only basic management APIs.
