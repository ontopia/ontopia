---------------------------------------------------------------------------------
 A prototype: how to produce Solr indexes from topic maps in Ontopia
---------------------------------------------------------------------------------

This is a simple example of how one can use tolog queries to generate
Apache Solr[1] indexes.

Have a look at the following presentation for more background
information about this project:

  http://ontopia.wordpress.com/2009/11/26/ontopia-solr-integration/

To get started do the following:

  1. Download Solr 1.4.0 (or later) from http://lucene.apache.org/solr/

  2. Install the Solr web application into the Apache Tomcat container

    cp solr.war $ONTOPIA/apache-tomcat/webapps/solr.war

  3. Start Apache Tomcat with a reference to the solr index location

    export CATALINA_OPTS=-Dsolr.solr.home=$ONTOPIA_SVN/sandbox/solr-utils/solr-indexes
    cd $ONTOPIA/apache-tomcat
    ./bin/startup

  4. Build the project: 

    mvn clean package

  5. Run the Solr indexer on your topic map.

    java -Xmx256M -cp $ONTOPIA/lib/ontopia.jar:$ONTOPIA_SVN/target/solr-utils-0.1-SNAPSHOT.jar net.ontopia.sandbox.solrutils.SolrIndexer postgresql-930001 solr-indexer:core0 http://localhost:8080/solr/core0 500

Note that the last step requires your topic map to contain the
neccessary topic types and instance of them in your topic map. At the
moment this sandbox project is just meant as an illustration of how to
product Solr indexes from an existing topic map. The way the current
SolrIndexer.java class works is just one of several possible ways of
doing it. Have a look at the presentation and let us know on the
mailing list[2] what you think about this idea.

Enjoy!

---

[1] http://lucene.apache.org/solr/
[2] http://groups.google.com/group/ontopia
