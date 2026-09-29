param([switch]$KeepProcesses)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Set-Location $repoRoot

function Wait-Http {
    param([string]$Url, [int]$TimeoutSeconds = 120)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) { return }
        } catch { Start-Sleep -Seconds 2 }
    } while ((Get-Date) -lt $deadline)
    throw "Timed out waiting for $Url"
}

function Start-GradleBootRun {
    param([string]$Project, [string]$LogName)
    $stdout = Join-Path $env:TEMP "$LogName.out.log"
    $stderr = Join-Path $env:TEMP "$LogName.err.log"
    $args = @(":$Project`:bootRun", "--no-daemon")
    return Start-Process -FilePath (Join-Path $repoRoot "gradlew.bat") -ArgumentList $args -WorkingDirectory $repoRoot -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
}

if ([string]::IsNullOrWhiteSpace($env:COUCHBASE_ADMIN_USERNAME) -or [string]::IsNullOrWhiteSpace($env:COUCHBASE_ADMIN_PASSWORD)) {
    throw "Set COUCHBASE_ADMIN_USERNAME and COUCHBASE_ADMIN_PASSWORD before running the smoke test."
}
if ([string]::IsNullOrWhiteSpace($env:BUYER_COUCHBASE_USERNAME)) { $env:BUYER_COUCHBASE_USERNAME = $env:COUCHBASE_ADMIN_USERNAME }
if ([string]::IsNullOrWhiteSpace($env:BUYER_COUCHBASE_PASSWORD)) { $env:BUYER_COUCHBASE_PASSWORD = $env:COUCHBASE_ADMIN_PASSWORD }

$env:CONFIG_SERVER_URL = "http://localhost:8888"
$env:BUYER_COUCHBASE_CONNECTION_STRING = "couchbase://localhost"
$env:BUYER_COUCHBASE_BUCKET = "buyer"
$env:BUYER_COUCHBASE_SCOPE = "buyer_service"
$env:BUYER_COUCHBASE_COLLECTION = "preferences"
$processes = @()

try {
    & docker compose --profile buyer up -d buyer-couchbase
    if ($LASTEXITCODE -ne 0) { throw "Failed to start buyer-couchbase." }

    & (Join-Path $repoRoot "infra\couchbase\bootstrap-buyer.ps1")
    if ($LASTEXITCODE -ne 0) { throw "Couchbase bootstrap failed." }

    $configProcess = Start-GradleBootRun -Project "ConfigServerLocal" -LogName "day09-config-server"
    $processes += $configProcess
    Wait-Http "http://localhost:8888/buyer-service/default"

    $eurekaProcess = Start-GradleBootRun -Project "EurekaServer" -LogName "day09-eureka-server"
    $processes += $eurekaProcess
    Wait-Http "http://localhost:8761/actuator/health"

    $buyerProcess = Start-GradleBootRun -Project "BuyerService" -LogName "day09-buyer-service"
    $processes += $buyerProcess
    Wait-Http "http://localhost:9093/actuator/health"

    $registered = $false
    $deadline = (Get-Date).AddSeconds(90)
    do {
        try {
            $eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps/BUYER-SERVICE" -Headers @{ Accept = "application/json" } -TimeoutSec 5
            $registered = @($eureka.application.instance) | Where-Object { $_.status -eq "UP" }
        } catch { $registered = $false }
        if (-not $registered) { Start-Sleep -Seconds 2 }
    } while (-not $registered -and (Get-Date) -lt $deadline)
    if (-not $registered) { throw "buyer-service did not register in Eureka as UP." }

    $buyerId = [guid]::NewGuid().ToString()
    $preferencesBody = @{
        minPrice = 1000000; maxPrice = 5000000; currency = "TRY";
        preferredLocations = @(@{ city = "Istanbul"; district = "Kadikoy" });
        propertyTypes = @("APARTMENT"); minRooms = 1; maxRooms = 3;
        minArea = 70; maxArea = 140; preferredFeatures = @("BALCONY");
        notificationSettings = @{ emailEnabled = $true; pushEnabled = $false; smsEnabled = $false }
    } | ConvertTo-Json -Depth 10

    $put = Invoke-RestMethod -Method Put -Uri "http://localhost:9093/buyers/$buyerId/preferences" -ContentType "application/json" -Body $preferencesBody
    if ($put.buyerId -ne $buyerId) { throw "PUT response buyerId mismatch." }

    $get = Invoke-RestMethod -Method Get -Uri "http://localhost:9093/buyers/$buyerId/preferences"
    if ($get.buyerId -ne $buyerId) { throw "GET response buyerId mismatch." }

    $savedSearchBody = @{
        name = "Kadikoy apartments"; minPrice = 1500000; maxPrice = 4500000; currency = "TRY";
        locations = @(@{ city = "Istanbul"; district = "Kadikoy" });
        propertyTypes = @("APARTMENT"); minRooms = 1; maxRooms = 3;
        minArea = 70; maxArea = 140; preferredFeatures = @("BALCONY")
    } | ConvertTo-Json -Depth 10

    $post = Invoke-RestMethod -Method Post -Uri "http://localhost:9093/buyers/$buyerId/saved-searches" -ContentType "application/json" -Body $savedSearchBody
    if ($post.buyerId -ne $buyerId -or @($post.savedSearches).Count -lt 1) { throw "POST saved-search response validation failed." }

    Write-Host "Day 9 runtime smoke passed."
    Write-Host "Config Server: UP"
    Write-Host "Eureka Server: UP"
    Write-Host "Buyer Couchbase: UP"
    Write-Host "BuyerService: UP and registered"
    Write-Host "PUT preferences: PASS"
    Write-Host "GET preferences: PASS"
    Write-Host "POST saved-search: PASS"
}
finally {
    if (-not $KeepProcesses) {
        foreach ($process in ($processes | Select-Object -Reverse)) {
            if ($null -ne $process -and -not $process.HasExited) { Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue }
        }
    }
}
