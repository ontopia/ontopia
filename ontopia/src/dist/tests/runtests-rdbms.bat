@echo off
if "%1"=="" goto error1
if "%OKS_HOME%"=="" goto error2

java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors=true -Dnet.ontopia.topicmaps.impl.rdbms.PropertyFile=%1 "-Dnet.ontopia.test.root=%OKS_HOME%\tests\test-data" net.ontopia.test.TestRunner --loglevel=ERROR "%OKS_HOME%\tests\test-data\tests-oks.xml" ontopia-rdbms
goto done

:error1
echo "Usage: runtests-rdbms.bat <propfile>"
goto done

:error2
echo "ERROR: OKS_HOME environment variable not set!"
goto done

:done
