@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
javac -encoding UTF-8 -source 17 -target 17 -d build\classes src\cadastropessoa\*.java
if errorlevel 1 exit /b 1
java -cp "build\classes;src\lib\mysql-connector-java-8.0.11.jar" cadastropessoa.CadastroPessoa
