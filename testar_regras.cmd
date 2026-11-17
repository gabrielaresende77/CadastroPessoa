@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
javac -encoding UTF-8 -source 17 -target 17 -d build\classes src\cadastropessoa\*.java
if errorlevel 1 exit /b 1
javac -encoding UTF-8 -source 17 -target 17 -cp build\classes -d build\classes test\cadastropessoa\*.java
if errorlevel 1 exit /b 1
java -ea -cp build\classes cadastropessoa.TesteRegras
