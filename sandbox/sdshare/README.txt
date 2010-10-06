
  EXPERIMENTAL SDSHARE IMPLEMENTATION
=======================================

This is an experimental implementation of the SDshare protocol, as
defined here:
  http://www.egovpt.org/fg/CWA_Part_1b

It has not been properly tested yet, so please do not rely on this for
anything serious.


--- WHAT IT DOES

Essentially, this is a web application which you drop into an Ontopia
installation. You tell it which topic maps you wish to share, and it
automatically creates snapshot and fragment feeds for those topic maps.

Note that it does *not* store snapshots or fragments statically, but
instead generates these dynamically as needed.

It has some limitations at the moment:

  * fragment feeds do not persist when the server is restarted, and

  * some events are not captured by the listener, and thus do not
    create fragments. See 
    http://code.google.com/p/ontopia/issues/detail?id=313


--- INSTALLATION

Copy src/webapp into your apache-tomcat/webapps directory, probably
under the name "sdshare".

Run "ant compile jar". Put the sdshare.jar wherever you want. Either
in apache-tomcat/common/lib or sdshare/WEB-INF/lib.

Copy sdshare.properties where sdshare.jar can see it. That is, into
the /classes directory that corresponds to where you put sdshare.jar.

Edit sdshare.properties as appropriate.

Start the server.

Go to http://localhost:8080/sdshare/topicmaps.jsp


