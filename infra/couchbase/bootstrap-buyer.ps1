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

function Invoke-Docker {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments)
    & docker @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Docker command failed: docker $($Arguments -join ' ')" }
}

function Invoke-CouchbaseCli {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments)
    Invoke-Docker exec $ContainerName /opt/couchbase/bin/couchbase-cli @Arguments
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

$clusterReady = $false
$clusterProbe = & docker exec $ContainerName curl -sS -o /dev/null -w "%{http_code}" -u "${username}:${password}" http://127.0.0.1:8091/pools/default
if ($LASTEXITCODE -ne 0) {
    throw "Failed to query Couchbase cluster status."
}
if ($clusterProbe -eq "200") {
    $clusterReady = $true
}
elseif ($clusterProbe -ne "404") {
    throw "Unexpected HTTP status '$clusterProbe' while checking Couchbase cluster status."
}

if (-not $clusterReady) {
    Write-Host "Initializing Couchbase cluster..."
    Invoke-CouchbaseCli cluster-init --cluster 127.0.0.1:8091 --cluster-username $username --cluster-password $password --services "data,index,query" --cluster-ramsize $ClusterRamMb --cluster-index-ramsize $IndexRamMb
}

$bucketExists = $false
$bucketProbe = & docker exec $ContainerName curl -sS -o /dev/null -w "%{http_code}" -u "${username}:${password}" "http://127.0.0.1:8091/pools/default/buckets/$Bucket"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to query Couchbase bucket '$Bucket'."
}
if ($bucketProbe -eq "200") {
    $bucketExists = $true
}
elseif ($bucketProbe -ne "404") {
    throw "Unexpected HTTP status '$bucketProbe' while checking bucket '$Bucket'."
}

if (-not $bucketExists) {
    Write-Host "Creating bucket '$Bucket'..."
    Invoke-CouchbaseCli bucket-create --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --bucket-type couchbase --bucket-ramsize $BucketRamMb --bucket-replica 0 --wait
}

$scopeList = (& docker exec $ContainerName /opt/couchbase/bin/couchbase-cli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --list-scopes) -join "`n"
if ($scopeList -notmatch "(?m)^$([regex]::Escape($Scope))$") {
    Write-Host "Creating scope '$Scope'..."
    Invoke-CouchbaseCli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --create-scope $Scope
}

$collectionPath = "$Scope.$Collection"
$collectionList = (& docker exec $ContainerName /opt/couchbase/bin/couchbase-cli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --list-collections) -join "`n"
if ($collectionList -notmatch "(?m)^$([regex]::Escape($collectionPath))$") {
    Write-Host "Creating collection '$collectionPath'..."
    Invoke-CouchbaseCli collection-manage --cluster 127.0.0.1:8091 --username $username --password $password --bucket $Bucket --create-collection $collectionPath
}

Write-Host "Buyer Couchbase bootstrap complete."
Write-Host "Bucket: $Bucket"
Write-Host "Scope: $Scope"
Write-Host "Collection: $Collection"
Write-Host "Secondary indexes: none (direct document-key access only)"
