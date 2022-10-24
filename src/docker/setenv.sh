# OKS: add refer to JAAS configuration file
CATALINA_OPTS="${CATALINA_OPTS} -Djava.security.auth.login.config=${PRGDIR}/../conf/jaas.config"

# copy topicmaps.dist topicmaps to topicmaps, but only if they don't exist
# this allows an empty directory to be used as volume mount point, but still have topicmaps and the
# empty h2 database.
cp -R -u -v -p $ONTOPIA_DIST/* $ONTOPIA_HOME/
