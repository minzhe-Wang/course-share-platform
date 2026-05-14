param(
    [string]$BaseUrl = "http://localhost",
    [string]$StudentUsername = "student",
    [string]$ReviewerUsername = "reviewer",
    [string]$AdminUsername = "admin",
    [string]$Password = "123456"
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Assert-Result {
    param(
        [object]$Response,
        [string]$Name
    )

    if ($null -eq $Response) {
        throw "$Name failed: empty response"
    }
    if ($Response.code -ne 200) {
        $message = if ($Response.message) { $Response.message } else { "unknown error" }
        throw "$Name failed: code=$($Response.code), message=$message"
    }
}

function Invoke-Json {
    param(
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [string]$Token = $null,
        [string]$Name = $Path
    )

    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $uri = "$BaseUrl$Path"
    if ($null -eq $Body) {
        $response = Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers
    } else {
        $jsonBody = $Body | ConvertTo-Json -Depth 20
        $response = Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers -ContentType "application/json; charset=utf-8" -Body $jsonBody
    }

    Assert-Result -Response $response -Name $Name
    return $response
}

function Assert-HealthUp {
    param([object]$Response)

    if ($Response.data.status -ne "UP") {
        $components = $Response.data.components | ForEach-Object {
            "$($_.name)=$($_.status)($($_.message))"
        }
        throw "health check failed: status=$($Response.data.status), components=$($components -join ', ')"
    }
}

function Login {
    param(
        [string]$Username,
        [string]$RoleName
    )

    $response = Invoke-Json -Method "POST" -Path "/api/user/login" -Body @{
        username = $Username
        password = $Password
    } -Name "login $RoleName"

    if (-not $response.data.token) {
        throw "login $RoleName failed: token is empty"
    }
    return $response.data.token
}

Write-Host "Course Share Platform API smoke test" -ForegroundColor Green
Write-Host "BaseUrl: $BaseUrl"

Write-Step "Environment checks"
Invoke-Json -Method "GET" -Path "/api/test" -Name "backend test" | Out-Null
$health = Invoke-Json -Method "GET" -Path "/api/health" -Name "health check"
Assert-HealthUp -Response $health
Invoke-Json -Method "GET" -Path "/api/db/time" -Name "database time" | Out-Null
Invoke-Json -Method "GET" -Path "/api/redis/test" -Name "redis test" | Out-Null

Write-Step "Login seeded users"
$studentToken = Login -Username $StudentUsername -RoleName "student"
$reviewerToken = Login -Username $ReviewerUsername -RoleName "reviewer"
$adminToken = Login -Username $AdminUsername -RoleName "admin"

Write-Step "Student-facing APIs"
Invoke-Json -Method "GET" -Path "/api/user/me" -Token $studentToken -Name "current user" | Out-Null
Invoke-Json -Method "GET" -Path "/api/categories" -Name "categories" | Out-Null
Invoke-Json -Method "GET" -Path "/api/tags" -Name "tags" | Out-Null
Invoke-Json -Method "GET" -Path "/api/materials?pageNum=1&pageSize=5" -Name "material list" | Out-Null
Invoke-Json -Method "GET" -Path "/api/questions?pageNum=1&pageSize=5" -Name "question list" | Out-Null
Invoke-Json -Method "GET" -Path "/api/hot/materials?limit=5" -Name "hot materials" | Out-Null
Invoke-Json -Method "GET" -Path "/api/hot/questions?limit=5" -Name "hot questions" | Out-Null
Invoke-Json -Method "GET" -Path "/api/user/materials?pageNum=1&pageSize=5" -Token $studentToken -Name "my materials" | Out-Null
Invoke-Json -Method "GET" -Path "/api/user/favorites?pageNum=1&pageSize=5" -Token $studentToken -Name "my favorites" | Out-Null
Invoke-Json -Method "GET" -Path "/api/user/questions?pageNum=1&pageSize=5" -Token $studentToken -Name "my questions" | Out-Null
Invoke-Json -Method "GET" -Path "/api/user/answers?pageNum=1&pageSize=5" -Token $studentToken -Name "my answers" | Out-Null
Invoke-Json -Method "GET" -Path "/api/user/downloads?pageNum=1&pageSize=5" -Token $studentToken -Name "my downloads" | Out-Null

Write-Step "AI audit mock"
Invoke-Json -Method "POST" -Path "/api/ai-audit/test" -Body @{
    targetType = "QUESTION"
    content = "This is a normal study note."
} -Name "ai audit pass" | Out-Null
$rejectKeyword = -join ([char]24191, [char]21578)
Invoke-Json -Method "POST" -Path "/api/ai-audit/test" -Body @{
    targetType = "QUESTION"
    content = "mock $rejectKeyword content"
} -Name "ai audit reject" | Out-Null

Write-Step "Reviewer/admin APIs"
Invoke-Json -Method "GET" -Path "/api/admin/reports?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin reports" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/materials?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin materials" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/questions?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin questions" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/answers?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin answers" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/replies?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin replies" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/ai-audits?pageNum=1&pageSize=5" -Token $reviewerToken -Name "admin ai audits" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/dashboard/summary" -Token $reviewerToken -Name "admin dashboard" | Out-Null

Write-Step "Admin-only APIs"
Invoke-Json -Method "GET" -Path "/api/admin/users?pageNum=1&pageSize=5" -Token $adminToken -Name "admin users" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/categories?pageNum=1&pageSize=5" -Token $adminToken -Name "admin categories" | Out-Null
Invoke-Json -Method "GET" -Path "/api/admin/tags?pageNum=1&pageSize=5" -Token $adminToken -Name "admin tags" | Out-Null

Write-Host ""
Write-Host "Smoke test passed." -ForegroundColor Green
