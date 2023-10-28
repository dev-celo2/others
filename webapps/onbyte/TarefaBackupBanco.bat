@ECHO OFF

CLS

if exist "%ProgramFiles(x86)%" goto 64BIT

SCHTASKS /DELETE /TN "BACKUPBANCO8" /f
SCHTASKS /DELETE /TN "BACKUPBANCO11" /f
SCHTASKS /DELETE /TN "BACKUPBANCO14" /f
SCHTASKS /DELETE /TN "BACKUPBANCO17" /f
SCHTASKS /DELETE /TN "BACKUPBANCO20" /f

SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO8" /TR "\"C:\Program Files\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 08:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO11" /TR "\"C:\Program Files\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 11:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO14" /TR "\"C:\Program Files\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 14:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO17" /TR "\"C:\Program Files\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 17:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO20" /TR "\"C:\Program Files\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 20:00 /RU System /RL HIGHEST

goto END

:64BIT

SCHTASKS /DELETE /TN "BACKUPBANCO8" /f
SCHTASKS /DELETE /TN "BACKUPBANCO11" /f
SCHTASKS /DELETE /TN "BACKUPBANCO14" /f
SCHTASKS /DELETE /TN "BACKUPBANCO17" /f
SCHTASKS /DELETE /TN "BACKUPBANCO20" /f

SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO8" /TR "\"C:\Program Files (x86)\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 08:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO11" /TR "\"C:\Program Files (x86)\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 11:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO14" /TR "\"C:\Program Files (x86)\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 14:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO17" /TR "\"C:\Program Files (x86)\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 17:00 /RU System /RL HIGHEST
SCHTASKS /CREATE /SC DAILY /TN "BACKUPBANCO20" /TR "\"C:\Program Files (x86)\SistemaEscola\webapps\onbyte\BackupBanco.bat\"" /ST 20:00 /RU System /RL HIGHEST

:END