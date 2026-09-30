param(
    [string]$ContainerName = "real-estate-buyer-couchbase",
    [string]$Bucket = "buyer",
    [string]$Scope = "buyer_service",
    [string]$Collection = "preferences",
    [int]$ClusterRamMb = 512,
    [int]$IndexRamMb = 256,
    [int]$BucketRamMb = 256
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env"

function Invoke-Docker {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments)
    & docker @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Docker command failed: docker $($Arguments -join ' ')" }
}

function Invoke-CouchbaseCli {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments)
    Invoke-Docker exec $ContainerName /opt/couchbase/bin/couchbase-cli @Arguments
}

function Get-LocalSetting {
    param([Parameter(Mandatory = $true)][string]$Name)

    $environmentValue = [Environment]::GetEnvironmentVariable($Name)
    if (-not [string]::IsNullOrWhiteSpace($environmentValue)) {
        return $environmentValue
    }

    if (-not (Test-Path $envFile)) {
        return $null
    }

    foreach ($rawLine in Get-Content $envFile) {
        $line = $rawLine.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#") -or -not $line.Contains("=")) {
            continue
        }

        $separatorIndex = $line.IndexOf("=")
        $key = $line.Substring(0, $separatorIndex).Trim()
        if ($key -eq $Name) {
            return $line.Substring($separatorIndex + 1).Trim()
        }
    }

    return $null
}

$running = (& docker inspect -f "{{.State.Running}}" $ContainerName 2>$null).Trim()
if ($running -ne "true") {
    throw "Couchbase container '$ContainerName' is not running. Start it with: docker compose --profile buyer up -d buyer-couchbase"
}

$username = (& docker exec $ContainerName printenv COUCHBASE_ADMIN_USERNAME).Trim()
$password = (& docker exec $ContainerName printenv COUCHBASE_ADMIN_PASSWORD).Trim()
if ([string]::IsNullOrWhiteSpace($username) -or [string]::IsNullOrWhiteSpace($password)) {
    throw "Couchbase admin credentials are not available inside the container."
}

$clusterProbe = & docker exec $ContainerName curl -sS -o /dev/null -w "%{http_code}" -u "${username}:${password}" http://127.0.0.1:8091/pools/default
if ($LASTEXITCODE -ne 0) {
    throw "Failed to query Couchbase cluster status."
}
if ($clusterProbe -eq "404") {
    Write-Host "Initializing Couchbase cluster..."
    Invoke-CouchbaseCli cluster-init --cluster 127.0.0.1:8091 --cluster-username $username --cluster-password $password --services "data,index,query" --cluster-ramsize $ClusterRamMb --cluster-index-ramsize $IndexRamMb
}
elseif ($clusterProbe -ne "200") {
    throw "Unexpected HTTP status '$clusterProbe' while checking Couchbase cluster status."
}

$bucketProbe = & docker exec $ContainerName curl -sS -o /dev/null -w "%{http_code}" -u "${username}:${password}" "http://127.0.0.1:8091/pools/default/buckets/$Bucket"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to query Couchbase bucket '$Bucket'."
}
if ($bucketProbe -eq "404") {
    Write-Host "Creating bucket '$Bucket'..."
    Invoke-CouchbaseCli bucket-create --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --bucket-type couchbase --storage-backend couchstore --bucket-ramsize $BucketRamMb --bucket-replica 0 --wait
}
elseif ($bucketProbe -ne "200") {
    throw "Unexpected HTTP status '$bucketProbe' while checking bucket '$Bucket'."
}

$scopesJson = (& docker exec $ContainerName curl -sS -u "${username}:${password}" "http://127.0.0.1:8091/pools/default/buckets/$Bucket/scopes") -join ""
if ($LASTEXITCODE -ne 0) {
    throw "Failed to query Couchbase scopes and collections."
}

$scopes = $scopesJson | ConvertFrom-Json
$scopeEntry = $scopes.scopes | Where-Object { $_.name -eq $Scope }

if ($null -eq $scopeEntry) {
    Write-Host "Creating scope '$Scope'..."
    Invoke-CouchbaseCli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --create-scope $Scope

    $scopesJson = (& docker exec $ContainerName curl -sS -u "${username}:${password}" "http://127.0.0.1:8091/pools/default/buckets/$Bucket/scopes") -join ""
    $scopes = $scopesJson | ConvertFrom-Json
    $scopeEntry = $scopes.scopes | Where-Object { $_.name -eq $Scope }
}

$collectionEntry = $scopeEntry.collections | Where-Object { $_.name -eq $Collection }
if ($null -eq $collectionEntry) {
    $collectionPath = "$Scope.$Collection"
    Write-Host "Creating collection '$collectionPath'..."
    Invoke-CouchbaseCli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --create-collection $collectionPath
}

$appUsername = Get-LocalSetting "BUYER_DB_USERNAME"
$appPassword = Get-LocalSetting "BUYER_DB_PASSWORD"
if ([string]::IsNullOrWhiteSpace($appUsername) -or [string]::IsNullOrWhiteSpace($appPassword)) {
    throw "BUYER_DB_USERNAME and BUYER_DB_PASSWORD must be set in the OS environment or root .env file."
}

$userList = ((& docker exec $ContainerName /opt/couchbase/bin/couchbase-cli user-manage --cluster 127.0.0.1:8091 --username $username --password $password --list --auth-domain local) | Out-String)
$userExists = $userList -match "(?m)^id: $([regex]::Escape($appUsername))$"
$applicationRole = "data_writer[" + $Bucket + ":" + $Scope + ":" + $Collection + "]"

if (-not $userExists) {
    Write-Host "Creating BuyerService Couchbase application user '$appUsername'..."
    Invoke-CouchbaseCli user-manage --cluster 127.0.0.1:8091 --username $username --password $password --set --rbac-username $appUsername --rbac-password $appPassword --roles $applicationRole --auth-domain local
}
else {
    Write-Host "BuyerService Couchbase application user '$appUsername' already exists."
}

Write-Host "Buyer Couchbase bootstrap complete."
Write-Host "Bucket: $Bucket"
Write-Host "Scope: $Scope"
Write-Host "Collection: $Collection"
Write-Host "Application user: $appUsername"
Write-Host "Application role: $applicationRole"
Write-Host "Secondary indexes: none (direct document-key access only)"
