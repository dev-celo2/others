
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto 64BIT
set path=C:\Program Files\Firebird\Firebird_2_5\bin

isql.exe -q -i dropBI.txt -u sysdba -p masterkey  "C:\Program Files\SistemaEscola\webapps\onbyte\Banco.fdb"
goto END

:64BIT

set path=C:\Program Files (x86)\Firebird\Firebird_2_5\bin

isql.exe -q -i dropBI.txt -u sysdba -p masterkey  "C:\Program Files (x86)\SistemaEscola\webapps\onbyte\Banco.fdb"

:END

exit

