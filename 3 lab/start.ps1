$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
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
