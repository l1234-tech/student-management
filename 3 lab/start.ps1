$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$envFile = Join-Path $PSScriptRoot ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()

        if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
            $parts = $line -split "=", 2
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()

            if ($name -and -not [string]::IsNullOrWhiteSpace($value)) {
                [Environment]::SetEnvironmentVariable($name, $value, "Process")
            }
        }
    }
}

if (-not $env:DB_PASSWORD) {
    $secureDbPassword = Read-Host 'Пароль PostgreSQL' -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secureDbPassword).Password
}
if (-not $env:ADMIN_USERNAME) { $env:ADMIN_USERNAME = Read-Host 'Имя администратора (например admin)' }
if (-not $env:ADMIN_EMAIL) { $env:ADMIN_EMAIL = Read-Host 'Email администратора' }
if (-not $env:ADMIN_PASSWORD) {
    $secureAdminPassword = Read-Host 'Пароль администратора (8–72 символа)' -AsSecureString
    $env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', $secureAdminPassword).Password
}
mvn spring-boot:run
