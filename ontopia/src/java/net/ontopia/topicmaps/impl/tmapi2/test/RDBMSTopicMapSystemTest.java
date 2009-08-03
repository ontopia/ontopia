package net.ontopia.topicmaps.impl.tmapi2.test;

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class RDBMSTopicMapSystemTest 
extends net.ontopia.test.AbstractOntopiaTestCase {

  private TopicMapSystemFactory tmsf;
  private TopicMapSystem tms;

  private Locator locFirst; 

  public RDBMSTopicMapSystemTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    tmsf = TopicMapSystemFactory.newInstance();
    
    tmsf.setProperty("net.ontopia.topicmaps.store", "rdbms");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.Database", "mysql");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionString", "jdbc:mysql://localhost/ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.DriverClass", "com.mysql.jdbc.Driver");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.UserName", "ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.Password", "ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "true");

    tms = tmsf.newTopicMapSystem();

    locFirst = tms.createLocator("http://ontopia.net/first");
  }

  @Override
  protected void tearDown() throws Exception {
    tms.close();
  }

  public void xtestAll() {
    
    // first clean all existing topic maps
    Set<Locator> locators = tms.getLocators();
    for (Locator loc : locators) {
      TopicMap tm = tms.getTopicMap(loc);
      tm.remove();
    }
    
    TopicMap tm;

    try {
      tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);

    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    assertNotNull("could not get newly created TopicMap", tm2);

    tm2.remove();

    tm = tms.getTopicMap(locFirst);
    assertNull("TopicMap has not been removed from TopicMapSystem after remove operation", tm);
  }
}
