# Levanta ms-reservas en local (perfil dev) usando las variables de .env.local
Set-Location $PSScriptRoot

$env:Path = "$env:SystemRoot\System32\WindowsPowerShell\v1.0;$env:Path"

$envFile = Join-Path $PSScriptRoot ".env.local"

if (-not (Test-Path $envFile)) {
    Write-Host ""
    Write-Host "No existe el archivo .env.local." -ForegroundColor Red
    Write-Host "Copia .env.local.example a .env.local y completa los valores (BD y RabbitMQ)." -ForegroundColor Yellow
    Write-Host "Ejemplo:" -ForegroundColor Yellow
    Write-Host "  Copy-Item .env.local.example .env.local" -ForegroundColor Cyan
    Write-Host ""
    exit 1
}

# Separar lineas de variables (ignorando vacias y comentarios)
$lineas = @(Get-Content $envFile | Where-Object {
    $t = $_.Trim()
    $t -ne "" -and -not $t.StartsWith("#") -and $t.Contains("=")
})

if ($lineas.Count -eq 0) {
    Write-Host ""
    Write-Host "El archivo .env.local esta vacio o no contiene variables." -ForegroundColor Red
    Write-Host "Completa .env.local a partir de .env.local.example antes de arrancar." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# Cargar variables y registrar solo sus NOMBRES (nunca sus valores)
$cargadas = @()
foreach ($raw in $lineas) {
    $linea = $raw.Trim()
    $idx = $linea.IndexOf("=")
    if ($idx -lt 1) { continue }

    $nombre = $linea.Substring(0, $idx).Trim()
    $valor = $linea.Substring($idx + 1).Trim()

    Set-Item -Path "Env:$nombre" -Value $valor
    $cargadas += $nombre
}

foreach ($nombre in $cargadas) {
    Write-Host "Cargada: $nombre" -ForegroundColor Green
}

# Validar variables obligatorias
$obligatorias = @("DB_URL", "DB_USER", "DB_PASS")
$faltantes = @()
foreach ($var in $obligatorias) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($var, "Process"))) {
        $faltantes += $var
    }
}

if ($faltantes.Count -gt 0) {
    Write-Host ""
    Write-Host "Faltan variables obligatorias en .env.local: $($faltantes -join ', ')" -ForegroundColor Red
    Write-Host "Completa esas variables antes de arrancar la app." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
