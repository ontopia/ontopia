#
#
#

# Note: requires ontopia-distribution-tomcat to have been build!

FROM tomcat:9.0-jdk8

ENV ONTOPIA_HOME=/usr/local/ontopia
ENV ONTOPIA_DIST=/usr/local/ontopia.dist
RUN mkdir -p $ONTOPIA_HOME &&\
    mkdir -p $ONTOPIA_HOME/topicmaps &&\
    mkdir -p $ONTOPIA_DIST/topicmaps

# --- copy files from ontopia-distribution-tomcat source
ENV ASSEMBLY=ontopia-distribution-tomcat/src/main/assembly/resources

# config
COPY $ASSEMBLY/apache-tomcat/conf/jaas.config $CATALINA_HOME/conf

# topicmaps
ADD $ASSEMBLY/topicmaps $ONTOPIA_DIST/topicmaps


# --- copy files from ontopia-distribution-tomcat build
# todo: can this be replaced with a RUN mvn assembly?, like tomcat, but just the jars and wars
ENV DIST=ontopia-distribution-tomcat/target/ontopia-distribution-tomcat-?.?.?
ENV TOMCAT=$DIST/apache-tomcat

# jars
COPY $DIST/lib/* $CATALINA_HOME/lib
# webapps
ADD $TOMCAT/webapps $CATALINA_HOME/webapps


# --- copy docker specific files
ENV SRC=src/docker

COPY $SRC/log4j.properties $CATALINA_HOME/lib
COPY $SRC/catalina.properties $CATALINA_HOME/conf
COPY $SRC/server.xml $CATALINA_HOME/conf

# add h2 database as source
COPY $SRC/tm-sources.xml $ONTOPIA_DIST
COPY $SRC/h2.properties $ONTOPIA_DIST
ADD https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar $CATALINA_HOME/lib
COPY $SRC/ontopia.mv.db $ONTOPIA_DIST

# --- tweak the startup to allow for empty volume to be polulated with topicmaps
COPY $SRC/setenv.sh $CATALINA_HOME/bin

# --- Volume for ontopia directory
VOLUME $ONTOPIA_HOME
