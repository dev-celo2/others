@echo off
echo *** INICIANDO ***

set path=C:\Program Files (x86)\Firebird\Firebird_2_5\bin


isql.exe -q -i "C:\Program Files (x86)\SistemaEscola\webapps\onbyte\ScriptbAtualizacao.txt" -u sysdba -p masterkey  "C:\Program Files (x86)\SistemaEscola\webapps\onbyte\Banco.fdb"




