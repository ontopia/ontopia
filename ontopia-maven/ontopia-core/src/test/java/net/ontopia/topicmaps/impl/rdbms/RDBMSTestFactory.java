
// $Id: CoreTestGenerator.java,v 1.22 2008/01/11 13:29:33 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.io.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.persistence.proxy.*;

import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;
import net.ontopia.persistence.rdbms.DatabaseProjectReader;
import net.ontopia.persistence.rdbms.DDLExecuter;
import net.ontopia.persistence.rdbms.GenericSQLProducer;
import net.ontopia.persistence.rdbms.Project;
import org.xml.sax.SAXException;

public class RDBMSTestFactory implements TestFactoryIF {

  //! protected RDBMSStorage storage;
  protected RDBMSTopicMapSource source;
  
  public RDBMSTestFactory() throws IOException, SQLException, SAXException {
    checkDatabasePresence();
    //! storage = new RDBMSStorage(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source = new RDBMSTopicMapSource();
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setSupportsCreate(true);
    source.setSupportsDelete(true);
  }
  
  public TestFactoryIF getFactory() {
    return this;
  }

  public TopicMapStoreIF makeStandaloneTopicMapStore() {
    try {
      return new RDBMSTopicMapStore();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public TopicMapReferenceIF makeTopicMapReference() {
    //! // Open the topic map store
    //! return new RDBMSTopicMapStore(storage).getTopicMap();

    //! return new StoreFactoryReference("rdbms", "RDBMS Implementation",
    //!                                  new TopicMapStoreFactoryIF() {
    //!                                    public TopicMapStoreIF createStore() {
    //!                                      return new RDBMSTopicMapStore(storage);
    //!                                    }
    //!                                  });

    return source.createTopicMap(null, null);
  }

  public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef) {
    topicmapRef.delete();
    //! topicmapRef.close();
    //! topicmap.getStore().close();
  }
  

  // Checks if database property file is given
  // Creates in-memory H2 database otherwise
  public static void checkDatabasePresence() throws IOException, SQLException, SAXException {

    if (System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile") != null) {
      // PropertyFile has been set, check is ok
      return;
    }

    // PropertyFile has not been set, fall back to in-memory H2 database
    final String propertiesLocation = "classpath:net/ontopia/topicmaps/impl/rdbms/rdbms.h2.props";
    final String schemaLocation = "classpath:net/ontopia/topicmaps/impl/rdbms/config/schema.xml";

    // Load database properties
    Properties databaseProperties = new Properties();
    databaseProperties.load(StreamUtils.getInputStream(propertiesLocation));

    // Load topic map database schema
    DefaultConnectionFactory cfactory = new DefaultConnectionFactory(databaseProperties, false);
    Project project = DatabaseProjectReader.loadProject(schemaLocation);
    GenericSQLProducer producer = DDLExecuter.getSQLProducer(project, new String[]{"h2", "generic"});

    // Inject database schema
    Connection conn = cfactory.requestConnection();
    producer.executeCreate(conn);
    conn.commit();
    conn.close();

    // All is done, set rdbms.PropertyFile to H2
    System.setProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile", propertiesLocation);

  }

}
