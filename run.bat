@echo off
setlocal

rem -- Ensure javac/java are on PATH (or set full paths)
set JAVAC=javac
set JAVA=java

rem -- Paths
set SRC_DIR=src
set OUT_DIR=out
set LIB_DIR=lib

rem -- Prepare out dir
if exist "%OUT_DIR%" (
    rd /s /q "%OUT_DIR%"
)
mkdir "%OUT_DIR%"

rem -- Generate sources list (no BOM) using cmd FOR /R
if exist sources.txt del /f /q sources.txt
(for /r "%SRC_DIR%" %%f in (*.java) do @echo %%f) > sources.txt

if not exist sources.txt (
  echo Failed to generate sources.txt
  exit /b 1
)

rem -- Compile all sources, include any jars in lib
"%JAVAC%" -cp "%LIB_DIR%\*" -d "%OUT_DIR%" @sources.txt
if errorlevel 1 (
  echo Compilation failed.
  type sources.txt
  del /f /q sources.txt
  exit /b 1
)

del /f /q sources.txt

rem -- Run the application (change main class if needed)
"%JAVA%" -cp "%OUT_DIR%;%LIB_DIR%\*" com.examSystem.gui.LoginFrame

endlocal
pause