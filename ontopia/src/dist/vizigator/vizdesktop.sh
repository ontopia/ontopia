CLASSPATH=../lib/ontopia.jar:../lib/touchgraph.jar:$CLASSPATH
java -cp $CLASSPATH net.ontopia.topicmaps.viz.VizDesktop $@
