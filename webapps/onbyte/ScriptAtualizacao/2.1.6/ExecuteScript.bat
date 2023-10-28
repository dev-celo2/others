@echo off
CHCP 1252


echo !!!!!! ATENÇÃO !!!!!!!!!!
echo VOCÊ JÁ REALIZOU O BACKUP DE SEU BANCO DE DADOS?
echo DIGITE S PARA SIM E N PARA NÃO EM SEGUIDA TECLE ENTER

set/p continuar=

if /i %continuar% equ S goto FECHAR

echo on
exit

:FECHAR

echo *** INICIANDO ALTERAÇÔES EM SEU BANCO DE DADOS ***



if exist "%ProgramFiles(x86)%" goto 64BIT

set path=C:/Program Files/Firebird/Firebird_2_5/bin



echo *** ATUALIZANDO BANCO ***
isql.exe -q -i "C:/Program Files/SistemaEscola/webapps/onbyte/ScriptAtualizacao/2.1.6/ScriptAtualizacao.txt" -u sysdba -p masterkey  "C:/Program Files/SistemaEscola/webapps/onbyte/Banco.fdb" -ch WIN1252


goto END

:64BIT

set path=C:/Program Files (x86)/Firebird/Firebird_2_5/bin



echo *** ATUALIZANDO BANCO***
isql.exe -q -i "C:/Program Files (x86)/SistemaEscola/webapps/onbyte/ScriptAtualizacao/2.1.6/ScriptAtualizacao.txt" -u sysdba -p masterkey  "C:/Program Files (x86)/SistemaEscola/webapps/onbyte/Banco.fdb" -ch WIN1252
echo *** BANCO ATUALIZADO ***


pause




:END












