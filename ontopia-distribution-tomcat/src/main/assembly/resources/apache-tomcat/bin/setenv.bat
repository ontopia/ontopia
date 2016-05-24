rem OKS: add refer to JAAS configuration file
set CATALINA_OPTS=%CATALINA_OPTS% "-Djava.security.auth.login.config=%CATALINA_HOME%\conf\jaas.config"
