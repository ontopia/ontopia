
package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;

public class RDBMSTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public RDBMSTopicMapSourceTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  // --- Test cases

  public void testSource() {
    RDBMSTopicMapSource source = new RDBMSTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setSupportsCreate(true);
    source.setSupportsDelete(true);
    
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
