@echo off
setlocal
cd /d "%~dp0"
if not "%~1"=="--base-de-testes" (
  echo Configure db.properties para uma base de testes e execute testar_banco.cmd --base-de-testes
  exit /b 1
)
call testar_regras.cmd
if errorlevel 1 exit /b 1
java -ea -cp "build\classes;src\lib\mysql-connector-java-8.0.11.jar" cadastropessoa.TesteBanco --base-de-testes
