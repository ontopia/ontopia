@echo off
java -cp "../lib/oks-enterprise.jar;../lib/GraphLayout.jar;%CLASSPATH%"  net.ontopia.topicmaps.viz.VizDesktop %1 %2 %3
