#!/bin/sh

java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Slf4jSaxErrorHandler.ignoreNamespaceErrors=true -Dnet.ontopia.test.root=`pwd`/tests/test-data net.ontopia.test.TestRunner --loglevel=ERROR `pwd`/tests/test-data/tests-ontopia.xml ontopia
