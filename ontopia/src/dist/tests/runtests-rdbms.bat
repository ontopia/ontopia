@echo off
if "%1"=="" goto error1
if "%ONTOPIA_HOME%"=="" goto error2

java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Slf4jSaxErrorHandler.ignoreNamespaceErrors=true -Dnet.ontopia.topicmaps.impl.rdbms.PropertyFile=%1 "-Dnet.ontopia.test.root=%ONTOPIA_HOME%\tests\test-data" net.ontopia.test.TestRunner --loglevel=ERROR "%ONTOPIA_HOME%\tests\test-data\tests-oks.xml" ontopia-rdbms
goto done

:error1
echo "Usage: runtests-rdbms.bat <propfile>"
goto done

:error2
echo "ERROR: ONTOPIA_HOME environment variable not set!"
goto done

:done
