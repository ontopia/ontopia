---------------------------------------------------------------------------------
 Ontopoly - the Ontopia Topic Maps Editor
---------------------------------------------------------------------------------

To get started do the following:

  1. Check out Ontopia from github. See https://github.com/ontopia/ontopia/

  2. Build the ontopia-engine jar file and deploy it to the local
     maven repository: 

    cd ontopia/trunk/ontopia
    ant dist.jar.ontopia ivy.install-local-snapshot

  3. Build the ontopoly artifacts:

    cd src/webapps/ontopoly
    mvn clean install -Dmaven.test.skip

  3. Start up the standalone Ontopoly-distribution, which is an
     executable war-file that includes a web server and a relational database.

    cd ontopoly-webapp-standalone/target/
    java -jar ontopoly-standalone.war

  4. Open the editor in your browser: http://localhost:8080/

