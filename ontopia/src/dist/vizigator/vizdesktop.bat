@echo off
java -cp "../lib/ontopia.jar;../lib/touchgraph.jar;%CLASSPATH%"  net.ontopia.topicmaps.viz.VizDesktop %1 %2 %3
