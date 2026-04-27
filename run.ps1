# ============================================================
# run.ps1 — PowerShell convenience shortcuts
# ============================================================

function Show-Help {
    Write-Host "Usage: .\run.ps1 [command]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Commands:"
    Write-Host "  dev         - Start DB and run app in dev mode"
    Write-Host "  test        - Run all tests with coverage report"
    Write-Host "  build       - Build the executable JAR"
    Write-Host "  db-up       - Start only the database"
    Write-Host "  docker-up   - Build and start full Docker stack (prod)"
    Write-Host "  docker-down - Stop and remove Docker stack"
    Write-Host "  bump        - Run versioning script"
    Write-Host "  clean       - Clean build artifacts"
}

$command = $args[0]

switch ($command) {
    "db-up" {
        docker-compose up db -d
    }
    "dev" {
        docker-compose up db -d
        .\gradlew.bat bootRun --args='--spring.profiles.active=dev'
    }
    "test" {
        $env:DOCKER_HOST="npipe:////./pipe/dockerDesktopLinuxEngine"
        .\gradlew.bat test jacocoTestReport
    }
    "build" {
        .\gradlew.bat bootJar
    }
    "docker-up" {
        docker-compose up --build -d
    }
    "docker-down" {
        docker-compose down
    }
    "bump" {
        python bump_version.py
    }
    "clean" {
        .\gradlew.bat clean
    }
    default {
        Show-Help
    }
}
