@echo off
setlocal

REM Obtém a data atual no formato dd-mm-aaaa
for /F "tokens=1-3 delims=/-" %%a in ("%date%") do (
  set "dia=%%a"
  set "mes=%%b"
  set "ano=%%c"
)

REM Obtém o horário atual no formato hh-mm-ss
for /F "tokens=1-3 delims=:." %%a in ("%time%") do (
  set "hora=%%a"
  set "minuto=%%b"
)

REM Define o caminho do arquivo de origem e o nome do arquivo de destino
set "origem=C:\Program Files (x86)\SistemaEscola\webapps\onbyte\Banco.fdb"
set "destino=C:\bkp_banco\Banco_%dia%-%mes%-%ano%_%hora%h%minuto%.fdb"

REM Cria a pasta de destino, caso não exista
if not exist "C:\bkp_banco" (
  mkdir "C:\bkp_banco"
)

REM Copia o arquivo para o destino
copy "%origem%" "%destino%"

echo Arquivo copiado com sucesso para "%destino%"

REM Apaga os arquivos com mais de 7 dias no diretório de destino
forfiles /P "C:\bkp_banco" /M "Banco*.fdb" /D -7 /C "cmd /c del @path"

echo Arquivos com mais de 7 dias foram removidos do diretório de destino.

endlocal
