@echo off
setlocal
set "GRADLE_VERSION=9.4.1"
set "PROJECT_DIR=%~dp0"
set "GRADLE_HOME=%PROJECT_DIR%.gradle\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"
if not exist "%GRADLE_BIN%" (
  for /f "usebackq delims=" %%G in (`powershell -NoProfile -ExecutionPolicy Bypass -Command "$path = Join-Path $env:USERPROFILE '.gradle\wrapper\dists\gradle-%GRADLE_VERSION%-bin'; if (Test-Path -LiteralPath $path) { Get-ChildItem -LiteralPath $path -Recurse -Filter gradle.bat | Select-Object -First 1 -ExpandProperty FullName }"`) do set "GRADLE_BIN=%%G"
)
if not exist "%GRADLE_BIN%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $v='%GRADLE_VERSION%'; $root='%PROJECT_DIR%'; $zip=Join-Path $root '.gradle\gradle.zip'; $dir=Join-Path $root '.gradle'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; & curl.exe -L --retry 3 --retry-delay 2 -o $zip \"https://downloads.gradle.org/distributions/gradle-$v-bin.zip\"; if ($LASTEXITCODE -ne 0) { throw 'Gradle download failed' }; Expand-Archive -LiteralPath $zip -DestinationPath $dir -Force"
)
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
