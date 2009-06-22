@echo off
if "%OKS_HOME%"=="" goto error
java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors=true "-Dnet.ontopia.test.root=%OKS_HOME%\tests\test-data" net.ontopia.test.TestRunner --loglevel=ERROR "%OKS_HOME%\tests\test-data\tests-oks.xml" ontopia
goto done

:error
echo "ERROR: OKS_HOME environment variable not set!"

:done
