rem OKS: add refer to JAAS configuration file
set CATALINA_OPTS=%CATALINA_OPTS% "-Djava.security.auth.login.config=%CATALINA_HOME%\conf\jaas.config"

echo.
echo "                d8888888888888888888888P   dP          "
echo "                     88                                "
echo "  .d8888b. 88d888b.  88  .d8888b. 88d888b. dP .d8888b. "
echo "  88'  '88 88'  '88  88  88'  '88 88'  '88 88 88'  '88 "
echo "  88.  .88 88    88  88  88.  .88 88.  .88 88 88.  .88 "
echo "  '88888P' dP    dP  dP  '88888P' 88Y888P' dP '88888P8 "
echo "                                  88                   "
echo "                                  dP                   "
echo "     version ${project.version}"
echo.
