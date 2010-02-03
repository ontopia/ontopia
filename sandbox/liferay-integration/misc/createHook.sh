#!/bin/bash

if [ $# -lt 1 ]; then
	echo "Usage: ./createHook.sh <ProjectNameHere>"
	exit 1
fi
	
NAME=$1
BUILDXMLCONTENT="<project name=\"hook\" basedir=\".\" default=\"deploy\"><import file=\"../build-common-hook.xml\"/></project>"
LIFERAYHOOKCONTENT="<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE hook PUBLIC \"-//Liferay//DTD Hook 5.2.0//EN\" \"http://www.liferay.com/dtd/liferay-hook_5_2_0.dtd\"><hook><portal-properties>portal-ext.properties</portal-properties></hook>"

mkdir $NAME
cd $NAME
echo $BUILDXMLCONTENT >> build.xml

mkdir docroot
mkdir docroot/WEB-INF
cd docroot/WEB-INF
echo $LIFERAYHOOKCONTENT >> liferay-hook.xml

mkdir src
touch src/portal-ext.properties
cd ../../../..
exit 0