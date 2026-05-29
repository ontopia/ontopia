# OKS: add refer to JAAS configuration file
CATALINA_OPTS="${CATALINA_OPTS} -Djava.security.auth.login.config=${PRGDIR}/../conf/jaas.config"

ONTOPIA_VERSION=$(cat /usr/local/ONTOPIA_VERSION)

printf "\n"
printf "                \e[1;31md8888888888888888888888P   dP          \e[0m\n"
printf "                     \e[1;31m88                                \e[0m\n"
printf "\e[1;37m  .d8888b. 88d888b.  \e[1;31m88  \e[1;30m.d8888b. 88d888b. dP .d8888b. \e[0m\n"
printf "\e[1;37m  88'  '88 88'  '88  \e[1;31m88  \e[1;30m88'  '88 88'  '88 88 88'  '88 \e[0m\n"
printf "\e[1;37m  88.  .88 88    88  \e[1;31m88  \e[1;30m88.  .88 88.  .88 88 88.  .88 \e[0m\n"
printf "\e[1;37m  '88888P' dP    dP  \e[1;31mdP  \e[1;30m'88888P' 88Y888P' dP '88888P8 \e[0m\n"
printf "                                  \e[1;30m88                   \e[0m\n"
printf "                                  \e[1;30mdP                   \e[0m\n"
printf "\e[1;37m     version $ONTOPIA_VERSION\e[0m\n\n"

# copy topicmaps.dist topicmaps to topicmaps, but only if they don't exist
# this allows an empty directory to be used as volume mount point, but still have topicmaps and the
# empty h2 database.
cp -R -u -v -p $ONTOPIA_DIST/* $ONTOPIA_HOME/
