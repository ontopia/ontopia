#!/bin/sh

java net.ontopia.Ontopia
java -Dnet.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors=true -Dnet.ontopia.test.root=`pwd`/tests/test-data net.ontopia.test.TestRunner --loglevel=ERROR `pwd`/tests/test-data/tests-oks.xml ontopia
