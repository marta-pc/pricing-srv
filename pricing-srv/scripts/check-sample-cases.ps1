param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

$cases = @(
    @{ Name = "Test 1"; ApplicationDate = "2020-06-14T10:00:00"; ExpectedPriceList = 1; ExpectedPrice = 35.50 },
    @{ Name = "Test 2"; ApplicationDate = "2020-06-14T16:00:00"; ExpectedPriceList = 2; ExpectedPrice = 25.45 },
    @{ Name = "Test 3"; ApplicationDate = "2020-06-14T21:00:00"; ExpectedPriceList = 1; ExpectedPrice = 35.50 },
    @{ Name = "Test 4"; ApplicationDate = "2020-06-15T10:00:00"; ExpectedPriceList = 3; ExpectedPrice = 30.50 },
    @{ Name = "Test 5"; ApplicationDate = "2020-06-16T21:00:00"; ExpectedPriceList = 4; ExpectedPrice = 38.95 }
)

function Format-Amount([decimal]$Value) {
    return $Value.ToString("0.00", [System.Globalization.CultureInfo]::InvariantCulture)
}

Write-Host ""
Write-Host "Checking pricing scenarios against $BaseUrl" -ForegroundColor Cyan
Write-Host ""

$results = foreach ($case in $cases) {
    $uri = "$BaseUrl/api/v1/prices?applicationDate=$($case.ApplicationDate)&productId=35455&brandId=1"

    try {
        $response = Invoke-RestMethod -Uri $uri -Method Get

        $actualPrice = [decimal]$response.price
        $actualPriceList = [int]$response.priceList
        $passed = $actualPriceList -eq $case.ExpectedPriceList -and $actualPrice -eq $case.ExpectedPrice

        [PSCustomObject]@{
            Test = $case.Name
            ApplicationDate = $case.ApplicationDate
            ExpectedPriceList = $case.ExpectedPriceList
            ActualPriceList = $actualPriceList
            ExpectedPrice = Format-Amount $case.ExpectedPrice
            ActualPrice = Format-Amount $actualPrice
            Status = if ($passed) { "PASS" } else { "FAIL" }
        }
    }
    catch {
        Write-Host "The API is not reachable at $BaseUrl." -ForegroundColor Red
        Write-Host "Start the service first with:" -ForegroundColor Yellow
        Write-Host 'mvn "-Dmaven.repo.local=.m2/repository" spring-boot:run' -ForegroundColor Yellow
        throw
    }
}

$results | Format-Table -AutoSize

$failed = @($results | Where-Object { $_.Status -eq "FAIL" })

Write-Host ""
if ($failed.Count -eq 0) {
    Write-Host "All sample scenarios passed." -ForegroundColor Green
}
else {
    Write-Host "$($failed.Count) scenario(s) failed." -ForegroundColor Red
    exit 1
}
