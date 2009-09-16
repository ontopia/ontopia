#!/bin/bash

LIBDIR=./lib
CLASSPATH=./classes$(find $LIBDIR -name *.jar -exec printf :{} ';')
echo $CLASSPATH
java -cp $CLASSPATH net.ontopia.topicmaps.query.toma.tools.QueryTool "$@"
