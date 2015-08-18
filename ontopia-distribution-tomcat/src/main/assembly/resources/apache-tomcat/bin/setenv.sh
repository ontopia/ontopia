# OKS: add refer to JAAS configuration file
CATALINA_OPTS="${CATALINA_OPTS} -Djava.security.auth.login.config=${PRGDIR}/../conf/jaas.config"
