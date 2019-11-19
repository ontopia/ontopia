/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.persistence.rdbms.DDLExecuter;
import net.ontopia.persistence.rdbms.DatabaseProjectReader;
import net.ontopia.persistence.rdbms.GenericSQLProducer;
import net.ontopia.persistence.rdbms.Project;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
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

  public TopicMapSourceIF getSource() {
    return source;
  }

  @Override
  public TopicMapStoreIF makeStandaloneTopicMapStore() {
    try {
      return new RDBMSTopicMapStore();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
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

  @Override
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
