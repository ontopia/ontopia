@echo off
if "%ONTOPIA_HOME%"=="" goto error
java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors=true "-Dnet.ontopia.test.root=%ONTOPIA_HOME%\tests\test-data" net.ontopia.test.TestRunner --loglevel=ERROR "%ONTOPIA_HOME%\tests\test-data\tests-ontopia.xml" ontopia
goto done

:error
echo "ERROR: ONTOPIA_HOME environment variable not set!"

:done
