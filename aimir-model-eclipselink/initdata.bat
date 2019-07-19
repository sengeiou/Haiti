@echo off

set MAVEN_REPOSITORY=

for /f "tokens=3 delims=<>" %%i in ('findstr "<localRepository>" %USERPROFILE%\.m2\settings.xml') do set MAVEN_REPOSITORY=%%i
if "%MAVEN_REPOSITORY%"=="" (
    SET MAVEN_REPOSITORY=%USERPROFILE%\.m2\repository
)

echo %MAVEN_REPOSITORY%

pause 10

if "%1"=="" goto usage

goto execute

:usage
echo USAGE: ./initdata ../aimir-model/src/initdata/xxxx.xml
goto eof

:execute
mvn -e -f pom-init.xml antrun:run -Dfile.encoding=EUC_KR -Declipselink.path=%MAVEN_REPOSITORY%\org\eclipse\persistence\eclipselink -Dspring.instrument.path=%MAVEN_REPOSITORY%\org\springframework\spring-instrument -DdataFile=%1

:eof
