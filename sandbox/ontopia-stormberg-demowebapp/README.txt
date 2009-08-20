This is a simple demo project of creating a web application using a topic map with Ontopia and JSP.
It requires Maven2 http://maven.apache.org/

The topic map is based on a screenscraping a simple product catalog of Stormberg, a Norwegian clothing manufacturer.

Starting the web application:
mvn jetty:run
Go to http://localhost:8080

Compiling, testing and creating war file:
mvn clean install


Quick pointers for installing on Google App Engine:
1. Grab a Google App Engine account http://appengine.google.com . Create a new application on it. Call it hello-ontopia or something.
2. Download and unzip GAE Java SDK http://code.google.com/appengine/docs/java/gettingstarted/installing.html
3. Add the file ontopia-stormberg-demowebapp/src/main/webapp/WEB-INF/appengine-web.xml
<?xml version="1.0" encoding="utf-8"?>
<appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
 <application>hello-ontopia</application>
 <version>1</version>
 <sessions-enabled>true</sessions-enabled>
</appengine-web-app>
4. Run mvn clean install (Requires Maven 2 http://maven.apache.org)
5. Run appcfg.sh update target/ontopia-stormberg-something-something
6. You're laughing!