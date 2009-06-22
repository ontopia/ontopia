#!/bin/sh

if [ -n "$1" ]
		then
		java net.ontopia.Ontopia
		java -Dnet.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors=true -Dnet.ontopia.topicmaps.impl.rdbms.PropertyFile=$1 -Dnet.ontopia.test.root=`pwd`/tests/test-data net.ontopia.test.TestRunner --loglevel=ERROR `pwd`/tests/test-data/tests-oks.xml ontopia-rdbms
else
		echo "Usage: runtests-rdbms.sh <propfile>"
fi
